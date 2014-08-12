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
package com.wabacus.system.assistant;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.Config;
import com.wabacus.config.ConfigLoadManager;
import com.wabacus.config.component.IComponentConfigBean;
import com.wabacus.config.component.application.report.AbsReportDataPojo;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.DisplayBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.config.component.application.report.SqlBean;
import com.wabacus.config.component.application.report.condition.ConditionInSqlBean;
import com.wabacus.config.component.container.AbsContainerConfigBean;
import com.wabacus.config.database.type.AbsDatabaseType;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.CacheDataBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.buttons.AbsButtonType;
import com.wabacus.system.buttons.IButtonClickeventGenerate;
import com.wabacus.system.component.application.report.CrossListReportType;
import com.wabacus.system.component.application.report.EditableDetailReportType;
import com.wabacus.system.component.application.report.EditableListFormReportType;
import com.wabacus.system.component.application.report.abstractreport.AbsListReportType;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.component.application.report.abstractreport.IEditableReportType;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportColBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportDisplayBean;
import com.wabacus.system.component.application.report.configbean.crosslist.CrossListReportColBean;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditActionBean;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditableReportEditDataBean;
import com.wabacus.system.dataset.IReportDataSet;
import com.wabacus.system.datatype.IDataType;
import com.wabacus.system.inputbox.AbsInputBox;
import com.wabacus.system.inputbox.SelectBox;
import com.wabacus.system.inputbox.TextBox;
import com.wabacus.system.intercept.AbsInterceptorDefaultAdapter;
import com.wabacus.system.intercept.ColDataBean;
import com.wabacus.system.intercept.ReportDataBean;
import com.wabacus.system.intercept.RowDataBean;
import com.wabacus.util.Consts;
import com.wabacus.util.Consts_Private;
import com.wabacus.util.Tools;
import com.wabacus.util.UniqueArrayList;

public class ReportAssistant
{
    private static Log log=LogFactory.getLog(ReportAssistant.class);

    private final static ReportAssistant instance=new ReportAssistant();

    protected ReportAssistant()
    {}

    public static ReportAssistant getInstance()
    {
        return instance;
    }

    public String mixDynorderbyAndRowgroupCols(ReportBean rbean,String dynorderby)
    {
        List<String> lstTemp=Tools.parseStringToList(dynorderby," ");
        Map<String,String> mOldDynOrderBy=new HashMap<String,String>();
        if(lstTemp.size()!=2)
        {
            throw new WabacusRuntimeException("查询报表"+rbean.getPath()+"数据失败，传入的动态排序子句"+dynorderby+"不合法");
        }else
        {
            mOldDynOrderBy.put(lstTemp.get(0).trim(),lstTemp.get(1).trim());
        }
        StringBuffer orderbybuf=new StringBuffer();
        AbsListReportDisplayBean alrdbean=(AbsListReportDisplayBean)rbean.getDbean().getExtendConfigDataForReportType(AbsListReportType.KEY);
        if(alrdbean!=null&&alrdbean.getRowGroupColsNum()>0)
        {
            List<Map<String,String>> lstRowgroupColsAndOrders=alrdbean.getLstRowgroupColsAndOrders();
            if(lstRowgroupColsAndOrders!=null&&lstRowgroupColsAndOrders.size()>0)
            {
                String ordercol;
                for(Map<String,String> mOrderCols:lstRowgroupColsAndOrders)
                {
                    if(mOrderCols==null||mOrderCols.size()==0) continue;
                    ordercol=mOrderCols.keySet().iterator().next();
                    if(mOldDynOrderBy!=null&&mOldDynOrderBy.containsKey(ordercol))
                    {
                        orderbybuf.append(ordercol).append(" ").append(mOldDynOrderBy.get(ordercol)).append(",");
                        mOldDynOrderBy=null;
                    }else
                    {
                        orderbybuf.append(ordercol).append(" ").append(mOrderCols.get(ordercol)).append(",");
                    }
                }
            }
        }
        if(mOldDynOrderBy!=null)
        {
            orderbybuf.append(dynorderby);
        }
        if(orderbybuf.charAt(orderbybuf.length()-1)==',')
        {
            orderbybuf.deleteCharAt(orderbybuf.length()-1);
        }
        return orderbybuf.toString();
    }

    public String parseRuntimeSqlAndCondition(ReportRequest rrequest,ReportBean rbean,ReportDataSetValueBean svbean,String sql,List<String> lstConditionValues,
            List<IDataType> lstConditionTypes)
    {
        List<ConditionBean> lstConditionBeans=rbean.getSbean().getLstConditions();
        if(lstConditionBeans==null||lstConditionBeans.size()==0)
        {
            if(sql!=null&&sql.indexOf("{#condition#}")>0)
            {
                sql=removeConditionPlaceHolderFromSql(rbean,sql,"{#condition#}");
            }
            return sql;
        }
        List<ConditionInSqlBean> lstConditionInSqlBeans=svbean.getLstConditionInSqlBeans();
        if(lstConditionInSqlBeans==null||lstConditionInSqlBeans.size()==0)
        {
            sql=addDynamicConditionExpressionsToSql(rrequest,rbean,svbean,sql,rbean.getSbean().getLstConditions(),lstConditionValues,lstConditionTypes);
        }else
        {
            for(ConditionInSqlBean conbeanInSqlTmp:lstConditionInSqlBeans)
            {
                sql=conbeanInSqlTmp.parseConditionInSql(rrequest,sql,lstConditionValues,lstConditionTypes);
            }
        }
        return sql;
    }

    public String addDynamicConditionExpressionsToSql(ReportRequest rrequest,ReportBean rbean,ReportDataSetValueBean svbean,String sql,List<ConditionBean> lstConditionBeans,
            List<String> lstConditionValues,List<IDataType> lstConditionTypes)
    {
        if(lstConditionBeans==null||lstConditionBeans.size()==0) return removeConditionPlaceHolderFromSql(rbean,sql,"{#condition#}");
        StringBuffer dynConditionsBuf=new StringBuffer();
        String conditionExpressionTmp;
        for(ConditionBean conbean:lstConditionBeans)
        {
            if(svbean!=null&&!conbean.isBelongTo(svbean)) continue;
            conditionExpressionTmp=conbean.getConditionExpressionAndParams(rrequest,lstConditionValues,lstConditionTypes);
            if(conditionExpressionTmp==null||conditionExpressionTmp.trim().equals("")) continue;//没有在<condition/>中配置条件表达式（可能是通过#name#的形式直接在sql语句中指定条件）
            if(dynConditionsBuf.length()==0)
            {
                dynConditionsBuf.append(conditionExpressionTmp);
            }else
            {
                dynConditionsBuf.append(" and ").append(conditionExpressionTmp);
            }
        }
        String dynconditions=dynConditionsBuf.toString();
        if(dynconditions.trim().equals(""))
        {
            
            sql=removeConditionPlaceHolderFromSql(rbean,sql,"{#condition#}");
        }else
        {
            sql=Tools.replaceAll(sql,"{#condition#}"," ("+dynconditions+") ");
        }
        return sql;
    }

