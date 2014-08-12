package com.zxyx.model;

import java.util.ArrayList;

/**
 * 服务器信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public abstract class ServerInfo {
	// 服务器标识
	private int serverID;
	// 服务器名称
	private String serverName;
	// 服务器IP地址
	private String serverIP;
	// 操作系统版本信息
	private String osInfo;
	// CPU型号
	private String cpuType;
	// CPU数目
	private int cpuCount;
	// 系统内存大小 
	private long memSize;
	
	// 总CPU使用率
	private float totalCpuUsage;
	// CPU使用个数
	private int cpuUsedCount;
	// CPU使用信息列表
	private ArrayList<CPUUsageInfo> cpuUsageInfos;
	
	// 磁盘个数
	private int diskCount;
	// 磁盘使用信息列表
	private ArrayList<DiskUsageInfo> diskUsageInfos;
	
	// 内存使用信息
	private MemUsageInfo memUsageInfo;
	
	// 进程个数
	private int procCount;
	// 进程信息列表
	private ArrayList<ProcInfo> procInfos;
	
	// 服务器系统信息计数
	private int sysInfoCnt;
	// CPU使用信息计数
	private int cpuInfoCnt;
	// 内存使用信息计数
	private int memInfoCnt;
	// 磁盘使用信息计数
	private int diskInfoCnt;
	// 进程信息计数
	private int procInfoCnt;
	

	public int getServerID() {
		return serverID;
	}
	public void setServerID(int serverID) {
		this.serverID = serverID;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	public String getOsInfo() {
		return osInfo;
	}
	public void setOsInfo(String osInfo) {
		this.osInfo = osInfo;
	}
	public String getCpuType() {
		return cpuType;
	}
	public void setCpuType(String cpuType) {
		this.cpuType = cpuType;
	}
	public int getCpuCount() {
		return cpuCount;
	}
	public void setCpuCount(int cpuCount) {
		this.cpuCount = cpuCount;
	}
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public float getTotalCpuUsage() {
		return totalCpuUsage;
	}
	public void setTotalCpuUsage(float totalCpuUsage) {
		this.totalCpuUsage = totalCpuUsage;
	}	
	public int getCpuUsedCount() {
		return cpuUsedCount;
	}
	public void setCpuUsedCount(int cpuUsedCount) {
		this.cpuUsedCount = cpuUsedCount;
	}
	public ArrayList<CPUUsageInfo> getCpuUsageInfos() {
		return cpuUsageInfos;
	}
	public void setCpuUsageInfos(ArrayList<CPUUsageInfo> cpuUsageInfos) {
		this.cpuUsageInfos = cpuUsageInfos;
	}
	public int getDiskCount() {
		return diskCount;
	}
	public void setDiskCount(int diskCount) {
		this.diskCount = diskCount;
	}
	public ArrayList<DiskUsageInfo> getDiskUsageInfos() {
		return diskUsageInfos;
	}
	public void setDiskUsageInfos(ArrayList<DiskUsageInfo> diskUsageInfos) {
		this.diskUsageInfos = diskUsageInfos;
	}
	public MemUsageInfo getMemUsageInfo() {
		return memUsageInfo;
	}
	public void setMemUsageInfo(MemUsageInfo memUsageInfo) {
		this.memUsageInfo = memUsageInfo;
	}
	public int getProcCount() {
		return procCount;
	}
	public void setProcCount(int procCount) {
		this.procCount = procCount;
	}
	public ArrayList<ProcInfo> getProcInfos() {
		return procInfos;
	}
	public void setProcInfos(ArrayList<ProcInfo> procInfos) {
		this.procInfos = procInfos;
	}
	public int getSysInfoCnt() {
		return sysInfoCnt;
	}
	public void setSysInfoCnt(int sysInfoCnt) {
		this.sysInfoCnt = sysInfoCnt;
	}
	public int getCpuInfoCnt() {
		return cpuInfoCnt;
	}
	public void setCpuInfoCnt(int cpuInfoCnt) {
		this.cpuInfoCnt = cpuInfoCnt;
	}
	public int getMemInfoCnt() {
		return memInfoCnt;
	}
	public void setMemInfoCnt(int memInfoCnt) {
		this.memInfoCnt = memInfoCnt;
	}
	public int getDiskInfoCnt() {
		return diskInfoCnt;
	}
	public void setDiskInfoCnt(int diskInfoCnt) {
		this.diskInfoCnt = diskInfoCnt;
	}
	public int getProcInfoCnt() {
		return procInfoCnt;
	}
	public void setProcInfoCnt(int procInfoCnt) {
		this.procInfoCnt = procInfoCnt;
	}
	
}
