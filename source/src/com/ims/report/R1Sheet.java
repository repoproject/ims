/**
 * 
 */
package com.ims.report;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ims.util.ExcelUtil;

/**
 * @author ChengNing
 * @date   2014年9月3日
 */
public class R1Sheet implements RSheet{
	private HSSFWorkbook wb;

	private final int footerRowNum = 10;
	private final int dataRowNum = 9;
	
	public R1Sheet(HSSFWorkbook wb){
		this.wb = wb;
	}
	
	/**
	 * 
	 */
	public  void createSheet(){
		HSSFSheet sheet = wb.getSheetAt(5);
		int startRow = 7; //第8行开始
		//第10行是模板尾行，poi无插入，所以复制此行，最后添加到最后
		HSSFRow rowFooter = sheet.getRow(footerRowNum);
		HSSFRow dataRow = sheet.getRow(dataRowNum);
		HSSFCellStyle dataCellStype = dataRow.getCell(0).getCellStyle();
		//如下用于标号
		for(int i=startRow;i<12;i++){
			HSSFRow row = sheet.getRow(i);
			if(row == null)
				row = sheet.createRow(i);
			for(int j=0;j<50;j++){
				HSSFCell cell = row.getCell(j);//row.createCell(j);
				if(cell == null)
					cell = row.createCell(j);
				cell.setCellValue(String.valueOf(i) + "," + String.valueOf(j));
				cell.setCellStyle(dataCellStype);
			}
		}
		
		HSSFRow row = sheet.createRow(20);
		ExcelUtil.copyRow(rowFooter, row);
	}
	
	public void getData(){
		
	}

	@Override
	public String getSheetName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setData() {
		// TODO Auto-generated method stub
		
	}
	
}