    public String removeConditionPlaceHolderFromSql(ReportBean rbean,String sql,String placeholder)
    {
        int idx=sql.indexOf(placeholder);
        if(idx<0) return sql;
        String sql1=sql.substring(0,idx).trim();
        String sql2=sql.substring(idx+placeholder.length()).trim();
        while(sql1.endsWith("(")&&sql2.startsWith(")"))
        {
            sql1=sql1.substring(0,sql1.length()-1).trim();
            sql2=sql2.substring(1).trim();
        }
        if(sql1.endsWith("("))
        {//sql为select xxx where 条件1 and/or ({当前条件} and/or 其它条件) ...形式，此时{当前条件}后面只有可能是and或or
            if(!sql2.toLowerCase().startsWith("and ")&&!sql2.toLowerCase().startsWith("or ")||sql2.indexOf(")")<0)
            {
                throw new WabacusRuntimeException("报表"+rbean.getPath()+"中的sql语句"+sql+"格式不对，"+placeholder+"所在位置不合法");
            }
            if(sql2.toLowerCase().startsWith("and "))
            {
                sql2=sql2.substring(3);
            }else if(sql2.toLowerCase().startsWith("or "))
            {
                sql2=sql2.substring(2);
            }
        }else if(sql2.startsWith(")"))
        {//sql为select xxx where 条件1 and/or (其它条件 and/or {当前条件}) ...形式或者select * from table where id in(select id from table2 where {当前条件})形式
            if(!sql1.toLowerCase().endsWith(" and")&&!sql1.toLowerCase().endsWith(" or")&&!sql1.toLowerCase().endsWith(" where")||sql1.indexOf("(")<0)
            {
                throw new WabacusRuntimeException("报表"+rbean.getPath()+"中的sql语句"+sql+"格式不对，"+placeholder+"所在位置不合法");
            }
            if(sql1.toLowerCase().endsWith(" and"))
            {
                sql1=sql1.substring(0,sql1.length()-3);
            }else if(sql1.toLowerCase().endsWith(" or"))
            {
                sql1=sql1.substring(0,sql1.length()-2);
            }else if(sql1.toLowerCase().endsWith(" where"))
            {
                sql1=sql1.substring(0,sql1.length()-5);
            }
        }else
        {//sql为select xxx where/and/or {当前条件} and/or/其它非条件值或空
            if(sql1.toLowerCase().endsWith(" where"))
            {//sql为select xxx where {当前条件} and/or/其它非条件值或空
                if(sql2.toLowerCase().startsWith("or "))
                {
                    sql2=sql2.substring(2);
                }else if(sql2.toLowerCase().startsWith("and "))
                {
                    sql2=sql2.substring(3);
                }else
                {
                    sql1=sql1.substring(0,sql1.length()-5);
                }
            }else if(sql1.toLowerCase().endsWith(" and"))
            {//sql为select xxx where 其它条件 and {当前条件} and/or/其它非条件值或空
                sql1=sql1.substring(0,sql1.length()-3);//即不管后面是哪一种，都只要去掉{当前条件}前面的and即可
            }else if(sql1.toLowerCase().endsWith(" or"))
            {//sql为select xxx where 其它条件 or {当前条件} and/or/其它非条件值或空
                if(sql2.toLowerCase().startsWith("and "))
                {
                    sql2=sql2.substring(3);
                }else
                {
                    sql1=sql1.substring(0,sql1.length()-2);
                }
            }
        }
        return sql1+" "+sql2;
    }

    public List<AbsReportDataPojo> loadReportDataSet(ReportRequest rrequest,AbsReportType reportObj,boolean isLoadAllDataMandatory)
    {
        SqlBean sbean=reportObj.getReportBean().getSbean();
        if(sbean.getLstDatasetBeans()==null||sbean.getLstDatasetBeans().size()==0) return null;
        CacheDataBean cdb=rrequest.getCdb(reportObj.getReportBean().getId());
        Map<String,IReportDataSet> mDatasetObjs=new HashMap<String,IReportDataSet>();
        if(!cdb.isLoadAllReportData()&&cdb.getRefreshNavigateInfoType()<=0&&!isLoadAllDataMandatory)
        {
            loadReportDataRecordcount(rrequest,reportObj,cdb,mDatasetObjs);
            if(cdb.getRecordcount()==0) return null;
        }
        List<AbsReportDataPojo> lstData=doLoadReportDataset(rrequest,reportObj,cdb,mDatasetObjs,isLoadAllDataMandatory);
        List<AbsReportDataPojo> lstDataTmp=(List<AbsReportDataPojo>)((ArrayList<AbsReportDataPojo>)lstData).clone();
        for(AbsReportDataPojo dataObjTmp:lstDataTmp)
        {
            dataObjTmp.format();
        }
        if(cdb.isLoadAllReportData())
        {
            cdb.setRecordcount(lstData.size());
            if(cdb.getRecordcount()>0) cdb.setPagecount(1);
        }
        return lstData;
    }
    
    private void loadReportDataRecordcount(ReportRequest rrequest,AbsReportType reportObj,CacheDataBean cdb,Map<String,IReportDataSet> mDatasetObjs)
    {
        if(cdb.getRefreshNavigateInfoType()<0)
        {
            IReportDataSet dataSetObjTmp;
            for(ReportDataSetBean dsbeanTmp:reportObj.getReportBean().getSbean().getLstDatasetBeans())
            {
                int recordcount=0;
                for(ReportDataSetValueBean dsvbeanTmp:dsbeanTmp.getLstValueBeans())
                {
                    if(dsvbeanTmp.isDependentDataSet()) continue;
                    dataSetObjTmp=dsvbeanTmp.createDataSetObj(rrequest,reportObj);
                    int recordcntTmp=dataSetObjTmp.getRecordcount(rrequest,reportObj,dsvbeanTmp);
                    cdb.addRecordcount(dsvbeanTmp.getGuid(),recordcntTmp);
                    if(recordcntTmp>recordcount) recordcount=recordcntTmp;
                    mDatasetObjs.put(dsvbeanTmp.getGuid(),dataSetObjTmp);
                }
                cdb.setRecordcount(cdb.getRecordcount()+recordcount);
            }
        }
        cdb.setPagecount(ReportAssistant.getInstance().calPageCount(cdb.getPagesize(),cdb.getRecordcount()));
    }
    
    private List<AbsReportDataPojo> doLoadReportDataset(ReportRequest rrequest,AbsReportType reportObj,CacheDataBean cdb,Map<String,IReportDataSet> mDatasetObjs,
            boolean isLoadAllDataMandatory)
    {
        Object resultDataSetTmp;
        IReportDataSet dataSetObjTmp;
        List<AbsReportDataPojo> lstDataResult=new ArrayList<AbsReportDataPojo>();
        List<AbsReportDataPojo> lstDataLocal=new ArrayList<AbsReportDataPojo>();
        int pagesize=-1, startNum=-1;
        if(!cdb.isLoadAllReportData()&&!isLoadAllDataMandatory)
        {
            pagesize=cdb.getPagesize();
            startNum=(cdb.getFinalPageno()-1)*pagesize;
        }
        int maxrecordcount=cdb.getMaxrecordcount();
        if(maxrecordcount<=0) maxrecordcount=-1;
        int prevDatasetDisplayedTotalRowcount=0;
        outer: for(ReportDataSetBean dsbeanTmp:reportObj.getReportBean().getSbean().getLstDatasetBeans())
        {//逐个<dataset/>查询记录
            int maxDisplayedTotalRowCountThisDataset=0;//记录本<dataset/>中在翻到本页时各子<value/>数据集显示的最大记录数
            for(ReportDataSetValueBean dsvbeanTmp:dsbeanTmp.getLstValueBeans())
            {
                if(startNum>=0&&!dsvbeanTmp.isDependentDataSet())
                {
                    int myrecordcnt=cdb.getRecordcountOfDataset(dsvbeanTmp.getGuid());
                    if(myrecordcnt==0) continue;
                    if(prevDatasetDisplayedTotalRowcount+myrecordcnt<startNum)
                    {
                        if(myrecordcnt>maxDisplayedTotalRowCountThisDataset) maxDisplayedTotalRowCountThisDataset=myrecordcnt;
                        continue;
                    }else
                    {
                        int startNumLocal=0;//本<value/>数据集在本页显示的起始记录号
                        if(startNum>prevDatasetDisplayedTotalRowcount)
                        {//如果本次显示的起始记录号大于前面所有数据集显示的总记录数，则说明本数据集在前面页也有显示（否则前面页的记录数不够）
                            startNumLocal=startNum-prevDatasetDisplayedTotalRowcount;
                        }
                        int pagesizelocal=pagesize;
                        if(prevDatasetDisplayedTotalRowcount>startNum)
                        {
                            pagesizelocal=startNum+pagesize-prevDatasetDisplayedTotalRowcount;
                        }
                        if(startNumLocal+pagesizelocal>myrecordcnt) pagesizelocal=myrecordcnt-startNumLocal;
                        int endNumLocal=startNumLocal+pagesizelocal;
                        if(endNumLocal>maxDisplayedTotalRowCountThisDataset) maxDisplayedTotalRowCountThisDataset=endNumLocal;
                        cdb.setStartEndRownumOfDataset(dsvbeanTmp.getGuid(),new int[] { startNumLocal, endNumLocal });
                    }
                }
                if((lstDataLocal.size()==0)&&dsvbeanTmp.isDependentDataSet()) continue;
                dataSetObjTmp=mDatasetObjs.get(dsvbeanTmp.getGuid());
                if(dataSetObjTmp==null)
                {
                    if(isLoadAllDataMandatory)
                    {
                        dataSetObjTmp=dsvbeanTmp.createLoadAllDataSetObj(rrequest,reportObj);
                    }else
                    {
                        dataSetObjTmp=dsvbeanTmp.createDataSetObj(rrequest,reportObj);
                    }
                }
                resultDataSetTmp=dataSetObjTmp.getDataSet(rrequest,reportObj,dsvbeanTmp,lstDataLocal);
                if(resultDataSetTmp==null) continue;
                if(dsvbeanTmp.isDependentDataSet())
                {
                    parseDependentReportData(rrequest,reportObj,dsvbeanTmp,resultDataSetTmp,lstDataLocal);
                }else if(cdb.isLoadAllReportData()||isLoadAllDataMandatory)
                {
                    parseAllReportData(rrequest,reportObj,dsvbeanTmp,resultDataSetTmp,lstDataLocal);
                }else
                {
                    parseOnePageReportData(rrequest,reportObj,dsvbeanTmp,resultDataSetTmp,lstDataLocal);
                }
            }
            prevDatasetDisplayedTotalRowcount+=maxDisplayedTotalRowCountThisDataset;
            for(int i=0;i<lstDataLocal.size();i++)
            {
                lstDataLocal.get(i).setLstAllDataObjs(lstDataResult);
                lstDataResult.add(lstDataLocal.get(i));
                if(pagesize>0)
                {
                    if(lstDataResult.size()==pagesize||(maxrecordcount>0&&startNum+lstDataResult.size()==maxrecordcount)) break outer;
                }else if(maxrecordcount>0&&lstDataResult.size()==maxrecordcount)
                {
                    break outer;
                }
            }
            lstDataLocal.clear();
        }
        return lstDataResult;
    }
    
