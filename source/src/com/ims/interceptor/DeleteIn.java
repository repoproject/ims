/**
 * 
 */
package com.ims.interceptor;

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
 * @date   2014-8-29
 */
public class DeleteIn extends AbsInterceptorDefaultAdapter{
	
	private static Logger log = Logger.getLogger(DeleteIn.class);
	
	public int doSavePerRow( ReportRequest rrequest, ReportBean rbean,
			Map<String,String> row, Map<String,String> mParamValues,
			AbsEditableReportEditDataBean editbean){
		if(editbean instanceof EditableReportDeleteDataBean) {
			//如果库存小于当前入库的数量，说明当前入库的已经存在出库，则不允许删除入库
			try {
				int delNum = Integer.parseInt(row.get("num"));
				int total = 0;//TODO:从数据库中取值
				if(total < delNum){
					rrequest.getWResponse().getMessageCollector().alert("当前入库已经存在被出库的情况，不允许删除",null,false);
					return WX_RETURNVAL_TERMINATE;
				}else {
					super.doSavePerRow(rrequest, rbean, row, mParamValues,editbean);
				}
			} catch (Exception e) {
				log.error("获得入库数量失败" + row.get("num"));
				return WX_RETURNVAL_TERMINATE;
			}
		}
		return WX_RETURNVAL_SUCCESS;
	}
}
