package com.stratosim.server.responsecache;

import static com.stratosim.server.AppInfo.isProduction;
import static com.stratosim.server.HttpCacheControlHelper.FOREVER_TIME;
import static com.stratosim.server.HttpCacheControlHelper.LIMITED_TIME;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;

/**
 * 
 * This filter will look at response headers and act as a caching proxy within each instance. In
 * order to trigger the cache, the cache-control header must have max-age set to either of the
 * constants set in HttpCacheControlHelper. This is guaranteed by only using HttpCacheControlHelper
 * to generate cache headers.
 * 
 * The cache depends on the Cache-Control and X-Internal-Cache-Control headers' max-age parameter.
 * It will use the max max-age present. The X-Internal-Cache-Control is useful for keeping instance
 * static content cached for the instance lifetime while having browsers revalidate much more
 * frequently.
 * 
 * This filter does not support all the methods of HttpServletResponse. If any of those methods are
 * used, this will print a warning and not utilize the cache. This filter has been spot checked, but
 * not yet unit tested.
 * 
 * This filter will not cache any data in development mode.
 * 
 * @author tarun
 * 
 */
public class ResponseCacheFilter implements Filter {

  private static final Logger logger = Logger.getLogger(ResponseCacheFilter.class
      .getCanonicalName());

  private Cache<String, CacheableResponseData> foreverCache;
  private Cache<String, CacheableResponseData> limitedCache;

  // In bytes. Not a hard limit, only counts data not the headers and object size, etc.
  private final static long MAX_DATA_BYTES_FOREVER_CACHE = 1 * 1024L * 1024L;
  private final static long MAX_DATA_BYTES_LIMITED_CACHE = 50 * 1024L * 1024L;

  @Override
  public void init(FilterConfig arg0) throws ServletException {
    Weigher<String, CacheableResponseData> cacheableResponseDataWeigher =
        new Weigher<String, CacheableResponseData>() {
          public int weigh(String k, CacheableResponseData data) {
            return data.getSize();
          }
        };

    foreverCache =
        CacheBuilder.newBuilder().maximumWeight(MAX_DATA_BYTES_FOREVER_CACHE)
            .weigher(cacheableResponseDataWeigher).build();

    limitedCache =
        CacheBuilder.newBuilder().maximumWeight(MAX_DATA_BYTES_LIMITED_CACHE)
            .weigher(cacheableResponseDataWeigher).expireAfterWrite(LIMITED_TIME, SECONDS).build();
  }

  @Override
  public void destroy() {
    foreverCache = null;
    limitedCache = null;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) req;
    HttpServletResponse httpResponse = (HttpServletResponse) resp;

    String key = httpRequest.getRequestURI();
    if (httpRequest.getQueryString() != null) {
      key += "?" + (httpRequest.getQueryString());
    }

    Optional<CacheableResponseData> foreverCachedOrNull =
        Optional.fromNullable(foreverCache.getIfPresent(key));
    Optional<CacheableResponseData> limitedCachedOrNull =
        Optional.fromNullable(limitedCache.getIfPresent(key));

    Optional<CacheableResponseData> cacheOrNull = foreverCachedOrNull.or(limitedCachedOrNull);

    if (cacheOrNull.isPresent()) {
      cacheOrNull.get().populateResponse(httpResponse);
      logger.log(Level.INFO, "Serving from front end forever cache.");

    } else {
      ResponseCacheResponseWrapper responseCacheResponseWrapper =
          new ResponseCacheResponseWrapper(httpResponse);

      chain.doFilter(req, responseCacheResponseWrapper);

      CacheableResponseData generated = responseCacheResponseWrapper.getResponseData();

      // Only cache in production because it's really annoying to restart the dev server all
      // the time.
      if (responseCacheResponseWrapper.isSafelyWrapped() && isProduction()) {
        if (responseCacheResponseWrapper.getMaxOfInternalMaxAgeAndMaxAge() == FOREVER_TIME) {
          foreverCache.put(key, generated);
        } else if (responseCacheResponseWrapper.getMaxOfInternalMaxAgeAndMaxAge() == LIMITED_TIME) {
          limitedCache.put(key, generated);
        } else if (responseCacheResponseWrapper.getMaxOfInternalMaxAgeAndMaxAge() != 0) {
          logger.log(Level.WARNING, "Max-age is not set to a front end cacheable value!");
        }

      } else if (responseCacheResponseWrapper.getMaxOfInternalMaxAgeAndMaxAge() != 0) {
        logger.log(Level.WARNING,
            "Could not cache this response because it calls unsupported methods.");
      }

    }

  }

}
