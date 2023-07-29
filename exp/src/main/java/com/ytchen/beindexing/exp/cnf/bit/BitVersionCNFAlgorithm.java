package com.ytchen.beindexing.exp.cnf.bit;

import com.ytchen.beindexing.exp.cnf.AttrValueKey;
import com.ytchen.beindexing.exp.cnf.CNFAlgorithm;
import com.ytchen.beindexing.exp.cnf.ICNFEvaluator;
import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.Reader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.*;

public class BitVersionCNFAlgorithm implements ICNFEvaluator {

    private static Logger LOG = LogManager.getLogger(BitVersionCNFAlgorithm.class);

    BitVersionInvertedList<AttrValueKey> invertedList;

    Map<Integer, Integer> queryMap;

    public BitVersionCNFAlgorithm(BitVersionInvertedList<AttrValueKey> invertedList, Map<Integer, Integer> queryMap) {
        this.invertedList = invertedList;
        this.queryMap = queryMap;
    }

    @Override
    public List<Integer> evaluate(List<Tuple2<String, Integer>> assignment) {
        List<Integer> result = new ArrayList<>();
        int K = invertedList.getMaxK();
        for (int l =K; l >= 0; l--) {
            LOG.debug("l={}", l);
            Map<AttrValueKey, BitVersionPostingList>  workingMap = invertedList.getPostingList(l);
            if (workingMap == null) continue;

            List<BitVersionPostingList> workingList = new ArrayList<>();
            // obtain related posting lists.
            for (Tuple2<String, Integer> pair: assignment) {
                AttrValueKey key = new AttrValueKey(pair.get_1(), pair.get_2());
                BitVersionPostingList pl = workingMap.get(key);
                if (pl!= null) {
                    if (LOG.isDebugEnabled()) {
                        pl.setOriginKey(key.toString());
                    }
                    workingList.add(pl);
                }
            }
            // add special Z
//            PostingList specialZ = workingMap.get("Z");
//            workingList.add(specialZ);

            int k = l > 0 ? l : 1;

            if (LOG.isDebugEnabled()) {
                // TODO describe the posting list.
                LOG.debug("current workingList for k={}", k);
                workingList.stream().map(i -> i.toString()).forEach(LOG::debug);
            }

            // split the working list.
            List<BitVersionPostingList> pList = new ArrayList<>();
            for (BitVersionPostingList pl : workingList) {
                pList.addAll(pl.split());
            }

//            System.out.println("working list size:" + workingList.size());

            // reset the pointers.
            pList.forEach(i -> i.reset());

            // continue if not enough items.
            if (pList.size() < k) continue;

            while(pList.size() > k-1) {
                // sort according to current pointing value.
                Collections.sort(pList, (x1, x2) -> x1.getCurrent().compareTo(x2.getCurrent()));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("sorted=>");
                    pList.stream().map(i -> i.getOriginKey()+"=>"+i.toString()).forEach(LOG::debug);
                    LOG.debug("<= end");
                }
                int currentFirstId = pList.get(0).getCurrent().queryId;
                int nextId = -1;
                if (result.contains(currentFirstId)) nextId = currentFirstId +1;
                else if (currentFirstId == pList.get(k -1).getCurrent().queryId) {
                    int evalValue = 0;

                    for (BitVersionPostingList list1: pList) {
                        if (list1.getCurrent().queryId == currentFirstId) {
                            evalValue |= list1.getCurrent().code;
                        } else {
                            break;
                        }
                    }
                    // get queryMap
                    int expected = queryMap.get(currentFirstId);
                    boolean satisfied = expected == evalValue;
                    LOG.debug("queryId: {}, expected: {}, evalValue: {}", currentFirstId, expected, evalValue);
                    if (satisfied) {
                        // add to result.
                        result.add(currentFirstId);
                    }
                    nextId = pList.get(k-1).getCurrent().queryId + 1;
                } else {
                    nextId = pList.get(k-1).getCurrent().queryId;
                }
                // skip to next id.
                for (int i = pList.size() - 1; i >= 0; i--) {
                    BitVersionPostingList list2 = pList.get(i);
                    LOG.debug("#{} skip to {}", list2.getOriginKey(), nextId);
                    list2.skipToNext(nextId);
                    if (!list2.hasNext()) {
                        // remove it.
                        pList.remove(i);
                    }
                }
                if (LOG.isDebugEnabled()) {
                    // log current status.
                    LOG.debug(" after iteration =========");
                    pList.stream().map(i -> i.getOriginKey()+"=>"+i.toString()).forEach(LOG::debug);
                    LOG.debug("current result : {}", result);
//                    System.exit(-1);
                }
            }
        }
        return result;
    }

    public static BitVersionCNFAlgorithm fromFile(String file) throws FileNotFoundException {
        List<CNFExpression> cnfExpressionList = Reader.readCNFExpressions(file);
        BitVersionInvertedList<AttrValueKey> invertedList = BitVersionInvertedList.fromCNFExpressions(cnfExpressionList);
        Map<Integer, Integer> counterMap = new HashMap<>();
        for (CNFExpression cnfExpression: cnfExpressionList) {
            counterMap.put(cnfExpression.getId(), cnfExpression.computeCode());
        }
        return new BitVersionCNFAlgorithm(invertedList, counterMap);
    }
}
