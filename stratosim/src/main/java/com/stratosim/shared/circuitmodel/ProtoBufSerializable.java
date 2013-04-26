package com.stratosim.shared.circuitmodel;

import com.google.protobuf.GeneratedMessage;

public interface ProtoBufSerializable<M extends GeneratedMessage> {
  M toProto(String id);
}
