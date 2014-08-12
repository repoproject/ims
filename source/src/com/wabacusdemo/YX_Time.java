package com.wabacusdemo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.sql.PreparedStatement;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;

/**
 * 业务数据监视页面拦截器
 * @创建者：wangs
 * @创建时间：2013-09-06
 * @修改人：zhouhl
 * @修改时间：2013-12-30
 *
 */
public class YX_Time extends AbsInterceptorDefaultAdapter {

	/**
	 * 每行数据显示到页面之前执行的函数
	 * @创建者：zhouhl
	 * @修改人：zhouhl
	 * @修改时间：2014-06-16
	 * @返回值：无
	 * 
	 */
	public void beforeDisplayReportDataPerRow( ReportRequest rrequest, ReportBean rbean, RowDataBean rowDataBean)
	{
		if(rowDataBean.getRowindex()==-1) return ;//标题行
		if(rowDataBean.getRowindex()%2==1)
		{
			String style=rowDataBean.getRowstyleproperty();
			if(style==null) style="";
			style+=" style='background:#CFDFF8'";
			rowDataBean.setRowstyleproperty(style);
		}
	}
	
	/**
	 * 装载数据之前执行的函数
	 * @创建者：wangs
	 * @创建时间：2013-09-06
	 * @修改人：zhouhl
	 * @修改时间：2013-12-30
	 * @返回值：查询语句字符串对象
	 */
	public Object beforeLoadData(ReportRequest rrequest, ReportBean rbean,
			Object typeObj, String sql) {
		
		//设置响应头信息，设定页面无缓存
		rrequest.getWResponse().getResponse().setHeader("Cache-Control","no-cache");
		rrequest.getWResponse().getResponse().setHeader("Cache-Control","no-store");
		rrequest.getWResponse().getResponse().setDateHeader("Expires", 0);
		rrequest.getWResponse().getResponse().setHeader("Pragma", "No-cache");
		
		//判断查询字符串中是否含有占位符（1=1）和计划开始时间查询条件
		if (sql.contains("1=1") && !sql.contains("businessinfo.PLANSTARTTIME >= to_date") && !sql.contains("businessinfo.PLANENDTIME <= to_date")) {
			
			//没有计划开始时间查询条件时，取当前系统时间最近的24小时为查询时间段
			Date date = new Date();
			
			//设置时间格式
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH");
			
			//获取当前日历信息
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR,-23);
			SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH");
			SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd HH");
			String time = sf1.format(cal.getTime()) + ":00:00";

			//计划开始时间和计划结束时间
			String time1 = sf2.format(cal.getTime()) + ":00:00";
			Calendar ca2 = Calendar.getInstance();
			ca2.add(Calendar.HOUR,+1);
			SimpleDateFormat sf3 = new SimpleDateFormat("yyyy-MM-dd HH");
			String time2 = sf3.format(ca2.getTime()) + ":00:00";
			rrequest.setAttribute("txtbegin1", time1);
			rrequest.setAttribute("txtbegin2", time1);
			rrequest.setAttribute("txtbegin3", time1);
			rrequest.setAttribute("txtbegin4", time1);
			rrequest.setAttribute("txtbegin5", time1);
			rrequest.setAttribute("txtend1", time2);
			rrequest.setAttribute("txtend2", time2);
			rrequest.setAttribute("txtend3", time2);
			rrequest.setAttribute("txtend4", time2);
			rrequest.setAttribute("txtend5", time2);
			String now = sf.format(date) + ":00:00";
			sql = sql.replaceAll("1=1", "(businessinfo.PLANSTARTTIME >= to_date('" + time1 + "','yyyy-MM-dd hh24:mi:ss') or businessinfo.PLANSTARTTIME is null) and (businessinfo.PLANENDTIME <= to_date('" + time2 + "','yyyy-MM-dd hh24:mi:ss') or businessinfo.PLANENDTIME is null)" );
		}
		
		//获取文件类型
		if(rrequest.getAttribute("type") != null){
			String strtype = rrequest.getAttribute("type").toString();
			if(strtype.trim().equalsIgnoreCase("3")){
			    sql = sql.replaceAll("BUSINESSINFO.type =", "BUSINESSINFO.type >=" );
			}
		}
		
		//设置文件名查询条件的sql语句
		String filename = "";
		if (rrequest.getAttribute("filename1") != null ){
			filename = rrequest.getAttribute("filename1").toString();
			
		}
		if (rrequest.getAttribute("filename2") != null ){
			filename = rrequest.getAttribute("filename2").toString();
			
		}
		if (rrequest.getAttribute("filename3") != null ){
			filename = rrequest.getAttribute("filename3").toString();
			
		}
		if (rrequest.getAttribute("filename4") != null ){
			filename = rrequest.getAttribute("filename4").toString();
			
		}
		if (rrequest.getAttribute("filename5") != null ){
			filename = rrequest.getAttribute("filename5").toString();
			
		}
		if (sql.contains("fileName")) {
			if (filename.indexOf("%") > -1) {
				filename = filename.replaceAll("%", "/%");
			}
			if (filename.indexOf("_") > -1) {
				filename = filename.replaceAll("_", "/_");
			}
			sql = sql.replaceAll("fileName", "BUSINESSINFO.filename like '%" + filename +  "%' escape '/'");
		}
		
		//返回数据库查询语句字符串
		return sql;
	}

}
