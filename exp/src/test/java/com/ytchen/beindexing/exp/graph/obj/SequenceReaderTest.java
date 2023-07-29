package com.ytchen.beindexing.exp.graph.obj;

import com.ytchen.beindexing.exp.graphs.obj.SequenceReader;
import org.junit.Test;

import java.io.IOException;

import static com.ytchen.beindexing.exp.utils.ToStringUtils.repeat;

public class SequenceReaderTest {

    @Test
    public void readFramesFromFile() throws IOException {
        System.out.println(SequenceReader.readFramesFromFile("./data/objects.sequence"));
        System.out.println(repeat("-", 20));
        System.out.println(SequenceReader.readCompactedFramesFromFile("./data/objects.sequence"));
    }
}
