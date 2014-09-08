/**
 * 
 */
package com.ims.util;

import java.io.File;

/**
 * 系统工具
 * @author ChengNing
 * @date   2014-9-8
 */
public class Sys {
	
	/**
	 * 返回WebRoot\
	 * web应用根目录,通过获取class文件夹，然后取其祖父目录
	 * 注意web的目录结构是WebRoot/WEB_INF/class
	 * @return
	 */
	public static String serverRootPath(){
		String classPath = classRootPath();
		File classFile = new File(classPath);
		File serverRoot = classFile.getParentFile().getParentFile();
		if(serverRoot.exists())
			return serverRoot.getPath() + "\\";
		return null;
	}
	
	/**
	 * 返回classes/
	 * class跟目录，java的src目录或者WEB-INF下的class目录
	 * @return
	 */
	public static String classRootPath(){
		return Thread.currentThread().getContextClassLoader().getResource("").getPath();
	}
}
