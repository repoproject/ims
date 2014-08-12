package com.wabacusdemo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.wabacus.config.component.application.report.AbsReportDataPojo;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
/**
 * FTP报表模型类
 * @author jyp
 * @修改人：zhouhl
 * @修改时间：2013-12-30
 *
 */
public class FTP1_POJO extends AbsReportDataPojo
{
	//FTP1类构造函数，调用父类的构造函数
    public FTP1_POJO(ReportRequest rrequest,ReportBean rbean)
    {
        super(rrequest,rbean);
    }
    
    //国家名
    private String countryname;

    //FTP链接总数
    private String ftpljzs;

    private String ftpbfljs;
    
    //运行以来的链接总数
    private String ftpyxylljzs;
    
    //获取国家名
	public String getCountryname() {
		return countryname;
	}
	
	//设定国家名
	public void setCountryname(String countryname) {
		this.countryname = countryname;
	}
	
	//获取FTP链接总数
	public String getFtpljzs() {
		return ftpljzs;
	}
	
	//设置FTP链接总数
	public void setFtpljzs(String ftpljzs) {
		this.ftpljzs = ftpljzs;
	}

	public String getFtpbfljs() {
		return ftpbfljs;
	}

	public void setFtpbfljs(String ftpbfljs) {
		this.ftpbfljs = ftpbfljs;
	}

	public String getFtpyxylljzs() {
		return ftpyxylljzs;
	}

	public void setFtpyxylljzs(String ftpyxylljzs) {
		this.ftpyxylljzs = ftpyxylljzs;
	}

	public void format()
    {
        
    }
}
