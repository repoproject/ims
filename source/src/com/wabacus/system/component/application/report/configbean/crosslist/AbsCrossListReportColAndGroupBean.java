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
package com.wabacus.system.component.application.report.configbean.crosslist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.Config;
import com.wabacus.config.ConfigLoadManager;
import com.wabacus.config.component.ComponentConfigLoadAssistant;
import com.wabacus.config.component.application.report.AbsConfigBean;
import com.wabacus.config.component.application.report.AbsReportDataPojo;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.DisplayBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.config.component.application.report.SqlBean;
import com.wabacus.config.component.application.report.extendconfig.AbsExtendConfigBean;
import com.wabacus.config.database.type.AbsDatabaseType;
import com.wabacus.config.database.type.SQLSERVER2K;
import com.wabacus.config.database.type.SQLSERVER2K5;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.ReportAssistant;
import com.wabacus.system.component.application.report.CrossListReportType;
import com.wabacus.system.component.application.report.configbean.UltraListReportGroupBean;
import com.wabacus.system.dataset.IDynamicColGroupDataSet;
import com.wabacus.system.dataset.ISqlDataSet;
import com.wabacus.system.dataset.sqldataset.GetAllDataSetByPreparedSQL;
import com.wabacus.system.dataset.sqldataset.GetAllDataSetBySQL;
import com.wabacus.system.datatype.IDataType;
import com.wabacus.util.Consts;
import com.wabacus.util.Tools;

public abstract class AbsCrossListReportColAndGroupBean extends AbsExtendConfigBean
{
    private static Log log=LogFactory.getLog(AbsCrossListReportColAndGroupBean.class);

    protected String realvalue;

    protected String datasetid;

    private String configDynColGroupTitleDataset;

    protected IDynamicColGroupDataSet dynColgroupTitleDatasetObj;

    protected String staticondition;

    protected List<ConditionBean> lstStatiConditions;

    protected List<ConditionBean> lstDatasetConditions;

    protected CrossListReportGroupBean parentCrossGroupBean;

    protected List<CrossListReportStatiDisplayBean> lstDisplayStatisBeans;//本<col/>下要显示的统计项对象，在doPostLoad()方法中构造

    protected List<CrossListReportStatiDisplayBean> lstDisplayStatisBeansOfReport;//针对整个报表进行横向统计的统计项对象，即statitems配置为report的<statistic/>标签对象，在doPostLoad()方法中构造

    private List<Map<String,String>> lstDynCols;

    private String dataheaderformatContent;

    private List<String> lstDataHeaderFormatImports;

    protected Class dataHeaderPojoClass;

    protected AbsDynamicColGroupBean dynColGroupSpecificBean;

    public AbsCrossListReportColAndGroupBean(AbsConfigBean owner)
    {
        super(owner);
    }

    public void setRealvalue(String realvalue)
    {
        this.realvalue=realvalue;
    }

    public String getDatasetid()
    {
        return this.datasetid;
    }

    public void setDatasetid(String datasetid)
    {
        this.datasetid=datasetid;
    }

    public void setDynColgroupTitleDatasetObj(IDynamicColGroupDataSet dynColgroupTitleDatasetObj)
    {
        this.dynColgroupTitleDatasetObj=dynColgroupTitleDatasetObj;
    }

    public void setConfigDynColGroupTitleDataset(String configDynColGroupTitleDataset)
    {
        this.configDynColGroupTitleDataset=configDynColGroupTitleDataset;
    }

    public void setStaticondition(String staticondition)
    {
        this.staticondition=staticondition;
    }

    public void setLstStatiConditions(List<ConditionBean> lstStatiConditions)
    {
        if(lstStatiConditions!=null&&lstStatiConditions.size()==0) lstStatiConditions=null;
        this.lstStatiConditions=lstStatiConditions;
    }

    public List<ConditionBean> getLstDatasetConditions()
    {
        return lstDatasetConditions;
    }

    public void setLstDatasetConditions(List<ConditionBean> lstDatasetConditions)
    {
        if(lstDatasetConditions!=null&&lstDatasetConditions.size()==0) lstDatasetConditions=null;
        this.lstDatasetConditions=lstDatasetConditions;
    }

    public void setParentCrossGroupBean(CrossListReportGroupBean parentCrossGroupBean)
    {
        this.parentCrossGroupBean=parentCrossGroupBean;
    }

    public List<CrossListReportStatiDisplayBean> getLstDisplayStatisBeans()
    {
        return lstDisplayStatisBeans;
    }

    public void setDataheaderformatContent(String dataheaderformatContent)
    {
        this.dataheaderformatContent=dataheaderformatContent;
    }

    public void setLstDataHeaderFormatImports(List<String> lstDataHeaderFormatImports)
    {
        this.lstDataHeaderFormatImports=lstDataHeaderFormatImports;
    }

    public void setDataHeaderPojoClass(Class dataHeaderPojoClass)
    {
        this.dataHeaderPojoClass=dataHeaderPojoClass;
    }

    public boolean isCommonCrossColGroup()
    {
        if(this.dynColGroupSpecificBean==null) return false;
        return this.dynColGroupSpecificBean.isCommonCrossColGroup();
    }

    public boolean isStatisticCrossColGroup()
    {
        if(this.dynColGroupSpecificBean==null) return false;
        return this.dynColGroupSpecificBean.isStatisticCrossColGroup();
    }

    public void addStatiDisplayBeanOfReport(CrossListReportStatiDisplayBean statidbean)
    {
        if(this.lstDisplayStatisBeansOfReport==null) this.lstDisplayStatisBeansOfReport=new ArrayList<CrossListReportStatiDisplayBean>();
        this.lstDisplayStatisBeansOfReport.add(statidbean);
    }

