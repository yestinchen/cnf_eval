package com.ytchen.beindexing.exp.cnf;

import java.util.Objects;

public class AttrValueKey implements Comparable<AttrValueKey>{

    public String attr;
    public int value;

    public AttrValueKey(String attr, int value) {
        this.attr = attr;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttrValueKey that = (AttrValueKey) o;
        return value == that.value &&
                Objects.equals(attr, that.attr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attr, value);
    }

    @Override
    public String toString() {
        return "("+attr+","+value+")";
    }

    @Override
    public int compareTo(AttrValueKey o) {
        int result= attr.compareTo(o.attr) ;
        if (result == 0) {
            return Integer.compare(value, o.value);
        }
        return result;
    }
}
