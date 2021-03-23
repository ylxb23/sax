package com.zero.sax.domain;

public class FileShareDetail {
    private String owner;   // owner name
    private String smd5;    // share md5sum
    private String md5;     // file md5sum
    private String remark;  // share remark words
    private long limit;     // out of timestamp
    private long timestamp; // share timestamp

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSmd5() {
        return smd5;
    }

    public void setSmd5(String smd5) {
        this.smd5 = smd5;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
