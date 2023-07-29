package com.ytchen.beindexing.exp.expression;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ytchen.beindexing.exp.utils.StringUtils.strip;
import static com.ytchen.beindexing.exp.utils.StringUtils.toStringWithBrackets;

public class SimpleExpression extends Expression<String, Integer, List<Integer>> {
    @Override
    public String toString() {
        if (op  == OP.IN) return left+" in " + toStringWithBrackets(right);
        else if (op == OP.NOTIN) return left + " notin " + toStringWithBrackets(right);
        else if (op == OP.EQUAL) return left + " = " + right.get(0);
        else if (op == OP.GE) return left + " >= " + right.get(0);
        else if (op == OP.LE) return left + " <= " + right.get(0);
        return "Unknown{" +
                "left=" + left +
                ", op=" + op +
                ", right=" + right +
                '}';
    }

    public static SimpleExpression fromString(String str) {
        str = strip(str);
        SimpleExpression exp = new SimpleExpression();
        String[] arr = null;
        if (str.contains(" in ")) {
            arr = str.split(" in ");
            exp.op = OP.IN;
        }else if (str.contains(" notin ")) {
            arr = str.split(" notin ");
            exp.op = OP.NOTIN;
        } else if (str.contains(" >= ")) {
            arr = str.split(" >= ");
            exp.op = OP.GE;
        } else if (str.contains(" <= ")) {
            arr = str.split(" <= ");
            exp.op = OP.LE;
        } else if (str.contains(" = ")) {
            arr = str.split(" = ");
            exp.op = OP.EQUAL;
        }
        if (arr == null) {
            System.err.println(" Critical error! not recognized op: " +str);
            System.exit(-1);
        }
        exp.left = arr[0].trim();
        String rightPart = arr[1].trim();
        if (exp.op == OP.IN || exp.op == OP.NOTIN) {
            exp.right = Arrays.stream(rightPart.substring(1,
                    rightPart.length() - 1).split("[,]")).map(i ->
                    Integer.valueOf(i.trim())).collect(Collectors.toList());
        } else if (exp.op == OP.EQUAL || exp.op == OP.LE || exp.op == OP.GE) {
            // will only allow one value.
            exp.right = Arrays.asList(Integer.valueOf(rightPart));
        }
        return exp;
    }
}
