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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportColBean;
import com.wabacus.system.datatype.IDataType;
import com.wabacus.util.Consts;
import com.wabacus.util.Consts_Private;
import com.wabacus.util.Tools;

public class ColBean extends AbsConfigBean
{
    private String colid;
    
    private String property;

    private String column;

    private String datasetValueId;
    
    private String label;
    
    private Map<String,String> mDynLableParts;

    private String displaytype=Consts.COL_DISPLAYTYPE_INITIAL;
    
    private String tagcontent;
    
    private boolean isI18n;

    private IDataType datatypeObj;
 
    private String labelstyleproperty;
    
    private List<String> lstDynLabelstylepropertyParts;
    
    private String valuestyleproperty;
    
    private List<String> lstDynValuestylepropertyParts;
    
    private String labelalign;//配置的列标题对齐方式，不用配置，而是从labelstyleproperty中解析出来，以便数据导出或打印时使用
    
    private String valuealign;
    
    private float plainexcelwidth;
    
    private float pdfwidth;
    
    private String printwidth;
    
    private String printlabelstyleproperty;
    
    private List<String> lstDynPrintlabelstylepropertyParts;
    
    private String printvaluestyleproperty;
    
    private List<String> lstDynPrintvaluestylepropertyParts;
    
    private Method setMethod=null;

    private Method getMethod=null;

    public final static String NON_LABEL="{non-label}";





//    private Map<String,String> mFormatParamsColProperties;//存放当前列的所有格式化方法参数中用到的其它<col/>的定义property(即@{}格式)和真正property
    
    public ColBean(AbsConfigBean parent)
    {
        super(parent);
        this.colid=String.valueOf(((DisplayBean)parent).generate_childid());
    }

    public ColBean(AbsConfigBean parent,int colid)
    {
        super(parent);
        this.colid=String.valueOf(colid);
    }
    
    public String getColid()
    {
        return colid;
    }

    public void setColid(String colid)
    {
        this.colid=colid;
    }

    public String getDatasetValueId()
    {
        return datasetValueId;
    }

    public void setDatasetValueId(String datasetid)
    {
        this.datasetValueId=datasetid;
    }

    public float getPlainexcelwidth()
    {
        return plainexcelwidth;
    }

    public void setPlainexcelwidth(float plainexcelwidth)
    {
        this.plainexcelwidth=plainexcelwidth;
    }

    public float getPdfwidth()
    {
        return pdfwidth;
    }

    public void setPdfwidth(float pdfwidth)
    {
        this.pdfwidth=pdfwidth;
    }

    public Method getGetMethod()
    {
        return getMethod;
    }

    public void setGetMethod(Method getMethod)
    {
        this.getMethod=getMethod;
    }

    public Method getSetMethod()
    {
        return setMethod;
    }

    public void setSetMethod(Method setMethod)
    {
        this.setMethod=setMethod;
    }

    public boolean isI18n()
    {
        return isI18n;
    }

    public void setI18n(boolean isI18n)
    {
        this.isI18n=isI18n;
    }

    public String getPrintwidth()
    {
        return printwidth;
    }

    public void setPrintwidth(String printwidth)
    {
        this.printwidth=printwidth;
    }

    public String getPrintlabelstyleproperty(ReportRequest rrequest,boolean isStaticPart)
    {
        if(isStaticPart) return this.printlabelstyleproperty==null?"":this.printlabelstyleproperty;
        return WabacusAssistant.getInstance().getStylepropertyWithDynPart(rrequest,this.printlabelstyleproperty,this.lstDynPrintlabelstylepropertyParts,"");
    }
    
    public void setPrintlabelstyleproperty(String printlabelstyleproperty,boolean isStaticPart)
    {
        if(isStaticPart)
        {
            this.printlabelstyleproperty=printlabelstyleproperty;
        }else
        {
            Object[] objArr=WabacusAssistant.getInstance().parseStylepropertyWithDynPart(printlabelstyleproperty);
            this.printlabelstyleproperty=(String)objArr[0];
            this.lstDynPrintlabelstylepropertyParts=(List<String>)objArr[1];
        }
    }

