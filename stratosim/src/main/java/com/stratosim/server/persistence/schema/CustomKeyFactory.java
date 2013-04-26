package com.stratosim.server.persistence.schema;

import com.google.appengine.api.datastore.KeyFactory;
import com.stratosim.server.persistence.kinds.AccountKind;
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
import com.stratosim.server.persistence.kinds.FileRoleKind;
import com.stratosim.server.persistence.kinds.FileVersionsKind;
import com.stratosim.server.persistence.kinds.GoogleAPIsKind;
import com.stratosim.server.persistence.kinds.PublicFileKind;
import com.stratosim.server.persistence.kinds.UserFileKind;
import com.stratosim.server.persistence.kinds.WhitelistedUserKind;
import com.stratosim.shared.filemodel.CircuitHash;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class CustomKeyFactory {
  
  // WARNING: Emails should be converted to lowercase.

  public static PKey<FileRoleKind> fileRoleKey(FileKey fileKey) {
    return datastoreKey(new FileRoleKind(), fileKey.get());
  }
  
  public static PKey<FileVersionsKind> fileVersionsKey(FileKey fileKey) {
    return datastoreKey(new FileVersionsKind(), fileKey.get());
  }
  
  public static PKey<UserFileKind> userFileKey(LowercaseEmailAddress email) {
    return datastoreKey(new UserFileKind(), email.getEmail());
  }

  public static PKey<CircuitDataKind> circuitDataKey(CircuitHash hash) {
    return datastoreKey(new CircuitDataKind(), hash.get());
  }

  public static PKey<CircuitPdfKind> circuitPdfKey(CircuitHash hash) {
    return datastoreKey(new CircuitPdfKind(), hash.get());
  }

  public static PKey<CircuitPngKind> circuitPngKey(CircuitHash hash) {
    return datastoreKey(new CircuitPngKind(), hash.get());
  }
  
  public static PKey<CircuitSvgKind> circuitSvgKey(CircuitHash hash) {
    return datastoreKey(new CircuitSvgKind(), hash.get());
  }
  
  public static PKey<CircuitThumbnailKind> circuitThumbnailKey(CircuitHash hash) {
    return datastoreKey(new CircuitThumbnailKind(), hash.get());
  }

  public static PKey<CircuitPsKind> circuitPsKey(CircuitHash hash) {
    return datastoreKey(new CircuitPsKind(), hash.get());
  }

  public static PKey<CircuitSpiceKind> circuitSpiceKey(CircuitHash hash) {
    return datastoreKey(new CircuitSpiceKind(), hash.get());
  }
  
  public static PKey<CircuitCsvKind> circuitCsvKey(CircuitHash hash) {
    return datastoreKey(new CircuitCsvKind(), hash.get());
  }

  public static PKey<CircuitSimulationPsKind> circuitSimulationPsKey(CircuitHash hash) {
    return datastoreKey(new CircuitSimulationPsKind(), hash.get());
  }
  
  public static PKey<CircuitSimulationPdfKind> circuitSimulationPdfKey(CircuitHash hash) {
    return datastoreKey(new CircuitSimulationPdfKind(), hash.get());
  }
  
  public static PKey<CircuitSimulationPngKind> circuitSimulationPngKey(CircuitHash hash) {
    return datastoreKey(new CircuitSimulationPngKind(), hash.get());
  }
  
  public static PKey<CircuitDataKind> circuitDataKey(
    CircuitDataKind circuitDataKind, CircuitHash hash) {
    return datastoreKey(circuitDataKind, hash.get());
  }

  public static PKey<WhitelistedUserKind> whitelistedUserKey(LowercaseEmailAddress email) {
    return datastoreKey(new WhitelistedUserKind(), email.getEmail());
  }
  
  public static PKey<AccountKind> accountKey(LowercaseEmailAddress email) {
    return datastoreKey(new AccountKind(), email.getEmail());
  }
  
  public static PKey<GoogleAPIsKind> googleAPIsKey(LowercaseEmailAddress email) {
    return datastoreKey(new GoogleAPIsKind(), email.getEmail());

  }
  
  public static PKey<PublicFileKind> publicFileKey(FileKey fileKey) {
    return datastoreKey(new PublicFileKind(), fileKey.get());
  }

  
  // Internals

  private CustomKeyFactory() {
    throw new UnsupportedOperationException("uninstantiable");
  }

  private static <K extends Kind<K>> PKey<K> datastoreKey(K kind, String customPart) {
    return new PKey<K>(KeyFactory.createKeyString(kind.kindName(), customPart));
  }

}
