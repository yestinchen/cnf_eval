package com.ytchen.beindexing.exp.cnf.enhanced;

import com.ytchen.beindexing.exp.cnf.*;
import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.Reader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.*;

import static com.ytchen.beindexing.exp.utils.ListUtils.isTheSame;


public class EnhancedBatchCNFAlgorithm implements ICNFBatchEvaluator {

    private Logger LOG = LogManager.getLogger(EnhancedBatchCNFAlgorithm.class);

    InvertedList exactlyInvertedList;
    OrderedInvertedList  existInvertedList;

    Map<Integer, int[]> counterMap;

    public EnhancedBatchCNFAlgorithm(InvertedList exactlyInvertedList,
                                     OrderedInvertedList existInvertedList,
                                     Map<Integer, int[]> counterMap) {
        this.exactlyInvertedList = exactlyInvertedList;
        this.existInvertedList = existInvertedList;
        this.counterMap = counterMap;
    }

    public List<Integer> evaluateBatch(List<List<Tuple2<String, Integer>>> assignments) {
        List<Integer> result = new ArrayList<>();
        int K = Math.max(existInvertedList.getMaxK(), exactlyInvertedList.getMaxK()); // get the max K.

        // 1. gen working list.
        List<Map<Integer, List<PostingList>>> workingListMaps = new ArrayList<>();
        List<List<Integer>> ids = new ArrayList<>();
        for (List<Tuple2<String, Integer>> assignment: assignments) {
            Map<Integer, List<PostingList>> workingMap = new HashMap<>();
            List<Integer> thisIds = new ArrayList<>();
            for (int l =K; l >= 0; l--) {
                LOG.debug("l={}", l);
                // two maps.
                Map<String, List<Tuple2<Integer, PostingList>>> workingExistMap =
                        existInvertedList.getPostingList(l);
                Map<String, PostingList> workingExactlyMap = exactlyInvertedList.getPostingList(l);
                if (workingExactlyMap == null && workingExistMap == null) continue;

                List<PostingList> workingList = new ArrayList<>();
                // obtain related posting lists.
                for (Tuple2<String, Integer> pair : assignment) {
                    // add exactly list.
                    if (workingExactlyMap != null && workingExactlyMap.size() != 0) {
                        AttrValueKey key = new AttrValueKey(pair.get_1(), pair.get_2());
                        PostingList pl = workingExactlyMap.get(key);
                        if (pl != null) {
                            if (LOG.isDebugEnabled()) {
                                pl.setOriginKey(key.toString());
                            }
                            workingList.add(pl);
                            thisIds.add(pl.getId());
                        }
                    }
                    // add exist list.
                    if (workingExistMap != null && workingExistMap.size() != 0) {
                        // get key.
                        List<Tuple2<Integer, PostingList>> value = workingExistMap.get(pair.get_1());
                        if (value != null) {
                            // e.g. a >=3, given 4. from 1~4
                            int i = 0;
                            while (i < value.size() && value.get(i).get_1() <= pair.get_2()) {
                                // add current.
                                workingList.add(value.get(i).get_2());
                                thisIds.add(value.get(i).get_2().getId());
                                i++;
                            }
                        }
                    }
                }
                workingMap.put(l, workingList);
            }
            // check if exists.
            // gather workingList to ids.
//            Collections.sort(thisIds);
            if (ids.stream().filter(i -> isTheSame(i, thisIds)).count() == 0) {
                // add
                ids.add(thisIds);
                workingListMaps.add(workingMap);
            }
        }

//        System.out.println("after pruning: " + workingLists.size() +", " + assignments.size());
            // add special Z
//            PostingList specialZ = workingMap.get("Z");
//            workingList.add(specialZ);
        // for each working list, evaluate.
        for (Map<Integer, List<PostingList>> workingMap: workingListMaps) {
            for (int l =K; l >= 0; l--) {
                int k = l > 0 ? l : 1;
                List<PostingList> workingList = workingMap.get(l);
                if (workingList == null) continue;

                if (LOG.isDebugEnabled()) {
                    // TODO describe the posting list.
                    LOG.debug("current workingList for k={}", k);
                    workingList.stream().map(i -> i.toString()).forEach(LOG::debug);
                }

                // split the working list.
                List<PostingList> pList = new ArrayList<>();
                for (PostingList pl : workingList) {
                    pList.addAll(pl.split());
                }

//                System.out.println("working list size:" + workingList.size());

                // reset the pointers.
                pList.forEach(i -> i.reset());

                // continue if not enough items.
                if (pList.size() < k) continue;

                while (pList.size() > k - 1) {
                    // sort according to current pointing value.
                    Collections.sort(pList, (x1, x2) -> x1.getCurrent().compareTo(x2.getCurrent()));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("sorted=>");
                        pList.stream().map(i -> i.getOriginKey() + "=>" + i.toString()).forEach(LOG::debug);
                        LOG.debug("<= end");
                    }
                    int currentFirstId = pList.get(0).getCurrent().getQueryId();
                    int nextId = -1;
                    if (result.contains(currentFirstId)) nextId = currentFirstId + 1;
                    else if (currentFirstId == pList.get(k - 1).getCurrent().getQueryId()) {
                        // init counters.
                        int[] counterArr = counterMap.get(currentFirstId);
                        int[] counter = Arrays.copyOf(counterArr, counterArr.length);
                        for (PostingList list1 : pList) {
                            if (list1.getCurrent().getQueryId() == currentFirstId) {
                                if (list1.getCurrent().getDisjunctionId() == -1) {
                                    // ignore it.
                                    continue;
                                } else if (list1.getCurrent().getOp() == PostingItem.PostingOperator.NOTIN) {
                                    counter[list1.getCurrent().getDisjunctionId()]++;
                                } else {
                                    counter[list1.getCurrent().getDisjunctionId()] = 1;
                                }
                            } else {
                                break;
                            }
                        }
                        boolean satisfied = true;
                        for (int cv : counter) {
                            if (cv == 0) satisfied = false;
                        }
                        if (satisfied) {
                            // add to result.
                            result.add(currentFirstId);
                        }
                        nextId = pList.get(k - 1).getCurrent().getQueryId() + 1;
                    } else {
                        nextId = pList.get(k - 1).getCurrent().getQueryId();
                    }
                    // skip to next id.
                    for (int i = pList.size() - 1; i >= 0; i--) {
                        PostingList list2 = pList.get(i);
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
                        pList.stream().map(i -> i.getOriginKey() + "=>" + i.toString()).forEach(LOG::debug);
                        LOG.debug("current result : {}", result);
//                    System.exit(-1);
                    }
                }
            }
        }
        return result;
    }


    public static EnhancedBatchCNFAlgorithm fromFile(String file) throws FileNotFoundException {
        List<CNFExpression> cnfExpressionList = Reader.readCNFExpressions(file);
        InvertedList invertedList = InvertedList.fromCNFExpressions(cnfExpressionList);
        OrderedInvertedList orderedInvertedList = OrderedInvertedList.fromCNFExpressions(cnfExpressionList);

        Map<Integer, int[]> counterMap = new HashMap<>();
        for (CNFExpression cnfExpression: cnfExpressionList) {
            counterMap.put(cnfExpression.getId(), cnfExpression.computeCounters());
        }
        return new EnhancedBatchCNFAlgorithm(invertedList, orderedInvertedList, counterMap);
    }
}
