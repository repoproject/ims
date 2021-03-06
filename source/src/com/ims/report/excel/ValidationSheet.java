/**
 * 
 */
package com.ims.report.excel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.ims.dao.RateDao;
import com.ims.model.Rate;
import com.ims.model.sysEnum.MoneyType;
import com.ims.report.SaveReportData;
import com.ims.report.config.Sheet;
import com.ims.util.DBUtil;
import com.ims.util.ExcelUtil;

/**
 * @author ChengNing
 * @date   2014-9-6
 */
public class ValidationSheet extends AbsRSheet implements ISheet{
	
	private static Logger logger = Logger.getLogger(ValidationSheet.class);

	/**
	 * 
	 * @param sheet
	 * @param config
	 * @param startDate
	 */
	public ValidationSheet(HSSFSheet sheet, Sheet config, Date startDate) {
		super(sheet, config, startDate);
	}
	
	/**
	 * 指定开始时间和结束时间
	 * @param sheet
	 */
	public ValidationSheet(HSSFSheet sheet,Sheet config,Date startDate,Date endDate){
		super(sheet, config, startDate,endDate);
	}

	/**
	 * 
	 */
	@Override
	public void setHeader() {
		logger.info("设置表头数据");
		int rowIndex=0;
		int colIndex=1;
		HSSFRow row = this.sheet.getRow(rowIndex);
		HSSFCell cell = row.getCell(colIndex);
		if(cell == null)
			cell = row.createCell(colIndex);
		ExcelUtil.setCellValue(cell, this.endDate);
		//设置汇率
		colIndex = 8;
		HSSFCell rateCell = row.getCell(colIndex);
		RateDao rateDao = new RateDao();
		Rate rate = rateDao.getCurrentRate();
		ExcelUtil.setCellValue(rateCell, rate.getValue(MoneyType.USD));
		
	}
	
	/**
	 * 得到数据对象
	 */
	@Override
	protected List<Object> queryData(){
		List<Object> data = new ArrayList<Object>();
		data = DBUtil.query(this.sql,this.startDate,this.endDate);
		return data;
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
		SaveReportData save = new SaveReportData(SaveReportData.ReportType.Validation,data);
		Thread saveThread = new Thread(save, "saveReportData");
		saveThread.start();
	}

}
