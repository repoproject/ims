/**
 * 
 */
package com.ims.report.config;

import java.util.List;

/**
 * @author ChengNing
 * @date   2014-9-6
 */
public class Sheet {
	private String name;
	private int dataRowNum;
	private int dataStartNum;
	private int footerRowNum;
	private String className;
	private String sql;
	private List<Column> cols;
	
	public Sheet(){}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDataRowNum() {
		return dataRowNum;
	}

	public void setDataRowNum(int dataRowNum) {
		this.dataRowNum = dataRowNum;
	}

	public int getDataStartNum() {
		return dataStartNum;
	}

	public void setDataStartNum(int dataStartNum) {
		this.dataStartNum = dataStartNum;
	}

	public int getFooterRowNum() {
		return footerRowNum;
	}

	public void setFooterRowNum(int footerRowNum) {
		this.footerRowNum = footerRowNum;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<Column> getCols() {
		return cols;
	}

	public void setCols(List<Column> cols) {
		this.cols = cols;
	}
	
	
	
}
