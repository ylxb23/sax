package com.zero.sax.service;

import com.zero.sax.domain.FileDetail;
import com.zero.sax.domain.FileShareDetail;
import com.zero.sax.domain.FileWrapper;
import com.zero.sax.domain.dto.*;
import com.zero.sax.init.SaxProperties;
import com.zero.sax.redis.FileRedisInfo;
import com.zero.sax.util.DateUtil;
import com.zero.sax.util.FileUtil;
import com.zero.sax.util.MD5Util;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FileService {
    @Resource
    private SaxProperties saxProperties;
    @Resource
    private FileRedisInfo fileRedisInfo;

    /**
     *
     * @param user  owner
     * @param md5   file md5sum
     * @param limit share out of timestamp
     * @param shareRemark   share remark
     * @return
     */
    public FileShareOut share(String user, String md5, long limit, String shareRemark) {
        FileDetail detail = fileRedisInfo.getFileDetail(user, md5);
        FileShareOut out = new FileShareOut();
        if(detail == null) {
            out.setStatus(2);
            out.setMsg("File not Exist");
            return out;
        }
        long shareTimestamp = System.currentTimeMillis();
        FileShareDetail shareDetail = new FileShareDetail();
        shareDetail.setOwner(user);
        shareDetail.setMd5(md5);
        shareDetail.setLimit(limit);
        String smd5 = smd5(user, md5);
        shareDetail.setSmd5(smd5);
        shareDetail.setTimestamp(shareTimestamp);
        shareDetail.setRemark(shareRemark);
        fileRedisInfo.setFileShareUserTag(user, smd5, limit);
        fileRedisInfo.addFileShareToList(user, shareTimestamp, smd5);
        fileRedisInfo.addFileShareDetailInfo(user, shareDetail);
        detail.setSmd5(smd5);
        fileRedisInfo.appendFileShareTag(user, md5, detail);

        out.setLimit(limit);
        out.setSize(detail.getSize());
        out.setTimestamp(shareTimestamp);
        out.setType(detail.getType());
        out.setName(detail.getName());
        out.setSmd5(smd5);
        out.setRemark(shareRemark);
        return out;
    }

    private static String smd5(String user, String md5) {
        return MD5Util.md5((user + md5).getBytes());
    }

    public FileShareOut getShareInfo(String smd5) {
        FileShareOut shareOut = new FileShareOut();
        String user = fileRedisInfo.getFileShareUser(smd5);
        if(user == null) {
            shareOut.setStatus(2);
            return shareOut;
        }
        FileShareDetail shareDetail = fileRedisInfo.getFileShareDetailInfo(user, smd5);
        if(shareDetail == null) {
            //
            shareOut.setStatus(2);
            return null;
        }
        FileDetail detail = fileRedisInfo.getFileDetail(user, shareDetail.getMd5());
        return detailToOut(shareDetail, detail);
    }

    public FileOut get(String smd5) {
        String user = fileRedisInfo.getFileShareUser(smd5);
        if(user == null) {
            return null;
        }
        FileShareDetail shareDetail = fileRedisInfo.getFileShareDetailInfo(user, smd5);
        if(shareDetail == null) {
            //
            return null;
        }
        return download(user, shareDetail.getMd5());
    }

    public FileOut download(String user, String md5) {
        FileDetail detail = fileRedisInfo.getFileDetail(user, md5);
        FileOut out = new FileOut();
        if(detail == null) {
            out.setStatus(2);
            out.setMsg("File not Exist");
            return out;
        }
        out.setName(detail.getName());
        out.setSize(detail.getSize());
        out.setType(detail.getType());
        File file = new File(fileStoragePath(saxProperties.getRoot(), detail.getRelatePath()));
        if(!file.exists()) {
            out.setStatus(2);
            out.setMsg("File not Exist");
            return out;
        }
        out.setPath(file.getAbsolutePath());
        out.setSize(detail.getSize());
        out.setType(detail.getType());
        return out;
    }

    public List<FileInfo> query(String owner, long fromTimestamp, long toTimestamp, int offset, int limit) {
        long from = fromTimestamp;
        long to = toTimestamp;
        if(from > to) {
            to = from ^ to;
            from = to ^ from;
            to = from ^ to;
        }

        Set<String> md5s = fileRedisInfo.getFileMd5Set(owner, from, to);
        if(CollectionUtils.isEmpty(md5s)) {
            return new ArrayList<>();
        }
        List<FileDetail> details = fileRedisInfo.getFileDetailList(owner, md5s);
        // page
        if(details.size() < offset) {
            return new ArrayList<>();
        }
        List<FileDetail> list;
        if(details.size() > (offset+limit)) {
            list = details.subList(offset, offset+limit);
        } else {
            list = details.subList(offset, details.size());
        }
        Set<String> smd5s = list.stream().filter(detail -> detail!=null&&detail.getSmd5()!=null).map(FileDetail::getSmd5).collect(Collectors.toSet());
        List<FileShareDetail> shareDetails;
        if(CollectionUtils.isEmpty(smd5s)) {
            shareDetails = new ArrayList<>();
        } else {
            shareDetails = fileRedisInfo.getFileShareDetailList(owner, smd5s);
        }
        Map<String, FileShareDetail> shareDetailMap;
        if(CollectionUtils.isEmpty(shareDetails)) {
            shareDetailMap = new HashMap<>();
        } else {
            shareDetailMap = shareDetails.stream().collect(Collectors.toMap(FileShareDetail::getSmd5, Function.identity(), (v1,v2) -> v1));
        }
        return list.stream().map(detail -> detailToInfo(detail, shareDetailMap.get(detail.getSmd5()))).filter(Objects::nonNull).collect(Collectors.toList());
    }


    public List<FileShareOut> queryShareHistory(String user, long fromTimestamp, long toTimestamp, int offset, int limit) {
        long from = fromTimestamp;
        long to = toTimestamp;
        if(from > to) {
            to = from ^ to;
            from = to ^ from;
            to = from ^ to;
        }
        Set<String> smd5s = fileRedisInfo.getFileShareMd5Set(user, from, to);
        if(CollectionUtils.isEmpty(smd5s)) {
            return new ArrayList<>();
        }
        List<FileShareDetail> details = fileRedisInfo.getFileShareDetailList(user, smd5s);
        // page
        if(details.size() < offset) {
            return new ArrayList<>();
        }
        List<FileShareDetail> list;
        if(details.size() > (offset+limit)) {
            list = details.subList(offset, offset+limit);
        } else {
            list = details.subList(offset, details.size());
        }
        Set<String> md5s = list.stream().map(FileShareDetail::getMd5).collect(Collectors.toSet());
        List<FileDetail> fileDetailList = fileRedisInfo.getFileDetailList(user, md5s);
        if(CollectionUtils.isEmpty(fileDetailList)) {
            return new ArrayList<>();
        }
        Map<String, FileDetail> fileDetailMap = fileDetailList.stream().collect(Collectors.toMap(FileDetail::getMd5, Function.identity(), (v1,v2) -> v1));
        // get share detail
        return list.stream()
                .map(detail -> detailToOut(detail, fileDetailMap.get(detail.getMd5())))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    private FileShareOut detailToOut(FileShareDetail detail, FileDetail fileDetail) {
        FileShareOut out = new FileShareOut();
        if(detail == null || fileDetail == null) {
            out.setStatus(2);
            return out;
        }
        out.setLimit(detail.getLimit());
        out.setSize(fileDetail.getSize());
        out.setTimestamp(detail.getTimestamp());
        out.setType(fileDetail.getType());
        out.setName(fileDetail.getName());
        out.setRemark(StringUtils.hasLength(detail.getRemark()) ? detail.getRemark() : "-");
        out.setFetch(saxProperties.getProtocol() + saxProperties.getDomain() + "/sax/file/get/" + detail.getSmd5());
        out.setLink(saxProperties.getProtocol() + saxProperties.getDomain() + "/share.html?smd5=" + detail.getSmd5());
        out.setSmd5(detail.getSmd5());
        return out;
    }

    private FileInfo detailToInfo(FileDetail detail, FileShareDetail shareDetail) {
        if(detail == null) {
            return null;
        }
        FileInfo info = new FileInfo();
        info.setAlia(detail.getAlia());
        info.setMd5(detail.getMd5());
        info.setName(detail.getName());
        info.setOwner(detail.getOwner());
        info.setSize(detail.getSize());
        info.setTimestamp(detail.getTimestamp());
        info.setType(detail.getType());
        info.setRemark(detail.getRemark() == null ? "-" : detail.getRemark());
        info.setPath(saxProperties.getProtocol() + saxProperties.getDomain() + "/sax/file/download?md5=" + detail.getMd5());
        if(shareDetail != null) {
            info.setShare(detailToOut(shareDetail, detail));
        }
        return info;
    }

    public FileUploadDescription upload(ClientMeta client, FileWrapper fileWrapper) throws IOException {
        FileUploadDescription res = new FileUploadDescription();
        if(client == null || client.getUser() == null || client.getUser().getName() == null || fileWrapper == null || fileWrapper.getFile() == null) {
            res.setResult(-1);  // bad request
            res.setMsg("Bad request");
            return res;
        }
        String owner = client.getUser().getName();
        String type = FileUtil.getFileSuffix(fileWrapper.getFile().getOriginalFilename());
        String relatePath = getRelatePath(owner, fileWrapper.getTimestamp(), fileWrapper.getMd5(), type);
        String path = fileStoragePath(saxProperties.getRoot(), relatePath);
        FileDetail detail = FileUtil.buildFileDetail(owner, relatePath, fileWrapper);
        FileUtil.write(path, fileWrapper.getFile().getBytes());
        if(fileRedisInfo.isFileIfUploadedBefore(owner, fileWrapper.getMd5())) { // file uploaded before
            res.setResult(1);   // file uploaded before
            res.setMsg("File exist, you uploaded before.");
            return res;
        }
        fileRedisInfo.addFileDetailInfo(owner, detail);
        fileRedisInfo.addFileToList(owner, fileWrapper.getTimestamp(), fileWrapper.getMd5());
        res.setDetail(detail);
        return res;
    }

    private static String fileStoragePath(String root, String relatePath) {
        return root + FileUtil.SP + relatePath;
    }


    /**
     * 用户名/年/月/文件MD5
     * @param owner
     * @param timestamp
     * @param md5
     * @param suffix
     * @return
     */
    private static String getRelatePath(String owner, long timestamp, String md5, String suffix) {
        StringBuilder sb = new StringBuilder();
        sb.append(owner).append(FileUtil.SP);
        Calendar c = DateUtil.timestampToCalendar(timestamp);
        sb.append(c.get(Calendar.YEAR)).append(FileUtil.SP)
                .append(String.format("%02d", c.get(Calendar.MONTH)+1)).append(FileUtil.SP)
                .append(md5).append(".").append(suffix);
        return sb.toString();
    }

}
