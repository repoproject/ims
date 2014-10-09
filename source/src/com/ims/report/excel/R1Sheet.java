/**
 * 
 */
package com.ims.report.excel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @date   2014年9月3日
 */
public class R1Sheet extends AbsRSheet implements ISheet{
	private static Logger logger = Logger.getLogger(R1Sheet.class);
	

	/**
	 * 
	 * @param sheet
	 * @param config
	 * @param startDate
	 */
	public R1Sheet(HSSFSheet sheet, Sheet config, Date startDate) {
		super(sheet, config, startDate);
	}
	
	/**
	 * 指定开始时间和结束时间
	 * @param sheet
	 */
	public R1Sheet(HSSFSheet sheet,Sheet config,Date startDate,Date endDate){
		super(sheet, config, startDate,endDate);
	}

	/**
	 * 
	 */
	@Override
	public void setHeader() {
		logger.info("设置表头数据");
		HSSFRow row = this.sheet.getRow(5);
		HSSFCell cell = row.getCell(3);
		if(cell == null)
			cell = row.createCell(3);
		ExcelUtil.setCellValue(cell, this.startDate);
		

		HSSFCell cell2 = row.getCell(16);
		if(cell2 == null)
			cell2 = row.createCell(16);
		ExcelUtil.setCellValue(cell2, this.endDate);
		

		HSSFRow row1 = this.sheet.getRow(2);
		HSSFCell cell3 = row1.getCell(32);
		if(cell3  == null)
			cell3  = row1.createCell(32);
		ExcelUtil.setCellValue(cell3 , this.startDate);
		
		HSSFCell cell4 = row1.getCell(35);
		if(cell4 == null)
			cell4 = row1.createCell(35);
		ExcelUtil.setCellValue(cell4, this.endDate);
		
		setRate();
	}
	
	/**
	 * 设置表头汇率数据
	 */
	private void setRate(){
		RateDao rateDao = new RateDao();
		Rate rate = rateDao.getCurrentRate();
		
		HSSFRow rateRow = this.sheet.getRow(6);
		HSSFCell cellCNY = rateRow.getCell(17);
		if(cellCNY == null)
			cellCNY = rateRow.createCell(17);
		ExcelUtil.setCellValue(cellCNY, rate.getValue(MoneyType.CNY));
		
		HSSFCell cellUSD = rateRow.getCell(18);
		if(cellUSD == null)
			cellUSD = rateRow.createCell(18);
		ExcelUtil.setCellValue(cellUSD, rate.getValue(MoneyType.USD));
		
		HSSFCell cellSGD = rateRow.getCell(19);
		if(cellSGD == null)
			cellSGD = rateRow.createCell(19);
		ExcelUtil.setCellValue(cellSGD, rate.getValue(MoneyType.SGD));
		
		HSSFCell cellEUR = rateRow.getCell(20);
		if(cellEUR == null)
			cellEUR = rateRow.createCell(20);
		ExcelUtil.setCellValue(cellEUR, rate.getValue(MoneyType.EUR));
		
		HSSFCell cellGBP = rateRow.getCell(21);
		if(cellGBP == null)
			cellGBP = rateRow.createCell(21);
		ExcelUtil.setCellValue(cellGBP, rate.getValue(MoneyType.GBP));
	}
	
	/**
	 * 得到数据对象
	 */
	protected List<Object> queryData(){
		List<Object> data = new ArrayList<Object>();
		data = DBUtil.query(this.sql,this.startDate,this.endDate,this.startDate,this.endDate);
		return data;
	}

	/**
	 * 
	 */
	@Override
	protected List<Object> formatData(List<Object> data) {
		Map<String,String> mNoList = new HashMap<String,String>();
		//机器序号从1开始 by gq 2014-9-29
		int mechineSeq = 1;
		String mName = "";
		Map<Object, Object> rowMap = null;
		for(int i=0;i<data.size();i++){
			rowMap = (Map)data.get(i);
			if(rowMap.get("machineName") == null)
				continue;
			mName = rowMap.get("machineName").toString();
			//单独显示机器行，机器名称显示在试剂名称列
			if(!mNoList.containsKey(mName)){
				Map<String, String> mRow = new HashMap<String, String>();
				mRow.put("machineNo", String.valueOf(mechineSeq++));
				mRow.put("catname", rowMap.get("machineName").toString());
				//机器行添加到数据集中
				data.add(i, mRow);
				i++;
				mNoList.put(mName, "");
			}
			//非机器行不显示机器
			rowMap.put("machineNo", "");
		}
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
