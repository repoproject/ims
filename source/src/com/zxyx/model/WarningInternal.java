package com.zxyx.model;

/**
 * 内部告警信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class WarningInternal {
	// 告警信息编号
	private int warnID;
	// 软件名称
	private String softName;
	// 进程名称
	private String procName;
	// 告警时间
	private String warnTime;
	// 恢复时间（用于状态为可恢复告警的告警信息,其值为对应的状态为恢复的告警信息的告警时间字段）
	private String resumeTime;
	// 告警级别
	private byte warnLevel;
	// 告警类型
	private byte warnType;
	// 文件名
	private String fileName;	
	// 告警状态（0恢复 1可恢复告警  3一次性告警）
	private byte warnStatus;
	// 保留1(状态为可恢复和恢复的告警信息中，该字段用作服务器ID)
	private byte ID1;
	// 保留2
	private byte ID2;	
	// 告警信息长度
	private int warnInfoLen;
	// 告警信息描述
	private String warnInfo;

	public int getWarnID() {
		return warnID;
	}
	public void setWarnID(int warnID) {
		this.warnID = warnID;
	}
	public String getSoftName() {
		return softName;
	}
	public void setSoftName(String softName) {
		this.softName = softName;
	}
	public String getProcName() {
		return procName;
	}
	public void setProcName(String procName) {
		this.procName = procName;
	}
	public String getWarnTime() {
		return warnTime;
	}
	public void setWarnTime(String warnTime) {
		this.warnTime = warnTime;
	}
	public String getResumeTime() {
		return resumeTime;
	}
	public void setResumeTime(String resumeTime) {
		this.resumeTime = resumeTime;
	}
	public byte getWarnLevel() {
		return warnLevel;
	}
	public void setWarnLevel(byte warnLevel) {
		this.warnLevel = warnLevel;
	}
	public byte getWarnType() {
		return warnType;
	}
	public void setWarnType(byte warnType) {
		this.warnType = warnType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public byte getWarnStatus() {
		return warnStatus;
	}
	public void setWarnStatus(byte warnStatus) {
		this.warnStatus = warnStatus;
	}
	public byte getID1() {
		return ID1;
	}
	public void setID1(byte iD1) {
		ID1 = iD1;
	}
	public byte getID2() {
		return ID2;
	}
	public void setID2(byte iD2) {
		ID2 = iD2;
	}
	public int getWarnInfoLen() {
		return warnInfoLen;
	}
	public void setWarnInfoLen(int warnInfoLen) {
		this.warnInfoLen = warnInfoLen;
	}
	public String getWarnInfo() {
		return warnInfo;
	}
	public void setWarnInfo(String warnInfo) {
		this.warnInfo = warnInfo;
	}
	
}
