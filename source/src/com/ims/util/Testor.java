package com.ims.util;

import org.apache.log4j.Logger;

public class Testor {
	private static Logger logger = Logger.getLogger(Testor.class) ;
	public static void main(String[] argc){
		System.out.println("test sysout");
		logger.info("log4j");
	}
}
