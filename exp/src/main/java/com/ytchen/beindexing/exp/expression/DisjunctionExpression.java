package com.ytchen.beindexing.exp.expression;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ytchen.beindexing.exp.utils.StringUtils.strip;

public class DisjunctionExpression {
    List<SimpleExpression> expressions;
    Integer op = OP.AND;

    public List<SimpleExpression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<SimpleExpression> expressions) {
        this.expressions = expressions;
    }

    public Integer getOp() {
        return op;
    }

    public int getNotInNum() {
        return (int) expressions.stream().filter(i -> i.op == OP.NOTIN).count();
    }

    public static DisjunctionExpression fromString(String str) {
        String[] arr;
        DisjunctionExpression list = new DisjunctionExpression();
        arr = strip(str).split(" or ");
        list.op = OP.OR;
        list.expressions = Arrays.stream(arr).map(i ->
                SimpleExpression.fromString(i)).collect(Collectors.toList());
        return list;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i =0; i < expressions.size(); i++) {
            SimpleExpression expression = expressions.get(i);
            sb.append(expression);
            if (i != expressions.size() -1) {
                sb.append(" or ");
            }
        }
        return sb.toString();
    }
}
