package com.zero.sax.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户表使用hash存储在redis中，key为"sax:users", filed为"{name}", value为"{pass}"
 */
@Service
public class UserTable {
    private static final String USER_INFO_KEY = "sax:users";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String userPass(String name) {
        Object pass = stringRedisTemplate.boundHashOps(USER_INFO_KEY).get(name);
        if(pass == null) {
            return null;
        }
        return String.valueOf(pass);
    }

    public boolean isUserExist(String name) {
        return stringRedisTemplate.boundHashOps(USER_INFO_KEY).hasKey(name);
    }
}
