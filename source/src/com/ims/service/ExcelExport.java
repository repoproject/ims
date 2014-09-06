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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.Region;

import com.ims.report.R1Sheet;
import com.ims.util.ExcelUtil;
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
	
	private void createExcel(InputStream is){
		try {
			wb = new HSSFWorkbook(is);
			R1Sheet r1 = new R1Sheet(wb);
			r1.createSheet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	

//	
//	/**
//	 * set style
//	 */
//	private void setStyle(HSSFSheet sheet){
//		HSSFCellStyle headerStyle  = this.wb.createCellStyle();
//		HSSFFont font = this.wb.createFont();
//		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//		
//		headerStyle.setFont(font);
//		//设置背景色
//		HSSFRow row3 = sheet.getRow(2);
//		HSSFCellStyle rowStyle = this.wb.createCellStyle();
//		rowStyle.setBorderTop(HSSFCellStyle.BORDER_THICK);
//		row3.setRowStyle(headerStyle);
//		
//		//
//		for(int i= 2;i<6;i++){
//			HSSFRow row = sheet.getRow(i);
//			for(int j=4;j<9;j++){
//				HSSFCell cell = row.getCell(j);
//				HSSFCellStyle style = this.wb.createCellStyle();
//				style.setFillBackgroundColor((short) 13);
//				//style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//				cell.setCellStyle(style);
//			}
//		}
//		
//		
//
//		Region region = new Region(3,(short)4,3,(short)8);
//		sheet.addMergedRegion(region);
//	}
	

	
	public static void main(String [] arc){
		System.out.println("test");
		try {
			FileInputStream fileInputStream = new FileInputStream(ExcelExport.class.getResource("").getPath() + "template.xls");
			
			ExcelExport testExcelExport = new ExcelExport();
			testExcelExport.createExcel(fileInputStream);
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
