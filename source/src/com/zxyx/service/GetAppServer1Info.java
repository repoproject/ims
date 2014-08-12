package com.zxyx.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zxyx.model.AppServer1Info;

/**
 * 获取应用服务器1信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class GetAppServer1Info extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置响应内容类型
		response.setContentType("text/html");
		// 设置response编码
		response.setCharacterEncoding("UTF-8");
		
		// 获取应用服务器1的系统信息
		AppServer1Info appServer1Info = AppServer1Info.getAppServer1Info();
		PrintWriter out = response.getWriter();

		// 将应用服务器1的系统信息转换为JSON格式数据
		JSONObject jsonObject = (JSONObject) JSON.toJSON(appServer1Info);
		out.write(jsonObject.toJSONString());
     	out.flush();
		out.close();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
}
