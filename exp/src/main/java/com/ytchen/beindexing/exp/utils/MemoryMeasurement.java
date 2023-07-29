package com.ytchen.beindexing.exp.utils;

import java.util.ArrayList;
import java.util.List;

public class MemoryMeasurement {
    List<Long> values;
    long base = 0;

    public MemoryMeasurement() {
        values = new ArrayList<>();
//        base = getUsedMemory();
    }

    public void measure() {
        values.add(getUsedMemory() - base);
    }

    private long getUsedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public List<Long> getValues() {
        return values;
    }

    public static MemoryMeasurement merge(List<MemoryMeasurement> list) {
        MemoryMeasurement memoryMeasurement = new MemoryMeasurement();
        for (int i=0; i < list.get(0).values.size(); i++) {
            long newValue = 0;
            for (int j = 0; j < list.size(); j++) {
                newValue += list.get(j).getValues().get(i);
            }
            newValue /= list.size();
            memoryMeasurement.values.add(newValue);
        }
        return memoryMeasurement;
    }
}
