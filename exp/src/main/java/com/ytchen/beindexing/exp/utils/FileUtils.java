package com.ytchen.beindexing.exp.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static List<String> readLines(String file) throws IOException {
        List<String> lines = new ArrayList<>();
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }
}
