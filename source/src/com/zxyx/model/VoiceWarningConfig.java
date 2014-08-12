package com.zxyx.model;

/**
 * 声音告警设置
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */

public class VoiceWarningConfig {

	// 告警开关    0关闭  1打开
	private int warnSwitch = 1;
	// 严重告警    0未选  1选中
	private int serious = 1;
	// 重要告警    0未选  1选中
	private int important = 1;
	// 一般告警    0未选  1选中
	private int general = 1;	
	// 自动停止时间开关    0关闭  1打开
	private int stopTimeSwitch = 1;
	// 自动停止时间
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
	public int getStopTimeSwitch() {
		return stopTimeSwitch;
	}
	public void setStopTimeSwitch(int stopTimeSwitch) {
		this.stopTimeSwitch = stopTimeSwitch;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
	
	// 单例模式
	private volatile static VoiceWarningConfig voiceWarningConfig;
	private VoiceWarningConfig() {
		
	}
	public static VoiceWarningConfig getVoiceWarningConfig() {
		if(voiceWarningConfig == null){
			synchronized (VoiceWarningConfig.class) {
				if (voiceWarningConfig == null) {
					voiceWarningConfig = new VoiceWarningConfig();
				}
			}
		}	
		return voiceWarningConfig;
	}

}
