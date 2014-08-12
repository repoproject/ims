package com.zxyx.model;

/**
 * 数据库表空间使用率信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class TableSpaceInfo {
    // 服务器ID
	private int serverID;
	// 表空间总大小	单位 M
	private int totalSpace;
	// 表空间空闲大小
	private int freeSpace;
	// 表空间使用率
	private float tableUsage;
	
	public int getServerID() {
		return serverID;
	}
	public void setServerID(int serverID) {
		this.serverID = serverID;
	}
	public int getTotalSpace() {
		return totalSpace;
	}
	public void setTotalSpace(int totalSpace) {
		this.totalSpace = totalSpace;
	}
	public int getFreeSpace() {
		return freeSpace;
	}
	public void setFreeSpace(int freeSpace) {
		this.freeSpace = freeSpace;
	}
	public float getTableUsage() {
		return tableUsage;
	}
	public void setTableUsage(float tableUsage) {
		this.tableUsage = tableUsage;
	}
	
}
