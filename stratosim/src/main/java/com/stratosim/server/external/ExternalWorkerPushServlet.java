package com.stratosim.server.external;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.Instant;

import com.google.appengine.api.datastore.Blob;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Closeables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stratosim.server.AppInfo;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.server.persistence.WorkerPersistenceLayer;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.CircuitHash;
import com.stratosim.shared.filemodel.DownloadFormat;

public class ExternalWorkerPushServlet extends HttpServlet {
  private static final long serialVersionUID = 6517788914863710358L;

  private static final Logger logger = Logger.getLogger(ExternalWorkerPushServlet.class
      .getCanonicalName());

  private static final Joiner BODY_KV_JOINER = Joiner.on("=");

  private static final Joiner BODY_ARGS_JOINER = Joiner.on("&");

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws IOException {
    String circuitHashIn = checkNotNull(request.getParameter("circuitHash"));
    String inputFormatIn = checkNotNull(request.getParameter("inputFormat"));
    String outputFormatIn = checkNotNull(request.getParameter("outputFormat"));
    String dataIn = checkNotNull(request.getParameter("data"));
    String workerUrl = checkNotNull(request.getParameter("workerUrl"));

    logger.log(Level.INFO, "circuitHash: " + circuitHashIn);
    logger.log(Level.INFO, "inputFormat: " + inputFormatIn);
    logger.log(Level.INFO, "outputFormat: " + outputFormatIn);

    try {
      Instant connectionStartTime = Instant.now();

      URL url = new URL(workerUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Client-Key", AppInfo.WORKER_CLIENT_KEY);
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");

      OutputStreamWriter writer = null;
      try {
        String body = BODY_ARGS_JOINER.join(
          BODY_KV_JOINER.join("input_format", inputFormatIn),
          BODY_KV_JOINER.join("output_format", outputFormatIn),
          BODY_KV_JOINER.join("circuit_hash", circuitHashIn),
          BODY_KV_JOINER.join("data", dataIn));
        writer = new OutputStreamWriter(connection.getOutputStream(), Charsets.UTF_8);
        writer.write(body);
      } finally {
        Closeables.close(writer, false);
      }

      int responseCode = -1;
      try {
        responseCode = connection.getResponseCode();
      } catch (SocketTimeoutException e) {
        // Retry.
        logger.log(Level.WARNING, "Timeout", e);
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
      }

      if (responseCode == HttpURLConnection.HTTP_OK) {
        Instant connectionEndTime = Instant.now();
        logger.log(Level.INFO, "Request Time: " + (connectionEndTime.getMillis() - connectionStartTime.getMillis()));

        BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
        byte[] inputBytes = new byte[connection.getContentLength()];
        input.read(inputBytes);
        String responseString = new String(inputBytes);

        JsonParser parser = new JsonParser();
        JsonObject responseJsonObject = parser.parse(responseString).getAsJsonObject();

        String hostNameOut = responseJsonObject.get("hostname").getAsString();
        String requestIdOut = responseJsonObject.get("request_id").getAsString();
        String timeOut = responseJsonObject.get("time").getAsString();
        String dataOut = responseJsonObject.get("data").getAsString();

        logger.log(Level.INFO, "hostName: " + hostNameOut);
        logger.log(Level.INFO, "requestIdOut: " + requestIdOut);
        logger.log(Level.INFO, "timeOut: " + timeOut);

        CircuitHash circuitHash = new CircuitHash(circuitHashIn);
        Blob result = new Blob(Base64.decodeBase64(dataOut));

        WorkerPersistenceLayer layer = PersistenceLayerFactory.createInternalLayer();
        try {
          layer.putRenderedCircuit(circuitHash, DownloadFormat.from(outputFormatIn), result);
        } catch (PersistenceException e) {
          // No retry.
          logger.log(Level.SEVERE, "Error", e);
        }

      } else {
        // Retry.
        logger.log(Level.WARNING, "Error response returned by worker");
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
      }
    } catch (MalformedURLException e) {
      // No retry.
      logger.log(Level.WARNING, "Error", e);
    } catch (IOException e) {
      // Retry.
      logger.log(Level.WARNING, "Error", e);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
  }
}
