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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.component.application.report.abstractreport.IEditableReportType;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportColBean;
import com.wabacus.system.inputbox.option.SelectboxOptionBean;
import com.wabacus.util.Tools;

public abstract class AbsSelectBox extends AbsInputBox implements Cloneable
{
    protected Map<String,Boolean> mParentIds;

    protected Map<String,AbsInputBox> mParentInputboxes;
    
    protected boolean isRegex=false;

    protected boolean isMultiply;
    
    protected String separator;
    
    protected boolean isBelongtoUpdatecolSrcCol;
    
    protected List<SelectboxOptionBean> lstOptions=null;
    
    public AbsSelectBox(String typename)
    {
        super(typename);
    }

    public boolean isRegex()
    {
        return isRegex;
    }

    public void setRegex(boolean isRegex)
    {
        this.isRegex=isRegex;
    }

    public void setLstOptions(List<SelectboxOptionBean> lstOptions)
    {
        this.lstOptions=lstOptions;
    }

    public boolean isDependsOtherInputbox()
    {
        return this.mParentIds!=null&&this.mParentIds.size()>0;
    }
    
    public Map<String,Boolean> getMParentIds()
    {
        return mParentIds;
    }

    protected abstract boolean isMultipleSelect();
    
    protected String initDisplaySpanStart(ReportRequest rrequest)
    {
        if(!this.isMultipleSelect()) return super.initDisplaySpanStart(rrequest);
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(super.initDisplaySpanStart(rrequest));
        resultBuf.append(" separator=\"").append(this.separator).append("\"");
        return resultBuf.toString();
    }
    
    protected String initDisplaySpanContent(ReportRequest rrequest)
    {
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(super.initDisplaySpanContent(rrequest));
        if(!this.isDependsOtherInputbox())
        {
            List<Map<String,String>> lstOptionsResult=getLstOptionsFromCache(rrequest,this.owner.getInputBoxId());
            if(lstOptionsResult!=null&&lstOptionsResult.size()>0)
            {
                String name_temp, value_temp;
                for(Map<String,String> mItems:lstOptionsResult)
                {
                    name_temp=mItems.get("label");
                    value_temp=mItems.get("value");
                    value_temp=value_temp==null?"":value_temp.trim();
                    resultBuf.append("<span value=\""+value_temp+"\" label=\""+name_temp+"\"></span>");
                }
            }
        }
        return resultBuf.toString();
    }
    
    public String getDefaultlabel(ReportRequest rrequest)
    {
        if(this.defaultvalue==null) return null;
        List<Map<String,String>> lstOptionsResult=null;
        ReportBean rbean=owner.getReportBean();
        if(!this.isDependsOtherInputbox())
        {
            lstOptionsResult=getLstOptionsFromCache(rrequest,this.owner.getInputBoxId());
        }else
        {
            lstOptionsResult=(List<Map<String,String>>)rrequest.getAttribute("LISTOPTIONS_"+rbean.getId()+owner.getInputBoxId()+"_[ALL]");
            if(lstOptionsResult==null)
            {
                Map<String,String> mParentValues=new HashMap<String,String>();
                for(String parentidTmp:this.mParentIds.keySet())
                {
                    mParentValues.put(parentidTmp,"[%ALL%]");
                }
                lstOptionsResult=getOptionsList(rrequest,mParentValues);
                if(lstOptionsResult!=null)
                {
                    rrequest.setAttribute("LISTOPTIONS_"+rbean.getId()+owner.getInputBoxId()+"_[ALL]",lstOptionsResult);
                }
            }
        }
        if(lstOptionsResult==null||lstOptionsResult.size()==0) return null;
        StringBuffer labelBuf=new StringBuffer();
        String selectedvalue=this.getDefaultvalue(rrequest);
        for(Map<String,String> mItems:lstOptionsResult)
        {
            if(mItems.get("value")!=null&&isSelectedValueOfSelectBox(selectedvalue,mItems.get("value")))
            {
                if(!this.isMultipleSelect()) return mItems.get("label");
                labelBuf.append(mItems.get("label")).append(separator);
            }
        }
        if(this.isMultipleSelect()&&labelBuf.toString().endsWith(separator)) labelBuf.delete(0,labelBuf.length()-separator.length());
        return labelBuf.toString();
    }
    
