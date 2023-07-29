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

public class OverlappedCNFGenerator {

    int classNum;

    double overlapPercent;

    int value;

    int disjunctionLength = 2;
    int conjunctionLength = 3;

    public OverlappedCNFGenerator(int classNum, double overlapPercent, int value) {
        this.classNum = classNum;
        this.overlapPercent = overlapPercent;
        this.value = value;

        // compute record.
        // 5 * 2 * 3 = 30 conditions.
        // min overlap: 5 -5 = 0.
        // max overlap: (30-5)/5= 500%.

    }

    public void generate(Callback callback) throws IOException {
        // first generate samples.
        SimpleExpression[] sampleExpressions = new SimpleExpression[classNum];
        for (int i =0; i < classNum; i++) {
            SimpleExpression simpleExpression = new SimpleExpression();
            simpleExpression.setLeft(String.valueOf((char)('a'+i)));
            simpleExpression.setOp(OP.GE);
            simpleExpression.setRight(Arrays.asList(value));
            sampleExpressions[i] = simpleExpression;
        }
        int overlapped = 0;
        int expectedOverlapped = (int) (overlapPercent * classNum);
        int remainingConditions = disjunctionLength * conjunctionLength * classNum;
        for (int queryId = 0; queryId < classNum; queryId ++) {
            CNFExpression cnfExpression = new CNFExpression();
            List<DisjunctionExpression> disjunctionExpressions = new ArrayList<>();
            for (int i =0; i < conjunctionLength; i++) {
                DisjunctionExpression disjunctionExpression = new DisjunctionExpression();
                List<SimpleExpression> simpleExpressions = new ArrayList<>();
                for (int j = 0; j < disjunctionLength; j++) {
                    double random = Math.random();
                    if (expectedOverlapped - overlapped == remainingConditions || (random > 0.5 && expectedOverlapped > overlapped)) {
                        // overlap one.
                        int index = -1;
                        while(index <0 || index > classNum || index == queryId) {
                            index = (int) (Math.random() * 5);
                        }
                        simpleExpressions.add(sampleExpressions[index]);
                        overlapped ++;
                    } else {
                        simpleExpressions.add(sampleExpressions[queryId]);
                    }
                    remainingConditions --;
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


    public static void generateFile(int classNum, int value, double overlapPercent, boolean override) throws Exception {
        String fileName = "./datagen/exp/overlapped-3-2-"+classNum+"-"+value+"-"+ overlapPercent+".cnf2";
        if (!override) {
            // check if file exists.
            if (new File(fileName).isFile()) {
                System.err.println("file ["+fileName+"] already exists, will not generate new file.");
                return;
            }
        }
        OverlappedCNFGenerator generator = new OverlappedCNFGenerator(classNum, overlapPercent, value);
        FileWriter fileWriter = new FileWriter(new File(fileName));
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        generator.generate(obj -> {
            bufferedWriter.write(obj.toString().split(":")[1].trim());
            bufferedWriter.write("\n");
        });
        bufferedWriter.close();
        fileWriter.close();
    }

    public static void main(String[] args) throws Exception {
        generateFile(5, 10, 0, false);
        generateFile(5, 10, 1, false);
        generateFile(5, 10, 2, false);
        generateFile(5, 10, 3, false);
        generateFile(5, 10, 4, false);
        generateFile(5, 10, 5, false);


        generateFile(5, 15, 0, false);
        generateFile(5, 15, 1, false);
        generateFile(5, 15, 2, false);
        generateFile(5, 15, 3, false);
        generateFile(5, 15, 4, false);
        generateFile(5, 15, 5, false);


        generateFile(5, 20, 0, false);
        generateFile(5, 20, 1, false);
        generateFile(5, 20, 2, false);
        generateFile(5, 20, 3, false);
        generateFile(5, 20, 4, false);
        generateFile(5, 20, 5, false);
    }

}
