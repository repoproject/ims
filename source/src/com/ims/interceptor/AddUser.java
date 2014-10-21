package com.ims.interceptor;

import java.util.Map;
import com.ims.rule.AddEditUser;
import org.apache.log4j.Logger;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.buttons.EditableReportSQLButtonDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.*;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
/**
 * 测试用
 * @author jyp
 * @修改人：guanq
 * @修改时间：2013-12-30
 *
 */
public class AddUser extends AbsInterceptorDefaultAdapter{

	private static Logger log = Logger.getLogger(AddUser.class);
	
	public int doSavePerRow( ReportRequest rrequest, ReportBean rbean, Map<String,String> mRowData, Map<String,String> mParamValues, AbsEditableReportEditDataBean editbean){
		if(editbean instanceof EditableReportInsertDataBean) {
			//添加操作
			String usernameString = mRowData.get("nickName");
			usernameString = usernameString.trim();
			try
			{
				//判断是否已经存在该昵称用户
				if(AddEditUser.IsExitNickname(usernameString)){
					rrequest.getWResponse().getMessageCollector().alert(
							"已经存在[" + usernameString + "]的用户昵称，请重新输入！", null, false);
					return WX_RETURNVAL_TERMINATE;
				}
			}
			catch (Exception e) {
				log.error( e.toString());
			}
			 
			  super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		}else if(editbean instanceof EditableReportUpdateDataBean) {
			//修改操作
			String usernameString = mRowData.get("nickName");
			usernameString = usernameString.trim();
			try
			{
				//判断是否已经存在该昵称用户
				if(AddEditUser.IsExitNickname(usernameString)){
					rrequest.getWResponse().getMessageCollector().alert(
							"已经存在[" + usernameString + "]的用户昵称，请重新输入！", null, false);
					return WX_RETURNVAL_TERMINATE;
				}
			}
			catch (Exception e) {
				log.error( e.toString());
			}
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		}else if(editbean instanceof EditableReportSQLButtonDataBean) {
			//调用配置在<button/>中配置的调用后台服务操作
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		}else if(editbean instanceof EditableReportDeleteDataBean) {
			//删除操作
		
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
			
		}
		return WX_RETURNVAL_SUCCESS;
	}
	
	
}
