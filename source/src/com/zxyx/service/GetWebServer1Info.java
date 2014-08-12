package com.zxyx.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zxyx.model.WebServer1Info;

/**
 * 获取Web服务器1信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class GetWebServer1Info extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置响应内容类型
		response.setContentType("text/html");
		// 设置response编码
		response.setCharacterEncoding("UTF-8");
		
		// 获取Web服务器1的系统信息
		WebServer1Info webServer1Info = WebServer1Info.getWebServer1Info();
		PrintWriter out = response.getWriter();

		// 将Web服务器1的系统信息转换为JSON格式数据
		JSONObject jsonObject = (JSONObject) JSON.toJSON(webServer1Info);	
		
		out.write(jsonObject.toJSONString());
     	out.flush();
		out.close();
		/*
		System.out.println("WebServer1Info:"+"\n"+jsonObject);
		System.out.println("WebServer1Info:"+"\n"+jsonObject.toJSONString());
		*/
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
}
