package com.stratosim.shared.model.circuit.impl.pointsetindex.impl;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.stratosim.shared.model.circuit.impl.pointsetindex.RectangularPointSets.newRPSet;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.stratosim.shared.model.circuit.impl.pointsetindex.RectangularPointSet;
import com.stratosim.shared.model.circuit.impl.pointsetindex.RectangularPointSetIndex;
import com.stratosim.shared.model.circuit.impl.pointsetindex.impl.BruteForceRectangularPointSetIndex;

public class BruteForceRectangularPointSetIndexTest {

  private RectangularPointSetIndex<Object> index;

  private Object o1, o2;
  private RectangularPointSet p1, s1, p2, s2;

  @Before
  public void setUp() throws Exception {
    index = new BruteForceRectangularPointSetIndex<Object>();

    o1 = new Object();
    p1 = newRPSet(0, 4, 0, 4);
    s1 = newRPSet(1, 3, 1, 3);

    o2 = new Object();
    p2 = newRPSet(7, 9, 7, 9);
    s2 = newRPSet(8, 8, 8, 8);
  }

  @After
  public void tearDown() throws Exception {}

  @Test(expected = NullPointerException.class)
  public void testGet_null() {
    newInstance().get(null);
  }

  @Test(expected = NullPointerException.class)
  public void testSet_null1() {
    newInstance().put(null, new Object());
  }

  @Test(expected = NullPointerException.class)
  public void testSet_null2() {
    newInstance().put(newRPSet(0, 0, 0, 0), null);
  }

  @Test
  public void testPutAndGet_single1() {
    index.put(p1, o1);
    assertSame(o1, getOnlyElement(index.get(p1)));
    assertSame(o1, getOnlyElement(index.get(s1)));
    assertEmpty(index.get(s2));
  }

  @Test
  public void testPutAndGet_single2() {
    index.put(p2, o2);
    assertSame(o2, getOnlyElement(index.get(p2)));
    assertSame(o2, getOnlyElement(index.get(s2)));
    assertEmpty(index.get(s1));
  }

  @Test
  public void testGetAndSet_overlap() {
    index.put(p1, o1);
    index.put(p2, o2);

    Object o3 = new Object();
    RectangularPointSet p3 = newRPSet(3, 8, 3, 8), s3 = newRPSet(5, 5, 5, 5);
    index.put(p3, o3);

    assertSame(o3, getOnlyElement(index.get(s3)));

    RectangularPointSet q1 = newRPSet(2, 5, 2, 5);
    RectangularPointSet q2 = newRPSet(5, 8, 5, 8);
    RectangularPointSet q3 = newRPSet(2, 8, 2, 8);
    assertSymmetricContainsAll(ImmutableList.of(o1, o3), index.get(q1));
    assertSymmetricContainsAll(ImmutableList.of(o2, o3), index.get(q2));
    assertSymmetricContainsAll(ImmutableList.of(o1, o2, o3), index.get(q3));
  }

  private static <T> void assertSymmetricContainsAll(Collection<T> a, Collection<T> b) {
    assertTrue(a.containsAll(b));
    assertTrue(b.containsAll(a));
  }

  private static <T> void assertEmpty(Collection<T> ps) {
    assertTrue(ps.isEmpty());
  }

  private static <T> BruteForceRectangularPointSetIndex<T> newInstance() {
    return new BruteForceRectangularPointSetIndex<T>();
  }

}
