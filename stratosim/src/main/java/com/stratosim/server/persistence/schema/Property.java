package com.stratosim.server.persistence.schema;

import com.google.common.base.Preconditions;

public class Property<T> {

  public String name() {
    return name;
  }

  public T get() {
    return value;
  }

  public void set(T newValue) {
    value = newValue;
  }

  public Type<T> type() {
    return type;
  }

  private final String name;
  private T value;
  private final Type<T> type;

  private Property(String name, Type<T> type) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(type);

    this.name = name;
    this.value = null;
    this.type = type;
  }

  /**
   * create a new property. requires passing a {@code Type} to so that only appengine-recognized
   * types can be stored.
   */
  public static <P> Property<P> newProperty(String name, Type<P> type) {
    return new Property<P>(name, type);
  }

}
