package com.zxyx.model;


/**
 * 数据库管理服务器1的信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class DBAServer1Info extends ServerInfo {
	
	// 数据表空间使用率信息
	private TableSpaceInfo tableSpaceInfo;
	// 数据表空间使用率信息计数
	private int tableSpaceInfoCnt;
	
	public TableSpaceInfo getTableSpaceInfo() {
		return tableSpaceInfo;
	}
	public void setTableSpaceInfo(TableSpaceInfo tableSpaceInfo) {
		this.tableSpaceInfo = tableSpaceInfo;
	}
	public int getTableSpaceInfoCnt() {
		return tableSpaceInfoCnt;
	}
	public void setTableSpaceInfoCnt(int tableSpaceInfoCnt) {
		this.tableSpaceInfoCnt = tableSpaceInfoCnt;
	}



	// 单例模式
	private volatile static DBAServer1Info dbaServer1Info;
	private DBAServer1Info() {
		
	}
	public static DBAServer1Info getDBAServer1Info() {
		if(dbaServer1Info == null){
			synchronized (DBAServer1Info.class) {
				if (dbaServer1Info == null) {
					dbaServer1Info = new DBAServer1Info();
				}
			}
		}	
		return dbaServer1Info;
	}	
}
