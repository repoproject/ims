/**
 * 
 */
package com.ims.report;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

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
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.Region;

import com.ims.report.excel.R1Sheet;
import com.ims.util.DateTimeUtil;
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
        
		//FileInputStream fileInputStream = new FileInputStream(path);
		createExcel();

//		logger.info("Excel创建完毕，开始下载输出");
//		response.setContentType("application/vnd.ms-excel");	
//		
//		ServletOutputStream out = response.getOutputStream();
//		this.wb.write(out);
//		out.flush();
//		out.close();
	}
	
	private void createExcel(){
	    Date startDate=DateTimeUtil.getDate("2014-08-26");
		Date endDate = DateTimeUtil.getDate("2014-10-26");
			
		InventoryReport reportor = new InventoryReport(startDate,endDate);
		reportor.run();
//			Thread reportThread = new Thread(reportor);
//			reportThread.start();
			//r1.createSheet();
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
			testExcelExport.Test(fileInputStream);
		    FileOutputStream fStream = new FileOutputStream("D://test1.xls");
		    testExcelExport.wb.write(fStream);
		    fStream.flush();
			fStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void Test(FileInputStream is){
		try {
			this.wb = new HSSFWorkbook(is);
			HSSFSheet sheet = wb.createSheet("test");
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			
			DataFormat hssfDF = wb.createDataFormat();
			CellStyle doubleStyle = wb.createCellStyle(); //会计专用  
			doubleStyle.setDataFormat(hssfDF.getFormat("_ * #,##0.00_ ;_ * \\-#,##0.00_ ;_ * \"-\"??_ ;_ @_ ")); //poi写入后为会计专用  
//	        intStyle.setDataFormat(hssfDF.getFormat("0")); //poi写入后为会计专用  
	        
	        cell.setCellStyle(doubleStyle);
	        cell.setCellValue(0);
	       
	        HSSFSheet sheet2 = wb.getSheetAt(5);
	        HSSFRow row2 = sheet2.getRow(7);
	        HSSFCell cell2 = row2.getCell(6);
	        String string = cell2.getCellStyle().getDataFormatString();
	        cell2.setCellValue(0);
	        System.out.println(string);
	        System.out.println(doubleStyle.getDataFormatString());
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
