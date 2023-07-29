package com.ytchen.beindexing.exp.utils;

import org.junit.Assert;
import org.junit.Test;

import static com.ytchen.beindexing.exp.utils.StringUtils.strip;

public class StringUtilsTest {

    @Test
    public void stripTest() {
        Assert.assertEquals("sss", strip("(sss)"));
        Assert.assertEquals("sss)", strip("(sss))"));
        Assert.assertEquals("sss", strip("sss"));
    }
}
