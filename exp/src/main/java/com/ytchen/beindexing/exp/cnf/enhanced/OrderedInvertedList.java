package com.ytchen.beindexing.exp.cnf.enhanced;

import com.ytchen.beindexing.exp.cnf.PostingItem;
import com.ytchen.beindexing.exp.cnf.PostingList;
import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.DisjunctionExpression;
import com.ytchen.beindexing.exp.expression.OP;
import com.ytchen.beindexing.exp.expression.SimpleExpression;
import com.ytchen.beindexing.exp.utils.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ytchen.beindexing.exp.utils.ToStringUtils.center;
import static com.ytchen.beindexing.exp.utils.ToStringUtils.repeat;

public class OrderedInvertedList {
    Map<Integer, Map<String, List<Tuple2<Integer, PostingList>>>> map;
    int maxK = 0;

    public OrderedInvertedList(Map<Integer, Map<String, List<Tuple2<Integer, PostingList>>>> map) {
        this.map = map;
        for (int k : map.keySet()) {
            if (k > maxK) maxK = k;
        }
    }

    public int getMaxK() {
        return maxK;
    }

    public Map<String, List<Tuple2<Integer, PostingList>>> getPostingList(int l) {
        return map.get(l);
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
                int currentMax = map.get(k).keySet().stream().mapToInt(i -> i.length()).max().getAsInt();
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

                Map<String, List<Tuple2<Integer, PostingList>>> postingListMap = map.get(k);
                // compute height
                int height = postingListMap.size();
                int kPosition = height / 2 ;

                List<String> keySorted = new ArrayList<>(postingListMap.keySet());
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
                    sb.append(center(keySorted.get(i), keySize)).append("|");

                    // posting list
                    sb.append(" ").append(postingListMap.get(keySorted.get(i))).append("\n");
                }

            }
            sb.append(repeat("-",totalLength)).append("\n");
        }
        return sb.toString();
    }

    /**
     * build an ordered inverted list based on the expressions.
     * Will only parse expression with `>=` and ignore others.
     * @param expressions
     * @return
     */
    public static OrderedInvertedList fromCNFExpressions(List<CNFExpression> expressions) {
        Map<Integer, Map<String, List<Tuple2<Integer, PostingList>>>> map = new HashMap<>();
        for (CNFExpression e : expressions) {
            // extract k
            int k = e.computeK();
            Map<String, List<Tuple2<Integer, PostingList>>> workingMap = map.getOrDefault(k, new HashMap<>());
            if (!map.containsKey(k)) map.put(k, workingMap);
            // index
            int disjId =0;
            for (DisjunctionExpression de: e.getExpressions()) {
                for (SimpleExpression se: de.getExpressions()) {
                    PostingItem item;
                    if (se.getOp() == OP.GE)
                        item = new PostingItem(e.getId(), PostingItem.PostingOperator.IN, disjId);
                    else
                        continue;
//                        item = new PostingItem(e.getId(), PostingItem.PostingOperator.NOTIN, disjId);
                    // genkey
                    for (int v : se.getRight()) {
                        List<Tuple2<Integer, PostingList>> tupleList = workingMap.get(se.getLeft());
                        if (tupleList == null) {
                            tupleList = new ArrayList<>();
                            workingMap.put(se.getLeft(), tupleList);
                        }
                        // search the list with given value.
                        Tuple2<Integer, PostingList> tuple2 = ListUtils.
                                searchList(tupleList, new Tuple2(v, null), Comparator.comparing(x -> x.get_1()));
                        if (tuple2 == null) {
                            // create one.
                            tuple2 = new Tuple2<>(v, new PostingList(new ArrayList<>(Arrays.asList(item))));
                            tupleList.add(tuple2);
                        } else {
                            tuple2.get_2().add(item);
                        }
                    }
                }
                disjId ++;
            }

            if (k == 0) {
                // add special Z, 0
                List<Tuple2<Integer, PostingList>> postingList = workingMap.get("Z");
                PostingItem item = new PostingItem(e.getId(), PostingItem.PostingOperator.IN, disjId);
                if (postingList == null) {
                    workingMap.put("Z", new ArrayList<>(Arrays.asList(
                            new Tuple2<>(0, new PostingList(new ArrayList<>(Arrays.asList(item))) )
                    )));
                    item.setDisjunctionId(-1);
                    workingMap.put("Z", postingList);
                } else {
                    postingList.get(0).get_2().add(item);
                }
            }
        }
        // give them a final sort.
        map.values().forEach(x -> x.values().forEach(y -> y.sort(Comparator.comparing(z -> z.get_1()))));
        return new OrderedInvertedList(map);
    }
}
