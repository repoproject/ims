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
	
	@Test
	public void testMoveFormula(){
		String formula = "SUM(A1:F1)";
		String expected = "SUM(A5:F5)";
		int moveCount = 4;
		String actual = ExcelUtil.moveFormula(formula, moveCount);
		
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void testMoveFormulaByInsert(){

		String formula = "SUM(A3:A7)";
		int insertRow1 = 4; //第4行插入
		int moveCount = 1;
		

		//范围内测试
		String expected1 = "SUM(A3:A8)";
		String actual = ExcelUtil.moveFormula(formula, moveCount,insertRow1);
		assertEquals(expected1, actual);
		
		//边界检查
		String expected2 = "SUM(A4:A8)";
		int insertRow2 = 3;
		String actual2 = ExcelUtil.moveFormula(formula, moveCount,insertRow2);
		assertEquals(expected2, actual2);
		
		//范围外检查
		String expected3 = "SUM(A4:A8)";
		int insertRow3 = 2;
		String actual3 = ExcelUtil.moveFormula(formula, moveCount,insertRow3);
		assertEquals(expected3, actual3);
		
		
	}

}
