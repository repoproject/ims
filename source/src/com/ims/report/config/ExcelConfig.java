/**
 * 
 */
package com.ims.report.config;

import java.util.List;

import com.ims.util.Sys;



/**
 * excel
 * @author ChengNing
 * @date   2014-9-6
 */
public class ExcelConfig {
	
	public static String PATH = "reportconfig/excel.xml";
	
	/**
	 * 
	 * @param sheetName
	 */
	public ExcelConfig(){
	}
	
	/**
	 * 加载数据
	 * @return
	 */
	public static Sheet getSheet(String sheetName){
		return SheetConfigParser.getInstance().getSheet(sheetName);
	}
	
	/**
	 * 获得配置文件中所有的sheet配置
	 * @return
	 */
	public static List<Sheet> getAllSheet(){
		return SheetConfigParser.getInstance().getSheetList();
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getPath(){
		return Sys.classRootPath() + PATH;
	}
}
