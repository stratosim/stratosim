package com.stratosim.server.persistence.converter;

import com.stratosim.server.persistence.schema.Kind;
import com.stratosim.server.persistence.schema.PKey;
import com.stratosim.shared.filemodel.StratoSimKey;

public interface PKeyConverter<K extends Kind<K>, E extends StratoSimKey> {

  E toExternal(PKey<K> pkey);

  PKey<K> toInternal(E ekey);

}
