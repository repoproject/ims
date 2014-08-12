package com.zxyx.model;


/**
 * Web服务器2的信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class WebServer2Info extends ServerInfo {
	
	// 单例模式
	private volatile static WebServer2Info webServer2Info;
	private WebServer2Info() {
		
	}
	public static WebServer2Info getWebServer2Info() {
		if(webServer2Info == null){
			synchronized (WebServer2Info.class) {
				if (webServer2Info == null) {
					webServer2Info = new WebServer2Info();
				}
			}
		}	
		return webServer2Info;
	}	
}
