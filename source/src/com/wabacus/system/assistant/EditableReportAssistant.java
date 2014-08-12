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
package com.wabacus.system.assistant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.database.type.AbsDatabaseType;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.exception.WabacusRuntimeWarningException;
import com.wabacus.system.CacheDataBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.buttons.EditableReportSQLButtonDataBean;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.component.application.report.abstractreport.IEditableReportType;
import com.wabacus.system.component.application.report.abstractreport.SaveInfoDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditActionBean;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditableReportEditDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditActionGroupBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportColBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportDeleteDataBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportExternalValueBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportParamBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportSecretColValueBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportSqlBean;
import com.wabacus.system.component.application.report.configbean.editablereport.transaction.DefaultTransactionType;
import com.wabacus.system.datatype.AbsDateTimeType;
import com.wabacus.system.intercept.AbsPageInterceptor;
import com.wabacus.system.intercept.IInterceptor;
import com.wabacus.util.Consts;
import com.wabacus.util.Consts_Private;
import com.wabacus.util.Tools;
import com.wabacus.util.UUIDGenerator;

public class EditableReportAssistant
{
    private static Log log=LogFactory.getLog(EditableReportAssistant.class);

    private final static EditableReportAssistant instance=new EditableReportAssistant();

    protected EditableReportAssistant()
    {}

    public static EditableReportAssistant getInstance()
    {
        return instance;
    }

    public String getInputBoxId(ColBean cbean)
    {
        if(cbean.getProperty()==null||cbean.getProperty().trim().equals("")) return "";
        return cbean.getReportBean().getGuid()+"_wxcol_"+cbean.getProperty();
    }

    public String getInputBoxId(ReportBean rbean,String property)
    {
        if(property==null||property.trim().equals("")) return "";
        return rbean.getGuid()+"_"+property;
    }
    
    public String getColParamName(ColBean cbean)
    {
        return cbean.getProperty();
    }
    
    public String getColParamValue(ReportRequest rrequest,Map<String,String> mColParamsValue,ColBean cbean)
    {
        return getColParamValue(rrequest,cbean.getReportBean(),mColParamsValue,getColParamName(cbean));
    }
    
    public String getColParamValue(ReportRequest rrequest,ReportBean rbean,Map<String,String> mColParamsValue,String paramname)
    {
        if(mColParamsValue==null) return null;
        String paramvalue=null;
        if(mColParamsValue.containsKey(Consts_Private.COL_NONDISPLAY_PERMISSION_PREX+paramname))
        {
            EditableReportSecretColValueBean secretColvalueBean=EditableReportSecretColValueBean.loadFromSession(rrequest,rbean);
            if(secretColvalueBean==null)
            {
                rrequest.getWResponse().getMessageCollector().warn("session过期，没有取到保存数据，请刷新后重试",null,true,Consts.STATECODE_FAILED);
            }
            String colkey=mColParamsValue.get(Consts_Private.COL_NONDISPLAY_PERMISSION_PREX+paramname);
            if(colkey==null||colkey.trim().equals("")) return null;
            if(!secretColvalueBean.containsColkey(colkey))
            {
                paramvalue=colkey;
            }else
            {
                paramvalue=secretColvalueBean.getParamValue(colkey);
            }
        }else
        {
            paramvalue=mColParamsValue.get(paramname);
        }
        
        return paramvalue;
    }
    
    public String getColParamRealValue(ReportRequest rrequest,ReportBean rbean,String paramname,String paramvalue)
    {
        if(paramname==null||!paramname.startsWith(Consts_Private.COL_NONDISPLAY_PERMISSION_PREX)) return paramvalue;
        EditableReportSecretColValueBean secretColvalueBean=EditableReportSecretColValueBean.loadFromSession(rrequest,rbean);
        if(secretColvalueBean==null)
        {
            throw new WabacusRuntimeException("session过期，无法获取到"+paramname+"参数的真正参数值");
        }
        if(secretColvalueBean.containsColkey(paramvalue))
        {
            paramvalue=secretColvalueBean.getParamValue(paramvalue);
        }
        return paramvalue;
    }
    
    public boolean isReadonlyAccessMode(IEditableReportType reportTypeObj)
    {
        ReportBean rbean=((AbsReportType)reportTypeObj).getReportBean();
        ReportRequest rrequest=((AbsReportType)reportTypeObj).getReportRequest();
        String isReadonlyAccessmode=rrequest.getStringAttribute(rbean.getId()+"_isReadonlyAccessmode","");
        if(isReadonlyAccessmode.equals(""))
        {
            String accessmode=rrequest.getStringAttribute(rbean.getId()+"_ACCESSMODE",reportTypeObj.getDefaultAccessMode()).toLowerCase();
            if(accessmode.equals(Consts.READONLY_MODE)||rrequest.checkPermission(rbean.getId(),null,null,"readonly")||rbean.getSbean()==null)
            {
                isReadonlyAccessmode="true";
            }else
            {
                EditableReportSqlBean ersqlbean=(EditableReportSqlBean)rbean.getSbean().getExtendConfigDataForReportType(EditableReportSqlBean.class);
                if(ersqlbean==null||(ersqlbean.getDeletebean()==null&&ersqlbean.getInsertbean()==null&&ersqlbean.getUpdatebean()==null))
                {
                    isReadonlyAccessmode="true";
                }else
                {
                    isReadonlyAccessmode="false";
                }
            }
            rrequest.setAttribute(rbean.getId()+"_isReadonlyAccessmode",isReadonlyAccessmode);
        }
        return Boolean.valueOf(isReadonlyAccessmode);
    }

