package com.ytchen.beindexing.exp.generator;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObj;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * fixed num class & average objects.
 */
public class SimpleFixedObjectSequenceGenerator {

    // number of classes.
    int numClass;

    // average duration of an object.
    int averageDuration;

    int averageDurationSTD;

    // average number of objects in a frame.
    int averageObjects;

    float randomPercentage;

    Map<String, List<GeneratedId>> currentObjectMap;
    int currentId = 0;

    public SimpleFixedObjectSequenceGenerator(int numClass, int averageObjects, int averageDuration,
                                              int averageDurationSTD) {
        this.numClass = numClass;
        this.averageDuration = averageDuration;
        this.averageDurationSTD = averageDurationSTD;
        this.averageObjects = averageObjects;
        this.randomPercentage = randomPercentage;
    }

    public int getNumClass() {
        return numClass;
    }

    public void setNumClass(int numClass) {
        this.numClass = numClass;
    }

    public int getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(int averageDuration) {
        this.averageDuration = averageDuration;
    }

    public int getAverageObjects() {
        return averageObjects;
    }

    public void setAverageObjects(int averageObjects) {
        this.averageObjects = averageObjects;
    }

    public void generate(int frameNum, Callback callback) throws Exception {
        currentObjectMap = new HashMap<>();
        // generators
        Random durationGenerator = new Random(),
                objectNumGenerator = new Random(),
                numClassGenerator = new Random();

        for (int i =0; i < frameNum; i++) {
            int currentClassNum =  numClass;
            int currentObjectNum = averageObjects;
            int eachClass = currentObjectNum / currentClassNum;

            // current keys.
            List<String> currentClasses = new ArrayList<>(currentObjectMap.keySet());

            List<CompactedObj> objList =new ArrayList<>();
            for (int j = 0; j < currentClassNum; j++) {
                List<String> newIds = new ArrayList<>();
                String currentClass = (char)('a'+j)+"";
                if (j < currentClasses.size()) {
                    List<GeneratedId> generatedIds = currentObjectMap.get(currentClasses.get(j));
                    int existingIds = Math.min(generatedIds.size(), eachClass);
                    for (int k = existingIds -1 ; k >=0; k--) {
                        GeneratedId id = generatedIds.get(k);
                        newIds.add(id.id);
                        id.count++;
                        if (id.count >= id.ttl) {
                            generatedIds.remove(k);
                        }
                    }
                    if (existingIds < eachClass) {
                        // add the rest.
                        for (int k = 0; k < eachClass - existingIds; k++) {
                            GeneratedId newId = nextId(durationGenerator);
                            generatedIds.add(newId);
                            newIds.add(newId.id);
                        }
                    }
                } else {
                    // generate all new classes.
                    List<GeneratedId> generatedIds = new ArrayList<>();
                    for (int k = 0; k < eachClass; k++) {
                        GeneratedId newId = nextId(durationGenerator);
                        generatedIds.add(newId);
                        newIds.add(newId.id);
                    }
                    // put to map.
                    currentObjectMap.put(currentClass, generatedIds);
                }
                CompactedObj obj = new CompactedObj(newIds, currentClass);
                objList.add(obj);
            }
            // remove other classes.
            if (currentClassNum < currentClasses.size()) {
                // remove rest.
                for (int j = currentClassNum; j < currentClasses.size(); j++) {
                    currentObjectMap.remove((char)('a'+j)+"");
                }
            }
            // notify user.
            callback.generated(new CompactedObjectSequence(objList));
        }
    }

    private GeneratedId nextId(Random durationGenerator) {
        GeneratedId newId = new GeneratedId();
        newId.setCount(1);
        int duration = (int) (durationGenerator.nextGaussian()  * averageDurationSTD+ averageDuration);
        int id = ++currentId;
        newId.setId("id"+id);
        newId.setTtl(duration);
        return newId;
    }

    class GeneratedId {
        int ttl;
        String id;
        int count;

        public int getTtl() {
            return ttl;
        }

        public void setTtl(int ttl) {
            this.ttl = ttl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static interface Callback{
        void generated(CompactedObjectSequence obj) throws Exception;
    }

    public static void generateFile(int numClass, int averageObjects,
                                    int averageDuration, int averageDurationSTD, int recordNum, boolean override) throws Exception {
        String fileName = "./datagen/fixed-"+numClass+"-"+averageObjects+"-"+averageDuration+"-"+averageDurationSTD+"-"+recordNum+".sequence";
        if (!override) {
            // check if file exists.
            if (new File(fileName).isFile()) {
                System.err.println("file ["+fileName+"] already exists, will not generate new file.");
                return;
            }
        }
        SimpleFixedObjectSequenceGenerator generator = new SimpleFixedObjectSequenceGenerator(numClass,
                averageObjects, averageDuration, averageDurationSTD);
        FileWriter fileWriter = new FileWriter(new File(fileName));
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        generator.generate(recordNum, obj -> {
            obj.sort();
            bufferedWriter.write(obj.toString());
            bufferedWriter.write("\n");
        });
        bufferedWriter.close();
        fileWriter.close();
    }

    public static void main(String[] args) throws Exception {
        generateFile(10, 200, 500, 200, 3000, false);
        generateFile(5, 200, 500, 200, 3000, false);
        generateFile(1, 200, 500, 200, 3000, false);
        generateFile(10, 100, 500, 200, 3000, false);
        generateFile(5, 100, 500, 200, 3000, false);
        generateFile(1, 100, 500, 200, 3000, false);
        generateFile(10, 50, 500, 200, 3000, false);
        generateFile(5, 50, 500, 200, 3000, false);
        generateFile(1, 50, 500, 200, 3000, false);
        generateFile(10, 10, 500, 200, 3000, false);
        generateFile(5, 10, 500, 200, 3000, false);
        generateFile(1, 10, 500, 200, 3000, false);

        generateFile(3, 10, 500, 200, 3000, false);
    }
}
