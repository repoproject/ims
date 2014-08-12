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

import java.util.List;
import java.util.Map;

import com.wabacus.system.ReportRequest;

public abstract class AbsOptionDatasource implements Cloneable
{
    protected AbsOptionBean ownerOptionBean;

    public void setOwnerOptionBean(AbsOptionBean ownerOptionBean)
    {
        this.ownerOptionBean=ownerOptionBean;
    }

    public AbsOptionBean getOwnerOptionBean()
    {
        return ownerOptionBean;
    }

    public List<Map<String,String>> getLstSelectBoxOptions(ReportRequest rrequest,Map<String,String> mParentInputboxValues)
    {
        return null;
    }
    
    public List<Map<String,String>> getLstTypePromptOptions(ReportRequest rrequest,String txtValue)
    {
        return null;
    }
    
    public void doPostLoad()
    {}

    protected Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    protected AbsOptionDatasource clone(AbsOptionBean newOwnerOptionBean) throws CloneNotSupportedException
    {
        AbsOptionDatasource newdatasourceBean=(AbsOptionDatasource)clone();
        newdatasourceBean.ownerOptionBean=newOwnerOptionBean;
        return newdatasourceBean;
    }
}
