package com.ytchen.beindexing.exp.cnf;


import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.DisjunctionExpression;
import com.ytchen.beindexing.exp.expression.OP;
import com.ytchen.beindexing.exp.expression.SimpleExpression;
import com.ytchen.beindexing.exp.graphs.obj.Sortable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ytchen.beindexing.exp.common.KeyGen.genKey;
import static com.ytchen.beindexing.exp.utils.ToStringUtils.center;
import static com.ytchen.beindexing.exp.utils.ToStringUtils.repeat;

public class InvertedList<T extends Comparable<T>> {
    Map<Integer, Map<T, PostingList>> map;
    int maxK = 0;

    public InvertedList(Map<Integer, Map<T, PostingList>> map) {
        this.map = map;
        for (int k : map.keySet()) {
            if (k > maxK) maxK = k;
        }
    }

    public int getMaxK() {
        return maxK;
    }

    public Map<T, PostingList> getPostingList(int k) {
        return map.get(k);
    }

    /*
    -----------------------------
    K(size=3) | Key | PostingList
    -----------------------------
    0 | xxx | (), (), ()
    -----------------------------
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        if (map == null || map.keySet().size() == 0) {
            sb.append(repeat("-",20)).append("\n");
            sb.append("empty");
            sb.append(repeat("-",20)).append("\n");
        } else {
            // compute size.

            // compute k.
            int tmp = maxK;
            int kSize =1;
            while(tmp > 10) {
                tmp %= 10;
                kSize ++;
            }
            int keySize =0;
            int postingListSize =0;
            for (int k: map.keySet()) {
                // compute keys.
                int currentMax = map.get(k).keySet().stream().mapToInt(i -> i.toString().length()).max().getAsInt();
                if (keySize < currentMax) keySize = currentMax;
                // compute postingList.
                int plMax = map.get(k).values().stream().mapToInt(i -> i.toString().length()).max().getAsInt();
                if (postingListSize < plMax) postingListSize = plMax;
            }

            // add margins
            kSize += 2;
            keySize += 2;
            postingListSize += 2;

//            System.out.println("sizes:" + kSize +","+keySize+","+postingListSize);

            // format.
            int totalLength = kSize + keySize + postingListSize + 3;
            sb.append(repeat("-",totalLength)).append("\n");
            // header
            sb.append(center("K", kSize)).append("|").append(center("Key", keySize))
                    .append("|").append(center("PostingList", postingListSize)).append("\n");
            // content.
            List<Integer> kSorted = new ArrayList<>(map.keySet());
            Collections.sort(kSorted);
            for (int k: kSorted) {
                // add border
                sb.append(repeat("-", totalLength)).append("\n");

                Map<T, PostingList> postingListMap = map.get(k);
                // compute height
                int height = postingListMap.size();
                int kPosition = height / 2 ;

                List<T> keySorted = new ArrayList<>(postingListMap.keySet());
                Collections.sort(keySorted);
                for (int i=0; i < height; i++) {
                    // output items.

                    // k
                    if (i == kPosition) {
                        sb.append(center(k+"", kSize));
                    } else {
                        sb.append(repeat(" ", kSize));
                    }
                    sb.append("|");

                    // key.
                    sb.append(center(keySorted.get(i).toString(), keySize)).append("|");

                    // posting list
                    sb.append(" ").append(postingListMap.get(keySorted.get(i))).append("\n");
                }

            }
            sb.append(repeat("-",totalLength)).append("\n");
        }
        return sb.toString();
    }

    public static InvertedList<AttrValueKey> fromCNFExpressions(List<CNFExpression> expressions) {
        Map<Integer, Map<AttrValueKey, PostingList>> map = new HashMap<>();
        for (CNFExpression e : expressions) {
            // extract k
            int k = e.computeK();
            Map<AttrValueKey, PostingList> workingMap = map.getOrDefault(k, new HashMap<>());
            if (!map.containsKey(k)) map.put(k, workingMap);
            // index
            int disjId =0;
            for (DisjunctionExpression de: e.getExpressions()) {
                for (SimpleExpression se: de.getExpressions()) {
                    PostingItem item;
                    if (se.getOp() == OP.IN || se.getOp() == OP.EQUAL)
                        item = new PostingItem(e.getId(), PostingItem.PostingOperator.IN, disjId);
                    else if (se.getOp() == OP.NOTIN)
                        item = new PostingItem(e.getId(), PostingItem.PostingOperator.NOTIN, disjId);
                    else continue;
                    // genkey
                    for (int v : se.getRight()) {
                        AttrValueKey key = new AttrValueKey(se.getLeft(), v);
                        PostingList postingList = workingMap.get(key);
                        if (postingList == null) {
                            postingList = new PostingList(new ArrayList<PostingItem>(Arrays.asList(item)));
                            workingMap.put(key, postingList);
                        } else {
                            postingList.add(item);
                        }
                    }
                }
                disjId ++;
            }

            if (k == 0) {
                // add special Z
                PostingList postingList = workingMap.get("Z");
                PostingItem item = new PostingItem(e.getId(), PostingItem.PostingOperator.IN, disjId);
                if (postingList == null) {
                    item.setDisjunctionId(-1);
                    postingList = new PostingList(new ArrayList<>(Arrays.asList(item)));
                    workingMap.put(new AttrValueKey("Z",-1), postingList);
                } else {
                    postingList.add(item);
                }
            }
        }
        return new InvertedList(map);
    }
}
