package com.stratosim.server.persistence.kinds;

import com.stratosim.shared.filemodel.DownloadFormat;

public class CircuitSimulationPngKind extends AbstractDownloadFormatKind<CircuitSimulationPngKind> {

  public CircuitSimulationPngKind() {
    super(DownloadFormat.SIMULATIONPNG);
  }

}
