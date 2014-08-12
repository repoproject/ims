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

import com.wabacus.config.component.application.report.AbsConfigBean;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.extendconfig.AbsExtendConfigBean;

public class AbsChartReportDisplayBean extends AbsExtendConfigBean
{
    private String labelcolumn;//如果是横向数据集，这里存放显示label的<col/>的column属性值

    private ColBean cbeanLabel;

    private String labelDatasetid;//如果配置了多个<dataset/>，这里存放查询label数据的<dataset/>的id

    public AbsChartReportDisplayBean(AbsConfigBean owner)
    {
        super(owner);
    }

    public String getLabelcolumn()
    {
        return labelcolumn;
    }

    public void setLabelcolumn(String labelcolumn)
    {
        this.labelcolumn=labelcolumn;
    }

    public ColBean getCbeanLabel()
    {
        return cbeanLabel;
    }

    public void setCbeanLabel(ColBean cbeanLabel)
    {
        this.cbeanLabel=cbeanLabel;
    }

    public String getLabelDatasetid()
    {
        return labelDatasetid;
    }

    public void setLabelDatasetid(String labelDatasetid)
    {
        this.labelDatasetid=labelDatasetid;
    }
}
