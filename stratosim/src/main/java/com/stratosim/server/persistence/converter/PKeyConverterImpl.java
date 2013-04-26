package com.stratosim.server.persistence.converter;

import com.stratosim.server.algorithm.InvertibleFunction;
import com.stratosim.server.persistence.schema.Kind;
import com.stratosim.server.persistence.schema.PKey;
import com.stratosim.shared.filemodel.StratoSimKey;

/**
 * Convert internal keys to external ones.
 * 
 * @param <K> The kind type that defines the key.
 * @param <E> The external key type.
 */
abstract class PKeyConverterImpl<K extends Kind<K>, E extends StratoSimKey>
    implements
      PKeyConverter<K, E> {

  private final InvertibleFunction<String, String> function;

  protected PKeyConverterImpl(InvertibleFunction<String, String> function) {
    this.function = function;
  }

  protected abstract E newExternalKey(String key);

  @Override
  public E toExternal(PKey<K> pkey) {
    String f = function.forward(pkey.getAsString());
    return newExternalKey(f);
  }

  @Override
  public PKey<K> toInternal(E ekey) {
    String b = function.reverse(ekey.get());
    return new PKey<K>(b);
  }

}
