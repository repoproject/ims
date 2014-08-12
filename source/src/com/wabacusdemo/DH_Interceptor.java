package com.wabacusdemo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;
/**
 * 电话记录信息管理页面拦截器
 * @创建者： jyp
 * @创建时间：2013-08-14
 * @修改人：zhouhl
 * @修改时间：2013-12-29
 */
public class DH_Interceptor extends AbsInterceptorDefaultAdapter {
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
	 * @创建者：jyp
	 * @修改人：zhouhl
	 * @修改时间：2013-12-30
	 * @返回值：查询语句字符串对象
	 * 
	 */
	 public Object beforeLoadData(ReportRequest rrequest, ReportBean rbean,
			Object typeObj, String sql) {
		
		//设置响应头信息，设定页面无缓存
		rrequest.getWResponse().getResponse().setHeader("Cache-Control","no-cache");
		rrequest.getWResponse().getResponse().setHeader("Cache-Control","no-store");
		rrequest.getWResponse().getResponse().setDateHeader("Expires", 0);
		rrequest.getWResponse().getResponse().setHeader("Pragma", "No-cache");
		
		
		//判断查询数据字符串中是否含有kaishishijian和jieshushijian字符串
		//若没含有则是第一次打开页面，则取最近24小时内的数据
		if (sql.contains("1=1") && !sql.contains("kaishishijian") && !sql.contains("jieshushijian")) {
			
			//获取当前时间
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			String now = sf.format(date);
			rrequest.setAttribute("txtbegin", now);
			rrequest.setAttribute("txtend", now);
			
			//根据当前时间设定查询数据字符串
			sql = sql.replaceAll("1=1", "call_time >= to_date('" + now + " 00:00:00" +"','yyyy-MM-dd hh24:mi:ss') and call_time <= to_date('" + now + " 23:59:59" +"','yyyy-MM-dd hh24:mi:ss')");
		}
		
		String txtbegin = (String)rrequest.getAttribute("txtbegin");
		String txtend = (String)rrequest.getAttribute("txtend");

		//判断页面传过来的查询字符串，若含有kaishishijian和jieshushijian
		//则根据这个值设定查询数据字符串
		if(sql.contains("kaishishijian") && sql.contains("jieshushijian"))
		{
			sql = sql.replaceAll("kaishishijian", "call_time >= to_date('" + txtbegin + " 00:00:00" +"','yyyy-MM-dd hh24:mi:ss')");
			sql = sql.replaceAll("jieshushijian", "call_time <= to_date('" + txtend + " 23:59:59" +"','yyyy-MM-dd hh24:mi:ss')");
	
		}
		
		//若页面传过来的查询字符串只含有kaishishijian而没有jieshushijian
		//则开始时间由传过来的参数确定，结束时间由系统当前日期加上23:59:59确定
		if (sql.contains("kaishishijian") && !sql.contains("jieshushijian")) {
			sql = sql.replaceAll("kaishishijian", "call_time >= to_date('" + txtbegin + " 00:00:00" +"','yyyy-MM-dd hh24:mi:ss')");
		}
		
		//若页面传过来的查询字符串只有jieshushijian而没有kaishishijian
		//则取结束时间之前的所有数据
		if (!sql.contains("kaishishijian") && sql.contains("jieshushijian")) {
			sql = sql.replaceAll("jieshushijian", "call_time <= to_date('" + txtend + " 23:59:59" +"','yyyy-MM-dd hh24:mi:ss')");
		}
		
		//返回设定好条件的数据库查询语句字符串
		return sql;
	}
}
