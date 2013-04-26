package com.stratosim.server.external;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.common.collect.ImmutableMap;
import com.stratosim.server.AppInfo;
import com.stratosim.shared.filemodel.CircuitHash;
import com.stratosim.shared.filemodel.DownloadFormat;

public class ExternalWorkerHelper {

  private static final Logger logger = Logger.getLogger(ExternalWorkerHelper.class
      .getCanonicalName());

  public static Queue getQueue() {
    return QueueFactory.getQueue("worker");
  }

  public static TaskOptions createTask(DownloadFormat outputFormat, DownloadFormat inputFormat,
      CircuitHash circuitHash, Blob data) {
    String WORKER_URL;
    if (AppInfo.isProduction()) {
      WORKER_URL = AppInfo.WORKER_PRODUCTION_URL;
    } else {
      WORKER_URL = AppInfo.WORKER_DEVELOPMENT_URL;
    }
    return createTaskWithCustomWorkerUrl(outputFormat, inputFormat, circuitHash, data, WORKER_URL);
  }

  public static TaskOptions createTaskWithCustomWorkerUrl(DownloadFormat outputFormat,
      DownloadFormat inputFormat, CircuitHash circuitHash, Blob data, String workerUrl) {
    checkNotNull(inputFormat);
    checkNotNull(outputFormat);
    checkNotNull(circuitHash);
    checkNotNull(data);
    checkNotNull(workerUrl);

    logger.log(Level.INFO, ImmutableMap.of(
        "inputFormat", inputFormat,
        "outputFormat", outputFormat,
        "circuitHash", circuitHash,
        "workerUrl", workerUrl).toString());

    return TaskOptions.Builder.withUrl("/w/worker")
        .method(Method.POST)
        .param("inputFormat", inputFormat.getFormat())
        .param("outputFormat", outputFormat.getFormat())
        .param("circuitHash", circuitHash.get())
        .param("data", Base64.encodeBase64URLSafeString(data.getBytes()))
        .param("workerUrl", workerUrl);
  }
}