    public void setDynColGroupSpecificBean(AbsDynamicColGroupBean dynColGroupSpecificBean)
    {
        if(this.isDynamicColGroup())
        {
            this.dynColGroupSpecificBean=dynColGroupSpecificBean;
        }
        //即使父分组列不是动态列，也要调用它的此方法，因为在CrossListReportGroupBean的此方法中还要设置这种列有没有包含子动态列
        if(this.parentCrossGroupBean!=null) this.parentCrossGroupBean.setDynColGroupSpecificBean(dynColGroupSpecificBean);
    }

    public CrossListReportDynDatasetBean getDatasetBean()
    {
        if(!this.isDynamicColGroup()) return null;
        return getCrossReportBean().getCrossDatasetBean(this.getRootCrossColGroupBean(),true);
    }

    protected CrossListReportBean getCrossReportBean()
    {
        return ((CrossListReportBean)this.getOwner().getReportBean().getExtendConfigDataForReportType(CrossListReportType.KEY));
    }

    public AbsCrossListReportColAndGroupBean getRootCrossColGroupBean()
    {
        if(!this.isDynamicColGroup()) return null;
        if(this.parentCrossGroupBean==null||!this.parentCrossGroupBean.isDynamicColGroup())
        {
            return this;
        }
        return this.parentCrossGroupBean.getRootCrossColGroupBean();
    }

    public String getRootCrossColGroupId()
    {
        AbsCrossListReportColAndGroupBean rootColGroupBean=this.getRootCrossColGroupBean();
        if(rootColGroupBean instanceof CrossListReportGroupBean)
        {
            return ((UltraListReportGroupBean)rootColGroupBean.getOwner()).getGroupid();
        }else if(rootColGroupBean instanceof CrossListReportColBean)
        {
            return ((ColBean)rootColGroupBean.getOwner()).getColid();
        }
        return "";
    }

    public boolean isRootDynamicColGroup()
    {
        if(!this.isDynamicColGroup()) return false;
        if(this.parentCrossGroupBean==null||!this.parentCrossGroupBean.isDynamicColGroup())
        {
            return true;
        }
        return false;
    }

    public boolean isDynamicColGroup()
    {
        if(parentCrossGroupBean!=null&&parentCrossGroupBean.isDynamicColGroup()) return true;
        if(this.configDynColGroupTitleDataset!=null&&!this.configDynColGroupTitleDataset.trim().equals("")) return true;
        return false;
    }

    protected boolean hasDisplayStatisBeans(Map<String,Boolean> mDynamicColGroupDisplayType)
    {
        if(this.lstDisplayStatisBeans==null||this.lstDisplayStatisBeans.size()==0) return false;
        for(CrossListReportStatiDisplayBean cslsdbeanTmp:this.lstDisplayStatisBeans)
        {
            if(mDynamicColGroupDisplayType.get(cslsdbeanTmp.getStatiBean().getId()).booleanValue())
            {
                return true;
            }
        }
        return false;
    }

    protected boolean hasDisplayStatisBeansOfReport(Map<String,Boolean> mDynamicColGroupDisplayType)
    {
        if(this.lstDisplayStatisBeansOfReport==null||this.lstDisplayStatisBeansOfReport.size()==0) return false;
        for(CrossListReportStatiDisplayBean cslsdbeanTmp:this.lstDisplayStatisBeansOfReport)
        {
            if(mDynamicColGroupDisplayType.get(cslsdbeanTmp.getStatiBean().getId()).booleanValue())
            {
                return true;
            }
        }
        return false;
    }

    protected List<Map<String,String>> getDynColGroupLabelData(CrossListReportType crossListReportTypeObj)
    {
        List<Map<String,String>> lstResults=this.dynColgroupTitleDatasetObj.getDynamicColGroupDataSet(crossListReportTypeObj,this.getDatasetBean()
                .getDatasetbean(),this.lstDatasetConditions);
        ReportBean rbean=crossListReportTypeObj.getReportBean();
        if(rbean.getInterceptor()!=null)
        {
            lstResults=(List)rbean.getInterceptor().afterLoadData(crossListReportTypeObj.getReportRequest(),rbean,this,lstResults);
        }
        return lstResults;
    }

    protected void getRealDisplayLabel(ReportRequest rrequest,AbsReportDataPojo headDataObj,List lstDynChildren)
    {
        if(lstDynChildren==null||lstDynChildren.size()==0) return;
        for(Object objBeanTmp:lstDynChildren)
        {
            String label;
            if(objBeanTmp instanceof ColBean)
            {
                label=((ColBean)objBeanTmp).getLabel(rrequest);
                if(label!=null&&label.indexOf("_")==0&&label.substring(1).indexOf("_")>0)
                {
                    ((ColBean)objBeanTmp).setLabel((String)headDataObj.getDynamicColData(label));
                }
            }else
            {
                getRealDisplayLabel(rrequest,(UltraListReportGroupBean)objBeanTmp,headDataObj);
            }
        }
    }

    private void getRealDisplayLabel(ReportRequest rrequest,UltraListReportGroupBean objBeanTmp,AbsReportDataPojo headDataObj)
    {
        String label=objBeanTmp.getLabel(rrequest);
        if(label!=null&&label.indexOf("_")==0&&label.substring(1).indexOf("_")>0)
        {
            objBeanTmp.setLabel((String)headDataObj.getDynamicColData(label));
        }
        getRealDisplayLabel(rrequest,headDataObj,objBeanTmp.getLstChildren());
    }

    protected ColBean createDynamicCrossStatiColBean(DisplayBean disbean,String label,String labelstyleproperty,String valuestyleproperty,
            IDataType dataTypeObj,int colidx)
    {
        ColBean cbResult=new ColBean(disbean,colidx);
        cbResult.setLabel(label);
        cbResult.setDatasetValueId(this.getDatasetBean().getDatasetbean().getId());
        cbResult.setLabelstyleproperty(labelstyleproperty,false);
        cbResult.setValuestyleproperty(valuestyleproperty,false);
        cbResult.setDatatypeObj(dataTypeObj);
        cbResult.setDisplaytype(Consts.COL_DISPLAYTYPE_ALWAYS);
        cbResult.setProperty("[DYN_COL_DATA]");
        cbResult.setColumn("column_"+colidx);
        CrossListReportColBean crcbeanTmp=new CrossListReportColBean(cbResult);
        crcbeanTmp.setBelongToRootOwner(this.getRootCrossColGroupBean());
        cbResult.setExtendConfigDataForReportType(CrossListReportType.KEY,crcbeanTmp);
        return cbResult;
    }

