/**
 * 
 */
package com.ims.listener;

import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;


import com.ims.task.DayTask;
import com.ims.util.DateTimeUtil;

/**
 * 任务监听器
 * @author ChengNing
 * @date   2014年9月1日
 */
public class TaskListener implements ServletContextListener {

    protected Logger log = Logger.getLogger(getClass());
	
	private Timer timer = null;

	/**
	 * 监听器启动
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		log.info("定时任务监听器启动..........");
		timer = new Timer(true);
		DayTask dayTask = new DayTask();
		
		//指定时间开始固定的延迟时间执行
		Date startDate = dayTask.getExeDate();
		
		log.info("注册日终任务====执行时间" + DateTimeUtil.toDateTimeStr(startDate)+ "..........");
		timer.schedule(dayTask,startDate,DayTask.PERIOD);
	}

	public void contextDestroyed(ServletContextEvent arg0) {	
		timer.cancel();
	}

}
