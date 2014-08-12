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

import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.util.Tools;

public class SelectBox extends AbsSelectBox
{
    public SelectBox(String typename)
    {
        super(typename);
    }

    protected boolean isMultipleSelect()
    {
        return this.isMultiply;
    }

    protected String doGetDisplayStringValue(ReportRequest rrequest,String value,String style_property,boolean isReadonly)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(this.getBeforedescription(rrequest));
        if(isReadonly) style_property=addReadonlyToStyleProperty2(style_property);
        String realinputboxid=getInputBoxId(rrequest);
        resultBuf.append("<select typename='"+typename+"' name='"+realinputboxid+"' id='"+realinputboxid+"'");
        if(style_property!=null) resultBuf.append(" ").append(style_property);
        resultBuf.append(">");
        value=getInputBoxValue(rrequest,value);
        List<Map<String,String>> lstOptionsResult=getLstOptionsFromCache(rrequest,realinputboxid);
        if(lstOptionsResult!=null)
        {
            String optionLabelTmp, optionValueTmp, selected;
            for(Map<String,String> mItems:lstOptionsResult)
            {
                optionLabelTmp=mItems.get("label");
                optionValueTmp=mItems.get("value");
                selected=isSelectedValueOfSelectBox(value,this.isBelongtoUpdatecolSrcCol?optionLabelTmp:optionValueTmp)?"selected":"";
                optionValueTmp=optionValueTmp==null?"":optionValueTmp.trim();
                resultBuf.append("<option value='"+optionValueTmp+"' "+selected+">"+optionLabelTmp+"</option>");
            }
        }
        resultBuf.append("</select>");
        resultBuf.append(this.getAfterdescription(rrequest));
        return resultBuf.toString();
    }
    
    public String filledInContainer(String onblur)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("var onchangeEvent='';if(inputboxSpanObj!=null){onchangeEvent=inputboxSpanObj.getAttribute('onchange_propertyvalue');}");
        resultBuf.append("var boxstr=\"<select \";").append(getInputBoxCommonFilledProperties());
        resultBuf.append("boxstr=boxstr+\" onblur=\\\"try{\"+onblurmethod+\"").append(onblur).append("}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("if(onchangeEvent!=null&&onchangeEvent!=''){boxstr=boxstr+\" onchange=\\\"try{\"+onchangeEvent+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";}");
        resultBuf.append("  if(onfocusmethod!=null&&onfocusmethod!='') boxstr=boxstr+\" onfocus=\\\"try{\"+onfocusmethod+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("  boxstr=boxstr+\">\";");
        
        resultBuf.append("  if(inputboxSpanObj!=null){");
        resultBuf.append("      var optionSpans=inputboxSpanObj.getElementsByTagName(\"span\");");
        resultBuf.append("      if(optionSpans!=null&&optionSpans.length>0){ ");
        resultBuf.append("          var optionlabel=null;var optionvalue=null;");
        resultBuf.append("          for(var i=0,len=optionSpans.length;i<len;i++){");
        resultBuf.append("              optionlabel=optionSpans[i].getAttribute('label'); optionvalue=optionSpans[i].getAttribute('value');");
        resultBuf.append("              boxstr=boxstr+\"<option value='\"+optionvalue+\"'\";");
        resultBuf.append("              if(isSelectedValueForSelectedBox(boxValue,optionvalue,inputboxSpanObj)) boxstr=boxstr+\" selected\";");
        resultBuf.append("              boxstr=boxstr+\">\"+optionlabel+\"</option>\";");
        resultBuf.append("          }");
        resultBuf.append("      }");
        resultBuf.append("  }");
        resultBuf.append("boxstr=boxstr+\"</select>\";");
        return resultBuf.toString();
    }




//        {//如果是点击时再填充