    public String getPrintvaluestyleproperty(ReportRequest rrequest,boolean isStaticPart)
    {
        if(isStaticPart) return this.printvaluestyleproperty==null?"":this.printvaluestyleproperty;
        return WabacusAssistant.getInstance().getStylepropertyWithDynPart(rrequest,this.printvaluestyleproperty,this.lstDynPrintvaluestylepropertyParts,"");
    }
    
    public void setPrintvaluestyleproperty(String printvaluestyleproperty,boolean isStaticPart)
    {
        if(isStaticPart)
        {
            this.printvaluestyleproperty=printvaluestyleproperty;
        }else
        {
            Object[] objArr=WabacusAssistant.getInstance().parseStylepropertyWithDynPart(printvaluestyleproperty);
            this.printvaluestyleproperty=(String)objArr[0];
            this.lstDynValuestylepropertyParts=(List<String>)objArr[1];
        }
    }

    public void setProperty(String property)
    {
        this.property=property;
    }

    public void setColumn(String column)
    {
        if(Tools.isDefineKey("i18n",column))
        {
            String columnTemp=Tools.getRealKeyByDefine("i18n",column);
            if(columnTemp.trim().equals(""))
            {
                throw new WabacusConfigLoadingException("报表"+this.getReportBean().getPath()+"配置的列"
                        +column+"不合法");
            }
            setI18n(true);
            this.column=columnTemp;
        }else
        {
            setI18n(false);
            this.column=column;
        }
    }

    public void setLabel(String label)
    {
        Object[] objArr=WabacusAssistant.getInstance().parseStringWithDynPart(label);
        this.label=(String)objArr[0];
        this.mDynLableParts=(Map<String,String>)objArr[1];
    }

    public String getDisplaytype()
    {
        return displaytype;
    }

    public void setDisplaytype(String displaytype)
    {
        displaytype=displaytype==null?"":displaytype.toLowerCase().trim();
        if(displaytype.equals(""))
        {
            this.displaytype=Consts.COL_DISPLAYTYPE_INITIAL;
        }else
        {
            if(!displaytype.equals(Consts.COL_DISPLAYTYPE_ALWAYS)&&!displaytype.equals(Consts.COL_DISPLAYTYPE_INITIAL)
                    &&!displaytype.equals(Consts.COL_DISPLAYTYPE_HIDDEN)&&!displaytype.equals(Consts.COL_DISPLAYTYPE_OPTIONAL))
            {
                throw new WabacusConfigLoadingException("加载报表"+this.getReportBean().getPath()+"的列"+this.column+"失败，配置的displaytype属性"+displaytype
                        +"不支持");
            }
            this.displaytype=displaytype;
        }
    }

    public String getProperty()
    {
        return this.property;
    }

    public String getColumn()
    {
        return this.column;
    }

    public String getLabel(ReportRequest rrequest)
    {
        return WabacusAssistant.getInstance().getStringValueWithDynPart(rrequest,this.label,this.mDynLableParts,"");
    }

    public IDataType getDatatypeObj()
    {
        return datatypeObj;
    }

    public void setDatatypeObj(IDataType datatypeObj)
    {
        this.datatypeObj=datatypeObj;
    }
    
