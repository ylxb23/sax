package com.zero.sax.domain.dto;

public class ShareInfo {
    private String md5;
    private String limit;   // share out of time, format: yyyy-MM-dd
    private String remark;  // share remark

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
