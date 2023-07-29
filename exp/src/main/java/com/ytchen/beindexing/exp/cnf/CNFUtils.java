package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.DisjunctionExpression;
import com.ytchen.beindexing.exp.expression.Reader;
import com.ytchen.beindexing.exp.expression.SimpleExpression;
import com.ytchen.beindexing.exp.utils.ListUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CNFUtils {

    public static Map<Tuple2<String, Integer>, List<Integer>> readCNFQueriesToMap(String file) throws FileNotFoundException {
        List<CNFExpression> cnfExpressionList =  Reader.readCNFExpressions(file);
        // assemble
        Map<Tuple2<String, Integer>, List<Integer>> result = new HashMap<>();
        for (CNFExpression expression: cnfExpressionList) {
            for (DisjunctionExpression disjunctionExpression: expression.getExpressions()) {
                for (SimpleExpression simpleExpression: disjunctionExpression.getExpressions()) {
                    // get.
                    Tuple2<String, Integer> key = new Tuple2<>( simpleExpression.getLeft(), simpleExpression.getRight().get(0));
                    List<Integer> list =  result.getOrDefault(key, new ArrayList<>());
                    if (list.size() == 0 || list.get(list.size() -1) == expression.getId()) {
                        list.add(expression.getId());
                        result.put(key, list);
                    }
                }
            }
        }
        return result;
    }

    public static Map<String, List<Tuple2<Integer, List<Integer>>>> readCNFQueriesToOrderedMap(String file) throws FileNotFoundException {

        List<CNFExpression> cnfExpressionList =  Reader.readCNFExpressions(file);
        // assemble
        Map<String, List<Tuple2<Integer, List<Integer>>>> result = new HashMap<>();
        for (CNFExpression expression: cnfExpressionList) {
            for (DisjunctionExpression disjunctionExpression: expression.getExpressions()) {
                for (SimpleExpression simpleExpression: disjunctionExpression.getExpressions()) {
                    // get.
                    String key = simpleExpression.getLeft();
                    int value = simpleExpression.getRight().get(0);
                    List<Tuple2<Integer, List<Integer>>> list =  result.getOrDefault(key, new ArrayList<>());
                    // query the value.
                    Tuple2<Integer, List<Integer>> existingTuple =
                            ListUtils.searchList(list, new Tuple2<>(value, null), Comparator.comparingInt(x -> (int) x.get_1()));
                    if (existingTuple == null) {
                        // put a new one.
                        ListUtils.insertToSortedList(list, new Tuple2<>(value, new ArrayList<>(Arrays.asList(expression.getId()))),
                                Comparator.comparing(x -> x.get_1()), true);
                    } else {
                        if (existingTuple.get_2().get(existingTuple.get_2().size() -1) != expression.getId()) {
                            existingTuple.get_2().add(expression.getId());
                        }
                    }
                    result.put(key, list);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
//        String file = "./exp/data/exp/test_gen1.cnf2";
        String file = "./datagen/exp/fixed-5-3-2-10-100-100.cnf2";
        Map<Tuple2<String, Integer>, List<Integer>> result = readCNFQueriesToMap(file);
        for (Map.Entry<Tuple2<String, Integer>, List<Integer>> entry: result.entrySet()) {
            System.out.println(entry);
        }
        System.out.println("----------------");
        Map<String, List<Tuple2<Integer, List<Integer>>>> result2 = readCNFQueriesToOrderedMap(file);
        result2.entrySet().forEach(System.out::println);
    }
}
