package com.stratosim.shared.model.circuit.impl.pointsetindex.impl;

import static com.stratosim.shared.model.circuit.impl.pointsetindex.RectangularPointSets.newRPSet;
import static com.stratosim.shared.model.util.CircuitCoords.newCircuitCoord;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.stratosim.shared.model.circuit.impl.pointsetindex.PointSet;
import com.stratosim.shared.model.circuit.impl.pointsetindex.RectangularPointSet;

public class RectangularPointSetImplTest {

  @Test(expected = NullPointerException.class)
  public void testContains_null() {
    newRPSet(0, 0, 0, 0).contains(null);
  }

  @Test
  public void testContains() {
    createAndAssertInAndOut(0, 0, 0, 0);
    createAndAssertInAndOut(-1, 1, -1, 1);
    createAndAssertInAndOut(3, 4, 5, 6);
    createAndAssertInAndOut(0, 0, 1, 2);
    createAndAssertInAndOut(-2, -1, 0, 0);
  }

  @Test
  public void isEmpty() {
    createAndAssertNonEmpty(0, 0, 0, 0);
    createAndAssertNonEmpty(-1, 1, -1, 1);
    createAndAssertNonEmpty(3, 4, 5, 6);
    createAndAssertNonEmpty(0, 0, 1, 2);
    createAndAssertNonEmpty(-2, -1, 0, 0);

    createAndAssertEmpty(1, -1, -1, 1);
    createAndAssertEmpty(-1, 1, 1, -1);
  }

  @Test(expected = NullPointerException.class)
  public void testIntersection_null() {
    newRPSet(0, 0, 0, 0).intersect(null);
  }

  @Test
  public void testIntersection() {
    RectangularPointSet a = newRPSet(-2, 1, -2, 1);
    RectangularPointSet b = newRPSet(-1, 2, -1, 2);

    assertInAndOut(a.intersect(a), -2, 1, -2, 1);
    assertInAndOut(a.intersect(b), -1, 1, -1, 1);
    assertInAndOut(b.intersect(a), -1, 1, -1, 1);
    assertInAndOut(b.intersect(b), -1, 2, -1, 2);
  }

  private static void createAndAssertInAndOut(int minX, int maxX, int minY, int maxY) {
    assertInAndOut(newRPSet(minX, maxX, minY, maxY), minX, maxX, minY, maxY);
  }

  private static void createAndAssertNonEmpty(int minX, int maxX, int minY, int maxY) {
    assertFalse(newRPSet(minX, maxX, minY, maxY).isEmpty());
  }

  private static void createAndAssertEmpty(int minX, int maxX, int minY, int maxY) {
    assertTrue(newRPSet(minX, maxX, minY, maxY).isEmpty());
  }

  private static void assertInAndOut(PointSet set, int minX, int maxX, int minY, int maxY) {

    // bounds corners are included

    assertTrue(set.contains(newCircuitCoord(minX, minY)));
    assertTrue(set.contains(newCircuitCoord(minX, maxY)));
    assertTrue(set.contains(newCircuitCoord(maxX, maxY)));
    assertTrue(set.contains(newCircuitCoord(maxX, minY)));

    // bounds edges are included

    int midX = (minX + maxX) / 2;
    boolean uniqueMidX = (midX != minX && midX != maxX);
    if (uniqueMidX) {
      assertTrue(set.contains(newCircuitCoord(midX, minY)));
      assertTrue(set.contains(newCircuitCoord(midX, maxY)));
    }

    int midY = (minY + maxY) / 2;
    boolean uniqueMidY = (midY != minY && midY != maxY);
    if (uniqueMidY) {
      assertTrue(set.contains(newCircuitCoord(-1, midY)));
      assertTrue(set.contains(newCircuitCoord(1, midY)));
    }

    // interior is included

    if (uniqueMidX && uniqueMidY) {
      assertTrue(set.contains(newCircuitCoord(midX, midY)));
    }

    // outside is excluded
    assertFalse(set.contains(newCircuitCoord(minX - 1, minY)));
    assertFalse(set.contains(newCircuitCoord(minX, minY - 1)));
    assertFalse(set.contains(newCircuitCoord(minX - 1, maxY)));
    assertFalse(set.contains(newCircuitCoord(minX, maxY + 1)));
    assertFalse(set.contains(newCircuitCoord(maxX + 1, maxY)));
    assertFalse(set.contains(newCircuitCoord(maxX, maxY + 1)));
    assertFalse(set.contains(newCircuitCoord(maxX + 1, minY)));
    assertFalse(set.contains(newCircuitCoord(maxX, minY - 1)));
  }
}
