package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.Reader;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CNFAlgorithmTest {

    @Test
    public void evaluateTest() throws FileNotFoundException {
        List<CNFExpression> cnfExpressionList = Reader.readCNFExpressions("./data/exp/t2.expressions");
        InvertedList invertedList = InvertedList.fromCNFExpressions(cnfExpressionList);
        Map<Integer, int[]> counterMap = new HashMap<>();
        for (CNFExpression cnfExpression: cnfExpressionList) {
            counterMap.put(cnfExpression.getId(), cnfExpression.computeCounters());
        }

        CNFAlgorithm cnfAlgorithm = new CNFAlgorithm(invertedList, counterMap);
        List<Integer> result = cnfAlgorithm.evaluate(Arrays.asList(new Tuple2<>("A", 1), new Tuple2<>("C", 2)));
        System.out.println("final result : " + result);
    }

}
