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
package com.wabacus.config.component.application.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wabacus.config.Config;
import com.wabacus.config.component.ComponentConfigLoadAssistant;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.ReportRequest;
import com.wabacus.util.Consts;

public class SqlBean extends AbsConfigBean
{
    public final static int STMTYPE_STATEMENT=1;
    
    public final static int STMTYPE_PREPAREDSTATEMENT=2;
    
    private int stmttype=STMTYPE_STATEMENT;

    private String datasource;//此报表所使用的数据源，默认为wabacus.cfg.xml中<datasources/>标签中的default属性配置的值

//    private Object searchTemplateObj;//搜索栏的显示模板，可能是字符串或TemplateBean
    
    private List<ConditionBean> lstConditions=new ArrayList<ConditionBean>();

    private Map<String,ConditionBean> mConditions;//运行时由lstConditions生成，方便根据<condition/>的name属性取到对应的ConditionBean对象
    
    private List<String> lstConditionFromRequestNames;
    
    private String beforeSearchMethod;
    
    private List<ReportDataSetBean> lstDatasetBeans;//存放所有<value/>子标签，存放顺序为它们的执行顺序
    
    private Map<String,ReportDataSetBean> mDatasetBeans;
    
    public SqlBean(AbsConfigBean parent)
    {
        super(parent);
    }

    public int getStatementType()
    {
        return this.stmttype;
    }
    
    public String getBeforeSearchMethod()
    {
        return beforeSearchMethod;
    }

    public void setBeforeSearchMethod(String beforeSearchMethod)
    {
        this.beforeSearchMethod=beforeSearchMethod;
    }

    public void setStatementType(String statementtype)
    {
        if(statementtype==null||statementtype.trim().equals(""))
            statementtype=Config.getInstance().getSystemConfigValue("default-sqltype","statement");
        statementtype=statementtype.toLowerCase().trim();
        if(statementtype.equals("statement"))
        {
            this.stmttype=STMTYPE_STATEMENT;
        }else if(statementtype.equals("preparedstatement"))
        {
            this.stmttype=STMTYPE_PREPAREDSTATEMENT;
        }
    }

    public List<ConditionBean> getLstConditions()
    {
        return lstConditions;
    }

    public void setLstConditions(List<ConditionBean> lstConditions)
    {
        this.lstConditions=lstConditions;
    }

    public String getDatasource()
    {
        return datasource;
    }

    public void setDatasource(String datasource)
    {
        this.datasource=datasource;
    }
    
    public List<ReportDataSetBean> getLstDatasetBeans()
    {
        return lstDatasetBeans;
    }
    
    public void setLstDatasetBeans(List<ReportDataSetBean> lstDatasetBeans)
    {
        this.lstDatasetBeans=lstDatasetBeans;
        if(lstDatasetBeans==null||lstDatasetBeans.size()==0)
        {
            this.mDatasetBeans=null;
        }else
        {
            this.mDatasetBeans=new HashMap<String,ReportDataSetBean>();
            for(ReportDataSetBean dsbeanTmp:this.lstDatasetBeans)
            {
                mDatasetBeans.put(dsbeanTmp.getId(),dsbeanTmp);
            }
        }
    }

    public boolean isMultiDatasetRows()
    {
        return this.lstDatasetBeans!=null&&this.lstDatasetBeans.size()>1;
    }
    
    public boolean isMultiDataSetCols()
    {
        if(this.lstDatasetBeans==null||this.lstDatasetBeans.size()==0) return false;
        for(ReportDataSetBean datasetBeanTmp:this.lstDatasetBeans)
        {
            if(datasetBeanTmp.getLstValueBeans()!=null&&datasetBeanTmp.getLstValueBeans().size()>1) return true;
        }
        return false;
    }
    
    public ReportDataSetBean getDatasetBeanById(String datasetid)
    {
        if(this.mDatasetBeans==null) return null;
        if(datasetid==null||datasetid.trim().equals("")) datasetid=Consts.DEFAULT_KEY;
        return this.mDatasetBeans.get(datasetid);
    }
    
    public List<ReportDataSetValueBean> getLstDatasetValueBeansByValueid(String valueid)
    {
        if(this.mDatasetBeans==null) return null;
        if(valueid==null||valueid.trim().equals("")) valueid=Consts.DEFAULT_KEY;
        List<ReportDataSetValueBean> lstResults=new ArrayList<ReportDataSetValueBean>();
        ReportDataSetValueBean dsvbeanTmp;
        for(ReportDataSetBean dsbeanTmp:this.lstDatasetBeans)
        {
            dsvbeanTmp=dsbeanTmp.getDatasetValueBeanById(valueid);
            if(dsvbeanTmp!=null) lstResults.add(dsvbeanTmp);
        }
        return lstResults;
    }
    
    public boolean isExistDependentDataset(String dsvalueid)
    {
        if(lstDatasetBeans==null) return false;
        if(dsvalueid==null||dsvalueid.trim().equals("")) dsvalueid=Consts.DEFAULT_KEY;
        for(ReportDataSetBean dsbeanTmp:lstDatasetBeans)
        {
            if(dsbeanTmp.isDependentDatasetValue(dsvalueid)) return true;
        }
        return false;
    }
    
    public List<String> getLstConditionFromUrlNames()
    {
        if(lstConditionFromRequestNames==null&&lstConditions!=null&&lstConditions.size()>0)
        {
            List<String> lstConditionFromRequestNamesTmp=new ArrayList<String>();
            for(ConditionBean cbeanTmp:lstConditions)
            {
                if(cbeanTmp==null||cbeanTmp.isConstant()) continue;
                if(cbeanTmp.isConditionValueFromUrl()) lstConditionFromRequestNamesTmp.add(cbeanTmp.getName());
            }
            this.lstConditionFromRequestNames=lstConditionFromRequestNamesTmp;
        }
        return lstConditionFromRequestNames;
    }

