package com.ytchen.beindexing.exp.common;

import java.util.Iterator;
import java.util.List;

public class PointingList<T> implements Iterable<T>{
    List<T> list;
    int currentIndex = 0;

    public PointingList(List<T> list) {
        this.list = list;
        if (list.size() > 0) currentIndex = 0;
    }

    public PointingList(List<T> list, int currentIndex) {
        this.list = list;
        this.currentIndex = currentIndex;
    }

    public void add(T t) {
        list.add(t);
    }

    public T getCurrent() {
        if (currentIndex >= list.size()) return null;
        return list.get(currentIndex);
    }

    public void next() {
        currentIndex ++;
    }

    public void reset() {
        currentIndex = 0;
    }

    public boolean hasNext() {
        return currentIndex < list.size();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i =0; i < list.size(); i++) {
            if (currentIndex == i) sb.append("*");
            sb.append(list.get(i).toString());
            sb.append(",");
        }
        if (sb.charAt(sb.length() - 1) == ',') sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
    public int size() {
        return list.size();
    }
    public T get(int i) {
        return list.get(i);
    }
}
