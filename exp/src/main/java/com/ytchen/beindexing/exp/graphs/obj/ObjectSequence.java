package com.ytchen.beindexing.exp.graphs.obj;

import java.util.List;

public class ObjectSequence implements Sortable {
    List<NormalObj> sequence;

    public ObjectSequence(List<NormalObj> sequence) {
        this.sequence = sequence;
    }

    public List<NormalObj> getSequence() {
        return sequence;
    }

    public void setSequence(List<NormalObj> sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return sequence.toString().replace("[", "(").replace("]",")");
    }

    @Override
    public void sort() {
        // do nothing.
        System.err.println("sort ");
        System.exit(-1);
    }
}
