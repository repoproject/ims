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

import com.wabacus.config.Config;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.util.Consts_Private;
import com.wabacus.util.Tools;

public class DatePickerBox2 extends TextBox
{
    private String dateformat="y-mm-dd";

    public DatePickerBox2(String typename)
    {
        super(typename);
    }

    public String getInputboxInnerType()
    {
        return "datepicker2";
    }
    
    protected String getTextBoxExtraStyleProperty(ReportRequest rrequest,boolean isReadonly)
    {
        if(isReadonly) return super.getTextBoxExtraStyleProperty(rrequest,isReadonly);
        return Tools.mergeHtmlTagPropertyString(super.getTextBoxExtraStyleProperty(rrequest,isReadonly)," onfocus=\"try{showCalendar('"
                +getInputBoxId(rrequest)+"', '"+this.dateformat+"');}catch(e){logErrorsAsJsFileLoad(e);}\"",1);
    }

    protected String initDisplaySpanStart(ReportRequest rrequest)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(super.initDisplaySpanStart(rrequest));
        if(this.dateformat==null) this.dateformat="y-mm-dd";
        resultBuf.append(" dateformat=\""+this.dateformat.trim()+"\"");
        return resultBuf.toString();
    }
    
    public String filledInContainer(String onblur)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("var dateformat=null;if(inputboxSpanObj!=null) dateformat=inputboxSpanObj.getAttribute('dateformat');");
        resultBuf.append("if(dateformat==null||dateformat=='') dateformat='y-mm-dd';");
        resultBuf.append("boxstr=\"<input type='text' value=\\\"\"+boxValue+\"\\\"\";");
        resultBuf.append(getInputBoxCommonFilledProperties());
        //这种输入框一直是显示<td/>时显示出来，而不是点击后再显示，因此可以把fillInputboxvalue...()方法放在onblurmethod(可能有失去焦点时的校验方法)前面
        resultBuf.append("boxstr=boxstr+\" onblur=\\\"try{").append(onblur).append(";\"+onblurmethod+\"").append("}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("boxstr=boxstr+\" onfocus=\\\"try{showCalendar('\"+name+\"', '\"+dateformat+\"');\"+onfocusmethod+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("boxstr=boxstr+\">\";");
        return resultBuf.toString();
    }

    public String getIndependentDisplayString(ReportRequest rrequest,String value,String dynstyleproperty,Object specificDataObj,boolean isReadonly)
    {
       return null;
    }
    
    public void loadInputBoxConfig(IInputBoxOwnerBean ownerbean,XmlElementBean eleInputboxBean)
    {
        super.loadInputBoxConfig(ownerbean,eleInputboxBean);
        this.setTypePromptBean(null);
        if(eleInputboxBean!=null)
        {
            String dateformat=eleInputboxBean.attributeValue("dateformat");
            if(dateformat!=null) this.dateformat=dateformat.trim();
        }
    }

    protected String getDefaultStylePropertyForDisplayMode2()
    {
        String resultStr="onkeypress='return onKeyEvent(event);'";
        if(this.hasDescription())
        {
            resultStr+=" class='cls-inputbox2' ";
        }else
        {
            resultStr+=" class='cls-inputbox2-full' ";
        }
        return resultStr;
    }
    
    public void setDefaultFillmode(AbsReportType reportTypeObj)
    {
        this.fillmode=1;
    }

    public void doPostLoad(IInputBoxOwnerBean ownerbean)
    {
        super.doPostLoad(ownerbean);
        String jspick=Tools.replaceAll(Config.webroot+"/webresources/component/datepicker/js/calendar.js","//","/");
        ownerbean.getReportBean().getPageBean().addMyJavascriptFile(jspick,0);
        jspick=Tools.replaceAll(Config.webroot+"/webresources/component/datepicker/js/calendar-setup.js","//","/");
        ownerbean.getReportBean().getPageBean().addMyJavascriptFile(jspick,0);
        jspick=Config.webroot+"/webresources/component/datepicker/js/";
        if(this.language==null||this.language.trim().equals("")||this.language.trim().equals(Consts_Private.LANGUAGE_ZH))
        {
            jspick=jspick+"calendar-zh.js";
        }else
        {
            jspick=jspick+"calendar-en.js";
        }
        jspick=Tools.replaceAll(jspick,"//","/");
        ownerbean.getReportBean().getPageBean().addMyJavascriptFile(jspick,0);
        String csspick=Config.webroot+"/webresources/component/datepicker/css/calendar.css";
        csspick=Tools.replaceAll(csspick,"//","/");
        ownerbean.getReportBean().getPageBean().addMyCss(csspick);
    }
    
}
