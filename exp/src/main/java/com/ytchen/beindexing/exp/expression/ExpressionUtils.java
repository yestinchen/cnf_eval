package com.ytchen.beindexing.exp.expression;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ExpressionUtils {

    public static List<CNFExpression> replaceWithGE(List<CNFExpression> cnfExpressions) {
        List<CNFExpression> result = new ArrayList<>();
        for (CNFExpression cnf: cnfExpressions) {
            CNFExpression newCNF = new CNFExpression();
            newCNF.setId(cnf.getId());
            List<DisjunctionExpression> disjunctionExpressions = new ArrayList<>();
            for (DisjunctionExpression disjunctionExpression : cnf.getExpressions()) {
                DisjunctionExpression newDisj = new DisjunctionExpression();
                List<SimpleExpression> simpleExpressions = new ArrayList<>();
                for (SimpleExpression simpleExpression: disjunctionExpression.getExpressions()) {
                    SimpleExpression newSimpleExp = new SimpleExpression();
                    newSimpleExp.setLeft(simpleExpression.getLeft());
                    newSimpleExp.setOp(OP.GE);
                    newSimpleExp.setRight(simpleExpression.getRight());
                    simpleExpressions.add(newSimpleExp);
                }
                newDisj.setExpressions(simpleExpressions);
                disjunctionExpressions.add(newDisj);
            }
            newCNF.setExpressions(disjunctionExpressions);
            result.add(newCNF);
        }
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
        replaceWithGE(Reader.readCNFExpressions("./datagen/exp/overlapped-3-2-5-10-0.0.cnf2")).forEach(
                System.out::println
        );
    }
}
