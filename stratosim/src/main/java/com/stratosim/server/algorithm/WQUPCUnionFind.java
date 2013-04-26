package com.stratosim.server.algorithm;

/*
 * http://algs4.cs.princeton.edu/15uf/
 */

public class WQUPCUnionFind implements UnionFind {
  private int[] id; // id[i] = parent of i
  private int[] sz; // sz[i] = number of objects in subtree rooted at i
  private int count; // number of components

  // instantiate N isolated components 0 through N-1
  public WQUPCUnionFind(int N) {
    count = N;
    id = new int[N];
    sz = new int[N];
    for (int i = 0; i < N; i++) {
      id[i] = i;
      sz[i] = 1;
    }
  }

  // return number of connected components
  public int count() {
    return count;
  }

  // return component identifier for component containing p
  public int find(int p) {
    while (p != id[p]) {
      id[p] = id[id[p]]; // path compression by halving
      p = id[p];
    }
    return p;
  }

  // are elements p and q in the same component?
  public boolean connected(int p, int q) {
    return find(p) == find(q);
  }

  // merge components containing p and q, making smaller root point to larger
  // one
  public void union(int p, int q) {
    int i = find(p);
    int j = find(q);
    if (i == j) return;
    if (sz[i] < sz[j]) {
      id[i] = j;
      sz[j] += sz[i];
    } else {
      id[j] = i;
      sz[i] += sz[j];
    }
    count--;
  }
}
