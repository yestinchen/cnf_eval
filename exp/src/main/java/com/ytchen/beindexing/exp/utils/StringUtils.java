package com.ytchen.beindexing.exp.utils;

import java.util.Collection;
import java.util.List;

public class StringUtils {

    public static String strip(String s) {
        s = s.trim();
        if (s.startsWith("("))
            return s.substring(1, s.length() - 1);
        if (s.startsWith("<"))
            return s.substring(1, s.length() - 1);
        if (s.startsWith("{"))
            return s.substring(1, s.length() - 1);
        return s;
    }

    public static String toStringWithBrackets(List c) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i =0; i < c.size(); i++) {
            sb.append(c.get(i));
            if (i != c.size() -1 ) sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    public static String join(Collection c, String str) {
        if (c == null) return null;
        if (c.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (Object o : c){
            sb.append(o).append(str);
        }
        if (sb.charAt(sb.length()-1) == str.charAt(str.length() -1)) {
            sb.delete(sb.length() - str.length(), sb.length());
        }
        return sb.toString();
    }
}
