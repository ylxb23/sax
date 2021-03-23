package com.zero.sax.service;

import com.google.gson.Gson;
import com.zero.sax.domain.dto.AuthInfo;
import com.zero.sax.domain.dto.ClientMeta;
import com.zero.sax.domain.dto.UserInfo;
import com.zero.sax.redis.UserTable;
import com.zero.sax.redis.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Resource
    private Gson gson;
    @Resource
    private UserTable userTable;
    @Resource
    private UserSession userSession;

    public AuthInfo auth(ClientMeta client, UserInfo user) {
        if(client == null || user == null || user.getPass() == null || user.getName() == null) {
            return new AuthInfo(AuthInfo.STATUS_USER_NOT_EXIST, user==null?"-":user.getName(), "0", "-", "0", "bad request");
        }
        String pass = userTable.userPass(user.getName());
        if(pass == null || pass.isEmpty()) {
            // user not exist
            return new AuthInfo(AuthInfo.STATUS_USER_NOT_EXIST, user.getName(), "0", "-", "0", "User not exist");
        }
        if(user.getPass().equals(pass)) {   // login success
            // generate session
            String au = String.valueOf(System.currentTimeMillis()); // auth time
            String sn = UserSession.session(client, user.getName());
            userSession.joinSession(user.getName(), sn);
            logger.info("UserLogin, name:{}, client:{}", user.getName(), gson.toJson(client));
            return new AuthInfo(AuthInfo.STATUS_SUCCESS, user.getName(), au, sn, String.valueOf(UserSession.AUTH_TTL), "success");
        } else {
            return new AuthInfo(AuthInfo.STATUS_PASSWORD_IS_WRONG, user.getName(), "0", "-", "0", "Password is wrong");
        }
    }

    public boolean out(ClientMeta client) {
        if(client == null || client.getSn() == null) {
            return true;
        }
        userSession.delSession(client.getSn());
        return true;
    }

    public String getUserBySession(ClientMeta client) {
        if(client == null || client.getSn() == null) {
            return null;
        }
        return userSession.user(client.getSn());
    }
}
