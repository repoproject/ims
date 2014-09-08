package com.ims.interceptor;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;

public class OutAdd extends AbsInterceptorDefaultAdapter  {

	
	public Object afterLoadData(
			ReportRequest rrequest,
			ReportBean rbean,
			Object typeObj,
			Object dataObj){
		
		
		
				return dataObj;
				
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