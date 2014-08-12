package com.wabacusdemo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;

/**
 * 业务数据监视拦截器
 * @author jyp
 * @修改人：zhouhl
 * @修改时间：2013-12-30
 *
 */
public class YX_Interceptor extends AbsInterceptorDefaultAdapter {

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
	 * @author jyp
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
		
		//判断查询字符串中是否时间查询条件，若不含有则指定时间为当前时间
		if (sql.contains("1=1") && !sql.contains("time >= to_date") && !sql.contains("time <= to_date")) {
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			String now = sf.format(date);
			sql = sql.replaceAll("1=1", "time = to_date('" + now + "','yyyy-MM-dd')");
		}
		
		//返回数据库查询语句字符串
		return sql;
	}
}
