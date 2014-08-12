package com.wabacusdemo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportFilterBean;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.RowDataBean;
/**
 * FTP服务统计页面拦截器
 * @author jyp
 * @修改人：zhouhl
 * @修改时间：2013-12-29
 *
 */
public class FTP1_Interceptor extends AbsInterceptorDefaultAdapter {

	/**
	 * 装载数据之前执行的函数
	 * @author jyp
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
		
		//判断对象是否为ReportDataSetValueBean对象
		if(typeObj instanceof ReportDataSetValueBean)
        {
            PreparedStatement pstmt=null;
            Connection conn = null;
            ResultSet rs = null;
            List lstResults=new ArrayList();
            try
            {
            	//设定开始时间、结束时间和国家查询条件占位符
            	String kssj = "1=1";
            	String jssj = "1=1";
            	String guojia = "1=1";
            	
            	/*
            	 *判断页面传过来的查询数据字符串中是否含有kssj
            	 *若含有则获取请求中的开始时间
            	*/
            	if (sql.indexOf("kssj") > 0) {
					kssj = "notetime >= to_date('"+rrequest.getAttribute("txtbegin").toString()+"','yyyy-mm-dd')";
				}
            	
            	/*
            	 *判断页面传过来的查询数据字符串中是否含有jssj
            	 *若含有则获取请求中的结束时间
            	*/
            	if (sql.indexOf("jssj") > 0) {
					jssj = "notetime <= to_date('"+rrequest.getAttribute("txtend").toString()+"','yyyy-mm-dd')";
				}
            	
            	conn=rrequest.getConnection(rbean.getSbean().getDatasource());
            	
