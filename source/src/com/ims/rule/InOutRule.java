package com.ims.rule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ims.interceptor.InEdit;
import com.ims.util.DBUtil;

public class InOutRule {
	
	private static Logger log = Logger.getLogger(InEdit.class);
	/***
	 * 
	 * 入库操作时判断是否存在该试剂/耗材
	 * 
	 * @param strcatno
	 *            货号
	 * @param strcatname
	 *            名称
	 * @return true代表库中有该试剂/耗材，false代表库中不存在该试剂/耗材
	 */
	public static boolean catIsExit(String strcatno, String strcatname) {

		// 判断货号和名称是否存在的sql
		String sql = "SELECT DISTINCT id FROM d_catcode where catno=?  and  catname=? ";

		// 执行SQL
		List<Object> list = DBUtil.query(sql, strcatno, strcatname);
		// 试剂耗材库中不存在该货号
		if (list.size() < 1) {

			return false;
		} else {
			return true;
		}
	}

	/***
	 * 判断入库时间是否晚于上月统计的时间，如果早或者等于则不能进行增、删、改
	 * 
	 * @param strindate
	 *            入库时间
	 * @return false代表早于等于上月统计的时间，true代表晚于上月的统计时间
	 */
	public static boolean indateIsOK(String strindate) {

		// 本次入库时间
		Date indate = null;
		Date rundate = null;
		int iDay = 0;

		try {

			// 调用获取R统计的函数
			rundate = InOutRule.getRrundate();

			// 字符串转为时间
			indate = new SimpleDateFormat("yyyy-MM-dd").parse(strindate);

		} catch (ParseException e) {

			log.error("字符串转时间失败:" + strindate);
		}

		// 如果入库时间比统计时间晚于1天则可以入库，否则不行
		if (indate.after(rundate))
			return true;
		else
			return false;

	}

	/***
	 * 获取R统计时间
	 * 
	 * @return
	 */
	public static Date getRrundate() throws ParseException {

		Calendar calendar = Calendar.getInstance();
		int iyear = calendar.get(Calendar.YEAR);
		// 上月R统计时间
		Date rundate = null;

		int monthOfYear = calendar.get(calendar.MONTH);
		// 得到的月份是从0开始，但是我要取上个月的统计日，所以不用加1，但对于1月份的情况我要专门处理为上一年的12月份
		if (monthOfYear == 0) {
			monthOfYear = 12;
			iyear = iyear - 1;
		}
		// 查询上月的统计日期
		String sql = "select runPoint from d_task where code='monthtask' and flag = ?";
		String runPoint = DBUtil.getOneValue(sql, String
				.valueOf(monthOfYear));

		int iDay = 0;

		iDay = Integer.parseInt(runPoint);
		// 转为时间,注意月份还要-1，因为是从0开始的.另外年要减去1900
		rundate = new Date(iyear - 1900, monthOfYear - 1, iDay);

		return rundate;

	}
	/***
	 * 获取指定货号、批号和单价的剩余库存
	 * @param strcatno 货号
	 * @param strbatchno 批号
	 * @param strprice 单价
	 * @return
	 */
	public static int getcatTotal(String strcatno ,String strbatchno,	String strprice)
	{
		int itotal = 0;
		
		// 查询库库存信息
		String sql = "select total from b_cat where catno=? and batchno=? and price=?";
		String strtotal = DBUtil.getOneValue(sql, strcatno , strbatchno, strprice);
		
		//查询出结果
		itotal = Integer.parseInt(strtotal);
		
		return itotal;
	}
	/***
	 * 获取指定货号、批号和单价的出库数量
	 * @param strcatno 货号
	 * @param strbatchno 批号
	 * @param strprice 单价
	 * @return
	 */
	public static int getoutTotal(String strcatno ,String strbatchno,	String strprice)
	{
		int itotal = 0;
		
		// 查询库库存信息
		String sql = "select num from b_out where catno=? and batchno=? and price=?";
		String strtotal = DBUtil.getOneValue(sql, strcatno , strbatchno, strprice);
		
		//查询出结果
		itotal = Integer.parseInt(strtotal);
		
		return itotal;
	}
}
