/**
 * 
 */
package com.ims.dao;



import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.ims.model.FileReport;
import com.ims.util.DBUtil;

/**
 * 报表文件操作
 * @author ChengNing
 * @date   2014年9月25日
 */
public class FileDao {
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public int insert(FileReport file){
		setDate(file);
		String sql = "insert into b_file(name,path,makedate,modifydate) values(?,?,?,?)";
		int re = DBUtil.execute(sql, file.getName(),file.getPath(),file.getMakeDate(),file.getMakeDate());
		return re;
	}
	
	/**
	 * 暂时不做任何数据上的更新，因为保存路径和文件名称不会改变
	 * @return
	 */
	public int update(){
		//TODO:
		return 1;
	}
	
	/**
	 * 保存或者更新
	 * @param file
	 * @return
	 */
	public int saveOrUpdate(FileReport file){
		String sql = "";
		int re = 0;
		if(exist(file.getName())){
			re = update();
		}
		else{
			re = insert(file);
		}
		return re;
	}
	
	/**
	 * 文件不存在
	 * @param fileName
	 * @return
	 */
	private boolean exist(String fileName){
		String sql = "select count(*) from b_file where name = ?";
		String result = DBUtil.getOneValue(sql,fileName);
		if(StringUtils.equals(result, "0")){
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param file
	 */
	private void setDate(FileReport file){
		Date now = new Date();
		if(file.getMakeDate() == null)
			file.setMakeDate(now);
		if(file.getModifyDate() == null)
			file.setModifyDate(now);
		
	}
}
