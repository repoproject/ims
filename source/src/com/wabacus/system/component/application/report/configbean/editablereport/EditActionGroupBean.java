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
package com.wabacus.system.component.application.report.configbean.editablereport;

import java.util.ArrayList;
import java.util.List;

import com.wabacus.config.ConfigLoadManager;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.util.Tools;

public class EditActionGroupBean implements Cloneable
{
    private String datasource;
    
    private String actionscripts;//在<value/>中配置的更新脚本，可以是SQL语句、存储过程、JAVA类
    
    private List<AbsEditActionBean> lstEditActionBeans;
    
    private AbsEditableReportEditDataBean ownerUpdateBean;
    
    public EditActionGroupBean(AbsEditableReportEditDataBean ownerUpdateBean)
    {
        this.ownerUpdateBean=ownerUpdateBean;
        this.lstEditActionBeans=new ArrayList<AbsEditActionBean>();
    }
    
    public AbsEditableReportEditDataBean getOwnerUpdateBean()
    {
        return ownerUpdateBean;
    }

    public void setOwnerUpdateBean(AbsEditableReportEditDataBean ownerUpdateBean)
    {
        this.ownerUpdateBean=ownerUpdateBean;
    }

    public String getDatasource()
    {
        if(datasource==null||datasource.trim().equals(""))
            return this.getOwnerUpdateBean().getOwner().getReportBean().getSbean().getDatasource();
        return datasource;
    }

    public void setDatasource(String datasource)
    {
        this.datasource=datasource;
    }

    public String getActionscripts()
    {
        return actionscripts;
    }

    public void setActionscripts(String actionscripts)
    {
        this.actionscripts=actionscripts;
    }

    public List<AbsEditActionBean> getLstEditActionBeans()
    {
        return lstEditActionBeans;
    }

    public void setLstEditActionBeans(List<AbsEditActionBean> lstEditActionBeans)
    {
        this.lstEditActionBeans=lstEditActionBeans;
    }

    public void addActionBean(AbsEditActionBean actionbean)
    {
        if(actionbean instanceof AbsEditSqlActionBean)
        {
            String rtnValParaNameTmp=((AbsEditSqlActionBean)actionbean).getReturnValueParamname();
            if(rtnValParaNameTmp!=null&&!rtnValParaNameTmp.trim().equals(""))
            {
                if(Tools.isDefineKey("#",rtnValParaNameTmp))
                {//返回值存放在<params/>定义的变量中
                    rtnValParaNameTmp=Tools.getRealKeyByDefine("#",rtnValParaNameTmp);
                    if(rtnValParaNameTmp==null||rtnValParaNameTmp.trim().equals(""))
                    {
                        ((AbsEditSqlActionBean)actionbean).setReturnValueParamname(null);
                    }else if(this.ownerUpdateBean.getExternalValueBeanByName(rtnValParaNameTmp,false)==null)
                    {
                        throw new WabacusConfigLoadingException("加载报表"+this.ownerUpdateBean.getOwner().getReportBean()+"的更新SQL语句失败，返回值：#{"
                                +rtnValParaNameTmp+"}引用的变量没有在<params/>中定义");
                    }
                }else if(Tools.isDefineKey("rrequest",rtnValParaNameTmp))
                {
                    rtnValParaNameTmp=Tools.getRealKeyByDefine("rrequest",rtnValParaNameTmp);
                    if(rtnValParaNameTmp==null||rtnValParaNameTmp.trim().equals(""))
                    {
                        ((AbsEditSqlActionBean)actionbean).setReturnValueParamname(null);
                    }
                }else
                {
                    throw new WabacusConfigLoadingException("加载报表"+this.ownerUpdateBean.getOwner().getReportBean()+"的更新SQL语句失败，返回值："
                            +rtnValParaNameTmp+"不合法，必须是#{paramname}或rrequset{key}之一的格式");
                }
            }
        }
        this.lstEditActionBeans.add(actionbean);
    }
    
