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

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import oracle.jdbc.driver.OracleTypes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.config.database.type.AbsDatabaseType;
import com.wabacus.config.database.type.Oracle;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.CacheDataBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.ListReportAssistant;
import com.wabacus.system.assistant.ReportAssistant;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.system.component.application.report.abstractreport.AbsListReportType;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportDisplayBean;
import com.wabacus.system.dataset.ISqlDataSet;
import com.wabacus.system.datatype.IDataType;
import com.wabacus.system.datatype.VarcharType;
import com.wabacus.util.Tools;

public class GetDataSetByStoreProcedure implements ISqlDataSet
{
    private static Log log=LogFactory.getLog(GetDataSetByStoreProcedure.class);
    
    private boolean isLoadAllData;
    
    public void setLoadAllData(boolean isLoadAllData)
    {
        this.isLoadAllData=isLoadAllData;
    }

    public int getRecordcount(ReportRequest rrequest,AbsReportType reportTypeObj,ReportDataSetValueBean datasetbean)
    {
        ReportBean rbean=reportTypeObj.getReportBean();
        StringBuffer systemParamsBuf=new StringBuffer();
        
        String filterwhere=ListReportAssistant.getInstance().getFilterConditionExpression(rrequest,rbean,datasetbean);
        if(filterwhere!=null&&!filterwhere.trim().equals(""))
        {
            systemParamsBuf.append("{[(<filter_condition:"+filterwhere+">)]}");
        }
        systemParamsBuf.append("{[(<get_recordcount:true>)]}");
        systemParamsBuf.append("{[(<dynamic_selectcols:"+getDynamicSelectCols(rrequest,datasetbean)+">)]}");
        ResultSet rs=null;
        try
        {
            rs=doGetResultSet(rrequest,rbean,datasetbean,datasetbean,systemParamsBuf);
            
            
            
            
            
            
            
            
            
            
            //        }
            
            
            
            
            
            
            
            
            
            
            if(rs.next()) return rs.getInt(1);
        }catch(SQLException e)
        {
            log.warn("查询报表"+rbean.getPath()+"的报表记录数失败",e);
        }finally
        {
            try
            {
                if(rs!=null) rs.close();
            }catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
        return 0;
    }
    
    public Object getColFilterDataSet(ReportRequest rrequest,ColBean filterColBean,ReportDataSetValueBean datasetbean,
            Map<String,List<String>> mSelectedFilterValues)
    {
        ReportBean rbean=filterColBean.getReportBean();
        StringBuffer systemParamsBuf=new StringBuffer();
        systemParamsBuf.append("{[(<filter_column:"+filterColBean.getColumn()+">)]}");
        if(mSelectedFilterValues!=null&&mSelectedFilterValues.size()>0)
        {
            String filterwhere=ListReportAssistant.getInstance().getFilterConditionExpression(rrequest,rbean,datasetbean);
            if(filterwhere!=null&&!filterwhere.trim().equals(""))
            {
                systemParamsBuf.append("{[(<filter_condition:"+filterwhere+">)]}");
            }
        }
        return doGetResultSet(rrequest,rbean,datasetbean,rrequest.getAttribute(rbean.getId()+"_WABACUS_FILTERBEAN"),systemParamsBuf);
    }

    public Object getDataSet(ReportRequest rrequest,AbsReportType reportTypeObj,ReportDataSetValueBean datasetbean,List lstReportData)
    {
        ReportBean rbean=reportTypeObj.getReportBean();
        StringBuffer systemParamsBuf=new StringBuffer();
        
        if(reportTypeObj instanceof AbsListReportType)
        {
            addRowgroupColsToParams(rbean,systemParamsBuf);
            String[] orderbys=(String[])rrequest.getAttribute(rbean.getId(),"ORDERBYARRAY");
            if(orderbys!=null&&orderbys.length==2)
            {
                systemParamsBuf.append("{[(<dynamic_orderby:"+orderbys[0]+" "+orderbys[1]+">)]}");
            }
            String filterwhere=ListReportAssistant.getInstance().getFilterConditionExpression(rrequest,rbean,datasetbean);
            if(filterwhere!=null&&!filterwhere.trim().equals(""))
            {//有列过滤条件
                systemParamsBuf.append("{[(<filter_condition:"+filterwhere+">)]}");
            }
            systemParamsBuf.append("{[(<dynamic_selectcols:"+getDynamicSelectCols(rrequest,datasetbean)+">)]}");
        }
        CacheDataBean cdb=rrequest.getCdb(rbean.getId());
        int pagesize=isLoadAllData?-1:cdb.getPagesize();
        if(pagesize>0)
        {
            int[] startEndRownumArr=cdb.getStartEndRownumOfDataset(datasetbean.getGuid());
            if(startEndRownumArr==null||startEndRownumArr.length!=2||startEndRownumArr[0]<0||startEndRownumArr[1]<=0||startEndRownumArr[0]==startEndRownumArr[1]) return null;
            systemParamsBuf.append("{[(<pagesize:"+(startEndRownumArr[1]-startEndRownumArr[0])+">)]}");
            systemParamsBuf.append("{[(<startrownum:"+startEndRownumArr[0]+">)]}");
            systemParamsBuf.append("{[(<endrownum:"+startEndRownumArr[1]+">)]}");
        }else
        {
            systemParamsBuf.append("{[(<pagesize:-1>)]}");
            if(datasetbean.isDependentDataSet())
            {
                String realConExpress=datasetbean.getRealDependsConditionExpression(lstReportData);
                systemParamsBuf.append("{[(<parentdataset_conditions:").append(realConExpress).append(">)]}");
            }
        }
        return doGetResultSet(rrequest,rbean,datasetbean,datasetbean,systemParamsBuf);
        
        
    }

    public Object getStatisticDataSet(ReportRequest rrequest,AbsReportType reportObj,ReportDataSetValueBean svbean,Object typeObj,String sql,boolean isStatisticForOnePage)
    {
        StringBuffer systemParamsBuf=new StringBuffer();
        ReportBean rbean=reportObj.getReportBean();
        if(sql==null||sql.trim().equals(""))
        {
            throw new WabacusRuntimeException("调用报表"+rbean.getPath()+"的存储过程失败，没有传入在存储过程中要执行的SQL语句");
        }
        systemParamsBuf.append("{[(<statistic_sql:"+sql+">)]}");
        String filterwhere=ListReportAssistant.getInstance().getFilterConditionExpression(rrequest,rbean,svbean);
        if(filterwhere!=null&&!filterwhere.trim().equals(""))
        {
            systemParamsBuf.append("{[(<filter_condition:"+filterwhere+">)]}");
        }
        CacheDataBean cdb=rrequest.getCdb(rbean.getId());
        int pagesize=isLoadAllData?-1:cdb.getPagesize();
        if(pagesize>0)
        {
            int[] startEndRownumArr=cdb.getStartEndRownumOfDataset(svbean.getGuid());
            if(startEndRownumArr==null||startEndRownumArr.length!=2||startEndRownumArr[0]<0||startEndRownumArr[1]<=0||startEndRownumArr[0]==startEndRownumArr[1]) return null;
            systemParamsBuf.append("{[(<pagesize:"+(startEndRownumArr[1]-startEndRownumArr[0])+">)]}");
            systemParamsBuf.append("{[(<startrownum:"+startEndRownumArr[0]+">)]}");
            systemParamsBuf.append("{[(<endrownum:"+startEndRownumArr[1]+">)]}");
            addRowgroupColsToParams(rbean,systemParamsBuf);
            String[] orderbys=(String[])rrequest.getAttribute(rbean.getId(),"ORDERBYARRAY");
            if(orderbys!=null&&orderbys.length==2)
            {
                systemParamsBuf.append("{[(<dynamic_orderby:"+orderbys[0]+" "+orderbys[1]+">)]}");
            }
        }else
        {
            systemParamsBuf.append("{[(<pagesize:-1>)]}");
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
                systemParamsBuf.append("{[(<parentdataset_conditions:").append(realConExpress).append(">)]}");
            }
        }
        return doGetResultSet(rrequest,rbean,svbean,svbean,systemParamsBuf);
    }
    
