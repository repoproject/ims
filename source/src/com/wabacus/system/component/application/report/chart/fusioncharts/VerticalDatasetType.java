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

import java.util.ArrayList;
import java.util.List;

import com.wabacus.config.component.application.report.AbsReportDataPojo;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ReportDataSetBean;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.component.application.report.chart.FusionChartsReportType;
import com.wabacus.system.component.application.report.chart.configbean.FusionChartsReportColBean;
import com.wabacus.system.component.application.report.configbean.ColDisplayData;
import com.wabacus.system.intercept.RowDataBean;
import com.wabacus.util.Consts;

public class VerticalDatasetType extends AbsDatasetType
{
    public VerticalDatasetType(FusionChartsReportType reportTypeObj,List<ColBean> lstDisplayedColBeans)
    {
        super(reportTypeObj,lstDisplayedColBeans);
    }

    public void displayCategoriesPart(StringBuffer resultBuf)
    {
        RowDataBean rowInterceptorObjTmp;
        ColDisplayData colDisplayDataTmp;
        FusionChartsReportColBean fcrcbeanTmp;
        String stylepropertyTmp=this.reportTypeObj.getRowLabelstyleproperty();
        if(rbean.getInterceptor()!=null)
        {
            rowInterceptorObjTmp=new RowDataBean(reportTypeObj,stylepropertyTmp,lstDisplayedColBeans,null,-1,lstDisplayedColBeans.size());
            rbean.getInterceptor().beforeDisplayReportDataPerRow(rrequest,rbean,rowInterceptorObjTmp);
            stylepropertyTmp=rowInterceptorObjTmp.getRowstyleproperty();
            if(!rowInterceptorObjTmp.isShouldDisplayThisRow()) return;
        }
        resultBuf.append("<categories ").append(stylepropertyTmp==null?"":stylepropertyTmp.trim()).append(">");
        for(ColBean cbTmp:lstDisplayedColBeans)
        {
            if(cbTmp.isNonFromDbCol())
            {//不是从数据库获取的数据，则直接显示在<col></col>中配置的内容
                fcrcbeanTmp=(FusionChartsReportColBean)cbTmp.getExtendConfigDataForReportType(FusionChartsReportType.KEY);
                if(fcrcbeanTmp!=null) resultBuf.append(fcrcbeanTmp.getNonfromdb_colvalue(rrequest));
            }else if(!ColBean.NON_LABEL.equals(cbTmp.getLabel(null)))
            {//需要显示label，即<category/>
                colDisplayDataTmp=ColDisplayData.getColDataFromInterceptor(reportTypeObj,cbTmp,null,-1,this.reportTypeObj.getColLabelStyleproperty(cbTmp,null),cbTmp
                        .getLabel(rrequest));
                resultBuf.append("<category label='").append(colDisplayDataTmp.getValue()).append("' ");
                resultBuf.append(colDisplayDataTmp.getStyleproperty()).append("/>");
            }
        }
        resultBuf.append("</categories>");
    }

    public void displaySingleSeriesDataPart(StringBuffer resultBuf)
    {
        AbsReportDataPojo dataObj=lstReportData.get(0);
        if(rbean.getInterceptor()!=null)
        {
            RowDataBean rowInterceptorObjTmp=new RowDataBean(reportTypeObj,null,lstDisplayedColBeans,dataObj,0,lstDisplayedColBeans.size());
            rbean.getInterceptor().beforeDisplayReportDataPerRow(rrequest,rbean,rowInterceptorObjTmp);
            if(!rowInterceptorObjTmp.isShouldDisplayThisRow()) return;
        }
        ColDisplayData colDisplayDataTmp;
        FusionChartsReportColBean fcrcbeanTmp;
        for(ColBean cbTmp:lstDisplayedColBeans)
        {
            if(cbTmp.isNonValueCol())
            {
                throw new WabacusRuntimeException("报表"+this.rbean.getPath()+"是单序列数据图表，不能配置column为{non-value}的列");
            }
            if(cbTmp.isNonFromDbCol())
            {//不是从数据库获取的数据，则直接显示在<col></col>中配置的内容
                fcrcbeanTmp=(FusionChartsReportColBean)cbTmp.getExtendConfigDataForReportType(FusionChartsReportType.KEY);
                if(fcrcbeanTmp!=null) resultBuf.append(fcrcbeanTmp.getNonfromdb_colvalue(rrequest));
            }else
            {
                colDisplayDataTmp=ColDisplayData.getColDataFromInterceptor(reportTypeObj,cbTmp,dataObj,0,dataObj.getColValuestyleproperty(cbTmp
                        .getProperty()),dataObj.getColStringValue(cbTmp));
                resultBuf.append("<set label='").append(cbTmp.getLabel(rrequest)).append("' ");
                resultBuf.append(" value='").append(colDisplayDataTmp.getValue()).append("'");
                resultBuf.append(colDisplayDataTmp.getStyleproperty()).append("/>");
            }
        }
    }
    
