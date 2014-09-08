/**
 * 
 */
package com.ims.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期时间工具类
 * @author ChengNing
 * @date   2014-9-7
 */
public class DateTimeUtil {
	
	public static final String DEFAULT_FORMAT_DATE = "yyyy-MM-dd";
	public static final String DEFAULT_FORMAT_TIME = "HH:mm:SS";
	public static final String DEFAULT_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:SS";
	
	/**
	 * 字符串转换为日期
	 * @param dateString   日期字符串
	 * @param format       格式化字符串
	 * @return
	 */
	public static Date getDate(String dateString,String format){
		SimpleDateFormat sf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date getDate(String dateTimeString){
		return getDate(dateTimeString,DEFAULT_FORMAT_DATETIME);
	}
	
	
	/**
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String toDateString(Date date,String format) {
		SimpleDateFormat sf = new SimpleDateFormat(format);
		return sf.format(date);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String today(){
		return currentDate();
	}
	
	/**
	 * 
	 * @return
	 */
	public static String now(){
		return current();
	}
	
	/**
	 * 
	 * @return
	 */
	public static String currentTime(){
		return toDateString(new Date(), DEFAULT_FORMAT_TIME);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String currentDate(){
		return toDateString(new Date(), DEFAULT_FORMAT_DATE);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String current(){
		return toDateString(new Date(), DEFAULT_FORMAT_DATETIME);
	}
	
	public static String format(String text,Date date){
		int start = text.indexOf("{");
		int end = text.indexOf("}");
		while(start > 0 && end > 0){
			String subStr = text.substring(start, end+1);
			String format = text.substring(start+1, end);
			String dateStr = toDateString(date, format);
			text = text.replace(subStr, dateStr);
			
			start = text.indexOf("{");
			end = text.indexOf("}");
		}
		return text;
	}
	
}
