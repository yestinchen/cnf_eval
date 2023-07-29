package com.ytchen.beindexing.exp.cnf.bit;

import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.Reader;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitVersionCNFAlgorithmTest {

    @Test
    public void evaluateTest() throws FileNotFoundException {
        List<CNFExpression> cnfExpressionList = Reader.readCNFExpressions("./data/exp/t3.expressions");
        BitVersionInvertedList invertedList = BitVersionInvertedList.fromCNFExpressions(cnfExpressionList);
        Map<Integer, Integer> counterMap = new HashMap<>();
        for (CNFExpression cnfExpression: cnfExpressionList) {
            counterMap.put(cnfExpression.getId(), cnfExpression.computeCode());
        }

        BitVersionCNFAlgorithm cnfAlgorithm = new BitVersionCNFAlgorithm(invertedList, counterMap);
        List<Integer> result = cnfAlgorithm.evaluate(Arrays.asList(new Tuple2<>("A", 1), new Tuple2<>("C", 2), new Tuple2<>("C", 1)));
        System.out.println("final result : " + result);
    }

}
