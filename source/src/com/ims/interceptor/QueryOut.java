package com.ims.interceptor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;

public class QueryOut extends AbsInterceptorDefaultAdapter {

	public void beforeDisplayReportDataPerRow(ReportRequest rrequest,
			ReportBean rbean, RowDataBean rowDataBean) {
		if (rowDataBean.getRowindex() == -1)
			return;// 标题行
		if (rowDataBean.getRowindex() % 2 == 1) {
			String style = rowDataBean.getRowstyleproperty();
			if (style == null)
				style = "";
			style += " style='background:#CFDFF8'";
			rowDataBean.setRowstyleproperty(style);
		}
	}
	
	/**
	 * 装载数据之前执行的函数
	 * 
	 * @创建者：guanq
	 * @创建时间：2014-08-30
	 * @返回值：查询语句字符串对象
	 */
	public Object beforeLoadData(ReportRequest rrequest, ReportBean rbean,
			Object typeObj, String sql) {

		// 设置响应头信息，设定页面无缓存
		rrequest.getWResponse().getResponse().setHeader("Cache-Control",
				"no-cache");
		rrequest.getWResponse().getResponse().setHeader("Cache-Control",
				"no-store");
		rrequest.getWResponse().getResponse().setDateHeader("Expires", 0);
		rrequest.getWResponse().getResponse().setHeader("Pragma", "No-cache");

		// 判断查询字符串中是否含有占位符（1=1）和开始时间查询条件
		if (sql.contains("1=1")&& 
				!sql.contains("outdate>=DATE")
			&&	!sql.contains("outdate<=DATE")) {


			SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");

			// 获取当前日历信息
			Calendar cal = Calendar.getInstance();
	
			//结束时间默认就是当天
			String time1 = sf1.format(cal.getTime());
			
			//开始时间默认提前2天
			cal.add(Calendar.DATE, -2);
			String time2 = sf2.format(cal.getTime());

		
			rrequest.setAttribute("txtbegin1", time2);
			rrequest.setAttribute("txtend1", time1);
		
			sql = sql
			.replaceAll(
					"1=1",
					"(outdate>=DATE('"
							+ time2
							+ "')) and (outdate<=DATE('"
							+ time1
							+ "'))");
		}

		
		// 返回数据库查询语句字符串
		return sql;
	}
}
