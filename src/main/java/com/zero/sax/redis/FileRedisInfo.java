package com.zero.sax.redis;

import com.google.gson.Gson;
import com.zero.sax.domain.FileDetail;
import com.zero.sax.domain.FileShareDetail;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FileRedisInfo {
    @Resource
    private Gson gson;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public boolean isFileIfUploadedBefore(String user, String md5) {
        String key = userFileDetailKey(user);
        Object res = stringRedisTemplate.boundHashOps(key).get(md5);
        if(res == null) {   // 不存在
            return false;
        }
        return true;
    }

    /**
     * ZADD key {fileMD5} {timestamp}
     * @param user
     * @param timestamp
     * @param md5
     * @return
     */
    public boolean addFileToList(String user, long timestamp, String md5) {
        String key = userFileListKey(user);
        return stringRedisTemplate.boundZSetOps(key).add(md5, timestamp);
    }

    public boolean addFileDetailInfo(String user, FileDetail detail) {
        String key = userFileDetailKey(user);
        // if file exist, update info.
        stringRedisTemplate.boundHashOps(key).put(detail.getMd5(), fileDetailToJson(detail));
        return true;
    }

    public Set<String> getFileMd5Set(String user, long from, long to) {
        String key = userFileListKey(user);
        return stringRedisTemplate.boundZSetOps(key).rangeByScore(from, to);
    }

    public List<FileDetail> getFileDetailList(String user, Collection md5s) {
        String key = userFileDetailKey(user);
        List<String> jsons = stringRedisTemplate.boundHashOps(key).multiGet(md5s);
        if(CollectionUtils.isEmpty(jsons)) {
            return new ArrayList<>();
        }
        return jsons.stream()
                .map(this::jsonToFileDetail)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(FileDetail::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public FileDetail getFileDetail(String user, String md5) {
        String key = userFileDetailKey(user);
        Object json = stringRedisTemplate.boundHashOps(key).get(md5);
        if(json == null) {
            return null;
        }
        return jsonToFileDetail((String) json);
    }

    /**
     *
     * @param user
     * @param timestamp
     * @param smd5
     * @return
     */
    public boolean addFileShareToList(String user, long timestamp, String smd5) {
        String key = userShareFileListKey(user);
        return stringRedisTemplate.boundZSetOps(key).add(smd5, timestamp);
    }


    public Set<String> getFileShareMd5Set(String user, long from, long to) {
        String key = userShareFileListKey(user);
        return stringRedisTemplate.boundZSetOps(key).rangeByScore(from, to);
    }

    public boolean addFileShareDetailInfo(String user, FileShareDetail detail) {
        String key = userShareFileDetailKey(user);
        // if file exist, update info.
        stringRedisTemplate.boundHashOps(key).put(detail.getSmd5(), fileShareDetailToJson(detail));
        return true;
    }


    /**
     *
     * @param user
     * @param smd5
     * @param limit  expire at
     */
    public void setFileShareUserTag(String user, String smd5, long limit) {
        String key = shareFileTagKey(smd5);
        if(limit > 1616002123825L) {
            long expire = limit - System.currentTimeMillis();
            stringRedisTemplate.boundValueOps(key).set(user, expire, TimeUnit.MILLISECONDS);
        } else {
            stringRedisTemplate.boundValueOps(key).set(user);
        }
        return;
    }

    public void appendFileShareTag(String user, String md5, FileDetail detailWithShare) {
        String key = userFileDetailKey(user);
        stringRedisTemplate.boundHashOps(key).put(md5, fileDetailToJson(detailWithShare));
    }

    public String getFileShareUser(String smd5) {
        String key = shareFileTagKey(smd5);
        return stringRedisTemplate.boundValueOps(key).get();
    }


    public FileShareDetail getFileShareDetailInfo(String user, String smd5) {
        String key = userShareFileDetailKey(user);
        Object json = stringRedisTemplate.boundHashOps(key).get(smd5);
        if(json == null) {
            return null;
        }
        return jsonToFileShareDetail((String) json);
    }

    public List<FileShareDetail> getFileShareDetailList(String user, Set smd5s) {
        String key = userShareFileDetailKey(user);
        List<String> jsons = stringRedisTemplate.boundHashOps(key).multiGet(smd5s);
        if(CollectionUtils.isEmpty(jsons)) {
            return new ArrayList<>();
        }
        return jsons.stream()
                .map(this::jsonToFileShareDetail)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(FileShareDetail::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    private String fileShareDetailToJson(FileShareDetail detail) {
        return gson.toJson(detail);
    }
    private FileShareDetail jsonToFileShareDetail(String json) {
        try {
            return gson.fromJson(json, FileShareDetail.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private FileDetail jsonToFileDetail(String json) {
        try {
            return gson.fromJson(json, FileDetail.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private String fileDetailToJson(FileDetail detail) {
        return gson.toJson(detail);
    }

    /**
     * String
     * @param smd5
     * @return
     */
    private String shareFileTagKey(String smd5) {
        return "sax:file:share:smd5:" + smd5;
    }


    /**
     * Sorted set
     * @param user
     * @return
     */
    private String userShareFileListKey(String user) {
        return "sax:file:share:zset:" + user;
    }

    /**
     * Hash map
     * @param user
     * @return
     */
    private String userShareFileDetailKey(String user) {
        return "sax:file:share:map:" + user;
    }
    /**
     * Sorted set
     * @param user
     * @return
     */
    private static final String userFileListKey(String user) {
        return "sax:file:zset:" + user;
    }

    /**
     * Hash map
     * @param user
     * @return
     */
    private static final String userFileDetailKey(String user) {
        return "sax:file:map:" + user;
    }

}
