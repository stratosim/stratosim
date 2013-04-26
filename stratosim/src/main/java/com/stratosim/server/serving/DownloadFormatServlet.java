package com.stratosim.server.serving;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.stratosim.server.HttpCacheControlHelper.makeCachedPrivateForever;
import static com.stratosim.server.HttpCacheControlHelper.makeCachedPrivateLimited;
import static com.stratosim.server.HttpCacheControlHelper.makeCachedPublicForever;
import static com.stratosim.server.HttpCacheControlHelper.makeCachedPublicLimited;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.stratosim.server.FileOrVersionParser;
import com.stratosim.server.RetryingDataFetcher;
import com.stratosim.server.UsersHelper;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.server.persistence.UserPersistenceLayer;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.VersionMetadata;

public class DownloadFormatServlet extends AbstractStratoSimServlet {

  private static final long serialVersionUID = -2836893312626112953L;

  private static final Splitter SPLITTER = Splitter.on('/').trimResults().omitEmptyStrings();

  /**
   * Url Format: /public/download/[format]/[filekey|versionkey]
   */
  @Override
  protected void doHandledGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, AccessException, PersistenceException {

    String pathInfo = checkNotNull(request.getPathInfo());
    ImmutableList<String> args = ImmutableList.copyOf(SPLITTER.split(pathInfo));

    checkArgument(args.size() > 0);

    DownloadFormat downloadFormat = DownloadFormat.from(args.get(0));
    FileOrVersionParser parser =
        new FileOrVersionParser(userLayer(request), args.subList(1, args.size()));

    VersionMetadata versionMetadata = parser.getVersionMetadata();

    Blob data =
        new RetryingDataFetcher(versionMetadata.getVersionKey(), downloadFormat, userLayer(request))
            .getBlob();

    String contentDisposition =
        String.format("%s; filename=%s%s",
            (downloadFormat.isAttachment() ? "attachment" : "inline"),
            URLEncoder.encode(versionMetadata.getName(), "UTF-8"),
            downloadFormat.getDottedExtension());
    response.setHeader("Content-Disposition", contentDisposition);

    if (!parser.isFileOnly()) {
      // Only cache versions forever.
      if (parser.getIsPublic()) {
        makeCachedPublicForever(response);
      } else {
        makeCachedPrivateForever(response);
      }
    } else {
      // Cache file key based downloads for a short time. In the
      // app itself, we always do downloads via a version key so
      // those results are never stale. This will only cause a
      // short delay for embedded downloads based on the filekey.
      if (parser.getIsPublic()) {
        makeCachedPublicLimited(response);
      } else {
        makeCachedPrivateLimited(response);
      }
    }

    response.setContentType(downloadFormat.getMimeType());
    response.getOutputStream().write(data.getBytes());
  }

  private static UserPersistenceLayer userLayer(HttpServletRequest request) {
    return PersistenceLayerFactory.createUserLayer(UsersHelper.getCurrentUser(request));
  }
}
