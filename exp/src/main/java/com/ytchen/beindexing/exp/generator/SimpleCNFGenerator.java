package com.ytchen.beindexing.exp.generator;

import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.DisjunctionExpression;
import com.ytchen.beindexing.exp.expression.OP;
import com.ytchen.beindexing.exp.expression.SimpleExpression;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleCNFGenerator {

    private int classNum;

    private int disjunctionLength;

    private int conjunctionLength;

    private int lowerBound;
    private int upperBound;

    public SimpleCNFGenerator(int classNum, int disjunctionLength,
                              int conjunctionLength, int lowerBound, int upperBound) {
        this.classNum = classNum;
        this.disjunctionLength = disjunctionLength;
        this.conjunctionLength = conjunctionLength;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public void generate(int length, Callback callback) throws IOException {
        for (int queryId =0; queryId < length; queryId++) {
            CNFExpression cnfExpression = new CNFExpression();
            List<DisjunctionExpression> disjunctionExpressions = new ArrayList<>();
            for (int i = 0; i < conjunctionLength; i++) {
                DisjunctionExpression disjunctionExpression = new DisjunctionExpression();
                List<SimpleExpression> simpleExpressionList = new ArrayList<>();
                for (int j = 0; j < disjunctionLength; j++) {
                    SimpleExpression simpleExpression = new SimpleExpression();
                    simpleExpression.setLeft(randomClass(classNum));
                    simpleExpression.setOp(OP.GE);
                    simpleExpression.setRight(Arrays.asList(randomValue(lowerBound, upperBound)));
                    simpleExpressionList.add(simpleExpression);
                }
                disjunctionExpression.setExpressions(simpleExpressionList);
                disjunctionExpressions.add(disjunctionExpression);
            }
            cnfExpression.setId(queryId);
            cnfExpression.setExpressions(disjunctionExpressions);
            callback.generated(cnfExpression);
        }
    }

    int randomValue(int lowerBound, int upperBound) {
        int random = (int) (lowerBound + Math.random() * (upperBound-lowerBound));
        return random;
    }

    String randomClass(int range) {
        int clazz = (int) (Math.random() * range);
        return String.valueOf((char)('a'+clazz));
    }

    static interface Callback{
        public void generated(CNFExpression cnfExpression) throws IOException;
    }

    public static void generateFile(int classNum, int disjunctionLength,
                                    int conjunctionLength, int lowerBound, int upperBound, int recordNum, boolean override) throws Exception {
        String fileName = "./datagen/exp/fixed-"+classNum+"-"+disjunctionLength+"-"+conjunctionLength+"-"+lowerBound+"-"+upperBound+"-"+recordNum+".cnf2";
        if (!override) {
            // check if file exists.
            if (new File(fileName).isFile()) {
                System.err.println("file ["+fileName+"] already exists, will not generate new file.");
                return;
            }
        }
        SimpleCNFGenerator generator = new SimpleCNFGenerator(classNum,
                disjunctionLength, conjunctionLength, lowerBound, upperBound);
        FileWriter fileWriter = new FileWriter(new File(fileName));
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        generator.generate(recordNum, obj -> {
            bufferedWriter.write(obj.toString().split(":")[1].trim());
            bufferedWriter.write("\n");
        });
        bufferedWriter.close();
        fileWriter.close();
    }

    public static void main(String[] args) throws Exception {
        // generate
        generateFile(5, 3, 2, 10, 50, 100, false);
        generateFile(5, 3, 2, 15, 50, 100, false);
        generateFile(5, 3, 2, 20, 50, 100, false);
        generateFile(5, 3, 2, 25, 50, 100, false);
    }
}
