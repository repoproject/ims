/**
 * 
 */
package com.ims.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 系统配置变量
 * @author ChengNing
 * @date   2014-9-3
 */
public class SysVar {
	
	/**
	 * 系统变量常用，使用缓存，目前配置数据不多，不定缓存大小
	 * 如果数据过大，需要配置缓存大小并且确认缓存替换策略
	 */
	private static Map<String, String> cache = new HashMap<String, String>();
	private static boolean useCache = true;
    
	/**
	 * 读缓存
	 * @param key
	 * @return
	 */
	private static String fromCache(String key){
		if(!useCache)
			return null;
		if(cache.containsKey(key))
			return cache.get(key).toString();
		else 
			return null;
	}
	
	/**
	 * 写缓存
	 * @param key
	 * @param value
	 */
	private static void toCache(String key,String value){
		if(!useCache)
			return;
		//替换缓存
		if(StringUtils.isBlank(value))
			return;
		//进行缓存替换
		cache.put(key, value);
	}
	
	/**
	 * 取得系统级变量d_var中键值对
	 * @param key
	 * @return
	 */
	public static String getValue(String key){
		String value = fromCache(key);
		if(StringUtils.isBlank(value)){
			String sql = "select sysvalue from d_var where syskey = ?";
			value = DBUtil.getOneValue(sql, key);
			toCache(key,value);
		}
		return value;
	}
	
	/**
	 * 取得系统级变量b_var中键值对
	 * @param bizKey
	 * @return
	 */
	public static String getBizValue(String bizKey){
		String key = "b_" + bizKey;
		String value = fromCache(key);
		if(StringUtils.isBlank(value)){
			String sql = "select bizValue from b_var where bizkey = ?";
			value = DBUtil.getOneValue(sql, bizKey);
			toCache(key,value);
		}
		return value;
	}
	
	public static String noMachineName(){
		return getBizValue("nomachinename");
	}
	
	public static String noMachineNo(){
		return getBizValue("nomachineno");
	}
}
