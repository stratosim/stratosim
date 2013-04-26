package com.stratosim.server.persistence.objectify.entity;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

@Entity(name = "publicFile")
@Cache(expirationSeconds = 300)  // 5 minutes
public class PublicFile {

  @Id private String fileKey;
  
  private String addedBy;
  
  private PublicFile() {}
  
  public FileKey getFileKey() {
    return new FileKey(fileKey);
  }
  
  public LowercaseEmailAddress getEmail() {
    return new LowercaseEmailAddress(addedBy);
  }
}
