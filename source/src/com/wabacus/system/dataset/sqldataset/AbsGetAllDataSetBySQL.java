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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.config.database.type.AbsDatabaseType;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.CacheDataBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.ListReportAssistant;
import com.wabacus.system.assistant.ReportAssistant;
import com.wabacus.system.component.application.report.abstractreport.AbsListReportType;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.component.application.report.abstractreport.configbean.statistic.StatisticItemAndDataSetBean;
import com.wabacus.system.dataset.ISqlDataSet;
import com.wabacus.system.datatype.IDataType;
import com.wabacus.util.Consts_Private;
import com.wabacus.util.Tools;

public abstract class AbsGetAllDataSetBySQL implements ISqlDataSet
{
    private static Log log=LogFactory.getLog(AbsGetAllDataSetBySQL.class);

    protected List<String> lstConditions;

    protected List<IDataType> lstConditionsTypes;

    public int getRecordcount(ReportRequest rrequest,AbsReportType reportTypeObj,ReportDataSetValueBean svbean)
    {
        return 0;
    }

    public Object getColFilterDataSet(ReportRequest rrequest,ColBean filterColBean,ReportDataSetValueBean datasetbean,
            Map<String,List<String>> mSelectedFilterValues)
    {
        String sql=datasetbean.getFilterdata_sql();
        ReportBean rbean=filterColBean.getReportBean();
        if(sql==null||sql.trim().equals(""))
        {
            throw new WabacusRuntimeException("没有取到报表"+rbean.getPath()+"要获取过滤数据的SQL语句");
        }
        if(filterColBean.getColumn()==null||filterColBean.getColumn().trim().equals("")) return null;
        sql=Tools.replaceAll(sql,"%FILTERCOLUMN%",filterColBean.getColumn());
        if(mSelectedFilterValues!=null&&mSelectedFilterValues.size()>0)
        {
            sql=ListReportAssistant.getInstance().addColFilterConditionToSql(rrequest,rbean,datasetbean,sql);
        }else
        {
            sql=Tools.replaceAll(sql,Consts_Private.PLACEHODER_FILTERCONDITION,"");
        }
        return getDataSet(rrequest,rbean,datasetbean,rrequest.getAttribute(rbean.getId()+"_WABACUS_FILTERBEAN"),sql);
    }
    
    public Object getDataSet(ReportRequest rrequest,AbsReportType reportTypeObj,ReportDataSetValueBean datasetbean,List lstReportData)
    {
        ReportBean rbean=reportTypeObj.getReportBean();
        String sql=datasetbean.getValue();
        String datasetid=datasetbean.getId();
        datasetid=datasetid==null?"":datasetid.trim();
        if(reportTypeObj instanceof AbsListReportType)
        {
            String sqlKernel=rrequest.getStringAttribute(rbean.getId(),datasetid+"_DYN_SQL","");
            if("[NONE]".equals(sqlKernel)) return null;
            if(sqlKernel.equals("")) sqlKernel=datasetbean.getSql_kernel();
            sql=Tools.replaceAll(datasetbean.getSqlWithoutOrderby(),Consts_Private.PLACEHOLDER_LISTREPORT_SQLKERNEL,sqlKernel);
            ColBean cbeanClickOrderby=ListReportAssistant.getInstance().getClickOrderByCbean(rrequest,rbean);
            if(cbeanClickOrderby!=null&&cbeanClickOrderby.isMatchDataSet(datasetbean))
            {
                String[] orderbys=(String[])rrequest.getAttribute(rbean.getId(),"ORDERBYARRAY");
                String dynorderby=ReportAssistant.getInstance().mixDynorderbyAndRowgroupCols(rbean.getSbean().getReportBean(),orderbys[0]+" "+orderbys[1]);
                if(sql.indexOf("%orderby%")<0)
                {
                    sql=sql+" order by "+dynorderby;
                }else
                {
                    sql=Tools.replaceAll(sql,"%orderby%"," order by "+dynorderby);
                }
            }else
            {
                String ordertmp=datasetbean.getOrderby();
                if(ordertmp==null) ordertmp="";
                if(!ordertmp.trim().equals(""))
                {
                    ordertmp=" order by "+ordertmp;
                }
                sql=Tools.replaceAll(sql,"%orderby%",ordertmp);
            }
        }else
        {
            String sqlTmp=rrequest.getStringAttribute(rbean.getId(),datasetid+"_DYN_SQL","");
            if("[NONE]".equals(sql)) return null;
            if(!sqlTmp.equals("")) sql=sqlTmp;
        }
        if(datasetbean.isDependentDataSet())
        {
            String realConExpress=datasetbean.getRealDependsConditionExpression(lstReportData);
            if(realConExpress==null||realConExpress.trim().equals(""))
            {
                sql=ReportAssistant.getInstance().removeConditionPlaceHolderFromSql(rbean,sql,ReportDataSetValueBean.dependsConditionPlaceHolder);
            }else
            {
                sql=Tools.replaceAll(sql,ReportDataSetValueBean.dependsConditionPlaceHolder,realConExpress);
            }
        }
        return getDataSet(rrequest,rbean,datasetbean,datasetbean,sql);
    }

