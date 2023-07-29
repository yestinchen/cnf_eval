package com.ytchen.beindexing.exp.graphs.statistical;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import com.ytchen.beindexing.exp.graphs.obj.SequenceReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CollectAll {
    public static void main(String[] args) throws IOException {
//        String file = "./datagen/new/MOT16-13.txt";
//        String file = "./datagen/new/MOT16-06.txt";
//        String file = "./datagen/new/MVI_40751.txt";
//        String file = "./datagen/new/visualroad1.txt";
//        String file = "./datagen/new/MOT16-13.txt";
//        String file = "./datagen/new/visualroad2xt2.txt";

//        String file = "./datagen/new2/visualroad2.txt";
//        String file = "./datagen/new2/visualroad4.txt";
        String file = "./datagen/new2/MOT16-13.txt";
//        String file = "./datagen/new2/MVI_40171.txt";
//        String file = "./datagen/new2/MVI_40751.txt";
//        String file = "./datagen/new2/visualroad5xt0.txt";

        List<String> classes = Arrays.asList("person", "car", "truck", "bus");

        System.out.println("total frames: " + SequenceReader.readCompactedFramesFromFile(file).getSequence().size());

        GeneralStatistics.printGeneralInfo(file, classes);

        OcclusionStatistics.printStatistics(file, classes);
    }
}
