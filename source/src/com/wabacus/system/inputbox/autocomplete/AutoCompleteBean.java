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
package com.wabacus.system.inputbox.autocomplete;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wabacus.config.ConfigLoadManager;
import com.wabacus.config.component.ComponentConfigLoadAssistant;
import com.wabacus.config.component.ComponentConfigLoadManager;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.DisplayBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.SqlBean;
import com.wabacus.config.component.application.report.condition.ConditionExpressionBean;
import com.wabacus.config.xml.XmlElementBean;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.configbean.editablereport.EditableReportColBean;
import com.wabacus.system.dataset.ISqlDataSet;
import com.wabacus.system.dataset.sqldataset.GetAllDataSetByPreparedSQL;
import com.wabacus.system.dataset.sqldataset.GetAllDataSetBySQL;
import com.wabacus.system.dataset.sqldataset.GetDataSetByStoreProcedure;
import com.wabacus.system.datatype.VarcharType;
import com.wabacus.system.inputbox.AbsInputBox;
import com.wabacus.util.Tools;

public class AutoCompleteBean implements Cloneable
{
    public final static String MULTIPLE_FIRST="first";

    public final static String MULTIPLE_LAST="last";

    public final static String MULTIPLE_NONE="none";

    private AbsInputBox owner;

    private IAutoCompleteDataSet datasetObj;

    private List<String> lstAutoCompleteColumns;

    private List<ColBean> lstAutoCompleteColBeans;
    
    private String colvalueCondition;

    private List<String> lstColPropertiesInColvalueConditions;
    
    private List<ConditionBean> lstConditionBeans;

    private String multiple;//如果匹配了多条记录的处理办法，可取值为first/last/none，分别表示取第一条、最后一条、不取任何记录

    public AutoCompleteBean(AbsInputBox owner)
    {
        this.owner=owner;
    }

    public AbsInputBox getOwner()
    {
        return owner;
    }

    public void setOwner(AbsInputBox owner)
    {
        this.owner=owner;
    }

    public List<String> getLstColPropertiesInColvalueConditions()
    {
        return lstColPropertiesInColvalueConditions;
    }

    public IAutoCompleteDataSet getDatasetObj()
    {
        return datasetObj;
    }

    public List<ColBean> getLstAutoCompleteColBeans()
    {
        return lstAutoCompleteColBeans;
    }

    public List<ConditionBean> getLstConditionBeans()
    {
        return lstConditionBeans;
    }

    public String getMultiple()
    {
        return multiple;
    }

