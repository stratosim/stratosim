package com.stratosim.server.responsecache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

class ServletOutputStreamCopier extends ServletOutputStream {

  private final ServletOutputStream servletOutputStream;
  private final ByteArrayOutputStream copy;

  public ServletOutputStreamCopier(ServletOutputStream servletOutputStream) {
    this.servletOutputStream = servletOutputStream;
    this.copy = new ByteArrayOutputStream();
  }

  @Override
  public void write(int b) throws IOException {
    servletOutputStream.write(b);
    copy.write(b);
  }

  public byte[] getCopy() {
    return copy.toByteArray();
  }

}