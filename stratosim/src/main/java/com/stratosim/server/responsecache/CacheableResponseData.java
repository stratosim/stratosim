package com.stratosim.server.responsecache;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

// TODO(tpondich): Use the immutable / builder pattern for this.
class CacheableResponseData {
  private byte[] content = new byte[0];
  
  // TODO(tpondich): Does the actual Response allow nulls?
  private String contentType = null;
  private int contentLength = -1;
  private Locale locale = null;
  private String characterEncoding = null;
  
  private Multimap<String, String> headers = ArrayListMultimap.create();

  CacheableResponseData() {

  }

  void setContent(byte[] content) {
    this.content = content;
  }

  void setContentType(String contentType) {
    checkNotNull(contentType);
    this.contentType = contentType;
  }
  
  void setContentLength(int contentLength) {
    this.contentLength = contentLength;
  }

  void setLocale(Locale locale) {
    this.locale = locale;
  }

  void setCharacterEncoding(String characterEncoding) {    
    this.characterEncoding = characterEncoding;
  }

  void setHeader(String key, String value) {
    checkNotNull(key);
    checkNotNull(value);

    headers.removeAll(key);
    headers.put(key, value);
  }
  
  void addHeader(String key, String value) {
    checkNotNull(key);
    checkNotNull(value);

    headers.put(key, value);
  }

  void populateResponse(HttpServletResponse response) throws IOException {
    if (contentType != null) {
      response.setContentType(contentType);
    }
    
    if (contentLength >= 0) {
      response.setContentLength(contentLength);
    }
    
    if (locale != null) {
      response.setLocale(locale);
    }
    
    if (characterEncoding != null) {
      response.setCharacterEncoding(characterEncoding);
    }

    for (Map.Entry<String, String> header : headers.entries()) {
      response.addHeader(header.getKey(), header.getValue());
    }

    OutputStream out = response.getOutputStream();
    out.write(content);
    out.flush();
  }

  int getSize() {
    return content.length;
  }
}
