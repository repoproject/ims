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
package com.wabacus.system.component.application.report.configbean.crosslist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.config.component.application.report.SqlBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.CacheDataBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.ReportAssistant;
import com.wabacus.system.component.application.report.CrossListReportType;
import com.wabacus.system.dataset.sqldataset.AbsGetAllDataSetBySQL;
import com.wabacus.system.dataset.sqldataset.GetAllDataSetByPreparedSQL;
import com.wabacus.system.dataset.sqldataset.GetAllDataSetBySQL;
import com.wabacus.util.Tools;

public class CrossListReportDynDatasetBean
{
    public final static String GROUPBY_DYNAMICOLUMNS_PLACEHOLDER="#GROUPBY_DYNAMICOLUMNS_PLACEHOLDER#";

    private static Log log=LogFactory.getLog(CrossListReportDynDatasetBean.class);
    
    private ReportDataSetValueBean datasetbean;

    private String dynamicColsPlaceholder;

    private String sql_getVerticalStatisData;

    private List<AbsCrossListReportColAndGroupBean> lstIncludeCommonCrossColAndGroupBeans;

    private List<AbsCrossListReportColAndGroupBean> lstIncludeCrossStatiColAndGroupBeans;

    public ReportDataSetValueBean getDatasetbean()
    {
        return datasetbean;
    }

    public void setDatasetbean(ReportDataSetValueBean datasetbean)
    {
        this.datasetbean=datasetbean;
    }
    
    public String getSql_getVerticalStatisData()
    {
        return sql_getVerticalStatisData;
    }

    public void addCrossColGroupBean(AbsCrossListReportColAndGroupBean crossColGroupBean)
    {
        if(crossColGroupBean.isCommonCrossColGroup())
        {
            if(lstIncludeCommonCrossColAndGroupBeans==null) lstIncludeCommonCrossColAndGroupBeans=new ArrayList<AbsCrossListReportColAndGroupBean>();
            for(AbsCrossListReportColAndGroupBean cgBeanTmp:this.lstIncludeCommonCrossColAndGroupBeans)
            {
                if(cgBeanTmp.getColumn().equals(crossColGroupBean.getColumn())) return;
            }
            this.lstIncludeCommonCrossColAndGroupBeans.add(crossColGroupBean);
        }else if(crossColGroupBean.isStatisticCrossColGroup())
        {
            if(lstIncludeCrossStatiColAndGroupBeans==null) lstIncludeCrossStatiColAndGroupBeans=new ArrayList<AbsCrossListReportColAndGroupBean>();
            for(AbsCrossListReportColAndGroupBean cgBeanTmp:this.lstIncludeCrossStatiColAndGroupBeans)
            {
                if(cgBeanTmp.getColumn().equals(crossColGroupBean.getColumn())) return;
            }
            this.lstIncludeCrossStatiColAndGroupBeans.add(crossColGroupBean);
        }
    }

    public void buildRealSelectSqls(CrossListReportType crossListReportType,Map<String,String> mAllSelectCols)
    {
        ReportRequest rrequest=crossListReportType.getReportRequest();
        ReportBean rbean=crossListReportType.getReportBean();
        String allSelectCols=getAllDynamicSelectCols(mAllSelectCols);
        String datasetid=this.datasetbean.getId();
        datasetid=datasetid==null?"":datasetid.trim();
        if(this.datasetbean.isStoreProcedure()||this.datasetbean.getCustomizeDatasetObj()!=null)
        {
            rrequest.setAttribute(rbean.getId(),datasetid+"_DYN_SELECT_COLS",allSelectCols.toString());
        }else if(allSelectCols.trim().equals("")&&this.dynamicColsPlaceholder.equals("(#dynamic-columns#)"))
        {
            rrequest.setAttribute(rbean.getId(),datasetid+"_DYN_SQL","[NONE]");
        }else
        {
            String sql=this.datasetbean.getSql_kernel();
            if(sql.indexOf(GROUPBY_DYNAMICOLUMNS_PLACEHOLDER)>0)
            {
                String crossStatiDynCols=getDynamicCrossStatiSelectCols(mAllSelectCols).trim();
                String commonDynCols=getDynamicCommonSelectCols(mAllSelectCols).trim();
                if(crossStatiDynCols.equals(""))
                {
                    Map<String,String> mSqlParts=parseStatiSql(sql);
                    sql=mSqlParts.get("prevselectpart")+" select "+mSqlParts.get("selectcolumnspart")+" from "+mSqlParts.get("frompart")+" "
                            +mSqlParts.get("lastpart");
                }else if(commonDynCols.equals(""))
                {//没有普通动态列，则把group by中普通动态列的占位符去掉
                    Map<String,String> mSqlParts=parseStatiSql(sql);
                    String groupbypart=mSqlParts.get("groupbypart").trim();
                    if(groupbypart.equals(GROUPBY_DYNAMICOLUMNS_PLACEHOLDER))
                    {
                        sql=mSqlParts.get("prevselectpart")+" select "+mSqlParts.get("selectcolumnspart")+" from "+mSqlParts.get("frompart")+" "
                                +mSqlParts.get("lastpart");
                    }else
                    {
                        groupbypart=crossListReportType.replaceDynColPlaceHolder(groupbypart,"",GROUPBY_DYNAMICOLUMNS_PLACEHOLDER);
                        sql=mSqlParts.get("prevselectpart")+" select "+mSqlParts.get("selectcolumnspart");
                        sql=sql+" from "+mSqlParts.get("frompart")+" group by "+groupbypart+" "+mSqlParts.get("lastpart");
                    }
                }else
                {
                    sql=Tools.replaceAll(sql,GROUPBY_DYNAMICOLUMNS_PLACEHOLDER,commonDynCols);
                }
            }
            sql=crossListReportType.replaceDynColPlaceHolder(sql,allSelectCols,this.dynamicColsPlaceholder);
            rrequest.setAttribute(rbean.getId(),datasetid+"_DYN_SQL",sql.trim());
        }
    }
    