    public void displaySingleLayerDatasetDataPart(StringBuffer resultBuf)
    {
        for(int i=0;i<lstReportData.size();i++)
        {
            resultBuf.append(showRowData(lstReportData.get(i),i));
        }
    }

    public void displayDualLayerDatasetDataPart(StringBuffer resultBuf)
    {
        List<String> lstDatasetIdsInGroupTmp=new ArrayList<String>();
        List lstProcessedData=new ArrayList();//存放已经处理过的POJO对象，因为一行记录只会来自一个<dataset/>，所以某条记录是来自某个<dataset/>，则不可能再来自其它<dataset/>
        String currentDatasetidTmp;
        for(List<ReportDataSetBean> lstDatasetBeansTmp:this.reportTypeObj.getAcrbean().getLstDatasetGroupBeans())
        {
            lstDatasetIdsInGroupTmp.clear();//清空用于存放本<dataset/>分组中包含的datasetid，以便判断某条记录所在的<dataset/>的id是否属于此组数据集
            for(ReportDataSetBean dsbeanTmp:lstDatasetBeansTmp)
            {
                lstDatasetIdsInGroupTmp.add(dsbeanTmp.getId()+";");
            }
            StringBuffer datasetGroupBuf=new StringBuffer();
            AbsReportDataPojo dataObjTmp;
            for(int i=0;i<lstReportData.size();i++)
            {
                dataObjTmp=this.lstReportData.get(i);
                if(lstProcessedData.contains(dataObjTmp)) continue;//此POJO已经在其它组的<dataset/>中处理过了，则肯定不属于此组<dataset/>，因为一个记录行只会来自一个<dataset/>
                currentDatasetidTmp=dataObjTmp.getWx_belongto_datasetid();
                if(!lstDatasetIdsInGroupTmp.contains(currentDatasetidTmp+";")) continue;//此POJO所在的<dataset/>的id不属于本次显示的<dataset/>组
                lstProcessedData.add(dataObjTmp);
                datasetGroupBuf.append(showRowData(dataObjTmp,i));
            }
            if(datasetGroupBuf.length()>0)
            {
                resultBuf.append("<dataset ").append(lstDatasetBeansTmp.get(0).getDatasetstyleproperty(rrequest,false));//只取第一个<dataset/>的styleproperty显示在外层<dataset/>中
                resultBuf.append(">").append(datasetGroupBuf.toString()).append("</dataset>");
            }
        }
    }
    
