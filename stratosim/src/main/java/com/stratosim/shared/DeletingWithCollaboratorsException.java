package com.stratosim.shared;

import com.stratosim.shared.filemodel.FileKey;

/**
 * Thrown when trying to delete a file that still has collaborators.
 */
public class DeletingWithCollaboratorsException extends StratoSimException {
  
  private static final long serialVersionUID = 1549490855254573154L;
  
  private /*final*/ FileKey fileKey;
  
  public DeletingWithCollaboratorsException() {
    super("unimplemented");
    this.fileKey = null;
  }
  
  public DeletingWithCollaboratorsException(FileKey fileKey) {
    super("trying to remove last owner for fk=" + fileKey.get());
    this.fileKey = fileKey;
  }
  
  public FileKey getFileKey() {
    return fileKey;
  }
  
}
