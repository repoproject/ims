package com.test.report;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ims.report.config.Sheet;
import com.ims.report.excel.ISheet;
import com.ims.report.excel.R1Sheet;

public class InventoryReportTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInventoryReportDate() {
		fail("Not yet implemented");
	}

	@Test
	public void testInventoryReportDateDate() {
		fail("Not yet implemented");
	}

	@Test
	public void testRun() {
		fail("Not yet implemented");
	}

	@Test
	public void testReport() {
		try {
			Class clazz = Class.forName("com.ims.report.excel.R1Sheet");
			Constructor constructor = clazz.getConstructor(String.class);
			R1Sheet sheetObj = (R1Sheet) constructor.newInstance("ttttt");
			//sheetObj.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
