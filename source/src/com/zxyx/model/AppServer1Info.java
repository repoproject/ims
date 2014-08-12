package com.zxyx.model;


/**
 * 应用服务器1的信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class AppServer1Info extends ServerInfo {	
	
	// 单例模式
	private volatile static AppServer1Info appServer1Info;
	private AppServer1Info() {
		
	}
	public static AppServer1Info getAppServer1Info() {
		if(appServer1Info == null){
			synchronized (AppServer1Info.class) {
				if (appServer1Info == null) {
					appServer1Info = new AppServer1Info();
				}
			}
		}	
		return appServer1Info;
	}	
}
