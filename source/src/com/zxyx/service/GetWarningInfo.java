package com.zxyx.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zxyx.model.WarningInfo;

/**
 * 获取告警信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class GetWarningInfo extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置响应内容类型
		response.setContentType("text/html");
		// 设置response编码
		response.setCharacterEncoding("UTF-8");
		
		// 获取告警信息
		WarningInfo warningInfo = WarningInfo.getWarningInfo();
		PrintWriter out = response.getWriter();
		
		// 打印告警信息：WarningInfo
//		System.out.println(warningInfo);

		try {
			// 将告警信息转换为JSON格式数据
			JSONObject jsonObject = (JSONObject) JSON.toJSON(warningInfo);
			out.write(jsonObject.toJSONString());
		} catch (Exception e) {
			System.out.println("告警信息数：" + warningInfo.getWarningInternals().size() + "\n" + "warninginfo" + warningInfo.toString());
			e.printStackTrace();
		}finally {
			out.flush();
			out.close();
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
