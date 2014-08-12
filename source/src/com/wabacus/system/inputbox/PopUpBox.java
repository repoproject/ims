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
package com.wabacus.system.inputbox;

import java.util.Map.Entry;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.ReportAssistant;
import com.wabacus.system.component.application.report.EditableDetailReportType;
import com.wabacus.system.component.application.report.EditableListFormReportType;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.component.application.report.abstractreport.IEditableReportType;
import com.wabacus.util.Tools;

public class PopUpBox extends AbsPopUpBox
{
    private String sourcebox;
    
    public PopUpBox(String typename)
    {
        super(typename);
    }

    public String getInputboxInnerType()
    {
        return "popupbox";
    }
    
    protected String doGetDisplayStringValue(ReportRequest rrequest,String value,String style_property,boolean isReadonly)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(this.getBeforedescription(rrequest));
        String inputboxid=getInputBoxId(rrequest);
        if("textareabox".equals(this.sourcebox))
        {
            resultBuf.append("<textarea");
        }else
        {
            resultBuf.append("<input  type='text'");
            resultBuf.append(" value=\""+getInputBoxValue(rrequest,value)+"\"");
        }
        resultBuf.append(" typename='"+typename+"' name='"+inputboxid+"' id='"+inputboxid+"' ");
        if(!isReadonly)
        {
            String myonclick=null;
            if(owner instanceof ConditionBean)
            {
                String pageurl=getPopupPageUrlInCondition(rrequest);
                myonclick="var oldvalue=encodeURIComponent(getInputBoxValue('"+inputboxid+"','"+typename+"'));";
                myonclick=myonclick+"wx_winpage('"+pageurl+"&INPUTBOXID="+inputboxid+"&OLDVALUE='+oldvalue,"+this.popupparams+");";
            }else
            {
                myonclick="popupPageByPopupInputbox('"+inputboxid+"','"+getPopupUrlJsonString()+"')";
            }
            style_property=Tools.mergeHtmlTagPropertyString(style_property,"onclick=\"try{"+myonclick+"}catch(e){logErrorsAsJsFileLoad(e);}\"",1);
        }
        if(style_property!=null) resultBuf.append(" ").append(style_property);
        resultBuf.append(">");
        if("textareabox".equals(this.sourcebox))
        {
            resultBuf.append(getInputBoxValue(rrequest,value)).append("</textarea>");
        }
        resultBuf.append(this.getAfterdescription(rrequest));
        return resultBuf.toString();
    }

    private String getPopupPageUrlInCondition(ReportRequest rrequest)
    {
        String newpageurl=this.poppageurl;
        if(this.mDynParamColumns!=null&&this.mDynParamColumns.size()>0)
        {
            AbsReportType reportObj=Config.getInstance().getReportType(owner.getReportBean().getType());
            Object dataObj=rrequest.getReportDataObj(owner.getReportBean().getId(),0);
            if(dataObj==null)
            {
                for(Entry<String,String> entryParamTmp:this.mDynParamColumns.entrySet())
                {
                    newpageurl=Tools.replaceAll(newpageurl,"@{"+entryParamTmp.getValue()+"}","");
                }
            }else
            {
                String colsuffix="";
                if((reportObj instanceof IEditableReportType)
                        &&!(reportObj instanceof EditableDetailReportType||reportObj instanceof EditableListFormReportType))
                {//editabledetail2/editablelist2两种报表的显示数据与真正数据可能不同，因此取它们的真正列数据都需从property_old中取。
                    colsuffix="_old";
                }
                ColBean cbTmp;
                String colValTmp;
                for(Entry<String,String> entryParamTmp:this.mDynParamColumns.entrySet())
                {
                    cbTmp=owner.getReportBean().getDbean().getColBeanByColProperty(entryParamTmp.getValue());
                    colValTmp=ReportAssistant.getInstance().getPropertyValueAsString(dataObj,entryParamTmp.getValue()+colsuffix,
                            cbTmp.getDatatypeObj());
                    colValTmp=colValTmp==null?"":colValTmp.trim();
                    newpageurl=Tools.replaceAll(newpageurl,"@{"+cbTmp.getProperty()+"}",colValTmp);
                }
            }
        }
        if(this.mDynParamConditions!=null&&this.mDynParamConditions.size()>0)
        {
            String conValTmp;
            for(Entry<String,String> entryParamTmp:this.mDynParamConditions.entrySet())
            {
                conValTmp=rrequest.getStringAttribute(entryParamTmp.getValue(),"");
                newpageurl=Tools.replaceAll(newpageurl,"condition{"+entryParamTmp.getValue()+"}",conValTmp);
            }
        }
        String token="?";
        if(newpageurl.indexOf("?")>0) token="&";
        newpageurl=newpageurl+token+"SRC_PAGEID="+owner.getReportBean().getPageBean().getId()+"&SRC_REPORTID="+owner.getReportBean().getId();
        return newpageurl;
    }

    protected String initDisplaySpanStart(ReportRequest rrequest)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(super.initDisplaySpanStart(rrequest));
        resultBuf.append(" paramsOfGetPageUrl=\"").append(getPopupUrlJsonString()).append("\"");
        if("textareabox".equals(this.sourcebox)) resultBuf.append(" sourcebox=\"textareabox\"");
        return resultBuf.toString();
    }

    public String filledInContainer(String onblur)
    {
        StringBuffer resultBuf=new StringBuffer();
        
        resultBuf.append("if(inputboxSpanObj==null){wx_warn('显示弹出窗口输入框失败，没有取到弹出窗口页面所需的参数'); return false;}");
        resultBuf.append("var paramsOfGetPageUrl=inputboxSpanObj.getAttribute('paramsOfGetPageUrl');");
        resultBuf.append("var onclick_propertyvalue=inputboxSpanObj.getAttribute('onclick_propertyvalue');");
        resultBuf.append("if(onclick_propertyvalue==null) onclick_propertyvalue='';");
        resultBuf.append("onclick_propertyvalue=\"popupPageByPopupInputbox('\"+name+\"','\"+paramsOfGetPageUrl+\"');\"+onclick_propertyvalue;");
        
        resultBuf.append("if(inputboxSpanObj.getAttribute('sourcebox')=='textareabox'){boxstr=\"<textarea\";}else{boxstr=\"<input type='text' value=\\\"\"+boxValue+\"\\\"\";}");
        resultBuf.append(getInputBoxCommonFilledProperties());
        resultBuf.append("if(onfocusmethod!=null&&onfocusmethod!='') boxstr=boxstr+\" onfocus=\\\"try{\"+onfocusmethod+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("if(onblurmethod!=null&&onblurmethod!='') boxstr=boxstr+\" onblur=\\\"try{\"+onblurmethod+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("boxstr=boxstr+\" onClick=\\\"try{\"+onclick_propertyvalue+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\">\";");
        resultBuf.append("if(inputboxSpanObj.getAttribute('sourcebox')=='textareabox'){boxstr=boxstr+boxValue+\"</textarea>\";}");
        return resultBuf.toString();
    }

    public void loadInputBoxConfig(IInputBoxOwnerBean ownerbean,XmlElementBean eleInputboxBean)
    {
        poppageurl=eleInputboxBean.getContent();
        if(poppageurl==null||poppageurl.trim().equals(""))
        {
            throw new WabacusConfigLoadingException("报表"+ownerbean.getReportBean().getPath()+"配置的弹出输入框没有配置poppageurl属性");
        }
        poppageurl=poppageurl.trim();
        if(!poppageurl.startsWith(Config.webroot))
        {
            poppageurl=Config.webroot+poppageurl;
        }
        poppageurl=Tools.replaceAll(poppageurl,"//","/");
        this.sourcebox=eleInputboxBean.attributeValue("sourcebox");
        super.loadInputBoxConfig(ownerbean,eleInputboxBean);
        ownerbean.getReportBean().addPopUpBox(this);
    }

    protected String getDefaultWidth()
    {
        return "500";
    }
    
    protected String getDefaultHeight()
    {
        return "300";
    }
    
    public Object clone(IInputBoxOwnerBean owner)
    {
        PopUpBox popbNew=(PopUpBox)super.clone(owner);
        if(owner!=null&&owner.getReportBean()!=null)
        {
            owner.getReportBean().addPopUpBox(popbNew);
        }
        return popbNew;
    }
}
