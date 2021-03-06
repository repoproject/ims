/**
 * 
 */
package com.ims.report.excel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.ims.report.config.Column;
import com.ims.report.config.Sheet;
import com.ims.util.DBUtil;
import com.ims.util.ExcelUtil;

/**
 * Rsheet统计抽象类
 * Abstract Template 模式
 * @author ChengNing
 * @date   2014年9月19日
 */
public abstract class AbsRSheet implements ISheet {
	
	private static Logger logger = Logger.getLogger(AbsRSheet.class);
	
	protected HSSFSheet sheet;
	protected HSSFRow templateDataRow;
	protected String sheetName;
	protected Sheet sheetConfig;

	protected int footerRowNum;
	protected int dataRowNum;
	protected int startRow;
	protected String sql;
	protected List<Column> cols;

	protected Map<Object, Object> footerData = null;
	private int insertRowCount = 0;  //模板中还需要插入的行数
	
	protected Date startDate;
	protected Date endDate;
	protected boolean isBackupData = false;
	
	

	/**
	 * 指定开始时间到当期时间
	 * @param sheet
	 * @param startDate
	 * @param endDate
	 */
	protected AbsRSheet(HSSFSheet sheet,Sheet config,Date startDate){
		this.sheet = sheet;
		this.sheetConfig = config;
		this.sheetName=sheet.getSheetName();
		this.startDate = startDate;
		this.endDate = new Date();
	}
	
