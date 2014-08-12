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
package com.wabacus.system.component.application.report.configbean.editablereport;

import java.util.ArrayList;
import java.util.List;

import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.assistant.EditableReportAssistant;
import com.wabacus.system.buttons.EditableReportSQLButtonDataBean;
import com.wabacus.util.Consts;
import com.wabacus.util.Tools;

public abstract class AbsEditableReportEditDataBean implements Cloneable
{
    private String refreshParentidOnSave;////如果当前报表是可编辑从报表，则保存数据时需要刷新它的哪个主报表（因为可能有多层主报表）
    
    private boolean resetNavigateInfoOnRefreshParent;
    
    private List<EditActionGroupBean> lstEditActionGroupBeans;//配置的所有<value/>更新脚本

    protected List<EditableReportExternalValueBean> lstExternalValues;//通过<params/>配置的外部值

    private IEditableReportEditGroupOwnerBean owner;

    public AbsEditableReportEditDataBean(IEditableReportEditGroupOwnerBean owner)
    {
        this.owner=owner;
        lstEditActionGroupBeans=new ArrayList<EditActionGroupBean>();
    }

    public String getRefreshParentidOnSave()
    {
        return refreshParentidOnSave;
    }

    public void setRefreshParentidOnSave(String refreshParentidOnSave)
    {
        this.refreshParentidOnSave=refreshParentidOnSave;
    }

    public boolean isResetNavigateInfoOnRefreshParent()
    {
        return resetNavigateInfoOnRefreshParent;
    }

    public void setResetNavigateInfoOnRefreshParent(boolean resetNavigateInfoOnRefreshParent)
    {
        this.resetNavigateInfoOnRefreshParent=resetNavigateInfoOnRefreshParent;
    }

    public List<EditActionGroupBean> getLstEditActionGroupBeans()
    {
        return lstEditActionGroupBeans;
    }

    public void setLstEditActionGroupBeans(List<EditActionGroupBean> lstEditActionGroupBeans)
    {
        this.lstEditActionGroupBeans=lstEditActionGroupBeans;
    }

    public void addEditActionGroupBean(EditActionGroupBean editActionGroupBean)
    {
        this.lstEditActionGroupBeans.add(editActionGroupBean);
    }
    
    public List<EditableReportExternalValueBean> getLstExternalValues()
    {
        return lstExternalValues;
    }

    public void setLstExternalValues(List<EditableReportExternalValueBean> lstExternalValues)
    {
        this.lstExternalValues=lstExternalValues;
    }

    public IEditableReportEditGroupOwnerBean getOwner()
    {
        return owner;
    }

    public void setOwner(IEditableReportEditGroupOwnerBean owner)
    {
        this.owner=owner;
    }

    public boolean isAutoReportdata()
    {
        return true;
    }
    
    public EditableReportExternalValueBean getExternalValueBeanByName(String name,boolean isMust)
    {
        if(lstExternalValues!=null)
        {
            for(EditableReportExternalValueBean evbeanTmp:lstExternalValues)
            {
                if(evbeanTmp.getName().equals(name)) return evbeanTmp;
            }
        }
        if(isMust)
        {
            throw new WabacusConfigLoadingException("加载报表"+this.getOwner().getReportBean().getPath()+"失败，没有在<params/>中定义name属性为"
                    +name+"对应的变量值");
        }
        return null;
    }

