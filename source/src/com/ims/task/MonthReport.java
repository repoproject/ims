/**
 * 
 */
package com.ims.task;

import org.apache.log4j.Logger;

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
		createReport();
		saveReport();

		logger.info("生成报表成功");
	}
	
	/**
	 * 创建报表文件
	 */
	private void createReport(){
		logger.info("开始创建报表文件");
		
		logger.info("创建报表文件完成");
	}
	
	/**
	 * 保存报表
	 */
	private void saveReport(){
		String path = getReportFilePath();
		
		logger.info("保存报表成功");
	}
	
	/**
	 * 
	 * @return
	 */
	private String getReportFilePath(){
		String path = SysVar.getValue("reportpath");
		logger.info("系统配置报表保存路径：" + path);
		return path;
	}
}
