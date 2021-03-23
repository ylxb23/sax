package com.zero.sax.util;

import org.springframework.util.DigestUtils;

public class MD5Util {


    public static String md5(byte[] bytes) {
        return DigestUtils.md5DigestAsHex(bytes);
    }
}
