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
package com.wabacus.config.template;

import java.util.ArrayList;
import java.util.List;

import com.wabacus.config.template.tags.AbsTagInTemplate;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.AbsComponentType;
import com.wabacus.util.Consts_Private;
import com.wabacus.util.Tools;

public class TemplateBean
{
    private String content="";
    
    private List<AbsTagInTemplate> lstTagChildren;

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content=content;
    }

    public List<AbsTagInTemplate> getLstTagChildren()
    {
        return lstTagChildren;
    }

    public void setLstTagChildren(List<AbsTagInTemplate> lstTagChildren)
    {
        this.lstTagChildren=lstTagChildren;
    }
    
    public void addChildTag(AbsTagInTemplate tagbean)
    {
        if(lstTagChildren==null)
        {
            lstTagChildren=new ArrayList<AbsTagInTemplate>();
        }
        lstTagChildren.add(tagbean);
    }
    
    public String getDisplayValue(ReportRequest rrequest,AbsComponentType ownerComponentObj)
    {
        StringBuffer sbuffer=new StringBuffer();
        if(lstTagChildren==null||lstTagChildren.size()==0) return content;
        int start=lstTagChildren.get(0).getStartposition();
        int end=lstTagChildren.get(lstTagChildren.size()-1).getEndposition()+1;
        sbuffer.append(content.substring(0,start));
        int endPrev=-1;
        int startCurr=-1;
        for(AbsTagInTemplate tagObjTmp:lstTagChildren)
        {
            startCurr=tagObjTmp.getStartposition();
            if(endPrev>0&&startCurr-endPrev>1)
            {
                sbuffer.append(content.substring(endPrev+1,startCurr));
            }
            sbuffer.append(tagObjTmp.getDisplayValue(rrequest,ownerComponentObj));
            endPrev=tagObjTmp.getEndposition();
        }
        if(end<content.length()-1) sbuffer.append(content.substring(end));
        return Tools.replaceAll(sbuffer.toString(),Consts_Private.SKIN_PLACEHOLDER,rrequest.getPageskin());
    }
}

