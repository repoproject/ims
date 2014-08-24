package com.ims.interceptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.buttons.EditableReportSQLButtonDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditableReportEditDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportDeleteDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportInsertDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportUpdateDataBean;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;

public class RegentEdit  extends AbsInterceptorDefaultAdapter {
	public int doSavePerRow( ReportRequest rrequest, ReportBean rbean, Map<String,String> mRowData, Map<String,String> mParamValues, AbsEditableReportEditDataBean editbean){
		if(editbean instanceof EditableReportInsertDataBean) {
			//添加操作
			String catnoString = mRowData.get("catno");
			catnoString = catnoString.trim();
			Connection conn=Config.getInstance().getDataSource("ds_mysql").getConnection();//取配置的默认数据源的连接
	        PreparedStatement pstmt = null;
			ResultSet rs = null;
			//String dataName = "";
			try {
			    	//从数据库中获取数据
			    	pstmt = conn.prepareStatement("select distinct catno from d_catcode");
			    	rs = pstmt.executeQuery();
			    	while (rs.next()) {	
			        	 if (catnoString.equals(rs.getString("catno"))) {
			        		 rrequest.getWResponse().getMessageCollector().alert("货号已经存在，请重新输入！",null,false);
								return WX_RETURNVAL_TERMINATE;
						}
			        }
			  }catch (SQLException e) {
					e.printStackTrace();
			  }finally{
			  	// 关闭结果集
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				// 关闭Statement
				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				// 关闭数据库连接
				if (conn != null) {
					try {
						conn.close();
						conn=null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			  }
			  super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		}else if(editbean instanceof EditableReportUpdateDataBean) {
				//修改操作
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		}else if(editbean instanceof EditableReportSQLButtonDataBean) {
			//调用配置在<button/>中配置的调用后台服务操作
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
		}else if(editbean instanceof EditableReportDeleteDataBean) {			
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);			
		}
		return WX_RETURNVAL_SUCCESS;
	}
	
}
