package com.ytchen.beindexing.exp.utils;

public class ControlUtils {
    public static void loop(int times, Runnable runnable) {
        for (int i =0; i < times; i++) {
            runnable.run();
        }
    }

    public static void measureAverage(int times, Runnable runnable, Runnable teardown) {
        Ticker ticker = new Ticker();
        for (int i=0; i < times; i++) {
            ticker.startTick();
            runnable.run();
            ticker.stopTick();
            teardown.run();
        }
        System.out.println("average time: " + (ticker.getTotal()/ times) +"ms" );
    }
}
