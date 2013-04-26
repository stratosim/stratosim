package com.stratosim.server.persistence.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.Instant;

import com.stratosim.server.persistence.proto.Persistence.VersionMetadataPb;
import com.google.appengine.api.datastore.Blob;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Ref;
import com.stratosim.server.CircuitToPostScript;
import com.stratosim.server.CircuitUpdater;
import com.stratosim.server.external.ExternalWorkerHelper;
import com.stratosim.server.persistence.impl.helpers.DatastoreHelper;
import com.stratosim.server.persistence.impl.helpers.PermissionsHelper;
import com.stratosim.server.persistence.kinds.CircuitCsvKind;
import com.stratosim.server.persistence.kinds.CircuitDataKind;
import com.stratosim.server.persistence.kinds.CircuitPdfKind;
import com.stratosim.server.persistence.kinds.CircuitPngKind;
import com.stratosim.server.persistence.kinds.CircuitPsKind;
import com.stratosim.server.persistence.kinds.CircuitSimulationPdfKind;
import com.stratosim.server.persistence.kinds.CircuitSimulationPngKind;
import com.stratosim.server.persistence.kinds.CircuitSimulationPsKind;
import com.stratosim.server.persistence.kinds.CircuitSpiceKind;
import com.stratosim.server.persistence.kinds.CircuitSvgKind;
import com.stratosim.server.persistence.kinds.CircuitThumbnailKind;
import com.stratosim.server.persistence.kinds.PublicFileKind;
import com.stratosim.server.persistence.kinds.UserFileKind;
import com.stratosim.server.persistence.objectify.entity.AbstractDownloadFormatEntity;
import com.stratosim.server.persistence.objectify.impl.OCircuitDataHelper;
import com.stratosim.server.persistence.schema.CustomKeyFactory;
import com.stratosim.server.persistence.schema.PKey;
import com.stratosim.shared.DeletingWithCollaboratorsException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.RemovingLastOwnerException;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.filemodel.CircuitHash;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

class RealPersistenceLayer {

  private static final Logger logger = Logger.getLogger(RealPersistenceLayer.class
      .getCanonicalName());

  private static final String HASH_ALGORITHM = "SHA-256";

  private static final FileRole DEFAULT_NEW_FILE_ROLE = FileRole.OWNER;

  private final DatastoreHelper datastoreHelper;

  private final PermissionsHelper permissionsHelper;

  public RealPersistenceLayer(DatastoreHelper datastoreHelper) {
    this.datastoreHelper = datastoreHelper;
    this.permissionsHelper = new PermissionsHelper(datastoreHelper);
  }

  /**
   * Put a new circuit for the specified user. This should not be used by GWT-RPC services.
   */
  public VersionMetadata putCircuit(Circuit circuit, LowercaseEmailAddress user)
      throws PersistenceException {

    FileKey fileKey = null;
    if (circuit.getFileKey() == null) {
      // New file needs to get a fileKey generated.
      fileKey = FileKeyGenerator.newFileKey();
    } else {
      fileKey = circuit.getFileKey();
    }

    String circuitName = circuit.getName();

    // To detect errors, clean the circuit so there is no metadata.
    // Read errors with keys are then guaranteed to throw exceptions all
    // over instead of doing strange things.
    circuit.setFileKey(null);
    circuit.setVersionKey(null);

    Blob dataBlob = serializeCircuit(circuit);
    CircuitHash circuitHash = hashBlob(dataBlob);

    PKey<CircuitDataKind> circuitDataKey = CustomKeyFactory.circuitDataKey(circuitHash);

    try {
      datastoreHelper.get(circuitDataKey, new CircuitDataKind());

    } catch (PersistenceException expected) {
      // new circuit
      CircuitDataKind circuitDataKind = new CircuitDataKind();
      circuitDataKind.data.set(dataBlob);
      datastoreHelper.overwriteOrPut(circuitDataKey, circuitDataKind);
    }

    Blob psBlob = new Blob(CircuitToPostScript.convert(circuit).getBytes());
    putCircuitData(circuitHash, DownloadFormat.PS, psBlob);

    ExternalWorkerHelper.getQueue().add(
        ExternalWorkerHelper.createTask(DownloadFormat.THUMBNAIL, DownloadFormat.PS, circuitHash,
            psBlob));

    VersionMetadataPb versionMetadataPb = VersionMetadataPb.newBuilder()
        .setSaverEmail(user.getEmail())
        .setName(circuitName)
        .setCircuitHash(circuitHash.get())
        .build();

    VersionMetadataKey versionMetadataKey = datastoreHelper.addVersion(fileKey, versionMetadataPb);
    try {
      datastoreHelper.updatePermissions(fileKey, ImmutableMap.of(user, DEFAULT_NEW_FILE_ROLE));
    } catch (RemovingLastOwnerException ex) {
      throw new AssertionError("new user should be OWNER -- should not get this error, " + "fk="
          + ex.getFileKey().get());
    }

    return new VersionMetadata(circuitName, versionMetadataKey);
  }
  
  
  // ----------------------------------------------------------------------------------------------
  // Circuit data
  // ----------------------------------------------------------------------------------------------
  

