package com.ytchen.beindexing.exp.graphs.builder;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class NaiveBuilder implements IStateBuilder {
    private Logger LOG = LogManager.getLogger(NaiveBuilder.class);

    int duration;

    int maxCount;

    Map<CompactedObjectSequence, List<Integer>> stateMap;
    List<CompactedObjectSequence> originFrames = new ArrayList<>();

    int time = 1;

    public NaiveBuilder(int duration, int maxCount) {
        this.duration = duration;
        this.maxCount = maxCount;
    }

    public List<Integer> get(CompactedObjectSequence sequence) {
        return stateMap.get(sequence);
    }
    /**
     * logic:
     * keep sequences indexed using hash map.
     * when processing a new frame:
     * 1. check with every other sequence to obtain result.
     * 2. if the result is empty, copy the old sequence & count list & prune old tuples.
     * 3. if the result is not empty, create a new sequence based on the new sequence & previous count list + 1 & remove old tuples.
     * 4. finally add a new sequence with count list = (time, 1)
     * Note: when adding, check if the result is already in the counting list.
     * @param sequence
     * @return
     */
    public List<CompactedObjectSequence> feed(CompactedObjectSequence sequence) {
//        System.out.println("=======================NAIVE:time:"+time);
        if (sequence != null && sequence.size() ==0) sequence = null;
        Set<CompactedObjectSequence> resultSet = new HashSet<>();
        if (stateMap != null) {
            // 1. get set.
            for (CompactedObjectSequence seq: new ArrayList<>(stateMap.keySet())) {
                // 1. remove oldest time.
//                int expiredTime = time - duration;
                List<Integer> oldTimeSet = stateMap.get(seq);
                while (oldTimeSet.size() > 0 && oldTimeSet.get(0) + duration <= time) {
                    oldTimeSet.remove(0);
                }
                if (oldTimeSet.size() == 0) {
                    // remove from map.
                    stateMap.remove(seq);
                } else {
                    // check if it's result.
                    if (oldTimeSet.size() >= maxCount) {
                        resultSet.add(seq);
                    }
                    if (sequence != null) {
                        // compute intersection.
                        CompactedObjectSequence inter = seq.intersect(sequence);
                        if (inter != null) {
                            List<Integer> interTimeSet = stateMap.get(inter);
                            if (interTimeSet == null) {
                                interTimeSet = new ArrayList<>();
                                stateMap.put(inter, interTimeSet);
                            } else {
                                // remove old one.
                                while (interTimeSet.size() > 0 && interTimeSet.get(0) + duration <= time) {
                                    interTimeSet.remove(0);
                                }
                            }
                            // add both.
                            mergeTheLaterListToFirst(interTimeSet, oldTimeSet);
                            if (interTimeSet.get(interTimeSet.size() - 1) != time)
                                interTimeSet.add(time);
                            // check if the inter is result.
                            if (interTimeSet.size() >= maxCount) {
                                resultSet.add(inter);
                            }
                        }
                    }
                }
            }
        } else {
            stateMap = new HashMap<>();
        }
        if (sequence != null) {
            // add current one.
            List<Integer> timeSet = stateMap.get(sequence);
            if (timeSet == null) {
                timeSet = new ArrayList<>();
                stateMap.put(sequence, timeSet);
            }
            if (timeSet.size() == 0 || timeSet.get(timeSet.size() -1) != time) {
                timeSet.add(time);
            }
        }

        LOG.debug("current map===============");
        stateMap.entrySet().forEach(LOG::debug);
        LOG.debug("============================");
        time++;
        Map<List<Integer>, CompactedObjectSequence> framesSeqMap = new HashMap<>();
        for (CompactedObjectSequence seq: resultSet) {
            List<Integer> keySet = stateMap.get(seq);
            if (framesSeqMap.get(keySet) == null || framesSeqMap.get(keySet).size() < seq.size()) {
                framesSeqMap.put(keySet, seq);
            }
        }
        if (framesSeqMap.size() == 0) return null;
        return new ArrayList<>(framesSeqMap.values());
    }

    public void mergeTheLaterListToFirst(List<Integer> oldCounts, List<Integer> newCounts) {
        oldCounts.addAll(newCounts);
        Collections.sort(oldCounts);
        int lastV = -1;
        for (int i = oldCounts.size() - 1; i >= 0; i--) {
            if (lastV == oldCounts.get(i)) {
                oldCounts.remove(i);
            }
            lastV = oldCounts.get(i);
        }
    }
    @Override
    public void reset() {
        time = 0;
        stateMap = null;
    }

    public int getSize() {
        return stateMap.keySet().size();
    }
}
