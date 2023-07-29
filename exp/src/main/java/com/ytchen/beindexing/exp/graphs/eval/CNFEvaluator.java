package com.ytchen.beindexing.exp.graphs.eval;

import com.ytchen.beindexing.exp.cnf.ICNFBatchEvaluator;
import com.ytchen.beindexing.exp.cnf.ICNFEvaluator;
import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;

import java.io.FileNotFoundException;
import java.util.*;

public class CNFEvaluator {
    ICNFEvaluator algorithm;
    ICNFBatchEvaluator batchAlgorithm;
    Feeder feeder;

    public CNFEvaluator(ICNFEvaluator algorithm, Feeder feeder) throws FileNotFoundException {
        //algorithm = CNFAlgorithm.fromFile(file);
        this.algorithm = algorithm;
        this.feeder = feeder;
    }

    public CNFEvaluator(ICNFBatchEvaluator algorithm, Feeder feeder) throws FileNotFoundException {
        //algorithm = CNFAlgorithm.fromFile(file);
        this.batchAlgorithm = algorithm;
        this.feeder = feeder;
    }

    public List<Integer> eval(CompactedObjectSequence sequence) {
        List<List<Tuple2<String, Integer>>> result = feeder.feed(sequence);
        if (result == null) return new ArrayList<>();
        Set<Integer> results = new HashSet<>();
        if (this.batchAlgorithm != null) {
            // batch mode.
            return batchAlgorithm.evaluateBatch(result);
        } else {
            for (List<Tuple2<String, Integer>> r : result) {
                results.addAll(algorithm.evaluate(r));
            }
            return new ArrayList<>(results);
        }
    }


    public static interface Feeder {
        List<List<Tuple2<String, Integer>>> feed(CompactedObjectSequence seq);
    }
}
