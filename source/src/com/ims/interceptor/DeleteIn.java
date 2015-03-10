/**
 * 
 */
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
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportUpdateDataBean;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;

/**
 * @author ChengNing
 * @date 2014-8-29
 */
public class DeleteIn extends AbsInterceptorDefaultAdapter {

	private static Logger log = Logger.getLogger(DeleteIn.class);

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

	/***
	 * 入库记录删除时的业务规则判断： 1、判断入库时间是否晚于上次R统计，晚于才能删除，否则不能删除
	 * 2、如果库存小于当前入库的数量，说明当前入库的已经存在出库，则不允许删除入库
	 */
	public int doSavePerRow(ReportRequest rrequest, ReportBean rbean,
			Map<String, String> row, Map<String, String> mParamValues,
			AbsEditableReportEditDataBean editbean) {

		// 入库时间
		String strindate = "";
		// 上月R统计时间
		String strRDate = "";

		// 货号
		String strcatno = "";
		// 批号
		String strbatchno = "";
		// 单价
		String strprice = "";
		double total = 0.0;
		double delNum = 0.0;

		try {

			// 入库时间
			strindate = row.get("inDate").trim();
			// 调用函数获取上个月R统计时间
			Date Rrundate = InOutRule.getRrundate();

			// 日期转为字符串
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			strRDate = format.format(Rrundate);

			// 入库的数量
			delNum = Double.valueOf(row.get("num"));

			// 货号
			strcatno = row.get("catno").trim();
			// 批号
			strbatchno = row.get("batchno").trim();
			// 单价
			strprice = row.get("price").trim();

			// 获取库存函数
			total = InOutRule.getcatTotal(strcatno, strbatchno, strprice);

		} catch (Exception e) {
			log.error("获得入库时间、货号、名称、单价等失败:" + Double.valueOf(row.get("num"))
					+ e.toString());
		}

		if (editbean instanceof EditableReportDeleteDataBean) {

			// 判断入库时间是否晚于上次R统计，晚于才能删除，否则不能删除
			if (!InOutRule.indateIsOK(strindate)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"删除入库记录的入库时间[" + strindate + "]必须晚于上月统计库存的时间["
								+ strRDate + "]！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}	

			// 调用函数获取出库数量
			double outNum = 0.0;

			try {
				//如果有出库记录则不能删除
				outNum = InOutRule.getoutTotal(strcatno, strbatchno, strprice);
			} catch (Exception e) {

				log
						.error("调用失败InOutRule.getoutTotal(strcatno, strbatchno, strprice)失败:"
								+ e.toString());
			}
			if (outNum > 0) {
				rrequest.getWResponse().getMessageCollector()
						.alert(
								"该试剂/耗材已经出库了[" + outNum
										+ "]个，不能进行删除，请先撤销所有出库记录后才能删除入库记录!",
								null, false);
				return WX_RETURNVAL_TERMINATE;

			}
			super.doSavePerRow(rrequest, rbean, row, mParamValues, editbean);

		} else if (editbean instanceof EditableReportUpdateDataBean) {
			rrequest.getWResponse().getMessageCollector().alert("jjjj", null,
					false);

			super.doSavePerRow(rrequest, rbean, row, mParamValues, editbean);
		}
		return WX_RETURNVAL_SUCCESS;
	}

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
		if (sql.contains("1=1") && !sql.contains("i.indate>=DATE")
				&& !sql.contains("i.indate<=DATE")) {

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

			sql = sql.replaceAll("1=1", "(i.indate>=DATE('" + time2
					+ "')) and (i.indate<=DATE('" + time1 + "'))");
		}

		// 返回数据库查询语句字符串
		return sql;
	}

}
