/**
 * 
 */
package com.ims.service;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.itextpdf.text.List;

/**
 * @author ChengNing
 * @date   2014年8月13日
 */
public class ExcelHeader {
	
	private HSSFSheet sheet;
	private int headerRow = 6;
	private int headerCol = 40;
	
	public ExcelHeader(HSSFSheet sheet) {
		this.sheet = sheet;
	}
	
	/**
	 * 
	 */
	public void createHeader() {
		setData();
		setStyle();
	}
	
//	private List<List> getData()
//	{
//		List<List> list = new List<List>();
//		return list;
//	}
	
	/**
	 * 
	 */
	private void setData(){
		HSSFRow r3= sheet.createRow(2);
		HSSFRow r4= sheet.createRow(3);
		HSSFRow r5= sheet.createRow(4);
		HSSFRow r6= sheet.createRow(5);
		//row3
		
		//===row4
		HSSFCell cell =  r4.createCell(3);
		cell.setCellValue("Opening");
		cell = r4.createCell(4);
		cell.setCellValue("QTY IN ");
		cell = r4.createCell(9);
		cell.setCellValue("QTY OUT ");
		
		//====row5
		cell = r5.createCell(0);
		cell.setCellValue("Cat. No.");
		
		cell = r5.createCell(3);
		cell.setCellValue("Stock");
		
		cell = r6.createCell(3);
		cell.setCellValue("26-Mar-13");	
		
		//===4
		cell = r5.createCell(4);
		cell.setCellValue("From Vendor");
		cell = r5.createCell(5);
		cell.setCellValue("From InterLab");
		
		cell = r5.createCell(6);
		cell.setCellValue("From Sponsor");
		cell = r5.createCell(7);
		cell.setCellValue("Other Charges");
		cell = r5.createCell(8);
		cell.setCellValue(" TOTAL");
		cell = r5.createCell(9);
		cell.setCellValue(" Trial Test");
		cell = r5.createCell(10);
		cell.setCellValue(" Validation");
		cell = r5.createCell(11);
		cell.setCellValue(" Discard");
		cell = r5.createCell(12);
		cell.setCellValue(" To Interlab");
		cell = r5.createCell(13);
		cell.setCellValue(" To Site/Sponsor");
		cell = r5.createCell(14);
		cell.setCellValue(" Other Cost");
		cell = r5.createCell(15);
		cell.setCellValue("  TOTAL ");
	}
	
	/**
	 * http://blog.csdn.net/npp616/article/details/8546737
	 */
	private void setStyle(){
		sheet.createFreezePane(3, 7);//设定冻结区域

	}
}
