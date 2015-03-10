package com.ims.rule;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ims.common.TaskData;
import com.ims.util.DBUtil;
import com.ims.util.DateTimeUtil;

public class InOutRule {

    private static Logger log = Logger.getLogger(InOutRule.class);
    /***
     *
     * 入库操作时判断是否存在该试剂/耗材
     *
     * @param strcatno
     *            货号
     * @param strcatname
     *            名称
     * @return true代表库中有该试剂/耗材，false代表库中不存在该试剂/耗材
     */
    public static boolean catIsExit(String strcatno, String strcatname) {

        // 判断货号和名称是否存在的sql
        String sql = "SELECT DISTINCT id FROM d_catcode where catno=?  and  catname=? ";

        // 执行SQL
        List<Object> list = DBUtil.query(sql, strcatno, strcatname);
        // 试剂耗材库中不存在该货号
        if (list.size() < 1) {

            return false;
        } else {
            return true;
        }
    }

    /***
     * 判断出入库时间是否晚于上月统计的时间，如果早或者等于则不能进行增、删、改
     *
     * @param strindate  yyyy-mm-dd
     *            出入库时间
     * @return false代表早于等于上月统计的时间，true代表晚于上月的统计时间
     */
    public static boolean indateIsOK(String strinoutdate) {
        // 本次出入库时间
        Date indate = DateTimeUtil.getDate(strinoutdate);
        Date rundate = getRrundate(); //上次统计时间
        // 如果出入库时间比统计时间晚至少于1天则可以出入库，否则不行
        if (indate.after(rundate))
            return true;
        else
            return false;
    }

    /***
     * 获取上次R统计时间
     *
     */
    public static Date getRrundate() {
        TaskData taskData = new TaskData();
        return taskData.lastTaskDate();
    }
    /***
     * 获取指定货号、批号和单价的剩余库存
     * @param strcatno 货号
     * @param strbatchno 批号
     * @param strprice 单价
     * @return
     */
    public static double getcatTotal(String strcatno ,String strbatchno,	String strprice)
    {
        double itotal = 0.0;

        String strtotal ="";
        try{
        // 查询库库存信息
        String sql = "select total from b_cat where catno=? and batchno=? and price=?";
        strtotal = DBUtil.getOneValue(sql, strcatno, strbatchno,
                strprice);
        // 查询出结果
        itotal = Double.valueOf(strtotal);
        }
        catch(Exception e)
        {
            log.error("字符串转int失败:" + Double.valueOf(strtotal)+e.toString());
        }
        return itotal;
    }
    /***
     *
     * @param strcatno 货号
     * @param strbatchno 批号
     * @param strprice 单价
     * @return 获取指定货号、批号和单价的已经出库数量
     */
    public static double getoutTotal(String strcatno ,String strbatchno,	String strprice)
    {
        double itotal = 0.0;
        String strtotal = "";
        try {
            // 查询库库存信息
            String sql = "select sum(num) from b_out where catno=? and batchno=? and price=?";
            strtotal = DBUtil.getOneValue(sql, strcatno, strbatchno,
                    strprice);

            // 查询出结果
            itotal = Double.valueOf(strtotal);

        } catch (Exception e) {

            log.error("字符串转int失败:" + Double.valueOf(strtotal)+e.toString());
        }
        return itotal;
    }
    
    /***
    *
    * @param strcatno 货号
    * @param strbatchno 批号
    * @param strprice 单价
    * @param strperson 出库人
    * @return 获取指定货号、批号、单价和某个出库人的已经出库数量
    */
   public static double getoutTotalofPerson(String strcatno ,String strbatchno,String strprice,String strperson)
   {
       double itotal = 0;
       String strtotal = "";
       try {
           // 查询库库存信息
           String sql = "select sum(num) from b_out where catno=? and batchno=? and price=? and person=?";
           strtotal = DBUtil.getOneValue(sql, strcatno, strbatchno,
                   strprice,strperson);

           // 查询出结果
           itotal = Double.valueOf(strtotal);

       } catch (Exception e) {

           log.error("字符串转int失败:" + Double.valueOf(strtotal)+e.toString());
       }
       return itotal;
   }
    
    
    /***
    *
    * @param strcatno 货号
    * @param strbatchno 批号
    * @param strprice 单价
    * @return 获取指定货号、批号和单价的已经入库数量
    */
   public static double getinTotal(String strcatno ,String strbatchno,	String strprice)
   {
       double itotal = 0;
       String strtotal = "";
       try {
           // 查询库库存信息
           String sql = "select sum(num) from b_in where catno=? and batchno=? and price=?";
           strtotal = DBUtil.getOneValue(sql, strcatno, strbatchno,
                   strprice);

           // 查询出结果
           itotal = Double.valueOf(strtotal);

       } catch (Exception e) {

           log.error("字符串转int失败:" + Double.valueOf(strtotal)+e.toString());
       }
       return itotal;
   }
   
   /***
    * 判断该货号和批号的试剂耗材是否在库存表中存在
    * @param strcatno 货号
    * @param strbatchno 批号
    * @return 如果存在返回true，否则返回false
    */
   public static boolean IsExitBatchno(String strcatno ,String strbatchno)
   {
	   String strtotal ="";
	   double itotal=0;
	   try{
		// 查询库库存信息
           String sql = "select count(id) from b_cat where catno=? and batchno=? ";
           strtotal = DBUtil.getOneValue(sql, strcatno, strbatchno);

           // 查询出结果
           itotal = Double.valueOf(strtotal);
	   }
	   catch (Exception e) {

           log.error("字符串转int失败:" + Double.valueOf(strtotal)+e.toString());
       }
	   //不存在相同货号的试剂，需要提示
	   if(itotal<=0)

		   return false;
	   else
		   return true;

   }
}
