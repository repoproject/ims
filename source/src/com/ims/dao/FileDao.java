/**
 * 
 */
package com.ims.dao;



import java.text.SimpleDateFormat;
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
		//setDate(file);
		file.setMakeDate(new Date());
		file.setModifyDate(new Date());
		String sql = "insert into b_file(name,path,makedate,modifydate) values(?,?,?,?)";
		
		String dateStr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.getMakeDate());
		String dateStr2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.getModifyDate());
		
		//int re = DBUtil.execute(sql, file.getName(),file.getPath(),file.getMakeDate(),file.getMakeDate());
		int re = DBUtil.execute(sql, file.getName(),file.getPath(),dateStr1,dateStr2);
		return re;
	}
	
	/**
	 * 暂时不做任何数据上的更新，因为保存路径和文件名称不会改变
	 * @return
	 */
	public int update(FileReport file){
		file.setModifyDate(new Date());
		String sql = "update b_file set modifydate=? where name=?";
		//int re = DBUtil.execute(sql, file.getModifyDate(),file.getName());
	
		String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.getModifyDate());

		int re = DBUtil.execute(sql,dateStr,file.getName());
		return re;
	}
	
	/**
	 * 保存或者更新
	 * @param file
	 * @return
	 */
	public int saveOrUpdate(FileReport file){
		int re = 0;
		if(exist(file.getName())){
			re = update(file);
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
