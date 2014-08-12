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
package com.wabacus.config.component.application.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.WabacusAssistant;

public class DisplayBean extends AbsConfigBean
{
    private Boolean colselect=null;
    
    private int colselectwidth;
    
    private int colselectmaxheight=350;
    
    private String dataheader;

    private String labelstyleproperty;//对于列表报表，显示标题行<tr/>的样式字符串，对于块数据自动列表报表，显示每个块的<div/>的样式字符串
    
    private List<String> lstDynLabelstylepropertyParts;
    
    private String valuestyleproperty;//显示数据行<tr/>的样式字符串
    
    private List<String> lstDynValuestylepropertyParts;
    
    private List<ColBean> lstCols=new ArrayList<ColBean>();
    private Map<String,ColBean> mPropsAndColBeans;
    
    private Map<String,ColBean> mColumnsAndColBeans;
    
    private Map<String,ColBean> mColIdsAndColBeans;
    
    private int generate_childid=0;//此属性纯粹用于加载<group/>和<col/>时，产生各个<group/>和<col/>对应ColBean和GroupBean的id属性值。
        
    public DisplayBean(AbsConfigBean parent)
    {
        super(parent);
        generate_childid=0;
    }

    public String getDataheader()
    {
        return dataheader;
    }

    public void setDataheader(String dataheader)
    {
        this.dataheader=dataheader;
    }

    public Boolean getColselect()
    {
        return colselect;
    }

    public boolean isColselect()
    {
        if(colselect==null) return false;
        return colselect.booleanValue();
    }
    
    public void setColselect(Boolean colselect)
    {
        this.colselect=colselect;
    }

    public int getColselectwidth()
    {
        return colselectwidth;
    }

    public void setColselectwidth(int colselectwidth)
    {
        this.colselectwidth=colselectwidth;
    }

    public int getColselectmaxheight()
    {
        return colselectmaxheight;
    }

    public void setColselectmaxheight(int colselectmaxheight)
    {
        this.colselectmaxheight=colselectmaxheight;
    }

    public void clearChildrenInfo()
    {
        if(lstCols!=null) lstCols.clear(); 
        generate_childid=0;
    }
    
    public int generate_childid()
    {
        return generate_childid++;
    }
    
    public List<ColBean> getLstCols()
    {
        return lstCols;
    }

    public void setLstCols(List<ColBean> lstCols)
    {
        this.lstCols=lstCols;
    }

    public String getLabelstyleproperty(ReportRequest rrequest,boolean isStaticPart)
    {
        if(isStaticPart) return this.labelstyleproperty;
        return WabacusAssistant.getInstance().getStylepropertyWithDynPart(rrequest,this.labelstyleproperty,this.lstDynLabelstylepropertyParts,"");
    }

    public void setLabelstyleproperty(String labelstyleproperty,boolean isStaticPart)
    {
        if(isStaticPart)
        {
            this.labelstyleproperty=labelstyleproperty;
        }else
        {
            Object[] objArr=WabacusAssistant.getInstance().parseStylepropertyWithDynPart(labelstyleproperty);
            this.labelstyleproperty=(String)objArr[0];
            this.lstDynLabelstylepropertyParts=(List<String>)objArr[1];
        }
    }

    public String getValuestyleproperty(ReportRequest rrequest,boolean isStaticPart)
    {
        if(isStaticPart) return this.valuestyleproperty;
        return WabacusAssistant.getInstance().getStylepropertyWithDynPart(rrequest,this.valuestyleproperty,this.lstDynValuestylepropertyParts,"");
    }

    public void setValuestyleproperty(String valuestyleproperty,boolean isStaticPart)
    {
        if(isStaticPart)
        {
            this.valuestyleproperty=valuestyleproperty;
        }else
        {
            Object[] objArr=WabacusAssistant.getInstance().parseStylepropertyWithDynPart(valuestyleproperty);
            this.valuestyleproperty=(String)objArr[0];
            this.lstDynValuestylepropertyParts=(List<String>)objArr[1];
        }
    }

    public ColBean getColBeanByColProperty(String property)
    {
        if(property==null||property.trim().equals("")||lstCols==null) return null;
        property=property.trim();
        if(mPropsAndColBeans==null||mPropsAndColBeans.get(property)==null)
        {
            Map<String,ColBean> mPropsAndColBeansTmp=new HashMap<String,ColBean>();
            for(ColBean cbTmp:lstCols)
            {
                if(cbTmp.getProperty()==null) continue;
                mPropsAndColBeansTmp.put(cbTmp.getProperty(),cbTmp);
            }
            mPropsAndColBeans=mPropsAndColBeansTmp;
        }
        return mPropsAndColBeans.get(property);
    }

    public ColBean getColBeanByColColumn(String column)
    {
        if(column==null||column.trim().equals("")||lstCols==null) return null;
        column=column.trim();
        if(mColumnsAndColBeans==null||mColumnsAndColBeans.get(column)==null)
        {
            Map<String,ColBean> mColumnsAndColBeansTmp=new HashMap<String,ColBean>();
            for(ColBean cbTmp:this.lstCols)
            {
                if(cbTmp.getColumn()==null) continue;
                mColumnsAndColBeansTmp.put(cbTmp.getColumn(),cbTmp);
            }
            mColumnsAndColBeans=mColumnsAndColBeansTmp;
        }
        return mColumnsAndColBeans.get(column);
    }
    
    public ColBean getColBeanByColId(String colid)
    {
        if(colid==null||colid.trim().equals("")||lstCols==null) return null;
        colid=colid.trim();
        if(mColIdsAndColBeans==null||mColIdsAndColBeans.size()!=this.lstCols.size())
        {
            Map<String,ColBean> mColidsAndColBeansTmp=new HashMap<String,ColBean>();
            for(ColBean cbTmp:this.lstCols)
            {
                mColidsAndColBeansTmp.put(cbTmp.getColid(),cbTmp);
            }
            mColIdsAndColBeans=mColidsAndColBeansTmp;
        }
        return mColIdsAndColBeans.get(colid);
    }
    
    public AbsConfigBean clone(AbsConfigBean parent)
    {
        DisplayBean dbeanNew=(DisplayBean)super.clone(parent);
        ((ReportBean)parent).setDbean(dbeanNew);
        if(lstCols!=null)
        {
            
            List<ColBean> lstColsNew=new ArrayList<ColBean>();
            for(int i=0;i<lstCols.size();i++)
            {
                lstColsNew.add((ColBean)lstCols.get(i).clone(dbeanNew));
            }
            dbeanNew.setLstCols(lstColsNew);
        }
        dbeanNew.mPropsAndColBeans=null;
        dbeanNew.mColIdsAndColBeans=null;
        dbeanNew.mColumnsAndColBeans=null;
        cloneExtendConfig(dbeanNew);
        return dbeanNew;
    }
}
