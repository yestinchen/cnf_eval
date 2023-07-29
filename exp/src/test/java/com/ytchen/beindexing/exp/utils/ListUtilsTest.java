package com.ytchen.beindexing.exp.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.ytchen.beindexing.exp.utils.ListUtils.insertToSortedList;
import static org.junit.Assert.assertEquals;

public class ListUtilsTest {

    @Test
    public void testInsert() {
        List<Integer> list = new ArrayList<>();
        insertToSortedList(list, 1, Integer::compareTo, false);
        assertEquals(list.toString(), "[1]");
        insertToSortedList(list, 2, Integer::compareTo, false);
        assertEquals(list.toString(), "[1, 2]");
        insertToSortedList(list, 4, Integer::compareTo, false);
        assertEquals(list.toString(), "[1, 2, 4]");
        insertToSortedList(list, 3, Integer::compareTo, false);
        assertEquals(list.toString(), "[1, 2, 3, 4]");
        insertToSortedList(list, 10, Integer::compareTo, false);
        assertEquals(list.toString(), "[1, 2, 3, 4, 10]");
        insertToSortedList(list, 7, Integer::compareTo, false);
        assertEquals(list.toString(), "[1, 2, 3, 4, 7, 10]");
        insertToSortedList(list, 5, Integer::compareTo, false);
        assertEquals(list.toString(), "[1, 2, 3, 4, 5, 7, 10]");
        insertToSortedList(list, 5, Integer::compareTo, false);
        assertEquals(list.toString(), "[1, 2, 3, 4, 5, 5, 7, 10]");
        insertToSortedList(list, 0, Integer::compareTo, false);
        assertEquals(list.toString(), "[0, 1, 2, 3, 4, 5, 5, 7, 10]");
    }


    @Test
    public void testInsert2() {
        List<Integer> list = new ArrayList<>();
        insertToSortedList(list, 1, Integer::compareTo, true);
        assertEquals(list.toString(), "[1]");
        insertToSortedList(list, 2, Integer::compareTo, true);
        assertEquals(list.toString(), "[1, 2]");
        insertToSortedList(list, 4, Integer::compareTo, true);
        assertEquals(list.toString(), "[1, 2, 4]");
        insertToSortedList(list, 3, Integer::compareTo, true);
        assertEquals(list.toString(), "[1, 2, 3, 4]");
        insertToSortedList(list, 10, Integer::compareTo, true);
        assertEquals(list.toString(), "[1, 2, 3, 4, 10]");
        insertToSortedList(list, 7, Integer::compareTo, true);
        assertEquals(list.toString(), "[1, 2, 3, 4, 7, 10]");
        insertToSortedList(list, 5, Integer::compareTo, true);
        assertEquals(list.toString(), "[1, 2, 3, 4, 5, 7, 10]");
        insertToSortedList(list, 5, Integer::compareTo, true);
        assertEquals(list.toString(), "[1, 2, 3, 4, 5, 7, 10]");
        insertToSortedList(list, 0, Integer::compareTo, true);
        assertEquals(list.toString(), "[0, 1, 2, 3, 4, 5, 7, 10]");
        list = new ArrayList<>();
        insertToSortedList(list, 8, Integer::compareTo, true);
        assertEquals(list.toString(), "[8]");
        insertToSortedList(list, 9, Integer::compareTo, true);
        assertEquals(list.toString(), "[8, 9]");
        insertToSortedList(list, 9, Integer::compareTo, true);
        assertEquals(list.toString(), "[8, 9]");
    }
}