    private void parseDependentReportData(ReportRequest rrequest,AbsReportType reportObj,ReportDataSetValueBean datasetbean,Object resultDataSet,
            List<AbsReportDataPojo> lstData)
    {
        Map<String,Map<ColBean,Object>> mChildDatasetDataObjs=new HashMap<String,Map<ColBean,Object>>();
        ReportBean rbean=reportObj.getReportBean();
        List<ColBean> lstColBeans=rrequest.getCdb(rbean.getId()).getLstDynOrderColBeans();
        if(lstColBeans==null||lstColBeans.size()==0) lstColBeans=rbean.getDbean().getLstCols();
        List<String> lstDependMyColumns=datasetbean.getLstDependMyColumns();
        StringBuffer allKeysBuf=new StringBuffer();
        String keyTmp;
        if(resultDataSet instanceof ResultSet)
        {
            AbsDatabaseType dbtype=rrequest.getDbType(datasetbean.getDatasource());
            ResultSet rs=(ResultSet)resultDataSet;
            try
            {
                while(rs.next())
                {
                    keyTmp=getRealDependsColumnsValueAsKey(datasetbean,lstDependMyColumns,rs,1,false);
                    allKeysBuf.append(keyTmp).append(";");
                    Map<ColBean,Object> mColData=getMColDataWithThisRelateKey(mChildDatasetDataObjs,keyTmp);
                    for(ColBean cbTmp:lstColBeans)
                    {
                        if(!cbTmp.isMatchDataSet(datasetbean)) continue;//不是从这个数据集中取数据
                        Object colValObjTmp=getColumnValueFromRs(rs,dbtype,cbTmp,rrequest);
                        if(colValObjTmp==null) continue;
                        addChildDataSetColValueToMColData(datasetbean,mColData,cbTmp,colValObjTmp);
                    }
                }
                rs.close();
            }catch(SQLException e)
            {
                throw new WabacusRuntimeException("获取报表"+rbean.getPath()+"数据失败",e);
            }
        }else if(resultDataSet instanceof List)
        {
            List lstDataLocal=(List)resultDataSet;
            if(lstDataLocal==null||lstDataLocal.size()==0) return;
            boolean isMapObj=lstDataLocal.get(0) instanceof Map;
            for(Object dataObjLocalTmp:lstDataLocal)
            {
                if(dataObjLocalTmp==null) continue;
                keyTmp=getRealDependsColumnsValueAsKey(datasetbean,lstDependMyColumns,dataObjLocalTmp,isMapObj?2:3,false);
                allKeysBuf.append(keyTmp).append(";");
                Map<ColBean,Object> mColData=getMColDataWithThisRelateKey(mChildDatasetDataObjs,keyTmp);
                Object objColValTmp;
                for(ColBean cbTmp:lstColBeans)
                {
                    if(!cbTmp.isMatchDataSet(datasetbean)) continue;
                    if(isMapObj)
                    {
                        if(!"[DYN_COL_DATA]".equals(cbTmp.getProperty()))
                        {
                            objColValTmp=((Map)dataObjLocalTmp).get(cbTmp.getProperty());
                        }else
                        {
                            objColValTmp=((Map)dataObjLocalTmp).get(cbTmp.getColumn());
                        }
                    }else
                    {
                        objColValTmp=((AbsReportDataPojo)dataObjLocalTmp).getColValue(cbTmp);
                    }
                    if(objColValTmp==null) continue;
                    addChildDataSetColValueToMColData(datasetbean,mColData,cbTmp,objColValTmp);
                }
            }
        }else
        {
            throw new WabacusRuntimeException("获取报表"+rbean.getPath()+"数据时，返回的结果集："+resultDataSet.getClass().getName()+"为非法结果类型");
        }
        log.debug("子数据集关联字段数据："+allKeysBuf.toString());
        if(mChildDatasetDataObjs.size()==0) return;
        List<String> lstDependParentColumns=datasetbean.getLstDependParentColumns();
        if(lstDependParentColumns==null||lstDependParentColumns.size()==0) return;
        ColBean cbTmp;
        allKeysBuf=new StringBuffer();
        for(AbsReportDataPojo dataObjTmp:lstData)
        {
            keyTmp=getRealDependsColumnsValueAsKey(datasetbean,lstDependParentColumns,dataObjTmp,3,true);
            Map<ColBean,Object> mColData=mChildDatasetDataObjs.get(keyTmp);
            if(mColData==null) continue;
            for(Entry<ColBean,Object> entryTmp:mColData.entrySet())
            {
                cbTmp=entryTmp.getKey();
                dataObjTmp.setColValue(cbTmp,entryTmp.getValue());
            }
            allKeysBuf.append(keyTmp).append(";");
        }
        log.debug("父数据集关联字段数据："+allKeysBuf.toString());
    }

    private void addChildDataSetColValueToMColData(ReportDataSetValueBean dsvbean,Map<ColBean,Object> mColData,ColBean cbean,Object colValObj)
    {
        if(!"multiple".equals(dsvbean.getDependstype()))
        {
            mColData.put(cbean,colValObj);
        }else
        {
            if(String.valueOf(colValObj).equals("")) return;
            String oldColValTmp=(String)mColData.get(cbean);
            if(oldColValTmp==null||oldColValTmp.trim().equals(""))
            {
                mColData.put(cbean,colValObj);
            }else
            {
                mColData.put(cbean,oldColValTmp+dsvbean.getSeperator()+String.valueOf(colValObj));
            }
        }
    }

    private Map<ColBean,Object> getMColDataWithThisRelateKey(Map<String,Map<ColBean,Object>> mChildDatasetDataObjs,String key)
    {
        Map<ColBean,Object> mColData=mChildDatasetDataObjs.get(key);
        if(mColData==null)
        {//这个字段值组合的KEY还没有在其它已解析过的记录中出现过
            mColData=new HashMap<ColBean,Object>();
            mChildDatasetDataObjs.put(key,mColData);
        }
        return mColData;
    }
    
    private String getRealDependsColumnsValueAsKey(ReportDataSetValueBean datasetbean,List<String> lstDependsColumns,Object dataSourceObj,int dataSourceType,
            boolean isParentColumns)
    {
        StringBuffer keyBuf=new StringBuffer();
        Object keyTmp;
        for(String relateColumnTmp:lstDependsColumns)
        {
            if(dataSourceType==1)
            {
                try
                {
                    keyTmp=((ResultSet)dataSourceObj).getString(relateColumnTmp);
                }catch(SQLException e)
                {
                    throw new WabacusRuntimeException("获取报表"+datasetbean.getReportBean().getPath()+"数据失败",e);
                }
            }else if(dataSourceType==2)
            {
                keyTmp=((Map)dataSourceObj).get(relateColumnTmp);
            }else
            {
                keyTmp=this.getPropertyValue(dataSourceObj,relateColumnTmp);
            }
            if(keyTmp==null) keyTmp="";
            if(!isParentColumns) keyTmp=datasetbean.format(relateColumnTmp,String.valueOf(keyTmp));
            keyBuf.append(String.valueOf(keyTmp)).append("_");
        }
        return keyBuf.toString();
    }
    
