/**
 * 
 */
package com.ims.service;

/**
 * @author ChengNing
 * @date   2014年8月14日
 */
public class SheetR {
	private String no;
	private String machine;
	private String name;
	private int opening;
	private int qtyIn;
	private int qtyOut;
	private int closeing; 
	private double unitPrice;//单价CNY
	private double price;   //价格
	private String unit;    //单位
	private double totalAmount;
	private String remark;
	
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getMachine() {
		return machine;
	}
	public void setMachine(String machine) {
		this.machine = machine;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOpening() {
		return opening;
	}
	public void setOpening(int opening) {
		this.opening = opening;
	}
	public int getQtyIn() {
		return qtyIn;
	}
	public void setQtyIn(int qtyIn) {
		this.qtyIn = qtyIn;
	}
	public int getQtyOut() {
		return qtyOut;
	}
	public void setQtyOut(int qtyOut) {
		this.qtyOut = qtyOut;
	}
	public int getCloseing() {
		return closeing;
	}
	public void setCloseing(int closeing) {
		this.closeing = closeing;
	}
	public double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
}
