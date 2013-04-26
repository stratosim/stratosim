package com.stratosim.server.responsecache;

import static java.lang.Math.max;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

class ResponseCacheResponseWrapper extends HttpServletResponseWrapper {

  private ServletOutputStream outputStream;
  private PrintWriter writer;
  private ServletOutputStreamCopier copier;

  // We don't wrap everything because our responses don't call those
  // methods. This will prevent us from caching anything that does
  // use those methods.
  // TODO(tpondich): Wrap everything.
  private boolean isSafelyWrapped = true;

  private int maxAge = 0;
  private int internalMaxAge = 0;

  private CacheableResponseData responseData;

  ResponseCacheResponseWrapper(HttpServletResponse response) {
    super(response);

    responseData = new CacheableResponseData();
  }

  @Override
  public void addHeader(String headerType, String headerValue) {
    super.addHeader(headerType, headerValue);

    addHeaderInternal(headerType, headerValue);
  }

  private void addHeaderInternal(String headerType, String headerValue) {
    interceptHeaders(headerType, headerValue);

    responseData.addHeader(headerType.toLowerCase(), headerValue);
  }

  @Override
  public void addDateHeader(String name, long date) {
    super.addDateHeader(name, date);

    addHeaderInternal(name, Long.toString(date));
  }

  @Override
  public void addIntHeader(String name, int value) {
    super.addIntHeader(name, value);

    addHeaderInternal(name, Integer.toString(value));
  }

  @Override
  public void setHeader(String headerType, String headerValue) {
    super.setHeader(headerType, headerValue);

    setHeaderInternal(headerType, headerValue);
  }

  @Override
  public void setDateHeader(String name, long date) {
    super.setDateHeader(name, date);

    setHeaderInternal(name, Long.toString(date));
  }

  @Override
  public void setIntHeader(String name, int value) {
    super.setIntHeader(name, value);

    setHeaderInternal(name, Integer.toString(value));
  }

  // TODO(tpondich): Flagging repeated code. Next release of guava will have this.
  private static int parseIntOrZero(String string) {
    try {
      return Integer.parseInt(string);
    } catch (NumberFormatException ex) {
      return 0;
    }
  }

  private static int getCacheControlMaxAge(String headerValue) {
    String headerValueLower = headerValue.toLowerCase();

    if (headerValueLower.contains("max-age")) {
      String maxAgeString = headerValueLower.substring(headerValueLower.indexOf("max-age") + 8);
      if (maxAgeString.indexOf(",") != -1) {
        maxAgeString = maxAgeString.substring(0, maxAgeString.indexOf(","));
      }

      return parseIntOrZero(maxAgeString);
    }

    return 0;
  }

  private void interceptHeaders(String headerType, String headerValue) {

    // Note that header names are case insensitive, but header values may be case sensitive.
    // Header values for cache control, however, appear to be case insensitive.

    String headerTypeLower = headerType.toLowerCase();

    if (headerTypeLower.equals("cache-control")) {
      maxAge = getCacheControlMaxAge(headerValue);

    } else if (headerTypeLower.equals("x-internal-cache-control")) {
      internalMaxAge = getCacheControlMaxAge(headerValue);

    }

  }

  private void setHeaderInternal(String headerType, String headerValue) {
    interceptHeaders(headerType, headerValue);

    responseData.setHeader(headerType.toLowerCase(), headerValue);
  }

  @Override
  public void setContentType(String type) {
    super.setContentType(type);

    responseData.setContentType(type);
  }

  @Override
  public void setCharacterEncoding(String charset) {
    super.setCharacterEncoding(charset);

    responseData.setCharacterEncoding(charset);
  }

  @Override
  public void setContentLength(int len) {
    super.setContentLength(len);

    responseData.setContentLength(len);
  }

  @Override
  public void setLocale(Locale loc) {
    super.setLocale(loc);

    responseData.setLocale(loc);
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (writer != null) {
      throw new IllegalStateException("getWriter() has already been called on this response.");
    }

    if (outputStream == null) {
      outputStream = getResponse().getOutputStream();
      copier = new ServletOutputStreamCopier(outputStream);
    }

    return copier;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (outputStream != null) {
      throw new IllegalStateException("getOutputStream() has already been called on this response.");
    }

    if (writer == null) {
      copier = new ServletOutputStreamCopier(getResponse().getOutputStream());
      writer =
          new PrintWriter(new OutputStreamWriter(copier, getResponse().getCharacterEncoding()),
              true);
    }

    return writer;
  }

  @Override
  public void addCookie(Cookie cookie) {
    super.addCookie(cookie);

    isSafelyWrapped = false;
  }

  @Override
  public void sendError(int sc) throws IOException {
    super.sendError(sc);

    isSafelyWrapped = false;
  }

  @Override
  public void sendError(int sc, String msg) throws IOException {
    super.sendError(sc, msg);

    isSafelyWrapped = false;
  }

  @Override
  public void sendRedirect(String location) throws IOException {
    super.sendRedirect(location);

    isSafelyWrapped = false;
  }

  @Override
  public void setStatus(int sc) {
    super.setStatus(sc);

    isSafelyWrapped = false;
  }

  @Override
  public void setStatus(int sc, String sm) {
    super.setStatus(sc, sm);

    isSafelyWrapped = false;
  }

  @Override
  public void flushBuffer() throws IOException {
    if (writer != null) {
      writer.flush();
    } else if (outputStream != null) {
      copier.flush();
    }
  }



  CacheableResponseData getResponseData() {
    if (copier != null) {
      responseData.setContent(copier.getCopy());
    }

    return responseData;
  }

  int getMaxOfInternalMaxAgeAndMaxAge() {
    return max(internalMaxAge, maxAge);
  }

  boolean isSafelyWrapped() {
    return isSafelyWrapped;
  }

}
