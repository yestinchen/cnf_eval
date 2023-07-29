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

public class ExactlyCNFGenerator2 {

    int classNum;
    double overlapPercent;

    int lowerBound = 0;
    int upperBound = 0;

    int disjunctionLength = 3;
    int conjunctionLength = 2;

    public ExactlyCNFGenerator2(int classNum, double overlapPercent, int disjunctionLength, int conjunctionLength, int lowerBound, int upperBound) {
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
        int allConditions = (int) (1.0 * (upperBound - lowerBound) * classNum );
        if (disjunctionLength * conjunctionLength * num > allConditions) {
            System.out.println("not enough conditions:"+ allConditions);
            return;
        }

        int uniqueConditions = (int) ((1.0 - overlapPercent) * num * classNum);
        if (uniqueConditions > (upperBound - lowerBound)) {
            uniqueConditions = upperBound - lowerBound;
        }

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

        // swith the last to the second.
        for (int i =0; i < classNum; i++) {
            SimpleExpression tmpExpression = sampleExpressions[i][uniqueConditions -1];
            sampleExpressions[i][uniqueConditions-1] = sampleExpressions[i][1];
            sampleExpressions[i][1] = tmpExpression;
        }

        // first generate all possible states.
        List<CNFExpression> generatedExpressions = new ArrayList<>();
        int queryId = 0;
        int currentConditionNum = 0;
        for (; queryId < num * (1 - overlapPercent); queryId ++) {
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
            generatedExpressions.add(cnfExpression);
        }

        for (; queryId < num; queryId ++) {
            int randomOne= (int) (Math.random() * generatedExpressions.size());
            CNFExpression cnfExpression = generatedExpressions.get(randomOne);
            CNFExpression clonedOne = new CNFExpression();
            clonedOne.setId(queryId);
            clonedOne.setExpressions(cnfExpression.getExpressions());
            callback.generated(clonedOne);
        }

    }


    static interface Callback{
        public void generated(CNFExpression cnfExpression) throws IOException;
    }


    public static void generateFile(int classNum, int disjunctionLength, int conjunctionLength,
                                    int lowerBound, int upperBound, double overlapPercent,

                                    int total, boolean override) throws Exception {
        String fileName = "./datagen/exp/exactly2-3-2-"+classNum+"-"+disjunctionLength+"-"+conjunctionLength+
                "-"+lowerBound+"-"+upperBound+"-"+ overlapPercent+"-"+total +".cnf2";
        if (!override) {
            // check if file exists.
            if (new File(fileName).isFile()) {
                System.err.println("file ["+fileName+"] already exists, will not generate new file.");
                return;
            }
        }
        ExactlyCNFGenerator2 generator = new ExactlyCNFGenerator2(classNum, overlapPercent, disjunctionLength,
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
//        generateFile(5, 2, 2, 15, 60, 0, 50, false);
//        generateFile(5, 2, 2, 15, 60, 0.3, 50, false);
//        generateFile(5, 2, 2, 15, 60, 0.6, 50, false);
//        generateFile(5, 2, 2, 15, 60, 0.9, 50, false);
//        generateFile(5, 2, 2, 10, 60, 0, 50, false);
//        generateFile(5, 2, 2, 10, 60, 0.3, 50, false);
//        generateFile(5, 2, 2, 10, 60, 0.6, 50, false);
//        generateFile(5, 2, 2, 10, 60, 0.9, 50, false);
//        generateFile(5, 2, 2, 20, 60, 0, 50, false);
//        generateFile(5, 2, 2, 20, 60, 0.3, 50, false);
//        generateFile(5, 2, 2, 20, 60, 0.6, 50, false);
//        generateFile(5, 2, 2, 20, 60, 0.9, 50, false);

        generateFile(4, 2, 2, 15, 80, 0, 50, false);
        generateFile(4, 2, 2, 15, 80, 0.3, 50, false);
        generateFile(4, 2, 2, 15, 80, 0.6, 50, false);
        generateFile(4, 2, 2, 15, 80, 0.9, 50, false);
        generateFile(4, 2, 2, 10, 80, 0, 50, false);
        generateFile(4, 2, 2, 10, 80, 0.3, 50, false);
        generateFile(4, 2, 2, 10, 80, 0.6, 50, false);
        generateFile(4, 2, 2, 10, 80, 0.9, 50, false);
        generateFile(4, 2, 2, 20, 80, 0, 50, false);
        generateFile(4, 2, 2, 20, 80, 0.3, 50, false);
        generateFile(4, 2, 2, 20, 80, 0.6, 50, false);
        generateFile(4, 2, 2, 20, 80, 0.9, 50, false);
    }
}


/*
overlap:

1. condition overlap.
2. select percentage?
 */