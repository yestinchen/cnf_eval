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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExactlyCNFGenerator {

    int classNum;
    double overlapPercent;

    int lowerBound = 0;
    int upperBound = 0;

    int disjunctionLength = 3;
    int conjunctionLength = 2;

    public ExactlyCNFGenerator(int classNum, double overlapPercent, int disjunctionLength, int conjunctionLength, int lowerBound, int upperBound) {
        this.classNum = classNum;
        this.overlapPercent = overlapPercent;
        this.disjunctionLength = disjunctionLength;
        this.conjunctionLength = conjunctionLength;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public void generate(int num, Callback callback) throws IOException {
        // checks.
        if (disjunctionLength * conjunctionLength > classNum) {
            System.out.println("classnum should larger or equal to disjunctionLength * conjunctionLength");
            return;
        }
        // values.
        int allConditions = (int) (1.0 * (upperBound - lowerBound) * classNum / (1.0 - overlapPercent));
        if (disjunctionLength * conjunctionLength * num > allConditions) {
            System.out.println("not enough conditions:"+ allConditions);
            return;
        }

        int uniqueConditions = (int) ((upperBound - lowerBound) * (1 - overlapPercent));

        // generate all possible conditions.
        SimpleExpression[][] sampleExpressions = new SimpleExpression[classNum][uniqueConditions];

        for (int i =0; i < classNum; i++) {
            for (int j = 0; j < uniqueConditions - 1; j++ ) {
                int value = j + lowerBound;
                SimpleExpression simpleExpression = new SimpleExpression();
                simpleExpression.setLeft(String.valueOf((char)('a'+i)));
                simpleExpression.setOp(OP.EQUAL);
                simpleExpression.setRight(Arrays.asList(value));
                sampleExpressions[i][j] = simpleExpression;
            }
            // add a special one. upperBound.
            SimpleExpression simpleExpression = new SimpleExpression();
            simpleExpression.setLeft(String.valueOf((char)('a'+i)));
            simpleExpression.setOp(OP.EQUAL);
            simpleExpression.setRight(Arrays.asList(upperBound));
            sampleExpressions[i][uniqueConditions-1] = simpleExpression;
        }

        // first add all unique conditions.

        int currentConditionNum = 0;
        for (int queryId = 0; queryId < num; queryId++) {
            CNFExpression cnfExpression = new CNFExpression();
            List<DisjunctionExpression> disjunctionExpressions = new ArrayList<>();
            for (int i =0; i < conjunctionLength; i++) {
                DisjunctionExpression disjunctionExpression = new DisjunctionExpression();
                List<SimpleExpression> simpleExpressions = new ArrayList<>();
                for (int j = 0; j < disjunctionLength; j++) {
                    int row = currentConditionNum % classNum;
                    if (currentConditionNum < uniqueConditions * classNum) {
                        // add directly.
                        int column = currentConditionNum / classNum;
                        simpleExpressions.add(sampleExpressions[row][column]);
                    } else {
                        // generate it.
                        int column = (int) (uniqueConditions * Math.random());
                        simpleExpressions.add(sampleExpressions[row][column]);
                    }
                    currentConditionNum ++;
                }
                disjunctionExpression.setExpressions(simpleExpressions);
                disjunctionExpressions.add(disjunctionExpression);
            }
            cnfExpression.setId(queryId);
            cnfExpression.setExpressions(disjunctionExpressions);
            callback.generated(cnfExpression);
        }
    }


    static interface Callback{
        public void generated(CNFExpression cnfExpression) throws IOException;
    }


    public static void generateFile(int classNum, int disjunctionLength, int conjunctionLength,
                                    int lowerBound, int upperBound, double overlapPercent,

                                    int total, boolean override) throws Exception {
        String fileName = "./datagen/exp/exactly-3-2-"+classNum+"-"+disjunctionLength+"-"+conjunctionLength+
                "-"+lowerBound+"-"+upperBound+"-"+ overlapPercent+"-"+total +".cnf2";
        if (!override) {
            // check if file exists.
            if (new File(fileName).isFile()) {
                System.err.println("file ["+fileName+"] already exists, will not generate new file.");
                return;
            }
        }
        ExactlyCNFGenerator generator = new ExactlyCNFGenerator(classNum, overlapPercent, disjunctionLength,
                conjunctionLength, lowerBound, upperBound);
        FileWriter fileWriter = new FileWriter(new File(fileName));
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        generator.generate(total, obj -> {
            bufferedWriter.write(obj.toString().split(":")[1].trim());
            bufferedWriter.write("\n");
        });
        bufferedWriter.close();
        fileWriter.close();
    }

    public static void main(String[] args) throws Exception {
        generateFile(5, 2, 2, 15, 55, 0, 50, false);
        generateFile(5, 2, 2, 15, 55, 0.3, 50, false);
        generateFile(5, 2, 2, 15, 55, 0.6, 50, false);
        generateFile(5, 2, 2, 15, 55, 0.9, 50, false);
    }
}


/*
overlap:

1. condition overlap.
2. select percentage?
 */