  public Circuit getCircuit(VersionMetadataKey versionMetadataKey) throws PersistenceException {

    VersionMetadataPb versionMetadataPb = getVersionMetadata(versionMetadataKey);
    CircuitHash hash = new CircuitHash(versionMetadataPb.getCircuitHash());
    PKey<CircuitDataKind> circuitDataKey = CustomKeyFactory.circuitDataKey(hash);
    CircuitDataKind circuitDataKind = datastoreHelper.get(circuitDataKey, new CircuitDataKind());
    Circuit circuit = deserializeCircuit(circuitDataKind.data.get());

    circuit.setFileKey(versionMetadataKey.getFileKey());
    circuit.setVersionKey(versionMetadataKey);

    // Temporary to test the updater before a batch update.
    CircuitUpdater.update(circuit);

    return circuit;
  }

  public Circuit getCircuitWithoutKeys(CircuitHash circuitHash) throws PersistenceException {

    PKey<CircuitDataKind> circuitDataKey = CustomKeyFactory.circuitDataKey(circuitHash);
    CircuitDataKind circuitDataKind = datastoreHelper.get(circuitDataKey, new CircuitDataKind());
    return deserializeCircuit(circuitDataKind.data.get());
  }

  public Blob getRenderedCircuit(CircuitHash circuitHash, DownloadFormat format)
      throws PersistenceException {
    return getCircuitDataInternal(circuitHash, format);
  }

  public Blob getCircuitData(CircuitHash circuitHash, DownloadFormat format)
      throws PersistenceException {
    return getCircuitDataInternal(circuitHash, format);
  }

  public Blob getCircuitDataFromVersion(VersionMetadataKey versionMetadataKey, DownloadFormat format)
      throws PersistenceException {

    VersionMetadataPb versionMetadataPb = getVersionMetadata(versionMetadataKey);
    CircuitHash hash = new CircuitHash(versionMetadataPb.getCircuitHash());
    return getCircuitDataInternal(hash, format);
  }

  public Blob getSimulatedCircuitFromVersion(VersionMetadataKey versionMetadataKey,
      DownloadFormat format) throws PersistenceException {

    VersionMetadataPb versionMetadataPb = getVersionMetadata(versionMetadataKey);
    CircuitHash hash = new CircuitHash(versionMetadataPb.getCircuitHash());
    return getCircuitDataInternal(hash, format);
  }

