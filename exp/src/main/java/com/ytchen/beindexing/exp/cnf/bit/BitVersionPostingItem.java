package com.ytchen.beindexing.exp.cnf.bit;

/**
 * does not support != op.
 */
public class BitVersionPostingItem implements Comparable<BitVersionPostingItem>{
    int queryId;
    int code;

    public BitVersionPostingItem(int queryId, int code) {
        this.queryId = queryId;
        this.code = code;
    }

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "(" +queryId +
                ", " + code +
                ')';
    }

    @Override
    public int compareTo(BitVersionPostingItem bitVersionPostingItem) {
        int v1 = Integer.compare(queryId, bitVersionPostingItem.queryId);
        if (v1 != 0) return v1;
        return Integer.compare(code, bitVersionPostingItem.code);
    }
}