    public int parseActionscripts(String reportTypeKey)
    {
        if(lstExternalValues!=null&&lstExternalValues.size()>0)
        {
            for(EditableReportExternalValueBean evbeanTmp:lstExternalValues)
            {
                evbeanTmp.parseValues(reportTypeKey);
            }
        }
        if(this.lstEditActionGroupBeans==null||this.lstEditActionGroupBeans.size()==0) return 0;
        for(EditActionGroupBean actiongroupbeanTmp:this.lstEditActionGroupBeans)
        {
            actiongroupbeanTmp.parseActionscripts(reportTypeKey);
        }
        return 1;
    }

//    /**






//     */







//        }


//        /**

//         */




//        /**

//         * 1：说明是@{property}类型的参数，即从<col/>中获取值做为参数；
//         * 2：说明是#{参数名}类型的参数，即引用<params/>中定义的参数

//         */





//            if(isParam)
//            {//说明当前是在"@{"内，后面的字符串都是动态参数对应的<col/>的property，直到"}"为止。

//                {//遇到}，说明动态参数对应的property结束。











//                        i++;//跳过右边的%号


//                    {//@{
//                        String realproperty=property;




//                            throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，配置的要更新字段"+property+"不合法，没有取到其值对应的<col/>");






//                    {//#{



//                    {//!{

//                    }














//                    {//是有效的动态查询条件
//                        isParam=true;







//                    {//是有效的动态查询条件







//                    if(k>i)
//                    {//是有效的动态查询条件














//        whereclause=whereBuffer.toString();
//      //将它们替换回来





//    /**






//     */



//        for(;k<whereclause.length();k++)

//            if(whereclause.charAt(k)==' ') continue;//@、#、! 与 {中间有空格，则跳过

//            {//在@、#、! 之后碰到{，则说明是真正的动态参数

//                {//即是%@{property}、%#{property}、%!{property}格式的动态条件

//                    whereBuffer.deleteCharAt(whereBuffer.length()-1);//删除掉刚才加进去的%号




//            {//在@之后碰到的是其它字符，则不是参数，此时的@、#、! 就是一个普通字符。





//        {//是以@字符结尾，则此@字符就是一个普通的字符。

//        }



    public String parseStandardEditSql(String sql,List<EditableReportParamBean> lstDynParams,String reportTypeKey)
    {
        sql=EditableReportAssistant.getInstance().parseStandardEditSql(this.owner.getReportBean(),sql,lstDynParams);
        if(lstDynParams.size()>0)
        {
            for(EditableReportParamBean paramBeanTmp:lstDynParams)
            {
                parseStandardSqlParamBean(paramBeanTmp,reportTypeKey);
            }
        }
        return sql;
    }
    
    private void parseStandardSqlParamBean(EditableReportParamBean paramBean,String reportTypeKey)
    {
        if(Tools.isDefineKey("@",paramBean.getParamname()))
        {
            if(this.isAutoReportdata())
            {//自动获取某列的数据进行保存操作（这个判断主要是针对配置更新脚本的<button/>，因为它的@{param}数据有可能是客户端传的，而不是从报表中获取的）
                String configproperty=Tools.getRealKeyByDefine("@",paramBean.getParamname());
                String realproperty=configproperty;
                if(realproperty.endsWith("__old")) realproperty=realproperty.substring(0,realproperty.length()-"__old".length());
                ColBean cbeanUpdateDest=this.owner.getReportBean().getDbean().getColBeanByColProperty(realproperty);
                if(cbeanUpdateDest==null)
                {
                    throw new WabacusConfigLoadingException("解析报表的更新语句失败，没有找到column/property属性为"+realproperty+"的列");
                }
                if(cbeanUpdateDest.isNonValueCol()||cbeanUpdateDest.isSequenceCol()||cbeanUpdateDest.isControlCol())
                {
                    throw new WabacusConfigLoadingException("加载报表"+this.owner.getReportBean().getPath()+"失败，列"+cbeanUpdateDest.getColumn()
                            +"不是从数据库获取数据的列，不能取其数据");
                }
                EditableReportColBean ercbeanDest=(EditableReportColBean)cbeanUpdateDest.getExtendConfigDataForReportType(reportTypeKey);
                if(ercbeanDest==null)
                {
                    ercbeanDest=new EditableReportColBean(cbeanUpdateDest);
                    cbeanUpdateDest.setExtendConfigDataForReportType(reportTypeKey,ercbeanDest);
                }else
                {
                    paramBean.setDefaultvalue(ercbeanDest.getDefaultvalue());
                }
                paramBean.setOwner(cbeanUpdateDest);
                ColBean cbeanUpdateSrc=cbeanUpdateDest;
                if(Consts.COL_DISPLAYTYPE_HIDDEN.equals(cbeanUpdateDest.getDisplaytype()))
                {
                    ColBean cbSrcTmp=cbeanUpdateDest.getUpdateColBeanSrc(false);
                    if(cbSrcTmp!=null) cbeanUpdateSrc=cbSrcTmp;
                }
                setParamBeanInfoOfColBean(cbeanUpdateSrc,paramBean,configproperty,reportTypeKey);
            }else
            {
                ((EditableReportSQLButtonDataBean)this).setHasReportDataParams(true);
            }
        }else if(Tools.isDefineKey("#",paramBean.getParamname()))
        {
            String paramname=Tools.getRealKeyByDefine("#",paramBean.getParamname());
            paramBean.setParamname(paramname);
            paramBean.setOwner(this.getExternalValueBeanByName(paramname,true));
        }
    }
    
