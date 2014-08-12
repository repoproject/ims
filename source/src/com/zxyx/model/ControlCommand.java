package com.zxyx.model;

/**
 * 控制指令执行状态
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class ControlCommand {
	// 控制指令标识
	private int CommandID;
	// 发送时间
	private String sendTime;
	// 发送者
	private String sender;
	// 发送位置
	private String senderLocation;
	// 系统名称
	private String systemName;
	// 设备名称
	private String equipmentName;
	// 指令名称
	private String commandName;	
	// 指令状态
	private int commandState;
	// 状态时间
	private String stateTime;
	// 指令内容长度
	private int commandInfoLen;
	// 指令内容
	private String commandInfo;
	
	public int getCommandID() {
		return CommandID;
	}
	public void setCommandID(int commandID) {
		CommandID = commandID;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSenderLocation() {
		return senderLocation;
	}
	public void setSenderLocation(String senderLocation) {
		this.senderLocation = senderLocation;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	public String getEquipmentName() {
		return equipmentName;
	}
	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}
	public String getCommandName() {
		return commandName;
	}
	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}
	public int getCommandState() {
		return commandState;
	}
	public void setCommandState(int commandState) {
		this.commandState = commandState;
	}
	public String getStateTime() {
		return stateTime;
	}
	public void setStateTime(String stateTime) {
		this.stateTime = stateTime;
	}
	public int getCommandInfoLen() {
		return commandInfoLen;
	}
	public void setCommandInfoLen(int commandInfoLen) {
		this.commandInfoLen = commandInfoLen;
	}
	public String getCommandInfo() {
		return commandInfo;
	}
	public void setCommandInfo(String commandInfo) {
		this.commandInfo = commandInfo;
	}

}
