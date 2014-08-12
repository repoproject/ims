package com.zxyx.model;


/**
 * 文件交换服务器2的信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class FileServer2Info extends ServerInfo {
	
	// 单例模式
	private volatile static FileServer2Info fileServer2Info;
	private FileServer2Info() {
		
	}
	public static FileServer2Info getFileServer2Info() {
		if(fileServer2Info == null){
			synchronized (FileServer2Info.class) {
				if (fileServer2Info == null) {
					fileServer2Info = new FileServer2Info();
				}
			}
		}	
		return fileServer2Info;
	}	
}
