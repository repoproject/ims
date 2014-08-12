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
package com.wabacus.system.component.application.report.abstractreport.configbean.statistic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.config.component.application.report.SqlBean;
import com.wabacus.config.database.type.AbsDatabaseType;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.abstractreport.AbsListReportType;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportDisplayBean;
import com.wabacus.system.dataset.IReportDataSet;
import com.wabacus.util.Tools;

public class StatisticItemAndDataSetBean implements Comparable<StatisticItemAndDataSetBean>
{
    public final static String STATISQL_PLACEHOLDER="%STATISTIC_SQL%";
    
    private ReportDataSetValueBean datasetbean;

    private String statiReportSqlWithoutCondition;

    private List<StatisticItemBean> lstReportStatitemBeansWithCondition;

    private List<StatisticItemBean> lstReportStatitemBeansWithoutCondition;
    
    private String statiPageSqlWithoutCondition;

    private List<StatisticItemBean> lstPageStatitemBeansWithCondition;

    private List<StatisticItemBean> lstPageStatitemBeansWithoutCondition;
    
    private String statiSqlWithCondition;
    
    public void addStaticItemBean(StatisticItemBean staticitembean)
    {
        if(staticitembean.getLstConditions()==null||staticitembean.getLstConditions().size()==0)
        {
            if(staticitembean.getStatiscope()==StatisticItemBean.STATSTIC_SCOPE_ALL
                    ||staticitembean.getStatiscope()==StatisticItemBean.STATSTIC_SCOPE_REPORT)
            {
                if(this.lstReportStatitemBeansWithoutCondition==null) this.lstReportStatitemBeansWithoutCondition=new ArrayList<StatisticItemBean>();
                this.lstReportStatitemBeansWithoutCondition.add(staticitembean);
            }
            if(staticitembean.getStatiscope()==StatisticItemBean.STATSTIC_SCOPE_ALL
                    ||staticitembean.getStatiscope()==StatisticItemBean.STATSTIC_SCOPE_PAGE)
            {
                if(this.lstPageStatitemBeansWithoutCondition==null) this.lstPageStatitemBeansWithoutCondition=new ArrayList<StatisticItemBean>();
                this.lstPageStatitemBeansWithoutCondition.add(staticitembean);
            }
        }else
        {
            if(staticitembean.getStatiscope()==StatisticItemBean.STATSTIC_SCOPE_ALL
                    ||staticitembean.getStatiscope()==StatisticItemBean.STATSTIC_SCOPE_REPORT)
            {
                if(this.lstReportStatitemBeansWithCondition==null) this.lstReportStatitemBeansWithCondition=new ArrayList<StatisticItemBean>();
                this.lstReportStatitemBeansWithCondition.add(staticitembean);
            }
            if(staticitembean.getStatiscope()==StatisticItemBean.STATSTIC_SCOPE_ALL
                    ||staticitembean.getStatiscope()==StatisticItemBean.STATSTIC_SCOPE_PAGE)
            {
                if(this.lstPageStatitemBeansWithCondition==null) this.lstPageStatitemBeansWithCondition=new ArrayList<StatisticItemBean>();
                this.lstPageStatitemBeansWithCondition.add(staticitembean);
            }
            
        }
    }

    public ReportDataSetValueBean getDatasetbean()
    {
        return datasetbean;
    }

    public void setDatasetbean(ReportDataSetValueBean datasetbean)
    {
        this.datasetbean=datasetbean;
    }

    public void buildStatisticSql()
    {

//        {//当前报表是用存储过程查询数据
//            sql="%ORIGINAL_SQL%";//因为存储过程中SQL语句是动态拼凑的，所以这里放上占位符，而不是真正的SQL语句，以便在存储过程中真正替换



//            //            sql=this.datasetbean.getSqlWithoutOrderby();
//            //            sql=Tools.replaceAll(sql,"%orderby%","");
//            //            sql=Tools.replaceAll(sql,Consts_Private.PLACEHOLDER_LISTREPORT_SQLKERNEL,this.datasetbean.getSql_kernel());
//        }
        this.statiReportSqlWithoutCondition=parseStatiSqlWithoutCondition(this.lstReportStatitemBeansWithoutCondition,STATISQL_PLACEHOLDER);
        this.statiPageSqlWithoutCondition=parseStatiSqlWithoutCondition(this.lstPageStatitemBeansWithoutCondition,STATISQL_PLACEHOLDER);
        if((this.lstReportStatitemBeansWithCondition!=null&&this.lstReportStatitemBeansWithCondition.size()>0)
                ||(this.lstPageStatitemBeansWithCondition!=null&&this.lstPageStatitemBeansWithCondition.size()>0))
        {
            this.statiSqlWithCondition="select %SELECTEDCOLUMNS% from (select * from ("+STATISQL_PLACEHOLDER+") wx_tableStati1 %CONDITION%) tableStati2";
        }
    }
    
