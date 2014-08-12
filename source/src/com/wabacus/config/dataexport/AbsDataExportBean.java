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
package com.wabacus.config.dataexport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wabacus.config.component.ComponentConfigLoadAssistant;
import com.wabacus.config.component.IComponentConfigBean;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.util.Tools;

public abstract class AbsDataExportBean implements Cloneable
{
    private String filename;//本<dataexport/>对应的导出的文件名
    
    private Map<String,String> mDynFilename;
    
    private String type;

    private String includeApplicationids;

    private List<String> lstIncludeApplicationids;

    private Map<String,Integer> mReportRecordCounts;
    
    protected IComponentConfigBean owner;
    
    public AbsDataExportBean(IComponentConfigBean owner,String type)
    {
        this.owner=owner;
        this.type=type;
    }

    public String getType()
    {
        return type;
    }

    public String getIncludeApplicationids()
    {
        return includeApplicationids;
    }

    public void setIncludeApplicationids(String includeApplicationids)
    {
        this.includeApplicationids=includeApplicationids;
    }

    public List<String> getLstIncludeApplicationids()
    {
        return lstIncludeApplicationids;
    }

    public void setLstIncludeApplicationids(List<String> lstIncludeApplicationids)
    {
        this.lstIncludeApplicationids=lstIncludeApplicationids;
    }

    public Map<String,Integer> getMReportRecordCounts()
    {
        return mReportRecordCounts;
    }

    public void setMReportRecordCounts(Map<String,Integer> reportRecordCounts)
    {
        mReportRecordCounts=reportRecordCounts;
    }

    public IComponentConfigBean getOwner()
    {
        return owner;
    }

    public void setOwner(IComponentConfigBean owner)
    {
        this.owner=owner;
    }
    
    public String getFilename(ReportRequest rrequest)
    {
        return WabacusAssistant.getInstance().getStringValueWithDynPart(rrequest,this.filename,this.mDynFilename,"");
    }

    public int getDataExportRecordcount(String reportid)
    {
        if(this.mReportRecordCounts==null||this.mReportRecordCounts.get(reportid)==null||this.mReportRecordCounts.get(reportid)<0) return -1;
        return this.mReportRecordCounts.get(reportid).intValue();
    }
    
    public void loadConfig(XmlElementBean eleDataExport)
    {
        String filename=eleDataExport.attributeValue("filename");
        if(filename!=null)
        {
            Object[] objArr=WabacusAssistant.getInstance().parseStringWithDynPart(filename);
            this.filename=(String)objArr[0];
            this.mDynFilename=(Map<String,String>)objArr[1];
        }
        String dataexportinclude=eleDataExport.attributeValue("include");
        if(dataexportinclude!=null&&!dataexportinclude.trim().equals(""))
        {
            this.lstIncludeApplicationids=Tools.parseStringToList(dataexportinclude,";",false);
        }
    }
    
    public void doPostLoad()
    {
        Object[] objResult=ComponentConfigLoadAssistant.getInstance().parseIncludeApplicationids(this.owner,this.lstIncludeApplicationids);
        this.includeApplicationids=(String)objResult[0];
        this.lstIncludeApplicationids=(List<String>)objResult[1];
        this.mReportRecordCounts=(Map<String,Integer>)objResult[2];
    }
    
    public AbsDataExportBean clone(IComponentConfigBean owner) 
    {
        try
        {
            AbsDataExportBean newBean=(AbsDataExportBean)super.clone();
            newBean.setOwner(owner);
            if(lstIncludeApplicationids!=null)
            {
                newBean.setLstIncludeApplicationids((List<String>)((ArrayList<String>)lstIncludeApplicationids).clone());
            }
            if(mReportRecordCounts!=null)
            {
                newBean.setMReportRecordCounts((Map<String,Integer>)((HashMap<String,Integer>)this.mReportRecordCounts).clone());
            }
            return newBean;
        }catch(CloneNotSupportedException e)
        {
            throw new WabacusConfigLoadingException("clone组件"+this.owner.getPath()+"的数据导出对象失败",e);
        }
    }
}
