package com.ims.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.wabacus.config.Config;



/**
 * 用户登录
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class Login extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	/*	// URL基本路径
		String basePath = request.getScheme() + "://"+request.getServerName() + ":"
		+ request.getServerPort() + request.getContextPath() + "/";*/
		// 设置request编码
		request.setCharacterEncoding("UTF-8");		
		// 设置响应内容类型
		response.setContentType("text/html");
		// 设置response编码
		response.setCharacterEncoding("UTF-8");
		
		// 获取请求参数：用户名和密码
		String name = request.getParameter("username");
		String password = request.getParameter("password");		
		
		// 登录验证结果  1 成功  2用户名不存在  3 密码输入有误
		int result = 3;
		
		
		Connection conn=Config.getInstance().getDataSource("ds_mysql").getConnection();//取配置的默认数据源的连接
        PreparedStatement pstmt = null;
		ResultSet rs = null;
		// 查询用户名和密码是否匹配，1：正常匹配，2：用户名不存在，3：密码不正确
		try {
	    	//从数据库中获取数据
	    	pstmt = conn.prepareStatement("SELECT nickname FROM d_user WHERE nickname = ? ");
	    	pstmt.setString(1, name);
	    	
	    	rs = pstmt.executeQuery();
	    	//无结果，说明用户名不存在
	    	if (!rs.next()) {	
	        	 result=2;
	        	 
				}
	    	// 用户名存在，验证密码是否正确
	    	else
	    	{
	    		//pstmt=null;//释放一次
	    		pstmt = conn.prepareStatement( "SELECT nickname,role FROM d_user WHERE nickname = ? and password = ?");
	    		pstmt.setString(1,name);
		    	pstmt.setString(2,password);
		    	rs = pstmt.executeQuery();
		    	
				//无结果，说明用户名密码不匹配
		    	if (!rs.next()) {	
		        	 result=3;
					}
		    	//登录成功
		    	else{
		    		// 获取用户角色    0 系统管理员  1普通用户
					int role =  rs.getInt("role");
					result=1;//匹配且成功
					// 保存用户名和用户角色
					HttpSession session = request.getSession();
					session.setAttribute("username", name);
					session.setAttribute("nickname", name);
					session.setAttribute("role", role);					
		    	}
		    		
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
/*		List<Object> list = new ArrayList<Object>();
		
		// 查询用户名是否存在
		String sql1 = "SELECT * FROM YXGLUSER WHERE NAME = ?";		
		try {
			list = DBUtil.executeQuery(sql1,name);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		// 用户名不存在
		if (list.size() == 0) {
			result = 2;
		}
		// 用户名存在，验证密码是否正确
		else {		
			String sql2 = "SELECT * FROM YXGLUSER WHERE NAME = ? AND PASSWORD = ?";
			try {
				list = DBUtil.executeQuery(sql2,name,password);
			} catch (SQLException e) {

				e.printStackTrace();
			}
			// 密码错误
			if (list.size() == 0) {
				result = 3;
			}
			// 登录成功
			else {
				// 获取用户角色    0 系统管理员  1普通用户
				Map<Object, Object> map = (Map<Object, Object>) list.get(0);
				int role =  Integer.parseInt((map.get("ROLE").toString()));
				
				// 保存用户名和用户角色
				HttpSession session = request.getSession();
				session.setAttribute("username", name);
				session.setAttribute("role", role);
				
				// 跳转到系统首页
				//response.sendRedirect(basePath + "index.jsp");
				
			}
		}
*/
		// 以JSON格式返回登录验证结果
        PrintWriter out = response.getWriter();		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", result);		
		out.write(jsonObject.toJSONString());
     	out.flush();
		out.close();
	

	}

}
