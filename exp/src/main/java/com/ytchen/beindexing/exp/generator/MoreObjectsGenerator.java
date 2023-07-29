package com.ytchen.beindexing.exp.generator;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObj;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import com.ytchen.beindexing.exp.graphs.obj.FrameSequence;
import com.ytchen.beindexing.exp.graphs.obj.SequenceReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MoreObjectsGenerator {

    public static void moreObject(String file, int times) throws IOException {

        FileWriter fileWriter = new FileWriter(file.replace(".txt", "-more-"+times+".txt"));
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        FrameSequence<CompactedObjectSequence> frames = SequenceReader.readCompactedFramesFromFile(file);
//        HashMap<String, List<String>> existingObjects = new HashMap<>();
//        HashMap<String, String> mappingObjects = new HashMap<>();
//        HashMap<String, LinkedHashMap<String, Integer>> vanishedList = new HashMap<>();

        for (CompactedObjectSequence frame: frames) {
            if (frame == null) {
                bufferedWriter.write("\n");

                continue;
            }
            List<CompactedObj> translatedObjs = new ArrayList<>();

            for (int i =0; i< times; i++) {
                for (int j = 0; j < frame.getSequence().size(); j++) {
//                for (CompactedObj objs : frame.getSequence()) {
                    CompactedObj objs = frame.getSequence().get(j);
                    CompactedObj newObjs = new CompactedObj(new ArrayList<>(objs.getIds()), objs.getClazz());
                    if (i ==0) {
                        translatedObjs.add(newObjs);
                    } else {
                        List<String> newIds= new ArrayList<>();
                        for (String id: objs.getIds()) {
                            newIds.add(id+""+i);
                        }
                        Collections.sort(newIds);
                        translatedObjs.get(j).getIds().addAll(newIds);
                    }
                }
            }
            for (CompactedObj obj : translatedObjs) {
                Collections.sort(obj.getIds());
            }
            CompactedObjectSequence translatedFrame = new CompactedObjectSequence(translatedObjs);
            bufferedWriter.write(translatedFrame.toString());
            bufferedWriter.write("\n");
        }
        bufferedWriter.close();
        fileWriter.close();
    }

    public static void main(String[] args) throws IOException{

        moreObject("./datagen/visualroad1.txt", 1);
        moreObject("./datagen/visualroad1.txt", 2);
        moreObject("./datagen/visualroad1.txt", 3);
        moreObject("./datagen/visualroad1.txt", 4);
        moreObject("./datagen/visualroad1.txt", 5);


        moreObject("./datagen/visualroad2.txt", 1);
        moreObject("./datagen/visualroad2.txt", 2);
        moreObject("./datagen/visualroad2.txt", 3);
        moreObject("./datagen/visualroad2.txt", 4);
        moreObject("./datagen/visualroad2.txt", 5);

        moreObject("./datagen/visualroad3.txt", 1);
        moreObject("./datagen/visualroad3.txt", 2);
        moreObject("./datagen/visualroad3.txt", 3);
        moreObject("./datagen/visualroad3.txt", 4);
        moreObject("./datagen/visualroad3.txt", 5);

        moreObject("./datagen/visualroad4.txt", 1);
        moreObject("./datagen/visualroad4.txt", 2);
        moreObject("./datagen/visualroad4.txt", 3);
        moreObject("./datagen/visualroad4.txt", 4);
        moreObject("./datagen/visualroad4.txt", 5);
    }
}
