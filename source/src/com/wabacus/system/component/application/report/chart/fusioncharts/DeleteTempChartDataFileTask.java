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

import java.io.File;

import com.wabacus.config.Config;
import com.wabacus.system.component.application.report.chart.FusionChartsReportType;
import com.wabacus.system.task.ITask;

public class DeleteTempChartDataFileTask implements ITask
{
    private long lastExecuteMilSeconds;

    private long intervalMilSeconds;
    
    private long persistencePeriods;
    
    public DeleteTempChartDataFileTask()
    {
        lastExecuteMilSeconds=0L;
        intervalMilSeconds=Long.MIN_VALUE;
        persistencePeriods=Long.MIN_VALUE;
        String deleteXmlFilesParam=Config.getInstance().getSystemConfigValue("fusioncharts-autodelete-interval","");
        int idx=deleteXmlFilesParam.indexOf("|");
        if(!deleteXmlFilesParam.equals("")&&idx>0)
        {
            intervalMilSeconds=Long.parseLong(deleteXmlFilesParam.substring(0,idx).trim())*1000;
            persistencePeriods=Long.parseLong(deleteXmlFilesParam.substring(idx+1).trim())*1000;
        }
        if(intervalMilSeconds<=0) intervalMilSeconds=300*1000L;
        if(persistencePeriods<=0) persistencePeriods=600*1000L;
    }
    
    public String getTaskId()
    {
        return DeleteTempChartDataFileTask.class.getName();
    }
    
    public boolean shouldExecute()
    {
        if(FusionChartsReportType.chartXmlFileTempPath==null) return false;
        return System.currentTimeMillis()-lastExecuteMilSeconds>=intervalMilSeconds;
    }
    
    public synchronized void execute()
    {
        lastExecuteMilSeconds=System.currentTimeMillis();
        if(FusionChartsReportType.chartXmlFileTempPath==null) return;
        File f=new File(FusionChartsReportType.chartXmlFileTempPath);
        if(!f.exists()||!f.isDirectory()) return;
        File[] filesArr=f.listFiles();
        if(filesArr==null) return;
        for(int i=0;i<filesArr.length;i++)
        {
            if(lastExecuteMilSeconds-filesArr[i].lastModified()>=persistencePeriods)
            {
                filesArr[i].delete();
            }
        }
    }

    public void destory()
    {
        if(FusionChartsReportType.chartXmlFileTempPath==null) return;
        File f=new File(FusionChartsReportType.chartXmlFileTempPath);
        if(f.exists()&&f.isDirectory())
        {
            File[] filesArr=f.listFiles();
            if(filesArr==null) return;
            for(int i=0,len=filesArr.length;i<len;i++)
            {
                filesArr[i].delete();
            }
        }
    }
    
}

