package com.stratosim.shared.circuitmodel;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.stratosim.shared.drawing.DrawableContext;

// Immutable
public class Wire implements DrawableObject, Serializable {
  private static final long serialVersionUID = -637779606228561448L;

  private/* final */PortId startPortId;
  private/* final */PortId endPortId;

  private/* final */List<WireSegment> segments = Lists.newArrayList();

  // For GWT RPC
  @SuppressWarnings("unused")
  private Wire() {}

  public Wire(List<Point> points, PortId startPortId, PortId endPortId) {
    super();
    this.startPortId = startPortId;
    this.endPortId = endPortId;

    for (Point point : points) {
      addPoint(point);
    }
  }

  private void addPoint(Point p) {
    if (segments.isEmpty()) {
      WireSegment newSegment = new WireSegment(p, true);
      segments.add(newSegment);
    } else {
      WireSegment lastSegment = segments.get(segments.size() - 1);
      Point end = null;
      if (lastSegment.isVertical()) {
        if (lastSegment.getEndLinearPosition() != p.getY()) {
          end = new Point(lastSegment.getLinearPosition(), p.getY());
        }
      } else {
        if (lastSegment.getEndLinearPosition() != p.getX()) {
          end = new Point(p.getX(), lastSegment.getLinearPosition());
        }
      }
      if (end != null) {
        if (!end.equals(lastSegment.getStartPosition())) {
          lastSegment = new WireSegment(lastSegment.getStartPosition(), end);
        } else {
          lastSegment = new WireSegment(lastSegment.getStartPosition(), true);
        }
      }
      segments.set(segments.size() - 1, lastSegment);
      if (!p.equals(lastSegment.getEndPosition())) {
        WireSegment newSegment = new WireSegment(lastSegment.getEndPosition(), p);
        segments.add(newSegment);
      }
    }
  }

  public PortId getStartPortId() {
    return startPortId;
  }

  public PortId getEndPortId() {
    return endPortId;
  }

  public ImmutableList<Point> getPoints() {
    ImmutableList.Builder<Point> builder = ImmutableList.builder();
    for (WireSegment segment : segments) {
      builder.add(segment.getStartPosition());
    }

    if (segments.size() > 0) {
      // get last segment's end position
      builder.add(segments.get(segments.size() - 1).getEndPosition());
    }

    return builder.build();
  }

  @Override
  public boolean contains(Point p, int margin) {
    for (WireSegment s : segments) {
      if (s.contains(p, margin)) {
        return true;
      }
    }
    return false;
  }

  private void drawAllSegments(DrawableContext context) {
    for (WireSegment segment : segments) {
      segment.draw(context, false, false);
    }
  }
  
  @Override
  public void draw(DrawableContext context, boolean selected, boolean hovered) {
    context.save();

    if (selected) {
      context.save();
      context.setLineWidth(context.getLineWidth() * 3);
      context.setColor(SELECTED_COLOR.getRed(), SELECTED_COLOR.getGreen(), SELECTED_COLOR.getBlue());
      drawAllSegments(context);
      context.restore();
      context.setColor(DEFAULT_COLOR.getRed(), DEFAULT_COLOR.getGreen(), DEFAULT_COLOR.getBlue());
      drawAllSegments(context);

    } else if (hovered) {
      context.save();
      context.setLineWidth(context.getLineWidth() * 3);
      context.setColor(HOVERED_COLOR.getRed(), HOVERED_COLOR.getGreen(), HOVERED_COLOR.getBlue());
      drawAllSegments(context);
      context.restore();
      context.setColor(DEFAULT_COLOR.getRed(), DEFAULT_COLOR.getGreen(), DEFAULT_COLOR.getBlue());
      drawAllSegments(context);

    } else {
      context.setColor(DEFAULT_COLOR.getRed(), DEFAULT_COLOR.getGreen(), DEFAULT_COLOR.getBlue());
      drawAllSegments(context);
      
    }

    context.restore();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((segments == null) ? 0 : segments.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Wire other = (Wire) obj;
    if (segments == null) {
      if (other.segments != null) return false;
    } else if (!segments.equals(other.segments)) return false;
    return true;
  }

  public int getWireSegmentIndexAt(Point mousePosition, int margin) {
    int segmentIndex = 0;
    for (WireSegment segment : segments) {
      if (segment.contains(mousePosition, margin)) {
        return segmentIndex;
      }
      segmentIndex++;
    }

    return -1;
  }

  public boolean isWireSegmentVertical(int wireSegmentIndex) {
    return segments.get(wireSegmentIndex).isVertical;
  }

  @Override
  public String toString() {
    return "Wire [startPortId=" + startPortId + ", endPortId=" + endPortId + ", getPoints()="
        + getPoints() + "]";
  }

}
