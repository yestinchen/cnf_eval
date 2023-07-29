package com.ytchen.beindexing.exp.expression;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class ReaderTest {

    @Test
    public void readCNFExpressionsTest() throws FileNotFoundException {
        Reader.readCNFExpressions("data/exp/t2.expressions").stream().forEach(System.out::println);
    }

    @Test
    public void readCNFExpressionsTest2() throws FileNotFoundException {
        Reader.readCNFExpressions("data/exp/2semantics1.expressions").stream().forEach(System.out::println);
    }
}