    public void doAllReportsSaveAction(ReportRequest rrequest)
    {
        String flag=rrequest.getStringAttribute("WX_HAS_SAVING_DATA_"+rrequest.getPagebean().getId(),"");
        if(flag.equals("true")) return;
        rrequest.setAttribute("WX_HAS_SAVING_DATA_"+rrequest.getPagebean().getId(),"true");
        Map<Object,Object> mAtts=new HashMap<Object,Object>();
        mAtts.putAll(rrequest.getAttributes());
        Object objKeyTmp,objValueTmp;
        String keyTmp,valueTmp;
        //Map<String,ReportBean> mSavingReportBeans=new HashMap<String,ReportBean>();//存放待保存报表的ReportBean对象
        Map<String,IEditableReportType> mSavingReportObjs=new HashMap<String,IEditableReportType>();
        String reportidTmp;
        for(Entry<Object,Object> entryTmp:mAtts.entrySet())
        {
            objKeyTmp=entryTmp.getKey();
            objValueTmp=entryTmp.getValue();
            if(!(objKeyTmp instanceof String)||!(objValueTmp instanceof String)) continue;
            keyTmp=(String)objKeyTmp;
            if(keyTmp.endsWith("_INSERTDATAS"))
            {
                reportidTmp=keyTmp.substring(0,keyTmp.length()-"_INSERTDATAS".length());
            }else if(keyTmp.endsWith("_UPDATEDATAS"))
            {
                reportidTmp=keyTmp.substring(0,keyTmp.length()-"_UPDATEDATAS".length());
            }else if(keyTmp.endsWith("_DELETEDATAS"))
            {
                reportidTmp=keyTmp.substring(0,keyTmp.length()-"_DELETEDATAS".length());
            }else if(keyTmp.endsWith("_CUSTOMIZEDATAS"))
            {
                reportidTmp=keyTmp.substring(0,keyTmp.length()-"_CUSTOMIZEDATAS".length());
            }else
            {
                continue;
            }
            if(reportidTmp.trim().equals("")) continue;
            ReportBean rbeanTmp=rrequest.getPagebean().getReportChild(reportidTmp,true);
            if(rbeanTmp==null) continue;
            valueTmp=(String)objValueTmp;
            if(valueTmp.trim().equals("")) continue;
            Object objTmp=rrequest.getComponentTypeObj(rbeanTmp,null,true);
            if(!(objTmp instanceof IEditableReportType)) continue;//当前报表不是可编辑报表
            if(isReadonlyAccessMode((IEditableReportType)objTmp)) continue;
            
            mSavingReportObjs.put(reportidTmp,(IEditableReportType)objTmp);
            initEditedParams(rrequest,rbeanTmp);
        }
        doSaveAction(rrequest,mSavingReportObjs);
    }

    private void initEditedParams(ReportRequest rrequest,ReportBean reportbean)
    {
        EditableReportSqlBean ersqlbean=(EditableReportSqlBean)reportbean.getSbean().getExtendConfigDataForReportType(EditableReportSqlBean.class);
        CacheDataBean cdb=rrequest.getCdb(reportbean.getId());
        boolean[] shouldDoSave=new boolean[4];
        SaveInfoDataBean sidbean=new SaveInfoDataBean();
        sidbean.setShouldDoSave(shouldDoSave);
        List<Map<String,String>> lstParamsValue=parseSaveDataStringToList(rrequest.getStringAttribute(reportbean.getId()+"_CUSTOMIZEDATAS",""));
        if(lstParamsValue!=null&&lstParamsValue.size()>0)
        {
            Map<String,String> mCustomizeData=lstParamsValue.get(0);
            cdb.getAttributes().put("WX_UPDATE_CUSTOMIZEDATAS",mCustomizeData);
            shouldDoSave[3]=true;
            if(mCustomizeData!=null&&mCustomizeData.containsKey("WX_UPDATETYPE"))
            {
                sidbean.setUpdatetype(mCustomizeData.get("WX_UPDATETYPE"));
            }else
            {
                sidbean.setUpdatetype("");
            }
        }else
        {
            shouldDoSave[3]=false;
        }
        shouldDoSave[0]=initEditedParams(rrequest.getStringAttribute(reportbean.getId()+"_INSERTDATAS",""),rrequest,reportbean,ersqlbean
                .getInsertbean());
        shouldDoSave[1]=initEditedParams(rrequest.getStringAttribute(reportbean.getId()+"_UPDATEDATAS",""),rrequest,reportbean,ersqlbean
                .getUpdatebean());
        shouldDoSave[2]=initEditedParams(rrequest.getStringAttribute(reportbean.getId()+"_DELETEDATAS",""),rrequest,reportbean,ersqlbean
                .getDeletebean());
        rrequest.setAttribute(reportbean.getId(),"SAVEINFO_DATABEAN",sidbean);
    }

    private boolean initEditedParams(String strParams,ReportRequest rrequest,ReportBean reportbean,AbsEditableReportEditDataBean editbean)
    {
        if(strParams.equals("")||editbean==null) return false;
        log.debug(strParams);
        List<Map<String,String>> lstParamsValue=parseSaveDataStringToList(strParams);
        if(lstParamsValue==null||lstParamsValue.size()==0) return false;
        CacheDataBean cdb=rrequest.getCdb(reportbean.getId());
        cdb.setLstEditedData(editbean,lstParamsValue);
        cdb.setLstEditedParamValues(editbean,getExternalValues(editbean,lstParamsValue,reportbean,rrequest));
        if(!(editbean instanceof EditableReportDeleteDataBean))
        {
            List<ColBean> lstCBeans=reportbean.getDbean().getLstCols();
            EditableReportColBean ecbeanTmp;
            for(Map<String,String> mParamValuesTmp:lstParamsValue)
            {
                for(ColBean cbTmp:lstCBeans)
                {
                    ecbeanTmp=(EditableReportColBean)cbTmp.getExtendConfigDataForReportType(EditableReportColBean.class);
                    if(ecbeanTmp!=null&&ecbeanTmp.getServerValidateBean()!=null)
                    {
                        ColBean cbDest=cbTmp.getUpdateColBeanDest(false);
                        if(cbDest==null) cbDest=cbTmp;
                        if(!ecbeanTmp.getServerValidateBean().validate(rrequest,mParamValuesTmp.get(cbDest.getProperty()),mParamValuesTmp,
                                new ArrayList<String>(),false)) return false;
                    }
                }
            }
        }
        return true;
    }

