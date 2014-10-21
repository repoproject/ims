package com.ims.interceptor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
public class Rate extends AbsInterceptorDefaultAdapter {
	
	/**
	 * 装载数据之前执行的函数
	 * 
	 * @创建者：guanq
	 * @创建时间：2014-08-06
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
				!sql.contains("d_rate.startDateTime>=DATE")
			&&	!sql.contains("d_rate.startDateTime<=DATE")) {

			SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");

			// 获取当前日历信息
			Calendar cal = Calendar.getInstance();
	
			//结束时间默认就是当天
			String time1 = sf1.format(cal.getTime());
			
			//开始时间默认提前2天
			cal.add(Calendar.MONTH, -6);
			String time2 = sf2.format(cal.getTime());

		
			rrequest.setAttribute("txtbegin1", time2);
			rrequest.setAttribute("txtend1", time1);
		
			sql = sql
					.replaceAll(
							"1=1",
							"(d_rate.startDateTime>=DATE('"
									+ time2
									+ "')) and (d_rate.startDateTime<=DATE('"
									+ time1
									+ "'))");
		}

		
		// 返回数据库查询语句字符串
		return sql;
	}
}
