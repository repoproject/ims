/**
 * 
 */
package com.ims.model.sysEnum;

import org.apache.commons.lang.StringUtils;

/**
 * @author ChengNing
 * @date 2014年9月25日
 */
public enum MoneyType {
	CNY("CNY"), USD("USD"), SGD("SGD"), EUR("EUR"), GBP("GBP");

	private String name;

	private MoneyType(String name) {
		this.name = name;
	}
	
	public static MoneyType getInstance(String name){
		if(StringUtils.isBlank(name))
			return MoneyType.CNY;
		for(MoneyType type : MoneyType.values()){
			if(StringUtils.equals(name, type.name)){
				return type;
			}
		}
		return null;
	}

}
