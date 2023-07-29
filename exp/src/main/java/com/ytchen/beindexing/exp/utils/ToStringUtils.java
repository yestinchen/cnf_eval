package com.ytchen.beindexing.exp.utils;

public class ToStringUtils {
    public static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n;i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String center(String s, int width) {
        int length = s.length();
        if (length >= width) return s;
        int leftSpace = (width - length) / 2;
        int rightSpace = width - leftSpace - length;
        return repeat(" ", leftSpace)+ s + repeat(" ", rightSpace);
    }
}
