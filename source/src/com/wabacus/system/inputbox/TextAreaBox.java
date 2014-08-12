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

import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.util.Tools;

public class TextAreaBox extends AbsInputBox
{
    public TextAreaBox(String typename)
    {
        super(typename);
    }

    public String getInputboxInnerType()
    {
        return "textarea";
    }
    
    protected String doGetDisplayStringValue(ReportRequest rrequest,String value,String style_property,boolean isReadonly)
    {
        if(isReadonly) style_property=addReadonlyToStyleProperty1(style_property);
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(this.getBeforedescription(rrequest));
        String inputboxid=getInputBoxId(rrequest);
        resultBuf.append("<textarea typename='"+typename+"' name='"+inputboxid+"'  id='"+inputboxid+"' ");
        if(style_property!=null) resultBuf.append(" ").append(style_property);
        resultBuf.append(">").append(getInputBoxValue(rrequest,value)).append("</textarea>");
        resultBuf.append(this.getAfterdescription(rrequest));
        return resultBuf.toString();
    }

    public String filledInContainer(String onblur)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("if(fillmode==2){");
        resultBuf.append("  var textAreabox=document.getElementById('WX_TEXTAREA_BOX');");
        resultBuf.append("  if(textAreabox==null){");
        resultBuf.append("      textAreabox=document.createElement('textarea');");
        resultBuf.append("      textAreabox.className='cls-inputbox-textareabox2';textAreabox.setAttribute('typename','"+this.typename+"');");
        resultBuf.append("      textAreabox.setAttribute('id','WX_TEXTAREA_BOX');");
        resultBuf.append("      textAreabox.setAttribute('isStoreOldValue','true');");
        resultBuf.append("      document.body.appendChild(textAreabox);");
        resultBuf.append("  }");
        String onblur2=Tools.replaceAll(onblur,"'\"+reportguid+\"'","reportguid");
        onblur2=Tools.replaceAll(onblur2,"'\"+reportfamily+\"'","reportfamily");
        onblur2=Tools.replaceAll(onblur2,"\"+fillmode+\"","fillmode");
        resultBuf.append("  textAreabox.onblur=function(){if(onblurmethod!=''){eval(onblurmethod);}"+onblur2+";};");
        resultBuf.append("  textAreabox.onfocus=function(){if(onfocusmethod!='') eval(onfocusmethod);};");
        resultBuf.append("  textAreabox.value=boxValue;");
        resultBuf.append("  setTextAreaBoxPosition(textAreabox,tdObj);");
        resultBuf.append("  textAreabox.focus();textAreabox.dataObj=initInputBoxData(textAreabox,tdObj);boxstr='';");
        resultBuf.append("}else if(fillmode==1){");
        resultBuf.append("  boxstr=\"<textarea  \";");
        resultBuf.append(getInputBoxCommonFilledProperties());
        resultBuf.append("  boxstr=boxstr+\" onblur=\\\"try{\"+onblurmethod+\"").append(onblur).append("}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("  if(onfocusmethod!=null&&onfocusmethod!=''){boxstr=boxstr+\" onfocus=\\\"try{\"+onfocusmethod+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";}");
        resultBuf.append("  boxstr=boxstr+\">\"+boxValue+\"</textarea>\";");
        resultBuf.append("}");
        return resultBuf.toString();
    }

    public String getIndependentDisplayString(ReportRequest rrequest,String value,String dynstyleproperty,Object specificDataObj,boolean isReadonly)
    {
       return null;
    }
    
    public String createGetValueByInputBoxObjJs()
    {
        StringBuffer sbuffer=new StringBuffer();
        sbuffer.append("if(fillmode==2){");
        sbuffer
                .append("var textareaObj=document.getElementById('WX_TEXTAREA_BOX');value=textareaObj.value; label=textareaObj.value;textareaObj.style.display='none';");
        sbuffer.append("}else if(fillmode==1){");
        sbuffer.append("value=boxObj.value; label=boxObj.value;");
        sbuffer.append("}");
        return sbuffer.toString();
    }

    protected void processStylePropertyAfterMerged(AbsReportType reportTypeObj,IInputBoxOwnerBean ownerbean)
    {
        super.processStylePropertyAfterMerged(reportTypeObj,ownerbean);
        this.styleproperty=Tools.mergeHtmlTagPropertyString(this.styleproperty,"isStoreOldValue=\"true\"  onfocus=\"try{storeInputboxOldValue('"
                +this.getOwner().getReportBean().getGuid()+"',this);}catch(e){logErrorsAsJsFileLoad(e);}\"",1);
    }
    
    protected String getDefaultStylePropertyForDisplayMode2()
    {
        return "";
    }    
}
