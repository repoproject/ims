package com.zxyx.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zxyx.model.VoiceWarningConfig;

/**
 * 获取声音告警配置
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class GetVoiceWarningConfig extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置响应内容类型
		response.setContentType("text/html");
		// 设置response编码
		response.setCharacterEncoding("UTF-8");
		
		// 获取声音告警设置对象
		VoiceWarningConfig voiceWarningConfig = VoiceWarningConfig.getVoiceWarningConfig();
		
		//从文件中获取声音告警配置信息
		String pathString = this.getServletContext().getRealPath(File.separator);
	    pathString += "voice" + File.separator + "voiceCongig.txt";
		File file = new File(pathString);
		if (file.exists()) {
			InputStreamReader isrReader = new InputStreamReader(new FileInputStream(file),"utf-8");
			Scanner scaner = new Scanner(isrReader);
			voiceWarningConfig.setWarnSwitch(scaner.nextInt());
			scaner.nextLine();
			voiceWarningConfig.setSerious(scaner.nextInt());
			scaner.nextLine();
			voiceWarningConfig.setImportant(scaner.nextInt());
			scaner.nextLine();
			voiceWarningConfig.setGeneral(scaner.nextInt());
			scaner.nextLine();
			voiceWarningConfig.setStopTimeSwitch(scaner.nextInt());
			scaner.nextLine();
			voiceWarningConfig.setTime(scaner.nextInt());
			scaner.close();
			isrReader.close();
		}
		
		PrintWriter out = response.getWriter();

		// 将声音告警设置转换为JSON格式数据
		JSONObject jsonObject = (JSONObject) JSON.toJSON(voiceWarningConfig);
		out.write(jsonObject.toJSONString());
     	out.flush();
		out.close();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