//        //下面处理显示时就直接填充的情况，此时如果没有配置默认值，则以第一个下拉选项做为默认值



    public String getIndependentDisplayString(ReportRequest rrequest,String value,String dynstyleproperty,Object specificDataObj,boolean isReadonly)
    {
        List<String[]> lstOptionsResult=(List<String[]>)specificDataObj;
        StringBuffer resultBuf=new StringBuffer();
        dynstyleproperty=Tools.mergeHtmlTagPropertyString(this.defaultstyleproperty,dynstyleproperty,1);
        if(isReadonly) dynstyleproperty=addReadonlyToStyleProperty1(dynstyleproperty);
        resultBuf.append("<select ").append(dynstyleproperty).append(">");
        if(lstOptionsResult!=null&&lstOptionsResult.size()>0)
        {
            String name_temp,value_temp,selected;
            for(String[] items:lstOptionsResult)
            {
                name_temp=items[0];
                value_temp=items[1];
                value_temp=value_temp==null?"":value_temp.trim();
                selected=value_temp.equals(value)?"selected":"";
                resultBuf.append("<option value='"+value_temp+"' "+selected+">"+name_temp+"</option>");
            }
        }
        resultBuf.append("</select>");
        return resultBuf.toString();
    }
    
    public String createGetValueByInputBoxObjJs()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("if(boxObj.options.length==0){value='';label='';return;}");
        resultBuf.append("var separator=boxObj.getAttribute('separator');");
        resultBuf.append("if(separator==null||separator==''){");//单选下拉框
        resultBuf.append("  value=boxObj.options[boxObj.options.selectedIndex].value;");
        resultBuf.append("  label=boxObj.options[boxObj.options.selectedIndex].text;");
        resultBuf.append("}else{");
        resultBuf.append("  value='';label='';");
        resultBuf.append("  for(var i=0,len=boxObj.options.length;i<len;i++){");
        resultBuf.append("      if(boxObj.options[i].selected){");
        resultBuf.append("          value=value+boxObj.options[i].value+separator;");
        resultBuf.append("          label=label+boxObj.options[i].text+separator;");
        resultBuf.append("      }");
        resultBuf.append("  }");
        resultBuf.append("  value=wx_rtrim(value,separator);label=wx_rtrim(label,separator);");
        resultBuf.append("}");
        return resultBuf.toString();
    }

    public String createGetValueByIdJs()
    {
        return createGetSelectBoxContentByIdJs("value");
    }

    public String createGetLabelByIdJs()
    {
        return createGetSelectBoxContentByIdJs("text");
    }
    
    private String createGetSelectBoxContentByIdJs(String contenttype)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("var selectboxObj=document.getElementById(id);");
        resultBuf.append("if(selectboxObj==null) return null;");
        resultBuf.append("if(selectboxObj.options.length==0) return '';");
        resultBuf.append("var separator=selectboxObj.getAttribute('separator');");
        resultBuf.append("if(separator==null||separator=='') return selectboxObj.options[selectboxObj.options.selectedIndex]."+contenttype+";");
        resultBuf.append("var resultVal='';");
        resultBuf.append("for(var i=0,len=selectboxObj.options.length;i<len;i++){");
        resultBuf.append("  if(selectboxObj.options[i].selected){resultVal=resultVal+selectboxObj.options[i]."+contenttype+"+separator;}");
        resultBuf.append("}");
        resultBuf.append("return wx_rtrim(resultVal,separator);");
        return resultBuf.toString();
    }
    
    public String createSetInputBoxValueByIdJs()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("var selectboxObj=document.getElementById(id);");
        resultBuf.append("if(selectboxObj==null||selectboxObj.options.length==0){return;}");
        resultBuf.append("var separator=selectboxObj.getAttribute('separator');");
        resultBuf.append("if(separator!=null&&separator!=''){");
        resultBuf.append("  for(var j=0,len=selectboxObj.options.length;j<len;j++){");
        resultBuf.append("      if(selectboxObj.options[j].selected&&selectboxObj.options[j].value==newvalue){return;}");
        resultBuf.append("  }");
        resultBuf.append("}else{");
        resultBuf.append("  var oldvalue=selectboxObj.options[selectboxObj.selectedIndex].value;");
        resultBuf.append("  if(oldvalue&&oldvalue==newvalue){return;}");
        resultBuf.append("}");
        resultBuf.append("var i=0;");
        resultBuf.append("for(len=selectboxObj.options.length;i<len;i=i+1){");
        resultBuf.append("  if(selectboxObj.options[i].value==newvalue){selectboxObj.options[i].selected=true;break;}");
        resultBuf.append("}");
        resultBuf.append("if(i!=selectboxObj.options.length&&selectboxObj.onchange){selectboxObj.onchange();}");
        return resultBuf.toString();
    }
    
    public String getInputboxInnerType()
    {
        return "selectbox";
    }

    public void loadInputBoxConfig(IInputBoxOwnerBean ownerbean,XmlElementBean eleInputboxBean)
    {
        String multiply=eleInputboxBean.attributeValue("multiply");
        this.isMultiply=multiply!=null&&multiply.toLowerCase().trim().equals("true");
        if(this.isMultiply)
        {
            this.separator=eleInputboxBean.attributeValue("separator");
            if(this.separator==null||this.separator.equals("")) this.separator=" ";
        }
        super.loadInputBoxConfig(ownerbean,eleInputboxBean);
    }

    protected String getDefaultStylePropertyForDisplayMode2()
    {
        String resultStr="onkeypress='return onKeyEvent(event);'";
        if(this.hasDescription())
        {
            resultStr+=" class='cls-inputbox2-selectbox' ";
        }else
        {
            resultStr+=" class='cls-inputbox2-selectbox-full' ";
        }
        return resultStr;
    }
    
    protected void processRelativeInputBoxes()
    {
        super.processRelativeInputBoxes();
        if(this.displaymode==2)
        {//editablelist2/editabledetail2报表类型的编辑框
            ReportBean rbean=this.owner.getReportBean();
            if(this.isDependsOtherInputbox())
            {
                String event="onFocus=\"reloadSelectBoxDataByFocus('"+rbean.getPageBean().getId()+"','"+rbean.getId()+"',this,'"
                        +this.getAllParentIdsAsString()+"')\"";
                this.styleproperty=Tools.mergeHtmlTagPropertyString(this.styleproperty,event,1);
            }
        }
    }

    protected String getRefreshChildboxDataEventName()
    {
        return "onchange";
    }

    protected void processStylePropertyAfterMerged(AbsReportType reportTypeObj,IInputBoxOwnerBean ownerbean)
    {
        super.processStylePropertyAfterMerged(reportTypeObj,ownerbean);
        if(this.isMultiply) this.styleproperty=this.styleproperty+" multiple ";
    }
    
    protected void processStylePropertyForFillInContainer()
    {
       super.processStylePropertyForFillInContainer(); 
       String onchange=Tools.getPropertyValueByName("onchange",this.styleproperty,false);
       if(onchange!=null&&!onchange.trim().equals(""))
       {
           this.mStyleProperties2.put("onchange",onchange);
       }
       this.styleproperty2=Tools.removePropertyValueByName("onchange",this.styleproperty2);
    }

    
}
