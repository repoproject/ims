/**
 * 
 */
package com.ims.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ims.util.DBUtil;
import com.ims.util.DateTimeUtil;

/**
 * @author ChengNing
 * @date   2014-9-25
 */
public class ReportDao {
	
	/**
	 * 保存R表数据
	 * @param data
	 * @return
	 */
	public int saveR(List<Object> data){
		return save(data, 1);
	}
	
	/**
	 * 保存discard表数据
	 * @param data
	 * @return
	 */
	public int saveDiscard(List<Object> data){
		return save(data, 2);
	}
	
	/**
	 * 备份validate表报数据
	 * data中的数据map中的key值请从excel中对应的列名称中取得
	 * @param data
	 * @return
	 */
	public int saveValidation(List<Object> data){
		return save(data, 3);
	}
	
	/**
	 * 通用保存，策略方法
	 * @param data
	 * @param type
	 * @return
	 */
	private int save(List<Object> data,int type){
		if(data == null || data.size() == 0)
			return 0;
		Map<Object, Object> rowData = new HashMap<Object, Object>();
		String sql = "";
		int result = 0;
		int re = 0;
		for(Object obj : data){
			rowData = (HashMap)obj;
			if(type == 1)
			    re = saveR(rowData);
			else if(type == 2)
			    re = saveDiscard(rowData);
			else if(type == 3)
			    re = saveValidation(rowData);
			
			result+=re;
		}
		return result;
	}
	
	/**
	 * 
	 * @param rowData
	 * @return
	 */
	private int saveR(Map<Object, Object> rowData){
		Date now = new Date();
		String sql = "insert into b_report_r " +
		" (flag,rType,catno,machineNo,catname," +
		"  opening,inVendor,inInterlab,inSponsor,inCharges,inTotal," +
		"  outTrialTest,outValidation,outDiscard,outIntelLab,outSponsor,outOther,outTotal,closing," +
		"  CNY,USD,SGD,EUR,GBP,localprice,totalAmount,remark," +
		"  qtyIn,qtyOut,checkQty,checkAmt,pqtyIn,pqtyOut,cqtyIn,cqtyOut," +
		"  makedate,operator) " +
		" values (?,?,?,?,?," +
		" ?,?,?,?,?,?," +
		" ?,?,?,?,?,?,?,?," +
		" ?,?,?,?,?,?,?,?," +
		" ?,?,?,?,?,?,?,?," +
		" ?,?)";
        int re = DBUtil.execute(sql, 
			DateTimeUtil.toDateString(now, "yyyyMM"),rowData.get("rType"),rowData.get("catno"),rowData.get("machineNo"),rowData.get("catname"),
			
			rowData.get("opening"),rowData.get("inVendor"),rowData.get("inInterlab"),rowData.get("inSponsor"),
			rowData.get("inCharges"),rowData.get("inTotal"),
			
			rowData.get("outTrialTest"),rowData.get("outValidation"),rowData.get("outDiscard"),rowData.get("outIntelLab"),
			rowData.get("outSponsor"),rowData.get("outOther"),rowData.get("outTotal"),rowData.get("closing"),
			
			rowData.get("CNY"),
			rowData.get("USD"),
			rowData.get("SGD"),
			rowData.get("EUR"),
			rowData.get("GBP"),
			rowData.get("localprice"),
			rowData.get("totalAmount"),
			rowData.get("remark"),
			
			rowData.get("qtyIn"),
			rowData.get("qtyOut"),
			rowData.get("checkQty"),
			rowData.get("checkAmt"),
			rowData.get("pqtyIn"),
			rowData.get("pqtyOut"),
			rowData.get("cqtyIn"),
			rowData.get("cqtyOut"),
			
			now,
			"001");
        return re;
	}
	
	/**
	 * 
	 * @param rowData
	 * @return
	 */
	private int saveDiscard(Map<Object, Object> rowData){
		Date now = new Date();
		String sql = "insert into b_report_discard " +
		" (flag,rownum,outDate,description,catno,price,currency,expiredate,qty,totalAmnt,rootCause,section,producer,dealer,catFrom,makedate,operator) " +
		" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int re = DBUtil.execute(sql, 
			DateTimeUtil.toDateString(now, "yyyyMM"),
			rowData.get("rownum"),
			rowData.get("outDate"),
			rowData.get("description"),
			rowData.get("catno"),
			rowData.get("price"),
			rowData.get("currency"),
			rowData.get("expiredate"),
			rowData.get("qty"),
			rowData.get("totalAmnt"),
			rowData.get("rootCause"),
			rowData.get("section"),
			rowData.get("producer"),
			rowData.get("dealer"),
			rowData.get("catFrom"),
			now,
			"001");
        return re;
	}
	
	/**
	 * 
	 * @param rowData
	 * @return
	 */
	private int saveValidation(Map<Object, Object> rowData){
		Date now = new Date();
		String sql = "insert into b_report_validation " +
		" (flag,section,outdate,description,rType,catname,qty,priceCNY,totalCostCNY,totalCostUSD,projectCode,makedate,operator) " +
		" values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int re = DBUtil.execute(sql, 
			DateTimeUtil.toDateString(now, "yyyyMM"),
			rowData.get("section"),
			rowData.get("outdate"),
			rowData.get("description"),
			rowData.get("rType"),
			rowData.get("catname"),
			rowData.get("qty"),
			rowData.get("priceCNY"),
			rowData.get("totalCostCNY"),
			rowData.get("totalCostUSD"),
			rowData.get("projectCode"),
			now,
			"001");
        return re;
	}

}
