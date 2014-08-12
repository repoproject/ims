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
package com.wabacus.system.inputbox.option;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wabacus.config.component.ComponentConfigLoadAssistant;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.SqlBean;
import com.wabacus.config.typeprompt.TypePromptBean;
import com.wabacus.config.typeprompt.TypePromptColBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.dataset.ISqlDataSet;
import com.wabacus.system.dataset.sqldataset.GetAllDataSetByPreparedSQL;
import com.wabacus.system.dataset.sqldataset.GetAllDataSetBySQL;
import com.wabacus.system.inputbox.AbsSelectBox;
import com.wabacus.system.inputbox.TextBox;
import com.wabacus.util.Tools;

public class SQLOptionDatasource extends AbsOptionDatasource
{
    private String datasource;//当前SQL语句执行时所用的数据源名字，如果没有在<option/>中配置，则用<sql/>的datasource的值
    
    private String sql;
    
    private List<ConditionBean> lstConditions=new ArrayList<ConditionBean>();//当为SQL语句查询选项数据时，且需要从request/session中获取条件数据，这里存放所有的条件bean
    
    public String getDatasource()
    {
        if(this.datasource==null||this.datasource.trim().equals(""))
        {
            this.datasource=this.ownerOptionBean.getOwnerInputboxObj().getOwner().getReportBean().getSbean().getDatasource();
        }
        return datasource;
    }

    public void setDatasource(String datasource)
    {
        this.datasource=datasource;
    }

    public String getSql()
    {
        return sql;
    }

    public void setSql(String sql)
    {
        this.sql=sql;
    }

    public List<ConditionBean> getLstConditions()
    {
        return lstConditions;
    }

    public void setLstConditions(List<ConditionBean> lstConditions)
    {
        this.lstConditions=lstConditions;
    }

    public List<Map<String,String>> getLstSelectBoxOptions(ReportRequest rrequest,Map<String,String> mParentInputboxValues)
    {
        String sql=this.sql;
        Map<String,Boolean> mParentIds=((AbsSelectBox)this.getOwnerOptionBean().getOwnerInputboxObj()).getMParentIds();
        if(mParentIds!=null&&mParentIds.size()>0)
        {
            String  parentValTmp;
            for(String parentNameTmp:mParentIds.keySet())
            {
                parentValTmp=mParentInputboxValues.get(parentNameTmp);
                parentValTmp=Tools.removeSQLKeyword(parentValTmp);
                if(parentValTmp==null||parentValTmp.equals(""))
                {
                    sql=Tools.replaceAll(sql,"#["+parentNameTmp+"]#","");
                    sql=Tools.replaceAll(sql,"#"+parentNameTmp+"#","_WX_NONE_OPTION_VALUE_");
                }else if(parentValTmp.equals("[%ALL%]"))
                {
                    sql=Tools.replaceAll(sql,"#["+parentNameTmp+"]#","");
                    sql=Tools.replaceAll(sql,"#"+parentNameTmp+"#","");
                }else
                {
                    sql=Tools.replaceAll(sql,"#["+parentNameTmp+"]#",parentValTmp);
                    sql=Tools.replaceAll(sql,"#"+parentNameTmp+"#",parentValTmp);
                }
            }
        }
        Map<String,String> mColKeyAndColumn=new HashMap<String,String>();
        mColKeyAndColumn.put("label",((SelectboxOptionBean)this.ownerOptionBean).getLabel());
        mColKeyAndColumn.put("value",((SelectboxOptionBean)this.ownerOptionBean).getValue());
        return getOptionListFromDB(rrequest,sql,mColKeyAndColumn);
    }

    private List<Map<String,String>> getOptionListFromDB(ReportRequest rrequest,String sql,Map<String,String> mColKeyAndColumns)
    {
        List<Map<String,String>> lstResults=new ArrayList<Map<String,String>>();
        try
        {
            ReportBean rbean=this.ownerOptionBean.getOwnerInputboxObj().getOwner().getReportBean();
            ISqlDataSet ImpISQLType=null;
            if(rbean.getSbean().getStatementType()==SqlBean.STMTYPE_PREPAREDSTATEMENT)
            {
                ImpISQLType=new GetAllDataSetByPreparedSQL();
            }else
            {
                ImpISQLType=new GetAllDataSetBySQL();
            }
            Object objTmp=ImpISQLType.getDataSet(rrequest,rbean,this.ownerOptionBean,sql,this.lstConditions,this.getDatasource());
            if(objTmp instanceof List)
            {
                for(Object itemTmp:(List)objTmp)
                {
                    if(itemTmp==null) continue;
                    if(!(itemTmp instanceof Map))
                    {
                        throw new WabacusRuntimeException("加载报表"+rbean.getPath()+"选项数据的拦截器返回的List对象中元素类型不对，必须为Map类型");
                    }
                    lstResults.add((Map<String,String>)itemTmp);
                }
            }else if(objTmp instanceof ResultSet)
            {
                ResultSet rs=(ResultSet)objTmp;
                Map<String,String> mOptionTmp;
                while(rs.next())
                {
                    mOptionTmp=new HashMap<String,String>();
                    for(Entry<String,String> entryColTmp:mColKeyAndColumns.entrySet())
                    {
                        String valueTmp=rs.getString(entryColTmp.getValue());
                        valueTmp=valueTmp==null?"":valueTmp.trim();
                        mOptionTmp.put(entryColTmp.getKey(),valueTmp);
                    }
                    lstResults.add(mOptionTmp);
                }
                rs.close();
            }else if(objTmp!=null)
            {
                throw new WabacusRuntimeException("加载报表"+rbean.getPath()+"的选项数据失败，在加载选项数据的拦截器中返回的对象类型"+objTmp.getClass().getName()+"不合法");
            }
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("执行SQL语句："+sql+" 从数据库中获取选项失败",e);
        }
        return lstResults;
    }
    
