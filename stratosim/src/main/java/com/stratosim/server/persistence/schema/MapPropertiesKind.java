package com.stratosim.server.persistence.schema;

import java.util.Map;

import com.google.common.collect.Maps;
import com.stratosim.server.algorithm.InvertibleFunction;

public abstract class MapPropertiesKind<K extends MapPropertiesKind<K, T, U>, T, U>
    extends AbstractKind<K> implements Kind<K> {

  public final Map<T, U> properties;

  private final InvertibleFunction<T, String> ft;

  private final InvertibleFunction<U, Object> fu;

  public MapPropertiesKind(String kind, InvertibleFunction<T, String> ft,
                            InvertibleFunction<U, Object> fu) {
    super(kind);
    this.properties = Maps.newHashMap();
    this.ft = ft;
    this.fu = fu;
  }

  @Override
  protected Map<String, Object> pairsForDatastore() {
    Map<String, Object> pairs = Maps.newHashMap();

    for (Map.Entry<T, U> entry : properties.entrySet()) {
      pairs.put(ft.forward(entry.getKey()), fu.forward(entry.getValue()));
    }

    return pairs;
  }

  @Override
  protected void mergeFromPairs(Map<String, Object> pairs) {
    for (Map.Entry<String, Object> entry : pairs.entrySet()) {
      properties.put(ft.reverse(entry.getKey()), fu.reverse(entry.getValue()));
    }
  }

}
