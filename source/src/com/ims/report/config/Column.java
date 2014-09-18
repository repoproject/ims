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
	private boolean sum;
	private Object value;
	
	public Column(int index,String type,Object value,boolean sum){
		this.index = index;
		this.type = type;
		this.value = value;
		this.sum = sum;
	}

	public int getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}
	
	public boolean getSum(){
		return sum;
	}

	public Object getValue() {
		return value;
	}
}
