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

import java.util.HashMap;
import java.util.Map;

import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.util.Consts;

public class EditableReportInsertDataBean extends AbsEditableReportEditDataBean
{

    private Map<String,String> mUpdateConditions;

    private String insertstyle;
    
    private String addposition;//对于editablelist2/listform报表类型，指定添加记录的位置，可配置值为top和bottom，默认是bottom
    
    private String callbackmethod;//对于editablelist2/listform报表类型，指定添加记录时的回调函数
    
    public EditableReportInsertDataBean(IEditableReportEditGroupOwnerBean owner)
    {
        super(owner);
    }

    public Map<String,String> getMUpdateConditions()
    {
        return mUpdateConditions;
    }

    public void setMUpdateConditions(Map<String,String> updateConditions)
    {
        mUpdateConditions=updateConditions;
    }

    public String getInsertstyle()
    {
        return insertstyle;
    }

    public void setInsertstyle(String insertstyle)
    {
        this.insertstyle=insertstyle;
    }

    public String getAddposition()
    {
        return addposition;
    }

    public void setAddposition(String addposition)
    {
        this.addposition=addposition;
    }

    public String getCallbackmethod()
    {
        return callbackmethod;
    }

    public void setCallbackmethod(String callbackmethod)
    {
        this.callbackmethod=callbackmethod;
    }

    protected void setParamBeanInfoOfColBean(ColBean cbUpdateSrc,EditableReportParamBean paramBean,String configColProperty,String reportTypeKey)
    {
        if(!Consts.COL_DISPLAYTYPE_HIDDEN.equals(cbUpdateSrc.getDisplaytype()))
        {
            EditableReportColBean ercbeanUpdated=(EditableReportColBean)cbUpdateSrc.getExtendConfigDataForReportType(reportTypeKey);
            ercbeanUpdated.setEditableWhenInsert(2);
        }
        if(configColProperty.endsWith("__old")) configColProperty=configColProperty.substring(0,configColProperty.length()-"__old".length());
        paramBean.setParamname(configColProperty);
    }

    
    protected void setRealParamnameInDoPostLoadFinally(EditableReportParamBean paramBean)
    {
        //提供空实现，因为<insert/>中字段名不可能实现__old结尾，在setParamBeanInfoOfColBean()方法中已经统一去掉了
    }
    
    public Object clone(IEditableReportEditGroupOwnerBean newowner)
    {
        EditableReportInsertDataBean newbean=(EditableReportInsertDataBean)super.clone(newowner);
        if(mUpdateConditions!=null)
        {
            newbean.setMUpdateConditions((Map)((HashMap)mUpdateConditions).clone());
        }
        return newbean;
    }
}
