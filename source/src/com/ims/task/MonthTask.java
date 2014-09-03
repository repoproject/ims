/**
 * 
 */
package com.ims.task;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.ims.util.DBUtil;

/**
 * 月度报表任务，单独的线程
 * @author ChengNing
 * @date   2014-9-3
 */
public class MonthTask implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 判断今日是否月结任务日
	 * @return
	 */
	public static boolean isMonthTaskDay(){
		Calendar calendar = Calendar.getInstance();
		int dayOfMonth = calendar.get(calendar.DAY_OF_MONTH);
		int monthOfYear = calendar.get(calendar.MONTH);
		monthOfYear++;
		String sql = "select runPoint from d_task where code='monthtask' and flag = ?";
		String runPoint = DBUtil.getOneValue(sql,String.valueOf(monthOfYear));
		if(StringUtils.equals(String.valueOf(dayOfMonth), runPoint))
			return true;
		return false;
	}

}
