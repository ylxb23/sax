package com.zero.sax.simple;

import org.junit.jupiter.api.Test;

public class UtilTest {

    @Test
    public void testGetFileSuffix() {

    }

    @Test
    public void testSwitch() {
        long from = 20;
        long to = 15;
        if(from > to) {
            to = from ^ to;
            from = to ^ from;
            to = from ^ to;
        }
        System.out.println("from: " + from + ", to: " + to);
    }

}
