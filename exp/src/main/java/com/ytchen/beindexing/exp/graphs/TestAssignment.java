package com.ytchen.beindexing.exp.graphs;

import com.ytchen.beindexing.exp.common.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestAssignment {

    private static List<Tuple2<String, Integer>> calMissingAssignment(List<Tuple2<String, Integer>> oldAssign, List<Tuple2<String, Integer>> newAssign) {
        // they should have the same order.
        int i=0, j = 0;
        List<Tuple2<String, Integer>> delta = new ArrayList<>();
        while(i < oldAssign.size() && j < newAssign.size()) {
            Tuple2<String, Integer> oldTuple = oldAssign.get(i);
            Tuple2<String, Integer> newTuple = newAssign.get(j);
            if (oldTuple.get_1().equals(newTuple.get_1())) {
                if (oldTuple.get_2() > newTuple.get_2()) {
                    for (int k = newTuple.get_2()+1; k <= oldTuple.get_2(); k++) {
                        delta.add(new Tuple2<>(oldTuple.get_1(), k));
                    }
                }
                i++;j++;
            } else {
                // if they not eq. means missing oldTuple
                for (int k = 1; k <= oldTuple.get_2(); k++) {
                    delta.add(new Tuple2<>(oldTuple.get_1(), k));
                }
                i++;
            }
        }
        // check if there are any left.
        while (i < oldAssign.size()) {
            Tuple2<String, Integer> oldTuple = oldAssign.get(i);
            for (int k = 1; k <= oldTuple.get_2(); k++) {
                delta.add(new Tuple2<>(oldTuple.get_1(), k));
            }
            i++;
        }
        return delta;
    }

    public static void main(String[] args) {
        System.out.println(calMissingAssignment(
                Arrays.asList(new Tuple2<>("x", 2), new Tuple2<>("y",9), new Tuple2<>("z", 2)),
                Arrays.asList(new Tuple2<>("y",6))
        ));
    }
}
