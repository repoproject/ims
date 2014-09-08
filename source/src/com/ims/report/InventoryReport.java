/**
 * 
 */
package com.ims.report;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ims.common.SysConst;
import com.ims.report.config.ExcelConfig;
import com.ims.report.config.Sheet;
import com.ims.report.excel.ISheet;
import com.ims.util.Sys;
import com.ims.util.SysVar;


/**
 * @author ChengNing
 * @date   2014-9-7
 */
public class InventoryReport implements Runnable{
	private static Logger logger = Logger.getLogger(InventoryReport.class);
	private Date startDate;
	private Date endDate;
	private HSSFWorkbook wb;
	
	public InventoryReport(Date startDate){
		this.startDate = startDate;
		this.endDate = new Date();
	}
	
	public InventoryReport(Date startDate,Date endData){
		this.startDate = startDate;
		this.endDate = endData;
	}

	@Override
	public void run() {
		logger.info("开始运行报表线程......");
		report();
		logger.info("报表线程完成......");
	}
	
	/**
	 * 报表处理
	 */
	public void report(){
		try {
			FileInputStream fileInputStream = new FileInputStream(ExcelExport.class.getResource("").getPath() + "template.xls");
			this.wb = new HSSFWorkbook(fileInputStream);
			this.createExcel();
			String filePath = getFilePath();
		    FileOutputStream fStream = new FileOutputStream(filePath);
		    wb.write(fStream);
		    fStream.flush();
			fStream.close();
			logger.info("报表文件保存成功" + filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.wb = null;//释放
	}
	
	/**
	 * 创建Excel
	 */
	private void createExcel(){
		logger.info("创建Excel报表文档");
		List<Sheet> sheetList = ExcelConfig.getAllSheet();
		for (Sheet sheet : sheetList) {
			try {
				Class clazz = Class.forName(sheet.getClassName());
				Constructor constructor = clazz.getConstructor(String.class);
				ISheet sheetObj = (ISheet) constructor.newInstance(sheet,this.startDate,this.endDate);
				sheetObj.createSheet();
			} catch (Exception e) {
				logger.info("创建Excel的sheet失败，Sheet名称：" + sheet.getName());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 文件存储路径
	 * @return
	 */
	private String getFilePath(){
		String serverRootPath = Sys.serverRootPath();
		String reportPath = SysVar.getValue(SysConst.Var.REPORT_PATH);
		return serverRootPath + reportPath;
	}
}
