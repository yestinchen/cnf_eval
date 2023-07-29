package com.ytchen.beindexing.exp.graphs.statistical;

import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.graphs.obj.*;

import java.io.IOException;
import java.util.*;

public class GeneralStatistics {

    public static Map<String, Double> calAverageClassNum(String file, List<String> classes) throws IOException {
        FrameSequence<CompactedObjectSequence> frames = SequenceReader.readCompactedFramesFromFile(file);

        for (CompactedObjectSequence seq : frames) {
            if (seq == null || seq.getSequence() == null) continue;
            for (CompactedObj obj : seq.getSequence()) {
                obj.setIds(new ArrayList<>(new HashSet<>(obj.getIds())));
            }
        }
        Map<String, Long> totalClasses = new HashMap<>();
        for (CompactedObjectSequence frame: frames) {
            if (frame == null) continue;
            List<Tuple2<String, Integer>> ccs =  frame.genAssignment();
            for (Tuple2<String, Integer> t : ccs) {
                if (classes.contains(t.get_1())) {
                    long c = totalClasses.getOrDefault(t.get_1(), 0L);
                    c += t.get_2();
                    totalClasses.put(t.get_1(), c);
                }
            }
        }
        Map<String, Double> result = new HashMap<>();
        for (String k :totalClasses.keySet()) {
            result.put(k, totalClasses.get(k)*1.0/frames.getSequence().size());
        }
        return result;
    }

    public static void printGeneralInfo(String file, List<String> classes) throws IOException {
        Map<String, Double> map = calAverageClassNum(file, classes);
        List<Tuple2<String, Double>> displayList = new ArrayList<>();
        for (String k : map.keySet()) {
            if (map.get(k) < 0.1) continue;
            displayList.add(new Tuple2<>(k, map.get(k)));
        }
        Collections.sort(displayList, (x1, x2) -> -Double.compare(x1.get_2(), x2.get_2()));
        StringBuilder sb = new StringBuilder();
        double v = 0;
        for (Tuple2<String, Double> t : displayList) {
//            System.out.println(t.get_1() + ":" + String.format("%.2f", t.get_2()));
            sb.append(t.get_1() + ":" + String.format("%.2f", t.get_2())).append(", ");
            v += t.get_2();
        }
        sb.append("overall: ").append(String.format("%.2f", v));
        System.out.println("{"+sb+"}");
    }

    public static void main(String[] args) throws IOException {

        List<String> classes = Arrays.asList("person", "car", "truck", "bus");

//        Map<String, Double> map = calAverageClassNum("./datagen/atrium_frames.txt");
//        Map<String, Double> map = calAverageClassNum("./datagen/rouen_frames.txt");
//        Map<String, Double> map = calAverageClassNum("./datagen/stmarc_frames.txt", classes);
        Map<String, Double> map = calAverageClassNum("./datagen/new/MOT16-01.txt", classes);
        List<Tuple2<String, Double>> displayList = new ArrayList<>();
        for (String k : map.keySet()) {
            if (map.get(k) < 0.1) continue;
            displayList.add(new Tuple2<>(k, map.get(k)));
        }
        Collections.sort(displayList, (x1, x2) -> -Double.compare(x1.get_2(), x2.get_2()));
        StringBuilder sb = new StringBuilder();
        double v = 0;
        for (Tuple2<String, Double> t : displayList) {
//            System.out.println(t.get_1() + ":" + String.format("%.2f", t.get_2()));
            sb.append(t.get_1() + ":" + String.format("%.2f", t.get_2())).append(", ");
            v += t.get_2();
        }
        sb.append("overall: ").append(String.format("%.2f", v));
        System.out.println("{"+sb+"}");
    }
}