    public void setLstConditionFromRequestNames(List<String> lstConditionFromRequestNames)
    {
        this.lstConditionFromRequestNames=lstConditionFromRequestNames;
    }

    public ConditionBean getConditionBeanByName(String name)
    {
        if(name==null||name.trim().equals("")) return null;
        if(this.lstConditions==null||this.lstConditions.size()==0) return null;
        if(this.mConditions==null)
        {
            Map<String,ConditionBean> mConditionsTmp=new HashMap<String,ConditionBean>();
            for(ConditionBean cbTmp:lstConditions)
            {
                mConditionsTmp.put(cbTmp.getName(),cbTmp);
            }
            this.mConditions=mConditionsTmp;
        }
        return this.mConditions.get(name);
    }

    public void initConditionValues(ReportRequest rrequest)
    {
        if(this.lstConditions==null||this.lstConditions.size()==0) return;
        Map<String,String> mConditionValues=new HashMap<String,String>();
        for(ConditionBean cbean:lstConditions)
        {
            cbean.initConditionValueByInitMethod(rrequest,mConditionValues);
        }
        for(ConditionBean cbean:lstConditions)
        {
            cbean.validateConditionValue(rrequest,mConditionValues);
        }
    }
    
    public boolean isExistConditionWithInputbox(ReportRequest rrequest)
    {
        if(this.lstConditions==null||this.lstConditions.size()==0) return false;
        for(ConditionBean cbeanTmp:this.lstConditions)
        {
            if(!cbeanTmp.isConditionWithInputbox()) continue;
            if(rrequest!=null
                    &&!rrequest.checkPermission(this.getReportBean().getId(),Consts.SEARCH_PART,cbeanTmp.getName(),Consts.PERMISSION_TYPE_DISPLAY))
                continue;
            return true;
        }
        return false;
    }
    
    public List<ConditionBean> getLstDisplayConditions(ReportRequest rrequest)
    {
        if(this.lstConditions==null||this.lstConditions.size()==0) return null;
        List<ConditionBean> lstConditionsResult=new ArrayList<ConditionBean>();
        for(ConditionBean cbeanTmp:this.lstConditions)
        {
            if(!cbeanTmp.isConditionWithInputbox()) continue;
            if(rrequest!=null
                    &&!rrequest.checkPermission(this.getReportBean().getId(),Consts.SEARCH_PART,cbeanTmp.getName(),Consts.PERMISSION_TYPE_DISPLAY))
                continue;
            lstConditionsResult.add(cbeanTmp);
        }
        return lstConditions;
    }
    
    public void afterSqlLoad()
    {
        if(lstDatasetBeans==null) return;
        for(ReportDataSetBean dsbeanTmp:lstDatasetBeans)
        {
            dsbeanTmp.afterSqlLoad();
        }
    }
    
    public void doPostLoad()
    {
        if(this.lstConditions!=null)
        {
            List<String> lstTmp=new ArrayList<String>();
            for(ConditionBean cbTmp:lstConditions)
            {
                if(cbTmp==null||cbTmp.getName()==null) continue;
                if(lstTmp.contains(cbTmp.getName()))
                {
                    throw new WabacusConfigLoadingException("报表 "+this.getReportBean().getPath()+"配置的查询条件name:"+cbTmp.getName()+"存在重复，必须确保唯一");
                }
                lstTmp.add(cbTmp.getName());
                cbTmp.doPostLoad();
            }
        }
        if(lstDatasetBeans!=null)
        {
            for(ReportDataSetBean dsbeanTmp:this.lstDatasetBeans)
            {
                for(ReportDataSetValueBean svbeanTmp:dsbeanTmp.getLstValueBeans())
                {
                    svbeanTmp.doPostLoad();
                }
            }
        }
    }
    
    public AbsConfigBean clone(AbsConfigBean parent)
    {
        SqlBean sbeanNew=(SqlBean)super.clone(parent);
        ((ReportBean)parent).setSbean(sbeanNew);
        sbeanNew.setLstConditions(ComponentConfigLoadAssistant.getInstance().cloneLstConditionBeans(sbeanNew,lstConditions));
        if(lstConditionFromRequestNames!=null)
        {
            sbeanNew.setLstConditionFromRequestNames((List<String>)((ArrayList<String>)lstConditionFromRequestNames).clone());
        }
        if(lstDatasetBeans!=null)
        {
            List<ReportDataSetBean> lstDataSetBeansNew=new ArrayList<ReportDataSetBean>();
            Map<String,ReportDataSetBean> mDatasetBeansNew=new HashMap<String,ReportDataSetBean>();
            ReportDataSetBean rdsbeanTmp;
            for(ReportDataSetBean svbeanTmp:lstDatasetBeans)
            {
                rdsbeanTmp=(ReportDataSetBean)svbeanTmp.clone(sbeanNew);
                lstDataSetBeansNew.add(rdsbeanTmp);
                mDatasetBeansNew.put(rdsbeanTmp.getId(),rdsbeanTmp);
            }
            sbeanNew.lstDatasetBeans=lstDataSetBeansNew;
            sbeanNew.mDatasetBeans=mDatasetBeansNew;
        }
        cloneExtendConfig(sbeanNew);
        return sbeanNew;
    }
}
