/**
 * 
 */
package com.ims.interceptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ims.util.DBUtil;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditableReportEditDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportDeleteDataBean;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;

/**
 * @author ChengNing
 * @date 2014-8-29
 */
public class DeleteIn extends AbsInterceptorDefaultAdapter {

	private static Logger log = Logger.getLogger(DeleteIn.class);

	public int doSavePerRow(ReportRequest rrequest, ReportBean rbean,
			Map<String, String> row, Map<String, String> mParamValues,
			AbsEditableReportEditDataBean editbean) {
		if (editbean instanceof EditableReportDeleteDataBean) {

			// 入库时间
			String strindate = "";

			try {
				// 入库时间
				strindate = row.get("inDate").trim();
			} catch (Exception e) {
				log.error("获得入库时间失败:" + row.get("indate"));
			}

			// 判断入库时间是否晚于上次R统计，晚于才能删除，否则不能删除
			if (!indateIsOK(strindate)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"删除入库记录的入库时间[" + strindate + "]不能早于上月统计库存的时间！", null,
						false);
				return WX_RETURNVAL_TERMINATE;
			}

			// 如果库存小于当前入库的数量，说明当前入库的已经存在出库，则不允许删除入库
			try {
				int delNum = Integer.parseInt(row.get("num"));
				int total = 0;
				// TODO:从数据库中取值
				if (total < delNum) {
					rrequest.getWResponse().getMessageCollector().alert(
							"当前入库已经存在被出库的情况，不允许删除", null, false);
					return WX_RETURNVAL_TERMINATE;
				} else {
					super.doSavePerRow(rrequest, rbean, row, mParamValues,
							editbean);
				}
			} catch (Exception e) {
				log.error("获得入库数量失败" + row.get("num"));
				return WX_RETURNVAL_TERMINATE;
			}
		}
		return WX_RETURNVAL_SUCCESS;
	}

	/***
	 * 判断入库时间是否晚于上月统计的时间，如果早或者等于则不能进行增、删、改
	 * 
	 * @param strindate
	 *            入库时间
	 * @return false代表早于等于上月统计的时间，true代表晚于上月的统计时间
	 */
	private boolean indateIsOK(String strindate) {

		Calendar calendar = Calendar.getInstance();
		int iyear = calendar.get(Calendar.YEAR);

		int monthOfYear = calendar.get(calendar.MONTH);
		// 得到的月份是从0开始，但是我要取上个月的统计日，所以不用加1，但对于1月份的情况我要专门处理为上一年的12月份
		if (monthOfYear == 0) {
			monthOfYear = 12;
			iyear = iyear - 1;
		}

		// 查询上月的统计日期
		String sql = "select runPoint from d_task where code='monthtask' and flag = ?";
		String runPoint = DBUtil.getOneValue(sql, String.valueOf(monthOfYear));

		// 上月R统计时间
		Date rundate = null;
		// 本次入库时间
		Date indate = null;

		int iDay = 0;

		try {
			iDay = Integer.parseInt(runPoint);
			// 转为时间,注意月份还要-1，因为是从0开始的.另外年要减去1900
			rundate = new Date(iyear - 1900, monthOfYear - 1, iDay);

			// 字符串转为时间
			indate = new SimpleDateFormat("yyyy-MM-dd").parse(strindate);

		} catch (ParseException e) {

			log.error("int转字符串失败:" + Integer.parseInt(runPoint));
			log.error("int转时间失败:" + Integer.toString(iyear) + " "
					+ Integer.toString(monthOfYear) + " "
					+ Integer.toString(iDay));
			log.error("字符串转时间失败:" + indate);
		}

		// 如果入库时间比统计时间晚于1天则可以入库，否则不行
		if (indate.after(rundate))
			return true;
		else
			return false;

	}

}
