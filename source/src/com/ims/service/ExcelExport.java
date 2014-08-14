/**
 * 
 */
package com.ims.service;


import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @author ChengNing
 * @date   2014年8月13日
 */
public class ExcelExport  extends HttpServlet{
	
	private HSSFWorkbook wb;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("excelExport");
		//

		createExcel();
		
		//
		response.setContentType("application/vnd.ms-excel");	
		ServletOutputStream out = response.getOutputStream();
		wb.write(out);
		out.flush();
		out.close();
	}
	/**
	 * ref http://www.cnblogs.com/marsmile/p/3669513.html
	 */
	private void createExcel(){
		wb = new HSSFWorkbook();
		
		createSheet("R1 - CHEM");
		createSheet("R2 - LCMS & Micro ");
		createSheet("R3-Hema & Flow");
		createSheet("R4-Variant 2");
		createSheet("R5 - Oversea");
		createSheet("R6 - PCR");
		createSheet("R7 - Urine");
		createSheet("R8 - Cent& Immu&Liai&Mini");
		createSheet("R9 - Elisa");
		createSheet("Lab Supplier");
		createSheet("Discard_CLab");
		createSheet("Validation_CLab");
		createSheet("R10-Oncology");
		createSheet("Discard_APLab");
		createSheet("Validation_APLab");
	}
	
	/**
	 * 
	 * @param sheetName
	 */
	private void createSheet(String sheetName){
		HSSFSheet sheet = wb.createSheet(sheetName);
		createHeader(sheet);

	}
	
	private void createHeader(HSSFSheet sheet){
		
		ExcelHeader header = new ExcelHeader(sheet);
		header.createHeader();
	
	}

	
	public static void main(String [] arc){
		System.out.println("test");
		ExcelExport testExcelExport = new ExcelExport();
		testExcelExport.createExcel();
		try {
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