    public Object getDataSet(ReportRequest rrequest,ReportBean rbean,ReportDataSetValueBean datasetbean,Object typeObj,String sql)
    {
        try
        {
            if(lstConditions!=null) lstConditions.clear();
            if(lstConditionsTypes!=null) lstConditionsTypes.clear();
            sql=ReportAssistant.getInstance().parseRuntimeSqlAndCondition(rrequest,rbean,datasetbean,sql,lstConditions,lstConditionsTypes);
            sql=ListReportAssistant.getInstance().addColFilterConditionToSql(rrequest,rbean,datasetbean,sql);
            if(rbean.getInterceptor()!=null&&typeObj!=null)
            {
                Object obj=rbean.getInterceptor().beforeLoadData(rrequest,rbean,typeObj,sql);
                if(!(obj instanceof String)) return obj;
                sql=(String)obj;
            }
            return executeQuery(rrequest,rbean,datasetbean.getDatasource(),sql);
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("从数据库取数据失败，执行SQL："+sql+"抛出异常",e);
        }
    }

    public Object getDataSet(ReportRequest rrequest,ReportBean reportbean,Object typeObj,String sql,List<ConditionBean> lstConditionBeans,String datasource)
    {
        try
        {
            if(lstConditions!=null) lstConditions.clear();
            if(lstConditionsTypes!=null) lstConditionsTypes.clear();
            sql=ReportAssistant.getInstance().addDynamicConditionExpressionsToSql(rrequest,reportbean,null,sql,lstConditionBeans,lstConditions,
                    lstConditionsTypes);
            if(reportbean.getInterceptor()!=null&&typeObj!=null)
            {
                Object obj=reportbean.getInterceptor().beforeLoadData(rrequest,reportbean,typeObj,sql);
                if(!(obj instanceof String)) return obj;
                sql=(String)obj;
            }
            if(datasource==null||datasource.trim().equals("")) datasource=reportbean.getSbean().getDatasource();
            return executeQuery(rrequest,reportbean,datasource,sql);
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("从数据库取数据失败，执行SQL："+sql+"抛出异常",e);
        }
    }
    
    public Object getStatisticDataSet(ReportRequest rrequest,AbsReportType reportObj,ReportDataSetValueBean svbean,Object typeObj,String sql,boolean isStatisticForOnePage)
    {
        try
        {
            if(lstConditions!=null) lstConditions.clear();
            if(lstConditionsTypes!=null) lstConditionsTypes.clear();
            ReportBean rbean=reportObj.getReportBean();
            String sqlTmp=svbean.getSqlWithoutOrderby();
            sqlTmp=Tools.replaceAll(sqlTmp,"%orderby%","");
            sqlTmp=Tools.replaceAll(sqlTmp,Consts_Private.PLACEHOLDER_LISTREPORT_SQLKERNEL,svbean.getSql_kernel());
            sqlTmp=ReportAssistant.getInstance().parseRuntimeSqlAndCondition(rrequest,rbean,svbean,sqlTmp,lstConditions,lstConditionsTypes);
            sqlTmp=ListReportAssistant.getInstance().addColFilterConditionToSql(rrequest,rbean,svbean,sqlTmp);
            CacheDataBean cdb=rrequest.getCdb(rbean.getId());
            if(svbean.isDependentDataSet())
            {
                List lstReportData=null;
                if(!cdb.isLoadAllReportData()&&!isStatisticForOnePage)
                {
                    lstReportData=(List)rrequest.getAttribute(rbean.getId()+"wx_all_data_tempory");
                    if(lstReportData==null)
                    {
                        lstReportData=ReportAssistant.getInstance().loadReportDataSet(rrequest,reportObj,true);
                        rrequest.setAttribute(rbean.getId()+"wx_all_data_tempory",lstReportData);
                    }
                }else
                {
                    lstReportData=reportObj.getLstReportData();
                }
                String realConExpress=svbean.getRealDependsConditionExpression(lstReportData);
                if(realConExpress==null||realConExpress.trim().equals(""))
                {
                    sqlTmp=ReportAssistant.getInstance().removeConditionPlaceHolderFromSql(rbean,sqlTmp,ReportDataSetValueBean.dependsConditionPlaceHolder);
                }else
                {
                    sqlTmp=Tools.replaceAll(sqlTmp,ReportDataSetValueBean.dependsConditionPlaceHolder,realConExpress);
                }
            }
            sql=Tools.replaceAll(sql,StatisticItemAndDataSetBean.STATISQL_PLACEHOLDER,sqlTmp);
            if(rbean.getInterceptor()!=null&&typeObj!=null)
            {
                Object obj=rbean.getInterceptor().beforeLoadData(rrequest,rbean,typeObj,sql);
                if(!(obj instanceof String)) return obj;
                sql=(String)obj;
            }
            return executeQuery(rrequest,rbean,svbean.getDatasource(),sql);
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("从数据库取数据失败，执行SQL："+sql+"抛出异常",e);
        }
    }
    
    protected ResultSet executeQuery(ReportRequest rrequest,ReportBean rbean,String datasource,String sql) throws SQLException
    {
        if(Config.show_sql)
        {
            log.info("Execute sql: "+sql);
        }
        if(this.isPreparedStatement())
        {
            PreparedStatement pstmt=rrequest.getConnection(datasource).prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            if(lstConditions.size()>0)
            {
                AbsDatabaseType dbtype=rrequest.getDbType(datasource);
                for(int j=0;j<lstConditions.size();j++)
                {
                    if(Config.show_sql)
                    {
                        log.info("param"+(j+1)+"="+lstConditions.get(j));
                    }
                    lstConditionsTypes.get(j).setPreparedStatementValue(j+1,lstConditions.get(j),pstmt,dbtype);
                }

            }
            rrequest.addUsedStatement(pstmt);
            return pstmt.executeQuery();
        }else
        {
            Statement stmt=rrequest.getConnection(datasource).createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rrequest.addUsedStatement(stmt);
            return stmt.executeQuery(sql);
        }
    }
    
    protected abstract boolean isPreparedStatement();
}
