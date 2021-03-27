package com.zero.sax.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 彩蛋
 */
@Service
public class PaintedEggshells {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String getEggshells(String user) {
        String key = keyEggshells(user);
        Object res = stringRedisTemplate.boundValueOps(key).get();
        return res == null ? "" : (String) res;
    }

    static String keyEggshells(String user) {
        return "sax:eggshells:" + user;
    }
}
