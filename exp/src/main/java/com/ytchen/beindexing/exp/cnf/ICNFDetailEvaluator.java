package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.cnf.bean.ResultDetail;
import com.ytchen.beindexing.exp.common.Tuple2;

import java.util.List;

public interface ICNFDetailEvaluator {
    public ResultDetail evaluate(List<Tuple2<String, Integer>> assignment);
}
