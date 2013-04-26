package com.stratosim.server.persistence.impl.helpers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.stratosim.server.persistence.proto.Persistence.VersionMetadataPb;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.googlecode.objectify.cache.CacheControl;
import com.googlecode.objectify.cache.CachingAsyncDatastoreService;
import com.googlecode.objectify.cache.CachingDatastoreServiceFactory;
import com.googlecode.objectify.cache.EntityMemcache;
import com.stratosim.server.Namespace;
import com.stratosim.server.persistence.kinds.DeletedFileKind;
import com.stratosim.server.persistence.kinds.FileRoleKind;
import com.stratosim.server.persistence.kinds.FileVersionsKind;
import com.stratosim.server.persistence.kinds.UserFileKind;
import com.stratosim.server.persistence.kinds.WhitelistedUserKind;
import com.stratosim.server.persistence.schema.CustomKeyFactory;
import com.stratosim.server.persistence.schema.Kind;
import com.stratosim.server.persistence.schema.MemcacheCacheable;
import com.stratosim.server.persistence.schema.PKey;
import com.stratosim.shared.DeletingWithCollaboratorsException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.RemovingLastOwnerException;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class DatastoreHelper {

  private static final Logger logger = Logger.getLogger(DatastoreHelper.class.getCanonicalName());

  private static final Ordering<FileRole> PERMISSIONS_DIFF_ORDERING;
  static {
    Set<FileRole> other =
        Sets.difference(ImmutableSet.copyOf(FileRole.values()), ImmutableSet.of(FileRole.OWNER));
    PERMISSIONS_DIFF_ORDERING =
        Ordering.explicit(FileRole.OWNER, Iterables.toArray(other, FileRole.class));
  }

  private final DatastoreService datastore;
  
  private final CachingAsyncDatastoreService cachingAsyncDatastore;

  private final AsyncMemcacheService asyncMemcache;

  public DatastoreHelper() {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.cachingAsyncDatastore = CachingDatastoreServiceFactory.getAsyncDatastoreService(
        new EntityMemcache(Namespace.getCurrent().toString(), new CacheControl() {
          @Override
          public Integer getExpirySeconds(Key key) {
            if (key.getKind().equals(new FileVersionsKind().kindName())) {
              return (int) new FileVersionsKind().getExpirationDelta().getStandardSeconds();
            } else {
              logger.severe("Need to configure objectify memcaching for kind: " + key.getKind());
              return 0;
            }
          }
        }));
    this.asyncMemcache = MemcacheServiceFactory.getAsyncMemcacheService();
  }

  // ---------------------------------------------------------------------------------------------
  // General put and get
  // ---------------------------------------------------------------------------------------------

  public <KindType extends Kind<KindType> & MemcacheCacheable> PKey<KindType> overwriteOrPut(
      PKey<KindType> key, KindType kind) {
    return putCachedEntity(kind.toEntityWithKey(key), kind.getExpirationDelta());
  }

  public <KindType extends Kind<KindType>> PKey<KindType> uncachedOverwriteOrPut(
      PKey<KindType> key, KindType kind) {
    return putUncachedEntity(kind.toEntityWithKey(key));
  }

  public <KindType extends Kind<KindType> & MemcacheCacheable> KindType get(PKey<KindType> key,
      KindType kind) throws PersistenceException {
    try {
      Entity entity = getCachedEntity(key, kind.getExpirationDelta());
      kind.mergeFrom(entity);
      return kind;

    } catch (EntityNotFoundException ex) {
      logger.log(Level.WARNING, "entity not found" + ", kind: " + kind.kindName() + ", pkey: "
          + key);
      throw new PersistenceException();
    }
  }

  public <KindType extends Kind<KindType>> KindType uncachedGet(PKey<KindType> key, KindType kind)
      throws PersistenceException {
    try {
      Entity entity = getUncachedEntity(key);
      kind.mergeFrom(entity);
      return kind;

    } catch (EntityNotFoundException ex) {
      logger.log(Level.WARNING, "entity not found" + ", kind: " + kind.kindName() + ", pkey: "
          + key);
      throw new PersistenceException();
    }
  }

  public <KindType extends Kind<KindType> & MemcacheCacheable> void delete(PKey<KindType> key) {
    uncachedDelete(key);
    asyncMemcache.delete(key.dsKey());
  }

  public <KindType extends Kind<KindType>> void uncachedDelete(PKey<KindType> key) {
    datastore.delete(key.dsKey());
  }

  public <KindType extends Kind<KindType> & MemcacheCacheable> boolean hasValue(PKey<KindType> key,
      KindType kind) {
    try {
      getCachedEntity(key, kind.getExpirationDelta());
      return true;

    } catch (EntityNotFoundException ex) {
      return false;
    }
  }

  public <KindType extends Kind<KindType>> boolean uncachedHasValue(PKey<KindType> key) {
    try {
      getUncachedEntity(key);
      return true;

    } catch (EntityNotFoundException ex) {
      return false;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Permissions
  // ---------------------------------------------------------------------------------------------

  private void updatePermissionsChunk(FileKey fileKey,
      ImmutableMap<LowercaseEmailAddress, FileRole> diff) throws RemovingLastOwnerException {
    TransactionOptions options = TransactionOptions.Builder.withXG(true);
    Transaction txn = datastore.beginTransaction(options);

    try {
      updatePermissionsChunk(txn, fileKey, diff, true);
      txn.commit();

    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
  }

  // This does not actually commit the transaction. That is the responsibility
  // of the caller.
  private void updatePermissionsChunk(Transaction txn, FileKey fileKey,
      ImmutableMap<LowercaseEmailAddress, FileRole> diff, boolean checkLastOwner)
      throws RemovingLastOwnerException {

    PKey<FileRoleKind> fileRoleKey = CustomKeyFactory.fileRoleKey(fileKey);
    FileRoleKind fileRoleKind = getOrNew(txn, fileRoleKey, new FileRoleKind());

    for (LowercaseEmailAddress user : diff.keySet()) {
      PKey<UserFileKind> userFileKey = CustomKeyFactory.userFileKey(user);
      UserFileKind userFileKind = getOrNew(txn, userFileKey, new UserFileKind());

      FileRole role = diff.get(user);
      PermissionsHelper.applyPermissionDiff(fileRoleKind.properties, userFileKind.properties, user,
          fileKey, role);
      PermissionsHelper.checkNoNone(userFileKind.properties.values());

      datastore.put(txn, userFileKind.toEntityWithKey(userFileKey));
    }

    // linear in number of users file is shared with.
    if (checkLastOwner) {
      PermissionsHelper.checkAtLeastOneOwner(fileKey, fileRoleKind.properties.values());
    }
    PermissionsHelper.checkNoNone(fileRoleKind.properties.values());

    datastore.put(txn, fileRoleKind.toEntityWithKey(fileRoleKey));
  }

  public void updatePermissions(FileKey fileKey, ImmutableMap<LowercaseEmailAddress, FileRole> diff)
      throws RemovingLastOwnerException {

    ImmutableSortedSet<LowercaseEmailAddress> sortedUsers =
        ImmutableSortedSet.copyOf(PERMISSIONS_DIFF_ORDERING.onResultOf(Functions.forMap(diff)),
            diff.keySet());

    // Divide the permissions diff into chunks of cross-group transaction-compatible size.
    // TODO(josh): divide this non-trivially
    for (LowercaseEmailAddress user : sortedUsers) {
      FileRole role = diff.get(user);
      updatePermissionsChunk(fileKey, ImmutableMap.of(user, role));
    }
  }

  public ImmutableMap<LowercaseEmailAddress, FileRole> setPermissions(FileKey fileKey,
      ImmutableMap<LowercaseEmailAddress, FileRole> permissions) throws RemovingLastOwnerException {
    PKey<FileRoleKind> fileRoleKey = CustomKeyFactory.fileRoleKey(fileKey);
    Map<LowercaseEmailAddress, FileRole> oldPermissions =
        uncachedGetOrNew(fileRoleKey, new FileRoleKind()).properties;

    updatePermissions(fileKey,
        PermissionsHelper.computePermissionsDiff(ImmutableMap.copyOf(oldPermissions), permissions));

    return ImmutableMap.copyOf(oldPermissions);
  }

  public void deleteFile(FileKey fileKey) throws DeletingWithCollaboratorsException {
    TransactionOptions options = TransactionOptions.Builder.withXG(true);
    Transaction txn = datastore.beginTransaction(options);

    try {
      PKey<FileRoleKind> fileRoleKey = CustomKeyFactory.fileRoleKey(fileKey);
      FileRoleKind fileRoleKind = getOrNew(txn, fileRoleKey, new FileRoleKind());

      // Assert only 1 user, and user is owner
      try {
        LowercaseEmailAddress user = Iterables.getOnlyElement(fileRoleKind.properties.keySet());

        Preconditions.checkState(fileRoleKind.properties.get(user) == FileRole.OWNER);

        updatePermissionsChunk(txn, fileKey, ImmutableMap.of(user, FileRole.NONE), false);

        DeletedFileKind deletedFileKind = new DeletedFileKind();
        deletedFileKind.lastOwner.set(user);
        deletedFileKind.fileKey.set(fileKey);
        deletedFileKind.deletionTime.set(Instant.now());
        this.<DeletedFileKind>putUncachedEntity(txn, deletedFileKind.toEntity());

        txn.commit();
      } catch (IllegalArgumentException e) {
        throw new DeletingWithCollaboratorsException(fileKey);
      } catch (RemovingLastOwnerException e) {
        // This can't happen.
        throw new IllegalStateException();
      }

    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }

  }

  // ---------------------------------------------------------------------------------------------
  // Whitelist
  // ---------------------------------------------------------------------------------------------

  public boolean isWhitelistedUser(LowercaseEmailAddress user) {
    PKey<WhitelistedUserKind> key = CustomKeyFactory.whitelistedUserKey(user);
    return uncachedHasValue(key);
  }

  public WhitelistedUserKind getWhitelistedUser(LowercaseEmailAddress user)
      throws PersistenceException {
    PKey<WhitelistedUserKind> key = CustomKeyFactory.whitelistedUserKey(user);
    return uncachedGet(key, new WhitelistedUserKind());
  }

  public void putWhitelistedUser(LowercaseEmailAddress user) throws PersistenceException,
      IllegalArgumentException {
    PKey<WhitelistedUserKind> key = CustomKeyFactory.whitelistedUserKey(user);

    WhitelistedUserKind whitelistedUserKind = new WhitelistedUserKind();
    whitelistedUserKind.user.set(user);
    whitelistedUserKind.accessStart.set(Instant.now());

    // Check if user is already whitelisted.
    // Could use a transaction, but if the user is added in two almost simultaneous writes,
    // the accessStart times should be close enough.
    if (isWhitelistedUser(user)) {
      throw new IllegalArgumentException("user: " + user.getEmail() + " is already whitelisted.");
    }

    uncachedOverwriteOrPut(key, whitelistedUserKind);
  }

  // ---------------------------------------------------------------------------------------------
  // Version metadata
  // ---------------------------------------------------------------------------------------------

  public VersionMetadataKey addVersion(FileKey fileKey, VersionMetadataPb pb) {
    checkNotNull(fileKey);
    checkNotNull(pb);
    checkArgument(pb.hasSaverEmail());
    checkArgument(pb.hasName());
    checkArgument(pb.hasCircuitHash());

    TransactionOptions options = TransactionOptions.Builder.withDefaults();
    Transaction txn = datastore.beginTransaction(options);
    try {
      PKey<FileVersionsKind> fileVersionsKey = CustomKeyFactory.fileVersionsKey(fileKey);
      FileVersionsKind fileVersionsKind = getOrNew(txn, fileVersionsKey, new FileVersionsKind());

      Instant now = Instant.now();
      fileVersionsKind.properties.put(now, pb);
      Entity entity = fileVersionsKind.toEntityWithKey(fileVersionsKey);
      datastore.put(txn, entity);

      txn.commit();

      // only put in cache after a successful commit
      putInCache(fileVersionsKey, entity, fileVersionsKind.getExpirationDelta());

      return new VersionMetadataKey(fileKey, now.getMillis());

    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
  }

  public Future<ImmutableSortedMap<Instant, VersionMetadataPb>> asyncGetVersions(FileKey fileKey) {
    checkNotNull(fileKey);

    PKey<FileVersionsKind> fileVersionsKey = CustomKeyFactory.fileVersionsKey(fileKey);
    return lazyTransformWithDefault(cachingAsyncDatastore.get(fileVersionsKey.dsKey()),
        new Function<Entity, ImmutableSortedMap<Instant, VersionMetadataPb>>() {
          @Override
          public ImmutableSortedMap<Instant, VersionMetadataPb> apply(Entity entity) {
            FileVersionsKind fileVersionsKind = new FileVersionsKind();
            fileVersionsKind.mergeFrom(entity);
            return ImmutableSortedMap.copyOf(fileVersionsKind.properties);
          }
        }, ImmutableSortedMap.<Instant, VersionMetadataPb>of());
  }

  public ImmutableSortedMap<Instant, VersionMetadataPb> getVersions(FileKey fileKey) {
    checkNotNull(fileKey);

    PKey<FileVersionsKind> fileVersionsKey = CustomKeyFactory.fileVersionsKey(fileKey);
    FileVersionsKind fileVersionsKind = cachedGetOrNew(fileVersionsKey, new FileVersionsKind());
    return ImmutableSortedMap.copyOf(fileVersionsKind.properties);
  }

  // ---------------------------------------------------------------------------------------------
  // Query (these can be expensive. should be admin only at the moment)
  // ---------------------------------------------------------------------------------------------

  public <KindType extends Kind<KindType>> ImmutableList<PKey<KindType>> listKeys(KindType kind) {
    Query query = new Query(kind.kindName());
    return FluentIterable.from(datastore.prepare(query).asIterable())
        .transform(new Function<Entity, PKey<KindType>>() {
          @Override
          public PKey<KindType> apply(Entity entity) {
            return new PKey<KindType>(KeyFactory.keyToString(entity.getKey()));
          }
        }).toImmutableList();
  }

  // ---------------------------------------------------------------------------------------------
  // Internal
  // ---------------------------------------------------------------------------------------------

  private <KindType extends Kind<KindType>> PKey<KindType> putUncachedEntity(Entity entity) {
    String keyAsString = KeyFactory.keyToString(datastore.put(entity));
    PKey<KindType> pkey = new PKey<KindType>(keyAsString);
    return pkey;
  }

  private <KindType extends Kind<KindType>> PKey<KindType> putUncachedEntity(Transaction txn,
      Entity entity) {
    String keyAsString = KeyFactory.keyToString(datastore.put(entity));
    PKey<KindType> pkey = new PKey<KindType>(keyAsString);
    return pkey;
  }

  private <KindType extends Kind<KindType> & MemcacheCacheable> PKey<KindType> putCachedEntity(
      Entity entity, Duration delta) {
    PKey<KindType> pkey = putUncachedEntity(entity);
    putInCache(pkey, entity, delta);
    return pkey;
  }

  private <KindType extends Kind<KindType>> Entity getUncachedEntity(PKey<KindType> key)
      throws EntityNotFoundException {
    return datastore.get(key.dsKey());
  }

  private <KindType extends Kind<KindType>> Entity getUncachedEntity(Transaction txn,
      PKey<KindType> key) throws EntityNotFoundException {
    return datastore.get(txn, key.dsKey());
  }
  
  private <KindType extends Kind<KindType> & MemcacheCacheable> Entity getCachedEntity(
      PKey<KindType> key, Duration delta) throws EntityNotFoundException {
    Entity entity = getFromCacheOrNull(key);
    if (entity == null) {
      entity = getUncachedEntity(key);
      putInCache(key, entity, delta);
    }
    return entity;
  }

  @VisibleForTesting
  <KindType extends Kind<KindType> & MemcacheCacheable> KindType cachedGetOrNew(PKey<KindType> key,
      KindType kind) {
    try {
      Entity entity = getCachedEntity(key, kind.getExpirationDelta());
      kind.mergeFrom(entity);
    } catch (EntityNotFoundException ex) {
      // ignore; just return new kind.
    }

    return kind;
  }

  @VisibleForTesting
  <KindType extends Kind<KindType>> KindType uncachedGetOrNew(PKey<KindType> key, KindType kind) {
    try {
      Entity entity = getUncachedEntity(key);
      kind.mergeFrom(entity);
    } catch (EntityNotFoundException ex) {
      // ignore; just return new kind.
    }

    return kind;
  }

  @VisibleForTesting
  <KindType extends Kind<KindType>> KindType getOrNew(Transaction txn, PKey<KindType> key,
      KindType kind) {
    try {
      Entity entity = getUncachedEntity(txn, key);
      kind.mergeFrom(entity);
    } catch (EntityNotFoundException ex) {
      // ignore; just return new kind.
    }

    return kind;
  }

  private <KindType extends Kind<KindType> & MemcacheCacheable> void putInCache(PKey<KindType> key,
      Entity entity, Duration delta) {
    if (key.getAsString() == null || entity == null) {
      logger.warning("not caching null key or entity: key=" + key + ", entity=" + entity);
    } else {
      asyncMemcache.put(key.getAsString(), entity, getExpiration(delta));
    }
  }

  private <KindType extends Kind<KindType> & MemcacheCacheable> Entity getFromCacheOrNull(
      PKey<KindType> key) {
    if (key.getAsString() == null) {
      logger.warning("not fetching null key from cache");
    } else {
      try {
        return (Entity) asyncMemcache.get(key.getAsString()).get();
      } catch (InterruptedException e) {
        logger.log(Level.WARNING, "error fetching from cache: key=" + key, e);
      } catch (ExecutionException e) {
        logger.log(Level.WARNING, "error fetching from cache: key=" + key, e);
      }
    }
    return null;
  }

  private static Expiration getExpiration(Duration delta) {
    long longSeconds = delta.getStandardSeconds();
    int intSeconds = Ints.checkedCast(longSeconds);
    return Expiration.byDeltaSeconds(intSeconds);
  }
  
  private static <U, V> Future<V> lazyTransformWithDefault(final Future<U> future,
      final Function<U, V> function, final V defaultV) {
    return new Future<V>() {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
      }

      @Override
      public boolean isCancelled() {
        return future.isCancelled();
      }

      @Override
      public boolean isDone() {
        return future.isDone();
      }

      @Override
      public V get() {
        try {
          return function.apply(future.get());
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          logger.log(Level.FINE, "exception in lazyTransformWithDefault", e);
          return defaultV;
        } catch (ExecutionException e) {
          logger.log(Level.FINE, "exception in lazyTransformWithDefault", e);
          return defaultV;
        }
      }

      @Override
      public V get(long timeout, TimeUnit unit) {
        try {
          return function.apply(future.get(timeout, unit));
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          logger.log(Level.FINE, "exception in lazyTransformWithDefault", e);
          return defaultV;
        } catch (ExecutionException e) {
          logger.log(Level.FINE, "exception in lazyTransformWithDefault", e);
          return defaultV;
        } catch (TimeoutException e) {
          logger.log(Level.FINE, "exception in lazyTransformWithDefault", e);
          return defaultV;
        }
      }
    };
  }
}
