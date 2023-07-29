package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import com.ytchen.beindexing.exp.expression.Reader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static com.ytchen.beindexing.exp.utils.ListUtils.isTheSame;

public class CNFBatchAlgorithm implements ICNFBatchEvaluator {

    private Logger LOG = LogManager.getLogger(CNFBatchAlgorithm.class);

    InvertedList invertedList;
    Map<Integer, int[]> counterMap;

    public CNFBatchAlgorithm(InvertedList postingList, Map<Integer, int[]> counterMap) {
        this.invertedList = postingList;
        this.counterMap = counterMap;
    }

    /**
     * return ids that evaluated as true.
     * @param assignments
     * @return
     */
    public List<Integer> evaluateBatch(List<List<Tuple2<String,Integer>>> assignments){
        List<Integer> result = new ArrayList<>();
        int K = invertedList.getMaxK();
        List<List<Integer>> ids = new ArrayList<>();
        List<List<PostingList>> workingLists = new ArrayList<>();

        for (List<Tuple2<String, Integer>> assignment: assignments) {
            for (int l = K; l >= 0; l--) {
                LOG.debug("l={}", l);
                Map<String, PostingList> workingMap = invertedList.getPostingList(l);
                if (workingMap == null) continue;

                List<PostingList> workingList = new ArrayList<>();
                // obtain related posting lists.
                for (Tuple2<String, Integer> pair : assignment) {
                    AttrValueKey key = new AttrValueKey(pair.get_1(), pair.get_2());
                    PostingList pl = workingMap.get(key);
                    if (pl != null) {
                        if (LOG.isDebugEnabled()) {
                            pl.setOriginKey(key.toString());
                        }
                        workingList.add(pl);
                    }
                }

                // check if exists.
                // gather workingList to ids.
                List<Integer> thisIds =
                        workingList.stream().map(i -> i.getId()).collect(Collectors.toList());
                Collections.sort(thisIds);
                if (ids.stream().filter(i -> isTheSame(i, thisIds)).count() == 0) {
                    // add
                    ids.add(thisIds);
                    workingLists.add(workingList);
                }
            }
        }
        // distinct.

            // add special Z
//            PostingList specialZ = workingMap.get("Z");
//            workingList.add(specialZ);
        for (List<PostingList> workingList: workingLists) {
            for (int l =K; l >= 0; l--) {
                int k = l > 0 ? l : 1;

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

//            System.out.println("working list size:" + workingList.size());

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
                    int currentFirstId = pList.get(0).getCurrent().queryId;
                    int nextId = -1;
                    if (result.contains(currentFirstId)) nextId = currentFirstId + 1;
                    else if (currentFirstId == pList.get(k - 1).getCurrent().queryId) {
                        // init counters.
                        int[] counterArr = counterMap.get(currentFirstId);
                        int[] counter = Arrays.copyOf(counterArr, counterArr.length);
                        for (PostingList list1 : pList) {
                            if (list1.getCurrent().queryId == currentFirstId) {
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
                        nextId = pList.get(k - 1).getCurrent().queryId + 1;
                    } else {
                        nextId = pList.get(k - 1).getCurrent().queryId;
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

    public static CNFBatchAlgorithm fromFile(String file) throws FileNotFoundException {
        List<CNFExpression> cnfExpressionList = Reader.readCNFExpressions(file);
        InvertedList invertedList = InvertedList.fromCNFExpressions(cnfExpressionList);
        Map<Integer, int[]> counterMap = new HashMap<>();
        for (CNFExpression cnfExpression: cnfExpressionList) {
            counterMap.put(cnfExpression.getId(), cnfExpression.computeCounters());
        }
        return new CNFBatchAlgorithm(invertedList, counterMap);
    }
}
