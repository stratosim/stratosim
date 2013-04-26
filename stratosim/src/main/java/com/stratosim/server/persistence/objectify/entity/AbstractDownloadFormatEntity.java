package com.stratosim.server.persistence.objectify.entity;

import com.google.appengine.api.datastore.Blob;
import com.googlecode.objectify.annotation.Id;
import com.stratosim.shared.filemodel.CircuitHash;

public abstract class AbstractDownloadFormatEntity {

  @Id private String circuitHash;
  
  private Blob data;
  
  public final CircuitHash getCircuitHash() {
    return new CircuitHash(circuitHash);
  }
  
  public final Blob getData() {
    return data;
  }
}
