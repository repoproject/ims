package com.test.util;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.ims.util.DateTimeUtil;

public class DateTimeUtilTest {

	@Test
	public void testGetDateStringString() {
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat(DateTimeUtil.DEFAULT_FORMAT_DATE);
		Date actual = DateTimeUtil.getDate(sf.format(now),DateTimeUtil.DEFAULT_FORMAT_DATE);
		assertEquals(now, actual);
	}

	@Test
	public void testGetDateString() {
		//fail("Not yet implemented");
	}

	@Test
	public void testToDateString() {
		//fail("Not yet implemented");
	}

	@Test
	public void testToday() {
		//fail("Not yet implemented");
	}

	@Test
	public void testNow() {
		String now = DateTimeUtil.now();
	}

	@Test
	public void testCurrentTime() {
		//fail("Not yet implemented");
	}

	@Test
	public void testCurrentDate() {
		///fail("Not yet implemented");
	}

	@Test
	public void testCurrent() {
		//fail("Not yet implemented");
	}
	
	@Test
	public void testFormat(){
		String t = "Test2014-09-06";
		Date dt = DateTimeUtil.getDate("2014-09-06 00:00:00");
		String s = DateTimeUtil.format("Test{yyyy-MM-dd}",dt);
		System.out.println(t);
		System.out.println(s);
		assertEquals(s, t);
	}

}
