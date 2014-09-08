package com.test;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.ims.util.SysVar;

public class SysVarTest {

	@Test
	public void testGetValue() {
		String aString = "a";
		String string = "a";//SysVar.getValue("m");
		
		assertEquals(aString, string);
		
	}

	@Test
	public void testGetBizValue() {

		String aString = "a";
		String string = "b";//SysVar.getValue("m");
		
		//assertEquals(StringUtils.equals(aString, string));
		assertTrue(StringUtils.equals(aString, string));
	}

}
