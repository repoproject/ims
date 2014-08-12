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
package com.wabacus.system;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.Config;
import com.wabacus.config.component.IComponentConfigBean;
import com.wabacus.config.component.application.IApplicationConfigBean;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.container.page.PageBean;
import com.wabacus.config.database.type.AbsDatabaseType;
import com.wabacus.config.dataexport.PDFExportBean;
import com.wabacus.config.print.DefaultPrintProviderConfigBean;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.assistant.AuthorizationAssistant;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.system.component.IComponentType;
import com.wabacus.system.component.application.report.abstractreport.AbsListReportType;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.component.application.report.abstractreport.IEditableReportType;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportDisplayBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportFilterBean;
import com.wabacus.system.component.application.report.configbean.ColAndGroupDisplayBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportSqlBean;
import com.wabacus.system.component.application.report.configbean.editablereport.transaction.ITransactionType;
import com.wabacus.system.component.application.report.configbean.editablereport.transaction.TransactionTypeWrapper;
import com.wabacus.system.component.container.AbsContainerType;
import com.wabacus.system.component.container.page.PageType;
import com.wabacus.system.inputbox.AbsInputBox;
import com.wabacus.system.intercept.AbsPageInterceptor;
import com.wabacus.system.permission.ComponentPermissionBean;
import com.wabacus.system.serveraction.UpdateComponentDataServerActionBean;
import com.wabacus.util.Consts;
import com.wabacus.util.Consts_Private;
import com.wabacus.util.Tools;
import com.wabacus.util.UniqueArrayList;

public class ReportRequest
{
    private final static Log log=LogFactory.getLog(ReportRequest.class);

    private HttpServletRequest request;

    private PageBean pagebean;

    private List<IComponentConfigBean> lstComponentBeans;
    
    private String pageskin;
    
    private List<ReportBean> lstAllReportBeans;

    private List<String> lstApplicationIds;
    
    private Map<String,String> mReportidsInPdfTemplate;
    
    private Map<String,CacheDataBean> mCacheDataBeans=new HashMap<String,CacheDataBean>();

    private Map attributes=new HashMap();

    private Map<String,IComponentType> mComponentTypeObjs;

    private WabacusResponse wresponse;

    private Map<String,Connection> connections=new HashMap<String,Connection>();

    private Map<String,AbsDatabaseType> mDbTypes=new HashMap<String,AbsDatabaseType>();

    private int showtype;

    private PageType pageObj;

    private String url;
    
    private List<String> lstAncestorUrls;

    private String actiontype=Consts.SHOWREPORT_ACTION;

    private String locallanguage;

    private List<Statement> lstAllUsedStatement=new ArrayList<Statement>();

    private ReportBean slaveReportBean;

    private AbsReportType slaveReportTypeObj;

    private IComponentConfigBean refreshComponentBean;

    private IComponentType refreshComponentTypeObj;//本次加载更新的组件类型对象

    private Map<String,List<ColAndGroupDisplayBean>> mColAndGroupDisplayBeans;

    private Map<String,ComponentPermissionBean> mComponentsPermissions;

    private List<AbsPageInterceptor> lstPageInterceptors;
    
    private List<String> lstSearchReportIds;
    
    private UpdateComponentDataServerActionBean serverActionBean;
    
    private Map<String,Set<String>> mShouldAddToUrlAttriuteNames=null;
    
    private TransactionTypeWrapper transactionWrapper=null;
    
    private Map<String,String> mTransactionLevels;
    
    private List<String> lstReportWithDefaultSelectedRows;
    
    private Map<String,String> mServerValidateDatas;
    
    public ReportRequest(HttpServletRequest request,int _showtype)
    {
        this.locallanguage=request.getLocale().getLanguage();
        if(this.locallanguage==null||this.locallanguage.trim().toLowerCase().equals("en"))
        {
            this.locallanguage="";
        }else
        {
            this.locallanguage=this.locallanguage.trim().toLowerCase();
        }
        Enumeration enume=request.getAttributeNames();
        while(enume.hasMoreElements())
        {
            String name=(String)enume.nextElement();
            Object value=request.getAttribute(name);
            if(value!=null)
            {
                if(this.attributes.containsKey(name))
                {
                    log.warn("request attribute中存在多个变量名为"+name+"的参数值");
                }
                this.attributes.put(name,value);
            }
        }

        Enumeration enumer=request.getParameterNames();
        while(enumer.hasMoreElements())
        {
            String name=(String)enumer.nextElement();
            if(name!=null&&!name.trim().equals(""))
            {
                if(this.attributes.containsKey(name))
                {
                    log.warn("request中存在多个变量名为"+name+"的参数值");
                }
                
                String[] values=request.getParameterValues(name);
                if(values==null)
                {
                    this.attributes.put(name,null);
                }else if(values.length==1)
                {
                    this.attributes.put(name,values[0]);
                }else
                {
                    this.attributes.put(name,values);
                }

            }
        }
        this.showtype=_showtype;
        this.request=request;
        request.setAttribute("WX_REPORTREQUEST",this);
        this.actiontype=Tools.getRequestValue(request,"ACTIONTYPE",Consts.SHOWREPORT_ACTION);
    }

    public ReportRequest(String pageid,int _showtype,Locale locale)
    {
        if(locale==null)
        {
            this.locallanguage="";
        }else
        {
            this.locallanguage=locale.getLanguage();
            if(this.locallanguage==null||this.locallanguage.trim().toLowerCase().equals("en"))
            {
                this.locallanguage="";
            }else
            {
                this.locallanguage=this.locallanguage.trim().toLowerCase();
            }
        }
        this.showtype=_showtype;
        
    }

    public void initGetFilterDataList()
    {
        initReportCommon();
        ReportBean rbean=this.lstAllReportBeans.get(0);
        rbean.getSbean().initConditionValues(this);
        setFilterCondition(rbean);
    }

    private AbsInputBox autoCompleteSourceInputBoxObj;
    
    public AbsInputBox getAutoCompleteSourceInputBoxObj()
    {
        return autoCompleteSourceInputBoxObj;
    }

    public void initGetAutoCompleteColValues()
    {
        initReportCommon();
        String inputboxid=this.getStringAttribute("INPUTBOXID","");
        if(inputboxid.equals(""))
        {
            throw new WabacusRuntimeException("没有取到源输入框ID，无法完成其它列的数据自动填充");
        }
        autoCompleteSourceInputBoxObj=this.lstAllReportBeans.get(0).getInputboxWithAutoComplete(inputboxid);
        if(autoCompleteSourceInputBoxObj==null)
        {
            throw new WabacusRuntimeException("没有取到源输入框ID为"+inputboxid+"对应的输入框对象，无法完成其它列的数据自动填充");
        }
    }
    
    public void initGetChartDataString()
    {
        initReportCommon();
        this.lstAllReportBeans.get(0).getSbean().initConditionValues(this);
    }
    
    public void initReportCommon()
    {
        String pageid=getStringAttribute("PAGEID","");
        String reportid=getStringAttribute("REPORTID","");
        pageid=pageid==null?"":pageid.trim();
        reportid=reportid==null?"":reportid.trim();
        if(pageid.equals(""))
        {
            this.wresponse.getMessageCollector().warn("页面ID不能为空",true,Consts.STATECODE_FAILED);
        }
        this.pagebean=Config.getInstance().getPageBean(pageid);
        if(pagebean==null)
        {
            this.wresponse.getMessageCollector().warn("没有配置页面ID为："+pageid+"的报表",true,Consts.STATECODE_FAILED);
        }
        if(reportid==null||reportid.equals(""))
        {
            this.wresponse.getMessageCollector().warn("没有取到报表ID",true,Consts.STATECODE_FAILED);
        }
        lstAllReportBeans=new ArrayList<ReportBean>();
        ReportBean rbean=this.pagebean.getReportChild(reportid,true);
        if(rbean==null)
        {
            this.wresponse.getMessageCollector().warn("页面："+pageid+"中没有配置ID为"+reportid+"的报表",true,Consts.STATECODE_FAILED);
        }
        lstAllReportBeans.add(rbean);
        this.url="";
    }
    
