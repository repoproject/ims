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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.wabacus.config.Config;
import com.wabacus.config.ConfigLoadAssistant;
import com.wabacus.config.ConfigLoadManager;
import com.wabacus.config.OnloadMethodBean;
import com.wabacus.config.component.ComponentConfigLoadManager;
import com.wabacus.config.component.IComponentConfigBean;
import com.wabacus.config.component.application.report.AbsReportDataPojo;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.DisplayBean;
import com.wabacus.config.component.application.report.FormatBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.config.component.application.report.SqlBean;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.CacheDataBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.ComponentAssistant;
import com.wabacus.system.assistant.EditableReportAssistant;
import com.wabacus.system.assistant.ReportAssistant;
import com.wabacus.system.assistant.StandardExcelAssistant;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.system.commoninterface.IListReportRoworderPersistence;
import com.wabacus.system.component.application.report.UltraListReportType;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportColBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportDisplayBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportFilterBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportRowGroupSubDisplayRowBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportSubDisplayBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportSubDisplayColBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportSubDisplayRowBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.statistic.StatisticItemAndDataSetBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.statistic.StatisticItemBean;
import com.wabacus.system.component.application.report.configbean.ColDisplayData;
import com.wabacus.system.component.application.report.configbean.UltraListReportColBean;
import com.wabacus.system.component.application.report.configbean.UltraListReportDisplayBean;
import com.wabacus.system.component.application.report.configbean.UltraListReportGroupBean;
import com.wabacus.system.component.container.AbsContainerType;
import com.wabacus.util.Consts;
import com.wabacus.util.Consts_Private;
import com.wabacus.util.Tools;

public abstract class AbsListReportType extends AbsReportType
{
    public final static String KEY=AbsListReportType.class.getName();

    private final static Log log=LogFactory.getLog(AbsListReportType.class);

    protected AbsListReportBean alrbean;

    protected AbsListReportDisplayBean alrdbean;

    protected Object subDisplayDataObj;

    protected Map<String,Object> mRowGroupSubDisplayDataObj;

    public AbsListReportType(AbsContainerType parentContainerType,IComponentConfigBean comCfgBean,ReportRequest rrequest)
    {
        super(parentContainerType,comCfgBean,rrequest);
        if(comCfgBean!=null)
        {
            alrbean=(AbsListReportBean)((ReportBean)comCfgBean).getExtendConfigDataForReportType(KEY);
            alrdbean=(AbsListReportDisplayBean)((ReportBean)comCfgBean).getDbean().getExtendConfigDataForReportType(KEY);
        }
    }

    public Object getSubDisplayDataObj()
    {
        return subDisplayDataObj;
    }

    public Map<String,Object> getMRowGroupSubDisplayDataObj()
    {
        return mRowGroupSubDisplayDataObj;
    }

    public void initUrl(IComponentConfigBean applicationConfigBean,ReportRequest rrequest)
    {
        ReportBean reportbean=(ReportBean)applicationConfigBean;
        super.initUrl(reportbean,rrequest);
        String colFilterId=rrequest.getStringAttribute(reportbean.getId()+"_COL_FILTERID","");
        if(!colFilterId.equals(""))
        {
            AbsListReportDisplayBean alrdbean=(AbsListReportDisplayBean)reportbean.getDbean().getExtendConfigDataForReportType(KEY);
            AbsListReportFilterBean filterbean=alrdbean.getFilterBeanById(colFilterId);
            if(filterbean!=null&&!filterbean.isConditionRelate())
            {
                String filterVal=rrequest.getStringAttribute(colFilterId,"");
                if(!filterVal.trim().equals(""))
                {
                    rrequest.addParamToUrl(colFilterId,filterVal,true);
                    rrequest.addParamToUrl(reportbean.getId()+"_COL_FILTERID",colFilterId,true);
                }
            }
        }
        String reportid=reportbean.getId();
        rrequest.addParamToUrl(reportid+"ORDERBY","rrequest{"+reportid+"ORDERBY}",true);

        rrequest.addParamToUrl(reportid+"_DYNCOLUMNORDER","rrequest{"+reportid+"_DYNCOLUMNORDER}",true);
    }

    public void initReportBeforeDoStart()
    {
        super.initReportBeforeDoStart();
        rrequest.setFilterCondition(rbean);
        String orderby=null;
        String orderbyAction=rrequest.getStringAttribute(rbean.getId()+"ORDERBY_ACTION","");
        if(orderbyAction.equals("true"))
        {
            orderby=rrequest.getStringAttribute(rbean.getId()+"ORDERBY","");
            if(rbean.getPersonalizeObj()!=null)
            {
                rbean.getPersonalizeObj().storeOrderByCol(rrequest,rbean,orderby);
            }
        }else
        {
            if(rbean.getPersonalizeObj()!=null)
            {
                orderby=rbean.getPersonalizeObj().loadOrderByCol(rrequest,rbean);
            }
            if(orderby==null||orderby.trim().equals(""))
            {
                orderby=rrequest.getStringAttribute(rbean.getId()+"ORDERBY","");
            }
        }
        if(!orderby.equals(""))
        {
            List<String> lstTemp=Tools.parseStringToList(orderby,"||");
            if(lstTemp==null||lstTemp.size()!=2)
            {
                log.error("URL中传入的排序字段"+orderby+"不合法，必须为字段名+||+(asc|desc)格式");
            }else
            {
                String[] str=new String[2];
                str[0]=lstTemp.get(0);
                str[1]=lstTemp.get(1);
                if(str[0]==null) str[0]="";
                if(str[1]==null||str[1].trim().equals("")||(!str[1].equalsIgnoreCase("desc")&&!str[1].equalsIgnoreCase("asc")))
                {
                    str[1]="asc";
                }
                rrequest.setAttribute(rbean.getId(),"ORDERBYARRAY",str);
            }
        }
        CacheDataBean cdb=rrequest.getCdb(rbean.getId());
        List<String> lstColIds=null;
        String dyncolumnorder=null;
        if(rbean.getPersonalizeObj()!=null)
        {
            dyncolumnorder=rbean.getPersonalizeObj().loadColOrderData(rrequest,rbean);
        }
        if(dyncolumnorder==null||dyncolumnorder.trim().equals(""))
        {
            dyncolumnorder=rrequest.getStringAttribute(rbean.getId()+"_DYNCOLUMNORDER","");
        }
        if(!dyncolumnorder.equals(""))
        {
            lstColIds=Tools.parseStringToList(dyncolumnorder,";");
        }
        String dragcols=rrequest.getStringAttribute(rbean.getId()+"_DRAGCOLS","");
        if(!dragcols.equals(""))
        {
            if(lstColIds==null)
            {
                lstColIds=new ArrayList<String>();
                for(ColBean cbTmp:rbean.getDbean().getLstCols())
                {
                    lstColIds.add(cbTmp.getColid());
                }
            }
            lstColIds=processDragCols(dragcols,lstColIds);
        }
        if(lstColIds!=null&&lstColIds.size()>0)
        {
            List<ColBean> lstColBeansDyn=new ArrayList<ColBean>();
            ColBean cbTmp;
            StringBuffer dynColOrderBuf=new StringBuffer();
            for(String colidTmp:lstColIds)
            {
                cbTmp=rbean.getDbean().getColBeanByColId(colidTmp);
                if(cbTmp==null)
                {
                    throw new WabacusRuntimeException("在报表"+rbean.getPath()+"中没有取到colid为"+colidTmp+"的ColBean对象");
                }
                dynColOrderBuf.append(colidTmp).append(";");
                lstColBeansDyn.add(cbTmp);
            }
            cdb.setLstDynOrderColBeans(lstColBeansDyn);
            if(!dragcols.equals(""))
            {
                rrequest.addParamToUrl(rbean.getId()+"_DYNCOLUMNORDER",dynColOrderBuf.toString(),true);
                if(rbean.getPersonalizeObj()!=null) rbean.getPersonalizeObj().storeColOrderData(rrequest,rbean,dynColOrderBuf.toString());
            }
        }
    }

    private List<String> processDragCols(String dragcols,List<String> lstColIds)
    {
        String dragdirect=rrequest.getStringAttribute(rbean.getId()+"_DRAGDIRECT","1");
        if(!dragdirect.equals("1")&&!dragdirect.equals("-1")) dragdirect="1";
        String[] dragcolsArr=dragcols.split(";");
        if(dragcolsArr==null||dragcolsArr.length!=2)
        {
            log.warn("传入的移动列数据不合法，移动报表列失败");
            return lstColIds;
        }
        List<String> lstFromColids=new ArrayList<String>();
        UltraListReportDisplayBean ulrdbean=(UltraListReportDisplayBean)rbean.getDbean().getExtendConfigDataForReportType(UltraListReportType.KEY);
        if(dragcolsArr[0].indexOf("group_")==0)
        {//被拖动的列是个分组列
            if(ulrdbean==null)
            {
                log.warn("当前报表没有配置列分组，但传入的移动列ID为分组ID，移动报表列失败");
                return lstColIds;
            }
            UltraListReportGroupBean groupBean=ulrdbean.getGroupBeanById(dragcolsArr[0]);
            if(groupBean==null)
            {
                log.warn("没有取到id为"+dragcolsArr[0]+"的列分组，移动报表列失败");
                return lstColIds;
            }
            groupBean.getAllChildColIdsInerit(lstFromColids);
        }else
        {
            lstFromColids.add(dragcolsArr[0]);
        }
        String targetColid=dragcolsArr[1];
        if(targetColid.indexOf("group_")==0)
        {
            UltraListReportGroupBean groupBean=ulrdbean.getGroupBeanById(targetColid);
            if(groupBean==null)
            {
                log.warn("没有取到id为"+targetColid+"的列分组，移动报表列失败");
                return lstColIds;
            }
            if(dragdirect.equals("1"))
            {
                targetColid=groupBean.getLastColId(lstColIds);
            }else
            {
                targetColid=groupBean.getFirstColId(lstColIds);
            }
        }
        List<String> lstColIdsNew=new ArrayList<String>();
        for(String colidTmp:lstColIds)
        {
            if(lstFromColids.contains(colidTmp)) continue;
            if(targetColid.equals(colidTmp))
            {
                if(dragdirect.equals("1"))
                {
                    lstColIdsNew.add(colidTmp);
                    lstColIdsNew.addAll(lstFromColids);
                }else
                {
                    lstColIdsNew.addAll(lstFromColids);
                    lstColIdsNew.add(colidTmp);
                }
            }else
            {
                lstColIdsNew.add(colidTmp);
            }
        }
        return lstColIdsNew;
    }

    protected void initReportAfterDoStart()
    {
        super.initReportAfterDoStart();
        if(rrequest.getSaveSlaveReportIdsSet().contains(rbean.getId()))
        {
            EditableReportAssistant.getInstance().doAllReportsSaveAction(rrequest);
        }
    }

    protected void doLoadReportDataPostAction()
    {
        loadSubDisplayDataForWholeReport();
        super.doLoadReportDataPostAction();
    }

    protected boolean isHiddenCol(ColBean cbean)
    {
        if(Consts.COL_DISPLAYTYPE_HIDDEN.equals(cbean.getDisplaytype())) return true;
        int displaymodeTmp=cacheDataBean.getColDisplayModeAfterAuthorize(cbean);
        return displaymodeTmp<=0;
    }
    
