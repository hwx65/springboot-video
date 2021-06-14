package com.example.downloadvideo.controller;


import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.downloadvideo.minioConfig.MinioProp;
import com.google.api.client.util.IOUtils;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.minio.MinioClient;

@Controller
public class DownloadController {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private RabbitTemplate template;

    private static final boolean NON_DURABLE = false;
	private static final String MY_QUEUE_NAME = "downloadQueue";

    private static String fileName;

    @Bean
	public Queue encodeQueue() {
		return new Queue(MY_QUEUE_NAME, NON_DURABLE);
	}

	@RabbitListener(queues = MY_QUEUE_NAME)
	public void listen(String fileInfo) {
        System.out.print(fileInfo);
        fileName = fileInfo;
    }

    @RequestMapping(value = "/getVido", method = RequestMethod.GET)
    @ResponseBody
    public void getVido(HttpServletRequest request, HttpServletResponse response) {
        try {
            InputStream inputStream = minioClient.getObject(MinioProp.MINIO_BUCKET, fileName);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            response.setContentType("video/mp4");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + diskfilename + "\"");
            System.out.println("data.length " + data.length);
            response.setContentLength(data.length);
            response.setHeader("Content-Range", "" + Integer.valueOf(data.length - 1));
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Etag", "W/\"9767057-1323779115364\"");
            OutputStream os = response.getOutputStream();

            os.write(data);
            //先声明的流后关掉！
            os.flush();
            os.close();
            inputStream.close();

        } catch (Exception e) {

        }
    }

    @RequestMapping(value = "/preview2", method = RequestMethod.GET)
    @ResponseBody
    public void getPreview2( HttpServletResponse response) {
        try {
			InputStream stream = minioClient.getObject(MinioProp.MINIO_BUCKET, fileName);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename="+fileName.replace(" ", "_"));
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (java.nio.file.NoSuchFileException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
 
    @GetMapping("/")
	public String welcome() {
		return "video";
	}
}