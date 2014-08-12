package com.zxyx.model;

/**
 * 磁盘使用信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class DiskUsageInfo {
    // 磁盘名称
	private String diskName;
	// 安装点
	private String mountPoint;
	// 磁盘大小
	private long diskSize;
	// 磁盘已使用空间
	private long diskUsedSize;
	// 磁盘使用率
	private float diskUsage;
	
	public String getDiskName() {
		return diskName;
	}
	public void setDiskName(String diskName) {
		this.diskName = diskName;
	}
	public String getMountPoint() {
		return mountPoint;
	}
	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}
	public long getDiskSize() {
		return diskSize;
	}
	public void setDiskSize(long diskSize) {
		this.diskSize = diskSize;
	}
	public long getDiskUsedSize() {
		return diskUsedSize;
	}
	public void setDiskUsedSize(long diskUsedSize) {
		this.diskUsedSize = diskUsedSize;
	}
	public float getDiskUsage() {
		return diskUsage;
	}
	public void setDiskUsage(float diskUsage) {
		this.diskUsage = diskUsage;
	}
	
}
