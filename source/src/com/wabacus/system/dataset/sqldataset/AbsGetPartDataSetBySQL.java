/* 
 * Copyright (C) 2010---2013 星星(wuweixing)<349446658@qq.com>
 * 
 * This file is part of Wabacus 
 * 
 * Wabacus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wabacus.system.dataset.sqldataset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.config.database.type.SQLSERVER2K;
import com.wabacus.config.database.type.SQLSERVER2K5;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.CacheDataBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.ListReportAssistant;
import com.wabacus.system.assistant.ReportAssistant;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.component.application.report.abstractreport.configbean.statistic.StatisticItemAndDataSetBean;
import com.wabacus.util.Consts_Private;
import com.wabacus.util.Tools;

public abstract class AbsGetPartDataSetBySQL extends AbsGetAllDataSetBySQL
{
    private String sqlKernel;

    public int getRecordcount(ReportRequest rrequest,AbsReportType reportTypeObj,ReportDataSetValueBean svbean)
    {
        ReportBean rbean=reportTypeObj.getReportBean();
        String datasetid=svbean.getId();
        datasetid=datasetid==null?"":datasetid.trim();
        sqlKernel=rrequest.getStringAttribute(rbean.getId(),datasetid+"_DYN_SQL","");
        if("[NONE]".equals(sqlKernel)) return 0;
        if(sqlKernel.equals("")) sqlKernel=svbean.getSql_kernel();
        sqlKernel=ReportAssistant.getInstance().parseRuntimeSqlAndCondition(rrequest,rbean,svbean,sqlKernel,lstConditions,lstConditionsTypes);
        String sqlCount=Tools.replaceAll(svbean.getSqlCount(),Consts_Private.PLACEHOLDER_LISTREPORT_SQLKERNEL,sqlKernel);
        sqlCount=ListReportAssistant.getInstance().addColFilterConditionToSql(rrequest,rbean,svbean,sqlCount);
        int recordcount=0;
        ResultSet rsCount=null;
        if(rbean.getInterceptor()!=null)
        {
            Object obj=rbean.getInterceptor().beforeLoadData(rrequest,rbean,svbean,sqlCount);
            if(obj==null) return -1;
            if(obj instanceof List)
            {
                List lst=(List)obj;
                if(lst.size()==0)
                {
                    recordcount=0;
                }else
                {
                    if(!(lst.get(0) instanceof Integer))
                    {
                        throw new WabacusRuntimeException("拦截器返回的记录数不是合法数字，必须返回Integer类型的数据");
                    }
                    recordcount=(Integer)lst.get(0);
                    if(recordcount<0) recordcount=0;
                }
                return recordcount;
            }else if(obj instanceof String)
            {
                sqlCount=(String)obj;
            }else if(obj instanceof ResultSet)
            {
                rsCount=(ResultSet)obj;
            }else
            {
                throw new WabacusRuntimeException("执行报表"+rbean.getPath()+"的加载数据拦截器失败，返回的数据类型"+obj.getClass().getName()+"不合法");
            }
        }
        try
        {
            if(rsCount==null)
            {
                rsCount=executeQuery(rrequest,rbean,svbean.getDatasource(),sqlCount);
            }
            rsCount.next();
            recordcount=rsCount.getInt(1);
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("从数据库取数据时执行SQL："+sqlCount+"失败",e);
        }finally
        {
            try
            {
                if(rsCount!=null) rsCount.close();
            }catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
        return recordcount;
    }
    
    public Object getDataSet(ReportRequest rrequest,AbsReportType reportTypeObj,ReportDataSetValueBean svbean,List lstReportData)
    {
        ReportBean rbean=reportTypeObj.getReportBean();
        if(sqlKernel==null||sqlKernel.trim().equals(""))
        {
            String datasetid=svbean.getId();
            datasetid=datasetid==null?"":datasetid.trim();
            sqlKernel=rrequest.getStringAttribute(rbean.getId(),datasetid+"_DYN_SQL","");
            if("[NONE]".equals(sqlKernel)) return null;
            if(sqlKernel.equals(""))
            {
                sqlKernel=svbean.getSql_kernel();
            }
            sqlKernel=ReportAssistant.getInstance().parseRuntimeSqlAndCondition(rrequest,rbean,svbean,sqlKernel,lstConditions,lstConditionsTypes);
        }
        CacheDataBean cdb=rrequest.getCdb(rbean.getId());
        if(cdb.getPagecount()<=0) return null;
        int[] startEndRownumArr=cdb.getStartEndRownumOfDataset(svbean.getGuid());
        if(startEndRownumArr==null||startEndRownumArr.length!=2||startEndRownumArr[0]<0||startEndRownumArr[1]<=0||startEndRownumArr[0]==startEndRownumArr[1]) return null;
        String sql=null;
        try
        {
            sql=svbean.getSplitpage_sql();
            ColBean cbeanClickOrderby=ListReportAssistant.getInstance().getClickOrderByCbean(rrequest,rbean);
            if(cbeanClickOrderby!=null&&cbeanClickOrderby.isMatchDataSet(svbean))
            {
                String[] orderbys=(String[])rrequest.getAttribute(rbean.getId(),"ORDERBYARRAY");
                sql=rrequest.getDbType(svbean.getDatasource()).constructSplitPageSql(svbean,orderbys[0]+" "+orderbys[1]);
            }
            sql=Tools.replaceAll(sql,Consts_Private.PLACEHOLDER_LISTREPORT_SQLKERNEL,sqlKernel);
            sql=ListReportAssistant.getInstance().addColFilterConditionToSql(rrequest,rbean,svbean,sql);
            sql=Tools.replaceAll(sql,"%START%",String.valueOf(startEndRownumArr[0]));
            sql=Tools.replaceAll(sql,"%END%",String.valueOf(startEndRownumArr[1]));
            sql=Tools.replaceAll(sql,"%PAGESIZE%",String.valueOf(startEndRownumArr[1]-startEndRownumArr[0]));
            if(rbean.getInterceptor()!=null)
            {
                Object obj=rbean.getInterceptor().beforeLoadData(rrequest,rbean,svbean,sql);
                if(obj==null) return null;
                if(obj instanceof List||obj instanceof ResultSet) return obj;
                if(!(obj instanceof String))
                {
                    throw new WabacusRuntimeException("执行报表"+rbean.getPath()+"的加载数据拦截器失败，返回的数据类型"+obj.getClass().getName()+"不合法");
                }
                sql=(String)obj;
            }
            return this.executeQuery(rrequest,rbean,svbean.getDatasource(),sql);
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("从数据库取数据失败，执行SQL："+sql+"失败",e);
        }
    }
    
    public Object getStatisticDataSet(ReportRequest rrequest,AbsReportType reportObj,ReportDataSetValueBean svbean,Object typeObj,String sql,boolean isStatisticForOnePage)
    {
        if(lstConditions!=null) lstConditions.clear();
        if(lstConditionsTypes!=null) lstConditionsTypes.clear();
        ReportBean rbean=reportObj.getReportBean();
        String datasetid=svbean.getId();
        datasetid=datasetid==null?"":datasetid.trim();
        String sqlKernel=rrequest.getStringAttribute(rbean.getId(),datasetid+"_DYN_SQL","");
        if("[NONE]".equals(sqlKernel)) return null;
        if(sqlKernel.equals(""))
        {
            sqlKernel=svbean.getSql_kernel();
        }
        sqlKernel=ReportAssistant.getInstance().parseRuntimeSqlAndCondition(rrequest,rbean,svbean,sqlKernel,lstConditions,lstConditionsTypes);
        String sqlTmp=svbean.getSplitpage_sql();
        sqlTmp=Tools.replaceAll(sqlTmp,Consts_Private.PLACEHOLDER_LISTREPORT_SQLKERNEL,sqlKernel);
        sqlTmp=ListReportAssistant.getInstance().addColFilterConditionToSql(rrequest,rbean,svbean,sqlTmp);
        CacheDataBean cdb=rrequest.getCdb(rbean.getId());
        if(cdb.getPagecount()<=0) return null;//没有记录
        int[] startEndRownumArr=cdb.getStartEndRownumOfDataset(svbean.getGuid());
        if(startEndRownumArr==null||startEndRownumArr.length!=2||startEndRownumArr[0]<0||startEndRownumArr[1]<=0||startEndRownumArr[0]==startEndRownumArr[1]) return null;
        if(rrequest.getDbType(svbean.getDatasource()) instanceof SQLSERVER2K||rrequest.getDbType(svbean.getDatasource()) instanceof SQLSERVER2K5)
        {
            String sqlTmp2=Tools.removeBracketAndContentInside(sqlTmp,true);
            sqlTmp2=Tools.replaceAll(sqlTmp2,"  "," ");
            if(sqlTmp2.toLowerCase().indexOf("order by")>0)
            {
                int idx_orderby=sqlTmp.toLowerCase().lastIndexOf("order by");
                sqlTmp=sqlTmp.substring(0,idx_orderby);
            }
        }
        sql=Tools.replaceAll(sql,StatisticItemAndDataSetBean.STATISQL_PLACEHOLDER,sqlTmp);
        sql=Tools.replaceAll(sql,"%START%",String.valueOf(startEndRownumArr[0]));
        sql=Tools.replaceAll(sql,"%END%",String.valueOf(startEndRownumArr[1]));
        sql=Tools.replaceAll(sql,"%PAGESIZE%",String.valueOf(startEndRownumArr[1]-startEndRownumArr[0]));
        if(rbean.getInterceptor()!=null)
        {
            Object obj=rbean.getInterceptor().beforeLoadData(rrequest,rbean,typeObj,sql);
            if(!(obj instanceof String)) return obj;
            sql=(String)obj;
        }
        try
        {
            return this.executeQuery(rrequest,rbean,svbean.getDatasource(),sql);
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("加载报表"+rbean.getPath()+"的统计数据时执行"+sql+"失败",e);
        }
    }    
}
