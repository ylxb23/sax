package com.zero.sax.controller;

import com.zero.sax.domain.FileWrapper;
import com.zero.sax.domain.dto.*;
import com.zero.sax.init.SaxProperties;
import com.zero.sax.service.FileService;
import com.zero.sax.service.UserService;
import com.zero.sax.util.DateUtil;
import com.zero.sax.util.FileUtil;
import com.zero.sax.util.MD5Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping(path = "/sax/file")
public class FileController extends BaseController {
    @Resource
    private SaxProperties saxProperties;
    @Resource
    private FileService fileService;
    @Resource
    private UserService userService;


    /**
     * query file list
     * @param request
     * @param response
     * @param from      date format like 'yyyy-MM-dd'
     * @param to        date format like 'yyyy-MM-dd'
     * @param offset    default 0
     * @param limit     default 20
     */
    @GetMapping(path = "/share/history", consumes = {MediaType.ALL_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public SaxResponse queryShareHistory(HttpServletRequest request, HttpServletResponse response,
                      @RequestParam(value = "from", required = true) String from,
                      @RequestParam(value = "to", required = true) String to,
                      @RequestParam(value = "offset", defaultValue = "0") int offset,
                      @RequestParam(value = "limit", defaultValue = "20") int limit) {
        if(!isAuthed(request)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return SaxResponse.notAccessed("Not Login");
        }
        ClientMeta client = clientMeta(request);
        String user = userService.getUserBySession(client);
        long fromTimestamp = DateUtil.fromStr(from);
        long toTimestamp = DateUtil.fromStr(to);
        if(user == null || toTimestamp == 0L || fromTimestamp == 0L) {  // bad request param
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return SaxResponse.badRequest("Not allow");
        }
        List<FileShareOut> res = fileService.queryShareHistory(user, fromTimestamp, toTimestamp, offset, limit);
        boolean isEnd = res.isEmpty() || res.size() < limit;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return SaxResponse.success(res, isEnd);
    }


    @PostMapping(path = "/share", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SaxResponse> share(HttpServletRequest request, HttpServletResponse response,
                      @RequestBody ShareInfo shareInfo) throws IOException {
        if(!isAuthed(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(SaxResponse.notAccessed("Not Login"));
        }
        ClientMeta client = clientMeta(request);
        String user = userService.getUserBySession(client);
        if(user == null || shareInfo == null) {  // bad request param
            return ResponseEntity.ok(SaxResponse.badRequest("Not allow"));
        }
        long limitTimestamp = DateUtil.fromStr(shareInfo.getLimit());
        if(limitTimestamp > 0 && limitTimestamp <= System.currentTimeMillis()) {
            return ResponseEntity.ok(SaxResponse.badRequest("Expire time is before current timestamp"));
        }
        limitTimestamp += DateUtil.MILL_24H;    // add 24h
        FileShareOut out = fileService.share(user, shareInfo.getMd5(), limitTimestamp, shareInfo.getRemark());
        if(out == null || out.getStatus() == 2) {
            return ResponseEntity.ok(SaxResponse.badRequest("File Not Exist"));
        }
        return ResponseEntity.ok(SaxResponse.success(out));
    }


    @GetMapping(path = "/download", consumes = {MediaType.ALL_VALUE})
    public ResponseEntity<SaxResponse> download(HttpServletRequest request, HttpServletResponse response,
                      @RequestParam(value = "md5", required = true) String md5) throws IOException {
        if(!isAuthed(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(SaxResponse.notAccessed("Not Login"));
        }
        ClientMeta client = clientMeta(request);
        String user = userService.getUserBySession(client);
        if(user == null) {  // bad request param
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(SaxResponse.notAccessed("Not allow"));
        }
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM.getType());
        FileOut out = fileService.download(user, md5);
        if(out == null || out.getStatus() == 2) {
            String msg = (out == null || out.getName() == null) ? "File Not Exist" : ("File [" + out.getName() + "] Not Exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(SaxResponse.badRequest(msg));
        }
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(out.getPath()))) {
            OutputStream os = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(out.getName(), UTF8));
            response.setContentType(request.getServletContext().getMimeType(out.getPath()));
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = bis.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception e) {
            logger.error("DownloadFileError, client:{}, md5:{}", client, md5, e);
        }
        return null;
    }

    /**
     * query file list
     * @param request
     * @param response
     * @param from      date format like 'yyyy-MM-dd'
     * @param to        date format like 'yyyy-MM-dd'
     * @param offset    default 0
     * @param limit     default 20
     * @throws IOException
     */
    @GetMapping(path = "/query", consumes = {MediaType.ALL_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SaxResponse> query(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam(value = "from", required = true) String from,
                                @RequestParam(value = "to", required = true) String to,
                                @RequestParam(value = "offset", defaultValue = "0") int offset,
                                @RequestParam(value = "limit", defaultValue = "20") int limit) throws IOException {
        if(!isAuthed(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(SaxResponse.notAccessed("Not Login"));
        }
        ClientMeta client = clientMeta(request);
        String user = userService.getUserBySession(client);
        long fromTimestamp = DateUtil.fromStr(from);
        long toTimestamp = DateUtil.fromStr(to);
        if(user == null || toTimestamp == 0L || fromTimestamp == 0L) {  // bad request param
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SaxResponse.badRequest("Not allow"));
        }
        toTimestamp = toTimestamp + DateUtil.MILL_24H; // add 24h
        List<FileInfo> res = fileService.query(user, fromTimestamp, toTimestamp, offset, limit);
        boolean isEnd = res.isEmpty() || res.size() < limit;
        return ResponseEntity.ok(SaxResponse.success(res, isEnd));
    }

    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SaxResponse> upload(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
        if(!isAuthed(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(SaxResponse.notAccessed("Not Login"));
        }
        ClientMeta client = clientMeta(request);
        String user = userService.getUserBySession(client);
        if(user == null || user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SaxResponse.badRequest("Not allow"));
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setName(user);
        client.setUser(userInfo);
        responseAppendHeader(response, client);
        Map<String, MultipartFile> fileMap = request.getFileMap();
        if(fileMap.size() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SaxResponse.badRequest("Bad Request"));
        }
        String alias = request.getParameter("alias");
        String remark = request.getParameter("remark");
        Map<String, FileUploadDescription> multiFileRes = new ConcurrentHashMap<>(fileMap.size());
        fileMap.values().stream().forEach(multipartFile -> { // only one
            FileUploadDescription res = uploadFile(client, alias, remark, multipartFile);
            String fileName = multipartFile.getName();
            multiFileRes.put(fileName, res);
        });
        return ResponseEntity.ok(SaxResponse.success(multiFileRes));
    }

    protected FileUploadDescription uploadFile(ClientMeta client, String alias, String remark, MultipartFile file) {
        FileUploadDescription description = new FileUploadDescription();
        String originalFilename = file.getOriginalFilename();
        try {
            long size = file.getSize(); // TODO: file size limit
            if(size > saxProperties.getMaxFileSize()) {
                // out of limit
                logger.warn("You upload file size:{}, out of limit:{}", size, saxProperties.getMaxFileSize());
                description.setMsg("Your file out of limit");
                description.setResult(2);
                return description;
            }
            String contentType = file.getContentType();
            FileWrapper wrapper = new FileWrapper();
            wrapper.setAlia(alias);
            wrapper.setOriginalName(originalFilename);
            wrapper.setTimestamp(System.currentTimeMillis());
            wrapper.setSize(size);
            wrapper.setMd5(MD5Util.md5(file.getBytes()));
            wrapper.setFile(file);
            wrapper.setRemark(remark);
            wrapper.setType(FileUtil.getFileSuffix(originalFilename));
            logger.info("GetFile, content-type:{}, file info:{}", contentType, wrapper);
            return fileService.upload(client, wrapper);
        } catch (IOException e) {
            logger.error("UploadFileError, client:{}, file original name:{}", client, originalFilename);
            description.setResult(-1);
            description.setMsg("Error");
            return description;
        }
    }

}
