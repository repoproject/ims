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
package com.wabacus.system.component.application.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.itextpdf.text.Element;
import com.wabacus.config.Config;
import com.wabacus.config.ConfigLoadAssistant;
import com.wabacus.config.component.ComponentConfigLoadManager;
import com.wabacus.config.component.IComponentConfigBean;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.DisplayBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.config.component.application.report.extendconfig.IGroupExtendConfigLoad;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.ListReportAssistant;
import com.wabacus.system.assistant.StandardExcelAssistant;
import com.wabacus.system.component.application.report.abstractreport.AbsListReportType;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportColBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportSubDisplayBean;
import com.wabacus.system.component.application.report.configbean.ColAndGroupTitlePositionBean;
import com.wabacus.system.component.application.report.configbean.UltraListReportDisplayBean;
import com.wabacus.system.component.application.report.configbean.UltraListReportGroupBean;
import com.wabacus.system.component.application.report.configbean.crosslist.AbsCrossListReportColAndGroupBean;
import com.wabacus.system.component.application.report.configbean.crosslist.CrossListReportBean;
import com.wabacus.system.component.application.report.configbean.crosslist.CrossListReportColBean;
import com.wabacus.system.component.application.report.configbean.crosslist.CrossListReportDynDatasetBean;
import com.wabacus.system.component.application.report.configbean.crosslist.CrossListReportGroupBean;
import com.wabacus.system.component.application.report.configbean.crosslist.CrossListReportStatiBean;
import com.wabacus.system.component.container.AbsContainerType;
import com.wabacus.util.Consts;
import com.wabacus.util.Tools;
import com.wabacus.util.UniqueArrayList;

public class CrossListReportType extends UltraListReportType implements IGroupExtendConfigLoad
{
    private static Log log=LogFactory.getLog(CrossListReportType.class);

    public final static String KEY=CrossListReportType.class.getName();

    private int dynColGroupIndexId=10000;

    private Map<String,ColAndGroupTitlePositionBean> mDynColGroupPositionBeans;

    private List lstAllDisplayColAndGroupBeans;

    private Map<String,RuntimeDynamicDatasetBean> mDynamicDatasetBeans;

    private List<VerticalCrossStatisticColData> lstVerticalStatiColBeansAndValues;

    public CrossListReportType(AbsContainerType parentContainerType,IComponentConfigBean comCfgBean,ReportRequest rrequest)
    {
        super(parentContainerType,comCfgBean,rrequest);
    }

    private boolean hasLoadedData=false;

    public boolean isLoadedReportData()
    {
        return this.hasLoadedData;
    }

    public void setHasLoadedDataFlag(boolean hasLoadedDataFlag)
    {
        super.setHasLoadedDataFlag(hasLoadedDataFlag);
        this.hasLoadedData=hasLoadedDataFlag;
    }

    public int generateColGroupIdxId()
    {
        return dynColGroupIndexId++;
    }

    public void loadReportData(boolean shouldInvokePostaction)
    {
        if(this.hasLoadedData) return;
        this.hasLoadedData=true;
        CrossListReportBean csrbean=(CrossListReportBean)rbean.getExtendConfigDataForReportType(KEY);
        if(csrbean==null||!csrbean.isHasDynamicColGroupBean())
        {
            super.loadReportData(shouldInvokePostaction);
            return;
        }
        super.initLoadReportData();
        List<ColBean> lstAllRuntimeColBeans=new ArrayList<ColBean>();
        this.cacheDataBean.setLstDynOrderColBeans(lstAllRuntimeColBeans);
        this.lstAllDisplayColAndGroupBeans=new ArrayList();
        List lstConfigChildren=getLstConfigChildren(rbean.getDbean());
        Map<String,Boolean> mDynamicColGroupDisplayType=getMDynamicColGroupDisplayType(lstConfigChildren);
        getAllRuntimeColGroupBeans(lstConfigChildren,this.lstAllDisplayColAndGroupBeans,lstAllRuntimeColBeans,mDynamicColGroupDisplayType);
        processFixedColsAndRows(rbean);
        if(csrbean.isShouldCreateRowSelectCol()&&rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
        {
            super.insertRowSelectNewCols(alrbean,lstAllRuntimeColBeans);
        }
        this.mDynColGroupPositionBeans=calPosition(rbean,this.lstAllDisplayColAndGroupBeans,null);
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PLAINEXCEL)
        {
            calPositionForStandardExcel(lstAllDisplayColAndGroupBeans,null,mDynColGroupPositionBeans);
        }
        for(Entry<String,RuntimeDynamicDatasetBean> entryTmp:this.mDynamicDatasetBeans.entrySet())
        {
            entryTmp.getValue().buildRealSelectSqls();
        }
        super.loadReportData(shouldInvokePostaction);
        //如果某个交叉统计报表配置了针对每一列数据的统计，开始加载针对每个动态列的统计数据
        createVerticalStaticColBeansAndData(lstAllRuntimeColBeans);
    }

