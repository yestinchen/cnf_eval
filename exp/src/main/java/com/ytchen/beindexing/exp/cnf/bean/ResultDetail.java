package com.ytchen.beindexing.exp.cnf.bean;

import java.util.List;
import java.util.Map;

public class ResultDetail {

    List<Integer> result;
    Map<Integer, int[]> counterMap;

    public List<Integer> getResult() {
        return result;
    }

    public void setResult(List<Integer> result) {
        this.result = result;
    }

    public Map<Integer, int[]> getCounterMap() {
        return counterMap;
    }

    public void setCounterMap(Map<Integer, int[]> counterMap) {
        this.counterMap = counterMap;
    }

    @Override
    public String toString() {
        return "ResultDetail{" +
                "result=" + result +
                ", counterMap=" + counterMap +
                '}';
    }
}
