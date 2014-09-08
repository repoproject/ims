/**
 * 
 */
package com.ims.task;

import org.apache.log4j.Logger;

/**
 * 每日进行盘库操作
 * @author ChengNing
 * @date   2014-9-8
 */
public class DayClosing implements Runnable {
	
	private static Logger logger = Logger.getLogger(DayClosing.class);
	
	public DayClosing(){
		
	}

	@Override
	public void run() {
		logger.info("运行盘库任务");		
	}

}
