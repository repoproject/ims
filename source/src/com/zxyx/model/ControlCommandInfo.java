package com.zxyx.model;

import java.util.ArrayList;


/**
 * 控制指令执行状态信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */

public class ControlCommandInfo {

	// 控制指令执行状态最大缓存数量  300条
	public final int MAX_COUNT = 300;
	// 控制指令执行状态列表
	private ArrayList<ControlCommand> controlCommands;
	
	public ArrayList<ControlCommand> getControlCommands() {
		return controlCommands;
	}
	public void setControlCommands(ArrayList<ControlCommand> controlCommands) {
		this.controlCommands = controlCommands;
	}
	
	// 单例模式
	private volatile static ControlCommandInfo controlCommandInfo;	
	private ControlCommandInfo() {
		controlCommands = new ArrayList<ControlCommand>();
	}
	public static ControlCommandInfo getControlCommandInfo() {
		if(controlCommandInfo == null){
			synchronized (ControlCommandInfo.class) {
				if (controlCommandInfo == null) {
					controlCommandInfo = new ControlCommandInfo();
				}
			}
		}	
		return controlCommandInfo;
	}


}
