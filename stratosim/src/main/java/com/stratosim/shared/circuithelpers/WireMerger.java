package com.stratosim.shared.circuithelpers;

import java.util.List;

import com.google.common.collect.Lists;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.PortId;
import com.stratosim.shared.circuitmodel.PortOwnerId;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireId;
import com.stratosim.shared.circuitmodel.WireJunction;

public class WireMerger {
  private Wire newWire;

  private final PortOwnerId wireJunctionId;

  public WireMerger(PortOwnerId wireJunctionId) {
    this.wireJunctionId = wireJunctionId;
  }

  public void commitToCircuit(Circuit circuit) {
    WireJunction junction = circuit.getWireJunction(wireJunctionId);
    assert (junction != null);

    WireId wireOneId = null;
    WireId wireTwoId = null;
    for (WireId wireId : junction.getOnlyPort().getWireIds()) {
      if (wireOneId == null) {
        wireOneId = wireId;
      } else if (wireTwoId == null) {
        wireTwoId = wireId;
      } else {
        assert (false);
      }
    }

    Wire one = circuit.getWire(wireOneId);
    Wire two = circuit.getWire(wireTwoId);

    PortId wireJunctionPortId = circuit.getPortIdOfWireJunctionId(wireJunctionId);

    assert (one.getStartPortId().equals(wireJunctionPortId) ^ one.getEndPortId().equals(
        wireJunctionPortId));
    assert (two.getStartPortId().equals(wireJunctionPortId) ^ two.getEndPortId().equals(
        wireJunctionPortId));

    List<Point> points = Lists.newArrayList();
    // The wire constructor takes care of making the points reasonable by
    // merging linear points and removing duplicate points.

    PortId startPortId = null;
    PortId endPortId = null;

    // Add points going toward to the junction.
    if (one.getStartPortId().equals(wireJunctionPortId)) {
      points.addAll(one.getPoints().reverse());
      startPortId = one.getEndPortId();
    } else {
      points.addAll(one.getPoints());
      startPortId = one.getStartPortId();
    }

    // Add points coming from the junction.
    if (two.getStartPortId().equals(wireJunctionPortId)) {
      points.addAll(two.getPoints());
      endPortId = two.getEndPortId();
    } else {
      points.addAll(two.getPoints().reverse());
      endPortId = two.getStartPortId();
    }

    newWire = new Wire(points, startPortId, endPortId);

    circuit.removeWire(wireOneId, false);
    circuit.removeWire(wireTwoId, false);
    circuit.removeWireJunction(wireJunctionId);

    circuit.addWire(newWire);
  }
}
