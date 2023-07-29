package com.ytchen.beindexing.exp.graphs.obj;

import com.ytchen.beindexing.exp.common.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

import static com.ytchen.beindexing.exp.utils.StringUtils.join;

public class CompactedObjectSequence implements Sortable, SetOperation<CompactedObjectSequence>, Comparable<CompactedObjectSequence>{
    List<CompactedObj> sequence;

    public CompactedObjectSequence(List<CompactedObj> sequence) {
        this.sequence = sequence;
    }

    public List<CompactedObj> getSequence() {
        return sequence;
    }

    public void setSequence(List<CompactedObj> sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return join(sequence, "; ");
    }

    @Override
    public void sort() {
        for (CompactedObj obj: sequence) {
            obj.sort();
        }
        Collections.sort(sequence, Comparator.comparing(x -> x.clazz));
    }

    public SetCompareResult onePassForSetCompareInfo(CompactedObjectSequence other) {
        SetCompareResult result = new SetCompareResult();

        return result;
    }

    @Override
    public CompactedObjectSequence minus(CompactedObjectSequence other) {
        List<CompactedObj> diffObjs = new ArrayList<>();
        int cursor1 = 0, cursor2  =0;
        while(cursor1 < sequence.size() && cursor2 < other.sequence.size()) {
            // compare two cursors.
            CompactedObj obj1 = sequence.get(cursor1);
            CompactedObj obj2 = other.sequence.get(cursor2);
            int value = obj1.clazz.compareTo(obj2.clazz);
            if (value == 0) {
                // compute difference.
                CompactedObj diffObj = obj1.minus(obj2);
                if (diffObj != null) diffObjs.add(diffObj);
                // inc both.
                cursor1 ++; cursor2 ++;
            } else if (value < 0) {
                // add
                diffObjs.add(obj1);
                cursor1 ++;
            } else {
                cursor2 ++;
            }
        }
        // if cursor1 not the end, add all the rest.
        if (cursor1 < sequence.size()) {
            diffObjs.addAll(sequence.subList(cursor1, sequence.size()));
        }
        return diffObjs.size() > 0 ? new CompactedObjectSequence(diffObjs): null;
    }

    @Override
    public CompactedObjectSequence intersect(CompactedObjectSequence other) {
        Map<String, Integer> countMap = new HashMap<>();
        for (CompactedObj obj : this.sequence) {
            for (String id : obj.getIds()) {
                countMap.put(id, 1);
            }
        }
        for (CompactedObj obj : other.sequence) {
            for (String id : obj.getIds()) {
                countMap.put(id, countMap.getOrDefault(id, 0) + 1);
            }
        }
        List<CompactedObj> diffObjs = new ArrayList<>();
        for(CompactedObj obj : this.sequence) {
            List<String> ids = new ArrayList<>();
            for (String id : obj.getIds()) {
                if (countMap.get(id) == 2) ids.add(id);
            }
            if (ids.size() > 0) {
                diffObjs.add(new CompactedObj(ids, obj.clazz));
            }
        }
//        List<CompactedObj> diffObjs = new ArrayList<>();
//        int cursor1 = 0, cursor2  =0;
//        while(cursor1 < sequence.size() && cursor2 < other.sequence.size()) {
//            // compare two cursors.
//            CompactedObj obj1 = sequence.get(cursor1);
//            CompactedObj obj2 = other.sequence.get(cursor2);
//            int value = obj1.clazz.compareTo(obj2.clazz);
//            if (value == 0) {
//                // compute difference.
//                CompactedObj diffObj = obj1.intersect(obj2);
//                if (diffObj != null) diffObjs.add(diffObj);
//                // inc both.
//                cursor1 ++; cursor2 ++;
//            } else if (value < 0) {
//                cursor1 ++;
//            } else {
//                cursor2 ++;
//            }
//        }
//        System.out.println("diff1:"+this);
//        System.out.println("diff2:"+other);
//        System.out.println("result:"+diffObjs);
        CompactedObjectSequence seq = diffObjs.size() > 0 ? new CompactedObjectSequence(diffObjs): null;
//        if (seq != null) {
//            seq.sort();
//        }
        return seq;
    }

    @Override
    public CompactedObjectSequence union(CompactedObjectSequence other) {
        List<CompactedObj> diffObjs = new ArrayList<>();
        int cursor1 = 0, cursor2  =0;
        while(cursor1 < sequence.size() && cursor2 < other.sequence.size()) {
            // compare two cursors.
            CompactedObj obj1 = sequence.get(cursor1);
            CompactedObj obj2 = other.sequence.get(cursor2);
            int value = obj1.clazz.compareTo(obj2.clazz);
            if (value == 0) {
                // compute difference.
                CompactedObj unionObj = obj1.union(obj2);
                if (unionObj != null) {
                    diffObjs.add(unionObj);
                }
                // inc both.
                cursor1 ++; cursor2 ++;
            } else if (value < 0) {
                diffObjs.add(obj1);
                cursor1 ++;
            } else {
                diffObjs.add(obj2);
                cursor2 ++;
            }
        }
        if (cursor1 < sequence.size()) {
            diffObjs.addAll(sequence.subList(cursor1, sequence.size()));
        }
        if (cursor2 < other.sequence.size()) {
            diffObjs.addAll(other.sequence.subList(cursor2, other.sequence.size()));

        }
        return diffObjs.size() > 0 ? new CompactedObjectSequence(diffObjs): null;
    }

    int size = -1;
    public int size() {
        if (size == -1) {
            size = this.sequence.stream().mapToInt(i -> i.ids.size()).sum();
        }
        return size;
    }

    public List<Tuple2<String, Integer>> assignment;
    public List<Tuple2<String, Integer>> genAssignment() {
        if (assignment == null) {
            assignment = sequence.stream().map(i -> new Tuple2<String, Integer>(i.clazz, i.ids.size())).collect(Collectors.<Tuple2<String, Integer>>toList());
        }
        return assignment;
    }

    @Override
    public int compareTo(CompactedObjectSequence o) {
        // ensure two are sorted.
        for (int i =0; i < getSequence().size(); i++) {
            if (o.getSequence().size() <= i) {
                return getSequence().size() - o.getSequence().size();
            } else {
                int thisR = getSequence().get(i).compareTo(o.getSequence().get(i));
                if (thisR != 0)
                    return thisR;
            }
        }
        return getSequence().size() - o.getSequence().size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompactedObjectSequence sequence1 = (CompactedObjectSequence) o;
        return Objects.equals(sequence, sequence1.sequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence);
    }
}
