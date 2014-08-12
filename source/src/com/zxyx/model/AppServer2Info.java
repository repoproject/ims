package com.zxyx.model;


/**
 * 应用服务器2的信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class AppServer2Info extends ServerInfo {
	
	// 单例模式
	private volatile static AppServer2Info appServer2Info;
	private AppServer2Info() {
		
	}
	public static AppServer2Info getAppServer2Info() {
		if(appServer2Info == null){
			synchronized (AppServer2Info.class) {
				if (appServer2Info == null) {
					appServer2Info = new AppServer2Info();
				}
			}
		}	
		return appServer2Info;
	}	
}
