package com.ytchen.beindexing.exp.graph.obj;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObj;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CompactedObjectSequenceTest {

    @Test
    public void minusTest(){
        List<CompactedObj> list1 = Arrays.asList(
                new CompactedObj(Arrays.asList("id1","id2","id3"), "a"),
                new CompactedObj(Arrays.asList("id4","id5"), "b"),
                new CompactedObj(Arrays.asList("id11","id12","id13"), "c")
        );
        List<CompactedObj> list2 = Arrays.asList(
                new CompactedObj(Arrays.asList("id2","id3"), "a"),
                new CompactedObj(Arrays.asList("id4"), "b"),
                new CompactedObj(Arrays.asList("id12","id13"), "c")
        );
        CompactedObjectSequence seq1 = new CompactedObjectSequence(list1);
        CompactedObjectSequence seq2 = new CompactedObjectSequence(list2);
        System.out.println("result:"+seq1.minus(seq2));
    }

    @Test
    public void unionTest() {

        List<CompactedObj> list1 = Arrays.asList(
                new CompactedObj(Arrays.asList("id1","id2","id3"), "a"),
                new CompactedObj(Arrays.asList("id4"), "b"),
                new CompactedObj(Arrays.asList("id12"), "c")
        );
        List<CompactedObj> list2 = Arrays.asList(
                new CompactedObj(Arrays.asList("id1","id3"), "a"),
                new CompactedObj(Arrays.asList("id5"), "b"),
                new CompactedObj(Arrays.asList("id11","id13"), "c")
        );
        CompactedObjectSequence seq1 = new CompactedObjectSequence(list1);
        CompactedObjectSequence seq2 = new CompactedObjectSequence(list2);
        System.out.println("result:"+seq1.union(seq2));
    }

    @Test
    public void intersectionTest() {

        List<CompactedObj> list1 = Arrays.asList(
                new CompactedObj(Arrays.asList("ida2"), "a"),
                new CompactedObj(Arrays.asList("idb1", "idb2"), "b"),
                new CompactedObj(Arrays.asList("idc1", "idc2"), "c")
        );
        List<CompactedObj> list2 = Arrays.asList(
                new CompactedObj(Arrays.asList("ida2"), "a"),
                new CompactedObj(Arrays.asList("idb2"), "b"),
                new CompactedObj(Arrays.asList("idc2"), "c")
        );
        CompactedObjectSequence seq1 = new CompactedObjectSequence(list1);
        CompactedObjectSequence seq2 = new CompactedObjectSequence(list2);
        System.out.println("result:"+seq1.intersect(seq2));
    }
}
