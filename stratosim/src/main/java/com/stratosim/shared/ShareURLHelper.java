package com.stratosim.shared;


import com.stratosim.shared.filemodel.StratoSimKey;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class ShareURLHelper {

  private ShareURLHelper() {
    throw new UnsupportedOperationException("not instantiable");
  }

  private static final String DOWNLOAD_SERVICE_HANDLER = AppPaths.PUBLIC_PATH + "/download";
  private static final String EMBED_SERVICE_HANDLER = AppPaths.PUBLIC_PATH + "/embed";
  private static final String SIMULATION_DATATABLE_SERVICE_HANDLER = AppPaths.PUBLIC_PATH + "/datasource";

  private static final String FILE_ANCHOR = AppPaths.APP_PATH + "/#file";
  private static final String VERSION_ANCHOR = AppPaths.APP_PATH + "/#version";
  private static final String SIMULATE_ANCHOR = AppPaths.APP_PATH + "/#simulate";
  private static final String SIMULATEVERSION_ANCHOR = AppPaths.APP_PATH + "/#simulateversion";

  public static String getCircuitDownloadServiceUrl(StratoSimKey key, DownloadFormat format) {
    if (key instanceof FileKey) {
      return getCircuitDownloadServiceUrlInternal((FileKey) key, format);
    } else if (key instanceof VersionMetadataKey) {
      return getCircuitDownloadServiceUrlInternal((VersionMetadataKey) key, format);
    } else {
      throw new IllegalArgumentException();
    }
  }

  private static String getCircuitDownloadServiceUrlInternal(FileKey fileKey, DownloadFormat format) {
    return DOWNLOAD_SERVICE_HANDLER + "/" + format.getFormat() + "/" + fileKey.get();
  }

  private static String getCircuitDownloadServiceUrlInternal(VersionMetadataKey versionMetadataKey,
      DownloadFormat format) {
    return DOWNLOAD_SERVICE_HANDLER + "/" + format.getFormat() + "/" + versionMetadataKey.get();
  }


  public static String getSimulateLinkUrl(StratoSimKey key) {
    if (key instanceof FileKey) {
      return getSimulateLinkUrlInternal((FileKey) key);
    } else if (key instanceof VersionMetadataKey) {
      return getSimulateLinkUrlInternal((VersionMetadataKey) key);
    } else {
      throw new IllegalArgumentException();
    }
  }

  public static String getCircuitLinkUrl(StratoSimKey key) {
    if (key instanceof FileKey) {
      return getCircuitLinkUrlInternal((FileKey) key);
    } else if (key instanceof VersionMetadataKey) {
      return getCircuitLinkUrlInternal((VersionMetadataKey) key);
    } else {
      throw new IllegalArgumentException();
    }
  }

  public static String getEmbedServiceUrl(StratoSimKey key) {
    if (key instanceof FileKey) {
      return getEmbedServiceUrlInternal((FileKey) key);
    } else if (key instanceof VersionMetadataKey) {
      return getEmbedServiceUrlInternal((VersionMetadataKey) key);
    } else {
      throw new IllegalArgumentException();
    }
  }

  private static String getCircuitLinkUrlInternal(FileKey fileKey) {
    return FILE_ANCHOR + "/" + fileKey.get();
  }

  private static String getCircuitLinkUrlInternal(VersionMetadataKey versionMetadataKey) {
    return VERSION_ANCHOR + "/" + versionMetadataKey.get();
  }

  private static String getSimulateLinkUrlInternal(FileKey fileKey) {
    return SIMULATE_ANCHOR + "/" + fileKey.get();
  }

  private static String getSimulateLinkUrlInternal(VersionMetadataKey versionMetadataKey) {
    return SIMULATEVERSION_ANCHOR + "/" + versionMetadataKey.get();
  }

  private static String getEmbedServiceUrlInternal(FileKey fileKey) {
    return EMBED_SERVICE_HANDLER + "/" + fileKey.get();
  }

  private static String getEmbedServiceUrlInternal(VersionMetadataKey versionMetadataKey) {
    return EMBED_SERVICE_HANDLER + "/" + versionMetadataKey.get();
  }

  public static String getEmbedServiceUrl(VersionMetadataKey versionMetadataKey) {
    return EMBED_SERVICE_HANDLER + "/" + versionMetadataKey.get();
  }

  public static String getSimulationDataTableServiceUrl(VersionMetadataKey versionMetadataKey) {
    return SIMULATION_DATATABLE_SERVICE_HANDLER + "?key=" + versionMetadataKey.get();
  }

}
