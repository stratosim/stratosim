package com.stratosim.server.persistence.schema;

import java.io.Serializable;

import org.joda.time.Instant;

import com.google.appengine.api.datastore.Blob;
import com.stratosim.server.persistence.kinds.CircuitDataKind;
import com.stratosim.server.persistence.kinds.CircuitPdfKind;
import com.stratosim.server.persistence.kinds.CircuitPngKind;
import com.stratosim.server.persistence.kinds.CircuitPsKind;
import com.stratosim.shared.filemodel.CircuitHash;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

/**
 * Types that can be persisted to the Appengine datastore. New types from the following list can be
 * added if needed:
 * 
 * http://code.google.com/appengine/docs/java/datastore/entities.html
 */
public abstract class Type<T> {

  public static Type<String> string() {
    return new NoConversionType<String>();
  }

  public static Type<Blob> blob() {
    return new NoConversionType<Blob>();
  }

  public static Type<LowercaseEmailAddress> lowercaseEmail() {
    return new LowercaseEmailType();
  }

  public static Type<FileKey> fileKey() {
    return new FileKeyType();
  }

  public static Type<CircuitHash> circuitHash() {
    return new CircuitHashType();
  }

  public static Type<PKey<CircuitDataKind>> circuitDataKey() {
    return new PKeyType<CircuitDataKind>();
  }

  public static Type<PKey<CircuitPdfKind>> circuitPdfKey() {
    return new PKeyType<CircuitPdfKind>();
  }

  public static Type<PKey<CircuitPngKind>> circuitPngKey() {
    return new PKeyType<CircuitPngKind>();
  }

  public static Type<PKey<CircuitPsKind>> circuitPsKey() {
    return new PKeyType<CircuitPsKind>();
  }

  public static Type<Instant> instant() {
    return new InstantType();
  }

  public static Type<Boolean> booleanType() {
    return new NoConversionType<Boolean>();
  }

  // -----------------------
  // FOR PERSISTENCE
  // -----------------------

  public abstract Serializable toDatastore(T obj);

  public abstract T fromDatastore(Serializable obj);

  // -----------------------
  // INTERNALS
  // -----------------------

  private Type() {} // only let inner classes extend

  @SuppressWarnings("unchecked")
  public static <U> U tryCast(Object obj) {

    try {
      return (U) obj;

    } catch (ClassCastException e) {
      throw new IllegalArgumentException("wrong data type: found "
          + obj.getClass().getCanonicalName(), e);
    }
  }

  private static class NoConversionType<U extends Serializable> extends Type<U> {

    @Override
    public Serializable toDatastore(U obj) {
      return obj;
    }

    @Override
    public U fromDatastore(Serializable obj) {
      return tryCast(obj);
    }
  }
  
  private static class LowercaseEmailType extends Type<LowercaseEmailAddress> {

    @Override
    public Serializable toDatastore(LowercaseEmailAddress email) {
      return email.getEmail();
    }

    @Override
    public LowercaseEmailAddress fromDatastore(Serializable obj) {
      String str = tryCast(obj);
      return new LowercaseEmailAddress(str);
    }
  }

  private static class FileKeyType extends Type<FileKey> {

    @Override
    public Serializable toDatastore(FileKey obj) {
      return obj.get();
    }

    @Override
    public FileKey fromDatastore(Serializable obj) {
      String str = tryCast(obj);
      return new FileKey(str);
    }
  }

  private static class CircuitHashType extends Type<CircuitHash> {

    @Override
    public Serializable toDatastore(CircuitHash obj) {
      return obj.get();
    }

    @Override
    public CircuitHash fromDatastore(Serializable obj) {
      String str = tryCast(obj);
      return new CircuitHash(str);
    }
  }

  private static class PKeyType<K extends Kind<K>> extends Type<PKey<K>> {

    @Override
    public Serializable toDatastore(PKey<K> obj) {
      return obj.getAsString();
    }

    @Override
    public PKey<K> fromDatastore(Serializable obj) {
      String str = tryCast(obj);
      return new PKey<K>(str);
    }
  }

  private static class InstantType extends Type<Instant> {

    @Override
    public Serializable toDatastore(Instant obj) {
      return obj.getMillis();
    }

    @Override
    public Instant fromDatastore(Serializable obj) {
      Long millis = tryCast(obj);
      return new Instant(millis);
    }
  }

}
