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

import com.wabacus.config.xml.XmlElementBean;


public class CheckBox extends AbsRadioCheckBox
{
    public CheckBox(String typename)
    {
        super(typename);
    }
    
    protected boolean isMultipleSelect()
    {
        return true;
    }
    
    public String createGetValueByIdJs()
    {
        return createGetContentByIdJs(true);
    }

    public String createGetLabelByIdJs()
    {
        return createGetContentByIdJs(false);
    }
    
    public String getInputboxInnerType()
    {
        return "checkbox";
    }

    private String createGetContentByIdJs(boolean isGetValue)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("var chkObjs=document.getElementsByName(id);");
        resultBuf.append("if(chkObjs==null||chkObjs.length==0) return '';");
        resultBuf.append("var value=''; var separator=chkObjs[0].getAttribute('separator');if(separator==null||separator=='') separator=' ';");
        resultBuf.append("for(i=0,len=chkObjs.length;i<len;i=i+1){");
        resultBuf.append("    if(chkObjs[i].checked){");
        if(isGetValue)
        {
            resultBuf.append("        value=value+chkObjs[i].value+separator;");
        }else
        {
            resultBuf.append("        value=value+chkObjs[i].getAttribute('label')+separator;");
        }
        resultBuf.append("    }");
        resultBuf.append("}");
        resultBuf.append("value=wx_rtrim(value,separator);");
        resultBuf.append("return value;");
        return resultBuf.toString();
    }
    
    public String createGetValueByInputBoxObjJs()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("var chkboxname=boxObj.getAttribute('name');");
        resultBuf.append("if(chkboxname==null||chkboxname=='') return '';");
        resultBuf.append("var chkObjs=document.getElementsByName(chkboxname);");
        resultBuf.append("if(chkObjs==null||chkObjs.length==0) return '';");
        resultBuf.append("var value='';var label=''; var separator=chkObjs[0].getAttribute('separator');if(separator==null||separator=='') separator=' ';");
        resultBuf.append("for(i=0,len=chkObjs.length;i<len;i=i+1){");
        resultBuf.append("    if(chkObjs[i].checked){");
        resultBuf.append("        label=label+chkObjs[i].getAttribute('label')+separator;value=value+chkObjs[i].value+separator;");
        resultBuf.append("    }");
        resultBuf.append("}");
        resultBuf.append("label=wx_rtrim(label,separator);");
        resultBuf.append("value=wx_rtrim(value,separator);");
        return resultBuf.toString();
    }
    
    public String createSetInputBoxValueByIdJs()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("var chkObjs=document.getElementsByName(id);");
        resultBuf.append("if(chkObjs==null||chkObjs.length==0) return;");
        resultBuf.append("for(var i=0,len=chkObjs.length;i<len;i=i+1){");
        resultBuf.append("  if(isSelectedValueForSelectedBox(newvalue,chkObjs[i].value,chkObjs[0])){chkObjs[i].checked=true;}");
        resultBuf.append("}");
        return resultBuf.toString();
    }
    
    public void loadInputBoxConfig(IInputBoxOwnerBean ownerbean,XmlElementBean eleInputboxBean)
    {
        this.isMultiply=true;
        this.separator=eleInputboxBean.attributeValue("separator");
        if(this.separator==null||this.separator.equals("")) this.separator=" ";
        super.loadInputBoxConfig(ownerbean,eleInputboxBean);
    }
}
