package com.ytchen.beindexing.exp.generator;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObj;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import com.ytchen.beindexing.exp.graphs.obj.FrameSequence;
import com.ytchen.beindexing.exp.graphs.obj.SequenceReader;
import com.ytchen.beindexing.exp.utils.StringUtils;

import java.io.*;
import java.util.*;

public class OcclusionReproducerV3 {

    public static void produceInfo(String file) throws IOException {
        FileWriter fileWriter = new FileWriter(file.replace(".txt", ".info.txt"));
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        FrameSequence<CompactedObjectSequence> frames = SequenceReader.readCompactedFramesFromFile(file);
        HashMap<String, Map<String, List<Integer>>> classObjectFrames = new HashMap<>();
        int i =0;
        for (CompactedObjectSequence frame: frames) {
            if (frame == null) {
                i++;
                continue;
            }
            for (CompactedObj obj : frame.getSequence()) {
                Map<String, List<Integer>> objMap = classObjectFrames.get(obj.getClazz());
                if (objMap == null) {
                    objMap = new LinkedHashMap<>();
                    classObjectFrames.put(obj.getClazz(), objMap);
                }
                for (String id : obj.getIds()) {
                    List<Integer> list = objMap.get(id);
                    if (list == null) {
                        list = new ArrayList<>();
                        objMap.put(id, list);
                    }
                    list.add(i);
                }
            }
            i ++;
        }
        for (String c:classObjectFrames.keySet()) {
            for (String id : classObjectFrames.get(c).keySet()) {
                bufferedWriter.write(c);
                bufferedWriter.write(";");
                bufferedWriter.write(id);
                bufferedWriter.write(";");
                bufferedWriter.write(StringUtils.join(classObjectFrames.get(c).get(id), ","));
                bufferedWriter.write("\n");
            }
        }
        bufferedWriter.close();
        fileWriter.close();
    }