    protected abstract void setParamBeanInfoOfColBean(ColBean cbUpdateSrc,EditableReportParamBean paramBean,String configColProperty,
            String reportTypeKey);

    public void doPostLoadFinally()
    {
        if(!this.isAutoReportdata()) return;
        if(lstEditActionGroupBeans!=null)
        {
            List<AbsEditActionBean> lstActionBeanTmp;
            for(EditActionGroupBean groupBeanTmp:this.lstEditActionGroupBeans)
            {
                lstActionBeanTmp=groupBeanTmp.getLstEditActionBeans();
                if(lstActionBeanTmp==null||lstActionBeanTmp.size()==0) continue;
                for(AbsEditActionBean actionBeanTmp:lstActionBeanTmp)
                {
                    actionBeanTmp.doPostLoadFinally();
                }
            }
        }
        if(lstExternalValues!=null)
        {
            for(EditableReportExternalValueBean valueBeanTmp:this.lstExternalValues)
            {
                valueBeanTmp.doPostLoadFinally();
            }
        }
    }
    
    protected void setRealParamnameInDoPostLoadFinally(EditableReportParamBean paramBean)
    {
        if(paramBean.getParamname()==null||!paramBean.getParamname().endsWith("__old")) return;
        if(paramBean.getOwner() instanceof ColBean)
        {
            ColBean cbTmp=((ColBean)paramBean.getOwner()).getUpdateColBeanSrc(false);
            if(cbTmp==null) cbTmp=(ColBean)paramBean.getOwner();
            EditableReportColBean ercbeanTmp=(EditableReportColBean)((ColBean)paramBean.getOwner())
                    .getExtendConfigDataForReportType(EditableReportColBean.class);
            if(ercbeanTmp==null||!ercbeanTmp.isEditableForUpdate())
            {
                paramBean.setParamname(paramBean.getParamname().substring(0,paramBean.getParamname().length()-"__old".length()));
            }
        }
    }
    
    public Object clone(IEditableReportEditGroupOwnerBean newowner)
    {
        try
        {
            AbsEditableReportEditDataBean newbean=(AbsEditableReportEditDataBean)super.clone();
            newbean.setOwner(newowner);
            if(this.lstEditActionGroupBeans!=null)
            {
                List<EditActionGroupBean> lstGroupBeansNew=new ArrayList<EditActionGroupBean>();
                for(EditActionGroupBean groupBeanTmp:lstEditActionGroupBeans)
                {
                    lstGroupBeansNew.add((EditActionGroupBean)groupBeanTmp.clone(newbean));
                }
                newbean.setLstEditActionGroupBeans(lstGroupBeansNew);
            }

            if(lstExternalValues!=null)
            {
                List<EditableReportExternalValueBean> lstExternalValuesNew=new ArrayList<EditableReportExternalValueBean>();
                for(EditableReportExternalValueBean valueBeanTmp:lstExternalValues)
                {
                    lstExternalValuesNew.add((EditableReportExternalValueBean)valueBeanTmp.clone());
                }
                newbean.setLstExternalValues(lstExternalValuesNew);
            }
            return newbean;
        }catch(CloneNotSupportedException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
