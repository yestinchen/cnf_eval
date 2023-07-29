package com.ytchen.beindexing.exp.cnf.enhanced;

import com.ytchen.beindexing.exp.cnf.InvertedList;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.Reader;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;

public class OrderedInvertedListTest {

    @Test
    public void fromCNFExpressionsTest() throws FileNotFoundException {
        List<CNFExpression> e = Reader.readCNFExpressions("./data/exp/2semantics1.expressions");
        OrderedInvertedList list = OrderedInvertedList.fromCNFExpressions(e);
        System.out.println(list);
    }


}
