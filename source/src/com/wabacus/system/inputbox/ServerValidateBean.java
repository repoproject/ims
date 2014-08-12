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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.exception.WabacusRuntimeWarningException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.util.Consts;
import com.wabacus.util.Tools;

public class ServerValidateBean
{
    private AbsInputBox ownerInputbox;

    private String validatetype;

    private String servervalidateCallback;

    private List<ParamBean> lstParams;

    public ServerValidateBean(AbsInputBox owner)
    {
        this.ownerInputbox=owner;
    }

    public String getValidatetype()
    {
        return validatetype;
    }

    public void setValidatetype(String validatetype)
    {
        this.validatetype=validatetype;
    }

    public String getServervalidateCallback()
    {
        return servervalidateCallback;
    }

    public void setServervalidateCallback(String servervalidateCallback)
    {
        this.servervalidateCallback=servervalidateCallback;
    }

    public List<ParamBean> getLstParams()
    {
        return lstParams;
    }

    public void setLstParams(List<ParamBean> lstParams)
    {
        this.lstParams=lstParams;
    }

    public void addValidateMethod(ReportBean rbean,String methodname,String errormsg)
    {
        if(methodname==null||methodname.trim().equals("")) return;
        ParamBean paramBean=getValidateMethodParamBean(methodname,errormsg,rbean.getLstServerValidateClasses());//先从<report/>中配置的服务器端校验类中找此方法
        if(paramBean==null)
        {
            paramBean=getValidateMethodParamBean(methodname,errormsg,Config.getInstance().getLstServerValidateClasses());
        }
        if(paramBean==null)
        {
            throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，在所有服务器端校验类中没有找到服务器端校验方法："+methodname+"的定义");
        }
        if(this.lstParams==null) this.lstParams=new ArrayList<ParamBean>();
        this.lstParams.add(paramBean);
    }

    private ParamBean getValidateMethodParamBean(String methodname,String errormsg,List<Class> lstClassesTmp)
    {
        if(lstClassesTmp==null||lstClassesTmp.size()==0) return null;
        ParamBean paramBean=null;
        Method mTmp;
        for(Class cTmp:lstClassesTmp)
        {
            try
            {
                mTmp=cTmp.getMethod(methodname,new Class[] { ReportRequest.class, String.class, Map.class, List.class });
            }catch(NoSuchMethodException nse)
            {
                continue;
            }
            if(mTmp==null) continue;
            paramBean=new ParamBean();
            paramBean.setValidateClass(cTmp);
            paramBean.setValidateMethod(mTmp);
            Object[] objArr=WabacusAssistant.getInstance().parseStringWithDynPart(errormsg);
            paramBean.setMessage((String)objArr[0]);
            paramBean.setMDynMessageParts((Map<String,String>)objArr[1]);
            break;
        }
        return paramBean;
    }

