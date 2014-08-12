package com.wabacusdemo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;
/**
 * 公文页面拦截器
 * @创建者： zhouhl
 * @创建时间：2014-06-18
 *
 */
public class SBGL_Interceptor extends AbsInterceptorDefaultAdapter {

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
		
		//设置设备名称查询条件的sql语句
		if (rrequest.getAttribute("txtname") != null) {
			String filename = rrequest.getAttribute("txtname").toString();
			if (sql.contains("equipName")) {
				if (filename.indexOf("%") > -1) {
					filename = filename.replaceAll("%", "/%");
				}
				if (filename.indexOf("_") > -1) {
					filename = filename.replaceAll("_", "/_");
				}
				sql = sql.replaceAll("equipName", "equipname like '%" + filename +  "%' escape '/'");
			}
			
		}
		//设置序列号查询条件的sql语句
		if (rrequest.getAttribute("cdkey") != null) {
			String filename = rrequest.getAttribute("cdkey").toString();
			if (sql.contains("equipCDKEY")) {
				if (filename.indexOf("%") > -1) {
					filename = filename.replaceAll("%", "/%");
				}
				if (filename.indexOf("_") > -1) {
					filename = filename.replaceAll("_", "/_");
				}
				sql = sql.replaceAll("equipCDKEY", "cdkey like '%" + filename +  "%' escape '/'");
			}
			
		}
		//设置管理员查询条件的sql语句
		if (rrequest.getAttribute("operator") != null) {
			String filename = rrequest.getAttribute("operator").toString();
			if (sql.contains("equipOperator")) {
				if (filename.indexOf("%") > -1) {
					filename = filename.replaceAll("%", "/%");
				}
				if (filename.indexOf("_") > -1) {
					filename = filename.replaceAll("_", "/_");
				}
				sql = sql.replaceAll("equipOperator", "operator like '%" + filename +  "%' escape '/'");
			}
			
		}
		//返回数据库查询语句字符串
		return sql;
	}

}
