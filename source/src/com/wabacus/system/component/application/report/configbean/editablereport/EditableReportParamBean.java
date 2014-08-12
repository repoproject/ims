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

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.ReportAssistant;
import com.wabacus.system.datatype.IDataType;
import com.wabacus.system.datatype.IntType;
import com.wabacus.system.datatype.VarcharType;
import com.wabacus.util.Tools;

public class EditableReportParamBean
{
    private String paramname;

    private String defaultvalue;

    private boolean hasLeftPercent;

    private boolean hasRightPercent;

    private Object owner;

    public String getParamname()
    {
        return paramname;
    }

    public void setParamname(String paramname)
    {
        this.paramname=paramname;
    }

    public void setDefaultvalue(String defaultvalue)
    {
        this.defaultvalue=defaultvalue;
    }

    public boolean isHasLeftPercent()
    {
        return hasLeftPercent;
    }

    public void setHasLeftPercent(boolean hasLeftPercent)
    {
        this.hasLeftPercent=hasLeftPercent;
    }

    public boolean isHasRightPercent()
    {
        return hasRightPercent;
    }

    public void setHasRightPercent(boolean hasRightPercent)
    {
        this.hasRightPercent=hasRightPercent;
    }

    public Object getOwner()
    {
        return owner;
    }

    public void setOwner(Object owner)
    {
        this.owner=owner;
    }

    public ColBean getColbeanOwner()
    {
        if(!(this.owner instanceof ColBean))
        {
            return null;
        }
        return (ColBean)this.owner;
    }

    public IDataType getDataTypeObj()
    {
        IDataType typeObj=null;
        if(this.owner instanceof EditableReportExternalValueBean)
        {
            typeObj=((EditableReportExternalValueBean)this.owner).getTypeObj();
        }else if(this.owner instanceof ColBean)
        {
            typeObj=((ColBean)this.owner).getDatatypeObj();
        }else if(Tools.isDefineKey("increment",this.paramname))
        {
            typeObj=new IntType();
        }
        if(typeObj==null)
        {
            typeObj=new VarcharType();
        }
        return typeObj;
    }

    public String getParamValue(String value,ReportRequest rrequest,ReportBean rbean)
    {
        if(value==null||value.trim().equals(""))
        {
            value=ReportAssistant.getInstance().getColAndConditionDefaultValue(rrequest,defaultvalue);
        }
        if(getDataTypeObj() instanceof VarcharType)
        {
            if(this.hasLeftPercent)
            {
                if(value==null) value="";
                value="%"+value;
            }
            if(this.hasRightPercent)
            {
                if(value==null) value="";
                value=value+"%";
            }
        }
        return value;
    }
}
