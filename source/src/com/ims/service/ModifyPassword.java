package com.ims.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ims.util.DBUtil;
import com.wabacus.config.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.buttons.EditableReportSQLButtonDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.*;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;

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
		Connection conn=Config.getInstance().getDataSource("ds_mysql").getConnection();//取配置的默认数据源的连接
        PreparedStatement pstmt = null;
		ResultSet rs = null;
	
		// 查询旧密码是否存在
		try {
	    	//从数据库中获取数据
	    	pstmt = conn.prepareStatement("select nickName from d_user");
	    	rs = pstmt.executeQuery();
	    	while (rs.next()) {	
	        	 
				}
	        
	  }catch (SQLException e) {
			e.printStackTrace();
	  }finally{
	  	// 关闭结果集
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// 关闭Statement
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// 关闭数据库连接
		if (conn != null) {
			try {
				conn.close();
				conn=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
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

