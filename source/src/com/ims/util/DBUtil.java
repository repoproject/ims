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

/**
 * 数据库工具， 提供常用的数据库操作
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class DBUtil {
	// 数据库驱动
	public static String driver = "";
	// 数据库url
	public static String url = "";
	// 用户名
	public static String user = "";
	// 密码
	public static String password = "";

	/**
	 * 获取数据库连接
	 */
	private static Connection getConnection() {
/*
		// 数据库驱动
		String driver = "oracle.jdbc.driver.OracleDriver";
		// 数据库url
		String url = "jdbc:oracle:thin:@192.168.14.151:1521:WJQW";
//		String url = "jdbc:oracle:thin:@localhost:1521:WJQW";
		// 用户名
		String user = "cpzh";
//		String user = "cpzhe";
		// 密码
		String password = "cpzh";
//		String password = "cpzhe";
	*/	
		// 数据库连接
		Connection conn = null;
		try {
			// 注册数据库驱动
			Class.forName(driver);
			// 获取数据库连接
		    conn = DriverManager.getConnection(url, user, password);			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 数据库查询
	 * @param sql 不带参数的SQL语句
	 * @return List 查询结果
	 */
	public static List<Object> executeQuery(String sql) {
		// 查询结果列表
		List<Object> list = new ArrayList<Object>();		
		// 数据库连接
		Connection conn = null;
		// Statement
		Statement stmt = null;
		// 结果集
		ResultSet rs = null;	
		// 元数据
		ResultSetMetaData rsmd = null;
		
		try {
			// 获取连接
			conn = getConnection();
			// 获取Statement
			stmt = conn.createStatement();
			// 执行sql语句
			rs = stmt.executeQuery(sql);	
			// 获取元数据
			rsmd = rs.getMetaData();
			
			// 遍历结果集，将其中每行数据数据放入list
			while (rs.next()) {		
				Map<Object, Object> map = new HashMap<Object, Object>();
				// 每行数据中的每一列以键-值对的形式放入HashMap中
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					map.put(rsmd.getColumnName(i), rs.getObject(i));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭结果集
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			// 关闭Statement
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
		
        return list;
	}
	/**
	 * 数据库查询
	 * @param sql 带参数的SQL语句
	 * @param params SQL语句参数
	 * @return List 查询结果
	 */
	public static List<Object> executeQuery(String sql, Object...params) throws SQLException {
		
		// 查询结果列表
		List<Object> list = new ArrayList<Object>();		
		// 数据库连接
		Connection conn = null;
		// PreparedStatement
		PreparedStatement pstmt = null;
		// 结果集
		ResultSet rs = null;	
		// 元数据
		ResultSetMetaData rsmd = null;
		
		try {
			conn = getConnection();
			// 预编译带参数的sql语句
			pstmt = conn.prepareStatement(sql);
			// 设置sql语句参数
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}
			// 执行sql语句
			rs = pstmt.executeQuery();	
			// 获取元数据
			rsmd = rs.getMetaData();
			
			// 遍历结果集，将其中的每行数据数据放入list
			while (rs.next()) {		
				Map<Object, Object> map = new HashMap<Object, Object>();
				// 每行数据中的每一列以键-值对的形式放入HashMap中
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					map.put(rsmd.getColumnName(i), rs.getObject(i));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭结果集
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			// 关闭PreparedStatement
			if (pstmt != null) {
				try {
					pstmt.close();
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
		
        return list;
		
	}
	
	/**
	 * 数据库更新（增删改）
	 * @param sql 不带参数的SQL语句
	 * @return int 更新操作影响行数
	 */
	public static int executeUpdate(String sql) {
		// 更新操作影响行数
		int count = 0;		
		// 数据库连接
		Connection conn = null;
		// Statement
		Statement stmt = null;
		
		try {
			// 获取连接
			conn = getConnection();
			// 获取Statement
			stmt = conn.createStatement();
			// 执行sql语句
			count = stmt.executeUpdate(sql);			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			// 关闭Statement
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
		
		return count;
	}
	
	/**
	 * 数据库更新（增删改）
	 * @param sql 带参数的SQL语句
	 * @param params SQL语句参数
	 * @return int 更新操作影响行数
	 */
	public static int executeUpdate(String sql, Object...params) {
		// 更新操作影响行数
		int count = 0;
		// 数据库连接
		Connection conn = null;
		// PreparedStatement
		PreparedStatement pstmt = null;

		try {
			// 获取连接
			conn = getConnection();
			// 预编译带参数的sql语句
			pstmt = conn.prepareStatement(sql);
			// 设置sql语句参数
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}
			// 执行sql语句
			count = pstmt.executeUpdate();		

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {			
			// 关闭 PreparedStatement
			if (pstmt != null) {
				try {
					pstmt.close();
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
		return count;
	}

}
