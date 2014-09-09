package com.test.common;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ims.common.TaskData;

public class TaskDataTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTaskData() {
		fail("Not yet implemented");
	}

	@Test
	public void testDayRunTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testTodayRunTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testNextTaskDate() {
		fail("Not yet implemented");
	}

	@Test
	public void testLastTaskDate() {
		String runpointString = "26";
		TaskData tData = new TaskData();
		Date date = tData.lastTaskDate();
	}

	@Test
	public void testCurrentMonthTask() {
		fail("Not yet implemented");
	}

}
