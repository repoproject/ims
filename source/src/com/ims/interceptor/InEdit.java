/**
 * 
 */
package com.ims.interceptor;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
 * @date   2014年8月25日
 */
public class InEdit extends AbsInterceptorDefaultAdapter {
	
	public int doSavePerRow(ReportRequest rrequest, ReportBean rbean,
			Map<String, String> mRowData, Map<String, String> mParamValues,
			AbsEditableReportEditDataBean editbean) {
		if (editbean instanceof EditableReportInsertDataBean) {
			//editbean.get
			
			String strcatno= mRowData.get("catno").trim();
			
			String strcatname= mRowData.get("catname").trim();
			//判断该试剂/耗材是否在库中存在，不存在则提示并不能保存
			if(!this.addIsExit(strcatno, strcatname))			
			{
				 rrequest.getWResponse().getMessageCollector().alert("不存在货号为["+strcatno+"]名称为["+strcatname+"]的试剂/耗材，请提前维护后再进行入库！",null,false);
					return WX_RETURNVAL_TERMINATE;
			}			
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		} else if (editbean instanceof EditableReportUpdateDataBean) {

			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		} else if (editbean instanceof EditableReportSQLButtonDataBean) {

			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		} else if (editbean instanceof EditableReportDeleteDataBean) {
			// 删除操作
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		}
		return WX_RETURNVAL_SUCCESS;
	}
	

	/***
	 * 
	 * 入库操作时判断是否存在该试剂/耗材
	 *
	 * @param strcatno 货号
	 * @param strcatname 名称
	 * @return true代表库中有该试剂/耗材，false代表库中不存在该试剂/耗材
	 */
	private boolean addIsExit(String strcatno,String strcatname ){
		
		//判断货号和名称是否存在的sql
		String sql = "SELECT DISTINCT id FROM d_catcode where catno=?  and  catname=? ";
		 
		//执行SQL
		List<Object> list = DBUtil.query(sql,strcatno, strcatname);
		//试剂耗材库中不存在该货号
		if(list.size() <1 ){
    		return false;
		}
		else{
			return true;
		}
	}
	
	private void update(){
		
	}
	
	private void delete(){
		
	}
}
