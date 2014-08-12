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

public class DatePickerBox extends TextBox
{
    private String dateformat;

    public DatePickerBox(String typename)
    {
        super(typename);
    }

    public String getInputboxInnerType()
    {
        return "datepicker1";
    }
    
    protected String getTextBoxExtraStyleProperty(ReportRequest rrequest,boolean isReadonly)
    {
        if(isReadonly) return super.getTextBoxExtraStyleProperty(rrequest,isReadonly);
        String dynstyleproperty=" onclick=\"try{WdatePicker(";
        if(inputboxparams!=null&&!inputboxparams.trim().equals(""))
        {
            dynstyleproperty=dynstyleproperty+this.inputboxparams;
        }
        dynstyleproperty=dynstyleproperty+");}catch(e){logErrorsAsJsFileLoad(e);}\"";
        return Tools.mergeHtmlTagPropertyString(super.getTextBoxExtraStyleProperty(rrequest,isReadonly),dynstyleproperty,1);
    }
    
    public String filledInContainer(String onblur)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("var datepickerparams=null;var onclick_propertyvalue=null;");
        resultBuf.append("if(inputboxSpanObj!=null){");
        resultBuf.append("  datepickerparams=inputboxSpanObj.getAttribute('inputboxparams');onclick_propertyvalue=inputboxSpanObj.getAttribute('onclick_propertyvalue');");
        resultBuf.append("}");
        resultBuf.append("if(onclick_propertyvalue==null) onclick_propertyvalue='';");
        resultBuf.append("boxstr=\"<input type='text' value=\\\"\"+boxValue+\"\\\"\";");
        resultBuf.append(getInputBoxCommonFilledProperties());
        //先调用fillInpuboxValueToTd()，再调用onblurmethod，因为这种输入框一直是显示<td/>时即显示出来，不担心输入框消失后无法显示失去焦点时的出错提示，而且这种输入框一选中日期后还没将选中日期赋给输入框即失去焦点，进行客户端校验，因此必须将校验函数放在后面
        resultBuf.append("boxstr=boxstr+\" onblur=\\\"try{").append(onblur).append(";\"+onblurmethod+\"").append("}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("if(onfocusmethod!=null&&onfocusmethod!=''){boxstr=boxstr+\" onfocus=\\\"try{\"+onfocusmethod+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";}");
        resultBuf.append("onclick_propertyvalue=onclick_propertyvalue+\";WdatePicker(\";");
        resultBuf.append("if(datepickerparams!=null&&datepickerparams!=''){onclick_propertyvalue=onclick_propertyvalue+datepickerparams}");
        resultBuf.append("onclick_propertyvalue=onclick_propertyvalue+\");\";");
        resultBuf.append("boxstr=boxstr+\" onclick=\\\"try{\"+onclick_propertyvalue+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("boxstr=boxstr+\">\";");
        return resultBuf.toString();
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
        String dynparams="";
        if(this.dateformat!=null&&!this.dateformat.trim().equals(""))
        {
            dynparams=dynparams+"dateFmt:'"+this.dateformat+"',";
        }
        
        if(this.language!=null&&!this.language.trim().equals(""))
        {
            if(this.language.equals(Consts_Private.LANGUAGE_ZH))
            {
                dynparams=dynparams+"lang:'zh-cn',";
            }else
            {
                dynparams=dynparams+"lang:'en',";
            }
        }
        this.inputboxparams=Tools.mergeJsonValue(dynparams,this.inputboxparams);
        this.inputboxparams=this.inputboxparams==null?"":this.inputboxparams.trim();
        if(!this.inputboxparams.trim().equals(""))
        {
            this.inputboxparams="{"+this.inputboxparams+"}";
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
    
    protected void processStylePropertyForFillInContainer()
    {
        super.processStylePropertyForFillInContainer();
        String onclick=Tools.getPropertyValueByName("onclick",this.styleproperty,false);
        if(onclick!=null&&!onclick.trim().equals(""))
        {
            this.mStyleProperties2.put("onclick",onclick);
        }
        this.styleproperty2=Tools.removePropertyValueByName("onclick",this.styleproperty2);
    }
    
    public void setDefaultFillmode(AbsReportType reportTypeObj)
    {
        this.fillmode=1;
    }

    public void doPostLoad(IInputBoxOwnerBean ownerbean)
    {
        super.doPostLoad(ownerbean);
        String jspick=Config.webroot+"/webresources/component/My97DatePicker/WdatePicker.js";
        jspick=Tools.replaceAll(jspick,"//","/");
        ownerbean.getReportBean().getPageBean().addMyJavascriptFile(jspick,0);
    }
    
    
}
