package com.example.encodevideo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.example.encodevideo.minioConfig.MinioProp;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import io.minio.MinioClient;

@Controller
public class EncodeController {
	private static final boolean NON_DURABLE = false;
	private static final String MY_QUEUE_NAME = "encodeQueue";
	private java.util.Queue<String> FileQueue = new LinkedList<String>();
	private boolean isRunning = false;
	private int MaxNum = 5;
	private int ThreadNum = 0;

	// @Bean
	// public ApplicationRunner runner(RabbitTemplate template) {
	// return args -> {
	// template.convertAndSend("myQueue", "Hello, world!");
	// };
	// }

	@Autowired
	private MinioClient minioClient;

	@Bean
	public Queue encodeQueue() {
		return new Queue(MY_QUEUE_NAME, NON_DURABLE);
	}

	@RabbitListener(queues = MY_QUEUE_NAME)
	public void listen(String fileInfo) {
		FileQueue.add(fileInfo);
		if (!isRunning){
			isRunning = true;
			new Thread(){
				@Override
				public void run(){
					encoding();
					isRunning = false;
				}
			}.start();
		}
	}

	public void encoding() {
		while (FileQueue.size() > 0) {
			System.out.print("ThreadNum:   " + ThreadNum + "\n");
			System.out.print("FizeSize:   " + FileQueue.size() + "\n");
			if (ThreadNum < MaxNum) {
				String fileInfo = FileQueue.remove();
				System.out.print(fileInfo);
				ThreadNum++;
				new Thread() {
					@Override
					public void run() {
						encodeFunc(fileInfo);
						ThreadNum--;
					}
				}.start();
			}
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
	}

	private void encodeFunc(String fileInfo) {
		String[] arr = fileInfo.split(",");
		String fileName = arr[0];
		String contentType = arr[1];
		try {
			InputStream stream = minioClient.getObject(MinioProp.MINIO_BUCKET, fileName);

			File originFile = File.createTempFile("originFile", ".mp4");
			FileOutputStream fos = new FileOutputStream(originFile);
			int count = 0;
			byte[] b = new byte[100];

			while ((count = stream.read(b)) != -1) {
				fos.write(b, 0, count);
			}
			stream.close();
			fos.close();

			File file_720p = File.createTempFile("file_720p", ".mp4");
			File file_360p = File.createTempFile("file_360p", ".mp4");

			transform("..\\ffmpeg\\bin\\ffmpeg.exe", originFile.getAbsolutePath(),
					file_720p.getAbsolutePath(), "1280x720");
			transform("..\\ffmpeg\\bin\\ffmpeg.exe", originFile.getAbsolutePath(),
					file_360p.getAbsolutePath(), "600x360");

			InputStream in_720p = new FileInputStream(file_720p);
			InputStream in_360p = new FileInputStream(file_360p);
			minioClient.putObject(MinioProp.MINIO_BUCKET1, "720p_" + fileName, in_720p, null, null, null, contentType);
			minioClient.putObject(MinioProp.MINIO_BUCKET2, "360p_" + fileName, in_360p, null, null, null, contentType);

			originFile.delete();
			file_720p.delete();
			file_360p.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Boolean transform(String ffmpegPath, String oldPath, String newPath, String resolution) {
		List<String> command = getFfmpegCommand(ffmpegPath, oldPath, newPath, resolution);
		if (null != command && command.size() > 0) {
			return process(command);
		}
		return false;
	}

	private static boolean process(List<String> commands) {
		try {
			ProcessBuilder pb = new ProcessBuilder(commands);
			pb.redirectErrorStream(true);
			Process p = pb.start();// 启动进程

			BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream(), "gbk"));
			String line;
			while ((line = is.readLine()) != null) {
			}
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static List<String> getFfmpegCommand(String ffmpegPath, String oldfilepath, String outputPath,
			String resolution) {
		List<String> command = new ArrayList<String>();
		command.add(ffmpegPath); // 添加转换工具路径
		command.add("-i"); // 添加参数＂-i＂，该参数指定要转换的文件
		command.add(oldfilepath); // 添加要转换格式的视频文件的路径

		command.add("-s"); // 设置分辨率
		command.add(resolution);
		command.add("-y");
		command.add(outputPath);
		return command;
	}
}