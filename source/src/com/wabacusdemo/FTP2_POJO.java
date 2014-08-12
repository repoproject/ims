package com.wabacusdemo;

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
public class FTP2_POJO extends AbsReportDataPojo
{
	//FTP1类构造函数，调用父类的构造函数
    public FTP2_POJO(ReportRequest rrequest,ReportBean rbean)
    {
        super(rrequest,rbean);
    }
    
    //国家名
    private String countryname;
    
    //文件类型
    private String filetype;
    
    //FTP下载数
    private String ftpxzs;
    
    //FTP下载量
    private String ftpxzl;
    
    //获取国家名
	public String getCountryname() {
		return countryname;
	}
	
	//设定国家名属性
	public void setCountryname(String countryname) {
		this.countryname = countryname;
	}
	
	//获取文件类型
	public String getFiletype() {
		return filetype;
	}
	
	//设定文件类型
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
	
	//获取FTP下载数
	public String getFtpxzs() {
		return ftpxzs;
	}
	
	//设定FTP下载数
	public void setFtpxzs(String ftpxzs) {
		this.ftpxzs = ftpxzs;
	}
	
	//获取FTP下载量
	public String getFtpxzl() {
		return ftpxzl;
	}
	
	//设定FTP下载量
	public void setFtpxzl(String ftpxzl) {
		this.ftpxzl = ftpxzl;
	}

	public void format()
    {
        
    }
}