    protected void afterGetRuntimeColGroups(CrossListReportType crossListReportType,Map<String,Boolean> mDynamicColGroupDisplayType,
            StringBuffer allDynColConditonsBuf,StringBuffer dynSelectedColsBuf,List lstAllRuntimeChildren,List<ColBean> lstAllRuntimeColBeans,
            List lstDynChildren,AbsReportDataPojo headDataObj)
    {
        ReportBean rbean=crossListReportType.getReportBean();
        ReportRequest rrequest=crossListReportType.getReportRequest();
        if(headDataObj!=null)
        {
            headDataObj.format();
            getRealDisplayLabel(crossListReportType.getReportRequest(),headDataObj,lstDynChildren);
        }
        if(dynSelectedColsBuf.length()>0&&dynSelectedColsBuf.charAt(dynSelectedColsBuf.length()-1)==',')
        {
            dynSelectedColsBuf.deleteCharAt(dynSelectedColsBuf.length()-1);
        }
        String selectedCols=dynSelectedColsBuf.toString();
        if(this.hasDisplayStatisBeansOfReport(mDynamicColGroupDisplayType))
        {
            StringBuffer tmpBuf=new StringBuffer();
            String allColConditions=allDynColConditonsBuf.toString().trim();
            if(allColConditions.endsWith("or")) allColConditions=allColConditions.substring(0,allColConditions.length()-2);
            createStatisForWholeRow(crossListReportType,tmpBuf,lstDynChildren,allColConditions,mDynamicColGroupDisplayType);
            if(!tmpBuf.toString().trim().equals(""))
            {
                if(selectedCols.trim().equals(""))
                {
                    selectedCols=tmpBuf.toString();
                }else
                {
                    selectedCols=selectedCols+","+tmpBuf.toString();
                }
            }
        }
        if(!selectedCols.trim().equals(""))
        {
            createAllDisplayChildren(lstAllRuntimeColBeans,lstAllRuntimeChildren,lstDynChildren);
        }
        crossListReportType.addDynamicSelectCols(this,selectedCols);//将动态查询的selectCols字段加到数据集中，因为一个数据集可能查询多个动态列，因此现在这里收集，稍后根据它们构造数据集真正的查询SQL语句
    }

    protected void createStatisForWholeRow(CrossListReportType crossListReportType,StringBuffer dynselectedColsBuf,List lstChildren,
            String allColConditions,Map<String,Boolean> mDynamicColGroupDisplayType)
    {
        if(this.lstDisplayStatisBeansOfReport==null||this.lstDisplayStatisBeansOfReport.size()==0) return;
        DisplayBean disbean=this.getOwner().getReportBean().getDbean();
        ReportRequest rrequest=crossListReportType.getReportRequest();
        for(CrossListReportStatiDisplayBean statisdBeanTmp:this.lstDisplayStatisBeansOfReport)
        {
            if(!mDynamicColGroupDisplayType.get(statisdBeanTmp.getStatiBean().getId())) continue;
            int colidx=crossListReportType.generateColGroupIdxId();
            lstChildren.add(createDynamicCrossStatiColBean(disbean,rrequest.getI18NStringValue(statisdBeanTmp.getLabel()),statisdBeanTmp
                    .getLabelstyleproperty(rrequest),statisdBeanTmp.getValuestyleproperty(rrequest),statisdBeanTmp.getStatiBean().getDatatypeObj(),colidx));
            dynselectedColsBuf.append(statisdBeanTmp.getStatiBean().getType()+"(");
            if(!allColConditions.trim().equals(""))
            {
                dynselectedColsBuf.append("case when ").append(allColConditions).append(" then ").append(statisdBeanTmp.getStatiBean().getColumn())
                        .append("  end ");
            }else
            {
                dynselectedColsBuf.append(statisdBeanTmp.getStatiBean().getColumn());
            }
            dynselectedColsBuf.append(") as ").append("column_"+colidx).append(",");
        }
        if(dynselectedColsBuf.length()>0&&dynselectedColsBuf.charAt(dynselectedColsBuf.length()-1)==',')
        {
            dynselectedColsBuf.deleteCharAt(dynselectedColsBuf.length()-1);
        }
    }

    private void createAllDisplayChildren(List<ColBean> lstCbeans,List lstAllChildren,List lstDynChildren)
    {
        lstAllChildren.addAll(lstDynChildren);
        List<ColBean> lstCbeansNew=new ArrayList<ColBean>();
        for(Object objBeanTmp:lstDynChildren)
        {
            if(objBeanTmp instanceof ColBean)
            {
                lstCbeansNew.add((ColBean)objBeanTmp);
            }else
            {
                ((UltraListReportGroupBean)objBeanTmp).getAllColBeans(lstCbeansNew,null);//将<group/>下的所有ColBean取出放在lstCbeansNew中
            }
        }
        lstCbeans.addAll(lstCbeansNew);
    }

