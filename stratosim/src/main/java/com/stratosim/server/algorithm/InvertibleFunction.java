package com.stratosim.server.algorithm;

public interface InvertibleFunction<T, U> {

  U forward(T t);

  T reverse(U u);

}