    protected List<Map<String,String>> getLstOptionsFromCache(ReportRequest rrequest,String realinputboxid)
    {
        ReportBean rbean=this.owner.getReportBean();
        List<Map<String,String>> lstOptionsResult=null;
        if(!this.isDependsOtherInputbox())
        {
            lstOptionsResult=(List<Map<String,String>>)rrequest.getAttribute("LISTOPTIONS_"+rbean.getId()+owner.getInputBoxId());
        }else
        {
            lstOptionsResult=(List<Map<String,String>>)rrequest.getAttribute("LISTOPTIONS_"+rbean.getId()+realinputboxid);
        }
        if(lstOptionsResult==null)
        {//缓存中没有取到
            if(!this.isDependsOtherInputbox())
            {
                lstOptionsResult=getOptionsList(rrequest,null);
                if(lstOptionsResult!=null) rrequest.setAttribute("LISTOPTIONS_"+rbean.getId()+owner.getInputBoxId(),lstOptionsResult);
            }else
            {
                lstOptionsResult=getOptionsList(rrequest,getAllParentValues(rrequest,realinputboxid));
                if(this.getLstChildids()!=null&&this.getLstChildids().size()>0)
                {
                    rrequest.setAttribute("LISTOPTIONS_"+rbean.getId()+realinputboxid,lstOptionsResult);
                }
            }
        }
        return lstOptionsResult;
    }
    
    public List<Map<String,String>> getOptionsList(ReportRequest rrequest,Map<String,String> mParentValues)
    {
        List<Map<String,String>> lstResults=new ArrayList<Map<String,String>>();
        List<Map<String,String>> lstOptionsTmp;
        for(SelectboxOptionBean obean:lstOptions)
        {
            lstOptionsTmp=obean.getLstRuntimeOptions(rrequest,mParentValues);
            if(lstOptionsTmp!=null) lstResults.addAll(lstOptionsTmp);
        }
        Map<String,String> mOptionTmp;
        if(lstResults.size()==0)
        {
            for(SelectboxOptionBean obean:lstOptions)
            {
                if(obean.getOptionDatasourceObj()!=null||obean.getType()==null) continue;
                if(obean.getType().length==1&&obean.getType()[0].equals("%false-false%"))
                {
                    mOptionTmp=new HashMap<String,String>();
                    mOptionTmp.put("label",rrequest.getI18NStringValue(obean.getLabel()));
                    mOptionTmp.put("value",obean.getValue());
                    lstResults.add(mOptionTmp);
                }
            }
        }else
        {
            for(SelectboxOptionBean obean:lstOptions)
            {
                if(obean.getOptionDatasourceObj()!=null||obean.getType()==null) continue;
                if(obean.getType().length==1&&obean.getType()[0].equals("%true-true%"))
                {
                    mOptionTmp=new HashMap<String,String>();
                    mOptionTmp.put("label",rrequest.getI18NStringValue(obean.getLabel()));
                    mOptionTmp.put("value",obean.getValue());
                    lstResults.add(0,mOptionTmp);
                }
            }
        }
        ReportBean rbean=this.owner.getReportBean();
        if(rbean.getInterceptor()!=null)
        {
            lstResults=(List<Map<String,String>>)rbean.getInterceptor().afterLoadData(rrequest,rbean,this,lstResults);
        }
        return lstResults;
    }

