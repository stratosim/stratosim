package com.stratosim.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Blob;
import com.stratosim.server.external.ExternalWorkerHelper;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.server.persistence.UserPersistenceLayer;
import com.stratosim.server.persistence.WorkerPersistenceLayer;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.filemodel.CircuitHash;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public final class GenerateHelper {

  private static final Logger logger = Logger.getLogger(GenerateHelper.class.getCanonicalName());

  private GenerateHelper() {
    throw new UnsupportedOperationException("not instantiable");
  }

  /**
   * If the requested format is cached in the datastore this returns true. If it's not in the
   * datastore, but can be generated here, it will generate the data and return true. If it needs to
   * be generated externally, it will add the generation task to the queue and return false.
   */
  public static final boolean generateIfNeeded(VersionMetadataKey versionKey, DownloadFormat format,
      UserPersistenceLayer userLayer) throws AccessException,
      PersistenceException {

    logger.log(Level.INFO, "versionKey: " + versionKey);
    logger.log(Level.INFO, "format: " + format);

    try {
      userLayer.getCircuitDataFromVersion(versionKey, format);
      return true;
      
    } catch (PersistenceException e) {
      // This format does not currently exist. Let's generate it.
      logger.log(Level.INFO, "datastore miss");

      CircuitHash circuitHash;
      try {
        circuitHash = userLayer.getCircuitHash(versionKey);
      } catch (PersistenceException e1) {
        throw new IllegalStateException();
      }

      if (format.isRenderableFromPS()) {
        Blob result;
        try {
          result = userLayer.getCircuitDataFromVersion(versionKey, DownloadFormat.PS);
          logger.log(Level.INFO, "postscript hit");
        } catch (PersistenceException e2) {
          // PostScript doesn't exist. Let's generate it first.
          result = generateAndStorePostScript(circuitHash, userLayer.getCircuit(versionKey));
          // This should not happen since postscript is generated on save.
          logger.log(Level.WARNING, "postscript miss");
        }

        ExternalWorkerHelper.getQueue().add(
            ExternalWorkerHelper.createTask(format, DownloadFormat.PS, circuitHash, result));

      } else if (format.isRenderableFromSPICE()) {
        Blob result;
        try {
          result = userLayer.getCircuitDataFromVersion(versionKey, DownloadFormat.SPICE);
          logger.log(Level.INFO, "spice hit");
        } catch (PersistenceException e2) {
          // Spice doesn't exist. Let's generate it first.
          result = generateAndStoreSpice(circuitHash, userLayer.getCircuit(versionKey));
          logger.log(Level.INFO, "spice miss");
        }

        ExternalWorkerHelper.getQueue().add(
            ExternalWorkerHelper.createTask(format, DownloadFormat.SPICE, circuitHash, result));
      } else {
        if (format == DownloadFormat.PS) {
          generateAndStorePostScript(circuitHash, userLayer.getCircuit(versionKey));
          logger.log(Level.INFO, "postscript miss");
          return true;

        } else if (format == DownloadFormat.SPICE) {
          generateAndStoreSpice(circuitHash, userLayer.getCircuit(versionKey));
          // This should not happen since postscript is generated on save.
          logger.log(Level.WARNING, "spice miss");
          return true;

        } else {
          throw new IllegalArgumentException(format.getFormat());
        }

      }

    }

    return false;
  }

  private static final Blob generateAndStorePostScript(CircuitHash circuitHash, Circuit circuit)
      throws PersistenceException {
    WorkerPersistenceLayer workerLayer = PersistenceLayerFactory.createInternalLayer();
    Blob blob = new Blob(CircuitToPostScript.convert(circuit).getBytes());
    workerLayer.putRenderedCircuit(circuitHash, DownloadFormat.PS, blob);

    return blob;
  }

  private static final Blob generateAndStoreSpice(CircuitHash circuitHash, Circuit circuit)
      throws PersistenceException {
    WorkerPersistenceLayer workerLayer = PersistenceLayerFactory.createInternalLayer();
    Blob blob = new Blob(CircuitToSpice.convert(circuit).getBytes());
    workerLayer.putRenderedCircuit(circuitHash, DownloadFormat.SPICE, blob);

    return blob;
  }
}
