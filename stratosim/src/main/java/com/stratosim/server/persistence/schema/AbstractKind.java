package com.stratosim.server.persistence.schema;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Map;

import com.google.appengine.api.datastore.Entity;

public abstract class AbstractKind<K extends AbstractKind<K>> implements Kind<K> {

  private final String kind;

  public AbstractKind(String kind) {
    this.kind = checkNotNull(kind);
  }

  @Override
  public final String kindName() {
    return kind;
  }
  
  protected abstract Map<String, Object> pairsForDatastore();
  
  protected abstract void mergeFromPairs(Map<String, Object> pairs);
  
  @Override
  public final Entity toEntity() {
    Entity entity = new Entity(kindName());
    fillEntity(entity);
    return entity;
  }

  @Override
  public final Entity toEntityWithKey(PKey<K> key) {
    checkNotNull(key);
    Entity entity = new Entity(key.dsKey());
    fillEntity(entity);
    return entity;
  }
  
  @Override
  public final void mergeFrom(Entity entity) {
    checkNotNull(entity);
    checkArgument(entity.getKind().equals(kindName()));
    mergeFromPairs(Collections.unmodifiableMap(entity.getProperties()));
  }

  private void fillEntity(Entity entity) {
    for (Map.Entry<String, Object> entry : pairsForDatastore().entrySet()) {
      entity.setUnindexedProperty(entry.getKey(), entry.getValue());
    }
  }

}
