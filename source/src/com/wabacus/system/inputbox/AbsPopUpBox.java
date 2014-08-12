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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.DisplayBean;
import com.wabacus.config.component.application.report.SqlBean;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.EditableReportAssistant;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.system.component.application.report.EditableListReportType2;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportColBean;
import com.wabacus.util.Tools;

public abstract class AbsPopUpBox extends AbsInputBox
{
    protected String poppageurl;

    protected Map<String,String> mDynParamColumns;

    protected Map<String,String> mDynParamConditions;
    
    protected String popupparams;

    protected String initsize;//初始大小，可配置值包括min/max/normal，分别表示最大化、最小化、正常窗口大小（即上面pagewidth/pageheight配置的大小）
    
    public AbsPopUpBox(String typename)
    {
        super(typename);
    }
    
    public String getPoppageurl()
    {
        return poppageurl;
    }

    public String getPopupparams()
    {
        return popupparams;
    }

    public void setPopupparams(String popupparams)
    {
        this.popupparams=popupparams;
    }

    protected String getDefaultStylePropertyForDisplayMode2()
    {
        String resultStr="onfocus='this.select();' onkeypress='return onKeyEvent(event);' onmouseover=\"this.style.cursor='pointer';\" readonly";
        if(this.hasDescription())
        {
            resultStr+=" class='cls-inputbox2' ";
        }else
        {
            resultStr+=" class='cls-inputbox2-full' ";
        }
        return resultStr;
    }
    
    public Map<String,String> getMDynParamColumns()
    {
        return mDynParamColumns;
    }

    public Map<String,String> getMDynParamConditions()
    {
        return mDynParamConditions;
    }

    public String getIndependentDisplayString(ReportRequest rrequest,String value,String dynstyleproperty,Object specificDataObj,boolean isReadonly)
    {
       return null;
    }
    