    public void loadConfig(XmlElementBean eleAutocompleteBean)
    {
        ReportBean rbean=this.owner.getOwner().getReportBean();
        String columns=eleAutocompleteBean.attributeValue("columns");
        if(columns==null||columns.trim().equals(""))
        {
            throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"上的输入框"+this.owner.getOwner().getInputBoxId()
                    +"的自动填充配置失败，没有指定要填充的列columns属性");
        }
        this.lstAutoCompleteColumns=Tools.parseStringToList(columns,";",false);
        String dataset=eleAutocompleteBean.attributeValue("dataset");
        if(dataset==null||dataset.trim().equals(""))
        {
            throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"上的输入框"+this.owner.getOwner().getInputBoxId()
                    +"的自动填充配置失败，没有配置dataset属性指定数据集");
        }
        dataset=dataset.trim();
        if(dataset.startsWith("{")&&dataset.endsWith("}")) dataset=dataset.substring(1,dataset.length()-1).trim();
        if(Tools.isDefineKey("class",dataset))
        {
            Object obj;
            try
            {
                obj=ConfigLoadManager.currentDynClassLoader.loadClassByCurrentLoader(Tools.getRealKeyByDefine("class",dataset)).newInstance();
            }catch(Exception e)
            {
                throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"上的输入框"+this.owner.getOwner().getInputBoxId()+"的自动填充配置失败，配置的数据集"
                        +dataset+"类无法实例化",e);
            }
            if(!(obj instanceof IAutoCompleteDataSet))
            {
                throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"上的输入框"+this.owner.getOwner().getInputBoxId()+"的自动填充配置失败，配置的数据集"
                        +dataset+"类没有实现接口"+IAutoCompleteDataSet.class.getName());
            }
            this.datasetObj=(IAutoCompleteDataSet)obj;
        }else if(dataset.toLowerCase().startsWith("call "))
        {
            this.datasetObj=new SPDataSet();
            String spname=dataset.substring("call ".length()).trim();
            if(spname.equals(""))
            {
                throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"上的输入框"+this.owner.getOwner().getInputBoxId()+"的自动填充配置失败，配置的数据集"
                        +dataset+"是存储过程，但没有指定存储过程名");
            }
            if(spname.indexOf("(")>0) spname=spname.substring(0,spname.indexOf("("));
            spname="{call "+spname+"(?)}";
            ((SPDataSet)this.datasetObj).setSp(spname);
        }else
        {
            this.datasetObj=new SqlDataSet();
            if(dataset.indexOf("{#condition#}")<0)
            {
                throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"上的输入框"+this.owner.getOwner().getInputBoxId()+"的自动填充配置失败，配置的数据集"
                        +dataset+"是SQL语句，必须指定{#condition#}占位符");
            }
            ((SqlDataSet)this.datasetObj).setSql(dataset.trim());
        }
        String colvaluecondition=eleAutocompleteBean.attributeValue("colvaluecondition");
        if(colvaluecondition!=null) this.colvalueCondition=colvaluecondition.trim();
        String multiple=eleAutocompleteBean.attributeValue("multiple");
        if(multiple==null||multiple.trim().equals("")) multiple=MULTIPLE_FIRST;
        multiple=multiple.toLowerCase().trim();
        if(!multiple.equals(MULTIPLE_FIRST)&&!multiple.equals(MULTIPLE_LAST)&&!multiple.equals(MULTIPLE_NONE))
        {
            throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"上的输入框"+this.owner.getOwner().getInputBoxId()+"的自动填充配置失败，配置的multiple属性值"
                    +multiple+"无效，只能配置为first/last/none");
        }
        this.multiple=multiple;
        this.lstConditionBeans=ComponentConfigLoadManager.loadConditionsInOtherPlace(eleAutocompleteBean,rbean);
    }

    public void doPostLoad()
    {
        if(this.datasetObj instanceof SqlDataSet&&this.lstConditionBeans!=null)
        {
            ReportBean rbean=this.owner.getOwner().getReportBean();
            boolean isPreparedStmt=false;
            if(rbean.getSbean().getStatementType()==SqlBean.STMTYPE_PREPAREDSTATEMENT)
            {
                isPreparedStmt=true;
            }
            ((SqlDataSet)this.datasetObj).setPreparedStmt(isPreparedStmt);
            for(ConditionBean cbTmp:this.lstConditionBeans)
            {
                if(isPreparedStmt) cbTmp.getConditionExpression().parseConditionExpression();
                if(cbTmp.isConditionValueFromUrl())
                {
                    rbean.addParamNameFromURL(cbTmp.getName());
                }
            }
        }
        processAutoCompleteCols();
        if(this.datasetObj instanceof SqlDataSet)
        {
            processColvalueConditionForSqlDataset();
        }else
        {
            processColvalueConditionForJAVASPDataset();
        }
        ColBean cbOwner=(ColBean)((EditableReportColBean)owner.getOwner()).getOwner();
        ColBean cbUpdatecolDest=cbOwner.getUpdateColBeanDest(false);
        if(!this.lstColPropertiesInColvalueConditions.contains(cbOwner.getProperty()))
        {
            if(cbUpdatecolDest==null||!this.lstColPropertiesInColvalueConditions.contains(cbUpdatecolDest.getProperty()))
            {
                throw new WabacusConfigLoadingException("加载报表"+cbOwner.getReportBean().getPath()+"的列"+cbOwner.getColumn()
                        +"失败，没有在colvaluecondition属性中指定本列输入框的值做为动态条件");
            }
        }
        if(!this.lstColPropertiesInColvalueConditions.contains(cbOwner.getProperty())) this.lstColPropertiesInColvalueConditions.add(cbOwner.getProperty());
        if(cbUpdatecolDest!=null&&!this.lstColPropertiesInColvalueConditions.contains(cbUpdatecolDest.getProperty()))
        {
            this.lstColPropertiesInColvalueConditions.add(cbUpdatecolDest.getProperty());
        }
    }
    
    private void processAutoCompleteCols()
    {
        DisplayBean dbean=owner.getOwner().getReportBean().getDbean();
        this.lstAutoCompleteColBeans=new ArrayList<ColBean>();
        ColBean cbOwner=(ColBean)((EditableReportColBean)owner.getOwner()).getOwner();
        if(!this.lstAutoCompleteColumns.contains(cbOwner.getColumn())) this.lstAutoCompleteColumns.add(cbOwner.getColumn());
        ColBean cbTmp, cbUpdatecolDest, cbUpdatecolSrc;
        List<String> lstRealAutoCompleteColumns=new ArrayList<String>();
        for(String columnTmp:this.lstAutoCompleteColumns)
        {
            if(lstRealAutoCompleteColumns.contains(columnTmp)) continue;
            lstRealAutoCompleteColumns.add(columnTmp);
            cbTmp=dbean.getColBeanByColColumn(columnTmp);
            if(cbTmp==null||cbTmp.isControlCol()||cbTmp.getProperty()==null||cbTmp.getProperty().trim().equals(""))
            {
                throw new WabacusConfigLoadingException("加载报表"+dbean.getReportBean().getPath()+"的列"+cbOwner.getColumn()+"失败，为它配置的自动填充列"+columnTmp
                        +"不存在或不是有效填充列");
            }
            this.lstAutoCompleteColBeans.add(cbTmp);
            cbUpdatecolDest=cbTmp.getUpdateColBeanDest(false);
            if(cbUpdatecolDest!=null&&!lstRealAutoCompleteColumns.contains(cbUpdatecolDest.getColumn()))
            {
                this.lstAutoCompleteColBeans.add(cbUpdatecolDest);
                lstRealAutoCompleteColumns.add(cbUpdatecolDest.getColumn());
            }
            cbUpdatecolSrc=cbTmp.getUpdateColBeanSrc(false);
            if(cbUpdatecolSrc!=null&&!lstRealAutoCompleteColumns.contains(cbUpdatecolSrc.getColumn()))
            {
                this.lstAutoCompleteColBeans.add(cbUpdatecolSrc);
                lstRealAutoCompleteColumns.add(cbUpdatecolSrc.getColumn());
            }
        }
        this.lstAutoCompleteColumns=null;
    }
    
    private void processColvalueConditionForSqlDataset()
    {
        DisplayBean dbean=owner.getOwner().getReportBean().getDbean();
        ColBean cbOwner=(ColBean)((EditableReportColBean)owner.getOwner()).getOwner();
        if(this.lstColPropertiesInColvalueConditions==null) this.lstColPropertiesInColvalueConditions=new ArrayList<String>();
        if(this.colvalueCondition==null||this.colvalueCondition.trim().equals(""))
        {
            boolean isVarcharType=cbOwner.getDatatypeObj() instanceof VarcharType;
            this.colvalueCondition=cbOwner.getColumn()+"=";
            if(isVarcharType) this.colvalueCondition+="'";
            this.colvalueCondition+="#"+cbOwner.getProperty()+"#";
            if(isVarcharType) this.colvalueCondition+="'";
            this.lstColPropertiesInColvalueConditions.add(cbOwner.getProperty());
        }else
        {
            ColBean cbTmp;
            String expressTmp=this.colvalueCondition;
            String propertyTmp;
            while(true)
            {
                int idx=expressTmp.indexOf("#");
                if(idx<0) break;
                expressTmp=expressTmp.substring(idx+1);
                idx=expressTmp.indexOf("#");
                if(idx<=0) break;
                propertyTmp=expressTmp.substring(0,idx);
                cbTmp=dbean.getColBeanByColProperty(propertyTmp.trim());
                if(cbTmp!=null)
                {
                    if(cbTmp.isControlCol()||cbTmp.getProperty()==null||cbTmp.getProperty().trim().equals(""))
                    {
                        throw new WabacusConfigLoadingException("加载报表"+dbean.getReportBean().getPath()+"的列"+cbOwner.getColumn()
                                +"失败，为它配置的自动填充列所用做为条件的列"+propertyTmp+"不存在或不是有效数据列");
                    }
                    if(!lstColPropertiesInColvalueConditions.contains(cbTmp.getProperty()))
                    {
                        lstColPropertiesInColvalueConditions.add(cbTmp.getProperty());
                    }
                    expressTmp=expressTmp.substring(idx+1);
                }
            }
        }
    }
    
    private void processColvalueConditionForJAVASPDataset()
    {
        DisplayBean dbean=owner.getOwner().getReportBean().getDbean();
        ColBean cbOwner=(ColBean)((EditableReportColBean)owner.getOwner()).getOwner();
        if(this.lstColPropertiesInColvalueConditions==null) this.lstColPropertiesInColvalueConditions=new ArrayList<String>();
        if(this.colvalueCondition==null||this.colvalueCondition.trim().equals(""))
        {
            this.lstColPropertiesInColvalueConditions.add(cbOwner.getProperty());
        }else
        {
            ColBean cbTmp;
            List<String> lstColProperties=Tools.parseStringToList(this.colvalueCondition,";",false);
            for(String propTmp:lstColProperties)
            {
                if(propTmp==null||propTmp.trim().equals("")) continue;
                cbTmp=dbean.getColBeanByColProperty(propTmp.trim());
                if(cbTmp==null||cbTmp.isControlCol())
                {
                    throw new WabacusConfigLoadingException("加载报表"+dbean.getReportBean().getPath()+"的列"+cbOwner.getColumn()+"失败，为它配置的自动填充列所用做为条件的列"
                            +propTmp+"不存在或不是有效数据列");
                }
                if(!lstColPropertiesInColvalueConditions.contains(cbTmp.getProperty()))
                {
                    lstColPropertiesInColvalueConditions.add(cbTmp.getProperty());
                }
            }
        }
    }
    
    private class SqlDataSet implements IAutoCompleteDataSet
    {
        private String sql;
        
        private boolean isPreparedStmt=false;

        public String getSql()
        {
            return sql;
        }

        public void setSql(String sql)
        {
            this.sql=sql;
        }

        public void setPreparedStmt(boolean isPreparedStmt)
        {
            this.isPreparedStmt=isPreparedStmt;
        }

        public Map<String,String> getAutoCompleteColumnsData(ReportRequest rrequest,AutoCompleteBean autoCompleteBean,Map<String,String> mParams)
        {
            if(mParams==null||mParams.size()==0) return null;
            String realColConditionExpression=colvalueCondition;
            String colValTmp;
            for(String colpropertyTmp:lstColPropertiesInColvalueConditions)
            {
                colValTmp=mParams.get(colpropertyTmp);
                if(colValTmp==null) colValTmp="";
                realColConditionExpression=Tools.replaceAll(realColConditionExpression,"#"+colpropertyTmp+"#",colValTmp);
            }
            if(realColConditionExpression==null||realColConditionExpression.trim().equals("")) return null;
            ISqlDataSet sqlDataSet=null;
            if(this.isPreparedStmt)
            {
                sqlDataSet=new GetAllDataSetByPreparedSQL();
            }else
            {
                sqlDataSet=new GetAllDataSetBySQL();
            }
            String sqlTmp=Tools.replaceAll(sql,"{#condition#}","{@condition@} and "+realColConditionExpression);//保留{#condition#}，因为后面还有可能拼凑<condition/>中配置的条件
            sqlTmp=Tools.replaceAll(sqlTmp,"{@condition@}","{#condition#}");
            Object objTmp=sqlDataSet.getDataSet(rrequest,owner.getOwner().getReportBean(),autoCompleteBean,sqlTmp,lstConditionBeans,null);
            if(objTmp==null||rrequest.getWResponse().getMessageCollector().hasErrors()||rrequest.getWResponse().getMessageCollector().hasWarnings())
            {
                return null;
            }
            if(objTmp instanceof Map) return (Map<String,String>)objTmp;//用户直接在拦截器加载数据前置动作中返回了自己构造的填充列的值
            return getMColDataValuesByResultSet((ResultSet)objTmp);
        }
    }

    private class SPDataSet implements IAutoCompleteDataSet
    {
        private String sp;

        public String getSp()
        {
            return sp;
        }

        public void setSp(String sp)
        {
            this.sp=sp;
        }

        public Map<String,String> getAutoCompleteColumnsData(ReportRequest rrequest,AutoCompleteBean autoCompleteBean,Map<String,String> mParams)
        {
            StringBuffer paramsBuf=new StringBuffer();
            String colValTmp;
            for(String colpropertyTmp:lstColPropertiesInColvalueConditions)
            {
                colValTmp=mParams.get(colpropertyTmp);
                if(colValTmp==null) colValTmp="";
                paramsBuf.append("["+colpropertyTmp+"="+colValTmp+"]");
            }
            if(lstConditionBeans!=null)
            {
                String conValTmp;
                for(ConditionBean cbTmp:lstConditionBeans)
                {
                    conValTmp=cbTmp.getConditionValue(rrequest,-1);
                    if(conValTmp==null) conValTmp="";
                    paramsBuf.append("["+cbTmp.getName()+"="+conValTmp+"]");
                }
            }
            ConditionBean conbeanTmp=new ConditionBean(null);
            conbeanTmp.setConstant(true);
            conbeanTmp.setHidden(true);
            ConditionExpressionBean cebean=new ConditionExpressionBean();
            cebean.setValue(paramsBuf.toString());
            conbeanTmp.setConditionExpression(cebean);
            List<ConditionBean> lstConditionsTmp=new ArrayList<ConditionBean>();
            lstConditionsTmp.add(conbeanTmp);
            Object objTmp=new GetDataSetByStoreProcedure().getDataSet(rrequest,owner.getOwner().getReportBean(),autoCompleteBean,sp,lstConditionsTmp,null);
            if(objTmp==null||rrequest.getWResponse().getMessageCollector().hasErrors()||rrequest.getWResponse().getMessageCollector().hasWarnings())
            {
                return null;
            }
            if(objTmp instanceof Map) return (Map<String,String>)objTmp;
            return getMColDataValuesByResultSet((ResultSet)objTmp);
        }
    }

    private Map<String,String> getMColDataValuesByResultSet(ResultSet rs)
    {
        String colValTmp;
        Map<String,String> mResults=new HashMap<String,String>();
        try
        {
            while(rs.next())
            {
                if(mResults.size()>0)
                {
                    if(MULTIPLE_FIRST.equals(this.multiple)) return mResults;
                    if(MULTIPLE_NONE.equals(this.multiple)) return null;
                }
                for(ColBean cbTmp:lstAutoCompleteColBeans)
                {
                    colValTmp=rs.getString(cbTmp.getColumn());
                    if(colValTmp==null) colValTmp="";
                    mResults.put(cbTmp.getProperty(),colValTmp);
                }
            }
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("获取报表"+this.owner.getOwner().getReportBean().getPath()+"的自动填充数据失败",e);
        }finally
        {
            try
            {
                if(rs!=null) rs.close();
            }catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
        return mResults;
    }
    
    protected Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public AutoCompleteBean clone(AbsInputBox newowner)
    {
        AutoCompleteBean newBean=null;
        try
        {
            newBean=(AutoCompleteBean)clone();
            newBean.setOwner(newowner);
            if(lstAutoCompleteColumns!=null) newBean.lstAutoCompleteColumns=(List<String>)((ArrayList<String>)lstAutoCompleteColumns).clone();
            if(lstConditionBeans!=null)
            {
                newBean.lstConditionBeans=ComponentConfigLoadAssistant.getInstance().cloneLstConditionBeans(null,lstConditionBeans);
            }
        }catch(CloneNotSupportedException e)
        {
            throw new WabacusConfigLoadingException("clone输入框对象失败",e);
        }
        return newBean;
    }
}
