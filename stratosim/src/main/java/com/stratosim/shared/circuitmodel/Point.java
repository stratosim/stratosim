package com.stratosim.shared.circuitmodel;

import java.io.Serializable;
import java.util.Comparator;

// Immutable
public class Point implements Serializable {
  private static final long serialVersionUID = -1708575773593894222L;

  private int x;
  private int y;

  public static final Comparator<Point> COMPARE_BY_X = new XComparator();
  public static final Comparator<Point> COMPARE_BY_Y = new YComparator();

  @SuppressWarnings("unused")
  // for GWT RPC
  private Point() {}

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public String toString() {
    return "(" + x + "," + y + ")";
  }

  public Point translate(int dx, int dy) {
    return new Point(getX() + dx, getY() + dy);
  }

  public Point scale(double scale) {
    return new Point((int) (getX() * scale), (int) (getY() * scale));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Point other = (Point) obj;
    if (x != other.x) return false;
    if (y != other.y) return false;
    return true;
  }

  private static class XComparator implements Comparator<Point> {
    @Override
    public int compare(Point arg0, Point arg1) {
      if (arg0.x < arg1.x) {
        return -1;
      } else if (arg0.x > arg1.x) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  private static class YComparator implements Comparator<Point> {
    @Override
    public int compare(Point arg0, Point arg1) {
      if (arg0.y < arg1.y) {
        return -1;
      } else if (arg0.y > arg1.y) {
        return 1;
      } else {
        return 0;
      }
    }
  }
}