    private String getDynamicCommonSelectCols(Map<String,String> mAllSelectCols)
    {
        return getDynamicSelectCols(this.lstIncludeCommonCrossColAndGroupBeans,mAllSelectCols);
    }

    private String getDynamicCrossStatiSelectCols(Map<String,String> mAllSelectCols)
    {
        return getDynamicSelectCols(this.lstIncludeCrossStatiColAndGroupBeans,mAllSelectCols);
    }

    private String getDynamicSelectCols(List<AbsCrossListReportColAndGroupBean> lstCrossColAndGroupBeans,Map<String,String> mAllSelectCols)
    {
        if(lstCrossColAndGroupBeans==null||lstCrossColAndGroupBeans.size()==0) return "";
        if(mAllSelectCols==null||mAllSelectCols.size()==0) return "";
        StringBuffer resultBuf=new StringBuffer();
        String selectColsTmp;
        for(AbsCrossListReportColAndGroupBean colgroupBeanTmp:lstCrossColAndGroupBeans)
        {
            selectColsTmp=mAllSelectCols.get(colgroupBeanTmp.getRootCrossColGroupId());
            if(selectColsTmp==null||selectColsTmp.trim().equals("")) continue;
            resultBuf.append(selectColsTmp).append(",");
        }
        if(resultBuf.length()>0&&resultBuf.charAt(resultBuf.length()-1)==',') resultBuf.deleteCharAt(resultBuf.length()-1);
        return resultBuf.toString();
    }
    
    private String getAllDynamicSelectCols(Map<String,String> mAllSelectCols)
    {
        String crossStatiDynCols=getDynamicCrossStatiSelectCols(mAllSelectCols).trim();
        String commonDynCols=getDynamicCommonSelectCols(mAllSelectCols).trim();
        if(crossStatiDynCols.equals("")) return commonDynCols;
        if(commonDynCols.equals("")) return crossStatiDynCols;
        return commonDynCols+","+crossStatiDynCols;
    }
    