    public void init(String pageid)
    {
        pageid=pageid==null?"":pageid.trim();
        if(pageid.equals(""))
        {
            this.wresponse.getMessageCollector().warn("页面ID为空，请重试",true,Consts.STATECODE_FAILED);
        }
        this.pagebean=Config.getInstance().getPageBean(pageid);
        if(pagebean==null)
        {
            this.wresponse.getMessageCollector().warn("没有配置页面ID为："+pageid+"的报表",true,Consts.STATECODE_FAILED);
        }
        if(this.showtype==Consts.DISPLAY_ON_PAGE)
        {
            if(Config.showreport_onpage_url.indexOf("?")>0)
            {
                url=Config.showreport_onpage_url+"&";
            }else
            {
                url=Config.showreport_onpage_url+"?";
            }
            url=url+"PAGEID="+pageid;
        }
        this.pageObj=(PageType)pagebean.createComponentTypeObj(this,null);
        lstPageInterceptors=new ArrayList<AbsPageInterceptor>();
        lstPageInterceptors.addAll(Config.getInstance().getLstGlobalPageInterceptors(pageid));
        lstPageInterceptors.addAll(pagebean.getLstInterceptors());
        for(AbsPageInterceptor interceptorTmp:lstPageInterceptors)
        {//依次执行所有全局级页面拦截器
            interceptorTmp.doStart(this);
        }
        this.pageskin=Config.getInstance().getSkin(this.request,this.pagebean);
        if(this.showtype==Consts.DISPLAY_ON_PAGE)
        {
            initDisplayOnPage(pageid);
        }else
        {
            initNonDisplayOnPage();
        }
    }

    private void initDisplayOnPage(String pageid)
    {
        UpdateComponentDataServerActionBean.initServerActionBean(this);
        String ancestorUrls=getStringAttribute("ancestorPageUrls","");
        if(!ancestorUrls.equals(""))
        {
            this.lstAncestorUrls=Tools.parseStringToList(ancestorUrls,"||");
        }
        String slaveid=this.getStringAttribute("SLAVE_REPORTID","");
        if(!slaveid.equals(""))
        {
            initSlaveReport(slaveid);
        }else
        {
            String refreshComponentGuid=this.getStringAttribute("refreshComponentGuid","");
            if(!this.isLoadedByAjax()||refreshComponentGuid.equals("")||this.pagebean.getId().equals(refreshComponentGuid))
            {
                this.refreshComponentBean=this.pagebean;
                refreshComponentTypeObj=pageObj;
            }else
            {
                if(refreshComponentGuid.startsWith("[DYNAMIC]"))
                {
                    String refreshId=getDynamicRefreshIdOfAllComponents(refreshComponentGuid);
                    String refreshSlaveReportid=null;
                    if(!refreshId.equals(pagebean.getId()))
                    {
                        IComponentConfigBean refreshCcbTmp=pagebean.getChildComponentBean(refreshId,true);
                        if(refreshCcbTmp instanceof ReportBean)
                        {
                            if(((ReportBean)refreshCcbTmp).isSlaveReportDependsonListReport())
                            {
                                refreshSlaveReportid=refreshCcbTmp.getId();
                            }
                        }
                    }
                    if(refreshSlaveReportid==null||refreshSlaveReportid.trim().equals(""))
                    {
                        initCommonComponent(refreshId);
                        this.wresponse.setDynamicRefreshComponentGuid(refreshComponentBean.getGuid(),null);
                    }else
                    {
                        initSlaveReport(refreshSlaveReportid);
                        this.wresponse.setDynamicRefreshComponentGuid(refreshComponentBean.getGuid(),refreshSlaveReportid);
                        return;
                    }
                }else
                {
                    if(!refreshComponentGuid.startsWith(pagebean.getId()+Consts_Private.GUID_SEPERATOR))
                    {
                        this.wresponse.getMessageCollector().warn("刷新页面失败","传入的刷新组件GUID："+refreshComponentGuid+"不合法",true,Consts.STATECODE_FAILED);
                    }
                    initCommonComponent(refreshComponentGuid.substring((pagebean.getId()+Consts_Private.GUID_SEPERATOR).length()).trim());
                }
            }
            pageObj.initUrl(pagebean,this);
            if(refreshComponentTypeObj instanceof AbsReportType)
            {
                lstAllReportBeans=new ArrayList<ReportBean>();
                lstAllReportBeans.add((ReportBean)refreshComponentBean);
                ((AbsReportType)refreshComponentTypeObj).init();
                ((AbsReportType)refreshComponentTypeObj).loadReportData(true);
            }else if(refreshComponentTypeObj instanceof AbsContainerType)
            {
                this.lstAllReportBeans=((AbsContainerType)refreshComponentTypeObj).initDisplayOnPage();
            }else
            {

            }
        }
    }

    private String getDynamicRefreshIdOfAllComponents(String refreshComponentGuid)
    {
        String componentIds=refreshComponentGuid.substring("[DYNAMIC]".length()).trim();
        if(componentIds.equals(""))
        {
            this.wresponse.getMessageCollector().warn("刷新页面失败","没有取到refreshComponentGuid"+refreshComponentGuid,true,Consts.STATECODE_FAILED);
        }
        List<String> lstComponentids=Tools.parseStringToList(componentIds,";");
        String refreshId=null;
        IComponentConfigBean ccbTmp;
        for(String cidTmp:lstComponentids)
        {
            if(cidTmp==null||cidTmp.trim().equals("")) continue;
            if(cidTmp.equals(pagebean.getId()))
            {//如果当前组件ID是页面ID，则不用再看其它组件了，本次就是刷新整个页面
                refreshId=pagebean.getId();
                break;
            }
            ccbTmp=pagebean.getChildComponentBean(cidTmp,true);
            if(ccbTmp==null) this.wresponse.getMessageCollector().warn("刷新页面失败","在页面"+pagebean.getId()+"下没有取到ID为"+cidTmp+"的组件",true,Consts.STATECODE_FAILED);
            if(refreshId==null||refreshId.equals(""))
            {
                refreshId=ccbTmp.getRefreshid();
            }else
            {
                refreshId=pagebean.getCommonRefreshIdOfComponents(refreshId,ccbTmp.getRefreshid());
                if(refreshId.equals(pagebean.getId())) break;
            }
        }
        if(refreshId==null||refreshId.trim().equals(""))
        {
            this.wresponse.getMessageCollector().warn("刷新页面失败","刷新页面失败，根据"+refreshComponentGuid+"没有取到真正的刷新页面ID",true,Consts.STATECODE_FAILED);
        }
        return refreshId;
    }

    private void initCommonComponent(String refreshComponentid)
    {
        if(refreshComponentid.equals("")||refreshComponentid.equals(pagebean.getId()))
        {
            this.refreshComponentBean=this.pagebean;
            refreshComponentTypeObj=pageObj;
        }else
        {
            this.refreshComponentBean=this.pagebean.getChildComponentBean(refreshComponentid,true);
            if(refreshComponentBean==null)
            {
                wresponse.getMessageCollector().warn("加载页面失败","没有取到ID为"+refreshComponentid+"的组件",true,Consts.STATECODE_FAILED);
            }
            refreshComponentTypeObj=getComponentTypeObj(refreshComponentBean,null,true);
        }
    }

    private void initSlaveReport(String slaveid)
    {
        this.slaveReportBean=this.pagebean.getReportChild(slaveid,true);
        if(slaveReportBean==null)
        {
            wresponse.getMessageCollector().warn("没有取到ID为"+slaveid+"的从报表",true,Consts.STATECODE_FAILED);
        }
        slaveReportTypeObj=(AbsReportType)getComponentTypeObj(slaveReportBean,null,true);
        slaveReportTypeObj.initUrl(slaveReportBean,this);
        slaveReportTypeObj.init();
        slaveReportTypeObj.loadReportData(true);
        lstAllReportBeans=new ArrayList<ReportBean>();
        lstAllReportBeans.add(slaveReportBean);
        this.refreshComponentBean=slaveReportBean;
    }