  private Blob getCircuitDataInternal(CircuitHash circuitHash, DownloadFormat format)
      throws PersistenceException {

    Blob blob = null;
    if (format == DownloadFormat.PDF) {
      PKey<CircuitPdfKind> key = CustomKeyFactory.circuitPdfKey(circuitHash);
      CircuitPdfKind kind = datastoreHelper.get(key, new CircuitPdfKind());
      blob = kind.data.get();

    } else if (format == DownloadFormat.PNG) {
      PKey<CircuitPngKind> key = CustomKeyFactory.circuitPngKey(circuitHash);
      CircuitPngKind kind = datastoreHelper.get(key, new CircuitPngKind());
      blob = kind.data.get();

    } else if (format == DownloadFormat.SVG) {
      PKey<CircuitSvgKind> key = CustomKeyFactory.circuitSvgKey(circuitHash);
      CircuitSvgKind kind = datastoreHelper.get(key, new CircuitSvgKind());
      blob = kind.data.get();

    } else if (format == DownloadFormat.THUMBNAIL) {
      PKey<CircuitThumbnailKind> key = CustomKeyFactory.circuitThumbnailKey(circuitHash);
      CircuitThumbnailKind kind = datastoreHelper.get(key, new CircuitThumbnailKind());
      blob = kind.data.get();

    } else if (format == DownloadFormat.PS) {
      PKey<CircuitPsKind> key = CustomKeyFactory.circuitPsKey(circuitHash);
      CircuitPsKind kind = datastoreHelper.get(key, new CircuitPsKind());
      blob = kind.data.get();

    } else if (format == DownloadFormat.SPICE) {
      PKey<CircuitSpiceKind> key = CustomKeyFactory.circuitSpiceKey(circuitHash);
      CircuitSpiceKind kind = datastoreHelper.get(key, new CircuitSpiceKind());
      blob = kind.data.get();

    } else if (format == DownloadFormat.CSV) {
      PKey<CircuitCsvKind> key = CustomKeyFactory.circuitCsvKey(circuitHash);
      CircuitCsvKind kind = datastoreHelper.get(key, new CircuitCsvKind());
      blob = kind.data.get();

    } else if (format == DownloadFormat.SIMULATIONPS) {
      PKey<CircuitSimulationPsKind> key = CustomKeyFactory.circuitSimulationPsKey(circuitHash);
      CircuitSimulationPsKind kind = datastoreHelper.get(key, new CircuitSimulationPsKind());
      blob = kind.data.get();

    } else if (format == DownloadFormat.SIMULATIONPDF) {
      PKey<CircuitSimulationPdfKind> key = CustomKeyFactory.circuitSimulationPdfKey(circuitHash);
      CircuitSimulationPdfKind kind = datastoreHelper.get(key, new CircuitSimulationPdfKind());
      blob = kind.data.get();

    } else if (format == DownloadFormat.SIMULATIONPNG) {
      PKey<CircuitSimulationPngKind> key = CustomKeyFactory.circuitSimulationPngKey(circuitHash);
      CircuitSimulationPngKind kind = datastoreHelper.get(key, new CircuitSimulationPngKind());
      blob = kind.data.get();

    } else {
      throw new IllegalArgumentException("unrecognized format: " + format);
    }

    return blob;
  }