    public void getAllRuntimeColGroupBeans(List lstConfigChildren,List lstAllRuntimeChildren,List<ColBean> lstAllRuntimeColBeans,
            Map<String,Boolean> mDynamicColGroupDisplayType)
    {
        AbsCrossListReportColAndGroupBean clrcgbeanTmp;
        for(Object childColGroupBeanTmp:lstConfigChildren)
        {
            if(!(childColGroupBeanTmp instanceof ColBean)&&!(childColGroupBeanTmp instanceof UltraListReportGroupBean)) continue;
            clrcgbeanTmp=ListReportAssistant.getInstance().getCrossColAndGroupBean(childColGroupBeanTmp);
            if(clrcgbeanTmp==null||(!clrcgbeanTmp.isDynamicColGroup()&&!clrcgbeanTmp.hasDynamicColGroupChild()))
            {
                if(childColGroupBeanTmp instanceof ColBean)
                {
                    lstAllRuntimeColBeans.add((ColBean)childColGroupBeanTmp);
                    if(((ColBean)childColGroupBeanTmp).getDisplaytype()!=Consts.COL_DISPLAYTYPE_HIDDEN)
                    {
                        lstAllRuntimeChildren.add(childColGroupBeanTmp);
                    }
                }else if(childColGroupBeanTmp instanceof UltraListReportGroupBean)
                {
                    ((UltraListReportGroupBean)childColGroupBeanTmp).getAllColBeans(lstAllRuntimeColBeans,null);
                    lstAllRuntimeChildren.add(childColGroupBeanTmp);
                }
            }else
            {
                clrcgbeanTmp.getRuntimeColGroupBeans(this,lstAllRuntimeChildren,lstAllRuntimeColBeans,mDynamicColGroupDisplayType);
            }
        }
    }

    private Map<String,Boolean> getMDynamicColGroupDisplayType(List lstConfigChildren)
    {
        Map<String,Boolean> mDynamicColGroupDisplayType=new HashMap<String,Boolean>();
        AbsCrossListReportColAndGroupBean clrcgbeanTmp;
        for(Object childColGroupBeanTmp:lstConfigChildren)
        {
            if(!(childColGroupBeanTmp instanceof ColBean)&&!(childColGroupBeanTmp instanceof UltraListReportGroupBean)) continue;
            clrcgbeanTmp=ListReportAssistant.getInstance().getCrossColAndGroupBean(childColGroupBeanTmp);
            if(clrcgbeanTmp==null||(!clrcgbeanTmp.isDynamicColGroup()&&!clrcgbeanTmp.hasDynamicColGroupChild()))
            {
                continue;
            }else
            {
                clrcgbeanTmp.getMDynamicColGroupDisplayType(rrequest,mDynamicColGroupDisplayType);
            }
        }
        return mDynamicColGroupDisplayType;
    }