    public static Map<String, Map<String, List<Integer>>> readInfo(String file) throws IOException {
        String infoFile = file.replace(".txt", ".info.txt");
        if (!new File(infoFile).exists()) {
            produceInfo(file);
        }
        FileReader fileReader = new FileReader(infoFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line = null;

        Map<String, Map<String, List<Integer>>> readMap = new HashMap<>();

        while( (line = bufferedReader.readLine()) != null) {
            String[] arr = line.split(";");
            String clazz = arr[0].trim();
            String id = arr[1].trim();
            List<Integer> frames = new ArrayList<>();
            for (String i : Arrays.asList(arr[2].trim().split(","))) {
                frames.add(Integer.valueOf(i.trim()));
            }
            Map<String, List<Integer>> objMap = readMap.get(clazz);
            if (objMap == null) {
                objMap = new LinkedHashMap<>();
                readMap.put(clazz, objMap);
            }
            objMap.put(id, frames);
        }

        bufferedReader.close();
        fileReader.close();
        return readMap;
    }

    public static boolean isOverlap(Set<Integer> i1, List<Integer> i2) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i: i1) {
            map.put(i, 1);
        }
        for (int i : i2) {
            map.put(i, map.getOrDefault(i, 0)+1);
        }
        boolean overlap = false;
        for (int i : map.values()) {
            if (i == 2) {
                overlap = true;
                break;
            }
        }
        return overlap;
    }

    public static void reproduce(String file, int times) throws IOException {
        file = file.replaceAll(".txt","-filtered-4.txt");
        System.out.println("processing:"+file+"; occ:"+ times);
        Map<String, Map<String, List<Integer>>> objOccMap = readInfo(file);
        Random random = new Random();
        for (String clazz: objOccMap.keySet()) {
            for (String id : objOccMap.get(clazz).keySet()) {
                // handle frames.
                List<Integer> frames = objOccMap.get(clazz).get(id);
                if (frames.size() > times * 3) {
                    // remove frames.
                    // try 10 times.
                    for (int i=0; i < times; i++) {
                        for(int j = 0; j < 10000; j++) {
                            int rmFrame = random.nextInt(frames.size());
                            if (rmFrame > 0 && rmFrame < frames.size() -1 &&
                                    frames.get(rmFrame - 1) + 1 == frames.get(rmFrame)
                                    && frames.get(rmFrame + 1) - 1 == frames.get(rmFrame)) {
                                // remove it.
                                frames.remove(rmFrame);
                                break;
                            }
                        }
                    }
                }
            }
        }

        // restructure.

        // output.

        FileWriter fileWriter = new FileWriter(file.replace(".txt", "-occv3-"+times+".txt"));
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
                    if (!objOccMap.get(objs.getClazz()).get(id).contains(frameNum)) {
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
//        reproduce("./datagen/sherbrooke_frames.txt", 1);
//        reproduce("./datagen/sherbrooke_frames.txt", 2);
//        reproduce("./datagen/sherbrooke_frames.txt", 3);
//        reproduce("./datagen/sherbrooke_frames.txt", 4);
//        reproduce("./datagen/sherbrooke_frames.txt", 5);
//        reproduce("./datagen/sherbrooke_frames.txt", 6);
//        reproduce("./datagen/sherbrooke_frames.txt", 7);
//        reproduce("./datagen/sherbrooke_frames.txt", 8);
//        reproduce("./datagen/sherbrooke_frames.txt", 9);
//        reproduce("./datagen/sherbrooke_frames.txt", 10);
//        reproduce("./datagen/sherbrooke_frames.txt", 11);
//        reproduce("./datagen/sherbrooke_frames.txt", 12);
////
//        reproduce("./datagen/rouen_frames.txt", 1);
//        reproduce("./datagen/rouen_frames.txt", 2);
//        reproduce("./datagen/rouen_frames.txt", 3);
//        reproduce("./datagen/rouen_frames.txt", 4);
//        reproduce("./datagen/rouen_frames.txt", 5);
//        reproduce("./datagen/rouen_frames.txt", 6);
//        reproduce("./datagen/rouen_frames.txt", 7);
//        reproduce("./datagen/rouen_frames.txt", 8);
//        reproduce("./datagen/rouen_frames.txt", 9);
//        reproduce("./datagen/rouen_frames.txt", 10);
//        reproduce("./datagen/rouen_frames.txt", 11);
//        reproduce("./datagen/rouen_frames.txt", 12);
////
//////
//        reproduce("./datagen/stmarc_frames.txt", 1);
//        reproduce("./datagen/stmarc_frames.txt", 2);
//        reproduce("./datagen/stmarc_frames.txt", 3);
//        reproduce("./datagen/stmarc_frames.txt", 4);
//        reproduce("./datagen/stmarc_frames.txt", 5);
//        reproduce("./datagen/stmarc_frames.txt", 6);
//        reproduce("./datagen/stmarc_frames.txt", 7);
//        reproduce("./datagen/stmarc_frames.txt", 8);
//        reproduce("./datagen/stmarc_frames.txt", 9);
//        reproduce("./datagen/stmarc_frames.txt", 10);
//        reproduce("./datagen/stmarc_frames.txt", 11);
//        reproduce("./datagen/stmarc_frames.txt", 12);
////
////
//        reproduce("./datagen/atrium_frames.txt", 1);
//        reproduce("./datagen/atrium_frames.txt", 2);
//        reproduce("./datagen/atrium_frames.txt", 3);
//        reproduce("./datagen/atrium_frames.txt", 4);
//        reproduce("./datagen/atrium_frames.txt", 5);
//        reproduce("./datagen/atrium_frames.txt", 6);
//        reproduce("./datagen/atrium_frames.txt", 7);
//        reproduce("./datagen/atrium_frames.txt", 8);
//        reproduce("./datagen/atrium_frames.txt", 9);
//        reproduce("./datagen/atrium_frames.txt", 10);
//        reproduce("./datagen/atrium_frames.txt", 11);
//        reproduce("./datagen/atrium_frames.txt", 12);
//
//        reproduce("./datagen/visualroad1.txt", 1);
//        reproduce("./datagen/visualroad1.txt", 2);
//        reproduce("./datagen/visualroad1.txt", 3);
//        reproduce("./datagen/visualroad1.txt", 4);
//        reproduce("./datagen/visualroad1.txt", 5);
//        reproduce("./datagen/visualroad1.txt", 6);
//        reproduce("./datagen/visualroad1.txt", 7);
//        reproduce("./datagen/visualroad1.txt", 8);
//        reproduce("./datagen/visualroad1.txt", 9);
//        reproduce("./datagen/visualroad1.txt", 10);
//        reproduce("./datagen/visualroad1.txt", 11);
//        reproduce("./datagen/visualroad1.txt", 12);
////
//        reproduce("./datagen/visualroad2.txt", 1);
//        reproduce("./datagen/visualroad2.txt", 2);
//        reproduce("./datagen/visualroad2.txt", 3);
//        reproduce("./datagen/visualroad2.txt", 4);
//        reproduce("./datagen/visualroad2.txt", 5);
//        reproduce("./datagen/visualroad2.txt", 6);
//        reproduce("./datagen/visualroad2.txt", 7);
//        reproduce("./datagen/visualroad2.txt", 8);
//        reproduce("./datagen/visualroad2.txt", 9);
//        reproduce("./datagen/visualroad2.txt", 10);
//        reproduce("./datagen/visualroad2.txt", 11);
//        reproduce("./datagen/visualroad2.txt", 12);
////
//        reproduce("./datagen/visualroad3.txt", 1);
//        reproduce("./datagen/visualroad3.txt", 2);
//        reproduce("./datagen/visualroad3.txt", 3);
//        reproduce("./datagen/visualroad3.txt", 4);
//        reproduce("./datagen/visualroad3.txt", 5);
//        reproduce("./datagen/visualroad3.txt", 6);
//        reproduce("./datagen/visualroad3.txt", 7);
//        reproduce("./datagen/visualroad3.txt", 8);
//        reproduce("./datagen/visualroad3.txt", 9);
//        reproduce("./datagen/visualroad3.txt", 10);
//        reproduce("./datagen/visualroad3.txt", 11);
//        reproduce("./datagen/visualroad3.txt", 12);
////
//        reproduce("./datagen/visualroad4.txt", 1);
//        reproduce("./datagen/visualroad4.txt", 2);
//        reproduce("./datagen/visualroad4.txt", 3);
//        reproduce("./datagen/visualroad4.txt", 4);
//        reproduce("./datagen/visualroad4.txt", 5);
//        reproduce("./datagen/visualroad4.txt", 6);
//        reproduce("./datagen/visualroad4.txt", 7);
//        reproduce("./datagen/visualroad4.txt", 8);
//        reproduce("./datagen/visualroad4.txt", 9);
//        reproduce("./datagen/visualroad4.txt", 10);
//        reproduce("./datagen/visualroad4.txt", 11);
//        reproduce("./datagen/visualroad4.txt", 12);
//
//        reproduce("./datagen/rene_frames.txt", 1);
//        reproduce("./datagen/rene_frames.txt", 2);
//        reproduce("./datagen/rene_frames.txt", 3);
//        reproduce("./datagen/rene_frames.txt", 4);
//        reproduce("./datagen/rene_frames.txt", 5);
//        reproduce("./datagen/rene_frames.txt", 6);
//        reproduce("./datagen/rene_frames.txt", 7);
//        reproduce("./datagen/rene_frames.txt", 8);
//        reproduce("./datagen/rene_frames.txt", 9);
//        reproduce("./datagen/rene_frames.txt", 10);
//        reproduce("./datagen/rene_frames.txt", 11);
//        reproduce("./datagen/rene_frames.txt", 12);

//        reproduce("./datagen/MOT16-04.txt", 1);
//        reproduce("./datagen/MOT16-04.txt", 2);
//        reproduce("./datagen/MOT16-04.txt", 3);
//        reproduce("./datagen/MOT16-04.txt", 4);
//        reproduce("./datagen/MOT16-04.txt", 5);
//        reproduce("./datagen/MOT16-04.txt", 6);
//        reproduce("./datagen/MOT16-04.txt", 7);
//        reproduce("./datagen/MOT16-04.txt", 8);
//        reproduce("./datagen/MOT16-04.txt", 9);
//        reproduce("./datagen/MOT16-04.txt", 10);
//        reproduce("./datagen/MOT16-04.txt", 11);
//        reproduce("./datagen/MOT16-04.txt", 12);


//        reproduce("./datagen/MOT16-13.txt", 1);
//        reproduce("./datagen/MOT16-13.txt", 2);
//        reproduce("./datagen/MOT16-13.txt", 3);
//        reproduce("./datagen/MOT16-13.txt", 4);
//        reproduce("./datagen/MOT16-13.txt", 5);
//        reproduce("./datagen/MOT16-13.txt", 6);
//        reproduce("./datagen/MOT16-13.txt", 7);
//        reproduce("./datagen/MOT16-13.txt", 8);
//        reproduce("./datagen/MOT16-13.txt", 9);
//        reproduce("./datagen/MOT16-13.txt", 10);
//        reproduce("./datagen/MOT16-13.txt", 11);
//        reproduce("./datagen/MOT16-13.txt", 12);

//        reproduce("./datagen/new/visualroad1.txt", 1);
//        reproduce("./datagen/new/visualroad1.txt", 2);
//        reproduce("./datagen/new/visualroad1.txt", 3);
//        reproduce("./datagen/new/visualroad2.txt", 1);
//        reproduce("./datagen/new/visualroad2.txt", 2);
//        reproduce("./datagen/new/visualroad2.txt", 3);
//        reproduce("./datagen/new/visualroad3.txt", 1);
//        reproduce("./datagen/new/visualroad3.txt", 2);
//        reproduce("./datagen/new/visualroad3.txt", 3);
//
//        reproduce("./datagen/new/visualroad4.txt", 1);
//        reproduce("./datagen/new/visualroad4.txt", 2);
//        reproduce("./datagen/new/visualroad4.txt", 3);
//
//
//        reproduce("./datagen/new/MOT16-01.txt", 1);
//        reproduce("./datagen/new/MOT16-01.txt", 2);
//        reproduce("./datagen/new/MOT16-01.txt", 3);
//
//        reproduce("./datagen/new/MOT16-13.txt", 1);
//        reproduce("./datagen/new/MOT16-13.txt", 2);
//        reproduce("./datagen/new/MOT16-13.txt", 3);
//
//        reproduce("./datagen/new/stmarc.txt", 1);
//        reproduce("./datagen/new/stmarc.txt", 2);
//        reproduce("./datagen/new/stmarc.txt", 3);
//
//        reproduce("./datagen/new/sherbrooke.txt", 1);
//        reproduce("./datagen/new/sherbrooke.txt", 2);
//        reproduce("./datagen/new/sherbrooke.txt", 3);
//
//        reproduce("./datagen/new/MVI_40732.txt", 1);
//        reproduce("./datagen/new/MVI_40732.txt", 2);
//        reproduce("./datagen/new/MVI_40732.txt", 3);
//
//        reproduce("./datagen/new/MVI_40171.txt", 1);
//        reproduce("./datagen/new/MVI_40171.txt", 2);
//        reproduce("./datagen/new/MVI_40171.txt", 3);
//        reproduce("./datagen/new/MOT16-06.txt", 1);
//        reproduce("./datagen/new/MOT16-06.txt", 2);
//        reproduce("./datagen/new/MOT16-06.txt", 3);
//
//        reproduce("./datagen/new/MVI_40751.txt", 1);
//        reproduce("./datagen/new/MVI_40751.txt", 2);
//        reproduce("./datagen/new/MVI_40751.txt", 3);


        reproduce("./datagen/new2/MVI_40751.txt", 1);
        reproduce("./datagen/new2/MVI_40751.txt", 2);
        reproduce("./datagen/new2/MVI_40751.txt", 3);
        reproduce("./datagen/new2/MVI_40171.txt", 1);
        reproduce("./datagen/new2/MVI_40171.txt", 2);
        reproduce("./datagen/new2/MVI_40171.txt", 3);
        reproduce("./datagen/new2/MOT16-06.txt", 1);
        reproduce("./datagen/new2/MOT16-06.txt", 2);
        reproduce("./datagen/new2/MOT16-06.txt", 3);
        reproduce("./datagen/new2/MOT16-13.txt", 1);
        reproduce("./datagen/new2/MOT16-13.txt", 2);
        reproduce("./datagen/new2/MOT16-13.txt", 3);
        reproduce("./datagen/new2/visualroad1.txt", 1);
        reproduce("./datagen/new2/visualroad1.txt", 2);
        reproduce("./datagen/new2/visualroad1.txt", 3);
        reproduce("./datagen/new2/visualroad2.txt", 1);
        reproduce("./datagen/new2/visualroad2.txt", 2);
        reproduce("./datagen/new2/visualroad2.txt", 3);
//        reproduce("./datagen/new2/visualroad3.txt", 1);
//        reproduce("./datagen/new2/visualroad3.txt", 2);
//        reproduce("./datagen/new2/visualroad3.txt", 3);
////
//        reproduce("./datagen/new2/visualroad4.txt", 1);
//        reproduce("./datagen/new2/visualroad4.txt", 2);
//        reproduce("./datagen/new2/visualroad4.txt", 3);

//        reproduce("./datagen/new2/MVI_40172.txt", 1);
//        reproduce("./datagen/new2/MVI_40172.txt", 2);
//        reproduce("./datagen/new2/MVI_40172.txt", 3);
//
//        reproduce("./datagen/new2/MVI_40732.txt", 1);
//        reproduce("./datagen/new2/MVI_40732.txt", 2);
//        reproduce("./datagen/new2/MVI_40732.txt", 3);
    }
}
