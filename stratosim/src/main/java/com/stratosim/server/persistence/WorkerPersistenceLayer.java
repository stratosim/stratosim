package com.stratosim.server.persistence;

import com.google.appengine.api.datastore.Blob;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.filemodel.CircuitHash;
import com.stratosim.shared.filemodel.DownloadFormat;

/**
 * For internal processing.
 */
public interface WorkerPersistenceLayer {

  Circuit getCircuitWithoutKeys(CircuitHash circuitHash) throws PersistenceException;

  Blob getRenderedCircuit(CircuitHash circuitHash, DownloadFormat format) throws PersistenceException;

  void putRenderedCircuit(CircuitHash circuitHash, DownloadFormat format, Blob data)
      throws PersistenceException;

  Blob getSimulatedCircuit(CircuitHash circuitHash,  DownloadFormat format) throws PersistenceException;

  void putSimulatedCircuit(CircuitHash circuitHash, DownloadFormat format, Blob data)
      throws PersistenceException;

}
