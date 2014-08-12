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
package com.wabacus.exception;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.wabacus.system.WabacusResponse;
import com.wabacus.util.Consts;

public class MessageCollector
{
    private WabacusResponse wresponse;
    
    private List<String> lstLogErrors;

    private List<String> lstLogWarns;

    private List<String> lstJsWarnMessages;
    
    private List<String> lstJsAlertMessages;
    
    private List<String> lstJsSuccessMessages;
    
    private List<String> lstJsErrorMessages;
    
    private String confirmmessage;
    
    private String confirmkey;
    
    private String confirmurl;
    
    public MessageCollector(WabacusResponse wresponse)
    {
        lstLogErrors=new ArrayList<String>();
        lstLogWarns=new ArrayList<String>();
        lstJsAlertMessages=new ArrayList<String>();
        lstJsSuccessMessages=new ArrayList<String>();
        lstJsWarnMessages=new ArrayList<String>();
        lstJsErrorMessages=new ArrayList<String>();
        this.wresponse=wresponse;
    }

    public void error(String errorInfo,boolean isTerminate)
    {
        if(errorInfo!=null&&!errorInfo.trim().equals(""))
        {
            lstLogErrors.add(errorInfo.trim());
            lstJsErrorMessages.add(errorInfo.trim());
        }
        wresponse.setStatecode(Consts.STATECODE_FAILED);
        if(isTerminate) throw new WabacusRuntimeException();
    }

    public void error(String alertErrorInfo,String logErrorInfo,boolean isTerminate)
    {
        if(alertErrorInfo!=null&&!alertErrorInfo.trim().equals(""))
        {
            lstJsErrorMessages.add(alertErrorInfo);
        }
        if(logErrorInfo!=null&&!logErrorInfo.trim().equals(""))
        {
            lstLogErrors.add(logErrorInfo);
        }
        wresponse.setStatecode(Consts.STATECODE_FAILED);
        if(isTerminate) throw new WabacusRuntimeException();
    }

    public void error(String alertErrorInfo,String logErrorInfo,Throwable t)
    {
        if(alertErrorInfo!=null&&!alertErrorInfo.trim().equals(""))
        {
            lstJsErrorMessages.add(alertErrorInfo);
        }
        if(logErrorInfo!=null&&!logErrorInfo.trim().equals(""))
        {
            lstLogErrors.add(logErrorInfo);
        }
        wresponse.setStatecode(Consts.STATECODE_FAILED);
        throw new WabacusRuntimeException(t);
    }

    public void warn(String alertWarninfo,boolean isTerminate)
    {
        if(alertWarninfo!=null&&!alertWarninfo.trim().equals("")&&!lstJsWarnMessages.contains(alertWarninfo)) lstJsWarnMessages.add(alertWarninfo);
        if(isTerminate) throw new WabacusRuntimeWarningException(alertWarninfo);
    }
    
    public void warn(String alertWarninfo,boolean isTerminate,int statecode)
    {
        if(alertWarninfo!=null&&!alertWarninfo.trim().equals("")&&!lstJsWarnMessages.contains(alertWarninfo)) lstJsWarnMessages.add(alertWarninfo);
        wresponse.setStatecode(statecode);
        if(isTerminate) throw new WabacusRuntimeWarningException(alertWarninfo);
    }
    
    public void warn(String alertWarninfo,String logWarninfo,boolean isTerminate,int statecode)
    {
        if(alertWarninfo!=null&&!alertWarninfo.trim().equals(""))
        {
            lstJsWarnMessages.add(alertWarninfo);
        }
        if(logWarninfo!=null&&!logWarninfo.trim().equals(""))
        {
            lstLogWarns.add(logWarninfo);
        }
        wresponse.setStatecode(statecode);
        if(isTerminate) throw new WabacusRuntimeWarningException();
    }

    public void warn(String alertWarninfo,String logWarninfo,Throwable t,int statecode)
    {
        if(alertWarninfo!=null&&!alertWarninfo.trim().equals(""))
        {
            lstJsWarnMessages.add(alertWarninfo);
        }
        if(logWarninfo!=null&&!logWarninfo.trim().equals(""))
        {
            lstLogWarns.add(logWarninfo);
        }
        wresponse.setStatecode(statecode);
        throw new WabacusRuntimeWarningException(t);
    }

    public void alert(String alertinfo,boolean isTerminate)
    {
        if(alertinfo!=null&&!alertinfo.trim().equals("")&&!lstJsAlertMessages.contains(alertinfo)) lstJsAlertMessages.add(alertinfo);
        if(isTerminate) throw new WabacusRuntimeWarningException();
    }
    