	/**
	 * 指定开始时间和结束时间
	 * @param sheet
	 */
	protected AbsRSheet(HSSFSheet sheet,Sheet config,Date startDate,Date endDate){
		this.sheet = sheet;
		this.sheetConfig = config;
		this.sheetName=sheet.getSheetName();
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	/**
	 * 加载配置属性
	 */
	public void loadConfig(){
		logger.info("加载配置属性");
		this.footerRowNum = sheetConfig.getFooterRowNum()-1;
		this.dataRowNum = sheetConfig.getDataRowNum()-1;
		this.startRow = sheetConfig.getDataStartNum()-1;
		this.sql = sheetConfig.getSql();
		this.cols = sheetConfig.getCols();
	}
	
	/**
	 * 是否备份数据
	 */
	public void isBackupData(boolean isBackup) {
		this.isBackupData = isBackup;
	}
	
	/**
	 * 创建sheet
	 */
	public void createSheet(){
		loadConfig();
		//第10行是模板尾行，poi无插入，所以复制此行，最后添加到最后
		//模板中的数据行，获取格式
		this.templateDataRow = sheet.getRow(dataRowNum);
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
	 * 创建数据区，设置数据行，尾行
	 */
	protected void createDataRegion(int rowCount){
		int moveCount = rowCount-(this.footerRowNum-this.startRow);
		if(moveCount <= 0)
			return;
		this.insertRowCount = moveCount;//设置需要插入的行
		//移动尾行开始的行及其后所有的行
		ExcelUtil.moveRow(this.sheet, this.footerRowNum, this.insertRowCount,this.dataRowNum);
	}
	
	/**
	 * sheet中填数
	 */
	protected void setData(){
		logger.info("设置数据");
		List<Object> data = getData();
		int rowCount = data.size();
		logger.info("得到数据总数===" + rowCount);
		if(rowCount == 0){
			return ;
		}
		//得到footer数据
		getFooterData(data);
		//得到数据之后先设置尾行,否则模板行会被覆盖
		createDataRegion(rowCount);
		//创建数据区域
		fillData(rowCount);
		Map<Object, Object> rowData = null;
		for(int dataIndex=0,rowIndex=startRow;dataIndex<rowCount;dataIndex++,rowIndex++){
			HSSFRow row = sheet.getRow(rowIndex);
			rowData = (Map)data.get(dataIndex);
			if(row == null)
				row = sheet.createRow(rowIndex);
			setRowData(row, rowData);
		}
		//设置最后一行数据
		int newFooterRowNum = (this.footerRowNum >= this.startRow + rowCount ? this.footerRowNum : this.startRow + rowCount);
		setFooterData(this.sheet.getRow(newFooterRowNum));
	}
	
	/**
	 * 记录尾行的数据
	 * @param data
	 */
	protected void getFooterData(List<Object> data){
		if(data.size() > 0){
			footerData = new HashMap<Object, Object>();
			Map map = (Map)data.get(0);
			Iterator i = map.keySet().iterator();
			while(i.hasNext()){
				footerData.put(i.next(), null);
			}
		}
	}
	
	/**
	 * 填充尾行数据
	 * @param footerRow
	 */
	protected void setFooterData(HSSFRow footerRow){
		for(int j=0;j<this.cols.size();j++){
			Column col = this.cols.get(j);
			if(!col.getSum()){
				continue;
			}
			//模板中配置的列
			int index = Integer.valueOf(col.getIndex());
			index--;//转换为下表从0开始
			HSSFCell cell = footerRow.getCell(index);
			if(cell == null)
				cell = footerRow.createCell(index);
			String valueName = col.getValue().toString();
			Object value = this.footerData.get(valueName);
			if(value!=null){
			    ExcelUtil.setCellValue(cell, value);
			}
		}
	}
	
	/**
	 * 填充数据
	 * @param dataRowCount
	 */
	protected void fillData(int dataRowCount){
		int rowIndex=0;
		for(int i = 1;i< dataRowCount;i++){
			//数据模板行的下一行开始创建数据区
			rowIndex = this.startRow + i;
			if(rowIndex <= this.dataRowNum){
				continue;//模板行跳过
			}
			HSSFRow row = sheet.createRow(rowIndex);
			ExcelUtil.copyRow(this.templateDataRow, row);
		}
	}
	
	/**
	 * 行级写数
	 * @param row
	 * @param rowData
	 */
	protected void setRowData(HSSFRow row,Map<Object, Object> rowData){
		for(int j=0;j<this.cols.size();j++){
			Column col = this.cols.get(j);
			//模板中配置的列
			int index = Integer.valueOf(col.getIndex());
			index--;//转换为下表从0开始
			HSSFCell cell = row.getCell(index);
			if(cell == null)
				cell = row.createCell(index);
			String valueName = col.getValue().toString();
			Object value = rowData.get(valueName);
			ExcelUtil.setCellValue(cell, value);
			if(col.getSum()){
				calFooterData(valueName, value);
			}
		}
	}
	
	/**
	 * 合计行求值
	 * @param key
	 * @param value
	 */
	protected void calFooterData(String key,Object value){
		if(value == null)
			return;
		try{
			Object objValue = this.footerData.get(key);
			double sum = 0.0;
			if(value instanceof Integer){
				if(value == null) value = 0;
				if(objValue == null) objValue = 0;
				this.footerData.put(key, (Integer)value + (Integer)objValue);
			}
			else if(value instanceof Long){
				if(value == null) value = new Long(0);
				if(objValue == null) objValue = new Long(0);;
				this.footerData.put(key, (Long)value + (Long)objValue);
			}
			else if(value instanceof Double){
				if(value == null) value = 0.0;
				if(objValue == null) objValue = 0.0;
				this.footerData.put(key, (Double)value + (Double)objValue);
			}
			else if(value instanceof BigDecimal){
				if(value == null) value = new BigDecimal(0);
				if(objValue == null) objValue = new BigDecimal(0);
				this.footerData.put(key, ((BigDecimal)value).add((BigDecimal)objValue));
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage() + "合计出错，列：" + key + " 值  " + value.toString());
		}
	}
	
	/**
	 * 格式化数据
	 * @return
	 */
	protected List<Object> getData(){
		List<Object> data = queryData();
		data = formatData(data);
		if(this.isBackupData){
		    saveData(data);
		}
		return data;
	}

	
	/**
	 * 设置日期汇率
	 */
	protected abstract void setHeader();
	
	/**
	 * 抽象方法子类实现，主要是没个统计sql的参数个数不一致，所以子类自己实现
	 * @param params
	 * @return
	 */
	protected abstract List<Object> queryData();
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	protected abstract List<Object> formatData(List<Object> data);
	
	/**
	 * 数据是否保存由子类决定
	 * 如果实现请使用多线程完成，不影响当前的报表线程
	 * @param data
	 * @return
	 */
	protected abstract void saveData(List<Object> data);

}
