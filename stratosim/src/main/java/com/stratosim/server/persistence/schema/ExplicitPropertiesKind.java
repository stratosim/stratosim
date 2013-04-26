package com.stratosim.server.persistence.schema;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class ExplicitPropertiesKind<K extends ExplicitPropertiesKind<K>>
    extends AbstractKind<K> implements Kind<K> {

  /**
   * any properties not explicitly declared by subclass. more specifically, properties not returned
   * by the subclass in {@code getProperties}.
   * 
   * explicitly declared properties take precedence when writing, if there are properties declared
   * explicitly and in this map.
   * 
   * field is final to ensure it is always non-null.
   */
  public final Map<String, Serializable> other;

  public ExplicitPropertiesKind(String name) {
    super(name);
    // This catches copy paste errors when creating kinds.
    checkArgument(name.equalsIgnoreCase(getClass().getSimpleName().replaceFirst("Kind", "")),
        "Name must be camel case of " + getClass().getSimpleName() + " but was " + name);
    this.other = Maps.newHashMap();
  }

  @Override
  protected Map<String, Object> pairsForDatastore() {
    Map<String, Object> pairs = Maps.newHashMap();

    // copy from map first so that explicit properties take precedence
    for (String propName : other.keySet()) {
      pairs.put(propName, other.get(propName));
    }

    for (Property<?> property : getProperties()) {
      pairs.put(property.name(), toDatastore(property));
    }

    return pairs;
  }

  @Override
  protected void mergeFromPairs(Map<String, Object> pairs) {
    other.clear();
    for (String key : pairs.keySet()) {
      Serializable s = Type.tryCast(pairs.get(key));
      other.put(key, s);
    }

    // remove explicitly declared properties from other and assign them
    // to this object's properties if they are null
    for (Property<?> property : getProperties()) {
      Serializable obj = other.remove(property.name());
      if (property.get() == null) {
        fromDatastore(property, obj);
      }
    }
  }

  private Iterable<Property<?>> getProperties() {

    List<Property<?>> properties = Lists.newArrayList();

    for (Field field : getClass().getFields()) {
      try {
        Property<?> property = (Property<?>) field.get(this);
        properties.add(property);
      } catch (IllegalArgumentException e) {
        // ignore
      } catch (IllegalAccessException e) {
        // ignore
      } catch (ClassCastException e) {
        // ignore
      }
    }

    return properties;
  }

  /**
   * capture wildcard and use the type to convert to datastore format
   */
  private static <T> Serializable toDatastore(Property<T> p) {
    if (p.get() == null) {
      return null;
    } else {
      return p.type().toDatastore(p.get());
    }
  }

  /**
   * capture wildcard and use the type to convert from datastore format and set the property.
   */
  private static <T> void fromDatastore(Property<T> p, Serializable obj) {
    p.set(p.type().fromDatastore(obj));
  }

}