    public Map<String,String> getAllParentValues(ReportRequest rrequest,String realinputboxid)
    {
        if(!this.isDependsOtherInputbox()) return null;
        initAllParentInputboxs();
        int rowidx=-1;
        String realinputboxidSuffix="";
        int idx=realinputboxid.lastIndexOf("__");
        if(idx>0)
        {
            try
            {
                rowidx=Integer.parseInt(realinputboxid.substring(idx+2).trim());
                realinputboxidSuffix=realinputboxid.substring(idx);
            }catch(NumberFormatException e)
            {
                rowidx=-1;
            }
        }
        if(rowidx<0) rowidx=0;//说明当前列不是在editablelist2/listform报表类型中，则取第一条记录的此列值
        boolean isConditionBox=this.owner instanceof ConditionBean;
        Map<String,String> mResults=new HashMap<String,String>();
        AbsInputBox parentBoxTmp;
        String parentValue;
        ColBean colbeanTmp=null;
        for(String parentidTmp:this.mParentIds.keySet())
        {
            if(isConditionBox)
            {
                ConditionBean cbTmp=this.getOwner().getReportBean().getSbean().getConditionBeanByName(parentidTmp);
                parentValue=cbTmp.getConditionValue(rrequest,-1);
            }else
            {
                colbeanTmp=this.getOwner().getReportBean().getDbean().getColBeanByColProperty(parentidTmp);
                AbsReportType reportTypeObj=rrequest.getDisplayReportTypeObj(this.owner.getReportBean().getId());
                if(reportTypeObj.getLstReportData()==null||reportTypeObj.getLstReportData().size()==0
                        ||rowidx>=reportTypeObj.getLstReportData().size())
                {
                    parentValue="";
                }else
                {
                    if(reportTypeObj instanceof IEditableReportType)
                    {
                        parentValue=((IEditableReportType)reportTypeObj).getColOriginalValue(reportTypeObj.getLstReportData().get(rowidx),colbeanTmp);
                    }else
                    {
                        parentValue=reportTypeObj.getLstReportData().get(rowidx).getColStringValue(colbeanTmp);
                    }
                    
                }
            }
            if((parentValue==null||parentValue.trim().equals(""))&&this.mParentInputboxes.get(parentidTmp) instanceof SelectBox)
            {
                parentBoxTmp=this.mParentInputboxes.get(parentidTmp);
                String parentRealInputboxId=parentBoxTmp.getOwner().getInputBoxId();
                parentRealInputboxId=parentRealInputboxId+realinputboxidSuffix;
                List<Map<String,String>> lstParentOptions=((SelectBox)parentBoxTmp).getLstOptionsFromCache(rrequest,parentRealInputboxId);
                if(lstParentOptions!=null&&lstParentOptions.size()>0&&!hasBlankValueOption(lstParentOptions))
                {
                    if(!isConditionBox&&(colbeanTmp.getUpdateColBeanDest(false)!=null))
                    {
                        parentValue=lstParentOptions.get(0).get("label");
                    }else
                    {
                        parentValue=lstParentOptions.get(0).get("value");
                    }
                }
            }
            mResults.put(parentidTmp,parentValue);
        }
        return mResults;
    }
    
    private void initAllParentInputboxs()
    {
        if(!this.isDependsOtherInputbox()) return;
        if(this.mParentInputboxes!=null&&this.mParentInputboxes.size()==0) return;
        this.mParentInputboxes=new HashMap<String,AbsInputBox>();
        if(this.owner instanceof ConditionBean)
        {
            ConditionBean cbTmp;
            for(String connameTmp:this.mParentIds.keySet())
            {
                cbTmp=this.owner.getReportBean().getSbean().getConditionBeanByName(connameTmp);
                if(cbTmp==null||cbTmp.isHidden()||cbTmp.isConstant()||!cbTmp.isConditionValueFromUrl()||cbTmp.getInputbox()==null) continue;//被依赖的此条件没有输入框，即是一个隐藏条件
                this.mParentInputboxes.put(connameTmp,cbTmp.getInputbox());
            }
        }else
        {
            ColBean cbTmp,cbSrcTmp;
            EditableReportColBean ercbTmp;
            for(String colpropertyTmp:this.mParentIds.keySet())
            {
                cbTmp=this.owner.getReportBean().getDbean().getColBeanByColProperty(colpropertyTmp);
                if(cbTmp==null) continue;
                cbSrcTmp=cbTmp.getUpdateColBeanSrc(false);
                if(cbSrcTmp==null) cbSrcTmp=cbTmp;
                ercbTmp=(EditableReportColBean)cbSrcTmp.getExtendConfigDataForReportType(EditableReportColBean.class);
                if(ercbTmp==null||ercbTmp.getInputbox()==null) continue;
                this.mParentInputboxes.put(colpropertyTmp,ercbTmp.getInputbox());
            }
        }
    }
    
