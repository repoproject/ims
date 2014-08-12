package com.zxyx.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zxyx.model.MsgWarningConfig;
import com.zxyx.util.DBUtil;

/**
 * 获取短信告警配置
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class GetMsgWarningConfig extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置响应内容类型
		response.setContentType("text/html");
		// 设置response编码
		response.setCharacterEncoding("UTF-8");
		
		// 获取短信告警设置
		MsgWarningConfig msgWarningConfig = MsgWarningConfig.getMsgWarningConfig();
		PrintWriter out = response.getWriter();
		
		int updateResult1 = 0;
		List<Object> list = new ArrayList<Object>();
		String selectStr = "SELECT * from yxgldxgjpz";
		try {
			list = DBUtil.executeQuery(selectStr);
			int resultCounts = list.size();
			
			Map<Object, Object> map = (Map<Object, Object>) list.get(0);
			int warnSwitch =  Integer.parseInt((map.get("DXGJKQ").toString()));
			String levelStr =  (map.get("JB").toString());
			int intervalSwitch =  Integer.parseInt((map.get("KQJG").toString()));
			int intervalValue =  Integer.parseInt((map.get("JG").toString()));
			int serious = 0;
			int important = 0;
			int general = 0;
			if(levelStr.indexOf("10") >= 0){
				 serious = 1;
			}
			if(levelStr.indexOf("20") >= 0){
				important = 1;
			}
			if(levelStr.indexOf("30") >= 0){
				general = 1;
			}
			msgWarningConfig.setWarnSwitch(warnSwitch);
			msgWarningConfig.setSerious(serious);
			msgWarningConfig.setImportant(important);
			msgWarningConfig.setGeneral(general);
			msgWarningConfig.setIntervalSwitch(intervalSwitch);
			msgWarningConfig.setTime(intervalValue);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 将短信告警设置转换为JSON格式数据
		JSONObject jsonObject = (JSONObject) JSON.toJSON(msgWarningConfig);
		out.write(jsonObject.toJSONString());
     	out.flush();
		out.close();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
