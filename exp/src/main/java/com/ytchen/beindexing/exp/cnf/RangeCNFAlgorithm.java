package com.ytchen.beindexing.exp.cnf;

import com.ytchen.beindexing.exp.cnf.enhanced.OrderedInvertedList;
import com.ytchen.beindexing.exp.common.Tuple2;
import com.ytchen.beindexing.exp.expression.CNFExpression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Range CNF Algorithm, performs a full range evaluation.
 */
public class RangeCNFAlgorithm implements ICNFRangeEvaluator {

    private Logger LOG = LogManager.getLogger(RangeCNFAlgorithm.class);

    OrderedInvertedList existInvertedList;

    Map<Integer, int[]> counterMap;

    public RangeCNFAlgorithm(OrderedInvertedList existInvertedList,
                             Map<Integer, int[]> counterMap) {
        this.existInvertedList = existInvertedList;
        this.counterMap = counterMap;
    }

    private List<PostingList> obtainWorkingList(List<Tuple2<String, Integer>> assignment,
                                                Map<String, List<Tuple2<Integer, PostingList>>> workingMap) {
        if (workingMap == null) return null;
        List<PostingList> workingList = new ArrayList<>();
        for (Tuple2<String, Integer> pair: assignment) {
            List<Tuple2<Integer, PostingList>> value = workingMap.get(pair.get_1());
            if (value != null) {
                // e.g. a >=3, given 4. from 1~4
                int i =0;
                while(i < value.size() && value.get(i).get_1() <= pair.get_2()) {
                    // add current.
                    workingList.add(value.get(i).get_2());
                    i ++;
                }
            }
        }
        return workingList;
    }

    public ICNFRangeEvaluator.EvalResult evaluate(List<Tuple2<String, Integer>> assignment) {
        List<Integer> result = new ArrayList<>();
        Map<Integer, Integer> resultMap = new HashMap<>();

        int K = existInvertedList.getMaxK();

        for (int l = K; l >=0; l --) {
            LOG.debug("l = {}", l);
            Map<String, List<Tuple2<Integer, PostingList>>> workingMap = existInvertedList.getPostingList(l);
            // obtain working list.
            List<PostingList> workingList = obtainWorkingList(assignment, workingMap);
            if (workingList == null) {
                continue;
            }

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

            pList.forEach(i -> i.reset());
            if (pList.size() < k) continue;
            while(pList.size() > k -1) {
                // sort according to current pointing value.
                Collections.sort(pList, (x1, x2) -> x1.getCurrent().compareTo(x2.getCurrent()));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("sorted=>");
                    pList.stream().map(i -> i.getOriginKey()+"=>"+i.toString()).forEach(LOG::debug);
                    LOG.debug("<= end");
                }
                int currentFirstId = pList.get(0).getCurrent().getQueryId();
                int nextId = -1;
                if (result.contains(currentFirstId)) nextId = currentFirstId +1;
                else if (currentFirstId == pList.get(k -1).getCurrent().getQueryId()) {
                    // init counters.
                    int[] counterArr = counterMap.get(currentFirstId);
                    int[] counter = Arrays.copyOf(counterArr, counterArr.length);
                    for (PostingList list1: pList) {
                        if (list1.getCurrent().getQueryId() == currentFirstId) {
                            if (list1.getCurrent().getDisjunctionId() == -1) {
                                // ignore it.
                                continue;
                            } else if (list1.getCurrent().getOp() == PostingItem.PostingOperator.NOTIN) {
                                counter[list1.getCurrent().getDisjunctionId()] ++;
                            } else {
                                counter[list1.getCurrent().getDisjunctionId()] ++;
                            }
                        } else {
                            break;
                        }
                    }
                    boolean satisfied = true;
                    int count = 0;
                    for(int cv: counter) {
                        if (cv == 0) satisfied = false;
                        else {
                            count +=cv;
                        }
                    }
                    if (satisfied) {
                        // add to result.
                        result.add(currentFirstId);
                        count -= counter.length;
                        // result map keeps how many conditions to lose to fail the query.
                        resultMap.put(currentFirstId, count);
                    }
                    nextId = pList.get(k-1).getCurrent().getQueryId() + 1;
                } else {
                    nextId = pList.get(k-1).getCurrent().getQueryId();
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
                    pList.stream().map(i -> i.getOriginKey()+"=>"+i.toString()).forEach(LOG::debug);
                    LOG.debug("current result : {}", result);
//                    System.exit(-1);
                }
            }
        }
        ICNFRangeEvaluator.EvalResult evalResult = new ICNFRangeEvaluator.EvalResult();
        evalResult.setCountMap(resultMap);
        evalResult.setResult(result);
        return evalResult;
    }

    public static RangeCNFAlgorithm fromCNFExpressions(List<CNFExpression> cnfExpressionList) throws FileNotFoundException {
        OrderedInvertedList orderedInvertedList = OrderedInvertedList.fromCNFExpressions(cnfExpressionList);

        Map<Integer, int[]> counterMap = new HashMap<>();
        for (CNFExpression cnfExpression: cnfExpressionList) {
            counterMap.put(cnfExpression.getId(), cnfExpression.computeCounters());
        }
        return new RangeCNFAlgorithm(orderedInvertedList, counterMap);
    }
}
