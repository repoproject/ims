package com.zxyx.model;
/**
 * CPU使用信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class CPUUsageInfo {
	// CPU标识
	private int cpuID;
	// CPU使用率
	private float cpuUsage;
	public int getCpuID() {
		return cpuID;
	}
	
	public void setCpuID(int cpuID) {
		this.cpuID = cpuID;
	}
	public float getCpuUsage() {
		return cpuUsage;
	}
	public void setCpuUsage(float cpuUsage) {
		this.cpuUsage = cpuUsage;
	}
	

	
}
