package com.zxyx.model;

/**
 * 声音告警设置
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */

public class MsgWarningConfig {

	// 告警开关    0关闭  1打开
	private int warnSwitch = 1;
	// 严重告警    0未选  1选中
	private int serious = 1;
	// 重要告警    0未选  1选中
	private int important = 1;
	// 一般告警    0未选  1选中
	private int general = 1;	
	// 相同告警发送间隔开关    0关闭  1打开
	private int intervalSwitch = 1;
	// 相同告警发送间隔
	private int time = 0;
	
	public int getWarnSwitch() {
		return warnSwitch;
	}
	public void setWarnSwitch(int warnSwitch) {
		this.warnSwitch = warnSwitch;
	}
	public int getSerious() {
		return serious;
	}
	public void setSerious(int serious) {
		this.serious = serious;
	}
	public int getImportant() {
		return important;
	}
	public void setImportant(int important) {
		this.important = important;
	}
	public int getGeneral() {
		return general;
	}
	public void setGeneral(int general) {
		this.general = general;
	}	
	public int getIntervalSwitch() {
		return intervalSwitch;
	}
	public void setIntervalSwitch(int intervalSwitch) {
		this.intervalSwitch = intervalSwitch;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}

	// 单例模式
	private volatile static MsgWarningConfig voiceWarningConfig;
	private MsgWarningConfig() {
		
	}
	public static MsgWarningConfig getMsgWarningConfig() {
		if(voiceWarningConfig == null){
			synchronized (MsgWarningConfig.class) {
				if (voiceWarningConfig == null) {
					voiceWarningConfig = new MsgWarningConfig();
				}
			}
		}	
		return voiceWarningConfig;
	}
}
