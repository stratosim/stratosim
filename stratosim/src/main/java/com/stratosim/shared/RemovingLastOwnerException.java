package com.stratosim.shared;

import com.stratosim.shared.filemodel.FileKey;

/**
 * Thrown when trying to remove last owner of a file.
 */
public class RemovingLastOwnerException extends StratoSimException {
  
  private static final long serialVersionUID = 1549490855254573154L;
  
  private /*final*/ FileKey fileKey;
  
  public RemovingLastOwnerException() {
    super("unimplemented");
    this.fileKey = null;
  }
  
  public RemovingLastOwnerException(FileKey fileKey) {
    super("trying to remove last owner for fk=" + fileKey.get());
    this.fileKey = fileKey;
  }
  
  public FileKey getFileKey() {
    return fileKey;
  }
  
}