    private boolean hasBlankValueOption(List<Map<String,String>> lstParentOptions)
    {
        if(lstParentOptions==null||lstParentOptions.size()==0) return false;
        for(Map<String,String> optionArrTmp:lstParentOptions)
        {
            if(optionArrTmp.get("value")==null||optionArrTmp.get("value").trim().equals("")) return true;
        }
        return false;
    }
    
    protected boolean isSelectedValueOfSelectBox(String selectedvalues,String optionvalue)
    {
        if(selectedvalues==null||optionvalue==null) return false;
        if(this.isMultipleSelect())
        {
            if(separator==null||separator.equals("")) separator=" ";
            selectedvalues=selectedvalues.trim();
            optionvalue=optionvalue.trim();
            while(selectedvalues.startsWith(separator))
            {
                selectedvalues=selectedvalues.substring(separator.length());
            }
            while(selectedvalues.endsWith(separator))
            {
                selectedvalues=selectedvalues.substring(0,selectedvalues.length()-separator.length());
            }
            if(selectedvalues.equals(optionvalue)) return true;
            String[] tmpArr=selectedvalues.split(separator);
            for(int i=0;i<tmpArr.length;i++)
            {
                if(optionvalue.equals(tmpArr[i].trim())) return true;
            }
            return false;
        }else
        {
            return selectedvalues.trim().equals(optionvalue);
        }
    }
    
    public void loadInputBoxConfig(IInputBoxOwnerBean ownerbean,XmlElementBean eleInputboxBean)
    {
        super.loadInputBoxConfig(ownerbean,eleInputboxBean);
        if(eleInputboxBean==null)
        {
            throw new WabacusConfigLoadingException("加载报表"+ownerbean.getReportBean().getPath()+"的选择框类型输入框失败，没有配置下拉选项");
        }
        List<SelectboxOptionBean> lstObs=new ArrayList<SelectboxOptionBean>();
        List<XmlElementBean> lstOptionElements=eleInputboxBean.getLstChildElementsByName("option");
        if(lstOptionElements!=null&&lstOptionElements.size()>0)
        {
            loadOptionInfo(lstObs,lstOptionElements);
        }
        if(lstObs==null||lstObs.size()==0)
        {
            throw new WabacusConfigLoadingException("加载报表"+ownerbean.getReportBean().getPath()+"配置的选择框类型的输入框失败，没有配置下拉选项");
        }
        this.setLstOptions(lstObs);
        String depends=eleInputboxBean.attributeValue("depends");
        if(depends!=null&&!depends.trim().equals(""))
        {
            String isregex=eleInputboxBean.attributeValue("isregrex");
            if(isregex!=null&&!isregex.trim().equals(""))
            {
                this.isRegex=Boolean.parseBoolean(isregex.trim());
            }
            List<String> lstParentidsTmp=Tools.parseStringToList(depends,";",false);
            this.mParentIds=new HashMap<String,Boolean>();
            String dependtypeTmp=null;
            for(String parentidTmp:lstParentidsTmp)
            {
                if(parentidTmp==null||parentidTmp.trim().equals("")) continue;
                dependtypeTmp=null;
                int idx=parentidTmp.indexOf("=");
                if(idx>0)
                {
                    dependtypeTmp=parentidTmp.substring(idx+1).trim();
                    parentidTmp=parentidTmp.substring(0,idx).trim();
                }
                this.mParentIds.put(parentidTmp,dependtypeTmp==null||!dependtypeTmp.trim().toLowerCase().equals("false"));
                ownerbean.getReportBean().addSelectBoxWithRelate(this);
            }
        }
    }
    
