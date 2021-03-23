package com.zero.sax.domain.dto;

public class SaxResponse<T> {
    /**
     * 200-success,
     * 400-bad request,
     * 403-not accessed,
     * 500-bad hand(can't retry),
     * 501-bad hand(can retry)
     */
    private int status;
    private String msg;     // error msg
    private T data;         // response data
    private boolean isEnd = true;

    public SaxResponse() {
        this.status = 200;
        this.msg = "success";
    }
    public SaxResponse(T data) {
        this();
        this.data = data;
    }
    public SaxResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public SaxResponse(T data, boolean isEnd) {
        this();
        this.data = data;
        this.isEnd = isEnd;
    }

    public static <DT> SaxResponse success(DT data) {
        return new SaxResponse(data);
    }
    public static <DT> SaxResponse success(DT data, boolean isEnd) {
        return new SaxResponse(data, isEnd);
    }
    public static <DT> SaxResponse notAccessed(String msg) {
        return new SaxResponse(403, msg, null);
    }
    public static <DT> SaxResponse badRequest(String msg) {
        return new SaxResponse(400, msg, null);
    }
    public static <DT> SaxResponse badHand(String msg) {
        return new SaxResponse(500, msg, null);
    }

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    @Override
    public String toString() {
        return "SaxResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", isEnd=" + isEnd +
                '}';
    }
}
