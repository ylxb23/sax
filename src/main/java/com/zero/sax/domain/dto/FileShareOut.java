package com.zero.sax.domain.dto;

public class FileShareOut {
    private int status;     // 0-success, 2-file not exist
    private String msg;     // error msg
    private String name;    // file name
    private String type;    // file type
    private long size;      // file size
    private long timestamp; // share timestamp
    private String smd5;    // share md5sum
    private long limit;     // share out of time limit timestamp
    private String remark;  // share remark
    private String link;    // share outer link
    private String fetch;   // share outer download link


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSmd5() {
        return smd5;
    }

    public void setSmd5(String smd5) {
        this.smd5 = smd5;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFetch() {
        return fetch;
    }

    public void setFetch(String fetch) {
        this.fetch = fetch;
    }
}
