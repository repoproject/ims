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
package com.wabacus.system.component.application.report.configbean.crosslist;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.wabacus.config.component.application.report.AbsConfigBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.config.component.application.report.extendconfig.AbsExtendConfigBean;
import com.wabacus.exception.WabacusConfigLoadingException;

public class CrossListReportBean extends AbsExtendConfigBean
{
    public final static String EMPTY_DATASET_ID="WX_EMPTY_DATASET_ID";

    private boolean hasDynamicColGroupBean;

    private boolean shouldCreateRowSelectCol;//当前交叉报表是否需要在运行时新增行选中列（因为这种报表是动态查询要显示的列的，因此如果是checkbox/radiobox行选中类型，且没有配置行选中列，则不能在加载时生成，而是在运行时生成）

    private Map<String,CrossListReportDynDatasetBean> mCrossDatasetBeans;

    public CrossListReportBean(AbsConfigBean owner)
    {
        super(owner);
    }

    public boolean isHasDynamicColGroupBean()
    {
        return hasDynamicColGroupBean;
    }

    public void setHasDynamicColGroupBean(boolean hasDynamicColGroupBean)
    {
        this.hasDynamicColGroupBean=hasDynamicColGroupBean;
    }

    public boolean isShouldCreateRowSelectCol()
    {
        return shouldCreateRowSelectCol;
    }

    public void setShouldCreateRowSelectCol(boolean shouldCreateRowSelectCol)
    {
        this.shouldCreateRowSelectCol=shouldCreateRowSelectCol;
    }

    public CrossListReportDynDatasetBean getCrossDatasetBean(AbsCrossListReportColAndGroupBean crossColGroupBean,boolean isCreateNewWhileNull)
    {
        String datasetid=crossColGroupBean.getDatasetid();
        datasetid=datasetid==null||datasetid.trim().equals("")?EMPTY_DATASET_ID:datasetid.trim();
        if(this.mCrossDatasetBeans==null) this.mCrossDatasetBeans=new HashMap<String,CrossListReportDynDatasetBean>();
        CrossListReportDynDatasetBean crossDatasetbean=this.mCrossDatasetBeans.get(datasetid);
        if(crossDatasetbean==null&&isCreateNewWhileNull)
        {
            crossDatasetbean=new CrossListReportDynDatasetBean();
            ReportDataSetValueBean datasetbean=this.getOwner().getReportBean().getSbean().getLstDatasetBeans().get(0).getDatasetValueBeanById(crossColGroupBean.getDatasetid());
            if(datasetbean==null)
            {
                throw new WabacusConfigLoadingException("加载报表"+this.getOwner().getReportBean().getPath()+"失败，根据它的datasetid："
                        +crossColGroupBean.getDatasetid()+"在<sql/>中没有取到对应的数据集");
            }
            crossDatasetbean.setDatasetbean(datasetbean);
            this.mCrossDatasetBeans.put(datasetid,crossDatasetbean);
        }
        return crossDatasetbean;
    }

    public void doPostLoad()
    {
        if(mCrossDatasetBeans==null) return;
        for(Entry<String,CrossListReportDynDatasetBean> entryTmp:mCrossDatasetBeans.entrySet())
        {
            entryTmp.getValue().doPostLoad();
        }
    }
}
