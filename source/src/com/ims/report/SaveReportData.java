/**
 * 
 */
package com.ims.report;

import java.util.List;

import org.apache.log4j.Logger;


/**
 * 用来保存报表数据，作为备份
 * @author ChengNing
 * @date   2014-9-25
 */
public class SaveReportData implements Runnable{
	
	public enum ReportType{
		R,Discard,Validation
	}
	
	private static Logger logger = Logger.getLogger(SaveReportData.class);
	
	private ReportType type;
	private List<Object> data;
	
	/**
	 * 
	 * @param type
	 * @param data
	 */
	public SaveReportData(ReportType type,List<Object> data){
		this.type = type;
		this.data = data;
	} 

	/**
	 * 
	 */
	@Override
	public void run() {
		try {
			switch (this.type) {
			case R:
				saveR();
				break;
			case Discard:
				saveDiscard();
				break;
			case Validation:
				saveValidation();
				break;
			default:
				logger.info("保存报表数据传入类型不对，找不对对应的保存方法");
				break;
			}
		} catch (Exception e) {
			logger.error("保存报表数据数据失败" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	private void saveR(){
		
	}
	
	/**
	 * 
	 */
	private void saveDiscard(){
		
	}
	
	/**
	 * 
	 */
	private void saveValidation(){
		
	}

}
