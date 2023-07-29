package com.ytchen.beindexing.exp.graphs.io;

import com.ytchen.beindexing.exp.common.Tuple2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EvalSequenceReader {
    String file;
    FileReader fileReader;
    BufferedReader bufferedReader;
    String lastLine = null;
    public EvalSequenceReader(String file) throws FileNotFoundException {
        this.file = file;
        fileReader = new FileReader(file);
        bufferedReader = new BufferedReader(fileReader);
    }

    public List<List<List<Tuple2<String, Integer>>>> read() throws IOException {
        String line = null;
        List<List<List<Tuple2<String, Integer>>>> totalList = new ArrayList<>();
        while((line = bufferedReader.readLine()) != null) {
            List<List<Tuple2<String, Integer>>> frameList = new ArrayList<>();
            while (!EvalSequenceWriter.SPLITTER.equals(line)) {
                line = bufferedReader.readLine();
                if (!line.equals(EvalSequenceWriter.SPLITTER)) {
                    String[] kvs = line.split(";");
                    List<Tuple2<String, Integer>> assignment = new ArrayList<>();
                    for (String kv : kvs) {
                        Tuple2<String, Integer> tuple = new Tuple2<>(
                                kv.split(",")[0], Integer.valueOf(kv.split(",")[1]));
                        assignment.add(tuple);
                    }
                    frameList.add(assignment);
                }
            }
            if (frameList.size() == 0) {
                totalList.add(null);
            } else {
                totalList.add(frameList);
            }
        }
        return totalList;
    }
//
//    public boolean isEOF() throws IOException {
//        bufferedReader.mark(1);
//        int i = bufferedReader.read();
//        bufferedReader.reset();
//        return i < 0;
//    }

    public void close() throws IOException {
        bufferedReader.close();
        fileReader.close();
    }
}
