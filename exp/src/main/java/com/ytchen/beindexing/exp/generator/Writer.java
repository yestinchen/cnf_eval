package com.ytchen.beindexing.exp.generator;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObj;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import com.ytchen.beindexing.exp.graphs.obj.FrameSequence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Writer {

    public static void write(String filePath, FrameSequence<CompactedObjectSequence> frames) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        int frameNum = 0;
        for (CompactedObjectSequence frame: frames) {
            if (frame == null) {
                frameNum++;
                bufferedWriter.write("\n");

                continue;
            }
            bufferedWriter.write(frame.toString());
            bufferedWriter.write("\n");
            frameNum++;
        }
        bufferedWriter.close();
        fileWriter.close();
    }

    public static void main(String[] args) {


    }
}
