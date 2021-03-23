package com.zero.sax.util;

import com.zero.sax.domain.FileDetail;
import com.zero.sax.domain.FileWrapper;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtil {
    public static final String SP = File.separator;


    public static FileDetail buildFileDetail(String user, String relatePath, FileWrapper fileWrapper) {
        FileDetail detail = new FileDetail();
        detail.setAlia(fileWrapper.getAlia());
        detail.setMd5(fileWrapper.getMd5());
        detail.setName(fileWrapper.getOriginalName());
        detail.setOwner(user);
        detail.setSize(fileWrapper.getSize());
        detail.setTimestamp(fileWrapper.getTimestamp());
        detail.setType(fileWrapper.getType());
        detail.setRelatePath(relatePath);
        detail.setRemark(fileWrapper.getRemark());
        return detail;
    }

    public static String getFileSuffix(String name) {
        String type = "";
        if(name != null && name.indexOf(".") >= 0) {
            int idx = name.lastIndexOf(".") + 1;
            if(idx == name.length()) {

            }
            type = name.substring(idx);
        }
        return type;
    }

    public static String read(String path) {
        try {
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
        } catch (FileNotFoundException e) {

        }
        return null;
    }

    public static void write(String path, byte[] bytes) {
        File file = new File(path);
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try(FileOutputStream fos = new FileOutputStream(file, false);
            FileChannel channel = fos.getChannel()) {
            System.out.println(path + ": " + file.getAbsolutePath());
            channel.write(ByteBuffer.wrap(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
