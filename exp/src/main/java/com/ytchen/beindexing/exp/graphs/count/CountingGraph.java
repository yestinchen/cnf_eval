package com.ytchen.beindexing.exp.graphs.count;

import java.util.ArrayList;
import java.util.List;

public class CountingGraph<T> {

    List<Bucket<SimpleRuntimeLoad>> rootBuckets = new ArrayList<>();

    public List<Bucket<SimpleRuntimeLoad>> getRootBuckets() {
        return rootBuckets;
    }

    public void setRootBuckets(List<Bucket<SimpleRuntimeLoad>> rootBuckets) {
        this.rootBuckets = rootBuckets;
    }
}
