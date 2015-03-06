package com.ims.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ims.rule.InOutRule;
import com.ims.util.SysVar;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.buttons.EditableReportSQLButtonDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditableReportEditDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportDeleteDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportInsertDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportUpdateDataBean;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;

public class OutAdd extends AbsInterceptorDefaultAdapter  {
	
	private static Logger log = Logger.getLogger(OutAdd.class);
	
	public Object afterLoadData(
			ReportRequest rrequest,
			ReportBean rbean,
			Object typeObj,
			Object dataObj){
		
		
		
				return dataObj;
				
	}
	
	/***
	 * 增加出库记录时的业务规则校验：
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
		
		//出庫的数量
		double outNum=0.0;

		//所属R类型
		String strRtype= "";
		
		
		try {
			
			//如果是lab的耗材则不论界面如何填写设备名称和序号，都改为“所有设备”和-1
			strRtype=mRowData.get("rtype").trim();
			String strVar = SysVar.getBizValue("labrtype");
			String strmachinename = SysVar.getBizValue("nomachinename");
			String strmachineno = SysVar.getBizValue("nomachineno");
			//
			if(strRtype.equals(strVar))
			{
				mRowData.put("machinename", strmachinename);
				mRowData.put("machineno", strmachineno);
			}
			// 货号
			strcatno = mRowData.get("catno").trim();
			//批号
			strbatchno= mRowData.get("batchno").trim();
			//单价
			strprice = mRowData.get("price").trim();
			// 出库时间
			stroutdate = mRowData.get("outdate").trim();
			
			//出库数量
			outNum = Double.valueOf(mRowData.get("num").trim());
			

			// 调用函数获取上个月R统计时间
			Date Rrundate = InOutRule.getRrundate();

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			strRDate = format.format(Rrundate);

		} catch (Exception e) {
			log.error("获得货号、批号、数量或者出库时间失败:" + mRowData.get("catno") + " "
					+ mRowData.get("batchno") + " " + mRowData.get("outdate")+Double.valueOf(mRowData.get("num").trim())+e.toString());
		}

		if (editbean instanceof EditableReportInsertDataBean) {
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,
					editbean);
		} 
		//修改时的业务规则判断
		else if (editbean instanceof EditableReportUpdateDataBean) {
			
			double cattotal=0.0;
			try {
				//调用函数获取库存数量
				cattotal = InOutRule.getcatTotal(strcatno, strbatchno, strprice);
			} catch (Exception e) {
				
				log.error("调用失败InOutRule.getcatTotal(strcatno, strbatchno, strprice)失败:"  +e.toString());
			}
			//判断如果维护出库的数量大于剩余库存，则进行提示不能维护。即出库修改时修改的数量不能大于剩余库存			
			if(outNum>cattotal){
				rrequest.getWResponse().getMessageCollector().alert(
						"出库数量["+outNum+"]不能大于剩余库存["+cattotal+"]!", null, false);
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


	
	
/*	public void beforeDisplayReportDataPerRow(ReportRequest rrequest,
			ReportBean rbean, RowDataBean rowDataBean) {
		if (rowDataBean.getRowindex() == -1)
			return;// 标题行
		
		if(rowDataBean.getColData("reason")!=null && rowDataBean.getColData("reason").equals("Validation"))
		{
			rrequest.getWResponse().getMessageCollector().alert(
					"4444455555", null, false);
			String style = rowDataBean.getRowstyleproperty();
			if (style == null)
				style = "";
			style += " style='background:#FF0000'";
			rowDataBean.setRowstyleproperty(style);
		}
	}*/
}