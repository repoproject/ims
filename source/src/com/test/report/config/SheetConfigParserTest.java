/**
 * 
 */
package com.test.report.config;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ims.report.config.SheetConfigParser;

/**
 * @author ChengNing
 * @date   2014-9-6
 */
public class SheetConfigParserTest {

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
	
	@Test
	public void testGetSheet(){
		String sheetName = "test";
		SheetConfigParser.getInstance().getSheet(sheetName);
	}

}
