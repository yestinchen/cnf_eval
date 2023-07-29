package com.ytchen.beindexing.exp.common;

import org.junit.Test;

import java.util.Arrays;

public class PostingListTest {

    @Test
    public void testToString() {

        PointingList<Integer> pointingList = new PointingList<>(Arrays.asList(1, 2, 3, 4, 5));
        System.out.println(pointingList);
    }
}
