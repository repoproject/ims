package com.wabacusdemo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.crypto.provider.RSACipher;
import com.wabacus.system.ReportRequest;
/**
 * 服务器端数据验证
 * @author jyp
 * @修改人：zhouhl
 * @修改时间：2013-12-30
 *
 */
public class Servervalidate1 {
	
	/**
	 * 验证文件名是否唯一
	 * @author:jyp
	 * @修改者：zhouhl
	 * @修改时间：2013-12-31
	 * @返回值：文件名不存在返回true，否则返回false
	 */
    public static boolean isUniqueFilename(ReportRequest rrequest,String filenameid,Map<String,String> mValues,List<String> lstErrorMessages)
    {
    	//文件名为空返回ture
        if(filenameid==null||filenameid.trim().equals("")){
        	return true;
        }
        System.out.println("验证文件名是否唯一开始...");
        
        for(Entry<String,String> entryTmp:mValues.entrySet())
        {
            System.out.print(entryTmp.getKey()+"="+entryTmp.getValue()+";;");
        }
        System.out.println();
        filenameid=filenameid.trim();
        
        //获取旧的文件名编号
        String oldfilenameid=mValues.get("filenameid__old");
        
        //若当前文件名编号等于旧的文件名编号，返回真
        if(filenameid.equals(oldfilenameid)) {
        	return true;
        }
        
        //根据文件名编号从数据库中查询对应文件名编号的文件总和
        Connection conn=rrequest.getConnection();
        Statement stmt=null;
        ResultSet rs = null;
        try
        {
            stmt=conn.createStatement();
            rs=stmt.executeQuery("select count(*) from pfilematadata where filenameid='"+filenameid+"'");
            rs.next();
            int count=rs.getInt(1);
            return count==0;
        }catch(SQLException e)
        {
        	//异常处理，打印异常信息
            e.printStackTrace();
            return false;
        }finally
        {
        	//关闭数据库链接
            try
            {
            	if(rs != null){
            		rs.close();
            	}
                if(stmt!=null) stmt.close();
                if (conn != null) {
					conn.close();
				}
            }catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
	 * 验证文件类型编号是否唯一
	 * @author:jyp
	 * @修改者：zhouhl
	 * @修改时间：2013-12-31
	 * @返回值：文件类型编号不存在返回true，否则返回false
	 */
    public static boolean isUniqueFiletype(ReportRequest rrequest,String id,Map<String,String> mValues,List<String> lstErrorMessages)
    {
    	//若文件类型ID为空，返回真
    	if(id==null||id.trim().equals("")){
    		return true;
    	}
    	
        for(Entry<String,String> entryTmp:mValues.entrySet())
        {
            System.out.print(entryTmp.getKey()+"="+entryTmp.getValue()+";;");
        }
        System.out.println();
        id=id.trim();
        
        //获取旧的的文件类型编号
        String oldfilenameid=mValues.get("id__old");
        
        //若当前编号和旧的的编号相等，返回真
        if(id.equals(oldfilenameid)){
        	return true;
        }
        
        //根据ID号从数据库中查询对应的文件类型
        Connection conn=rrequest.getConnection();
        Statement stmt=null;
        ResultSet rs = null;
        try
        {
            stmt=conn.createStatement();
            rs=stmt.executeQuery("select count(*) from filetype where id='"+id+"'");
            rs.next();
            int count=rs.getInt(1);
            
            return count==0;
        }catch(SQLException e)
        {
        	//异常处理，打印异常信息
            e.printStackTrace();
            return false;
        }finally
        {
        	//关闭数据库链接
            try
            {
            	if (rs != null) {
					rs.close();
				}
                if(stmt!=null) stmt.close();
                if (conn != null) {
					conn.close();
				}
            }catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}
