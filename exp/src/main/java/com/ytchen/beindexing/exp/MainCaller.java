package com.ytchen.beindexing.exp;

import java.io.IOException;

public class MainCaller {
    public static void main(String[] args) throws IOException {

        String testFile = "./datagen/new2/MVI_40751-occ-3.txt";
        String frameNum = "1145";
        Main.main(new String[]{
                "StateGraph", "SSG",
                testFile,
                "300", "240", frameNum,"1", "1"
        });
        Main.main(new String[]{
                "StateGraph", "MFS",
                testFile,
                "300", "240", frameNum,"1", "1"
        });
    }
}
