/**
 * 
 */
package com.ims.dao;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import com.ims.model.Rate;
import com.ims.model.sysEnum.MoneyType;
import com.ims.util.DBUtil;

/**
 * 汇率表操作
 * @author ChengNing
 * @date   2014年9月25日
 */
public class RateDao {
	
	
	public RateDao(){}
	
	/**
	 * 得到当前时间的汇率
	 * @return
	 */
	public Rate getCurrentRate(){
		Rate rate = new Rate();
		String sql = "select a.foreignMoneyName,a.rate from d_rate a WHERE (a.foreignMoney,a.startDateTime) in (SELECT foreignmoney,max(startdatetime) from d_rate GROUP BY foreignmoney)";
		List<Object> list = DBUtil.query(sql);
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Map<Object, Object> map = (Map)it.next();
			setRate(rate, String.valueOf(map.get("foreignMoneyName")), ((BigDecimal)map.get("rate")).doubleValue());
		}
		return rate;
	}
	
	/**
	 * 数据库汇率转换到应用的汇率表示
	 * @param rate
	 * @param moneyName
	 * @param rateValue
	 */
	private void setRate(Rate rate,String moneyName,double rateValue) {
		MoneyType moneyType = MoneyType.getInstance(moneyName);
		rate.setValue(moneyType, rateValue);
	}
	
}