    public String getLabelstyleproperty(ReportRequest rrequest,boolean isStaticPart)
    {
        if(rrequest!=null&&rrequest.getShowtype()==Consts.DISPLAY_ON_PRINT) return this.getPrintlabelstyleproperty(rrequest,isStaticPart);
        if(isStaticPart) return this.labelstyleproperty==null?"":this.labelstyleproperty;
        String reallabelstyleproperty=WabacusAssistant.getInstance().getStylepropertyWithDynPart(rrequest,this.labelstyleproperty,
                this.lstDynLabelstylepropertyParts,"");
        if(rrequest!=null&&rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE&&this.getReportBean().isListReportType())
        {
            String stylevalue=Tools.getPropertyValueByName("style",reallabelstyleproperty,false);
            if(stylevalue==null) stylevalue="";
            if(!stylevalue.trim().equals("")&&!stylevalue.endsWith(";")) stylevalue=stylevalue+";";
            if(stylevalue.toLowerCase().indexOf("text-align")<0)
            {
                stylevalue=stylevalue+"text-align:center;";
            }
            if(stylevalue.toLowerCase().indexOf("vertical-align")<0)
            {
                stylevalue=stylevalue+"vertical-align:middle;";
            }
            reallabelstyleproperty=Tools.removePropertyValueByName("style",reallabelstyleproperty);
            reallabelstyleproperty=reallabelstyleproperty+" style=\""+stylevalue+"\"";
        }
        return reallabelstyleproperty;
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
        if(rrequest!=null&&rrequest.getShowtype()==Consts.DISPLAY_ON_PRINT) return this.getPrintvaluestyleproperty(rrequest,isStaticPart);
        if(isStaticPart) return this.valuestyleproperty==null?"":this.valuestyleproperty;
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

    public String getTagcontent()
    {
        return tagcontent;
    }

    public void setTagcontent(String tagcontent)
    {
        this.tagcontent=tagcontent;
    }

    public String getLabelalign()
    {
        return labelalign;
    }

    public void setLabelalign(String labelalign)
    {
        this.labelalign=labelalign;
    }

    public String getValuealign()
    {
        return valuealign;
    }

    public void setValuealign(String valuealign)
    {
        this.valuealign=valuealign;
    }

    public boolean isMatchDataSet(ReportDataSetValueBean dsvbean)
    {
        if(this.isControlCol()||this.isSequenceCol()||this.isNonFromDbCol()||this.isNonValueCol()) return false;
        if((this.datasetValueId==null||this.datasetValueId.trim().equals(""))&&!dsvbean.getReportBean().getSbean().isMultiDataSetCols()) return true;//没有配置datasetid属性的，且本报表没有提供横向多数据集
        return dsvbean.getId().equals(this.datasetValueId);
    }
    
    public boolean checkDisplayPermission(ReportRequest rrequest)
    {
        if(!rrequest.checkPermission(this.getReportBean().getId(),Consts.DATA_PART,this.column,Consts.PERMISSION_TYPE_DISPLAY)) return false;
        if(this.property!=null&&!this.property.trim().equals("")&&!this.property.equals(this.column))
        {
            if(!rrequest.checkPermission(this.getReportBean().getId(),Consts.DATA_PART,this.property,Consts.PERMISSION_TYPE_DISPLAY)) return false;
        }
        return true;
    }
    
    public boolean checkReadonlyPermission(ReportRequest rrequest)
    {
        if(rrequest.checkPermission(this.getReportBean().getId(),Consts.DATA_PART,this.column,Consts.PERMISSION_TYPE_READONLY)) return true;
        if(this.property!=null&&!this.property.trim().equals("")&&!this.property.equals(this.column))
        {
            if(rrequest.checkPermission(this.getReportBean().getId(),Consts.DATA_PART,this.property,Consts.PERMISSION_TYPE_READONLY)) return true;
        }
        return false;
    }
    
    public int getDisplaymode(ReportRequest rrequest,List<String> lstDisplayColIds)
    {
        if(rrequest!=null)
        {
            if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE&&this.isControlCol()) return 0;
            if(!checkDisplayPermission(rrequest)) return -1;
        }
        if(Consts.COL_DISPLAYTYPE_HIDDEN.equals(displaytype)) return 0;
        if(Consts.COL_DISPLAYTYPE_ALWAYS.equals(displaytype)) return 2;
        DisplayBean dbean=(DisplayBean)this.getParent();
        if(!dbean.isColselect()) return 1;
        if(lstDisplayColIds==null||lstDisplayColIds.size()==0)
        {
            if(Consts.COL_DISPLAYTYPE_INITIAL.equals(displaytype)) return 1;
        }else if(lstDisplayColIds.contains(colid))
        {
            return 1;
        }
        return 0;
    }
    
    public boolean isNonValueCol()
    {
        if(column==null||column.trim().equals("")) return false;
        return column.equalsIgnoreCase(Consts_Private.NON_VALUE);
    }
    
    public boolean isSequenceCol()
    {
        if(column==null||column.trim().equals("")) return false;
        return column.indexOf("{sequence")==0&&column.indexOf("}")==column.length()-1;
    }
    