    protected String getPopupUrlJsonString()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("{pageid:\"").append(owner.getReportBean().getPageBean().getId()).append("\"");
        resultBuf.append(",reportid:\"").append(owner.getReportBean().getId()).append("\"");
        resultBuf.append(",popupPageUrl:\"").append(this.poppageurl).append("\"");
        if(this.mDynParamColumns!=null&&this.mDynParamColumns.size()>0)
        {
            resultBuf.append(",paramColumns:\"").append(getDynParamsAsJsonString(this.mDynParamColumns)).append("\"");
        }
        if(this.mDynParamConditions!=null&&this.mDynParamConditions.size()>0)
        {
            resultBuf.append(",paramConditions:\"").append(getDynParamsAsJsonString(this.mDynParamConditions)).append("\"");
        }
        resultBuf.append(",popupparams:\"").append(this.popupparams).append("\"");
        resultBuf.append("}");
        return Tools.jsParamEncode(resultBuf.toString());
    }
    
    private String getDynParamsAsJsonString(Map<String,String> mDynParams)
    {
        if(mDynParams==null||mDynParams.size()==0) return "";
        StringBuffer paramsBuf=new StringBuffer();
        for(Entry<String,String> entryParamTmp:mDynParams.entrySet())
        {
            paramsBuf.append(entryParamTmp.getValue()).append(";");
        }
        if(paramsBuf.charAt(paramsBuf.length()-1)==';')
        {
            paramsBuf.deleteCharAt(paramsBuf.length()-1);
        }
        return paramsBuf.toString();
    }
    
    public void setDefaultFillmode(AbsReportType reportTypeObj)
    {
        this.fillmode=1;
    }

    
    public void loadInputBoxConfig(IInputBoxOwnerBean ownerbean,XmlElementBean eleInputboxBean)
    {
        super.loadInputBoxConfig(ownerbean,eleInputboxBean);
        String popupparams=eleInputboxBean.attributeValue("popupparams");
        if(popupparams!=null)  this.popupparams=popupparams.trim();
        String initsize=eleInputboxBean.attributeValue("initsize");
        if(initsize!=null) this.initsize=initsize.trim().toLowerCase();
        parseDynParamsInUrl();
    }
    
    private void parseDynParamsInUrl()
    {
        if(this.poppageurl==null||this.poppageurl.trim().equals("")) return;
        this.mDynParamColumns=new HashMap<String,String>();
        this.mDynParamConditions=new HashMap<String,String>();
        String params=this.poppageurl;
        if(params.indexOf('?')>0) params=params.substring(params.indexOf('?')+1);
        List<String> lstParams=Tools.parseStringToList(params,"&");
        String paramNameTmp,paramValueTmp;
        int idxTmp;
        for(String paramTmp:lstParams)
        {
            if(paramTmp==null) continue;
            idxTmp=paramTmp.indexOf("=");
            if(idxTmp<=0) continue;
            paramNameTmp=paramTmp.substring(0,idxTmp).trim();
            paramValueTmp=paramTmp.substring(idxTmp+1).trim();
            if(Tools.isDefineKey("@",paramValueTmp))
            {
                this.mDynParamColumns.put(paramNameTmp,Tools.getRealKeyByDefine("@",paramValueTmp));
            }else if(Tools.isDefineKey("condition",paramValueTmp))
            {
                this.mDynParamConditions.put(paramNameTmp,Tools.getRealKeyByDefine("condition",paramValueTmp));
            }
        }
    }
    
    public void doPostLoad(IInputBoxOwnerBean ownerbean)
    {
        super.doPostLoad(ownerbean);
        if(this.mDynParamColumns!=null&&this.mDynParamColumns.size()>0)
        {
            DisplayBean dbean=this.owner.getReportBean().getDbean();
            ColBean cbTmp;
            for(Entry<String,String> entryParamTmp:mDynParamColumns.entrySet())
            {
                cbTmp=dbean.getColBeanByColProperty(entryParamTmp.getValue());
                if(cbTmp==null)
                {
                    throw new WabacusConfigLoadingException("加载报表"+this.owner.getReportBean().getPath()+"的输入框"+this.owner.getInputBoxId()
                            +"失败，在弹出窗口URL的参数中指定的column为"+entryParamTmp.getValue()+"的列不存在");
                }
            }
        }
        if(this.mDynParamConditions!=null&&this.mDynParamConditions.size()>0)
        {
            SqlBean sbean=this.owner.getReportBean().getSbean();
            ConditionBean cbTmp;
            for(Entry<String,String> entryParamTmp:this.mDynParamConditions.entrySet())
            {
                cbTmp=sbean.getConditionBeanByName(entryParamTmp.getValue());
                if(cbTmp==null)
                {
                    throw new WabacusConfigLoadingException("加载报表"+this.owner.getReportBean().getPath()+"的输入框"+this.owner.getInputBoxId()
                            +"失败，在弹出窗口URL的参数中指定的name为"+entryParamTmp.getValue()+"的查询条件不存在");
                }
                if(!cbTmp.isConditionWithInputbox())
                {
                    throw new WabacusConfigLoadingException("加载报表"+this.owner.getReportBean().getPath()+"的输入框"+this.owner.getInputBoxId()
                            +"失败，不能在弹出窗口URL的参数中指定没有输入框的条件做为动态参数值");
                }
            }
        }
        popupparams=WabacusAssistant.getInstance().addDefaultPopupParams(popupparams,this.initsize,getDefaultWidth(),getDefaultHeight(),"closePopUpPageEvent");
    }
    
    protected abstract String getDefaultWidth();
    
    protected abstract String getDefaultHeight();
    
    protected void processStylePropertyAfterMerged(AbsReportType reportTypeObj,IInputBoxOwnerBean ownerbean)
    {
        super.processStylePropertyAfterMerged(reportTypeObj,ownerbean);
        
        if(this.styleproperty.toLowerCase().indexOf("readonly")<0) this.styleproperty=this.styleproperty+" readonly ";
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
    
    public String createSelectOkFunction(String realinputboxid,boolean isAutoincludejs)
    {
        boolean isConditionBox=false;
        String fillmode="";
        String paramname=null;
        if(this.getOwner() instanceof ConditionBean)
        {
            isConditionBox=true;
            paramname=((ConditionBean)this.getOwner()).getName();
        }else
        {
            isConditionBox=false;
            fillmode=String.valueOf(this.getFillmode());
            paramname=EditableReportAssistant.getInstance().getColParamName((ColBean)((EditableReportColBean)this.getOwner()).getOwner());
        }
        StringBuffer resultBuf=new StringBuffer();
        String parentWindowName,closeMeCodeString;
        if(Config.getInstance().getSystemConfigValue("prompt-dialog-type","artdialog").equals("artdialog"))
        {
            if(isAutoincludejs)
            {
                resultBuf.append("<script type=\"text/javascript\"  src=\""+Config.webroot
                        +"webresources/component/artDialog/artDialog.js\"></script>");
                resultBuf.append("<script type=\"text/javascript\"  src=\""+Config.webroot
                        +"webresources/component/artDialog/plugins/iframeTools.js\"></script>");
            }
            parentWindowName="artDialog.open.origin";
            closeMeCodeString="art.dialog.close();";
        }else
        {
            parentWindowName="parent";
            closeMeCodeString="parent.closePopupWin();";
        }
        resultBuf.append("<script language=\"javascript\">");
        resultBuf.append("function selectOK(value,name,label,closeme){");
        resultBuf.append("if(name==null||name==''||name=='"+paramname+"'){");
        resultBuf.append(parentWindowName+".setPopUpBoxValueToParent(value,'").append(realinputboxid).append("','").append(fillmode);
        resultBuf.append("','").append(this.owner.getReportBean().getGuid()).append("','").append(this.getTypename()).append("');");
        resultBuf.append("}else{");
        if(isConditionBox)
        {
            resultBuf.append(parentWindowName+".setReportInputBoxValue(\""+this.owner.getReportBean().getPageBean().getId()+"\",\""
                    +this.owner.getReportBean().getId()+"\",true,"+parentWindowName+".getObjectByJsonString(\"{\"+name+\":\\\"\"+value+\"\\\"}\"));");
        }else
        {
            resultBuf.append("var newvalues=\"{\"+name+\":\\\"\"+value+\"\\\"\";");
            resultBuf.append("if(label!=null){newvalues=newvalues+\",\"+name+\"$label:\\\"\"+label+\"\\\"\";}");
            resultBuf.append("newvalues=newvalues+\"}\";");
            AbsReportType reportTypeObj=Config.getInstance().getReportType(this.owner.getReportBean().getType());
            if(reportTypeObj instanceof EditableListReportType2)
            {
                resultBuf.append("var srcboxObj="+parentWindowName+".document.getElementById('"+realinputboxid+"');");//取到弹出窗口对应的源输入框对象，以便下面设置其它列的值时，可以取到其<tr/>对象
                resultBuf
                        .append(parentWindowName+".setEditableListReportColValueInRow(\""+this.owner.getReportBean().getPageBean().getId()+"\",\""
                                +this.owner.getReportBean().getId()
                                +"\","+parentWindowName+".getParentElementObj(srcboxObj,'TR'),"+parentWindowName+".getObjectByJsonString(newvalues));");
            }else
            {
                resultBuf.append(parentWindowName+".setEditableReportColValue(\""+this.owner.getReportBean().getPageBean().getId()+"\",\""
                        +this.owner.getReportBean().getId()+"\","+parentWindowName+".getObjectByJsonString(newvalues),null);");
            }
        }
        resultBuf.append("}");
        resultBuf.append("if(closeme!==false) "+closeMeCodeString+"}");
        resultBuf.append("</script>");
        return resultBuf.toString();
    }    
}

