/**
 * 
 */
package com.ims.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;
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
		
		copyValue(source, target);
		
		copyFormula(source, target);
		
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyValue(HSSFCell source,HSSFCell target){
		int cellType = source.getCellType();
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
		default:
			logger.error("类型为" + source.getCellType() + "无法复制");
			break;
		}
	}
	
	/**
	 * 复制公式，同excel的复制，目前只支持行
	 * @param source
	 * @param target
	 */
	public static void copyFormula(HSSFCell source,HSSFCell target){
		if(source.getCellType() != Cell.CELL_TYPE_FORMULA)
			return;
		String formula = source.getCellFormula();
		if(StringUtils.isBlank(formula))
			return;
		int sRowIndex = source.getRowIndex();
		int tRowIndex = target.getRowIndex();
		int moveCount = tRowIndex - sRowIndex;
		formula = moveFormula(formula, moveCount);
		
		target.setCellFormula(formula);
	}
	
	/**
	 * 移动行公式
	 * @param formula    移动之前的公式
	 * @param moveRowCount  移动的行数
	 * @return   移动之后的公式
	 */
	public static String moveFormula(String formula,int moveRowCount){
		//=开始的是赋值，直接赋值过去即可
		if(formula.startsWith("=") || moveRowCount == 0){
			return formula;
		}
		List<String> sRowNumList = new ArrayList<String>();
		List<String> tRowNumList = new ArrayList<String>();
		char[] chs = formula.toCharArray();
		boolean flag = false;
		String numStr = "";
		for(char c : chs){
			String s = String.valueOf(c);
			if(StringUtils.isNumeric(s)){
				flag = true;
				numStr = numStr + s;
			}
			else{
				flag = false;
			}
			if(!flag && StringUtils.isBlank(numStr)){
				int rowNum = Integer.parseInt(numStr) + 1;
				String sRowNum = String.valueOf(rowNum);
				String tRowNum = String.valueOf(rowNum + moveRowCount);
				numStr = "";
				if(!sRowNumList.contains(sRowNum)){
					sRowNumList.add(sRowNum);
					tRowNumList.add(tRowNum);
				}
			}
		}
		
		for(int i=0;i<sRowNumList.size();i++){
			String sRowNum = sRowNumList.get(i);
			String tRowNum = tRowNumList.get(i);
			formula.replaceAll(sRowNum, tRowNum);
		}
		return formula;
	}
	
	/**
	 * 
	 * @param formula    计算之前的公式
	 * @param moveRowCount    移动行
	 * @param moveColCount    移动列
	 * @return   计算之后的公式
	 */
	public static String moveFormula(String formula,int moveRowCount,int moveColCount){
		//TODO:no implement
		return formula;
	}
	
	/**
	 * 在指定的位置插入给定的行
	 * @param row   需要插入的行对象，数据和样式
	 * @param rowNum 插入的行数，插入前的行后移
	 */
    public static void insertRow(HSSFSheet sheet,HSSFRow row,int rowIndex){
    	sheet.shiftRows(rowIndex, sheet.getLastRowNum(), 1);
    	HSSFRow insertRow = sheet.getRow(rowIndex);
    	if(insertRow == null)
    		insertRow = sheet.createRow(rowIndex);
    	copyRow(row, insertRow);
    }
    
    
    /**
     * 移动行
     * @param sheet   sheet
     * @param startRowIndex   移动开始行，移动行的行号
     * @param moveCount    移动的距离eg 5 向后移动5行
     */
    public static void moveRow(HSSFSheet sheet,int startRowIndex,int moveCount){
    	sheet.shiftRows(startRowIndex, sheet.getLastRowNum(), moveCount);
    	//移动公式
    	String formula = "";
    	for(int i=startRowIndex;i<sheet.getLastRowNum();i++){
    		HSSFRow row = sheet.getRow(i);
    		for(int j=0;j<row.getLastCellNum();j++){
    			HSSFCell cell = row.getCell(j);
    			if(cell.getCellType()== cell.CELL_TYPE_FORMULA){
    				formula = moveFormula(formula, moveCount);
    				cell.setCellFormula(formula);
    			}
    		}
    	}
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
    	else if(value instanceof Long){
    		cell.setCellValue(((Long)value).intValue());
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
    	else if(value instanceof BigDecimal){
    		cell.setCellValue(((BigDecimal)value).doubleValue());
    	}
    	else{
    		cell.setCellValue(value.toString());
    	}
    }
   
}
