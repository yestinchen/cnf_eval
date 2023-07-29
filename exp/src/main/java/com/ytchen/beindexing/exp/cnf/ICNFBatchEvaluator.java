package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.common.Tuple2;

import java.util.List;

public interface ICNFBatchEvaluator {
    public List<Integer> evaluateBatch(List<List<Tuple2<String, Integer>>> assignment);
}
