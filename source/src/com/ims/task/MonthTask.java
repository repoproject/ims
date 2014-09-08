/**
 * 
 */
package com.ims.task;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ims.util.DBUtil;

/**
 * 月度报表任务，单独的线程
 * @author ChengNing
 * @date   2014-9-3
 */
public class MonthTask implements Runnable{

	private static Logger logger = Logger.getLogger(MonthTask.class);
	
	@Override
	public void run() {
		logger.info("开始执行MonthTask.......");
		MonthReport monthTask = new MonthReport();
		monthTask.report();
	}
	
	/**
	 * 判断今日是否月结任务日
	 * @return
	 */
	public static boolean isMonthTaskDay(){
		Calendar calendar = Calendar.getInstance();
		int dayOfMonth = calendar.get(calendar.DAY_OF_MONTH);
		int monthOfYear = calendar.get(calendar.MONTH);
		monthOfYear++;//得到的月份是从0开始，需要加1
		String sql = "select runPoint from d_task where code='monthtask' and flag = ?";
		String runPoint = DBUtil.getOneValue(sql,String.valueOf(monthOfYear));
		logger.info("本月运行日" + runPoint);
		if(StringUtils.equals(String.valueOf(dayOfMonth), runPoint))
			return true;
		return false;
	}

}
