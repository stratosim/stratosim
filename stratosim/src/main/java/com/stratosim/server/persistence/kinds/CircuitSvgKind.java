package com.stratosim.server.persistence.kinds;

import com.stratosim.shared.filemodel.DownloadFormat;

public class CircuitSvgKind extends AbstractDownloadFormatKind<CircuitSvgKind> {

  public CircuitSvgKind() {
    super(DownloadFormat.SVG);
  }

}
