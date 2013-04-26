package com.stratosim.server;

import org.joda.time.Instant;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.stratosim.server.persistence.UserPersistenceLayer;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class FileOrVersionParser {
  
  private final UserPersistenceLayer layer;
  
  private final ImmutableList<String> args;
  
  public FileOrVersionParser(UserPersistenceLayer layer, ImmutableList<String> args) {
    this.layer = layer;
    this.args = args;
    
    // Check that there is either a filekey parameter or both filekey and version time.
    Preconditions.checkArgument(args.size() == 1 || args.size() == 2);
  }

  public VersionMetadata getVersionMetadata() throws AccessException, PersistenceException {
    FileKey fileKey = new FileKey(args.get(0));
    switch (args.size()) {
      case 1: // just a file key
        return layer.getLatestVersion(fileKey);

      case 2: // also a version
        Instant instant = new Instant(Long.parseLong(args.get(1)));
        VersionMetadataKey versionKey = new VersionMetadataKey(fileKey, instant.getMillis());
        return layer.getVersion(versionKey);

      default:
        throw new IllegalArgumentException("must have exactly one or two args at end after start");
    }
  }
  
  public boolean getIsPublic() throws AccessException, PersistenceException {
    FileKey fileKey = new FileKey(args.get(0));
    return layer.isPublic(fileKey);
  }
  
  public boolean isFileOnly() {
    return args.size() == 1;
  }
}
