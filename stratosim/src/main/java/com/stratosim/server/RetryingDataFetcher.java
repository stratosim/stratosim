package com.stratosim.server;

import static com.google.common.base.Preconditions.checkState;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Blob;
import com.google.common.collect.ImmutableMap;
import com.stratosim.server.persistence.UserPersistenceLayer;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class RetryingDataFetcher {

  private static final Logger logger = Logger.getLogger(RetryingDataFetcher.class
      .getCanonicalName());

  private final VersionMetadataKey versionKey;

  private final DownloadFormat desiredFormat;

  private final UserPersistenceLayer layer;
  
  private final static int RETRY_DELAY = 2000;
  private final static int RETRIES = 10;

  private Blob desiredBlob;

  public RetryingDataFetcher(VersionMetadataKey versionKey, DownloadFormat desiredFormat,
      UserPersistenceLayer layer) {
    this.versionKey = versionKey;
    this.desiredFormat = desiredFormat;
    this.layer = layer;
  }

  public Blob getBlob() throws AccessException, PersistenceException {
    if (!haveData()) {
      fetch(RETRIES);
    }
    checkState(haveData());
    return desiredBlob;
  }

  private boolean haveData() {
    return desiredBlob != null;
  }

  private void fetch(int maxTries) throws AccessException, PersistenceException {
    try {
      simpleFetch();
    } catch (PersistenceException ex) {
      GenerateHelper.generateIfNeeded(versionKey, desiredFormat, layer);

      // TODO(tpondich): There has to be a better way to do this.
      // Right now, this ensures that if an image is embedded and never generated
      // it will generate and serve.
      for (int tries = 0; !haveData() && tries < maxTries; tries++) {
        silentWaitAndFetch();
      }

      if (!haveData()) {
        logger.log(Level.WARNING, "failed to fetch format " + desiredFormat + " for " + versionKey);
        throw new PersistenceException();
      }
    }
  }

  private void silentWaitAndFetch() throws AccessException {
    try {
      Thread.sleep(RETRY_DELAY);
    } catch (InterruptedException ex) {
      // No problem.
    }

    try {
      simpleFetch();
    } catch (PersistenceException e) {
      logger.log(
          Level.WARNING,
          "Persistence Exception while waiting: "
              + ImmutableMap.of("versionKey", versionKey, "desiredFormat", desiredFormat));
    }
  }

  private void simpleFetch() throws AccessException, PersistenceException {
    desiredBlob = layer.getCircuitDataFromVersion(versionKey, desiredFormat);
  }
}
