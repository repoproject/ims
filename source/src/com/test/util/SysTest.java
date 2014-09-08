/**
 * 
 */
package com.test.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ims.util.Sys;

/**
 * @author ChengNing
 * @date   2014-9-8
 */
public class SysTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.ims.util.Sys#serverRootPath()}.
	 */
	@Test
	public void testServerRootPath() {
		System.out.println(Sys.serverRootPath());
	}

	/**
	 * Test method for {@link com.ims.util.Sys#classRootPath()}.
	 */
	@Test
	public void testClassRootPath() {
		System.out.println(Sys.classRootPath());
	}

}
