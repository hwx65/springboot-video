package com.example.uploadvideo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {
 
    @PostMapping()
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file) {
        return "jjjjjj";
    }

    @GetMapping("/")
	public String welcome() {
		return "upload";
	}
}