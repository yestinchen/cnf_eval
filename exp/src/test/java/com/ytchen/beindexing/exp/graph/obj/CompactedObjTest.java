package com.ytchen.beindexing.exp.graph.obj;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObj;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CompactedObjTest {

    @Test
    public void minusTest() {
        CompactedObj obj1 = new CompactedObj(Arrays.asList("id1", "id2", "id3"), "a");
        CompactedObj obj2 = new CompactedObj(Arrays.asList("id2", "id3"), "a");
        assertEquals(new CompactedObj(Arrays.asList("id1"), "a"), obj1.minus(obj2));
        assertNull(obj2.minus(obj1));
    }

    @Test
    public void intersectTest() {
        CompactedObj obj1 = new CompactedObj(Arrays.asList("id1", "id2", "id3"), "a");
        CompactedObj obj2 = new CompactedObj(Arrays.asList("id2", "id3"), "a");
        assertEquals(new CompactedObj(Arrays.asList("id2", "id3"), "a"), obj1.intersect(obj2));
        assertEquals(new CompactedObj(Arrays.asList("id2", "id3"), "a"), obj2.intersect(obj1));


        obj1 = new CompactedObj(Arrays.asList("id1", "id2", "id3"), "a");
        obj2 = new CompactedObj(Arrays.asList("id3", "id6", "id8"), "a");
        assertEquals(new CompactedObj(Arrays.asList("id3"), "a"), obj1.intersect(obj2));
        assertEquals(new CompactedObj(Arrays.asList("id3"), "a"), obj2.intersect(obj1));

        obj1 = new CompactedObj(Arrays.asList("id10", "id11", "id12", "id13", "id14"), "a");
        obj1.sort();
        obj2 = new CompactedObj(Arrays.asList("id8", "id9", "id10", "id11", "id12", "id13", "id14"), "a");
        obj2.sort();
        assertEquals(obj1, obj1.intersect(obj2));
        assertEquals(obj1, obj2.intersect(obj1));


        obj1 = new CompactedObj(Arrays.asList("idb1", "idb2"), "a");
        obj1.sort();
        obj2 = new CompactedObj(Arrays.asList("idb2"), "a");
        obj2.sort();
        assertEquals(obj2, obj1.intersect(obj2));
        assertEquals(obj2, obj2.intersect(obj1));
    }
}
