package com.stratosim.shared.circuithelpers;

import java.util.List;

import com.google.common.collect.Lists;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Wire;

/**
 * Takes an input wire and generates copies that have one segment nudged by a given amount.
 */
// TODO(tpondich): Maybe this should extend WireBuilder?
public class WireDeformer {

  private Wire originalWire;
  private WireBuilder builder;

  private int nudgingSegmentStart;
  private boolean isVertical;

  public WireDeformer(Wire wire, int nudgingSegmentIndex) {
    assert (wire != null);
    assert (nudgingSegmentIndex >= 0);

    this.nudgingSegmentStart = nudgingSegmentIndex + 1;

    this.originalWire = wire;
    this.isVertical = wire.isWireSegmentVertical(nudgingSegmentIndex);

    List<Point> points = Lists.newArrayList(wire.getPoints());
    points.addAll(
        nudgingSegmentIndex + 1,
        Lists.newArrayList(wire.getPoints().get(nudgingSegmentIndex),
            wire.getPoints().get(nudgingSegmentIndex + 1)));
    builder = new WireBuilder();
    builder.addAllPoints(points);
  }

  public void setPosition(Point mousePosition) {
    int linearPosition = isVertical ? mousePosition.getX() : mousePosition.getY();

    int x1 = isVertical ? linearPosition : builder.getPoint(nudgingSegmentStart).getX();
    int y1 = isVertical ? builder.getPoint(nudgingSegmentStart).getY() : linearPosition;
    int x2 = isVertical ? linearPosition : builder.getPoint(nudgingSegmentStart + 1).getX();
    int y2 = isVertical ? builder.getPoint(nudgingSegmentStart + 1).getY() : linearPosition;

    builder.replacePoint(nudgingSegmentStart, new Point(x1, y1));
    builder.replacePoint(nudgingSegmentStart + 1, new Point(x2, y2));
  }

  public Wire build() {
    return builder.build(originalWire.getStartPortId(), originalWire.getEndPortId());
  }

  public Wire buildWithNullPorts() {
    return builder.buildWithNullPorts();
  }
}
