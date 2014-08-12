package com.zxyx.model;
/**
 * 进程信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class ProcInfo {
    // 软件名称
	private String softwareName;
	// 映像名称
	private String imageName;
	// 进程ID
	private long procID;
	// 用户名
	private String userName;
	// 进程状态
	private String procStatus;
	// CPU使用率
	private float cpuUsage;
	// 总CPU时间
	private int totalCPUTime;
	// 内存使用
	private long memUsed;
	// 进程优先级
	private int priority;
	// 状态描述
	private String statusDesc;
	
	public String getSoftwareName() {
		return softwareName;
	}
	public void setSoftwareName(String softwareName) {
		this.softwareName = softwareName;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public long getProcID() {
		return procID;
	}
	public void setProcID(long procID) {
		this.procID = procID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getProcStatus() {
		return procStatus;
	}
	public void setProcStatus(String procStatus) {
		this.procStatus = procStatus;
	}
	public float getCpuUsage() {
		return cpuUsage;
	}
	public void setCpuUsage(float cPUUsage) {
		cpuUsage = cPUUsage;
	}
	public int getTotalCPUTime() {
		return totalCPUTime;
	}
	public void setTotalCPUTime(int totalCPUTime) {
		this.totalCPUTime = totalCPUTime;
	}
	public long getMemUsed() {
		return memUsed;
	}
	public void setMemUsed(long memUsed) {
		this.memUsed = memUsed;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
}
