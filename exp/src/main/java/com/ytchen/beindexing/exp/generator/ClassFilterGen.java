package com.ytchen.beindexing.exp.generator;

import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObj;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import com.ytchen.beindexing.exp.graphs.obj.FrameSequence;
import com.ytchen.beindexing.exp.graphs.obj.SequenceReader;

import java.io.IOException;
import java.util.*;

public class ClassFilterGen {

    static void produceFiltered(String fileName, List<String> classes) throws IOException {
        String outputFile = fileName.replaceAll(".txt","-filtered-"+classes.size()+ ".txt");

        FrameSequence<CompactedObjectSequence> frames = SequenceReader.readCompactedFramesFromFile(fileName);

        List<CompactedObjectSequence> cosList = new ArrayList<>();
        for (CompactedObjectSequence seq : frames) {
            if (seq == null || seq.getSequence() == null) {
                cosList.add(null);
            } else {
                List<CompactedObj>  compactedObjs = new ArrayList<>();
                for (CompactedObj obj : seq.getSequence()) {
                    if (classes.contains(obj.getClazz())){
                        obj.setIds(new ArrayList<>(new HashSet<>(obj.getIds())));
                        compactedObjs.add(obj);
                    }
                }
                if (compactedObjs.size() > 0) {
                    CompactedObjectSequence newSeq = new CompactedObjectSequence(compactedObjs);
                    cosList.add(newSeq);
                } else {
                    cosList.add(null);
                }
            }
        }
        FrameSequence<CompactedObjectSequence> newFrames = new FrameSequence<>(cosList);

        Writer.write(outputFile, newFrames);
    }

    public static void main(String[] args) throws IOException {
        List<String> classes = Arrays.asList("person", "car", "truck", "bus");
        produceFiltered("./datagen/new2/visualroad1.txt", classes);
        produceFiltered("./datagen/new2/visualroad2.txt", classes);
        produceFiltered("./datagen/new2/MOT16-13.txt", classes);
        produceFiltered("./datagen/new2/MOT16-06.txt", classes);
        produceFiltered("./datagen/new2/MVI_40171.txt", classes);
        produceFiltered("./datagen/new2/MVI_40751.txt", classes);
    }
}
