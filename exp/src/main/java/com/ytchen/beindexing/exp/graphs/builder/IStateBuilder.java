package com.ytchen.beindexing.exp.graphs.builder;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;

import java.util.List;

public interface IStateBuilder {

    List<CompactedObjectSequence> feed(CompactedObjectSequence sequence);

    public void reset();
}
