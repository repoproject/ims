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
 * 
 * @创建者： zhouhan
 * @创建时间：2013-09-27
 * @修改人：zhouhl
 * @修改时间：2013-12-29
 *
 */
public class FTP_TIME2 extends AbsInterceptorDefaultAdapter
{
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
	 * @创建者： zhouhan
	 * @创建时间：2013-09-27
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
		
		//根据页面传过来的查询数据字符串中的值判断是第一次访问页面还是点击页面的查询功能
		if (sql.contains("1=1") && !sql.contains("t.ftptime >= to_date") && !sql.contains("t.ftptime <= to_date")) {
			
			//第一次访问页面，时间默认为当前系统时间
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH");
			
			//获取当前日历信息
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR,-1);
			
			//设置时间显示格式
			SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH");
			SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd HH");
			String time = sf1.format(cal.getTime()) + ":00:00";
			
			//设定开始时间
			String time1 = sf2.format(cal.getTime()) + ":00:00";
			Calendar ca2 = Calendar.getInstance();
			
			//设定结束时间
			SimpleDateFormat sf3 = new SimpleDateFormat("yyyy-MM-dd HH");
			String time2 = sf3.format(ca2.getTime()) + ":00:00";
			rrequest.setAttribute("txtbegin1", time1);
			rrequest.setAttribute("txtend1", time2);
			
			String now = sf.format(date) + ":00:00";
			sql = sql.replaceAll("1=1", "t.ftptime = to_date('" + now + "','yyyy-MM-dd hh24:mi:ss')");
		}
		//设置查村条件的sql语句
		if (rrequest.getAttribute("txtname") != null) {
			String filename = rrequest.getAttribute("txtname").toString();
			if (sql.contains("textName")) {
				if (filename.indexOf("%") > -1) {
					filename = filename.replaceAll("%", "/%");
				}
				if (filename.indexOf("_") > -1) {
					filename = filename.replaceAll("_", "/_");
				}
				sql = sql.replaceAll("textName", "filename like '%" + filename +  "%' escape '/'");
			}
			
		}
		//返回数据库查询语句字符串
		return sql;
	}
}
