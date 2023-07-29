package com.ytchen.beindexing.exp.expression;

import org.junit.Assert;
import org.junit.Test;

public class CNFExpressionTest {

    @Test
    public void fromStringTest1() {
        String str = "A notin {1} or B in {1}";
        CNFExpression expression = CNFExpression.fromStringWithId(str, 1);
        System.out.println(expression);
        Assert.assertTrue(expression.toString().contains(str));
    }

    @Test
    public void fromStringTest2() {
        String str = "A >= 0";
        CNFExpression expression = CNFExpression.fromStringWithId(str, 1);
        System.out.println(expression);
        Assert.assertTrue(expression.toString().contains(str));
    }
}
