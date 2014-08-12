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

import com.wabacus.config.ConfigLoadManager;
import com.wabacus.config.component.ComponentConfigLoadManager;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.inputbox.AbsInputBox;
import com.wabacus.util.Tools;

public abstract class AbsOptionBean implements Cloneable
{
    protected AbsOptionDatasource optionDatasourceObj;

    protected AbsInputBox ownerInputboxObj;
    
    public AbsOptionBean(AbsInputBox ownerInputboxObj)
    {
        this.ownerInputboxObj=ownerInputboxObj;
    }
    
    public AbsInputBox getOwnerInputboxObj()
    {
        return ownerInputboxObj;
    }

    public AbsOptionDatasource getOptionDatasourceObj()
    {
        return optionDatasourceObj;
    }

    public void setOptionDatasourceObj(AbsOptionDatasource optionDatasourceObj)
    {
        this.optionDatasourceObj=optionDatasourceObj;
    }
    
    public void loadOptionDynDatasourceObj(XmlElementBean eleOptionBean,String source)
    {
        ReportBean rbean=this.ownerInputboxObj.getOwner().getReportBean();
        AbsOptionDatasource optionDatasourceObjTmp=null;
        if(Tools.isDefineKey("@",source))
        {
            SQLOptionDatasource sqldatasource=new SQLOptionDatasource();
            String datasource=eleOptionBean.attributeValue("datasource");
            if(datasource!=null) sqldatasource.setDatasource(datasource.trim());
            sqldatasource.setSql(Tools.getRealKeyByDefine("@",source));
            sqldatasource.setLstConditions(ComponentConfigLoadManager.loadConditionsInOtherPlace(eleOptionBean,rbean));
            optionDatasourceObjTmp=sqldatasource;
        }else if(Tools.isDefineKey("class",source))
        {
            String classname=Tools.getRealKeyByDefine("class",source);
            if(classname.trim().equals(""))
            {
                throw new WabacusConfigLoadingException("报表"+rbean.getPath()+"配置的选项的source"+"指定的JAVA类为空");
            }
            Object optionDsObj=null;
            try
            {
                optionDsObj=ConfigLoadManager.currentDynClassLoader.loadClassByCurrentLoader(classname).newInstance();
            }catch(Exception e)
            {
                throw new WabacusConfigLoadingException("报表"+rbean.getPath()+"配置的选项的source"+"指定的JAVA类"+classname+"无法实例化",e);
            }
            if(!(optionDsObj instanceof AbsOptionDatasource))
            {
                throw new WabacusConfigLoadingException("报表"+rbean.getPath()+"配置的选项的source"+"指定的JAVA类没有继承"+AbsOptionDatasource.class.getName()+"类");
            }
            optionDatasourceObjTmp=((AbsOptionDatasource)optionDsObj);
        }
        if(optionDatasourceObjTmp!=null) optionDatasourceObjTmp.setOwnerOptionBean(this);
        this.setOptionDatasourceObj(optionDatasourceObjTmp);
    }
    
    public void doPostLoad()
    {
        if(this.optionDatasourceObj!=null) this.optionDatasourceObj.doPostLoad();
    }
    
    public AbsOptionBean clone(AbsInputBox newOwnerInputboxObj) 
    {
        AbsOptionBean newOptionBean=null;
        try
        {
            newOptionBean=(AbsOptionBean)super.clone();
            newOptionBean.ownerInputboxObj=newOwnerInputboxObj;
            if(optionDatasourceObj!=null)
            {
                newOptionBean.optionDatasourceObj=this.optionDatasourceObj.clone(newOptionBean);
            }
        }catch(CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        return newOptionBean;
    }
    
    
}

