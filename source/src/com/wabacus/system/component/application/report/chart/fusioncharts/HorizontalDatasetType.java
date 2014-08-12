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
import com.wabacus.system.CacheDataBean;
import com.wabacus.system.assistant.ReportAssistant;
import com.wabacus.system.component.application.report.chart.FusionChartsReportType;
import com.wabacus.system.component.application.report.configbean.ColDisplayData;
import com.wabacus.system.intercept.RowDataBean;

public class HorizontalDatasetType extends AbsDatasetType
{
    private ColBean labelColBean;//显示label的<col/>对象

    public HorizontalDatasetType(FusionChartsReportType reportTypeObj,List<ColBean> lstDisplayedColBeans)
    {
        super(reportTypeObj,lstDisplayedColBeans);
        this.labelColBean=reportTypeObj.getAcrdbean().getCbeanLabel();
    }

    public void displayCategoriesPart(StringBuffer resultBuf)
    {
        if(!this.labelColBean.checkDisplayPermission(rrequest)) return;
        RowDataBean rowInterceptorObjTmp;
        ColDisplayData colDisplayDataTmp;
        String stylepropertyTmp=this.reportTypeObj.getRowLabelstyleproperty();
        if(rbean.getInterceptor()!=null)
        {
            rowInterceptorObjTmp=new RowDataBean(reportTypeObj,stylepropertyTmp,lstDisplayedColBeans,null,-1,lstDisplayedColBeans.size());
            rbean.getInterceptor().beforeDisplayReportDataPerRow(rrequest,rbean,rowInterceptorObjTmp);
            stylepropertyTmp=rowInterceptorObjTmp.getRowstyleproperty();
            if(!rowInterceptorObjTmp.isShouldDisplayThisRow()) return;
        }
        boolean isMultiDatasetRows=rbean.getSbean().isMultiDatasetRows();
        String labelDatasetid=null;
        if(isMultiDatasetRows)
        {//在配置有多个<dataset/>时，取第一个查询标题的数据集<dataset/>id存放到labelDatasetid变量中
            labelDatasetid=this.reportTypeObj.getAcrdbean().getLabelDatasetid();
            if(labelDatasetid==null||labelDatasetid.trim().equals("")) return;
        }
        resultBuf.append("<categories ").append(stylepropertyTmp==null?"":stylepropertyTmp.trim()).append(">");
        String currentDatasetidTmp=null,labelTmp;
        for(AbsReportDataPojo dataObjTmp:lstReportData)
        {
            labelTmp=dataObjTmp.getColStringValue(this.labelColBean);
            if(labelTmp!=null&&labelTmp.toLowerCase().startsWith("{non-fromdb}"))
            {
                resultBuf.append(labelTmp.substring("{non-fromdb}".length()));
            }else
            {
                if(isMultiDatasetRows)
                {//如果本报表配置了多个<dataset/>，则只id为firstBelongToDsid的<dataset/>中取<category/>的值
                    currentDatasetidTmp=dataObjTmp.getWx_belongto_datasetid();
                    if(!labelDatasetid.equals(currentDatasetidTmp)) continue;
                }
                colDisplayDataTmp=ColDisplayData.getColDataFromInterceptor(reportTypeObj,this.labelColBean,dataObjTmp,-1,this.reportTypeObj
                        .getColLabelStyleproperty(this.labelColBean,dataObjTmp),labelTmp);
                resultBuf.append("<category label='").append(colDisplayDataTmp.getValue()).append("' ");
                resultBuf.append(colDisplayDataTmp.getStyleproperty()).append("/>");
            }
        }
        resultBuf.append("</categories>");
    }

    public void displaySingleSeriesDataPart(StringBuffer resultBuf)
    {
        ColBean dataColBean=null;//这种显示方式说明只配置了一个统计数据列，且只有一个<dataset/>
        for(ColBean cbTmp:lstDisplayedColBeans)
        {
            if(this.labelColBean.getColumn().equals(cbTmp.getColumn())||cbTmp.isNonFromDbCol()||cbTmp.isNonValueCol()) continue;
            dataColBean=cbTmp;
            break;
        }
        if(dataColBean==null) return;
        if(rbean.getInterceptor()!=null)
        {
            RowDataBean rowInterceptorObjTmp=new RowDataBean(reportTypeObj,null,lstDisplayedColBeans,null,0,lstDisplayedColBeans.size());
            rbean.getInterceptor().beforeDisplayReportDataPerRow(rrequest,rbean,rowInterceptorObjTmp);
            if(!rowInterceptorObjTmp.isShouldDisplayThisRow()) return;
        }
        ColDisplayData colDisplayDataTmp;
        String labelTmp;
        for(AbsReportDataPojo dataObjTmp:lstReportData)
        {
            labelTmp=dataObjTmp.getColStringValue(this.labelColBean);
            if(labelTmp!=null&&labelTmp.toLowerCase().startsWith("{non-fromdb}"))
            {
                resultBuf.append(labelTmp.substring("{non-fromdb}".length()));
            }else
            {
                colDisplayDataTmp=ColDisplayData.getColDataFromInterceptor(reportTypeObj,dataColBean,dataObjTmp,0,dataObjTmp
                        .getColValuestyleproperty(dataColBean.getProperty()),dataObjTmp.getColStringValue(dataColBean));
                resultBuf.append("<set label='").append(labelTmp).append("' ");
                resultBuf.append(" value='").append(colDisplayDataTmp.getValue()).append("'");
                resultBuf.append(colDisplayDataTmp.getStyleproperty()).append("/>");
            }
        }
    }
    
