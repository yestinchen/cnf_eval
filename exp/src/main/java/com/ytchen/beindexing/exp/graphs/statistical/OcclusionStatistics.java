package com.ytchen.beindexing.exp.graphs.statistical;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObj;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import com.ytchen.beindexing.exp.graphs.obj.FrameSequence;
import com.ytchen.beindexing.exp.graphs.obj.SequenceReader;

import java.io.IOException;
import java.util.*;

public class OcclusionStatistics {

    public static Map<String, List<Integer>> getIdFrameMap(String file, List<String> classes) throws IOException {
        FrameSequence<CompactedObjectSequence> frames = SequenceReader.readCompactedFramesFromFile(file);

        for (CompactedObjectSequence seq : frames) {
            if (seq == null || seq.getSequence() == null) continue;
            for (CompactedObj obj : seq.getSequence()) {
                obj.setIds(new ArrayList<>(new HashSet<>(obj.getIds())));
            }
        }
        // id, frames
        Map<String, List<Integer>> idFrameMap = new HashMap<>();
        int f = 0;
        for (CompactedObjectSequence frame: frames) {
            f++;
            if (frame == null) continue;
            for (CompactedObj co: frame.getSequence()) {
                if (classes.contains(co.getClazz())) {
                    for (String id : co.getIds()) {
                        List<Integer> flist = idFrameMap.get(id);
                        if (flist == null) {
                            flist = new ArrayList<>();
                            idFrameMap.put(id, flist);
                        }
                        flist.add(f);
                    }
                }
            }
        }
        return idFrameMap;
    }

    static int computeOcc(List<Integer> frames) {
        int occ = 0;
        int f1 = frames.get(0);
        for (int i =1; i < frames.size(); i++) {
            if (f1 + 1 == frames.get(i)) {
                // nothing.
            } else {
                occ ++;
            }
            f1 = frames.get(i);
        }
        return occ;
    }

    public static void printStatistics(String file, List<String> classes) throws IOException {
        Map<String, List<Integer>> idFrameMap = getIdFrameMap(file, classes);
        // 1. compute occlusions.
        Map<String, Integer> occlusion = new HashMap<>();
        int total = 0;
        for (String id: idFrameMap.keySet()) {
            int occ = computeOcc(idFrameMap.get(id));
            occlusion.put(id, occ);
            total += occ;
        }
        System.out.println("average occ:" + total * 1.0 / occlusion.size());
        // 2. compute average appear length.

        int length = 0;
        for (String id : idFrameMap.keySet()) {
            length += idFrameMap.get(id).size();
        }
        System.out.println("average appear frame num:" + length * 1.0/ occlusion.size());



        System.out.println("unique objects: "+ idFrameMap.keySet().size());

    }

    public static void main(String[] args) throws IOException {
        List<String> classes = Arrays.asList("person", "car", "truck", "bus");
//        printStatistics("./datagen/new/visualroad2.txt", classes);
        printStatistics("./datagen/new/visualroad4.txt", classes);
//        printStatistics("./datagen/new/stmarc.txt", classes);
//        printStatistics("./datagen/new/MOT16-13.txt", classes);

//        printStatistics("./datagen/new/visualroad1.txt", classes);
//
//        printStatistics("./datagen/new/visualroad1-occ-1.txt", classes);
//        printStatistics("./datagen/new/visualroad1-occ-2.txt", classes);
//        printStatistics("./datagen/new/visualroad1-occ-3.txt", classes);

//        printStatistics("./datagen/new/sherbrooke.txt", classes);
    }
}
