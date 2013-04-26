package com.stratosim.shared.circuithelpers;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.PortId;
import com.stratosim.shared.circuitmodel.PortOwnerId;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireId;
import com.stratosim.shared.circuitmodel.WireJunction;

public class WireBreaker {
  private final WireId oldWireId;
  private final int oldSegmentIndex;
  private final Point breakPoint;

  private PortOwnerId newWireJunctionId;
  private WireJunction newWireJunction;
  private Wire newWireOne;
  private Wire newWireTwo;

  private boolean hasCommitted = false;

  public WireBreaker(WireId wireId, int segment, Point breakPoint) {
    assert (segment >= 0);

    this.oldWireId = wireId;
    this.oldSegmentIndex = segment;
    this.breakPoint = breakPoint;
  }

  /**
   * return the newWireJunction located at the break point that joins the newWireTwo new wires.
   */
  public WireJunction getWireJunction() {
    Preconditions.checkState(hasCommitted);

    return newWireJunction;
  }

  public PortOwnerId getWireJunctionId() {
    Preconditions.checkState(hasCommitted);

    return newWireJunctionId;
  }

  /**
   * get the wire containing the points before the break. the start port is set to the
   * newWireJunction's port and the wire has no end port.
   * 
   * @return
   */
  public Wire getWireOne() {
    Preconditions.checkState(hasCommitted);

    return newWireOne;
  }

  /**
   * get the wire containing the points after the break. the start port is set to the
   * newWireJunction's port and the wire has no end port.
   */
  public Wire getWireTwo() {
    Preconditions.checkState(hasCommitted);

    return newWireTwo;
  }

  /**
   * commits this break to the circuit. the passed circuit must be the same newWireOne the wire and
   * wire segment in the constructor belong to.
   * 
   * this will add the newWireTwo new wires and the wire newWireJunction to the circuit, end the new
   * wires on the start and end points of the old wire, and remove the old wire from the circuit.
   */
  public void commitToCircuit(Circuit circuit) {
    Preconditions.checkState(!hasCommitted);

    Wire wire = circuit.getWire(oldWireId);
    assert (wire != null);

    PortId startPortId = wire.getStartPortId();
    PortId endPortId = wire.getEndPortId();
    List<Point> points = Lists.newArrayList(wire.getPoints());

    circuit.removeWire(oldWireId, false);

    newWireJunction = new WireJunction(breakPoint);
    newWireJunctionId = circuit.addWireJunction(newWireJunction);
    PortId newWireJunctionPortId = circuit.getPortIdOfWireJunctionId(newWireJunctionId);

    points.add(oldSegmentIndex + 1, newWireJunction.getLocation());

    // Reverse points to start at newWireJunction and go back to start.
    newWireOne =
        new Wire(Lists.reverse(points.subList(0, oldSegmentIndex + 2)), newWireJunctionPortId,
            startPortId);
    // This starts at newWireJunction and goes to old wire's end, so points are in order.
    newWireTwo =
        new Wire(points.subList(oldSegmentIndex + 1, points.size()), newWireJunctionPortId,
            endPortId);

    circuit.addWire(newWireOne);
    circuit.addWire(newWireTwo);

    hasCommitted = true;
  }
}
