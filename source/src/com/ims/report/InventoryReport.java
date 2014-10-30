/**
 * 
 */
package com.ims.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ims.common.SysConst;
import com.ims.dao.FileDao;
import com.ims.model.FileReport;
import com.ims.report.config.ExcelConfig;
import com.ims.report.config.Sheet;
import com.ims.report.excel.ISheet;
import com.ims.util.DateTimeUtil;
import com.ims.util.Sys;
import com.ims.util.SysVar;


/**
 * @author ChengNing
 * @date   2014-9-7
 */
public class InventoryReport implements Runnable{
	
	public static final String TEMPLATE_PATH = "conf/template/template.xls";
	private static Logger logger = Logger.getLogger(InventoryReport.class);
	private Date startDate;
	private Date endDate;
	private HSSFWorkbook wb;
	private FileReport reportFile;
	private boolean backupData = false;


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
			FileInputStream fileInputStream = new FileInputStream(InventoryReport.getTemplateFile());
			this.wb = new HSSFWorkbook(fileInputStream);
			this.createExcel();
			
			save();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.wb = null;//释放
	}
	
	/**
	 * 保存报表成果物
	 */
	private void save(){
		try {
			this.reportFile = new FileReport();
			String filePath = getFilePath();
		    FileOutputStream fStream = new FileOutputStream(filePath);
		    this.wb.write(fStream);
		    fStream.flush();
			fStream.close();
			logger.info("报表文件保存成功" + filePath);
			FileDao fileDao = new FileDao();
			int re = fileDao.saveOrUpdate(this.reportFile);
			if(re > 0){
				logger.info("文件保存在数据库成功");
			}else{
				logger.info("文件保存在数据库失败");
			}
		} catch (Exception e) {
			logger.info("报表文件保存异常");
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建Excel
	 */
	private void createExcel(){
		logger.info("创建Excel报表文档");
		List<Sheet> sheetList = ExcelConfig.getAllSheet();
		HSSFSheet excelSheet = null;
		for (Sheet config : sheetList) {
			try {
				logger.info("创建Excel的sheet，Sheet名称：" + config.getName());
				excelSheet = this.wb.getSheet(config.getName());
				if(excelSheet == null){
					logger.error("模板中取不到对应的sheet，名称不对");
				}
				Class clazz = Class.forName(config.getClassName());
				Constructor constructor = clazz.getConstructor(HSSFSheet.class,Sheet.class,Date.class,Date.class);
				ISheet sheetObj = (ISheet)constructor.newInstance(excelSheet,config,this.startDate,this.endDate);
				
				//String StrStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.startDate);
				//String StrEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.endDate);
				
				//ISheet sheetObj = (ISheet)constructor.newInstance(excelSheet,config,StrStart,StrEnd);
				sheetObj.createSheet();
			} catch (Exception e) {
				logger.error("创建Excel的sheet失败，Sheet名称：" + config.getName() + "处理类" + config.getClassName());
				e.printStackTrace();
				
			}
		}
	}
	
	/**
	 * 获取文件存储路径
	 * @return
	 */
	private String getFilePath(){
		String serverRootPath = Sys.serverRootPath();
		String reportPath = SysVar.getValue(SysConst.Var.REPORT_PATH);
		reportPath = DateTimeUtil.format(reportPath, this.endDate);
		this.reportFile.setPath(reportPath);
		reportPath = serverRootPath + reportPath;
		File rFile= new File(reportPath);
		rFile = rFile.isFile()?rFile.getParentFile():rFile;
		if(!rFile.exists()){
			if(!rFile.mkdir())
				logger.error("创建报表目录失败" + rFile.getPath());
		}
		String fileName = SysVar.getValue(SysConst.Var.REPORT_NAME);
		fileName = DateTimeUtil.format(fileName, this.endDate);
		this.reportFile.setName(fileName);
		return  reportPath + fileName;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getTemplateFile(){
		return Sys.serverRootPath() + TEMPLATE_PATH;
	}
	
	

	public void setBackupData(boolean backupData) {
		this.backupData = backupData;
	}
	
}
