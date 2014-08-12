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
package com.wabacus.system.component.application.report.chart.fusioncharts;

import java.util.List;

import com.wabacus.config.component.application.report.AbsReportDataPojo;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.abstractreport.AbsChartReportType;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsChartReportBean;
import com.wabacus.system.component.application.report.chart.FusionChartsReportType;

public abstract class AbsDatasetType
{
    protected FusionChartsReportType reportTypeObj;

    protected ReportRequest rrequest;

    protected ReportBean rbean;
    
    protected List<AbsReportDataPojo> lstReportData;

    protected List<ColBean> lstDisplayedColBeans;

    public AbsDatasetType(FusionChartsReportType reportTypeObj,List<ColBean> lstDisplayedColBeans)
    {
        this.reportTypeObj=reportTypeObj;
        this.rrequest=reportTypeObj.getReportRequest();
        this.rbean=reportTypeObj.getReportBean();
        this.lstReportData=reportTypeObj.getLstReportData();
        this.lstDisplayedColBeans=lstDisplayedColBeans;
    }

    public abstract void displayCategoriesPart(StringBuffer resultBuf);

    public abstract void displaySingleSeriesDataPart(StringBuffer resultBuf);
    
    public abstract void displaySingleLayerDatasetDataPart(StringBuffer resultBuf);

    public abstract void displayDualLayerDatasetDataPart(StringBuffer resultBuf);
}
