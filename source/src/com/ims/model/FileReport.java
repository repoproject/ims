/**
 * 
 */
package com.ims.model;

import java.util.Date;


/**
 * @author ChengNing
 * @date   2014年9月25日
 */
public class FileReport {
	private long id;
	private String name;
	private String path;
	private Date makeDate;
	private Date modifyDate;
	


	public FileReport(){}

	public FileReport(String name,String path){
		this.name = name;
		this.path = path;
		Date date = new Date();
		this.makeDate = date;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getMakeDate() {
		return makeDate;
	}

	public void setMakeDate(Date makeDate) {
		this.makeDate = makeDate;
	}
	
	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	
}