    protected String getMyStatisticConditon(CrossListReportType crossListReportType,String myRealLabelValue)
    {
        if(this.staticondition==null||this.staticondition.trim().equals("")) return "";
        String column=this.getColumn();
        String realvalueTmp=this.realvalue;
        if(realvalueTmp==null||realvalueTmp.trim().equals("")) realvalueTmp=column;
        String myConditionTmp=this.staticondition;
        if(!realvalueTmp.equals(column)) myConditionTmp=Tools.replaceAll(myConditionTmp,column,realvalueTmp);
        if(this.lstStatiConditions!=null&&this.lstStatiConditions.size()>0)
        {
            ReportBean rbean=crossListReportType.getReportBean();
            ReportDataSetValueBean datasetbean=rbean.getSbean().getLstDatasetBeans().get(0).getDatasetValueBeanById(this.getRootCrossColGroupBean().getDatasetid());
            String othercondition=ReportAssistant.getInstance().addDynamicConditionExpressionsToSql(crossListReportType.getReportRequest(),rbean,
                    datasetbean,"{#condition#}",this.lstStatiConditions,null,null);
            if(othercondition!=null&&!othercondition.trim().equals("")&&!othercondition.trim().equals("{#condition#}"))
            {
                myConditionTmp=myConditionTmp+" and "+othercondition;
            }
        }
        return Tools.replaceAll(myConditionTmp,"#data#",myRealLabelValue);
    }

    public void getVerticalStatisticColBeanAndData(CrossListReportType crossListReportType,List<ColBean> lstAllRuntimeColBeans)
    {
        ReportBean rbean=this.getOwner().getReportBean();
        List<ColBean> lstColsTmp=new ArrayList<ColBean>();
        String currentCrossStatiColGroupid=this.getRootCrossColGroupId();
        CrossListReportColBean crcbeanTmp;
        boolean isStartMyDynCol=false;
        for(ColBean cbTmp:lstAllRuntimeColBeans)
        {
            crcbeanTmp=(CrossListReportColBean)cbTmp.getExtendConfigDataForReportType(CrossListReportType.KEY);
            if(crcbeanTmp==null||crcbeanTmp.getBelongToRootOwner()==null
                    ||!currentCrossStatiColGroupid.equals(crcbeanTmp.getBelongToRootOwner().getRootCrossColGroupId()))
            {
                if(isStartMyDynCol) break;
                continue;
            }
            isStartMyDynCol=true;
            lstColsTmp.add(cbTmp);
        }
        if(lstColsTmp.size()==0) return;
        ReportRequest rrequest=crossListReportType.getReportRequest();
        ResultSet rs=crossListReportType.getVerticalStatisticResultSet(this);
        try
        {
            
            AbsDatabaseType dbtype=rrequest.getDbType(this.getDatasetBean().getDatasetbean().getDatasource());
            AbsReportDataPojo pojoData=ReportAssistant.getInstance().getPojoClassInstance(rrequest,rbean,rbean.getPojoClassObj());
            Object objVal;
            for(ColBean cbeanTmp:lstColsTmp)
            {
                if(rs!=null)
                {
                    objVal=cbeanTmp.getDatatypeObj().getColumnValue(rs,cbeanTmp.getColumn(),dbtype);
                }else
                {
                    objVal=null;
                }
                pojoData.setDynamicColData(cbeanTmp.getColumn(),objVal);
            }
            pojoData.format();
            for(ColBean cbeanTmp:lstColsTmp)
            {
                crossListReportType.addVerticalCrossStatisticColData(cbeanTmp,pojoData.getColValue(cbeanTmp),1);
            }
        }catch(SQLException sqle)
        {
            throw new WabacusRuntimeException("获取报表："+rbean.getPath()+"针对每列数据的垂直统计失败",sqle);
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("获取报表："+rbean.getPath()+"针对每列数据的垂直统计时，将统计数据设置到POJO对象中失败",e);
        }
    }

    public void processColGroupRelationStart()
    {
        ReportBean reportbean=this.getOwner().getReportBean();
        if(this.configDynColGroupTitleDataset!=null&&!this.configDynColGroupTitleDataset.trim().equals(""))
        {
            if(parentCrossGroupBean!=null&&parentCrossGroupBean.isDynamicColGroup())
            {
                throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"配置失败，如果父分组列是动态列，则子（分组）列不能再配置dataset，它的数据必须来源于其所在的顶层动态列的dataset");
            }
            this.configDynColGroupTitleDataset=this.configDynColGroupTitleDataset.trim();
            if(this.configDynColGroupTitleDataset.equals(""))
            {
                this.dynColgroupTitleDatasetObj=null;
            }else if(Tools.isDefineKey("class",this.configDynColGroupTitleDataset))
            {
                String configdataset=Tools.getRealKeyByDefine("class",this.configDynColGroupTitleDataset).trim();
                if(configdataset.equals(""))
                {
                    this.dynColgroupTitleDatasetObj=null;
                }else
                {
                    Class c=ConfigLoadManager.currentDynClassLoader.loadClassByCurrentLoader(configdataset);
                    Object obj=null;
                    try
                    {
                        obj=c.newInstance();
                    }catch(Exception e)
                    {
                        throw new WabacusConfigLoadingException("实例化报表"+reportbean.getPath()+"查询动态标题的类"+configdataset+"失败",e);
                    }
                    if(!(obj instanceof IDynamicColGroupDataSet))
                    {
                        throw new WabacusConfigLoadingException("报表"+reportbean.getPath()+"查询动态标题的类"+configdataset+"没有实现"
                                +IDynamicColGroupDataSet.class.getName()+"接口");
                    }
                    this.dynColgroupTitleDatasetObj=(IDynamicColGroupDataSet)obj;
                }

            }else
            {
                if(this.lstDatasetConditions!=null&&this.lstDatasetConditions.size()>0)
                {
                    if(configDynColGroupTitleDataset.toLowerCase().trim().indexOf("select ")!=0)
                    {
                        configDynColGroupTitleDataset="select * from "+configDynColGroupTitleDataset+" where {#condition#}";
                    }else if(configDynColGroupTitleDataset.toLowerCase().trim().indexOf("{#condition#}")<0)
                    {
                        throw new WabacusConfigLoadingException("加载交叉报表"+reportbean.getPath()
                                +"失败，为其配置了<datasetconditions/>，但没有在dataset属性配置的SQL语句中指定{#condition#}动态条件占位符");
                    }
                }
                this.dynColgroupTitleDatasetObj=new SQLCrossListReportDynColGroupLabelDataSet(this.configDynColGroupTitleDataset);
            }
        }

