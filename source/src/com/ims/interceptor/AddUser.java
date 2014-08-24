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

	
	public int doSavePerRow( ReportRequest rrequest, ReportBean rbean, Map<String,String> mRowData, Map<String,String> mParamValues, AbsEditableReportEditDataBean editbean){
		if(editbean instanceof EditableReportInsertDataBean) {
			//添加操作
			String usernameString = mRowData.get("nickName");
			usernameString = usernameString.trim();
			Connection conn=Config.getInstance().getDataSource("ds_mysql").getConnection();//取配置的默认数据源的连接
	        PreparedStatement pstmt = null;
			ResultSet rs = null;
			//String dataName = "";
			try {
			    	//从数据库中获取数据
			    	pstmt = conn.prepareStatement("select nickName from d_user");
			    	rs = pstmt.executeQuery();
			    	while (rs.next()) {	
			        	 if (usernameString.equals(rs.getString("nickName"))) {
			        		 rrequest.getWResponse().getMessageCollector().alert("用户昵称已经存在，请重新输入！",null,false);
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
			//删除操作
		
			super.doSavePerRow(rrequest, rbean, mRowData, mParamValues,editbean);
			
		}
		return WX_RETURNVAL_SUCCESS;
	}
	
}