    public ResultSet loadVerticalStatiData(CrossListReportType crossListReportType,Map<String,String> mAllSelectCols)
    {
        if(this.sql_getVerticalStatisData==null||this.sql_getVerticalStatisData.trim().equals("")) return null;
        if(this.datasetbean.isStoreProcedure()||this.datasetbean.getCustomizeDatasetObj()!=null)
        {
            throw new WabacusRuntimeException("加载交叉统计报表"+this.datasetbean.getReportBean().getPath()+"失败，此报表不是采用SQL语句获取交叉报表数据，因此不支持进行垂直统计");
        }
        String crossStatiDynCols=getDynamicCrossStatiSelectColsWithVerticalStati(mAllSelectCols).trim();
        if(crossStatiDynCols.equals("")) return null;
        ReportRequest rrequest=crossListReportType.getReportRequest();
        ReportBean rbean=crossListReportType.getReportBean();
        String statisql=Tools.replaceAll(this.sql_getVerticalStatisData,this.dynamicColsPlaceholder,crossStatiDynCols);
        if(datasetbean.isDependentDataSet())
        {
            List lstReportData=null;
            CacheDataBean cdb=rrequest.getCdb(rbean.getId());
            if(!cdb.isLoadAllReportData())
            {
                lstReportData=(List)rrequest.getAttribute(rbean.getId()+"wx_all_data_tempory");
                if(lstReportData==null)
                {
                    lstReportData=ReportAssistant.getInstance().loadReportDataSet(rrequest,crossListReportType,true);
                    rrequest.setAttribute(rbean.getId()+"wx_all_data_tempory",lstReportData);
                }
            }else
            {
                lstReportData=crossListReportType.getLstReportData();
            }
            String realConExpress=datasetbean.getRealDependsConditionExpression(lstReportData);
            if(realConExpress==null||realConExpress.trim().equals(""))
            {
                statisql=ReportAssistant.getInstance().removeConditionPlaceHolderFromSql(rbean,statisql,ReportDataSetValueBean.dependsConditionPlaceHolder);
            }else
            {
                statisql=Tools.replaceAll(statisql,ReportDataSetValueBean.dependsConditionPlaceHolder,realConExpress);
            }
        }
        AbsGetAllDataSetBySQL datasetObj=null;
        if(rbean.getSbean().getStatementType()==SqlBean.STMTYPE_PREPAREDSTATEMENT)
        {
            datasetObj=new GetAllDataSetByPreparedSQL();
        }else
        {
            datasetObj=new GetAllDataSetBySQL();
        }
        Object objTmp=datasetObj.getDataSet(rrequest,rbean,this.datasetbean,crossListReportType,statisql);
        if(objTmp==null) return null;
        if(!(objTmp instanceof ResultSet))
        {
            throw new WabacusRuntimeException("获取报表："+rbean.getPath()+"针对每列数据的垂直统计失败，此时不能在拦截器中返回非ResultSet类型的数据");
        }
        ResultSet rs=(ResultSet)objTmp;
        try
        {
            if(!rs.next())
            {
                rs.close();
                rs=null;
            }
        }catch(SQLException e)
        {
            log.warn("获取报表"+rbean.getPath()+"的针对每列数据进行垂直统计的记录集失败",e);
        }
        return rs;
    }
    
    private String getDynamicCrossStatiSelectColsWithVerticalStati(Map<String,String> mAllSelectCols)
    {
        if(this.lstIncludeCrossStatiColAndGroupBeans==null||lstIncludeCrossStatiColAndGroupBeans.size()==0) return "";
        if(mAllSelectCols==null||mAllSelectCols.size()==0) return "";
        StringBuffer resultBuf=new StringBuffer();
        String selectColsTmp;
        for(AbsCrossListReportColAndGroupBean colgroupBeanTmp:lstIncludeCrossStatiColAndGroupBeans)
        {
            if(!colgroupBeanTmp.getInnerDynamicColBean().isHasVerticalstatistic()) continue;
            selectColsTmp=mAllSelectCols.get(colgroupBeanTmp.getRootCrossColGroupId());
            if(selectColsTmp==null||selectColsTmp.trim().equals("")) continue;
            resultBuf.append(selectColsTmp).append(",");
        }
        if(resultBuf.length()>0&&resultBuf.charAt(resultBuf.length()-1)==',') resultBuf.deleteCharAt(resultBuf.length()-1);
        return resultBuf.toString();
    }
    
