package com.stratosim.server.persistence.kinds;

import com.stratosim.shared.filemodel.DownloadFormat;

public class CircuitSimulationPdfKind extends AbstractDownloadFormatKind<CircuitSimulationPdfKind> {

  public CircuitSimulationPdfKind() {
    super(DownloadFormat.SIMULATIONPDF);
  }

}