            	/*
            	 *判断页面传过来的查询数据字符串中是否含有guojia
            	 *若含有则获取请求中的国家名
            	 */
            	if (sql.indexOf("guojia") > 0) {
            		String countryname = rrequest.getAttribute("country").toString();
					guojia = "countryname = '"+countryname+"'";
					
					//判断查询字符串中是否含有"count(*)"字符串
					if(sql.indexOf("count(*)")>0)
	                {
						//对满足条件的数据求和
						pstmt=conn.prepareStatement("select count(distinct countryname) from ftpstat where STATUS = 'c' and "+kssj+" and "+jssj+" and countryname='"+countryname+"'");
                        rs=pstmt.executeQuery();
                        rs.next();
	                    lstResults.add(rs.getInt(1));
	                    rs.close();
	                }else
	                {
	                	
	                	//若查询字符串中没有求和则将获取FTP1对象
	                    FTP1_POJO pojo=new FTP1_POJO(rrequest,rbean);
	                    	
	                    pojo = getPOJO(conn,pstmt,pojo,countryname,kssj,jssj);
	                    	
	                    lstResults.add(pojo);
	                }
            	}
            	else
            	{
            		//若查询字符串中没有国家和求和字符串，则只对开始时间和结束时间进行查询
            		if(sql.indexOf("count(*)")>0)
                    {
                    	pstmt=conn.prepareStatement("select count(distinct countryname) from ftpstat where STATUS = 'c' and "+kssj+" and "+jssj+" ");
                        rs=pstmt.executeQuery();
                        rs.next();
                        lstResults.add(rs.getInt(1));
                        rs.close();
                    }else
                    {
                    	//从数据库中查询数据，并赋值给FTP对象
                    	pstmt=conn.prepareStatement("select distinct countryname from ftpstat where STATUS = 'c' and "+kssj+" and "+jssj+" ");
                        rs=pstmt.executeQuery();
                        while(rs.next())
                        {
                        	String countryname = rs.getString("countryname");
                        	FTP1_POJO pojo=new FTP1_POJO(rrequest,rbean);
                        	
                        	pojo = getPOJO(conn,pstmt,pojo,countryname,kssj,jssj);
                        	
                            lstResults.add(pojo);
                        }
                        rs.close();
                    }
            	}
                return lstResults;
            }catch(Exception e)
            {
            	//异常处理，打印错误信息
                e.printStackTrace();
                if(sql.indexOf("count(*)")>0)
                {
                    lstResults.add(0);
                }
                return lstResults;
            }finally{
            	//关闭数据库查询对象
                if(rs!=null)
                {
                    try
                    {
                    	rs.close();
                    }catch(SQLException e)
                    {
                        e.printStackTrace();
                    }
                }
                if(pstmt!=null)
                {
                    try
                    {
                    	pstmt.close();
                    }catch(SQLException e)
                    {
                        e.printStackTrace();
                    }
                }
                if(conn!=null)
                {
                    try
                    {
                    	conn.close();
                    }catch(SQLException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
		
		//返回数据库查询语句字符串
		return sql;
	}
	
	/**
	 * 每行报表数据显示之前执行的处理函数
	 * @author jyp
	 * @修改者：zhouhl
	 * @修改时间：2013-12-31
	 * 返回值：无
	 */
	public void beforeDisplayReportDataPerRow( ReportRequest rrequest, ReportBean rbean, RowDataBean rowDataBean)
	{
		//若数据行数为-1，则返回
		if(rowDataBean.getRowindex()==-1){
			return ;
		}
		
		//若数据行数为奇数行，则添加下面的样式
		if(rowDataBean.getRowindex()%2==1)
		{
			String style=rowDataBean.getRowstyleproperty();
			if(style==null) style="";
			style+=" style='background:#CFDFF8'";
			rowDataBean.setRowstyleproperty(style);
		}
		else
		{
			//当数据行数为偶数行时，添加白底样式
			String style=rowDataBean.getRowstyleproperty();
			if(style==null) style="";
			style+=" style='background:#FFF'";
			rowDataBean.setRowstyleproperty(style);
		}
	}
	
	/**
	 * 获取FTP报表对象
	 * @author jyp
	 * @修改者：zhouhl
	 * @修改时间：2013-12-31
	 * 返回值：FTP对象
	 */
	public FTP1_POJO getPOJO(Connection conn,PreparedStatement pstmt,FTP1_POJO pojo,String countryname,String kssj,String jssj) throws SQLException
	{
		//根据国家名设定对象属性
        pojo.setCountryname(countryname);
        ResultSet rs1 = null;
        //从数据中获取指定时间段、指定国家的FTP链接总数
        try {
        	pstmt=conn.prepareStatement("select count(*) as ftpljzs from ftpstat where countryname='"+countryname+"' and STATUS = 'c' and "+kssj+" and "+jssj+" ");
            rs1=pstmt.executeQuery();
            if(rs1.next())
            {	
            	//根据数据库返回的值设定FTP对象中的链接总数属性值
            	pojo.setFtpljzs(rs1.getString("ftpljzs"));
            }
            else
            {
            	pojo.setFtpljzs("无数据");
            }
            
            //设定查询字符串
            pstmt=conn.prepareStatement("select notetime, count(notetime) as num from ftpstat where countryname='"+countryname+"' and STATUS = 'c' and "+kssj+" and "+jssj+" group by notetime");
            rs1=pstmt.executeQuery();
            int ftpbfljs = 0;
            
            //获取所有的数据，并累加
            while(rs1.next())
            {
            	int temp = rs1.getInt("num");
            	if (temp>1) {
    				ftpbfljs += temp;
    			}
            }
            pojo.setFtpbfljs(String.valueOf(ftpbfljs));
            
            //设定查询字符串
        	pstmt=conn.prepareStatement("select count(*) as num from ftpstat t where STATUS = 'c' ");
            rs1=pstmt.executeQuery();
            if(rs1.next())
            {
            	pojo.setFtpyxylljzs(rs1.getString("num"));
            }
            else
            {
            	pojo.setFtpyxylljzs("无数据");
            }
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
    	
        return pojo;
	}
}
