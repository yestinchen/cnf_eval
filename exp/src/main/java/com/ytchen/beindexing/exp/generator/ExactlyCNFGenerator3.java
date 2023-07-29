package com.ytchen.beindexing.exp.generator;

import com.ytchen.beindexing.exp.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class ExactlyCNFGenerator3 {


    public static void generateFile(int classNum, int disjunctionLength, int conjunctionLength,
                                    int lowerBound, int upperBound, double overlapPercent,

                                    int total, boolean override) throws Exception {
        String readFileName = "./datagen/exp/exactly2-3-2-"+classNum+"-"+disjunctionLength+"-"+conjunctionLength+
                "-"+lowerBound+"-"+upperBound+"-0.0-"+total +".cnf2";
        String writeFileName = "./datagen/exp/exactly3-3-2-"+classNum+"-"+disjunctionLength+"-"+conjunctionLength+
                "-"+lowerBound+"-"+upperBound+"-"+ overlapPercent+"-"+total +".cnf2";
        if (!new File(readFileName).isFile()) {
            System.err.println("file ["+ readFileName + "] does not exist, will not generate new files");
        }
        if (!override) {
            // check if file exists.
            if (new File(writeFileName).isFile()) {
                System.err.println("file ["+writeFileName+"] already exists, will not generate new file.");
                return;
            }
        }
        List<String> lines = FileUtils.readLines(readFileName);

        FileWriter fileWriter = new FileWriter(new File(writeFileName));
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        int bound = (int) ((1.0 - overlapPercent) * lines.size());
        for (int i =0 ; i < total; i++) {
            bufferedWriter.write(lines.get(i % bound));
            bufferedWriter.write("\n");
        }
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

        generateFile(4, 2, 2, 15, 60, 0, 50, false);
        generateFile(4, 2, 2, 15, 60, 0.3, 50, false);
        generateFile(4, 2, 2, 15, 60, 0.6, 50, false);
        generateFile(4, 2, 2, 15, 60, 0.9, 50, false);
        generateFile(4, 2, 2, 10, 60, 0, 50, false);
        generateFile(4, 2, 2, 10, 60, 0.3, 50, false);
        generateFile(4, 2, 2, 10, 60, 0.6, 50, false);
        generateFile(4, 2, 2, 10, 60, 0.9, 50, false);
        generateFile(4, 2, 2, 20, 60, 0, 50, false);
        generateFile(4, 2, 2, 20, 60, 0.3, 50, false);
        generateFile(4, 2, 2, 20, 60, 0.6, 50, false);
        generateFile(4, 2, 2, 20, 60, 0.9, 50, false);
    }
}
