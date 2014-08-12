package com.zxyx.model;

/**
 * 服务器工作状态
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */

public class ServerState {

	// 文件交换服务器1关键软件状态    0正常  1故障
	private byte fileServer1SoftStatus = -1;
	// 文件交换服务器1本机状态  0正常  1代理故障  2离线
	private byte fileServer1NetStatus = -1;
	
	// 文件交换服务器2关键软件状态    0正常  1故障
	private byte fileServer2SoftStatus = -1;
	// 文件交换服务器2本机状态  0正常  1代理故障  2离线
	private byte fileServer2NetStatus = -1;
	
	// Web服务器1关键软件状态    0正常  1故障
	private byte webServer1SoftStatus = -1;
	// Web服务器1本机状态  0正常  1代理故障  2离线
	private byte webServer1NetStatus = -1;
	
	// Web服务器2关键软件状态    0正常  1故障
	private byte webServer2SoftStatus = -1;
	// Web服务器2本机状态  0正常  1代理故障  2离线
	private byte webServer2NetStatus = -1;
		
	// CMS服务器关键软件状态    0正常  1故障
	private byte cmsServer1SoftStatus = -1;
	// CMS服务器本机状态  0正常  1代理故障  2离线
	private byte cmsServer1NetStatus = -1;
	
	// 数据库管理服务器1关键软件状态    0正常  1故障
	private byte dbServer1SoftStatus = -1;
	// 数据库管理服务器1本机状态  0正常  1代理故障  2离线
	private byte dbServer1NetStatus = -1;
	
	// 数据库管理服务器2关键软件状态    0正常  1故障
	private byte dbServer2SoftStatus = -1;
	// 数据库管理服务器2本机状态  0正常  1代理故障  2离线
	private byte dbServer2NetStatus = -1;
	
	// 应用服务器1关键软件状态    0正常  1故障
	private byte appServer1SoftStatus = -1;
	// 应用服务器1本机状态  0正常  1代理故障  2离线
	private byte appServer1NetStatus = -1;
	
	// 应用服务器2关键软件状态    0正常  1故障
	private byte appServer2SoftStatus = -1;
	// 应用服务器2本机状态  0正常  1代理故障  2离线
	private byte appServer2NetStatus = -1;
	
	// 服务器工作状态计数
	private int serverStateCnt;
	
	// 单例模式
	private volatile static ServerState serverState;
	private ServerState() {
		
	}
	public static ServerState getServerState() {
		if(serverState == null){
			synchronized (ServerState.class) {
				if (serverState == null) {
					serverState = new ServerState();
				}
			}
		}	
		return serverState;
	}
	
	
	public byte getFileServer1SoftStatus() {
		return fileServer1SoftStatus;
	}
	public void setFileServer1SoftStatus(byte fileServer1SoftStatus) {
		this.fileServer1SoftStatus = fileServer1SoftStatus;
	}
	public byte getFileServer1NetStatus() {
		return fileServer1NetStatus;
	}
	public void setFileServer1NetStatus(byte fileServer1NetStatus) {
		this.fileServer1NetStatus = fileServer1NetStatus;
	}
	public byte getFileServer2SoftStatus() {
		return fileServer2SoftStatus;
	}
	public void setFileServer2SoftStatus(byte fileServer2SoftStatus) {
		this.fileServer2SoftStatus = fileServer2SoftStatus;
	}
	public byte getFileServer2NetStatus() {
		return fileServer2NetStatus;
	}
	public void setFileServer2NetStatus(byte fileServer2NetStatus) {
		this.fileServer2NetStatus = fileServer2NetStatus;
	}
	public byte getWebServer1SoftStatus() {
		return webServer1SoftStatus;
	}
	public void setWebServer1SoftStatus(byte webServer1SoftStatus) {
		this.webServer1SoftStatus = webServer1SoftStatus;
	}
	public byte getWebServer1NetStatus() {
		return webServer1NetStatus;
	}
	public void setWebServer1NetStatus(byte webServer1NetStatus) {
		this.webServer1NetStatus = webServer1NetStatus;
	}
	public byte getWebServer2SoftStatus() {
		return webServer2SoftStatus;
	}
	public void setWebServer2SoftStatus(byte webServer2SoftStatus) {
		this.webServer2SoftStatus = webServer2SoftStatus;
	}
	public byte getWebServer2NetStatus() {
		return webServer2NetStatus;
	}
	public void setWebServer2NetStatus(byte webServer2NetStatus) {
		this.webServer2NetStatus = webServer2NetStatus;
	}
	public byte getCmsServer1SoftStatus() {
		return cmsServer1SoftStatus;
	}
	public void setCmsServer1SoftStatus(byte cmsServer1SoftStatus) {
		this.cmsServer1SoftStatus = cmsServer1SoftStatus;
	}
	public byte getCmsServer1NetStatus() {
		return cmsServer1NetStatus;
	}
	public void setCmsServer1NetStatus(byte cmsServer1NetStatus) {
		this.cmsServer1NetStatus = cmsServer1NetStatus;
	}
	public byte getDbServer1SoftStatus() {
		return dbServer1SoftStatus;
	}
	public void setDbServer1SoftStatus(byte dbServer1SoftStatus) {
		this.dbServer1SoftStatus = dbServer1SoftStatus;
	}
	public byte getDbServer1NetStatus() {
		return dbServer1NetStatus;
	}
	public void setDbServer1NetStatus(byte dbServer1NetStatus) {
		this.dbServer1NetStatus = dbServer1NetStatus;
	}
	public byte getDbServer2SoftStatus() {
		return dbServer2SoftStatus;
	}
	public void setDbServer2SoftStatus(byte dbServer2SoftStatus) {
		this.dbServer2SoftStatus = dbServer2SoftStatus;
	}
	public byte getDbServer2NetStatus() {
		return dbServer2NetStatus;
	}
	public void setDbServer2NetStatus(byte dbServer2NetStatus) {
		this.dbServer2NetStatus = dbServer2NetStatus;
	}
	public byte getAppServer1SoftStatus() {
		return appServer1SoftStatus;
	}
	public void setAppServer1SoftStatus(byte appServer1SoftStatus) {
		this.appServer1SoftStatus = appServer1SoftStatus;
	}
	public byte getAppServer1NetStatus() {
		return appServer1NetStatus;
	}
	public void setAppServer1NetStatus(byte appServer1NetStatus) {
		this.appServer1NetStatus = appServer1NetStatus;
	}
	public byte getAppServer2SoftStatus() {
		return appServer2SoftStatus;
	}
	public void setAppServer2SoftStatus(byte appServer2SoftStatus) {
		this.appServer2SoftStatus = appServer2SoftStatus;
	}
	public byte getAppServer2NetStatus() {
		return appServer2NetStatus;
	}
	public void setAppServer2NetStatus(byte appServer2NetStatus) {
		this.appServer2NetStatus = appServer2NetStatus;
	}
	public int getServerStateCnt() {
		return serverStateCnt;
	}
	public void setServerStateCnt(int serverStateCnt) {
		this.serverStateCnt = serverStateCnt;
	}
	

}
