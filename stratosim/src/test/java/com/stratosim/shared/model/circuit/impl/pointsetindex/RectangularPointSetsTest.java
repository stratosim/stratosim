package com.stratosim.shared.model.circuit.impl.pointsetindex;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

import com.stratosim.shared.model.circuit.impl.pointsetindex.RectangularPointSet;
import com.stratosim.shared.model.circuit.impl.pointsetindex.RectangularPointSets;

public class RectangularPointSetsTest {

  private RectangularPointSet m5;
  private RectangularPointSet m1;
  private RectangularPointSet p0;
  private RectangularPointSet p6;

  @Before
  public void setUp() {
    m5 = allBounds(-5);
    m1 = allBounds(-1);
    p0 = allBounds(0);
    p6 = allBounds(6);
  }

  @Test
  public void testCompareByMinX() {
    Comparator<RectangularPointSet> cmp = RectangularPointSets.compareByMinX();

    assertCompares(cmp);
  }

  @Test
  public void testCompareByMaxX() {
    Comparator<RectangularPointSet> cmp = RectangularPointSets.compareByMaxX();

    assertCompares(cmp);
  }

  @Test
  public void testCompareByMinY() {
    Comparator<RectangularPointSet> cmp = RectangularPointSets.compareByMinY();

    assertCompares(cmp);
  }

  @Test
  public void testCompareByMaxY() {
    Comparator<RectangularPointSet> cmp = RectangularPointSets.compareByMaxY();

    assertCompares(cmp);
  }

  private void assertCompares(Comparator<RectangularPointSet> cmp) {
    // equals

    assertEquals(0, cmp.compare(m5, m5));
    assertEquals(0, cmp.compare(m1, m1));
    assertEquals(0, cmp.compare(p0, p0));
    assertEquals(0, cmp.compare(p6, p6));

    // less

    assertEquals(-1, cmp.compare(m5, m1));
    assertEquals(-1, cmp.compare(m5, p0));
    assertEquals(-1, cmp.compare(m5, p6));

    assertEquals(-1, cmp.compare(m1, p0));
    assertEquals(-1, cmp.compare(m1, p6));

    assertEquals(-1, cmp.compare(p0, p6));

    // greater

    assertEquals(1, cmp.compare(p6, p0));
    assertEquals(1, cmp.compare(p6, m1));
    assertEquals(1, cmp.compare(p6, m5));

    assertEquals(1, cmp.compare(p0, m1));
    assertEquals(1, cmp.compare(p0, m5));

    assertEquals(1, cmp.compare(m1, m5));
  }

  private static RectangularPointSet allBounds(int b) {
    return RectangularPointSets.newRPSet(b, b, b, b);
  }

}
