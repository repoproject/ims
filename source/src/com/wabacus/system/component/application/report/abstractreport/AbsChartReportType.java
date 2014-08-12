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
package com.wabacus.system.component.application.report.abstractreport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wabacus.config.ConfigLoadAssistant;
import com.wabacus.config.component.IComponentConfigBean;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.DisplayBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetBean;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsChartReportBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsChartReportDisplayBean;
import com.wabacus.system.component.container.AbsContainerType;
import com.wabacus.util.Consts;

public abstract class AbsChartReportType extends AbsReportType
{
    public final static String KEY=AbsChartReportType.class.getName();

    protected AbsChartReportBean acrbean;
    
    protected AbsChartReportDisplayBean acrdbean;
    
    public AbsChartReportType(AbsContainerType parentContainerType,IComponentConfigBean comCfgBean,ReportRequest rrequest)
    {
        super(parentContainerType,comCfgBean,rrequest);
        if(comCfgBean!=null)
        {
            acrbean=(AbsChartReportBean)((ReportBean)comCfgBean).getExtendConfigDataForReportType(KEY);
            acrdbean=(AbsChartReportDisplayBean)((ReportBean)comCfgBean).getDbean().getExtendConfigDataForReportType(KEY);
        }
    }

    public AbsChartReportBean getAcrbean()
    {
        return acrbean;
    }

    public AbsChartReportDisplayBean getAcrdbean()
    {
        return acrdbean;
    }

    public abstract String loadStringChartData(boolean invokeInterceptor);
    
    protected boolean isHiddenCol(ColBean cbean)
    {
        if(Consts.COL_DISPLAYTYPE_HIDDEN.equals(cbean.getDisplaytype())) return true;
        return !cbean.checkDisplayPermission(rrequest);
    }

    public String getColSelectedMetadata()
    {
        return null;
    }

    protected String getDefaultNavigateKey()
    {
        return null;
    }

    protected int getTotalColCount()
    {
        return 0;
    }

    public int afterDisplayLoading(DisplayBean disbean,List<XmlElementBean> lstEleDisplayBeans)
    {
        super.afterDisplayLoading(disbean,lstEleDisplayBeans);
        AbsChartReportDisplayBean acrdbean=(AbsChartReportDisplayBean)disbean.getExtendConfigDataForReportType(KEY);
        if(acrdbean==null)
        {
            acrdbean=new AbsChartReportDisplayBean(disbean);
            disbean.setExtendConfigDataForReportType(KEY,acrdbean);
        }
        Map<String,String> mDisplayProperties=ConfigLoadAssistant.getInstance().assembleAllAttributes(lstEleDisplayBeans,
                new String[] { "labelcolumn" });
        String labelcolumn=mDisplayProperties.get("labelcolumn");
        if(labelcolumn!=null)
        {
            acrdbean.setLabelcolumn(labelcolumn.trim());
        }
        return 1;
    }

    public int afterReportLoading(ReportBean reportbean,List<XmlElementBean> lstEleReportBeans)
    {
        super.afterReportLoading(reportbean,lstEleReportBeans);
        AbsChartReportBean acrbean=(AbsChartReportBean)reportbean.getExtendConfigDataForReportType(KEY);
        if(acrbean==null)
        {
            acrbean=new AbsChartReportBean(reportbean);
            reportbean.setExtendConfigDataForReportType(KEY,acrbean);
        }
        XmlElementBean eleReportBean=lstEleReportBeans.get(0);
        String chartype=eleReportBean.attributeValue("chartype");
        if(chartype!=null) acrbean.setChartype(chartype.trim());
        if(acrbean.getChartype()==null||acrbean.getChartype().trim().equals(""))
        {
            throw new WabacusConfigLoadingException("报表"+reportbean.getPath()+"必须指定图表类型");
        }
        String datatype=eleReportBean.attributeValue("datatype");
        if(datatype!=null) acrbean.setDatatype(datatype.trim());
        String chartstyleproperty=eleReportBean.attributeValue("chartstyleproperty");
        if(chartstyleproperty!=null)
        {
            acrbean.setChartstyleproperty(chartstyleproperty.trim(),false);
        }
        return 1;
    }

