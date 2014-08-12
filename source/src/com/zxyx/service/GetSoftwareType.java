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
 * 获取软件类型
  * @创建者 yyy
 * @创建时间 2013-12-16
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class GetSoftwareType extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// 设置响应内容类型
		response.setContentType("text/html");
		// 设置response编码
		response.setCharacterEncoding("UTF-8");
		
		// 查询软件类别
		String sql = "SELECT ID,NAME FROM SOURCE WHERE (TYPE = ? OR TYPE = ?) AND NAME <> ? AND NAME <> ?";
		String type1 = "软件";
		String type2 = "功能";
		String name1 = "系统场景可视化软件";
		String name2 = "产品管理和服务软件";
			
		List<Object> softwareList = new ArrayList<Object>();
		try {
			softwareList = DBUtil.executeQuery(sql, type1, type2, name1, name2);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		// 以JSON格式返回修改密码结果
        PrintWriter out = response.getWriter();		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("softwareList", softwareList);		
		out.write(jsonObject.toJSONString());
     	out.flush();
		out.close();
				
	}

}
