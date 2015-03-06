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
public class InAdd extends AbsInterceptorDefaultAdapter {

	private static Logger log = Logger.getLogger(InAdd.class);

	/***
	 * 修改入库记录时的业务规则校验：
	 * 1、判断该试剂/耗材是否在库中存在，不存在则提示并不能保存
	 * 2、判断入库时间是否晚于上次R统计，晚于才能修改，否则不能修改
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
		double inNum=0.0;

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
			inNum = Double.valueOf(mRowData.get("num").trim());
			

			// 调用函数获取上个月R统计时间
			Date Rrundate = InOutRule.getRrundate();

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			strRDate = format.format(Rrundate);

		} catch (Exception e) {
			log.error("获得货号、名称、数量或者入库时间失败:" + mRowData.get("catno") + " "
					+ mRowData.get("catname") + " " + mRowData.get("indate")+Double.valueOf(mRowData.get("num").trim())+e.toString());
		}

		//增加的业务规则判断
		if (editbean instanceof EditableReportInsertDataBean) {


			// 判断该试剂/耗材是否在库中存在，不存在则提示并不能保存
			if (!InOutRule.catIsExit(strcatno, strcatname)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"不存在货号为[" + strcatno + "]名称为[" + strcatname
								+ "]的试剂/耗材，请提前维护后再进行入库！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}
			
			// 判断入库时间是否晚于上次R统计，晚于才能增加，否则不能增加
			if (!InOutRule.indateIsOK(strindate)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"新增的入库时间[" + strindate + "]必须晚于上月统计库存的时间["
								+ strRDate + "]！", null, false);
				return WX_RETURNVAL_TERMINATE;
			}
			
			// 判断是该货号和批号以前是否存在，不存在则给出提示存在则不用提示
			if (!InOutRule.IsExitBatchno(strcatno, strbatchno)) {
				
				String val=rrequest.getStringAttribute("key"+mRowData.get("no"),"");
				if(val.equals("true"))
				{//已经在“确认”提示窗口中点击了“是”，则保存
					return super.doSavePerRow(rrequest,rbean,mRowData,mParamValues,editbean);
				}else if(val.equals("false"))
				{//已经在“确认”提示窗口中点击了“否”，则不保存
					return WX_RETURNVAL_SKIP;
				}else
				{
					//开始保存，则在客户端弹出一个确认操作提示窗口				
				rrequest.getWResponse().getMessageCollector().confirm("key"+mRowData.get("no"),"请注意！批号为[" +  strbatchno + "]的["
						+strcatname + "]是第一次入库，需要继续保存吗？");
				}
			}
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
					editbean);

			
		} 
		else if (editbean instanceof EditableReportUpdateDataBean) {
				super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
						editbean);
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
