/**
 * 
 */
package com.ims.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;

import com.ims.rule.InOutRule;
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
		// 上月R统计时间
		String strRDate = "";

		try {
			// 货号
			strcatno = mRowData.get("catno").trim();
			// 名称
			strcatname = mRowData.get("catname").trim();
			// 入库时间
			strindate = mRowData.get("indate").trim();

			// 调用函数获取上个月R统计时间
			Date Rrundate = InOutRule.getRrundate();

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			strRDate = format.format(Rrundate);

		} catch (Exception e) {
			log.error("获得货号、名称或者入库时间失败:" + mRowData.get("catno") + " "
					+ mRowData.get("catname") + " " + mRowData.get("indate"));
		}

		if (editbean instanceof EditableReportInsertDataBean) {

			// 判断入库时间是否晚于上次R统计，晚于才能增加，否则不能增加
			if (!com.ims.rule.InOutRule.indateIsOK(strindate)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"新增入库记录的入库时间[" + strindate + "]必须晚于上月统计库存的时间["
								+ strRDate + "]！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}

			// 判断该试剂/耗材是否在库中存在，不存在则提示并不能保存
			if (!com.ims.rule.InOutRule.catIsExit(strcatno, strcatname)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"不存在货号为[" + strcatno + "]名称为[" + strcatname
								+ "]的试剂/耗材，请提前维护后再进行入库！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}

			//判断如果维护入库的数量小于出库量，则进行提示不能维护。即入库修改时修改的数量不能小于出库数量
			
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
					editbean);

		} else if (editbean instanceof EditableReportUpdateDataBean) {

			// 判断入库时间是否晚于上次R统计，晚于才能修改，否则不能修改
			if (!com.ims.rule.InOutRule.indateIsOK(strindate)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"修改入库记录的入库时间[" + strindate + "]必须晚于上月统计库存的时间["
								+ strRDate + "]！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}
			// 判断该试剂/耗材是否在库中存在，不存在则提示并不能保存
			if (!com.ims.rule.InOutRule.catIsExit(strcatno, strcatname)) {
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
			// 判断入库时间是否晚于上次R统计，晚于才能删除，否则不能删除
			if (!com.ims.rule.InOutRule.indateIsOK(strindate)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"删除入库记录的入库时间" + strindate + "必须晚于上月统计库存的时间[" + strRDate
								+ "]！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
					editbean);
		}
		return WX_RETURNVAL_SUCCESS;
	}


}