    private void loadSubDisplayDataForWholeReport()
    {
        AbsListReportSubDisplayBean subdisplayBean=this.alrbean.getSubdisplaybean();
        if(subdisplayBean==null) return;
        
        
        //        {//如果当前报表是分页显示，且不存在每页都显示的统计行，且当前不是显示最后一页
        
        
        if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE&&cacheDataBean.getPagesize()<0||rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE
                &&!this.cacheDataBean.isExportPrintPartData())
        {
            if(subdisplayBean.isAllDisplayPerpageDataRows()) return;//如果所有统计行的显示类型都为page，即在分页报表中每页统计
        }
        if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE&&this.cacheDataBean.isExportPrintPartData()||rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE
                &&cacheDataBean.getPagesize()>0)
        {//当前是打印/导出或在页面上显示分页报表的一页数据
            if(cacheDataBean.getFinalPageno()!=cacheDataBean.getPagecount()&&subdisplayBean.isAllDisplayWholeReportDataRows()) return;
        }
        this.subDisplayDataObj=subdisplayBean.getPojoObject();
        loadSubDisplayDataObj(subdisplayBean,this.subDisplayDataObj,"","");
    }

    protected void loadSubDisplayDataObj(AbsListReportSubDisplayBean subdisplayBean,Object subdisplayDataObj,String groupbyClause,
            String rowgroupcolumn)
    {
        List<StatisticItemAndDataSetBean> lstItemAndDataseBeans=subdisplayBean.getLstStatitemAndDatasetBeans();
        if(lstItemAndDataseBeans!=null&&lstItemAndDataseBeans.size()>0)
        {
            for(StatisticItemAndDataSetBean itemAndDatasetBeanTmp:lstItemAndDataseBeans)
            {
                itemAndDatasetBeanTmp.loadStatisticData(this,subdisplayDataObj,groupbyClause);
            }
        }
        try
        {
            Method formatMethod=subdisplayBean.getPojoclass().getMethod("format",new Class[] { ReportRequest.class, ReportBean.class, String.class });
            formatMethod.invoke(subdisplayDataObj,new Object[] { rrequest, rbean, rowgroupcolumn });
        }catch(Exception e)
        {
            throw new WabacusConfigLoadingException("格式化报表"+rbean.getPath()+"的辅助显示数据时调用格式化方法失败",e);
        }
    }

    protected String showMetaDataDisplayStringStart()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(super.showMetaDataDisplayStringStart());
        String rowselecttype=this.alrbean.getRowSelectType();
        if(rowselecttype!=null&&!rowselecttype.trim().equals(""))
        {
            resultBuf.append(" rowselecttype=\"").append(rowselecttype).append("\"");
            resultBuf.append(" isSelectRowCrossPages=\"").append(this.alrbean.isSelectRowCrossPages()).append("\"");
            if(!rowselecttype.trim().equalsIgnoreCase(Consts.ROWSELECT_NONE))
            {
                List<String> lstRowSelectCallBackFuns=this.alrbean.getLstRowSelectCallBackFuncs();
                if(lstRowSelectCallBackFuns!=null&&lstRowSelectCallBackFuns.size()>0)
                {
                    StringBuffer rowSelectMethodBuf=new StringBuffer();
                    rowSelectMethodBuf.append("{rowSelectMethods:[");
                    for(String callbackFunc:lstRowSelectCallBackFuns)
                    {
                        if(callbackFunc!=null&&!callbackFunc.trim().equals(""))
                        {
                            rowSelectMethodBuf.append("{value:").append(callbackFunc).append("},");
                        }
                    }
                    if(rowSelectMethodBuf.charAt(rowSelectMethodBuf.length()-1)==',') rowSelectMethodBuf.deleteCharAt(rowSelectMethodBuf.length()-1);
                    rowSelectMethodBuf.append("]}");
                    resultBuf.append(" rowSelectMethods=\"").append(rowSelectMethodBuf.toString()).append("\"");
                }
            }
        }
        return resultBuf.toString();
    }

    protected String showReportDataWithVerticalScroll()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(showReportData(false));
        resultBuf.append("<div style=\"width:").append(rbean.getWidth()).append(";");
        if(Consts_Private.SCROLLSTYLE_NORMAL.equals(rbean.getScrollstyle()))
        {
            resultBuf.append("max-height:"+rbean.getScrollheight()+";overflow-x:hidden;overflow-y:auto;");
            resultBuf.append("height:expression(this.scrollHeight>parseInt('").append(rbean.getScrollheight()).append("')?'").append(
                    rbean.getScrollheight()).append("':'auto');\"");
        }else if(Consts_Private.SCROLLSTYLE_IMAGE.equals(rbean.getScrollstyle()))
        {
            resultBuf.append("overflow-x:hidden;overflow-y:hidden;\"");
            resultBuf.append("id=\"vscroll_"+rbean.getGuid()+"\"");
        }
        resultBuf.append(">");
        resultBuf.append(showReportData(true));
        resultBuf.append("</div>");
        return resultBuf.toString();
    }

    protected String showScrollStartTag()
    {
        if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE) return "";
        int scrolltype=this.alrbean.getScrollType();
        StringBuffer resultBuf=new StringBuffer();
        if(scrolltype==AbsListReportBean.SCROLLTYPE_ALL)
        {
            return ComponentAssistant.getInstance().showComponentScrollStartPart(rbean,true,true,rbean.getScrollwidth(),rbean.getScrollheight(),
                    rbean.getScrollstyle());
        }else if(scrolltype==AbsListReportBean.SCROLLTYPE_FIXED)
        {
            resultBuf.append("<div style=\"overflow:hidden;");
            if(rbean.getScrollwidth()!=null&&!rbean.getScrollwidth().trim().equals(""))
            {
                resultBuf.append("width:").append(rbean.getScrollwidth()).append(";");
            }
            if(rbean.getScrollheight()!=null&&!rbean.getScrollheight().trim().equals(""))
            {
                resultBuf.append("height:").append(rbean.getScrollheight()).append(";");
            }
            resultBuf.append("\">");

        }else if(scrolltype==AbsListReportBean.SCROLLTYPE_HORIZONTAL)
        {
            resultBuf.append("<div style=\"width:").append(rbean.getScrollwidth()).append(";");
            if(Consts_Private.SCROLLSTYLE_NORMAL.equals(rbean.getScrollstyle()))
            {
                resultBuf.append("overflow-x:auto;overflow-y:hidden;height:expression(this.scrollHeight+15);\"");
            }else if(Consts_Private.SCROLLSTYLE_IMAGE.equals(rbean.getScrollstyle()))
            {
                resultBuf.append("overflow-x:hidden;overflow-y:hidden;\"");
                resultBuf.append(" id=\"hscroll_"+rbean.getGuid()+"\"");
            }
            resultBuf.append(">");
        }
        
        return resultBuf.toString();
    }

    protected String showScrollEndTag()
    {
        if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE) return "";
        int scrolltype=this.alrbean.getScrollType();
        if(scrolltype==AbsListReportBean.SCROLLTYPE_ALL)
        {//显示普通纵横滚动条
            return ComponentAssistant.getInstance().showComponentScrollEndPart(true,true);
        }else if(scrolltype==AbsListReportBean.SCROLLTYPE_FIXED||scrolltype==AbsListReportBean.SCROLLTYPE_HORIZONTAL)
        {
            return "</div>";
        }
        return "";
    }

    public abstract String showReportData(boolean showtype);

    protected String getDefaultNavigateKey()
    {
        return Consts.LISTREPORT_NAVIGATE_DEFAULT;
    }

    
    
    
    
    
    
    
    
    
    //        {//不分页或依赖其它报表的翻页导航栏
    
    
    
    //        {//从数据库中没有取到数据，注意不能用recordcount==0判断，因为在可编辑列表报表中，recordcount为0时可能还要显示一页或多页的添加的行。
    
    //        }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //        String tablewidth=rbean.getDisplayWidth();
    
    
    
    
    
    
    
    
    
    //        rtnStr.append("</td>");
    
    
    
    
    
    //                rtnStr.append("<td align='center' width='1%' nowrap>").append(buttonStr).append(
    //                        "</td>");
    
    
    //        rtnStr.append("<td>&nbsp;</td>");
    
    
    
    
    
    
    //            rtnStr.append("  <select name=\"SELEPAGENUM\"><option>1</option></select>");
    
    
    
    //            {//当前为第一页
    
    
    
    //                                rrequest));
    
    //            {//当前为最后一页
    
    
    
    
    //            {//中间页
    
    
    
    
    
    
    
    
    
    //        rtnStr.append("</td></tr></table>");
    //        return rtnStr.toString();
    

    public List<ColBean> getLstDisplayColBeans()
    {
        List<ColBean> lstColBeans=this.cacheDataBean.getLstDynOrderColBeans();
        if(lstColBeans==null||lstColBeans.size()==0) lstColBeans=rbean.getDbean().getLstCols();
        return lstColBeans;
    }

    //    /**
    
    
    
    //     */
    
    
    
    
    
    
    //            {//拦截器中返回了<tr/>的样式字符串
    
    
    
    
    //    }

    protected String showSubRowDataForWholeReport(int position)
    {
        AbsListReportSubDisplayBean subdisplayBean=this.alrbean.getSubdisplaybean();
        if(subdisplayBean==null) return "";
        List<AbsListReportSubDisplayRowBean> lstSubDisplayRowBeans=subdisplayBean.getLstSubDisplayRowBeans();
        if(lstSubDisplayRowBeans==null||lstSubDisplayRowBeans.size()==0) return "";
        StringBuffer resultBuf=new StringBuffer();
        for(AbsListReportSubDisplayRowBean sRowBeanTmp:lstSubDisplayRowBeans)
        {
            if(sRowBeanTmp.getDisplaytype()==AbsListReportSubDisplayBean.SUBROW_DISPLAYTYPE_PAGE)
            {
                if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE&&!this.cacheDataBean.isExportPrintPartData()) continue;
                if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE&&cacheDataBean.getPagesize()<0) continue;
            }else if(sRowBeanTmp.getDisplaytype()==AbsListReportSubDisplayBean.SUBROW_DISPLAYTYPE_REPORT)
            {
                if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE&&this.cacheDataBean.isExportPrintPartData()
                        ||rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE&&cacheDataBean.getPagesize()>0)
                {
                    if(cacheDataBean.getFinalPageno()!=cacheDataBean.getPagecount()) continue;
                }
            }
            if(sRowBeanTmp.getDisplayposition()!=AbsListReportSubDisplayBean.SUBROW_POSITION_BOTH&&sRowBeanTmp.getDisplayposition()!=position)
                continue;
            resultBuf.append(showOneSubRowData(sRowBeanTmp));
        }
        return resultBuf.toString();
    }

    private String showOneSubRowData(AbsListReportSubDisplayRowBean sRowBean)
    {
        List<AbsListReportSubDisplayColBean> lstStatiColBeans=sRowBean.getLstSubColBeans();
        if(lstStatiColBeans==null||lstStatiColBeans.size()==0) return "";
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("<tr  class='cls-data-tr'>");
        ColDisplayData colDisplayData;
        String stativalue=null;
        if(rbean.getDbean().isColselect()
                ||lstStatiColBeans.size()==1
                ||(cacheDataBean.getAttributes().get("authroize_col_display")!=null&&String.valueOf(
                        cacheDataBean.getAttributes().get("authroize_col_display")).trim().equals("false"))
                ||(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE&&alrbean.hasControllCol()))
        {
            resultBuf.append("<td class='cls-data-td-list' ");
            int colspan=cacheDataBean.getTotalColCount();
            if(colspan<=0) return "";
            resultBuf.append(" colspan='").append(colspan).append("' ");
            StringBuffer statiContentBuf=new StringBuffer();
            String dyntdstyleproperty=null;
            for(AbsListReportSubDisplayColBean scbean:lstStatiColBeans)
            {
                if(!rrequest.checkPermission(rbean.getId(),Consts.DATA_PART,scbean.getProperty(),Consts.PERMISSION_TYPE_DISPLAY)) continue;
                stativalue=getSubColDisplayValue(this.subDisplayDataObj,scbean);
                colDisplayData=ColDisplayData.getColDataFromInterceptor(this,scbean,null,0,scbean.getValuestyleproperty(rrequest,false),stativalue);
                statiContentBuf.append(colDisplayData.getValue()).append("&nbsp;&nbsp;");
                if(dyntdstyleproperty==null)
                {
                    dyntdstyleproperty=Tools.removePropertyValueByName("colspan",colDisplayData.getStyleproperty());
                }
            }
            stativalue=statiContentBuf.toString().trim();
            if(stativalue.endsWith("&nbsp;&nbsp;")) stativalue=stativalue.substring(0,stativalue.length()-"&nbsp;&nbsp;".length()).trim();
            if(stativalue.equals("")) return "";
            if(dyntdstyleproperty!=null) resultBuf.append(dyntdstyleproperty);
            resultBuf.append(">").append(stativalue).append("</td>");
        }else
        {
            for(AbsListReportSubDisplayColBean scbean:lstStatiColBeans)
            {
                //                if(!rrequest.checkPermission(rbean.getId(),Consts.DATA_PART,scbean.getProperty(),Consts.PERMISSION_TYPE_DISPLAY)) continue;//当前统计项没有显示权限
                stativalue=getSubColDisplayValue(this.subDisplayDataObj,scbean);
                colDisplayData=ColDisplayData.getColDataFromInterceptor(this,scbean,null,0,scbean.getValuestyleproperty(rrequest,false),stativalue);
                resultBuf.append("<td class='cls-data-td-list' ");
                resultBuf.append(colDisplayData.getStyleproperty());
                resultBuf.append(">").append(colDisplayData.getValue()).append("</td>");
            }
        }
        resultBuf.append("</tr>");
        return resultBuf.toString();
    }

    protected String getSubColDisplayValue(Object statiDataObj,AbsListReportSubDisplayColBean scbean)
    {
        if(statiDataObj==null) return "";
        String stativalue;
        try
        {
            Object objTmp=scbean.getGetMethod().invoke(statiDataObj,new Object[] {});
            if(objTmp==null)
            {
                stativalue="";
            }else
            {
                stativalue=String.valueOf(objTmp);
            }
        }catch(Exception e)
        {
            log.error("获取报表"+rbean.getPath()+"统计数据失败",e);
            stativalue="";
        }
        return stativalue;
    }

    public String showColData(ColBean cbean,int rowidx)
    {
        if(this.lstReportData==null||this.lstReportData.size()==0) return "";
        if(rowidx==-1)
        {
            rowidx=this.lstReportData.size()-1;
        }else if(rowidx==-2)
        {//打印本页记录第一条记录此列的值
            int[] displayrowinfo=this.getDisplayRowInfo();
            if(displayrowinfo[1]<=0) return "";
            rowidx=displayrowinfo[0];
        }
        if(lstReportData.size()<=rowidx) return "";
        AbsReportDataPojo dataObj=this.lstReportData.get(rowidx);
        String strvalue=dataObj.getColStringValue(cbean);
        if(strvalue==null) strvalue="";
        return strvalue;
    }

    public void showReportOnPlainExcel(Workbook workbook)
    {
        if(!rrequest.checkPermission(rbean.getId(),Consts.DATA_PART,null,Consts.PERMISSION_TYPE_DISPLAY)) return;
        if(this.cacheDataBean.getTotalColCount()==0) return;
        createNewSheet(workbook,10);
        if(!this.cacheDataBean.shouldBatchDataExport())
        {
            showReportDataOnPlainExcel(workbook,0);
        }else
        {
            int startNum=0;
            for(int i=0;i<this.cacheDataBean.getPagecount();i++)
            {
                if(i!=0)
                {
                    this.cacheDataBean.setPageno(i+1);
                    this.cacheDataBean.setRefreshNavigateInfoType(1);
                    this.setHasLoadedDataFlag(false);
                    loadReportData(true);
                }
                showReportDataOnPlainExcel(workbook,startNum);
                startNum+=this.cacheDataBean.getPagesize();
            }
        }
    }

    protected void createNewSheet(Workbook workbook,int defaultcolumnwidth)
    {
        super.createNewSheet(workbook,defaultcolumnwidth);
        List<ColBean> lstColBeans=this.getLstDisplayColBeans();
        int i=0;
        for(ColBean cbean:lstColBeans)
        {
            if(cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<=0) continue;
            
            if(cbean.getPlainexcelwidth()>1.0f)
            {
                
                this.excelSheet.setColumnWidth(i,(int)cbean.getPlainexcelwidth());
            }
            i++;
        }
        showReportTitleOnPlainExcel(workbook);
    }

    protected void showReportTitleOnPlainExcel(Workbook workbook)
    {
        String plainexceltitle=null;
        if(this.pedebean!=null) plainexceltitle=this.pedebean.getPlainexceltitle();
        if("none".equals(plainexceltitle)) return;
        List<ColBean> lstColBeans=this.getLstDisplayColBeans();
        CellStyle titleCellStyle=StandardExcelAssistant.getInstance().getTitleCellStyleForStandardExcel(workbook);
        Row dataTitleRow=excelSheet.createRow(excelRowIdx++);
        ColDisplayData colDisplayData;
        int cellidx=0;
        Cell cell;
        String labelTmp;
        for(ColBean cbean:lstColBeans)
        {
            if(cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<=0) continue;
            if("column".equals(plainexceltitle))
            {
                labelTmp=cbean.getColumn();
            }else
            {
                labelTmp=cbean.getLabel(rrequest);
                colDisplayData=ColDisplayData.getColDataFromInterceptor(this,cbean,null,-1,null,labelTmp);
                labelTmp=colDisplayData.getValue();
            }
            cell=dataTitleRow.createCell(cellidx++);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(labelTmp);
            cell.setCellStyle(StandardExcelAssistant.getInstance().setCellAlign(titleCellStyle,cbean.getLabelalign()));
        }
        
        
        
    }

    private void showReportDataOnPlainExcel(Workbook workbook,int startNum)
    {
        boolean hasdata=true;//有数据
        if(lstReportData==null|lstReportData.size()==0)
        {
            hasdata=false;
            if(lstReportData==null) lstReportData=new ArrayList<AbsReportDataPojo>();
            lstReportData.add(ReportAssistant.getInstance().getPojoClassInstance(rrequest,rbean,rbean.getPojoClassObj()));
        }
        List<ColBean> lstColBeans=this.getLstDisplayColBeans();
        CellStyle dataCellStyle=StandardExcelAssistant.getInstance().getDataCellStyleForStandardExcel(workbook);
        CellStyle dataCellStyleWithFormat=StandardExcelAssistant.getInstance().getDataCellStyleForStandardExcel(workbook);
        if(hasdata) showSubRowDataInPlainExcelForWholeReport(workbook,dataCellStyle,AbsListReportSubDisplayBean.SUBROW_POSITION_TOP);
        Cell cell;
        AbsListReportColBean alrcbeanTmp;
        Object objvalueTmp;
        for(AbsReportDataPojo dataObjTmp:lstReportData)
        {
            if(sheetsize>0&&excelRowIdx>=sheetsize)
            {
                createNewSheet(workbook,10);
            }
            Row dataRow=excelSheet.createRow(excelRowIdx++);
            int cellidx=0;
            for(ColBean cbean:lstColBeans)
            {
                if(cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<=0) continue;
                alrcbeanTmp=(AbsListReportColBean)cbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
                cell=dataRow.createCell(cellidx++);
                boolean flag=false;
                if(cbean.isSequenceCol())
                {
                    if(hasdata)
                        cell.setCellValue(startNum+alrcbeanTmp.getSequenceStartNum());
                    else
                        cell.setCellValue("");
                }else if(!cbean.isControlCol())
                {
                    objvalueTmp=dataObjTmp.getColValue(cbean);
                    flag=StandardExcelAssistant.getInstance().setCellValue(workbook,cbean.getValuealign(),cell,objvalueTmp,cbean.getDatatypeObj(),
                            dataCellStyleWithFormat);
                }
                if(!flag) cell.setCellStyle(StandardExcelAssistant.getInstance().setCellAlign(dataCellStyle,cbean.getValuealign()));
            }
            startNum++;
        }
        if(hasdata) showSubRowDataInPlainExcelForWholeReport(workbook,dataCellStyle,AbsListReportSubDisplayBean.SUBROW_POSITION_BOTTOM);
    }

    protected void showSubRowDataInPlainExcelForWholeReport(Workbook workbook,CellStyle dataCellStyle,int position)
    {
        AbsListReportSubDisplayBean subDisplayBean=this.alrbean.getSubdisplaybean();
        if(subDisplayBean==null) return;
        List<AbsListReportSubDisplayRowBean> lstStatiDisplayRowBeans=subDisplayBean.getLstSubDisplayRowBeans();
        if(lstStatiDisplayRowBeans==null||lstStatiDisplayRowBeans.size()==0) return;
        
        

        List<AbsListReportSubDisplayColBean> lstStatiColBeans=null;
        for(AbsListReportSubDisplayRowBean sRowBeanTmp:lstStatiDisplayRowBeans)
        {
            /****if(sRowBeanTmp.getDisplaytype()==AbsListReportStatiBean.STATIROW_DISPLAYTYPE_PAGE)
            {
                if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE&&!this.cacheDataBean.isExportPrintPartData()) continue;
                if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE&&cacheDataBean.getPagesize()<0) continue;
            }else if(sRowBeanTmp.getDisplaytype()==AbsListReportStatiBean.STATIROW_DISPLAYTYPE_REPORT)
            {
                if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE&&this.cacheDataBean.isExportPrintPartData()
                        ||rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE&&cacheDataBean.getPagesize()>0)
                {
                    if(cacheDataBean.getFinalPageno()!=cacheDataBean.getPagecount()) continue;
                }
            }*/
            if(sRowBeanTmp.getDisplayposition()!=AbsListReportSubDisplayBean.SUBROW_POSITION_BOTH&&sRowBeanTmp.getDisplayposition()!=position)
                continue;
            lstStatiColBeans=sRowBeanTmp.getLstSubColBeans();
            if(lstStatiColBeans==null||lstStatiColBeans.size()==0) continue;
            
            String stativalue;
            int startcolidx=0;
            int endcolidx=-1;
            CellRangeAddress region;
            for(AbsListReportSubDisplayColBean scbean:lstStatiColBeans)
            {
                stativalue=getSubColDisplayValue(this.subDisplayDataObj,scbean);
                stativalue=Tools.replaceAll(stativalue,"&nbsp;"," ");
                stativalue=stativalue.replaceAll("<.*?\\>","");//替换掉html标签
                if(rbean.getDbean().isColselect()
                        ||lstStatiColBeans.size()==1
                        ||(cacheDataBean.getAttributes().get("authroize_col_display")!=null&&String.valueOf(
                                cacheDataBean.getAttributes().get("authroize_col_display")).trim().equals("false"))||alrbean.hasControllCol())
                {
                    startcolidx=0;
                    endcolidx=cacheDataBean.getTotalColCount()-1;
                    int deltaCount=0;
                    if(alrdbean.getRowGroupColsNum()>0&&alrdbean.getRowgrouptype()==2)
                    {
                        deltaCount=alrdbean.getRowGroupColsNum()-1;
                    }
                    endcolidx=endcolidx+deltaCount;
                }else
                {
                    startcolidx=endcolidx+1;
                    endcolidx=startcolidx+scbean.getPlainexcel_colspan()-1;
                }
                region=new CellRangeAddress(excelRowIdx,excelRowIdx,startcolidx,endcolidx);
                StandardExcelAssistant.getInstance().setRegionCellStringValue(workbook,excelSheet,region,dataCellStyle,stativalue);
            }
            excelRowIdx++;
        }
    }

    protected void showReportOnPdfWithoutTpl()
    {
        if(!rrequest.checkPermission(rbean.getId(),Consts.DATA_PART,null,Consts.PERMISSION_TYPE_DISPLAY)) return;
        if(this.cacheDataBean.getTotalColCount()==0) return;
        createNewPdfPage();
        showDataHeaderOnPdf();
        if(!this.cacheDataBean.shouldBatchDataExport())
        {
            showReportDataOnPdf();
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
                showReportDataOnPdf();
            }
        }
    }

    private float[] colwidthArr;

    protected void createNewPdfPage()
    {
        super.createNewPdfPage();
        if(this.totalcolcount<=0) return;
        if(colwidthArr==null)
        {
            colwidthArr=new float[this.totalcolcount];
            float totalconfigwidth=0f;
            int nonconfigwidthColcnt=0;
            List<ColBean> lstColBeans=this.getLstDisplayColBeans();
            for(ColBean cbean:lstColBeans)
            {
                if(cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<=0) continue;
                if(cbean.getPdfwidth()>0.1f)
                {
                    totalconfigwidth+=cbean.getPdfwidth();
                }else
                {//没有在<col/>中配置dataexportwidth
                    nonconfigwidthColcnt++;
                }
            }
            float nonconfigcolwidth=0f;//存放没有配置宽度的列的宽度
            if(nonconfigwidthColcnt==0)
            {
                pdfwidth=totalconfigwidth;
                this.pdfDataTable.setTotalWidth(totalconfigwidth);
            }else
            {
                if(pdfwidth<=totalconfigwidth)
                {
                    nonconfigcolwidth=50f;
                    pdfwidth=totalconfigwidth+nonconfigcolwidth*nonconfigwidthColcnt;
                    this.pdfDataTable.setTotalWidth(pdfwidth);
                }else
                {
                    nonconfigcolwidth=(pdfwidth-totalconfigwidth)/nonconfigwidthColcnt;
                }
            }
            int i=0;
            for(ColBean cbean:lstColBeans)
            {
                if(cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<=0) continue;
                if(cbean.getPdfwidth()>0.1f)
                {
                    colwidthArr[i]=cbean.getPdfwidth();
                }else
                {
                    colwidthArr[i]=nonconfigcolwidth;
                }
                i++;
            }
        }
        try
        {
            this.pdfDataTable.setWidths(colwidthArr);
        }catch(DocumentException e)
        {
            throw new WabacusRuntimeException("导出报表"+rbean.getPath()+"的数据到PDF文件失败",e);
        }
    }

    protected void showDataHeaderOnPdf()
    {
        List<ColBean> lstColBeans=this.getLstDisplayColBeans();
        ColDisplayData colDisplayData;
        String labelTmp;
        for(ColBean cbean:lstColBeans)
        {
            if(cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<=0) continue;
            labelTmp=cbean.getLabel(rrequest);
            colDisplayData=ColDisplayData.getColDataFromInterceptor(this,cbean,null,-1,null,labelTmp);
            addDataHeaderCell(cbean,colDisplayData.getValue(),1,1,getPdfCellAlign(cbean.getLabelalign(),Element.ALIGN_CENTER));
        }
    }

    private void showReportDataOnPdf()
    {
        boolean hasData=true;
        if(lstReportData==null|lstReportData.size()==0)
        {
            if(lstReportData==null) lstReportData=new ArrayList<AbsReportDataPojo>();
            lstReportData.add(ReportAssistant.getInstance().getPojoClassInstance(rrequest,rbean,rbean.getPojoClassObj()));
            hasData=false;
        }
        if(hasData) showSubRowDataOnPdfForWholeReport(AbsListReportSubDisplayBean.SUBROW_POSITION_TOP);
        List<ColBean> lstColBeans=this.getLstDisplayColBeans();
        ColDisplayData colDisplayData;
        AbsListReportColBean alrcbeanTmp;
        String valueTmp;
        for(AbsReportDataPojo dataObjTmp:lstReportData)
        {
            if(this.pdfpagesize>0&&this.pdfrowindex!=0&&this.pdfrowindex%this.pdfpagesize==0)
            {
                this.createNewPdfPage();
                if(this.isFullpagesplit) showDataHeaderOnPdf();
            }
            for(ColBean cbean:lstColBeans)
            {
                if(cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<=0) continue;
                valueTmp="";
                if(cbean.isSequenceCol())
                {
                    alrcbeanTmp=(AbsListReportColBean)cbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
                    valueTmp=String.valueOf(this.pdfrowindex+alrcbeanTmp.getSequenceStartNum());
                }else if(!cbean.isControlCol())
                {
                    colDisplayData=ColDisplayData.getColDataFromInterceptor(this,cbean,null,this.pdfrowindex,null,dataObjTmp.getColStringValue(cbean));
                    valueTmp=colDisplayData.getValue();
                }
                addDataCell(cbean,valueTmp,1,1,getPdfCellAlign(cbean.getValuealign(),Element.ALIGN_CENTER));
            }
            this.pdfrowindex++;
        }
        if(hasData) showSubRowDataOnPdfForWholeReport(AbsListReportSubDisplayBean.SUBROW_POSITION_BOTTOM);
    }

    protected int getTotalColCount()
    {
        List<ColBean> lstColBeans=this.getLstDisplayColBeans();
        int cnt=0;
        for(ColBean cbean:lstColBeans)
        {
            if(cacheDataBean.getColDisplayModeAfterAuthorize(cbean)<=0) continue;
            if(cbean.isControlCol()) continue;
            cnt++;
        }
        return cnt;
    }

    protected void showSubRowDataOnPdfForWholeReport(int position)
    {
        AbsListReportSubDisplayBean subDisplayBean=this.alrbean.getSubdisplaybean();
        if(subDisplayBean==null) return;
        List<AbsListReportSubDisplayRowBean> lstStatiDisplayRowBeans=subDisplayBean.getLstSubDisplayRowBeans();
        if(lstStatiDisplayRowBeans==null||lstStatiDisplayRowBeans.size()==0) return;
        
        
        List<AbsListReportSubDisplayColBean> lstStatiColBeans=null;
        for(AbsListReportSubDisplayRowBean sRowBeanTmp:lstStatiDisplayRowBeans)
        {
            /****if(sRowBeanTmp.getDisplaytype()==AbsListReportStatiBean.STATIROW_DISPLAYTYPE_PAGE)
            {
                if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE&&!this.cacheDataBean.isExportPrintPartData()) continue;
                if(rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE&&cacheDataBean.getPagesize()<0) continue;
            }else if(sRowBeanTmp.getDisplaytype()==AbsListReportStatiBean.STATIROW_DISPLAYTYPE_REPORT)
            {
                if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE&&this.cacheDataBean.isExportPrintPartData()
                        ||rrequest.getShowtype()==Consts.DISPLAY_ON_PAGE&&cacheDataBean.getPagesize()>0)
                {//当前是打印或导出或在页面显示一页数据
                    if(cacheDataBean.getFinalPageno()!=cacheDataBean.getPagecount()) continue;
                }
            }*/
            if(sRowBeanTmp.getDisplayposition()!=AbsListReportSubDisplayBean.SUBROW_POSITION_BOTH&&sRowBeanTmp.getDisplayposition()!=position)
                continue;
            lstStatiColBeans=sRowBeanTmp.getLstSubColBeans();
            if(lstStatiColBeans==null||lstStatiColBeans.size()==0) continue;
            
            String stativalue;
            int startcolidx=0;
            int endcolidx=-1;
            for(AbsListReportSubDisplayColBean scbean:lstStatiColBeans)
            {
                stativalue=getSubColDisplayValue(this.subDisplayDataObj,scbean);
                stativalue=Tools.replaceAll(stativalue,"&nbsp;"," ");
                stativalue=stativalue.replaceAll("<.*?\\>","");
                if(rbean.getDbean().isColselect()
                        ||lstStatiColBeans.size()==1
                        ||(cacheDataBean.getAttributes().get("authroize_col_display")!=null&&String.valueOf(
                                cacheDataBean.getAttributes().get("authroize_col_display")).trim().equals("false"))||alrbean.hasControllCol())
                {
                    startcolidx=0;
                    endcolidx=cacheDataBean.getTotalColCount();
                    int deltaCount=0;
                    if(alrdbean.getRowGroupColsNum()>0&&alrdbean.getRowgrouptype()==2)
                    {
                        deltaCount=alrdbean.getRowGroupColsNum()-1;
                    }
                    endcolidx=endcolidx+deltaCount;
                }else
                {
                    startcolidx=endcolidx+1;
                    endcolidx=startcolidx+scbean.getPlainexcel_colspan();
                }
                addDataCell(scbean,stativalue,1,endcolidx-startcolidx,Element.ALIGN_LEFT);
            }
            
        }
    }

    public int beforeReportLoading(ReportBean reportbean,List<XmlElementBean> lstEleReportBeans)
    {
        XmlElementBean eleReportBean=lstEleReportBeans.get(0);
        AbsListReportBean alrbean=(AbsListReportBean)reportbean.getExtendConfigDataForReportType(KEY);
        if(alrbean==null)
        {
            alrbean=new AbsListReportBean(reportbean);
            reportbean.setExtendConfigDataForReportType(KEY,alrbean);
        }
        String fixedcols=eleReportBean.attributeValue("fixedcols");
        if(fixedcols!=null)
        {
            fixedcols=fixedcols.trim();
            if(fixedcols.equals(""))
            {
                alrbean.setFixedcols(null,0);
            }else
            {
                try
                {
                    alrbean.setFixedcols(null,Integer.parseInt(fixedcols));
                }catch(NumberFormatException e)
                {
                    log.warn("报表"+reportbean.getPath()+"的<report/>标签上fixedcols属性配置的值"+fixedcols+"为无效数字，"+e.toString());
                    alrbean.setFixedcols(null,0);
                }
            }
        }
        if(alrbean.getFixedcols(null)<0) alrbean.setFixedcols(null,0);
        String fixedrows=eleReportBean.attributeValue("fixedrows");
        if(fixedrows!=null)
        {
            fixedrows=fixedrows.trim();
            if(fixedrows.equals(""))
            {
                alrbean.setFixedrows(0);
            }else if(fixedrows.toLowerCase().equals("title"))
            {
                alrbean.setFixedrows(Integer.MAX_VALUE);
            }else
            {
                try
                {
                    alrbean.setFixedrows(Integer.parseInt(fixedrows));
                }catch(NumberFormatException e)
                {
                    log.warn("报表"+reportbean.getPath()+"的<report/>标签上fixedrows属性配置的值"+fixedrows+"为无效数字，"+e.toString());
                    alrbean.setFixedrows(0);
                }
            }
        }
        if(alrbean.getFixedrows()<0) alrbean.setFixedrows(0);
        String rowselect=eleReportBean.attributeValue("rowselect");
        if(rowselect!=null)
        {
            rowselect=rowselect.toLowerCase().trim();
            boolean isSelectRowCrossPages=false;
            int idx=rowselect.indexOf("|");
            if(idx>0)
            {
                isSelectRowCrossPages=rowselect.substring(idx+1).trim().equals("true");
                rowselect=rowselect.substring(0,idx).trim();
            }
            if(rowselect.equals(""))
            {
                alrbean.setRowSelectType(null);
            }else
            {
                if(!Consts.lstAllRowSelectTypes.contains(rowselect))
                {
                    throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，配置的rowselect属性"+rowselect+"不合法");
                }
                alrbean.setRowSelectType(rowselect);
                alrbean.setSelectRowCrossPages(isSelectRowCrossPages);
            }
        }
        String rowselectcallback=eleReportBean.attributeValue("selectcallback");
        if(rowselectcallback!=null)
        {
            rowselectcallback=rowselectcallback.trim();
            if(rowselectcallback.equals(""))
            {
                alrbean.setLstRowSelectCallBackFuncs(null);
            }else
            {
                List<String> lstTemp=Tools.parseStringToList(rowselectcallback,";");
                for(String strfun:lstTemp)
                {
                    if(strfun==null||strfun.trim().equals("")) continue;
                    alrbean.addRowSelectCallBackFunc(strfun.trim());
                }
            }
        }
        String rowordertype=eleReportBean.attributeValue("rowordertype");
        if(rowordertype!=null)
        {
            rowordertype=rowordertype.toLowerCase().trim();
            if(rowordertype.equals(""))
            {
                alrbean.setLoadStoreRoworderObject(null);
                alrbean.setLstRoworderTypes(null);
            }else
            {
                List<String> lstRoworderTypes=new ArrayList<String>();
                List<String> lstTmp=Tools.parseStringToList(rowordertype,"|");
                for(String roworderTmp:lstTmp)
                {
                    if(roworderTmp==null||roworderTmp.trim().equals("")) continue;
                    roworderTmp=roworderTmp.trim();
                    if(!Consts.lstAllRoworderTypes.contains(roworderTmp))
                    {
                        throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，配置的rowordertype属性"+roworderTmp+"不支持");
                    }
                    if(!lstRoworderTypes.contains(roworderTmp)) lstRoworderTypes.add(roworderTmp);
                }
                if(lstRoworderTypes.size()==0)
                {
                    alrbean.setLoadStoreRoworderObject(null);
                    alrbean.setLstRoworderTypes(null);
                }else
                {
                    alrbean.setLstRoworderTypes(lstRoworderTypes);
                    String roworderclass=eleReportBean.attributeValue("roworderclass");
                    if(roworderclass!=null)
                    {
                        roworderclass=roworderclass.trim();
                        if(roworderclass.equals(""))
                        {
                            alrbean.setLoadStoreRoworderObject(null);
                        }else
                        {
                            Object obj=null;
                            try
                            {
                                obj=ConfigLoadManager.currentDynClassLoader.loadClassByCurrentLoader(roworderclass).newInstance();
                            }catch(Exception e)
                            {
                                throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，无法实例化"+roworderclass+"类对象",e);
                            }
                            if(!(obj instanceof IListReportRoworderPersistence))
                            {
                                throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，roworderclass属性配置的类"+roworderclass+"没有实现"
                                        +IListReportRoworderPersistence.class.getName()+"接口");
                            }
                            alrbean.setLoadStoreRoworderObject((IListReportRoworderPersistence)obj);
                        }
                    }
                    if(alrbean.getLoadStoreRoworderObject()==null&&Config.default_roworder_object==null)
                    {
                        throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()
                                +"失败，没有在wabacus.cfg.xml通过default-roworderclass配置项配置全局默认处理行排序的类，因此必须在其<report/>中配置roworderclass属性指定处理本报表行排序的类");

                    }
                }
            }
        }
        return 1;
    }

    public int afterColLoading(ColBean colbean,List<XmlElementBean> lstEleColBeans)
    {
        XmlElementBean eleColBean=lstEleColBeans.get(0);
        AbsListReportDisplayBean alrdbean=(AbsListReportDisplayBean)colbean.getParent().getExtendConfigDataForReportType(KEY);
        if(alrdbean==null)
        {
            alrdbean=new AbsListReportDisplayBean(colbean.getParent());
            colbean.getParent().setExtendConfigDataForReportType(KEY,alrdbean);
        }
        AbsListReportColBean alrcbean=(AbsListReportColBean)colbean.getExtendConfigDataForReportType(KEY);
        if(alrcbean==null)
        {
            alrcbean=new AbsListReportColBean(colbean);
            colbean.setExtendConfigDataForReportType(KEY,alrcbean);
        }
        ReportBean reportbean=colbean.getReportBean();
        String column=colbean.getColumn().trim();
        if(colbean.isNonValueCol())
        {
            throw new WabacusConfigLoadingException("报表"+reportbean.getPath()+"为数据自动列表报表，不允许<col/>标签的column属性配置为"+Consts_Private.NON_VALUE);
        }
        if(colbean.isSequenceCol())
        {
            String sequence=column.substring(1,column.length()-1);
            int start=1;
            int idx=sequence.indexOf(":");
            if(idx>0)
            {
                sequence=sequence.substring(idx+1);
                try
                {
                    if(!sequence.trim().equals("")) start=Integer.parseInt(sequence);
                }catch(NumberFormatException e)
                {
                    log.warn("报表"+reportbean.getPath()+"配置的序号列"+colbean.getColumn()+"中的起始序号不是合法数字",e);
                    start=1;
                }
            }
            alrcbean.setSequenceStartNum(start);
        }
        if(!colbean.isSequenceCol()&&!colbean.isControlCol()&&(colbean.getProperty()==null||colbean.getProperty().trim().equals("")))
        {
            throw new WabacusConfigLoadingException("报表"+reportbean.getPath()+"为数据自动列表报表，不允许<col/>标签的property属性为空");
        }
        String width=eleColBean.attributeValue("width");
        String align=eleColBean.attributeValue("align");//兼容老报表的配置，默认为center
        String clickorderby=eleColBean.attributeValue("clickorderby");
        String filter=eleColBean.attributeValue("filter");

        String rowselectvalue=eleColBean.attributeValue("rowselectvalue");//当前<col/>是否需要在行选中的javascript回调函数中使用，如果设置为true，则在显示当前<col/>时，会在<td/>中显示一个名为value属性，值为当前列的值
        String rowgroup=eleColBean.attributeValue("rowgroup");
        String treerowgroup=eleColBean.attributeValue("treerowgroup");

        if(filter!=null)
        {
            filter=filter.trim();
            if(filter.equals("")||filter.equalsIgnoreCase("false"))
            {
                alrcbean.setFilterBean(null);
            }else
            {
                if(colbean.isSequenceCol()||colbean.isNonFromDbCol()||colbean.isControlCol())
                {
                    throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，不能在column为非数据的<col/>中配置过滤列，即不能配置filter属性");
                }
                AbsListReportFilterBean filterBean=new AbsListReportFilterBean(colbean);
                if(Tools.isDefineKey("condition",filter))
                {
                    filterBean.setConditionname(Tools.getRealKeyByDefine("condition",filter).trim());
                }else if(!filter.toLowerCase().equals("true"))
                {
                    filterBean.setFilterColumnExpression(filter);
                }
                String filterwidth=eleColBean.attributeValue("filterwidth");
                if(filterwidth!=null) filterBean.setFilterwidth(Tools.getWidthHeightIntValue(filterwidth.trim()));
                String filtermaxheight=eleColBean.attributeValue("filtermaxheight");
                if(filtermaxheight!=null) filterBean.setFiltermaxheight(Tools.getWidthHeightIntValue(filtermaxheight.trim()));
                String filterformat=eleColBean.attributeValue("filterformat");
                if(filterformat!=null&&!filterformat.trim().equals(""))
                {
                    filterformat=filterformat.trim();
                    filterBean.setFilterformat(filterformat);
                    filterBean.setFormatClass(reportbean.getFormatMethodClass(filterformat,new Class[] { ReportBean.class, String[].class }));
                    try
                    {
                        filterBean.setFormatMethod(filterBean.getFormatClass().getMethod(filterformat,
                                new Class[] { ReportBean.class, String[].class }));
                    }catch(Exception e)
                    {
                        throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，无法得到"+filterformat+"格式化方法对象",e);
                    }
                }
                alrcbean.setFilterBean(filterBean);
            }
        }

        if(width!=null&&!width.trim().equals(""))
        {
            if(Tools.getPropertyValueByName("width",colbean.getValuestyleproperty(null,true),true)==null)
                colbean.setValuestyleproperty(colbean.getValuestyleproperty(null,true)+" width='"+width+"' ",true);
            if(Tools.getPropertyValueByName("width",colbean.getLabelstyleproperty(null,true),true)==null)
                colbean.setLabelstyleproperty(colbean.getLabelstyleproperty(null,true)+" width='"+width+"' ",true);
        }
        if(colbean.getValuealign()==null||colbean.getValuealign().trim().equals(""))
        {//如果没有在valuestyleproperty中指定align，则使用<col/>中配置的align属性
            if(align==null)
            {//在<col/>中没有配置align
                if(treerowgroup!=null&&treerowgroup.trim().equals("true"))
                {
                    align="left";
                }else
                {
                    align="center";
                }
            }
            colbean.setValuestyleproperty(colbean.getValuestyleproperty(null,true)+" align='"+align+"' ",true);
            colbean.setValuealign(align);
        }
        if(colbean.getPrintlabelstyleproperty(null,false)==null||colbean.getPrintlabelstyleproperty(null,false).trim().equals(""))
        {
            colbean.setPrintlabelstyleproperty(colbean.getLabelstyleproperty(null,false),false);
        }
        if(colbean.getPrintvaluestyleproperty(null,false)==null||colbean.getPrintvaluestyleproperty(null,false).trim().equals(""))
        {
            colbean.setPrintvaluestyleproperty(colbean.getValuestyleproperty(null,false),false);
        }
        String printwidth=colbean.getPrintwidth();
        if(printwidth!=null&&!printwidth.trim().equals(""))
        {
            String printlabelstyleproperty=colbean.getPrintlabelstyleproperty(null,true);
            if(printlabelstyleproperty==null) printlabelstyleproperty="";
            printlabelstyleproperty=Tools.removePropertyValueByName("width",printlabelstyleproperty);
            printlabelstyleproperty=printlabelstyleproperty+" width=\""+printwidth+"\"";
            colbean.setPrintlabelstyleproperty(printlabelstyleproperty,true);
            String printvaluestyleproperty=colbean.getPrintvaluestyleproperty(null,true);
            if(printvaluestyleproperty==null) printvaluestyleproperty="";
            printvaluestyleproperty=Tools.removePropertyValueByName("width",printvaluestyleproperty);
            printvaluestyleproperty=printvaluestyleproperty+" width=\""+printwidth+"\"";
            colbean.setPrintvaluestyleproperty(printvaluestyleproperty,true);
        }
        if(clickorderby!=null)
        {
            clickorderby=clickorderby.toLowerCase().trim();
            if(!clickorderby.equals("true")&&!clickorderby.equals("false"))
            {
                throw new WabacusConfigLoadingException("加载报表"+colbean.getReportBean().getPath()+"失败，column为"+colbean.getColumn()
                        +"的<col/>的clickorderby属性配置不合法，必须配置为true或false");
            }
            if(clickorderby.equals("true"))
            {
                if(colbean.getColumn()==null||colbean.getColumn().trim().equals(""))
                {
                    throw new WabacusConfigLoadingException("加载报表"+colbean.getReportBean().getPath()
                            +"失败，存在在没有配置column属性的<col/>中配置clickorderby为true的情况");
                }
                if(colbean.isSequenceCol()||colbean.isNonFromDbCol()||colbean.isControlCol())
                {
                    throw new WabacusConfigLoadingException("加载报表"+colbean.getReportBean().getPath()+"失败，不能在非数据列的<col/>中配置排序功能");
                }
                alrcbean.setRequireClickOrderby(true);
            }else
            {
                alrcbean.setRequireClickOrderby(false);
            }
        }
        if(rowselectvalue!=null)
        {
            rowselectvalue=rowselectvalue.trim();
            if(rowselectvalue.equalsIgnoreCase("true"))
            {
                alrcbean.setRowSelectValue(true);
            }else
            {
                alrcbean.setRowSelectValue(false);
            }
        }
        if(rowgroup!=null&&treerowgroup!=null)
        {
            throw new WabacusConfigLoadingException("加载报表"+colbean.getReportBean().getPath()+"失败，不能在同一个<col/>中同时配置treerowgroup和rowgroup属性");
        }else if(rowgroup!=null)
        {//加载参与普通行分组的<col/>的信息
            rowgroup=rowgroup.toLowerCase().trim();
            if(!rowgroup.equals("true")&&!rowgroup.equals("false"))
            {
                throw new WabacusConfigLoadingException("加载报表"+colbean.getReportBean().getPath()+"失败，参与分组的<col/>的rowgroup属性配置值不合法，只能配置true或false");
            }
            alrcbean.setRowgroup(Boolean.parseBoolean(rowgroup));
            if(alrcbean.isRowgroup())
            {
                if(colbean.isSequenceCol()||colbean.isNonFromDbCol()||colbean.isControlCol())
                {
                    throw new WabacusConfigLoadingException("加载报表"+colbean.getReportBean().getPath()
                            +"失败，不能将显示sequence或不从数据库某字段获取数据(即column为sequence或non-fromdb)的<col/>配置rowgroup为true");
                }
                if(alrdbean.getRowgrouptype()==2)
                {
                    throw new WabacusConfigLoadingException("加载报表"+colbean.getReportBean().getPath()+"失败，不能为同一个报表同时配置普通分组和行分组功能");
                }
                alrdbean.setRowgroupDatasetId(colbean.getDatasetValueId());
                colbean.setDisplaytype(Consts.COL_DISPLAYTYPE_ALWAYS);
                alrdbean.addRowgroupCol(colbean);
                alrdbean.setRowgrouptype(1);
            }
        }else if(treerowgroup!=null)
        {//加载参与树形行分组的<col/>的信息
            treerowgroup=treerowgroup.toLowerCase().trim();
            if(!treerowgroup.equals("true")&&!treerowgroup.equals("false"))
            {
                throw new WabacusConfigLoadingException("加载报表"+colbean.getReportBean().getPath()+"失败，参与分组的<col/>的treerowgroup属性配置值不合法，只能配置true或false");
            }
            alrcbean.setRowgroup(Boolean.parseBoolean(treerowgroup));
            if(alrcbean.isRowgroup())
            {
                if(colbean.isSequenceCol()||colbean.isNonFromDbCol()||colbean.isControlCol())
                {
                    throw new WabacusConfigLoadingException("加载报表"+colbean.getReportBean().getPath()
                            +"失败，不能将显示sequence或不从数据库某字段获取数据(即column为sequence或non-fromdb)的<col/>配置treerowgroup为true");
                }
                if(alrdbean.getRowgrouptype()==1)
                {
                    throw new WabacusConfigLoadingException("加载报表"+colbean.getReportBean().getPath()+"失败，不能为同一个报表同时配置普通分组和行分组功能");
                }
                alrdbean.setRowgroupDatasetId(colbean.getDatasetValueId());
                colbean.setDisplaytype(Consts.COL_DISPLAYTYPE_ALWAYS);//参与树形分组的列永远显示
                alrdbean.addRowgroupCol(colbean);
                alrdbean.setRowgrouptype(2);
            }
        }
        if((alrdbean.getRowgrouptype()==1||alrdbean.getRowgrouptype()==2))
        {
            if(alrdbean.getTreenodeid()!=null&&!alrdbean.getTreenodeid().trim().equals(""))
            {
                throw new WabacusConfigLoadingException("加载报表"+colbean.getReportBean().getPath()+"失败，此报表已经配置为不限层级的树形分组报表，不能再配置普通行分组或树形行分组功能");
            }
            if(!alrcbean.isRowgroup()) alrdbean.addRowgroupCol(null);//当前列不参与行/树形分组，则后面的列都不能再参与行分组/树形分组，因为参与行分组/树形分组的列必须配置在最前面
        }
        String rowordervalue=eleColBean.attributeValue("rowordervalue");
        if(rowordervalue!=null)
        {
            alrcbean.setRoworderValue(rowordervalue.trim().toLowerCase().equals("true"));
        }
        if(colbean.isRoworderInputboxCol())
        {
            String inputboxstyleproperty=eleColBean.attributeValue("inputboxstyleproperty");
            if(inputboxstyleproperty!=null)
            {
                alrcbean.setRoworder_inputboxstyleproperty(inputboxstyleproperty.trim());
            }
        }
        
        //        {//只有hidden为0的列才能参与折线标题列
        //            String curvelabelup=eleCol.attributeValue("curvelabelup");//折线标题列的上部标题
        //            String curvelabeldown=eleCol.attributeValue("curvelabeldown");//折线标题列的上部标题
        //            String curvelabel=eleCol.attributeValue("curvelabel");//是否参与了折线标题
        //            String curvecolor=eleCol.attributeValue("curvecolor");//是否参与了折线标题
        
        
        
        
        
        
        
        
        
        return 1;
    }

    public int beforeDisplayLoading(DisplayBean disbean,List<XmlElementBean> lstEleDisplayBeans)
    {
        super.beforeDisplayLoading(disbean,lstEleDisplayBeans);
        AbsListReportDisplayBean alrdbean=(AbsListReportDisplayBean)disbean.getExtendConfigDataForReportType(KEY);
        if(alrdbean==null)
        {
            alrdbean=new AbsListReportDisplayBean(disbean);
            disbean.setExtendConfigDataForReportType(KEY,alrdbean);
        }
        alrdbean.setRowgroupDatasetId(null);
        Map<String,String> mDisplayProperties=ConfigLoadAssistant.getInstance().assembleAllAttributes(lstEleDisplayBeans,
                new String[] { "treenodeid", "treenodename", "treenodeparentid" });
        String treenodeid=mDisplayProperties.get("treenodeid");
        String treenodename=mDisplayProperties.get("treenodename");
        String treenodeparentid=mDisplayProperties.get("treenodeparentid");
        if(treenodeid!=null) alrdbean.setTreenodeid(treenodeid.trim());
        if(treenodename!=null) alrdbean.setTreenodename(treenodename.trim());
        if(treenodeparentid!=null) alrdbean.setTreenodeparentid(treenodeparentid.trim());
        return 1;
    }

    public int afterDisplayLoading(DisplayBean disbean,List<XmlElementBean> lstEleDisplayBeans)
    {
        Map<String,String> mDisplayProperties=ConfigLoadAssistant.getInstance().assembleAllAttributes(
                lstEleDisplayBeans,
                new String[] { "treeborder", "treecloseable", "treexpandlayer", "treeasyn", "treexpandimg", "treeclosedimg", "treeleafimg",
                        "mouseoverbgcolor" });
        String treeborder=mDisplayProperties.get("treeborder");
        String treecloseable=mDisplayProperties.get("treecloseable");
        //        String treecheckbox=eleDisplay.attributeValue("treecheckbox");//是否需要为树形行分组树枝节点显示复选框
        String treexpandlayer=mDisplayProperties.get("treexpandlayer");//树形分组初始展开层数
        String treeasyn=mDisplayProperties.get("treeasyn");
        String treexpandimg=mDisplayProperties.get("treexpandimg");
        String treeclosedimg=mDisplayProperties.get("treeclosedimg");
        String treeleafimg=mDisplayProperties.get("treeleafimg");
        String mouseoverbgcolor=mDisplayProperties.get("mouseoverbgcolor");
        AbsListReportDisplayBean alrdbean=(AbsListReportDisplayBean)disbean.getExtendConfigDataForReportType(KEY);
        if(mouseoverbgcolor==null)
        {
            if(alrdbean.getMouseoverbgcolor()==null)
            {
                alrdbean.setMouseoverbgcolor(Config.getInstance().getSystemConfigValue("default-mouseoverbgcolor",""));
            }
        }else
        {
            alrdbean.setMouseoverbgcolor(mouseoverbgcolor.trim());
        }
        if(treeborder!=null)
        {
            treeborder=treeborder.trim();
            if(treeborder.equals("")) treeborder="2";
            if(!treeborder.equals("0")&&!treeborder.equals("1")&&!treeborder.equals("2")&&!treeborder.equals("3"))
            {
                throw new WabacusConfigLoadingException("加载报表"+disbean.getReportBean().getPath()+"失败，<display>的treeborder属性只能配置为0、1、2、3");
            }
            alrdbean.setTreeborder(Integer.parseInt(treeborder));
        }
        if(treecloseable!=null)
        {
            treecloseable=treecloseable.toLowerCase().trim();
            if(treecloseable.equals("")) treecloseable="true";
            if(!treecloseable.equals("true")&&!treecloseable.equals("false"))
            {
                throw new WabacusConfigLoadingException("加载报表"+disbean.getReportBean().getPath()+"失败，<display>的treecloseable属性只能配置为true或false");
            }
            alrdbean.setTreecloseable(Boolean.parseBoolean(treecloseable));
        }
        if(treexpandlayer!=null)
        {
            treexpandlayer=treexpandlayer.trim();
            if(treexpandlayer.equals("")) treexpandlayer="-1";
            alrdbean.setTreexpandlayer(Integer.parseInt(treexpandlayer));
        }
        if(!alrdbean.isTreecloseable()) alrdbean.setTreexpandlayer(-1);
        if(treeasyn!=null)
        {
            alrdbean.setTreeAsynLoad(treeasyn.toLowerCase().trim().equals("true"));
        }
        if(treexpandimg!=null)
        {
            treexpandimg=treexpandimg.trim();
            if(treexpandimg.equals(""))
            {
                alrdbean.setLstTreexpandimgs(null);
            }else
            {
                alrdbean.setLstTreexpandimgs(Tools.parseStringToList(treexpandimg,';','\''));
            }
        }
        if(treeclosedimg!=null)
        {
            treeclosedimg=treeclosedimg.trim();
            if(treeclosedimg.equals(""))
            {
                alrdbean.setLstTreeclosedimgs(null);
            }else
            {
                alrdbean.setLstTreeclosedimgs(Tools.parseStringToList(treeclosedimg,';','\''));
            }
        }
        if(treeleafimg!=null) alrdbean.setTreeleafimg(treeleafimg.trim());

        //        processRowSelectCol(disbean);//处理提供行选中的列，只有报表行选中类型为Consts.ROWSELECT_CHECKBOX或Consts.ROWSELECT_RADIOBOX类型时，才会有行选中的列
        
        
        
        
        
        
        
        
        
        
        //        boolean isAllAlwayOrNeverCol=true;//是否全部是hidden为1或3的<col/>（如果<col/>全部是1或3，则<group/>的hidden不可能出现0和2的情况，所以不用判断它）
        List<ColBean> lstColBeans=disbean.getLstCols();
        boolean bolContainsClickOrderby=false;
        //        boolean bolContainsNonConditionFilter=false;
        if(lstColBeans!=null&&lstColBeans.size()>0)
        {
            AbsListReportColBean alcbeanTemp;
            for(ColBean cbeanTmp:lstColBeans)
            {
                if(cbeanTmp==null) continue;
                
                
                alcbeanTemp=(AbsListReportColBean)cbeanTmp.getExtendConfigDataForReportType(KEY);
                if(alcbeanTemp==null) continue;
                if(alcbeanTemp.isRequireClickOrderby())
                {
                    bolContainsClickOrderby=true;
                }
                
                
                
                
                
            }
        }
        //        if(isAllAlwayOrNeverCol) bean.setColselected(false);//全部是hidden为1或3的<col/>，后面显示时将不为它提供列选择框
        alrdbean.setContainsClickOrderBy(bolContainsClickOrderby);
        //        bean.setContainsNonConditionFilter(bolContainsNonConditionFilter);//此报表包括与条件无关的列过滤功能
        
        //        if(eleStatisticBean!=null) loadStatisticConfig(eleStatisticBean,disbean,alrdbean);//加载统计信息的配置
        return 1;
    }

    public int afterReportLoading(ReportBean reportbean,List<XmlElementBean> lstEleReportBeans)
    {
        XmlElementBean eleReportBean=lstEleReportBeans.get(0);
        AbsListReportBean alrbean=(AbsListReportBean)reportbean.getExtendConfigDataForReportType(KEY);
        if(alrbean==null)
        {
            alrbean=new AbsListReportBean(reportbean);
            reportbean.setExtendConfigDataForReportType(KEY,alrbean);
        }
        XmlElementBean eleSubdisplayBean=eleReportBean.getChildElementByName("subdisplay");
        if(eleSubdisplayBean!=null) alrbean.setSubdisplaybean(loadSubDisplayConfig(eleSubdisplayBean,reportbean));
        return 1;
    }

    
    
    
    
    //        for(XmlElementBean eleDisBeanTmp:lstEleDisplayBeans)
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //            }
    
    
    
    private AbsListReportSubDisplayBean loadSubDisplayConfig(XmlElementBean eleSubDisplayBean,ReportBean reportbean)
    {
        AbsListReportSubDisplayBean subDisplayBean=new AbsListReportSubDisplayBean(reportbean);
        boolean hasSubDisplayChild=false;
        List<XmlElementBean> lstEleRowBeans=eleSubDisplayBean.getLstChildElementsByName("subrow");
        if(lstEleRowBeans!=null&&lstEleRowBeans.size()>0)
        {
            List<AbsListReportSubDisplayRowBean> lstSDisRowBeans=new ArrayList<AbsListReportSubDisplayRowBean>();
            subDisplayBean.setLstSubDisplayRowBeans(lstSDisRowBeans);
            AbsListReportSubDisplayRowBean sRowBeanTmp;
            for(XmlElementBean eleRowTmp:lstEleRowBeans)
            {
                if(eleRowTmp==null) continue;
                sRowBeanTmp=new AbsListReportSubDisplayRowBean();
                String displaytype=eleRowTmp.attributeValue("displaytype");
                String displayposition=eleRowTmp.attributeValue("displayposition");
                if(displaytype!=null)
                {
                    boolean isDisplayPerpage=false, isDisplayReport=false;
                    displaytype=displaytype.toLowerCase().trim();
                    if(!displaytype.equals(""))
                    {
                        List<String> lstTmp=Tools.parseStringToList(displaytype,"|",false);
                        for(String tmp:lstTmp)
                        {
                            tmp=tmp.trim();
                            if(tmp.equals("")) continue;
                            if(tmp.equals("page"))
                            {
                                isDisplayPerpage=true;
                            }else if(tmp.equals("report"))
                            {
                                isDisplayReport=true;
                            }else
                            {
                                throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"的统计显示行<subrow/>失败，其displaytype属性配置不合法");
                            }
                        }
                    }
                    if(isDisplayPerpage&&isDisplayReport)
                    {
                        sRowBeanTmp.setDisplaytype(AbsListReportSubDisplayBean.SUBROW_DISPLAYTYPE_PAGEREPORT);
                    }else if(isDisplayPerpage)
                    {
                        sRowBeanTmp.setDisplaytype(AbsListReportSubDisplayBean.SUBROW_DISPLAYTYPE_PAGE);
                    }else
                    {
                        sRowBeanTmp.setDisplaytype(AbsListReportSubDisplayBean.SUBROW_DISPLAYTYPE_REPORT);
                    }
                }
                if(displayposition!=null)
                {
                    boolean isTop=false, isBottom=false;
                    displayposition=displayposition.toLowerCase().trim();
                    if(!displayposition.equals(""))
                    {
                        List<String> lstTmp=Tools.parseStringToList(displayposition,"|",false);
                        for(String tmp:lstTmp)
                        {
                            tmp=tmp.trim();
                            if(tmp.equals("")) continue;
                            if(tmp.equals("top"))
                            {
                                isTop=true;
                            }else if(tmp.equals("bottom"))
                            {
                                isBottom=true;
                            }else
                            {
                                throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"的统计显示行<subrow/>失败，其displayposition属性配置不合法");
                            }
                        }
                    }
                    if(isTop&&isBottom)
                    {
                        sRowBeanTmp.setDisplayposition(AbsListReportSubDisplayBean.SUBROW_POSITION_BOTH);
                    }else if(isTop)
                    {
                        sRowBeanTmp.setDisplayposition(AbsListReportSubDisplayBean.SUBROW_POSITION_TOP);
                    }else
                    {
                        sRowBeanTmp.setDisplayposition(AbsListReportSubDisplayBean.SUBROW_POSITION_BOTTOM);
                    }
                }
                List<XmlElementBean> lstEleColBeans=eleRowTmp.getLstChildElementsByName("subcol");
                List<AbsListReportSubDisplayColBean> lstSubColBeans=new ArrayList<AbsListReportSubDisplayColBean>();
                sRowBeanTmp.setLstSubColBeans(lstSubColBeans);
                if(lstEleColBeans!=null&&lstEleColBeans.size()>0)
                {
                    for(XmlElementBean objTmp:lstEleColBeans)
                    {
                        lstSubColBeans.add(loadSubColConfig(reportbean,objTmp));
                    }
                }
                if(lstSubColBeans.size()>0) lstSDisRowBeans.add(sRowBeanTmp);
            }
            hasSubDisplayChild=lstSDisRowBeans.size()>0;
        }

        List<XmlElementBean> lstRowGroupStatistics=eleSubDisplayBean.getLstChildElementsByName("rowgroup-subrow");
        List<XmlElementBean> lstTreeRowGroupStatistics=eleSubDisplayBean.getLstChildElementsByName("treerowgroup-subrow");
        if(lstRowGroupStatistics!=null&&lstRowGroupStatistics.size()>0&&lstTreeRowGroupStatistics!=null&&lstTreeRowGroupStatistics.size()>0)
        {
            throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，不能在<subdisplay/>同时配置<rowgroup-subrow/>和<treerowgroup-subrow/>");
        }
        if(lstRowGroupStatistics!=null&&lstRowGroupStatistics.size()>0)
        {
            for(XmlElementBean objTmp:lstRowGroupStatistics)
            {
                subDisplayBean.addRowGroupSubDisplayRowBean(loadRowGroupSubRowConfig(reportbean,objTmp));
            }
            hasSubDisplayChild=subDisplayBean.getMRowGroupSubDisplayRowBeans()!=null&&subDisplayBean.getMRowGroupSubDisplayRowBeans().size()>0;
        }
        if(lstTreeRowGroupStatistics!=null&&lstTreeRowGroupStatistics.size()>0)
        {
            for(XmlElementBean objTmp:lstTreeRowGroupStatistics)
            {
                subDisplayBean.addRowGroupSubDisplayRowBean(loadRowGroupSubRowConfig(reportbean,objTmp));
            }
            hasSubDisplayChild=subDisplayBean.getMRowGroupSubDisplayRowBeans()!=null&&subDisplayBean.getMRowGroupSubDisplayRowBeans().size()>0;
        }
        if(!hasSubDisplayChild) return null;
        XmlElementBean eleStatitemsBean=eleSubDisplayBean.getChildElementByName("statitems");
        if(eleStatitemsBean!=null)
        {
            List<XmlElementBean> lstEleStatitemBeans=eleStatitemsBean.getLstChildElementsByName("statitem");
            if(lstEleStatitemBeans!=null&&lstEleStatitemBeans.size()>0)
            {
                List<StatisticItemBean> lstStatitemBeans=new ArrayList<StatisticItemBean>();
                StatisticItemBean statitemBeanTmp;
                for(XmlElementBean eleBeanTmp:lstEleStatitemBeans)
                {
                    statitemBeanTmp=new StatisticItemBean();
                    loadStatitemConfig(reportbean,statitemBeanTmp,eleBeanTmp);
                    lstStatitemBeans.add(statitemBeanTmp);
                }
                subDisplayBean.setLstStatitemBeans(lstStatitemBeans);
            }
        }
        FormatBean fbean=ConfigLoadAssistant.getInstance().loadFormatConfig(eleSubDisplayBean.getChildElementByName("format"));
        if(fbean==null)
        {
            throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"的<subdisplay/>配置失败，没有配置格式化显示<format/>标签");
        }
        subDisplayBean.setFbean(fbean);
        return subDisplayBean;
    }

    private void loadStatitemConfig(ReportBean reportbean,StatisticItemBean statitemBean,XmlElementBean eleStatitemBean)
    {
        String property=eleStatitemBean.attributeValue("property");
        String value=eleStatitemBean.attributeValue("value");
        String statiscope=eleStatitemBean.attributeValue("statiscope");
        String dataset=eleStatitemBean.attributeValue("dataset");
        if(property==null||property.trim().equals(""))
        {
            throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"的统计项<statitems/>失败，property属性不能为空");
        }
        statitemBean.setProperty(property.trim());
        if(value==null)
        {
            throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"的统计项"+property+"失败，必须配置<statitem/>的value属性");
        }
        value=value.trim();
        int idxl=value.indexOf("(");
        int idxr=value.lastIndexOf(")");
        if(idxl<=0||idxr!=value.length()-1)
        {
            throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"的统计项"+property+"失败，"+value+"不是有效的统计");
        }
        String statitype=value.substring(0,idxl).trim();
        String column=value.substring(idxl+1,idxr).trim();
        if(!Consts.lstStatisticsType.contains(statitype))
        {
            throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"的统计项"+property+"失败，"+statitype+"不是有效的统计类型");
        }
        if(column.equals(""))
        {
            throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"的统计项"+property+"失败，"+value+"中统计字段为空");
        }
        statitemBean.setValue(value);
        statitemBean.setDatatypeObj(ConfigLoadAssistant.loadDataType(eleStatitemBean));
        statitemBean.setLstConditions(ComponentConfigLoadManager.loadConditionsInOtherPlace(eleStatitemBean,reportbean));
        if(statiscope!=null)
        {
            statiscope=statiscope.toLowerCase().trim();
            boolean isStatPage=false, isStatReport=false;
            if(!statiscope.equals(""))
            {
                List<String> lstTmp=Tools.parseStringToList(statiscope,"|",false);
                for(String tmp:lstTmp)
                {
                    tmp=tmp.trim();
                    if(tmp.equals("")) continue;
                    if(tmp.equals("page"))
                    {
                        isStatPage=true;
                    }else if(tmp.equals("report"))
                    {
                        isStatReport=true;
                    }else
                    {
                        throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"的统计项"+property+"失败，其statiscope属性配置不合法");
                    }
                }
            }
            if(isStatPage&&isStatReport)
            {
                statitemBean.setStatiscope(StatisticItemBean.STATSTIC_SCOPE_ALL);
            }else if(isStatPage)
            {
                statitemBean.setStatiscope(StatisticItemBean.STATSTIC_SCOPE_PAGE);
            }else
            {
                statitemBean.setStatiscope(StatisticItemBean.STATSTIC_SCOPE_REPORT);
            }
        }
        if(dataset!=null) statitemBean.setDatasetid(dataset.trim());
    }

    private AbsListReportRowGroupSubDisplayRowBean loadRowGroupSubRowConfig(ReportBean reportbean,XmlElementBean eleRowGroupBean)
    {
        String column=eleRowGroupBean.attributeValue("column");
        if(column==null||column.trim().equals(""))
        {
            throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，在<subdisplay/>配置<rowgroup-subrow/>时，column属性不能为空");
        }
        String condition=eleRowGroupBean.attributeValue("condition");
        AbsListReportRowGroupSubDisplayRowBean srgbean=new AbsListReportRowGroupSubDisplayRowBean();
        srgbean.setRowgroupcolumn(column.trim());
        if(condition!=null) srgbean.setCondition(condition.trim());

        List<XmlElementBean> lstEleCols=eleRowGroupBean.getLstChildElementsByName("subcol");
        List<AbsListReportSubDisplayColBean> lstSubColBeans=new ArrayList<AbsListReportSubDisplayColBean>();
        srgbean.setLstSubColBeans(lstSubColBeans);
        if(lstEleCols!=null&&lstEleCols.size()>0)
        {
            for(XmlElementBean colobjTmp:lstEleCols)
            {
                lstSubColBeans.add(loadSubColConfig(reportbean,colobjTmp));
            }
        }
        return lstSubColBeans.size()==0?null:srgbean;
    }

    private AbsListReportSubDisplayColBean loadSubColConfig(ReportBean reportbean,XmlElementBean eleScolBean)
    {
        AbsListReportSubDisplayColBean subColBean=new AbsListReportSubDisplayColBean();
        String property=eleScolBean.attributeValue("property");
        if(property==null||property.trim().equals(""))
        {
            throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"的统计项<subcol/>失败，property属性不能为空");
        }
        subColBean.setProperty(property.trim());
        String colspan=eleScolBean.attributeValue("colspan");
        String valuestyleproperty=eleScolBean.attributeValue("valuestyleproperty");
        if(valuestyleproperty==null) valuestyleproperty="";
        String colspanInStyleprop=Tools.getPropertyValueByName("colspan",valuestyleproperty,true);
        if(colspan!=null&&!colspan.trim().equals("")&&colspanInStyleprop!=null&&!colspanInStyleprop.trim().equals(""))
        {
            throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，不能同时在<scol/>的colspan和valuestyleproperty中同时配置colspan值");
        }
        if(colspan!=null&&!colspan.trim().equals(""))
        {
            valuestyleproperty=valuestyleproperty+" colspan=\""+colspan+"\"";
        }
        subColBean.setValuestyleproperty(valuestyleproperty.trim(),false);
        return subColBean;
    }

    public int doPostLoad(ReportBean reportbean)
    {
        super.doPostLoad(reportbean);
        constructSqlForListType(reportbean.getSbean());
        AbsListReportBean alrbean=(AbsListReportBean)reportbean.getExtendConfigDataForReportType(KEY);
        if(alrbean==null) return 1;
        processFixedColsAndRows(reportbean);
        processReportScrollConfig(reportbean);
        if(alrbean.getScrollType()==AbsListReportBean.SCROLLTYPE_VERTICAL||alrbean.getScrollType()==AbsListReportBean.SCROLLTYPE_FIXED)
        {
            reportbean.setCellresize(0);
        }
        DisplayBean dbean=reportbean.getDbean();
        if(dbean.getColselect()==null) dbean.setColselect(true);
        processRowSelectCol(dbean);
        processRoworderCol(dbean);
        ((AbsListReportDisplayBean)dbean.getExtendConfigDataForReportType(KEY)).doPostLoad();
        if(alrbean.getSubdisplaybean()!=null) alrbean.getSubdisplaybean().doPostLoad();
        return 1;
    }

    private void constructSqlForListType(SqlBean sqlbean)
    {
        if(sqlbean==null||sqlbean.getLstDatasetBeans()==null) return;
        AbsListReportDisplayBean alrdbean=(AbsListReportDisplayBean)sqlbean.getReportBean().getDbean().getExtendConfigDataForReportType(
                AbsListReportType.KEY);
        boolean isMatchedRowGroupColAndDataset=false;
        for(ReportDataSetBean dsbeanTmp:sqlbean.getLstDatasetBeans())
        {
            for(ReportDataSetValueBean dsvbeanTmp:dsbeanTmp.getLstValueBeans())
            {
                String value=dsvbeanTmp.getValue();
                if(value==null||value.trim().equals("")||dsvbeanTmp.isStoreProcedure()||dsvbeanTmp.getCustomizeDatasetObj()!=null) continue;
                dsvbeanTmp.doPostLoadSql(true);
                if(alrdbean!=null&&alrdbean.getRowGroupColsNum()>0&&dsvbeanTmp.isMatchDatasetid(alrdbean.getRowgroupDatasetId()))
                {
                    if(isMatchedRowGroupColAndDataset)
                    {
                        throw new WabacusConfigLoadingException("报表"+sqlbean.getReportBean().getPath()+"配置了多个独立数据集，必须为其行分组列指定来自哪个数据集");
                    }
                    isMatchedRowGroupColAndDataset=true;
                    List<String> lstColsColumnInRowGroup=alrdbean.getLstRowgroupColsColumn();
                    if(lstColsColumnInRowGroup!=null&&lstColsColumnInRowGroup.size()>0)
                    {
                        List<Map<String,String>> lstRowgroupColsAndOrders=new ArrayList<Map<String,String>>();
                        alrdbean.setLstRowgroupColsAndOrders(lstRowgroupColsAndOrders);
                        if(dsvbeanTmp.getSqlWithoutOrderby().indexOf("%orderby%")<0)
                        {
                            dsvbeanTmp.setSqlWithoutOrderby(dsvbeanTmp.getSqlWithoutOrderby()+" %orderby%");
                            StringBuffer orderbybuf=new StringBuffer();
                            for(String column:lstColsColumnInRowGroup)
                            {
                                if(column==null) continue;
                                orderbybuf.append(column).append(",");
                                Map<String,String> mColAndOrders=new HashMap<String,String>();
                                mColAndOrders.put(column,"asc");//默认都为升序排序
                                lstRowgroupColsAndOrders.add(mColAndOrders);
                            }
                            if(orderbybuf.charAt(orderbybuf.length()-1)==',')
                            {
                                orderbybuf.deleteCharAt(orderbybuf.length()-1);
                            }
                            dsvbeanTmp.setOrderby(orderbybuf.toString());
                        }else
                        {
                            addRowGroupColumnToOrderByClause(dsvbeanTmp,lstColsColumnInRowGroup,lstRowgroupColsAndOrders);
                        }
                    }
                }
                if(!dsvbeanTmp.isDependentDataSet()) dsvbeanTmp.buildPageSplitSql();
            }
        }
    }

    private void addRowGroupColumnToOrderByClause(ReportDataSetValueBean svbean,List<String> lstColsColumnInRowGroup,
            List<Map<String,String>> lstRowgroupColsAndOrders)
    {
        String oldorderby=svbean.getOrderby();
        List<String> lstOrderByColumns=Tools.parseStringToList(oldorderby,",");
        List<Map<String,String>> lstOldOrderByColumns=new ArrayList<Map<String,String>>();
        for(String orderby_tmp:lstOrderByColumns)
        {
            if(orderby_tmp==null||orderby_tmp.trim().equals("")) continue;
            orderby_tmp=orderby_tmp.trim();
            List<String> lstTemp=Tools.parseStringToList(orderby_tmp," ");
            Map<String,String> mOldOrderBy=new HashMap<String,String>();
            lstOldOrderByColumns.add(mOldOrderBy);
            if(lstTemp.size()==1)
            {
                mOldOrderBy.put(lstTemp.get(0),"asc");
            }else if(lstTemp.size()==2)
            {
                mOldOrderBy.put(lstTemp.get(0),lstTemp.get(1).trim().toLowerCase());
            }else
            {
                throw new WabacusConfigLoadingException("报表"+svbean.getReportBean().getPath()+"配置的SQL语句中order by子句"+svbean.getOrderby()+"不合法");
            }
        }
        StringBuffer orderBuf=new StringBuffer();
        for(String rowgroupCol:lstColsColumnInRowGroup)
        {
            if(rowgroupCol==null) continue;
            Map<String,String> mColAndOrders=new HashMap<String,String>();
            lstRowgroupColsAndOrders.add(mColAndOrders);
            Map<String,String> mTemp=null;
            for(Map<String,String> mTemp2:lstOldOrderByColumns)
            {
                if(mTemp2.containsKey(rowgroupCol))
                {
                    mTemp=mTemp2;
                    lstOldOrderByColumns.remove(mTemp2);
                    break;
                }
            }
            if(mTemp!=null)
            {
                orderBuf.append(rowgroupCol).append(" ").append(mTemp.get(rowgroupCol)).append(",");
                mColAndOrders.put(rowgroupCol,mTemp.get(rowgroupCol));
            }else
            {
                orderBuf.append(rowgroupCol).append(",");
                mColAndOrders.put(rowgroupCol,"asc");
            }
        }
        
        for(Map<String,String> mTemp:lstOldOrderByColumns)
        {
            Entry<String,String> entry=mTemp.entrySet().iterator().next();
            orderBuf.append(entry.getKey()).append(" ").append(entry.getValue()).append(",");
        }
        if(orderBuf.charAt(orderBuf.length()-1)==',')
        {
            orderBuf.deleteCharAt(orderBuf.length()-1);
        }
        svbean.setOrderby(orderBuf.toString());
    }

    protected void processFixedColsAndRows(ReportBean reportbean)
    {
        AbsListReportBean alrbean=(AbsListReportBean)reportbean.getExtendConfigDataForReportType(KEY);
        if(alrbean.getFixedcols(null)>0)
        {
            int cnt=0;
            for(ColBean cbTmp:reportbean.getDbean().getLstCols())
            {
                if(cbTmp.getDisplaytype()==Consts.COL_DISPLAYTYPE_HIDDEN) continue;
                cnt++;
            }
            if(cnt<=alrbean.getFixedcols(null)) alrbean.setFixedcols(null,0);
        }
        if(alrbean.getFixedcols(null)>0)
        {
            boolean isChkRadioRowselectReport=Consts.ROWSELECT_CHECKBOX.equals(alrbean.getRowSelectType())
                    ||Consts.ROWSELECT_RADIOBOX.equals(alrbean.getRowSelectType())
                    ||Consts.ROWSELECT_MULTIPLE_CHECKBOX.equals(alrbean.getRowSelectType())
                    ||Consts.ROWSELECT_SINGLE_RADIOBOX.equals(alrbean.getRowSelectType());
            AbsListReportColBean alrcbeanTmp;
            int cnt=0;
            for(ColBean cbTmp:reportbean.getDbean().getLstCols())
            {
                if(cbTmp.getDisplaytype()==Consts.COL_DISPLAYTYPE_HIDDEN) continue;
                if(cbTmp.isRowSelectCol())
                {
                    if(!isChkRadioRowselectReport) continue;
                    throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败,在<report/>的fixedcols中配置的冻结列数包括了行选中列，这样不能正常选中行");
                }
                alrcbeanTmp=(AbsListReportColBean)cbTmp.getExtendConfigDataForReportType(KEY);
                if(alrcbeanTmp==null)
                {
                    alrcbeanTmp=new AbsListReportColBean(cbTmp);
                    cbTmp.setExtendConfigDataForReportType(KEY,alrcbeanTmp);
                }
                alrcbeanTmp.setFixedCol(null,true);
                if(++cnt==alrbean.getFixedcols(null)) break;
            }
        }
        AbsListReportDisplayBean alrdbean=(AbsListReportDisplayBean)reportbean.getDbean().getExtendConfigDataForReportType(KEY);
        if(alrbean.getFixedcols(null)>0||alrbean.getFixedrows()>0)
        {
            if(alrdbean!=null&&alrdbean.getRowgrouptype()==2&&alrdbean.getRowGroupColsNum()>0)
            {
                throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败,树形分组报表不能冻结行列标题");
            }
        }
    }

    protected void processReportScrollConfig(ReportBean reportbean)
    {
        AbsListReportBean alrbean=(AbsListReportBean)reportbean.getExtendConfigDataForReportType(KEY);
        int scrolltype=alrbean.getScrollType();
        if(scrolltype==AbsListReportBean.SCROLLTYPE_NONE||scrolltype==AbsListReportBean.SCROLLTYPE_FIXED) return;
        if(scrolltype==AbsListReportBean.SCROLLTYPE_ALL)
        {
            ComponentAssistant.getInstance().doPostLoadForComponentScroll(reportbean,true,true,reportbean.getScrollwidth(),
                    reportbean.getScrollheight(),reportbean.getScrollstyle());
        }else
        {
            if(scrolltype==AbsListReportBean.SCROLLTYPE_VERTICAL)
            {//只显示垂直滚动条的数据自动列表报表必须以像素的形式配置宽度，否则不能保证标题列和数据列对齐
                String[] htmlsizeArr=WabacusAssistant.getInstance().parseHtmlElementSizeValueAndType(reportbean.getWidth());
                if(htmlsizeArr==null||htmlsizeArr[0].equals("")||htmlsizeArr[0].equals("0"))
                {
                    throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败,此报表只显示垂直滚动条，因此必须为其配置width属性指定报表宽度");
                }else
                {
                    if(htmlsizeArr[1]!=null&&htmlsizeArr[1].equals("%"))
                    {
                        throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败,只显示垂直滚动条时，不能将报表的width属性配置为百分比");
                    }
                    reportbean.setWidth(htmlsizeArr[0]+htmlsizeArr[1]);
                }
                reportbean.setShowContextMenu(false);
            }
            if(Consts_Private.SCROLLSTYLE_IMAGE.equals(reportbean.getScrollstyle()))
            {
                String scrolljs="/webresources/script/wabacus_scroll.js";
                
                
                //                    scrolljs="/webresources/script/wabacus_scroll.js";
                
                
                
                
                
                
                
                //                    scrolljs="/webresources/script/"+encode.toLowerCase()+"/wabacus_scroll.js";
                
                scrolljs=Tools.replaceAll(Config.webroot+"/"+scrolljs,"//","/");
                reportbean.getPageBean().addMyJavascriptFile(scrolljs,-1);
                String css=Config.webroot+"/webresources/skin/"+Consts_Private.SKIN_PLACEHOLDER+"/wabacus_scroll.css";
                css=Tools.replaceAll(css,"//","/");
                reportbean.getPageBean().addMyCss(css);
                if(scrolltype==AbsListReportBean.SCROLLTYPE_HORIZONTAL)
                {//只显示横向滚动条
                    reportbean.addOnloadMethod(new OnloadMethodBean(Consts_Private.ONlOAD_IMGSCROLL,"showComponentScroll('"+reportbean.getGuid()
                            +"','-1',12)"));
                }else if(scrolltype==AbsListReportBean.SCROLLTYPE_VERTICAL)
                {
                    reportbean.addOnloadMethod(new OnloadMethodBean(Consts_Private.ONlOAD_IMGSCROLL,"showComponentScroll('"+reportbean.getGuid()
                            +"','"+reportbean.getScrollheight()+"',11)"));
                }
            }
        }
    }

    protected ColBean[] processRowSelectCol(DisplayBean disbean)
    {
        List<ColBean> lstCols=disbean.getLstCols();
        ColBean cbRowSelect=null;
        for(ColBean cbTmp:lstCols)
        {
            if(cbTmp.isRowSelectCol())
            {
                if(cbRowSelect!=null)
                {
                    throw new WabacusConfigLoadingException("加载报表"+disbean.getReportBean().getPath()+"失败，不能配置多个行选择的单选框或复选框列");
                }
                cbRowSelect=cbTmp;
            }
        }
        ReportBean reportbean=disbean.getReportBean();
        AbsListReportBean alrbean=(AbsListReportBean)disbean.getReportBean().getExtendConfigDataForReportType(AbsListReportType.KEY);
        if(alrbean.getRowSelectType()==null
                ||(!alrbean.getRowSelectType().trim().equals(Consts.ROWSELECT_CHECKBOX)
                        &&!alrbean.getRowSelectType().trim().equals(Consts.ROWSELECT_RADIOBOX)
                        &&!alrbean.getRowSelectType().trim().equals(Consts.ROWSELECT_MULTIPLE_CHECKBOX)&&!alrbean.getRowSelectType().trim().equals(
                        Consts.ROWSELECT_SINGLE_RADIOBOX)))
        {//当前报表要么没有提供行选中功能，要么提供的不是复选框/单选框的行选择功能
            if(cbRowSelect==null) return null;
            for(int i=0,len=lstCols.size();i<len;i++)
            {
                if(lstCols.get(i).isRowSelectCol())
                {
                    lstCols.remove(i);
                    break;
                }
            }
            return null;
        }
        //提供了复选框/单选框的行选择功能
        if(cbRowSelect==null) return insertRowSelectNewCols(alrbean,lstCols);
        if(Consts.ROWSELECT_CHECKBOX.equals(alrbean.getRowSelectType())||Consts.ROWSELECT_MULTIPLE_CHECKBOX.equals(alrbean.getRowSelectType()))
        {
            String label=cbRowSelect.getLabel(rrequest);
            label=label==null?"":label.trim();
            if(label.indexOf("<input")<0||label.indexOf("type")<0||label.indexOf("checkbox")<0)
            {
                label=label
                        +"<input type=\"checkbox\" onclick=\"try{doSelectedAllDataRowChkRadio(this);}catch(e){logErrorsAsJsFileLoad(e);}\" name=\""
                        +reportbean.getGuid()+"_rowselectbox\">";
            }
            cbRowSelect.setLabel(label);
        }
        cbRowSelect.setLabelstyleproperty(Tools.addPropertyValueToStylePropertyIfNotExist(cbRowSelect.getLabelstyleproperty(null,true),"align","center"),true);
        cbRowSelect.setLabelstyleproperty(Tools.addPropertyValueToStylePropertyIfNotExist(cbRowSelect.getLabelstyleproperty(null,true),"valign","middle"),true);
        cbRowSelect.setValuestyleproperty(Tools.addPropertyValueToStylePropertyIfNotExist(cbRowSelect.getValuestyleproperty(null,true),"align","center"),true);
        cbRowSelect.setValuestyleproperty(Tools.addPropertyValueToStylePropertyIfNotExist(cbRowSelect.getValuestyleproperty(null,true),"valign","middle"),true);
        cbRowSelect.setDisplaytype(Consts.COL_DISPLAYTYPE_ALWAYS);
        return null;
    }

    protected ColBean[] insertRowSelectNewCols(AbsListReportBean alrbean,List<ColBean> lstCols)
    {
        ReportBean reportbean=(ReportBean)alrbean.getOwner();
        ColBean[] cbResult=new ColBean[2];
        ColBean cbNewRowSelect=new ColBean(reportbean.getDbean());
        cbNewRowSelect.setColumn(Consts_Private.COL_ROWSELECT);
        cbNewRowSelect.setProperty(Consts_Private.COL_ROWSELECT);
        cbResult[0]=cbNewRowSelect;
        AbsListReportColBean alrcbean=new AbsListReportColBean(cbNewRowSelect);
        cbNewRowSelect.setExtendConfigDataForReportType(KEY,alrcbean);
        if(Consts.ROWSELECT_CHECKBOX.equals(alrbean.getRowSelectType())||Consts.ROWSELECT_MULTIPLE_CHECKBOX.equals(alrbean.getRowSelectType()))
        {
            cbNewRowSelect
                    .setLabel("<input type=\"checkbox\" onclick=\"try{doSelectedAllDataRowChkRadio(this);}catch(e){logErrorsAsJsFileLoad(e);}\" name=\""
                            +reportbean.getGuid()+"_rowselectbox\">");
        }else
        {
            cbNewRowSelect.setLabel("");
        }
        cbNewRowSelect.setDisplaytype(Consts.COL_DISPLAYTYPE_ALWAYS);
        cbNewRowSelect.setLabelstyleproperty("style=\"text-align:center;vertical-align:middle;\"",true);
        cbNewRowSelect.setValuestyleproperty("style=\"text-align:center;vertical-align:middle;\"",true);
        UltraListReportDisplayBean ulrdbean=(UltraListReportDisplayBean)reportbean.getDbean().getExtendConfigDataForReportType(
                UltraListReportType.KEY);
        ColBean cbTmp;
        for(int i=0,len=lstCols.size();i<len;i++)
        {
            cbTmp=lstCols.get(i);
            if(cbTmp.getDisplaytype().equals(Consts.COL_DISPLAYTYPE_HIDDEN))
            {
                if(i==len-1) lstCols.add(cbNewRowSelect);
                continue;
            }
            AbsListReportColBean alrcbeanTmp=(AbsListReportColBean)cbTmp.getExtendConfigDataForReportType(KEY);
            if(alrcbeanTmp!=null&&(alrcbeanTmp.isRowgroup()||alrcbeanTmp.isFixedCol(rrequest)))
            {
                if(i==len-1) lstCols.add(cbNewRowSelect);
                continue;
            }
            UltraListReportColBean ulrcbeanTmp=(UltraListReportColBean)cbTmp.getExtendConfigDataForReportType(UltraListReportType.KEY);
            if(ulrcbeanTmp!=null&&ulrcbeanTmp.getParentGroupid()!=null&&!ulrcbeanTmp.getParentGroupid().trim().equals(""))
            {//当前列是在<group/>中
                String parentgroupid=ulrcbeanTmp.getParentGroupid();
                if(ulrdbean!=null&&(hasRowgroupColSibling(parentgroupid,ulrdbean)||hasFixedColSibling(parentgroupid,ulrdbean)))
                {//如果此列所在的<group/>或任意层父<group/>中有行分组列或被冻结的列，则新生成的行选择列不能在它的前面
                    if(i==len-1) lstCols.add(cbNewRowSelect);
                    continue;
                }
            }
            lstCols.add(i,cbNewRowSelect);
            cbResult[1]=cbTmp;
            break;
        }
        return cbResult;
    }

    protected List<ColBean> processRoworderCol(DisplayBean disbean)
    {
        List<ColBean> lstCols=disbean.getLstCols();
        AbsListReportBean alrbean=(AbsListReportBean)disbean.getReportBean().getExtendConfigDataForReportType(AbsListReportType.KEY);
        if(alrbean.getLstRoworderTypes()!=null&&alrbean.getLstRoworderTypes().size()>0)
        {
            List<ColBean> lstRoworderValueCols=new ArrayList<ColBean>();
            AbsListReportColBean alrcbeanTmp=null;
            for(ColBean cbTmp:lstCols)
            {
                alrcbeanTmp=(AbsListReportColBean)cbTmp.getExtendConfigDataForReportType(AbsListReportType.KEY);
                if(alrcbeanTmp==null||!alrcbeanTmp.isRoworderValue()) continue;
                lstRoworderValueCols.add(cbTmp);
            }
            if(lstRoworderValueCols.size()==0)
            {
                throw new WabacusConfigLoadingException("加载报表"+disbean.getReportBean().getPath()
                        +"失败，为它配置了行排序功能，但没有一个<col/>的rowordervalue属性配置为true，这样无法完成行排序功能");
            }
            AbsListReportDisplayBean alrdbean=(AbsListReportDisplayBean)disbean.getExtendConfigDataForReportType(KEY);
            alrdbean.setLstRoworderValueCols(lstRoworderValueCols);
        }
        Map<String,ColBean> mCreatedRoworderCols=new HashMap<String,ColBean>();
        for(String rowordertypeTmp:Consts.lstAllRoworderTypes)
        {
            if(rowordertypeTmp.equals(Consts.ROWORDER_DRAG)) continue;
            if(alrbean.getLstRoworderTypes()==null||!alrbean.getLstRoworderTypes().contains(rowordertypeTmp))
            {
                for(int i=lstCols.size()-1;i>=0;i--)
                {//删除掉所有这种行排序类型的列
                    if(lstCols.get(i).isRoworderCol(getRoworderColColumnByRoworderType(rowordertypeTmp)))
                    {
                        lstCols.remove(i);
                    }
                }
            }else
            {
                boolean isExistCol=false;
                for(int i=lstCols.size()-1;i>=0;i--)
                {
                    if(lstCols.get(i).isRoworderCol(getRoworderColColumnByRoworderType(rowordertypeTmp)))
                    {
                        isExistCol=true;
                        lstCols.get(i).setDisplaytype(Consts.COL_DISPLAYTYPE_ALWAYS);
                    }
                }
                if(!isExistCol)
                {
                    ColBean cbNewRoworder=new ColBean(disbean);
                    cbNewRoworder.setColumn(getRoworderColColumnByRoworderType(rowordertypeTmp));
                    cbNewRoworder.setProperty(getRoworderColColumnByRoworderType(rowordertypeTmp));
                    AbsListReportColBean alrcbean=new AbsListReportColBean(cbNewRoworder);
                    cbNewRoworder.setExtendConfigDataForReportType(KEY,alrcbean);
                    if(rowordertypeTmp.equals(Consts.ROWORDER_ARROW))
                    {
                        cbNewRoworder.setLabel(Config.getInstance().getResourceString(null,disbean.getPageBean(),"${roworder.arrow.label}",false));
                    }else if(rowordertypeTmp.equals(Consts.ROWORDER_INPUTBOX))
                    {
                        cbNewRoworder.setLabel(Config.getInstance().getResourceString(null,disbean.getPageBean(),"${roworder.inputbox.label}",false));
                    }else if(rowordertypeTmp.equals(Consts.ROWORDER_TOP))
                    {
                        cbNewRoworder.setLabel(Config.getInstance().getResourceString(null,disbean.getPageBean(),"${roworder.top.label}",false));
                    }
                    cbNewRoworder.setDisplaytype(Consts.COL_DISPLAYTYPE_ALWAYS);
                    cbNewRoworder.setLabelstyleproperty("style=\"text-align:center;vertical-align:middle;\"",true);
                    cbNewRoworder.setValuestyleproperty("style=\"text-align:center;vertical-align:middle;\"",true);
                    mCreatedRoworderCols.put(rowordertypeTmp,cbNewRoworder);
                }
            }
        }
        List<ColBean> lstCreatedColBeans=new ArrayList<ColBean>();
        if(mCreatedRoworderCols.size()>0)
        {
            for(String roworderTmp:alrbean.getLstRoworderTypes())
            {
                if(!mCreatedRoworderCols.containsKey(roworderTmp)) continue;
                lstCols.add(mCreatedRoworderCols.get(roworderTmp));
                lstCreatedColBeans.add(mCreatedRoworderCols.get(roworderTmp));
            }
        }
        return lstCreatedColBeans;
    }

    protected String getRoworderColColumnByRoworderType(String rowordertype)
    {
        if(rowordertype==null) return null;
        if(rowordertype.equals(Consts.ROWORDER_ARROW))
        {
            return Consts_Private.COL_ROWORDER_ARROW;
        }else if(rowordertype.equals(Consts.ROWORDER_INPUTBOX))
        {
            return Consts_Private.COL_ROWORDER_INPUTBOX;
        }else if(rowordertype.equals(Consts.ROWORDER_TOP))
        {
            return Consts_Private.COL_ROWORDER_TOP;
        }
        return "";
    }

    private boolean hasRowgroupColSibling(String parentgroupid,UltraListReportDisplayBean ulrdbean)
    {
        if(parentgroupid==null||parentgroupid.trim().equals("")) return false;
        UltraListReportGroupBean ulrgbean=ulrdbean.getGroupBeanById(parentgroupid);
        if(ulrgbean==null) return false;
        if(ulrgbean.hasRowgroupChildCol()) return true;
        return hasRowgroupColSibling(ulrgbean.getParentGroupid(),ulrdbean);
    }

    private boolean hasFixedColSibling(String parentgroupid,UltraListReportDisplayBean ulrdbean)
    {
        if(parentgroupid==null||parentgroupid.trim().equals("")) return false;
        UltraListReportGroupBean ulrgbean=ulrdbean.getGroupBeanById(parentgroupid);
        if(ulrgbean==null) return false;
        if(ulrgbean.hasFixedChildCol(rrequest)) return true;
        return hasFixedColSibling(ulrgbean.getParentGroupid(),ulrdbean);
    }

    public String getReportFamily()
    {
        return Consts_Private.REPORT_FAMILY_LIST;
    }
}
