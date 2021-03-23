package com.zero.sax.domain.dto;

public class ClientMeta {
    private String ip;  // remote ip
    private String ua;  // user-agent
    private String lts; // last timestamp
    private String ts;  // timestamp
    private String aut;  // authed timestamp
    private String sn;  // session

    private UserInfo user;  // user info

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getLts() {
        return lts;
    }

    public void setLts(String lts) {
        this.lts = lts;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getAut() {
        return aut;
    }

    public void setAut(String aut) {
        this.aut = aut;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ClientMeta{" +
                "ip='" + ip + '\'' +
                ", ua='" + ua + '\'' +
                ", lts='" + lts + '\'' +
                ", ts='" + ts + '\'' +
                ", aut='" + aut + '\'' +
                ", sn='" + sn + '\'' +
                ", user=" + user +
                '}';
    }
}