  /**
   * Add a rendered circuit in the specified format. This should never be called by a GWT-RPC
   * service.
   */
  public void putCircuitData(CircuitHash circuitHash, DownloadFormat format, Blob data) {

    if (format == DownloadFormat.PDF) {
      CircuitPdfKind kind = new CircuitPdfKind();
      kind.data.set(data);
      PKey<CircuitPdfKind> key = CustomKeyFactory.circuitPdfKey(circuitHash);
      datastoreHelper.overwriteOrPut(key, kind);

    } else if (format == DownloadFormat.PNG) {
      CircuitPngKind kind = new CircuitPngKind();
      kind.data.set(data);
      PKey<CircuitPngKind> key = CustomKeyFactory.circuitPngKey(circuitHash);
      datastoreHelper.overwriteOrPut(key, kind);

    } else if (format == DownloadFormat.SVG) {
      CircuitSvgKind kind = new CircuitSvgKind();
      kind.data.set(data);
      PKey<CircuitSvgKind> key = CustomKeyFactory.circuitSvgKey(circuitHash);
      datastoreHelper.overwriteOrPut(key, kind);

    } else if (format == DownloadFormat.THUMBNAIL) {
      CircuitThumbnailKind kind = new CircuitThumbnailKind();
      kind.data.set(data);
      PKey<CircuitThumbnailKind> key = CustomKeyFactory.circuitThumbnailKey(circuitHash);
      datastoreHelper.overwriteOrPut(key, kind);

    } else if (format == DownloadFormat.PS) {
      CircuitPsKind kind = new CircuitPsKind();
      kind.data.set(data);
      PKey<CircuitPsKind> key = CustomKeyFactory.circuitPsKey(circuitHash);
      datastoreHelper.overwriteOrPut(key, kind);

    } else if (format == DownloadFormat.SPICE) {
      CircuitSpiceKind kind = new CircuitSpiceKind();
      kind.data.set(data);
      PKey<CircuitSpiceKind> key = CustomKeyFactory.circuitSpiceKey(circuitHash);
      datastoreHelper.overwriteOrPut(key, kind);

    } else if (format == DownloadFormat.CSV) {
      CircuitCsvKind kind = new CircuitCsvKind();
      kind.data.set(data);
      PKey<CircuitCsvKind> key = CustomKeyFactory.circuitCsvKey(circuitHash);
      datastoreHelper.overwriteOrPut(key, kind);

    } else if (format == DownloadFormat.SIMULATIONPS) {
      CircuitSimulationPsKind kind = new CircuitSimulationPsKind();
      kind.data.set(data);
      PKey<CircuitSimulationPsKind> key = CustomKeyFactory.circuitSimulationPsKey(circuitHash);
      datastoreHelper.overwriteOrPut(key, kind);

    } else if (format == DownloadFormat.SIMULATIONPDF) {
      CircuitSimulationPdfKind kind = new CircuitSimulationPdfKind();
      kind.data.set(data);
      PKey<CircuitSimulationPdfKind> key = CustomKeyFactory.circuitSimulationPdfKey(circuitHash);
      datastoreHelper.overwriteOrPut(key, kind);

    } else if (format == DownloadFormat.SIMULATIONPNG) {
      CircuitSimulationPngKind kind = new CircuitSimulationPngKind();
      kind.data.set(data);
      PKey<CircuitSimulationPngKind> key = CustomKeyFactory.circuitSimulationPngKey(circuitHash);
      datastoreHelper.overwriteOrPut(key, kind);

    } else {
      throw new IllegalArgumentException("unrecognized format: " + format);
    }
  }

  // ----------------------------------------------------------------------------------------------
  // File metadata
  // ----------------------------------------------------------------------------------------------
  

  public ImmutableMap<FileKey, VersionMetadata> getLatestVersions(LowercaseEmailAddress user) {
    ImmutableMap<FileKey, FileRole> roles = permissionsHelper.getAllUserRoles(user);

    Map<FileKey, Future<ImmutableSortedMap<Instant, VersionMetadataPb>>> futures = Maps.newHashMap();
    for (FileKey fileKey : roles.keySet()) {
      futures.put(fileKey, datastoreHelper.asyncGetVersions(fileKey));
    }

    ImmutableMap.Builder<FileKey, VersionMetadata> builder = ImmutableMap.builder();
    for (FileKey fileKey : futures.keySet()) {
      builder.put(fileKey,
          latestVersionFromVersionsMap(fileKey, Futures.getUnchecked(futures.get(fileKey))));
    }

    return builder.build();
  }

  public VersionMetadata getLatestVersion(FileKey fileKey) {
    return latestVersionFromVersionsMap(fileKey, datastoreHelper.getVersions(fileKey));
  }
  
  private static VersionMetadata latestVersionFromVersionsMap(FileKey fileKey,
      ImmutableSortedMap<Instant, VersionMetadataPb> versions) {
    Preconditions.checkState(!versions.isEmpty());

    Instant last = versions.lastKey();
    VersionMetadataPb versionMetadataPb = versions.get(last);

    VersionMetadataKey versionMetadataKey = new VersionMetadataKey(fileKey, last.getMillis());
    return versionMetadataFromRaw(versionMetadataKey, versionMetadataPb);
  }

