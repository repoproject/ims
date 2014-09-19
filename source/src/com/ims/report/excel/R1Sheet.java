/**
 * 
 */
package com.ims.report.excel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.ims.report.config.Sheet;
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
	}

	/**
	 * 
	 */
	@Override
	protected List<Object> formatData(List<Object> data) {
		Map<String,String> mNoList = new HashMap<String,String>();
		String mNo = "";
		Map<Object, Object> rowMap = null;
		for(int i=0;i<data.size();i++){
			rowMap = (Map)data.get(i);
			if(rowMap.get("machineNo") == null)
				continue;
			mNo = rowMap.get("machineNo").toString();
			//单独显示机器行，机器名称显示在试剂名称列
			if(!mNoList.containsKey(mNo)){
				Map<String, String> mRow = new HashMap<String, String>();
				mRow.put("machineNo", mNo);
				mRow.put("catname", rowMap.get("machineName").toString());
				//机器行添加到数据集中
				data.add(i, mRow);
				i++;
				mNoList.put(mNo, "");
			}
			//非机器行不显示机器
			rowMap.put("machineNo", "");
		}
		return data;
	}
	
}
