package com.ims.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wabacus.config.Config;

/**
 * 数据库工具， 提供常用的数据库操作
 * @author ChengNing
 * @version 1.0
 */
public class DBUtil {
	
	private static Logger logger = Logger.getLogger(DBUtil.class);

	private Connection conn = null;
	private PreparedStatement stmt=null;
	private ResultSet rs = null;
	
	/**
	 * 获取数据库连接
	 */
	private Connection getConnection() {
		DBAdapter adapter = new DBAdapter();
		// 数据库连接
		Connection conn = adapter.getConnection();
		return conn;
	}
	
	/**
	 * 查询执行方法
	 * @param sql
	 * @param params
	 * @return
	 */
	private List<Object> executeQuery(String sql,Object...params){
		List<Object> list = new ArrayList<Object>();
		if(Config.show_sql) logger.info(sql);
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			if(params !=null){
				for(int i=0;i< params.length;i++){
					stmt.setObject(i+1, params[i]);
				}
			}
			rs = stmt.executeQuery();
			ResultSetMetaData rsmData = rs.getMetaData();
			while(rs.next()){
				Map<Object, Object> map = new HashMap<Object, Object>();
				for (int i = 1; i <= rsmData.getColumnCount(); i++) {
					map.put(rsmData.getColumnName(i), rs.getObject(i));
				}
				list.add(map);
			}
		} catch (Exception e) {
			logger.error(e.getClass().getName() + ":" + e.getMessage());
		}
		finally{
			close();
		}
		return list;
	}
	
	/**
	 * 数据库更新（增删改）
	 * @param sql 带参数的SQL语句
	 * @param params SQL语句参数
	 * @return int 更新操作影响行数
	 */
	private int executeUpdate(String sql, Object...params) {
		// 更新操作影响行数
		int count = 0;
		if(Config.show_sql) logger.info(sql);
		try {
			// 获取连接
			conn = getConnection();
			// 预编译带参数的sql语句
			stmt = conn.prepareStatement(sql);
			// 设置sql语句参数
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i + 1, params[i]);
			}
			// 执行sql语句
			count = stmt.executeUpdate();		

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return count;
	}
	
	/**
	 * 销毁所有数据库连接资源
	 */
	private void close(){
		// 关闭结果集
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// 关闭PreparedStatement
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// 关闭数据库连接
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param sql
	 * @return
	 */
	public static String getOneValue(String sql){
		return getOneValue(sql, null);
	}
	/**
	 * 得到一个值的查询
	 * @param sql
	 * @return
	 */
	public static String getOneValue(String sql,Object...params){
		String re = "";
		List<Object> list = query(sql,params);
		if(list != null && list.size() > 0){

			Map<Object, Object> map = (HashMap<Object, Object>)list.get(0);
			Object key = map.keySet().toArray()[0];
			re = map.get(key).toString();
			return re;
		}
		return "";
	}
	
	/**
	 * 数据库查询
	 * @param sql 不带参数的SQL语句
	 * @return List 查询结果
	 */
	public static List<Object> query(String sql) {
        return query(sql,null);
	}
	
	/**
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public static List<Object> query(String sql,Object...params) {
		DBUtil db = new DBUtil();
        return db.executeQuery(sql,params);
	}
	
	/**
	 * 
	 * @param sql
	 * @return
	 */
	public static int execute(String sql){
		return execute(sql, null);
	}
	
	/**
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public static int execute(String sql,Object...params){
		DBUtil db = new DBUtil();
		return db.executeUpdate(sql, params);
	}
	
	
	
	


}
