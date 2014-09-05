package com.test.util;

import static org.junit.Assert.*;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Before;
import org.junit.Test;

import com.ims.util.ExcelUtil;

public class ExcelUtilTest {
	private HSSFWorkbook wb;
	private HSSFSheet sheet;
	
	
	public ExcelUtilTest(){}
	
	
	@Before
    public void setUp() throws Exception {
		this.wb = new HSSFWorkbook();
		this.sheet = this.wb.createSheet();
    }


	@Test
	public void testCopyRow() {
		HSSFRow source = this.sheet.createRow(1);
		HSSFRow target = this.sheet.createRow(2);
		//source.setrow
		assertTrue(true);
	}

	@Test
	public void testCopyCell() {
		HSSFRow row = this.sheet.createRow(0);
		HSSFCell source =row.createCell(1);
		source.setCellType(Cell.CELL_TYPE_STRING);
		source.setCellValue("test");
		
		HSSFCell target = row.createCell(2);
		ExcelUtil.copyCell(source, target);
		
		assertEquals(source.getCellType(), target.getCellType());
	    assertEquals(source.getStringCellValue(), target.getStringCellValue());
	}

}
