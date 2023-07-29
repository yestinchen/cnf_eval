package com.ytchen.beindexing.exp.utils;

import org.junit.Assert;
import org.junit.Test;

import static com.ytchen.beindexing.exp.utils.ToStringUtils.center;

public class ToStringUtilsTest {

    @Test
    public void centerTest() {
        Assert.assertEquals(" s ", center("s", 3));
    }
}