  public ImmutableList<VersionMetadata> getVersions(FileKey fileKey) throws PersistenceException {
    ImmutableSortedMap<Instant, VersionMetadataPb> versions = datastoreHelper.getVersions(fileKey);

    checkState(!versions.isEmpty());

    ImmutableList.Builder<VersionMetadata> builder = ImmutableList.builder();

    for (Map.Entry<Instant, VersionMetadataPb> entry : versions.entrySet()) {
      Instant version = entry.getKey();
      VersionMetadataKey versionMetadataKey = new VersionMetadataKey(fileKey, version.getMillis());
      VersionMetadata versionMetadata = versionMetadataFromRaw(
          versionMetadataKey, entry.getValue());
      builder.add(versionMetadata);
    }

    return builder.build();
  }
  
  public VersionMetadata getVersion(VersionMetadataKey versionMetadataKey)
      throws PersistenceException {
    return versionMetadataFromRaw(versionMetadataKey, getVersionMetadata(versionMetadataKey));
  }

  public ImmutableSortedMap<Instant, VersionMetadataPb> getVersionProtos(FileKey fileKey) {
    return datastoreHelper.getVersions(fileKey);
  }

  private VersionMetadataPb getVersionMetadata(VersionMetadataKey versionMetadataKey)
      throws PersistenceException {
    checkNotNull(versionMetadataKey);

    ImmutableSortedMap<Instant, VersionMetadataPb> versions =
        datastoreHelper.getVersions(versionMetadataKey.getFileKey());
    Instant time = new Instant(versionMetadataKey.getTimeMillis());

    if (!versions.containsKey(time)) {
      logger.log(Level.WARNING, "version not found: " + versionMetadataKey.toString());
      throw new PersistenceException();
    }

    return versions.get(time);
  }
  
  private static VersionMetadata versionMetadataFromRaw(VersionMetadataKey versionMetadataKey,
                                                 VersionMetadataPb versionMetadataPb) {
    String name = versionMetadataPb.getName();
    return new VersionMetadata(name, versionMetadataKey);
  }
  
  public ImmutableMap<FileKey, String> getLatestThumbnails(LowercaseEmailAddress user) {
    ImmutableMap<FileKey, FileRole> roles = permissionsHelper.getAllUserRoles(user);

    Map<FileKey, Ref<? extends AbstractDownloadFormatEntity>> refs = Maps.newHashMap();
    for (FileKey fileKey : roles.keySet()) {
      ImmutableSortedMap<Instant, VersionMetadataPb> versions = getVersionProtos(fileKey);
      Instant last = versions.lastKey();
      VersionMetadataPb versionMetadataPb = versions.get(last);
      CircuitHash hash = new CircuitHash(versionMetadataPb.getCircuitHash());

      refs.put(fileKey,
          OCircuitDataHelper.getCircuitBinaryData(DownloadFormat.THUMBNAIL, hash));
    }

    ImmutableMap.Builder<FileKey, String> builder = ImmutableMap.builder();
    for (FileKey fileKey : refs.keySet()) {
      try {
        Blob blob = refs.get(fileKey).safeGet().getData();
        builder.put(fileKey, Base64.encodeBase64String(blob.getBytes()));
      } catch (NotFoundException e) {
        logger.warning("Thumbnail not found for fileKey: " + fileKey);
      }
    }
    
    return builder.build();
  }

  
  // ----------------------------------------------------------------------------------------------
  // Permissions
  // ----------------------------------------------------------------------------------------------
  

  public void deleteFile(FileKey fileKey) throws DeletingWithCollaboratorsException {
    datastoreHelper.deleteFile(fileKey);
  }

  public ImmutableMap<LowercaseEmailAddress, FileRole> getPermissions(FileKey fileKey)
      throws PersistenceException {
    return permissionsHelper.getAllFileRoles(fileKey);
  }

  public ImmutableMap<FileKey, FileRole> getPermissions(LowercaseEmailAddress user) {
    return permissionsHelper.getAllUserRoles(user);
  }

  public FileRole getPermission(FileKey fileKey, LowercaseEmailAddress user) throws PersistenceException {
    ImmutableMap<LowercaseEmailAddress, FileRole> allRoles = permissionsHelper.getAllFileRoles(fileKey);
    FileRole maybeRole = allRoles.get(user);
    if (maybeRole != null) {
      return maybeRole;
    } else {
      return FileRole.NONE;
    }
  }

