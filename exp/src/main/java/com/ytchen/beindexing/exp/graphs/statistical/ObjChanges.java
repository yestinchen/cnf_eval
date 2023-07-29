package com.ytchen.beindexing.exp.graphs.statistical;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import com.ytchen.beindexing.exp.graphs.obj.FrameSequence;
import com.ytchen.beindexing.exp.graphs.obj.SequenceReader;

import java.io.IOException;
import java.util.*;

public class ObjChanges {

    public static void printObjChanges(String file, List<String> classes) throws IOException {
        FrameSequence<CompactedObjectSequence> frames = SequenceReader.readCompactedFramesFromFile(file);

        int totalObjChange = 0, last100Change = 0, inter100Change = 0, totalInter = 0;
        double changeRatio = 0, last100ChangeRatio = 0;
        CompactedObjectSequence lastSeq = null;
        int count = 0;
        for (CompactedObjectSequence seq : frames) {
            count ++;
            if (count % 100 == 0 && count != 0) {
                System.out.println("last 100:" + (last100Change *1.0 / 100));
                System.out.println("inter 100:" + inter100Change * 1.0 / 100);
                System.out.println("ratio 100: " + last100ChangeRatio / 100);
                last100Change = 0;
                inter100Change = 0;
                last100ChangeRatio =0;
            }
            // remove other objects.
            if (seq == null || seq.getSequence() == null) continue;
            for (int i =seq.getSequence().size()-1; i >=0; i--) {
                if (!classes.contains(seq.getSequence().get(i).getClazz())
                        || seq.getSequence().get(i).getIds().size() == 0) {
                    seq.getSequence().remove(i);
                }
            }
            if (lastSeq != null) {
                CompactedObjectSequence interResult = seq.intersect(lastSeq);
                CompactedObjectSequence unionResult = seq.union(lastSeq);
//                System.out.println("count:"+count);
//                System.out.println("lastSeq:" + lastSeq.getSequence());
//                System.out.println("seq:"+seq.getSequence());
//                System.out.println(interResult);
//                System.out.println(unionResult);
                inter100Change += (interResult == null ? 0 : interResult.size());
                totalInter += (interResult == null ? 0 : interResult.size());
                if (unionResult != null) {
                    int change = (unionResult.size() - (interResult == null ? 0 : interResult.size()));
                    totalObjChange += change;
                    last100Change += change;

                    changeRatio += (change * 1.0 / unionResult.size());
                    last100ChangeRatio += (change * 1.0 / unionResult.size());
                }
            }
            lastSeq = seq;
        }
        System.out.println("total changes: " + (totalObjChange * 1.0/ frames.getSequence().size()));
        System.out.println("total inter: " + (totalInter * 1.0 /frames.getSequence().size()));
        System.out.println("avg change ratio:" + changeRatio);
    }

    public static void printNewObjRatio(String file, List<String> classes) throws IOException {
        FrameSequence<CompactedObjectSequence> frames = SequenceReader.readCompactedFramesFromFile(file);

        Set<String> oldObjs = new HashSet<>();
        int newObjChange = 0, last100Change = 0;
        int lastTime = 0;
        CompactedObjectSequence lastSeq = null;
        int count = 0;
        for (CompactedObjectSequence seq : frames) {
            count ++;
            if (count % 100 == 0 && count != 0) {
                lastTime = oldObjs.size() - lastTime;
                System.out.println("last 100:" + last100Change);
                last100Change = 0;
            }
            // remove other objects.
            if (seq == null || seq.getSequence() == null) continue;
            for (int i =seq.getSequence().size()-1; i >=0; i--) {
                if (!classes.contains(seq.getSequence().get(i).getClazz())
                        || seq.getSequence().get(i).getIds().size() == 0) {
                    seq.getSequence().remove(i);
                } else {
                    // compute old objs.
                    for (String id : seq.getSequence().get(i).getIds()) {
                        if (!oldObjs.contains(id)) {
                            oldObjs.add(id);
                            newObjChange ++;
                            last100Change ++;
                        }
                    }
                }
            }
        }
        System.out.println("total changes: " + (newObjChange * 1.0/ oldObjs.size()));
        System.out.println("total objs: " + oldObjs.size());
    }

    public static void main(String[] args) throws IOException {
        List<String> classes = Arrays.asList("person", "car", "truck", "bus");
        printNewObjRatio("./datagen/new/visualroad1.txt", classes);
        printNewObjRatio("./datagen/new/visualroad2.txt", classes);
//        printObjChanges("./datagen/new/MOT16-13.txt", classes);
//        printObjChanges("./datagen/new/MOT16-06.txt", classes);
        System.out.println("171");
        printNewObjRatio("./datagen/new/MVI_40171.txt", classes);
        printObjChanges("./datagen/new/MVI_40171.txt", classes);
        System.out.println("751");
        printNewObjRatio("./datagen/new/MVI_40751.txt", classes);
        printObjChanges("./datagen/new/MVI_40751.txt", classes);
    }
}
