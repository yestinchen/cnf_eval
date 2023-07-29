package com.ytchen.beindexing.exp.graphs.builder;

import com.ytchen.beindexing.exp.graphs.obj.CompactedObjectSequence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class SSGBuilder implements IStateBuilder {

    private static final Logger LOG = LogManager.getLogger(SSGBuilder.class);

    {
//        Configurator.setLevel(LOG.getName(), Level.DEBUG);
    }
    int duration;

    int maxCount;
    List<State> principalStates = new ArrayList<>();
    Map<State, Integer> psSet = new HashMap<>();
    Map<CompactedObjectSequence, State> indexedMap = new HashMap<>();

    Set<State> lastResult;
    int time = 1;
    int visitedStateCount = 0;
    int generatedStateCount = 0;
    int beforeProcessTotalStateCount = 0;
    int oldStateCounts = 0;

    public SSGBuilder(int duration, int maxCount) {
        this.duration = duration;
        this.maxCount = maxCount;
    }

    @Override
    public void reset() {
        principalStates.clear();
        indexedMap.clear();
        lastResult = null;
        visitedStateCount = 0;
        generatedStateCount  = 0;
        beforeProcessTotalStateCount = 0;
        oldStateCounts = 0;
        psSet.clear();
    }

    private void pruneState(State state) {
        while(state.getTimes().size() > 0 && state.getTimes().get(0).getTime() + duration <= time) {
            // remove it.
            MarkableTime time = state.getTimes().remove(0);
            if (time.isMarker()) state.decMarkerSize();
        }
    }

    void incTime(State currentState, Set<State> result) {
        if (currentState.getTimes().size() == 0 || currentState.getTimes().get(currentState.getTimes().size() -1 ).getTime() != time) {
            // add time.
            currentState.getTimes().add(new MarkableTime(time, false));
        }
        if (currentState.getTimes().size() >= maxCount && currentState.markerSize > 0 ) {
            // add new one.
            LOG.debug("add to result: {}", currentState);
            result.add(currentState);
        }
    }

    public void appendNewTimeToAllSubNodes(State currentState, Set<State> result) {
        pruneState(currentState);
        incTime(currentState, result);
        if (currentState.nextStates == null) return;
        for (State n : currentState.nextStates) {
            appendNewTimeToAllSubNodes(n, result);
        }
    }

    public void processOriginState(State currentState, CompactedObjectSequence givenSequence,
                                   Set<State> result, boolean compute) {
        if (currentState.flagVersion == time) {
            return;
        }
        currentState.flagVersion = time;
        pruneState(currentState);
        if (currentState.markerSize == 0) {
            //  remove current edge.
            State ps = currentState.parentState;
            ps.nextStates.remove(currentState);
            indexedMap.remove(currentState.getSequence());
            currentState.parentState = null;
            if (currentState.nextStates != null) {
                for (State n : new ArrayList<>(currentState.nextStates)){
                    // reconnect.
                    n.parentState = ps;
                    ps.nextStates.add(n);
                    processOriginState(n, givenSequence, result, compute);
                }
                currentState.nextStates = null;
            }
            return;
        }
        // check if current state should be added.
        if (currentState.markerSize > 0 && currentState.times.size() >= maxCount) {
            result.add(currentState);
        }
        if (compute) {
            CompactedObjectSequence intersection = currentState.sequence.intersect(givenSequence);
            if (intersection != null) {
                if (intersection.size() == currentState.sequence.size()) {
                    // append new time.
                    // append to all the rest nodes.
                    appendNewTimeToAllSubNodes(currentState, result);
                } else {
                    // a sub result.
                    State newState = indexedMap.get(intersection);
                    boolean firstCreated = false;
                    if (newState == null) {
                        newState = new State();
                        newState.setSequence(intersection);
                        indexedMap.put(intersection, newState);
                        firstCreated = true;
                    } else {
                        pruneState(newState);
                    }
                    mergeTheLaterListToFirst(newState, currentState, false);
                    incTime(newState, result);

                    // continue to its sub nodes.
                    if (currentState.nextStates != null) {
                        for (State n : new ArrayList<>(currentState.nextStates)) {
                            processOriginState(n, givenSequence, result, true);
                        }
                    }
                    // add to next only if this is created for the first time.
                    if (firstCreated) {
                        currentState.addNextState(newState);
                        newState.setParentState(currentState);
                    }
                }

            } else {
                if (currentState.nextStates != null) {
                    for (State n : new ArrayList<>(currentState.nextStates)) {
                        processOriginState(n, givenSequence, result, false);
                    }
                }
            }
        } else {
            if (currentState.nextStates != null) {
                for (State n : new ArrayList<>(currentState.nextStates)) {
                    processOriginState(n, givenSequence, result, false);
                }
            }
        }
    }

    public List<State> feedReturnState(CompactedObjectSequence sequence) {
//        LOG.debug("time:#{}, feeding: {}", time, sequence);
        // reset counters;
        visitedStateCount = 0;
        generatedStateCount = 0;
        oldStateCounts = 0;
        beforeProcessTotalStateCount = indexedMap.size();
        Set<State> result = new HashSet<>();

        if (principalStates.size() != 0) {

            // 2. for each state, start compute.
            if (principalStates.size() == duration) {
                // get oldest.
                State oldestState = principalStates.remove(0);
                removeFromPSSet(oldestState);
                // 1. obtain current ps
                removeOldStates(oldestState, null, psSet.keySet());
            }

            State newFrameState = indexedMap.get(sequence);
//            boolean newCreated = false;
            if (newFrameState == null) {
                newFrameState = new State();
                newFrameState.setSequence(sequence);
                indexedMap.put(sequence, newFrameState);
//                newCreated = true;
            }
            // prune newFrame.
            pruneState(newFrameState);

            newFrameState.getTimes().add(new MarkableTime(time, true));
            // inc marker count.
            newFrameState.incMarkerSize();

            if (newFrameState.markerSize > 0 && newFrameState.times.size() >= maxCount) {
                result.add(newFrameState);
            }


            for (int i =0; i < principalStates.size(); i++) {
                State originState = principalStates.get(i);
                if (originState.flagVersion != time ) {
                    // only visit if not visited.
                    processOriginState(originState, sequence, result, true);
                }
            }

            principalStates.add(newFrameState);
            addToPSSet(newFrameState);
            // break the connection.
            if (newFrameState.parentState != null) {
                newFrameState.parentState.nextStates.remove(newFrameState);
                newFrameState.parentState = null;
            }

        } else {
            generatedStateCount ++;
            // add directly.
            State state = new State();
            state.setSequence(sequence);
            state.getTimes().add(new MarkableTime(time, true));
            state.incMarkerSize();
            principalStates.add(state);
            indexedMap.put(sequence, state);
            addToPSSet(state);
        }
        time ++;
        return new ArrayList<>(result);
    }

    void addToPSSet(State s ) {
        psSet.put(s, psSet.getOrDefault(s, 0) + 1);
    }

    void removeFromPSSet(State s) {
        int c = psSet.get(s);
        if (c == 1) {
            psSet.remove(s);
        } else {
            psSet.put(s, c -1);
        }
    }

    public List<CompactedObjectSequence> feed(CompactedObjectSequence sequence) {
        List<State> result = feedReturnState(sequence);
        return result.size() == 0 ? null :result.stream().map(i -> i.getSequence()).collect(Collectors.toList());
    }

    public State get(CompactedObjectSequence seq) {
        return indexedMap.get(seq);
    }

    public void removeOldStates(State oldestState, State attachState, Set<State> allPS) {
        LOG.debug("removing state: {}", oldestState.id);
        pruneState(oldestState);
        List<State> stateToVisit = new ArrayList<>();
        if (oldestState.getMarkerSize() == 0) {
            indexedMap.remove(oldestState.getSequence());

            // remove from parent state.
            if (oldestState.parentState != null) {
                if (oldestState.parentState.nextStates != null) {
                    oldestState.parentState.nextStates.remove(oldestState);
                }
                oldestState.parentState = null;
                // remove all parents.
            }
            // remove next.
            if (oldestState.nextStates != null) {
                stateToVisit = oldestState.nextStates;
            }
            oldestState.nextStates = null;
            // continue next
            for (State s : stateToVisit) {
                removeOldStates(s, attachState, allPS);
            }
        } else {
            // check parent.
            if (attachState == null) {
                // get attach state.
                int next = oldestState.getNextMark();
                int pos = next - (time - duration) -1 ;
                attachState = principalStates.get(pos);
            }
            if (attachState != oldestState.parentState && attachState != oldestState) {
                // modify
                if (oldestState.parentState != null &&
                        oldestState.parentState.nextStates != null) {
                    oldestState.parentState.nextStates.remove(oldestState);
                }
                // check if the current state is a principal state.
                if (!allPS.contains(oldestState)) {
                    oldestState.parentState = attachState;
                    attachState.addNextState(oldestState);
                }
            }
            if (oldestState.nextStates != null) {
                stateToVisit.addAll(oldestState.nextStates);
            }
            attachState = oldestState;
        }
    }

    public void describeLastProcess() {
        System.out.println("report from time=#"+(time-1));
        System.out.println("total states before: " + beforeProcessTotalStateCount);
        System.out.println("visited states: " + visitedStateCount);
        System.out.println("new states:" + generatedStateCount);
        System.out.println("total states now: " + indexedMap.size());
        System.out.println("old states count: " + oldStateCounts);
        System.out.println("report done");
    }

    public int size(){
        return indexedMap.size();
    }

    public void mergeTheLaterListToFirst(State baseState, List<MarkableTime> newList, boolean ignoreMarkers) {

        List<MarkableTime> oldCounts = baseState.getTimes();
        List<MarkableTime> newCounts = newList;
//        LOG.debug("merging: {} and {}", oldCounts, newCounts);
        int index1 = 0, index2 = 0;
        while(index1 < oldCounts.size() && index2 < newCounts.size()) {
            MarkableTime leftTime = oldCounts.get(index1);
            MarkableTime rightTime = newCounts.get(index2);
            if (!ignoreMarkers && leftTime.time == rightTime.time) {
                // check
                // merge marker.
                if (newCounts.get(index2).isMarker() && !oldCounts.get(index1).isMarker()){
                    // set marker.
                    oldCounts.get(index1).setMarker(true);
                    baseState.incMarkerSize();
                }
                index1 ++;
                index2 ++;
            } else if (leftTime.time > rightTime.time) {
                if (ignoreMarkers) {
                    oldCounts.add(index1, new MarkableTime(rightTime.time, false));
                } else {
                    //  add
                    oldCounts.add(index1, new MarkableTime(rightTime));
                    if (rightTime.isMarker()) {
                        baseState.incMarkerSize();
                    }
                }
                index2 ++;
            } else {
                index1 ++;
            }
        }
        while (index2 < newCounts.size()) {
            MarkableTime rightTime = newCounts.get(index2);
            // add the result.
            if (ignoreMarkers) {
                oldCounts.add(new MarkableTime(rightTime.time, false));
            } else {
                //  add
                oldCounts.add(new MarkableTime(rightTime));
                if (rightTime.isMarker()) {
                    baseState.incMarkerSize();
                }
            }
            index2 ++;
        }
//        LOG.debug("merge result: {}", oldCounts);
    }

    public void mergeTheLaterListToFirst(State baseState, State newState, boolean ignoreMarkers) {
//        LOG.debug("base state: {}, newState: {}", baseState, newState);
        mergeTheLaterListToFirst(baseState, newState.getTimes(), ignoreMarkers);
    }

    public Map<CompactedObjectSequence, State> getIndexedMap() {
        return indexedMap;
    }

    static class ProcessItem {
        State parentState;
        State currentState;
        State pInterState;
        CompactedObjectSequence pInter;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProcessItem that = (ProcessItem) o;
            return Objects.equals(currentState, that.currentState);
        }

        @Override
        public int hashCode() {
            return Objects.hash(currentState);
        }

        @Override
        public String toString() {
            return "ProcessItem{" +
                    "parentState=" + parentState +
                    ", currentState=" + currentState +
                    ", pInterState=" + pInterState +
                    ", pInter=" + pInter +
                    '}';
        }
    }

    public static class MarkableTime {
        int time;
        boolean marker;

        public MarkableTime (MarkableTime markableTime) {
            this.time = markableTime.time;
            this.marker = markableTime.marker;
        }

        public MarkableTime(int time, boolean marker) {
            this.time = time;
            this.marker = marker;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public boolean isMarker() {
            return marker;
        }

        public void setMarker(boolean marker) {
            this.marker = marker;
        }

        @Override
        public String toString() {
            if (marker) {
                return "*"+time;
            }
            return String.valueOf(time);
        }
    }

    public static class OriginState {
        State state;
        int time;

        @Override
        public String toString() {
            return "frame#" + time+","+
                    state;
        }
    }

    public static class Candidate {
        State state;
        Set<State> id = new HashSet<>();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Candidate candidate = (Candidate) o;
            return Objects.equals(state.id, candidate.state.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state.id);
        }
    }

    public static class State {
        CompactedObjectSequence sequence;
        List<MarkableTime> times = new ArrayList<>();
        int markerSize;
        List<State> nextStates;
        State parentState;
        int flagVersion;
        int clearFlag;
        int marker;
        int id = genId();
        private static int nextId = 1;

        static int genId() {
            return nextId++;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        public CompactedObjectSequence getSequence() {
            return sequence;
        }

        public void setSequence(CompactedObjectSequence sequence) {
            this.sequence = sequence;
        }

        public List<MarkableTime> getTimes() {
            return times;
        }

        public void setTimes(List<MarkableTime> times) {
            this.times = times;
        }

        public List<State> getNextStates() {
            return nextStates;
        }

        public void setNextStates(List<State> nextStates) {
            this.nextStates = nextStates;
        }

        public void addNextState(State nextState) {
            if (nextStates == null) {
                nextStates = new ArrayList<>();
            }
            nextStates.add(nextState);
        }

        public void setParentState(State parentState) {
            this.parentState = parentState;
        }

        public int getFlagVersion() {
            return flagVersion;
        }

        public void setFlagVersion(int flagVersion) {
            this.flagVersion = flagVersion;
        }

        public int getClearFlag() {
            return clearFlag;
        }

        public void setClearFlag(int clearFlag) {
            this.clearFlag = clearFlag;
        }

        public int getMarkerSize() {
            return markerSize;
        }

        public void setMarkerSize(int markerSize) {
            this.markerSize = markerSize;
        }

        public void incMarkerSize() {
            markerSize ++;
        }

        public void decMarkerSize() {
            markerSize --;
        }

        public int getId() {
            return id;
        }

        public int getNextMark() {
            if (markerSize == 0) return -1;
            for (MarkableTime mt: times) {
                if (mt.isMarker()) return mt.time;
            }
            return -1;
        }

        @Override
        public String toString() {
            return "State{#" +id+", " +
                    "sequence=" + sequence +
                    ", times=" + times +
                    ", timeSize="+ times.size()+
                    ", markerSize=" + markerSize +
                    ", flagVersion=" + flagVersion +
                    ", clearFlag=" + clearFlag +
                    ", nextStates=" + nextStates +
                    '}';
        }
    }

}