    private String parseStatiSqlWithoutCondition(List<StatisticItemBean> lstStatitemBeansWithoutCondition,String sql)
    {
        if(lstStatitemBeansWithoutCondition==null||lstStatitemBeansWithoutCondition.size()==0) return "";
        StringBuffer statisticColumnsBuf=new StringBuffer();
        for(StatisticItemBean statItemBeanTmp:lstStatitemBeansWithoutCondition)
        {
            statisticColumnsBuf.append(statItemBeanTmp.getValue()).append(" as ").append(statItemBeanTmp.getProperty()).append(",");
        }
        if(statisticColumnsBuf.length()>0&&statisticColumnsBuf.charAt(statisticColumnsBuf.length()-1)==',')
        {
            statisticColumnsBuf.deleteCharAt(statisticColumnsBuf.length()-1);
        }
        String sqlStati="";
        if(statisticColumnsBuf.length()>0)
        {
            sqlStati="select "+statisticColumnsBuf.toString()+" from ("+sql+") wx_tableStati";
        }
        return sqlStati;
    }
    
    public boolean loadStatisticData(AbsListReportType listReportTypeObj,Object statiDataObj,String groupbyClause)
    {
        boolean hasStatisticData=false;
        ReportRequest rrequest=listReportTypeObj.getReportRequest();
        IReportDataSet datasetObj=this.datasetbean.createLoadAllDataSetObj(rrequest,listReportTypeObj);
        hasStatisticData|=doLoadStatisticData(listReportTypeObj,statiDataObj,groupbyClause,datasetObj,this.statiReportSqlWithoutCondition,
                this.lstReportStatitemBeansWithoutCondition,this.statiSqlWithCondition,this.lstReportStatitemBeansWithCondition,false);
        if(groupbyClause==null||groupbyClause.trim().equals(""))
        {
            datasetObj=this.datasetbean.createDataSetObj(rrequest,listReportTypeObj);
            hasStatisticData|=doLoadStatisticData(listReportTypeObj,statiDataObj,null,datasetObj,this.statiPageSqlWithoutCondition,
                    this.lstPageStatitemBeansWithoutCondition,this.statiSqlWithCondition,this.lstPageStatitemBeansWithCondition,true);
        }


//        {//当前是分页显示在页面上，则统计针对每页数据的统计（对分组的统计不会对每页数据进行统计）




        return hasStatisticData;
    }

    private boolean doLoadStatisticData(AbsListReportType listReportTypeObj,Object statiDataObj,String groupbyClause,IReportDataSet datasetObj,
            String sqlWithoutCondition,List<StatisticItemBean> lstStatitemsWithoutCondition,String sqlWithCondition,
            List<StatisticItemBean> lstStatitemsWithCondition,boolean isStatiForOnePage)
    {
        ReportRequest rrequest=listReportTypeObj.getReportRequest();
        ReportBean rbean=listReportTypeObj.getReportBean();
        AbsListReportBean alrbean=(AbsListReportBean)rbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
        AbsListReportDisplayBean alrdbean=(AbsListReportDisplayBean)rbean.getDbean().getExtendConfigDataForReportType(AbsListReportType.KEY);
        boolean hasStatisticData=false;
        AbsDatabaseType dbtype=rrequest.getDbType(this.datasetbean.getDatasource());
        try
        {
            if(lstStatitemsWithoutCondition!=null&&lstStatitemsWithoutCondition.size()>0)
            {
                if(groupbyClause!=null&&!groupbyClause.trim().equals("")&&this.datasetbean.isMatchDatasetid(alrdbean.getRowgroupDatasetId()))
                {
                    sqlWithoutCondition=sqlWithoutCondition+"  "+groupbyClause;
                }
                Object objTmp=datasetObj.getStatisticDataSet(rrequest,listReportTypeObj,datasetbean,alrbean.getSubdisplaybean(),sqlWithoutCondition,isStatiForOnePage);
                if(objTmp!=null)
                {
                    if(!(objTmp instanceof ResultSet))
                    {
                        throw new WabacusRuntimeException("加载报表"+rbean.getPath()+"数据失败，在加载数据的前置动作中，如果是统计数据的SQL语句，则只能返回SQL语句或ResultSet，不能返回加载好的List对象");
                    }
                    ResultSet rs=(ResultSet)objTmp;
                    if(rs.next())
                    {
                        for(StatisticItemBean alrsibeanTmp:lstStatitemsWithoutCondition)
                        {//循环每个统计项
                            Object colVal=alrsibeanTmp.getDatatypeObj().getColumnValue(rs,alrsibeanTmp.getProperty(),dbtype);
                            if(!isStatiForOnePage)
                            {
                                alrsibeanTmp.getSetMethod().invoke(statiDataObj,new Object[] { colVal });
                            }else
                            {
                                alrsibeanTmp.getPageStatiSetMethod().invoke(statiDataObj,new Object[] { colVal });
                            }
                        }
                        hasStatisticData=true;
                    }
                    rs.close();
                }
            }
            if(lstStatitemsWithCondition!=null&&lstStatitemsWithCondition.size()>0)
            {
                String sqlTmp;
                for(StatisticItemBean alrsibeanTmp:lstStatitemsWithCondition)
                {
                    sqlTmp=Tools.replaceAll(sqlWithCondition,"%SELECTEDCOLUMNS%",alrsibeanTmp.getValue()+" as "+alrsibeanTmp.getProperty());
                    String realcondition=getRealStatisticItemConditionValues(rrequest,alrsibeanTmp.getLstConditions());
                    if(realcondition.trim().equals(""))
                    {
                        sqlTmp=Tools.replaceAll(sqlTmp,"%CONDITION%","");
                    }else
                    {
                        sqlTmp=Tools.replaceAll(sqlTmp,"%CONDITION%"," where "+realcondition);
                    }
                    if(groupbyClause!=null&&!groupbyClause.trim().equals("")&&this.datasetbean.isMatchDatasetid(alrdbean.getRowgroupDatasetId()))
                    {
                        sqlTmp=sqlTmp+"  "+groupbyClause;
                    }
                    Object objTmp=datasetObj.getStatisticDataSet(rrequest,listReportTypeObj,datasetbean,alrbean.getSubdisplaybean(),sqlTmp,isStatiForOnePage);
                    if(objTmp!=null)
                    {
                        if(!(objTmp instanceof ResultSet))
                        {
                            throw new WabacusRuntimeException("加载报表"+rbean.getPath()
                                    +"数据失败，在加载数据的前置动作中，如果是统计数据的SQL语句，则只能返回SQL语句或ResultSet，不能返回加载好的List对象");
                        }
                        ResultSet rs=(ResultSet)objTmp;
                        if(rs.next())
                        {
                            Object colVal=alrsibeanTmp.getDatatypeObj().getColumnValue(rs,alrsibeanTmp.getProperty(),dbtype);
                            if(!isStatiForOnePage)
                            {
                                alrsibeanTmp.getSetMethod().invoke(statiDataObj,new Object[] { colVal });
                            }else
                            {
                                alrsibeanTmp.getPageStatiSetMethod().invoke(statiDataObj,new Object[] { colVal });
                            }
                            hasStatisticData=true;
                        }
                        rs.close();
                    }
                }
            }
        }catch(SQLException sqle)
        {
            throw new WabacusRuntimeException("查询报表"+listReportTypeObj.getReportBean().getPath()+"统计数据失败",sqle);
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("设置报表"+listReportTypeObj.getReportBean().getPath()+"统计数据到POJO对象中失败",e);
        }
        return hasStatisticData;
    }
    