    private void parseAllReportData(ReportRequest rrequest,AbsReportType reportObj,ReportDataSetValueBean svbean,Object resultDataSet,List<AbsReportDataPojo> lstData)
    {
        CacheDataBean cdb=rrequest.getCdb(reportObj.getReportBean().getId());
        int maxrecordcount=cdb.getMaxrecordcount();
        if(maxrecordcount<=0) maxrecordcount=-1;
        try
        {
            if(resultDataSet instanceof ResultSet)
            {
                ResultSet rs=(ResultSet)resultDataSet;
                parseDataIntoBean(reportObj,svbean,rs,maxrecordcount,rrequest,lstData);
                rs.close();
            }else if(resultDataSet instanceof List)
            {
                List lstDataLocal=(List)resultDataSet;
                if(maxrecordcount>0&&lstDataLocal.size()>maxrecordcount)
                {
                    while(lstDataLocal.size()>maxrecordcount)
                    {
                        lstDataLocal.remove(lstDataLocal.size()-1);
                    }
                }
                copyListDataToLstResultsData(rrequest,svbean,lstDataLocal,lstData);
            }else
            {
                throw new WabacusRuntimeException("获取数据时，返回的结果集："+resultDataSet.getClass().getName()+"为非法结果类型");
            }
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("加载报表"+reportObj.getReportBean().getPath()+"数据失败",e);
        }
    }

    private void parseOnePageReportData(ReportRequest rrequest,AbsReportType reportObj,ReportDataSetValueBean dsvbean,Object resultDataSet,List<AbsReportDataPojo> lstData)
    {
        ReportBean rbean=reportObj.getReportBean();
        CacheDataBean cdb=rrequest.getCdb(rbean.getId());
        int pagesize=cdb.getPagesize();
        try
        {
            if(resultDataSet instanceof ResultSet)
            {
                ResultSet rs=(ResultSet)resultDataSet;
                parseDataIntoBean(reportObj,dsvbean,rs,pagesize,rrequest,lstData);
                rs.close();
            }else if(resultDataSet instanceof List)
            {
                List lstTmp=(List)resultDataSet;
                while(lstTmp!=null&&lstTmp.size()>pagesize)
                {
                    lstTmp.remove(lstTmp.size()-1);
                }
                copyListDataToLstResultsData(rrequest,dsvbean,lstTmp,lstData);
            }else
            {
                throw new WabacusRuntimeException("获取数据时，返回的结果集："+resultDataSet.getClass().getName()+"为非法结果类型");
            }
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("加载报表"+rbean.getPath()+"数据失败",e);
        }
    }

    private void copyListDataToLstResultsData(ReportRequest rrequest,ReportDataSetValueBean dsvbean,List lstDataLocal,List<AbsReportDataPojo> lstData)
    {
        if(lstDataLocal==null||lstDataLocal.size()==0) return;
        ReportBean rbean=dsvbean.getReportBean();
        boolean isMultiDataset=rbean.getSbean().isMultiDatasetRows();
        boolean isMapObj=lstDataLocal.get(0) instanceof Map;
        if(!isMapObj&&lstData.size()==0)
        {
            if(isMultiDataset)
            {//配置有多个<dataset/>，则设置每条记录所属的<dataset/>的id
                for(Object pojoTmp:lstDataLocal)
                {
                    ((AbsReportDataPojo)pojoTmp).setWx_belongto_datasetid(((ReportDataSetBean)dsvbean.getParent()).getId());
                }
            }
            lstData.addAll(lstDataLocal);
            return;
        }
        List<ColBean> lstColBeans=rrequest.getCdb(rbean.getId()).getLstDynOrderColBeans();
        if(lstColBeans==null||lstColBeans.size()==0) lstColBeans=rbean.getDbean().getLstCols();
        int i=-1;
        AbsReportDataPojo dataObjTmp;
        try
        {
            for(Object dataObjLocalTmp:lstDataLocal)
            {
                i++;
                if(dataObjLocalTmp==null) continue;
                if(i>=lstData.size())
                {
                    if(isMapObj)
                    {
                        dataObjTmp=getPojoClassInstance(rrequest,rbean,rbean.getPojoClassObj());
                        if(isMultiDataset) dataObjTmp.setWx_belongto_datasetid(((ReportDataSetBean)dsvbean.getParent()).getId());
                        lstData.add(dataObjTmp);
                    }else
                    {
                        if(isMultiDataset) ((AbsReportDataPojo)dataObjLocalTmp).setWx_belongto_datasetid(((ReportDataSetBean)dsvbean.getParent()).getId());
                        lstData.add((AbsReportDataPojo)dataObjLocalTmp);
                        continue;
                    }
                }else
                {
                    dataObjTmp=lstData.get(i);
                }
                Object objColValTmp;
                for(ColBean cbTmp:lstColBeans)
                {
                    if(!cbTmp.isMatchDataSet(dsvbean)) continue;
                    if(isMapObj)
                    {
                        objColValTmp=((Map)dataObjLocalTmp).get(cbTmp.getProperty());
                    }else
                    {
                        objColValTmp=((AbsReportDataPojo)dataObjLocalTmp).getColValue(cbTmp);
                    }
                    dataObjTmp.setColValue(cbTmp,objColValTmp);
                }
            }
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("解析报表"+rbean.getPath()+"数据失败",e);
        }
    }

    private void parseDataIntoBean(AbsReportType reportObj,ReportDataSetValueBean dsvbean,ResultSet rs,long size,ReportRequest rrequest,
            List<AbsReportDataPojo> lstData)
    {
        ReportBean rbean=reportObj.getReportBean();
        if(rs==null) return;
        AbsDatabaseType dbtype=rrequest.getDbType(dsvbean.getDatasource());
        int n=0;
        if(size==-1) size=Long.MAX_VALUE;
        List<ColBean> lstColBeans=rrequest.getCdb(rbean.getId()).getLstDynOrderColBeans();
        if(lstColBeans==null||lstColBeans.size()==0) lstColBeans=rbean.getDbean().getLstCols();
        try
        {
            AbsReportDataPojo rowDataObjTmp;
            while(rs.next()&&n++<size)
            {
                if(n>lstData.size())
                {
                    rowDataObjTmp=getPojoClassInstance(rrequest,rbean,rbean.getPojoClassObj());
                    if(rbean.getSbean().isMultiDatasetRows())
                    {//如果配置了多个<dataset/>，则保存每条记录对应的<dataset/>的id
                        rowDataObjTmp.setWx_belongto_datasetid(((ReportDataSetBean)dsvbean.getParent()).getId());
                    }
                    lstData.add(rowDataObjTmp);
                }else
                {
                    rowDataObjTmp=lstData.get(n-1);
                }
                for(ColBean cbTmp:lstColBeans)
                {
                    if(!cbTmp.isMatchDataSet(dsvbean)) continue;//不是从这个数据集中取数据
                    if(cbTmp.getColumn()==null||cbTmp.getColumn().trim().equals("")) continue;
                    if(cbTmp.isNonFromDbCol()||cbTmp.isNonValueCol()||cbTmp.isSequenceCol()||cbTmp.isControlCol()) continue;
                    rowDataObjTmp.setColValue(cbTmp,getColumnValueFromRs(rs,dbtype,cbTmp,rrequest));
                }
            }
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("将报表"+rbean.getPath()+"数据从ResultSet解析到Bean中出错",e);
        }
    }
    
