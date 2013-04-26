package com.stratosim.server.serving;

import static com.google.common.base.Preconditions.checkArgument;
import static com.stratosim.server.HttpCacheControlHelper.makeCachedInternalForever;
import static com.stratosim.server.HttpCacheControlHelper.makeCachedPublicLimited;
import static com.stratosim.shared.ShareURLHelper.getCircuitDownloadServiceUrl;
import static com.stratosim.shared.ShareURLHelper.getCircuitLinkUrl;
import static com.stratosim.shared.ShareURLHelper.getSimulationDataTableServiceUrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.stratosim.server.ConfigFactory;
import com.stratosim.server.FileOrVersionParser;
import com.stratosim.server.GenerateHelper;
import com.stratosim.server.UsersHelper;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.server.persistence.UserPersistenceLayer;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * This is a public servlet that serves the content for embedding circuits.
 */
public class EmbedServlet extends AbstractStratoSimServlet {
  private static final long serialVersionUID = -6588037237080771792L;

  private static final Splitter SPLITTER = Splitter.on('/').trimResults().omitEmptyStrings();

  /**
   * Url Format: /public/embed/[filekey|versionkey]/
   */
  @Override
  protected void doHandledGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, AccessException, PersistenceException, TemplateException {

    // Use checkArgument to generate the right exception to be caught.
    checkArgument(request.getPathInfo() != null);

    String pathInfo = request.getPathInfo();

    ImmutableList<String> args = ImmutableList.copyOf(SPLITTER.split(pathInfo));
    FileOrVersionParser parser = new FileOrVersionParser(userLayer(request), args);

    boolean showCircuit = false;
    if (request.getParameter("circuitHeight") != null
        && parseIntOrZero(request.getParameter("circuitHeight")) > 0) {
      showCircuit = true;
    }

    boolean showCharts = false;
    if (request.getParameter("chartHeight") != null
        && parseIntOrZero(request.getParameter("chartHeight")) > 0) {
      showCharts = true;
    }

    checkArgument(showCircuit || showCharts);

    VersionMetadata versionMetadata = parser.getVersionMetadata();
    VersionMetadataKey versionMetadataKey = versionMetadata.getVersionKey();

    boolean hasSvg = false;
    if (showCircuit) {
      // TODO(tpondich): Prevent requesting known failures infinitely.
      hasSvg =
          GenerateHelper.generateIfNeeded(versionMetadataKey, DownloadFormat.SVG,
              PersistenceLayerFactory.createUserLayer(null));
    }

    boolean hasCsv = false;
    if (showCharts) {
      // TODO(tpondich): Prevent requesting known failures infinitely.
      hasCsv =
          GenerateHelper.generateIfNeeded(versionMetadataKey, DownloadFormat.CSV,
              PersistenceLayerFactory.createUserLayer(null));
    }

    response.setHeader("Content-Disposition", "inline");
    response.setContentType("text/html");

    Configuration config = ConfigFactory.get(getServletContext());
    Template template;
    Map<String, String> root = Maps.newHashMap();

    if ((showCircuit && !hasSvg) || (showCharts && !hasCsv)) {
      root.put("url", request.getRequestURI());

      template = config.getTemplate("EmbedServletRetry.html");

    } else {

      String linkUri = getCircuitLinkUrl(versionMetadataKey);
      String pdfUri = getCircuitDownloadServiceUrl(versionMetadataKey, DownloadFormat.PDF);
      String pngUri = getCircuitDownloadServiceUrl(versionMetadataKey, DownloadFormat.PNG);
      String svgUri = getCircuitDownloadServiceUrl(versionMetadataKey, DownloadFormat.SVG);
      String simulationpdfUri =
          getCircuitDownloadServiceUrl(versionMetadataKey, DownloadFormat.SIMULATIONPDF);
      String simulationpngUri =
          getCircuitDownloadServiceUrl(versionMetadataKey, DownloadFormat.SIMULATIONPNG);

      String datasourceUri = getSimulationDataTableServiceUrl(versionMetadataKey);

      root.put("name", versionMetadata.getName());
      root.put("linkUrl", linkUri);

      int menuItems = 0;
      if (showCircuit) {
        root.put("svgUrl", svgUri);
        root.put("pdfUrl", pdfUri);
        root.put("pngUrl", pngUri);
        menuItems += 3;
      }

      if (showCharts) {
        root.put("simulationpdfUrl", simulationpdfUri);
        root.put("simulationpngUrl", simulationpngUri);
        menuItems += 2;
        root.put("datasourceUrl", datasourceUri);
      }
      root.put("menuItems", "" + menuItems);

      template = config.getTemplate("EmbedServlet.html");

      // Cache the embed page so any high traffic embeds will not
      // keep querying the server for the base page. This is okay
      // because there is no secure data on this page. We do not
      // cache it forever because we may want to change the format
      // of this page and do not have control over the url.
      makeCachedPublicLimited(response);
      
      if (!parser.isFileOnly()) {
        makeCachedInternalForever(response);
      }
    }

    PrintWriter out = response.getWriter();
    template.process(root, out);
    out.flush();
  }

  private static int parseIntOrZero(String s) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private static UserPersistenceLayer userLayer(HttpServletRequest request) {
    return PersistenceLayerFactory.createUserLayer(UsersHelper.getCurrentUser(request));
  }
}
