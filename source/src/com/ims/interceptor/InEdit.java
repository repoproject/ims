/**
 * 
 */
package com.ims.interceptor;

import java.text.ParseException;
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
import com.wabacus.system.intercept.RowDataBean;

/**
 * @author ChengNing
 * @date 2014年8月25日
 */
public class InEdit extends AbsInterceptorDefaultAdapter {

	private static Logger log = Logger.getLogger(InEdit.class);


	
	/***
	 * 修改入库记录时的业务规则校验：
	 * 1、判断该试剂/耗材是否在库中存在，不存在则提示并不能保存
	 * 2、判断入库时间是否晚于上次R统计，晚于才能修改，否则不能修改
	 * 3、判断如果已经有出库记录，则不能删除
	 */
	public int doSavePerRow(ReportRequest rrequest, ReportBean rbean,
			Map<String, String> mRowData, Map<String, String> mParamValues,
			AbsEditableReportEditDataBean editbean) {

		// 货号
		String strcatno = "";
		// 名称
		String strcatname = "";
		
		// 批号
		String strbatchno = "";
		//单价
		String strprice = "";
		
		// 入库时间
		String strindate = "";
		// 上月R统计时间
		String strRDate = "";
		
		//入库的数量
		int inNum=0;

		try {
			// 货号
			strcatno = mRowData.get("catno").trim();
			// 名称
			strcatname = mRowData.get("catname").trim();
			
			//批号
			strbatchno= mRowData.get("batchno").trim();
			//单价
			strprice = mRowData.get("price").trim();
			// 入库时间
			strindate = mRowData.get("indate").trim();
			
			//入库数量
			inNum = Integer.parseInt(mRowData.get("num").trim());
			

			// 调用函数获取上个月R统计时间
			Date Rrundate = InOutRule.getRrundate();

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			strRDate = format.format(Rrundate);

		} catch (Exception e) {
			log.error("获得货号、名称、数量或者入库时间失败:" + mRowData.get("catno") + " "
					+ mRowData.get("catname") + " " + mRowData.get("indate")+Integer.parseInt(mRowData.get("num").trim())+e.toString());
		}

		if (editbean instanceof EditableReportInsertDataBean) {
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
					editbean);
		} 
		//修改时的业务规则判断
		else if (editbean instanceof EditableReportUpdateDataBean) {

			// 判断该试剂/耗材是否在库中存在，不存在则提示并不能保存
			if (!InOutRule.catIsExit(strcatno, strcatname)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"不存在货号为[" + strcatno + "]名称为[" + strcatname
								+ "]的试剂/耗材，请提前维护后再进行入库！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}
			
			// 判断入库时间是否晚于上次R统计，晚于才能修改，否则不能修改
			if (!InOutRule.indateIsOK(strindate)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"修改的入库时间[" + strindate + "]必须晚于上月统计库存的时间["
								+ strRDate + "]！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}

			//调用函数获取出库数量
			int outNum=0;
			
			try {
				outNum = InOutRule.getoutTotal(strcatno, strbatchno, strprice);
			} catch (Exception e) {
				
				log.error("调用失败InOutRule.getoutTotal(strcatno, strbatchno, strprice)失败:"  +e.toString());
			}
/*			//判断如果维护入库的数量小于出库量，则进行提示不能维护。即入库修改时修改的数量不能小于出库数量			
			if(inNum<outNum){
				rrequest.getWResponse().getMessageCollector().alert(
						"修改试剂/耗材时的入库数量["+inNum+"]不能小于已经出库的数量["+outNum+"]!", null, false);
				return WX_RETURNVAL_TERMINATE;
			}*/	
			//如果已经有相应的出库记录则不能再进行修改操作
			if(outNum>0){
				rrequest.getWResponse().getMessageCollector().alert(
						"该试剂/耗材已经出库了["+outNum+"]个，不能进行修改操作，请先撤销所有出库后才能修改!", null, false);
				return WX_RETURNVAL_TERMINATE;
			}
			
			else {

				super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
						editbean);
			}
		} else if (editbean instanceof EditableReportSQLButtonDataBean) {

			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
					editbean);
		} else if (editbean instanceof EditableReportDeleteDataBean) {


			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
					editbean);
		}
		return WX_RETURNVAL_SUCCESS;
	}


}