    private Object getColumnValueFromRs(ResultSet rs,AbsDatabaseType dbtype,ColBean colbean,ReportRequest rrequest)
    {
        String column=colbean.getColumn();
        column=column==null?"":column.trim();
        if(!column.equals("")&&!colbean.isNonFromDbCol()&&!colbean.isNonValueCol()&&!colbean.isSequenceCol()&&!colbean.isControlCol())
        {
            try
            {
                Object columnvalue=null;
                if(colbean.isI18n()&&rrequest!=null&&!rrequest.getLocallanguage().trim().equals(""))
                {
                    column=column+"_"+rrequest.getLocallanguage();
                    try
                    {
                        columnvalue=colbean.getDatatypeObj().getColumnValue(rs,column,dbtype);
                    }catch(SQLException sqle)
                    {
                        log.warn("根据列名"+column+"获取数据失败，可能是数据表中不支持"+rrequest.getLocallanguage()+"语言",sqle);
                        columnvalue=colbean.getDatatypeObj().getColumnValue(rs,colbean.getColumn(),dbtype);
                    }
                }else
                {
                    columnvalue=colbean.getDatatypeObj().getColumnValue(rs,column,dbtype);
                }
                return columnvalue;
            }catch(Exception e)
            {


//                    //log.debug("获取报表"+colbean.getReportBean().getPath()+"的列"+colbean.getColumn()+"数据失败，数据集中没有找到此字段名；"+e);


                    throw new WabacusRuntimeException("获取报表"+colbean.getReportBean().getPath()+"的列"+colbean.getColumn()+"数据失败",e);

            }
        }
        return null;
    }

    public Object getPropertyValue(Object dataobj,String property)
    {
        if(dataobj==null||property==null||property.trim().equals(""))
        {
            return null;
        }
        String getMethodName="get"+property.substring(0,1).toUpperCase()+property.substring(1);
        try
        {
            Method getMethod=dataobj.getClass().getMethod(getMethodName,new Class[] {});
            return getMethod.invoke(dataobj,new Object[] {});
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("获取属性："+property+"数据失败",e);
        }

    }

    public String getPropertyValueAsString(Object dataobj,String property,IDataType datatypeObj)
    {
        if(dataobj==null||property==null||property.trim().equals(""))
        {
            return null;
        }
        String getMethodName="get"+property.substring(0,1).toUpperCase()+property.substring(1);
        try
        {
            Method getMethod=dataobj.getClass().getMethod(getMethodName,new Class[] {});
            Object value=getMethod.invoke(dataobj,new Object[] {});
            if(value==null) return null;
            if(datatypeObj==null) return value.toString();
            return datatypeObj.value2label(value);
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("获取属性："+property+"数据失败",e);
        }
    }

    public int calPageCount(int ipagesize,int irecordcount)
    {
        int ipagecount=0;
        if(irecordcount%ipagesize==0)
        {
            ipagecount=irecordcount/ipagesize;
        }else
        {
            ipagecount=irecordcount/ipagesize+1;
        }
        return ipagecount;
    }
    
    public String formatCondition(String src,String token)
    {
        if(src==null||src.trim().length()<2||token==null||token.trim().equals(""))
        {
            return src;
        }
        src=src.trim();
        String dest="";
        if(token.equals("2"))
        {
            dest=src.substring(0,1);
            for(int i=1;i<src.length()-1;i++)
            {
                if(src.charAt(i)!=' ')
                {
                    dest=dest+"%"+src.charAt(i);
                }
            }
            dest=dest+"%"+src.substring(src.length()-1);
        }else
        {
            if(token.equals("1"))
            {
                token=" ";
            }

            while(src.indexOf(token)==0)
            {
                src=src.substring(1);
                src=src.trim();
            }
            while(src.endsWith(token))
            {
                src=src.substring(0,src.length()-1);
                src=src.trim();
            }
            

            StringTokenizer st=new StringTokenizer(src,token);
            while(st.hasMoreElements())
            {
                dest=dest+((String)st.nextElement()).trim()+"%";
                
            }
            if(dest.endsWith("%"))
            {
                dest=dest.substring(0,dest.length()-1);
            }
        }
        log.debug("条件值："+src+"在经过splitlike转换后，变为"+dest);
        return dest;
    }

    public Class buildReportPOJOClass(ReportBean rbean)
    {
        DisplayBean dbean=rbean.getDbean();
        if(dbean==null) return null;
        List<String> lstImports=null;
        String format=null;
        if(rbean.getFbean()!=null)
        {
            format=rbean.getFbean().getFormatContent();
            lstImports=rbean.getFbean().getLstImports();
        }
        format=format==null?"":format.trim();
        return buildPOJOClass(rbean,dbean.getLstCols(),lstImports,format,"Pojo_"+rbean.getPageBean().getId()+rbean.getId());
    }
    
    public Class buildPOJOClass(ReportBean rbean,List<ColBean> lstColBeans,List<String> lstImports,String formatMethod,String className)
    {
        if(formatMethod==null) formatMethod="";
        try
        {
            ClassPool pool=ClassPoolAssistant.getInstance().createClassPool();
            CtClass cclass=pool.makeClass(Consts.BASE_PACKAGE_NAME+"."+className);
            if(lstImports==null) lstImports=new ArrayList<String>();
            if(!lstImports.contains("com.wabacus.config.component.application.report"))
                lstImports.add("com.wabacus.config.component.application.report");
            ClassPoolAssistant.getInstance().addImportPackages(pool,lstImports);
            cclass.setSuperclass(pool.get(AbsReportDataPojo.class.getName()));
            ClassPoolAssistant.getInstance()
                    .addConstructor(
                            cclass,
                            "public "+className+"("+ReportRequest.class.getName()+" rrequest,"+ReportBean.class.getName()
                                    +" rbean){super(rrequest,rbean);\n}");
            if(lstColBeans!=null)
            {
                for(ColBean cbean:lstColBeans)
                {
                    buildFieldAndGetSetMethodForColBean(rbean,cbean,pool,cclass);
                }
            }
            if(!formatMethod.equals(""))
            {
                ClassPoolAssistant.getInstance().addMethod(cclass,"public void format(){"+formatMethod+" \n}");
            }
            if(!rbean.isPojoClassCache())
            {
                cclass.writeFile(Config.homeAbsPath+"WEB-INF/classes");
            }
            Class c=ConfigLoadManager.currentDynClassLoader.loadClass(Consts.BASE_PACKAGE_NAME+"."+className,cclass.toBytecode());
            cclass.detach();
            pool.clearImportedPackages();
            pool=null;
            return c;
        }catch(Exception e)
        {
            throw new WabacusConfigLoadingException("为报表"+rbean.getPath()+"生成类"+className+"时失败，<format/>代码为："+formatMethod,e);
        }
    }

    public void buildFieldAndGetSetMethodForColBean(ReportBean rbean,ColBean cbean,ClassPool pool,CtClass cclass) throws CannotCompileException
    {
        if(cbean==null) return;
        String property=cbean.getProperty();
        if(property==null||property.trim().equals("")) return;
        if(cbean.isNonValueCol()|| 
                cbean.isSequenceCol()||cbean.isControlCol())
        {
            return;
        }
        CtField cfield=ClassPoolAssistant.getInstance().addField(cclass,property,cbean.getDatatypeObj().getCreatedClass(pool),Modifier.PRIVATE);
        CtMethod setMethod=ClassPoolAssistant.getInstance().addSetMethod(cclass,cfield,property);
        ClassPoolAssistant.getInstance().addGetMethod(cclass,cfield,property);
        if(isNeedOriginalColValue(rbean,cbean))
        {
            String propertyOld=property+"_old";
            CtField cfieldOld=ClassPoolAssistant.getInstance().addField(cclass,propertyOld,cbean.getDatatypeObj().getCreatedClass(pool),
                    Modifier.PRIVATE);
            ClassPoolAssistant.getInstance().addGetMethod(cclass,cfieldOld,propertyOld);
            setMethod.insertBefore("if($0."+propertyOld+"==null) $0."+propertyOld+"=$1;");
        }
    }
    
