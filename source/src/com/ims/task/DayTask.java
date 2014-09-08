/**
 * 
 */
package com.ims.task;

import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.ims.common.TaskData;
import com.ims.util.DateTimeUtil;

/**
 * 每日任务
 * @author ChengNing
 * @date   2014年9月1日
 */
public class DayTask extends TimerTask{

	public static final long PERIOD = 24*60*60*1000; //run
	
	private String runTime = "";
    protected Logger log = Logger.getLogger(getClass());
    
	@Override
	public void run() {
		log.info("DayTask run....");
		//如果是月结日进行月结操作
		if(MonthTask.isMonthTaskDay()){
			MonthTask monthTask = new MonthTask();
			Thread taskThread = new Thread(monthTask,"MonthTaskThread");
			taskThread.start();
		}
	}
	
	/**
	 * 任务执行时间
	 * @return
	 */
	public Date getExeDate(){
		if(StringUtils.isBlank(runTime)){
			runTime = getRunTime();
			log.info("设定执行时间点为" + runTime);
		}
		//指定时间点的日期
		Date date = DateTimeUtil.getToday(runTime);
		//如果今天已经过了指定时间，则下一个执行时间执行，不加一天会导致启动即执行。
		if(date.before(new Date())){
			date = DateUtils.addMilliseconds(date, (int) PERIOD);
		}
		return date;
	}
	
	/**
	 * 读取日终任务运行时间
	 * @return
	 */
	private String getRunTime(){
		return TaskData.dayRunTime();
	}
	
	

}
