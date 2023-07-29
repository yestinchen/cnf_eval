package com.ytchen.beindexing.exp.graphs.obj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.ytchen.beindexing.exp.utils.StringUtils.join;

public class CompactedObj implements Sortable, SetOperation<CompactedObj>, Comparable<CompactedObj> {
    List<String> ids;
    String clazz;

    public CompactedObj(List<String> id, String clazz) {
        this.ids = id;
        this.clazz = clazz;
    }

    public CompactedObj(String[] ids, String clazz) {
        this.ids = new ArrayList<String>(Arrays.asList(ids));
        this.clazz = clazz;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return clazz+": < " + join(ids, ", ") +">";
    }

    @Override
    public void sort() {
        Collections.sort(ids);
    }

    @Override
    public CompactedObj minus(CompactedObj other) {
        return filter(other, 0b010);
    }

    @Override
    public CompactedObj intersect(CompactedObj other) {
        return filter(other, 0b100);
    }

    @Override
    public CompactedObj union(CompactedObj other) {
        return filter(other, 0b111);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompactedObj that = (CompactedObj) o;
        return Objects.equals(ids, that.ids) &&
                Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ids, clazz);
    }

    /**
     * flag: what to keep.
     *
     * @param other
     * @param flag 0b1(keep same elements)1(keep in first set but not in the other)1(the other way round)
     * @return
     */
    private CompactedObj filter(CompactedObj other, int flag) {
        if (!clazz.equals(other.clazz)) return null;
        int cursor1 = 0, cursor2 = 0;
        List<String> result = new ArrayList<>();
        while(cursor1 < ids.size() && cursor2 < other.ids.size()) {
            String currentId1 = ids.get(cursor1), currentId2 = other.ids.get(cursor2);
            int compResult = currentId1.compareTo(currentId2);
            if (compResult == 0) {
                // both +1
                cursor1 ++; cursor2 ++;
                if ((flag & 0b100) == 0b100) {
                    result.add(currentId1);
                }
            } else if (compResult < 0) {
                // add to result.
                if ((flag & 0b010) == 0b010) {
                    result.add(currentId1);
                }
                // inc it
                cursor1 ++;
            } else {
                if ((flag & 0b001) == 0b001) {
                    result.add(currentId2);
                }
                cursor2 ++;
            }
        }
        if ((flag & 0b010) == 0b010 && cursor1 < ids.size()) {
            result.addAll(ids.subList(cursor1, ids.size()));
        }
        if ((flag & 0b001) == 0b001 && cursor2 < other.ids.size()) {
            result.addAll(other.ids.subList(cursor2, other.ids.size()));
        }
        return result.size() > 0 ? new CompactedObj(result, clazz) : null;

    }

//    public SetCompareResult onePassCompare(CompactedObj other, int flag) {
//        if (!clazz.equals(other.clazz)) return null;
//        int cursor1 = 0, cursor2 = 0;
//        SetCompareResult result = new SetCompareResult();
//        result.setDiff1_2(new CompactedObj(new ArrayList<>(), clazz));
//        result.setDiff2_1(new CompactedObj(new ArrayList<>(), clazz));
//        result.setIntersection(new CompactedObjectSequence(new ArrayList<>()));
//        while(cursor1 < ids.size() && cursor2 < other.ids.size()) {
//            String currentId1 = ids.get(cursor1), currentId2 = other.ids.get(cursor2);
//            int compResult = currentId1.compareTo(currentId2);
//            if (compResult == 0) {
//                // both +1
//                cursor1 ++; cursor2 ++;
//                if ((flag & 0b100) == 0b100) {
//                    result.getIntersection().add(currentId1);
//                }
//            } else if (compResult < 0) {
//                // add to result.
//                if ((flag & 0b010) == 0b010) {
//                    result.getDiff1_2().add(currentId1);
//                }
//                // inc it
//                cursor1 ++;
//            } else {
//                if ((flag & 0b001) == 0b001) {
//                    result.getDiff2_1().getSequence().add(currentId2);
//                }
//                cursor2 ++;
//            }
//        }
//        if ((flag & 0b010) == 0b010 && cursor1 < ids.size()) {
//            result.addAll(ids.subList(cursor1, ids.size()));
//        }
//        if ((flag & 0b001) == 0b001 && cursor2 < other.ids.size()) {
//            result.addAll(other.ids.subList(cursor2, other.ids.size()));
//        }
//        return result;
//    }

    @Override
    public int compareTo(CompactedObj o) {
        // should be sorted.
        int clazzResult = clazz.compareTo(o.clazz);
        if (clazzResult != 0) return clazzResult;
        for (int i= 0; i < ids.size(); i++) {
            if (i >= o.ids.size()) {
                return ids.size() - o.ids.size();
            } else {
                int elementResult = ids.get(i).compareTo(o.ids.get(i));
                if (elementResult != 0) return elementResult;
            }
        }
        return ids.size() - o.ids.size();
    }
}
