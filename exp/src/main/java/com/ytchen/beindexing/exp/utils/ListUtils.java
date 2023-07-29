package com.ytchen.beindexing.exp.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListUtils {

    public static <T> List<T> difference(List<T> list1, List<T> list2, Comparator<T> comparator) {
        // list1 > list2.
        List<T> result = new ArrayList<>();
        int i=0, j =0;
        while (i < list1.size() && j < list2.size()) {
            int compResult = comparator.compare(list1.get(i), list2.get(j));
            if (compResult == 0) {
                j ++; i++;
            } else if (compResult > 0) {
                j ++;
            } else {
                i ++;
                result.add(list1.get(i));
            }
        }
        return result;
    }

    /**
     * linear scan
     * @param <T>
     * @return
     */
    public static <T> T searchList(List<T> list, T someObj, Comparator<T> comparator) {
        for (T i : list) {
            if (comparator.compare(i, someObj) == 0) return i;
        }
        return null;
    }

    /**
     * returns -1 if every element < given one
     * @param list
     * @param value
     * @param comparator
     * @param <T>
     * @return
     */
    public static <T> int searchIndexFromList(List<T> list, T value, Comparator<T> comparator) {
        for (int i =0; i < list.size(); i++) {
            T current = list.get(i);
            if (comparator.compare(current, value) == 0) return i;
            else if (comparator.compare(current, value) > 0) return i-1;
        }
        return -1;
    }

    public static <T> boolean isTheSame(List<T> list1, List<T> list2) {
        if (list1.size() == list2.size()) {
            for (int i=0; i < list1.size(); i++) {
                if (!list1.get(i).equals(list2.get(i))) return false;
            }
            return true;
        }
        return false;
    }

    public static <T> T insertToSortedList(List<T> list, T newObj, Comparator<T> comparator, boolean replaceSameValue) {
        if (list.size() == 0) {
            list.add(newObj);
            return null;
        }
        // list should be sorted before hand.
        int startPos = 0, stopPos = list.size() - 1;
        int checkPos = (startPos + stopPos) / 2;
        int compareResult = comparator.compare(list.get(checkPos), newObj);
        while(compareResult != 0) {
            if (compareResult > 0) {
                stopPos = checkPos;
                int newCheckPos = (startPos + stopPos) /2;
                if (newCheckPos >= checkPos) break;
                checkPos = newCheckPos;
            } else {
                startPos = checkPos;
                int newCheckPos = (startPos + stopPos) /2;
                if (newCheckPos <= checkPos) break;
                checkPos = newCheckPos;
            }
            compareResult = comparator.compare(list.get(checkPos), newObj);
        }
        int upperBound = list.size() -1, lowerBound = 0;
        if (replaceSameValue) {
            if (compareResult < 0) {
                // means they equal.
                while (checkPos <= upperBound && comparator.compare(list.get(checkPos), newObj) < 0) {
                    checkPos++;
                }
            } else if (compareResult > 0){
                while (checkPos >= lowerBound && comparator.compare(list.get(checkPos), newObj) >= 0) {
                    checkPos--;
                }
            }
            if (checkPos > upperBound) {
                list.add(newObj);
            } else {
                if (checkPos < 0) checkPos = 0;
                if (comparator.compare(list.get(checkPos), newObj) == 0) {
                    T obj = list.get(checkPos);
                    list.set(checkPos, newObj);
                    return obj;
                } else {
                    list.add(checkPos, newObj);
                }
            }
        } else {
            if (compareResult <= 0) {
                // means they equal.
                while (checkPos <= upperBound && comparator.compare(list.get(checkPos), newObj) <= 0) {
                    checkPos++;
                }
                if (checkPos > upperBound) {
                    list.add(newObj);
                } else {
                    list.add(checkPos, newObj);
                }
            } else {
                while (checkPos >= lowerBound && comparator.compare(list.get(checkPos), newObj) > 0) {
                    checkPos--;
                }
                if (checkPos < 0) checkPos = 0;
                list.add(checkPos, newObj);
            }
        }
        return null;
    }
}
