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
		
		formula = "A10+B10";
		expected = "A14+B14";
		actual = ExcelUtil.moveFormula(formula, moveCount);
		assertEquals(expected, actual);
		
		
	}
	
	@Test
	public void testMoveFormulaByInsert(){

		String formula = "SUM(A3:A11)";
		int insertRow1 = 3; //第4行插入
		int moveCount = 1;
		

		//范围内测试
		String expected1 = "SUM(A3:A12)";
		String actual = ExcelUtil.moveFormula(formula, moveCount,insertRow1);
		assertEquals(expected1, actual);
		
		//边界检查,插入本行后移，起点行插入两个数字都移动，范围不变
		String expected2 = "SUM(A4:A12)";
		int insertRow2 = 2;//第3行插入
		String actual2 = ExcelUtil.moveFormula(formula, moveCount,insertRow2);
		assertEquals(expected2, actual2);
		
		//边界检查，终点行测试，范围内插入，起点不变，终点增加
		String expected21 = "SUM(A3:A12)";
		int insertRow21 = 10;//第11行插入
		String actual21 = ExcelUtil.moveFormula(formula, moveCount,insertRow21);
		assertEquals(expected21, actual21);
		
		//范围外检查
		String expected3 = "SUM(A4:A12)";
		int insertRow3 = 1;//第2行插入
		String actual3 = ExcelUtil.moveFormula(formula, moveCount,insertRow3);
		assertEquals(expected3, actual3);
		
		//绝对引用符号检查
		formula = "=(R9/$R$7)+(S9/$S$7)+(T9/$T$7)+(U9/$U$7)+(V9/$V$7)";
		String expected4 = "=(R10/$R$7)+(S10/$S$7)+(T10/$T$7)+(U10/$U$7)+(V10/$V$7)";
		int insertRow4 = 0;//第1行插入
		String actual4 = ExcelUtil.moveFormula(formula, moveCount,insertRow4);
		assertEquals(expected4, actual4);
	}
	

}