    public void setMethodInfoToColBean(ReportBean rbean)
    {
        if(rbean.getPojoClassObj()==null) return;
        List<ColBean> lstColBeans=rbean.getDbean().getLstCols();
        if(lstColBeans==null||lstColBeans.size()==0) return;
        Class pojoclass=rbean.getPojoClassObj();
        String propertyTmp=null;
        CrossListReportColBean clrcbeanTmp;
        for(ColBean cbeanTmp:lstColBeans)
        {
            try
            {
                if(cbeanTmp==null) continue;
                propertyTmp=cbeanTmp.getProperty();
                if(propertyTmp==null||propertyTmp.trim().equals("")) continue;
                if(cbeanTmp.isNonValueCol()||cbeanTmp.isSequenceCol()||cbeanTmp.isControlCol()) continue;
                clrcbeanTmp=(CrossListReportColBean)cbeanTmp.getExtendConfigDataForReportType(CrossListReportType.KEY);
                if(clrcbeanTmp!=null&&clrcbeanTmp.isDynamicColGroup()) continue;
                String setMethodName="set"+propertyTmp.substring(0,1).toUpperCase()+propertyTmp.substring(1);
                Method setMethod=pojoclass.getMethod(setMethodName,new Class[] { cbeanTmp.getDatatypeObj().getJavaTypeClass() });
                cbeanTmp.setSetMethod(setMethod);

                String getMethodName="get"+propertyTmp.substring(0,1).toUpperCase()+propertyTmp.substring(1);
                Method getMethod=pojoclass.getMethod(getMethodName,new Class[] {});
                cbeanTmp.setGetMethod(getMethod);
            }catch(Exception e)
            {
                throw new WabacusConfigLoadingException("从POJO类"+pojoclass.getClass().getName()+"获取报表"+rbean.getPath()+"的列"
                        +propertyTmp+"的get/set方法失败",e);
            }
        }
    }

    private boolean isNeedOriginalColValue(ReportBean rbean,ColBean cbean)
    {
        AbsReportType reportTypeObj=Config.getInstance().getReportType(rbean.getType());
        if((reportTypeObj instanceof IEditableReportType)
                &&!(reportTypeObj instanceof EditableDetailReportType||reportTypeObj instanceof EditableListFormReportType))
        {
            return true;
        }
        
        //        {//如果当前报表需要提供纯数据的Excel下载
        //            return true;
        
        AbsListReportColBean alrcbean=(AbsListReportColBean)cbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
        if(alrcbean!=null&&alrcbean.isRowgroup())
        {
            AbsListReportBean alrbean=(AbsListReportBean)rbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
            if(alrbean!=null&&alrbean.getSubdisplaybean()!=null&&alrbean.getSubdisplaybean().getMRowGroupSubDisplayRowBeans()!=null
                    &&alrbean.getSubdisplaybean().getMRowGroupSubDisplayRowBeans().size()>0)
            {
                return true;
            }
        }
        return false;
    }
    
    public AbsReportDataPojo getPojoClassInstance(ReportRequest rrequest,ReportBean rbean,Class pojoClassObj)
    {
        try
        {
            if(pojoClassObj==null) return null;
            return (AbsReportDataPojo)pojoClassObj.getConstructor(new Class[] { ReportRequest.class, ReportBean.class }).newInstance(
                    new Object[] { rrequest, rbean });
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("存放报表"+rbean.getPath()+"数据的类"+pojoClassObj.getClass().getName()+"无法实例化",e);
        }
    }
    
    public IButtonClickeventGenerate createButtonEventGeneratorObject(String classname,String clickevent,List<String> lstImports)
    {
        try
        {
            if(clickevent==null||clickevent.trim().equals("")) return null;
            clickevent=clickevent.trim();
            ClassPool pool=new ClassPool();
            pool.appendSystemPath();
            pool.insertClassPath(new ClassClassPath(ReportAssistant.class));
            CtClass cclass=pool.makeClass(Consts.BASE_PACKAGE_NAME+"."+classname+"_Event");

            if(lstImports==null) lstImports=new UniqueArrayList<String>();
            lstImports.add("com.wabacus.system.buttons");
            ClassPoolAssistant.getInstance().addImportPackages(pool,lstImports);

            cclass.setInterfaces(new CtClass[] { pool.get(IButtonClickeventGenerate.class.getName()) });
            CtMethod generateMethod=CtNewMethod.make("public String generateClickEvent("+ReportRequest.class.getName()+"  rrequest,"
                    +AbsButtonType.class.getName()+" buttonObj){"+clickevent+" \n}",cclass);
            cclass.addMethod(generateMethod);
            Class cls=ConfigLoadManager.currentDynClassLoader.loadClass(Consts.BASE_PACKAGE_NAME+"."+classname+"_Event",cclass.toBytecode());
            return (IButtonClickeventGenerate)cls.newInstance();
        }catch(Exception e)
        {
            throw new WabacusConfigLoadingException("生成"+classname+"按钮事件类失败",e);
        }
    }

    public Class buildInterceptorClass(String className,List<String> lstImports,String preaction,String postaction,String saveaction,
            String saverowaction,String savesqlaction,String beforeloaddata,String afterloaddata,String beforedisplay,String displayperrow,
            String displaypercol)
    {
        try
        {
            ClassPool pool=ClassPoolAssistant.getInstance().createClassPool();
            CtClass cclass=pool.makeClass(Consts.BASE_PACKAGE_NAME+"."+className+"_Interceptor");
            List<String> lstImportsLocal=new UniqueArrayList<String>();
            if(lstImports!=null) lstImportsLocal.addAll(lstImports);
            lstImportsLocal.add("com.wabacus.system.intercept");
            lstImportsLocal.add("com.wabacus.config.component.application.report");
            lstImportsLocal.add("com.wabacus.system.component.application.report.configbean.editablereport");
            ClassPoolAssistant.getInstance().addImportPackages(pool,lstImportsLocal);

            cclass.setSuperclass(pool.get(AbsInterceptorDefaultAdapter.class.getName()));
            StringBuffer tmpBuf=null;
            if(preaction!=null&&!preaction.trim().equals(""))
            {
                tmpBuf=new StringBuffer();
                tmpBuf.append("public void doStart("+ReportRequest.class.getName()+" rrequest,"+ReportBean.class.getName()+" rbean) {");
                tmpBuf.append(preaction.trim()).append(" \n}");
                ClassPoolAssistant.getInstance().addMethod(cclass,tmpBuf.toString());
            }
            if(saveaction!=null&&!saveaction.trim().equals(""))
            {
                tmpBuf=new StringBuffer();
                tmpBuf.append("public int doSave("+ReportRequest.class.getName()+" rrequest,"+ReportBean.class.getName()+" rbean,"
                        +AbsEditableReportEditDataBean.class.getName()+" editbean){");
                tmpBuf.append(saveaction.trim()).append(" \n}");
                ClassPoolAssistant.getInstance().addMethod(cclass,tmpBuf.toString());
            }
            if(saverowaction!=null&&!saverowaction.trim().equals(""))
            {
                tmpBuf=new StringBuffer();
                tmpBuf.append("public int doSavePerRow("+ReportRequest.class.getName()+" rrequest,"+ReportBean.class.getName()+" rbean,"
                        +Map.class.getName()+" mRowData,"+Map.class.getName()+" mParamValues,"+AbsEditableReportEditDataBean.class.getName()
                        +" editbean){");
                tmpBuf.append(saverowaction.trim()).append(" \n}");
                ClassPoolAssistant.getInstance().addMethod(cclass,tmpBuf.toString());
            }
            if(savesqlaction!=null&&!savesqlaction.trim().equals(""))
            {
                tmpBuf=new StringBuffer();
                tmpBuf.append("public int doSavePerAction ("+ReportRequest.class.getName()+" rrequest,"+ReportBean.class.getName()+" rbean,"
                        +Map.class.getName()+" mRowData,"+Map.class.getName()+" mParamValues,"+AbsEditActionBean.class.getName()+" actionbean,"
                        +AbsEditableReportEditDataBean.class.getName()+" editbean){");
                tmpBuf.append(savesqlaction.trim()).append(" \n}");
                ClassPoolAssistant.getInstance().addMethod(cclass,tmpBuf.toString());
            }
            if(beforeloaddata!=null&&!beforeloaddata.trim().equals(""))
            {
                tmpBuf=new StringBuffer();
                tmpBuf.append("public Object beforeLoadData("+ReportRequest.class.getName()+" rrequest,"+ReportBean.class.getName()+" rbean,").append(
                        Object.class.getName()+" typeObj,").append(String.class.getName()).append(" sql){");
                tmpBuf.append(beforeloaddata.trim()).append(" \n}");
                ClassPoolAssistant.getInstance().addMethod(cclass,tmpBuf.toString());
            }
            if(afterloaddata!=null&&!afterloaddata.trim().equals(""))
            {
                tmpBuf=new StringBuffer();
                tmpBuf.append("public Object afterLoadData("+ReportRequest.class.getName()+" rrequest,"+ReportBean.class.getName()+" rbean,").append(
                        Object.class.getName()+" typeObj,").append(Object.class.getName()).append(" dataObj){");
                tmpBuf.append(afterloaddata.trim()).append(" \n}");
                ClassPoolAssistant.getInstance().addMethod(cclass,tmpBuf.toString());
            }
            if(beforedisplay!=null&&!beforedisplay.trim().equals(""))
            {
                tmpBuf=new StringBuffer();
                tmpBuf.append("public void beforeDisplayReportData("+ReportRequest.class.getName()+" rrequest,"+ReportBean.class.getName()+" rbean,"
                        +ReportDataBean.class.getName()+" reportDataBean){");
                tmpBuf.append(beforedisplay.trim()).append(" \n}");
                ClassPoolAssistant.getInstance().addMethod(cclass,tmpBuf.toString());
            }
            if(displayperrow!=null&&!displayperrow.trim().equals(""))
            {
                tmpBuf=new StringBuffer();
                tmpBuf.append("public void beforeDisplayReportDataPerRow("+ReportRequest.class.getName()+" rrequest,"+ReportBean.class.getName()+" rbean,"
                        +RowDataBean.class.getName()+" rowDataBean){");
                tmpBuf.append(displayperrow.trim()).append(" \n}");
                ClassPoolAssistant.getInstance().addMethod(cclass,tmpBuf.toString());
            }
            if(displaypercol!=null&&!displaypercol.trim().equals(""))
            {
                tmpBuf=new StringBuffer();
                tmpBuf.append("public void beforeDisplayReportDataPerCol("+ReportRequest.class.getName()+" rrequest,"+ReportBean.class.getName()+" rbean,"
                        +ColDataBean.class.getName()+" colDataBean){");
                tmpBuf.append(displaypercol.trim()).append(" \n}");
                ClassPoolAssistant.getInstance().addMethod(cclass,tmpBuf.toString());
            }
            if(postaction!=null&&!postaction.equals(""))
            {
                tmpBuf=new StringBuffer();
                tmpBuf.append("public void doEnd("+ReportRequest.class.getName()+" rrequest,"+ReportBean.class.getName()+" rbean) {");
                tmpBuf.append(postaction.trim()).append(" \n}");
                ClassPoolAssistant.getInstance().addMethod(cclass,tmpBuf.toString());
            }
            Class c=ConfigLoadManager.currentDynClassLoader.loadClass(Consts.BASE_PACKAGE_NAME+"."+className+"_Interceptor",cclass.toBytecode());
            cclass.detach();
            pool.clearImportedPackages();
            pool=null;
            return c;
        }catch(NotFoundException e)
        {
            throw new WabacusConfigLoadingException("生成"+className+"拦截器字节码时，执行pool.get()失败",e);
        }catch(CannotCompileException e)
        {
            throw new WabacusConfigLoadingException("生成拦截器"+className+"字节码时无法编译",e);
        }catch(IOException ioe)
        {
            throw new WabacusConfigLoadingException("生成拦截器"+className+"字节码时无法将生成的字节码写到本地文件系统",ioe);
        }
    }

