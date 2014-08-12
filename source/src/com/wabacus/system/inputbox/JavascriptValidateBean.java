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
import java.util.List;
import java.util.Map;

import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.util.Tools;

public class JavascriptValidateBean
{
    private String validatetype;

    private List<ParamBean> lstParams;

    public void setValidatetype(String validatetype)
    {
        this.validatetype=validatetype;
    }

    public void addParamBean(String jsparamname,String message)
    {
        if(lstParams==null) lstParams=new ArrayList<ParamBean>();
        ParamBean paramBean=new ParamBean();
        paramBean.setJsparamname(jsparamname);
        Object[] objArr=WabacusAssistant.getInstance().parseStringWithDynPart(message);
        paramBean.setMessage((String)objArr[0]);
        paramBean.setMDynMessageParts((Map<String,String>)objArr[1]);
        lstParams.add(paramBean);
    }

    public String displayValidateInfoOnMetadata(ReportRequest rrequest,String inputboxid)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(" validateMethod_"+inputboxid+"=\"{method:validate_"+inputboxid+"}\"");
        if(validatetype!=null) resultBuf.append(" validateType_"+inputboxid+"=\""+validatetype+"\"");
        if(lstParams!=null&&lstParams.size()>0)
        {
            resultBuf.append(" jsValidateParamsObj_"+inputboxid+"=\"{");
            for(ParamBean paramBeanTmp:lstParams)
            {
                resultBuf.append(paramBeanTmp.getJsparamname()).append(":'");
                resultBuf.append(Tools.jsParamEncode(paramBeanTmp.getRealMessage(rrequest)));
                resultBuf.append("',");
            }
            if(resultBuf.charAt(resultBuf.length()-1)==',') resultBuf.deleteCharAt(resultBuf.length()-1);
            resultBuf.append("}\"");
        }
        return resultBuf.toString();
    }
    
    private class ParamBean
    {
        private String jsparamname;

        private String message;

        private Map<String,String> mDynMessageParts;

        public String getJsparamname()
        {
            return jsparamname;
        }

        public void setJsparamname(String jsparamname)
        {
            this.jsparamname=jsparamname;
        }

        public String getMessage()
        {
            return message;
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

        public String getRealMessage(ReportRequest rrequest)
        {
            return WabacusAssistant.getInstance().getStringValueWithDynPart(rrequest,this.message,this.mDynMessageParts,"").trim();
        }
    }
}
