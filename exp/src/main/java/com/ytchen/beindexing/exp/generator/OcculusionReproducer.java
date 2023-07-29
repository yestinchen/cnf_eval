package com.ytchen.beindexing.exp.generator;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObj;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import com.ytchen.beindexing.exp.graphs.obj.FrameSequence;
import com.ytchen.beindexing.exp.graphs.obj.SequenceReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class OcculusionReproducer {


    public static void reproduce(String file, int times) throws IOException {
        FileWriter fileWriter = new FileWriter(file.replace(".txt", "-occ-"+times+".txt"));
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        FrameSequence<CompactedObjectSequence> frames = SequenceReader.readCompactedFramesFromFile(file);
        HashMap<String, List<String>> existingObjects = new HashMap<>();
        HashMap<String, String> mappingObjects = new HashMap<>();
        HashMap<String, LinkedHashMap<String, Integer>> vanishedList = new HashMap<>();
        for (CompactedObjectSequence frame: frames) {
            if (frame == null) {
                bufferedWriter.write("\n");

                continue;
            }
            List<CompactedObj> translatedObjs = new ArrayList<>();
            for (CompactedObj objs : frame.getSequence()){
                Set<String> thisRoundReused = new HashSet<>();
                List<String> classObjects = existingObjects.get(objs.getClazz());
                if (classObjects == null) {
                    classObjects = new ArrayList<>();
                    existingObjects.put(objs.getClazz(), classObjects);
                    vanishedList.put(objs.getClazz(), new LinkedHashMap<>());
                }
                List<String> currObjects = new ArrayList<>();
                for (String obj : objs.getIds()) {
                    currObjects.add(mappingObjects.getOrDefault(obj, obj));
                }
                // ---
                List<String> newOnes = new ArrayList<>();
                List<String> aliveOnes = new ArrayList<>();
                for (String id : currObjects) {
                    if (!classObjects.contains(id)) {
                        newOnes.add(id);
                    } else {
                        aliveOnes.add(id);
                    }
                }
                LinkedHashMap<String, Integer> thisTimeVanishedMap = new LinkedHashMap<>();
                // -get vanished ones.
                for (String lastOne: classObjects) {
                    if (!aliveOnes.contains(lastOne)) {
                        thisTimeVanishedMap.put(lastOne, 0);
                    }
                }
                // transform new ones.
                List<String> convertedNewOnes = new ArrayList<>();
                for (String newOne: newOnes) {
                    //
                    if (vanishedList.get(objs.getClazz()).size() != 0) {
                        // add one.
                        String oldId = null;
                        for (String k : vanishedList.get(objs.getClazz()).keySet()) {
                            if (vanishedList.get(objs.getClazz()).get(k) < times && !thisRoundReused.contains(k)) {
                                oldId = k;
                                break;
                            }
                        }
                        if (oldId != null) {
                            thisRoundReused.add(oldId);
                            int currCount = vanishedList.get(objs.getClazz()).get(oldId);
//                            if (currCount + 1 >= times) {
                                // never remove it.
//                            vanishedList.get(objs.getClazz()).remove(oldId);
//                        } else {
                                vanishedList.get(objs.getClazz()).put(oldId, currCount + 1);
//                            }
                            System.out.println("vanished:" + oldId + ", count:" + currCount);
                            // add a mapping.
                            mappingObjects.put(newOne, oldId);
                            convertedNewOnes.add(oldId);
                        } else {
                            // no mapping.
                            convertedNewOnes.add(newOne);

                        }
                    } else {
                        // no mapping.
                        convertedNewOnes.add(newOne);
                    }
                }
                // add new ones to existing.
                existingObjects.get(objs.getClazz()).addAll(convertedNewOnes);

                // convert.
                List<String> finalOutput = new ArrayList<>();
                finalOutput.addAll(aliveOnes);
                finalOutput.addAll(convertedNewOnes);
                Collections.sort(finalOutput);

                // add thisTimeVanished.

                LinkedHashMap<String, Integer> linkedHashMap = vanishedList.get(objs.getClazz());
                for (String k : thisTimeVanishedMap.keySet()) {
                    int count = linkedHashMap.getOrDefault(k, 0);
                    linkedHashMap.put(k, count);
                }

                finalOutput = new ArrayList<>(new HashSet<>(finalOutput));
                translatedObjs.add(new CompactedObj(finalOutput, objs.getClazz()));
            }

            CompactedObjectSequence translatedFrame = new CompactedObjectSequence(translatedObjs);
            bufferedWriter.write(translatedFrame.toString());
            bufferedWriter.write("\n");
        }
        bufferedWriter.close();
        fileWriter.close();
    }

    public static void main(String[] args) throws IOException {
        reproduce("./datagen/sherbrooke_frames.txt", 1);
        reproduce("./datagen/sherbrooke_frames.txt", 2);
        reproduce("./datagen/sherbrooke_frames.txt", 3);
        reproduce("./datagen/sherbrooke_frames.txt", 4);
        reproduce("./datagen/sherbrooke_frames.txt", 5);
        reproduce("./datagen/sherbrooke_frames.txt", 6);
        reproduce("./datagen/sherbrooke_frames.txt", 7);
        reproduce("./datagen/sherbrooke_frames.txt", 8);
        reproduce("./datagen/sherbrooke_frames.txt", 9);
        reproduce("./datagen/sherbrooke_frames.txt", 10);
        reproduce("./datagen/sherbrooke_frames.txt", 11);
        reproduce("./datagen/sherbrooke_frames.txt", 12);
//
        reproduce("./datagen/rouen_frames.txt", 1);
        reproduce("./datagen/rouen_frames.txt", 2);
        reproduce("./datagen/rouen_frames.txt", 3);
        reproduce("./datagen/rouen_frames.txt", 4);
        reproduce("./datagen/rouen_frames.txt", 5);
        reproduce("./datagen/rouen_frames.txt", 6);
        reproduce("./datagen/rouen_frames.txt", 7);
        reproduce("./datagen/rouen_frames.txt", 8);
        reproduce("./datagen/rouen_frames.txt", 9);
        reproduce("./datagen/rouen_frames.txt", 10);
        reproduce("./datagen/rouen_frames.txt", 11);
        reproduce("./datagen/rouen_frames.txt", 12);
//
////
        reproduce("./datagen/stmarc_frames.txt", 1);
        reproduce("./datagen/stmarc_frames.txt", 2);
        reproduce("./datagen/stmarc_frames.txt", 3);
        reproduce("./datagen/stmarc_frames.txt", 4);
        reproduce("./datagen/stmarc_frames.txt", 5);
        reproduce("./datagen/stmarc_frames.txt", 6);
        reproduce("./datagen/stmarc_frames.txt", 7);
        reproduce("./datagen/stmarc_frames.txt", 8);
        reproduce("./datagen/stmarc_frames.txt", 9);
        reproduce("./datagen/stmarc_frames.txt", 10);
        reproduce("./datagen/stmarc_frames.txt", 11);
        reproduce("./datagen/stmarc_frames.txt", 12);
//
//
        reproduce("./datagen/atrium_frames.txt", 1);
        reproduce("./datagen/atrium_frames.txt", 2);
        reproduce("./datagen/atrium_frames.txt", 3);
        reproduce("./datagen/atrium_frames.txt", 4);
        reproduce("./datagen/atrium_frames.txt", 5);
        reproduce("./datagen/atrium_frames.txt", 6);
        reproduce("./datagen/atrium_frames.txt", 7);
        reproduce("./datagen/atrium_frames.txt", 8);
        reproduce("./datagen/atrium_frames.txt", 9);
        reproduce("./datagen/atrium_frames.txt", 10);
        reproduce("./datagen/atrium_frames.txt", 11);
        reproduce("./datagen/atrium_frames.txt", 12);

        reproduce("./datagen/visualroad1.txt", 1);
        reproduce("./datagen/visualroad1.txt", 2);
        reproduce("./datagen/visualroad1.txt", 3);
        reproduce("./datagen/visualroad1.txt", 4);
        reproduce("./datagen/visualroad1.txt", 5);
        reproduce("./datagen/visualroad1.txt", 6);
        reproduce("./datagen/visualroad1.txt", 7);
        reproduce("./datagen/visualroad1.txt", 8);
        reproduce("./datagen/visualroad1.txt", 9);
        reproduce("./datagen/visualroad1.txt", 10);
        reproduce("./datagen/visualroad1.txt", 11);
        reproduce("./datagen/visualroad1.txt", 12);
//
        reproduce("./datagen/visualroad2.txt", 1);
        reproduce("./datagen/visualroad2.txt", 2);
        reproduce("./datagen/visualroad2.txt", 3);
        reproduce("./datagen/visualroad2.txt", 4);
        reproduce("./datagen/visualroad2.txt", 5);
        reproduce("./datagen/visualroad2.txt", 6);
        reproduce("./datagen/visualroad2.txt", 7);
        reproduce("./datagen/visualroad2.txt", 8);
        reproduce("./datagen/visualroad2.txt", 9);
        reproduce("./datagen/visualroad2.txt", 10);
        reproduce("./datagen/visualroad2.txt", 11);
        reproduce("./datagen/visualroad2.txt", 12);
//
        reproduce("./datagen/visualroad3.txt", 1);
        reproduce("./datagen/visualroad3.txt", 2);
        reproduce("./datagen/visualroad3.txt", 3);
        reproduce("./datagen/visualroad3.txt", 4);
        reproduce("./datagen/visualroad3.txt", 5);
        reproduce("./datagen/visualroad3.txt", 6);
        reproduce("./datagen/visualroad3.txt", 7);
        reproduce("./datagen/visualroad3.txt", 8);
        reproduce("./datagen/visualroad3.txt", 9);
        reproduce("./datagen/visualroad3.txt", 10);
        reproduce("./datagen/visualroad3.txt", 11);
        reproduce("./datagen/visualroad3.txt", 12);
//
        reproduce("./datagen/visualroad4.txt", 1);
        reproduce("./datagen/visualroad4.txt", 2);
        reproduce("./datagen/visualroad4.txt", 3);
        reproduce("./datagen/visualroad4.txt", 4);
        reproduce("./datagen/visualroad4.txt", 5);
        reproduce("./datagen/visualroad4.txt", 6);
        reproduce("./datagen/visualroad4.txt", 7);
        reproduce("./datagen/visualroad4.txt", 8);
        reproduce("./datagen/visualroad4.txt", 9);
        reproduce("./datagen/visualroad4.txt", 10);
        reproduce("./datagen/visualroad4.txt", 11);
        reproduce("./datagen/visualroad4.txt", 12);

        reproduce("./datagen/rene_frames.txt", 1);
        reproduce("./datagen/rene_frames.txt", 2);
        reproduce("./datagen/rene_frames.txt", 3);
        reproduce("./datagen/rene_frames.txt", 4);
        reproduce("./datagen/rene_frames.txt", 5);
        reproduce("./datagen/rene_frames.txt", 6);
        reproduce("./datagen/rene_frames.txt", 7);
        reproduce("./datagen/rene_frames.txt", 8);
        reproduce("./datagen/rene_frames.txt", 9);
        reproduce("./datagen/rene_frames.txt", 10);
        reproduce("./datagen/rene_frames.txt", 11);
        reproduce("./datagen/rene_frames.txt", 12);
    }
}
