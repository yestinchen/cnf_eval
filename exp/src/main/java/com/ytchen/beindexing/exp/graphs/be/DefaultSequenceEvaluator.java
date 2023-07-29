package com.ytchen.beindexing.exp.graphs.be;

import com.ytchen.beindexing.exp.cnf.ICNFBatchEvaluator;
import com.ytchen.beindexing.exp.cnf.ICNFEvaluator;
import com.ytchen.beindexing.exp.graphs.builder.IStateBuilder;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultSequenceEvaluator implements SequenceEvaluator {

    private IStateBuilder builder;
    private ICNFBatchEvaluator evaluator;

    public DefaultSequenceEvaluator(IStateBuilder builder, ICNFBatchEvaluator icnfEvaluator) {
        this.builder = builder;
        this.evaluator = icnfEvaluator;
    }

    @Override
    public List<Integer> evaluate(CompactedObjectSequence sequence) {
        List<CompactedObjectSequence> result = builder.feed(sequence);
        if (result == null || result.size() == 0) return null;
        List<Integer> finalResult = evaluator.evaluateBatch(result.stream().map(i -> i.genAssignment()).collect(Collectors.toList()));
        if (finalResult.size() == 0) return null;
        return finalResult;
    }

    @Override
    public void reset() {
        builder.reset();
    }

}