    private String showRowData(AbsReportDataPojo dataObj,int rowindex)
    {
        String stylepropertyTmp=dataObj.getRowValuestyleproperty();
        if(rbean.getInterceptor()!=null)
        {
            RowDataBean rowInterceptorObjTmp=new RowDataBean(reportTypeObj,stylepropertyTmp,lstDisplayedColBeans,dataObj,rowindex,
                    lstDisplayedColBeans.size());
            rbean.getInterceptor().beforeDisplayReportDataPerRow(rrequest,rbean,rowInterceptorObjTmp);
            if(!rowInterceptorObjTmp.isShouldDisplayThisRow()) return "";
            stylepropertyTmp=rowInterceptorObjTmp.getRowstyleproperty();
        }
        if(stylepropertyTmp==null) stylepropertyTmp="";
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("<dataset ").append(stylepropertyTmp).append(">");
        ColDisplayData colDisplayDataTmp;
        for(ColBean cbTmp:lstDisplayedColBeans)
        {
            if(!cbTmp.isNonFromDbCol()&&!cbTmp.isNonValueCol())
            {//{non-fromdb}的列不在这里处理，而是在<categories/>中显示
                colDisplayDataTmp=ColDisplayData.getColDataFromInterceptor(reportTypeObj,cbTmp,dataObj,rowindex,dataObj
                        .getColValuestyleproperty(cbTmp.getProperty()),dataObj.getColStringValue(cbTmp));
                resultBuf.append("<set value='").append(colDisplayDataTmp.getValue()).append("' ");
                resultBuf.append(colDisplayDataTmp.getStyleproperty()).append("/>");
            }
        }
        resultBuf.append("</dataset>");
        return resultBuf.toString();
    }
    
    public void displayXyPlotChartDataPart(StringBuffer resultBuf)
    {
        int rowindex=0;
        String prevDatasetidTmp=null, currentDatasetidTmp=null, stylepropertyTmp;
        boolean isMultiDatasetRows=rbean.getSbean().isMultiDatasetRows();
        StringBuffer dataBufTmp=new StringBuffer();
        for(AbsReportDataPojo dataObjTmp:this.lstReportData)
        {
            if(isMultiDatasetRows)
            {//有多个数据集<dataset/>
                currentDatasetidTmp=dataObjTmp.getWx_belongto_datasetid();
                currentDatasetidTmp=currentDatasetidTmp==null?Consts.DEFAULT_KEY:currentDatasetidTmp.trim();
                if(!currentDatasetidTmp.equals(prevDatasetidTmp))
                {
                    if(dataBufTmp.length()>0)
                    {
                        resultBuf.append("<dataset ").append(
                                rbean.getSbean().getDatasetBeanById(prevDatasetidTmp).getDatasetstyleproperty(rrequest,false)).append(">");
                        resultBuf.append(dataBufTmp.toString()).append("</dataset>");
                        dataBufTmp=new StringBuffer();
                    }
                    prevDatasetidTmp=currentDatasetidTmp;
                }
            }
            stylepropertyTmp=dataObjTmp.getRowValuestyleproperty();
            if(rbean.getInterceptor()!=null)
            {
                RowDataBean rowInterceptorObjTmp=new RowDataBean(reportTypeObj,stylepropertyTmp,lstDisplayedColBeans,dataObjTmp,rowindex,
                        lstDisplayedColBeans.size());
                rbean.getInterceptor().beforeDisplayReportDataPerRow(rrequest,rbean,rowInterceptorObjTmp);
                if(!rowInterceptorObjTmp.isShouldDisplayThisRow()) continue;
                stylepropertyTmp=rowInterceptorObjTmp.getRowstyleproperty();
            }
            if(stylepropertyTmp==null) stylepropertyTmp="";
            dataBufTmp.append("<set ").append(stylepropertyTmp);
            ColDisplayData colDisplayDataTmp;
            for(ColBean cbTmp:lstDisplayedColBeans)
            {
                if("x".equals(cbTmp.getProperty())||"y".equals(cbTmp.getProperty())||"z".equals(cbTmp.getProperty()))
                {
                    colDisplayDataTmp=ColDisplayData.getColDataFromInterceptor(reportTypeObj,cbTmp,dataObjTmp,rowindex,dataObjTmp
                            .getColValuestyleproperty(cbTmp.getProperty()),dataObjTmp.getColStringValue(cbTmp));
                    dataBufTmp.append(cbTmp.getProperty()+"='").append(colDisplayDataTmp.getValue()).append("' ");
                }
            }
            dataBufTmp.append("/>");
        }
        if(dataBufTmp.length()>0)
        {
            resultBuf.append("<dataset ").append(rbean.getSbean().getDatasetBeanById(currentDatasetidTmp).getDatasetstyleproperty(rrequest,false));
            resultBuf.append(">").append(dataBufTmp.toString()).append("</dataset>");
        }
    }
}
