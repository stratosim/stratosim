package com.stratosim.server.serving;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Blob;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;
import com.stratosim.client.filemanager.DownloadAvailableService;
import com.stratosim.server.UsersHelper;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class DownloadAvailableServiceImpl extends XsrfProtectedServiceServlet
    implements DownloadAvailableService {
  
  private static final long serialVersionUID = 5437052849426893065L;

  private static final Logger logger = Logger.getLogger(
    DownloadAvailableServiceImpl.class.getCanonicalName());

  @Override
  public boolean isAvailable(VersionMetadataKey versionKey, DownloadFormat format) {
    checkNotNull(versionKey);
    checkNotNull(format);
    
    logger.log(Level.INFO, "versionKey: " + versionKey);
    logger.log(Level.INFO, "format: " + format);
    
    Blob blob = null;
    VersionMetadata metadata = null;
    
    try {
      // This does actually do a fetch, but since it warms the cache, the total datastore ops should be about the same
      // after the actual download request.
      blob = PersistenceLayerFactory.createUserLayer(UsersHelper.getCurrentUser(getThreadLocalRequest()))
          .getCircuitDataFromVersion(versionKey, format);
      metadata = PersistenceLayerFactory.createUserLayer(UsersHelper.getCurrentUser(getThreadLocalRequest()))
          .getVersion(versionKey);
    } catch (PersistenceException ex) {
      logger.log(Level.WARNING, "Circuit download service could not find format " + format + " for " + versionKey);
    } catch (AccessException acex) {
      logger.log(Level.WARNING, "Access exeption in circuit download service", acex);
    }

    return blob != null && metadata != null;
  }
}
