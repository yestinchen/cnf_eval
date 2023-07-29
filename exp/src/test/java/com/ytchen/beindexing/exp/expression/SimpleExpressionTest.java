package com.ytchen.beindexing.exp.expression;

import org.junit.Test;

public class SimpleExpressionTest {

    @Test
    public void fromStringTest() {
        System.out.println(SimpleExpression.fromString("A in {1,2}"));
    }
}
