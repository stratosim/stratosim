package com.stratosim.server.serving;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;
import com.stratosim.client.filemanager.FileService;
import com.stratosim.server.ConfigFactory;
import com.stratosim.server.UsersHelper;
import com.stratosim.server.mail.Mailer;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.server.persistence.UserPersistenceLayer;
import com.stratosim.server.persistence.impl.helpers.PermissionsHelper;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.DeletingWithCollaboratorsException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.ShareURLHelper;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.filemodel.EmailAddress;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.FileVisibility;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FileServiceImpl extends XsrfProtectedServiceServlet implements FileService {

  private static final long serialVersionUID = -2778593870596051675L;

  private static final Logger logger = Logger.getLogger(FileServiceImpl.class.getCanonicalName());

  @Override
  public VersionMetadata save(Circuit circuit) throws AccessException {
    checkNotNull(circuit);
    logger.log(Level.INFO, "save: " + circuit.getMetadataString());
    try {
      return userLayer().putCircuit(circuit);
    } catch (Exception ex) {
      logger.log(Level.SEVERE, "error on save: " + circuit.getMetadataString(), ex);
      throw new IllegalStateException();
    }
  }

  @Override
  public VersionMetadata copy(Circuit circuit) throws AccessException {
    checkNotNull(circuit);
    logger.log(Level.INFO, "copy: " + circuit.getMetadataString());
    try {
      // Setting keys to null will cause a resave.
      // TODO(joshuan): Push copy stuff into persistence layer.
      circuit.setFileKey(null);
      circuit.setVersionKey(null);
      circuit.setFileRole(FileRole.OWNER);
      return userLayer().putCircuit(circuit);
    } catch (Exception ex) {
      logger.log(Level.SEVERE, "error on copy: " + circuit.getMetadataString(), ex);
      throw new IllegalStateException();
    }
  }

  @Override
  public Circuit open(VersionMetadataKey versionKey) throws AccessException, PersistenceException {
    checkNotNull(versionKey);
    logger.log(Level.INFO, "open: " + versionKey);

    return userLayer().getCircuit(versionKey);
  }

  @Override
  public Circuit openLatest(FileKey fileKey) throws AccessException, PersistenceException {
    checkNotNull(fileKey);
    logger.log(Level.INFO, "openLatest: " + fileKey);

    VersionMetadata versionMetadata = userLayer().getLatestVersion(fileKey);
    return open(versionMetadata.getVersionKey());
  }

  @Override
  public ImmutableList<VersionMetadata> listLatest() {
    logger.log(Level.INFO, "listLatest");
    try {
      return ImmutableList.copyOf(userLayer().getLatestVersions());
    } catch (Exception ex) {
      logger.log(Level.SEVERE, "error on listLatest", ex);
      throw new IllegalStateException();
    }
  }
  
  @Override
  public ImmutableList<VersionMetadata> listVersions(FileKey fileKey) {
    logger.log(Level.INFO, "listVersions");
    try {
      return ImmutableList.copyOf(userLayer().getVersions(fileKey));
    } catch (Exception ex) {
      logger.log(Level.SEVERE, "error on listVersions", ex);
      throw new IllegalStateException();
    }
  }

  @Override
  public ImmutableMap<FileKey, String> getLatestThumbnails() {
    logger.log(Level.INFO, "getLatestThumbnails");
    try {
      return ImmutableMap.copyOf(userLayer().getLatestThumbnails());
    } catch (Exception ex) {
      logger.log(Level.SEVERE, "error on getLatestThumbnails", ex);
      throw new IllegalStateException();
    }
  }

  @Override
  public void setFileVisibility(FileKey fileKey, FileVisibility visibility) throws AccessException,
      PersistenceException {
    checkNotNull(fileKey);
    checkNotNull(visibility);
    logger.log(Level.INFO, "setFileVisibility: " + fileKey + " " + visibility);

    try {
      LowercaseEmailAddress user = UsersHelper.getCurrentUser(getThreadLocalRequest());

      ImmutableMap<LowercaseEmailAddress, FileRole> permissions =
          byUser(visibility.getPermissions());

      userLayer().setPublic(fileKey, visibility.isPublic());
      ImmutableMap<LowercaseEmailAddress, FileRole> oldPermissions =
          userLayer().setPermissions(fileKey, permissions);

      // Send Emails

      ImmutableSet<LowercaseEmailAddress> newUsers =
          PermissionsHelper.computeNewUsers(ImmutableMap.copyOf(oldPermissions), permissions);

      logger.log(Level.INFO, "setFileVisibility: newUsers: " + newUsers);

      // TODO(tpondich): How fast is memcache really? Should this be optimized?
      // We can always pass it from the client to avoid this rpc.
      String title = userLayer().getLatestVersion(fileKey).getName();
      URL url = new URL(getThreadLocalRequest().getRequestURL().toString());
      String link = url.getProtocol() + "://" + url.getHost()
              + ShareURLHelper.getCircuitLinkUrl(fileKey);
      Map<String, String> root = new HashMap<String, String>();
      root.put("username", user.getEmail());
      root.put("title", title);
      root.put("link", link);

      Configuration config = ConfigFactory.get(getServletContext());
      Template htmlTemplate = config.getTemplate("NewCollaboratorEmail.html");
      Template textTemplate = config.getTemplate("NewCollaboratorEmail.txt");

      for (LowercaseEmailAddress email : newUsers) {
        // Send email.
        try {
          StringWriter htmlWriter = new StringWriter();
          htmlTemplate.process(root, htmlWriter);
          StringWriter textWriter = new StringWriter();
          textTemplate.process(root, textWriter);

          Mailer.sendMailFromNoReply(email.getEmail(), title + " (" + user.getEmail() + ")",
              htmlWriter.toString(), textWriter.toString());

        } catch (Exception ex) {
          logger.log(Level.SEVERE, "Failed to send email to: " + email, ex);
        }
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, "error on setFileVisibility: fk=" + fileKey, ex);
      throw new IllegalArgumentException();
    }
  }

  @Override
  public FileVisibility getFileVisibility(FileKey fileKey) throws AccessException,
      PersistenceException {
    checkNotNull(fileKey);
    logger.log(Level.INFO, "getFileVisibility: " + fileKey);

    boolean isPublic = userLayer().isPublic(fileKey);

    ImmutableMap<LowercaseEmailAddress, FileRole> permissions;
    try {
      permissions = userLayer().getPermissions(fileKey);
    } catch (Exception ex) {
      logger.log(Level.SEVERE, "error on getFileVisibility: fk=" + fileKey, ex);
      throw new IllegalArgumentException();
    }

    return new FileVisibility(byEmail(permissions), isPublic);
  }

  @Override
  public void deleteFile(FileKey fileKey) throws AccessException,
      DeletingWithCollaboratorsException {
    checkNotNull(fileKey);
    logger.log(Level.INFO, "deleteFile: " + fileKey);
    try {
      userLayer().deleteFile(fileKey);
    } catch (DeletingWithCollaboratorsException ex) {
      // Allow this exception to reach the client.
      throw ex;
    } catch (Exception ex) {
      logger.log(Level.SEVERE, "error on deleteFile: fk=" + fileKey, ex);
      throw new IllegalArgumentException();
    }
  }

  private UserPersistenceLayer userLayer() {
    return PersistenceLayerFactory.createUserLayer(UsersHelper
        .getCurrentUser(getThreadLocalRequest()));
  }

  private static ImmutableMap<LowercaseEmailAddress, FileRole> byUser(
      ImmutableMap<EmailAddress, FileRole> map) {
    ImmutableMap.Builder<LowercaseEmailAddress, FileRole> builder = ImmutableMap.builder();

    for (EmailAddress address : map.keySet()) {
      LowercaseEmailAddress user = new LowercaseEmailAddress(address.getEmail());
      builder.put(user, map.get(address));
    }

    return builder.build();
  }

  private static ImmutableMap<EmailAddress, FileRole> byEmail(
      ImmutableMap<LowercaseEmailAddress, FileRole> map) {
    ImmutableMap.Builder<EmailAddress, FileRole> builder = ImmutableMap.builder();

    for (LowercaseEmailAddress user : map.keySet()) {
      EmailAddress address = user;
      builder.put(address, map.get(user));
    }

    return builder.build();
  }

}
