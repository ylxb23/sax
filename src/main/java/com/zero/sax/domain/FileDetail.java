package com.zero.sax.domain;

public class FileDetail {
    String owner;
    String name;
    String alia;
    long size;
    String md5;
    long timestamp;
    String type;    // file type (file name suffix)
    String relatePath;
    String remark;  // remark words
    String smd5;    // share smd5sum if shared

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlia() {
        return alia;
    }

    public void setAlia(String alia) {
        this.alia = alia;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRelatePath() {
        return relatePath;
    }

    public void setRelatePath(String relatePath) {
        this.relatePath = relatePath;
    }

    public String getSmd5() {
        return smd5;
    }

    public void setSmd5(String smd5) {
        this.smd5 = smd5;
    }
}
