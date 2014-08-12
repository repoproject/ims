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

import java.lang.reflect.Method;
import java.util.List;

import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.system.datatype.IDataType;

public class StatisticItemBean implements Cloneable
{
    public final static int STATSTIC_SCOPE_PAGE=1;
    
    public final static int STATSTIC_SCOPE_REPORT=2;
    
    public final static int STATSTIC_SCOPE_ALL=3;
    
    private String property;

    private String value;
    
    private String datasetid;
    
    private int statiscope=StatisticItemBean.STATSTIC_SCOPE_REPORT;

    private IDataType datatypeObj;

    private Method setMethod=null;
    
    private Method pageStatiSetMethod=null;
    
    private List<ConditionBean> lstConditions;

    public String getProperty()
    {
        return property;
    }

    public void setProperty(String property)
    {
        this.property=property;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value=value;
    }

    public String getDatasetid()
    {
        return datasetid;
    }

    public void setDatasetid(String datasetid)
    {
        this.datasetid=datasetid;
    }

    public IDataType getDatatypeObj()
    {
        return datatypeObj;
    }

    public void setDatatypeObj(IDataType datatypeObj)
    {
        this.datatypeObj=datatypeObj;
    }

    public Method getSetMethod()
    {
        return setMethod;
    }

    public void setSetMethod(Method setMethod)
    {
        this.setMethod=setMethod;
    }

    public Method getPageStatiSetMethod()
    {
        return pageStatiSetMethod;
    }

    public void setPageStatiSetMethod(Method pageStatiSetMethod)
    {
        this.pageStatiSetMethod=pageStatiSetMethod;
    }

    public List<ConditionBean> getLstConditions()
    {
        return lstConditions;
    }

    public void setLstConditions(List<ConditionBean> lstConditions)
    {
        this.lstConditions=lstConditions;
    }

    public int getStatiscope()
    {
        return statiscope;
    }

    public void setStatiscope(int statiscope)
    {
        this.statiscope=statiscope;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }catch(CloneNotSupportedException e)
        {
            e.printStackTrace();
            return this;
        }
    }
}
