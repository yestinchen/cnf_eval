package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.common.Tuple2;

import java.util.List;
import java.util.Map;

public interface ICNFRangeEvaluator {

    EvalResult evaluate(List<Tuple2<String, Integer>> assignment);

    public static class EvalResult {
        List<Integer> result;
        Map<Integer, Integer> countMap;

        public List<Integer> getResult() {
            return result;
        }

        public void setResult(List<Integer> result) {
            this.result = result;
        }

        public Map<Integer, Integer> getCountMap() {
            return countMap;
        }

        public void setCountMap(Map<Integer, Integer> countMap) {
            this.countMap = countMap;
        }

        @Override
        public String toString() {
            return "EvalResult{" +
                    "result=" + result +
                    ", countMap=" + countMap +
                    '}';
        }
    }

}
