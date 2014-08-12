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

import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.util.Consts;

public class EditableReportDeleteDataBean extends AbsEditableReportEditDataBean
{
    private String deleteConfirmMessage=null;

    public EditableReportDeleteDataBean(IEditableReportEditGroupOwnerBean owner)
    {
        super(owner);
    }

    public String getDeleteConfirmMessage()
    {
        return deleteConfirmMessage;
    }

    public void setDeleteConfirmMessage(String deleteConfirmMessage)
    {
        this.deleteConfirmMessage=deleteConfirmMessage;
    }

    protected void setParamBeanInfoOfColBean(ColBean cbUpdateSrc,EditableReportParamBean paramBean,String configColProperty,String reportTypeKey)
    {
        if(Consts.COL_DISPLAYTYPE_HIDDEN.equals(cbUpdateSrc.getDisplaytype()))
        {
            if(configColProperty.endsWith("__old")) configColProperty=configColProperty.substring(0,configColProperty.length()-"__old".length());
        }else if(configColProperty.endsWith("__old"))
        {
            EditableReportColBean ercbeanUpdated=(EditableReportColBean)cbUpdateSrc.getExtendConfigDataForReportType(reportTypeKey);
            if(ercbeanUpdated==null||ercbeanUpdated.getEditableWhenUpdate()<=0)
            {
                configColProperty=configColProperty.substring(0,configColProperty.length()-"__old".length());
            }
        }
        paramBean.setParamname(configColProperty);
    }
}
