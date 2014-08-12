package com.zxyx.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.zxyx.model.VoiceWarningConfig;

/**
 * 设置声音告警
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class ConfigVoiceWarning extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置request编码
		request.setCharacterEncoding("UTF-8");		
		// 设置响应内容类型
		response.setContentType("text/html");
		// 设置response编码
		response.setCharacterEncoding("UTF-8");
		
		VoiceWarningConfig voiceWarningConfig = VoiceWarningConfig.getVoiceWarningConfig();
		String basePath = request.getScheme() + "://"+request.getServerName() + ":"
		+ request.getServerPort() + request.getContextPath() + "/";
		
		// 设置结果  1 成功  0 失败
		int result = 1;
		
		try {

			// 声音告警开关
		    int warnSwitch = Integer.parseInt(request.getParameter("warnSwitch"));
		    voiceWarningConfig.setWarnSwitch(warnSwitch);   

	    	// 声音告警开关打开
		    if (warnSwitch == 1) {
		    	// 信息级别选择参数
		    	String serious = request.getParameter("serious");
		    	String important = request.getParameter("important");
		    	String general = request.getParameter("general");
		    	
		    	if (serious != null) {
		    		voiceWarningConfig.setSerious(Integer.parseInt(serious));
				}else {
					voiceWarningConfig.setSerious(0);
				}
		    	
		    	if (important != null) {
		    		voiceWarningConfig.setImportant(Integer.parseInt(important));
				}else {
					voiceWarningConfig.setImportant(0);
				}
		    	
		    	if (general != null) {
		    		voiceWarningConfig.setGeneral(Integer.parseInt(general));
				}else {
					voiceWarningConfig.setGeneral(0);
				}
		    			
				// 自动停止时间开关
			    int stopTimeSwitch = Integer.parseInt(request.getParameter("stopTimeSwitch"));
			    voiceWarningConfig.setStopTimeSwitch(stopTimeSwitch);
				// 自动停止时间开关打开
				if (stopTimeSwitch == 1) {
					// 自动停止时间参数
					String time = request.getParameter("time");
					if (time != null) {
						voiceWarningConfig.setTime(Integer.parseInt(time));
					}
				
				}
				
			}
		    //将数据存到文件中
		    String pathString = this.getServletContext().getRealPath(File.separator);
		    pathString += "voice" + File.separator + "voiceCongig.txt";
		    System.out.println(pathString);
		    File file = new File(pathString);
		    OutputStreamWriter osWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
		    PrintWriter pwWriter = new PrintWriter(osWriter);
		    pwWriter.println(voiceWarningConfig.getWarnSwitch());
		    pwWriter.println(voiceWarningConfig.getSerious());
		    pwWriter.println(voiceWarningConfig.getImportant());
		    pwWriter.println(voiceWarningConfig.getGeneral());
		    pwWriter.println(voiceWarningConfig.getStopTimeSwitch());
		    pwWriter.println(voiceWarningConfig.getTime());
		    pwWriter.flush();
		    pwWriter.close();
		    osWriter.close();
		    
		} catch (Exception e) {
			result = 0;
			e.printStackTrace();
		} finally {
			// 以JSON格式返回设置结果  1成功  0失败
	        PrintWriter out = response.getWriter();		
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("result", result);		
			out.write(jsonObject.toJSONString());
	     	out.flush();
			out.close();
		}
					
		// 将声音告警设置存入文件？
		
	}

}
