/**
 * 
 */
package com.ims.listener;

import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.ims.task.DayTask;

/**
 * 每日定时作业
 * @author ChengNing
 * @date   2014年9月1日
 */
public class TaskListener implements ServletContextListener {

    protected Logger cLogger = Logger.getLogger(getClass());
	
	private static final long PERIOD_DAY = 24*60*60*1000; //run
	private Timer timer = null;


	public void contextInitialized(ServletContextEvent arg0) {
		cLogger.info("定时任务监听器启动..........");
		timer = new Timer(true);
		//指定时间开始固定的延迟时间执行
		Date startDate = DayTask.getExeDate();
		cLogger.info("注册日终任务====执行时间" + DateFormatUtils.format(startDate,"HH:mm:ss")  + "..........");
		timer.schedule(new DayTask(),startDate,PERIOD_DAY);
	}

	public void contextDestroyed(ServletContextEvent arg0) {	
		timer.cancel();
	}

}
