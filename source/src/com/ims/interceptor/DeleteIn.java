/**
 * 
 */
package com.ims.interceptor;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ims.util.DBUtil;
import com.ims.util.InOutRule;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditableReportEditDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportDeleteDataBean;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;

/**
 * @author ChengNing
 * @date 2014-8-29
 */
public class DeleteIn extends AbsInterceptorDefaultAdapter {

	private static Logger log = Logger.getLogger(DeleteIn.class);

	public int doSavePerRow(ReportRequest rrequest, ReportBean rbean,
			Map<String, String> row, Map<String, String> mParamValues,
			AbsEditableReportEditDataBean editbean) {
		if (editbean instanceof EditableReportDeleteDataBean) {

			// 入库时间
			String strindate = "";
			// 上月R统计时间
			String strRDate = "";
			
			// 货号
			String strcatno = "";
			// 批号
			String strbatchno = "";
			//单价
			String strprice = "";
			
			
			try {
								
				// 入库时间
				strindate = row.get("inDate").trim();
				// 调用函数获取上个月R统计时间
				Date Rrundate = InOutRule.getRrundate();

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				strRDate = format.format(Rrundate);
			} catch (Exception e) {
				log.error("获得入库时间失败:" + row.get("indate"));
			}

			// 判断入库时间是否晚于上次R统计，晚于才能删除，否则不能删除
			if (!InOutRule.indateIsOK(strindate)) {
				rrequest.getWResponse().getMessageCollector().alert(
						"删除入库记录的入库时间[" + strindate + "]必须晚于上月统计库存的时间["
						+ strRDate + "]！", null,
						false);
				return WX_RETURNVAL_TERMINATE;
			}

			// 如果库存小于当前入库的数量，说明当前入库的已经存在出库，则不允许删除入库
			try {
				int delNum = Integer.parseInt(row.get("num"));
				
				// 货号
				strcatno = row.get("catno").trim();
				// 名称
				strbatchno = row.get("batchno").trim();
				//单价
				strprice = row.get("price").trim();
				
				
				//获取库存函数
				int total = InOutRule.getcatTotal(strcatno, strbatchno, strprice);
				
				// TODO:从数据库中取值
				if (total < delNum) {
					rrequest.getWResponse().getMessageCollector().alert(
							"当前入库已经存在被出库的情况，不允许删除", null, false);
					return WX_RETURNVAL_TERMINATE;
				} else {
					super.doSavePerRow(rrequest, rbean, row, mParamValues,
							editbean);
				}
			} catch (Exception e) {
				log.error("获得库存信息失败:" + row.get("num")+e.toString());
				return WX_RETURNVAL_TERMINATE;
			}
		}
		return WX_RETURNVAL_SUCCESS;
	}

	
		
}
