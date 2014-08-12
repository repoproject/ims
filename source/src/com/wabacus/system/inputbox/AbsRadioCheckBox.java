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

import java.util.List;
import java.util.Map;

import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.util.Tools;

public abstract class AbsRadioCheckBox extends AbsSelectBox
{
    protected int inline_count;
    
    public AbsRadioCheckBox(String typename)
    {
        super(typename);
    }
    
    protected String doGetDisplayStringValue(ReportRequest rrequest,String value,String style_property,boolean isReadonly)
    {
        StringBuffer resultBuf=new StringBuffer();
        if(isReadonly) style_property=addReadonlyToStyleProperty2(style_property);
        String realinputboxid=getInputBoxId(rrequest);
        value=getInputBoxValue(rrequest,value);
        String optionLabelTmp,optionValueTmp,selected;
        List<Map<String,String>> lstOptionsResult=getLstOptionsFromCache(rrequest,realinputboxid);
        if(lstOptionsResult==null||lstOptionsResult.size()==0) return "";
        resultBuf.append(this.getBeforedescription(rrequest));
        int count=0;
        for(Map<String,String> mOptionTmp:lstOptionsResult)
        {
            optionLabelTmp=mOptionTmp.get("label");
            optionValueTmp=mOptionTmp.get("value");
            if(this.inline_count>0&&count>0&&count%this.inline_count==0)
            {
                resultBuf.append("<br>");
            }
            selected=isSelectedValueOfSelectBox(value,this.isBelongtoUpdatecolSrcCol?optionLabelTmp:optionValueTmp)?" checked ":"";
            optionValueTmp=optionValueTmp==null?"":optionValueTmp.trim();
            resultBuf.append("<input type=\""+this.getInputboxInnerType()+"\" typename='"+typename+"' name=\""+realinputboxid+"\" id=\""+realinputboxid+"\"");
            resultBuf.append(" label=\"").append(optionLabelTmp).append("\" value=\""+optionValueTmp+"\" ").append(selected);
            if(style_property!=null) resultBuf.append(" ").append(style_property);
            resultBuf.append(">").append(optionLabelTmp).append("</input> ");
            count++;
        }
        resultBuf.append(this.getAfterdescription(rrequest));
        return resultBuf.toString().trim();
    }
    
    protected String initDisplaySpanStart(ReportRequest rrequest)
    {
        if(this.inline_count<=0) return super.initDisplaySpanStart(rrequest);
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(super.initDisplaySpanStart(rrequest));
        resultBuf.append(" inline_count=\"").append(this.inline_count).append("\"");
        return resultBuf.toString();
    }

    public String filledInContainer(String onblur)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("if(inputboxSpanObj!=null){");
        resultBuf.append(" var inline_count=inputboxSpanObj.getAttribute('inline_count');");
        resultBuf.append(" var iinlinecount=0;if(inline_count!=null&&inline_count!='') iinlinecount=parseInt(inline_count,10);");
        resultBuf.append("  var childs=inputboxSpanObj.getElementsByTagName(\"span\");");
        resultBuf.append("  if(childs!=null&&childs.length>0){");
        resultBuf.append("      var optionlabel=null;var optionvalue=null;");
        resultBuf.append("      for(var i=0,len=childs.length;i<len;i++){ ");
        resultBuf.append("          if(iinlinecount>0&&i>0&&i%iinlinecount==0) boxstr=boxstr+\"<br>\";");
        resultBuf.append("          optionlabel=childs[i].getAttribute('label'); optionvalue=childs[i].getAttribute('value');");
        resultBuf.append("          boxstr=boxstr+\"<input type='"+this.getInputboxInnerType()+"'  value=\\\"\"+optionvalue+\"\\\" label='\"+optionlabel+\"'\";");
        resultBuf.append(getInputBoxCommonFilledProperties());
        resultBuf.append("          if(isSelectedValueForSelectedBox(boxValue,optionvalue,inputboxSpanObj)) boxstr=boxstr+\" checked\";");
        resultBuf.append("          boxstr=boxstr+\" onblur=\\\"try{\"+onblurmethod+\";fillGroupBoxValue(this,'"+this.typename
                +"','\"+name+\"','\"+reportguid+\"','\"+reportfamily+\"',\"+fillmode+\");}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("          boxstr=boxstr+\" onfocus=\\\"try{\"+onfocusmethod+\";setGroupBoxStopFlag('\"+name+\"');}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("          boxstr=boxstr+\">\"+optionlabel+\"</input>\";");
        resultBuf.append("      }");
        resultBuf.append("  }");
        resultBuf.append("}");
        return resultBuf.toString();
    }
    
    public String getIndependentDisplayString(ReportRequest rrequest,String value,String dynstyleproperty,Object specificDataObj,boolean isReadonly)
    {
        List<String[]> lstOptionsResult=(List<String[]>)specificDataObj;
        StringBuffer resultBuf=new StringBuffer();
        dynstyleproperty=Tools.mergeHtmlTagPropertyString(this.defaultstyleproperty,dynstyleproperty,1);
        if(isReadonly) dynstyleproperty=addReadonlyToStyleProperty1(dynstyleproperty);
        if(lstOptionsResult!=null&&lstOptionsResult.size()>0)
        {
            String optionLabelTmp="";
            String optionValueTmp="";
            String selected="";
            for(String[] items:lstOptionsResult)
            {
                optionLabelTmp=items[0];
                optionValueTmp=items[1];
                optionValueTmp=optionValueTmp==null?"":optionValueTmp.trim();
                if(isSelectedValueOfSelectBox(value,optionValueTmp)) selected=" checked ";
                resultBuf.append("<input type=\""+this.getInputboxInnerType()+"\" value=\""+optionValueTmp+"\" ").append(selected);
                if(dynstyleproperty!=null) resultBuf.append(" ").append(dynstyleproperty);
                resultBuf.append(">").append(optionLabelTmp).append("</input> ");
                selected="";
            }
        }
        return resultBuf.toString();
    }

    protected String getInputBoxCommonFilledProperties()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("boxstr=boxstr+\" id= '\"+name+\"'").append(" name='\"+name+\"'");
        resultBuf.append(" typename='"+this.typename+"' \"+styleproperty;");
        resultBuf.append("boxstr=boxstr+\" style=\\\"\"+style_propertyvalue+\"\\\"\";");
        return resultBuf.toString();
    }

    public void loadInputBoxConfig(IInputBoxOwnerBean ownerbean,XmlElementBean eleInputboxBean)
    {
        super.loadInputBoxConfig(ownerbean,eleInputboxBean);
        String inlinecount=eleInputboxBean.attributeValue("inlinecount");
        if(inlinecount!=null&&!inlinecount.trim().equals(""))
        {
            this.inline_count=Integer.parseInt(inlinecount.trim());
        }
        
    }

    protected String getDefaultStylePropertyForDisplayMode2()
    {
        return "onkeypress='return onKeyEvent(event);'";
    }

    protected void processStylePropertyAfterMerged(AbsReportType reportTypeObj,IInputBoxOwnerBean ownerbean)
    {
        super.processStylePropertyAfterMerged(reportTypeObj,ownerbean);
        this.styleproperty=Tools.mergeHtmlTagPropertyString(this.styleproperty,"onclick=\"this.focus();\"",1);
    }
}
