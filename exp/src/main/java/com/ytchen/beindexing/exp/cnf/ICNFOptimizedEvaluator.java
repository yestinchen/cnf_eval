package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.common.Tuple2;

import java.util.List;

public interface ICNFOptimizedEvaluator  extends ICNFEvaluator {
    List<Integer> evaluate(List<Tuple2<String, Integer>> assignment, List<Integer> evaluatingIds);
}
