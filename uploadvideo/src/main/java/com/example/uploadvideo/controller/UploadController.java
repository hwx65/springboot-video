package com.example.uploadvideo.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.example.uploadvideo.minioConfig.MinioProp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import io.minio.MinioClient;

@Controller
public class UploadController {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private RabbitTemplate template;

    @PostMapping("/")
    @ResponseBody
    ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.getSize() == 0) {
            return ResponseEntity.badRequest().build();
        }
        String orgfileName = file.getOriginalFilename();
        try {
            InputStream in = file.getInputStream();
            String contentType = file.getContentType();
            minioClient.putObject(MinioProp.MINIO_BUCKET, orgfileName, in, null, null, null, contentType);
            Map<String, Object> data = new HashMap<>();
            data.put("bucketName", MinioProp.MINIO_BUCKET);
            template.convertAndSend("encodeQueue", orgfileName + "," + contentType);
            return ResponseEntity.ok().body(orgfileName);
        } catch (Exception e) {
            System.out.print(e);
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/")
    public String welcome() {
        try {
            boolean isExist = minioClient.bucketExists("video");
            if (!isExist) {
                minioClient.makeBucket("video");
            }
            isExist = minioClient.bucketExists("720p");
            if (!isExist) {
                minioClient.makeBucket("720p");
            }
            isExist = minioClient.bucketExists("360p");
            if (!isExist) {
                minioClient.makeBucket("360p");
            }
        } catch (Exception e) {
        }
        return "upload";
    }
}