package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.DisjunctionExpression;
import com.ytchen.beindexing.exp.expression.OP;
import com.ytchen.beindexing.exp.expression.Reader;
import com.ytchen.beindexing.exp.expression.SimpleExpression;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressionEvalAlgorithm implements ICNFEvaluator {
    List<CNFExpression> cnfExpressions;
    public ExpressionEvalAlgorithm(List<CNFExpression> expression) {
        this.cnfExpressions = expression;
    }

//    @Override
//    public List<Integer> evaluateBatch(List<List<Tuple2<String, Integer>>> assignments) {
//        if (assignments != null) {
//            for (List<Tuple2<String, Integer>> assginment: assignments) {
//                List<Integer> result = evaluate(assginment);
//                if (result.size() > 0) return result;
//            }
//        }
//        return null;
//    }

    @Override
    public List<Integer> evaluate(List<Tuple2<String, Integer>> assignment) {
        // turn it into k-v.
        Map<String, Integer> kvs = new HashMap<>();
        for (Tuple2<String, Integer> v : assignment) {
            kvs.put(v.get_1(), v.get_2());
        }
        List<Integer> resultList = new ArrayList<>();
        for (int i =0; i < cnfExpressions.size(); i++) {
            CNFExpression cnfExpression = cnfExpressions.get(i);
            // get through all expressions.
            boolean result = true;
            for (DisjunctionExpression expression : cnfExpression.getExpressions()) {
                boolean thisone = false;
                for (SimpleExpression exp : expression.getExpressions()) {
                    if (thisone) break;
                    switch (exp.getOp()) {
                        case OP.GE:
                            if (kvs.get(exp.getLeft()) >= exp.getRight().get(0)) {
                                thisone = true;
                            }
                            break;
                        case OP.EQUAL:
                            if (kvs.get(exp.getLeft()) == exp.getRight().get(0)) {
                                thisone = true;
                            }
                            break;
                    }
                }
                if (!thisone) {
                    result = false;
                    break;
                }
            }
            resultList.add(result ? i+1 : null);
        }
        return resultList;
    }

    public static ExpressionEvalAlgorithm fromFile(String file) throws FileNotFoundException {
        List<CNFExpression> cnfExpressionList = Reader.readCNFExpressions(file);
        return new ExpressionEvalAlgorithm(cnfExpressionList);
    }
}
