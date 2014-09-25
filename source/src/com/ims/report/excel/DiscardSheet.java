/**
 * 
 */
package com.ims.report.excel;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.ims.report.SaveReportData;
import com.ims.report.config.Sheet;
import com.ims.util.ExcelUtil;

/**
 * 如果CP和AP的不一样，可以分别继承本类
 * @author ChengNing
 * @date   2014-9-6
 */
public class DiscardSheet extends AbsRSheet implements ISheet {
	
	private static Logger logger = Logger.getLogger(DiscardSheet.class);

	/**
	 * 
	 * @param sheet
	 * @param config
	 * @param startDate
	 */
	public DiscardSheet(HSSFSheet sheet, Sheet config, Date startDate) {
		super(sheet, config, startDate);
	}
	
	/**
	 * 指定开始时间和结束时间
	 * @param sheet
	 */
	public DiscardSheet(HSSFSheet sheet,Sheet config,Date startDate,Date endDate){
		super(sheet, config, startDate,endDate);
	}

	/**
	 * 
	 */
	@Override
	public void setHeader() {
		logger.info("设置表头数据");
		int rowIndex=0;
		int colIndex=3;
		HSSFRow row = this.sheet.getRow(rowIndex);
		HSSFCell cell = row.getCell(colIndex);
		if(cell == null)
			cell = row.createCell(colIndex);
		ExcelUtil.setCellValue(cell, this.endDate);
	}

	/**
	 * 
	 */
	@Override
	protected List<Object> formatData(List<Object> data) {
		return data;
	}
	
	/**
	 * 单开线程保存备份报表数据
	 */
	@Override
	protected void saveData(List<Object> data) {
		SaveReportData save = new SaveReportData(SaveReportData.ReportType.R,data);
		Thread saveThread = new Thread(save, "saveReportData");
		saveThread.start();
	}

}
