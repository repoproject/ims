package com.wabacusdemo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.buttons.EditableReportSQLButtonDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditActionBean;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditableReportEditDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportDeleteDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportInsertDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportUpdateDataBean;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.AbsPageInterceptor;
import com.wabacus.system.intercept.RowDataBean;
/**
 * 测试用
 * @author jyp
 * @修改人：zhouhl
 * @修改时间：2013-12-30
 *
 */
public class DeleteUser extends AbsInterceptorDefaultAdapter{

	
	/**
	 * 每行数据显示到页面之前执行的函数
	 * @创建者：zhouhl
	 * @修改人：zhouhl
	 * @修改时间：2014-06-16
	 * @返回值：无
	 * 
	 */
	public void beforeDisplayReportDataPerRow( ReportRequest rrequest, ReportBean rbean, RowDataBean rowDataBean)
	{
		if(rowDataBean.getRowindex()==-1) return ;//标题行
		if(rowDataBean.getRowindex()%2==1)
		{
			String style=rowDataBean.getRowstyleproperty();
			if(style==null) style="";
			style+=" style='background:#CFDFF8'";
			rowDataBean.setRowstyleproperty(style);
		}
	}
	@Override
	public int doSave(ReportRequest rrequest, ReportBean rbean,
			AbsEditableReportEditDataBean editbean) {
		// TODO Auto-generated method stub
		return super.doSave(rrequest, rbean, editbean);
	}

	@Override
	public int doSavePerAction(ReportRequest rrequest, ReportBean rbean,
			Map<String, String> mRowData, Map<String, String> mParamValues,
			AbsEditActionBean actionbean, AbsEditableReportEditDataBean editbean) {
		// TODO Auto-generated method stub
		return super.doSavePerAction(rrequest, rbean, mRowData, mParamValues,
				actionbean, editbean);
	}
	/**
	 * 装载数据之前执行的函数
	 * @创建者： zhouhl
	 * @创建时间：2014-06-18
	 * @修改人：zhouhl
	 * @修改时间：2013-12-30
	 * @返回值：查询语句字符串对象
	 */
	public Object beforeLoadData(ReportRequest rrequest, ReportBean rbean,
			Object typeObj, String sql) {
		
		//设置响应头信息，设定页面无缓存
		rrequest.getWResponse().getResponse().setHeader("Cache-Control","no-cache");
		rrequest.getWResponse().getResponse().setHeader("Cache-Control","no-store");
		rrequest.getWResponse().getResponse().setDateHeader("Expires", 0);
		rrequest.getWResponse().getResponse().setHeader("Pragma", "No-cache");
		
		
		//设置序列号查询条件的sql语句
		if (rrequest.getAttribute("txtname") != null) {
			String filename = rrequest.getAttribute("txtname").toString();
			if (sql.contains("textName")) {
				if (filename.indexOf("%") > -1) {
					filename = filename.replaceAll("%", "/%");
				}
				if (filename.indexOf("_") > -1) {
					filename = filename.replaceAll("_", "/_");
				}
				sql = sql.replaceAll("textName", "name like '%" + filename +  "%' escape '/'");
			}
			
		}
		//返回数据库查询语句字符串
		return sql;
	}
	public int doSavePerRow( ReportRequest rrequest, ReportBean rbean, Map<String,String> mRowData, Map<String,String> mParamValues, AbsEditableReportEditDataBean editbean){
		if(editbean instanceof EditableReportInsertDataBean) {
			//添加操作
			String usernameString = mRowData.get("name");
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		}else if(editbean instanceof EditableReportUpdateDataBean) {
				//修改操作
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		}else if(editbean instanceof EditableReportSQLButtonDataBean) {
			//调用配置在<button/>中配置的调用后台服务操作
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		}else if(editbean instanceof EditableReportDeleteDataBean) {
			//删除操作
			String usernameString = mRowData.get("name");
			if (usernameString.equalsIgnoreCase("admin")) {
				rrequest.getWResponse().getMessageCollector().alert("admin用户禁止删除",false);
				return WX_RETURNVAL_SKIP;
			}else {
				super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
			}
		}
		return WX_RETURNVAL_SUCCESS;
	}
	
}