    private void loadOptionInfo(List<SelectboxOptionBean> lstOptions,List<XmlElementBean> lstOptionBeans)
    {
        if(lstOptionBeans==null) return;
        ReportBean rbean=this.getOwner().getReportBean();
        String labeltemp,valuetemp;
        for(XmlElementBean eleOptionBeanTmp:lstOptionBeans)
        {
            if(eleOptionBeanTmp==null) continue;
            SelectboxOptionBean ob=new SelectboxOptionBean(this);
            String source=eleOptionBeanTmp.attributeValue("source");
            labeltemp=eleOptionBeanTmp.attributeValue("label");
            valuetemp=eleOptionBeanTmp.attributeValue("value");
            labeltemp=labeltemp==null?"":labeltemp.trim();
            valuetemp=valuetemp==null?"":valuetemp.trim();
            ob.setLabel(Config.getInstance().getResourceString(null,rbean.getPageBean(),labeltemp,true));
            ob.setValue(valuetemp);
            source=source==null?"":source.trim();
            if(source.equals(""))
            {
                String type=eleOptionBeanTmp.attributeValue("type");
                if(type!=null)
                {
                    type=type.trim();
                    String[] typearray=null;
                    if(type.equalsIgnoreCase("true"))
                    {
                        typearray=new String[1];
                        typearray[0]="%true-true%";
                    }else if(type.equalsIgnoreCase("false"))
                    {
                        typearray=new String[1];
                        typearray[0]="%false-false%";
                    }else if(!type.equals(""))
                    {
                        if(!type.startsWith("[")&&!type.endsWith("]"))
                        {
                            throw new WabacusConfigLoadingException("报表"+rbean.getPath()+"配置的下拉框的type属性值没有用[]括住");
                        }
                        typearray=Tools.parseStringToArray(type,'[',']');
                        if(typearray==null||typearray.length==0)
                        {
                            throw new WabacusConfigLoadingException("报表"+rbean.getPath()+"配置的下拉框的下拉选项的type属性不合法");
                        }
                    }
                    ob.setType(typearray);
                }
            }else if(Tools.isDefineKey("$",source))
            {
                loadOptionInfo(lstOptions,(List<XmlElementBean>)Config.getInstance().getResourceObject(null,rbean.getPageBean(),source,true));
                continue;
            }else if(Tools.isDefineKey("@",source)||Tools.isDefineKey("class",source))
            {
                ob.loadOptionDynDatasourceObj(eleOptionBeanTmp,source);
            }else
            {
                throw new WabacusConfigLoadingException("报表"+rbean.getPath()+"配置的选择框选项的source："+eleOptionBeanTmp.attributeValue("source")+"不合法");
            }

            lstOptions.add(ob);
        }
    }

    public void processRelateInputboxByReportBean()
    {
        if(!this.isDependsOtherInputbox()) return;
        boolean isConditionBox=this.owner instanceof ConditionBean;
        String parentidTmp;
        AbsInputBox parentBoxTmp;
        for(Entry<String,Boolean> entryTmp:this.mParentIds.entrySet())
        {
            if(entryTmp.getValue()==Boolean.FALSE) continue;
            parentBoxTmp=null;
            parentidTmp=entryTmp.getKey();
            if(isConditionBox)
            {//当前选择框是查询条件输入框，则parentidTmp对应的是<conditon/>的name属性
                ConditionBean cbTmp=this.owner.getReportBean().getSbean().getConditionBeanByName(parentidTmp);
                if(cbTmp==null)
                {
                    throw new WabacusConfigLoadingException("加载报表"+this.owner.getReportBean().getPath()+"的选择框"+this.owner.getInputBoxId()+"失败，其依赖的"
                            +parentidTmp+"对应的父输入框不存在");
                }
                if(cbTmp.isHidden()||cbTmp.isConstant()||cbTmp.getInputbox()==null)
                {
                    this.mParentIds.put(parentidTmp,Boolean.FALSE);
                }else
                {
                    parentBoxTmp=cbTmp.getInputbox();
                }
            }else
            {//是编辑列的输入框，则parentidTmp对应的是<col/>的property属性
                ColBean cbTmp=this.owner.getReportBean().getDbean().getColBeanByColProperty(parentidTmp);
                if(cbTmp==null||cbTmp.isControlCol())
                {
                    throw new WabacusConfigLoadingException("加载报表"+this.owner.getReportBean().getPath()+"的选择框"+this.owner.getInputBoxId()+"失败，其依赖的"
                            +parentidTmp+"对应的父输入框不存在");
                }
                ColBean cbSrc=cbTmp.getUpdateColBeanSrc(false);
                if(cbSrc==null) cbSrc=cbTmp;
                EditableReportColBean ercbTmp=(EditableReportColBean)cbSrc.getExtendConfigDataForReportType(EditableReportColBean.class);
                if(ercbTmp==null||ercbTmp.getInputbox()==null)
                {
                    this.mParentIds.put(parentidTmp,Boolean.FALSE);
                }else
                {
                    parentBoxTmp=ercbTmp.getInputbox();
                }
            }
            if(parentBoxTmp!=null) parentBoxTmp.addChildInputboxId(this.getOwner().getInputBoxId());
        }
    }
    
