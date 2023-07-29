package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.cnf.enhanced.EnhancedCNFAlgorithm;
import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.ExpressionUtils;
import com.ytchen.beindexing.exp.expression.Reader;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RangeCNFAlgorithmTest {

    @Test
    public void testEnhancedOne() throws FileNotFoundException {
        List<CNFExpression> cnfExpressions =
                Reader.readCNFExpressions("../datagen/exp/exactly3-3-2-5-2-2-20-60-0.0-50.cnf2");
        cnfExpressions =  ExpressionUtils.replaceWithGE(cnfExpressions);
        EnhancedCNFAlgorithm enhancedCNFAlgorithm = EnhancedCNFAlgorithm.fromCNFExpressions(cnfExpressions);

        List<Tuple2<String, Integer>> assignment = Arrays.asList(
                new Tuple2<>("a", 20),
                new Tuple2<>("b", 20),
                new Tuple2<>("c", 20),
                new Tuple2<>("d", 20),
                new Tuple2<>("e", 20)
        );
        System.out.println(enhancedCNFAlgorithm.evaluate(assignment));
    }

    @Test
    public void testRangeEval() throws FileNotFoundException {
        List<CNFExpression> cnfExpressions =
                Reader.readCNFExpressions("../datagen/exp/exactly3-3-2-5-2-2-20-60-0.0-50.cnf2");
        cnfExpressions =  ExpressionUtils.replaceWithGE(cnfExpressions);
        RangeCNFAlgorithm rangeCNFAlgorithm = RangeCNFAlgorithm.fromCNFExpressions(cnfExpressions);

        List<Tuple2<String, Integer>> assignment = Arrays.asList(
                new Tuple2<>("a", 22),
                new Tuple2<>("b", 22),
                new Tuple2<>("c", 22),
                new Tuple2<>("d", 22),
                new Tuple2<>("e", 22)
        );

        RangeCNFAlgorithm.EvalResult result = rangeCNFAlgorithm.evaluate(assignment);
        for (int queryId: result.getResult()) {
            System.out.println("#"+queryId+":"+result.getCountMap().get(queryId));
        }

    }
}
