/**
 * 
 */
package com.ims.task;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ims.common.TaskData;
import com.ims.report.InventoryReport;
import com.ims.util.DateTimeUtil;
import com.ims.util.SysVar;

/**
 * 月度报表业务
 * @author ChengNing
 * @date   2014-9-3
 */
public class MonthReport {
	private static Logger logger = Logger.getLogger(MonthReport.class);

	public MonthReport() {
		
	}
	
	/**
	 * 报表操作
	 */
	public void report(){
		logger.info("开始生成报表");

	    Date startDate=getStartDate();
		Date endDate = new Date();
			
		InventoryReport reportor = new InventoryReport(startDate,endDate);
		reportor.run();

		logger.info("生成报表成功");
	}
	
	/**
	 * 月报统计开始时间
	 * @return
	 */
	private Date getStartDate(){
		TaskData taskData = new TaskData();
		return taskData.lastTaskDate();
	}
	

}
