package com.zero.sax.domain;

import org.springframework.web.multipart.MultipartFile;

public class FileWrapper {
    private String alia;
    private String originalName;
    private String type;
    private long size;
    private long timestamp;
    private String md5;
    private String path;
    private String remark;
    private MultipartFile file;

    public String getAlia() {
        return alia;
    }

    public void setAlia(String alia) {
        this.alia = alia;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "FileWrapper{" +
                "alia='" + alia + '\'' +
                ", originalName='" + originalName + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", timestamp=" + timestamp +
                ", md5='" + md5 + '\'' +
                ", path='" + path + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