    private void initNonDisplayOnPage()
    {
        String comids=this.getStringAttribute("COMPONENTIDS","");
        String applicationids=this.getStringAttribute("INCLUDE_APPLICATIONIDS","");
        if(comids.equals(""))
        {
            this.wresponse.getMessageCollector().warn("页面初始化失败","没有传入对页面"+this.pagebean.getId()+"进行打印/数据导出等操作所针对的组件ID",null,Consts.STATECODE_FAILED);
        }
        if(applicationids.trim().equals(""))
        {
            this.wresponse.getMessageCollector().warn("初始化失败","没有取到页面"+this.pagebean.getId()+"要操作的应用",null,Consts.STATECODE_FAILED);
        }
        this.lstComponentBeans=new ArrayList<IComponentConfigBean>();
        List<String> lstComids=new UniqueArrayList<String>();
        lstComids.addAll(Tools.parseStringToList(comids,";",false));
        for(String comidTmp:lstComids)
        {
            if(comidTmp.equals(pagebean.getId()))
            {
                this.lstComponentBeans.add(pagebean);
            }else
            {
                this.lstComponentBeans.add(pagebean.getChildComponentBean(comidTmp,true));
            }
        }
        this.lstApplicationIds=new UniqueArrayList<String>();
        this.lstApplicationIds.addAll(Tools.parseStringToList(applicationids,";",false));
        this.lstAllReportBeans=new UniqueArrayList<ReportBean>();
        IApplicationConfigBean appbeanTmp;
        for(String appidTmp:this.lstApplicationIds)
        {
            if(appidTmp==null||appidTmp.trim().equals("")) continue;
            appbeanTmp=pagebean.getApplicationChild(appidTmp,true);
            if(appbeanTmp==null) this.wresponse.getMessageCollector().warn("导出/打印应用数据失败","在页面"+this.pagebean.getId()+"中没有取到ID为"+appidTmp+"的应用",null,Consts.STATECODE_FAILED);
            if(appbeanTmp instanceof ReportBean) lstAllReportBeans.add((ReportBean)appbeanTmp);
        }
        if(this.showtype==Consts.DISPLAY_ON_PDF)
        {
            this.mReportidsInPdfTemplate=new HashMap<String,String>();
            boolean ispdfprint=this.isPdfPrintAction();
            for(IComponentConfigBean ccbeanTmp:this.lstComponentBeans)
            {
                PDFExportBean pdfbeanTmp=null;
                if(ispdfprint)
                {
                    pdfbeanTmp=ccbeanTmp.getPdfPrintBean();
                }else if(ccbeanTmp.getDataExportsBean()!=null)
                {
                    pdfbeanTmp=(PDFExportBean)ccbeanTmp.getDataExportsBean().getDataExportBean(Consts.DATAEXPORT_PDF);
                }
                if(pdfbeanTmp!=null&&pdfbeanTmp.getPdftemplate()!=null&&!pdfbeanTmp.getPdftemplate().trim().equals(""))
                {
                    for(String appidTmp:pdfbeanTmp.getLstIncludeApplicationids())
                    {
                        this.mReportidsInPdfTemplate.put(appidTmp,"true");
                    }
                }
            }
        }
        if(lstAllReportBeans!=null)
        {
            AbsReportType reportObjTmp;
            for(ReportBean rbeanTmp:lstAllReportBeans)
            {
                reportObjTmp=(AbsReportType)getComponentTypeObj(rbeanTmp,null,true);
                reportObjTmp.init();
                reportObjTmp.loadReportData(true);
            }
        }
    }

    public boolean isReportInPdfTemplate(String reportid)
    {
        if(this.showtype!=Consts.DISPLAY_ON_PDF) return false;
        if(this.mReportidsInPdfTemplate==null) return false;
        return "true".equals(this.mReportidsInPdfTemplate.get(reportid));
    }
    
    public boolean isPdfPrintAction()
    {
        if(this.showtype!=Consts.DISPLAY_ON_PDF) return false;
        return this.getStringAttribute("WX_IS_PDFPRINT_ACTION","").equals("true");
    }
    
    public void setFilterCondition(ReportBean rbean)
    {
        String colFilterId=getStringAttribute(rbean.getId()+"_COL_FILTERID","");
        if(!colFilterId.equals(""))
        {
            AbsListReportDisplayBean alrdbean=(AbsListReportDisplayBean)rbean.getDbean().getExtendConfigDataForReportType(AbsListReportType.KEY);
            AbsListReportFilterBean filterbean=alrdbean.getFilterBeanById(colFilterId);
            if(filterbean!=null&&!filterbean.isConditionRelate())
            {
                String filterVal=getStringAttribute(colFilterId,"");
                if(!filterVal.trim().equals(""))
                {
                    getCdb(rbean.getId()).setFilteredBean(filterbean);
                }
            }
        }
    }
    
    
    private List<String> lstProcessedTargetPageIds;
    public String forwardPageWithBack(String url)
    {
        return forwardPageWithBack(url,null);
    }
    
    public String forwardPageWithBack(String url,String beforeCallbackMethodName)
    {
        if(this.getShowtype()!=Consts.DISPLAY_ON_PAGE) return "";
        this.pagebean.setShouldProvideEncodePageUrl(true);
        String targetpageid=Tools.getParamvalueFromUrl(url,"PAGEID");
        targetpageid=targetpageid==null?"":targetpageid.trim();
        if(!this.isLoadedByAjax()&&!targetpageid.equals("")&&!(lstProcessedTargetPageIds!=null&&lstProcessedTargetPageIds.contains(targetpageid)))
        {
            PageBean pbeanTarget=Config.getInstance().getPageBean(targetpageid);
            if(pbeanTarget!=null)
            {
                if(lstProcessedTargetPageIds==null) lstProcessedTargetPageIds=new ArrayList<String>();
                lstProcessedTargetPageIds.add(targetpageid);
                this.pageObj.addDynJsFileBeans(pbeanTarget.getLstMyJavascriptFiles());
                this.pageObj.addDynCsses(pbeanTarget.getUlstMyCss());
            }
        }
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("forwardPageWithBack('").append(this.pagebean.getId()).append("','");
        resultBuf.append(Tools.jsParamEncode(url)).append("',");
        if(beforeCallbackMethodName!=null&&!beforeCallbackMethodName.trim().equals(""))
        {
            resultBuf.append(beforeCallbackMethodName);
        }else
        {
            resultBuf.append("null");
        }
        resultBuf.append(")");
        return resultBuf.toString();
    }
    
    public Object getAttribute(String name)
    {
        return attributes.get(name);
    }

    public String getStringAttribute(String name)
    {
        Object val=attributes.get(name);
        if(val==null) return null;
        String value=null;
        if(val instanceof String[])
        {
            String[] temp=(String[])val;
            StringBuffer tempBuf=new StringBuffer();
            if(temp.length>0)
            {
                for(int i=0;i<temp.length;i++)
                {
                    tempBuf.append("["+temp[i]+"]");
                }
            }
            log.warn("根据参数名"+name+"获取的参数值为一个数组，其值分别为："+tempBuf.toString()+"，将以"+temp[0]+"做为其参数值");
            value=temp[0];
        }else if(val instanceof String)
        {
            value=(String)val;
        }
        return value;
    }

    public String getStringAttribute(String name,String defaultvalue)
    {
        String value=getStringAttribute(name);
        if(value==null||value.trim().equals(""))
        {
            value=defaultvalue;
        }else
        {
            value=value.trim();
        }
        return value;
    }

    public int getIntAttribute(String name,int defaultvalue)
    {
        String value=getStringAttribute(name);
        if(value==null||value.trim().equals(""))
        {
            return defaultvalue;
        }else
        {
            try
            {
                return Integer.parseInt(value.trim());
            }catch(NumberFormatException e)
            {
                return defaultvalue;
            }
        }
    }

    public Object getAttribute(String reportid,String name)
    {
        CacheDataBean cdb=this.getCdb(reportid);
        if(cdb==null)
        {
            return null;
        }
        return cdb.getAttributes().get(name);
    }

