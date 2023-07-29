package com.ytchen.beindexing.exp.graphs.obj;

public interface SetOperation<T> {

    T minus(T other);

    T intersect(T other);

    T union(T other);
}
