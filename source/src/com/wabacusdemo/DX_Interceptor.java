package com.wabacusdemo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;
/**
 * 公文页面拦截器
 * @创建者： jyp
 * @创建时间：2013-08-14
 * @修改人：zhouhl
 * @修改时间：2013-12-30
 *
 */
public class DX_Interceptor extends AbsInterceptorDefaultAdapter {

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
	 * @创建者： jyp
	 * @创建时间：2013-08-14
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
		
		
		//设置查村条件的sql语句
		if (rrequest.getAttribute("name") != null) {
			String filename = rrequest.getAttribute("name").toString();
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

}
