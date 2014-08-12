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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.Config;
import com.wabacus.config.component.IComponentConfigBean;
import com.wabacus.config.component.application.report.AbsReportDataPojo;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.DisplayBean;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.EditableReportAssistant;
import com.wabacus.system.assistant.ListReportAssistant;
import com.wabacus.system.assistant.ReportAssistant;
import com.wabacus.system.component.application.report.abstractreport.AbsListReportType;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportColBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportRowGroupSubDisplayRowBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportSubDisplayBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportSubDisplayColBean;
import com.wabacus.system.component.application.report.configbean.ColAndGroupDisplayBean;
import com.wabacus.system.component.application.report.configbean.ColDisplayData;
import com.wabacus.system.component.container.AbsContainerType;
import com.wabacus.system.inputbox.AbsInputBox;
import com.wabacus.system.inputbox.TextBox;
import com.wabacus.system.intercept.ReportDataBean;
import com.wabacus.system.intercept.RowDataBean;
import com.wabacus.util.Consts;
import com.wabacus.util.Consts_Private;
import com.wabacus.util.Tools;

public class ListReportType extends AbsListReportType
{
    private final static Log log=LogFactory.getLog(ListReportType.class);

    protected FixedColAndGroupDataBean fixedDataBean;

    private Map<Integer,List<RowGroupDataBean>> mAllParentRowGroupDataBeansForPerDataRow;

    private Map<ColBean,List<RowGroupDataBean>> mRowGroupCols;

    protected long global_rowindex=0L;

    protected long global_sequence=0L;

    public ListReportType(AbsContainerType parentContainerType,IComponentConfigBean comCfgBean,ReportRequest rrequest)
    {
        super(parentContainerType,comCfgBean,rrequest);
    }

    public void setGlobal_rowindex(long global_rowindex)
    {
        this.global_rowindex=global_rowindex;
    }

    public long getGlobal_rowindex()
    {
        return global_rowindex;
    }

    public void initReportBeforeDoStart()
    {
        super.initReportBeforeDoStart();
        ListReportAssistant.getInstance().storeRoworder(rrequest,rbean);
    }

    protected void doLoadReportDataPostAction()
    {
        if(this.lstReportData!=null&&this.lstReportData.size()>0&&alrdbean!=null&&alrdbean.getRowGroupColsNum()>0
                &&(alrdbean.getRowgrouptype()==1||alrdbean.getRowgrouptype()==2)
                &&(rrequest.getShowtype()!=Consts.DISPLAY_ON_PDF&&rrequest.getShowtype()!=Consts.DISPLAY_ON_PLAINEXCEL))
        {
            parseRowGroupColData();
        }
        super.doLoadReportDataPostAction();
    }

    private void parseRowGroupColData()
    {
        DisplayBean disbean=rbean.getDbean();
        List<String> lstRowGroupColsColumn=alrdbean.getLstRowgroupColsColumn();
        this.mAllParentRowGroupDataBeansForPerDataRow=new HashMap<Integer,List<RowGroupDataBean>>();
        this.mRowGroupCols=new HashMap<ColBean,List<RowGroupDataBean>>();
        AbsReportDataPojo dataObj;
        List<RowGroupDataBean> lstrgdb;
        RowGroupDataBean rgdbean;
        List<Integer> lstParentDisplayRowIdx=null;
        List<Integer> lstCurrentDisplayRowIdx;

        Map<String,AbsListReportRowGroupSubDisplayRowBean> mStatiRowGroupBeans=null;
        if(this.alrbean.getSubdisplaybean()!=null)
        {
            mStatiRowGroupBeans=this.alrbean.getSubdisplaybean().getMRowGroupSubDisplayRowBeans();
        }
        int[] displayrowinfo=getDisplayRowInfo();
        int layer=0;
        ColBean cbeanTemp;
        for(String colcolumn:lstRowGroupColsColumn)
        {
            if(colcolumn==null) continue;
            cbeanTemp=disbean.getColBeanByColColumn(colcolumn);
            lstrgdb=new ArrayList<RowGroupDataBean>();
            mRowGroupCols.put(cbeanTemp,lstrgdb);
            lstCurrentDisplayRowIdx=new ArrayList<Integer>();
            for(int i=displayrowinfo[0];i<displayrowinfo[1];i++)
            {
                dataObj=lstReportData.get(i);
                String value=dataObj.getColStringValue(cbeanTemp);
                value=value==null?"":value.trim();
                if(lstrgdb.size()==0||(lstParentDisplayRowIdx!=null&&lstParentDisplayRowIdx.contains(i))
                        ||!(value.equals(lstrgdb.get(lstrgdb.size()-1).getDisplayvalue())))
                {
                    lstCurrentDisplayRowIdx.add(i);
                    lstrgdb.add(createRowGroupDataObj(dataObj,this.alrbean.getSubdisplaybean(),layer,cbeanTemp,i,value));
                }else
                {
                    rgdbean=lstrgdb.get(lstrgdb.size()-1);
                    rgdbean.setRowspan(rgdbean.getRowspan()+1);
                    rgdbean.addChildDataRowIdx("tr_"+i);
                    List<RowGroupDataBean> lstParentDataBeans=this.mAllParentRowGroupDataBeansForPerDataRow.get(i);//取出间接包括此行的所有父分组节点
                    if(lstParentDataBeans==null)
                    {
                        lstParentDataBeans=new ArrayList<RowGroupDataBean>();
                        this.mAllParentRowGroupDataBeansForPerDataRow.put(i,lstParentDataBeans);
                    }
                    lstParentDataBeans.add(rgdbean);
                    if(alrdbean.getRowgrouptype()==1&&mStatiRowGroupBeans!=null)
                    {
                        if(mStatiRowGroupBeans.containsKey(cbeanTemp.getColumn()))
                        {
                            ((CommonRowGroupDataBean)lstrgdb.get(lstrgdb.size()-1)).setDisplaystatidata_rowidx(i);
                        }
                    }
                }
            }
            lstParentDisplayRowIdx=lstCurrentDisplayRowIdx;
            layer++;
        }
    }

    private RowGroupDataBean createRowGroupDataObj(Object dataObj,AbsListReportSubDisplayBean subDisplayBean,int layer,ColBean cbean,int rowidx,
            String value)
    {
        RowGroupDataBean rgdbean=null;
        if(alrdbean.getRowgrouptype()==1)
        {
            rgdbean=new CommonRowGroupDataBean(cbean,value,rowidx,layer);
        }else if(alrdbean.getRowgrouptype()==2)
        {
            rgdbean=new TreeRowGroupDataBean(cbean,value,rowidx,layer);
        }
        rgdbean.setValue(subDisplayBean,dataObj,this.mAllParentRowGroupDataBeansForPerDataRow);
        return rgdbean;
    }

    public String showReportData()
    {
        if(!rrequest.checkPermission(rbean.getId(),Consts.DATA_PART,null,Consts.PERMISSION_TYPE_DISPLAY)) return "";
        if(this.cacheDataBean.getTotalColCount()==0) return "";
        StringBuffer resultBuf=new StringBuffer();
        ReportDataBean reportDataObjFromInterceptor=null;
        if(rbean.getInterceptor()!=null)
        {
            reportDataObjFromInterceptor=new ReportDataBean(this,this.getLstAllRealColBeans());
            rbean.getInterceptor().beforeDisplayReportData(rrequest,rbean,reportDataObjFromInterceptor);
        }
        if(reportDataObjFromInterceptor!=null&&reportDataObjFromInterceptor.getBeforeDisplayString()!=null)
        {
            resultBuf.append(reportDataObjFromInterceptor.getBeforeDisplayString());
        }
        if(reportDataObjFromInterceptor==null||reportDataObjFromInterceptor.isShouldDisplayReportData())
        {
            if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
            {
                if(this.alrbean.getScrollType()==AbsListReportBean.SCROLLTYPE_VERTICAL)
                {
                    return showReportDataWithVerticalScroll();
                }else if(this.alrbean.getScrollType()==AbsListReportBean.SCROLLTYPE_FIXED)
                {
                    fixedDataBean=new FixedColAndGroupDataBean();
                }
            }
            resultBuf.append(showScrollStartTag());
            resultBuf.append(showReportTableStart());
            resultBuf.append(">");
            DisplayBean dbean=rbean.getDbean();
            if(dbean!=null)
            {
                if(dbean.getDataheader()!=null)
                {
                    resultBuf.append(rrequest.getI18NStringValue(dbean.getDataheader()));
                }else
                {
                    resultBuf.append(showDataHeaderPart());
                }
            }
            resultBuf.append(showDataPart());
            if(this.rbean.getInterceptor()!=null)
            {
                RowDataBean rowdataObjTmp=new RowDataBean(this,this.rbean.getDbean().getValuestyleproperty(rrequest,false),this
                        .getLstDisplayColBeans(),null,Integer.MAX_VALUE,this.cacheDataBean.getTotalColCount());
                this.rbean.getInterceptor().beforeDisplayReportDataPerRow(this.rrequest,this.rbean,rowdataObjTmp);
                if(rowdataObjTmp.getInsertDisplayRowHtml()!=null)
                {
                    resultBuf.append(rowdataObjTmp.getInsertDisplayRowHtml());
                }
            }
            resultBuf.append("</table>");
            resultBuf.append(showScrollEndTag());
            if(this.fixedDataBean!=null)
            {
                rrequest.getWResponse().addOnloadMethod("fixedRowColTable",
                        "{pageid:\""+rbean.getPageBean().getId()+"\",reportid:\""+rbean.getId()+"\"}",false);
            }
        }
        if(reportDataObjFromInterceptor!=null&&reportDataObjFromInterceptor.getAfterDisplayString()!=null)
        {
            resultBuf.append(reportDataObjFromInterceptor.getAfterDisplayString());
        }
        return resultBuf.toString();
    }

