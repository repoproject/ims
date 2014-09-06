/**
 * 
 */
package com.ims.report;

import java.util.ArrayList;
import java.util.List;

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
	private HSSFCellStyle dataCellStyle;
	private HSSFSheet sheet;
	private HSSFRow templateFooterRow;
	private HSSFRow templateDataRow;

	private final int footerRowNum = 10;
	private final int dataRowNum = 9;
	private final int startRow = 7;
	
	public R1Sheet(HSSFWorkbook wb){
		this.wb = wb;
	}
	
	/**
	 * 创建sheet
	 */
	public  void createSheet(){
		this.sheet = wb.getSheetAt(5);
		//第10行是模板尾行，poi无插入，所以复制此行，最后添加到最后
		this.templateFooterRow = sheet.getRow(footerRowNum);
		//模板中的数据行，获取格式
		this.templateDataRow = sheet.getRow(dataRowNum);
		this.dataCellStyle = this.templateDataRow.getCell(0).getCellStyle();
		
		//设置数据
		setData();
		//设置尾行
		setFooter();
	}
	

	@Override
	public String getSheetName() {
		return this.sheet.getSheetName();
	}
	
	/**
	 * 设置尾行
	 */
	private void setFooter(){
		HSSFRow row = sheet.createRow(20);
		ExcelUtil.copyRow(this.templateFooterRow, row);
	}
	
	/**
	 * sheet中填数
	 */
	private void setData(){
		List<Object[]> data = new ArrayList<Object[]>();
		int rowCount = data.size();
		int rowIndex = 0;
		int colCount = 50;
		//如下用于标号
		for(int i=0;i<rowCount;i++){
			rowIndex = i+startRow;
			HSSFRow row = sheet.getRow(rowIndex);
			if(row == null)
				row = sheet.createRow(rowIndex);
			for(int j=0;j<colCount;j++){
				HSSFCell cell = row.getCell(j);//row.createCell(j);
				if(cell == null)
					cell = row.createCell(j);
				cell.setCellValue(String.valueOf(i) + "," + String.valueOf(j));
				cell.setCellStyle(this.dataCellStyle);
			}
		}
	}
	

	/**
	 * 
	 */
	public void getData(){
		
	}
	
}
