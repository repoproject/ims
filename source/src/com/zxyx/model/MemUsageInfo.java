package com.zxyx.model;

/**
 * 内存使用信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class MemUsageInfo {
	// 内存使用大小
	private long memUsedSize;
	// 内存使用率
	private float memUsage;

	public long getMemUsedSize() {
		return memUsedSize;
	}
	public void setMemUsedSize(long memUsedSize) {
		this.memUsedSize = memUsedSize;
	}
	public float getMemUsage() {
		return memUsage;
	}
	public void setMemUsage(float memUsage) {
		this.memUsage = memUsage;
	}
	
}
