package com.zxyx.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.zxyx.util.DBUtil;

/**
 * 修改密码
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class ModifyPassword extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置request编码
		request.setCharacterEncoding("UTF-8");			
		// 设置响应内容类型
		response.setContentType("text/html");
		// 设置response编码
		response.setCharacterEncoding("UTF-8");
			
		// 获取请求参数：用户名，旧密码，新密码，确认密码
		String name = request.getParameter("username");
		String oldPassword = request.getParameter("oldPassword");	
		String newPassword = request.getParameter("newPassword");	
//		String confirmPassword = request.getParameter("confirmPassword");	

		// 修改密码结果  1 成功  0 失败  -1 旧密码输入有误
		int result = 0;
		
		// 查询旧密码是否存在
		String sql1 = "SELECT * FROM YXGLUSER WHERE NAME = ? AND PASSWORD = ?";
		List<Object> list = new ArrayList<Object>();
		try {
			list = DBUtil.executeQuery(sql1,name,oldPassword);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		// 旧密码输入错误
		if (list.size() == 0) {
			result = -1;
		}
		// 更新数据库
		else {
			String sql2 = "UPDATE YXGLUSER SET PASSWORD = ? WHERE NAME = ? ";
			result = DBUtil.executeUpdate(sql2, newPassword, name);
		}
		// 以JSON格式返回修改密码结果
        PrintWriter out = response.getWriter();		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", result);		
		out.write(jsonObject.toJSONString());
     	out.flush();
		out.close();
				
	}

}