  public void updatePermissions(FileKey fileKey, ImmutableMap<LowercaseEmailAddress, FileRole> diff)
      throws RemovingLastOwnerException {
    datastoreHelper.updatePermissions(fileKey, diff);
  }

  public ImmutableMap<LowercaseEmailAddress, FileRole> setPermissions(FileKey fileKey, ImmutableMap<LowercaseEmailAddress, FileRole> permissions)
      throws RemovingLastOwnerException {
    return datastoreHelper.setPermissions(fileKey, permissions);
  }

  public void makePublic(FileKey fileKey, LowercaseEmailAddress addedBy) {
    PublicFileKind publicFileKind = new PublicFileKind();
    publicFileKind.addedBy.set(addedBy);
    PKey<PublicFileKind> publicFileKey = CustomKeyFactory.publicFileKey(fileKey);
    datastoreHelper.overwriteOrPut(publicFileKey, publicFileKind);
  }

  public void makeNonPublic(FileKey fileKey) {
    PKey<PublicFileKind> publicFileKey = CustomKeyFactory.publicFileKey(fileKey);
    datastoreHelper.delete(publicFileKey);
  }

  public boolean isPublic(FileKey fileKey) {
    PKey<PublicFileKind> publicFileKey = CustomKeyFactory.publicFileKey(fileKey);
    return datastoreHelper.hasValue(publicFileKey, new PublicFileKind());
  }

  public boolean isWhitelistedUser(LowercaseEmailAddress user) {
    return datastoreHelper.isWhitelistedUser(user);
  }

  public void putWhitelistedUser(LowercaseEmailAddress user) throws IllegalArgumentException,
      PersistenceException {
    datastoreHelper.putWhitelistedUser(user);
  }

  public ImmutableList<LowercaseEmailAddress> listAllUsers() {
    return FluentIterable.from(datastoreHelper.listKeys(new UserFileKind()))
        .transform(new Function<PKey<UserFileKind>, LowercaseEmailAddress>() {
          @Override
          public LowercaseEmailAddress apply(PKey<UserFileKind> pkey) {
            return new LowercaseEmailAddress(pkey.dsKey().getName());
          }
        }).toImmutableList();
  }

  
  // ----------------------------------------------------------------------------------------------
  // Helpers
  // ----------------------------------------------------------------------------------------------
  

  private CircuitHash hashBlob(Blob blob) {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance(HASH_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    if (md == null) {
      // This is impossible.
      throw new IllegalStateException("Hashing Algorithm Not Found: " + HASH_ALGORITHM);
    }
    md.update(blob.getBytes());

    return new CircuitHash(String.format("%064x", new BigInteger(1, md.digest())));
  }

  // TODO(tpondich): Use protocol buffers since java serialization is flaky
  private Blob serializeCircuit(Circuit circuit) {
    checkNotNull(circuit);

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try {
      ObjectOutputStream out = new ObjectOutputStream(buffer);
      out.writeObject(circuit);
    } catch (IOException e) {
      throw new IllegalArgumentException("circuit serialization error", e);
    }
    return new Blob(buffer.toByteArray());
  }

  private Circuit deserializeCircuit(Blob blob) {
    ByteArrayInputStream buffer = new ByteArrayInputStream(blob.getBytes());
    Circuit circuit = null;
    try {
      ObjectInputStream in = new ObjectInputStream(buffer);
      circuit = (Circuit) in.readObject();
    } catch (IOException e) {
      throw new IllegalArgumentException("circuit serialization error", e);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("circuit serialization error", e);
    }

    return circuit;
  }

  public CircuitHash getCircuitHash(VersionMetadataKey versionMetadataKey)
      throws PersistenceException {
    VersionMetadataPb versionMetadataPb = getVersionMetadata(versionMetadataKey);
    CircuitHash hash = new CircuitHash(versionMetadataPb.getCircuitHash());

    return hash;
  }

}
