/**
 * 
 */
package com.ims.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.Region;

import com.itextpdf.text.List;
import com.wabacus.config.ConfigLoadManager;

/**
 * 行从1开始，列从0开始
 * @author ChengNing
 * @date   2014年8月13日
 */
public class ExcelExport  extends HttpServlet{
	
	private static Logger logger = Logger.getLogger(ExcelExport.class);
	private HSSFWorkbook wb;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("enter excelExport");
		String path = ExcelExport.class.getResource("").getPath() + "template.xls";
		logger.info("template:" + path);
        
		FileInputStream fileInputStream = new FileInputStream(path);
		createExcel(fileInputStream);

		logger.info("Excel创建完毕，开始下载输出");
		response.setContentType("application/vnd.ms-excel");	
		
		ServletOutputStream out = response.getOutputStream();
		this.wb.write(out);
		out.flush();
		out.close();
	}
	
	private void createExcel(InputStream is,String test){
		try {
			wb = new HSSFWorkbook(is);
			createSheet1("R1 - CHEM");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createSheet1(String sheetName){
//		for(int i=0;i<20;i++){
			HSSFSheet sheet = wb.getSheetAt(5);
//			System.out.println(sheet.getSheetName());
//		}
////		HSSFRow row0 = sheet.getRow(0);
////		HSSFCell cell1 = row0.getCell(0);
////		cell1.setCellValue("testtest");
		int startRow = 7; //第8行开始
		//第10行是模板尾行，poi无插入，所以复制此行，最后添加到最后
		HSSFRow rowFooter = sheet.getRow(9);
		//如下用于标号
		for(int i=startRow;i<8;i++){
			HSSFRow row = sheet.getRow(i);
			if(row == null)
				row = sheet.createRow(i);
			for(int j=0;j<50;j++){
				HSSFCell cell = row.getCell(j);//row.createCell(j);
				if(cell == null)
					cell = row.createCell(j);
//				System.out.println(cell.getStringCellValue());
				cell.setCellValue(String.valueOf(i) + "," + String.valueOf(j));
				System.out.println(cell.getStringCellValue());
				
			}
		}
		
		HSSFRow row = sheet.createRow(20);
		copyRow(rowFooter, row, 50);
		//row = rowFooter;
		//rowFooter.setRowNum(20);
	}
	
	private void copyRow(HSSFRow source,HSSFRow target,int cellCount){
		if(target == null)
			return;
		for(int i=0;i<cellCount;i++){
			HSSFCell sourceCell = source.getCell(i);
			if(sourceCell != null){
				HSSFCell targetCell = target.createCell(i);
				targetCell.setCellStyle(sourceCell.getCellStyle());
				targetCell.setCellType(sourceCell.getCellType());
				targetCell.setCellValue(sourceCell.getStringCellValue());
			}
		}
	}
	
	private void insertRow(){
		
	}
	
	private void createSheet2(){
		
	}
	/**
	 * ref http://www.cnblogs.com/marsmile/p/3669513.html
	 */
	private void createExcel(InputStream is){
//		wb = new HSSFWorkbook();
		try {
			wb = new HSSFWorkbook(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		createSheet("R1 - CHEM");
//		createSheet("R2 - LCMS & Micro ");
//		createSheet("R3-Hema & Flow");
//		createSheet("R4-Variant 2");
//		createSheet("R5 - Oversea");
//		createSheet("R6 - PCR");
//		createSheet("R7 - Urine");
//		createSheet("R8 - Cent& Immu&Liai&Mini");
//		createSheet("R9 - Elisa");
//		createSheet("Lab Supplier");
//		createSheet("Discard_CLab");
//		createSheet("Validation_CLab");
//		createSheet("R10-Oncology");
//		createSheet("Discard_APLab");
//		createSheet("Validation_APLab");
	}
	
	/**
	 * 
	 * @param sheetName
	 */
	private void createSheet(String sheetName){
		HSSFSheet sheet = wb.createSheet(sheetName);
		createHeader(sheet);
		//setStyle(sheet);
	}
	
	private void createHeader(HSSFSheet sheet){
		ExcelHeader header = new ExcelHeader(sheet);
		header.createHeader();
	
	}
	
	/**
	 * set style
	 */
	private void setStyle(HSSFSheet sheet){
		HSSFCellStyle headerStyle  = this.wb.createCellStyle();
		HSSFFont font = this.wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		
		headerStyle.setFont(font);
		//设置背景色
		HSSFRow row3 = sheet.getRow(2);
		HSSFCellStyle rowStyle = this.wb.createCellStyle();
		rowStyle.setBorderTop(HSSFCellStyle.BORDER_THICK);
		row3.setRowStyle(headerStyle);
		
		//
		for(int i= 2;i<6;i++){
			HSSFRow row = sheet.getRow(i);
			for(int j=4;j<9;j++){
				HSSFCell cell = row.getCell(j);
				HSSFCellStyle style = this.wb.createCellStyle();
				style.setFillBackgroundColor((short) 13);
				//style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				cell.setCellStyle(style);
			}
		}
		
		

		Region region = new Region(3,(short)4,3,(short)8);
		sheet.addMergedRegion(region);
	}
	

	
	public static void main(String [] arc){
		System.out.println("test");
		try {
			FileInputStream fileInputStream = new FileInputStream(ExcelExport.class.getResource("").getPath() + "template.xls");
			
			ExcelExport testExcelExport = new ExcelExport();
			testExcelExport.createExcel(fileInputStream,"test");
		    FileOutputStream fStream = new FileOutputStream("D://test1.xls");
		    testExcelExport.wb.write(fStream);
		    fStream.flush();
			fStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