    public void success(String successinfo,boolean isTerminate)
    {
        if(successinfo!=null&&!successinfo.trim().equals("")&&!lstJsSuccessMessages.contains(successinfo)) lstJsSuccessMessages.add(successinfo);
        if(isTerminate) throw new WabacusRuntimeWarningException();
    }
    
    public void confirm(String key,String message)
    {
        String confirmvalue=this.wresponse.getRRequest().getStringAttribute(key,"");
        HttpServletRequest request=this.wresponse.getRRequest().getRequest();
        if(request==null||message==null||message.trim().equals("")||key==null||key.trim().equals("")) return;
        String url=request.getRequestURI();
        String sign="?";
        Enumeration enumer=request.getParameterNames();
        while(enumer.hasMoreElements())
        {
            String name=(String)enumer.nextElement();
            if(name==null||name.trim().equals("")) continue;
            String[] values=request.getParameterValues(name);
            if(values==null) continue;
            for(int i=0;i<values.length;i++)
            {
                url+=sign+name+"="+values[i];
                sign="&";
            }
        }
        this.confirmmessage=message;
        this.confirmkey=key;
        this.confirmurl=url;
        throw new WabacusRuntimeWarningException();
    }
    
    public boolean hasErrors()
    {
        if(lstJsErrorMessages!=null&&lstJsErrorMessages.size()>0) return true;
        if(lstLogErrors!=null&&lstLogErrors.size()>0) return true;
        return false;
    }

    public boolean hasWarnings()
    {
        if(lstJsWarnMessages!=null&&lstJsWarnMessages.size()>0) return true;
        if(lstLogWarns!=null&&lstLogWarns.size()>0) return true;
        return false;
    }

    public String getLogErrorsMessages()
    {
        if(lstLogErrors==null||lstLogErrors.size()==0) return "";
        StringBuffer sbuffer=new StringBuffer();
        for(String errorinfoTmp:lstLogErrors)
        {
            if(errorinfoTmp==null||errorinfoTmp.trim().equals("")) continue;
            sbuffer.append(errorinfoTmp).append("\n");
        }
        return sbuffer.toString();
    }

    public String getLogWarnsMessages()
    {
        if(lstLogWarns==null||lstLogWarns.size()==0) return "";
        StringBuffer sbuffer=new StringBuffer();
        for(String warnInfoTmp:lstLogWarns)
        {
            if(warnInfoTmp==null||warnInfoTmp.trim().equals("")) continue;
            sbuffer.append(warnInfoTmp).append("\n");
        }
        return sbuffer.toString();
    }
    
    public String getJsErrorMessages(String seperator)
    {
        if(lstJsErrorMessages==null||lstJsErrorMessages.size()==0) return "";
        StringBuffer sbuffer=new StringBuffer();
        if(seperator==null||seperator.equals("")) seperator="\n";
        for(String errorinfoTmp:lstJsErrorMessages)
        {
            if(errorinfoTmp==null||errorinfoTmp.trim().equals("")) continue;
            sbuffer.append(errorinfoTmp).append(seperator);
        }
        return sbuffer.toString();
    }

    public String getJsWarnMessages(String seperator)
    {
        if(lstJsWarnMessages==null||lstJsWarnMessages.size()==0) return "";
        if(seperator==null||seperator.equals("")) seperator="\n";
        StringBuffer sbuffer=new StringBuffer();
        for(String warnInfoTmp:lstJsWarnMessages)
        {
            if(warnInfoTmp==null||warnInfoTmp.trim().equals("")) continue;
            sbuffer.append(warnInfoTmp).append(seperator);
        }
        return sbuffer.toString();
    }
    
    public String getJsSuccessMessages(String seperator)
    {
        if(lstJsSuccessMessages==null||lstJsSuccessMessages.size()==0) return "";
        if(seperator==null||seperator.equals("")) seperator="\n";
        StringBuffer sbuffer=new StringBuffer();
        for(String successInfoTmp:lstJsSuccessMessages)
        {
            if(successInfoTmp==null||successInfoTmp.trim().equals("")) continue;
            sbuffer.append(successInfoTmp).append(seperator);
        }
        return sbuffer.toString();
    }
    
    public String getJsAlertMessages(String seperator)
    {
        if(lstJsAlertMessages==null||lstJsAlertMessages.size()==0) return "";
        if(seperator==null||seperator.equals("")) seperator="\n";
        StringBuffer sbuffer=new StringBuffer();
        for(String alertInfoTmp:lstJsAlertMessages)
        {
            if(alertInfoTmp==null||alertInfoTmp.trim().equals("")) continue;
            sbuffer.append(alertInfoTmp).append(seperator);
        }
        return sbuffer.toString();
    }

    public String getConfirmmessage()
    {
        return confirmmessage;
    }

    public String getConfirmkey()
    {
        return confirmkey;
    }

    public String getConfirmurl()
    {
        return confirmurl;
    }
}