    private String getRealStatisticItemConditionValues(ReportRequest rrequest,List<ConditionBean> lstConditionBeans)
    {
        if(lstConditionBeans==null||lstConditionBeans.size()==0) return "";
        StringBuffer resultBuf=new StringBuffer();
        String conditionValTmp;
        for(ConditionBean cbeanTmp:lstConditionBeans)
        {
            if(cbeanTmp.isConstant())
            {
                conditionValTmp=cbeanTmp.getConditionExpression().getValue();
            }else
            {
                conditionValTmp=cbeanTmp.getDynamicConditionvalueForSql(rrequest,-1);
            }
            if(conditionValTmp!=null&&!conditionValTmp.trim().equals("")) resultBuf.append(conditionValTmp).append(" and ");
        }
        conditionValTmp=resultBuf.toString().trim();
        if(conditionValTmp.endsWith(" and"))
        {
            conditionValTmp=conditionValTmp.substring(0,conditionValTmp.length()-4);
        }
        return conditionValTmp;
    }
    
    public int compareTo(StatisticItemAndDataSetBean otherBean)
    {
        SqlBean sbean=datasetbean.getReportBean().getSbean();
        ReportDataSetBean dsbeanOther=(ReportDataSetBean)otherBean.getDatasetbean().getParent();
        ReportDataSetBean dsbeanMe=(ReportDataSetBean)this.datasetbean.getParent();
        int myIdx=0, otherIdx=0;
        if(dsbeanMe.getId().equals(dsbeanOther.getId()))
        {//是在同一个<dataset/>下面，则比较它们在本数据集中lstValueBeans中出现的顺序
            ReportDataSetValueBean dsvbeanTmp;
            for(int i=0;i<dsbeanMe.getLstValueBeans().size();i++)
            {
                dsvbeanTmp=dsbeanMe.getLstValueBeans().get(i);
                if(this.datasetbean.getId().equals(dsvbeanTmp.getId()))
                {
                    myIdx=i;
                }else if(otherBean.getDatasetbean().getId().equals(dsvbeanTmp.getId()))
                {
                    otherIdx=i;
                }
            }
        }else
        {//在不同的<dataset/>下面，则比较它们所在<dataset/>的配置顺序
            ReportDataSetBean dsbTmp;
            for(int i=0;i<sbean.getLstDatasetBeans().size();i++)
            {
                dsbTmp=sbean.getLstDatasetBeans().get(i);
                if(dsbeanMe.getId().equals(dsbTmp.getId()))
                {
                    myIdx=i;
                }else if(dsbeanOther.getId().equals(dsbTmp.getId()))
                {
                    otherIdx=i;
                }
            }
        }
        return myIdx>otherIdx?1:-1;
    }
}
