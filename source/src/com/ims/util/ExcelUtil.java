/**
 * 
 */
package com.ims.util;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Excel 操作工具
 * @author ChengNing
 * @date   2014-9-5
 */
public class ExcelUtil {
	private static Logger logger = Logger.getLogger(ExcelUtil.class);

	/**
	 * copy row
	 * @param source
	 * @param target
	 * @param cellCount
	 */
	public static void copyRow(HSSFRow source,HSSFRow target){
		if(target == null)
			return;
		target.setHeight(source.getHeight());
		int cellCount = source.getLastCellNum();
		for(int i=0;i<cellCount;i++){
			HSSFCell sourceCell = source.getCell(i);
			HSSFCell targetCell = target.createCell(i);
			if(sourceCell != null && targetCell != null){
				copyCell(sourceCell, targetCell);
			}
		}
	}
	
	/**
	 * copy cell
	 * @param source
	 * @param target
	 */
	public static void copyCell(HSSFCell source,HSSFCell target){
		if(target == null)
			return;

        if (source.getCellComment() != null) {  
        	target.setCellComment(source.getCellComment());  
        }  
        target.setCellStyle(source.getCellStyle());
		target.setCellType(source.getCellType());
		int cellType = source.getCellType();
		target.setCellType(cellType);
		switch (cellType) {
		case Cell.CELL_TYPE_STRING:
			target.setCellValue(source.getStringCellValue());
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			target.setCellValue(source.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
            if (HSSFDateUtil.isCellDateFormatted(source)) {  
            	target.setCellValue(source.getDateCellValue());  
            } else {  
            	target.setCellValue(source.getNumericCellValue());  
            }  
			target.setCellValue(source.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_ERROR:
			target.setCellErrorValue(source.getErrorCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			//do nothing
			break;
		case Cell.CELL_TYPE_FORMULA:
			target.setCellFormula(source.getCellFormula());
			break;
		default:
			logger.error("类型为" + source.getCellType() + "无法复制");
			break;
		}
	}
}
