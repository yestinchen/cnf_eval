package com.ytchen.beindexing.exp.generator;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObj;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import com.ytchen.beindexing.exp.graphs.obj.FrameSequence;
import com.ytchen.beindexing.exp.graphs.obj.SequenceReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.ytchen.beindexing.exp.generator.OcclusionReproducerV3.readInfo;

public class ReduceObjectsGen {

    public static void reduce(String file, double v) throws IOException {
        file = file.replaceAll(".txt","-filtered-4.txt");
        String output = file.replaceAll(".txt", "-reduced-"+v+".txt");

        Map<String, Map<String, List<Integer>>> objOccMapx = readInfo(file);
        Map<String, List<Integer>> objMap = new HashMap<>();
        for (Map<String, List<Integer>> m : objOccMapx.values()) {
            objMap.putAll(m);
        }
        Random random = new Random();
        // random delete keys.
        int totalNum = objMap.keySet().size();
        int toReduce = (int) (totalNum * v);
        for (int i=0; i < toReduce; i++) {
            List<String> currentKeys = new ArrayList<>(objMap.keySet());
            String reduceKey = currentKeys.get(random.nextInt(currentKeys.size()));
            objMap.remove(reduceKey);
        }

        // output the rest.

        FileWriter fileWriter = new FileWriter(output);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        FrameSequence<CompactedObjectSequence> frames = SequenceReader.readCompactedFramesFromFile(file);

        int frameNum = 0;
        for (CompactedObjectSequence frame: frames) {
            if (frame == null) {
                frameNum++;
                bufferedWriter.write("\n");

                continue;
            }
            List<CompactedObj> translatedObjs = new ArrayList<>();
            for (CompactedObj objs: frame.getSequence()) {
                for (int i =objs.getIds().size() - 1; i >= 0; i--) {
                    String id = objs.getIds().get(i);
                    if (!objMap.containsKey(id)) {
                        objs.getIds().remove(i);
                    }
                }
                translatedObjs.add(new CompactedObj(objs.getIds(), objs.getClazz()));
            }

            CompactedObjectSequence translatedFrame = new CompactedObjectSequence(translatedObjs);
            bufferedWriter.write(translatedFrame.toString());
            bufferedWriter.write("\n");
            frameNum++;
        }
        bufferedWriter.close();
        fileWriter.close();
    }

    public static void main(String[] args) throws IOException {
        reduce("./datagen/new2/MVI_40751.txt", 0);
        reduce("./datagen/new2/MVI_40751.txt", 0.1);
        reduce("./datagen/new2/MVI_40751.txt", 0.2);
        reduce("./datagen/new2/MVI_40751.txt", 0.3);

        reduce("./datagen/new2/MVI_40171.txt", 0);
        reduce("./datagen/new2/MVI_40171.txt", 0.1);
        reduce("./datagen/new2/MVI_40171.txt", 0.2);
        reduce("./datagen/new2/MVI_40171.txt", 0.3);

        reduce("./datagen/new2/MOT16-06.txt", 0);
        reduce("./datagen/new2/MOT16-06.txt", 0.1);
        reduce("./datagen/new2/MOT16-06.txt", 0.2);
        reduce("./datagen/new2/MOT16-06.txt", 0.3);

        reduce("./datagen/new2/MOT16-13.txt", 0);
        reduce("./datagen/new2/MOT16-13.txt", 0.1);
        reduce("./datagen/new2/MOT16-13.txt", 0.2);
        reduce("./datagen/new2/MOT16-13.txt", 0.3);

        reduce("./datagen/new2/visualroad1.txt", 0);
        reduce("./datagen/new2/visualroad1.txt", 0.1);
        reduce("./datagen/new2/visualroad1.txt", 0.2);
        reduce("./datagen/new2/visualroad1.txt", 0.3);

        reduce("./datagen/new2/visualroad2.txt", 0);
        reduce("./datagen/new2/visualroad2.txt", 0.1);
        reduce("./datagen/new2/visualroad2.txt", 0.2);
        reduce("./datagen/new2/visualroad2.txt", 0.3);
    }
}
