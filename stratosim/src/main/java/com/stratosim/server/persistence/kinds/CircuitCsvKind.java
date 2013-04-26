package com.stratosim.server.persistence.kinds;

import com.stratosim.shared.filemodel.DownloadFormat;

public class CircuitCsvKind extends AbstractDownloadFormatKind<CircuitCsvKind> {

  public CircuitCsvKind() {
    super(DownloadFormat.CSV);
  }

}
