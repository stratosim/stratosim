package com.stratosim.server.persistence.kinds;

import static com.stratosim.server.persistence.schema.Property.newProperty;

import org.joda.time.Instant;

import com.stratosim.server.persistence.schema.ExplicitPropertiesKind;
import com.stratosim.server.persistence.schema.Property;
import com.stratosim.server.persistence.schema.Type;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class DeletedFileKind extends ExplicitPropertiesKind<DeletedFileKind> {

  public final Property<LowercaseEmailAddress> lastOwner = newProperty("lastOwner", Type.lowercaseEmail());

  public final Property<Instant> deletionTime = newProperty("deletionTime", Type.instant());

  public final Property<FileKey> fileKey = newProperty("fileKey", Type.fileKey());
  
  public DeletedFileKind() {
    super("deletedFile");
  }

}
