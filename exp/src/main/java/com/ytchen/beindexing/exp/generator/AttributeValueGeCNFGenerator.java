package com.ytchen.beindexing.exp.generator;

import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.DisjunctionExpression;
import com.ytchen.beindexing.exp.expression.OP;
import com.ytchen.beindexing.exp.expression.SimpleExpression;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AttributeValueGeCNFGenerator {

    List<AttributeRange> ranges;
    private int disjunctionLength;

    private int conjunctionLength;


    public AttributeValueGeCNFGenerator(List<AttributeRange> ranges, int disjunctionLength,
                                        int conjunctionLength) {
        this.ranges = ranges;
        this.disjunctionLength = disjunctionLength;
        this.conjunctionLength = conjunctionLength;
    }

    public void generate(int length, SimpleCNFGenerator.Callback callback) throws IOException {
        // prepare.
        List<SimpleExpression> simpleExpressions = new ArrayList<>();
        for (AttributeRange ar : ranges) {
            int i = ar.lowerBound;
            while(i < ar.upperBound) {
                SimpleExpression simpleExpression = new SimpleExpression();
                simpleExpression.setLeft(ar.clazz);
                simpleExpression.setOp(OP.GE);
                simpleExpression.setRight(Arrays.asList(i));
                simpleExpressions.add(simpleExpression);
                i++;
            }
        }
        Random random = new Random();
        // begin.
        for (int queryId = 0; queryId < length; queryId ++) {
            CNFExpression cnfExpression = new CNFExpression();
            List<DisjunctionExpression> disjunctionExpressions = new ArrayList<>();
            for (int i = 0; i < conjunctionLength; i++) {
                Set<String> usedClasses = new HashSet<>();
                DisjunctionExpression disjunctionExpression = new DisjunctionExpression();
                List<SimpleExpression> simpleExpressionList = new ArrayList<>();
                for (int j = 0; j < disjunctionLength; j++) {
                    SimpleExpression simpleExpression = randomExpression(simpleExpressions, random);
                    while(usedClasses.contains(simpleExpression.getLeft())) {
                        simpleExpression = randomExpression(simpleExpressions, random);
                    }
                    simpleExpressionList.add(simpleExpression);
                    usedClasses.add(simpleExpression.getLeft());
                }
                disjunctionExpression.setExpressions(simpleExpressionList);
                disjunctionExpressions.add(disjunctionExpression);
            }
            cnfExpression.setId(queryId);
            cnfExpression.setExpressions(disjunctionExpressions);
            callback.generated(cnfExpression);
        }
    }

    private SimpleExpression randomExpression(List<SimpleExpression> simpleExpressions, Random random) {
        int n = random.nextInt(simpleExpressions.size());
        return simpleExpressions.get(n);
    }

    public static class AttributeRange {
        public String clazz;
        public int lowerBound;
        public int upperBound;

        public AttributeRange(String clazz, int lowerBound, int upperBound) {
            this.clazz = clazz;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }
    }


    public static void generateFile(int id, List<AttributeRange> attributeRanges, int disjunctionLength,
                                    int conjunctionLength, int recordNum, boolean override) throws Exception {
        String fileName = "./datagen/exp/attr-ge-"+id+"-"+disjunctionLength+"-"+conjunctionLength+"-"+recordNum+".cnf2";
        if (!override) {
            // check if file exists.
            if (new File(fileName).isFile()) {
                System.err.println("file ["+fileName+"] already exists, will not generate new file.");
                return;
            }
        }
        AttributeValueGeCNFGenerator generator = new AttributeValueGeCNFGenerator(attributeRanges,
                disjunctionLength, conjunctionLength);
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
//        generateFile(1, Arrays.asList(
//                new AttributeRange("person", 1, 10),
//                new AttributeRange("car", 1, 10),
//                new AttributeRange("bench", 1, 5)
//        ), 2, 2, 100, false);
//        generateFile(2, Arrays.asList(
//                new AttributeRange("person", 2, 10),
//                new AttributeRange("car", 2, 10),
//                new AttributeRange("bench", 2, 5)
//        ), 2, 2, 100, false);
//        generateFile(3, Arrays.asList(
//                new AttributeRange("person", 3, 10),
//                new AttributeRange("car", 3, 10),
//                new AttributeRange("bench", 3, 5)
//        ), 2, 2, 100, false);
//        generateFile(4, Arrays.asList(
//                new AttributeRange("person", 4, 10),
//                new AttributeRange("car", 4, 10),
//                new AttributeRange("bench", 4, 5)
//        ), 2, 2, 100, false);
//        generateFile(5, Arrays.asList(
//                new AttributeRange("person", 5, 10),
//                new AttributeRange("car", 5, 10),
//                new AttributeRange("bench", 5, 5)
//        ), 2, 2, 100, false);
//
//        generateFile(11, Arrays.asList(
//                new AttributeRange("person", 1, 10),
//                new AttributeRange("car", 1, 10)
//        ), 2, 2, 100, false);
//        generateFile(12, Arrays.asList(
//                new AttributeRange("person", 2, 10),
//                new AttributeRange("car", 2, 10)
//        ), 2, 2, 100, false);
//        generateFile(13, Arrays.asList(
//                new AttributeRange("person", 3, 10),
//                new AttributeRange("car", 3, 10)
//        ), 2, 2, 100, false);
//        generateFile(14, Arrays.asList(
//                new AttributeRange("person", 4, 10),
//                new AttributeRange("car", 4, 10)
//        ), 2, 2, 100, false);
//        generateFile(15, Arrays.asList(
//                new AttributeRange("person", 5, 10),
//                new AttributeRange("car", 5, 10)
//        ), 2, 2, 100, false);
//
//        generateFile(21, Arrays.asList(
//                new AttributeRange("person", 6, 20),
//                new AttributeRange("car", 6, 20)
//        ), 2, 2, 100, false);
//        generateFile(22, Arrays.asList(
//                new AttributeRange("person", 7, 20),
//                new AttributeRange("car", 7, 20)
//        ), 2, 2, 100, false);
//        generateFile(23, Arrays.asList(
//                new AttributeRange("person", 8, 20),
//                new AttributeRange("car", 8, 20)
//        ), 2, 2, 100, false);
//        generateFile(24, Arrays.asList(
//                new AttributeRange("person", 9, 20),
//                new AttributeRange("car", 9, 20)
//        ), 2, 2, 100, false);
//        generateFile(25, Arrays.asList(
//                new AttributeRange("person", 10, 20),
//                new AttributeRange("car", 10, 20)
//        ), 2, 2, 100, false);


//        generateFile(101, Arrays.asList(
//                new AttributeRange("person", 1, 20),
//                new AttributeRange("car", 1, 20)
//        ), 2, 2, 100, false);
//        generateFile(102, Arrays.asList(
//                new AttributeRange("person", 2, 20),
//                new AttributeRange("car", 2, 20)
//        ), 2, 2, 100, false);
//        generateFile(103, Arrays.asList(
//                new AttributeRange("person", 3, 20),
//                new AttributeRange("car", 3, 20)
//        ), 2, 2, 100, false);
//        generateFile(104, Arrays.asList(
//                new AttributeRange("person", 4, 20),
//                new AttributeRange("car", 4, 20)
//        ), 2, 2, 100, false);
//        generateFile(105, Arrays.asList(
//                new AttributeRange("person", 5, 20),
//                new AttributeRange("car", 5, 20)
//        ), 2, 2, 100, false);
//        generateFile(106, Arrays.asList(
//                new AttributeRange("person", 6, 20),
//                new AttributeRange("car", 6, 20)
//        ), 2, 2, 100, false);
//        generateFile(107, Arrays.asList(
//                new AttributeRange("person", 7, 20),
//                new AttributeRange("car", 7, 20)
//        ), 2, 2, 100, false);
//        generateFile(108, Arrays.asList(
//                new AttributeRange("person", 8, 20),
//                new AttributeRange("car", 8, 20)
//        ), 2, 2, 100, false);
//        generateFile(109, Arrays.asList(
//                new AttributeRange("person", 9, 20),
//                new AttributeRange("car", 9, 20)
//        ), 2, 2, 100, false);
//        generateFile(201, Arrays.asList(
//                new AttributeRange("truck", 1, 20),
//                new AttributeRange("car", 1, 20)
//        ), 2, 2, 100, false);
//        generateFile(202, Arrays.asList(
//                new AttributeRange("truck", 2, 20),
//                new AttributeRange("car", 2, 20)
//        ), 2, 2, 100, false);
//        generateFile(203, Arrays.asList(
//                new AttributeRange("truck", 3, 20),
//                new AttributeRange("car", 3, 20)
//        ), 2, 2, 100, false);
//        generateFile(204, Arrays.asList(
//                new AttributeRange("truck", 4, 20),
//                new AttributeRange("car", 4, 20)
//        ), 2, 2, 100, false);
//        generateFile(205, Arrays.asList(
//                new AttributeRange("truck", 5, 20),
//                new AttributeRange("car", 5, 20)
//        ), 2, 2, 100, false);
//        generateFile(206, Arrays.asList(
//                new AttributeRange("truck", 6, 20),
//                new AttributeRange("car", 6, 20)
//        ), 2, 2, 100, false);
//        generateFile(207, Arrays.asList(
//                new AttributeRange("truck", 7, 20),
//                new AttributeRange("car", 7, 20)
//        ), 2, 2, 100, false);
//        generateFile(208, Arrays.asList(
//                new AttributeRange("truck", 8, 20),
//                new AttributeRange("car", 8, 20)
//        ), 2, 2, 100, false);
//        generateFile(209, Arrays.asList(
//                new AttributeRange("truck", 9, 20),
//                new AttributeRange("car", 9, 20)
//        ), 2, 2, 100, false);
        generateFile(215, Arrays.asList(
                new AttributeRange("truck", 15, 30),
                new AttributeRange("car", 15, 30)
        ), 2, 2, 100, false);
        generateFile(220, Arrays.asList(
                new AttributeRange("truck", 20, 30),
                new AttributeRange("car", 20, 30)
        ), 2, 2, 100, false);
    }
}
