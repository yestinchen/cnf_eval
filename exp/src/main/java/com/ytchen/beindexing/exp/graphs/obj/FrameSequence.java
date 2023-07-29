package com.ytchen.beindexing.exp.graphs.obj;

import java.util.Iterator;
import java.util.List;

public class FrameSequence<T extends Sortable> implements Iterable<T> {
    List<T> sequence;

    public FrameSequence(List<T> sequence) {
        this.sequence = sequence;
    }

    public List<T> getSequence() {
        return sequence;
    }

    public void setSequence(List<T> sequence) {
        this.sequence = sequence;
    }

    public void ensureSorted() {
        for (T t: sequence) {
            t.sort();
        }
    }

    @Override
    public String toString() {
        if (sequence == null || sequence.size() == 0) return "no frames";
        StringBuilder sb = new StringBuilder();
        int count[] = new int[]{0};
        sequence.forEach(i -> sb.append("#").append(++count[0]).append(": ").append(i).append("\n"));
        return sb.toString();

    }

    @Override
    public Iterator<T> iterator() {
        return sequence.iterator();
    }
}
