package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.Reader;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;

public class InvertedListTest {

    @Test
    public void fromCNFExpressionsTest() throws FileNotFoundException {
        List<CNFExpression> e = Reader.readCNFExpressions("./data/exp/t2.expressions");
        InvertedList list = InvertedList.fromCNFExpressions(e);
        System.out.println(list);
    }


}
