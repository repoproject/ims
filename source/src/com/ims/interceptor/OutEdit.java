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

public class OutEdit extends AbsInterceptorDefaultAdapter  {
	
	private static Logger log = Logger.getLogger(OutEdit.class);
	
	
	/***
	 * 修改出库记录时的业务规则校验：
	 *1、判断出库时间是否晚于上次R统计，晚于才能修改，否则不能修改
	 *2、判断如果维护出库的数量大于剩余库存，则进行提示不能维护。即出库修改时修改的数量不能大于剩余库存
	 */
	public int doSavePerRow(ReportRequest rrequest, ReportBean rbean,
			Map<String, String> mRowData, Map<String, String> mParamValues,
			AbsEditableReportEditDataBean editbean) {

		// 货号
		String strcatno = "";
		
		// 批号
		String strbatchno = "";
		//单价
		String strprice = "";
		
		// 出库时间
		String stroutdate = "";
		// 上月R统计时间
		String strRDate = "";
		
		//准备更新的出库的数量
		int newoutNum=0;
		
		//库存量
		int itotal=0;
		
		//出库人
		String strperson="";

		try {
			// 货号
			strcatno = mRowData.get("catno").trim();
			//批号
			strbatchno= mRowData.get("batchno").trim();
			//单价
			strprice = mRowData.get("price").trim();
			// 出库时间
			stroutdate = mRowData.get("outdate").trim();
			
			//出库数量
			newoutNum = Integer.parseInt(mRowData.get("num").trim());
			
			//目前剩余的库存
			itotal = Integer.parseInt(mRowData.get("total").trim());
			
			
			//出库人
			strperson = mRowData.get("person").trim();

			// 调用函数获取上个月R统计时间
			Date Rrundate = InOutRule.getRrundate();

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			strRDate = format.format(Rrundate);

		} catch (Exception e) {
			log.error("获得货号、批号、数量或者出库时间失败:" + mRowData.get("catno") + " "
					+ mRowData.get("batchno") + " " + mRowData.get("outdate")+Integer.parseInt(mRowData.get("num").trim())+e.toString());
		}

		if (editbean instanceof EditableReportInsertDataBean) {
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
					editbean);
		} 
		//修改时的业务规则判断
		else if (editbean instanceof EditableReportUpdateDataBean) {
			
			int oldoutnum=0;
			try {
				//调用函数获取出库人本次更新前已经出库的数量
				oldoutnum = InOutRule.getoutTotalofPerson(strcatno, strbatchno, strprice, strperson);
			} catch (Exception e) {
				
				log.error("调用失败InOutRule.getoutTotalofPerson(strcatno, strbatchno, strprice, strperson)失败:"  +e.toString());
			}
			//本人准备更新的出库数量-本人更新前的出库数量<=更新前的库存量，也就是本人此次更新的出库增量不能超过库存的剩余数量			
			if(newoutNum-oldoutnum>itotal){
				rrequest.getWResponse().getMessageCollector().alert(
						"新增出库数量["+(newoutNum-oldoutnum)+"]不能大于剩余库存["+itotal+"]!", null, false);
				return WX_RETURNVAL_TERMINATE;
			}	
			
			// 判断出库时间是否晚于上次R统计，晚于才能增加，否则不能增加
			else if (!InOutRule.indateIsOK(stroutdate)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"出库时间[" + stroutdate + "]必须晚于上月统计库存的时间["
								+ strRDate + "]！", null, false);
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