    public boolean validate(ReportRequest rrequest,String boxvalue,Map<String,String> mOtherValues,List<String> lstErrorMessages,boolean isOnBlur)
    {
        if(lstParams==null||lstParams.size()==0) return true;
        if(isOnBlur&&AbsInputBox.VALIDATE_TYPE_ONSUBMIT.equals(validatetype)||!isOnBlur&&AbsInputBox.VALIDATE_TYPE_ONBLUR.equals(validatetype))
        {
            return true;
        }
        boolean bolResult=true;
        for(ParamBean paramBeanTmp:this.lstParams)
        {
            lstErrorMessages.clear();
            lstErrorMessages.add(paramBeanTmp.getMessage(rrequest,boxvalue));
            if(!paramBeanTmp.validate(rrequest,boxvalue,mOtherValues,lstErrorMessages))
            {
                bolResult=false;
                break;
            }
        }
        if(!isOnBlur&&!bolResult)
        {
            StringBuffer errormsgBuf=new StringBuffer();
            for(String errormsgTmp:lstErrorMessages)
            {
                if(errormsgTmp==null||errormsgTmp.trim().equals("")) continue;
                errormsgBuf.append(errormsgTmp).append(";");
            }
            if(errormsgBuf.length()>0&&errormsgBuf.charAt(errormsgBuf.length()-1)==';') errormsgBuf.deleteCharAt(errormsgBuf.length()-1);
            if(this.servervalidateCallback!=null&&!this.servervalidateCallback.trim().equals(""))
            {
                StringBuffer paramsBuf=new StringBuffer();
                paramsBuf.append("{inputboxid:\"").append(this.ownerInputbox.getOwner().getInputBoxId()).append("\"");
                paramsBuf.append(",value:\"").append(boxvalue).append("\"");
                paramsBuf.append(",errormess:\"").append(errormsgBuf.toString()).append("\"");
                paramsBuf.append(",validatetype:\"onsubmit\"");
                paramsBuf.append(",isSuccess:false");
                paramsBuf.append(",serverDataObj:{");
                if(rrequest.getMServerValidateDatas()!=null&&rrequest.getMServerValidateDatas().size()>0)
                {
                    for(Entry<String,String> entryTmp:rrequest.getMServerValidateDatas().entrySet())
                    {
                        paramsBuf.append(entryTmp.getKey()+":\""+entryTmp.getValue()+"\",");
                    }
                    if(paramsBuf.charAt(paramsBuf.length()-1)==',') paramsBuf.deleteCharAt(paramsBuf.length()-1);
                }
                paramsBuf.append("}}");
                rrequest.getWResponse().addOnloadMethod(this.servervalidateCallback,paramsBuf.toString(),true,Consts.STATECODE_FAILED);
            }
            if(errormsgBuf.toString().trim().equals(""))
            {
                rrequest.getWResponse().setStatecode(Consts.STATECODE_FAILED);
                throw new WabacusRuntimeWarningException();
            }else
            {
                rrequest.getWResponse().getMessageCollector().warn(errormsgBuf.toString(),true,Consts.STATECODE_FAILED);
            }
        }
        return bolResult;
    }

    private class ParamBean
    {
        private String message;

        private Map<String,String> mDynMessageParts;

        private Class validateClass;

        private Method validateMethod;

        public String getMessage()
        {
            return message;
        }

        public String getMessage(ReportRequest rrequest,String boxvalue)
        {
            String realmess=WabacusAssistant.getInstance().getStringValueWithDynPart(rrequest,this.message,this.mDynMessageParts,"").trim();
            if(boxvalue==null) boxvalue="";
            realmess=Tools.replaceAll(realmess,"#label#",ownerInputbox.getOwner().getLabel(rrequest));
            realmess=Tools.replaceAll(realmess,"#data#",boxvalue);
            return realmess;
        }

        public void setMessage(String message)
        {
            this.message=message;
        }

        public Map<String,String> getMDynMessageParts()
        {
            return mDynMessageParts;
        }

        public void setMDynMessageParts(Map<String,String> dynMessageParts)
        {
            mDynMessageParts=dynMessageParts;
        }

        public Class getValidateClass()
        {
            return validateClass;
        }

        public void setValidateClass(Class validateClass)
        {
            this.validateClass=validateClass;
        }

        public Method getValidateMethod()
        {
            return validateMethod;
        }

        public void setValidateMethod(Method validateMethod)
        {
            this.validateMethod=validateMethod;
        }

        public boolean validate(ReportRequest rrequest,String boxvalue,Map<String,String> mOtherValues,List<String> lstErrorMessages)
        {
            try
            {
                return (Boolean)this.validateMethod.invoke(this.validateClass,new Object[] { rrequest, boxvalue, mOtherValues, lstErrorMessages });
            }catch(Exception e)
            {
                throw new WabacusRuntimeException("对报表"+ownerInputbox.getOwner().getReportBean().getPath()+"的输入框"
                        +ownerInputbox.getOwner().getInputBoxId()+"的数据"+boxvalue+"进行服务器端校验时失败",e);
            }
        }
    }
}
