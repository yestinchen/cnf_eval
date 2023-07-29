package com.ytchen.beindexing.exp.graphs.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ytchen.beindexing.exp.utils.StringUtils.strip;

public class SequenceReader {

    public static FrameSequence<ObjectSequence> readFramesFromFile(String file) throws IOException {
        return readFromFile(file, x -> readObjectSequenceFromString(x));
    }

    public static FrameSequence<CompactedObjectSequence> readCompactedFramesFromFile(String file) throws IOException {
        return readFromFile(file, x -> readCompactedObjectSequenceFromString(x));
    }

    static <R extends Sortable> FrameSequence<R> readFromFile(String file, Function<String, R> function) throws IOException {
        List<R> objectSequence = new ArrayList<>();
        FileReader fileReader = new FileReader(new File(file));
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        bufferedReader.lines().forEach(i -> objectSequence.add(function.apply(i)));
        bufferedReader.close();
        fileReader.close();
        FrameSequence sequence = new FrameSequence(objectSequence);
        return sequence;
    }


    public static CompactedObjectSequence readCompactedObjectSequenceFromString(String str) {
        String fragments[] = str.split("[;]");
        List<CompactedObj> objList = new ArrayList<>();
        for (String fragment: fragments) {
            String[] pair = fragment.split("[:]");
            if (pair.length != 2) break;
            String clazz = pair[0].trim();
            String idStr = strip(pair[1]).trim();

            List<String> ids = idStr.equals("") ? new ArrayList<>():
                    Arrays.stream(strip(pair[1].trim()).split("[,]")).map(String::trim).collect(Collectors.toList());
//            System.out.println("ids:"+ids);
            if (ids.size() > 0) {
                objList.add(new CompactedObj(new ArrayList<>(new HashSet<>(ids)), clazz));
            }
        }
//        System.out.println("obj:"+objList);
        return objList.size() == 0 ? null : new CompactedObjectSequence(objList);
    }

    public static ObjectSequence readObjectSequenceFromString(String str) {
        String fragments[] = str.split("[;]");
        List<NormalObj> objList = new ArrayList<>();
        for (String fragment: fragments) {
            String[] pair = fragment.split("[:]");
            if (pair.length != 2) break;
            String clazz = pair[0].trim();
            objList.addAll(Arrays.stream(strip(pair[1]).split("[,]")).map(i ->
                    new NormalObj(i.trim(), clazz)).collect(Collectors.toList()));
        }
        return objList.size() == 0? null : new ObjectSequence(objList);
    }
}
