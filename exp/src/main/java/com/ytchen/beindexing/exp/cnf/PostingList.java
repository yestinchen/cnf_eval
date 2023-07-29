package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.common.PointingList;

import java.util.ArrayList;
import java.util.List;

public class PostingList extends PointingList<PostingItem> {

    private static int genId = 0;

    String originKey;

    int id = ++genId;

    public String getOriginKey() {
        return originKey;
    }

    public void setOriginKey(String originKey) {
        this.originKey = originKey;
    }

    public PostingList(List<PostingItem> list) {
        super(list);
    }

    public PostingList(List<PostingItem> list, int currentIndex) {
        super(list, currentIndex);
    }

    public void skipToNext(int queryId) {
        while(getCurrent() != null && getCurrent().queryId < queryId) {
            next();
        }
    }

    public int getId() {
        return id;
    }

    /**
     * split the posting list according to queryId.
     * @return
     */
    public List<PostingList> split() {
        int lastQueryId = -1;
        int firstOccur = -1;
        List<PostingList> result = new ArrayList<>();
        for (int i =0; i < size(); i++) {
            PostingItem item = get(i);
            int thisQueryId = item.getQueryId();
            if (thisQueryId != lastQueryId) {
                firstOccur = i;
                lastQueryId = thisQueryId;
                if (result.size() == 0) {
                    result.add(new PostingList(new ArrayList<>()));
                    result.get(0).setOriginKey(originKey);
                }
                result.get(0).add(item);
            } else {
                // compute.
                int pos = i-firstOccur;
                if (result.size() <= pos) {
                    result.add(new PostingList(new ArrayList<>()));
                    result.get(pos).setOriginKey(originKey);
                }
                result.get(pos).add(item);
            }
        }
        return result;
    }
}
