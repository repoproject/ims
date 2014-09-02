/**
 * 
 */
package com.ims.util;

import java.sql.Connection;

import com.wabacus.config.Config;


/**
 * DB适配器，适配wabacus框架的数据库操作
 * @author ChengNing
 * @date   2014年9月2日
 */
public class DBAdapter {
    
    private String datasource;//此报表所使用的数据源，默认为<sql/>中的datasource配置，如果这里也没配置，则取wabacus.cfg.xml中<datasources/>标签中的default属性配置的值

	public DBAdapter() {
		this.datasource = Config.getInstance().getDefault_datasourcename();
	}
    public String getDatasource()
    {
        return datasource;
    }

    public void setDatasource(String datasource)
    {
        this.datasource=datasource;
    }
    
    public static void main(String[] argc) {
    	DBAdapter dbAdapter = new DBAdapter();
    	
    	System.out.println(dbAdapter.getDatasource());
	}
    
    public Connection getConnection() {
		Connection conn=Config.getInstance().getDataSource("ds_mysql").getConnection();//取配置的默认数据源的连接
		return conn;
	}

}
