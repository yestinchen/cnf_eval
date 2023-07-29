package com.ytchen.beindexing.exp.expression;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ytchen.beindexing.exp.utils.StringUtils.strip;

public class CNFExpression {

    List<DisjunctionExpression> expressions;
    Integer op = OP.AND;
    int id;

    public List<DisjunctionExpression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<DisjunctionExpression> expressions) {
        this.expressions = expressions;
    }

    public Integer getOp() {
        return op;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int computeK() {
        int count =0;
        for (DisjunctionExpression e : expressions) {
            boolean noCounting = e.getExpressions().stream().anyMatch(i -> i.getOp() == OP.NOTIN);
            if (!noCounting) count++;
        }
        return count;
    }

    public int[] computeCounters() {
        int[] result = new int[expressions.size()];
        for (int i =0 ; i < expressions.size(); i++) {
            result[i] = - expressions.get(i).getNotInNum();
        }
        return result;
    }

    public int computeCode() {
        int code = 0;
        for (int i = 0; i < expressions.size(); i++) {
            code |= (1 << i);
        }
        return code;
    }

    public static CNFExpression fromStringWithId(String str, int id) {
        String[] arr = str.split(" and ");
        CNFExpression list = new CNFExpression();
        list.expressions = Arrays.stream(arr).map(i ->
                DisjunctionExpression.fromString(i)).collect(Collectors.toList());
        list.id = id;
        return list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("#").append(id).append(": ");
        for(int i =0; i < expressions.size(); i++) {
            DisjunctionExpression expression = expressions.get(i);
            if (i != expressions.size() -1) {
                sb.append("(").append(expression).append(")");
                sb.append(" and ");
            } else {
                sb.append("(").append(expression).append(")");
            }
        }
        return sb.toString();
    }
}
