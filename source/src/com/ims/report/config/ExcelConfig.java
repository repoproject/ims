/**
 * 
 */
package com.ims.report.config;



/**
 * excel
 * @author ChengNing
 * @date   2014-9-6
 */
public class ExcelConfig {
	
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
}
