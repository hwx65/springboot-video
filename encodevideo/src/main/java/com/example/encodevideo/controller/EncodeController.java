package com.example.encodevideo.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.encodevideo.minioConfig.MinioProp;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import io.minio.MinioClient;
import io.minio.ObjectStat;

@Controller
public class EncodeController{
    private static final boolean NON_DURABLE = false;
	private static final String MY_QUEUE_NAME = "encodeQueue";
    
	// @Bean
	// public ApplicationRunner runner(RabbitTemplate template) {
	// 	return args -> {
	// 		template.convertAndSend("myQueue", "Hello, world!");
	// 	};
	// }

	@Autowired
    private MinioClient minioClient;

	@Bean
	public Queue encodeQueue() {
		return new Queue(MY_QUEUE_NAME, NON_DURABLE);
	}

	@RabbitListener(queues = MY_QUEUE_NAME)
	public void listen(String fileInfo) {
		String[] arr = fileInfo.split(",");
		String fileName = arr[0];
		String contentType = arr[1];
		try{
			ObjectStat statObject = minioClient.statObject(MinioProp.MINIO_BUCKET, fileName);
			if (statObject != null && statObject.length() > 0) {
				InputStream stream = minioClient.getObject(MinioProp.MINIO_BUCKET, fileName);

				

				minioClient.putObject(MinioProp.MINIO_BUCKET1,fileName,stream ,null, null, null, contentType);
				Map<String,Object> data=new HashMap<>();
				data.put("bucketName",MinioProp.MINIO_BUCKET1);
				data.put("fileName",fileName);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
    }

	public static Boolean transform(String ffmpegPath, String oldPath, String newPath, String resolution) throws FFmpegException {
        boolean flag = transform("D:\\ffmpeg\\ffmpeg2016\\bin\\ffmpeg.exe", "d:\\ys\\StoryBrooke.mp4", "d:\\ys\\480p.flv", "480x320");
		List<String> command = getFfmpegCommand(ffmpegPath, oldPath, newPath, resolution);
        if (null != command && command.size() > 0) {
            return process(command);
        }
        return false;
    }

    private static boolean process(List<String> command) throws FFmpegException {
        try {
            if (null == command || command.size() == 0)
                return false;
            Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
            videoProcess.getInputStream().close();
            int exitcode = videoProcess.waitFor();
            if (exitcode == 1)
                return false;
            return true;
        } catch (Exception e) {
        }
		return true;
    }

    private static List<String> getFfmpegCommand(String ffmpegPath, String oldfilepath, String outputPath, String resolution) throws FFmpegException {
        List<String> command = new ArrayList<String>();
        command.add(ffmpegPath); // 添加转换工具路径
        command.add("-i"); // 添加参数＂-i＂，该参数指定要转换的文件
        command.add(oldfilepath); // 添加要转换格式的视频文件的路径
        command.add("-qscale"); // 指定转换的质量
        command.add("4");
        
        /*command.add("-ab"); //设置音频码率
        command.add("64"); 
        command.add("-ac"); //设置声道数 
        command.add("2"); 
        command.add("-ar"); //设置声音的采样频率
        command.add("22050");*/
         
        command.add("-r"); // 设置帧速率
        command.add("24");
        command.add("-s"); // 设置分辨率
        command.add(resolution);
        command.add("-y"); // 添加参数＂-y＂，该参数指定将覆盖已存在的文件
        command.add(outputPath);
        return command;
    }


	class FFmpegException extends Exception {

		private static final long serialVersionUID = 1L;
	
		public FFmpegException() {
			super();
		}
	
		public FFmpegException(String message) {
			super(message);
		}
	
		public FFmpegException(Throwable cause) {
			super(cause);
		}
	
		public FFmpegException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}