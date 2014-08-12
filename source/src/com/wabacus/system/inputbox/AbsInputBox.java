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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.JavaScriptAssistant;
import com.wabacus.system.assistant.ReportAssistant;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.system.component.application.report.EditableDetailReportType2;
import com.wabacus.system.component.application.report.EditableListFormReportType;
import com.wabacus.system.component.application.report.EditableListReportType2;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportColBean;
import com.wabacus.system.inputbox.autocomplete.AutoCompleteBean;
import com.wabacus.util.Tools;

public abstract class AbsInputBox implements Cloneable
{
    private static Log log=LogFactory.getLog(AbsInputBox.class);

    public final static String VALIDATE_TYPE_ONBLUR="onblur";

    public final static String VALIDATE_TYPE_ONSUBMIT="onsubmit";

    public final static String VALIDATE_TYPE_BOTH="both";

    protected String defaultvalue;//输入框的默认显示值，如果当前输入框没有数据进行显示时，将显示这里配置的默认值，只有此输入框属于编辑列，即配置在<col/>下时，此默认值才有效，当为查询条件下的输入框时，此属性无效。

    protected String defaultstyleproperty;//在wabacus.cfg.xml中配置的默认样式字符串，在editablelist2/editabledetail2两种报表类型的编辑列输入框中不会用到这里的样式，在其它任意场合的输入框中都会用到这里的样式，且不会被覆盖

    protected String inputboxparams;

    protected String styleproperty;

    private List<String> lstDynStylepropertyParts;

    protected String styleproperty2;

    private String beforedescription;

    private Map<String,String> mDynBeforedescriptionParts;

    private String afterdescription;

    private Map<String,String> mDynAfterdescriptionParts;

    private String tip;

    protected Map<String,String> mStyleProperties2;

    protected String language;

    private String jsvalidate;

    private String jsvalidatetype;

    private String servervalidate;//服务器端校验

    private String servervalidatetype;

    private String servervalidateCallback;

    protected String typename;

    protected IInputBoxOwnerBean owner;

    private AutoCompleteBean autoCompleteBean;

    private List<String> lstChildids;

    protected int fillmode=1;

    protected int displaymode=1;

    private String displayon;//如果当前输入框是配置给可编辑报表，且当此列没有出现在<insert/>和<update/>中时，如果想显示输入框，则通过此属性指定在什么条件下需要显示输入框，可配置值为insert、update、insert|update，此属性对查询条件中的输入框无效

    public AbsInputBox(String typename)
    {
        this.typename=typename;
    }

    public IInputBoxOwnerBean getOwner()
    {
        return owner;
    }

    public void setOwner(IInputBoxOwnerBean owner)
    {
        this.owner=owner;
    }

    public List<String> getLstChildids()
    {
        return lstChildids;
    }

    public void addChildInputboxId(String inputboxid)
    {
        if(inputboxid==null||inputboxid.trim().equals("")) return;
        if(this.lstChildids==null) this.lstChildids=new ArrayList<String>();
        if(!this.lstChildids.contains(inputboxid)) this.lstChildids.add(inputboxid);
    }

    protected String getInputBoxValue(ReportRequest rrequest,String value)
    {
        if(value==null||value.trim().equals("")&&defaultvalue!=null)
        {
            value=ReportAssistant.getInstance().getColAndConditionDefaultValue(rrequest,defaultvalue);
        }
        if(value==null) value="";
        return Tools.htmlEncode(value);
    }

    public String getJsvalidatetype()
    {
        return jsvalidatetype;
    }

    protected boolean isJsvalidateOnblur()
    {
        if(this.jsvalidate==null||this.jsvalidate.trim().equals("")) return false;
        return this.VALIDATE_TYPE_BOTH.equals(this.jsvalidatetype)||this.VALIDATE_TYPE_ONBLUR.equals(this.jsvalidatetype);
    }

    public String getLanguage()
    {
        return language;
    }

    public void setDefaultvalue(String defaultvalue)
    {
        this.defaultvalue=defaultvalue;
    }

