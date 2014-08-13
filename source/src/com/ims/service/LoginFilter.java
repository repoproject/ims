package com.ims.service;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;

/**
 * 登录过滤器
 * @创建者 yyy
 * @创建时间 2013-11-21
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class LoginFilter implements Filter {

	public void init(FilterConfig arg0) throws ServletException {
		
	}
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
//		String basePath = request.getContextPath() + "/";
		// URL基本路径
//		String basePath = request.getScheme() + "://"+request.getServerName() + ":"
//		+ request.getServerPort() + request.getContextPath() + "/";
		RequestDispatcher dispatcher = req.getRequestDispatcher("login.jsp");
		HttpSession session = request.getSession(true);

		// 从session中获取路径和用户名
		String path = request.getRequestURI();
		String username = (String) session.getAttribute("username");
		
		if (path.indexOf("/login.jsp") > -1 || path.indexOf("relogin.jsp") > -1) {
			chain.doFilter(request, response);
			return;
		}

		// 判断是否已经登录
		if (username == null || "".equals(username)) {
			// 尚未登录，跳转到登录页面
			//dispatcher.forward(req, res);
			response.sendRedirect("/zxyx/relogin.jsp");
		} else {
			// 已经登录，继续此次请求
			chain.doFilter(request, response);
		}
		
		
	
	}
	public void destroy() {

	}
}
