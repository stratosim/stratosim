package com.stratosim.server.persistence.kinds;

import com.stratosim.shared.filemodel.DownloadFormat;

public class CircuitSimulationPsKind extends AbstractDownloadFormatKind<CircuitSimulationPsKind> {

  public CircuitSimulationPsKind() {
    super(DownloadFormat.SIMULATIONPS);
  }

}
