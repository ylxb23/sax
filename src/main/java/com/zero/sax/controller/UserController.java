package com.zero.sax.controller;

import com.zero.sax.domain.dto.AuthInfo;
import com.zero.sax.domain.dto.ClientMeta;
import com.zero.sax.domain.dto.SaxResponse;
import com.zero.sax.domain.dto.UserInfo;
import com.zero.sax.init.SaxProperties;
import com.zero.sax.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(path = "/sax/user", consumes = {MediaType.ALL_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController extends BaseController {
    @Resource
    private SaxProperties saxProperties;
    @Resource
    private UserService userService;

    /**
     * need add cookies
     * @param request
     * @param response
     * @param user
     * @throws IOException
     */
    @PostMapping(path = "/auth")
    public void auth(HttpServletRequest request, HttpServletResponse response,
                     @RequestBody UserInfo user) throws IOException {
        ClientMeta client = clientMeta(request);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if(user == null || user.getName() == null || user.getPass() == null) {
            logger.info("Client invalid visit[auth], client:{}, user:{}", client, user);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().println(gson.toJson(SaxResponse.badRequest("Bad request")));
            return;
        }
        AuthInfo res = userService.auth(client, user);
        responseAppendHeader(response, client);
        if(res.getStatus() == AuthInfo.STATUS_SUCCESS) {
            response.setStatus(HttpStatus.OK.value());
            int expire = (int)((Long.valueOf(res.getTtl())) / 1000);
            Cookie aut = new Cookie(Key.COOKIE_KEY_AUTH_TIMESTAMP, res.getTtl());
            aut.setMaxAge(expire);
            aut.setDomain(saxProperties.getDomain());
            aut.setPath("/");
            response.addCookie(aut);
            Cookie sn = new Cookie(Key.COOKIE_KEY_SESSION, res.getSn());
            sn.setMaxAge(expire);
            sn.setDomain(saxProperties.getDomain());
            sn.setPath("/");
            response.addCookie(sn);
            Cookie usr = new Cookie(Key.COOKIE_KEY_USER, user.getName());
            usr.setMaxAge(expire);
            usr.setDomain(saxProperties.getDomain());
            usr.setPath("/");
            response.addCookie(usr);
            logger.info("UserSign, user:{}, client:{}", user.getName(), client);
        }
        response.getWriter().println(gson.toJson(SaxResponse.success(res)));
        return;
    }

    @RequestMapping(path = "/check", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<SaxResponse> check(HttpServletRequest request, HttpServletResponse response) {
        if(isAuthed(request)) {
            return ResponseEntity.ok(SaxResponse.success("ok"));
        } else {
            return ResponseEntity.ok(SaxResponse.notAccessed("Not Login"));
        }
    }

    @PostMapping(path = "/out")
    public ResponseEntity<SaxResponse> out(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(!isAuthed(request)) {
            return ResponseEntity.ok(SaxResponse.success("ok"));
        }
        ClientMeta client = clientMeta(request);
        logger.info("UserLogout, client:{}", client);
        userService.out(client);
        return ResponseEntity.ok(SaxResponse.success("ok"));
    }
}

