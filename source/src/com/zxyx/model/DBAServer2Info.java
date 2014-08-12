package com.zxyx.model;


/**
 * 数据库管理服务器2的信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class DBAServer2Info extends ServerInfo {

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
	private volatile static DBAServer2Info dbaServer2Info;
	private DBAServer2Info() {
		
	}
	public static DBAServer2Info getDBAServer2Info() {
		if(dbaServer2Info == null){
			synchronized (DBAServer2Info.class) {
				if (dbaServer2Info == null) {
					dbaServer2Info = new DBAServer2Info();
				}
			}
		}	
		return dbaServer2Info;
	}	
}
