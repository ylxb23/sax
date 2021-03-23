package com.zero.sax.controller;

import com.google.gson.Gson;
import com.zero.sax.domain.dto.ClientMeta;
import com.zero.sax.redis.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseController {
    protected static final String UTF8 = "UTF-8";
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    interface Key {
        String USER_AGENT = "User-Agent";
        String LAST_REQUEST_TIMESTAMP = "lts";
        String REQUEST_TIMESTAMP = "ts";

        String COOKIE_KEY_SESSION = "sn";
        String COOKIE_KEY_USER = "usr";
        String COOKIE_KEY_AUTH_TIMESTAMP = "aut";
    }

    @Resource
    private UserSession userSession;
    @Resource
    protected Gson gson;

    /**
     * to filter
     * @param request
     * @return
     */
    protected boolean isAuthed(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length == 0) {
            return false;
        }
        for(Cookie cookie : cookies) {
            if(cookie != null && Key.COOKIE_KEY_SESSION.equals(cookie.getName())) {
                String sn = cookie.getValue();
                if(userSession.isAuth(sn)) {
                    userSession.refreshSessionTtl(sn);
                    return true;
                }
            }
        }
        return false;
    }

    protected ClientMeta clientMeta(HttpServletRequest request) {
        ClientMeta client = new ClientMeta();
        client.setIp(request.getRemoteHost());
        client.setLts(getOrDefault(request, Key.LAST_REQUEST_TIMESTAMP, "0"));
        client.setTs(getOrDefault(request, Key.REQUEST_TIMESTAMP, String.valueOf(System.currentTimeMillis())));
        client.setUa(getOrDefault(request, Key.USER_AGENT, "unknown"));
        getCookieOrDefault(request, client);    // cookies
        return client;
    }


    protected void responseAppendHeader(HttpServletResponse response, ClientMeta client) {
        if(client.getTs() != null) {
            response.addHeader(Key.LAST_REQUEST_TIMESTAMP, client.getTs());
        }
        String user = userSession.user(client.getSn());
        if(user != null && !user.isEmpty()) {
            userSession.refreshSessionTtl(client.getSn());
        }
        response.addHeader("iTellYou", "Happy every day.");
    }

    public static String clientMark(String ua, String ip) {
        return ua + "+" + ip;
    }

    private String getOrDefault(HttpServletRequest request, String key, String defaultValue) {
        String obj = request.getHeader(key);
        if(obj == null) {
            return defaultValue;
        }
        return obj;
    }

    private void getCookieOrDefault(HttpServletRequest request, ClientMeta client) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length == 0) {
            return ;
        }
        for(Cookie cookie : cookies) {
            if(cookie == null) {
                continue;
            }
            switch (cookie.getName()) {
                case Key.COOKIE_KEY_AUTH_TIMESTAMP:
                    client.setAut(cookie.getValue());
                    break;
                case Key.COOKIE_KEY_SESSION:
                    client.setSn(cookie.getValue());
                    break;
                case Key.COOKIE_KEY_USER:   // just for debug
//                    if(client.getUser() == null) {
//                        client.setUser(new UserInfo());
//                    }
//                    client.getUser().setName(cookie.getValue());
                    break;
                default:
                    logger.debug("GetUnknownCookie, client:{}, cookie:{}", client, cookie);
                    break;
            }
        }
    }
}
