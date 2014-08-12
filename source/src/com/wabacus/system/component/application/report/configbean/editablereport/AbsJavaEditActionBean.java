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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.util.Tools;

public abstract class AbsJavaEditActionBean extends AbsEditActionBean
{
    private static Log log=LogFactory.getLog(AbsJavaEditActionBean.class);
    
    public AbsJavaEditActionBean(EditActionGroupBean ownerGroupBean)
    {
        super(ownerGroupBean);
    }

    public AbsJavaEditActionBean()
    {
        super(null);
    }
    
    public void setOwnerGroupBean(EditActionGroupBean ownerGroupBean)
    {
        this.ownerGroupBean=ownerGroupBean;
    }
    
    public void parseActionscript(String reportTypeKey,String actionscript)
    {
        
        if(actionscript==null||actionscript.trim().equals("")) return;
        if(this.ownerGroupBean.getOwnerUpdateBean().isAutoReportdata()
                &&!(this.ownerGroupBean.getOwnerUpdateBean() instanceof EditableReportDeleteDataBean))
        {
            List<String> lstParamsTmp=Tools.parseStringToList(actionscript,',','\'');
            for(String paramTmp:lstParamsTmp)
            {
                if(paramTmp==null||paramTmp.trim().equals("")) continue;
                if(!Tools.isDefineKey("@",paramTmp))
                {
                    throw new WabacusConfigLoadingException("加载报表"+this.ownerGroupBean.getOwnerUpdateBean().getOwner().getReportBean().getPath()
                            +"失败，配置的更新数据JAVA类中指定的参数"+paramTmp+"不合法，对于JAVA类，只能在参数列表中指定@{column}/@{column__old}两种之一的格式，如果要传入其它类型的参数，请配置相应的<param/>");
                }
                createParamBeanByColbean(Tools.getRealKeyByDefine("@",paramTmp),reportTypeKey,true,true);
            }
        }else
        {
            log.warn("报表"+this.ownerGroupBean.getOwnerUpdateBean().getOwner().getReportBean().getPath()+"的<delete/>或<button/>中配置的JAVA类不需要在括号中指定"
                    +actionscript+"参数，在这里的指定没有任何作用，可以在<params/>中定义要传入JAVA类中的参数");
        }
    }

    public void beginTransaction()
    {

    }

    public void commitTransaction()
    {

    }

    public void rollbackTransaction()
    {

    }

}
