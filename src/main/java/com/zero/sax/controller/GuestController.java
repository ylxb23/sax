package com.zero.sax.controller;


import com.zero.sax.domain.dto.ClientMeta;
import com.zero.sax.domain.dto.FileOut;
import com.zero.sax.domain.dto.FileShareOut;
import com.zero.sax.domain.dto.SaxResponse;
import com.zero.sax.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping(path = "/sax/file")
public class GuestController extends BaseController {

    @Resource
    private FileService fileService;


    @GetMapping(path = "/info/{smd5}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SaxResponse> shareInfo(HttpServletRequest request, HttpServletResponse response,
                                                @PathVariable(name = "smd5") String smd5) throws IOException {
        ClientMeta client = clientMeta(request);
        logger.info("ClientVisitSharePage, client:{}", client);
        if(!StringUtils.hasLength(smd5)) {  //
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SaxResponse.notAccessed("Bad request"));
        }
        FileShareOut out = fileService.getShareInfo(smd5);
//        if(out == null || out.getStatus() == 2) {
//            response.setStatus(HttpStatus.NOT_FOUND.value());
//            String msg = (out == null || out.getName() == null) ? "File Not Exist" : ("File [" + out.getName() + "] Not Exist");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SaxResponse.badRequest(msg));
//        }
        return ResponseEntity.ok(SaxResponse.success(out));
    }

    @GetMapping(path = "/get/{smd5}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE})
    public ResponseEntity<SaxResponse>  getByPath(HttpServletRequest request, HttpServletResponse response,
                          @PathVariable(name = "smd5") String smd5) throws IOException {
        return get(request, response, smd5);
    }

    @GetMapping(path = "/get", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE})
    public ResponseEntity<SaxResponse>  get(HttpServletRequest request, HttpServletResponse response,
                    @RequestParam(value = "smd5", required = true) String smd5) throws IOException {
        FileOut out = fileService.get(smd5);
        if(out == null || out.getStatus() == 2) {
            String msg = (out == null || out.getName() == null) ? "File Not Exist" : ("File [" + out.getName() + "] Not Exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SaxResponse.badRequest(msg));
        }
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(out.getPath()))) {
            OutputStream os = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;filename=" + out.getName());
            response.setContentType(request.getServletContext().getMimeType(out.getPath()));
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = bis.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception e) {
            logger.error("GetShareFileError, client:{}, smd5:{}", clientMeta(request), smd5, e);
        }
        return null;
    }
}
