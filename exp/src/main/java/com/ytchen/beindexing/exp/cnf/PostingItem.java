package com.ytchen.beindexing.exp.cnf;

public class PostingItem implements Comparable<PostingItem> {
    int queryId;
    PostingOperator op;
    int disjunctionId;

    public PostingItem(int queryId, PostingOperator op, int disjunctionId) {
        this.queryId = queryId;
        this.op = op;
        this.disjunctionId = disjunctionId;
    }

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public PostingOperator getOp() {
        return op;
    }

    public void setOp(PostingOperator op) {
        this.op = op;
    }

    public int getDisjunctionId() {
        return disjunctionId;
    }

    public void setDisjunctionId(int disjunctionId) {
        this.disjunctionId = disjunctionId;
    }

    @Override
    public int compareTo(PostingItem postingItem) {
        int v1 = Integer.compare(queryId, postingItem.queryId);
        if (v1 != 0) return v1;
        int v2 = op.compareTo(postingItem.op);
        if (v2 != 0) return v2;
        return Integer.compare(disjunctionId, postingItem.disjunctionId);
    }

    public enum PostingOperator {
        // NOTICE mind the order.
        NOTIN("≠"), IN("=");

        String str;
        PostingOperator(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }
    }

    @Override
    public String toString() {
        return "(" + queryId + ", " + op.getStr() + ", " + disjunctionId+")";
    }
}