    public void displaySingleLayerDatasetDataPart(StringBuffer resultBuf)
    {
        resultBuf.append(showOneDatasetRowsData(rbean.getSbean().getLstDatasetBeans(),new ArrayList()));
    }
    
    public void displayDualLayerDatasetDataPart(StringBuffer resultBuf)
    {
        List lstProcessedData=new ArrayList();//存放已经处理过的POJO对象，因为一行记录只会来自一个<dataset/>，所以某条记录是来自某个<dataset/>，则不可能再来自其它<dataset/>
        String dataStringFromOneDatasetGroup;
        for(List<ReportDataSetBean> lstDatasetBeansTmp:this.reportTypeObj.getAcrbean().getLstDatasetGroupBeans())
        {
            dataStringFromOneDatasetGroup=showOneDatasetRowsData(lstDatasetBeansTmp,lstProcessedData);
            if(dataStringFromOneDatasetGroup!=null||!dataStringFromOneDatasetGroup.trim().equals(""))
            {
                resultBuf.append("<dataset ").append(lstDatasetBeansTmp.get(0).getDatasetstyleproperty(rrequest,false));
                resultBuf.append(">").append(dataStringFromOneDatasetGroup).append("</dataset>");
            }
        }
    }
    
    private String showOneDatasetRowsData(List<ReportDataSetBean> lstDatasetBeans,List lstProcessedData)
    {
        StringBuffer resultBuf=new StringBuffer();
        RowDataBean rowInterceptorObjTmp;
        ColDisplayData colDisplayDataTmp;
        CacheDataBean cdb=rrequest.getCdb(rbean.getId());
        int rowindex=0;
        boolean isMultiDatasetRows=rbean.getSbean().isMultiDatasetRows();//是否配置了多个<dataset/>
        for(ReportDataSetBean currentDsbean:lstDatasetBeans)
        {
            for(ColBean cbTmp:lstDisplayedColBeans)
            {
                if(this.labelColBean.getColumn().equals(cbTmp.getColumn())) continue;
                if(cbTmp.isNonValueCol()||cbTmp.isNonFromDbCol()) continue;
                String rowStylepropertyTmp=cdb.getDynRowValuestyleproperty(ReportAssistant.getInstance().getHorizontalRowValueStylepropertyKey(
                        currentDsbean.getId(),cbTmp.getProperty(),isMultiDatasetRows));//横向数据集的数据行样式是由<col/>和所属<dataset/>的id（如果配置了多个<dataset/>的情况下）决定的
                if(rowStylepropertyTmp==null) rowStylepropertyTmp=rbean.getDbean().getValuestyleproperty(rrequest,false);
                if(rbean.getInterceptor()!=null)
                {
                    rowInterceptorObjTmp=new RowDataBean(reportTypeObj,rowStylepropertyTmp,lstDisplayedColBeans,null,rowindex,lstDisplayedColBeans
                            .size());
                    rbean.getInterceptor().beforeDisplayReportDataPerRow(rrequest,rbean,rowInterceptorObjTmp);
                    if(!rowInterceptorObjTmp.isShouldDisplayThisRow()) continue;
                    rowStylepropertyTmp=rowInterceptorObjTmp.getRowstyleproperty();
                }
                StringBuffer datasetBuf=new StringBuffer();
                String labelTmp;
                for(AbsReportDataPojo dataObjTmp:lstReportData)
                {
                    if(lstProcessedData!=null&&lstProcessedData.contains(dataObjTmp)) continue;//已经被其它<dataset/>处理过了，则不用处理，因为一行记录只会来自一个数据集<dataset/>
                    if(isMultiDatasetRows&&!currentDsbean.getId().equals(dataObjTmp.getWx_belongto_datasetid()))
                    {//如果报表配置了多个<dataset/>，且当前POJO不属于这个<dataset/>，即不是本<dataset/>查出来的记录
                        continue;
                    }
                    if(currentDsbean.getDatasetValueBeanById(cbTmp.getDatasetValueId())==null) continue;//当前<dataset/>不查询此<col/>的数据
                    if(lstProcessedData!=null) lstProcessedData.add(dataObjTmp);
                    labelTmp=dataObjTmp.getColStringValue(this.labelColBean);
                    if(labelTmp!=null&&labelTmp.toLowerCase().startsWith("{non-fromdb}")) continue;
                    colDisplayDataTmp=ColDisplayData.getColDataFromInterceptor(reportTypeObj,cbTmp,dataObjTmp,rowindex,dataObjTmp
                            .getColValuestyleproperty(cbTmp.getProperty()),dataObjTmp.getColStringValue(cbTmp));
                    datasetBuf.append("<set value='").append(colDisplayDataTmp.getValue()).append("' ");
                    datasetBuf.append(colDisplayDataTmp.getStyleproperty()).append("/>");
                }
                if(datasetBuf.length()>0)
                {
                    if(rowStylepropertyTmp==null) rowStylepropertyTmp="";
                    resultBuf.append("<dataset ").append(rowStylepropertyTmp).append(">");
                    resultBuf.append(datasetBuf.toString());
                    resultBuf.append("</dataset>");
                }
                rowindex++;
            }
        }
        return resultBuf.toString();
    }
}