    public void doPostLoad()
    {
        ReportBean reportbean=this.datasetbean.getReportBean();
        if(this.datasetbean.isSql())
        {
            String sql=this.datasetbean.getValue().trim();
            if(sql.indexOf("[#dynamic-columns#]")>0)
            {
                this.dynamicColsPlaceholder="[#dynamic-columns#]";
            }else if(sql.indexOf("(#dynamic-columns#)")>0)
            {
                this.dynamicColsPlaceholder="(#dynamic-columns#)";
            }else
            {
                throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()
                        +"失败，查询动态列的数据集的SQL语句中没有指定[#dynamic-columns#]或(#dynamic-columns#)做为动态字段占位符");
            }
            if(this.lstIncludeCrossStatiColAndGroupBeans!=null&&this.lstIncludeCrossStatiColAndGroupBeans.size()>0)
            {
                if(sql.toLowerCase().indexOf(" group ")<0||sql.toLowerCase().indexOf(" by ")<0)
                {
                    throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，查询动态列的数据集的SQL语句"+sql+"没有指定 group by");
                }
                Map<String,String> mSqlParts=parseStatiSql(sql);
                if(this.lstIncludeCommonCrossColAndGroupBeans!=null&&this.lstIncludeCommonCrossColAndGroupBeans.size()>0)
                {
                    String groupbypart=mSqlParts.get("groupbypart");
                    if(groupbypart.indexOf(this.dynamicColsPlaceholder)>0)
                    {
                        groupbypart=Tools.replaceAll(groupbypart,this.dynamicColsPlaceholder,GROUPBY_DYNAMICOLUMNS_PLACEHOLDER);
                    }else
                    {
                        groupbypart=groupbypart+","+GROUPBY_DYNAMICOLUMNS_PLACEHOLDER;
                    }
                    sql=mSqlParts.get("prevselectpart")+" select "+mSqlParts.get("selectcolumnspart");
                    sql=sql+" from "+mSqlParts.get("frompart")+" group by "+groupbypart+" "+mSqlParts.get("lastpart");
                    this.datasetbean.setValue(sql.trim());
                }
                for(AbsCrossListReportColAndGroupBean cgBeanTmp:this.lstIncludeCrossStatiColAndGroupBeans)
                {
                    if(cgBeanTmp.getInnerDynamicColBean().isHasVerticalstatistic())
                    {
                        String lastpart=mSqlParts.get("lastpart");//lastpart可能有order by子句，把order by去掉，但如果当前是里层的子查询，要保留)及后面的外层SQL语句
                        int idx=lastpart.indexOf(")");
                        if(idx>=0)
                        {
                            lastpart=lastpart.substring(idx);
                        }else
                        {
                            lastpart="";
                        }
                        this.sql_getVerticalStatisData=mSqlParts.get("prevselectpart")+" select "+this.dynamicColsPlaceholder+" from "
                                +mSqlParts.get("frompart")+" "+lastpart;
                        this.sql_getVerticalStatisData=this.sql_getVerticalStatisData.trim();
                        break;
                    }
                }
            }
        }
    }

    private Map<String,String> parseStatiSql(String sql)
    {
        ReportBean reportbean=this.datasetbean.getReportBean();
        String prevselectpart="", selectcolumnspart="", frompart="", groupbypart="", lastpart="";
        int idx=sql.indexOf(this.dynamicColsPlaceholder);
        String tmp=sql.substring(0,idx).trim();
        while(true)
        {
            int idxLocal=tmp.toLowerCase().lastIndexOf("select ");
            if(idxLocal<0)
            {
                throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，配置的sql语句"+sql+"不合法");
            }
            prevselectpart=tmp.substring(0,idxLocal).trim();
            selectcolumnspart=tmp.substring(idxLocal+"select".length())+selectcolumnspart;
            if(prevselectpart.equals("")||prevselectpart.endsWith("("))
            {
                break;
            }
            tmp=prevselectpart;
            selectcolumnspart="select"+selectcolumnspart;
            
        }
        tmp=sql.substring(idx);
        idx=tmp.toLowerCase().indexOf(" from ");
        selectcolumnspart=selectcolumnspart+tmp.substring(0,idx)+" ";
        tmp=tmp.substring(idx+" from ".length());
        
        while(true)
        {
            int idxLocal=tmp.toLowerCase().lastIndexOf(" group ");
            if(idxLocal<0)
            {
                throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，针对交叉统计报表，其SQL语句中必须包含group by子句");
            }
            frompart+=tmp.substring(0,idxLocal);
            tmp=tmp.substring(idxLocal+" group ".length()).trim();
            if(tmp.toLowerCase().startsWith("by "))
            {
                tmp=tmp.substring(2).trim();
                break;
            }
            frompart+=" group ";
        }
        int i=0;
        tmp=tmp.trim();
        for(;i<tmp.length();i++)
        {
            if(tmp.charAt(i)==')'
                    ||(tmp.charAt(i)==' '&&!tmp.substring(0,i).trim().endsWith(",")&&(tmp.length()==i+1||!tmp.substring(i+1).trim().startsWith(","))))
            {
                break;
            }else
            {
                groupbypart+=tmp.charAt(i);
            }
        }
        if(groupbypart.trim().equals(""))
        {
            throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，查询动态列的数据集的SQL语句"+sql+"没有指定 group by");
        }
        lastpart=tmp.substring(i);
        Map<String,String> mResults=new HashMap<String,String>();
        mResults.put("prevselectpart",prevselectpart);
        mResults.put("selectcolumnspart",selectcolumnspart);
        mResults.put("frompart",frompart);
        mResults.put("groupbypart",groupbypart);
        mResults.put("lastpart",lastpart);
        return mResults;
    }
}
