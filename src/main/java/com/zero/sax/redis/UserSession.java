package com.zero.sax.redis;

import com.zero.sax.controller.BaseController;
import com.zero.sax.domain.dto.ClientMeta;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 用户Session存储在redis中，使用"sax:sn:{cm}"作为key, "au"作为value, ttl控制session有效时间
 */
@Service
public class UserSession {

    public static final long AUTH_TTL = 1000 * 60 * 60;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void joinSession(String user, String session) {
        String key = key(session);
        stringRedisTemplate.boundValueOps(key).set(user, AUTH_TTL, TimeUnit.MILLISECONDS);
    }

    public boolean refreshSessionTtl(String session) {
        String key = key(session);
        return stringRedisTemplate.boundValueOps(key).expire(AUTH_TTL, TimeUnit.MILLISECONDS);
    }

    public boolean isAuth(String session) {
        String key = key(session);
        String au = stringRedisTemplate.boundValueOps(key).get();
        if(au == null) {
            return false;
        }
        return true;
    }

    public String user(String session) {
        String key = key(session);
        return stringRedisTemplate.boundValueOps(key).get();
    }

    public void delSession(String session) {
        String key = key(session);
        stringRedisTemplate.delete(key);
    }

    /**
     * 通过cm + name 生成惟一 session
     * @param client
     * @param name
     * @return session
     */
    public static String session(ClientMeta client, String name) {
        String cm = BaseController.clientMark(client.getUa(), client.getIp());
        return DigestUtils.md5DigestAsHex(new String(cm + name).getBytes());
    }

    private static final String key(String session) {
        return "sax:sn:" + session;
    }


}