    //    /**
    
    
    
    
    
    
    //     */
    
    
    
    
    //        whereclause = whereclause.trim();
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //        {
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //                    throw new WabacusConfigLoadingException("加载报表"
    //                            + sqlbean.getReportBean().getPath() + "失败，在<update/>中配置的更新语句中条件子句不对");
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //                        cb = getCbean(sqlbean,conval);
    
    
    
    
    
    //                {//条件值为常量，则不用解决此条件子句，直接附在后面即可。
    
    
    
    
    
    
    

    
    
    //        if (whereclause == null || whereclause.trim().equals("")) return "";
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //            }
    
    
    

    public boolean shouldShowThisApplication(IComponentConfigBean applicationBean,ReportRequest rrequest)
    {
        if(applicationBean instanceof AbsContainerConfigBean)
        {
            
            throw new WabacusRuntimeException("此方法只能传入应用进行判断，不能传入容器");
        }
        if(applicationBean==null) return false;
        if(rrequest.getSlaveReportBean()!=null)
        {
            if(applicationBean==rrequest.getSlaveReportBean()) return true;
            return false;
        }
        if(applicationBean==rrequest.getRefreshComponentBean()) return true;

        if(rrequest.getRefreshComponentBean() instanceof AbsContainerConfigBean)
        {
            if(((AbsContainerConfigBean)rrequest.getRefreshComponentBean()).isExistChildId(applicationBean.getId(),false,true))
            {
                return true;
            }

        }
        return false;
    }

    public String getColAndConditionDefaultValue(ReportRequest rrequest,String defaultvalue)
    {
        if(defaultvalue==null||defaultvalue.trim().equals("")) return "";
        if(WabacusAssistant.getInstance().isGetRequestContextValue(defaultvalue))
        {
            return WabacusAssistant.getInstance().getRequestContextStringValue(rrequest,defaultvalue,"");
        }
        return defaultvalue.trim();
    }

    
    
    
    
    
    ////        CacheDataBean cdb=null;
    
    //        if(lstExportReportids.size()==1)
    //        {//只下载一个报表
    
    
    ////            IComponentType typeObjTmp=rrequest.getComponentTypeObj(rbean,null,false);
    ////            if(typeObjTmp!=null) cdb=rrequest.getCdb(rbean.getId());//如果本报表参与了本次显示
    
    //        /**
    
    //         */
    //        boolean isAllNonDisplayPermission=true;//本次导出的所有报表是否都没有相应类型的导出链接的显示权限
    
    
    
    
    //            if(!rrequest.checkPermission(reportidTmp,Consts.BUTTON_PART,"type{"+exporttype+"}",Consts.PERMISSION_TYPE_DISPLAY)) continue;//没有显示权限
    
    
    //            {//此报表的此权限不是禁用
    
    
    
    //        if(isAllNonDisplayPermission) return "";//如果本次导出的所有报表都没有显示此类型数据导出功能的权限
    
    //        String clickevent="";
    
    //        {//不是所有报表都禁用这种类型的导出
    
    
    
    
    
    
    
    
    //            {//Consts.DATAEXPORT_RICHEXCEL
    
    
    
    //            {//当前只下载一个参与本次显示的报表，且此报表需要提供列动态选择框
    
    
    //                paramsBuf.append(",skin:\"").append(Config.skin).append("\"");
    
    
    
    
    
    
    
    
    
    
    
    
    
    //                clickevent="createTreeObjHtml(this,'"+Tools.jsParamEncode(paramsBuf.toString())+"',event);";//注意这个方法的第二个参数pageid必须为空，因为在此方法中要据此判断当前是在做下载Excel还是刷新页面的操作
    
    //            {//下载多个报表，或下载的一个报表不参与本次显示，或虽参与本次显示，但不需要提供动态列选择框，直接下载当前页面显示的数据
    //                //clickevent="postlinkurl('"+url+"',true);";
    //                clickevent="exportData('"+pbean.getId()+"','"+validExportReportids+"','"+Config.showreport_onpage_url+"','"+exporturl+"');";
    
    
    
    
    //        {//客户端提供了显示的label
    
    
    
    //            resultBuf.append("</a>");
    
    
    
    
    
    
    
    //            {
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //        return resultBuf.toString();
    
    
    
    
    
    
    
    //        {//如果不是禁用
    
    
    
    
    
    
    
    //                winwidth=300;
    
    
    
    
    
    
    
    
    
    
    //        {//客户端提供了显示的label
    
    
    //            resultBuf.append("</a>");
    
    
    //            label=rrequest.getI18NStringValue((Config.getInstance().getResources()
    
    
    
    
    
    
    //    /**
    
    
    
    //     */
    
    
    
    
    
    //        /**
    //         * 处理所有查询条件
    //         */
    //        boolean hasCondtionWithInputBox=false;//是否有带输入框的查询条件
    
    
    
    
    
    
    
    
    
    //            /**************临时删除*************if(cbeanTmp.getLstConditionValuesBean().size()>1)
    //            {//此条件有多个条件表达式供选择
    
    
    //            }*********************/
    
    
    
    //            }
    
    
    
    
    
    
    
    
    
    
    