    private void createVerticalStaticColBeansAndData(List<ColBean> lstAllRuntimeColBeans)
    {
        boolean hasVerticalStatiData=false;
        for(Entry<String,RuntimeDynamicDatasetBean> entryTmp:this.mDynamicDatasetBeans.entrySet())
        {
            hasVerticalStatiData|=entryTmp.getValue().loadVerticalStatiData();
        }
        if(!hasVerticalStatiData) return;
        this.lstVerticalStatiColBeansAndValues=new ArrayList<VerticalCrossStatisticColData>();
        int colspans=0;
        if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PDF&&rrequest.getShowtype()!=Consts.DISPLAY_ON_PLAINEXCEL&&alrdbean.getRowGroupColsNum()>0
                &&alrdbean.getRowgrouptype()==2)
        {
            colspans=1;
        }
        AbsCrossListReportColAndGroupBean crossColGroupBeanTmp;
        CrossListReportColBean crcbeanTmp;
        List<AbsCrossListReportColAndGroupBean> lstNotDisplayStatiLabelBeans=new ArrayList<AbsCrossListReportColAndGroupBean>();
        ColBean cbTmp;
        ColAndGroupTitlePositionBean positionBeanTmp;
        AbsListReportColBean alrcbeanTmp;
        Boolean isPrevFixedCol=null;
        for(int i=0;i<lstAllRuntimeColBeans.size();i++)
        {
            cbTmp=lstAllRuntimeColBeans.get(i);
            positionBeanTmp=this.mDynColGroupPositionBeans.get(cbTmp.getColid());
            if(positionBeanTmp==null||positionBeanTmp.getDisplaymode()<=0) continue;
            if("[DYN_COL_DATA]".equals(cbTmp.getProperty()))
            {
                isPrevFixedCol=false;
                crcbeanTmp=(CrossListReportColBean)cbTmp.getExtendConfigDataForReportType(KEY);
                crossColGroupBeanTmp=crcbeanTmp.getBelongToRootOwner();
                if(crossColGroupBeanTmp.isCommonCrossColGroup()||!crossColGroupBeanTmp.getInnerDynamicColBean().isHasVerticalstatistic())
                {
                    colspans++;
                }else
                {
                    lstNotDisplayStatiLabelBeans.add(crossColGroupBeanTmp);
                    if(colspans>0)
                    {//前面已经显示了普通列了
                        createVerticalStatiLabelColBean(lstNotDisplayStatiLabelBeans,colspans);
                        lstNotDisplayStatiLabelBeans.clear();
                        colspans=0;
                    }
                    crossColGroupBeanTmp.getVerticalStatisticColBeanAndData(this,lstAllRuntimeColBeans);
                    String currentCrossStatiColGroupid=crossColGroupBeanTmp.getRootCrossColGroupId();
                    int j=i;
                    for(;j<lstAllRuntimeColBeans.size();j++)
                    {
                        cbTmp=lstAllRuntimeColBeans.get(j);
                        crcbeanTmp=(CrossListReportColBean)cbTmp.getExtendConfigDataForReportType(KEY);
                        if(crcbeanTmp==null||crcbeanTmp.getBelongToRootOwner()==null
                                ||!currentCrossStatiColGroupid.equals(crcbeanTmp.getBelongToRootOwner().getRootCrossColGroupId()))
                        {
                            break;
                        }
                    }
                    i=j-1;
                }
            }else
            {
                alrcbeanTmp=(AbsListReportColBean)cbTmp.getExtendConfigDataForReportType(AbsListReportType.KEY);
                if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PDF&&rrequest.getShowtype()!=Consts.DISPLAY_ON_PLAINEXCEL&&alrcbeanTmp.isRowgroup()
                        &&alrdbean.getRowgrouptype()==2)
                {
                    isPrevFixedCol=alrcbeanTmp!=null&&alrcbeanTmp.isFixedCol(rrequest);
                }else if(isPrevFixedCol==null)
                {
                    colspans++;
                    isPrevFixedCol=alrcbeanTmp!=null&&alrcbeanTmp.isFixedCol(rrequest);
                }else if(isPrevFixedCol)
                {
                    if(alrcbeanTmp!=null&&alrcbeanTmp.isFixedCol(rrequest))
                    {
                        colspans++;
                    }else
                    {
                        createVerticalStatiLabelColBean(lstNotDisplayStatiLabelBeans,colspans);
                        lstNotDisplayStatiLabelBeans.clear();
                        colspans=1;
                        isPrevFixedCol=false;
                    }
                }else
                {
                    colspans++;
                    
                }
            }
        }
        if(colspans>0) createVerticalStatiLabelColBean(lstNotDisplayStatiLabelBeans,colspans);
        for(Entry<String,RuntimeDynamicDatasetBean> entryTmp:this.mDynamicDatasetBeans.entrySet())
        {
            entryTmp.getValue().closeResultSet();
        }
    }

    public void addVerticalCrossStatisticColData(ColBean cbean,Object displayvalue,int colspan)
    {
        if(displayvalue==null) displayvalue="";
        this.lstVerticalStatiColBeansAndValues.add(new VerticalCrossStatisticColData(cbean,String.valueOf(displayvalue),colspan));
    }

    private void createVerticalStatiLabelColBean(List<AbsCrossListReportColAndGroupBean> lstNotDisplayStatiLabelBeans,int colspan)
    {
        if(lstNotDisplayStatiLabelBeans==null||lstNotDisplayStatiLabelBeans.size()==0)
        {//到目前为止所有交叉统计列都已经显示了verticallabel，则此普通列只要显示空字符串
            ColBean cbTmp=new ColBean(rbean.getDbean());
            cbTmp.setValuestyleproperty(" colspan=\""+colspan+"\"",true);
            
            
            this.lstVerticalStatiColBeansAndValues.add(new VerticalCrossStatisticColData(cbTmp,"",colspan));
        }else
        {
            ColBean cbTmp=null;
            String displayvalue;
            for(int i=0;i<lstNotDisplayStatiLabelBeans.size();i++)
            {
                displayvalue=rrequest.getI18NStringValue(lstNotDisplayStatiLabelBeans.get(i).getInnerDynamicColBean().getVerticallabel());
                if(colspan<=0)
                {
                    this.lstVerticalStatiColBeansAndValues.get(this.lstVerticalStatiColBeansAndValues.size()-1).appendColValue(displayvalue);
                }else
                {
                    cbTmp=new ColBean(rbean.getDbean());
                    String valuestyleproperty=lstNotDisplayStatiLabelBeans.get(i).getInnerDynamicColBean().getVerticallabelstyleproperty();
                    valuestyleproperty=valuestyleproperty==null?"":valuestyleproperty.trim();
                    String strcolspan=Tools.getPropertyValueByName("colspan",valuestyleproperty,false);
                    if(strcolspan==null||strcolspan.trim().equals("")) strcolspan="1";
                    int icolspan=Integer.parseInt(strcolspan);
                    if(icolspan>colspan||icolspan<colspan&&i==lstNotDisplayStatiLabelBeans.size()-1)
                    {
                        icolspan=colspan;
                    }
                    valuestyleproperty=Tools.removePropertyValueByName("colspan",valuestyleproperty);
                    valuestyleproperty=valuestyleproperty+" colspan=\""+icolspan+"\"";
                    cbTmp.setValuestyleproperty(valuestyleproperty,true);
                    this.lstVerticalStatiColBeansAndValues.add(new VerticalCrossStatisticColData(cbTmp,displayvalue,icolspan));
                    colspan-=icolspan;
                }
            }

        }
    }

    public void addDynamicSelectCols(AbsCrossListReportColAndGroupBean crossColGroupBean,String dynSelectCols)
    {
        String datasetid=crossColGroupBean.getDatasetid();
        datasetid=datasetid==null||datasetid.trim().equals("")?CrossListReportBean.EMPTY_DATASET_ID:datasetid.trim();
        if(this.mDynamicDatasetBeans==null) this.mDynamicDatasetBeans=new HashMap<String,RuntimeDynamicDatasetBean>();
        RuntimeDynamicDatasetBean dynDatasetBean=this.mDynamicDatasetBeans.get(datasetid);
        if(dynDatasetBean==null)
        {
            dynDatasetBean=new RuntimeDynamicDatasetBean(crossColGroupBean.getDatasetBean());
            this.mDynamicDatasetBeans.put(datasetid,dynDatasetBean);
        }
        dynDatasetBean.addSelectColsOfCrossColGroup(crossColGroupBean.getRootCrossColGroupId(),dynSelectCols);
    }

    public ResultSet getVerticalStatisticResultSet(AbsCrossListReportColAndGroupBean crossColGroupBean)
    {
        if(this.mDynamicDatasetBeans==null) return null;
        String datasetid=crossColGroupBean.getDatasetid();
        datasetid=datasetid==null||datasetid.trim().equals("")?CrossListReportBean.EMPTY_DATASET_ID:datasetid.trim();
        RuntimeDynamicDatasetBean dynDatasetBean=this.mDynamicDatasetBeans.get(datasetid);
        if(dynDatasetBean==null) return null;
        return dynDatasetBean.getVerticalStatiResultSet();
    }

    protected List getLstDisplayChildren(UltraListReportDisplayBean ulrdbean)
    {
        return this.lstAllDisplayColAndGroupBeans;
    }

    protected List<ColBean> getLstAllRealColBeans()
    {
        return this.cacheDataBean.getLstDynOrderColBeans();
    }
    
    protected String getLastDisplayColIdInFirstTitleRow(UltraListReportDisplayBean urldbean)
    {
        return "[CROSSLISTREPORT]";
    }

    protected String showSubRowDataForWholeReport(int position)
    {
        StringBuffer resultBuf=new StringBuffer();
        if(position==AbsListReportSubDisplayBean.SUBROW_POSITION_BOTTOM&&this.lstVerticalStatiColBeansAndValues!=null
                &&this.lstVerticalStatiColBeansAndValues.size()>0)
        {
            resultBuf.append("<tr  class='cls-data-tr'>");
            for(VerticalCrossStatisticColData vcDataTmp:this.lstVerticalStatiColBeansAndValues)
            {
                resultBuf.append("<td class='cls-data-td-list' ");
                resultBuf.append(vcDataTmp.getCbean().getValuestyleproperty(rrequest,false)).append(">");
                resultBuf.append(vcDataTmp.getColValue());
                resultBuf.append("</td>");
            }
            resultBuf.append("</tr>");
        }
        resultBuf.append(super.showSubRowDataForWholeReport(position));
        return resultBuf.toString();
    }

    protected void showSubRowDataInPlainExcelForWholeReport(Workbook workbook,CellStyle dataCellStyle,int position)
    {
        if(position==AbsListReportSubDisplayBean.SUBROW_POSITION_BOTTOM&&this.lstVerticalStatiColBeansAndValues!=null
                &&this.lstVerticalStatiColBeansAndValues.size()>0)
        {
            String stativalue;
            int startcolidx=0;
            int endcolidx=-1;
            CellRangeAddress region;
            for(VerticalCrossStatisticColData vcDataTmp:this.lstVerticalStatiColBeansAndValues)
            {
                stativalue=vcDataTmp.getColValue();
                stativalue=Tools.replaceAll(stativalue,"&nbsp;"," ");
                stativalue=stativalue.replaceAll("<.*?\\>","");
                startcolidx=endcolidx+1;
                endcolidx=startcolidx+vcDataTmp.getColspan()-1;
                region=new CellRangeAddress(excelRowIdx,excelRowIdx,startcolidx,endcolidx);
                StandardExcelAssistant.getInstance().setRegionCellStringValue(workbook,excelSheet,region,dataCellStyle,stativalue);
            }
            excelRowIdx++;
        }
        super.showSubRowDataInPlainExcelForWholeReport(workbook,dataCellStyle,position);
    }

    protected void showSubRowDataOnPdfForWholeReport(int position)
    {
        if(position==AbsListReportSubDisplayBean.SUBROW_POSITION_BOTTOM&&this.lstVerticalStatiColBeansAndValues!=null
                &&this.lstVerticalStatiColBeansAndValues.size()>0)
        {
            String stativalue;
            int startcolidx=0;
            int endcolidx=-1;
            CellRangeAddress region;
            for(VerticalCrossStatisticColData vcDataTmp:this.lstVerticalStatiColBeansAndValues)
            {
                stativalue=vcDataTmp.getColValue();
                stativalue=Tools.replaceAll(stativalue,"&nbsp;"," ");
                stativalue=stativalue.replaceAll("<.*?\\>","");//替换掉html标签
                startcolidx=endcolidx+1;
                endcolidx=startcolidx+vcDataTmp.getColspan();
                addDataCell(vcDataTmp.getCbean(),stativalue,1,endcolidx-startcolidx,Element.ALIGN_LEFT);
            }
        }
        super.showSubRowDataOnPdfForWholeReport(position);
    }

    protected Map<String,ColAndGroupTitlePositionBean> getRuntimeColAndGroupPosition(UltraListReportDisplayBean ulrdbean)
    {
        return this.mDynColGroupPositionBeans;
    }

    public String getColSelectedMetadata()
    {
        return "";
    }

    public List sortChildrenByDynColOrders(List lstChildren,List<String> lstDynColids,
            Map<String,ColAndGroupTitlePositionBean> colAndGroupTitlePostions)
    {
        return lstChildren;
    }

    public int afterColLoading(ColBean colbean,List<XmlElementBean> lstEleColBeans)
    {
        XmlElementBean eleColBean=lstEleColBeans.get(0);
        CrossListReportColBean clrcbean=(CrossListReportColBean)colbean.getExtendConfigDataForReportType(KEY);
        if(clrcbean==null)
        {
            clrcbean=new CrossListReportColBean(colbean);
            colbean.setExtendConfigDataForReportType(KEY,clrcbean);
        }
        loadCrossColAndGroupCommonConfig(eleColBean,clrcbean);
        String verticalstatistic=eleColBean.attributeValue("verticalstatistic");
        String verticallabel=eleColBean.attributeValue("verticallabel");
        String verticallabelstyleproperty=eleColBean.attributeValue("verticallabelstyleproperty");
        clrcbean.setHasVerticalstatistic(verticalstatistic!=null&&verticalstatistic.trim().equalsIgnoreCase("true"));
        verticallabel=verticallabel==null?"":Config.getInstance().getResourceString(null,colbean.getPageBean(),verticallabel,true).trim();
        clrcbean.setVerticallabel(verticallabel);
        clrcbean.setVerticallabelstyleproperty(verticallabelstyleproperty==null?"":verticallabelstyleproperty.trim());
        clrcbean.setDatasetid(colbean.getDatasetValueId());
        
        
        //            throw new WabacusConfigLoadingException("加载报表"+colbean.getReportBean().getPath()+"失败，为交叉<col/>配置column属性不能为"
        
        
        List<XmlElementBean> lstEleChildren=eleColBean.getLstChildElementsByName("statistic");
        if(lstEleChildren!=null&&lstEleChildren.size()>0)
        {
            List<CrossListReportStatiBean> lstStatisBeans=new ArrayList<CrossListReportStatiBean>();
            clrcbean.setLstStatisBeans(lstStatisBeans);
            CrossListReportStatiBean statisBean;
            List<String> lstExistIds=new ArrayList<String>();
            for(XmlElementBean eleStaticBeanTmp:lstEleChildren)
            {
                statisBean=new CrossListReportStatiBean(colbean);
                String id=eleStaticBeanTmp.attributeValue("id");
                if(id==null||id.trim().equals(""))
                {
                    throw new WabacusConfigLoadingException("加载交叉报表"+colbean.getReportBean().getPath()+"失败，没有为<statistic/>配置id");
                }
                id=id.trim();
                if(lstExistIds.contains(id))
                {
                    throw new WabacusConfigLoadingException("加载交叉报表"+colbean.getReportBean().getPath()+"失败，<statistic/>的id值"+id+"存在重复");
                }
                lstExistIds.add(id);
                statisBean.setId(colbean.getColumn()+"."+id);//加上column前缀，方便保持唯一性，尤其是授权的时候，不会与其它<col/>的column有冲突
                String type=eleStaticBeanTmp.attributeValue("type");
                String column=eleStaticBeanTmp.attributeValue("column");
                String label=eleStaticBeanTmp.attributeValue("label");
                String labelstyleproperty=eleStaticBeanTmp.attributeValue("labelstyleproperty");
                String valuestyleproperty=eleStaticBeanTmp.attributeValue("valuestyleproperty");
                String statitems=eleStaticBeanTmp.attributeValue("statitems");
                statisBean.setType(type==null?"":type.toLowerCase().trim());
                statisBean.setColumn(column==null?"":column.trim());
                statisBean.setLstLabels(Tools
                        .parseAllStringToList(Config.getInstance().getResourceString(null,colbean.getPageBean(),label,false),"|"));
                statisBean.setLstLabelstyleproperties(Tools.parseAllStringToList(labelstyleproperty,"|"));
                statisBean.setLstValuestyleproperties(Tools.parseAllStringToList(valuestyleproperty,"|"));
                statisBean.setDatatypeObj(ConfigLoadAssistant.loadDataType(eleStaticBeanTmp));
                if(statitems!=null)
                {
                    statitems=statitems.trim();
                    if(statitems.equals(""))
                    {
                        statisBean.setLstStatitems(null);
                    }else
                    {
                        List<String> lstTemp=new UniqueArrayList<String>();
                        lstTemp.addAll(Tools.parseAllStringToList(statitems,"|"));
                        statisBean.setLstStatitems(lstTemp);
                    }
                }
                statisBean.validateConfig();
                lstStatisBeans.add(statisBean);
            }
        }
        super.afterColLoading(colbean,lstEleColBeans);
        return 1;
    }

    public int beforeGroupLoading(UltraListReportGroupBean groupbean,List<XmlElementBean> lstEleGroupBeans)
    {
        CrossListReportGroupBean clrgbean=(CrossListReportGroupBean)groupbean.getExtendConfigDataForReportType(KEY);
        if(clrgbean==null)
        {
            clrgbean=new CrossListReportGroupBean(groupbean);
            groupbean.setExtendConfigDataForReportType(KEY,clrgbean);
        }
        XmlElementBean eleGroupBean=lstEleGroupBeans.get(0);
        String column=eleGroupBean.attributeValue("column");
        if(column!=null)
        {
            column=column.trim();
            if(!column.equals(""))
            {
                int idx=column.indexOf(".");
                if(idx>0)
                {
                    clrgbean.setDatasetid(column.substring(0,idx).trim());
                    column=column.substring(idx+1).trim();
                }
            }
            clrgbean.setColumn(column.trim());
        }
        loadCrossColAndGroupCommonConfig(eleGroupBean,clrgbean);
        return 1;
    }

    private void loadCrossColAndGroupCommonConfig(XmlElementBean eleColGroupBean,AbsCrossListReportColAndGroupBean clrcgbean)
    {
        String staticondition=eleColGroupBean.attributeValue("staticondition");
        if(staticondition!=null&&!staticondition.trim().equals(""))
        {
            clrcgbean.setStaticondition(staticondition.trim());
            clrcgbean.setLstStatiConditions(loadCrossColAndGroupConditios(clrcgbean.getOwner().getReportBean(),eleColGroupBean
                    .getChildElementByName("staticonditions")));
        }
        String realvalue=eleColGroupBean.attributeValue("realvalue");
        if(realvalue!=null) clrcgbean.setRealvalue(realvalue.trim());
        String dataset=eleColGroupBean.attributeValue("dataset");
        if(dataset!=null)
        {
            dataset=dataset.trim();
            if(Tools.isDefineKey("class",dataset)&&Tools.getRealKeyByDefine("class",dataset).trim().equals(""))
            {
                dataset="";
            }
            clrcgbean.setConfigDynColGroupTitleDataset(dataset.trim());
            if(!dataset.equals(""))
            {
                clrcgbean.setLstDatasetConditions(loadCrossColAndGroupConditios(clrcgbean.getOwner().getReportBean(),eleColGroupBean
                        .getChildElementByName("datasetconditions")));
                XmlElementBean eleHeaderFormatBean=eleColGroupBean.getChildElementByName("format");
                if(eleHeaderFormatBean!=null)
                {
                    XmlElementBean eleFormatValueBean=eleHeaderFormatBean.getChildElementByName("value");
                    if(eleFormatValueBean!=null)
                    {
                        String formatmethod=eleFormatValueBean.getContent();
                        if(formatmethod==null||formatmethod.trim().equals(""))
                        {//如果将<value/>配置为空
                            clrcgbean.setDataHeaderPojoClass(null);
                            clrcgbean.setDataheaderformatContent(null);
                        }else
                        {
                            List<XmlElementBean> lstEleFormatBeans=new ArrayList<XmlElementBean>();
                            lstEleFormatBeans.add(eleHeaderFormatBean);
                            //这里把要生成的format内容先存起来，等在doPostLoad()方法中生成类，因为如果在这里生成，则对于定义了<group/>的报表，定义在<group/>之外的动态<col/>会生成两次字节码（因为会加载两次）导致出错
                            clrcgbean.setLstDataHeaderFormatImports(ComponentConfigLoadManager.getListImportPackages(lstEleFormatBeans));
                            clrcgbean.setDataheaderformatContent(formatmethod.trim());
                        }
                    }
                }
            }
        }
    }

    private List<ConditionBean> loadCrossColAndGroupConditios(ReportBean reportbean,XmlElementBean eleConditions)
    {
        if(eleConditions==null) return null;
        List<ConditionBean> lstConditionBeans=new ArrayList<ConditionBean>();
        List<XmlElementBean> lstConditionEles=eleConditions.getLstChildElementsByName("condition");
        if(lstConditionEles!=null&&lstConditionEles.size()>0)
        {//在<tablecondtions/>中配置了条件
            ConditionBean cbTmp;
            for(XmlElementBean eleConBeanTmp:lstConditionEles)
            {
                if(eleConBeanTmp==null) continue;
                cbTmp=ComponentConfigLoadManager.loadHiddenConditionConfig(eleConBeanTmp,reportbean);
                if(cbTmp==null) continue;
                if(!Tools.isDefineKey("ref",cbTmp.getName())
                        &&(cbTmp.getConditionExpression()==null||cbTmp.getConditionExpression().getValue()==null||cbTmp.getConditionExpression()
                                .getValue().trim().equals("")))
                {//如果没有引用在<sql/>中配置的条件，则必须在<condition/>中指定条件表达式
                    throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"的name为"+cbTmp.getName()
                            +"的<condition/>失败，没有引用<sql/>中的条件时必须为<value/>配置条件表达式");
                }
                lstConditionBeans.add(cbTmp);
            }
        }
        return lstConditionBeans;
    }

    public int afterGroupLoading(UltraListReportGroupBean groupbean,List<XmlElementBean> lstEleGroupBeans)
    {
        return 0;
    }

    public int afterReportLoading(ReportBean reportbean,List<XmlElementBean> lstEleReportBeans)
    {
        CrossListReportBean cslrbean=(CrossListReportBean)reportbean.getExtendConfigDataForReportType(KEY);
        if(cslrbean==null)
        {
            cslrbean=new CrossListReportBean(reportbean);
            reportbean.setExtendConfigDataForReportType(KEY,cslrbean);
        }
        reportbean.setCelldrag(0);
        return super.afterReportLoading(reportbean,lstEleReportBeans);
    }

    public int doPostLoad(ReportBean reportbean)
    {
        DisplayBean disbean=reportbean.getDbean();
        disbean.setColselect(false);
        reportbean.setCelldrag(0);
        List lstChildren=getLstConfigChildren(disbean);
        for(Object childObjTmp:lstChildren)
        {
            processCrossColAndGroupBeansStart(childObjTmp,null);
        }
        CrossListReportBean cslrbean=(CrossListReportBean)reportbean.getExtendConfigDataForReportType(KEY);
        if(!cslrbean.isHasDynamicColGroupBean()) return super.doPostLoad(reportbean);
        for(Object childObjTmp:lstChildren)
        {
            processCrossColAndGroupBeansEnd(childObjTmp,null);
        }
        cslrbean.doPostLoad();
        super.doPostLoad(reportbean);
        removeDyncolsFromFilterDataSql(reportbean);
        
        return 1;
    }

    private List getLstConfigChildren(DisplayBean disbean)
    {
        UltraListReportDisplayBean ulrdbean=(UltraListReportDisplayBean)disbean.getExtendConfigDataForReportType(UltraListReportType.KEY);
        List lstChildren=null;
        if(ulrdbean==null||!ulrdbean.isHasGroupConfig(null))
        {//如果不是列分组显示的报表
            lstChildren=disbean.getLstCols();
        }else
        {
            lstChildren=ulrdbean.getLstChildren();
        }
        return lstChildren;
    }

    private void processCrossColAndGroupBeansStart(Object colAndGroupBean,CrossListReportGroupBean parentGroupBean)
    {
        if(!(colAndGroupBean instanceof ColBean)&&!(colAndGroupBean instanceof UltraListReportGroupBean)) return;
        AbsCrossListReportColAndGroupBean clrcgbeanTmp=ListReportAssistant.getInstance().getCrossColAndGroupBean(colAndGroupBean);
        if(clrcgbeanTmp==null) return;
        clrcgbeanTmp.setParentCrossGroupBean(parentGroupBean);
        clrcgbeanTmp.processColGroupRelationStart();
        if(colAndGroupBean instanceof UltraListReportGroupBean)
        {
            for(Object childObjTmp:((UltraListReportGroupBean)colAndGroupBean).getLstChildren())
            {
                processCrossColAndGroupBeansStart(childObjTmp,(CrossListReportGroupBean)clrcgbeanTmp);
            }
        }
    }

    private void processCrossColAndGroupBeansEnd(Object colAndGroupBean,CrossListReportGroupBean parentGroupBean)
    {
        if(!(colAndGroupBean instanceof ColBean)&&!(colAndGroupBean instanceof UltraListReportGroupBean)) return;
        AbsCrossListReportColAndGroupBean clrcgbeanTmp=ListReportAssistant.getInstance().getCrossColAndGroupBean(colAndGroupBean);
        if(clrcgbeanTmp==null) return;
        clrcgbeanTmp.processColGroupRelationEnd();
        if(colAndGroupBean instanceof UltraListReportGroupBean)
        {
            for(Object childObjTmp:((UltraListReportGroupBean)colAndGroupBean).getLstChildren())
            {
                processCrossColAndGroupBeansEnd(childObjTmp,(CrossListReportGroupBean)clrcgbeanTmp);
            }
        }
    }

    private void removeDyncolsFromFilterDataSql(ReportBean reportbean)
    {
        String sqlFilterTmp;
        for(ReportDataSetBean dsbeanTmp:reportbean.getSbean().getLstDatasetBeans())
        {
            for(ReportDataSetValueBean dsvbeanTmp:dsbeanTmp.getLstValueBeans())
            {
                sqlFilterTmp=dsvbeanTmp.getFilterdata_sql();
                if(sqlFilterTmp!=null&&!sqlFilterTmp.trim().equals(""))
                {
                    sqlFilterTmp=replaceDynColPlaceHolder(sqlFilterTmp,"","[#dynamic-columns#]");
                    sqlFilterTmp=replaceDynColPlaceHolder(sqlFilterTmp,"","(#dynamic-columns#)");
                }
                dsvbeanTmp.setFilterdata_sql(sqlFilterTmp);
            }
        }
    }

    public String replaceDynColPlaceHolder(String sql,String realSelectCols,String dyncols_placeholder)
    {
        if(realSelectCols==null||realSelectCols.trim().equals(""))
        {
            int idx=sql.indexOf(dyncols_placeholder);
            while(idx>0)
            {
                String sql1=sql.substring(0,idx).trim();
                String sql2=sql.substring(idx+dyncols_placeholder.length());
                while(sql1.endsWith(","))
                    sql1=sql1.substring(0,sql1.length()-1).trim();
                sql=sql1+" "+sql2;
                idx=sql.indexOf(dyncols_placeholder);
            }
        }else
        {
            sql=Tools.replaceAll(sql,dyncols_placeholder,realSelectCols);
        }
        return sql;
    }

    protected void processFixedColsAndRows(ReportBean reportbean)
    {
        if(rrequest==null||rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE) return;
        List<ColBean> lstRuntimeColBeans=this.cacheDataBean.getLstDynOrderColBeans();
        if(lstRuntimeColBeans==null||lstRuntimeColBeans.size()==0) return;
        int fixedcols=this.alrbean.getFixedcols(null);
        if(fixedcols>0)
        {
            int cnt=0;
            for(ColBean cbTmp:lstRuntimeColBeans)
            {
                if(cbTmp.getDisplaytype()==Consts.COL_DISPLAYTYPE_HIDDEN) continue;
                cnt++;
            }
            if(cnt<=fixedcols)
            {
                fixedcols=0;
                alrbean.setFixedcols(rrequest,0);
            }
        }
        if(fixedcols>0)
        {
            AbsListReportColBean alrcbeanTmp;
            int cnt=0;
            for(ColBean cbTmp:lstRuntimeColBeans)
            {
                if(cbTmp.getDisplaytype()==Consts.COL_DISPLAYTYPE_HIDDEN) continue;
                if(cbTmp.isRowSelectCol())
                {
                    throw new WabacusConfigLoadingException("对于冻结列标题的交叉显示报表，如果加载报表"+reportbean.getPath()
                            +"失败,在<report/>的fixedcols中配置的冻结列数包括了行选中列，这样不能正常选中行");
                }
                alrcbeanTmp=(AbsListReportColBean)cbTmp.getExtendConfigDataForReportType(AbsListReportType.KEY);
                if(alrcbeanTmp==null)
                {
                    alrcbeanTmp=new AbsListReportColBean(cbTmp);
                    cbTmp.setExtendConfigDataForReportType(AbsListReportType.KEY,alrcbeanTmp);
                }
                alrcbeanTmp.setFixedCol(rrequest,true);
                if(++cnt==fixedcols) break;
            }
        }
        if(fixedcols>0||alrbean.getFixedrows()>0)
        {
            if(alrdbean!=null&&alrdbean.getRowgrouptype()==2&&alrdbean.getRowGroupColsNum()>0)
            {
                throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败,树形分组报表不能冻结行列标题");
            }
        }
    }

    protected ColBean[] insertRowSelectNewCols(AbsListReportBean alrbean,List<ColBean> lstCols)
    {
        
        CrossListReportBean cslrbean=(CrossListReportBean)alrbean.getOwner().getReportBean().getExtendConfigDataForReportType(KEY);
        if(!cslrbean.isHasDynamicColGroupBean()) return super.insertRowSelectNewCols(alrbean,lstCols);
        cslrbean.setShouldCreateRowSelectCol(true);
        return null;//对于交叉报表，不在这里新增行选择列，而是在运行时新增
    }

    protected Map<String,ColAndGroupTitlePositionBean> calPosition(ReportBean reportbean,List lstChildren,List<String> lstDisplayColIds)
    {
        if(rrequest==null) return null;
        return super.calPosition(reportbean,lstChildren,lstDisplayColIds);
    }

    protected void calPositionForStandardExcel(List lstChildren,List<String> lstDynColids,
            Map<String,ColAndGroupTitlePositionBean> mColAndGroupTitlePostions)
    {
        if(rrequest==null) return;
        super.calPositionForStandardExcel(lstChildren,lstDynColids,mColAndGroupTitlePostions);
    }

    private class RuntimeDynamicDatasetBean
    {
        private CrossListReportDynDatasetBean datasetBean;

        private Map<String,String> mAllSelectCols;

        private ResultSet verticalStatiResultSet;

        public RuntimeDynamicDatasetBean(CrossListReportDynDatasetBean datasetBean)
        {
            this.datasetBean=datasetBean;
        }

        public void addSelectColsOfCrossColGroup(String colgroupid,String selectcols)
        {
            if(mAllSelectCols==null) mAllSelectCols=new HashMap<String,String>();
            mAllSelectCols.put(colgroupid,selectcols);
        }

        public ResultSet getVerticalStatiResultSet()
        {
            return verticalStatiResultSet;
        }

        public void buildRealSelectSqls()
        {
            this.datasetBean.buildRealSelectSqls(CrossListReportType.this,this.mAllSelectCols);
        }

        public boolean loadVerticalStatiData()
        {
            verticalStatiResultSet=this.datasetBean.loadVerticalStatiData(CrossListReportType.this,this.mAllSelectCols);
            return verticalStatiResultSet!=null;
        }

        public void closeResultSet()
        {
            try
            {
                if(verticalStatiResultSet!=null) verticalStatiResultSet.close();
            }catch(SQLException e)
            {
                log.warn("关闭报表"+rbean.getPath()+"的针对每列数据进行垂直统计的记录集失败",e);
            }
        }
    }

    private class VerticalCrossStatisticColData
    {
        private ColBean cbean;

        private String colValue;

        private int colspan;

        public VerticalCrossStatisticColData(ColBean cbean,String colValue,int colspan)
        {
            this.cbean=cbean;
            if(colValue==null) colValue="";
            this.colValue=colValue;
            this.colspan=colspan;
        }

        public ColBean getCbean()
        {
            return cbean;
        }

        public String getColValue()
        {
            return colValue;
        }

        public int getColspan()
        {
            return colspan;
        }

        public void appendColValue(String colValue)
        {
            if(this.colValue==null) this.colValue="";
            if(colValue==null||colValue.trim().equals("")) return;
            this.colValue+=" "+colValue;
        }
    }
}
