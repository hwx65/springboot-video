package com.example.downloadvideo.controller;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.example.downloadvideo.minioConfig.MinioProp;
import com.google.api.client.util.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;

@Controller
public class DownloadController {

    @Autowired
    private MinioClient minioClient;

    private static String fileName="test11.mp4";

    @RequestMapping(value = "/getVideo", method = RequestMethod.GET)
    @ResponseBody
    public void getPreview2(HttpServletResponse response) {
        try {
			InputStream stream = minioClient.getObject(MinioProp.MINIO_BUCKET, fileName);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename="+fileName.replace(" ", "_"));
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
        }
    }

    @RequestMapping(value = "/getVideo_720p", method = RequestMethod.GET)
    @ResponseBody
    public void getVideo_720p(HttpServletResponse response) {
        try {
			InputStream stream = minioClient.getObject(MinioProp.MINIO_BUCKET1, "720p_" + fileName);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=720p_"+fileName.replace(" ", "_"));
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
        }
    }

    @RequestMapping(value = "/getVideo_360p", method = RequestMethod.GET)
    @ResponseBody
    public void getVideo_360p(HttpServletResponse response) {
        try {
			InputStream stream = minioClient.getObject(MinioProp.MINIO_BUCKET2, "360p_" + fileName);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=360p_"+fileName.replace(" ", "_"));
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
        }
    }


    @RequestMapping(value ="/video/{videoId}", method = RequestMethod.GET)
	public String playVideo(@PathVariable("videoId") String videoId) {
        fileName = videoId;
		return "video";
	}

    @RequestMapping(value ="/playvideo", method = RequestMethod.GET)
	public String playVideoOrigin() {
		return "video";
	}

    @RequestMapping(value ="/playvideo720p", method = RequestMethod.GET)
	public String playVideo720p() {
		return "video_720p";
	}

    @RequestMapping(value ="/playvideo360p", method = RequestMethod.GET)
	public String playVideo360p() {
		return "video_360p";
	}

    @GetMapping(value ="/download_origin")
	public String downloadVideoOrigin() {
		return "redirect:/getVideo";
	}

    @GetMapping(value ="/download_720p")
	public String downloadVideo720p() {
		return "redirect:/getVideo_720p";
	}

    @GetMapping(value ="/download_360p")
	public String downloadVideo360p() {
		return "redirect:/getVideo_360p";
	}
 
    @GetMapping("/")
	public String welcome(Map<String, Object> model) {
        Collection<String> restusts = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects("video");
            Iterator<Result<Item>> iterator = results.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next().get();
                restusts.add(item.objectName() + "\n");
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
        model.put("info", restusts);
		return "welcome";
	}
}