        if(this.isDynamicColGroup())
        {
            String column=this.getColumn();
            if(column==null||column.trim().equals(""))
            {
                throw new WabacusConfigLoadingException("加载报表"+this.getOwner().getReportBean().getPath()+"的列"+this.getLabel(null)
                        +"配置失败，此列是动态获取数据列，必须为其配置column属性，指定从哪个字段获取动态列数据");
            }
            if(this.isRootDynamicColGroup())
            {
                this.getCrossReportBean().setHasDynamicColGroupBean(true);
                if(this.dataheaderformatContent==null||this.dataheaderformatContent.trim().equals(""))
                {
                    this.dataHeaderPojoClass=null;
                }else
                {//创建存放动态表头数据的POJO对象，以便于进行格式化显示
                    this.dataHeaderPojoClass=ReportAssistant.getInstance().buildPOJOClass(reportbean,null,this.lstDataHeaderFormatImports,
                            dataheaderformatContent,"DataHeaderPojo_"+reportbean.getPageBean().getId()+reportbean.getId()+"_"+column);
                    this.lstDataHeaderFormatImports=null;
                    this.dataheaderformatContent=null;
                }
            }
            if(parentCrossGroupBean!=null&&parentCrossGroupBean.isDynamicColGroup())
            {
                String datasetid1=this.datasetid==null?"":this.datasetid.trim();
                String datasetid2=parentCrossGroupBean.getDatasetid()==null?"":parentCrossGroupBean.getDatasetid().trim();
                if(!datasetid1.equals("")&&!datasetid1.equals(datasetid2))
                {
                    throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"的列"+this.getLabel(null)+"分组动态列与子列配置的数据集ID不一致");
                }
            }
        }
    }

    public void processColGroupRelationEnd()
    {
        if(!this.isDynamicColGroup()) return;
        if(this.isRootDynamicColGroup()) getDatasetBean().addCrossColGroupBean(this);
        if(this.isCommonCrossColGroup())
        {
            if(this.staticondition!=null&&!this.staticondition.trim().equals(""))
            {
                log.warn("报表"+this.getOwner().getReportBean().getPath()+"的列"+this.getLabel(null)+"为普通动态列，不需要配置staticondition属性");
            }
        }else if(this.isStatisticCrossColGroup())
        {
            if(this.staticondition==null||this.staticondition.trim().equals(""))
            {
                throw new WabacusConfigLoadingException("加载报表"+this.getOwner().getReportBean().getPath()+"的列"+this.getLabel(null)
                        +"失败，此列为交叉统计列，必须为它配置staticondition属性");
            }
            if(this.getColumn().equals(CrossListReportStatiBean.STATICS_FOR_WHOLEREPORT))
            {
                throw new WabacusConfigLoadingException("加载报表"+this.getOwner().getReportBean().getPath()+"失败，为交叉统计<group/>或<col/>配置column属性不能为"
                        +CrossListReportStatiBean.STATICS_FOR_WHOLEREPORT);
            }
            initConditions(this.lstStatiConditions,true);
        }
        if(this.isRootDynamicColGroup())
        {
            lstDynCols=new ArrayList<Map<String,String>>();
            getDynCols(lstDynCols);
            if(this.dynColgroupTitleDatasetObj instanceof SQLCrossListReportDynColGroupLabelDataSet)
            {
                ((SQLCrossListReportDynColGroupLabelDataSet)this.dynColgroupTitleDatasetObj).initFetchDynColSql();
            }
            initConditions(this.lstDatasetConditions,false);
            if(this.isStatisticCrossColGroup())
            {



//                            +"失败，交叉统计报表的动态<group/>或<col/>的dataset不能配置为JAVA类的方式");

                initStatisDisplayBeanOfRootCrossStatiColGroup();
            }
        }
    }

    protected void getDynCols(List<Map<String,String>> lstDynCols)
    {
        Map<String,String> mTmp=new HashMap<String,String>();
        lstDynCols.add(mTmp);
        if(this.isCommonCrossColGroup())
        {
            mTmp.put("label_column",this.getColumn());
            if(this.realvalue!=null&&!this.realvalue.trim().equals("")) mTmp.put("value_column",this.realvalue);
        }else
        {
            mTmp.put(getColumn(),this.realvalue);
        }
    }

    private void initStatisDisplayBeanOfRootCrossStatiColGroup()
    {
        List<CrossListReportStatiBean> lstStatiBeans=this.getLstStatisBeans();
        List<String> lstStatitemsTmp;
        for(CrossListReportStatiBean statibeanTmp:lstStatiBeans)
        {
            lstStatitemsTmp=statibeanTmp.getLstStatitems();
            if(lstStatitemsTmp!=null)
            {
                lstStatitemsTmp=(List<String>)((ArrayList<String>)lstStatitemsTmp).clone();
                if(lstStatitemsTmp.size()>0&&lstStatitemsTmp.contains(CrossListReportStatiBean.STATICS_FOR_WHOLEREPORT))
                {//此<statistic/>配置有针对整个报表数据的统计
                    this.addStatiDisplayBeanOfReport(createStatisticDisplayBean(statibeanTmp,lstStatitemsTmp,
                            CrossListReportStatiBean.STATICS_FOR_WHOLEREPORT));
                }
            }
            initStatisDisplayBean(statibeanTmp,lstStatitemsTmp);
            if(lstStatitemsTmp!=null&&lstStatitemsTmp.size()>0)
            {//此<statistic/>的statitems属性中还存在没有找到对应分组或列的统计项
                throw new WabacusConfigLoadingException("加载报表"+this.getOwner().getReportBean().getPath()+"失败，id为"+statibeanTmp.getId()
                        +"的<statistic/>在statitems属性中配置的"+lstStatitemsTmp+"没有对应column的<col/>或<group/>");
            }
            statibeanTmp.setLstLabels(null);
            statibeanTmp.setLstLabelstyleproperties(null);
            statibeanTmp.setLstStatitems(null);
            statibeanTmp.setLstValuestyleproperties(null);
        }
        CrossListReportColBean clrcbean=this.getInnerDynamicColBean();
        List<CrossListReportStatiDisplayBean> lstDisplayStatisBeansInDynCol=clrcbean.getLstDisplayStatisBeans();//取到所有针对最里层<col/>进行统计的统计项对象
        if(lstDisplayStatisBeansInDynCol!=null
                &&(lstDisplayStatisBeansInDynCol.size()>1||(lstDisplayStatisBeansInDynCol.size()==1&&(lstDisplayStatisBeansInDynCol.get(0).getLabel()!=null&&!lstDisplayStatisBeansInDynCol
                        .get(0).getLabel().trim().equals("")))))
        {//如果在最里层的<col/>中要显示多个统计，或只显示一个统计，但配置了label，则当前列要显示两行，一行显示<col/>的label，一行显示统计的label
            ColBean cbOwner=(ColBean)clrcbean.getOwner();
            clrcbean.setShouldShowStaticLabel(true);
            String labelstyleproperty=cbOwner.getLabelstyleproperty(null,true);
            labelstyleproperty=Tools.addPropertyValueToStylePropertyIfNotExist(labelstyleproperty,"align","center");
            labelstyleproperty=Tools.addPropertyValueToStylePropertyIfNotExist(labelstyleproperty,"valign","middle");
            cbOwner.setLabelstyleproperty(labelstyleproperty,true);
            String rowspan=Tools.getPropertyValueByName("rowspan",labelstyleproperty,true);
            if(rowspan!=null&&!rowspan.trim().equals(""))
            {
                try
                {
                    clrcbean.setRowspan(Integer.parseInt(rowspan));
                }catch(NumberFormatException e)
                {
                    clrcbean.setRowspan(1);
                }
            }
        }
    }

    protected CrossListReportStatiDisplayBean createStatisticDisplayBean(CrossListReportStatiBean statibean,List<String> lstStatitems,String column)
    {
        CrossListReportStatiDisplayBean cslsdbean=new CrossListReportStatiDisplayBean();
        cslsdbean.setStatiBean(statibean);
        cslsdbean.setLabel(statibean.getLabel(column));
        cslsdbean.setLabelstyleproperty(statibean.getLabelstyleproperty(column));
        cslsdbean.setValuestyleproperty(statibean.getValuestyleproperty(column));
        if(column!=null)
        {
            while(lstStatitems.contains(column))
            {
                lstStatitems.remove(column);
            }
        }
        return cslsdbean;
    }

    private void initConditions(List<ConditionBean> lstConditions,boolean isStatementOnly)
    {
        if(lstConditions==null||lstConditions.size()==0) return;
        ReportBean reportbean=this.getOwner().getReportBean();
        SqlBean sqlbean=reportbean.getSbean();
        for(ConditionBean cbTmp:lstConditions)
        {
            if(Tools.isDefineKey("ref",cbTmp.getName()))
            {//此条件引用了其它<sql/>中配置的条件
                ConditionBean cbRefered=sqlbean.getConditionBeanByName(Tools.getRealKeyByDefine("ref",cbTmp.getName()));
                if(cbRefered==null)
                {
                    throw new WabacusConfigLoadingException("加载报表"+reportbean.getPath()+"失败，其<datasetconditions/>、<staticonditions/>中配置的name为"
                            +cbTmp.getName()+"的查询条件引用的查询条件在<sql/>中不存在");
                }
                cbTmp.setName(cbRefered.getName());
                cbTmp.setConstant(cbRefered.isConstant());
                cbTmp.setDefaultvalue(cbRefered.getDefaultvalue());
                cbTmp.setKeepkeywords(cbRefered.isKeepkeywords());
                cbTmp.setSource(cbRefered.getSource());
                if(cbTmp.getConditionExpression()==null||cbTmp.getConditionExpression().getValue()==null
                        ||cbTmp.getConditionExpression().getValue().trim().equals(""))
                {
                    cbTmp.setConditionExpression(cbRefered.getConditionExpression());
                    
                }else if(sqlbean.getStatementType()==SqlBean.STMTYPE_PREPAREDSTATEMENT&&!isStatementOnly)
                {
                    cbTmp.getConditionExpression().parseConditionExpression();
                }
                if(cbTmp.getConditionExpression()==null||cbTmp.getConditionExpression().getValue()==null
                        ||cbTmp.getConditionExpression().getValue().trim().equals(""))
                {
                    throw new WabacusConfigLoadingException("报表"+reportbean.getPath()+"在<datasetconditions/>、<staticonditions/>中配置的name为"
                            +cbTmp.getName()+"的查询条件引用的查询条件没有配置条件表达式");
                }
            }else if(sqlbean.getStatementType()==SqlBean.STMTYPE_PREPAREDSTATEMENT&&!isStatementOnly)
            {
                cbTmp.getConditionExpression().parseConditionExpression();
            }
        }
    }

    protected abstract void initStatisDisplayBean(CrossListReportStatiBean statibean,List<String> lstStatitems);

    public abstract String getColumn();

    public abstract String getLabel(ReportRequest rrequest);

    public abstract boolean hasDynamicColGroupChild();

    protected abstract List<CrossListReportStatiBean> getLstStatisBeans();

    public abstract CrossListReportColBean getInnerDynamicColBean();

    public abstract boolean getMDynamicColGroupDisplayType(ReportRequest rrequest,Map<String,Boolean> mDynamicColGroupDisplayType);

    public abstract void getRuntimeColGroupBeans(CrossListReportType crossListReportType,List lstAllRuntimeChildren,
            List<ColBean> lstAllRuntimeColBeans,Map<String,Boolean> mDynamicColGroupDisplayType);

    protected abstract void getRealLabelValueFromResultset(ResultSet rs,Map<String,String> mRowData) throws SQLException;

    public AbsExtendConfigBean clone(AbsConfigBean owner)
    {
        AbsCrossListReportColAndGroupBean beanNew=(AbsCrossListReportColAndGroupBean)super.clone(owner);
        beanNew.setLstDatasetConditions(ComponentConfigLoadAssistant.getInstance().cloneLstConditionBeans(null,this.lstDatasetConditions));
        beanNew.setLstStatiConditions(ComponentConfigLoadAssistant.getInstance().cloneLstConditionBeans(null,this.lstStatiConditions));
        //        if(this.dynColgroupTitleDatasetObj!=null)
        
        
        
        return beanNew;
    }

    protected abstract class AbsDynamicColGroupBean
    {
        public abstract boolean isCommonCrossColGroup();

        public abstract boolean isStatisticCrossColGroup();

        public String[] getSelectColumnsAndOrderbyClause(Map<String,String> mOrderbysInDynColGroupDatasetSql)
        {
            StringBuffer selectColumnsBuf=new StringBuffer();
            StringBuffer orderbyClauseBuf=null;
            if(getRootCrossColGroupBean() instanceof CrossListReportGroupBean)
            {
                orderbyClauseBuf=new StringBuffer();
            }
            getSelectColumnsAndOrderbyClause(selectColumnsBuf,orderbyClauseBuf,mOrderbysInDynColGroupDatasetSql);
            if(selectColumnsBuf.charAt(selectColumnsBuf.length()-1)==',') selectColumnsBuf.deleteCharAt(selectColumnsBuf.length()-1);
            if(orderbyClauseBuf!=null&&orderbyClauseBuf.length()>0&&orderbyClauseBuf.charAt(orderbyClauseBuf.length()-1)==',')
                orderbyClauseBuf.deleteCharAt(orderbyClauseBuf.length()-1);
            String orderby="";
            if(orderbyClauseBuf!=null) orderby=orderbyClauseBuf.toString();
            if(!orderby.trim().equals("")) orderby=" order by "+orderby;
            return new String[] { selectColumnsBuf.toString(), orderby };
        }

        public abstract void getSelectColumnsAndOrderbyClause(StringBuffer selectColumnsBuf,StringBuffer orderbyClauseBuf,
                Map<String,String> mOrderbysInDynColGroupDatasetSql);
    }

    protected class CommonCrossColGroupBean extends AbsDynamicColGroupBean
    {
        public boolean isCommonCrossColGroup()
        {
            return true;
        }

        public boolean isStatisticCrossColGroup()
        {
            return false;
        }

        public void getSelectColumnsAndOrderbyClause(StringBuffer selectColumnsBuf,StringBuffer orderbyClauseBuf,
                Map<String,String> mOrderbysInDynColGroupDatasetSql)
        {
            List<Map<String,String>> lstDynCols=getRootCrossColGroupBean().lstDynCols;//不能直接访问lstDynCols，因为此内部类对象是在最里层的动态<col/>中创建的，因此可能不是顶层动态列，而lstDynCols只会存放在顶层动态列中
            String orderbycolTmp, orderbytypeTmp;
            for(Map<String,String> mTmp:lstDynCols)
            {
                selectColumnsBuf.append(mTmp.get("label_column")).append(",");
                String value_column=mTmp.get("value_column");
                if(value_column!=null&&!value_column.trim().equals("")) selectColumnsBuf.append(value_column).append(",");//如果需要获取此动态列的值（动态<group/>等就不需要获取它的数据，所以不用加）
                if(orderbyClauseBuf!=null)
                {
                    orderbycolTmp=mTmp.get("label_column");
                    orderbytypeTmp=mOrderbysInDynColGroupDatasetSql.get(orderbycolTmp.toLowerCase());
                    if(orderbytypeTmp==null) orderbytypeTmp="";
                    orderbyClauseBuf.append(orderbycolTmp).append(" ").append(orderbytypeTmp).append(",");
                }
            }
        }
    }

    protected class CrossStatisticColGroupBean extends AbsDynamicColGroupBean
    {
        public boolean isCommonCrossColGroup()
        {
            return false;
        }

        public boolean isStatisticCrossColGroup()
        {
            return true;
        }

        public void getSelectColumnsAndOrderbyClause(StringBuffer selectColumnsBuf,StringBuffer orderbyClauseBuf,
                Map<String,String> mOrderbysInDynColGroupDatasetSql)
        {
            String columnTmp, realvalueTmp, orderbytypeTmp;
            List<Map<String,String>> lstDynCols=getRootCrossColGroupBean().lstDynCols;//不能直接访问lstDynCols，因为此内部类对象是在最里层的动态<col/>中创建的，因此可能不是顶层动态列，而lstDynCols只会存放在顶层动态列中
            for(Map<String,String> mTmp:lstDynCols)
            {
                columnTmp=mTmp.keySet().iterator().next();
                realvalueTmp=mTmp.get(columnTmp);
                if(realvalueTmp!=null&&!realvalueTmp.trim().equals(""))
                {
                    selectColumnsBuf.append(realvalueTmp).append(" as ");
                }
                selectColumnsBuf.append(columnTmp).append(",");
                if(orderbyClauseBuf!=null)
                {
                    orderbytypeTmp=mOrderbysInDynColGroupDatasetSql.get(columnTmp.toLowerCase());
                    if(orderbytypeTmp==null) orderbytypeTmp="";
                    orderbyClauseBuf.append(columnTmp).append(" ").append(orderbytypeTmp).append(",");
                }
            }
        }
    }

    protected class SQLCrossListReportDynColGroupLabelDataSet implements IDynamicColGroupDataSet
    {
        private String configDataset;

        private String sql_getcols;

        public SQLCrossListReportDynColGroupLabelDataSet(String configDataset)
        {
            this.configDataset=configDataset;
        }

        public void initFetchDynColSql()
        {
            if(configDataset==null||configDataset.trim().equals("")) return;
            String[] selectColsAndOrderbyArr=AbsCrossListReportColAndGroupBean.this.dynColGroupSpecificBean
                    .getSelectColumnsAndOrderbyClause(getMOrderbysFromDatasetSql());
            if(selectColsAndOrderbyArr==null||selectColsAndOrderbyArr.length==0) return;
            if((selectColsAndOrderbyArr[1]==null||selectColsAndOrderbyArr[1].trim().equals(""))
                    &&(configDataset.toLowerCase().trim().indexOf("select ")>=0&&configDataset.toLowerCase().indexOf(" from ")>0))
            {//没有order by，说明交叉统计列是单个<col/>，没有<group/>，且直接配置的是SQL语句，此时不用对SQL语句进行解析，以名丢失了它的order by
                this.sql_getcols=configDataset;
            }else
            {
                StringBuffer getDyncolsSqlBuf=new StringBuffer();
                getDyncolsSqlBuf.append("select distinct ").append(selectColsAndOrderbyArr[0]);
                getDyncolsSqlBuf.append(" from ");
                if(configDataset.toLowerCase().trim().indexOf("select ")>=0&&configDataset.toLowerCase().indexOf(" from ")>0)
                {
                    getDyncolsSqlBuf.append(" (").append(configDataset).append(")  tbl_getdyncol");
                }else
                {
                    getDyncolsSqlBuf.append(configDataset);
                }
                getDyncolsSqlBuf.append(" ").append(selectColsAndOrderbyArr[1]);
                this.sql_getcols=getDyncolsSqlBuf.toString();
            }
        }

        private Map<String,String> getMOrderbysFromDatasetSql()
        {
            String sqlTemp=Tools.removeBracketAndContentInside(this.configDataset,true);
            Map<String,String> mResults=new HashMap<String,String>();
            sqlTemp=Tools.replaceAll(sqlTemp,"  "," ");
            int idx_orderby=sqlTemp.toLowerCase().lastIndexOf("order by");
            if(idx_orderby>0)
            {
                AbsDatabaseType dbtype=Config.getInstance().getDataSource(AbsCrossListReportColAndGroupBean.this.getDatasetBean().getDatasetbean().getDatasource()).getDbType();
                if(dbtype instanceof SQLSERVER2K||dbtype instanceof SQLSERVER2K5)
                {//这两种数据库类型需要把order by子句去掉，否则做为子查询时会报错
                    this.configDataset=Tools.replaceAll(this.configDataset,"  "," ");
                    this.configDataset=this.configDataset.substring(0,this.configDataset.toLowerCase().lastIndexOf("order by"));
                }
                String orderbyTmp=sqlTemp.substring(idx_orderby+"order by ".length());
                List<String> lstOrderByColumns=Tools.parseStringToList(orderbyTmp,",",false);
                String ordercolumn, ordertype;
                for(String orderby_tmp:lstOrderByColumns)
                {
                    if(orderby_tmp==null||orderby_tmp.trim().equals("")) continue;
                    orderby_tmp=orderby_tmp.trim();
                    int idx=orderby_tmp.indexOf(".");
                    if(idx>0) orderby_tmp=orderby_tmp.substring(idx+1).trim();
                    idx=orderby_tmp.indexOf(" ");
                    if(idx>0)
                    {
                        ordercolumn=orderby_tmp.substring(0,idx).trim();
                        ordertype=orderby_tmp.substring(idx+1).trim();
                    }else
                    {
                        ordercolumn=orderby_tmp;
                        ordertype="asc";
                    }
                    mResults.put(ordercolumn.toLowerCase(),ordertype);
                }
            }
            return mResults;
        }

        public List<Map<String,String>> getDynamicColGroupDataSet(CrossListReportType crossListReportTypeObj,ReportDataSetValueBean datasetbean,
                List<ConditionBean> lstDatasetConditions)
        {
            ISqlDataSet datasetObj=null;
            if(datasetbean.getReportBean().getSbean().getStatementType()==SqlBean.STMTYPE_PREPAREDSTATEMENT)
            {
                datasetObj=new GetAllDataSetByPreparedSQL();
            }else
            {
                datasetObj=new GetAllDataSetBySQL();
            }
            ReportBean rbean=crossListReportTypeObj.getReportBean();
            Object objTmp=datasetObj.getDataSet(crossListReportTypeObj.getReportRequest(),rbean,AbsCrossListReportColAndGroupBean.this,
                    this.sql_getcols,lstDatasetConditions,datasetbean.getDatasource());
            List<Map<String,String>> lstResults=null;
            if(objTmp==null) return null;
            if(objTmp instanceof List)
            { 
                lstResults=(List)objTmp;
            }else if(objTmp instanceof ResultSet)
            {
                ResultSet rs=(ResultSet)objTmp;
                try
                {
                    lstResults=new ArrayList<Map<String,String>>();
                    Map<String,String> mRowDataTmp;
                    while(rs.next())
                    {
                        mRowDataTmp=new HashMap<String,String>();
                        getRealLabelValueFromResultset(rs,mRowDataTmp);
                        lstResults.add(mRowDataTmp);
                    }
                }catch(SQLException e)
                {
                    throw new WabacusRuntimeException("从数据库获取报表："+rbean.getPath()+"的动态标题列失败",e);
                }finally
                {
                    try
                    {
                        rs.close();
                    }catch(SQLException e)
                    {
                        e.printStackTrace();
                    }
                }
            }else if(objTmp!=null)
            {
                throw new WabacusRuntimeException("获取报表："+rbean.getPath()+"的动态标题列失败，在查询交叉统计报表的标题时在拦截器中返回无效的数据类型");
            }
            return lstResults;
        }
    }
}
