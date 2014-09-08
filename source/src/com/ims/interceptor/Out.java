package com.ims.interceptor;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;

public class Out  extends AbsInterceptorDefaultAdapter  {

	public void beforeDisplayReportDataPerRow(ReportRequest rrequest,
			ReportBean rbean, RowDataBean rowDataBean) {
		if (rowDataBean.getRowindex() == -1)
			return;// 标题行
		if (rowDataBean.getRowindex() % 2 == 1) {
			String style = rowDataBean.getRowstyleproperty();
			if (style == null)
				style = "";
			style += " style='background:#CFDFF8'";
			rowDataBean.setRowstyleproperty(style);
		}
		if(rowDataBean.getColData("ISEXP")!=null && rowDataBean.getColData("ISEXP").equals("1"))
		{
			String style = rowDataBean.getRowstyleproperty();
			if (style == null)
				style = "";
			style += " style='background:#FF0000'";
			rowDataBean.setRowstyleproperty(style);
		}
	}
}
