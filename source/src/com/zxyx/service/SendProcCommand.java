package com.zxyx.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.zxyx.util.SendData;

/**
 * 发送进程控制指令
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class SendProcCommand extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置request编码
		request.setCharacterEncoding("UTF-8");		
		
		// 获取进程控制指令参数
		String sender = request.getParameter("user");	// 用户名
		String senderLocation = request.getParameter("ipAddr");	// 客户端IP
		int serverID = Integer.parseInt(request.getParameter("serverID"));	// 服务器ID
		String softID = request.getParameter("softName");	// 软件标识
		byte command = Byte.parseByte(request.getParameter("commandType"));	// 指令类别
		
		// 发送进程控制指令
		int result = SendData.sendProcCommand(sender, senderLocation, serverID, softID, command);
		PrintWriter out = response.getWriter();
		
		// 以JSON格式返回控制指令是否发送成功  1成功  0失败
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", result);		
		out.write(jsonObject.toJSONString());
     	out.flush();
		out.close();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {	
		doGet(request, response);
			
	}

}
