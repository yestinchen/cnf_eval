package com.ytchen.beindexing.exp.common;

public class KeyGen {

    public static String genKey(Tuple2 tuple) {
        return tuple._1+"#"+tuple._2;
    }

    public static String genKey(String attr, int val) {
        return attr+"#"+val;
    }
}
