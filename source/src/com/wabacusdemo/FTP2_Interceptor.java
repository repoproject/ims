package com.wabacusdemo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class FTP2_Interceptor extends AbsInterceptorDefaultAdapter {
	
	/**
	 * 装载数据之前执行的函数
	 * @author jyp
	 * @修改人：zhouhl
	 * @修改时间：2013-12-30
	 * @返回值：查询语句字符串对象
	 * 
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
			//设定开始时间、结束时间和国家查询条件占位符
            PreparedStatement pstmt=null;
            ResultSet rs1 = null;
            ResultSet rs2 = null;
            ResultSet rs3 = null;
            Connection conn = null;
            List lstResults=new ArrayList();
            try
            {
            	String kssj = "1=1";
            	String jssj = "1=1";
            	String guojia = "1=1";
            	String filetype = "1=1";
            	
            	//判断页面传过来的查询数据字符串中是否含有kssj，若含有则获取请求中的开始时间
            	if (sql.indexOf("kssj") > 0) {
					kssj = "ftpstat.notetime >= to_date('"+rrequest.getAttribute("txtbegin").toString()+"','yyyy-mm-dd')";
				}
            	
            	//判断页面传过来的查询数据字符串中是否含有jssj，若含有则获取请求中的结束时间
            	if (sql.indexOf("jssj") > 0) {
					jssj = "ftpstat.notetime <= to_date('"+rrequest.getAttribute("txtend").toString()+"','yyyy-mm-dd')";
				}
            	
            	//判断页面传过来的查询数据字符串中是否含有guojia，若含有则获取请求中的国家名
            	if (sql.indexOf("wenjianleixing") > 0) {
            		String leixing = rrequest.getAttribute("filetype").toString();
					jssj = "filetype.id = '"+leixing+"' and ftpstat.filename like '%."+leixing+"%' ";
				}
            	
            	conn=rrequest.getConnection(rbean.getSbean().getDatasource());
            	
            	//判断查询字符串中是否包含‘guojia’字符串
            	if (sql.indexOf("guojia") > 0) { 
            		String countryname = rrequest.getAttribute("country").toString();
					guojia = "countryname = '"+countryname+"'";
					
					//判断查询字符串中是否包含"count(*)"字符串
					if(sql.indexOf("count(*)")>0)
	                {
						//对满足条件的数据求和
                    	pstmt=conn.prepareStatement("select ftpstat.filesize,ftpstat.filename from ftpstat,filetype where ftpstat.countryname='"+countryname+"' and ftpstat.STATUS = 'c' and "+kssj+" and "+jssj+" and "+filetype+" ");
                        rs1=pstmt.executeQuery();
                        Map<String, Integer> filetype_map = new HashMap<String, Integer>();
                        while(rs1.next())
                        {
                        	String filename = rs1.getString("filename");
                        	String filetype_val = filename.substring(filename.lastIndexOf("."));
                        	
                        	//若文件类型中不包含指定的文件类型，则将类型赋值为1
                        	if (!filetype_map.containsKey(filetype_val)) {
                        		filetype_map.put(filetype_val, 1);
							}
                        }
                        
                        //保存指定文件类型大小
	                    lstResults.add(filetype_map.size());
	                    rs1.close();
	                }else
	                {
	                	//从数据库中获取指定时间段、国家、文件类型的文件大小和文件名
	                    pstmt=conn.prepareStatement("select ftpstat.filesize,ftpstat.filename from ftpstat,filetype where ftpstat.countryname='"+countryname+"' and ftpstat.STATUS = 'c' and "+kssj+" and "+jssj+" and "+filetype+" ");
                        rs1=pstmt.executeQuery();
                        Map<String, Integer> filetype_map = new HashMap<String, Integer>();
                        Map<String, Integer> filesize_map = new HashMap<String, Integer>();
                        while(rs1.next())
                        {
                        	//将查询结果保存到返回数据集中
                        	String filename = rs1.getString("filename");
                        	int filesize = rs1.getInt("filesize");
                        	String filetype_val = filename.substring(filename.lastIndexOf("."));
                        	
                        	//判断文件类型值是否在文件类型集中
                        	if (filetype_map.containsKey(filetype_val)) {
								filetype_map.put(filetype_val, filetype_map.get(filetype_val)+1);
								filesize_map.put(filetype_val, filesize_map.get(filetype_val)+filesize);
							}
                        	else
                        	{
                        		//若文件类型值不在文件类型集中，则对集合重新复制
                        		filetype_map.put(filetype_val, 1);
                        		filesize_map.put(filetype_val, filesize);
                        	}
                        	
                        }
                        
                        //遍历上面查询到的所有文件类型数据，根据其结果并创建FTP2对象
                        Iterator iter = filetype_map.entrySet().iterator();
                        while(iter.hasNext())
                        {
                        	FTP2_POJO pojo=new FTP2_POJO(rrequest,rbean);
                        	Entry entry = (Entry) iter.next();
                        	String filetype_temp = entry.getKey().toString();
                        	String xiazaishu = entry.getValue().toString();
                        	pojo.setCountryname(countryname);
                        	pojo.setFiletype(filetype_temp);
                        	pojo.setFtpxzs(xiazaishu);
                        	pojo.setFtpxzl(filesize_map.get(entry.getKey().toString()).toString());
                        	lstResults.add(pojo);
                        }
	                }
            	}
            	else
            	{
            		Long begin = System.currentTimeMillis();
            		if(sql.indexOf("count(*)")>0)
                    {
            			//判断查询语句中是否含有求和字符串
            			int linenum=0;
            			
            			//从数据库中根据指定时间段和文件类型查询国家名
                		pstmt=conn.prepareStatement("select distinct ftpstat.countryname from ftpstat,filetype where ftpstat.STATUS = 'c' and "+kssj+" and "+jssj+" and "+filetype+" ");
                        rs2=pstmt.executeQuery();
                        
                        //将查询结果保存到文件类型MAP中
                        while(rs2.next())
                        {
                        	String countryname = rs2.getString("countryname");
                        	
                        	pstmt=conn.prepareStatement("select ftpstat.filename from ftpstat,filetype where ftpstat.countryname='"+countryname+"' and ftpstat.STATUS = 'c' and "+kssj+" and "+jssj+" and "+filetype+" ");
                            rs3=pstmt.executeQuery();
                            Map<String, Integer> filetype_map = new HashMap<String, Integer>();
                            while(rs3.next())
                            {
                            	String filename = rs3.getString("filename");
                            	String filetype_val = filename.substring(filename.lastIndexOf("."));
                            	
                            	//若文件类型中不包含指定的文件类型，则将类型赋值为1
                            	if (!filetype_map.containsKey(filetype_val)) {
                            		filetype_map.put(filetype_val, 1);
                            	}
                            }
                            
                            linenum+=filetype_map.size(); 
                        }
                        lstResults.add(linenum);
                    }else
                    {
                    	//从数据库中根据指定时间段和文件类型查询国家名
                    	pstmt=conn.prepareStatement("select distinct ftpstat.countryname from ftpstat,filetype where ftpstat.STATUS = 'c' and "+kssj+" and "+jssj+" and "+filetype+" ");
                        rs2=pstmt.executeQuery();
                        while(rs2.next())
                        {
                        	//获取国家名
                        	String countryname = rs2.getString("countryname");
                        	
                        	//设置查询语句
                        	pstmt=conn.prepareStatement("select ftpstat.filesize,ftpstat.filename from ftpstat,filetype where ftpstat.countryname='"+countryname+"' and ftpstat.STATUS = 'c' and "+kssj+" and "+jssj+" and "+filetype+" ");
                            rs3=pstmt.executeQuery();
                            Map<String, Integer> filetype_map = new HashMap<String, Integer>();
                            Map<String, Integer> filesize_map = new HashMap<String, Integer>();
                           
                            //遍历结果集
                            while(rs3.next())
                            {
                            	String filename = rs3.getString("filename");
                            	int filesize = rs3.getInt("filesize");
                            	String filetype_val = filename.substring(filename.lastIndexOf("."));
                            	if (filetype_map.containsKey(filetype_val)) {
									filetype_map.put(filetype_val, filetype_map.get(filetype_val)+1);
									filesize_map.put(filetype_val, filesize_map.get(filetype_val)+filesize);
								}
                            	else
                            	{
                            		filetype_map.put(filetype_val, 1);
                            		filesize_map.put(filetype_val, filesize);
                            	}
                            	
                            }
                            
                            //遍历上面查询到的所有文件类型数据，根据其结果并创建FTP2对象
                            Iterator iter = filetype_map.entrySet().iterator();
                            while(iter.hasNext())
                            {
                            	FTP2_POJO pojo=new FTP2_POJO(rrequest,rbean);
                            	Entry entry = (Entry) iter.next();
                            	String filetype_temp = entry.getKey().toString();
                            	String xiazaishu = entry.getValue().toString();
                            	pojo.setCountryname(countryname);
                            	pojo.setFiletype(filetype_temp);
                            	pojo.setFtpxzs(xiazaishu);
                            	pojo.setFtpxzl(filesize_map.get(entry.getKey().toString()).toString());
                            	lstResults.add(pojo);
                            }
                        	
                            
                        }
                        System.out.println(System.currentTimeMillis()-begin);
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
            	if (rs1 != null) {
					try {
						rs1.close();
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
            	if (rs2 != null) {
					try {
						rs2.close();
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
            	if (rs3 != null) {
					try {
						rs3.close();
					} catch (Exception e2) {
						// TODO: handle exception
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
                if (conn != null) {
					try {
						conn.close();
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
            }
        }
		
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

}
