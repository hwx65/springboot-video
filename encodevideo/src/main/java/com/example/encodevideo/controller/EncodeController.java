package com.example.encodevideo.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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

				File originFile = new File("originFile.mp4");
				FileOutputStream fos = new FileOutputStream(originFile);
				int count=0;
				byte[] b = new byte[100];
		
				while((count = stream.read(b)) != -1) {                
					fos.write(b, 0,count);
				}

				stream.close();
				fos.close();

				File file_720p = new File("file_720p.mp4");

				System.out.print(originFile.getAbsolutePath());
				boolean flag = transform("D:\\Program\\ffmpeg-4.4-essentials_build\\bin\\ffmpeg.exe", originFile.getAbsolutePath(), file_720p.getAbsolutePath(), "1280x720");

				InputStream in_720p = new FileInputStream(file_720p);
				minioClient.putObject(MinioProp.MINIO_BUCKET1,"720p_"+fileName,in_720p,null, null, null, contentType);
				Map<String,Object> data=new HashMap<>();
				data.put("bucketName",MinioProp.MINIO_BUCKET1);
				data.put("fileName","_720p" + fileName);

				// originFile.delete();
				// file_720p.delete();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
    }

	public static Boolean transform(String ffmpegPath, String oldPath, String newPath, String resolution){
		List<String> command = getFfmpegCommand(ffmpegPath, oldPath, newPath, resolution);
        if (null != command && command.size() > 0) {
            return process(command);
        }
        return false;
    }

    private static boolean process(List<String> commands) {
		try{
			ProcessBuilder pb = new ProcessBuilder(commands);
			pb.redirectErrorStream(true);
			Process p = pb.start();//启动进程

			BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream(),"gbk"));
			String line;
			while ((line = is.readLine()) != null) {
				if (line.toLowerCase().startsWith("warning")) {
					System.err.println("\tWARNING: " + line);
				} else if (line.toLowerCase().startsWith("error")) {
					System.err.println("\tERROR: " + line);
				} else if (line.toLowerCase().startsWith("fatal")) {
					System.err.println("\tFATAL ERROR: " + line);
				} else {
					System.out.println("\t" + line);
				}
			}
			p.waitFor();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

    private static List<String> getFfmpegCommand(String ffmpegPath, String oldfilepath, String outputPath, String resolution) {
        List<String> command = new ArrayList<String>();
        command.add(ffmpegPath); // 添加转换工具路径
        command.add("-i"); // 添加参数＂-i＂，该参数指定要转换的文件
        command.add(oldfilepath); // 添加要转换格式的视频文件的路径
		command.add("-b:v");
		command.add("10000k");
        
        command.add("-s"); // 设置分辨率
        command.add(resolution);
		command.add("-y");
        command.add(outputPath);
        return command;
    }
}