    public String getDefaultvalue(ReportRequest rrequest)
    {
        if(defaultvalue==null) return null;
        return ReportAssistant.getInstance().getColAndConditionDefaultValue(rrequest,defaultvalue);
    }

    public AutoCompleteBean getAutoCompleteBean()
    {
        return autoCompleteBean;
    }

    public void setAutoCompleteBean(AutoCompleteBean autoCompleteBean)
    {
        this.autoCompleteBean=autoCompleteBean;
    }

    public String getTypename()
    {
        return typename;
    }

    private String getStyleproperty(ReportRequest rrequest)
    {
        return WabacusAssistant.getInstance().getStylepropertyWithDynPart(rrequest,this.styleproperty,this.lstDynStylepropertyParts,"");
    }

    private String getStyleproperty2(ReportRequest rrequest)
    {
        return WabacusAssistant.getInstance().getStylepropertyWithDynPart(rrequest,this.styleproperty2,this.lstDynStylepropertyParts,"");
    }
    
    private void setStyleproperty(String styleproperty)
    {
        Object[] objArr=WabacusAssistant.getInstance().parseStylepropertyWithDynPart(styleproperty);
        this.styleproperty=(String)objArr[0];
        this.lstDynStylepropertyParts=(List<String>)objArr[1];
    }

    public String getJsvalidate()
    {
        return jsvalidate;
    }

    private void setBeforedescription(String beforedescription)
    {
        Object[] objArr=WabacusAssistant.getInstance().parseStringWithDynPart(beforedescription);
        this.beforedescription=(String)objArr[0];
        this.mDynBeforedescriptionParts=(Map<String,String>)objArr[1];
    }

    private void setAfterdescription(String afterdescription)
    {
        Object[] objArr=WabacusAssistant.getInstance().parseStringWithDynPart(afterdescription);
        this.afterdescription=(String)objArr[0];
        this.mDynAfterdescriptionParts=(Map<String,String>)objArr[1];
    }

    public String getBeforedescription(ReportRequest rrequest)
    {
        return WabacusAssistant.getInstance().getStringValueWithDynPart(rrequest,this.beforedescription,this.mDynBeforedescriptionParts,"");
    }

    public String getAfterdescription(ReportRequest rrequest)
    {
        return WabacusAssistant.getInstance().getStringValueWithDynPart(rrequest,this.afterdescription,this.mDynAfterdescriptionParts,"");
    }

    protected boolean hasDescription()
    {
        if(this.beforedescription!=null&&!this.beforedescription.trim().equals("")) return true;
        if(this.afterdescription!=null&&!this.afterdescription.trim().equals("")) return true;
        return false;
    }

    public String getTip(ReportRequest rrequest)
    {
        if(this.tip!=null&&!this.tip.trim().equals(""))
        {
            return rrequest.getI18NStringValue(this.tip);
        }
        return "";
    }

    public int getFillmode()
    {
        return fillmode;
    }

    public void setFillmode(int fillmode)
    {
        this.fillmode=fillmode;
    }

    public int getDisplaymode()
    {
        return displaymode;
    }

    public void setDisplaymode(int displaymode)
    {
        this.displaymode=displaymode;
    }

    public String getDisplayon()
    {
        return displayon;
    }
    
    public String getDefaultlabel(ReportRequest rrequest)
    {
        if(defaultvalue!=null) return ReportAssistant.getInstance().getColAndConditionDefaultValue(rrequest,defaultvalue);
        return "";
    }

    public String getDisplayStringValue(ReportRequest rrequest,String value,String dynstyleproperty,boolean isReadonly)
    {
        return doGetDisplayStringValue(rrequest,value,Tools.mergeHtmlTagPropertyString(this.getStyleproperty(rrequest),dynstyleproperty,1),isReadonly);
    }

    public void setDefaultstyleproperty(String defaultstyleproperty)
    {
        this.defaultstyleproperty=defaultstyleproperty;
    }

    public abstract String getIndependentDisplayString(ReportRequest rrequest,String value,String dynstyleproperty,Object specificDataObj,
            boolean isReadonly);

    protected abstract String doGetDisplayStringValue(ReportRequest rrequest,String value,String style_property,boolean isReadonly);

