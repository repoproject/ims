package com.ims.common;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.ims.util.DBUtil;
import com.ims.util.DateTimeUtil;

/**
 * task表数据相关操作
 * @author ChengNing
 * @date   2014-9-8
 */
public class TaskData {
	
	private Date thisMonth; //本月月结时间
	private Date nextMonth; //下月月结时间
	private Date lastMonth; //上月月结时间
	
	public TaskData(){
		setMonthTaskTime();
	}
	
	/**
	 * 得到每日任务运行时间
	 * @return
	 */
	public static String dayRunTime(){
		String sql = "select runTime from d_task where code='daytask' LIMIT 1";
		String runTime = DBUtil.getOneValue(sql);
		return runTime;
	}
	
	/**
	 * 今日任务运行时间
	 * @return
	 */
	public Date todayRunTime(){
		String runTime = dayRunTime();
		Date date = DateTimeUtil.getToday(runTime);
		return date;
	}
	
	/**
	 * 下次月结的时间
	 * @return
	 */
	public Date nextTaskDate(){
		Date now = new Date();
		if(now.after(this.thisMonth))
			return this.nextMonth;
		else if(now.before(this.thisMonth))
			return this.thisMonth;
		else if(now.equals(this.thisMonth))
			return this.nextMonth;
		else 
			return null;
	}
	
	/**
	 * 上次月结的时间
	 * @return
	 */
	public Date lastTaskDate(){
		Date now = new Date();
		if(now.after(this.thisMonth))
			return this.thisMonth;
		else if(now.before(this.thisMonth))
			return this.lastMonth;
		else if(now.equals(this.thisMonth))
			return this.lastMonth;
		else 
			return null;
	}
	
	/**
	 * 本月的任务运行时间
	 * @return
	 */
	public Date currentMonthTask(){
		return this.thisMonth;
	}
	
	/**
	 * 设置私有时间
	 * @param now
	 * @return
	 */
	private void setMonthTaskTime(){
		Date now = new Date();
		//为提高效率，一次性查出来三个
		int thisMonth = now.getMonth();
		int nextMonth = (thisMonth+1)%12;
		int lastMonth = (thisMonth+12-1)%12;
		//计算为从1开始
		String tm = String.valueOf(++thisMonth);
		String nm = String.valueOf(++nextMonth);
		String lm = String.valueOf(++lastMonth);
		
		String sql = "select flag,runPoint from d_task where code='monthtask' and flag in(?,?,?)";
		List<Object> list = DBUtil.query(sql,lm,tm,nm );
		for(Object obj : list){
			Map<String, String> map = (Map)obj;
			String flag = map.get("flag");
			String runpoint = map.get("runPoint");
			if(flag.equals(tm)){
				this.thisMonth = getThisMonthRunDate(runpoint);
			}
			else if(flag.equals(nm)){
				this.nextMonth = getNextMonthRunDate(runpoint);
			}
			else if(flag.equals(lm)){
				this.lastMonth = getLastMonthRunDate(runpoint);
			}
		}
	}
	
	/**
	 * 得到月结时间
	 * @param runpoint   月结日
	 * @param position  以当前月为轴，月份的移动值，0 当月，1下月，-1上月
	 * @return
	 */
	private Date getRunDate(String runpoint,int position){
		if(runpoint.length() == 1)
			runpoint = "0" + runpoint;
		Date date = new Date();
		date = DateUtils.addMonths(date, position);
		String dateStr = DateTimeUtil.toDateString(date, "yyyy-MM") + runpoint;
		String dateTime = dateStr + dayRunTime();
		return DateTimeUtil.getDateTime(dateTime);
	}
	
	/**
	 * 本月的月结时间
	 * @param runpoint
	 * @return
	 */
	private Date getThisMonthRunDate(String runpoint){
		return getRunDate(runpoint, 0);
	}
	
	/**
	 * 上一月的月结时间
	 * @param runpoint
	 * @return
	 */
	private Date getLastMonthRunDate(String runpoint){
		return getRunDate(runpoint, -1);
	}
	
	/**
	 * 下一月的月结时间
	 * @param runpoint
	 * @return
	 */
	private Date getNextMonthRunDate(String runpoint){
		return getRunDate(runpoint, 1);
	}

}
