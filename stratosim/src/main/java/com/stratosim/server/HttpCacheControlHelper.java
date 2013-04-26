package com.stratosim.server;

import javax.servlet.http.HttpServletResponse;

/**
 * Adding these to responses helps to reduce load and trigger GAE edge caching and allows
 * browser caching.
 * 
 * http://blog.keepitcloud.com/2012/04/gae-google-app-engine-edge-caching-google-proxy-caching-etags
 * /
 * 
 * @author tarun
 * 
 */
public class HttpCacheControlHelper {

  // These are in seconds.
  public static final int FOREVER_TIME = 31449600; // 364 days
  public static final int LIMITED_TIME = 300; // 5 minutes

  private HttpCacheControlHelper() {
    throw new UnsupportedOperationException("not instantiable");
  }

  /**
   * Content can be cached only by the browser and viewed by the end user.
   * 
   * @param resp
   */
  public final static void makeCachedPrivateForever(HttpServletResponse response) {
    response.setHeader("Cache-Control", "private, max-age=" + FOREVER_TIME);
  }

  /**
   * Content can be cached anywhere and viewed by anyone.
   * 
   * @param resp
   */
  public final static void makeCachedPublicForever(HttpServletResponse response) {
    response.setHeader("Cache-Control", "public, max-age=" + FOREVER_TIME);
    response.setHeader("Pragma", "Public");
  }

  /**
   * Content can be cached only by the browser and viewed by the end user. Will expire quickly so
   * only useful for high load requests. (Rather useless).
   * 
   * @param resp
   */
  public final static void makeCachedPrivateLimited(HttpServletResponse response) {
    response.setHeader("Cache-Control", "private, max-age=" + LIMITED_TIME);
  }

  /**
   * Content can be cached anywhere and viewed by anyone. Will expire quickly so only useful for
   * high load requests.
   * 
   * @param resp
   */
  public final static void makeCachedPublicLimited(HttpServletResponse response) {
    response.setHeader("Cache-Control", "public, max-age=" + LIMITED_TIME);
    response.addHeader("Pragma", "Public");
  }

  /**
   * Content can be cached by our servers during the lifetime of this instance. This is mainly for
   * static pages that we have short lifetimes for outside of the instance, but don't need to
   * reprocess during the instance lifetime. The response cache handles this.
   * 
   * @param resp
   */
  public final static void makeCachedInternalForever(HttpServletResponse response) {
    response.addHeader("X-Internal-Cache-Control", "max-age=" + FOREVER_TIME);
  }
}