    public abstract String filledInContainer(String onblur);

    protected abstract String getDefaultStylePropertyForDisplayMode2();

    public abstract String getInputboxInnerType();

    public void setDefaultFillmode(AbsReportType reportTypeObj)
    {
        if(reportTypeObj instanceof EditableListFormReportType)
        {
            this.fillmode=1;
        }else if(reportTypeObj instanceof EditableListReportType2||reportTypeObj instanceof EditableDetailReportType2)
        {
            this.fillmode=2;
        }else
        {
            this.fillmode=1;
        }
    }


    public String initDisplay(ReportRequest rrequest)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(initDisplaySpanStart(rrequest)).append(">");
        resultBuf.append(initDisplaySpanContent(rrequest));
        resultBuf.append("</span>");
        return resultBuf.toString();
    }

    protected String initDisplaySpanStart(ReportRequest rrequest)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("<span id=\"span_"+this.owner.getInputBoxId()+"_span\" style=\"display:none;\"");
        if(this.owner instanceof EditableReportColBean)
        {
            String formatemplate=((EditableReportColBean)this.owner).getFormatemplate(rrequest);
            if(formatemplate!=null&&!formatemplate.trim().equals(""))
            {
                resultBuf.append(" formatemplate=\"").append(Tools.onlyHtmlEncode(formatemplate)).append("\"");
                resultBuf.append(" formatemplate_dyncols=\"").append(
                        ((EditableReportColBean)this.owner).getColPropertyAndPlaceHoldersInFormatemplate()).append("\"");
            }
        }
        String style2=this.getStyleproperty2(rrequest);
        if(style2!=null&&!style2.trim().equals(""))
        {
            resultBuf.append(" styleproperty=\""+Tools.jsParamEncode(style2)+"\"");
        }
        if(this.mStyleProperties2!=null)
        {
            for(Entry<String,String> entryTmp:this.mStyleProperties2.entrySet())
            {
                if(entryTmp.getValue()==null||entryTmp.getValue().trim().equals("")) continue;
                resultBuf.append(" ").append(entryTmp.getKey()).append("_propertyvalue=\"").append(entryTmp.getValue()).append("\"");
            }
        }
        if(this.inputboxparams!=null&&!this.inputboxparams.trim().equals(""))
        {
            resultBuf.append(" inputboxparams=\""+this.inputboxparams.trim()+"\"");
        }
        if(this.lstChildids!=null&&this.lstChildids.size()>0)
        {
            resultBuf.append(" childboxids=\"");
            for(String childidTmp:this.lstChildids)
            {
                resultBuf.append(childidTmp).append(";");
            }
            if(resultBuf.charAt(resultBuf.length()-1)==';') resultBuf.deleteCharAt(resultBuf.length()-1);
            resultBuf.append("\"");
        }
        return resultBuf.toString();
    }

    protected String initDisplaySpanContent(ReportRequest rrequest)
    {
        return "";
    }

    protected String getInputBoxCommonFilledProperties()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("boxstr=boxstr+\" id= '\"+name+\"'").append(" name='\"+name+\"'");
        resultBuf.append(" typename='"+this.typename+"' \"+styleproperty;");
        resultBuf.append("boxstr=boxstr+\" style=\\\"text-align:\"+textalign+\";\";");
        resultBuf.append("if(wid!=null&&parseInt(wid)>0){boxstr=boxstr+\"width:\"+wid+\";\";}");
        resultBuf.append("boxstr=boxstr+style_propertyvalue+\"\\\"\";");
        return resultBuf.toString();
    }

    public String attachInfoForDisplay()
    {
        return "";
    }

    public String createSetInputBoxValueByIdJs()
    {
        StringBuffer buf=new StringBuffer();
        buf.append("var boxObj=document.getElementById(id);");
        buf.append("if(boxObj){boxObj.value=newvalue;}");
        return buf.toString();
    }

    public String createGetValueByIdJs()
    {
        StringBuffer buf=new StringBuffer();
        buf.append("var boxObj=document.getElementById(id);");
        buf.append("if(boxObj!=null){ return boxObj.value;}");
        buf.append("return null;");
        return buf.toString();
    }

    public String createGetLabelByIdJs()
    {
        return "return getInputBoxValue(id,type);";
    }

    public String createGetValueByInputBoxObjJs()
    {
        return "value=boxObj.value; label=boxObj.value;";
    }

    protected String getInputBoxId(ReportRequest rrequest)
    {
        String inputboxid=rrequest.getStringAttribute("DYN_INPUTBOX_ID");
        if(inputboxid==null||inputboxid.trim().equals("")) inputboxid=owner.getInputBoxId();
        return inputboxid;
    }

    
    //    {
    
    
    
    
    
    //            {//没有在styleproperty中指定onkeypress事件，则加上默认的事件
    
    //                {//如果要显示输入框边框，由按回车键时，跳到下一个输入框（一般是输入框显示在表单中的情况）
    
    
    //                {//如果不显示输入框边框，说明输入框只是做为其它可编辑元素的一部分，此时按回车键时，失去焦点即可（一般是editablelist2/editabledetail2的情况）
    
    
    
    
    
    
    //            resultStr=resultStr+"if(displaymode==1){boxstr=boxstr+\" onkeypress='return onInputBoxKeyPress(event)' \";}";
    
    
    
    
    
    
    
    
    
    //        {//如果是被getDisplayStringValue()调用
    
    
    
    
    
    //                classname="cls-inputbox-textbox2";
    
    
    //        {//如果是被filledInContainer()调用
    
    
    
    
    protected String addReadonlyToStyleProperty1(String style_property)
    {
        if(style_property==null)
        {
            style_property="";
        }else if(style_property.toLowerCase().indexOf(" readonly ")>=0)
        {
            return style_property;
        }
        return style_property+" readonly ";
    }

    protected String addReadonlyToStyleProperty2(String style_property)
    {
        if(style_property==null)
        {
            style_property="";
        }else if(style_property.toLowerCase().indexOf(" disabled ")>=0)
        {
            return style_property;
        }
        return style_property+" disabled ";
    }

    public void loadInputBoxConfig(IInputBoxOwnerBean ownerbean,XmlElementBean eleInputboxBean)
    {
        this.owner=ownerbean;
        if(eleInputboxBean==null) return;
        XmlElementBean eleAutocompleteBean=eleInputboxBean.getChildElementByName("autocomplete");
        if(eleAutocompleteBean!=null)
        {
            if(ownerbean instanceof EditableReportColBean)
            {
                this.autoCompleteBean=new AutoCompleteBean(this);
                this.autoCompleteBean.loadConfig(eleAutocompleteBean);
            }else
            {//是查询条件上的自动填充输入框，则不用加载<autocomplete/>里面的配置，只要生成一个此对象标识一下有这个功能
                
            }
        }
        String beforedescription=eleInputboxBean.attributeValue("beforedescription");
        if(beforedescription!=null)
        {
            this.setBeforedescription(Config.getInstance().getResourceString(null,ownerbean.getReportBean().getPageBean(),beforedescription,true));
        }
        String afterdescription=eleInputboxBean.attributeValue("afterdescription");
        if(afterdescription!=null)
        {
            this.setAfterdescription(Config.getInstance().getResourceString(null,ownerbean.getReportBean().getPageBean(),afterdescription,true));
        }
        String tip=eleInputboxBean.attributeValue("tip");
        if(tip!=null)
        {
            this.tip=Config.getInstance().getResourceString(null,ownerbean.getReportBean().getPageBean(),tip,true);
        }
        String styleproperty=eleInputboxBean.attributeValue("styleproperty");
        if(styleproperty!=null)
        {
            this.setStyleproperty(Tools.formatStringBlank(styleproperty.trim()));
        }
        String jsvalidate=eleInputboxBean.attributeValue("jsvalidate");
        if(jsvalidate!=null&&!jsvalidate.trim().equals(""))
        {
            this.jsvalidate=jsvalidate.trim();
            String jsvalidatetype=eleInputboxBean.attributeValue("jsvalidatetype");
            if(jsvalidatetype!=null&&!jsvalidatetype.trim().equals(""))
            {
                jsvalidatetype=jsvalidatetype.toLowerCase().trim();
                if(!jsvalidatetype.equals(VALIDATE_TYPE_BOTH)&&!jsvalidatetype.equals(VALIDATE_TYPE_ONBLUR)
                        &&!jsvalidatetype.equals(VALIDATE_TYPE_ONSUBMIT))
                {
                    throw new WabacusConfigLoadingException("加载报表"+ownerbean.getReportBean().getPath()+"上的输入框"+ownerbean.getInputBoxId()
                            +"失败，配置的jsvalidatetype无效");
                }
                this.jsvalidatetype=jsvalidatetype;
            }else
            {
                this.jsvalidatetype=Config.getInstance().getSystemConfigValue("default-jsvalidatetype",VALIDATE_TYPE_BOTH);
                if(!this.jsvalidatetype.equals(VALIDATE_TYPE_BOTH)&&!this.jsvalidatetype.equals(VALIDATE_TYPE_ONBLUR)
                        &&!this.jsvalidatetype.equals(VALIDATE_TYPE_ONSUBMIT))
                {
                    throw new WabacusConfigLoadingException("在wabacus.cfg.xml系统级配置文件配置的default-jsvalidatetype配置项无效");
                }
            }
        }
        String servervalidate=eleInputboxBean.attributeValue("servervalidate");
        if(servervalidate!=null&&!servervalidate.trim().equals(""))
        {
            this.servervalidate=servervalidate.trim();
            String servervalidatetype=eleInputboxBean.attributeValue("servervalidatetype");
            if(servervalidatetype!=null&&!servervalidatetype.trim().equals(""))
            {
                servervalidatetype=servervalidatetype.toLowerCase().trim();
                if(!servervalidatetype.equals(VALIDATE_TYPE_BOTH)&&!servervalidatetype.equals(VALIDATE_TYPE_ONBLUR)
                        &&!servervalidatetype.equals(VALIDATE_TYPE_ONSUBMIT))
                {
                    throw new WabacusConfigLoadingException("加载报表"+ownerbean.getReportBean().getPath()+"上的输入框"+ownerbean.getInputBoxId()
                            +"失败，配置的servervalidatetype无效");
                }
                this.servervalidatetype=servervalidatetype;
            }else
            {
                this.servervalidatetype=Config.getInstance().getSystemConfigValue("default-servervalidatetype",VALIDATE_TYPE_ONSUBMIT);
                if(!this.servervalidatetype.equals(VALIDATE_TYPE_BOTH)&&!this.servervalidatetype.equals(VALIDATE_TYPE_ONBLUR)
                        &&!this.servervalidatetype.equals(VALIDATE_TYPE_ONSUBMIT))
                {
                    throw new WabacusConfigLoadingException("在wabacus.cfg.xml系统级配置文件配置的default-servervalidatetype配置项无效");
                }
            }
        }
        String serverValidateCallback=eleInputboxBean.attributeValue("servervalidatecallback");
        if(serverValidateCallback!=null&&!serverValidateCallback.trim().equals(""))
        {
            this.servervalidateCallback=serverValidateCallback.trim();
        }
        if(this.servervalidateCallback==null||this.servervalidateCallback.trim().equals("")) this.servervalidateCallback="null";
        String inputboxparams=eleInputboxBean.attributeValue("inputboxparams");
        if(inputboxparams!=null) this.inputboxparams=inputboxparams.trim();
        String _language=eleInputboxBean.attributeValue("language");
        if(_language==null||_language.trim().equals(""))
        {
            this.language=null;
        }else
        {
            this.language=_language;
        }
        String displayon=eleInputboxBean.attributeValue("displayon");
        if(displayon!=null)
        {
            displayon=displayon.toLowerCase().trim();
            if(!displayon.equals(""))
            {
                this.displayon="";
                List<String> lstTmp=Tools.parseStringToList(displayon,"|");
                for(String tmp:lstTmp)
                {
                    if(!tmp.trim().equals("insert")&&!tmp.trim().equals("update")) continue;
                    this.displayon=this.displayon+tmp+"|";
                }
            }else
            {
                this.displayon=null;
            }
        }
    }

    public void doPostLoad(IInputBoxOwnerBean ownerbean)
    {
        AbsReportType reportTypeObj=Config.getInstance().getReportType(ownerbean.getReportBean().getType());
        setDisplaymode(reportTypeObj,ownerbean);
        if(this.displaymode==1)
        {
            this.styleproperty=this.styleproperty==null?"":this.styleproperty.trim();
            if(this.styleproperty.toLowerCase().startsWith("(overwrite)"))
            {
                this.styleproperty=this.styleproperty.substring("(overwrite)".length());
            }else if(this.styleproperty.toLowerCase().startsWith("[overwrite]"))
            {
                this.styleproperty=this.styleproperty.substring("[overwrite]".length());
                this.styleproperty=Tools.mergeHtmlTagPropertyString(this.defaultstyleproperty,this.styleproperty,0);
            }else
            {
                this.styleproperty=Tools.mergeHtmlTagPropertyString(this.defaultstyleproperty,this.styleproperty,1);
            }
        }else
        {
            if(this.styleproperty!=null
                    &&(this.styleproperty.toLowerCase().trim().startsWith("(overwrite)")||this.styleproperty.trim().toLowerCase().startsWith(
                            "[overwrite]")))
            {
                log.warn("报表"+ownerbean.getReportBean().getPath()
                        +"为editablelist2/editabledetail2之一的报表类型，不能在其编辑列的输入框上的styleproperty配置(overwrite)或[overwrite]方式");
                this.styleproperty=this.styleproperty.trim().substring("(overwrite)".length());
            }
            this.styleproperty=Tools.mergeHtmlTagPropertyString(this.getDefaultStylePropertyForDisplayMode2(),this.styleproperty,1);
        }

        processRelativeInputBoxes();

        if(this.autoCompleteBean!=null) this.autoCompleteBean.doPostLoad();
        if(this.autoCompleteBean!=null)
        {
            owner.getReportBean().addInputboxWithAutoComplete(this);
            ColBean cbOwner=(ColBean)((EditableReportColBean)owner).getOwner();
            styleproperty=Tools.mergeHtmlTagPropertyString(styleproperty,
                    "onfocus=\"autoComplete_oldData=getInputBoxValue(this.getAttribute('id'),this.getAttribute('typename'));\"",1);
            StringBuffer blurEventBuf=new StringBuffer();
            blurEventBuf.append("loadAutoCompleteInputboxData(");
            blurEventBuf.append("'").append(owner.getReportBean().getPageBean().getId()).append("'");
            blurEventBuf.append(",'").append(owner.getReportBean().getId()).append("'");
            blurEventBuf.append(",this.getAttribute('id'),'").append(cbOwner.getProperty()).append("'");
            if(cbOwner.getUpdateColBeanDest(false)!=null)
            {
                blurEventBuf.append(",'").append(cbOwner.getUpdateColBeanDest(false).getProperty()).append("'");
            }else
            {
                blurEventBuf.append(",''");
            }
            blurEventBuf.append(",'");
            if(this.autoCompleteBean.getLstColPropertiesInColvalueConditions()!=null)
            {
                for(String colPropTmp:this.autoCompleteBean.getLstColPropertiesInColvalueConditions())
                {
                    if(colPropTmp==null||colPropTmp.trim().equals("")) continue;
                    blurEventBuf.append(colPropTmp).append(";");
                }
            }
            blurEventBuf.append("');");
            styleproperty=Tools.mergeHtmlTagPropertyString(styleproperty,"onblur=\"try{"+blurEventBuf.toString()
                    +"}catch(e){logErrorsAsJsFileLoad(e);}\"",1);
        }
        if(this.jsvalidate!=null&&!this.jsvalidate.trim().equals(""))
        {
            JavaScriptAssistant.getInstance().createInputBoxValidateMethod(this);
        }
        processServerValidate();
        processStylePropertyAfterMerged(reportTypeObj,ownerbean);
        if((ownerbean instanceof EditableReportColBean)
                &&(reportTypeObj instanceof EditableDetailReportType2||reportTypeObj instanceof EditableListReportType2))
        {
            processStylePropertyForFillInContainer();
        }
        if(this.owner instanceof EditableReportColBean&&this.displayon!=null)
        {
            if(this.displayon.indexOf("insert")>=0&&((EditableReportColBean)this.owner).getEditableWhenInsert()<=0)
            {
                ((EditableReportColBean)this.owner).setEditableWhenInsert(1);
            }
            if(this.displayon.indexOf("update")>=0&&((EditableReportColBean)this.owner).getEditableWhenUpdate()<=0)
            {
                ((EditableReportColBean)this.owner).setEditableWhenUpdate(1);
            }
        }
        if(this.defaultvalue!=null&&Tools.isDefineKey("url",this.defaultvalue))
        {
            this.owner.getReportBean().addParamNameFromURL(Tools.getRealKeyByDefine("url",this.defaultvalue));
        }
    }

    private void processServerValidate()
    {
        if(this.servervalidate==null||this.servervalidate.trim().equals("")) return;
        List<String> lstValidateMethods=Tools.parseStringToList(servervalidate.trim(),';','\'');
        if(lstValidateMethods==null||lstValidateMethods.size()==0) return;
        ServerValidateBean svbean=new ServerValidateBean(this);
        svbean.setValidatetype(this.servervalidatetype);
        svbean.setServervalidateCallback(this.servervalidateCallback);
        String methodnameTmp, errormsgTmp;
        for(String methodTmp:lstValidateMethods)
        {
            methodTmp=methodTmp.trim();
            methodnameTmp=methodTmp;
            errormsgTmp=null;
            int lidx=methodTmp.indexOf("(");
            int ridx=methodTmp.lastIndexOf(")");
            if(lidx>0&&lidx<ridx)
            {
                methodnameTmp=methodTmp.substring(0,lidx);
                if(methodnameTmp.equals("")) continue;
                errormsgTmp=methodTmp.substring(lidx+1,ridx).trim();
                errormsgTmp=Config.getInstance().getResourceString(null,this.owner.getReportBean().getPageBean(),errormsgTmp,true);
            }
            svbean.addValidateMethod(this.owner.getReportBean(),methodnameTmp,errormsgTmp);
        }
        if(svbean.getLstParams()!=null&&svbean.getLstParams().size()>0)
        {
            this.owner.setServerValidateBean(svbean);
            if(!VALIDATE_TYPE_ONSUBMIT.equals(this.servervalidatetype))
            {//当前校验不是只在提交时进行校验，即在onblur时也要进行校验
                this.owner.getReportBean().addServerValidateBeanOnBlur(this.owner.getInputBoxId(),svbean);
                String onblur="onblur=\"wx_onblurValidate('"+this.owner.getReportBean().getGuid()+"',this,";
                onblur+=(this.owner instanceof ConditionBean)+",true,";
                onblur+=this.servervalidateCallback+")\"";
                this.styleproperty=Tools.mergeHtmlTagPropertyString(this.styleproperty,onblur,1);
            }
        }
    }

    protected void processRelativeInputBoxes()
    {
        if(this.lstChildids==null||this.lstChildids.size()==0) return;
        ReportBean rbean=this.owner.getReportBean();
        if(this.displaymode==1)
        {
            StringBuffer childidAndParamsBuf=new StringBuffer("{");
            AbsSelectBox childBoxObjTmp;
            boolean isConditionBox=this.owner instanceof ConditionBean;
            for(String childidTmp:this.lstChildids)
            {
                childidAndParamsBuf.append(childidTmp).append(":'");
                if(isConditionBox)
                {
                    childBoxObjTmp=rbean.getChildSelectBoxInConditionById(childidTmp);
                }else
                {
                    childBoxObjTmp=rbean.getChildSelectBoxInColById(childidTmp);
                }
                childidAndParamsBuf.append(childBoxObjTmp.getAllParentIdsAsString()).append("',");
            }
            if(childidAndParamsBuf.charAt(childidAndParamsBuf.length()-1)==',') childidAndParamsBuf.deleteCharAt(childidAndParamsBuf.length()-1);
            childidAndParamsBuf.append("}");
            String event=getRefreshChildboxDataEventName()+"=\"reloadSelectBoxData('"+rbean.getPageBean().getId()+"','"+rbean.getId()+"',this,"
                    +childidAndParamsBuf.toString()+","+isConditionBox+")\"";
            this.styleproperty=Tools.mergeHtmlTagPropertyString(this.styleproperty,event,1);
        }else
        {
            String event=getRefreshChildboxDataEventName()+"=\"resetChildSelectBoxData(this)\"";
            this.styleproperty=Tools.mergeHtmlTagPropertyString(this.styleproperty,event,1);
        }
    }

    protected String getRefreshChildboxDataEventName()
    {
        return "onblur";
    }

    private void setDisplaymode(AbsReportType reportTypeObj,IInputBoxOwnerBean ownerbean)
    {
        if(ownerbean instanceof EditableReportColBean
                &&((reportTypeObj instanceof EditableListReportType2&&!(reportTypeObj instanceof EditableListFormReportType))||reportTypeObj instanceof EditableDetailReportType2))
        {
            this.displaymode=2;
        }else
        {
            this.displaymode=1;
        }
    }

    protected void processStylePropertyAfterMerged(AbsReportType reportTypeObj,IInputBoxOwnerBean ownerbean)
    {
        addJsValidateOnBlurEvent(reportTypeObj,ownerbean);
        if(this.tip!=null&&!this.tip.trim().equals(""))
        {
            this.styleproperty=Tools.mergeHtmlTagPropertyString(this.styleproperty,"title=\""+this.tip+"\"",1);
        }
        if(!(this.owner instanceof ConditionBean))
        {
            this.styleproperty=Tools.mergeHtmlTagPropertyString(this.styleproperty,"onblur=\"try{addInputboxDataForSaving('"
                    +this.getOwner().getReportBean().getGuid()+"',this);}catch(e){logErrorsAsJsFileLoad(e);}\"",1);
        }
    }

    protected void addJsValidateOnBlurEvent(AbsReportType reportTypeObj,IInputBoxOwnerBean ownerbean)
    {
        if(this.isJsvalidateOnblur())
        {
            String onblur="onblur=\"wx_onblurValidate('"+this.owner.getReportBean().getGuid()+"',this,";
            onblur+=(this.owner instanceof ConditionBean)+",false,null)\"";
            this.styleproperty=Tools.mergeHtmlTagPropertyString(this.styleproperty,onblur,1);
        }
    }

    protected void processStylePropertyForFillInContainer()
    {
        this.mStyleProperties2=new HashMap<String,String>();
        this.styleproperty2=this.styleproperty;
        String onfocus=Tools.getPropertyValueByName("onfocus",this.styleproperty,false);
        if(onfocus!=null&&!onfocus.trim().equals("")) this.mStyleProperties2.put("onfocus",onfocus);
        this.styleproperty2=Tools.removePropertyValueByName("onfocus",this.styleproperty2);
        String onblur=Tools.getPropertyValueByName("onblur",this.styleproperty,false);
        if(onblur!=null&&!onblur.trim().equals("")) this.mStyleProperties2.put("onblur",onblur);
        this.styleproperty2=Tools.removePropertyValueByName("onblur",this.styleproperty2);
        String style=Tools.getPropertyValueByName("style",this.styleproperty,false);
        if(style!=null&&!style.trim().equals("")) this.mStyleProperties2.put("style",style);
        this.styleproperty2=Tools.removePropertyValueByName("style",this.styleproperty2);
    }

    protected Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public Object clone(IInputBoxOwnerBean owner)
    {
        try
        {
            AbsInputBox inputBoxNew=(AbsInputBox)clone();
            inputBoxNew.setOwner(owner);
            if(this.autoCompleteBean!=null)
            {
                inputBoxNew.setAutoCompleteBean(this.autoCompleteBean.clone(inputBoxNew));
                owner.getReportBean().addInputboxWithAutoComplete(inputBoxNew);
            }
            return inputBoxNew;
        }catch(CloneNotSupportedException e)
        {
            throw new WabacusConfigLoadingException("clone输入框对象失败",e);
        }
    }
}
