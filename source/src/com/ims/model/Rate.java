/**
 * 
 */
package com.ims.model;

import com.ims.model.sysEnum.MoneyType;


/**
 * 汇率
 * @author ChengNing
 * @date   2014年9月25日
 */
public class Rate {
	private double cny;
	private double usd;
	private double sgd;
	private double eur;
	private double gbp;
	
	
	/**
	 * 设置汇率
	 * @param type
	 * @param rate
	 */
	public void setValue(MoneyType type,double rate){
		switch (type) {
			case CNY:
				this.cny = rate;
				break;
			case USD:
				this.usd = rate;
				break;
			case SGD:
				this.sgd = rate;
				break;
			case EUR:
				this.eur = rate;
				break;
			case GBP:
				this.gbp = rate;
				break;
			default:
				break;
		}
	}
	
	/**
	 * 得到汇率
	 * @param type
	 * @return
	 */
	public double getValue(MoneyType type){
		switch (type) {
			case CNY:
				return this.cny;
			case USD:
				return this.usd ;
			case SGD:
				return this.sgd ;
			case EUR:
				return this.eur;
			case GBP:
				return this.gbp;
			default:
				break;
		}
		return 0.0;
	}
	
	
}



