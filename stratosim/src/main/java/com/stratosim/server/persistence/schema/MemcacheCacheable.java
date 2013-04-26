package com.stratosim.server.persistence.schema;

import org.joda.time.Duration;

/**
 * This will allow entities to be cached in memcache using a write through cache.
 * This means that all writes go to the datastore and update the cache, and reads
 * will read from memcache if memcache if possible. If an entity is not in 
 * memcache, it is fetched from the datastore and put in memcache. Since memcache
 * is shared between instances and consistent, any kinds that do not run in transactions
 * can implement this interface.
 */
public interface MemcacheCacheable {
  
  /**
   * Returns a {@link Duration} representing the amount of time this cached value will be valid
   * in the cache. The expiration time will be now + delta.
   * 
   * Expiration time is precise to ~1 second.
   * @see http://code.google.com/appengine/docs/java/javadoc/com/google/appengine/api/memcache/Expiration.html
   * 
   * Also, expiration is specified as an {@code int} number of seconds, so do not use anything
   * larger than ~68 years.
   * @see https://www.google.com/search?q=2147483647%20seconds%20in%20years
   */
  Duration getExpirationDelta();

}
