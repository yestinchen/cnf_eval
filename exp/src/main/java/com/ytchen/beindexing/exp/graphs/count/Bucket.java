package com.ytchen.beindexing.exp.graphs.count;

import com.ytchen.beindexing.exp.common.Tuple2;

import java.util.List;

public class Bucket<T> {

    List<Tuple2<String, Integer>> assignment;

    T load;

    public List<Tuple2<String, Integer>> getAssignment() {
        return assignment;
    }

    public void setAssignment(List<Tuple2<String, Integer>> assignment) {
        this.assignment = assignment;
    }

    public T getLoad() {
        return load;
    }

    public void setLoad(T load) {
        this.load = load;
    }
}
