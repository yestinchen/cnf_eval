package com.ytchen.beindexing.exp.graphs.state;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;

public class StateEdge<T> implements Comparable<StateEdge<T>>{
    CompactedObjectSequence delta;
    T nextState;

    public CompactedObjectSequence getDelta() {
        return delta;
    }

    public void setDelta(CompactedObjectSequence delta) {
        this.delta = delta;
    }

    public T getNextState() {
        return nextState;
    }

    public void setNextState(T nextState) {
        this.nextState = nextState;
    }

    public boolean isSubsetOf(CompactedObjectSequence other) {
        CompactedObjectSequence inter = delta.intersect(other);
        return inter != null && inter.size() == delta.size();
    }

    @Override
    public String toString() {
        return "StateEdge{" +
                "delta=" + delta +
                ", nextState=" + nextState +
                '}';
    }

    @Override
    public int compareTo(StateEdge<T> o) {
        return delta.compareTo(o.delta);
    }
}