    public Object getAttribute(String reportid,String name,Object defaultvalue)
    {
        Object o=getAttribute(reportid,name);
        if(o==null)
        {
            o=defaultvalue;
        }
        return o;
    }

    public String getStringAttribute(String reportid,String name,String defaultvalue)
    {
        Object obj=getAttribute(reportid,name);
        if(obj==null||!(obj instanceof String)) return defaultvalue;
        return ((String)obj).trim();
    }

    public int getIntAttribute(String reportid,String name,int defaultvalue)
    {
        String val=getStringAttribute(reportid,name,"");
        if(val==null||val.equals("")) return defaultvalue;
        try
        {
            return Integer.parseInt(val);
        }catch(Exception e)
        {
            log.error("",e);
        }
        return defaultvalue;
    }

    public boolean getBolAttribute(String reportid,String name,boolean defaultvalue)
    {
        String val=getStringAttribute(reportid,name,"");
        if(val==null||val.equals("")) return defaultvalue;
        try
        {
            return Boolean.parseBoolean(val);
        }catch(Exception e)
        {
            log.error("",e);
        }
        return defaultvalue;
    }

    public void setObjKeyAttribute(Object key,Object value)
    {
        this.attributes.put(key,value);
    }

    public Object getObjKeyAttribute(Object key)
    {
        if(this.attributes==null) return null;
        return this.attributes.get(key);
    }

    public void setAttribute(String name,Object value)
    {
        if(this.isShouldAddToUrlAttributeName(name))
        {
            if(value==null)
            {
                this.addParamToUrl(name,null,true);
            }else
            {
                this.addParamToUrl(name,value.toString(),true);
            }
        }
        this.attributes.put(name,value);
    }

    public void setAttribute(String reportid,String name,Object value)
    {
        if(this.isShouldAddToUrlAttributeName(reportid,name))
        {//如果需要更新到URL中
            if(value==null)
            {
                this.addParamToUrl(name,null,true);
            }else
            {
                this.addParamToUrl(name,value.toString(),true);
            }
        }
        this.getCdb(reportid).getAttributes().put(name,value);
    }

    private void setAttribute(HttpServletRequest request,String name,String defaultvalue)
    {
        String value=Tools.getRequestValue(request,name,defaultvalue);
        this.attributes.put(name,value);
    }

    public void removeAttribute(String name)
    {
        this.attributes.remove(name);

    }



//        //        CacheDataBean cdb = this.getCdb(reportid);
//        //        if (cdb == null || !(value instanceof String)) return;





//        {//如果此name属性是此报表的动态查询条件的name属性配置值





    public TransactionTypeWrapper getTransactionWrapper()
    {
        return transactionWrapper;
    }
    
    public void setTransactionObj(ITransactionType transactionObj)
    {
        this.transactionWrapper=new TransactionTypeWrapper(transactionObj);
    }

    public HttpServletRequest getRequest()
    {
        return request;
    }

    public boolean isDisplayOnPage()
    {
        if(this.showtype==Consts.DISPLAY_ON_PAGE) return true;
        if(this.showtype==Consts.DISPLAY_ON_PRINT&&this.lstComponentBeans.get(0).getPrintBean() instanceof DefaultPrintProviderConfigBean)
        {
            return true;
        }
        return false;
    }
    
    public IComponentType getComponentTypeObj(String componentId,AbsContainerType parentContainer,boolean createNew)
    {
        if(mComponentTypeObjs!=null&&mComponentTypeObjs.containsKey(componentId))
        {
            return mComponentTypeObjs.get(componentId);
        }
        if(!createNew) return null;
        IComponentConfigBean ccbean=null;
        if(this.pagebean.getId().equals(componentId))
        {
            ccbean=this.pagebean;
        }else
        {
            ccbean=this.pagebean.getChildComponentBean(componentId,true);
        }
        if(ccbean==null) return null;
        return getComponentTypeObj(ccbean,parentContainer,createNew);
    }
    
    public IComponentType getComponentTypeObj(IComponentConfigBean componentConfigBean,AbsContainerType parentContainer,boolean createNew)
    {
        if(this.mComponentTypeObjs==null) mComponentTypeObjs=new HashMap<String,IComponentType>();
        IComponentType typeObj=mComponentTypeObjs.get(componentConfigBean.getId());
        if(parentContainer!=null&&typeObj!=null&&typeObj.getParentContainerType()==null)
        {
            typeObj.setParentContainerType(parentContainer);
        }
        if(!createNew||typeObj!=null) return typeObj;
        typeObj=componentConfigBean.createComponentTypeObj(this,parentContainer);
        mComponentTypeObjs.put(componentConfigBean.getId(),typeObj);
        return typeObj;
    }
    
    public AbsReportType getDisplayReportTypeObj(String reportid)
    {
        if(reportid==null||reportid.trim().equals("")) return null;
        IComponentType reportObj=getComponentTypeObj(reportid,null,false);
        if(reportObj==null||!(reportObj instanceof AbsReportType))
        {
            throw new WabacusRuntimeException("在页面"+this.pagebean.getPath()+"中没有找到id为"+reportid+"的报表");
        }
        return (AbsReportType)reportObj;
    }

    public AbsReportType getDisplayReportTypeObj(ReportBean rbean)
    {
        IComponentType reportObj=getComponentTypeObj(rbean,null,false);
        if(reportObj==null||!(reportObj instanceof AbsReportType))
        {
            throw new WabacusRuntimeException("在页面"+this.pagebean.getPath()+"中没有取到"+rbean.getPath()+"报表");
        }
        return (AbsReportType)reportObj;
    }
    
    public List<String> getLstApplicationIds()
    {
        return lstApplicationIds;
    }

    public String getCurrentStatus()
    {
        StringBuffer sbuffer=new StringBuffer();
        if(this.attributes!=null)
        {
            Iterator itKeys=this.attributes.keySet().iterator();
            while(itKeys.hasNext())
            {
                String key=(String)itKeys.next();
                Object value=attributes.get(key);
                if(value!=null&&value instanceof String)
                {
                    sbuffer.append(key+"="+value+";");
                }
            }
            return sbuffer.toString();
        }
        return "";
    }

    public PageBean getPagebean()
    {
        return pagebean;
    }

    public CacheDataBean getCdb(String reportid)
    {
        if(this.mCacheDataBeans==null||this.mCacheDataBeans.size()==0||!mCacheDataBeans.containsKey(reportid))
        {
            ReportBean rbtemp=this.pagebean.getReportChild(reportid,true);
            if(rbtemp==null)
            {
                throw new WabacusRuntimeException("在页面："+this.pagebean.getPath()+"中没有配置id为"+reportid
                        +"的报表，无法根据此id调用ReportRequest的getCdb(String reportid)方法");
            }
            CacheDataBean cdb=new CacheDataBean(this);
            cdb.setReportBean(rbtemp);
            this.mCacheDataBeans.put(rbtemp.getId(),cdb);
        }
        return (CacheDataBean)mCacheDataBeans.get(reportid);
    }
    
    public Connection getConnection()
    {
        return getConnection(Config.getInstance().getDefault_datasourcename());
    }
    
    public Connection getConnection(String datasource)
    {
        if(Tools.isDefineKey("i18n",datasource))
        {
            datasource=Tools.getRealKeyByDefine("i18n",datasource);
            if(this.locallanguage!=null&&!this.locallanguage.trim().equals(""))
            {
                String dsTemp=datasource+"_"+this.locallanguage;
                Connection conn=connections.get(dsTemp);
                if(conn==null)
                {
                    conn=Config.getInstance().getDataSource(dsTemp).getConnection();
                    connections.put(dsTemp,conn);
                }
                if(conn!=null)
                {
                    log.debug("采用数据源："+dsTemp+"获取数据库连接");
                    return conn;
                }else
                {
                    log.warn("没有在系统配置文件中配置名为"+datasource+"的数据源，可能是不支持"+this.locallanguage+"语言");
                }
            }
        }
        if(datasource==null||datasource.trim().equals("")||datasource.trim().equals(Consts.DEFAULT_KEY)) datasource=Config.getInstance().getDefault_datasourcename();
        if(connections.get(datasource)==null)
        {
            connections.put(datasource,Config.getInstance().getDataSource(datasource).getConnection());
        }
        return connections.get(datasource);
    }

