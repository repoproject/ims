package com.zxyx.model;


/**
 * 文件交换服务器1的信息
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class FileServer1Info extends ServerInfo {
	
	// 单例模式
	private volatile static FileServer1Info fileServer1Info;
	private FileServer1Info() {
		
	}
	public static FileServer1Info getFileServer1Info() {
		if(fileServer1Info == null){
			synchronized (FileServer1Info.class) {
				if (fileServer1Info == null) {
					fileServer1Info = new FileServer1Info();
				}
			}
		}	
		return fileServer1Info;
	}	
}