    private void addRowgroupColsToParams(ReportBean rbean,StringBuffer systemParamsBuf)
    {
        AbsListReportDisplayBean alrdbean=(AbsListReportDisplayBean)rbean.getDbean().getExtendConfigDataForReportType(AbsListReportType.KEY);
        if(alrdbean!=null&&alrdbean.getLstRowgroupColsColumn()!=null)
        {
            StringBuffer rowGroupColsBuf=new StringBuffer();
            for(String rowgroupColTmp:alrdbean.getLstRowgroupColsColumn())
            {
                if(rowgroupColTmp!=null&&!rowgroupColTmp.trim().equals("")) rowGroupColsBuf.append(rowgroupColTmp).append(",");
            }
            if(rowGroupColsBuf.length()>0)
            {
                if(rowGroupColsBuf.charAt(rowGroupColsBuf.length()-1)==',') rowGroupColsBuf.deleteCharAt(rowGroupColsBuf.length()-1);
                systemParamsBuf.append("{[(<rowgroup_cols:"+rowGroupColsBuf.toString()+">)]}");
            }
        }
    }

    public Object getDataSet(ReportRequest rrequest,ReportBean rbean,Object typeObj,String sp,List<ConditionBean> lstConditionBeans,String datasource)
    {
        if(rbean.getInterceptor()!=null)
        {
            Object obj=rbean.getInterceptor().beforeLoadData(rrequest,rbean,typeObj,sp);
            if(!(obj instanceof String)) return obj;
            sp=(String)obj;
        }
        if(Config.show_sql) log.info("Execute sql: "+sp);
        CallableStatement cstmt=null;
        try
        {
            if(datasource==null||datasource.trim().equals("")) datasource=rbean.getSbean().getDatasource();
            cstmt=rrequest.getConnection(datasource).prepareCall(sp);
            AbsDatabaseType dbtype=rrequest.getDbType(datasource);
            VarcharType varcharObj=(VarcharType)Config.getInstance().getDataTypeByClass(VarcharType.class);
            IDataType datatypeObj;
            int idx=1;
            if(lstConditionBeans!=null&&lstConditionBeans.size()>0)
            {//存储过程有动态参数
                for(ConditionBean cbTmp:lstConditionBeans)
                {
                    datatypeObj=cbTmp.getDatatypeObj();
                    if(datatypeObj==null) datatypeObj=varcharObj;
                    datatypeObj.setPreparedStatementValue(idx++,cbTmp.getConditionValue(rrequest,-1),cstmt,dbtype);
                }
            }
            if(dbtype instanceof Oracle)
            {
                cstmt.registerOutParameter(idx,OracleTypes.CURSOR);
            }
            rrequest.addUsedStatement(cstmt);
            cstmt.executeQuery();
            ResultSet rs=null;
            if(dbtype instanceof Oracle)
            {
                rs=(ResultSet)cstmt.getObject(idx);
            }else
            {
                rs=cstmt.getResultSet();
            }
            return rs;
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("从数据库取报表"+rbean.getPath()+"数据时执行SQL："+sp+"失败",e);
        }
    }