    public void parseActionscripts(String reportTypeKey)
    {
        ReportBean rbean=this.ownerUpdateBean.getOwner().getReportBean();
        List<String> lstActionscripts=Tools.parseStringToList(this.actionscripts,';','\"');
        String realSqlTmp;
        for(String scriptTmp:lstActionscripts)
        {
            if(scriptTmp==null||scriptTmp.trim().equals("")) continue;
            scriptTmp=scriptTmp.trim();
            if(Tools.isDefineKey("class",scriptTmp))
            {
                scriptTmp=Tools.getRealKeyByDefine("class",scriptTmp).trim();
                String javaname=scriptTmp;
                String params=null;
                int idx1=scriptTmp.indexOf("(");
                int idx2=scriptTmp.indexOf(")");
                if(idx1>0&&idx2==scriptTmp.length()-1)
                {
                    javaname=scriptTmp.substring(0,idx1).trim();
                    params=scriptTmp.substring(idx1+1,idx2).trim();
                }else if(idx1>=0||idx2>=0)
                {
                    throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，配置的更新数据JAVA类"+scriptTmp+"不合法");
                }
                Object javaActionBean;
                try
                {
                    javaActionBean=ConfigLoadManager.currentDynClassLoader.loadClassByCurrentLoader(javaname).newInstance();
                }catch(Exception e)
                {
                    throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，配置的更新数据JAVA类"+scriptTmp+"无法实例化",e);
                }
                if(!(javaActionBean instanceof AbsJavaEditActionBean))
                {
                    throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，配置的更新数据JAVA类"+scriptTmp+"没有继承"
                            +AbsJavaEditActionBean.class.getName());
                }
                ((AbsEditActionBean)javaActionBean).setOwnerGroupBean(this);
                ((AbsEditActionBean)javaActionBean).parseActionscript(reportTypeKey,params);
                this.addActionBean((AbsEditActionBean)javaActionBean);
            }else
            {
                realSqlTmp=new UpdateSqlActionBean(this).parseAndRemoveReturnParamname(scriptTmp).toLowerCase().trim();
                if(realSqlTmp.startsWith("{")&&realSqlTmp.endsWith("}"))
                {
                    realSqlTmp=realSqlTmp.substring(1,realSqlTmp.length()-1).trim();
                }
                if(realSqlTmp.indexOf("insert ")==0)
                {
                    new InsertSqlActionBean(this).parseActionscript(reportTypeKey,scriptTmp);
                }else if(realSqlTmp.indexOf("update ")==0)
                {
                    new UpdateSqlActionBean(this).parseActionscript(reportTypeKey,scriptTmp);
                }else if(realSqlTmp.indexOf("delete ")==0)
                {
                    new DeleteSqlActionBean(this).parseActionscript(reportTypeKey,scriptTmp);
                }else if(realSqlTmp.indexOf("call ")==0)
                {
                    new StoreProcedureActionBean(this).parseActionscript(reportTypeKey,scriptTmp);
                }else
                {
                    throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，配置的更新数据的SQL语句"+scriptTmp+"不合法");
                }
            }
        }
    }
    
    public Object clone(AbsEditableReportEditDataBean newowner)
    {
        try
        {
            EditActionGroupBean newbean=(EditActionGroupBean)super.clone();
            newbean.ownerUpdateBean=newowner;
            if(lstEditActionBeans!=null)
            {
                List<AbsEditActionBean> lstEditActionBeansNew=new ArrayList<AbsEditActionBean>();
                for(AbsEditActionBean actionBeanTmp:this.lstEditActionBeans)
                {
                    lstEditActionBeansNew.add((AbsEditActionBean)actionBeanTmp.clone(newbean));
                }
                newbean.lstEditActionBeans=lstEditActionBeansNew;
            }
            return newbean;
        }catch(CloneNotSupportedException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public int hashCode()
    {
        final int prime=31;
        int result=1;
        result=prime*result+((ownerUpdateBean==null)?0:ownerUpdateBean.hashCode());
        result=prime*result+((actionscripts==null)?0:actionscripts.hashCode());
        return result;
    }

    public boolean equals(Object obj)
    {
        if(this==obj) return true;
        if(obj==null) return false;
        if(getClass()!=obj.getClass()) return false;
        final EditActionGroupBean other=(EditActionGroupBean)obj;
        if(ownerUpdateBean==null)
        {
            if(other.ownerUpdateBean!=null) return false;
        }else if(!ownerUpdateBean.equals(other.ownerUpdateBean)) return false;
        if(actionscripts==null)
        {
            if(other.actionscripts!=null) return false;
        }else if(!actionscripts.equals(other.actionscripts)) return false;
        return true;
    }
}

