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

public class RadioBox extends AbsRadioCheckBox
{
    public RadioBox(String typename)
    {
        super(typename);
    }

    public String getInputboxInnerType()
    {
        return "radio";
    }
    
    protected boolean isMultipleSelect()
    {
        return false;
    }

    public String createGetValueByIdJs()
    {
        return createGetContentByIdJs(true);
    }
    
    public String createGetLabelByIdJs()
    {
        return createGetContentByIdJs(false);
    }

    private String createGetContentByIdJs(boolean isGetValue)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("var radioObjs=document.getElementsByName(id);");
        resultBuf.append("if(radioObjs!=null){");
        resultBuf.append("  for(i=0;i<radioObjs.length;i=i+1){");
        resultBuf.append("      if(radioObjs[i].checked){");
        if(isGetValue)
        {
            resultBuf.append("          return radioObjs[i].value;");
        }else
        {
            resultBuf.append("          return radioObjs[i].getAttribute('label');");
        }
        resultBuf.append("      }");
        resultBuf.append("  }");
        resultBuf.append("  return '';");
        resultBuf.append("}");
        resultBuf.append("return '';");
        return resultBuf.toString();
    }
    
    public String createSetInputBoxValueByIdJs()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("var radioObjs=document.getElementsByName(id);");
        resultBuf.append("if(radioObjs!=null&&radioObjs.length>0){");
        resultBuf.append("  for(var i=0,len=radioObjs.length;i<len;i=i+1){");
        resultBuf.append("      if(radioObjs[i].value==newvalue){radioObjs[i].checked=true;break;}");
        resultBuf.append("  }");
        resultBuf.append("}");
        return resultBuf.toString();
    }

    public String createGetValueByInputBoxObjJs()
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("var radioname=boxObj.getAttribute('name');");
        resultBuf.append("if(radioname!=null&&radioname!=''){");
        resultBuf.append("  var radioObjs=document.getElementsByName(radioname);");
        resultBuf.append("  if(radioObjs!=null&&radioObjs.length>0){");
        resultBuf.append("      for(i=0,len=radioObjs.length;i<len;i=i+1){");
        resultBuf.append("          if(radioObjs[i].checked){");
        resultBuf.append("              value=radioObjs[i].value;label=radioObjs[i].getAttribute('label');break;");
        resultBuf.append("          }");
        resultBuf.append("      }");
        resultBuf.append("  }");
        resultBuf.append("}");
        return resultBuf.toString();
    }
    
    public void loadInputBoxConfig(IInputBoxOwnerBean ownerbean,XmlElementBean eleInputboxBean)
    {
        super.loadInputBoxConfig(ownerbean,eleInputboxBean);
        this.isMultiply=false;
        this.separator=null;
    }
}
