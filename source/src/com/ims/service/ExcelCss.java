/**
 * 
 */
package com.ims.service;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @author ChengNing
 * @date   2014年8月14日
 */
public class ExcelCss {
	private HSSFWorkbook wb; 
	
	public ExcelCss(HSSFWorkbook wb) {
		this.wb = wb;
	}
	public HSSFFont fontHeader(){
		HSSFFont font = this.wb.createFont();
		font.setFontName("黑体");
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		return font;
	}
	
	public HSSFCellStyle headerTyle(){
		HSSFCellStyle style  = this.wb.createCellStyle();
		style.setFont(fontHeader());
		return style;
	}
}
