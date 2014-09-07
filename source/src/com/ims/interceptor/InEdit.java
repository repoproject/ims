/**
 * 
 */
package com.ims.interceptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ims.util.DBUtil;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.buttons.EditableReportSQLButtonDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditableReportEditDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportDeleteDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportInsertDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportUpdateDataBean;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;

/**
 * @author ChengNing
 * @date 2014年8月25日
 */
public class InEdit extends AbsInterceptorDefaultAdapter {

	private static Logger log = Logger.getLogger(InEdit.class);

	public int doSavePerRow(ReportRequest rrequest, ReportBean rbean,
			Map<String, String> mRowData, Map<String, String> mParamValues,
			AbsEditableReportEditDataBean editbean) {

		// 货号
		String strcatno = "";
		// 名称
		String strcatname = "";
		// 入库时间
		String strindate = "";

		try {
			// 货号
			strcatno = mRowData.get("catno").trim();
			// 名称
			strcatname = mRowData.get("catname").trim();
			// 入库时间
			strindate = mRowData.get("indate").trim();
		} catch (Exception e) {
			log.error("获得货号、名称或者入库时间失败:" + mRowData.get("catno") + " "
					+ mRowData.get("catname") + " " + mRowData.get("indate"));
		}

		if (editbean instanceof EditableReportInsertDataBean) {


			//判断入库时间是否晚于上次R统计，晚于才能增加，否则不能增加
			if(!indateIsOK(strindate))
			{
				rrequest.getWResponse().getMessageCollector().alert(
						"新增入库记录的入库时间["+strindate+"]不能早于上月统计库存的时间！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}
			
			// 判断该试剂/耗材是否在库中存在，不存在则提示并不能保存
			if (!this.catIsExit(strcatno, strcatname)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"不存在货号为[" + strcatno + "]名称为[" + strcatname
								+ "]的试剂/耗材，请提前维护后再进行入库！", null, false);
				return WX_RETURNVAL_TERMINATE;
			} 

				super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
						editbean);
			
		} else if (editbean instanceof EditableReportUpdateDataBean) {

			//判断入库时间是否晚于上次R统计，晚于才能修改，否则不能修改
			if(!indateIsOK(strindate))
			{
				rrequest.getWResponse().getMessageCollector().alert(
						"修改入库记录的入库时间["+strindate+"]不能早于上月统计库存的时间！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}
			// 判断该试剂/耗材是否在库中存在，不存在则提示并不能保存
			if (!this.catIsExit(strcatno, strcatname)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"不存在货号为[" + strcatno + "]名称为[" + strcatname
								+ "]的试剂/耗材，请提前维护后再进行入库！", null, false);
				return WX_RETURNVAL_TERMINATE;
			} else {

				super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
						editbean);
			}
		} else if (editbean instanceof EditableReportSQLButtonDataBean) {

			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
					editbean);
		} else if (editbean instanceof EditableReportDeleteDataBean) {
			// 删除操作
			//判断入库时间是否晚于上次R统计，晚于才能删除，否则不能删除
			if(!indateIsOK(strindate))
			{
				rrequest.getWResponse().getMessageCollector().alert(
						"删除入库记录的入库时间"+strindate+"不能早于上月统计库存的时间！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
					editbean);
		}
		return WX_RETURNVAL_SUCCESS;
	}

	/***
	 * 
	 * 入库操作时判断是否存在该试剂/耗材
	 * 
	 * @param strcatno
	 *            货号
	 * @param strcatname
	 *            名称
	 * @return true代表库中有该试剂/耗材，false代表库中不存在该试剂/耗材
	 */
	private boolean catIsExit(String strcatno, String strcatname) {

		// 判断货号和名称是否存在的sql
		String sql = "SELECT DISTINCT id FROM d_catcode where catno=?  and  catname=? ";

		// 执行SQL
		List<Object> list = DBUtil.query(sql, strcatno, strcatname);
		// 试剂耗材库中不存在该货号
		if (list.size() < 1) {

			return false;
		} else {
			return true;
		}
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
		if(monthOfYear==0)
		{
		  monthOfYear=12;
		  iyear=iyear-1;
		}
	
		//查询上月的统计日期
		String sql = "select runPoint from d_task where code='monthtask' and flag = ?";
		String runPoint = DBUtil.getOneValue(sql, String.valueOf(monthOfYear));
		
		// 上月R统计时间
		Date rundate = null;
		// 本次入库时间
		Date indate = null;
		
		int iDay = 0;
		
		try {
			iDay = Integer.parseInt(runPoint);
			//转为时间,注意月份还要-1，因为是从0开始的.另外年要减去1900
			rundate= new Date(iyear-1900,monthOfYear-1,iDay);
			
			//字符串转为时间
			indate = new SimpleDateFormat("yyyy-MM-dd").parse(strindate);
			
		} catch (ParseException e) {

			log.error("int转字符串失败:"+Integer.parseInt(runPoint));
			log.error("int转时间失败:" +Integer.toString(iyear)+" "+Integer.toString(monthOfYear)+" "+Integer.toString(iDay));
			log.error("字符串转时间失败:" +indate);
		}


		  
		
		//如果入库时间比统计时间晚于1天则可以入库，否则不行
		if  (indate.after(rundate))
			return true;
		else
			return false;

	}

	private void delete() {

	}
}
