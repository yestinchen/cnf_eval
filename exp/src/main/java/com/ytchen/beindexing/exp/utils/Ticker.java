package com.ytchen.beindexing.exp.utils;

public class Ticker {

    long total;
    long startTime;

    public void startTick(){
        startTime = System.currentTimeMillis();
    }

    public void stopTick() {
        total += (System.currentTimeMillis() - startTime);
    }

    public long getTotal() {
        return total;
    }
}
