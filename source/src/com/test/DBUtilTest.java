/**
 * 
 */
package com.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.ims.util.DBUtil;

/**
 * @author ChengNing
 * @date   2014年9月2日
 */
public class DBUtilTest {

	/**
	 * Test method for {@link com.ims.util.DBUtil#getOneValue(java.lang.String)}.
	 */
	@Test
	public void testGetOneValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.ims.util.DBUtil#query(java.lang.String)}.
	 */
	@Test
	public void testQuery() {
		String sql = "select * from d_user";
		List<Object> list = DBUtil.query(sql);
	}

	/**
	 * Test method for {@link com.ims.util.DBUtil#execute(java.lang.String)}.
	 */
	@Test
	public void testExecuteString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.ims.util.DBUtil#execute(java.lang.String, java.lang.Object[])}.
	 */
	@Test
	public void testExecuteStringObjectArray() {
		fail("Not yet implemented");
	}

}