package com.ytchen.beindexing.exp.graphs.state;

import com.ytchen.beindexing.exp.graphs.count.Bucket;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;

import java.util.List;

/**
 * state use simple connections & boolean flag.
 * @param <T>
 */
public class StateV1<T> {

    CompactedObjectSequence sequence;

    List<StateEdge<StateV1<T>>> edges;

    boolean flag;

    Bucket<T> bucket;

    int count = 0;

    public StateV1(CompactedObjectSequence sequence) {
        this.sequence = sequence;
    }

    public CompactedObjectSequence getSequence() {
        return sequence;
    }

    public void setSequence(CompactedObjectSequence sequence) {
        this.sequence = sequence;
    }

    public List<StateEdge<StateV1<T>>> getEdges() {
        return edges;
    }

    public void setEdges(List<StateEdge<StateV1<T>>> edges) {
        this.edges = edges;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void resetFlag() {
        flag = false;
        if (edges != null) {
            for (StateEdge e : edges) {
                ((StateV1)e.nextState).resetFlag();
            }
        }
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

    @Override
    public String toString() {
        return "StateV1{" +
                "sequence=" + sequence +
                ", count=" + count +
                ", edges=" + edges +
                ", flag=" + flag +
                '}';
    }
}
