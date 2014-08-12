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
import com.zxyx.model.MsgWarningConfig;
import com.zxyx.util.DBUtil;


/**
 * 设置短信告警
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class ConfigMsgWarning extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 设置request编码
		request.setCharacterEncoding("UTF-8");		
		// 设置响应内容类型
		response.setContentType("text/html");
		// 设置response编码
		response.setCharacterEncoding("UTF-8");
		
		MsgWarningConfig msgWarningConfig = MsgWarningConfig.getMsgWarningConfig();
		
		// 设置结果  1 成功  0 失败
		int result = 0;
		try {			
			// 短信告警开关
			String warningLevel = "";
		    int warnSwitch = Integer.parseInt(request.getParameter("warnSwitch"));		   
	    	msgWarningConfig.setWarnSwitch(warnSwitch);   

	    	// 短信告警开关打开
		    if (warnSwitch == 1) {
		    	// 保存信息级别选择参数
		    	
		    	 String serious = request.getParameter("serious");
			     String important = request.getParameter("important");
			     String general = request.getParameter("general");
			     
		    	if (serious != null) {
		    		msgWarningConfig.setSerious(Integer.parseInt(serious));
		    		warningLevel = "10";
				}else{
					msgWarningConfig.setSerious(0);
					warningLevel = "";
				}
		    	
		    	if (important != null) {
		    		msgWarningConfig.setImportant(Integer.parseInt(important));
		    		if("".equalsIgnoreCase(warningLevel)){
		    			warningLevel += "20";
		    		}else{
		    			warningLevel += ",20";
		    		}
				}else{
					msgWarningConfig.setImportant(0);
				}
		    	
		    	if (general != null) {
		    		msgWarningConfig.setGeneral(Integer.parseInt(general));
		    		if("".equalsIgnoreCase(warningLevel)){
		    			warningLevel += "30";
		    		}else{
		    			warningLevel += ",30";
		    		}
				}else{
					msgWarningConfig.setGeneral(0);
				}
				
				// 相同告警发送间隔开关
			    int intervalSwitch = Integer.parseInt(request.getParameter("intervalSwitch"));
				msgWarningConfig.setIntervalSwitch(intervalSwitch);
				// 相同告警发送间隔开关打开
				int timeValue = 0;
				if (intervalSwitch == 1) {
					// 保存相同告警发送间隔参数
					String time = request.getParameter("time");
					if (time != null) {
						timeValue = Integer.parseInt(time);
						msgWarningConfig.setTime(timeValue);
					}
				}
				
				//将短信配置数据存入数据库
			    String deleteStr = "DELETE FROM yxgldxgjpz";
			    String insertStr = "INSERT INTO yxgldxgjpz(id,DXGJKQ,JB,KQJG,JG) VALUES(1,1,?,?,?)";
			    int updateResult = 0;
				try {
					updateResult = DBUtil.executeUpdate(deleteStr);
						updateResult = DBUtil.executeUpdate(insertStr, warningLevel,intervalSwitch,timeValue);
						if(updateResult != 0){
							result = 1;
						}
					
				} catch (Exception e) {

					e.printStackTrace();
				}
				
			}else{
			//短信告警开关关闭
				int updateResult1 = 0;
				List<Object> list = new ArrayList<Object>();
				String selectStr = "SELECT COUNT(*) from yxgldxgjpz";
				String updateStr = "UPDATE yxgldxgjpz SET DXGJKQ = 0";
				String insertStr = "INSERT INTO yxgldxgjpz VALUES(1,0,'',0,0)";
				try {
					list = DBUtil.executeQuery(selectStr);
					if(list.size() != 0){
						updateResult1 = DBUtil.executeUpdate(updateStr);
						if(updateResult1 != 0){
							result = 1;
						}
					}else{
						updateResult1 = DBUtil.executeUpdate(insertStr);
						if(updateResult1 != 0){
							result = 1;
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		    
		} catch (Exception e) {
			result = 0;
		} finally {
			// 以JSON格式返回设置结果  1成功  0失败
	        PrintWriter out = response.getWriter();		
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("result", result);		
			out.write(jsonObject.toJSONString());
	     	out.flush();
			out.close();
		}
		
	
		// 将短信告警设置存入文件？
		
	}

}
