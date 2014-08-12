package com.zxyx.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zxyx.model.CPUUsageInfo;
import com.zxyx.model.ServerInfo;
import com.zxyx.util.ParseData;

/**
 * 获取服务器信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class GetServerInfo extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置响应内容类型
		response.setContentType("text/html");
		// 设置response编码
		response.setCharacterEncoding("UTF-8");		
		// 设置request编码
		request.setCharacterEncoding("UTF-8");
		
		// 服务器标识
		int serverID = Integer.parseInt(request.getParameter("serverID"));
		
		// 根据服务器标识获取服务器信息实例
		ServerInfo serverInfo = ParseData.getServerInfo(serverID);		
		
		
		
		// 测试用：在CPU列表中添加64个cpu信息
		/*ArrayList<CPUUsageInfo> cpuUsageInfos = new ArrayList<CPUUsageInfo>(64);
		for(int k = 0; k < 64; k++){
			CPUUsageInfo cpuUsageInfoADD = new CPUUsageInfo();
			cpuUsageInfoADD.setCpuID(k);
			cpuUsageInfoADD.setCpuUsage((float)(Math.random()+2)*10);
			cpuUsageInfos.add(k, cpuUsageInfoADD);	
		}	
		serverInfo.setCpuUsageInfos(cpuUsageInfos);
		serverInfo.setCpuCount(64);
		int count = serverInfo.getCpuInfoCnt()+1;
		serverInfo.setCpuInfoCnt(count);
		*/
		
	    // 将服务器信息转换为JSON格式数据
		JSONObject jsonObject = (JSONObject) JSON.toJSON(serverInfo);
		PrintWriter out = response.getWriter();
		out.write(jsonObject.toJSONString());
		out.flush();
		out.close();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