    public AbsDatabaseType getDbType(String datasource_name)
    {
        if(Tools.isDefineKey("i18n",datasource_name))
        {
            datasource_name=Tools.getRealKeyByDefine("i18n",datasource_name);
            if(this.locallanguage!=null&&!this.locallanguage.trim().equals(""))
            {
                String dsTemp=datasource_name+"_"+this.locallanguage;
                AbsDatabaseType dbtype=mDbTypes.get(dsTemp);
                if(dbtype==null)
                {
                    dbtype=Config.getInstance().getDataSource(dsTemp).getDbType();
                    mDbTypes.put(dsTemp,dbtype);
                }
                if(dbtype!=null)
                {
                    return dbtype;
                }else
                {
                    log.warn("没有在系统配置文件中配置名为"+datasource_name+"的数据源，可能是不支持"+this.locallanguage+"语言");
                }
            }
        }
        if(datasource_name==null||datasource_name.trim().equals("")||datasource_name.trim().equals(Consts.DEFAULT_KEY)) datasource_name=Config.getInstance().getDefault_datasourcename();
        if(mDbTypes.get(datasource_name)==null)
        {
            mDbTypes.put(datasource_name,Config.getInstance().getDataSource(datasource_name).getDbType());
        }
        return mDbTypes.get(datasource_name);
    }

    public void setTransactionLevel(String ds_name,String translevel)
    {
        if(translevel==null||translevel.trim().equals("")) return;
        if(ds_name==null||ds_name.trim().equals("")||ds_name.trim().equals(Consts.DEFAULT_KEY)) ds_name=Config.getInstance().getDefault_datasourcename();
        if(mTransactionLevels==null) mTransactionLevels=new HashMap<String,String>();
        if(!Consts_Private.M_ALL_TRANSACTION_LEVELS.containsKey(translevel))
        {
            throw new WabacusRuntimeException("设置事务隔离级别："+translevel+"不合法，不支持这个事务隔离级别");
        }
        mTransactionLevels.put(ds_name,translevel);
    }
    
    public String getTransactionLevel(String ds_name)
    {
        if(mTransactionLevels==null) return null;
        if(ds_name==null||ds_name.trim().equals("")||ds_name.trim().equals(Consts.DEFAULT_KEY)) ds_name=Config.getInstance().getDefault_datasourcename();
        return mTransactionLevels.get(ds_name);
    }
    
    public boolean isLoadedByAjax()
    {
        return getStringAttribute("WX_ISAJAXLOAD","").equalsIgnoreCase("true");
    }
    
    public boolean isSearchReportAction(String reportid)
    {
        if(lstSearchReportIds==null)
        {
            lstSearchReportIds=new ArrayList<String>();
            String searchreportid=this.getStringAttribute("SEARCHREPORT_ID","");
            if(searchreportid.equals("")) return false;
            ReportBean rbean=this.pagebean.getReportChild(searchreportid,true);
            if(rbean==null) return false;
            lstSearchReportIds.add(reportid);
            if(rbean.getSRelateConditionReportids()!=null) lstSearchReportIds.addAll(rbean.getSRelateConditionReportids());
        }
        return lstSearchReportIds.contains(reportid);
    }
    
    public String addParamToUrl(String paramname,String paramvalue,boolean overwrite)
    {
        if(!overwrite&&(url.indexOf("&"+paramname+"=")>=0||url.indexOf("?"+paramname+"=")>=0))
        {//如果不覆盖，且URL中已经存在此参数名的参数，则不将新参数加入
            return url;
        }
        if(Tools.isDefineKey("rrequest",paramvalue))
        {
            paramvalue=Tools.getRealKeyByDefine("rrequest",paramvalue);
            paramvalue=this.getStringAttribute(paramvalue);
        }
        try
        {
            if(paramvalue!=null)
            {
                paramvalue=Tools.replaceAll(paramvalue," ","WX_BLANK_BLANK_BLANK_BLANK_BLANK_BLANK_BLANK");
                paramvalue=URLEncoder.encode(paramvalue,"utf-8");
                paramvalue=Tools.replaceAll(paramvalue,"WX_BLANK_BLANK_BLANK_BLANK_BLANK_BLANK_BLANK","%20");
            }
        }catch(UnsupportedEncodingException e)
        {
            log.warn("URL编码字符串"+paramvalue+"失败",e);
        }
        url=Tools.replaceUrlParamValue(url,paramname,paramvalue);
        return url;
    }

    public void authorize(String componentId,String parttype,String partid,String permissiontype,String permissionvalue)
    {
        if(mComponentsPermissions==null) mComponentsPermissions=new HashMap<String,ComponentPermissionBean>();
        ComponentPermissionBean cabean=this.mComponentsPermissions.get(componentId);
        if(cabean==null)
        {
            if(this.pagebean.getId().equals(componentId))
            {
                cabean=new ComponentPermissionBean(this.pagebean);
            }else
            {
                IComponentConfigBean ccbean=this.pagebean.getChildComponentBean(componentId,true);
                if(ccbean==null)
                {
                    throw new WabacusRuntimeException("在页面"+this.pagebean.getId()+"中没有取到ID为"+componentId+"的组件，无法给它授权");
                }
                if(Consts.DATA_PART.equals(parttype)&&Consts.PERMISSION_TYPE_DISPLAY.equals(permissiontype)
                        &&"false".equalsIgnoreCase(permissionvalue)&&partid!=null&&!partid.trim().equals("")&&ccbean instanceof ReportBean)
                {
                    this.setAttribute(componentId,"authroize_col_display","false");
                }
                cabean=new ComponentPermissionBean(ccbean);
            }
            cabean.setRRequest(this);
            this.mComponentsPermissions.put(componentId,cabean);
        }
        cabean.authorize(parttype,partid,permissiontype,permissionvalue);
    }

    public boolean checkPermission(String componentId,String parttype,String partid,String permissiontype)
    {
        return checkPermission(componentId,parttype,partid,permissiontype,"true");
    }

    public boolean checkPermission(String componentId,String parttype,String partid,String permissiontype,String permissionvalue)
    {
        if(componentId==null||componentId.trim().equals("")) return false;
        if(!this.pagebean.isCheckPermission())
        {
            return AuthorizationAssistant.getInstance().checkDefaultPermissionTypeValue(permissiontype,permissionvalue);
        }
        if(!AuthorizationAssistant.getInstance().isExistPermissiontype(permissiontype)) return false;
        if(!AuthorizationAssistant.getInstance().isExistValueOfPermissiontype(permissiontype,permissionvalue)) return false;
        
        String myPermissionKey=componentId;
        if(parttype!=null&&!parttype.trim().equals("")) myPermissionKey=myPermissionKey+"_"+parttype.trim();
        if(partid!=null&&!partid.trim().equals("")) myPermissionKey=myPermissionKey+"_"+partid.trim();
        myPermissionKey=myPermissionKey+"_"+permissiontype;
        if(permissionvalue!=null&&!permissionvalue.trim().equals("")) myPermissionKey=myPermissionKey+"_"+permissionvalue.trim();
        myPermissionKey=myPermissionKey+"_permission";
        Boolean bolHasPermission=(Boolean)this.getAttribute(myPermissionKey);
        if(bolHasPermission!=null)
        {
            return bolHasPermission.booleanValue();
        }
        if(!this.pagebean.getId().equals(componentId)||(parttype!=null&&!parttype.trim().equals("")))
        {//如果当前不是判断<page/>本身的权限，即有父组件，则先判断父组件的权限
            boolean parentPermisson=checkParentPermission(componentId,parttype,partid,permissiontype,permissionvalue);
            if(AuthorizationAssistant.getInstance().isConsistentWithParentPermission(permissiontype,permissionvalue,parentPermisson))
            {//如果父组件的此权限类型的权限值可以决定当前子组件/元素的权限值，则直接返回父组件的权限结果（例如当父组件为readonly为true时，则所有子组件及组件上的元素的display都为true）
                this.setAttribute(myPermissionKey,parentPermisson);
                return parentPermisson;
            }
        }
        //父组件/元素的权限不能决定当前组件/元素的权限，则再判断自己的权限
        int permission=checkRuntimePermission(componentId,parttype,partid,permissiontype,permissionvalue);
        if(permission==Consts.CHKPERMISSION_YES)
        {
            this.setAttribute(myPermissionKey,true);
            return true;
        }
        if(permission==Consts.CHKPERMISSION_NO)
        {
            this.setAttribute(myPermissionKey,false);
            return false;//显示授的权限值不是当前权限值
        }
        
        IComponentConfigBean ccbean=null;
        if(this.pagebean.getId().equals(componentId))
        {
            ccbean=this.pagebean;
        }else
        {
            ccbean=this.pagebean.getChildComponentBean(componentId,true);
            if(ccbean==null) throw new WabacusRuntimeException("页面"+this.pagebean.getId()+"下没有ID为"+componentId+"的组件");
        }
        permission=Config.getInstance().checkDefaultPermission(ccbean.getGuid(),parttype,partid,permissiontype,permissionvalue);
        if(permission==Consts.CHKPERMISSION_YES)
        {
            this.setAttribute(myPermissionKey,true);
            return true;//显式授了此组件/元素的默认权限值
        }
        if(permission==Consts.CHKPERMISSION_NO)
        {
            this.setAttribute(myPermissionKey,false);
            return false;//显示授了此组件/元素不具有此默认权限值
        }
        
        boolean defaultpermission=AuthorizationAssistant.getInstance().checkDefaultPermissionTypeValue(permissiontype,permissionvalue);
        this.setAttribute(myPermissionKey,defaultpermission);
        return defaultpermission;
    }
    
