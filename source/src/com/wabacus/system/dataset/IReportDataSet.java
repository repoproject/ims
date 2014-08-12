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
package com.wabacus.system.dataset;

import java.util.List;
import java.util.Map;

import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;

public interface IReportDataSet
{
    public int getRecordcount(ReportRequest rrequest,AbsReportType reportTypeObj,ReportDataSetValueBean datasetbean);

    public Object getDataSet(ReportRequest rrequest,AbsReportType reportTypeObj,ReportDataSetValueBean datasetbean,List lstReportData);

    public Object getColFilterDataSet(ReportRequest rrequest,ColBean filterColBean,ReportDataSetValueBean datasetbean,
            Map<String,List<String>> mSelectedFilterValues);

    public Object getStatisticDataSet(ReportRequest rrequest,AbsReportType reportObj,ReportDataSetValueBean datasetbean,Object typeObj,String sql,
            boolean isStatisticForOnePage);
}
