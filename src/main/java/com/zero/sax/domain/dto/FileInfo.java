package com.zero.sax.domain.dto;

public class FileInfo {
    private String alia;    // alia
    private String name;    // original name
    private String type;    // file type
    private long size;      // file size
    private String md5;     // md5sum
    private String owner;   // owner name
    private long timestamp; // upload timestamp
    private String remark;  // remark words
    private String path;    // download path
    private FileShareOut share;

    public String getAlia() {
        return alia;
    }

    public void setAlia(String alia) {
        this.alia = alia;
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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FileShareOut getShare() {
        return share;
    }

    public void setShare(FileShareOut share) {
        this.share = share;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "alia='" + alia + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", md5='" + md5 + '\'' +
                ", owner='" + owner + '\'' +
                ", timestamp=" + timestamp +
                ", remark='" + remark + '\'' +
                ", share=" + share +
                '}';
    }
}
