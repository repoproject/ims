/**
 * 
 */
package com.ims.report;

import java.util.List;

import org.apache.log4j.Logger;

import com.ims.dao.ReportDao;


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
		logger.info("备份报表数据,类型" + this.type.toString());
		int re = 0;
		try {
			switch (this.type) {
			case R:
				re = saveR();
				break;
			case Discard:
				re = saveDiscard();
				break;
			case Validation:
				re = saveValidation();
				break;
			default:
				logger.info("保存报表数据传入类型不对，找不对对应的保存方法");
				break;
			}
		} catch (Exception e) {
			logger.error("保存报表数据数据失败" + e.getMessage());
			e.printStackTrace();
		}
		logger.info("共有数据" + this.data.size() + ",保存成功" + re);
	}
	
	/**
	 * 保存R数据
	 */
	private int saveR(){
		ReportDao dao = new ReportDao();
		int re = dao.saveR(this.data);
		return re;
	}
	
	/**
	 * 保存discard数据
	 */
	private int saveDiscard(){
		ReportDao dao = new ReportDao();
		int re = dao.saveDiscard(this.data);
		return re;
	}
	
	/**
	 * 保存validatation数据
	 */
	private int saveValidation(){
		ReportDao dao = new ReportDao();
	    int re = dao.saveValidation(this.data);
	    return re;
	}

}