    public List<Map<String,String>> getLstTypePromptOptions(ReportRequest rrequest,String txtValue)
    {
        txtValue=txtValue==null?"":txtValue.trim();
        List<Map<String,String>> lstResults=new ArrayList<Map<String,String>>();
        ReportBean rbean=this.ownerOptionBean.getOwnerInputboxObj().getOwner().getReportBean();
        try
        {
            TypePromptBean typePromptBean=((TextBox)this.ownerOptionBean.getOwnerInputboxObj()).getTypePromptBean();
            List<TypePromptColBean> lstPColsBean=typePromptBean.getLstPColBeans();
            if(lstPColsBean==null||lstPColsBean.size()==0) return null;
            ISqlDataSet sqlDataSet=null;
            if(rbean.getSbean().getStatementType()==SqlBean.STMTYPE_PREPAREDSTATEMENT)
            {
                sqlDataSet=new GetAllDataSetByPreparedSQL();
            }else
            {
                sqlDataSet=new GetAllDataSetBySQL();
                txtValue=Tools.removeSQLKeyword(txtValue);
            }
            String sqlTemp=Tools.replaceAll(sql,"#data#",txtValue);
            Object objTmp=sqlDataSet.getDataSet(rrequest,rbean,this,sqlTemp,this.getLstConditions(),this.getDatasource());
            int cnt=0;
            if(objTmp instanceof List)
            {
                for(Object itemTmp:(List)objTmp)
                {
                    if(itemTmp==null) continue;
                    if(!(itemTmp instanceof Map))
                    {
                        throw new WabacusRuntimeException("加载报表"+rbean.getPath()
                                +"输入联想选项数据的拦截器返回的List对象中元素类型不对，必须为Map<String,String>类型，其中key为<typeprompt/>的value或label属性配置值，value为相应的选项数据");
                    }
                    lstResults.add((Map<String,String>)itemTmp);
                    if(++cnt==typePromptBean.getResultcount()) break;
                }
            }else if(objTmp instanceof ResultSet)
            {
                Map<String,String> mCols;
                String labelTmp,valueTmp;
                ResultSet rs=(ResultSet)objTmp;
                while(rs.next())
                {
                    mCols=new HashMap<String,String>();
                    for(TypePromptColBean tpcbean:lstPColsBean)
                    {
                        labelTmp=rs.getString(tpcbean.getLabel());
                        mCols.put(tpcbean.getLabel(),labelTmp==null?"":labelTmp.trim());
                        if(tpcbean.getValue()!=null&&!tpcbean.getValue().trim().equals("")&&!tpcbean.getValue().equals(tpcbean.getLabel()))
                        {
                            valueTmp=rs.getString(tpcbean.getValue());
                            mCols.put(tpcbean.getValue(),valueTmp==null?"":valueTmp.trim());
                        }
                    }
                    lstResults.add(mCols);
                    if(++cnt==typePromptBean.getResultcount()) break;
                }
                rs.close();
            }else if(objTmp!=null)
            {
                throw new WabacusRuntimeException("加载报表"+rbean.getPath()+"的输入联想选项数据失败，在加载输入联想选项数据的拦截器中返回的对象类型"+objTmp.getClass().getName()+"不合法");
            }
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("从数据库为报表"+rbean.getPath()+"获取输入提示数据失败",e);
        }
        return lstResults;
    }
    
    public void doPostLoad()
    {
        if(this.lstConditions!=null)
        {
            ReportBean rbean=this.ownerOptionBean.getOwnerInputboxObj().getOwner().getReportBean();
            boolean isPreparedStmt=false;
            if(rbean.getSbean().getStatementType()==SqlBean.STMTYPE_PREPAREDSTATEMENT)
            {
                isPreparedStmt=true;
            }
            for(ConditionBean cbTmp:this.lstConditions)
            {
                if(isPreparedStmt) cbTmp.getConditionExpression().parseConditionExpression();
                if(cbTmp.isConditionValueFromUrl())
                {
                    if(this.ownerOptionBean.getOwnerInputboxObj() instanceof AbsSelectBox
                            &&((AbsSelectBox)this.ownerOptionBean.getOwnerInputboxObj()).isDependsOtherInputbox())
                    {
                        throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()
                                +"配置的选择框类型的输入框失败，依赖其它选择框的子选择框的查询条件的数据不能配置为从url中获取，只能配置为从session中获取");
                    }
                    rbean.addParamNameFromURL(cbTmp.getName());
                }
            }
        }
    }
    
    protected AbsOptionDatasource clone(AbsOptionBean newOwnerOptionBean) throws CloneNotSupportedException
    {
        SQLOptionDatasource newDatasourceObj=(SQLOptionDatasource)super.clone(newOwnerOptionBean);
        newDatasourceObj.lstConditions=ComponentConfigLoadAssistant.getInstance().cloneLstConditionBeans(null,lstConditions);
        return newDatasourceObj;
    }
}

