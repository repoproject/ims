package com.ims.interceptor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ims.rule.InOutRule;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditableReportEditDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportDeleteDataBean;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;

public class Out extends AbsInterceptorDefaultAdapter {
	private static Logger log = Logger.getLogger(Out.class);

	/*****
	 * 设置快过期的库存变红色
	 */
	public void beforeDisplayReportDataPerRow(ReportRequest rrequest,
			ReportBean rbean, RowDataBean rowDataBean) {
		if (rowDataBean.getRowindex() == -1)
			return;// 标题行
		if (rowDataBean.getRowindex() % 2 == 1) {
			String style = rowDataBean.getRowstyleproperty();
			if (style == null)
				style = "";
			style += " style='background:#CFDFF8'";//有效期小于7天则红色提示
			rowDataBean.setRowstyleproperty(style);
		}
		if (rowDataBean.getColData("ISEXP") != null
				&& rowDataBean.getColData("ISEXP").equals("1")) {
			String style = rowDataBean.getRowstyleproperty();
			if (style == null)
				style = "";
			style += " style='background:#FF0000'";//有效期小于90天则黄色提示
			rowDataBean.setRowstyleproperty(style);
		}else if (rowDataBean.getColData("ISEXP") != null
					&& rowDataBean.getColData("ISEXP").equals("2")) {
				String style = rowDataBean.getRowstyleproperty();
				if (style == null)
					style = "";
				style += " style='background:#FFFF00'";
				rowDataBean.setRowstyleproperty(style);
			}
	}

	/**
	 * 装载数据之前执行的函数。设置初始化页面时时间条件为最近三天的函数
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
		if (sql.contains("1=1") && !sql.contains("outdate>=DATE")
				&& !sql.contains("outdate<=DATE")) {

			SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");

			// 获取当前日历信息
			Calendar cal = Calendar.getInstance();

			// 结束时间默认就是当天
			String time1 = sf1.format(cal.getTime());

			// 开始时间默认提前2天
			cal.add(Calendar.DATE, -2);
			String time2 = sf2.format(cal.getTime());

			rrequest.setAttribute("txtbegin1", time2);
			rrequest.setAttribute("txtend1", time1);

			sql = sql.replaceAll("1=1", "(outdate>=DATE('" + time2
					+ "')) and (outdate<=DATE('" + time1 + "'))");
		}

		// 返回数据库查询语句字符串
		return sql;
	}

	/***
	 * 出库删除时的业务规则判断： 判断出库时间是否晚于上次R统计，晚于才能删除，否则不能删除
	 */
	public int doSavePerRow(ReportRequest rrequest, ReportBean rbean,
			Map<String, String> row, Map<String, String> mParamValues,
			AbsEditableReportEditDataBean editbean) {

		// 出库时间
		String stroutdate = "";
		// 上月R统计时间
		String strRDate = "";

		try {

			// 入库时间
			stroutdate = row.get("outdate").trim();
			// 调用函数获取上个月R统计时间
			Date Rrundate = InOutRule.getRrundate();

			// 日期转为字符串
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			strRDate = format.format(Rrundate);


		} catch (Exception e) {
			log.error("获得出库时间失败:" + row.get("outdate")
					+ e.toString());
		}

		if (editbean instanceof EditableReportDeleteDataBean) {

			// 判断出库时间是否晚于上次R统计，晚于才能删除，否则不能删除
			if (!InOutRule.indateIsOK(stroutdate)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"删除出库记录的出库时间[" + stroutdate + "]必须晚于上月统计库存的时间["
								+ strRDate + "]！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}
			super.doSavePerRow(rrequest, rbean, row, mParamValues, editbean);
		}
		
		return WX_RETURNVAL_SUCCESS;
	}
}