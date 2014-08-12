package com.zxyx.model;


/**
 * Web服务器1的信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class WebServer1Info extends ServerInfo {
	
	// 单例模式
	private volatile static WebServer1Info webServer1Info;
	private WebServer1Info() {
		
	}
	public static WebServer1Info getWebServer1Info() {
		if(webServer1Info == null){
			synchronized (WebServer1Info.class) {
				if (webServer1Info == null) {
					webServer1Info = new WebServer1Info();
				}
			}
		}	
		return webServer1Info;
	}	
}
