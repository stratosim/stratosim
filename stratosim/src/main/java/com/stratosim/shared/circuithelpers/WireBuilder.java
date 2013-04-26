package com.stratosim.shared.circuithelpers;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.PortId;
import com.stratosim.shared.circuitmodel.Wire;

public class WireBuilder {
  private List<Point> points;

  public WireBuilder() {
    points = Lists.newArrayList();
  }

  public void addPoint(Point point) {
    assert point != null;
    points.add(point);
  }

  public void addAllPoints(Collection<Point> newPoints) {
    points.addAll(newPoints);
  }

  public void replacePoint(int index, Point point) {
    assert point != null;
    assert index > 0 && index < points.size();

    points.set(index, point);
  }

  public void replaceFirstPoint(Point point) {
    assert point != null;
    points.set(0, point);
  }

  public void replaceLastPoint(Point point) {
    assert point != null;
    points.set(points.size() - 1, point);
  }

  public Point getPoint(int index) {
    assert index > 0 && index < points.size();

    return points.get(index);
  }

  public Wire build(PortId startPortId, PortId endPortId) {
    assert (startPortId != null);
    assert (endPortId != null);
    // Should we also assert that the wire end points are on the ports?
    // Circuit does this anyway.

    // We should assert that the wire has a valid set of points.

    Wire wire = new Wire(points, startPortId, endPortId);
    return wire;
  }

  public Wire buildWithNullPorts() {
    Wire wire = new Wire(points, null, null);
    return wire;
  }
}