    private ResultSet doGetResultSet(ReportRequest rrequest,ReportBean rbean,ReportDataSetValueBean datasetbean,Object typeObj,StringBuffer systemParamsBuf)
    {
        log.debug(systemParamsBuf.toString());
        String procedure=datasetbean.getValue();
        if(rbean.getInterceptor()!=null)
        {
            Object obj=rbean.getInterceptor().beforeLoadData(rrequest,rbean,typeObj,procedure);
            if(obj==null) return null;
            if(obj instanceof List||obj instanceof ResultSet)
            {
                throw new WabacusRuntimeException("执行报表"+rbean.getPath()+"的加载数据拦截器失败，当前报表采用存储过程加载数据，不能在拦截方法中返回ResultSet或List对象");
            }
            if(!(obj instanceof String))
            {
                throw new WabacusRuntimeException("执行报表"+rbean.getPath()+"的加载数据拦截器失败，返回的数据类型"+obj.getClass().getName()+"不合法");
            }
            procedure=(String)obj;
        }
        if(Config.show_sql)
        {
            log.info("Execute sql: "+procedure);
        }
        CallableStatement cstmt=null;
        try
        {
            cstmt=rrequest.getConnection(datasetbean.getDatasource()).prepareCall(procedure);
            AbsDatabaseType dbtype=rrequest.getDbType(datasetbean.getDatasource());
            VarcharType varcharObj=(VarcharType)Config.getInstance().getDataTypeByClass(VarcharType.class);
            int idx=1;
            if(datasetbean.getLstStoreProcedureParams()!=null&&datasetbean.getLstStoreProcedureParams().size()>0)
            {
                ConditionBean cbeanTmp;
                for(String paramTmp:datasetbean.getLstStoreProcedureParams())
                {
                    if(WabacusAssistant.getInstance().isGetRequestContextValue(paramTmp))
                    {//从request/session中取值
                        varcharObj.setPreparedStatementValue(idx,WabacusAssistant.getInstance().getRequestContextStringValue(rrequest,paramTmp,""),cstmt,
                                dbtype);
                    }else if(Tools.isDefineKey("condition",paramTmp))
                    {
                        cbeanTmp=rbean.getSbean().getConditionBeanByName(Tools.getRealKeyByDefine("condition",paramTmp));
                        if(cbeanTmp.getIterator()>1||cbeanTmp.getCcolumnsbean()!=null||cbeanTmp.getCvaluesbean()!=null)
                        {
                            varcharObj.setPreparedStatementValue(idx,cbeanTmp.getConditionValueForSP(rrequest),cstmt,dbtype);
                        }else
                        {
                            cbeanTmp.getDatatypeObj().setPreparedStatementValue(idx,cbeanTmp.getConditionValueForSP(rrequest),cstmt,dbtype);
                        }
                    }else
                    {
                        varcharObj.setPreparedStatementValue(idx,paramTmp,cstmt,dbtype);
                    }
                    idx++;
                }
            }
            cstmt.setString(idx++,systemParamsBuf.toString());
            
            
            if(dbtype instanceof Oracle)
            {
                cstmt.registerOutParameter(idx,OracleTypes.CURSOR);
            }
            rrequest.addUsedStatement(cstmt);
            cstmt.executeQuery();
            ResultSet rs=null;
            if(dbtype instanceof Oracle)
            {
                rs=(ResultSet)cstmt.getObject(idx);
            }else
            {
                rs=cstmt.getResultSet();
            }
            return rs;
            
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("从数据库取报表"+rbean.getPath()+"数据时执行SQL："+procedure+"失败",e);
        }
    }
    
    private String getDynamicSelectCols(ReportRequest rrequest,ReportDataSetValueBean datasetbean)
    {
        String datasetid=datasetbean.getId();
        datasetid=datasetid==null?"":datasetid.trim();
        return rrequest.getStringAttribute(datasetbean.getReportBean().getId(),datasetid+"_DYN_SELECT_COLS","");
    }
}