    public int doPostLoad(ReportBean reportbean)
    {
        super.doPostLoad(reportbean);
        List<ReportDataSetBean> lstDatasetBeans=reportbean.getSbean().getLstDatasetBeans();
        if(lstDatasetBeans!=null&&lstDatasetBeans.size()>0)
        {
            List<String> lstGroupids=new ArrayList<String>();
            Map<String,List<ReportDataSetBean>> mReportDatasetGroupBeans=new HashMap<String,List<ReportDataSetBean>>();
            for(ReportDataSetBean dsbeanTmp:lstDatasetBeans)
            {
                List<ReportDataSetBean> lstDatasetGroupBeans=mReportDatasetGroupBeans.get(dsbeanTmp.getGroupid());
                if(lstDatasetGroupBeans==null)
                {
                    lstDatasetGroupBeans=new ArrayList<ReportDataSetBean>();
                    mReportDatasetGroupBeans.put(dsbeanTmp.getGroupid(),lstDatasetGroupBeans);
                    lstGroupids.add(dsbeanTmp.getGroupid());
                }
                lstDatasetGroupBeans.add(dsbeanTmp);
            }
            List<List<ReportDataSetBean>> lstDatasetGroupBeans=new ArrayList<List<ReportDataSetBean>>();
            for(String groupidTmp:lstGroupids)
            {
                lstDatasetGroupBeans.add(mReportDatasetGroupBeans.get(groupidTmp));
            }
            AbsChartReportBean crbean=(AbsChartReportBean)reportbean.getExtendConfigDataForReportType(KEY);
            crbean.setLstDatasetGroupBeans(lstDatasetGroupBeans);
        }
        AbsChartReportDisplayBean acrdbean=(AbsChartReportDisplayBean)reportbean.getDbean().getExtendConfigDataForReportType(KEY);
        if(acrdbean!=null&&acrdbean.getLabelcolumn()!=null&&!acrdbean.getLabelcolumn().trim().equals(""))
        {
            for(ColBean cbTmp:reportbean.getDbean().getLstCols())
            {
                if(acrdbean.getLabelcolumn().equals(cbTmp.getColumn()))
                {
                    if(cbTmp.isControlCol()||cbTmp.isNonFromDbCol()||Consts.COL_DISPLAYTYPE_HIDDEN.equals(cbTmp.getDisplaytype()))
                    {
                        throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，column为"+acrdbean.getLabelcolumn()
                                +"的列为隐藏列或控制列或不为从数据库取数据的列，不能做配置为<display/>的labelcolumn");
                    }
                    if(acrdbean.getCbeanLabel()!=null)
                    {
                        throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，column为"+acrdbean.getLabelcolumn()
                                +"为<display/>的labelcolumn，因此不能配置多个column为"+acrdbean.getLabelcolumn()+"的<col/>");
                    }
                    acrdbean.setCbeanLabel(cbTmp);
                }else if(cbTmp.isNonFromDbCol())
                {
                    throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()
                            +"失败，此报表在<display/>中配置有labelcolumn，因此不能配置column为{non-fromdb}的<col/>");
                }
            }
            if(acrdbean.getCbeanLabel()==null)
            {
                throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，此报表在<display/>中配置的labelcolumn属性："+acrdbean.getLabelcolumn()
                        +"没有对应的<col/>");
            }
            if(lstDatasetBeans!=null)
            {
                for(ReportDataSetBean dsbeanTmp:lstDatasetBeans)
                {
                    if(dsbeanTmp.getDatasetValueBeanById(acrdbean.getCbeanLabel().getDatasetValueId())!=null)
                    {
                        acrdbean.setLabelDatasetid(dsbeanTmp.getId());
                        break;
                    }
                }
            }
        }
        return 1;
    }
}
