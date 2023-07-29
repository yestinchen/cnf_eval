package com.ytchen.beindexing.exp.expression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Reader {

    public static List<CNFExpression> readCNFExpressions(String file) throws FileNotFoundException {
        FileReader fileReader= new FileReader(new File(file));
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        final int[] count = {0};
        return bufferedReader.lines().filter(x -> !x.startsWith("#")).map(i ->
                CNFExpression.fromStringWithId(i, ++count[0])).collect(Collectors.toList());
    }

    public static Set<String> readClasses(String file) throws FileNotFoundException {
        List<CNFExpression> expressions = readCNFExpressions(file);
        Set<String> result = new HashSet<>();
        for (CNFExpression e : expressions) {
            for (DisjunctionExpression de: e.getExpressions()) {
                for (SimpleExpression se: de.getExpressions()) {
                    result.add(se.getLeft());
                }
            }
        }
        return result;
    }
}
