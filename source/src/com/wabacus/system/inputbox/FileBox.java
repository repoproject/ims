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
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.intercept.AbsFileUploadInterceptor;
import com.wabacus.util.Tools;

public class FileBox extends AbsPopUpBox implements Cloneable
{
    private long maxsize=-1;

    private String allowTypes;

    private String savePath;//文件保存路径，格式为：absolute{path}/relative{path}之一

    private String newfilename;

    private String displaytype="inputbox";

    private String rooturl;
    
    private int deletetype=1;

    private AbsFileUploadInterceptor interceptor;
    
    public FileBox(String typename)
    {
        super(typename);
    }

    public String getInputboxInnerType()
    {
        return "filebox";
    }
    
    public long getMaxsize()
    {
        return maxsize;
    }

    public String getSavePath()
    {
        return savePath;
    }

    public String getAllowTypes()
    {
        return allowTypes;
    }

    public String getNewfilename()
    {
        return newfilename;
    }

    public String getRooturl()
    {
        return rooturl;
    }

    public int getDeletetype()
    {
        return deletetype;
    }
    
    public AbsFileUploadInterceptor getInterceptor()
    {
        return interceptor;
    }

    protected String doGetDisplayStringValue(ReportRequest rrequest,String value,String style_property,boolean isReadonly)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(this.getBeforedescription(rrequest));
        String realinputboxid=getInputBoxId(rrequest);
        if(!isReadonly)
        {
            style_property=Tools.mergeHtmlTagPropertyString(style_property,"onclick=\"try{popupPageByFileUploadInputbox('"+realinputboxid+"','"
                    +getPopupUrlJsonString()+"','"+this.displaytype+"')}catch(e){logErrorsAsJsFileLoad(e);}\"",1);
        }
        value=getInputBoxValue(rrequest,value);
        if(this.displaytype.equals("image"))
        {//利用<img/>显示图片，并加上进入文件上传的超链接
            String imgurl=value;
            if(value==null||value.trim().equals("")) imgurl=Config.webroot+"webresources/skin/nopicture.gif";
            resultBuf.append("<img  src=\"").append(imgurl).append("\" srcpath=\"").append(value).append("\"");
        }else
        {
            resultBuf.append("<input  type='text'  value=\""+value+"\"");
        }
        resultBuf.append(" typename='"+typename+"' name='"+realinputboxid+"' id='"+realinputboxid+"'");
        if(style_property!=null) resultBuf.append(" ").append(style_property);
        resultBuf.append("/>");
        resultBuf.append(this.getAfterdescription(rrequest));
        return resultBuf.toString();
    }

    protected String initDisplaySpanStart(ReportRequest rrequest)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(super.initDisplaySpanStart(rrequest));
        resultBuf.append(" displaytype=\""+this.displaytype.trim()+"\"");
        resultBuf.append(" paramsOfGetPageUrl=\"").append(getPopupUrlJsonString()).append("\"");
        return resultBuf.toString();
    }

    public String filledInContainer(String onblur)
    {
        StringBuffer resultBuf=new StringBuffer();
        
        resultBuf.append("var displaytype=null;onclick_propertyvalue=null;");
        resultBuf.append("if(inputboxSpanObj!=null){ ");
        resultBuf.append("var paramsOfGetPageUrl=inputboxSpanObj.getAttribute('paramsOfGetPageUrl');");
        resultBuf.append("  displaytype=inputboxSpanObj.getAttribute('displaytype');onclick_propertyvalue=inputboxSpanObj.getAttribute('onclick_propertyvalue');");
        resultBuf.append("}");
        resultBuf.append("if(displaytype==null||displaytype=='') displaytype='textbox';");
        resultBuf.append("if(onclick_propertyvalue==null) onclick_propertyvalue='';");
        resultBuf.append("onclick_propertyvalue=\"popupPageByFileUploadInputbox('\"+name+\"','\"+paramsOfGetPageUrl+\"','\"+displaytype+\"');\"+onclick_propertyvalue;");
        
        resultBuf.append("if(displaytype=='image'){");//以<img/>显示上传的图片形式
        resultBuf.append(" var imgurl=boxValue;if(imgurl==null||imgurl=='') imgurl=WXConfig.webroot+'webresources/skin/nopicture.gif';");
        resultBuf.append("  boxstr=\"<img  src=\\\"\"+imgurl+\"\\\" srcpath=\\\"\"+boxValue+\"\\\"\";");
        resultBuf.append("}else{");//以文本框形式显示文件路径
        resultBuf.append("  boxstr=\"<input type='text' value=\\\"\"+boxValue+\"\\\"\";");
        resultBuf.append("}");
        resultBuf.append(getInputBoxCommonFilledProperties());
        resultBuf.append("if(onfocusmethod!=null&&onfocusmethod!='') boxstr=boxstr+\" onfocus=\\\"try{\"+onfocusmethod+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("if(onblurmethod!=null&&onblurmethod!='') boxstr=boxstr+\" onblur=\\\"try{\"+onblurmethod+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\"\";");
        resultBuf.append("boxstr=boxstr+\" onclick=\\\"try{\"+onclick_propertyvalue+\"}catch(e){logErrorsAsJsFileLoad(e);}\\\"/>\";");
        return resultBuf.toString();
    }

    protected String getInputBoxCommonFilledProperties()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("if(displaytype=='image'){");
        resultBuf.append("  boxstr=boxstr+\" id= '\"+name+\"' name='\"+name+\"'");
        resultBuf.append("  typename='"+this.typename+"' \"+styleproperty;");
        resultBuf.append("  boxstr=boxstr+\" style=\\\"\"+style_propertyvalue+\"\\\"\";");
        resultBuf.append("}else{");
        resultBuf.append(super.getInputBoxCommonFilledProperties());
        resultBuf.append("}");
        return resultBuf.toString();
    }

    public String createGetValueByIdJs()
    {
        StringBuffer buf=new StringBuffer();
        buf.append("var obj=document.getElementById(id);");
        buf.append("if(obj!=null){var valTemp='';if(obj.tagName=='IMG'){valTemp=obj.getAttribute('srcpath');");
        buf.append("}else{valTemp=obj.value;}");
        buf.append("return valTemp;}");
        buf.append("return null;");
        return buf.toString();
    }

    public String createSetInputBoxValueByIdJs()
    {
        StringBuffer buf=new StringBuffer();
        buf.append("var boxObj=document.getElementById(id);");
        buf.append("if(boxObj){");
        buf.append("if(boxObj.tagName=='IMG'){boxObj.setAttribute('srcpath',newvalue);boxObj.src=newvalue;");
        buf.append("}else{boxObj.value=newvalue;}");
        buf.append("}");
        return buf.toString();
    }

    public String createGetValueByInputBoxObjJs()
    {
        StringBuffer sbuffer=new StringBuffer();
        sbuffer.append("if(boxObj.tagName=='IMG'){value=boxObj.getAttribute('srcpath'); label=value;");
        sbuffer.append("}else{value=boxObj.value; label=boxObj.value;}");
        return sbuffer.toString();
    }

    public void loadInputBoxConfig(IInputBoxOwnerBean ownerbean,XmlElementBean eleInputboxBean)
    {
        String strmaxsize=eleInputboxBean.attributeValue("maxsize");
        if(strmaxsize!=null)
        {
            strmaxsize=strmaxsize.trim();
            if(strmaxsize.equals(""))
            {
                this.maxsize=-1;
            }else
            {
                try
                {
                    this.maxsize=Long.parseLong(strmaxsize)*1024;
                }catch(NumberFormatException e)
                {
                    this.maxsize=-1;
                }
            }
        }
        this.allowTypes=eleInputboxBean.attributeValue("allowedtypes");
        String savepath=eleInputboxBean.attributeValue("savepath");
        if(savepath!=null&&!savepath.trim().equals(""))
        {
            savepath=Config.getInstance().getResourceString(null,ownerbean.getReportBean().getPageBean(),savepath,true);
            if(Tools.isDefineKey("classpath",savepath))
            {
                throw new WabacusConfigLoadingException("报表"+ownerbean.getReportBean().getPath()+"配置的文件上传输入框不合法，不能将savepath配置为classpath{}格式");
            }
            if(!Tools.isDefineKey("absolute",savepath)&&!Tools.isDefineKey("relative",savepath))
            {
                throw new WabacusConfigLoadingException("报表"+ownerbean.getReportBean().getPath()+"配置的文件上传输入框不合法，保存路径必须配置为absolute{}或relative{}格式");
            }
            this.savePath=WabacusAssistant.getInstance().parseConfigPathToRealPath(savepath,Config.webroot_abspath);
        }
        String newfilename=eleInputboxBean.attributeValue("newfilename");
        if(newfilename!=null)
        {
            if(newfilename.trim().equals(""))
            {
                this.newfilename=null;
            }else
            {
                this.newfilename=newfilename.trim();
            }
        }
        String displaytype=eleInputboxBean.attributeValue("displaytype");
        if(displaytype!=null)
        {
            displaytype=displaytype.toLowerCase().trim();
            if(displaytype.equals(""))
            {
                displaytype="textbox";
            }
            if(!displaytype.equals("textbox")&&!displaytype.equals("image"))
            {
                throw new WabacusConfigLoadingException("报表"+ownerbean.getReportBean().getPath()+"配置的文件上传输入框的显示类型不合法，只能配置为textbox和image之一");
            }
            this.displaytype=displaytype;
        }
        String rooturl=eleInputboxBean.attributeValue("rooturl");
        if(rooturl!=null&&!rooturl.trim().equals(""))
        {
            rooturl=rooturl.trim();
            rooturl=Config.getInstance().getResourceString(null,ownerbean.getReportBean().getPageBean(),rooturl,true);
            if(!rooturl.startsWith(Config.webroot)&&!rooturl.toLowerCase().startsWith("http://"))
            {
                rooturl=Config.webroot+"/"+rooturl;
            }
            rooturl=Tools.replaceAll(rooturl,"http://","HTTP:||");
            rooturl=Tools.replaceAll(rooturl,"//","/");
            rooturl=Tools.replaceAll(rooturl,"HTTP:||","http://");
            if(!rooturl.endsWith("/")) rooturl=rooturl+"/";
            this.rooturl=rooturl.trim();
        }
        if(displaytype!=null&&displaytype.equals("image")&&(this.rooturl==null||this.rooturl.equals("")))
        {
            throw new WabacusConfigLoadingException("报表"+ownerbean.getReportBean().getPath()+"配置的文件上传输入框的显示类型为image时，必须配置rooturl属性");
        }
        String delete=eleInputboxBean.attributeValue("deletetype");
        if(delete!=null)
        {
            delete=delete.trim();
            if(delete.equals(""))
            {
                this.deletetype=1;
            }else
            {
                try
                {
                    this.deletetype=Integer.parseInt(delete);
                }catch(NumberFormatException e)
                {
                    throw new WabacusConfigLoadingException("报表"+ownerbean.getReportBean().getPath()+"配置的文件上传输入框的deletetype属性不是合法数字",e);
                }
                if(this.deletetype<0||this.deletetype>2)
                {
                    throw new WabacusConfigLoadingException("报表"+ownerbean.getReportBean().getPath()+"配置的文件上传输入框的deletetype属性不是有效数字，只支持0，1，2三个值");
                }
            }
        }
        this.poppageurl=eleInputboxBean.attributeValue("inputboxparams");
        String interceptor=eleInputboxBean.attributeValue("interceptor");
        if(interceptor!=null)
        {
            if(interceptor.trim().equals(""))
            {
                this.interceptor=null;
            }else
            {
                this.interceptor=AbsFileUploadInterceptor.createInterceptorObj(interceptor.trim());
            }
        }
        super.loadInputBoxConfig(ownerbean,eleInputboxBean);
        ownerbean.getReportBean().addUploadFileBox(this);
    }

    protected String getDefaultStylePropertyForDisplayMode2()
    {
        if(displaytype.equals("image"))
        {//如果当前是以<img/>形式显示上传的图片文件
            return "class=\"cls-img-normal\" onmouseover=\"this.className='cls-img-mouseover';\" onmouseout=\"this.className='cls-img-normal';\"";
        }
        return super.getDefaultStylePropertyForDisplayMode2();
    }
    
    public void doPostLoad(IInputBoxOwnerBean ownerbean)
    {
        if(this.owner instanceof ConditionBean)
        {
            throw new WabacusConfigLoadingException("加载输入框"+this.owner.getInputBoxId()+"失败，文件上传输入框不能做为查询条件输入框");
        }
        if(displaytype.equals("image"))
        {//如果当前是以<img/>形式显示上传的图片文件，则不加上defaultstyleproperty中定义的样式字符串，只加上如下属性
            this.defaultstyleproperty="class=\"cls-img-normal\" onmouseover=\"this.className='cls-img-mouseover';\" onmouseout=\"this.className='cls-img-normal';\"";
        }
        super.doPostLoad(ownerbean);
    }

    protected String getDefaultWidth()
    {
        return "300";
    }
    
    protected String getDefaultHeight()
    {
        return "160";
    }
    
    protected void addJsValidateOnBlurEvent(AbsReportType reportTypeObj,IInputBoxOwnerBean ownerbean)
    {
        if(!displaytype.equals("image"))
        {//只有不是以<img/>形式显示上传文件时才提供失去焦点时的校验功能，并加上readonly
            super.addJsValidateOnBlurEvent(reportTypeObj,ownerbean);
        }
    }
    
    public Object clone(IInputBoxOwnerBean owner)
    {
        FileBox fbNew=(FileBox)super.clone(owner);
        if(owner!=null&&owner.getReportBean()!=null)
        {
            owner.getReportBean().addUploadFileBox(fbNew);
        }
        return fbNew;
    }

    public String getFilePathOrUrl(String name)
    {
        if(name==null) return "";
        if(this.rooturl==null||this.rooturl.trim().equals(""))
        {
            return this.savePath+name;
        }else
        {
            while(name.startsWith("/")) name=name.substring(1);
            return this.rooturl+name;
        }
    }
}