    public void doPostLoad(IInputBoxOwnerBean ownerbean)
    {
        if(this.isMultiply&&(this.separator==null||this.separator.equals(""))) this.separator=" ";
        super.doPostLoad(ownerbean);
        if(ownerbean instanceof EditableReportColBean)
        {
            ColBean cbean=(ColBean)((EditableReportColBean)ownerbean).getOwner();
            this.isBelongtoUpdatecolSrcCol=cbean.getUpdateColBeanDest(false)!=null;
        }
        for(SelectboxOptionBean obTmp:this.lstOptions)
        {
            obTmp.doPostLoad();
        }
    }
    
    protected void processStylePropertyAfterMerged(AbsReportType reportTypeObj,IInputBoxOwnerBean ownerbean)
    {
        super.processStylePropertyAfterMerged(reportTypeObj,ownerbean);
        if(this.isMultipleSelect())
        {
            if(this.separator==null||this.separator.equals("")) this.separator=" ";
            this.styleproperty=Tools.mergeHtmlTagPropertyString(this.styleproperty,"separator=\""+this.separator+"\"",1);
        }
    }
    
    protected String getAllParentIdsAsString()
    {
        if(!this.isDependsOtherInputbox()) return "";
        StringBuffer resultBuf=new StringBuffer();
        boolean isConditionBox=this.owner instanceof ConditionBean;
        ConditionBean conbeanTmp;
        for(String parentidTmp:this.mParentIds.keySet())
        {
            if(isConditionBox)
            {
                conbeanTmp=this.owner.getReportBean().getSbean().getConditionBeanByName(parentidTmp);
                if(conbeanTmp==null||conbeanTmp.isHidden()||conbeanTmp.isConstant()||!conbeanTmp.isConditionValueFromUrl()||conbeanTmp.getInputbox()==null) continue;
            }
            resultBuf.append(parentidTmp).append(";");
        }
        if(resultBuf.charAt(resultBuf.length()-1)==';') resultBuf.deleteCharAt(resultBuf.length()-1);
        return resultBuf.toString();
    }
    
    public Object clone(IInputBoxOwnerBean owner)
    {
        AbsSelectBox boxObjNew=(AbsSelectBox)super.clone(owner);
        if(lstOptions!=null)
        {
            List<SelectboxOptionBean> lstOptionsNew=new ArrayList<SelectboxOptionBean>();
            for(SelectboxOptionBean obTmp:lstOptions)
            {
                lstOptionsNew.add((SelectboxOptionBean)obTmp.clone(boxObjNew));
            }
            boxObjNew.setLstOptions(lstOptionsNew);
        }
        if(mParentIds!=null&&mParentIds.size()>0)
        {
            boxObjNew.mParentIds=(Map<String,Boolean>)((HashMap<String,Boolean>)mParentIds).clone();
            if(owner!=null&&owner.getReportBean()!=null)
            {
                owner.getReportBean().addSelectBoxWithRelate(boxObjNew);
            }
        }
        return boxObjNew;
    }
}
