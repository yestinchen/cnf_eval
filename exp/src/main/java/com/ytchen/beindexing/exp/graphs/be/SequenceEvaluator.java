package com.ytchen.beindexing.exp.graphs.be;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;

import java.util.List;

public interface SequenceEvaluator {

    public List<Integer> evaluate(CompactedObjectSequence sequence);

    public void reset();
}
