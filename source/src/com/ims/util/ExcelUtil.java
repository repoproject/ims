/**
 * 
 */
package com.ims.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
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
	
	/**
	 * 在指定的位置插入给定的行
	 * @param row   需要插入的行对象，数据和样式
	 * @param rowNum 插入的行数，插入前的行后移
	 */
    public static void insertRow(HSSFSheet sheet,HSSFRow row,int rowNum){
    	//int moveCount=sheet.getLastRowNum()-
    	sheet.shiftRows(rowNum, sheet.getLastRowNum(), 1);
    	HSSFRow insertRow = sheet.getRow(rowNum);
    	if(insertRow == null)
    		insertRow = sheet.createRow(rowNum);
    	copyRow(row, insertRow);
    }
    
    /**
     * 给cell设置值，针对对应的对象设置对应的值
     * @param cell
     * @param value
     */
    public static void setCellValue(HSSFCell cell,Object value){
    	if(value == null)
    		return;
    	else if(value instanceof Integer){
    		cell.setCellValue((Integer)value);
    	}
    	else if(value instanceof Date){
    		cell.setCellValue((Date)value);
    	}
    	else if(value instanceof Calendar){
    		cell.setCellValue((Calendar)value);
    	}
    	else if(value instanceof Double){
    		cell.setCellValue((Double)value);
    	}
    	else{
    		cell.setCellValue(value.toString());
    	}
    }
   
}
