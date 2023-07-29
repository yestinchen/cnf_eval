package com.ytchen.beindexing.exp.cnf.bit;

import com.ytchen.beindexing.exp.common.PointingList;

import java.util.ArrayList;
import java.util.List;

public class BitVersionPostingList extends PointingList<BitVersionPostingItem> {

    private static int genId = 0;

    String originKey;

    int id = ++genId;

    public BitVersionPostingList(List<BitVersionPostingItem> list) {
        super(list);
    }

    public String getOriginKey() {
        return originKey;
    }

    public void setOriginKey(String originKey) {
        this.originKey = originKey;
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
    public List<BitVersionPostingList> split() {
        int lastQueryId = -1;
        int firstOccur = -1;
        List<BitVersionPostingList> result = new ArrayList<>();
        for (int i =0; i < size(); i++) {
            BitVersionPostingItem item = get(i);
            int thisQueryId = item.getQueryId();
            if (thisQueryId != lastQueryId) {
                firstOccur = i;
                lastQueryId = thisQueryId;
                if (result.size() == 0) {
                    result.add(new BitVersionPostingList(new ArrayList<>()));
                    result.get(0).setOriginKey(originKey);
                }
                result.get(0).add(item);
            } else {
                // compute.
                int pos = i-firstOccur;
                if (result.size() <= pos) {
                    result.add(new BitVersionPostingList(new ArrayList<>()));
                    result.get(pos).setOriginKey(originKey);
                }
                result.get(pos).add(item);
            }
        }
        return result;
    }
}
