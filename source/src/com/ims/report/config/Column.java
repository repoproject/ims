/**
 * 
 */
package com.ims.report.config;

/**
 * @author ChengNing
 * @date   2014-9-6
 */
public class Column {
	private int index;
	private String type;
	private Object value;
	
	public Column(int index,String type,Object value){
		this.index = index;
		this.type = type;
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}
}
