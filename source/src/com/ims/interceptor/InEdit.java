/**
 * 
 */
package com.ims.interceptor;

import java.util.Map;

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
		} else if (editbean instanceof EditableReportUpdateDataBean) {

		} else if (editbean instanceof EditableReportSQLButtonDataBean) {

		} else if (editbean instanceof EditableReportDeleteDataBean) {
			// 删除操作

		}
		return WX_RETURNVAL_SUCCESS;
	}
	
	/**
	 * 入库操作
	 */
	private void add(){
		
	}
	
	private void update(){
		
	}
	
	private void delete(){
		
	}
}
