package com.stratosim.server.persistence.objectify.entity;

import org.joda.time.Instant;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

@Entity(name = "deletedFile")
@Cache(expirationSeconds = 0)  // no expiration
public class DeletedFile {

  @Id private Long ignored = null;  // we don't care about the key
  
  private String lastOwner;  // email
  
  private long deletionTimeMillis;
  
  private String fileKey;
  
  private DeletedFile() {}
  
  public LowercaseEmailAddress getLastOwner() {
    return new LowercaseEmailAddress(lastOwner);
  }
  
  public Instant getDeletionTime() {
    return new Instant(deletionTimeMillis);
  }
  
  public FileKey getFileKey() {
    return new FileKey(fileKey);
  }
}
