package com.stratosim.shared.model.circuit;

import com.stratosim.shared.model.circuit.Wire.Vertex;
import com.stratosim.shared.model.util.CircuitCoord;

public interface Locator {

  Device getDeviceAt(CircuitCoord cc);

  Wire getWireAt(CircuitCoord cc);

  Vertex getVertexAt(CircuitCoord cc);

}
