package com.test.util;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.ims.util.DateTimeUtil;

public class DateTimeUtilTest {

	@Test
	public void testGetDateTimeStringString() {
		String dateStr = "2010-11-01 10:10:00";
	    Date actual = DateTimeUtil.getDateTime(dateStr,DateTimeUtil.DEFAULT_FORMAT_DATETIME);
	    
	    Date date = null;
		try {
			SimpleDateFormat sf = new SimpleDateFormat(DateTimeUtil.DEFAULT_FORMAT_DATETIME);
			date = sf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		assertEquals(date, actual);
	}

	@Test
	public void testGetDateTimeString() {
		String dateStr = "2010-11-01 10:10:00";
	    Date actual = DateTimeUtil.getDateTime(dateStr);
	    
	    Date date = null;
		try {
			SimpleDateFormat sf = new SimpleDateFormat(DateTimeUtil.DEFAULT_FORMAT_DATETIME);
			date = sf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		assertEquals(date, actual);
	}
	
	@Test
	public void testGetDate(){
		String dateStr = "2010-11-01";
	    Date actual = DateTimeUtil.getDate(dateStr);
	    
	    Date date = null;
		try {
			SimpleDateFormat sf = new SimpleDateFormat(DateTimeUtil.DEFAULT_FORMAT_DATE);
			date = sf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		assertEquals(date, actual);
	}
	
	@Test
	public void testGetTime(){
		String time = "12:12:12";
	    Date actual = DateTimeUtil.getTime(time);
	    
	    Date date = null;
		try {
			SimpleDateFormat sf = new SimpleDateFormat(DateTimeUtil.DEFAULT_FORMAT_TIME);
			date = sf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		assertEquals(date, actual);
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
	public void testGetToday(){
		String time = "10:10:00";
		Date date = DateTimeUtil.getDateTime(DateTimeUtil.today() + " " + time);
		Date actual = DateTimeUtil.getToday(time);
		assertEquals(date, actual);
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
