package com.ytchen.beindexing.exp.cnf;

import org.junit.Test;

public class PostingItemTest {

    @Test
    public void toStringTest() {
        PostingItem item = new PostingItem(1, PostingItem.PostingOperator.IN, 2);
        System.out.println(item);
        item = new PostingItem(1, PostingItem.PostingOperator.NOTIN, 5);
        System.out.println(item);
    }
}
