package com.ytchen.beindexing.exp;

import java.io.IOException;

public class MainEvalCaller {
    public static void main(String[] args) throws IOException {

        String expFile = "../java/datagen/exp/attr-ge-107-2-2-100.cnf";
        String testFile = "../java/datagen/new/stmarc.txt";
        String frameNum = "700";
        Main.main(new String[]{
                "PAPER_EVAL", "SSG_OEVAL",
                expFile,
                testFile,
                "300", "240", frameNum,"1"
        });
        Main.main(new String[]{
                "PAPER_EVAL", "SSG_EVAL",
                expFile,
                testFile,
                "300", "240", frameNum,"1"
        });
        Main.main(new String[]{
                "PAPER_EVAL", "MFS_OEVAL",
                expFile,
                testFile,
                "300", "240", frameNum,"1"
        });
        Main.main(new String[]{
                "PAPER_EVAL", "MFS_EVAL",
                expFile,
                testFile,
                "300", "240", frameNum,"1"
        });
    }
}
