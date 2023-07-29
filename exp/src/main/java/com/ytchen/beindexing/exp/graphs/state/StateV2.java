package com.ytchen.beindexing.exp.graphs.state;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;

import javax.swing.plaf.nimbus.State;
import java.util.List;

public class StateV2<T> {
    CompactedObjectSequence sequence;

    List<StateEdge<StateV2<T>>> edges;

    int count;

    T load;

    int flagVersion;

    public StateV2 (CompactedObjectSequence sequence) {
        this.sequence = sequence;
    }

    public CompactedObjectSequence getSequence() {
        return sequence;
    }

    public void setSequence(CompactedObjectSequence sequence) {
        this.sequence = sequence;
    }

    public List<StateEdge<StateV2<T>>> getEdges() {
        return edges;
    }

    public void setEdges(List<StateEdge<StateV2<T>>> edges) {
        this.edges = edges;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incCount() {
        this.count ++;
    }

    public T getLoad() {
        return load;
    }

    public void setLoad(T load) {
        this.load = load;
    }

    public int getFlagVersion() {
        return flagVersion;
    }

    public void setFlagVersion(int flagVersion) {
        this.flagVersion = flagVersion;
    }
}
