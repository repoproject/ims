/**
 * 
 */
package com.ims.task;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * @author ChengNing
 * @date   2014年9月1日
 */
public class DayTask extends TimerTask{

	
	private static final String DEFAULT_TIME = "23:00:00";
    protected Logger cLogger = Logger.getLogger(getClass());
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public static Date getExeDate(){


		String[] startTime = DEFAULT_TIME.split(":");

		int startHour = Integer.parseInt(startTime[0]);
		int startMinute = Integer.parseInt(startTime[1]);
		int startSecond = Integer.parseInt(startTime[2]);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, startHour);
		calendar.set(Calendar.MINUTE, startMinute);
		calendar.set(Calendar.SECOND,startSecond);
		
		//指定时间点的日期
		Date date = calendar.getTime();
		//如果今天已经过了指定时间，则明天执行，不加一天会导致启动即执行。
		if(date.before(new Date())){
			//date = LisTask.addDay(date,1);
		}
		return date;
	}

}