    public List<Map<String,String>> parseSaveDataStringToList(String strSavedata)
    {
        if(strSavedata==null||strSavedata.trim().equals("")) return null;
        List<String> lstRowDatas=Tools.parseStringToList(strSavedata.trim(),Consts_Private.SAVE_ROWDATA_SEPERATOR);
        List<Map<String,String>> lstResults=new ArrayList<Map<String,String>>();
        for(String rowdataTmp:lstRowDatas)
        {
            if(rowdataTmp==null||rowdataTmp.trim().equals("")) continue;
            Map<String,String> mRowData=new HashMap<String,String>();
            List<String> lstColsData=Tools.parseStringToList(rowdataTmp,Consts_Private.SAVE_COLDATA_SEPERATOR);
            String colnameTmp;
            String colvalueTmp;
            for(String coldataTmp:lstColsData)
            {
                if(coldataTmp==null||coldataTmp.trim().equals("")) continue;
                int idx=coldataTmp.indexOf(Consts_Private.SAVE_NAMEVALUE_SEPERATOR);
                if(idx<=0) continue;
                colnameTmp=coldataTmp.substring(0,idx).trim();
                colvalueTmp=coldataTmp.substring(idx+Consts_Private.SAVE_NAMEVALUE_SEPERATOR.length());
                if(colnameTmp.equals("")||colvalueTmp.equals("")) continue;
                mRowData.put(colnameTmp,colvalueTmp);
            }
            if(mRowData.size()>0) lstResults.add(mRowData);
        }
        return lstResults;
    }

    private void doSaveAction(ReportRequest rrequest,Map<String,IEditableReportType> mSavingReportObjs)
    {
        if(mSavingReportObjs==null||mSavingReportObjs.size()==0) return;
        rrequest.setTransactionObj(new DefaultTransactionType());//先清空复位
        List<ReportBean> lstSaveReportBeans=new ArrayList<ReportBean>();
        for(Entry<String,IEditableReportType> entryTmp:mSavingReportObjs.entrySet())
        {
            lstSaveReportBeans.add(((AbsReportType)entryTmp.getValue()).getReportBean());
        }
        List<AbsPageInterceptor> lstPageInterceptors=rrequest.getLstPageInterceptors();
        if(lstPageInterceptors!=null&&lstPageInterceptors.size()>0)
        {
            for(AbsPageInterceptor pageInterceptorObjTmp:lstPageInterceptors)
            {
                pageInterceptorObjTmp.doStartSave(rrequest,lstSaveReportBeans);
            }
        }
        List<EditActionGroupBean> lstAllEditActionGroupBeans=new ArrayList<EditActionGroupBean>();
        for(Entry<String,IEditableReportType> entryTmp:mSavingReportObjs.entrySet())
        {
            entryTmp.getValue().collectEditActionGroupBeans(lstAllEditActionGroupBeans);
        }
        if(rrequest.getTransactionWrapper()!=null) rrequest.getTransactionWrapper().beginTransaction(rrequest,lstAllEditActionGroupBeans);
        boolean hasInsertData=false,hasUpdateData=false,hasDeleteData=false;
        boolean isFailed=false,hasSaveReport=false;
        boolean shouldStopRefreshDisplay=false;
        int[] resultTmp=null;
        try
        {
            IEditableReportType reportTypeObjTmp;
            ReportBean rbeanTmp;
            for(Entry<String,IEditableReportType> entryTmp:mSavingReportObjs.entrySet())
            {
                reportTypeObjTmp=entryTmp.getValue();
                rbeanTmp=((AbsReportType)reportTypeObjTmp).getReportBean();
                resultTmp=reportTypeObjTmp.doSaveAction();
                if(resultTmp==null||resultTmp.length!=2||resultTmp[0]==IInterceptor.WX_RETURNVAL_SKIP) continue;
                if(resultTmp[0]==IInterceptor.WX_RETURNVAL_TERMINATE)
                {
                    rrequest.getTransactionWrapper().rollbackTransaction(rrequest,lstAllEditActionGroupBeans);
                    rrequest.getWResponse().terminateResponse(Consts.STATECODE_FAILED);
                    return;
                }
                rrequest.getWResponse().addUpdateReportGuid(rbeanTmp.getGuid());
                if(resultTmp[0]==IInterceptor.WX_RETURNVAL_SUCCESS_NOTREFRESH) shouldStopRefreshDisplay=true;
                if(resultTmp[1]==IEditableReportType.IS_ADD_DATA)
                {
                    hasInsertData=true;
                }else if(resultTmp[1]==IEditableReportType.IS_UPDATE_DATA)
                {
                    hasUpdateData=true;
                }else if(resultTmp[1]==IEditableReportType.IS_ADD_UPDATE_DATA)
                {
                    hasInsertData=true;
                    hasUpdateData=true;
                }else if(resultTmp[1]==IEditableReportType.IS_DELETE_DATA)
                {
                    hasDeleteData=true;
                }
                hasSaveReport=true;
            }
            if(lstPageInterceptors!=null&&lstPageInterceptors.size()>0)
            {
                AbsPageInterceptor pageInterceptorObjTmp;
                for(int i=lstPageInterceptors.size()-1;i>=0;i--)
                {//这个调用顺序与调用doStartSave()方法相反
                    pageInterceptorObjTmp=lstPageInterceptors.get(i);
                    pageInterceptorObjTmp.doEndSave(rrequest,lstSaveReportBeans);
                }
            }
            if(rrequest.getTransactionWrapper()!=null) rrequest.getTransactionWrapper().commitTransaction(rrequest,lstAllEditActionGroupBeans);
        }catch(WabacusRuntimeWarningException wrwe)
        {
            if(rrequest.getTransactionWrapper()!=null)
            {
                if(rrequest.getWResponse().getStatecode()!=Consts.STATECODE_FAILED)
                {
                    rrequest.getTransactionWrapper().commitTransaction(rrequest,lstAllEditActionGroupBeans);
                }else
                {
                    rrequest.getTransactionWrapper().rollbackTransaction(rrequest,lstAllEditActionGroupBeans);
                }
            }
            throw new WabacusRuntimeWarningException();
        }catch(Exception e)
        {
            isFailed=true;
            if(rrequest.getTransactionWrapper()!=null) rrequest.getTransactionWrapper().rollbackTransaction(rrequest,lstAllEditActionGroupBeans);
            log.error("保存页面"+rrequest.getPagebean().getId()+"上的报表数据失败",e);
            if(resultTmp!=null&&resultTmp.length==2)
            {
                if(resultTmp[1]==1)
                {
                    hasInsertData=true;
                }else if(resultTmp[1]==2)
                {
                    hasUpdateData=true;
                }else if(resultTmp[1]==3)
                {
                    hasInsertData=true;
                    hasUpdateData=true;
                }else if(resultTmp[1]==4)
                {
                    hasDeleteData=true;
                }
            }
            if(!rrequest.isDisableAutoFailedPrompt()) promptFailedMessage(rrequest,hasInsertData,hasUpdateData,hasDeleteData);
        }finally
        {
            rrequest.setTransactionObj(null);
        }
        if(hasSaveReport&&!isFailed)
        {
            if(!rrequest.isDisableAutoSuccessPrompt())
            {
                promptSuccessMessage(rrequest,hasInsertData,hasUpdateData,hasDeleteData);
            }
            if(shouldStopRefreshDisplay)
            {
                rrequest.getWResponse().terminateResponse(Consts.STATECODE_NONREFRESHPAGE);
            }
        }
    }