    protected boolean isFixedLayoutTable()
    {
        if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE) return false;
        if(rbean.getCellresize()>0) return true;
        if(alrbean.getScrollType()==AbsListReportBean.SCROLLTYPE_VERTICAL) return true;
        return false;
    }

    protected String showReportTableStart()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(showReportTablePropsForCommon());
        resultBuf.append(showReportTablePropsForDataPart());
        resultBuf.append(showReportTablePropsForTitlePart(false));
        return resultBuf.toString();
    }

    protected String showReportTablePropsForCommon()
    {
        if(!rrequest.isDisplayOnPage()) return super.showReportTablePropsForNonOnPage();
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("<table class=\""+getDataTableClassName()+"\"");
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
        {
            resultBuf.append(" width=\""+getReportDataWidthOnPage()+"\"");
            resultBuf.append(" pageid=\"").append(rbean.getPageBean().getId()).append("\"");
            resultBuf.append(" reportid=\"").append(rbean.getId()).append("\"");
            resultBuf.append(" refreshComponentGuid=\"").append(rbean.getRefreshGuid()).append("\"");
            if(rbean.isSlaveReportDependsonListReport())
            {
                resultBuf.append(" isSlave=\"true\"");//是从报表
            }
        }else if(rrequest.getShowtype()==Consts.DISPLAY_ON_PRINT)
        {
            String printwidth=rbean.getPrintwidth();
            if(printwidth==null||printwidth.trim().equals("")) printwidth="100%";
            resultBuf.append(" width=\"").append(printwidth).append("\"");
        }
        return resultBuf.toString();
    }

    protected String showReportTablePropsForDataPart()
    {
        if(!rrequest.isDisplayOnPage()) return "";
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(" id=\"").append(rbean.getGuid()).append("_data\"");
        resultBuf.append(" style=\"");
        if(Consts_Private.REPORT_BORDER_NONE0.equals(rbean.getBorder()))
        {
            resultBuf.append("border:none;");
        }else if(Consts_Private.REPORT_BORDER_HORIZONTAL0.equals(rbean.getBorder()))
        {
            resultBuf.append("border-left:none;border-right:none;");
        }
        if(isFixedLayoutTable()) resultBuf.append("table-layout:fixed;");
        resultBuf.append("\"");
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
        {
            String rowselecttype=this.alrbean.getRowSelectType();
            if(rowselecttype!=null
                    &&(rowselecttype.trim().equals(Consts.ROWSELECT_SINGLE)||rowselecttype.trim().equals(Consts.ROWSELECT_MULTIPLE)
                            ||rowselecttype.trim().equals(Consts.ROWSELECT_MULTIPLE_CHECKBOX)||rowselecttype.trim().equals(
                            Consts.ROWSELECT_SINGLE_RADIOBOX)))
            {
                resultBuf.append(" onClick=\"try{doSelectDataRowEvent(event);}catch(e){logErrorsAsJsFileLoad(e);}\"");
            }
            if(rbean.shouldShowContextMenu())
            {
                resultBuf.append(" oncontextmenu=\"try{showcontextmenu('contextmenu_"+rbean.getGuid()
                        +"',event);}catch(e){logErrorsAsJsFileLoad(e);}\"");
            }
        }
        return resultBuf.toString();
    }

    protected String showReportTablePropsForTitlePart(boolean isAbsoluteTable)
    {
        StringBuffer resultBuf=new StringBuffer();
        if(!rrequest.isDisplayOnPage()) return "";
        if(isAbsoluteTable)
        {
            resultBuf.append(" id=\"").append(rbean.getGuid()).append("_dataheader\"");
            if(isFixedLayoutTable()) resultBuf.append(" style=\"table-layout:fixed;\"");
        }
        return resultBuf.toString();
    }

    protected String getDataTableClassName()
    {
        if(Consts_Private.REPORT_BORDER_HORIZONTAL2.equals(rbean.getBorder()))
        {
            return "cls-articlelist-data-table";
        }
        return "cls-data-table";
    }

    public String showReportData(boolean showtype)
    {
        if(!rrequest.checkPermission(rbean.getId(),Consts.DATA_PART,null,Consts.PERMISSION_TYPE_DISPLAY)) return "";
        if(this.cacheDataBean.getTotalColCount()==0) return "";
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(showReportTablePropsForCommon());
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
        {
            if(showtype)
            {
                resultBuf.append(showReportTablePropsForDataPart());
            }else
            {
                resultBuf.append(showReportTablePropsForTitlePart(true));
            }
        }
        resultBuf.append(">");
        if(showtype)
        {
            resultBuf.append(showDataPart());
        }else
        {
            DisplayBean dbean=rbean.getDbean();
            if(dbean!=null)
            {
                if(dbean.getDataheader()!=null)
                {
                    resultBuf.append(rrequest.getI18NStringValue(dbean.getDataheader()));
                }else
                {
                    resultBuf.append(showDataHeaderPart());
                }
            }
        }
        if(showtype&&this.rbean.getInterceptor()!=null)
        {
            RowDataBean rowdataObjTmp=new RowDataBean(this,this.rbean.getDbean().getValuestyleproperty(rrequest,false),this.getLstDisplayColBeans(),
                    null,Integer.MAX_VALUE,this.cacheDataBean.getTotalColCount());
            this.rbean.getInterceptor().beforeDisplayReportDataPerRow(this.rrequest,this.rbean,rowdataObjTmp);
            if(rowdataObjTmp.getInsertDisplayRowHtml()!=null)
            {
                resultBuf.append(rowdataObjTmp.getInsertDisplayRowHtml());
            }
        }
        resultBuf.append("</table>");
        return resultBuf.toString();
    }

    protected String showDataHeaderPart()
    {
        DisplayBean dbean=rbean.getDbean();
        List<ColBean> lstColBeans=this.getLstDisplayColBeans();
        StringBuffer resultBuf=new StringBuffer();
        String thstyleproperty=this.getRowLabelstyleproperty();
        if(this.rbean.getInterceptor()!=null)
        {
            RowDataBean rowdataObj=new RowDataBean(this,thstyleproperty,this.getLstDisplayColBeans(),null,-1,this.cacheDataBean.getTotalColCount());
            this.rbean.getInterceptor().beforeDisplayReportDataPerRow(this.rrequest,this.rbean,rowdataObj);
            if(rowdataObj.getInsertDisplayRowHtml()!=null) resultBuf.append(rowdataObj.getInsertDisplayRowHtml());
            thstyleproperty=rowdataObj.getRowstyleproperty();
            if(!rowdataObj.isShouldDisplayThisRow()) return resultBuf.toString();
        }
        if(thstyleproperty==null) thstyleproperty="";
        if(thstyleproperty.toLowerCase().indexOf("class=")<0) thstyleproperty+=" class='"+getDataHeaderTrClassName()+"'";
        resultBuf.append("<tr ").append(thstyleproperty).append(">");
        AbsListReportColBean alrcbean;
        ColDisplayData colDisplayData;//从拦截器中得到的用户为当前列生成的数据；
        List<ColAndGroupDisplayBean> lstColAndGroupDisplayBeans=null;
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE&&dbean.isColselect())
        {
            lstColAndGroupDisplayBeans=new ArrayList<ColAndGroupDisplayBean>();
            rrequest.addColAndGroupDisplayBean(rbean.getId(),lstColAndGroupDisplayBeans);
        }
        ColAndGroupDisplayBean cgDisplayBeanTmp;
        for(ColBean cbean:lstColBeans)
        {
            if(alrdbean!=null&&alrdbean.getRowgrouptype()==2&&alrdbean.getRowGroupColsNum()>0)
            {
                if(alrdbean.getLstRowgroupColsColumn().contains(cbean.getColumn())
                        &&!cbean.getColumn().equals(alrdbean.getLstRowgroupColsColumn().get(0)))
                {//如果当前cbean是树形行分组的列，但不是第一列，则不显示为一独立列，所以这里就不为它显示一个<td/>
                    continue;
                }
            }
            cgDisplayBeanTmp=null;
            if(Consts.COL_DISPLAYTYPE_HIDDEN.equals(cbean.getDisplaytype())) continue;
            int displaymodeTmp=cacheDataBean.getColDisplayModeAfterAuthorize(cbean);
            if(displaymodeTmp<0||(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE&&displaymodeTmp==0)) continue;
            alrcbean=(AbsListReportColBean)cbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
            if(lstColAndGroupDisplayBeans!=null)
            {
                cgDisplayBeanTmp=new ColAndGroupDisplayBean();
                cgDisplayBeanTmp.setId(cbean.getColid());
                cgDisplayBeanTmp.setControlCol(cbean.isControlCol());
                cgDisplayBeanTmp.setNonFixedCol(alrcbean==null||!alrcbean.isFixedCol(rrequest));
                if(displaymodeTmp==0)
                {
                    cgDisplayBeanTmp.setChecked(false);
                }else
                {
                    cgDisplayBeanTmp.setChecked(true);
                    cgDisplayBeanTmp.setAlways(Consts.COL_DISPLAYTYPE_ALWAYS.equals(cbean.getDisplaytype()));
                }
                lstColAndGroupDisplayBeans.add(cgDisplayBeanTmp);
            }
            colDisplayData=ColDisplayData.getColDataFromInterceptor(this,cbean,null,-1,getColLabelStyleproperty(cbean,null),cbean.getLabel(rrequest));
            if(cgDisplayBeanTmp!=null)
            {
                cgDisplayBeanTmp.setTitle(colDisplayData.getValue());
                if(displaymodeTmp==0) continue;
            }
            resultBuf.append("<td class='"+getDataHeaderThClassName()+"' ").append(colDisplayData.getStyleproperty());
            if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
            {
                if(rbean.getCelldrag()>0&&(alrcbean==null||alrcbean.isDragable(alrdbean)))
                {
                    resultBuf.append(" onmousedown=\"try{handleCellDragMouseDown(this,'"+rbean.getPageBean().getId()+"','"+rbean.getId()
                            +"');}catch(e){logErrorsAsJsFileLoad(e);}\"");
                    resultBuf.append(" dragcolid=\"").append(cbean.getColid()).append("\"");
                }
                if(this.fixedDataBean!=null) resultBuf.append(this.fixedDataBean.showFirstNonFixedColFlag(cbean));
                resultBuf.append(">");
                if(rbean.getCellresize()==1&&!cbean.getColid().equals(cacheDataBean.getLastColId()))
                {
                    resultBuf.append(ListReportAssistant.getInstance().appendCellResizeFunction(true));
                }else if(rbean.getCellresize()>0)
                {
                    resultBuf.append(ListReportAssistant.getInstance().appendCellResizeFunction(false));
                }
                if(dbean.isColselect()&&cbean.getColid().equals(cacheDataBean.getLastColId()))
                {
                    resultBuf.append(ReportAssistant.getInstance().getColSelectedLabelAndEvent(rrequest,rbean,true));
                }
                if(alrcbean!=null)
                {
                    if(this.getLstReportData()!=null&&this.getLstReportData().size()>0)
                    {
                        if(alrcbean.isRequireClickOrderby())
                        {
                            colDisplayData.setValue(ListReportAssistant.getInstance()
                                    .getColLabelWithOrderBy(cbean,rrequest,colDisplayData.getValue()));
                        }
                    }
                    if(alrcbean.getFilterBean()!=null) resultBuf.append(ListReportAssistant.getInstance().createColumnFilter(rrequest,alrcbean));
                }
            }else
            {
                String dataheaderbgcolor=Config.getInstance().getSkinConfigValue(rrequest.getPageskin(),"table.dataheader.bgcolor");
                if(dataheaderbgcolor==null) dataheaderbgcolor="";
                resultBuf.append(" bgcolor='"+dataheaderbgcolor+"'>");
            }
            resultBuf.append(colDisplayData.getValue()+"</td>");
        }
        resultBuf.append("</tr>");
        if(this.fixedDataBean!=null&&this.fixedDataBean.getFixedrowscount()==Integer.MAX_VALUE)
        {
            this.fixedDataBean.setFixedrowscount(1);
        }
        return resultBuf.toString();
    }

    protected String getDataHeaderTrClassName()
    {
        if(Consts_Private.REPORT_BORDER_HORIZONTAL2.equals(rbean.getBorder()))
        {
            return "cls-data-tr-head-articlelist";
        }
        return "cls-data-tr-head-list";
    }

    protected String getDataHeaderThClassName()
    {
        if(Consts_Private.REPORT_BORDER_HORIZONTAL2.equals(rbean.getBorder()))
        {
            return "cls-data-th-articlelist";
        }
        return "cls-data-th-list";
    }

    protected String getDataTdClassName()
    {
        if(Consts_Private.REPORT_BORDER_HORIZONTAL2.equals(rbean.getBorder()))
        {
            return "cls-data-td-articlelist";
        }
        return "cls-data-td-list";
    }
    
    protected String showDataPart()
    {
        StringBuffer resultBuf=new StringBuffer();
        List<ColBean> lstColBeans=getLstDisplayColBeans();
        if(getDisplayRowInfo()[1]<=0)
        {//没有数据
            String trstylepropertyTmp=this.rbean.getDbean().getValuestyleproperty(rrequest,false);
            if(this.rbean.getInterceptor()!=null)
            {
                RowDataBean rowdataObj=new RowDataBean(this,trstylepropertyTmp,lstColBeans,null,0,this.cacheDataBean.getTotalColCount());
                this.rbean.getInterceptor().beforeDisplayReportDataPerRow(this.rrequest,this.rbean,rowdataObj);
                if(rowdataObj.getInsertDisplayRowHtml()!=null) resultBuf.append(rowdataObj.getInsertDisplayRowHtml());
                trstylepropertyTmp=rowdataObj.getRowstyleproperty();
                if(!rowdataObj.isShouldDisplayThisRow()) return resultBuf.toString();
            }
            resultBuf.append("<tr "+getTrValueStyleproperty(trstylepropertyTmp,rbean.getGuid()+"_nodata_tr",0,null,false));
            resultBuf.append("<td class='"+getDataTdClassName()+"' bgcolor='#ffffff' colspan='").append(this.cacheDataBean.getTotalColCount())
                    .append("'>");
            if(this.isLazyDataLoad()&&rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
            {
                resultBuf.append(rrequest.getStringAttribute(rbean.getId()+"_lazyloadmessage",""));
            }else
            {
                resultBuf.append(rrequest.getI18NStringValue((Config.getInstance().getResources().getString(rrequest,rbean.getPageBean(),
                        Consts.NODATA_PROMPT_KEY,true))));
            }
            resultBuf.append("</td></tr>");
            return resultBuf.toString();
        }
        resultBuf.append(showSubRowDataForWholeReport(AbsListReportSubDisplayBean.SUBROW_POSITION_TOP));
        this.global_rowindex=0;
        this.global_sequence=0;
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
        {
            this.global_rowindex=this.global_sequence=(this.cacheDataBean.getFinalPageno()-1)*this.cacheDataBean.getPagesize();
        }
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE||rrequest.getShowtype()==Consts.DISPLAY_ON_PRINT
                ||!this.cacheDataBean.shouldBatchDataExport())
        {
            showRealDataPart(resultBuf,lstColBeans);
        }else
        {
            for(int i=0;i<this.cacheDataBean.getPagecount();i++)
            {
                if(i!=0)
                {
                    this.cacheDataBean.setPageno(i+1);
                    this.cacheDataBean.setRefreshNavigateInfoType(1);
                    this.setHasLoadedDataFlag(false);
                    loadReportData(true);
                }
                showRealDataPart(resultBuf,lstColBeans);
                
                
            }
        }
        resultBuf.append(showSubRowDataForWholeReport(AbsListReportSubDisplayBean.SUBROW_POSITION_BOTTOM));
        return resultBuf.toString();
    }

    private void showRealDataPart(StringBuffer resultBuf,List<ColBean> lstColBeans)
    {
        if(alrdbean!=null&&alrdbean.getRowGroupColsNum()>0)
        {
            if(alrdbean.getRowgrouptype()==1)
            {
                resultBuf.append(showCommonRowGroupDataPart(lstColBeans));
            }else if(alrdbean.getRowgrouptype()==2)
            {
                resultBuf.append(showTreeRowGroupDataPart(lstColBeans));
            }else
            {
                throw new WabacusRuntimeException("显示报表"+rbean.getPath()+"失败，无效的分组类型");
            }
        }else
        {
            resultBuf.append(showCommonDataPart(lstColBeans));
        }
    }

    private String showCommonDataPart(List<ColBean> lstColBeans)
    {
        StringBuffer resultBuf=new StringBuffer();
        StringBuffer tdPropsBuf;
        ColDisplayData colDisplayData;
        String col_displayvalue;
        AbsReportDataPojo rowDataObjTmp;
        int[] displayrowinfo=this.getDisplayRowInfo();
        RowDataBean rowInterceptorObjTmp=null;
        String trstylepropertyTmp=null;
        boolean isReadonlyByRowInterceptor;
        for(int i=displayrowinfo[0];i<displayrowinfo[1];i++)
        {
            if(i>=this.lstReportData.size())
            {
                resultBuf.append(showDataRowInAddMode(lstColBeans,i));
                continue;
            }
            rowDataObjTmp=this.lstReportData.get(i);
            isReadonlyByRowInterceptor=false;
            trstylepropertyTmp=rowDataObjTmp.getRowValuestyleproperty();
            rowInterceptorObjTmp=null;
            if(this.rbean.getInterceptor()!=null)
            {
                rowInterceptorObjTmp=new RowDataBean(this,trstylepropertyTmp,lstColBeans,rowDataObjTmp,i,this.cacheDataBean.getTotalColCount());
                this.rbean.getInterceptor().beforeDisplayReportDataPerRow(this.rrequest,this.rbean,rowInterceptorObjTmp);
                if(rowInterceptorObjTmp.getInsertDisplayRowHtml()!=null) resultBuf.append(rowInterceptorObjTmp.getInsertDisplayRowHtml());
                if(!rowInterceptorObjTmp.isShouldDisplayThisRow())
                {
                    this.global_rowindex++;//this.global_sequence不需要+1
                    continue;
                }
                trstylepropertyTmp=rowInterceptorObjTmp.getRowstyleproperty();
                isReadonlyByRowInterceptor=rowInterceptorObjTmp.isReadonly();
            }
            resultBuf.append(showDataRowTrStart(rowInterceptorObjTmp,trstylepropertyTmp,i,false)).append(">");
            boolean isReadonlyByColInterceptor;
            for(ColBean cbean:lstColBeans)
            {
                if(Consts.COL_DISPLAYTYPE_HIDDEN.equals(cbean.getDisplaytype())||this.cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<=0)
                {
                    resultBuf.append(showHiddenCol(cbean,rowDataObjTmp,i));
                    continue;
                }
                
                tdPropsBuf=new StringBuffer();
                Object colDataObj=initDisplayCol(cbean,rowDataObjTmp);
                resultBuf.append("<td ").append(getTdPropertiesForCol(cbean,colDataObj,i,false));
                col_displayvalue=getColDisplayValue(cbean,rowDataObjTmp,tdPropsBuf,colDataObj,i,isReadonlyByRowInterceptor);
                colDisplayData=ColDisplayData.getColDataFromInterceptor(this,cbean,rowDataObjTmp,i,getColValuestyleproperty(cbean,rowDataObjTmp),col_displayvalue);
                isReadonlyByColInterceptor=colDisplayData.getColdataByInterceptor()!=null&&colDisplayData.getColdataByInterceptor().isReadonly();
                if(!isReadonlyByRowInterceptor&&isReadonlyByColInterceptor)
                {
                    tdPropsBuf.delete(0,tdPropsBuf.length());
                    col_displayvalue=getColDisplayValue(cbean,rowDataObjTmp,tdPropsBuf,colDataObj,i,true);
                }else
                {
                    col_displayvalue=colDisplayData.getValue();
                }
                resultBuf.append(" class='"+getDataTdClassName()+"' ");
                resultBuf.append(tdPropsBuf.toString()).append(" ");
                resultBuf.append(colDisplayData.getStyleproperty()).append(">");
                resultBuf
                        .append(getColDisplayValueWithWrap(cbean,col_displayvalue,colDataObj,isReadonlyByRowInterceptor||isReadonlyByColInterceptor));
                resultBuf.append("</td>");
            }
            resultBuf.append("</tr>");
            this.global_rowindex++;
            this.global_sequence++;
        }
        return resultBuf.toString();
    }

    private String getColValuestyleproperty(ColBean cbean,AbsReportDataPojo rowDataObj)
    {
        return rowDataObj.getColValuestyleproperty("[DYN_COL_DATA]".equals(cbean.getProperty())?cbean.getColumn():cbean.getProperty());
    }
    
    private String showCommonRowGroupDataPart(List<ColBean> lstColBeans)
    {
        StringBuffer resultBuf=new StringBuffer();
        List<RowGroupDataBean> lstHasDisplayedRowGroupCols=null;
        Map<String,AbsListReportRowGroupSubDisplayRowBean> mStatiRowGroupBeans=null;
        if(this.alrbean.getSubdisplaybean()!=null&&this.alrbean.getSubdisplaybean().getMRowGroupSubDisplayRowBeans()!=null
                &&this.alrbean.getSubdisplaybean().getMRowGroupSubDisplayRowBeans().size()>0)
        {
            lstHasDisplayedRowGroupCols=new ArrayList<RowGroupDataBean>();
            mStatiRowGroupBeans=this.alrbean.getSubdisplaybean().getMRowGroupSubDisplayRowBeans();
        }
        RowGroupDataBean rgdbean;
        ColDisplayData colDisplayData;
        StringBuffer tdPropsBuf;
        AbsReportDataPojo rowDataObjTmp;
        int[] displayrowinfo=this.getDisplayRowInfo();
        if(displayrowinfo[1]<=0) return "";
        RowDataBean rowInterceptorObjTmp=null;
        String trstylepropertyTmp=null;
        boolean isReadonlyByRowInterceptor;
        for(int i=displayrowinfo[0];i<displayrowinfo[1];i++)
        {
            if(i>=this.lstReportData.size())
            {
                resultBuf.append(showDataRowInAddMode(lstColBeans,i));
                continue;
            }
            isReadonlyByRowInterceptor=false;
            rowInterceptorObjTmp=null;
            rowDataObjTmp=lstReportData.get(i);
            trstylepropertyTmp=rowDataObjTmp.getRowValuestyleproperty();
            if(this.rbean.getInterceptor()!=null)
            {
                rowInterceptorObjTmp=new RowDataBean(this,trstylepropertyTmp,lstColBeans,rowDataObjTmp,i,this.cacheDataBean.getTotalColCount());
                this.rbean.getInterceptor().beforeDisplayReportDataPerRow(this.rrequest,this.rbean,rowInterceptorObjTmp);
                if(rowInterceptorObjTmp.getInsertDisplayRowHtml()!=null) resultBuf.append(rowInterceptorObjTmp.getInsertDisplayRowHtml());
                if(!rowInterceptorObjTmp.isShouldDisplayThisRow())
                {
                    this.global_rowindex++;
                    continue;
                }
                trstylepropertyTmp=rowInterceptorObjTmp.getRowstyleproperty();
                isReadonlyByRowInterceptor=rowInterceptorObjTmp.isReadonly();
            }
            resultBuf.append(showDataRowTrStart(rowInterceptorObjTmp,trstylepropertyTmp,i,true)).append(" ");
            resultBuf.append(" parentCommonGroupTdId=\"").append(getDirectParentGroupId(this.mAllParentRowGroupDataBeansForPerDataRow.get(i)))
                    .append("\"");
            resultBuf.append(" grouprow=\"true\"");//在<tr/>中加上grouprow=true，这样如果有选中功能，则不将整个<tr/>改变背景色，那些分组列的背景色不能变，只变不参与行分组的列
            resultBuf.append(">");
            if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
            {
                for(RowGroupDataBean parentObjTmp:this.mAllParentRowGroupDataBeansForPerDataRow.get(i))
                {//为当前记录行对应的所有父分组节点，根据需要显示一个隐藏<td/>存放它的值，以便能用上
                    if(parentObjTmp.getDisplay_rowidx()!=i)
                    {//对于父分组对象，如果与当前数据行不在同一个<tr/>中，则根据需要为它显示一个隐藏的<td/>存放可能要用到的数据
                        resultBuf.append(showHiddenCol(parentObjTmp.getCbean(),rowDataObjTmp,i));
                    }
                }
            }
            boolean isReadonlyByColInterceptor;
            for(ColBean cbean:lstColBeans)
            {
                if(Consts.COL_DISPLAYTYPE_HIDDEN.equals(cbean.getDisplaytype())||this.cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<=0)
                {
                    if(mRowGroupCols.containsKey(cbean)&&this.cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<0)
                    {
                        throw new WabacusRuntimeException("显示报表"+rbean.getPath()+"失败，不能将参与行分组的列授权为不显示");
                    }
                    resultBuf.append(showHiddenCol(cbean,rowDataObjTmp,i));
                    continue;
                }
                isReadonlyByColInterceptor=false;
                int rowspan=1;
                boolean isRowGroup=false;
                tdPropsBuf=new StringBuffer();
                if(mRowGroupCols.containsKey(cbean))
                {
                    rgdbean=getCommonRowGroupDataBean(mRowGroupCols.get(cbean),mStatiRowGroupBeans,lstHasDisplayedRowGroupCols,cbean,i);
                    if(rgdbean==null) continue;//当前分组列在当前行中不用显示
                    isRowGroup=true;
                    rowspan=rgdbean.getRowspan();
                    String childIdSuffix=rgdbean.getAllChildDataRowIdxsAsString();
                    if(!childIdSuffix.equals(""))
                    {
                        tdPropsBuf.append(" childDataIdSuffixes=\"").append(childIdSuffix).append("\"");
                    }
                    childIdSuffix=rgdbean.getAllChildGroupIdxsAsString();
                    if(!childIdSuffix.equals(""))
                    {
                        tdPropsBuf.append(" childGroupIdSuffixes=\"").append(childIdSuffix).append("\"");
                    }
                    if(rgdbean.getParentGroupIdSuffix()!=null&&!rgdbean.getParentGroupIdSuffix().trim().equals(""))
                    {
                        tdPropsBuf.append(" parentCommonGroupTdId=\"").append(rgdbean.getParentGroupIdSuffix()).append("\"");//记下当前分组列的父分组列所在<td/>的id
                    }
                }
                Object colDataObj=initDisplayCol(cbean,rowDataObjTmp);
                resultBuf.append("<td ").append(getTdPropertiesForCol(cbean,colDataObj,i,isRowGroup));//在<td/>中附加上编辑时要用到的属性;
                String col_displayvalue=getColDisplayValue(cbean,rowDataObjTmp,tdPropsBuf,colDataObj,i,isReadonlyByRowInterceptor);
                colDisplayData=ColDisplayData.getColDataFromInterceptor(this,cbean,rowDataObjTmp,i,getColValuestyleproperty(cbean,rowDataObjTmp),col_displayvalue);
                isReadonlyByColInterceptor=colDisplayData.getColdataByInterceptor()!=null&&colDisplayData.getColdataByInterceptor().isReadonly();
                if(!isReadonlyByRowInterceptor&&isReadonlyByColInterceptor)
                {
                    tdPropsBuf.delete(0,tdPropsBuf.length());
                    col_displayvalue=getColDisplayValue(cbean,rowDataObjTmp,tdPropsBuf,colDataObj,i,true);
                }else
                {
                    col_displayvalue=colDisplayData.getValue();
                }
                resultBuf.append(" class='"+getDataTdClassName()+"' rowspan=\"").append(rowspan).append("\" ");
                resultBuf.append(tdPropsBuf.toString());
                resultBuf.append(" ").append(colDisplayData.getStyleproperty());
                if(isRowGroup)
                {
                    resultBuf.append(" groupcol=\"true\"");
                    if(alrbean.getLstRoworderTypes()!=null&&alrbean.getLstRoworderTypes().contains(Consts.ROWORDER_DRAG))
                    {
                        resultBuf.append(" onmouseover=\"dragrow_enabled=false;\" onmouseout=\"dragrow_enabled=true;\"");
                    }
                }
                resultBuf.append(">").append(
                        getColDisplayValueWithWrap(cbean,col_displayvalue,colDataObj,isReadonlyByRowInterceptor||isReadonlyByColInterceptor));
                resultBuf.append("</td>");
            }
            resultBuf.append("</tr>");
            this.global_rowindex++;
            this.global_sequence++;
            resultBuf.append(showStatisticForCommonRowGroup(lstHasDisplayedRowGroupCols,i));
        }
        return resultBuf.toString();
    }

    private RowGroupDataBean getCommonRowGroupDataBean(List<RowGroupDataBean> lstRgdbeans,
            Map<String,AbsListReportRowGroupSubDisplayRowBean> mStatiRowGroupBeans,List<RowGroupDataBean> lstHasDisplayedRowGroupCols,ColBean cbean,
            int rowindex)
    {
        if(lstRgdbeans==null||lstRgdbeans.size()==0) return null;
        RowGroupDataBean rgdbean=lstRgdbeans.get(0);
        if(rgdbean==null) return null;
        if(rgdbean.getDisplay_rowidx()!=rowindex) return null;
        lstRgdbeans.remove(0);
        if(lstHasDisplayedRowGroupCols!=null&&mStatiRowGroupBeans.containsKey(cbean.getColumn()))
        {
            lstHasDisplayedRowGroupCols.add(rgdbean);
        }
        return rgdbean;
    }

    private String showStatisticForCommonRowGroup(List<RowGroupDataBean> lstHasDisplayedRowGroupCols,int rowindex)
    {
        if(lstHasDisplayedRowGroupCols==null) return "";
        StringBuffer resultBuf=new StringBuffer();
        RowGroupDataBean rgdbTmp;
        for(int j=lstHasDisplayedRowGroupCols.size()-1;j>=0;j--)
        {
            rgdbTmp=lstHasDisplayedRowGroupCols.get(j);
            if(((CommonRowGroupDataBean)rgdbTmp).getDisplaystatidata_rowidx()!=rowindex) continue;
            resultBuf.append("<tr  class='cls-data-tr' >");
            resultBuf.append(showRowGroupStatiData(rgdbTmp,rgdbTmp.getLayer()+1));
            resultBuf.append("</tr>");
            lstHasDisplayedRowGroupCols.remove(j);
        }
        return resultBuf.toString();
    }

    private String showTreeRowGroupDataPart(List<ColBean> lstColBeans)
    {
        StringBuffer resultBuf=new StringBuffer();
        String trgroupid=rbean.getGuid()+"_trgroup_";
        List<RowGroupDataBean> lstTreeGroupDataBeans;
        TreeRowGroupDataBean trgdbean;
        RowDataBean rowInterceptorObjTmp=null;
        ColDisplayData colDisplayData;
        StringBuffer tdPropsBuf;
        AbsReportDataPojo rowDataObjTmp=null;
        int[] displayrowinfo=this.getDisplayRowInfo();
        if(displayrowinfo[1]<=0) return "";
        boolean isReadonlyByRowInterceptor;
        for(int i=displayrowinfo[0];i<displayrowinfo[1];i++)
        {
            if(i>=this.lstReportData.size())
            {
                resultBuf.append(showDataRowInAddMode(lstColBeans,i));
                continue;
            }
            rowDataObjTmp=lstReportData.get(i);
            ColBean cbeanTmp;
            String trstylepropertyTmp=null;
            for(String colcolumn:alrdbean.getLstRowgroupColsColumn())
            {
                if(colcolumn==null) continue;
                cbeanTmp=rbean.getDbean().getColBeanByColColumn(colcolumn);
                if(this.cacheDataBean.getColDisplayModeAfterAuthorize(cbeanTmp)<0)
                {
                    throw new WabacusRuntimeException("显示报表"+rbean.getPath()+"失败，不能将参与树形分组的列授权为不显示");
                }
                lstTreeGroupDataBeans=this.mRowGroupCols.get(cbeanTmp);
                trgdbean=getTreeRowGroupDataBean(lstTreeGroupDataBeans,i);
                if(trgdbean==null) continue;//此分组列在当前行中不用显示一新的行
                resultBuf.append(showTreeRowGroupTrStart(trgroupid+trgdbean.getLayer()+"_"+i,trgdbean));
                resultBuf.append("<td class='cls-data-td-list' ");
                Object colDataObj=initDisplayCol(cbeanTmp,rowDataObjTmp);
                tdPropsBuf=new StringBuffer();//对于树形分组节点，tdPropsBuf的属性不是显示在上面外层<td/>中，而是显示在最里层的<td/>中
                tdPropsBuf.append(getTdPropertiesForCol(cbeanTmp,colDataObj,i,false));//这个不能放在resultBuf，而应该放在tdPropsBuf中，因为稍后要放入显示内容的<td/>中
                String col_displayvalue=getColDisplayValue(cbeanTmp,rowDataObjTmp,tdPropsBuf,colDataObj,i,false);
                colDisplayData=ColDisplayData.getColDataFromInterceptor(this,cbeanTmp,rowDataObjTmp,i,getColValuestyleproperty(cbeanTmp,rowDataObjTmp),col_displayvalue);
                col_displayvalue=colDisplayData.getValue();
                resultBuf.append(" ").append(getTreeNodeTdValueStyleProperty(trgdbean,colDisplayData.getStyleproperty(),i,cbeanTmp)).append(">");
                String childIds=trgdbean.getAllChildDataRowIdxsAsString();
                if(!childIds.equals(""))
                {
                    tdPropsBuf.append(" childDataIdSuffixes=\"").append(childIds).append("\"");//在这里放一份子数据结点id集合，以便更新数据时能判断到当前输入框所在<td/>是否是分组节点的td
                }
                resultBuf.append(showTreeNodeContent(trgroupid+trgdbean.getLayer()+"_"+i,trgdbean,getColDisplayValueWithWrap(cbeanTmp,
                        col_displayvalue,colDataObj,true),tdPropsBuf.toString()));
                resultBuf.append("</td>");
                resultBuf.append(showOtherTdInTreeGroupRow(trgdbean,i,cbeanTmp));
                resultBuf.append("</tr>");
            }
            
            isReadonlyByRowInterceptor=false;
            trstylepropertyTmp=rowDataObjTmp.getRowValuestyleproperty();
            rowInterceptorObjTmp=null;
            if(this.rbean.getInterceptor()!=null)
            {
                rowInterceptorObjTmp=new RowDataBean(this,trstylepropertyTmp,lstColBeans,rowDataObjTmp,i,this.cacheDataBean.getTotalColCount());
                this.rbean.getInterceptor().beforeDisplayReportDataPerRow(this.rrequest,this.rbean,rowInterceptorObjTmp);
                if(rowInterceptorObjTmp.getInsertDisplayRowHtml()!=null) resultBuf.append(rowInterceptorObjTmp.getInsertDisplayRowHtml());
                if(!rowInterceptorObjTmp.isShouldDisplayThisRow())
                {
                    this.global_rowindex++;
                    continue;
                }
                trstylepropertyTmp=rowInterceptorObjTmp.getRowstyleproperty();
                isReadonlyByRowInterceptor=rowInterceptorObjTmp.isReadonly();
            }
            resultBuf.append(showTreeDataRowTrStart(rowInterceptorObjTmp,this.mAllParentRowGroupDataBeansForPerDataRow,trstylepropertyTmp,i));
            resultBuf.append(">");
            resultBuf.append(showTreeNodeTdInDataTr(i));//每个具体数据记录行都要在最前面多显示一个<td/>，因为所有树枝节点只占据一个<td/>，所以在记录行也要显示一个内容为空的单元格。
            for(RowGroupDataBean parentObjTmp:this.mAllParentRowGroupDataBeansForPerDataRow.get(i))
            {//为当前记录行对应的所有父分组节点，根据需要显示一个隐藏<td/>存放它的值，以便能用上
                resultBuf.append(showHiddenCol(parentObjTmp.getCbean(),rowDataObjTmp,i));
            }
            boolean isReadonlyByColInterceptor;
            for(ColBean cbean:lstColBeans)
            {
                if(alrdbean.getLstRowgroupColsColumn().contains(cbean.getColumn())) continue;
                if(Consts.COL_DISPLAYTYPE_HIDDEN.equals(cbean.getDisplaytype())||this.cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<=0)
                {
                    resultBuf.append(showHiddenCol(cbean,rowDataObjTmp,i));
                    continue;
                }
                isReadonlyByColInterceptor=false;
                Object colDataObj=initDisplayCol(cbean,rowDataObjTmp);
                tdPropsBuf=new StringBuffer();
                resultBuf.append("<td ").append(getTdPropertiesForCol(cbean,colDataObj,i,false));//在<td/>中附加上编辑时要用到的属性;
                String col_displayvalue=getColDisplayValue(cbean,rowDataObjTmp,tdPropsBuf,colDataObj,i,isReadonlyByRowInterceptor);
                colDisplayData=ColDisplayData.getColDataFromInterceptor(this,cbean,rowDataObjTmp,i,getColValuestyleproperty(cbean,rowDataObjTmp),col_displayvalue);
                isReadonlyByColInterceptor=colDisplayData.getColdataByInterceptor()!=null&&colDisplayData.getColdataByInterceptor().isReadonly();
                if(!isReadonlyByRowInterceptor&&isReadonlyByColInterceptor)
                {
                    tdPropsBuf.delete(0,tdPropsBuf.length());
                    col_displayvalue=getColDisplayValue(cbean,rowDataObjTmp,tdPropsBuf,colDataObj,i,true);
                }else
                {
                    col_displayvalue=colDisplayData.getValue();
                }
                resultBuf.append(" class='"+getDataTdClassName()+"' ");
                resultBuf.append(tdPropsBuf.toString()).append(" ");
                resultBuf.append(getTreeDataTdValueStyleProperty(cbean,colDisplayData.getStyleproperty(),i));
                resultBuf.append(">").append(
                        getColDisplayValueWithWrap(cbean,col_displayvalue,colDataObj,isReadonlyByRowInterceptor||isReadonlyByColInterceptor));
                resultBuf.append("</td>");
            }
            resultBuf.append("</tr>");
            this.global_rowindex++;
            this.global_sequence++;
        }
        return resultBuf.toString();
    }

    private TreeRowGroupDataBean getTreeRowGroupDataBean(List<RowGroupDataBean> lstTreeGroupDataBeans,int rowindex)
    {
        if(lstTreeGroupDataBeans==null||lstTreeGroupDataBeans.size()==0) return null;
        RowGroupDataBean rgdb=lstTreeGroupDataBeans.get(0);
        if(rgdb.getDisplay_rowidx()!=rowindex) return null;
        lstTreeGroupDataBeans.remove(0);
        return (TreeRowGroupDataBean)rgdb;
    }

    protected String showDataRowTrStart(RowDataBean rowInterceptorObj,String trstyleproperty,int rowindex,boolean isRowGroupDataTr)
    {
        StringBuffer builtinStylepropertyBuf=new StringBuffer();
        builtinStylepropertyBuf.append(" global_rowindex=\"").append(this.global_rowindex).append("\"");
        if(rowInterceptorObj!=null&&rowInterceptorObj.isSelectedRow())
        {
            builtinStylepropertyBuf.append(" default_rowselected=\"true\"");
        }
        AbsListReportBean alrbean=(AbsListReportBean)rbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
        if(alrbean.getLstRoworderTypes()!=null&&alrbean.getLstRoworderTypes().contains(Consts.ROWORDER_DRAG)
                &&!rrequest.checkPermission(this.rbean.getId(),"roworder",Consts.ROWORDER_DRAG,Consts.PERMISSION_TYPE_DISABLED))
        {
            builtinStylepropertyBuf.append(" onmousedown=\"try{handleRowDragMouseDown(this,'"+this.rbean.getPageBean().getId()+"','"+this.rbean.getId()
                    +"');}catch(e){logErrorsAsJsFileLoad(e);}\" ");
        }
        
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
        {
            if(alrdbean.getMouseoverbgcolor()!=null&&!alrdbean.getMouseoverbgcolor().trim().equals(""))
            {//当前报表需要鼠标滑过时改变行背景色
                builtinStylepropertyBuf.append(" onmouseover=\"try{changeRowBgcolorOnMouseOver(this,'").append(alrdbean.getMouseoverbgcolor()).append(
                        "');}catch(e){}\"");
                builtinStylepropertyBuf.append(" onmouseout=\"try{resetRowBgcolorOnMouseOver(this);}catch(e){}\"");
            }
        }
        return "<tr "+getTrValueStyleproperty(trstyleproperty,rbean.getGuid()+"_tr_"+rowindex,rowindex,builtinStylepropertyBuf.toString(),isRowGroupDataTr);
    }

    private String getTrValueStyleproperty(String trstyleproperty,String id,int rowindex,String builtinStyleproperty,boolean isRowGroupDataTr)
    {
        if(trstyleproperty==null) trstyleproperty="";
        if(trstyleproperty.toLowerCase().indexOf("id=")>=0)
        {
            trstyleproperty=Tools.removePropertyValueByName("id",trstyleproperty);
        }
        trstyleproperty+=" id=\""+id+"\"";
        if(trstyleproperty.toLowerCase().indexOf("class=")<0)
        {
            if(isRowGroupDataTr)
            {//如果是显示行分组数据的行，则不分奇偶，因为鼠标滑过时，是通过<td/>改变背景色，这里设置的行背景色无效
                trstyleproperty+=" class=\"cls-data-tr\"";
            }else
            {
                trstyleproperty+=" class=\"cls-data-tr-"+(rowindex%2==0?"even":"odd")+"\"";
            }
        }
        if(builtinStyleproperty!=null&&!builtinStyleproperty.equals(""))
        {
            trstyleproperty=Tools.mergeHtmlTagPropertyString(trstyleproperty,builtinStyleproperty,1);
        }
        return trstyleproperty;
    }
    
    protected String getTdPropertiesForCol(ColBean cbean,Object colDataObj,int rowidx,boolean isCommonRowGroup)
    {
        if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE) return "";
        StringBuffer resultBuf=new StringBuffer();
        AbsListReportColBean alrcbean=(AbsListReportColBean)cbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
        if(this.fixedDataBean!=null)
        {
            resultBuf.append(this.fixedDataBean.showFirstNonFixedColFlag(cbean));
            if(alrcbean!=null&&alrcbean.isFixedCol(rrequest))
            {
                resultBuf.append(" isFixedCol=\"true\"");
            }
        }
        if(cbean.isSequenceCol()||cbean.isControlCol()||alrcbean==null) return resultBuf.toString();
        if(alrcbean.shouldShowColNamePropertyInTd())
        {
            resultBuf.append(" name=\"").append(cbean.getProperty()).append("\"");
        }
        if(alrcbean.shouldShowColValuePropertyInTd())
        {
            if(colDataObj==null) colDataObj="";
            if(this.cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<0)
            {
                throw new WabacusRuntimeException("显示报表"+rbean.getPath()+"的列"+cbean.getColumn()+"失败，不能将其授权为不显示");
            }
            resultBuf.append(" value=\"").append(Tools.htmlEncode(colDataObj.toString())).append("\"");
        }
        if(alrcbean.getSlaveReportParamName()!=null&&!alrcbean.getSlaveReportParamName().trim().equals(""))
        {
            resultBuf.append(" slave_paramname=\"").append(alrcbean.getSlaveReportParamName()).append("\"");
        }
        return resultBuf.toString();
    }

    protected String showDataRowInAddMode(List<ColBean> lstColBeans,int rowidx)
    {
        return "";
    }

    protected Object initDisplayCol(ColBean cbean,AbsReportDataPojo rowDataObjTmp)
    {
        if(cbean.isSequenceCol()||cbean.isControlCol()) return null;
        if(cbean.getProperty()==null||cbean.getProperty().trim().equals("")) return null;
        return rowDataObjTmp.getColStringValue(cbean);
        
    }

    private String showHiddenCol(ColBean cbean,AbsReportDataPojo dataObj,int rowidx)
    {
        if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE) return "";
        StringBuffer resultBuf=new StringBuffer();
        Object colDataObj=initDisplayCol(cbean,dataObj);
        resultBuf.append("<td style='display:none' ");
        resultBuf.append(getTdPropertiesForCol(cbean,colDataObj,rowidx,false));
        resultBuf.append("></td>");
        return resultBuf.toString();
    }

    private String getTreeDataTdValueStyleProperty(ColBean cbean,String valuestyleproperty,int rowindex)
    {
        String styletmp=null;
        if(alrdbean.getTreeborder()==0||alrdbean.getTreeborder()==1)
        {
            styletmp="border-top:none;";
            if(rowindex!=lstReportData.size()-1)
            {
                styletmp+="border-bottom:none;";
            }
        }
        if(styletmp!=null)
        {
            valuestyleproperty=Tools.mergeHtmlTagPropertyString(valuestyleproperty,"style=\""+styletmp+"\"",1);
        }
        return valuestyleproperty;
    }

    private String showTreeNodeTdInDataTr(int rowindex)
    {
        StringBuffer resultBuf=new StringBuffer();
        if(alrdbean.getTreeborder()==3)
        {
            resultBuf.append("<td class='cls-data-td-list' groupcol='true'>&nbsp;</td>");
        }else
        {
            if(rowindex!=lstReportData.size()-1)
            {
                resultBuf.append("<td class='cls-data-td-list' style='border-top:none;border-bottom:none;' groupcol='true'>&nbsp;</td>");
            }else
            {
                resultBuf.append("<td class='cls-data-td-list' style='border-top:none;' groupcol='true'>&nbsp;</td>");
            }
        }
        return resultBuf.toString();
    }

    private String showTreeDataRowTrStart(RowDataBean rowInterceptorObj,Map<Integer,List<RowGroupDataBean>> mParentDataBeansForPerDataRow,String trstylepropertyTmp,int i)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(showDataRowTrStart(rowInterceptorObj,trstylepropertyTmp,i,true));
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
        {
            List<RowGroupDataBean> listAllParentsObj=mParentDataBeansForPerDataRow.get(i);
            if(this.alrdbean.getTreexpandlayer()>=0&&listAllParentsObj!=null&&listAllParentsObj.size()>0)
            {
                if(this.alrdbean.getTreexpandlayer()<=listAllParentsObj.get(listAllParentsObj.size()-1).getLayer())
                {
                    resultBuf.append(" style='display:none;'");
                }
            }
            resultBuf.append(" parentTridSuffix=\"").append(getDirectParentGroupId(listAllParentsObj)).append("\"");
            resultBuf.append(" grouprow=\"true\"");
        }
        return resultBuf.toString();
    }

    private String showOtherTdInTreeGroupRow(TreeRowGroupDataBean trgdbean,int rowindex,ColBean cbeanTemp)
    {
        StringBuffer resultBuf=new StringBuffer();
        int columncount=this.cacheDataBean.getTotalColCount();
        if(trgdbean.getLstScolbeans()!=null&&trgdbean.getLstScolbeans().size()>0&&trgdbean.getStatiDataObj()!=null)
        {
            resultBuf.append(showRowGroupStatiData(trgdbean,1));
        }else
        {
            if(alrdbean.getTreeborder()==0)
            {
                String style="";
                if(rowindex!=0||trgdbean.getLayer()!=0)
                {
                    style=style+"border-top:none;";
                }
                if(rowindex!=lstReportData.size()-1||trgdbean.getLayer()!=alrdbean.getRowGroupColsNum()-1)
                {
                    style=style+"border-bottom:none;";
                }
                if(!style.equals(""))
                {
                    style=Tools.mergeHtmlTagPropertyString(cbeanTemp.getValuestyleproperty(rrequest,false),"style=\""+style+"\"",1);
                }
                for(int m=0;m<columncount-1;m++)
                {//因为要确保树枝节点的行也显示竖线边框，因此不能用colspan=columncount-1将td占满剩余的行，否则这些行不会为没个单元格显示竖线边框，必须为它们各自显示一个<td/>
                    resultBuf.append("<td class='cls-data-td-list' ").append(style).append(">&nbsp;</td>");
                }
            }else
            {
                resultBuf.append("<td class='cls-data-td-list' colspan='").append(columncount-1).append("'>&nbsp;</td>");
            }
        }
        return resultBuf.toString();
    }

    private String showTreeNodeContent(String trgroupid,TreeRowGroupDataBean trgdbean,String displayvalue,String displayvalue_tdproperty)
    {
        StringBuffer resultBuf=new StringBuffer();
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
        {
            
            resultBuf.append("<table border='0' cellpadding='0' cellspacing='0' width='100%' style='margin:0pt;padding: 0pt;'><tr><td");
            resultBuf.append(" width='").append(trgdbean.getLayer()*12).append("px'>");
            resultBuf.append("</td>");
        }else
        {
            String nodeblank="&nbsp;&nbsp;&nbsp;&nbsp;";
            for(int k=0;k<trgdbean.getLayer();k++)
            {
                resultBuf.append(nodeblank);
            }
        }
        if(alrdbean.isTreecloseable()&&rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
        {
            resultBuf.append("<td align='right' width='12px'>");//因为折叠图片11px
            resultBuf.append("<img id=\"").append(trgroupid).append("_img\"");
            resultBuf.append(" src=\"").append(Config.webroot).append("webresources/skin/"+rrequest.getPageskin()+"/images/");
            if(this.alrdbean.getTreexpandlayer()>=0&&this.alrdbean.getTreexpandlayer()<=trgdbean.getLayer())
            {
                resultBuf.append("nodeclosed.gif");
            }else
            {
                resultBuf.append("nodeopen.gif");
            }
            resultBuf.append("\"");
            String tridPrex=trgroupid.substring(0,trgroupid.lastIndexOf("trgroup_"));
            String scrollid="";
            if(alrbean.getScrollType()==AbsListReportBean.SCROLLTYPE_VERTICAL&&Consts_Private.SCROLLSTYLE_IMAGE.equals(rbean.getScrollstyle()))
            {
                scrollid="vscroll_"+rbean.getGuid();
            }
            resultBuf.append(" onmouseover=\"this.style.cursor='pointer'\" onclick=\"try{expandOrCloseTreeNode('").append(Config.webroot).append(
                    "','"+rrequest.getPageskin()+"',this,'");
            resultBuf.append(tridPrex).append("','"+scrollid+"');}catch(e){logErrorsAsJsFileLoad(e);}\"");
            resultBuf.append("/>");
            resultBuf.append("</td>");
        }
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
        {
            if(Consts.ROWSELECT_CHECKBOX.equalsIgnoreCase(this.alrbean.getRowSelectType())
                    ||Consts.ROWSELECT_MULTIPLE_CHECKBOX.equalsIgnoreCase(this.alrbean.getRowSelectType()))
            {
                resultBuf
                        .append("<td width='1px' nowrap><input type=\"checkbox\" onclick=\"try{doSelectedDataRowChkRadio(this);}catch(e){logErrorsAsJsFileLoad(e);}\" name=\""
                                +rbean.getGuid()+"_rowselectbox_col\" rowgroup=\"true\"></td>");
            }
            resultBuf.append("<td align='left'");
            if(displayvalue_tdproperty!=null&&!displayvalue_tdproperty.trim().equals(""))
            {
                resultBuf.append(" ").append(displayvalue_tdproperty);
            }
            resultBuf.append(">");
            if(displayvalue==null||displayvalue.trim().equals(""))
            {
                displayvalue="&nbsp;";
            }
        }
        resultBuf.append(displayvalue);
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
        {
            resultBuf.append("</td></tr></table>");
        }
        return resultBuf.toString();
    }

    private String getTreeNodeTdValueStyleProperty(TreeRowGroupDataBean trgdbean,String valuestyleproperty,int rowindex,ColBean cbeanTemp)
    {
        if(alrdbean.getTreeborder()!=3)
        {
            String styletmp="border-bottom:none;";
            if(rowindex!=0||trgdbean.getLayer()!=0)
            {
                styletmp+="border-top:none;";
            }
            valuestyleproperty=Tools.mergeHtmlTagPropertyString(valuestyleproperty,"style=\""+styletmp+"\"",1);
        }
        return valuestyleproperty;
    }

    private String showTreeRowGroupTrStart(String trid,TreeRowGroupDataBean trgdbean)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("<tr  class='cls-data-tr' id=\""+trid+"\"");
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE)
        {
            
            String childIds=trgdbean.getAllChildGroupIdxsAsString();
            if(!childIds.equals(""))
            {
                resultBuf.append(" childGroupIdSuffixes=\"").append(childIds).append("\"");
            }
            childIds=trgdbean.getAllChildDataRowIdxsAsString();
            if(!childIds.equals(""))
            {
                resultBuf.append(" childDataIdSuffixes=\"").append(childIds).append("\"");
            }
            if(trgdbean.getParentGroupIdSuffix()!=null&&!trgdbean.getParentGroupIdSuffix().trim().equals(""))
            {
                resultBuf.append(" parentTridSuffix=\"").append(trgdbean.getParentGroupIdSuffix()).append("\"");
            }
            if(this.alrdbean.getTreexpandlayer()>=0&&trgdbean.getLayer()>=this.alrdbean.getTreexpandlayer())
            {
                resultBuf.append(" state=\"close\"");
                if(trgdbean.getLayer()>this.alrdbean.getTreexpandlayer())
                {
                    resultBuf.append(" style=\"display:none;\"");
                }
            }else
            {
                resultBuf.append(" state=\"open\"");
            }
        }
        resultBuf.append(">");
        return resultBuf.toString();
    }

    private String getDirectParentGroupId(List<RowGroupDataBean> listAllParentsObj)
    {
        if(listAllParentsObj==null||listAllParentsObj.size()==0) return "";
        return listAllParentsObj.get(listAllParentsObj.size()-1).getIdSuffix();
    }

    private String showRowGroupStatiData(RowGroupDataBean rgdbean,int startcolspan)
    {
        StringBuffer resultBuf=new StringBuffer();
        String stativalue;
        ColDisplayData colDisplayData;
        List<AbsListReportSubDisplayColBean> lstStatiColBeans=rgdbean.getLstScolbeans();
        Object statiDataObj=rgdbean.getStatiDataObj();
        if(statiDataObj==null||lstStatiColBeans==null) return "";
        if(rbean.getDbean().isColselect()
                ||lstStatiColBeans.size()==1
                ||(this.cacheDataBean.getAttributes().get("authroize_col_display")!=null&&String.valueOf(
                        this.cacheDataBean.getAttributes().get("authroize_col_display")).trim().equals("false"))
                ||(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE&&alrbean.hasControllCol()))
        {
            resultBuf.append("<td class='cls-data-td-list' ");
            int colspan=this.cacheDataBean.getTotalColCount()-startcolspan;
            if(colspan<=0) return "";
            resultBuf.append(" colspan='").append(colspan).append("' ");
            StringBuffer statiContentBuf=new StringBuffer();
            String dyntdstyleproperty=null;
            for(AbsListReportSubDisplayColBean scbean:lstStatiColBeans)
            {
                if(!rrequest.checkPermission(rbean.getId(),Consts.DATA_PART,scbean.getProperty(),Consts.PERMISSION_TYPE_DISPLAY)) continue;//当前统计项没有显示权限
                stativalue=getSubColDisplayValue(statiDataObj,scbean);
                colDisplayData=ColDisplayData.getColDataFromInterceptor(this,scbean,null,0,scbean.getValuestyleproperty(rrequest,false),stativalue);
                statiContentBuf.append(colDisplayData.getValue()).append("&nbsp;&nbsp;");
                if(dyntdstyleproperty==null)
                    dyntdstyleproperty=Tools.removePropertyValueByName("colspan",colDisplayData.getStyleproperty());
            }
            stativalue=statiContentBuf.toString().trim();
            if(stativalue.endsWith("&nbsp;&nbsp;")) stativalue=stativalue.substring(0,stativalue.length()-"&nbsp;&nbsp;".length()).trim();
            //            if(stativalue.equals("")) return "";//当前没有要显示的统计项
            if(dyntdstyleproperty!=null) resultBuf.append(dyntdstyleproperty);
            resultBuf.append(">").append(stativalue).append("</td>");
        }else
        {
            for(AbsListReportSubDisplayColBean scbean:lstStatiColBeans)
            {
                stativalue=getSubColDisplayValue(statiDataObj,scbean);
                colDisplayData=ColDisplayData.getColDataFromInterceptor(this,scbean,null,0,scbean.getValuestyleproperty(rrequest,false),stativalue);
                resultBuf.append("<td class='cls-data-td-list' ");
                resultBuf.append(colDisplayData.getStyleproperty());
                resultBuf.append(">").append(colDisplayData.getValue()).append("</td>");
            }
        }
        return resultBuf.toString();
    }

    public String getColOriginalValue(AbsReportDataPojo object,ColBean cbean)
    {
        return object.getColStringValue(cbean);
    }

    protected String getColDisplayValue(ColBean cbean,AbsReportDataPojo dataObj,StringBuffer tdPropBuf,Object colDataObj,int rowidx,boolean isReadonly)
    {
        String col_displayvalue="";
        if(cbean.isRowSelectCol())
        {
            AbsListReportBean alrbean=(AbsListReportBean)rbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
            if(Consts.ROWSELECT_CHECKBOX.equalsIgnoreCase(alrbean.getRowSelectType())
                    ||Consts.ROWSELECT_MULTIPLE_CHECKBOX.equalsIgnoreCase(alrbean.getRowSelectType()))
            {
                col_displayvalue="<input type=\"checkbox\" onclick=\"try{doSelectedDataRowChkRadio(this);}catch(e){logErrorsAsJsFileLoad(e);}\" name=\""
                        +rbean.getGuid()+"_rowselectbox_col\">";
            }else if(Consts.ROWSELECT_RADIOBOX.equalsIgnoreCase(alrbean.getRowSelectType())
                    ||Consts.ROWSELECT_SINGLE_RADIOBOX.equalsIgnoreCase(alrbean.getRowSelectType()))
            {
                col_displayvalue="<input type=\"radio\" onclick=\"try{doSelectedDataRowChkRadio(this);}catch(e){logErrorsAsJsFileLoad(e);}\" name=\""
                        +rbean.getGuid()+"_rowselectbox_col\">";
            }else
            {
                throw new WabacusRuntimeException("显示报表"+rbean.getPath()+"失败，此报表的行选中类型不是"+Consts.ROWSELECT_CHECKBOX+"、"+Consts.ROWSELECT_RADIOBOX+"、"
                        +Consts.ROWSELECT_MULTIPLE_CHECKBOX+"、"+Consts.ROWSELECT_SINGLE_RADIOBOX+"类型，不能配置"+Consts_Private.COL_ROWSELECT+"类型的列");
            }
        }else if(cbean.isRoworderCol())
        {
            if(cbean.isRoworderArrowCol())
            {
                String arrowup=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,this.rbean.getPageBean(),
                        "${roworder.arrow.up}",true));
                String arrowdown=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,this.rbean.getPageBean(),
                        "${roworder.arrow.down}",true));
                boolean hasNoPermission=rrequest.checkPermission(this.rbean.getId(),Consts.DATA_PART,Consts_Private.COL_ROWORDER_ARROW,
                        Consts.PERMISSION_TYPE_DISABLED)
                        ||rrequest.checkPermission(this.rbean.getId(),"roworder",Consts.ROWORDER_ARROW,Consts.PERMISSION_TYPE_DISABLED);
                if(hasNoPermission)
                {
                    col_displayvalue="<span class='cls-roworder-disabled'>"+arrowup+"</span>";
                }else
                {
                    col_displayvalue="<a onClick=\"changeListReportRoworderByArrow('"+this.rbean.getPageBean().getId()+"','"+this.rbean.getId()
                            +"',this,true)\">";
                    col_displayvalue=col_displayvalue+arrowup+"</a>";
                }
                col_displayvalue+="&nbsp;";
                if(hasNoPermission)
                {
                    col_displayvalue=col_displayvalue+"<span class='cls-roworder-disabled'>"+arrowdown+"</span>";
                }else
                {
                    col_displayvalue=col_displayvalue+"<a onClick=\"changeListReportRoworderByArrow('"+this.rbean.getPageBean().getId()+"','"
                            +this.rbean.getId()+"',this,false)\">";
                    col_displayvalue=col_displayvalue+arrowdown+"</a>";
                }
            }else if(cbean.isRoworderInputboxCol())
            {
                Map<String,String> mRoworderColValuesInRow=getRoworderColvaluesInRow(dataObj);
                String oldordervalue="";
                if(alrbean.getLoadStoreRoworderObject()!=null)
                {
                    oldordervalue=alrbean.getLoadStoreRoworderObject().loadRoworder(mRoworderColValuesInRow);
                }else
                {
                    oldordervalue=Config.default_roworder_object.loadRoworder(mRoworderColValuesInRow);
                }
                if(Tools.isDefineKey("@",oldordervalue))
                {
                    ColBean cbTmp=rbean.getDbean().getColBeanByColProperty(Tools.getRealKeyByDefine("@",oldordervalue));
                    if(cbTmp==null)
                    {
                        throw new WabacusRuntimeException("获取报表"+rbean.getPath()+"的行排序数据失败，不存在property为"+Tools.getRealKeyByDefine("@",oldordervalue)
                                +"的<col/>");
                    }
                    oldordervalue=this.getColOriginalValue(dataObj,cbTmp);
                }
                oldordervalue=oldordervalue==null?"":oldordervalue.trim();
                AbsListReportColBean alrcbean=(AbsListReportColBean)cbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
                String inputboxstyleproperty=" onblur=\"changeListReportRoworderByInputbox('"+this.rbean.getPageBean().getId()+"','"
                        +this.rbean.getId()+"',this,'"+oldordervalue+"')\" ";
                if(alrcbean!=null&&alrcbean.getRoworder_inputboxstyleproperty()!=null)
                {
                    inputboxstyleproperty=inputboxstyleproperty+" "+alrcbean.getRoworder_inputboxstyleproperty();
                }
                AbsInputBox box=Config.getInstance().getInputBoxByType(TextBox.class);
                col_displayvalue=box.getIndependentDisplayString(rrequest,oldordervalue,inputboxstyleproperty,null,rrequest.checkPermission(rbean
                        .getId(),Consts.DATA_PART,Consts_Private.COL_ROWORDER_INPUTBOX,Consts.PERMISSION_TYPE_DISABLED)
                        ||rrequest.checkPermission(rbean.getId(),"roworder",Consts.ROWORDER_INPUTBOX,Consts.PERMISSION_TYPE_DISABLED));
            }else if(cbean.isRoworderTopCol())
            {
                String topstr=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,this.rbean.getPageBean(),"${roworder.top}",
                        true));
                if(rrequest.checkPermission(this.rbean.getId(),Consts.DATA_PART,Consts_Private.COL_ROWORDER_TOP,Consts.PERMISSION_TYPE_DISABLED)
                        ||rrequest.checkPermission(this.rbean.getId(),"roworder",Consts.ROWORDER_TOP,Consts.PERMISSION_TYPE_DISABLED))
                {
                    col_displayvalue="<span class='cls-roworder-disabled'>"+topstr+"</span>";
                }else
                {
                    col_displayvalue="<a onMouseOver=\"this.style.cursor='pointer'\" onClick=\"changeListReportRoworderByTop('"
                            +this.rbean.getPageBean().getId()+"','"+this.rbean.getId()+"',this)\">";
                    col_displayvalue=col_displayvalue+topstr+"</a>";
                }
            }
        }else if(cbean.isSequenceCol())
        {
            AbsListReportColBean absListColbean=(AbsListReportColBean)cbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
            col_displayvalue=String.valueOf(this.global_sequence+absListColbean.getSequenceStartNum());
        }else
        {
            col_displayvalue=dataObj.getColStringValue(cbean);
            if(col_displayvalue==null) col_displayvalue="";
        }
        return col_displayvalue;
    }

    /*private boolean isDisabledRoworderByUpArrowOrTop(int rowidx)
    {
        if(this.lstReportData==null||this.lstReportData.size()<2) return true;
        CacheDataBean cdb=rrequest.getCdb(rbean.getId());
        if(cdb.getPagesize()<=0)
        {
            if(rowidx==0) return true;
        }else
        {
            if(cdb.getFinalPageno()==1&&rowidx==0) return true;
        }
        return false;
    }
    
    private boolean isDisabledRoworderByDownArrow(int rowidx)
    {
        if(this.lstReportData==null||this.lstReportData.size()<2) return true;
        CacheDataBean cdb=rrequest.getCdb(rbean.getId());
        if(cdb.getPagesize()<=0)
        {//当前是不分页显示
            if(rowidx==lstReportData.size()-1) return true;
        }else
        {
            if(cdb.getFinalPageno()==cdb.getPagecount()&&rowidx==lstReportData.size()-1) return true;
        }
        return false;
    }*/

    protected String getColDisplayValueWithWrap(ColBean cbean,String col_displayvalue,Object colDataObj,boolean isReadonlyByInterceptor)
    {
        if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE) return col_displayvalue;
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(getColDisplayValueWrapStart(cbean,false,this.isReadonlyCol(cbean,colDataObj,isReadonlyByInterceptor),false));
        resultBuf.append(col_displayvalue);
        resultBuf.append(getColDisplayValueWrapEnd(cbean,false,this.isReadonlyCol(cbean,colDataObj,isReadonlyByInterceptor),false));
        return resultBuf.toString();
    }

    protected String getColDisplayValueWrapStart(ColBean cbean,boolean isInProperty,boolean isReadonly,boolean ignoreFillmode)
    {
        StringBuffer resultBuf=new StringBuffer();
        String startTag="<";
        String endTag=">";
        if(isInProperty)
        {
            startTag="&lt;";
            endTag="&gt;";
        }
        if(rbean.getCellresize()>0||alrbean.getScrollType()==AbsListReportBean.SCROLLTYPE_VERTICAL)
        {
            resultBuf.append(startTag).append("div style='width:100%;' class='cls-data-content-list'").append(endTag);
        }
        if(alrbean.getLstRoworderTypes()!=null&&alrbean.getLstRoworderTypes().contains(Consts.ROWORDER_DRAG))
        {
            resultBuf.append(startTag).append("span onmouseover='dragrow_enabled=false;' onmouseout='dragrow_enabled=true;'").append(endTag);
        }
        
        return resultBuf.toString();
    }

    protected String getColDisplayValueWrapEnd(ColBean cbean,boolean isInProperty,boolean isReadonly,boolean ignoreFillmode)
    {
        StringBuffer resultBuf=new StringBuffer();
        String startTag="<";
        String endTag=">";
        if(isInProperty)
        {
            startTag="&lt;";
            endTag="&gt;";
        }
        if(alrbean.getLstRoworderTypes()!=null&&alrbean.getLstRoworderTypes().contains(Consts.ROWORDER_DRAG))
        {
            resultBuf.append(startTag).append("/span").append(endTag);
        }
        if(rbean.getCellresize()>0||alrbean.getScrollType()==AbsListReportBean.SCROLLTYPE_VERTICAL)
        {
            resultBuf.append(startTag).append("/div").append(endTag);
        }
        return resultBuf.toString();
    }

    protected boolean isReadonlyCol(ColBean cbean,Object colDataObj,boolean isReadonlyByInterceptor)
    {
        return true;
    }

    private Map<String,String> getRoworderColvaluesInRow(AbsReportDataPojo dataObj)
    {
        Map<String,String> mResults=new HashMap<String,String>();
        for(ColBean cbTmp:alrdbean.getLstRoworderValueCols())
        {
            mResults.put(cbTmp.getProperty(),this.getColOriginalValue(dataObj,cbTmp));
        }
        return mResults;
    }

    public String getColSelectedMetadata()
    {
        if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE) return "";
        if(!rbean.getDbean().isColselect()) return "";
        if(rrequest.getMColAndGroupDisplayBeans()==null||rrequest.getMColAndGroupDisplayBeans().get(rbean.getId())==null)
        {//如果没有此报表的列选择信息，因为是在showDataHeaderPart()方法中构造本报表的列选择信息，所以可能是由于没有调用此方法（比如配置了<display/>的dataheader=""或者在模板文件中没有调用），则调用一下
            showDataHeaderPart();
        }
        if(rrequest.getMColAndGroupDisplayBeans()==null) return "";
        List<ColAndGroupDisplayBean> lstCgdbeansTmp=rrequest.getMColAndGroupDisplayBeans().get(rbean.getId());
        if(lstCgdbeansTmp==null||lstCgdbeansTmp.size()==0) return "";
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("<span id=\"").append(rbean.getGuid()).append("_col_titlelist\" style=\"display:none\">");
        String title;
        for(ColAndGroupDisplayBean cgdbeanTmp:lstCgdbeansTmp)
        {
            title=cgdbeanTmp.getTitle();
            title=title==null?"":title.replaceAll("<.*?\\>","").trim();
            resultBuf.append("<item nodeid=\"").append(cgdbeanTmp.getId()).append("\"");
            resultBuf.append(" parentgroupid=\"").append(cgdbeanTmp.getParentGroupId()).append("\"");
            resultBuf.append(" childids=\"").append(cgdbeanTmp.getChildIds()).append("\"");
            resultBuf.append(" layer=\"").append(cgdbeanTmp.getLayer()).append("\"");
            resultBuf.append(" title=\"").append(title).append("\"");
            resultBuf.append(" checked=\"").append(cgdbeanTmp.isChecked()).append("\"");
            resultBuf.append(" isControlCol=\"").append(cgdbeanTmp.isControlCol()).append("\"");
            resultBuf.append(" isNonFixedCol=\"").append(cgdbeanTmp.isNonFixedCol()).append("\"");
            resultBuf.append(" always=\"").append(cgdbeanTmp.isAlways()).append("\"");
            resultBuf.append("></item>");
        }
        resultBuf.append("</span>");
        return resultBuf.toString();
    }

    protected String showMetaDataDisplayStringStart()
    {
        if(this.fixedDataBean==null||this.fixedDataBean.getTotalcolcount()<=0
                ||(this.fixedDataBean.getFixedcolids().trim().equals("")&&this.fixedDataBean.getFixedrowscount()<=0))
            return super.showMetaDataDisplayStringStart();
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(super.showMetaDataDisplayStringStart());
        resultBuf.append(" ft_fixedRowsCount=\"").append(this.fixedDataBean.getFixedrowscount()).append("\"");
        resultBuf.append(" ft_fixedColids=\"").append(this.fixedDataBean.getFixedcolids()).append("\"");
        resultBuf.append(" ft_totalColCount=\"").append(this.fixedDataBean.getTotalcolcount()).append("\"");
        return resultBuf.toString();
    }

    public String getReportDataInJsonString()
    {
        AbsListReportSubDisplayBean subdisplayBean=this.alrbean.getSubdisplaybean();
        if(subdisplayBean==null
                ||(subdisplayBean.getLstSubDisplayRowBeans()==null&&subdisplayBean.getLstSubDisplayRowBeans().size()==0
                        &&subdisplayBean.getMRowGroupSubDisplayRowBeans()==null&&subdisplayBean.getMRowGroupSubDisplayRowBeans().size()==0)
                ||(subdisplayBean.getMPropertiesAndGetMethods()==null||subdisplayBean.getMPropertiesAndGetMethods().size()==0))
        {
            return super.getReportDataInJsonString();
        }
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("{");
        resultBuf.append("\"reportdata\":").append(super.getReportDataInJsonString()).append(",");
        resultBuf.append("\"reportsubdata\":{");
        if(this.subDisplayDataObj!=null)
        {
            resultBuf.append("\"subdata\":{");
            resultBuf.append(getSubDataString(this.subDisplayDataObj));
            resultBuf.append("}");
        }
        if(this.mRowGroupSubDisplayDataObj!=null&&this.mRowGroupSubDisplayDataObj.size()>0)
        {
            for(Entry<String,Object> entryDataObj:this.mRowGroupSubDisplayDataObj.entrySet())
            {
                resultBuf.append("\""+entryDataObj.getKey()+"_subdata\":{");
                resultBuf.append(getSubDataString(entryDataObj.getValue()));
                resultBuf.append("}");
            }
        }
        resultBuf.append("}");
        resultBuf.append("}");
        return resultBuf.toString();
    }

    private String getSubDataString(Object dataObj)
    {
        StringBuffer resultBuf=new StringBuffer();
        Object coldataTmp;
        for(Entry<String,Method> entryTmp:this.alrbean.getSubdisplaybean().getMPropertiesAndGetMethods().entrySet())
        {
            resultBuf.append("\""+entryTmp.getKey()+"\":\"");
            try
            {
                coldataTmp=entryTmp.getValue().invoke(dataObj,new Object[] {});
            }catch(Exception e)
            {
                throw new WabacusRuntimeException("调用方法名"+entryTmp.getValue().getName()+"获取报表"+rbean.getPath()+"的数据失败",e);
            }
            if(coldataTmp==null)
            {
                resultBuf.append("");
            }else
            {
                resultBuf.append(Tools.jsParamEncode(String.valueOf(coldataTmp)));
            }
            resultBuf.append("\",");
        }
        if(resultBuf.charAt(resultBuf.length()-1)==',') resultBuf.deleteCharAt(resultBuf.length()-1);
        return resultBuf.toString();
    }
    
    protected class FixedColAndGroupDataBean
    {
        private int fixedrowscount;

        private String fixedcolids;

        private int totalcolcount;//本次显示的列数（包括隐藏列、显示列、冻结列和普通列）

        private String firstNonFixedColid;//第一个非冻结列的colid（之所以不用冻结列的isFixedCol去寻找第一个非冻结列，是isFixedCol只显示在数据列，不显示在标题列，所以没有数据时找不到isFixedCol进行定位。其次如果将isFixedCol显示到冻结列的标题部分，则当是<group/>时，它的isFixedCol为false，但不能将它视为非冻结列，因为它可能包括冻结列，所以专门用一个标识来标识第一个非冻结列）

        private boolean hasDisplayedFirstNonFixedColFlag;

        public FixedColAndGroupDataBean()
        {
            List<ColBean> lstColBeans=getLstDisplayColBeans();
            if(lstColBeans==null||lstColBeans.size()==0) return;
            String dynfixedRows=rrequest.getStringAttribute(rbean.getId()+"_FIXEDROWS","");
            if(!dynfixedRows.equals(""))
            {
                if(dynfixedRows.toLowerCase().equals("title"))
                {
                    fixedrowscount=Integer.MAX_VALUE;
                }else
                {
                    try
                    {
                        fixedrowscount=Integer.parseInt(dynfixedRows);
                    }catch(NumberFormatException e)
                    {
                        log.warn("动态设置的报表"+rbean.getPath()+"冻结行数"+dynfixedRows+"为无效数字");
                        fixedrowscount=alrbean.getFixedrows();
                    }
                }
            }else
            {
                fixedrowscount=alrbean.getFixedrows();
            }
            if(fixedrowscount<0) fixedrowscount=0;
            totalcolcount=0;
            fixedcolids="";
            boolean encounterNonFixedCol=false;
            AbsListReportColBean alrcbeanTmp;
            for(ColBean cbean:lstColBeans)
            {
                totalcolcount++;
                if(Consts.COL_DISPLAYTYPE_HIDDEN.equals(cbean.getDisplaytype())||cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<=0)
                {
                    continue;
                }
                
                if(encounterNonFixedCol) continue;
                alrcbeanTmp=(AbsListReportColBean)cbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
                if(alrcbeanTmp==null||!alrcbeanTmp.isFixedCol(rrequest))
                {
                    encounterNonFixedCol=true;
                    firstNonFixedColid=cbean.getColid();
                }else
                {
                    fixedcolids=fixedcolids+cbean.getColid()+";";
                }
            }
            if(fixedcolids.endsWith(";")) fixedcolids=fixedcolids.substring(0,fixedcolids.length()-1).trim();
        }

        public void setFixedrowscount(int fixedrowscount)
        {
            this.fixedrowscount=fixedrowscount;
        }

        public int getFixedrowscount()
        {
            return fixedrowscount;
        }

        public String getFixedcolids()
        {
            return fixedcolids;
        }

        public String getFirstNonFixedColid()
        {
            return firstNonFixedColid;
        }

        public int getTotalcolcount()
        {
            return totalcolcount;
        }

        public String showFirstNonFixedColFlag(ColBean cbean)
        {
            if(hasDisplayedFirstNonFixedColFlag||fixedcolids.trim().equals("")) return "";
            if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE) return "";
            if(cbean.getColid().equals(this.firstNonFixedColid))
            {
                this.hasDisplayedFirstNonFixedColFlag=true;
                return " first_nonfixed_col=\"true\"";
            }
            return "";
        }
    }

    private abstract class RowGroupDataBean
    {
        protected ColBean cbean;

        protected int layer;

        protected int display_rowidx;

        protected String displayvalue;

        protected int rowspan;

        protected String idSuffix;

        protected String parentGroupIdSuffix;

        protected List<String> lstAllChildDataRowIdxs=new ArrayList<String>();//对于普通分组，此属性只在可编辑分组报表中会用上

        private List<String> lstAllChildGroupIdxs=new ArrayList<String>();

        private Object statiDataObj;

        private List<AbsListReportSubDisplayColBean> lstScolbeans;

        public RowGroupDataBean(ColBean cbean,String value,int rowidx,int layer)
        {
            this.cbean=cbean;
            this.displayvalue=value;
            this.display_rowidx=rowidx;
            this.layer=layer;
        }

        public int getLayer()
        {
            return layer;
        }

        public void setLayer(int layer)
        {
            this.layer=layer;
        }

        public int getDisplay_rowidx()
        {
            return display_rowidx;
        }

        public void setDisplay_rowidx(int display_rowidx)
        {
            this.display_rowidx=display_rowidx;
        }

        public ColBean getCbean()
        {
            return cbean;
        }

        public void setCbean(ColBean cbean)
        {
            this.cbean=cbean;
        }

        public String getDisplayvalue()
        {
            return displayvalue;
        }

        public void setDisplayvalue(String displayvalue)
        {
            this.displayvalue=displayvalue;
        }

        public int getRowspan()
        {
            return rowspan;
        }

        public void setRowspan(int rowspan)
        {
            this.rowspan=rowspan;
        }

        public String getIdSuffix()
        {
            return idSuffix;
        }

        public void setIdSuffix(String idSuffix)
        {
            this.idSuffix=idSuffix;
        }

        public String getParentGroupIdSuffix()
        {
            return parentGroupIdSuffix;
        }

        public void setParentGroupIdSuffix(String parentGroupIdSuffix)
        {
            this.parentGroupIdSuffix=parentGroupIdSuffix;
        }

        public List<String> getLstAllChildDataRowIdxs()
        {
            return lstAllChildDataRowIdxs;
        }

        public void addChildDataRowIdx(String idx)
        {
            lstAllChildDataRowIdxs.add(idx);
        }

        public void addChildGroupIdx(String idx)
        {
            lstAllChildGroupIdxs.add(idx);
        }

        public List<String> getLstAllChildGroupIdxs()
        {
            return lstAllChildGroupIdxs;
        }

        public Object getStatiDataObj()
        {
            return statiDataObj;
        }

        public void setStatiDataObj(Object statiDataObj)
        {
            this.statiDataObj=statiDataObj;
        }

        public List<AbsListReportSubDisplayColBean> getLstScolbeans()
        {
            return lstScolbeans;
        }

        public void setLstScolbeans(List<AbsListReportSubDisplayColBean> lstScolbeans)
        {
            this.lstScolbeans=lstScolbeans;
        }

        public String getAllChildDataRowIdxsAsString()
        {
            if(lstAllChildDataRowIdxs==null||lstAllChildDataRowIdxs.size()==0) return "";
            StringBuffer sbuffer=new StringBuffer();
            for(String childdataidx:lstAllChildDataRowIdxs)
            {
                sbuffer.append(childdataidx).append(";");
            }
            while(sbuffer.length()>0&&sbuffer.charAt(sbuffer.length()-1)==';')
            {
                sbuffer.deleteCharAt(sbuffer.length()-1);
            }
            return sbuffer.toString();
        }

        public String getAllChildGroupIdxsAsString()
        {
            if(lstAllChildGroupIdxs==null||lstAllChildGroupIdxs.size()==0) return "";
            StringBuffer sbuffer=new StringBuffer();
            for(String childgroupidx:lstAllChildGroupIdxs)
            {
                sbuffer.append(childgroupidx).append(";");
            }
            while(sbuffer.length()>0&&sbuffer.charAt(sbuffer.length()-1)==';')
            {
                sbuffer.deleteCharAt(sbuffer.length()-1);
            }
            return sbuffer.toString();
        }

        public void setValue(AbsListReportSubDisplayBean subdisplayBean,Object dataObj,
                Map<Integer,List<RowGroupDataBean>> mParentDataBeansForPerDataRow)
        {
            this.addChildDataRowIdx("tr_"+display_rowidx);
            List<RowGroupDataBean> lstParentDataBeans=mParentDataBeansForPerDataRow.get(display_rowidx);
            if(lstParentDataBeans==null)
            {
                lstParentDataBeans=new ArrayList<RowGroupDataBean>();
                mParentDataBeansForPerDataRow.put(display_rowidx,lstParentDataBeans);
            }else
            {
                this.setParentGroupIdSuffix(lstParentDataBeans.get(lstParentDataBeans.size()-1).getIdSuffix());
            }
            for(RowGroupDataBean rgdbean:lstParentDataBeans)
            {
                rgdbean.addChildGroupIdx(this.getIdSuffix());
            }
            lstParentDataBeans.add(this);
            if(subdisplayBean!=null&&subdisplayBean.getMRowGroupSubDisplayRowBeans()!=null
                    &&subdisplayBean.getMRowGroupSubDisplayRowBeans().containsKey(cbean.getColumn()))
            {
                getRowGroupStatisticData(mParentDataBeansForPerDataRow.get(display_rowidx),dataObj,subdisplayBean,cbean,this);
            }
        }

        private void getRowGroupStatisticData(List lstParentDataBeans,Object dataObj,AbsListReportSubDisplayBean subdisplayBean,ColBean cbean,
                RowGroupDataBean rgdatabean)
        {
            if(subdisplayBean==null||subdisplayBean.getMRowGroupSubDisplayRowBeans()==null) return;
            AbsListReportRowGroupSubDisplayRowBean srgbean=subdisplayBean.getMRowGroupSubDisplayRowBeans().get(cbean.getColumn());
            if(srgbean==null) return;
            if(mRowGroupSubDisplayDataObj==null) mRowGroupSubDisplayDataObj=new HashMap<String,Object>();
            String statiSqlGroupBy=srgbean.getStatiSqlGroupby();
            rgdatabean.setLstScolbeans(srgbean.getLstSubColBeans());
            try
            {
                Object subdisplayDataObj=subdisplayBean.getPojoObject();
                ColBean cbeanGroupTmp;
                StringBuffer parentAndMyColValueBuf=new StringBuffer();
                for(Object beanTmp:lstParentDataBeans)
                {
                    cbeanGroupTmp=((RowGroupDataBean)beanTmp).getCbean();
                    String convalue=ReportAssistant.getInstance().getPropertyValueAsString(dataObj,cbeanGroupTmp.getColumn()+"_old",
                            cbeanGroupTmp.getDatatypeObj());
                    convalue=convalue==null?"":convalue.trim();
                    parentAndMyColValueBuf.append("[").append(convalue).append("]");
                    if(statiSqlGroupBy!=null&&!statiSqlGroupBy.trim().equals(""))
                    {
                        statiSqlGroupBy=Tools.replaceAll(statiSqlGroupBy,"#"+cbeanGroupTmp.getColumn()+"#",convalue);
                    }
                    String setMethodName="set"+cbeanGroupTmp.getColumn().substring(0,1).toUpperCase()+cbeanGroupTmp.getColumn().substring(1);
                    Method setMethod=subdisplayBean.getPojoclass().getMethod(setMethodName,new Class[] { String.class });
                    setMethod.invoke(subdisplayDataObj,new Object[] { convalue });
                }
                loadSubDisplayDataObj(subdisplayBean,subdisplayDataObj,statiSqlGroupBy,cbean.getColumn());
                rgdatabean.setStatiDataObj(subdisplayDataObj);
                mRowGroupSubDisplayDataObj.put(parentAndMyColValueBuf.toString(),subdisplayDataObj);
            }catch(Exception e)
            {
                throw new WabacusRuntimeException("加载报表"+rbean.getPath()+"针对分组列"+cbean.getColumn()+"的统计数据失败",e);
            }
        }
    }

    private class CommonRowGroupDataBean extends RowGroupDataBean
    {
        private int displaystatidata_rowidx;

        public int getDisplaystatidata_rowidx()
        {
            return displaystatidata_rowidx;
        }

        public void setDisplaystatidata_rowidx(int displaystatidata_rowidx)
        {
            this.displaystatidata_rowidx=displaystatidata_rowidx;
        }

        public CommonRowGroupDataBean(ColBean cbean,String value,int rowidx,int layer)
        {
            super(cbean,value,rowidx,layer);
        }

        public void setValue(AbsListReportSubDisplayBean statiBean,Object dataObj,Map<Integer,List<RowGroupDataBean>> mParentDataBeansForPerDataRow)
        {
            this.setRowspan(1);
            this.setIdSuffix(EditableReportAssistant.getInstance().getInputBoxId(cbean)+"__td"+display_rowidx);
            super.setValue(statiBean,dataObj,mParentDataBeansForPerDataRow);
            List<RowGroupDataBean> lstParentDataBeans=mParentDataBeansForPerDataRow.get(display_rowidx);
            if(statiBean!=null&&statiBean.getMRowGroupSubDisplayRowBeans()!=null
                    &&statiBean.getMRowGroupSubDisplayRowBeans().containsKey(cbean.getColumn()))
            {
                this.setDisplaystatidata_rowidx(display_rowidx);//显示当前分组行的统计信息的记录号先设置为rowidx，如果它包括多行，后面还会不断地修正，一直到它包括的最后一条记录，才是真正显示当前分组统计信息的行
                for(RowGroupDataBean rgdbeanTmp:lstParentDataBeans)
                {
                    rgdbeanTmp.setRowspan(rgdbeanTmp.getRowspan()+1);
                }
            }
        }
    }

    private class TreeRowGroupDataBean extends RowGroupDataBean
    {

        public TreeRowGroupDataBean(ColBean cbean,String value,int rowidx,int layer)
        {
            super(cbean,value,rowidx,layer);
        }

        public void setValue(AbsListReportSubDisplayBean statiBean,Object dataObj,Map<Integer,List<RowGroupDataBean>> mParentDataBeansForPerDataRow)
        {
            this.setRowspan(2);
            this.setIdSuffix("trgroup_"+layer+"_"+display_rowidx);
            super.setValue(statiBean,dataObj,mParentDataBeansForPerDataRow);
            List<RowGroupDataBean> lstParentDataBeans=mParentDataBeansForPerDataRow.get(display_rowidx);
            for(RowGroupDataBean trgdbean:lstParentDataBeans)
            {
                if(trgdbean==this) continue;
                trgdbean.setRowspan(trgdbean.getRowspan()+1);
            }
        }
    }
}