    public String getColSelectedLabelAndEvent(ReportRequest rrequest,ReportBean rbean,boolean isListReport)
    {
        StringBuffer paramsBuf=new StringBuffer();
        paramsBuf.append("{reportguid:\"").append(rbean.getGuid()).append("\"");
        paramsBuf.append(",skin:\"").append(rrequest.getPageskin()).append("\"");
        paramsBuf.append(",webroot:\"").append(Config.webroot).append("\"");
        paramsBuf.append(",width:").append(rbean.getDbean().getColselectwidth());
        paramsBuf.append(",maxheight:").append(rbean.getDbean().getColselectmaxheight());
        paramsBuf.append("}");
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("<img src=\""+Config.webroot+"webresources/skin/"+rrequest.getPageskin()+"/images/coltitle_selected/selectcols.gif\"");
        if(isListReport)
        {
            resultBuf.append(" class=\"colSelectedLabel_img\"");
        }else
        {
            resultBuf.append(" style=\"cursor:pointer;\"");
        }
        resultBuf.append(" onclick=\"");
        resultBuf.append("try{createTreeObjHtml(this,'"+Tools.jsParamEncode(paramsBuf.toString())+"',event);}catch(e){logErrorsAsJsFileLoad(e);}\">");
        return resultBuf.toString();
    }
    
     public String getNavigatePagenoWithEvent(ReportRequest rrequest,ReportBean rbean,int pageno,String label)
     {
         StringBuffer resultBuf=new StringBuffer();
         resultBuf.append("<span class=\"cls-navigate-label\" onmouseover=\"this.style.cursor='pointer';\" onclick=\"try{navigateReportPage('");
         resultBuf.append(rbean.getPageBean().getId()).append("','");
         resultBuf.append(rbean.getId()).append("','");
         resultBuf.append(pageno).append("');}catch(e){logErrorsAsJsFileLoad(e);}");
         resultBuf.append("\">").append(label).append("</span>");
         return resultBuf.toString();
     }
     
     public String getNavigateTextBox(ReportRequest rrequest,ReportBean rbean)
     {
         CacheDataBean cdb=rrequest.getCdb(rbean.getId());
         int pagecount=cdb.getPagecount();
         int pageno=cdb.getFinalPageno();
         boolean isReadonly=false;
         if(rrequest.checkPermission(rbean.getId(),Consts.NAVIGATE_PART,Consts_Private.NAVIGATE_PAGENO,Consts.PERMISSION_TYPE_DISABLED))
         {
             isReadonly=true;
         }
         String strpageno=String.valueOf(pagecount).trim();
         int width=strpageno.length()*10;//根据总页数的位数决定文本框的宽度
         if(width<30) width=30;
         String dynstyleproperty="style=\"width:"+width+"px;\"";
         if(!isReadonly&&pagecount>1)
         {
             StringBuffer blurEventBuf=new StringBuffer();
             blurEventBuf.append("if(isPositiveInteger(this.value)&&parseInt(this.value,10)!=").append(pageno).append("){");
             blurEventBuf.append("if(parseInt(this.value,10)>").append(pagecount).append(") this.value='").append(pagecount).append("';");
             blurEventBuf.append("try{navigateReportPage('");
             blurEventBuf.append(rbean.getPageBean().getId()).append("','");
             blurEventBuf.append(rbean.getId()).append("',this.value);}catch(e){logErrorsAsJsFileLoad(e);}");
             blurEventBuf.append("}");
             dynstyleproperty=dynstyleproperty+" onblur=\""+blurEventBuf.toString()+"\"";
         }
         AbsInputBox box=Config.getInstance().getInputBoxByType(TextBox.class);
         return box.getIndependentDisplayString(rrequest,String.valueOf(pageno),dynstyleproperty,null,isReadonly);
     }
     
     public String getNavigateSelectBox(ReportRequest rrequest,ReportBean rbean)
     {
         CacheDataBean cdb=rrequest.getCdb(rbean.getId());
         int pagecount=cdb.getPagecount();
         int pageno=cdb.getFinalPageno();
         boolean isDisabled=false;
         if(rrequest.checkPermission(rbean.getId(),Consts.NAVIGATE_PART,Consts_Private.NAVIGATE_PAGENO,Consts.PERMISSION_TYPE_DISABLED))
         {
             isDisabled=true;
         }
         String dynstyleproperty="name=\""+rbean.getGuid()+"_SELEPAGENUM\"";
         if(!isDisabled&&pagecount>1)
         {
             StringBuffer onchangeEventBuf=new StringBuffer();
             onchangeEventBuf.append("try{navigateReportPage('");
             onchangeEventBuf.append(rbean.getPageBean().getId()).append("','");
             onchangeEventBuf.append(rbean.getId()).append("',this.options[this.options.selectedIndex].value);}catch(e){logErrorsAsJsFileLoad(e);}");
             dynstyleproperty=dynstyleproperty+" onchange=\""+onchangeEventBuf.toString()+"\"";
         }
         List<String[]> lstOptionsResult=new ArrayList<String[]>();
         if(pagecount<=0)
         {
             lstOptionsResult.add(new String[] { String.valueOf(0), String.valueOf(0) });
         }else
         {
             for(int j=1;j<=pagecount;j++)
             {
                 lstOptionsResult.add(new String[] { String.valueOf(j), String.valueOf(j) });
             }
         }
         AbsInputBox box=Config.getInstance().getInputBoxByType(SelectBox.class);
         return box.getIndependentDisplayString(rrequest,String.valueOf(pageno),dynstyleproperty,lstOptionsResult,isDisabled);
     }
     
     public String getNavigateSelectBoxForPagesizeConvert(ReportRequest rrequest,
             ReportBean rbean)
     {
         AbsListReportBean alrbean=(AbsListReportBean)rbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
         if(alrbean==null) return "";
         CacheDataBean cdb=rrequest.getCdb(rbean.getId());
         int currentpagesize=cdb.getPagesize();
         boolean isDisabled=false;
         if(rrequest.checkPermission(rbean.getId(),Consts.NAVIGATE_PART,Consts_Private.NAVIGATE_PAGESIZE,Consts.PERMISSION_TYPE_DISABLED))
         {
             isDisabled=true;
         }
         StringBuffer onchangeEventBuf=new StringBuffer();
         onchangeEventBuf.append("var url=getComponentUrl('").append(rbean.getPageBean().getId()).append("','");
         onchangeEventBuf.append(rbean.getRefreshGuid()).append("','");
         if(rbean.isSlaveReportDependsonListReport())
         {
             onchangeEventBuf.append(rbean.getId());
         }
         onchangeEventBuf.append("');");
         onchangeEventBuf.append("url=replaceUrlParamValue(url,'").append(rbean.getId()).append("_PREV_PAGESIZE','"+currentpagesize+"');");
         onchangeEventBuf.append("url=replaceUrlParamValue(url,'").append(rbean.getId()).append(
                 "_PAGESIZE',this.options[this.options.selectedIndex].value);");
         onchangeEventBuf.append("refreshComponent(url);");

         List<String[]> lstOptionsResult=new ArrayList<String[]>();
         List<Integer> lstPagesizeTmp=rbean.getLstPagesize();
         String alldata_label="";
         if(lstPagesizeTmp.contains(-1))
         {
             alldata_label=rrequest.getI18NStringValue((Config.getInstance().getResources().getString(rrequest,rbean.getPageBean(),
                     Consts.NAVIGATE_ALLDATA_LABEL,true)).trim());
         }
         boolean isExistCurrentPagesizeOption=false;
         String labelTmp;
         for(int i=0;i<lstPagesizeTmp.size();i++)
         {
             labelTmp=lstPagesizeTmp.get(i)==-1?alldata_label:String.valueOf(lstPagesizeTmp.get(i));
             lstOptionsResult.add(new String[] { labelTmp, String.valueOf(lstPagesizeTmp.get(i)) });
             if(currentpagesize==lstPagesizeTmp.get(i)) isExistCurrentPagesizeOption=true;
         }
         if(!isExistCurrentPagesizeOption) lstOptionsResult.add(0,new String[] { String.valueOf(currentpagesize), String.valueOf(currentpagesize) });
         AbsInputBox box=Config.getInstance().getInputBoxByType(SelectBox.class);
         return box.getIndependentDisplayString(rrequest,String.valueOf(currentpagesize),"onchange=\""+onchangeEventBuf.toString()+"\"",
                 lstOptionsResult,isDisabled);
     }
     
     public String getHorizontalRowValueStylepropertyKey(String datasetid,String colproperty,boolean isMultiDatasetRows)
     {
         if(isMultiDatasetRows)
         {//如果是多数据集，则它的key为property+datasetid，因为每个<dataset/>都可能查出一条此列对应的记录，所以加上<dataset/>的id做为key
             colproperty=datasetid+"_col_"+colproperty;
         }
         return colproperty;
     }
}
