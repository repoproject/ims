package com.zxyx.model;


/**
 * CMS服务器的信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class CMSServerInfo extends ServerInfo {
	
	// 单例模式
	private volatile static CMSServerInfo cmsServerInfo;
	private CMSServerInfo() {
		
	}
	public static CMSServerInfo getCMSServerInfo() {
		if(cmsServerInfo == null){
			synchronized (CMSServerInfo.class) {
				if (cmsServerInfo == null) {
					cmsServerInfo = new CMSServerInfo();
				}
			}
		}	
		return cmsServerInfo;
	}	
}
