/**
 * 
 */
package com.ims.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ims.report.config.Column;
import com.ims.report.config.ExcelConfig;
import com.ims.report.config.Sheet;
import com.ims.util.DBUtil;
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

	private int footerRowNum;
	private int dataRowNum;
	private int startRow;
	private String sql;
	private List<Column> cols;
	
	private String sheetName;
	private final int SHEET_INDEX = 5;
	
	/**
	 * 
	 * @param wb
	 */
	public R1Sheet(HSSFWorkbook wb){
		this.wb = wb;
		this.sheet = wb.getSheetAt(SHEET_INDEX);
		this.sheetName=this.sheet.getSheetName();
	}
	
	/**
	 * 加载配置属性
	 */
	private void loadConfig(){
		Sheet sheetConfig = ExcelConfig.getSheet(sheetName);
		this.footerRowNum = sheetConfig.getFooterRowNum()-1;
		this.dataRowNum = sheetConfig.getDataRowNum()-1;
		this.startRow = sheetConfig.getDataStartNum()-1;
		this.sql = sheetConfig.getSql();
		this.cols = sheetConfig.getCols();
	}
	
	/**
	 * 创建sheet
	 */
	public  void createSheet(){
		loadConfig();
		//第10行是模板尾行，poi无插入，所以复制此行，最后添加到最后
		this.templateFooterRow = sheet.getRow(footerRowNum);
		//模板中的数据行，获取格式
		this.templateDataRow = sheet.getRow(dataRowNum);
		this.dataCellStyle = this.templateDataRow.getCell(1).getCellStyle();
		//设置表头数据
		setHeader();
		//设置数据
		setData();
	}
	

	@Override
	public String getSheetName() {
		return this.sheet.getSheetName();
	}
	
	/**
	 * 设置日期汇率
	 */
	private void setHeader(){
		HSSFRow row = sheet.getRow(5);
		HSSFCell cell = row.getCell(3);
		if(cell == null)
			cell = row.createCell(3);
		cell.setCellValue("26-Aug-14");
		

		HSSFCell cell2 = row.getCell(16);
		if(cell2 == null)
			cell2 = row.createCell(16);
		cell2.setCellValue("25-Sep-14");
	}
	
	/**
	 * 设置尾行
	 */
	private void setFooter(int rowIndex){
		HSSFRow row = sheet.createRow(rowIndex);
		ExcelUtil.copyRow(this.templateFooterRow, row);
	}
	
	/**
	 * sheet中填数
	 */
	private void setData(){
		List<Object> data = getData();
		int rowCount = data.size();
		
		//得到数据之后先设置尾行,否则模板行会被覆盖
		setFooter(rowCount+this.startRow);
		
		//创建数据区域
		createDataRegion(rowCount);
		
		Map<Object, Object> rowData = null;
		for(int dataIndex=0,rowIndex=startRow;dataIndex<rowCount;dataIndex++,rowIndex++){
			HSSFRow row = sheet.getRow(rowIndex);
			rowData = (Map)data.get(dataIndex);
			if(row == null)
				row = sheet.createRow(rowIndex);
			setRowData(row, rowData);
		}
	}
	
	/**
	 * 
	 * @param dataRowCount
	 */
	private void createDataRegion(int dataRowCount){
		for(int i = 1;i< dataRowCount;i++){
			//数据模板行的下一行开始创建数据区
			HSSFRow row = sheet.createRow(this.startRow + i);
			ExcelUtil.copyRow(this.templateDataRow, row);
		}
	}
	
	/**
	 * 行级写数
	 * @param row
	 * @param rowData
	 */
	private void setRowData(HSSFRow row,Map<Object, Object> rowData){
		for(int j=0;j<this.cols.size();j++){
			Column col = this.cols.get(j);
			//模板中配置的列
			int index = Integer.valueOf(col.getIndex());
			index--;//转换为下表从0开始
			HSSFCell cell = row.getCell(index);
			if(cell == null)
				cell = row.createCell(index);
			String valueName = col.getValue().toString();
			cell.setCellValue(rowData.get(valueName).toString());
			cell.setCellStyle(this.dataCellStyle);
		}
	}
	
//	/**
//	 * 行级写数
//	 * @param row
//	 * @param rowData
//	 */
//	private void setRowData(HSSFRow row,Object[] rowData){
//		int colCount = row.getLastCellNum();
//		Object cellValue = null;
//		for(int j=0;j<colCount;j++){
//			if(j == rowData.length)
//				break;
//			HSSFCell cell = row.getCell(j);
//			cellValue = rowData[j];
//			if(cell == null)
//				cell = row.createCell(j);
//			
//			cell.setCellValue(cellValue.toString());
//			cell.setCellStyle(this.dataCellStyle);
//		}
//	}
	

	/**
	 * 得到数据对象
	 */
	private List<Object> queryData(){
		List<Object> data = new ArrayList<Object>();
		data = DBUtil.query(this.sql);
		return data;
	}
	
	/**
	 * 格式化数据
	 * @return
	 */
	private List<Object> getData(){
		List<Object> data = queryData();
		Map<String,String> mNoList = new HashMap<String,String>();
		String mNo = "";
		Map<Object, Object> rowMap = null;
		for(int i=0;i<data.size();i++){
			rowMap = (Map)data.get(i);
			if(rowMap.get("machineno") == null)
				break;
			mNo = rowMap.get("machineno").toString();
			//单独显示机器行，机器名称显示在试剂名称列
			if(!mNoList.containsKey(mNo)){
				Map<String, String> mRow = new HashMap<String, String>();
				mRow.put("machineno", mNo);
				mRow.put("catname", rowMap.get("machineName").toString());
				//机器行添加到数据集中
				data.add(i, mRow);
				i++;
			}
			else{
				//非机器行不显示机器
				rowMap.put("machineno", "");
			}
		}
		
		
		return data;
	}
	
}
