package com.zxyx.model;

import java.util.ArrayList;


/**
 * 告警信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */

public class WarningInfo {

	// 内部告警信息最大缓存数量  300条
	public final int MAX_COUNT = 300;
	// 内部告警信息列表
	private ArrayList<WarningInternal> warningInternals;
	// 声音告警设置
	private VoiceWarningConfig voiceWarningConfig;
	// 短信告警设置
	private MsgWarningConfig msgWarningConfig;

	public ArrayList<WarningInternal> getWarningInternals() {
		return warningInternals;
	}
	public void setWarningInternals(ArrayList<WarningInternal> warningInternals) {
		this.warningInternals = warningInternals;
	}
	public VoiceWarningConfig getVoiceWarningConfig() {
		return voiceWarningConfig;
	}
	public void setVoiceWarningConfig(VoiceWarningConfig voiceWarningConfig) {
		this.voiceWarningConfig = voiceWarningConfig;
	}
	public MsgWarningConfig getMsgWarningConfig() {
		return msgWarningConfig;
	}
	public void setMsgWarningConfig(MsgWarningConfig msgWarningConfig) {
		this.msgWarningConfig = msgWarningConfig;
	}


	// 单例模式
	private volatile static WarningInfo warningInfo;	
	private WarningInfo() {
		warningInternals = new ArrayList<WarningInternal>();
		voiceWarningConfig = VoiceWarningConfig.getVoiceWarningConfig();
		msgWarningConfig = MsgWarningConfig.getMsgWarningConfig();
	}
	public static WarningInfo getWarningInfo() {
		if(warningInfo == null){
			synchronized (WarningInfo.class) {
				if (warningInfo == null) {
					warningInfo = new WarningInfo();
				}
			}
		}	
		return warningInfo;
	}


}
