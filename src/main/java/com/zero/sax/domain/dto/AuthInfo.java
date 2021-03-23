package com.zero.sax.domain.dto;

public class AuthInfo {
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_USER_NOT_EXIST = 1;
    public static final int STATUS_PASSWORD_IS_WRONG = 2;

    private String name;// name
    private String au;  // auth timestamp
    private String sn;  // session
    private String ttl; // auth in valid last timestamp,
    private int status; // 0-success, 1-user not exist, 2-password is wrong
    private String info;    // auth info, eg: "user not exist" | "password is wrong"

    public AuthInfo(int status, String name, String au, String sn, String ttl, String info) {
        this.status = status;
        this.name = name;
        this.au = au;
        this.sn = sn;
        this.ttl = ttl;
        this.info = info;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAu() {
        return au;
    }

    public void setAu(String au) {
        this.au = au;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