    public boolean isNonFromDbCol()
    {
        if(column==null||column.trim().equals("")) return false;
        return column.equalsIgnoreCase(Consts_Private.NON_FROMDB);
    }
    
    public boolean isRowSelectCol()
    {
        if(column==null||column.trim().equals("")) return false;
        return column.equalsIgnoreCase(Consts_Private.COL_ROWSELECT);
    }
    
    public boolean isRoworderArrowCol()
    {
        if(column==null||column.trim().equals("")) return false;
        return column.equalsIgnoreCase(Consts_Private.COL_ROWORDER_ARROW);
    }
    
    public boolean isRoworderInputboxCol()
    {
        if(column==null||column.trim().equals("")) return false;
        return column.equalsIgnoreCase(Consts_Private.COL_ROWORDER_INPUTBOX);
    }
    
    public boolean isRoworderTopCol()
    {
        if(column==null||column.trim().equals("")) return false;
        return column.equalsIgnoreCase(Consts_Private.COL_ROWORDER_TOP);
    }
    
    public boolean isRoworderCol(String rowordertypeColumn)
    {
        if(column==null||column.trim().equals("")) return false;
        return column.equalsIgnoreCase(rowordertypeColumn);
    }
    
    public boolean isRoworderCol()
    {
        return isRoworderArrowCol()||isRoworderInputboxCol()||isRoworderTopCol();
    }
    
    public boolean isEditableListEditCol()
    {
        if(this.column==null||this.column.trim().equals("")) return false;
        return this.column.equalsIgnoreCase(Consts_Private.COL_EDITABLELIST_EDIT);
    }
    
    public boolean isControlCol()
    {
        if(isRowSelectCol()||isRoworderCol()||isEditableListEditCol())
        {
            return true;
        }
        return false;
    }
    
    public String getBorderStylePropertyOnColBean()
    {
        ReportBean rb=this.getReportBean();
        String border=rb.getBorder();
        String borderstyle="";
        if(Consts_Private.REPORT_BORDER_NONE0.equals(border)||Consts_Private.REPORT_BORDER_NONE1.equals(border))
        {
            borderstyle="border:none;";
        }else
        {
            String bordercolor=rb.getBordercolor();
            if(bordercolor!=null&&!bordercolor.trim().equals(""))
            {
                borderstyle="border-color:"+bordercolor+";";
            }
           if(Consts_Private.REPORT_BORDER_HORIZONTAL0.equals(border)||Consts_Private.REPORT_BORDER_HORIZONTAL1.equals(border))
           {
               borderstyle=borderstyle+"border-left:none;border-right:none;";
           }else if(Consts_Private.REPORT_BORDER_VERTICAL.equals(border))
           {
               borderstyle=borderstyle+"border-top:none;border-bottom:none;";
           }
        }
        return borderstyle;
    }
    
    public ColBean getUpdateColBeanDest(boolean isMust)
    {
        EditableReportColBean ercbean=(EditableReportColBean)this.getExtendConfigDataForReportType(EditableReportColBean.class);
        if(ercbean==null||ercbean.getUpdatecolDest()==null||ercbean.getUpdatecolDest().trim().equals(""))
        {
            if(!isMust) return null;
            throw new WabacusConfigLoadingException("报表"+this.getReportBean().getPath()+"的column属性为"+this.getColumn()+"的<col/>没有配置updatecol更新其它列");
        }
        ColBean cbTemp=this.getReportBean().getDbean().getColBeanByColProperty(ercbean.getUpdatecolDest());
        if(cbTemp==null)
        {
            throw new WabacusConfigLoadingException("报表"+this.getReportBean().getPath()+"的column属性为"+this.getColumn()+"的<col/>通过updatecol为"
                    +ercbean.getUpdatecolDest()+"引用的列不存在");
        }
        if(!Consts.COL_DISPLAYTYPE_HIDDEN.equals(cbTemp.getDisplaytype()))
        {
            throw new WabacusConfigLoadingException("报表"+this.getReportBean().getPath()+"的column属性为"+this.getColumn()+"的<col/>通过updatecol为"
                    +ercbean.getUpdatecolDest()+"引用的列不是displaytype为hidden的列");
        }
        if(cbTemp.getProperty()==null||cbTemp.getProperty().trim().equals("")||cbTemp.isNonValueCol()||cbTemp.isSequenceCol()||cbTemp.isControlCol())
        {
            throw new WabacusConfigLoadingException("报表"+this.getReportBean().getPath()+"的column属性为"+this.getColumn()+"的<col/>通过updatecol为"
                    +ercbean.getUpdatecolDest()+"引用的列不是从数据库中获取数据，不能被引用");
        }
        return cbTemp;
    }
    
