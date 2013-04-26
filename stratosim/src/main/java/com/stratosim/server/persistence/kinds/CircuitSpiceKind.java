package com.stratosim.server.persistence.kinds;

import com.stratosim.shared.filemodel.DownloadFormat;

public class CircuitSpiceKind extends AbstractDownloadFormatKind<CircuitSpiceKind> {

  public CircuitSpiceKind() {
    super(DownloadFormat.SPICE);
  }

}