    private void promptFailedMessage(ReportRequest rrequest,boolean hasInsertData,boolean hasUpdateData,boolean hasDeleteData)
    {
        String errorprompt=null;
        if((hasInsertData&&hasDeleteData)||(hasUpdateData&&hasDeleteData))
        {
            errorprompt=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,rrequest.getPagebean(),"${operate.failed.prompt}",false));
        }else if(hasInsertData&&hasUpdateData)
        {
            errorprompt=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,rrequest.getPagebean(),"${save.failed.prompt}",false));
        }else if(hasInsertData)
        {
            errorprompt=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,rrequest.getPagebean(),"${insert.failed.prompt}",false));
        }else if(hasUpdateData)
        {
            errorprompt=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,rrequest.getPagebean(),"${update.failed.prompt}",false));
        }else if(hasDeleteData)
        {
            errorprompt=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,rrequest.getPagebean(),"${delete.failed.prompt}",false));
        }else
        {
            errorprompt=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,rrequest.getPagebean(),"${operate.failed.prompt}",false));
        }
        rrequest.getWResponse().getMessageCollector().error(errorprompt,true);
    }

    private void promptSuccessMessage(ReportRequest rrequest,boolean hasInsertData,boolean hasUpdateData,boolean hasDeleteData)
    {
        String successprompt=null;
        if((hasInsertData&&hasDeleteData)||(hasUpdateData&&hasDeleteData))
        {
            successprompt=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,rrequest.getPagebean(),"${operate.success.prompt}",false));
        }else if(hasInsertData&&hasUpdateData)
        {
            successprompt=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,rrequest.getPagebean(),"${save.success.prompt}",false));
        }else if(hasInsertData)
        {
            successprompt=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,rrequest.getPagebean(),"${insert.success.prompt}",false));
        }else if(hasUpdateData)
        {
            successprompt=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,rrequest.getPagebean(),"${update.success.prompt}",false));
        }else if(hasDeleteData)
        {
            successprompt=rrequest.getI18NStringValue(Config.getInstance().getResourceString(rrequest,rrequest.getPagebean(),"${delete.success.prompt}",false));
        }else
        {
            return;
        }
        rrequest.getWResponse().getMessageCollector().success(successprompt,false);
    }
    
    public int processAfterSaveAction(ReportRequest rrequest,ReportBean rbean,String updatetype,int originalRtnVal)
    {
        EditableReportSqlBean ersqlbean=(EditableReportSqlBean)rbean.getSbean().getExtendConfigDataForReportType(EditableReportSqlBean.class);
        if(ersqlbean.getAfterSaveAction()!=null&&ersqlbean.getAfterSaveAction().length>0)
        {
            String afterSaveActionMethod=ersqlbean.getAfterSaveActionMethod();
            if(!afterSaveActionMethod.equals(""))
            {//如果配置了保存后回调函数，则将它们加入本次的onload函数中执行。
                StringBuffer paramsBuf=new StringBuffer();
                paramsBuf.append("{pageid:\""+rbean.getPageBean().getId()+"\"");
                paramsBuf.append(",reportid:\""+rbean.getId()+"\"");
                paramsBuf.append(",updatetype:\""+updatetype+"\"}");
                rrequest.getWResponse().addOnloadMethod(afterSaveActionMethod,paramsBuf.toString(),true);
            }
            if(ersqlbean.getAfterSaveAction().length==2&&"true".equals(ersqlbean.getAfterSaveAction()[1]))
            {
                return IInterceptor.WX_RETURNVAL_SUCCESS_NOTREFRESH;
            }
        }
        return originalRtnVal;
    }
    
    public String getEditableMetaData(IEditableReportType editableReportTypeObj)
    {
        ReportBean rbean=((AbsReportType)editableReportTypeObj).getReportBean();
        StringBuffer resultBuf=new StringBuffer();
        if(EditableReportAssistant.getInstance().isReadonlyAccessMode(editableReportTypeObj))
        {
            resultBuf.append(" current_accessmode=\"").append(Consts.READONLY_MODE).append("\"");
        }else
        {
            resultBuf.append(" current_accessmode=\"").append(editableReportTypeObj.getRealAccessMode()).append("\"");
            EditableReportSqlBean ersqlbean=(EditableReportSqlBean)rbean.getSbean().getExtendConfigDataForReportType(EditableReportSqlBean.class);
            if(ersqlbean.getBeforeSaveAction()!=null&&!ersqlbean.getBeforeSaveAction().trim().equals(""))
            {
                resultBuf.append(" beforeSaveAction=\"{method:").append(ersqlbean.getBeforeSaveAction()).append("}\"");
            }
            if(ersqlbean.getDeletebean()!=null)
            {
                ReportRequest rrequest=((AbsReportType)editableReportTypeObj).getReportRequest();
                String deleteconfirmmess=ersqlbean.getDeletebean().getDeleteConfirmMessage();
                if(deleteconfirmmess==null||deleteconfirmmess.trim().equals(""))
                {
                    deleteconfirmmess=Config.getInstance().getResourceString(null,null,"${delete.confirm.prompt}",true);
                }
                if(deleteconfirmmess!=null&&!deleteconfirmmess.trim().equals(""))
                {
                    resultBuf.append(" deleteconfirmmessage=\"").append(rrequest.getI18NStringValue(deleteconfirmmess)).append("\"");
                }
            }
            if(rbean.getDependParentId()!=null&&!rbean.getDependParentId().trim().equals(""))
            {
                if(ersqlbean.getInsertbean()!=null) addRefreshParentProperty(resultBuf,rbean,ersqlbean.getInsertbean(),"OnInsert");
                if(ersqlbean.getUpdatebean()!=null) addRefreshParentProperty(resultBuf,rbean,ersqlbean.getUpdatebean(),"OnUpdate");
                if(ersqlbean.getDeletebean()!=null) addRefreshParentProperty(resultBuf,rbean,ersqlbean.getDeletebean(),"OnDelete");
            }
            EditableReportBean erbean=(EditableReportBean)rbean.getExtendConfigDataForReportType(EditableReportBean.class);
            resultBuf.append(" checkdirtydata=\"").append(erbean==null||erbean.isCheckdirtydata()).append("\"");
        }
        return resultBuf.toString();
    }

    private void addRefreshParentProperty(StringBuffer resultBuf,ReportBean rbean,AbsEditableReportEditDataBean editBean,String propertySuffix)
    {
        String refreshedParentid=editBean.getRefreshParentidOnSave();
        if(refreshedParentid==null||refreshedParentid.trim().equals("")) return;
        ReportBean rbeanMaster=rbean.getPageBean().getReportChild(refreshedParentid,true);
        if(rbeanMaster==null)
        {
            throw new WabacusRuntimeException("ID为"+refreshedParentid+"的报表不存在");
        }
        if(!rbean.isMasterReportOfMe(rbeanMaster,true))
        {
            throw new WabacusRuntimeException("显示报表"+rbean.getPath()+"失败，ID为"+refreshedParentid+"的报表不是ID为"+rbean.getId()+"报表的主报表");
        }
        resultBuf.append(" refreshParentReportid"+propertySuffix+"=\"").append(refreshedParentid).append("\"");
        resultBuf.append(" refreshParentReportType"+propertySuffix+"=\"").append(editBean.isResetNavigateInfoOnRefreshParent()).append("\"");
    }

    public int doSaveReport(ReportRequest rrequest,ReportBean rbean,AbsEditableReportEditDataBean editbean)
    {
        CacheDataBean cdb=rrequest.getCdb(rbean.getId());
        List<Map<String,String>> lstRowData=cdb.getLstEditedData(editbean);
        List<Map<String,String>> lstParamValues=cdb.getLstEditedParamValues(editbean);
        Map<String,String> mRowData, mParamValues;//分别存放要操作的各列数据以及<params/>中定义的变量的数据
        boolean hasSaveData=false, hasNonRefreshReport=false;
        if(lstRowData==null||lstRowData.size()==0)
        {
            if(editbean instanceof EditableReportSQLButtonDataBean)
            {//如果是直接配置更新脚本的<button/>
                EditableReportSQLButtonDataBean buttonEditBean=(EditableReportSQLButtonDataBean)editbean;
                if(!buttonEditBean.isAutoReportdata()&&!buttonEditBean.isHasReportDataParams())
                {//如果这个<button/>不是自动取报表数据，且所有更新脚本不需要取@{param}的数据进行操作，则即使没有记录也要执行，所以如果有参数的话也要初始化参数
                    mParamValues=lstParamValues!=null&&lstParamValues.size()>0?lstParamValues.get(0):null;
                    int rtnVal;
                    if(rbean.getInterceptor()!=null)
                    {
                        rtnVal=rbean.getInterceptor().doSavePerRow(rrequest,rbean,null,mParamValues,editbean);
                    }else
                    {
                        rtnVal=doSaveRow(rrequest,rbean,null,mParamValues,editbean);
                    }
                    if(rtnVal==IInterceptor.WX_RETURNVAL_TERMINATE||rtnVal==IInterceptor.WX_RETURNVAL_SKIP) return rtnVal;
                    hasSaveData=true;
                    if(rtnVal==IInterceptor.WX_RETURNVAL_SUCCESS_NOTREFRESH) hasNonRefreshReport=true;
                }
            }
        }else
        {
            for(int i=0;i<lstRowData.size();i++)
            {
                mRowData=lstRowData.get(i);
                mParamValues=lstParamValues!=null&&lstParamValues.size()>i?lstParamValues.get(i):null;
                int rtnVal;
                if(rbean.getInterceptor()!=null)
                {
                    rtnVal=rbean.getInterceptor().doSavePerRow(rrequest,rbean,mRowData,mParamValues,editbean);
                }else
                {
                    rtnVal=doSaveRow(rrequest,rbean,mRowData,mParamValues,editbean);
                }
                if(rtnVal==IInterceptor.WX_RETURNVAL_TERMINATE) return rtnVal;
                if(rtnVal==IInterceptor.WX_RETURNVAL_SKIP) continue;
                hasSaveData=true;
                if(rtnVal==IInterceptor.WX_RETURNVAL_SUCCESS_NOTREFRESH) hasNonRefreshReport=true;
            }
        }
        if(!hasSaveData) return IInterceptor.WX_RETURNVAL_SKIP;
        if(hasNonRefreshReport) return IInterceptor.WX_RETURNVAL_SUCCESS_NOTREFRESH;
        return IInterceptor.WX_RETURNVAL_SUCCESS;
    }

    public int doSaveRow(ReportRequest rrequest,ReportBean rbean,Map<String,String> mRowData,Map<String,String> mParamValues,
            AbsEditableReportEditDataBean editbean)
    {
        boolean hasSaveData=false, hasNonRefreshReport=false;
        for(EditActionGroupBean actionGroupBean:editbean.getLstEditActionGroupBeans())
        {
            for(AbsEditActionBean actionBeanTmp:actionGroupBean.getLstEditActionBeans())
            {
                int rtnVal;
                if(rbean.getInterceptor()!=null)
                {
                    rtnVal=rbean.getInterceptor().doSavePerAction(rrequest,rbean,mRowData,mParamValues,actionBeanTmp,editbean);
                }else
                {
                    rtnVal=doSavePerAction(rrequest,rbean,mRowData,mParamValues,actionBeanTmp,editbean);
                }
                if(rtnVal==IInterceptor.WX_RETURNVAL_TERMINATE) return rtnVal;
                if(rtnVal==IInterceptor.WX_RETURNVAL_SKIP) continue;
                hasSaveData=true;
                if(rtnVal==IInterceptor.WX_RETURNVAL_SUCCESS_NOTREFRESH) hasNonRefreshReport=true;
            }
        }
        if(!hasSaveData) return IInterceptor.WX_RETURNVAL_SKIP;
        if(hasNonRefreshReport) return IInterceptor.WX_RETURNVAL_SUCCESS_NOTREFRESH;
        return IInterceptor.WX_RETURNVAL_SUCCESS;
    }
    
    public int doSavePerAction(ReportRequest rrequest,ReportBean rbean,Map<String,String> mRowData,Map<String,String> mParamValues,
            AbsEditActionBean actionBean,AbsEditableReportEditDataBean editbean)
    {
        try
        {
            actionBean.updateData(rrequest,rbean,mRowData,mParamValues);
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("保存报表"+rbean.getPath()+"数据失败",e);
        }
        return IInterceptor.WX_RETURNVAL_SUCCESS;
    }
    
    public List<Map<String,String>> getExternalValues(AbsEditableReportEditDataBean editbean,List<Map<String,String>> lstColParamsValue,
            ReportBean rbean,ReportRequest rrequest)
    {
        if(editbean.getLstExternalValues()==null||editbean.getLstExternalValues().size()==0) return null;
        List<Map<String,String>> lstExternalValues=new ArrayList<Map<String,String>>();
        try
        {
            Connection conn=rrequest.getConnection(rbean.getSbean().getDatasource());
            AbsDatabaseType dbtype=rrequest.getDbType(rbean.getSbean().getDatasource());
            if(lstColParamsValue==null||lstColParamsValue.size()==0)
            {
                if(editbean instanceof EditableReportSQLButtonDataBean)
                {//如果是直接配置更新脚本的<button/>
                    EditableReportSQLButtonDataBean buttonEditBean=(EditableReportSQLButtonDataBean)editbean;
                    if(!buttonEditBean.isAutoReportdata()&&!buttonEditBean.isHasReportDataParams())
                    {//如果这个<button/>不是自动取报表数据，且所有脚本不需要取@{param}的数据进行操作，则即使没有记录也要执行，所以如果有参数的话也要初始化参数
                        lstExternalValues.add(initParamValuesForOneRowData(editbean,rbean,rrequest,conn,dbtype,null));
                    }
                }
            }else
            {
                for(Map<String,String> mColParamsValue:lstColParamsValue)
                {//保存的每一条记录都要计算一次与它相应的所有在<params/>中定义的参数值
                    lstExternalValues.add(initParamValuesForOneRowData(editbean,rbean,rrequest,conn,dbtype,mColParamsValue));
                }
            }
        }catch(SQLException sqle)
        {
            throw new WabacusRuntimeException("获取报表"+rbean.getPath()+"配置的<params/>值失败",sqle);
        }
        if(lstExternalValues.size()==0) lstExternalValues=null;
        return lstExternalValues;
    }

    private Map<String,String> initParamValuesForOneRowData(AbsEditableReportEditDataBean editbean,ReportBean rbean,ReportRequest rrequest,
            Connection conn,AbsDatabaseType dbtype,Map<String,String> mColParamsValue) throws SQLException
    {
        Map<String,String> mExternalValue=new HashMap<String,String>();
        Map<String,String> mCustomizedValues=rrequest.getMCustomizeEditData(rbean);
        for(EditableReportExternalValueBean valuebean:editbean.getLstExternalValues())
        {
            if(valuebean.getValue().equals("uuid{}"))
            {
                mExternalValue.put(valuebean.getName(),UUIDGenerator.generateID());
            }else if(Tools.isDefineKey("increment",valuebean.getValue()))
            {
                mExternalValue.put(valuebean.getName(),getAutoIncrementIdValue(rrequest,rbean,rbean.getSbean().getDatasource(),valuebean.getValue()));
            }else if(WabacusAssistant.getInstance().isGetRequestContextValue(valuebean.getValue()))
            {
                mExternalValue.put(valuebean.getName(),WabacusAssistant.getInstance().getRequestContextStringValue(rrequest,valuebean.getValue(),""));
            }else if(valuebean.getValue().equals("now{}"))
            {
                SimpleDateFormat sdf=new SimpleDateFormat(((AbsDateTimeType)valuebean.getTypeObj()).getDateformat());
                mExternalValue.put(valuebean.getName(),sdf.format(new Date()));
            }else if(Tools.isDefineKey("@",valuebean.getValue()))
            {//从某列中取值（可能为了重新定义它的数据类型）
                String valueTmp=mColParamsValue.get(Tools.getRealKeyByDefine("@",valuebean.getValue()));
                if(valueTmp==null) valueTmp="";
                mExternalValue.put(valuebean.getName(),valueTmp);
            }else if(Tools.isDefineKey("!",valuebean.getValue()))
            {
                String customizeParamName=Tools.getRealKeyByDefine("!",valuebean.getValue());
                if(mCustomizedValues==null||!mCustomizedValues.containsKey(customizeParamName))
                {
                    mExternalValue.put(valuebean.getName(),"");
                }else
                {
                    mExternalValue.put(valuebean.getName(),mCustomizedValues.get(customizeParamName));
                }
            }else if(Tools.isDefineKey("sequence",valuebean.getValue()))
            {
                Statement stmt=conn.createStatement();
                ResultSet rs=stmt.executeQuery(dbtype.getSequenceValueSql(Tools.getRealKeyByDefine("sequence",valuebean.getValue())));
                rs.next();
                mExternalValue.put(valuebean.getName(),String.valueOf(rs.getInt(1)));
                rs.close();
                stmt.close();
            }else if(valuebean.getValue().toLowerCase().trim().startsWith("select ")&&valuebean.getLstParamsBean()!=null)
            {
                PreparedStatement pstmtTemp=conn.prepareStatement(valuebean.getValue());
                if(valuebean.getLstParamsBean().size()>0)
                {
                    int j=1;
                    for(EditableReportParamBean paramBean:valuebean.getLstParamsBean())
                    {
                        String paramvalue=null;
                        if(paramBean.getOwner() instanceof EditableReportExternalValueBean)
                        {
                            paramvalue=paramBean.getParamValue(mExternalValue.get(paramBean.getParamname()),rrequest,rbean);
                        }else
                        {
                            if(mColParamsValue!=null)
                            {
                                paramvalue=paramBean.getParamValue(getColParamValue(rrequest,rbean,mColParamsValue,paramBean.getParamname()),
                                        rrequest,rbean);
                            }
                        }
                        paramBean.getDataTypeObj().setPreparedStatementValue(j++,paramvalue,pstmtTemp,dbtype);
                    }
                }
                ResultSet rs=pstmtTemp.executeQuery();
                if(rs.next())
                {
                    mExternalValue.put(valuebean.getName(),valuebean.getTypeObj().value2label(valuebean.getTypeObj().getColumnValue(rs,1,dbtype)));
                }else
                {
                    mExternalValue.put(valuebean.getName(),"");
                }
                rs.close();
                pstmtTemp.close();
            }else
            {//常量或#{reportid.insert.othervaluename}（即从别的报表中<external-value/>取出定义的数据）或@{reportid.insert.col}（即从别的报表中取某列数据）
                mExternalValue.put(valuebean.getName(),valuebean.getValue());
            }
        }
        return mExternalValue;
    }
    
    public String parseStandardEditSql(ReportBean rbean,String sql,List<EditableReportParamBean> lstDynParams)
    {
        if(sql==null||sql.trim().equals("")) return "";
        sql=sql.trim();
        sql=Tools.replaceCharacterInQuote(sql,'{',"$_LEFTBRACKET_$",true);
        sql=Tools.replaceCharacterInQuote(sql,'}',"$_RIGHTBRACKET_$",true);
        Map<String,EditableReportParamBean> mDynParamsAndPlaceHolder=new HashMap<String,EditableReportParamBean>();
        sql=parseCertainTypeDynParamsInStandardSql(rbean,sql,mDynParamsAndPlaceHolder,"url");
        sql=parseCertainTypeDynParamsInStandardSql(rbean,sql,mDynParamsAndPlaceHolder,"request");
        sql=parseCertainTypeDynParamsInStandardSql(rbean,sql,mDynParamsAndPlaceHolder,"session");
        sql=parseCertainTypeDynParamsInStandardSql(rbean,sql,mDynParamsAndPlaceHolder,"@");
        sql=parseCertainTypeDynParamsInStandardSql(rbean,sql,mDynParamsAndPlaceHolder,"!");
        sql=parseCertainTypeDynParamsInStandardSql(rbean,sql,mDynParamsAndPlaceHolder,"#");
        sql=convertPlaceHolderToRealParams(sql,mDynParamsAndPlaceHolder,lstDynParams);
        
        sql=Tools.replaceAll(sql,"$_LEFTBRACKET_$","{");
        sql=Tools.replaceAll(sql,"$_RIGHTBRACKET_$","}");
        return sql;
    }
    
    private String parseCertainTypeDynParamsInStandardSql(ReportBean rbean,String sql,Map<String,EditableReportParamBean> mDynParamsAndPlaceHolder,String paramtype)
    {
        String strStart,strDynValue,strEnd,placeHolderTmp;
        EditableReportParamBean paramBeanTmp;
        int placeholderIdxTmp=0;
        int idx=sql.indexOf(paramtype+"{");
        while(idx>=0)
        {
            strStart=sql.substring(0,idx).trim();
            strEnd=sql.substring(idx);
            idx=strEnd.indexOf("}");
            if(idx<0)
            {
                throw new WabacusConfigLoadingException("加载组件"+rbean.getPath()+"下的SQL语句"+sql+"失败，其中动态参数没有闭合的}");
            }
            strDynValue=strEnd.substring(0,idx+1);
            strEnd=strEnd.substring(idx+1).trim();
            paramBeanTmp=new EditableReportParamBean();
            paramBeanTmp.setParamname(strDynValue);
            if((strStart.endsWith("%")&&strStart.substring(0,strStart.length()-1).trim().toLowerCase().endsWith(" like"))
                    ||strStart.toLowerCase().endsWith(" like"))
            {//如果strStart是 ... like % 或者是 ... like 
                if(strStart.endsWith("%"))
                {
                    strStart=strStart.substring(0,strStart.length()-1);
                    paramBeanTmp.setHasLeftPercent(true);
                }
                if(strEnd.startsWith("%"))
                {
                    strEnd=strEnd.substring(1);
                    paramBeanTmp.setHasRightPercent(true);
                }
            }
            placeHolderTmp="[PLACE_HOLDER_"+paramtype+"_"+placeholderIdxTmp+"]";
            mDynParamsAndPlaceHolder.put(placeHolderTmp,paramBeanTmp);
            sql=strStart+placeHolderTmp+strEnd;
            idx=sql.indexOf(paramtype+"{");
            placeholderIdxTmp++;
        }
        return sql;
    }
    
    private String convertPlaceHolderToRealParams(String sql,Map<String,EditableReportParamBean> mDynParamsAndPlaceHolder,
            List<EditableReportParamBean> lstDynParams)
    {
        if(mDynParamsAndPlaceHolder==null||mDynParamsAndPlaceHolder.size()==0) return sql;
        int idxPlaceHolderStart=sql.indexOf("[PLACE_HOLDER_");
        String strStart=null;
        String strEnd=null;
        String placeHolderTmp;
        while(idxPlaceHolderStart>=0)
        {
            strStart=sql.substring(0,idxPlaceHolderStart);
            strEnd=sql.substring(idxPlaceHolderStart);
            int idxPlaceHolderEnd=strEnd.indexOf("]");
            placeHolderTmp=strEnd.substring(0,idxPlaceHolderEnd+1);
            strEnd=strEnd.substring(idxPlaceHolderEnd+1);
            lstDynParams.add(mDynParamsAndPlaceHolder.get(placeHolderTmp));
            sql=strStart+" ? "+strEnd;
            idxPlaceHolderStart=sql.indexOf("[PLACE_HOLDER_");
        }
        return sql;
    }

    private Map<String,Long> mAllAutoIncrementIdValues=new HashMap<String,Long>();
    
    public synchronized String getAutoIncrementIdValue(ReportRequest rrequest,ReportBean rbean,String datasource,String paramname)
    {
        if(paramname==null||paramname.trim().equals("")) return "-1";
        if(datasource==null) datasource="";
        String key=datasource+"__"+paramname;
        Long lid=mAllAutoIncrementIdValues.get(key);
        if(lid==null||lid.longValue()<0)
        {
            String realparamname=Tools.getRealKeyByDefine("increment",paramname);
            int idx=realparamname.indexOf(".");
            if(idx<0)
            {
                throw new WabacusRuntimeException("为报表"+rbean.getPath()+"配置的自动增长字段"+paramname+"不合法，没有指定表名和自动增长字段名，并用.分隔");
            }
            String tablename=realparamname.substring(0,idx).trim();
            String columnname=realparamname.substring(idx+1).trim();
            if(tablename.trim().equals("")||columnname.trim().equals(""))
            {
                throw new WabacusRuntimeException("为报表"+rbean.getPath()+"配置的自动增长字段"+paramname+"不合法，没有指定表名或自动增长字段名");
            }
            String sid="-1";
            Connection conn=rrequest.getConnection(datasource);
            Statement stmt=null;
            ResultSet rs=null;
            String sql="select max("+columnname+") from "+tablename;
            try
            {
                stmt=conn.createStatement();
                rs=stmt.executeQuery(sql);
                if(rs.next())
                {
                    sid=rs.getString(1);
                }
            }catch(SQLException e)
            {
                throw new WabacusRuntimeException("获取报表"+rbean.getPath()+"配置的自动增长字段"+realparamname+"失败",e);
            }finally
            {
                try
                {
                    if(rs!=null) rs.close();
                }catch(SQLException e)
                {
                    e.printStackTrace();
                }
                WabacusAssistant.getInstance().release(null,stmt);
            }
            lid=Long.valueOf(sid);
        }
        mAllAutoIncrementIdValues.put(key,++lid);
        return String.valueOf(lid);
    }
}