    public ColBean getUpdateColBeanSrc(boolean isMust)
    {
        EditableReportColBean ercbean=(EditableReportColBean)this.getExtendConfigDataForReportType(EditableReportColBean.class);
        if(ercbean==null||ercbean.getUpdatecolSrc()==null||ercbean.getUpdatecolSrc().trim().equals(""))
        {
            if(!isMust) return null;
            throw new WabacusConfigLoadingException("报表"+this.getReportBean().getPath()+"的column属性为"+this.getColumn()+"的<col/>没有被其它列通过updatecol属性引用");
        }
        ColBean cbTemp=this.getReportBean().getDbean().getColBeanByColProperty(ercbean.getUpdatecolSrc());
        if(cbTemp==null)
        {
            throw new WabacusConfigLoadingException("在报表"+this.getReportBean().getPath()+"中没有取到property为"+ercbean.getUpdatecolSrc()+"的列");
        }
        return cbTemp;
    }
    
    public AbsConfigBean clone(AbsConfigBean parent)
    {
        ColBean cbNew=(ColBean)super.clone(parent);
        cloneExtendConfig(cbNew);
        return cbNew;
    }

    public void doPostLoad()
    {
        /*if(formatproperty!=null&&!formatproperty.trim().equals(""))
        {
            this.formatProperties=FormatPropertyBean.convertFormatStringToFormatBean(
                    formatproperty,this);
            this.formatproperty=null;
            mFormatParamsColProperties=new HashMap<String,String>();
            if(this.formatProperties!=null&&this.formatProperties.size()>0)
            {
                for(int i=0;i<this.formatProperties.size();i++)
                {
                    FormatPropertyBean fpropbean=this.formatProperties.get(i);
                    if(fpropbean==null) continue;
                    fpropbean.doPostLoad(mFormatParamsColProperties);
                }
            }
            if(mFormatParamsColProperties.size()==0) mFormatParamsColProperties=null;
        }*/
        if(this.isControlCol()||this.isSequenceCol()||this.isNonValueCol()) return;
        if(!this.isNonFromDbCol()&&this.getReportBean().getSbean().isMultiDataSetCols()&&(this.datasetValueId==null||this.datasetValueId.trim().equals("")))
        {
            throw new WabacusConfigLoadingException("加载报表"+this.getReportBean().getPath()+"上的列"+this.column
                    +"失败，此报表配置了多个横向数据集查询各列数据，因此必须在column中指定数据集ID");
        }
        EditableReportColBean ecolbean=(EditableReportColBean)this.getExtendConfigDataForReportType(EditableReportColBean.class);
        if(ecolbean!=null) ecolbean.doPostLoad();
    }
    
    public int hashCode()
    {
        final int prime=31;
        int result=1;
        result=prime*result+((colid==null)?0:colid.hashCode());
        result=prime*result+((column==null)?0:column.hashCode());
        result=prime*result+((property==null)?0:property.hashCode());
        return result;
    }

    public boolean equals(Object obj)
    {
        if(this==obj) return true;
        if(obj==null) return false;
        if(getClass()!=obj.getClass()) return false;
        final ColBean other=(ColBean)obj;
        if(colid==null)
        {
            if(other.colid!=null) return false;
        }else if(!colid.equals(other.colid)) return false;
        if(column==null)
        {
            if(other.column!=null) return false;
        }else if(!column.equals(other.column)) return false;
        if(property==null)
        {
            if(other.property!=null) return false;
        }else if(!property.equals(other.property)) return false;
        if(this.getReportBean()==null)
        {
            if(other.getReportBean()!=null) return false;
        }else if(!this.getReportBean().equals(other.getReportBean()))
        {
            return false;
        }
        return true;
    }
}
