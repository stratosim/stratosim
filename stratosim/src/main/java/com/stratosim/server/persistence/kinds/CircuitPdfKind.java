package com.stratosim.server.persistence.kinds;

import com.stratosim.shared.filemodel.DownloadFormat;

public class CircuitPdfKind extends AbstractDownloadFormatKind<CircuitPdfKind> {

  public CircuitPdfKind() {
    super(DownloadFormat.PDF);
  }

}
