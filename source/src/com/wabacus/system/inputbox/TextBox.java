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

import com.wabacus.config.Config;
import com.wabacus.config.ConfigLoadManager;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.typeprompt.ITypePromptOptionMatcher;
import com.wabacus.config.typeprompt.TypePromptBean;
import com.wabacus.config.typeprompt.TypePromptColBean;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.inputbox.option.TypepromptOptionBean;
import com.wabacus.util.Tools;

public class TextBox extends AbsInputBox implements Cloneable
{
    private TypePromptBean typePromptBean;

    public TextBox(String typename)
    {
        super(typename);
    }

    public TypePromptBean getTypePromptBean()
    {
        return typePromptBean;
    }

    public void setTypePromptBean(TypePromptBean typePromptBean)
    {
        this.typePromptBean=typePromptBean;
    }

    protected String doGetDisplayStringValue(ReportRequest rrequest,String value,String style_property,boolean isReadonly)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(this.getBeforedescription(rrequest));
        String inputboxid=getInputBoxId(rrequest);
        resultBuf.append("<input  type='"+this.getInputboxInnerType()+"' typename='"+typename+"' id='"+inputboxid+"' name='"+inputboxid+"'");
        resultBuf.append(" value=\""+getInputBoxValue(rrequest,value)+"\"  ");
        style_property=Tools.mergeHtmlTagPropertyString(style_property,getTextBoxExtraStyleProperty(rrequest,isReadonly),1);
        if(isReadonly) style_property=addReadonlyToStyleProperty1(style_property);
        if(style_property!=null) resultBuf.append(" ").append(style_property).append(" ");
        resultBuf.append("/>");
        resultBuf.append(this.getAfterdescription(rrequest));
        return resultBuf.toString();
    }

    public String getInputboxInnerType()
    {
        return "text";
    }
    
    protected String getTextBoxExtraStyleProperty(ReportRequest rrequest,boolean isReadonly)
    {
        StringBuffer resultBuf=new StringBuffer();
        if(typePromptBean!=null)
        {
            resultBuf.append(" onfocus=\"try{initializeTypePromptProperties(this,'"+getTypePromptJsonString(rrequest,getInputBoxId(rrequest))
                    +"');}catch(e){logErrorsAsJsFileLoad(e);}\"");
        }
        return resultBuf.toString();
    }
    
    private String getTypePromptJsonString(ReportRequest rrequest,String inputboxid)
    {
        if(typePromptBean==null) return "";
        StringBuffer resultBuf=new StringBuffer("{");
        resultBuf.append("pageid:\"").append(owner.getReportBean().getPageBean().getId()).append("\"");
        resultBuf.append(",reportid:\"").append(owner.getReportBean().getId()).append("\"");
        resultBuf.append(",inputboxid:\"").append(inputboxid).append("\"");
        resultBuf.append(",spanOutputWidth:").append(typePromptBean.getResultspanwidth());
        resultBuf.append(",spanOutputMaxheight:").append(typePromptBean.getResultspanMaxheight());
        resultBuf.append(",resultCount:").append(typePromptBean.getResultcount());
        resultBuf.append(",timeoutSecond:").append(typePromptBean.getTimeout());
        resultBuf.append(",isShowTitle:").append(typePromptBean.isShowtitle());
        resultBuf.append(",isCasesensitive:").append(typePromptBean.isCasesensitive());
        resultBuf.append(",isSelectBox:").append(typePromptBean.isSelectbox());
        if(this.owner instanceof ConditionBean)
        {
            if(ConditionBean.LABELPOSITION_INNER.equals(((ConditionBean)this.owner).getLabelposition())&&((ConditionBean)this.owner).getLabel(rrequest)!=null)
            {
                resultBuf.append(",conditionlabel:\"").append(((ConditionBean)this.owner).getLabel(rrequest)).append("\"");
            }
        }
        if(typePromptBean.getCallbackmethod()!=null&&!typePromptBean.getCallbackmethod().trim().equals(""))
        {
            resultBuf.append(",callbackmethod:").append(typePromptBean.getCallbackmethod());
        }
        StringBuffer colBuf=new StringBuffer();
        for(TypePromptColBean tpColBean:typePromptBean.getLstPColBeans())
        {
            colBuf.append("{");
            colBuf.append("collabel:\"").append(tpColBean.getLabel()).append("\"");
            if(tpColBean.getValue()!=null) colBuf.append(",colvalue:\"").append(tpColBean.getValue()).append("\"");
            colBuf.append(",coltitle:\"").append(tpColBean.getTitle()==null?"":rrequest.getI18NStringValue(tpColBean.getTitle())).append("\"");
            colBuf.append(",matchmode:").append(tpColBean.getMatchmode());
            colBuf.append(",hidden:").append(tpColBean.isHidden());
            colBuf.append("},");
        }
        if(colBuf.charAt(colBuf.length()-1)==',')  colBuf.deleteCharAt(colBuf.length()-1);
        resultBuf.append(",colsArray:[").append(colBuf.toString()).append("]");
        if(typePromptBean.getClientMatcherMethodName()!=null&&!typePromptBean.getClientMatcherMethodName().trim().equals(""))
        {
            resultBuf.append(",clientMatchMethod:").append(typePromptBean.getClientMatcherMethodName());
        }
        resultBuf.append("}");
        return Tools.jsParamEncode(resultBuf.toString());
    }

    protected String initDisplaySpanStart(ReportRequest rrequest)
    {
        if(this.typePromptBean==null) return super.initDisplaySpanStart(rrequest);
        
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(super.initDisplaySpanStart(rrequest));
        resultBuf.append(" typePrompt=\"").append(getTypePromptJsonString(rrequest,this.owner.getInputBoxId())).append("\"");
        return resultBuf.toString();
    }

    public String filledInContainer(String onblur)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(" boxstr=\"<input type='"+this.getInputboxInnerType()+"' value=\\\"\"+boxValue+\"\\\"\";");
        resultBuf.append(getInputBoxCommonFilledProperties());
        resultBuf.append("boxstr=boxstr+\" onblur=\\\"try{\"+onblurmethod+\"").append(onblur).append("}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("var typePrompt=null;if(inputboxSpanObj!=null){typePrompt=inputboxSpanObj.getAttribute('typePrompt');}");
        resultBuf.append("if(onfocusmethod!=null&&onfocusmethod!=''||typePrompt!=null&&typePrompt!=''){");
        resultBuf.append("  boxstr=boxstr+\" onfocus=\\\"try{\"+onfocusmethod;");
        
        resultBuf.append("   if(typePrompt!=null&&typePrompt!=''){boxstr=boxstr+\"initializeTypePromptProperties(this,'\"+typePrompt+\"');\";}");
        resultBuf.append("  boxstr=boxstr+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("}");
        resultBuf.append("boxstr=boxstr+\">\";");
        return resultBuf.toString();
    }
    
    
    
    public String getIndependentDisplayString(ReportRequest rrequest,String value,String dynstyleproperty,Object specificDataObj,boolean isReadonly)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("<input  type='"+this.getInputboxInnerType()+"'  value=\""+value+"\"");
        dynstyleproperty=Tools.mergeHtmlTagPropertyString(this.defaultstyleproperty,dynstyleproperty,1);
        if(isReadonly) dynstyleproperty=addReadonlyToStyleProperty1(dynstyleproperty);
        if(dynstyleproperty!=null) resultBuf.append(" ").append(dynstyleproperty).append(" ");
        resultBuf.append("/>");
        return resultBuf.toString();
    }

    public void loadInputBoxConfig(IInputBoxOwnerBean ownerbean,XmlElementBean eleInputboxBean)
    {
        super.loadInputBoxConfig(ownerbean,eleInputboxBean);
        if(eleInputboxBean==null) return;
        
        XmlElementBean eleTypeprompt=eleInputboxBean.getChildElementByName("typeprompt");
        if(eleTypeprompt!=null)
        {
            ownerbean.getReportBean().addTextBoxWithingTypePrompt(this);
            typePromptBean=new TypePromptBean();
            String width=eleTypeprompt.attributeValue("width");
            if(width!=null&&!width.trim().equals(""))
            {
                typePromptBean.setResultspanwidth(Tools.getWidthHeightIntValue(width));
            }
            String maxheight=eleTypeprompt.attributeValue("maxheight");
            if(maxheight!=null&&!maxheight.trim().equals(""))
            {
                typePromptBean.setResultspanMaxheight(Tools.getWidthHeightIntValue(maxheight));
            }
            String count=eleTypeprompt.attributeValue("count");
            if(count!=null&&!count.trim().equals(""))
            {
                typePromptBean.setResultcount(Integer.parseInt(count.trim()));
            }
            String timeout=eleTypeprompt.attributeValue("timeout");
            if(timeout!=null&&!timeout.trim().equals(""))
            {
                typePromptBean.setTimeout(Integer.parseInt(timeout.trim()));
            }
            String casesensitive=eleTypeprompt.attributeValue("casesensitive");
            casesensitive=casesensitive==null?"true":casesensitive.toLowerCase().trim();
            typePromptBean.setCasesensitive(!casesensitive.toLowerCase().trim().equals("false"));
            String selectbox=eleTypeprompt.attributeValue("selectbox");
            if(selectbox!=null&&!selectbox.trim().equals(""))
            {
                typePromptBean.setSelectbox(selectbox.toLowerCase().trim().equals("true"));
            }
            if(typePromptBean.isSelectbox()) super.setDefaultFillmode(Config.getInstance().getReportType(ownerbean.getReportBean().getType()));//如果是模拟下拉框，则对于editalbelist2/editabledetail2等报表类型时时，是点击时再填充
            
            String callbackmethod=eleTypeprompt.attributeValue("callbackmethod");
            if(callbackmethod!=null&&!callbackmethod.trim().equals(""))
            {
                typePromptBean.setCallbackmethod(callbackmethod.trim());
            }
            String servermatcher=eleTypeprompt.attributeValue("servermatcher");
            if(servermatcher!=null&&!servermatcher.trim().equals(""))
            {
                Object obj=null;
                try
                {
                    obj=ConfigLoadManager.currentDynClassLoader.loadClassByCurrentLoader(servermatcher).newInstance();
                }catch(Exception e)
                {
                    throw new WabacusConfigLoadingException("为报表"+ownerbean.getReportBean().getPath()+"的<typeprompt/>配置的servermatcher属性："
                            +servermatcher+"无法实例化",e);
                }
                if(!(obj instanceof ITypePromptOptionMatcher))
                {
                    throw new WabacusConfigLoadingException("为报表"+ownerbean.getReportBean().getPath()+"的<typeprompt/>配置的servermatcher属性："
                            +servermatcher+"没有实现接口"+ITypePromptOptionMatcher.class.getName());
                }
                typePromptBean.setTypePromptMatcherObj((ITypePromptOptionMatcher)obj);
            }
            String clientmatcher=eleTypeprompt.attributeValue("clientmatcher");
            if(clientmatcher!=null&&!clientmatcher.trim().equals(""))
            {
                typePromptBean.setClientMatcherMethodName(clientmatcher.trim());
            }
            List<XmlElementBean> lstPromptcols=eleTypeprompt.getLstChildElementsByName("promptcol");
            loadPromptcolsConfig(ownerbean,lstPromptcols);
            XmlElementBean eleDataSources=eleTypeprompt.getChildElementByName("datasource");
            if(eleDataSources==null)
            {
                throw new WabacusConfigLoadingException("没有为报表"+ownerbean.getReportBean().getPath()+"的<typeprompt/>配置子标签<datasource/>");
            }
            List<XmlElementBean> lstOptionElements=eleDataSources.getLstChildElementsByName("option");
            if(lstOptionElements==null||lstOptionElements.size()==0)
            {
                throw new WabacusConfigLoadingException("没有为报表"+ownerbean.getReportBean().getPath()+"的<typeprompt/>配置的子标签<datasource/>配置<option/>选项标签");
            }
            List<TypepromptOptionBean> lstObs=new ArrayList<TypepromptOptionBean>();
            loadTypePromptOptionsConfig(ownerbean,lstOptionElements,lstObs);
            this.typePromptBean.setLstOptionBeans(lstObs);
        }
    }

    private void loadPromptcolsConfig(IInputBoxOwnerBean ownerbean,List<XmlElementBean> lstPromptcols)
    {
        if(lstPromptcols==null&&lstPromptcols.size()==0)
        {
            throw new WabacusConfigLoadingException("没有为报表"+ownerbean.getReportBean().getPath()+"<typeprompt/>的配置子标签<promptcol/>");
        }
        List<TypePromptColBean> lstPColBeans=new ArrayList<TypePromptColBean>();
        boolean isShowTitle=false;
        boolean isHasMatchCol=false;
        ReportBean rbean=ownerbean.getReportBean();
        TypePromptColBean tpColbeanTmp;
        for(XmlElementBean elePromptColBeanTmp:lstPromptcols)
        {
            if(elePromptColBeanTmp==null) continue;
            String label=elePromptColBeanTmp.attributeValue("label");
            String value=elePromptColBeanTmp.attributeValue("value");
            String title=elePromptColBeanTmp.attributeValue("title");
            String matchmode=elePromptColBeanTmp.attributeValue("matchmode");
            String hidden=elePromptColBeanTmp.attributeValue("hidden");
            String matchexpression=elePromptColBeanTmp.attributeValue("matchexpression");
            if(label==null||label.trim().equals(""))
            {
                throw new WabacusConfigLoadingException("报表"+rbean.getPath()+"<typeprompt/>的子标签<promptcol/>的label属性不能为空");
            }
            tpColbeanTmp=new TypePromptColBean();
            tpColbeanTmp.setLabel(Config.getInstance().getResourceString(null,rbean.getPageBean(),label.trim(),true));
            if(title!=null&&!title.trim().equals(""))
            {
                tpColbeanTmp.setTitle(Config.getInstance().getResourceString(null,rbean.getPageBean(),title.trim(),true));
                isShowTitle=true;//此输入联想需要显示标题行
            }
            if(hidden!=null) tpColbeanTmp.setHidden(hidden.toLowerCase().equals("true"));
            if(tpColbeanTmp.isHidden())
            {
                tpColbeanTmp.setMatchmode(0);
            }else
            {
                matchmode=matchmode==null?"":matchmode.toLowerCase().trim();
                if("anywhere".equals(matchmode))
                {
                    tpColbeanTmp.setMatchmode(2);
                }else if("start".equals(matchmode))
                {
                    tpColbeanTmp.setMatchmode(1);
                }else
                {
                    tpColbeanTmp.setMatchmode(0);
                }
                if(tpColbeanTmp.getMatchmode()>0)
                {
                    isHasMatchCol=true;
                    if(value==null||value.trim().equals("")) value=label;
                    tpColbeanTmp.setValue(value.trim());
                    if(matchexpression!=null&&!matchexpression.trim().equals(""))
                    {
                        if(matchexpression.indexOf("#data#")<0)
                        {
                            throw new WabacusConfigLoadingException("报表"+ownerbean.getReportBean().getPath()
                                    +"<typeprompt/>的子标签<promptcol/>的matchexpression属性："+matchexpression+"失败，没有在这里指定输入值的占位符#data#");
                        }
                        tpColbeanTmp.setMatchexpression(matchexpression.trim());
                    }
                }
            }
            lstPColBeans.add(tpColbeanTmp);
        }
        if(!isHasMatchCol)
        {
            throw new WabacusConfigLoadingException("报表"+ownerbean.getReportBean().getPath()
                    +"<typeprompt/>的子标签<promptcol/>的matchmode属性不能均配置为none，必须指定一个或以上用于匹配的列");
        }
        typePromptBean.setShowtitle(isShowTitle);
        typePromptBean.setLstPColBeans(lstPColBeans);
    }

    private void loadTypePromptOptionsConfig(IInputBoxOwnerBean ownerbean,List<XmlElementBean> lstOptionElements,List<TypepromptOptionBean> lstObs)
    {
        ReportBean rbean=ownerbean.getReportBean();
        for(XmlElementBean eleOptionTmp:lstOptionElements)
        {
            if(eleOptionTmp==null) continue;
            TypepromptOptionBean ob=new TypepromptOptionBean(this);
            String source=eleOptionTmp.attributeValue("source");
            source=source==null?"":source.trim();
            if(source.equals(""))
            {
                Map<String,String> mOption=new HashMap<String,String>();
                String nameTmp,valueTmp;
                for(TypePromptColBean tcolbeanTmp:typePromptBean.getLstPColBeans())
                {//需要在常量<option/>中配置所有匹配列/显示列所需的数据
                    nameTmp=tcolbeanTmp.getLabel();
                    if(nameTmp==null||nameTmp.trim().equals(""))
                    {
                        throw new WabacusConfigLoadingException("报表"+rbean.getPath()+"配置的输入联想选项失败，没有在常量选项<option/>标签中配置label属性，指定显示label的数据");
                    }
                    valueTmp=eleOptionTmp.attributeValue(nameTmp);
                    valueTmp=valueTmp==null?"":valueTmp.trim();
                    mOption.put(nameTmp,valueTmp);
                    nameTmp=tcolbeanTmp.getValue();
                    if(nameTmp==null||nameTmp.trim().equals("")||nameTmp.equals(tcolbeanTmp.getLabel())) continue;
                    valueTmp=eleOptionTmp.attributeValue(nameTmp);
                    valueTmp=valueTmp==null?"":valueTmp.trim();
                    mOption.put(nameTmp,valueTmp);
                }
                ob.setMPromptcolValues(mOption);
            }else if(Tools.isDefineKey("@",source)||Tools.isDefineKey("class",source))
            {
                ob.loadOptionDynDatasourceObj(eleOptionTmp,source);
            }else if(Tools.isDefineKey("$",source))
            {
                loadTypePromptOptionsConfig(ownerbean,(List<XmlElementBean>)Config.getInstance().getResourceObject(null,rbean.getPageBean(),source,
                        true),lstObs);
                continue;
            }else
            {
                throw new WabacusConfigLoadingException("报表"+rbean.getPath()+"配置的输入联想选项的source："+eleOptionTmp.attributeValue("source")+"不合法");
            }
            lstObs.add(ob);
        }
    }
    
    protected String getDefaultStylePropertyForDisplayMode2()
    {
        String style="onfocus='this.select();' onkeypress='return onKeyEvent(event);'";
        if(this.hasDescription())
        {
            style+=" class='cls-inputbox2";
        }else
        {
            style+=" class='cls-inputbox2-full";
        }
        if(this.typePromptBean!=null&&this.typePromptBean.isSelectbox())
        {
            style+=" cls-inputbox-typeselectbox";
        }
        style+="'";
        return style;
    }
    
    public void doPostLoad(IInputBoxOwnerBean ownerbean)
    {
        super.doPostLoad(ownerbean);
        if(this.typePromptBean!=null)
        {
            for(TypepromptOptionBean optionBeanTmp:this.typePromptBean.getLstOptionBeans())
            {
                optionBeanTmp.doPostLoad();
            }
            String typepromptjs="/webresources/script/wabacus_typeprompt.js";


//                typepromptjs="/webresources/script/wabacus_typeprompt.js";







//                typepromptjs="/webresources/script/"+encode.toLowerCase()+"/wabacus_typeprompt.js";
//            }
            typepromptjs=Tools.replaceAll(Config.webroot+"/"+typepromptjs,"//","/");
            ownerbean.getReportBean().getPageBean().addMyJavascriptFile(typepromptjs,0);
        }
    }

    protected void processStylePropertyAfterMerged(AbsReportType reportTypeObj,IInputBoxOwnerBean ownerbean)
    {
        super.processStylePropertyAfterMerged(reportTypeObj,ownerbean);
        if(this.typePromptBean!=null)
        {
            
            this.styleproperty=Tools.removePropertyValueByName("onkeypress",this.styleproperty);
            if(this.typePromptBean.isSelectbox())
            {
                this.styleproperty=Tools.replaceAll(this.styleproperty,"cls-inputbox-normal","cls-inputbox-temp-normal cls-inputbox-typeselectbox");
                this.styleproperty=Tools.replaceAll(this.styleproperty,"cls-inputbox-focus","cls-inputbox-temp-focus cls-inputbox-typeselectbox");
                this.styleproperty=Tools.replaceAll(this.styleproperty,"cls-inputbox-mouseover","cls-inputbox-temp-mouseover cls-inputbox-typeselectbox");
                this.styleproperty=Tools.replaceAll(this.styleproperty,"cls-inputbox-temp-normal","cls-inputbox-normal");
                this.styleproperty=Tools.replaceAll(this.styleproperty,"cls-inputbox-temp-focus","cls-inputbox-focus");
                this.styleproperty=Tools.replaceAll(this.styleproperty,"cls-inputbox-temp-mouseover","cls-inputbox-mouseover");
            }
        }
    }
    
    public Object clone(IInputBoxOwnerBean owner)
    {
        TextBox tbNew=(TextBox)super.clone(owner);
        if(typePromptBean!=null)
        {
            tbNew.setTypePromptBean((TypePromptBean)typePromptBean.clone(tbNew));
            if(owner!=null&&owner.getReportBean()!=null)
            {
                owner.getReportBean().addTextBoxWithingTypePrompt(tbNew);
            }
        }
        return tbNew;
    }
}
