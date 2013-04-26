package com.stratosim.server.serving;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;
import com.stratosim.client.filemanager.TriggerDownloadService;
import com.stratosim.server.GenerateHelper;
import com.stratosim.server.UsersHelper;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class TriggerDownloadServiceImpl extends XsrfProtectedServiceServlet
    implements
      TriggerDownloadService {
  private static final long serialVersionUID = -7121431363303301056L;

  private static final Logger logger = Logger.getLogger(TriggerDownloadServiceImpl.class
      .getCanonicalName());

  /**
   * If the requested format is cached in the datastore this returns true. If it's not in the
   * datastore, but can be generated here, it will generate the data and return true. If it needs to
   * be generated externally, it will add the generation task to the queue and return false.
   */
  @Override
  public boolean trigger(VersionMetadataKey versionKey, DownloadFormat format)
      throws AccessException, PersistenceException {

    logger.log(Level.INFO, "versionKey: " + versionKey);
    logger.log(Level.INFO, "format: " + format);

    return GenerateHelper.generateIfNeeded(versionKey, format, PersistenceLayerFactory.createUserLayer(UsersHelper
      .getCurrentUser(getThreadLocalRequest())));
  }
}
