package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.common.Tuple2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ICNFEvaluator extends ICNFBatchEvaluator {
    public List<Integer> evaluate(List<Tuple2<String, Integer>> assignment);

    default List<Integer> evaluateBatch(List<List<Tuple2<String, Integer>>> assignments){
        Set<Integer> result = new HashSet<>();
        if(assignments != null) {
            for (List<Tuple2<String, Integer>> assignment: assignments) {
                result.addAll(evaluate(assignment));
            }
        }
        return new ArrayList<>(result);
    }
}
