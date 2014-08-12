package com.zxyx.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户退出登录
 * @创建者 yyy
 * @创建时间 2013-11-21
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class Logout extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// URL基本路径
		String basePath = request.getScheme() + "://"+request.getServerName() + ":"
		+ request.getServerPort() + request.getContextPath() + "/";
		// 设置响应内容类型
		response.setContentType("text/html");
		// 设置response编码
		response.setCharacterEncoding("UTF-8");

			
		// 清除session中的用户信息
		HttpSession session = request.getSession();
		session.removeAttribute("username");
		session.removeAttribute("role");
		session.invalidate();

		// 跳转到登录页面
		response.sendRedirect(basePath + "login.jsp");
	

				
	}

}
