package com.wabacusdemo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;
/**
 * 告警页面拦截器
 * @创建者： jyp
 * @创建时间：2013-08-14
 * @修改人：zhouhl
 * @修改时间：2013-12-30
 *
 */
public class GJ_Interceptor extends AbsInterceptorDefaultAdapter {

	/**
	 * 装载数据之前执行的函数
	 * @创建者： jyp
	 * @创建时间：2013-08-14
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
		
		//用级别ID替换掉查询字符串中的levelidreplaceme
		if (sql.contains("levelidreplaceme"))
		{
			String levelid=rrequest.getAttribute("txtlevel_id").toString();
			sql = sql.replace("levelidreplaceme", "level_id in ("+levelid+")");
		}
		
		//根据页面传过来的查询数据字符串中的值判断是第一次访问页面还是点击页面的查询功能
		if (sql.contains("1=1") && !sql.contains("kaishishijian") && !sql.contains("jieshushijian")) {
			
			//第一次访问页面，时间默认为当前系统时间
			Date date = new Date();
			
			//获取当前日历信息
			Calendar cal = Calendar.getInstance();
			
			//设置时间显示格式
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
			String now = sf.format(date);
			
			//设定开始时间和结束时间
			String time1 = sf.format(cal.getTime()) + " 00:00:00";
			String time2 = sf1.format(cal.getTime()) + " 23:59:59";
			
			rrequest.setAttribute("txtbegin", time1);
			rrequest.setAttribute("txtend", time2);
			sql = sql.replaceAll("1=1", "time >= to_date('" + time1 +  "','yyyy-MM-dd hh24:mi:ss') and time <= to_date('" + time2 + "','yyyy-MM-dd hh24:mi:ss')");
		}
		String txtbegin = (String)rrequest.getAttribute("txtbegin");
		String txtend = (String)rrequest.getAttribute("txtend");
		
		//判断页面传过来的查询字符串，若含有kaishishijian和jieshushijian
		//则根据这个值设定查询数据字符串
		if(sql.contains("kaishishijian") && sql.contains("jieshushijian"))
		{
			sql = sql.replaceAll("kaishishijian", "time >= to_date('" + txtbegin + "','yyyy-MM-dd hh24:mi:ss')");
			sql = sql.replaceAll("jieshushijian", "time <= to_date('" + txtend + "','yyyy-MM-dd hh24:mi:ss')");
	
		}
		
		//若页面传过来的查询字符串只含有kaishishijian而没有jieshushijian
		//则开始时间由传过来的参数确定，结束时间由系统当前日期加上23:59:59确定
		if (sql.contains("kaishishijian") && !sql.contains("jieshushijian")) {
			sql = sql.replaceAll("kaishishijian", "time >= to_date('" + txtbegin + "','yyyy-MM-dd hh24:mi:ss')");
		}
		
		//若页面传过来的查询字符串只有jieshushijian而没有kaishishijian
		//则取结束时间之前的所有数据
		if (!sql.contains("kaishishijian") && sql.contains("jieshushijian")) {
			sql = sql.replaceAll("jieshushijian", "time <= to_date('" + txtend + "','yyyy-MM-dd hh24:mi:ss')");
		}
		//设置查村条件的sql语句
		if (rrequest.getAttribute("comm") != null) {
			String filename = rrequest.getAttribute("comm").toString();
			if (sql.contains("textName")) {
				if (filename.indexOf("%") > -1) {
					filename = filename.replaceAll("%", "/%");
				}
				if (filename.indexOf("_") > -1) {
					filename = filename.replaceAll("_", "/_");
				}
				sql = sql.replaceAll("textName", "comm like '%" + filename +  "%' escape '/'");
			}
			
		}
		//返回数据库查询语句字符串
		return sql;
	}
	
	/**
	 * 每行报表数据显示之前执行的处理函数
	 *@创建者： jyp
	 * @创建时间：2013-08-14
	 * @修改者：zhouhl
	 * @修改时间：2013-12-31
	 * 返回值：无
	 */
	public void beforeDisplayReportDataPerRow(ReportRequest rrequest, ReportBean rrbean, RowDataBean rowDataBean)
	{
		//若数据行数为-1，则返回
		if(rowDataBean.getRowindex()==-1){
			return ;
		}
		
		//若数据行数为奇数行，则添加下面的样式
		if(rowDataBean.getRowindex()%2==1)
		{
			String style=rowDataBean.getRowstyleproperty();
			if(style==null) style="";
			style+=" style='background:#CFDFF8'";
			rowDataBean.setRowstyleproperty(style);
		}
	}

}
