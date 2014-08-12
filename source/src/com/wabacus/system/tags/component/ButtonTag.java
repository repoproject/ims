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
package com.wabacus.system.tags.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.system.assistant.TagAssistant;
import com.wabacus.util.Consts;

public class ButtonTag extends AbsComponentTag
{
    private static final long serialVersionUID=-8123624007139208727L;

    private final static Log log=LogFactory.getLog(ButtonTag.class);

    private String type;

    private String name;//<button/>的name属性
    
    private String savebinding;
    
    private String deletebinding;

    private String pageurl;
    
    private String beforecallback;
    
    private String dataexportcomponentids;
    
    public void setType(String type)
    {
        this.type=type;
    }

    public void setName(String name)
    {
        this.name=name;
    }

    public void setSavebinding(String savebinding)
    {
        this.savebinding=savebinding;
    }

    public void setDeletebinding(String deletebinding)
    {
        this.deletebinding=deletebinding;
    }

    public void setPageurl(String pageurl)
    {
        this.pageurl=pageurl;
    }

    public void setBeforecallback(String beforecallback)
    {
        this.beforecallback=beforecallback;
    }

    public int doStartTag() throws JspException
    {
        if(Consts.lstDataExportTypes.contains(type))
        {
            this.dataexportcomponentids=this.componentid;
            this.componentid=null;
        }
        return super.doStartTag();
    }

    public int doMyStartTag() throws JspException,IOException
    {
        type=type==null?"":type.trim().toLowerCase();
        name=name==null?"":name.trim();
        savebinding=savebinding==null?"":savebinding.trim();
        deletebinding=deletebinding==null?"":deletebinding.trim();
        if(!name.equals("")&&!type.equals(""))
        {
            log.warn("在<wx:button/>中，同时指定name属性和type属性时，只有name属性有效");
        }else if(name.equals("")&&type.equals(""))
        {
            throw new JspException("当不是循环显示所有按钮时，必须通过type属性或name属性指定要显示的按钮");
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doMyEndTag() throws JspException,IOException
    {
        BodyContent bc=getBodyContent();
        String button=null;
        if(bc!=null) button=bc.getString();
        button=button==null?"":button.trim();
        Map<String,String> attributes=new HashMap<String,String>();
        attributes.put("type",type);
        attributes.put("name",name);
        attributes.put("savebinding",savebinding);
        attributes.put("deletebinding",deletebinding);
        attributes.put("label",button);
        attributes.put("componentids",this.dataexportcomponentids);
        attributes.put("pageurl",pageurl);
        attributes.put("beforecallback",beforecallback);
        if(Consts.lstDataExportTypes.contains(type))
        {
            TagAssistant.getInstance().printlnTag(out,rrequest,TagAssistant.getInstance().getButtonDisplayValue(this.ownerComponentObj,attributes));
        }else
        {
            if(this.displayComponentObj==null) return EVAL_PAGE;
            TagAssistant.getInstance().printlnTag(out,rrequest,TagAssistant.getInstance().getButtonDisplayValue(this.displayComponentObj,attributes));
        }
        return EVAL_PAGE;
    }
}
