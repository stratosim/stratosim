package com.stratosim.shared.filemodel;

import java.io.Serializable;

public enum DownloadFormat implements Serializable {

  PS("ps", "application/postscript", "eps", false, false, false),
  PDF("pdf", "application/pdf", "pdf", false, true, false),
  PNG("png", "image/png", "png", false, true, false),
  SVG("svg", "image/svg+xml", "svg", false, true, false),
  SPICE("spice", "text/plain", "spice", true, false, false),
  THUMBNAIL("thumbnail", "image/png", "png", false, true, false),
  CSV("csv", "text/csv", "csv", false, false, true),
  SIMULATIONPS("simulationps", "application/postscript", "eps", false, false, true),
  SIMULATIONPDF("simulationpdf", "application/pdf", "pdf", false, false, true),
  SIMULATIONPNG("simulationpng", "image/png", "png", false, false, true);

  private/* final */String format;
  
  private/* final */String mimetype;
  private/* final */String dottedExtension;
  private/* final */boolean isAttachment;
  
  private/* final */boolean isRenderableFromPS;
  private/* final */boolean isRenderableFromSPICE;

  private DownloadFormat() {

  }

  private DownloadFormat(String format, String mimetype, String dottedExtension, boolean isAttachment, boolean isRenderableFromPS, boolean isRenderableFromSPICE) {
    this.format = format;
    
    this.mimetype = mimetype;
    this.dottedExtension = dottedExtension;
    
    this.isAttachment = isAttachment;
    
    this.isRenderableFromPS = isRenderableFromPS;
    this.isRenderableFromSPICE = isRenderableFromSPICE;
  }

  public String getFormat() {
    return format;
  }

  public String getExtension() {
    return dottedExtension;
  }
  
  public String getDottedExtension() {
    return "." + dottedExtension;
  }

  public String getMimeType() {
    return mimetype;
  }

  public boolean isAttachment() {
    return isAttachment;
  }

  public boolean isRenderableFromPS() {
    return isRenderableFromPS;
  }
  
  public boolean isRenderableFromSPICE() {
    return isRenderableFromSPICE;
  }
  
  public static DownloadFormat from(String s) {
    return DownloadFormat.valueOf(s.toUpperCase());
  }
}
