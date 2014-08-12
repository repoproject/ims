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
package com.wabacus.system.component.application.report.abstractreport.configbean;

import java.util.ArrayList;
import java.util.List;

public class AbsListReportSubDisplayRowBean implements Cloneable
{
    private int displaytype=AbsListReportSubDisplayBean.SUBROW_DISPLAYTYPE_REPORT;
    
    private int displayposition=AbsListReportSubDisplayBean.SUBROW_POSITION_BOTTOM;
    
    protected List<AbsListReportSubDisplayColBean> lstSubColBeans;

    public int getDisplaytype()
    {
        return displaytype;
    }

    public void setDisplaytype(int displaytype)
    {
        this.displaytype=displaytype;
    }

    public int getDisplayposition()
    {
        return displayposition;
    }

    public void setDisplayposition(int displayposition)
    {
        this.displayposition=displayposition;
    }

    public List<AbsListReportSubDisplayColBean> getLstSubColBeans()
    {
        return lstSubColBeans;
    }

    public void setLstSubColBeans(List<AbsListReportSubDisplayColBean> lstSubColBeans)
    {
        this.lstSubColBeans=lstSubColBeans;
    }
    
    public AbsListReportSubDisplayRowBean clone()
    {
        AbsListReportSubDisplayRowBean newBean=null;
        try
        {
            newBean=(AbsListReportSubDisplayRowBean)super.clone();
            if(lstSubColBeans!=null)
            {
                List<AbsListReportSubDisplayColBean> lstStatiColBeansNew=new ArrayList<AbsListReportSubDisplayColBean>();
                for(AbsListReportSubDisplayColBean cb:lstSubColBeans)
                {
                    if(cb!=null)
                    {
                        lstStatiColBeansNew.add((AbsListReportSubDisplayColBean)cb.clone());
                    }
                }
                newBean.setLstSubColBeans(lstStatiColBeansNew);
            }
        }catch(CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        return newBean;
    }
}