    private boolean checkParentPermission(String componentId,String parttype,String partid,String permissiontype,String permissionvalue)
    {
        if(componentId==null||componentId.trim().equals(""))
        {
            throw new WabacusRuntimeException("判断组件权限失败，没有传入组件ID");
        }
        if(partid!=null&&!partid.trim().equals(""))
        {
            if(parttype==null||parttype.trim().equals(""))
            {
                throw new WabacusRuntimeException("判断ID为"+componentId+"的组件权限时失败，传入partid时，必须传入其父parttype");
            }
            return checkPermission(componentId,parttype,null,permissiontype,permissionvalue);
        }
        if(parttype!=null&&!parttype.trim().equals(""))
        {
            return checkPermission(componentId,null,null,permissiontype,permissionvalue);
        }
        
        if(componentId.equals(this.pagebean.getId())) throw new WabacusRuntimeException("不能判断<page/>的父组件权限");
        IComponentConfigBean ccbean=this.pagebean.getChildComponentBean(componentId,true);
        return ccbean.getParentContainer().invokeCheckPermissionByChild(this,ccbean,permissiontype,permissionvalue);
    }
    
    public int checkRuntimePermission(String componentId,String parttype,String partid,String permissiontype)
    {
        return checkRuntimePermission(componentId,parttype,partid,permissiontype,"true");
    }

    public int checkRuntimePermission(String componentId,String parttype,String partid,String permissiontype,String permissionvalue)
    {
        if(!AuthorizationAssistant.getInstance().isExistPermissiontype(permissiontype)) return Consts.CHKPERMISSION_UNSUPPORTEDTYPE;
        if(!AuthorizationAssistant.getInstance().isExistValueOfPermissiontype(permissiontype,permissionvalue))
            return Consts.CHKPERMISSION_UNSUPPORTEDVALUE;
        if(mComponentsPermissions==null||this.mComponentsPermissions.get(componentId)==null) return Consts.CHKPERMISSION_EMPTY;
        return this.mComponentsPermissions.get(componentId).checkPermission(parttype,partid,permissiontype,permissionvalue);
    }
    
    public void disableAutoSuccessPrompt()
    {
        if(this.attributes==null) this.attributes=new HashMap();
        this.attributes.put("DISABLED_AUTO_SUCCESSED_PROMPT","true");
    }
    
    public boolean isDisableAutoSuccessPrompt()
    {
        return this.attributes!=null&&"true".equals(String.valueOf(this.attributes.get("DISABLED_AUTO_SUCCESSED_PROMPT")));
    }
    
    public void disableAutoFailedPrompt()
    {
        if(this.attributes==null) this.attributes=new HashMap();
        this.attributes.put("DISABLED_AUTO_FAILED_PROMPT","true");
    }
    
    public boolean isDisableAutoFailedPrompt()
    {
        return this.attributes!=null&&"true".equals(String.valueOf(this.attributes.get("DISABLED_AUTO_FAILED_PROMPT")));
    }
    
    public List<AbsPageInterceptor> getLstPageInterceptors()
    {
        return lstPageInterceptors;
    }

    public void destroy(boolean flag)
    {
        if(this.getActiontype().equalsIgnoreCase(Consts.SHOWREPORT_ACTION))
        {
            if(flag&&this.lstAllReportBeans!=null&&this.lstAllReportBeans.size()>0)
            {
                for(ReportBean rbeanTemp:lstAllReportBeans)
                {
                    if(rbeanTemp!=null&&rbeanTemp.getInterceptor()!=null)
                    {
                        rbeanTemp.getInterceptor().doEnd(this,rbeanTemp);
                    }
                }
            }
            if(this.lstPageInterceptors!=null&&this.lstPageInterceptors.size()>0)
            {
                AbsPageInterceptor interceptorTmp;
                for(int i=this.lstPageInterceptors.size()-1;i>=0;i--)
                {
                    interceptorTmp=this.lstPageInterceptors.get(i);
                    interceptorTmp.doEnd(this);
                }
            }
        }
        if(this.lstAllUsedStatement!=null&&this.lstAllUsedStatement.size()>0)
        {
            Statement stmt;
            for(int i=0;i<this.lstAllUsedStatement.size();i++)
            {
                stmt=this.lstAllUsedStatement.get(i);
                if(stmt==null) continue;
                WabacusAssistant.getInstance().release(null,stmt);
            }
            this.lstAllUsedStatement=null;
        }
        if(connections!=null&&connections.size()>0)
        {
            Iterator<String> itConns=connections.keySet().iterator();
            String name;
            while(itConns.hasNext())
            {
                name=itConns.next();
                WabacusAssistant.getInstance().release(connections.get(name),null);
            }
            connections.clear();
        }
    }

    public void setEditableReportAccessMode(String reportid,String newaccessmode)
    {
        IComponentType typeObj=this.getComponentTypeObj(reportid,null,true);
        if(!(typeObj instanceof AbsReportType))
        {
            throw new WabacusRuntimeException("页面"+this.pagebean.getId()+"不存在ID为"+reportid+"的组件或不是报表");
        }
        if(!(typeObj instanceof IEditableReportType))
        {
            throw new WabacusRuntimeException("页面"+this.pagebean.getId()+"中ID为"+reportid+"的报表不是可编辑报表");
        }
        ((IEditableReportType)typeObj).setNewAccessMode(newaccessmode);
    }
    
    public String getCurrentAccessMode(String reportid)
    {
        AbsReportType reportObj=getDisplayReportTypeObj(reportid);
        if(reportObj==null||!(reportObj instanceof IEditableReportType)) return "";
        return getStringAttribute(reportid,"CURRENT_ACCESSMODE",((IEditableReportType)reportObj).getDefaultAccessMode());
    }
    
    public List<Map<String,String>> getLstUpdatedData(ReportBean rbean)
    {
        EditableReportSqlBean ersqlbean=(EditableReportSqlBean)rbean.getSbean().getExtendConfigDataForReportType(EditableReportSqlBean.class);
        if(ersqlbean==null) return null;
        return this.getCdb(rbean.getId()).getLstEditedData(ersqlbean.getUpdatebean());
    }

    public List<Map<String,String>> getLstUpdatedData(String reportid)
    {
        ReportBean rbean=this.pagebean.getReportChild(reportid,true);
        if(rbean==null)
        {
            throw new WabacusRuntimeException("获取报表"+reportid+"失败，在页面"+this.pagebean.getPath()+"中没有定义此报表");
        }
        return getLstUpdatedData(rbean);
    }

