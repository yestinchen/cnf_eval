package com.ytchen.beindexing.exp.graphs.io;

import com.ytchen.beindexing.exp.common.Tuple2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class EvalSequenceWriter {

    public static final String SPLITTER="---";

    String file;
    BufferedWriter bufferedWriter;
    FileWriter fileWriter;
    int currentLine = 0;
    public EvalSequenceWriter(String file) throws IOException {
        this.file = file;
        fileWriter = new FileWriter(file);
        bufferedWriter = new BufferedWriter(fileWriter);
    }

    public void write(List<List<Tuple2<String, Integer>>> assignments) throws IOException {
        for (List<Tuple2<String, Integer>> assignment: assignments) {
            int i =0;
            for (Tuple2<String, Integer> kv: assignment) {
                if (i != 0) {
                    bufferedWriter.write(";");
                }
                i++;
                bufferedWriter.write(kv.get_1());
                bufferedWriter.write(",");
                bufferedWriter.write(String.valueOf(kv.get_2()));
            }
            bufferedWriter.write("\n");
        }
    }

    public void newFrame() throws IOException {
        bufferedWriter.write(SPLITTER+"\n");
    }

    public void close() throws IOException {
        bufferedWriter.flush();
        bufferedWriter.close();
        fileWriter.close();
    }
}
