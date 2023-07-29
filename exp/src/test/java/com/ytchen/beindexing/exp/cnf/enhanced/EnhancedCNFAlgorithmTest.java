package com.ytchen.beindexing.exp.cnf.enhanced;

import com.ytchen.beindexing.exp.cnf.CNFAlgorithm;
import com.ytchen.beindexing.exp.cnf.InvertedList;
import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.Reader;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnhancedCNFAlgorithmTest {

    @Test
    public void evaluateTest() throws FileNotFoundException {
        List<CNFExpression> cnfExpressionList = Reader.readCNFExpressions("./data/exp/2semantics1.expressions");
        InvertedList invertedList = InvertedList.fromCNFExpressions(cnfExpressionList);
        OrderedInvertedList orderedInvertedList = OrderedInvertedList.fromCNFExpressions(cnfExpressionList);
        Map<Integer, int[]> counterMap = new HashMap<>();
        for (CNFExpression cnfExpression: cnfExpressionList) {
            counterMap.put(cnfExpression.getId(), cnfExpression.computeCounters());
        }

        EnhancedCNFAlgorithm cnfAlgorithm = new EnhancedCNFAlgorithm(invertedList, orderedInvertedList, counterMap);
        List<Integer> result = cnfAlgorithm.evaluate(Arrays.asList(new Tuple2<>("A", 4), new Tuple2<>("C", 2)));
        System.out.println("final result : " + result);
        result = cnfAlgorithm.evaluate(Arrays.asList(new Tuple2<>("A", 4), new Tuple2<>("C", 1)));
        System.out.println("final result : " + result);
        result = cnfAlgorithm.evaluate(Arrays.asList(new Tuple2<>("A", 4), new Tuple2<>("D", 1)));
        System.out.println("final result : " + result);
        result = cnfAlgorithm.evaluate(Arrays.asList(new Tuple2<>("A", 2), new Tuple2<>("D", 1)));
        System.out.println("final result : " + result);
        result = cnfAlgorithm.evaluate(Arrays.asList(new Tuple2<>("B", 4), new Tuple2<>("D", 1)));
        System.out.println("final result : " + result);
    }

}
