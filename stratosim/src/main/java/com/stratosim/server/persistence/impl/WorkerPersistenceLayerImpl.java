package com.stratosim.server.persistence.impl;

import com.google.appengine.api.datastore.Blob;
import com.stratosim.server.persistence.WorkerPersistenceLayer;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.filemodel.CircuitHash;
import com.stratosim.shared.filemodel.DownloadFormat;

public class WorkerPersistenceLayerImpl extends RealPersistenceLayerWrapper
    implements
      WorkerPersistenceLayer {

  @Override
  public Circuit getCircuitWithoutKeys(CircuitHash circuitHash) throws PersistenceException {
    return getReal().getCircuitWithoutKeys(circuitHash);
  }

  @Override
  public Blob getRenderedCircuit(CircuitHash circuitHash, DownloadFormat format)
      throws PersistenceException {
    return getReal().getRenderedCircuit(circuitHash, format);
  }

  @Override
  public void putRenderedCircuit(CircuitHash circuitHash, DownloadFormat format, Blob data)
      throws PersistenceException {
    getReal().putCircuitData(circuitHash, format, data);
  }

  @Override
  public Blob getSimulatedCircuit(CircuitHash circuitHash, DownloadFormat format)
      throws PersistenceException {
    return getReal().getCircuitData(circuitHash, format);
  }

  @Override
  public void putSimulatedCircuit(CircuitHash circuitHash, DownloadFormat format, Blob data)
      throws PersistenceException {
    getReal().putCircuitData(circuitHash, format, data);
  }
}