    public List<Map<String,String>> getLstUpdatedParamValues(ReportBean rbean)
    {
        EditableReportSqlBean ersqlbean=(EditableReportSqlBean)rbean.getSbean().getExtendConfigDataForReportType(EditableReportSqlBean.class);
        if(ersqlbean==null) return null;
        return this.getCdb(rbean.getId()).getLstEditedParamValues(ersqlbean.getUpdatebean());
    }

    public List<Map<String,String>> getLstUpdatedParamValues(String reportid)
    {
        ReportBean rbean=this.pagebean.getReportChild(reportid,true);
        if(rbean==null)
        {
            throw new WabacusRuntimeException("获取报表"+reportid+"失败，在页面"+this.pagebean.getPath()+"中没有定义此报表");
        }
        return getLstUpdatedParamValues(rbean);
    }

    public List<Map<String,String>> getLstInsertedData(ReportBean rbean)
    {
        EditableReportSqlBean ersqlbean=(EditableReportSqlBean)rbean.getSbean().getExtendConfigDataForReportType(EditableReportSqlBean.class);
        if(ersqlbean==null) return null;
        return this.getCdb(rbean.getId()).getLstEditedData(ersqlbean.getInsertbean());
    }

    public List<Map<String,String>> getLstInsertedData(String reportid)
    {
        ReportBean rbean=this.pagebean.getReportChild(reportid,true);
        if(rbean==null)
        {
            throw new WabacusRuntimeException("获取报表"+reportid+"失败，在页面"+this.pagebean.getPath()+"中没有定义此报表");
        }
        return getLstInsertedData(rbean);
    }

    public List<Map<String,String>> getLstInsertedParamValues(ReportBean rbean)
    {
        EditableReportSqlBean ersqlbean=(EditableReportSqlBean)rbean.getSbean().getExtendConfigDataForReportType(EditableReportSqlBean.class);
        if(ersqlbean==null) return null;
        return this.getCdb(rbean.getId()).getLstEditedParamValues(ersqlbean.getInsertbean());
    }

    public List<Map<String,String>> getLstInsertedParamValues(String reportid)
    {
        ReportBean rbean=this.pagebean.getReportChild(reportid,true);
        if(rbean==null)
        {
            throw new WabacusRuntimeException("获取报表"+reportid+"失败，在页面"+this.pagebean.getPath()+"中没有定义此报表");
        }
        return getLstInsertedParamValues(rbean);
    }

    public List<Map<String,String>> getLstDeletedData(ReportBean rbean)
    {
        EditableReportSqlBean ersqlbean=(EditableReportSqlBean)rbean.getSbean().getExtendConfigDataForReportType(EditableReportSqlBean.class);
        if(ersqlbean==null) return null;
        return this.getCdb(rbean.getId()).getLstEditedData(ersqlbean.getDeletebean());
    }

    public List<Map<String,String>> getLstDeletedData(String reportid)
    {
        ReportBean rbean=pagebean.getReportChild(reportid,true);
        if(rbean==null)
        {
            throw new WabacusRuntimeException("获取报表"+reportid+"失败，在页面"+this.pagebean.getPath()+"中没有定义此报表");
        }
        return getLstDeletedData(rbean);
    }

    public List<Map<String,String>> getLstDeletedParamValues(ReportBean rbean)
    {
        EditableReportSqlBean ersqlbean=(EditableReportSqlBean)rbean.getSbean().getExtendConfigDataForReportType(EditableReportSqlBean.class);
        if(ersqlbean==null) return null;
        return this.getCdb(rbean.getId()).getLstEditedParamValues(ersqlbean.getDeletebean());
    }

    public List<Map<String,String>> getLstDeletedParamValues(String reportid)
    {
        ReportBean rbean=this.pagebean.getReportChild(reportid,true);
        if(rbean==null)
        {
            throw new WabacusRuntimeException("获取报表"+reportid+"失败，在页面"+this.pagebean.getPath()+"中没有定义此报表");
        }
        return getLstDeletedParamValues(rbean);
    }

    public Map<String,String> getMCustomizeEditData(ReportBean rbean)
    {
        return (Map<String,String>)this.getCdb(rbean.getId()).getAttributes().get("WX_UPDATE_CUSTOMIZEDATAS");
    }

    public Map<String,String> getMCustomizeEditData(String reportid)
    {
        return (Map<String,String>)this.getCdb(reportid).getAttributes().get("WX_UPDATE_CUSTOMIZEDATAS");
    }

    public List<Map<String,String>> getLstInvokeServerActionParams(String componentid)
    {
        if(this.serverActionBean==null||componentid==null||componentid.trim().equals("")) return null;
        if(!componentid.equals(this.serverActionBean.getComponentid())) return null;//当前调用服务器端操作不是针对componentid对应的组件
        return this.serverActionBean.getLstParams();
    }
    
    public String getColDisplayValue(String reportid,String property,int rowidx)
    {
        AbsReportType reportObj=getDisplayReportTypeObj(reportid);
        if(reportObj.getLstReportData()==null||reportObj.getLstReportData().size()==0) return null;
        if(rowidx<0) rowidx=0;
        if(rowidx>=reportObj.getLstReportData().size()) return null;
        ColBean cbean=reportObj.getReportBean().getDbean().getColBeanByColProperty(property);
        if(cbean==null) throw new WabacusRuntimeException("在报表"+reportObj.getReportBean().getPath()+"中没有找到property为"+property+"的列");
        return reportObj.getLstReportData().get(rowidx).getColStringValue(cbean);
    }

    public Object getColRealValue(String reportid,String property,int rowidx)
    {
        AbsReportType reportObj=getDisplayReportTypeObj(reportid);
        if(reportObj.getLstReportData()==null||reportObj.getLstReportData().size()==0) return null;
        if(rowidx<0) rowidx=0;
        if(rowidx>=reportObj.getLstReportData().size()) return null;
        ColBean cbean=reportObj.getReportBean().getDbean().getColBeanByColProperty(property);
        if(cbean==null) throw new WabacusRuntimeException("在报表"+reportObj.getReportBean().getPath()+"中没有找到property为"+property+"的列");
        return reportObj.getLstReportData().get(rowidx).getColValue(cbean);
    }

    public int getReportDataListSize(String reportid)
    {
        AbsReportType reportObj=getDisplayReportTypeObj(reportid);
        if(reportObj.getLstReportData()==null) return 0;
        return reportObj.getLstReportData().size();
    }

    public Object getReportDataObj(String reportid,int rowidx)
    {
        AbsReportType reportObj=getDisplayReportTypeObj(reportid);
        if(reportObj.getLstReportData()==null||reportObj.getLstReportData().size()==0) return null;
        if(rowidx<0) rowidx=0;
        if(rowidx>=reportObj.getLstReportData().size()) return null;
        return reportObj.getLstReportData().get(rowidx);
    }

    public Object getI18NObjectValue(String key)
    {
        if(key==null) return null;
        key=key.trim();
        if(key.equals("")||!key.startsWith("i18n")) return null;
        int len=key.length();
        key=Tools.getRealKeyByDefine("i18n",key);
        if(key.length()==len) return null;
        return Config.getInstance().getResources().getI18NObjectValue(key,this);
    }

    public Object getI18NObjectValue(String key,String localelanguage)
    {
        if(key==null) return null;
        key=key.trim();
        if(key.equals("")||!key.startsWith("i18n")) return null;
        int len=key.length();
        key=Tools.getRealKeyByDefine("i18n",key);
        if(key.length()==len) return null;
        return Config.getInstance().getResources().getI18NObjectValue(key,localelanguage);
    }
    
    public String getI18NStringValue(String key)
    {
        if(key==null) return key;
        key=key.trim();
        if(key.equals("")||!key.startsWith("i18n")) return key;
        int len=key.length();
        key=Tools.getRealKeyByDefine("i18n",key);
        if(key.length()==len) return key;
        return Config.getInstance().getResources().getI18NStringValue(key,this);
    }
    
