package com.stratosim.server.persistence.schema;

import com.google.appengine.api.datastore.Entity;

public interface Kind<K extends Kind<K>> {

  String kindName();

  /**
   * creates an appengine {@code Entity} from the given {@code Kind}, setting all of the
   * {@code Kind}'s properties. lets appengine assign a new key to the entity.
   */
  Entity toEntity();

  /**
   * same as {@code #toEntity()} but creates an entity with the given {@code Key}. asserts that the
   * given key is non-null and of the right kind.
   */
  Entity toEntityWithKey(PKey<K> key);

  /**
   * fills the given {@code Kind}, setting properties with data from the appengine {@code Entity}
   * IFF they are {@code null} in this object.
   * 
   * any entity fields not in the {@code Kind} are ignored.
   */
  void mergeFrom(Entity entity);

}
