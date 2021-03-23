package com.zero.sax.domain.dto;

public class UserInfo {
    private String name;
    private String pass;
    private boolean keep;   // keep sign in for long time

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean isKeep() {
        return keep;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }
}
