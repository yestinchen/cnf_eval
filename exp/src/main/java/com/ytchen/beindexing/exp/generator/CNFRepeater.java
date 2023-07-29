package com.ytchen.beindexing.exp.generator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CNFRepeater {

    public static void repeat(String file, int times) throws IOException {
        File readFile = new File(file);
        String fName = readFile.getName().split("[.]")[0];
        File writeFile = new File(readFile.getParent()+"/"+fName+"-r-"+times+".cnf2");

        FileReader fr = new FileReader(readFile);
        BufferedReader bufferedReader = new BufferedReader(fr);

        String line = null;
        List<String> content = new ArrayList<>();
        while((line = bufferedReader.readLine())!= null) {
            content.add(line);
        }

        FileWriter fileWriter = new FileWriter(writeFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int i=0; i < times; i++) {
            for (String c : content) {
                bufferedWriter.write(c+"\n");
            }
        }

        bufferedReader.close();
        fr.close();

        bufferedWriter.close();
        fileWriter.close();
    }

    public static void main(String[] args) throws IOException {
//        repeat("datagen/exp/fixed-5-3-2-10-50-100.cnf2", 1);
//        repeat("datagen/exp/fixed-5-3-2-10-50-100.cnf2", 2);
//        repeat("datagen/exp/fixed-5-3-2-10-50-100.cnf2", 3);
//        repeat("datagen/exp/fixed-5-3-2-10-50-100.cnf2", 4);
//        repeat("datagen/exp/fixed-5-3-2-10-50-100.cnf2", 5);
//
//        repeat("datagen/exp/fixed-5-3-2-15-50-100.cnf2", 1);
//        repeat("datagen/exp/fixed-5-3-2-15-50-100.cnf2", 2);
//        repeat("datagen/exp/fixed-5-3-2-15-50-100.cnf2", 3);
//        repeat("datagen/exp/fixed-5-3-2-15-50-100.cnf2", 4);
//        repeat("datagen/exp/fixed-5-3-2-15-50-100.cnf2", 5);
//
//        repeat("datagen/exp/fixed-5-3-2-20-50-100.cnf2", 1);
//        repeat("datagen/exp/fixed-5-3-2-20-50-100.cnf2", 2);
//        repeat("datagen/exp/fixed-5-3-2-20-50-100.cnf2", 3);
//        repeat("datagen/exp/fixed-5-3-2-20-50-100.cnf2", 4);
//        repeat("datagen/exp/fixed-5-3-2-20-50-100.cnf2", 5);
//
//        repeat("datagen/exp/fixed-5-3-2-25-50-100.cnf2", 1);
//        repeat("datagen/exp/fixed-5-3-2-25-50-100.cnf2", 2);
//        repeat("datagen/exp/fixed-5-3-2-25-50-100.cnf2", 3);
//        repeat("datagen/exp/fixed-5-3-2-25-50-100.cnf2", 4);
//        repeat("datagen/exp/fixed-5-3-2-25-50-100.cnf2", 5);

        repeat("datagen/exp/attr-3-2-2-100.cnf2", 1);
        repeat("datagen/exp/attr-3-2-2-100.cnf2", 2);
        repeat("datagen/exp/attr-3-2-2-100.cnf2", 3);
        repeat("datagen/exp/attr-3-2-2-100.cnf2", 4);
        repeat("datagen/exp/attr-3-2-2-100.cnf2", 5);
    }
}
