package com.ytchen.beindexing.exp.cnf.bit;

import com.ytchen.beindexing.exp.cnf.AttrValueKey;
import com.ytchen.beindexing.exp.cnf.InvertedList;
import com.ytchen.beindexing.exp.cnf.PostingItem;
import com.ytchen.beindexing.exp.cnf.PostingList;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.DisjunctionExpression;
import com.ytchen.beindexing.exp.expression.OP;
import com.ytchen.beindexing.exp.expression.SimpleExpression;

import java.util.*;

import static com.ytchen.beindexing.exp.utils.ToStringUtils.center;
import static com.ytchen.beindexing.exp.utils.ToStringUtils.repeat;

public class BitVersionInvertedList<T extends  Comparable<T>> {

    Map<Integer, Map<T, BitVersionPostingList>> map;
    int maxK =0;

    public BitVersionInvertedList(Map<Integer, Map<T, BitVersionPostingList>> map) {
        this.map = map;
        for (int k : map.keySet()) {
            if (k > maxK) maxK = k;
        }
    }

    public int getMaxK() {
        return maxK;
    }

    public Map<T, BitVersionPostingList> getPostingList(int k) {
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

                Map<T, BitVersionPostingList> postingListMap = map.get(k);
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

    private static int genCode(int disjId) {
        return 1 << disjId;
    }

    public static BitVersionInvertedList<AttrValueKey> fromCNFExpressions(List<CNFExpression> cnfExpressions) {
        Map<Integer, Map<AttrValueKey, BitVersionPostingList>> map = new HashMap<>();
        for (CNFExpression e : cnfExpressions) {
            int k = e.computeK();
            Map<AttrValueKey, BitVersionPostingList> workingMap = map.getOrDefault(k, new HashMap<>());
            if (!map.containsKey(k)) map.put(k, workingMap);

            // index.
            int disjId = 0;
            for (DisjunctionExpression de : e.getExpressions()) {
                for (SimpleExpression se : de.getExpressions()) {
                    BitVersionPostingItem item;
                    if (se.getOp() == OP.IN || se.getOp() == OP.EQUAL) {
                        item = new BitVersionPostingItem(e.getId(), genCode(disjId));
                    } else {
                        System.err.println("can only accept = or in ops");
                        return null;
                    }
                    for (int v : se.getRight()) {
                        AttrValueKey key = new AttrValueKey(se.getLeft(), v);
                        BitVersionPostingList postingList = workingMap.get(key);
                        if (postingList == null) {
                            postingList = new BitVersionPostingList(new ArrayList<>(Arrays.asList(item)));
                            workingMap.put(key, postingList);
                        } else {
                            postingList.add(item);
                        }
                    }
                }
                disjId ++;
            }

            // FIXME: will skip adding special Z here, since we are not supporting not(!=) semantics
        }

        // overlap percentage.
        // we need to get the signature. order does not matter.
        // TODO.
        return new BitVersionInvertedList<>(map);
    }

}
