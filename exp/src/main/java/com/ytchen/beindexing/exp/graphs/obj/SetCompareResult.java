package com.ytchen.beindexing.exp.graphs.obj;

public class SetCompareResult {
    CompactedObjectSequence diff1_2;

    CompactedObjectSequence intersection;

    CompactedObjectSequence diff2_1;

    public CompactedObjectSequence getDiff1_2() {
        return diff1_2;
    }

    public void setDiff1_2(CompactedObjectSequence diff1_2) {
        this.diff1_2 = diff1_2;
    }

    public CompactedObjectSequence getIntersection() {
        return intersection;
    }

    public void setIntersection(CompactedObjectSequence intersection) {
        this.intersection = intersection;
    }

    public CompactedObjectSequence getDiff2_1() {
        return diff2_1;
    }

    public void setDiff2_1(CompactedObjectSequence diff2_1) {
        this.diff2_1 = diff2_1;
    }
}