    public String getI18NStringValue(String key,String localelanguage)
    {
        if(key==null) return key;
        key=key.trim();
        if(key.equals("")||!key.startsWith("i18n")) return key;
        int len=key.length();
        key=Tools.getRealKeyByDefine("i18n",key);
        if(key.length()==len) return key;
        return Config.getInstance().getResources().getI18NStringValue(key,localelanguage);
    }
    
    public Map<String,List<ColAndGroupDisplayBean>> getMColAndGroupDisplayBeans()
    {
        return mColAndGroupDisplayBeans;
    }

    public void addColAndGroupDisplayBean(String reportid,List<ColAndGroupDisplayBean> lstColAndGroupDisplayBeans)
    {
        if(mColAndGroupDisplayBeans==null)
        {
            mColAndGroupDisplayBeans=new HashMap<String,List<ColAndGroupDisplayBean>>();
        }
        mColAndGroupDisplayBeans.put(reportid,lstColAndGroupDisplayBeans);
    }

    public List<ReportBean> getLstAllReportBeans()
    {
        return lstAllReportBeans;
    }
    
    public void addShouldAddToUrlAttributeName(String reportid,String attributename)
    {
        if(this.showtype!=Consts.DISPLAY_ON_PAGE) return;
        if(attributename==null||attributename.trim().equals("")) return;
        if(this.mShouldAddToUrlAttriuteNames==null)
        {
            this.mShouldAddToUrlAttriuteNames=new HashMap<String,Set<String>>();
        }
        Set<String> sShouldAddToUrlAttriuteNames=this.mShouldAddToUrlAttriuteNames.get(reportid);
        if(sShouldAddToUrlAttriuteNames==null)
        {
            sShouldAddToUrlAttriuteNames=new HashSet<String>();
            this.mShouldAddToUrlAttriuteNames.put(reportid,sShouldAddToUrlAttriuteNames);
        }
        sShouldAddToUrlAttriuteNames.add(attributename);
    }
    
    public void addShouldAddToUrlAttributeName(String reportid,List<String> lstAttributeNames)
    {
        if(this.showtype!=Consts.DISPLAY_ON_PAGE) return;
        if(lstAttributeNames==null||lstAttributeNames.size()==0) return;
        if(this.mShouldAddToUrlAttriuteNames==null)
        {
            this.mShouldAddToUrlAttriuteNames=new HashMap<String,Set<String>>();
        }
        Set<String> sShouldAddToUrlAttriuteNames=this.mShouldAddToUrlAttriuteNames.get(reportid);
        if(sShouldAddToUrlAttriuteNames==null)
        {
            sShouldAddToUrlAttriuteNames=new HashSet<String>();
            this.mShouldAddToUrlAttriuteNames.put(reportid,sShouldAddToUrlAttriuteNames);
        }
        sShouldAddToUrlAttriuteNames.addAll(lstAttributeNames);
    }
    
    private boolean isShouldAddToUrlAttributeName(String attributename)
    {
        if(this.mShouldAddToUrlAttriuteNames==null) return false;
        for(Entry<String,Set<String>> entryTmp:this.mShouldAddToUrlAttriuteNames.entrySet())
        {
            if(entryTmp!=null&&entryTmp.getValue()!=null&&entryTmp.getValue().contains(attributename)) return true;
        }
        return false;
    }
    
    private boolean isShouldAddToUrlAttributeName(String reportid,String attributename)
    {
        if(this.mShouldAddToUrlAttriuteNames==null||this.mShouldAddToUrlAttriuteNames.get(reportid)==null) return false;
        return this.mShouldAddToUrlAttriuteNames.get(reportid).contains(attributename);
    }
    
    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url=url;
    }

    public int getShowtype()
    {
        return showtype;
    }

    public void setShowtype(int showtype)
    {
        this.showtype=showtype;
    }

    public PageType getPageObj()
    {
        return pageObj;
    }

    public String getActiontype()
    {
        return actiontype;
    }

    public void setActiontype(String actiontype)
    {
        this.actiontype=actiontype;
    }

    public Map getAttributes()
    {
        return attributes;
    }

    public void addUsedStatement(Statement stmt)
    {
        this.lstAllUsedStatement.add(stmt);
    }

    public String getLocallanguage()
    {
        return locallanguage;
    }

    public void setLocallanguage(String locallanguage)
    {
        this.locallanguage=locallanguage;
    }
    
    public WabacusResponse getWResponse()
    {
        return wresponse;
    }

    public void setWResponse(WabacusResponse wresponse)
    {
        this.wresponse=wresponse;
    }

    public IComponentConfigBean getRefreshComponentBean()
    {
        return refreshComponentBean;
    }

    public void setRefreshComponentBean(IComponentConfigBean refreshComponentBean)
    {
        this.refreshComponentBean=refreshComponentBean;
    }

    public ReportBean getSlaveReportBean()
    {
        return slaveReportBean;
    }

    public void setSlaveReportBean(ReportBean slaveReportBean)
    {
        this.slaveReportBean=slaveReportBean;
    }

    public AbsReportType getSlaveReportTypeObj()
    {
        return slaveReportTypeObj;
    }

    public void setSlaveReportTypeObj(AbsReportType slaveReportTypeObj)
    {
        this.slaveReportTypeObj=slaveReportTypeObj;
    }

    public IComponentType getRefreshComponentTypeObj()
    {
        return refreshComponentTypeObj;
    }

    public void setRefreshComponentTypeObj(IComponentType refreshComponentTypeObj)
    {
        this.refreshComponentTypeObj=refreshComponentTypeObj;
    }

    public Map<String,ComponentPermissionBean> getMComponentsPermissions()
    {
        return mComponentsPermissions;
    }

    public List<String> getLstAncestorUrls()
    {
        return lstAncestorUrls;
    }

    public UpdateComponentDataServerActionBean getServerActionBean()
    {
        return serverActionBean;
    }

    public void setServerActionBean(UpdateComponentDataServerActionBean serverActionBean)
    {
        this.serverActionBean=serverActionBean;
    }

    public String getPageskin()
    {
        return pageskin;
    }

    public List<IComponentConfigBean> getLstComponentBeans()
    {
        return this.lstComponentBeans;
    }

    private Set<String> setSaveSlaveReportIds;
    
    public Set<String> getSaveSlaveReportIdsSet()
    {
        if(setSaveSlaveReportIds==null)
        {
            setSaveSlaveReportIds=new HashSet<String>();
            String saveSlaveReportIds=this.getStringAttribute("SAVEDSLAVEREPORT_ROOTREPORT_IDS","");
            setSaveSlaveReportIds.addAll(Tools.parseStringToList(saveSlaveReportIds,";",false));
        }
        return setSaveSlaveReportIds;
    }
    
    public void addListReportWithDefaultSelectedRows(ReportBean rbean,boolean isSelected)
    {
        if(rbean==null) return;
        AbsListReportBean alrbean=(AbsListReportBean)rbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
        if(alrbean==null||alrbean.getRowSelectType()==null||alrbean.getRowSelectType().trim().equals("")
                ||Consts.ROWSELECT_NONE.equals(alrbean.getRowSelectType())) return;
        if(isSelected)
        {
            if(this.lstReportWithDefaultSelectedRows==null) this.lstReportWithDefaultSelectedRows=new ArrayList<String>();
            if(!this.lstReportWithDefaultSelectedRows.contains(rbean.getId())) this.lstReportWithDefaultSelectedRows.add(rbean.getId());
        }else if(this.lstReportWithDefaultSelectedRows!=null&&this.lstReportWithDefaultSelectedRows.contains(rbean.getId()))
        {
            this.lstReportWithDefaultSelectedRows.remove(rbean.getId());
        }
    }

    public List<String> getLstReportWithDefaultSelectedRows()
    {
        return lstReportWithDefaultSelectedRows;
    }
    
    public void addServerValidateParams(String paramname,String paramvalue)
    {
        if(this.mServerValidateDatas==null) this.mServerValidateDatas=new HashMap<String,String>();
        this.mServerValidateDatas.put(paramname,paramvalue);
    }

    public Map<String,String> getMServerValidateDatas()
    {
        return mServerValidateDatas;
    }
}
