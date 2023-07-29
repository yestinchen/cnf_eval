package com.ytchen.beindexing.exp.graphs.be;

import com.ytchen.beindexing.exp.cnf.ICNFEvaluator;
import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MFSIntegratedEvaluatorV2 implements SequenceEvaluator {
    private Logger LOG = LogManager.getLogger(MFSIntegratedEvaluatorV2.class);

    int duration;

    int maxCount;

    Map<CompactedObjectSequence, SequenceCounter> stateMap;

    int time = 1;
    double ratio = -1;

    ICNFEvaluator evaluator;

    public MFSIntegratedEvaluatorV2(int duration, int maxCount, ICNFEvaluator evaluator) {
        this.duration = duration;
        this.maxCount = maxCount;
        this.evaluator = evaluator;
    }

    public Map<CompactedObjectSequence, SequenceCounter> getStateMap() {
        return stateMap;
    }

    public SequenceCounter get(CompactedObjectSequence seq) {
        return stateMap.get(seq);
    }

    long totalCount = 0, nonEmptyCount = 0;
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
//        System.out.println("====================time:"+time);
        Set<CompactedObjectSequence> result = new HashSet<>();
//        originStates.add(new OriginState(time, sequence));
//        if (originStates.size() > duration) {
//            originStates.remove(0);
//        }
        if (stateMap != null) {
            Set<CompactedObjectSequence> tobeRemoved = new HashSet<>();
            // visit all sequences.
            for (CompactedObjectSequence seq: new ArrayList<>(stateMap.keySet())) {
                totalCount ++;
                SequenceCounter counter = stateMap.get(seq);
                // remove oldest.
                if (counter.getTimes().size() > 0 && counter.getTimes().get(0).time + duration <= time) {
                    // remove.
                    MarkableTime mt = counter.getTimes().remove(0);
                    if (mt.mark) {
                        counter.decMark();
                    }
                }
                // check if it's empty.
                if (counter.markCount == 0) {
                    // remove it.
                    tobeRemoved.add(seq);
                } else {
                    // check if its result.
                    if (counter.getTimes().size() >= maxCount) {
                        result.add(seq);
                    }
                    // compute intersection.
                    CompactedObjectSequence newSequence = seq.intersect(sequence);
                    if (newSequence != null) {
                        nonEmptyCount++;
                        SequenceCounter newCounter = stateMap.get(newSequence);
                        // test it.
                        if (newCounter == null) {
                            newCounter = new SequenceCounter();
                            List<Integer> evalResult = evaluator.evaluate(newSequence.genAssignment());
                            newCounter.terminate = evalResult== null || evalResult.size() == 0;
                            if (!newCounter.terminate) {
                                // add times.
                                for (MarkableTime c : counter.getTimes()) {
                                    MarkableTime mt = new MarkableTime();
                                    mt.mark = c.mark;
                                    mt.time = c.time;
                                    newCounter.getTimes().add(mt);
                                }
                                newCounter.markCount = counter.markCount;
                            }
                            stateMap.put(newSequence, newCounter);
                        } else {
                            if (!newCounter.terminate) {
                                // remove oldest one.
                                if (newCounter.getTimes().get(0).time + duration <= time) {
                                    MarkableTime mt = newCounter.getTimes().remove(0);
                                    if (mt.mark) {
                                        newCounter.decMark();
                                    }
                                }
                            }
                        }

                        if (!newCounter.terminate) {
                            // merge times.
//                        System.out.println("before merge: "+ newCounter);
                            mergeTheLaterListToFirst(newCounter, counter.getTimes());
                            if (newCounter.getTimes().get(newCounter.getTimes().size() - 1).time != time) {
                                MarkableTime mt = new MarkableTime();
                                mt.time = time;
                                newCounter.getTimes().add(mt);
                            }
//                        System.out.println("count:"+counter);
//                        System.out.println("newCounter:"+newCounter);
                            if (newCounter.getTimes().size() >= maxCount) {
                                // add new one.
                                result.add(newSequence);
                            }
                        }
                    }
                }
            }
            // add current one if needed.
            if (sequence != null && sequence.size() != 0) {
                if (!stateMap.containsKey(sequence)) {
                    boolean continueProcess = true;
                    continueProcess = evaluator.evaluate(sequence.genAssignment()).size() != 0;
                    if (continueProcess) {
                        SequenceCounter sequenceCounter = new SequenceCounter();
                        MarkableTime mt = new MarkableTime();
                        mt.time = time;
                        mt.mark = true;
                        sequenceCounter.getTimes().add(mt);
                        sequenceCounter.incMark();
                        stateMap.put(sequence, sequenceCounter);
                    }
                } else {
                    SequenceCounter sequenceCounter = stateMap.get(sequence);
                    List<MarkableTime> times = sequenceCounter.getTimes();
                    if (times.size() == 0 || times.get(times.size() - 1).time != time) {
                        MarkableTime mt = new MarkableTime();
                        mt.time = time;
                        mt.mark = true;
                        sequenceCounter.getTimes().add(mt);
                        sequenceCounter.incMark();
                    } else if (!times.get(times.size() -1).mark) {
                        times.get(times.size() -1).mark = true;
                        sequenceCounter.incMark();
                        if (sequenceCounter.times.size() >= maxCount) {
                            result.add(sequence);
                        }
                    }
                }
            }
            // merge origins.
//            for (OriginState s : originStates) {
//                // compute inter
//                CompactedObjectSequence inter = sequence.intersect(s.sequence);
//                // merge.
//                if (inter != null) {
//                    SequenceCounter originCounter = stateMap.get(s.sequence);
//                    SequenceCounter newCounter = stateMap.get(inter);
//                    if (newCounter != null) {
//                        mergeTheLaterListToFirst(newCounter, originCounter.getTimes());
//                        if (newCounter.times.size() >= maxCount && newCounter.markCount > 0) {
//                            result.add(inter);
//                        }
//                    }
//                    // mark time also ?
//                }
//            }
            for (CompactedObjectSequence seq: tobeRemoved) {
                if (stateMap.get(seq).markCount == 0) {
                    // remove it.
//                    System.out.println("removing:"+seq);
//                    System.out.println("removing counter:"+stateMap.get(seq));
                    stateMap.remove(seq);
                }
            }
        } else {
            if (sequence != null) {
                stateMap = new HashMap<>();
                SequenceCounter sequenceCounter = new SequenceCounter();
                MarkableTime mt = new MarkableTime();
                mt.time = time;
                mt.mark = true;
                sequenceCounter.getTimes().add(mt);
                sequenceCounter.incMark();
                stateMap.put(sequence, sequenceCounter);
            }
        }
        // add current one.
        LOG.debug("current map===============");
        stateMap.entrySet().forEach(LOG::debug);
        LOG.debug("============================");
//        System.out.println("current map size: "+ stateMap.size());
        time++;

        return result.size() == 0 ? null : new ArrayList<>(result);
    }

    @Override
    public List<Integer> evaluate(CompactedObjectSequence sequence) {
        List<CompactedObjectSequence> sequences =  feed(sequence);
        Set<Integer> result = new HashSet<>();
        if (sequences == null) return null;
        for (CompactedObjectSequence seq: sequences) {
            result.addAll(evaluator.evaluate(seq.genAssignment()));
        }
        return new ArrayList<>(result);
    }

    @Override
    public void reset() {
        System.out.println("ratio:"+(1.0 * nonEmptyCount / totalCount));
        totalCount = 0;
        nonEmptyCount = 0;
        stateMap.clear();
        time = 1;
    }

    public void mergeTheLaterListToFirst(SequenceCounter counter, List<MarkableTime> newCounts) {
        List<MarkableTime> oldCounts = counter.times;
        LOG.debug("merging: {} and {}", oldCounts, newCounts);
        int index1 = 0, index2 = 0;
        while(index1 < oldCounts.size() && index2 < newCounts.size()) {
            MarkableTime leftTime = oldCounts.get(index1);
            MarkableTime rightTime = newCounts.get(index2);
            if (leftTime.time == rightTime.time) {
                // check
                // merge marker.
                if (newCounts.get(index2).mark && !oldCounts.get(index1).mark){
                    // set marker.
                    oldCounts.get(index1).mark = true;
                    counter.incMark();
                }
                index1 ++;
                index2 ++;
            } else if (leftTime.time > rightTime.time) {
                //  add
                MarkableTime mt = new MarkableTime();
                mt.time = rightTime.time;
                mt.mark = rightTime.mark;
                oldCounts.add(index1, mt);
                if (rightTime.mark) {
                    counter.incMark();
                }
                index2 ++;
            } else {
                index1 ++;
            }
        }
        while (index2 < newCounts.size()) {
            MarkableTime rightTime = newCounts.get(index2);
            // add the result.
                //  add
            MarkableTime mt = new MarkableTime();
            mt.time = rightTime.time;
            mt.mark = rightTime.mark;
            oldCounts.add(index1, mt);
            if (rightTime.mark) {
                counter.incMark();
            }
            index2 ++;
        }

//        LOG.debug("merge two counts: {} with {}", oldCounts, newCounts);
//        int cursor1 = 0, cursor2 = 0;
//        while(cursor1 < oldCounts.size() && cursor2 < newCounts.size()) {
//            int oldV = oldCounts.get(cursor1);
//            int newV = newCounts.get(cursor2);
//            if (oldV == newV) {
//                // do nothing.
//                cursor1 ++;
//                cursor2 ++;
//            } else if (oldV < newV) {
//                cursor1 ++;
//            } else {
//                cursor2 ++;
//                oldCounts.add(oldCounts.size() -1, newV);
//            }
//        }
//        if (cursor2 < newCounts.size()) {
//            // add all
//            oldCounts.addAll(newCounts.subList(cursor2, newCounts.size()));
//        }
//        LOG.debug("merge result: {}", oldCounts);

    }

    public int getSize() {
        return stateMap.keySet().size();
    }


    public static class OriginState {
        int time;
        CompactedObjectSequence sequence;

        public OriginState(int time, CompactedObjectSequence sequence) {
            this.time = time;
            this.sequence = sequence;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public CompactedObjectSequence getSequence() {
            return sequence;
        }

        public void setSequence(CompactedObjectSequence sequence) {
            this.sequence = sequence;
        }
    }

    public static class SequenceCounter {
        List<MarkableTime> times;
        int markCount;
        boolean terminate;

        public SequenceCounter() {
            times = new ArrayList<>();
        }

        public List<MarkableTime> getTimes() {
            return times;
        }

        public void setTimes(List<MarkableTime> times) {
            this.times = times;
        }

        public void incMark() {
            markCount ++;
        }
        public void decMark() {
            markCount --;
        }

        @Override
        public String toString() {
            return "SequenceCounter{" +
                    "times=" + times +
                    ", markCount=" + markCount +
                    '}';
        }

        public boolean isTerminate() {
            return terminate;
        }

        public void setTerminate(boolean terminate) {
            this.terminate = terminate;
        }
    }

    public static class MarkableTime{
        public int time;
        public boolean mark;

        @Override
        public String toString() {
            return (mark ? "*" : "") + time;
        }
    }
}
