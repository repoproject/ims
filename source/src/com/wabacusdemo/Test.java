package com.wabacusdemo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.wabacus.system.ReportRequest;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.AbsPageInterceptor;
/**
 * 测试用
 * @author jyp
 * @修改人：zhouhl
 * @修改时间：2013-12-30
 *
 */
public class Test extends AbsInterceptorDefaultAdapter{

	public void doStart(ReportRequest rrequest)
	{
		//获取数据库连接
		Connection conn=rrequest.getConnection();
        Statement stmt=null;
        try
        {
            stmt=conn.createStatement();
            for (int i = 0; i < 1000000; i++) {
            	String a = "insert into msguser(id,name,msg_number,comm) values(msguser_id.nextval,'"+String.valueOf(i)+"','"+String.valueOf(i)+"','"+String.valueOf(i)+"');";
            	System.out.println(a);
            	stmt.executeQuery(a);
			}
            
        }catch(SQLException e)
        {
        	//异常处理，打印异常信息
            e.printStackTrace();
        }finally
        {
        	//关闭数据库链接
            try
            {
                if(stmt!=null) stmt.close();
            }catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
	}
}
