package com.zero.sax.domain.dto;

import com.zero.sax.domain.FileDetail;

public class FileUploadDescription {
    private int result = 0;         // 0:success, 1:file uploaded before, -1:bad request, 2:file size out of limit.
    private String msg = "success"; // msg
    private FileDetail detail;      // file detail

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public FileDetail getDetail() {
        return detail;
    }

    public void setDetail(FileDetail detail) {
        this.detail = detail;
    }
}
