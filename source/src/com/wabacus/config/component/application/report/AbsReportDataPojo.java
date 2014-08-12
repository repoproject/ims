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
import java.util.Map.Entry;

import com.wabacus.config.Config;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.CacheDataBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.ReportAssistant;
import com.wabacus.system.component.application.report.abstractreport.AbsDetailReportType;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.component.application.report.chart.FusionChartsReportType;
import com.wabacus.util.Tools;

public abstract class AbsReportDataPojo
{
    protected ReportRequest rrequest;

    protected ReportBean rbean;
    
    protected Map<String,Object> mDynamicColData;

    protected String wx_rowdata_key;

    protected String wx_belongto_datasetid;//当前记录所属的<dataset/>的id，只有配置了多个<dataset/>时才会设置它的值

    protected List<AbsReportDataPojo> lstAllDataObjs;
    
    public AbsReportDataPojo(ReportRequest rrequest,ReportBean rbean)
    {
        this.rrequest=rrequest;
        this.rbean=rbean;
        this.wx_rowdata_key=this+Tools.getRandomString(3);
    }

    public ReportRequest getReportRequest()
    {
        return rrequest;
    }

    public ReportBean getReportBean()
    {
        return rbean;
    }

    public void setLstAllDataObjs(List<AbsReportDataPojo> lstAllDataObjs)
    {
        this.lstAllDataObjs=lstAllDataObjs;
    }

    public String getWx_rowdata_key()
    {
        return wx_rowdata_key;
    }

    public String getWx_belongto_datasetid()
    {
        return wx_belongto_datasetid;
    }

    public void setWx_belongto_datasetid(String wx_belongto_datasetid)
    {
        this.wx_belongto_datasetid=wx_belongto_datasetid;
    }

    public Object getDynamicColData(String colname)
    {
        if(mDynamicColData==null) return null;
        return mDynamicColData.get(colname);
    }

    public void setDynamicColData(String colname,Object value)
    {
        if(mDynamicColData==null) mDynamicColData=new HashMap<String,Object>();
        mDynamicColData.put(colname,value);
    }

    public boolean setColValue(ColBean cbean,Object value)
    {
        if(cbean.getProperty()==null||cbean.getProperty().trim().equals("")) return false;
        if(cbean.isNonValueCol()||cbean.isSequenceCol()||cbean.isControlCol()) return false;
        if("[DYN_COL_DATA]".equals(cbean.getProperty()))
        {
            setDynamicColData(cbean.getColumn(),value);
        }else
        {
            try
            {
                cbean.getSetMethod().invoke(this,new Object[] { value });
            }catch(Exception e)
            {
                throw new WabacusRuntimeException("设置报表"+cbean.getReportBean().getPath()+"的列"+cbean.getColumn()+"数据到POJO对象时失败",e);
            }
        }
        return true;
    }

    public boolean setColValue(String property,Object value)
    {
        ColBean cbean=rbean.getDbean().getColBeanByColProperty(property);
        if(cbean==null) return false;
        return setColValue(cbean,value);
    }

    public Object getColValue(ColBean cbean)
    {
        if(cbean.getProperty()==null||cbean.getProperty().trim().equals("")) return null;
        if(cbean.isNonValueCol()||cbean.isSequenceCol()||cbean.isControlCol()) return null;
        if("[DYN_COL_DATA]".equals(cbean.getProperty()))
        {
            return getDynamicColData(cbean.getColumn());
        }else
        {
            try
            {
                return cbean.getGetMethod().invoke(this,new Object[] {});
            }catch(Exception e)
            {
                throw new WabacusRuntimeException("从POJO中获取报表"+cbean.getReportBean().getPath()+"的列"+cbean.getColumn()+"数据失败",e);
            }
        }
    }

    public Object getColValue(String property)
    {
        ColBean cbean=rbean.getDbean().getColBeanByColProperty(property);
        if(cbean==null) return null;
        return getColValue(cbean);
    }

    public String getColStringValue(ColBean cbean)
    {
        Object objValue=getColValue(cbean);
        if(objValue==null) return null;
        if(cbean.getDatatypeObj()==null) return String.valueOf(objValue);
        return cbean.getDatatypeObj().value2label(objValue);
    }

    public String getColStringValue(String property)
    {
        ColBean cbean=rbean.getDbean().getColBeanByColProperty(property);
        if(cbean==null) return null;
        return getColStringValue(cbean);
    }

    public void setRowLabelstyleproperty(String labelstyleproperty,boolean isAppend)
    {
        if(isAppend)
        {
            if(labelstyleproperty==null||labelstyleproperty.equals("")) return;
            String oldlabelstyleproperty=getRowLabelstyleproperty();
            if(oldlabelstyleproperty==null) oldlabelstyleproperty="";
            labelstyleproperty=oldlabelstyleproperty+" "+labelstyleproperty;
        }
        rrequest.getCdb(rbean.getId()).setRowLabelstyleproperty(labelstyleproperty);
    }
    
    public String getRowLabelstyleproperty()
    {
        String labelstyleproperty=rrequest.getCdb(rbean.getId()).getDynRowLabelstyleproperty();
        if(labelstyleproperty==null) labelstyleproperty=rbean.getDbean().getLabelstyleproperty(rrequest,false);
        return labelstyleproperty;
    }

    public void setRowValuestyleproperty(String valuestyleproperty,boolean isAppend)
    {
        if(isAppend)
        {
            if(valuestyleproperty==null||valuestyleproperty.equals("")) return;
            String oldvaluestyleproperty=getRowValuestyleproperty();
            if(oldvaluestyleproperty==null) oldvaluestyleproperty="";
            valuestyleproperty=oldvaluestyleproperty+" "+valuestyleproperty;
        }
        rrequest.getCdb(rbean.getId()).setRowValuestyleproperty(this.wx_rowdata_key,valuestyleproperty);
    }    

    public String getRowValuestyleproperty()
    {
        String rowvaluestyleproperty=rrequest.getCdb(rbean.getId()).getDynRowValuestyleproperty(this.wx_rowdata_key);
        if(rowvaluestyleproperty==null) rowvaluestyleproperty=rbean.getDbean().getValuestyleproperty(rrequest,false);
        return rowvaluestyleproperty;
    }

    public void setRowValuestyleproperty(String colproperty,String valuestyleproperty,boolean isAppend)
    {
        if(isAppend)
        {
            if(valuestyleproperty==null||valuestyleproperty.equals("")) return;
            String oldvaluestyleproperty=getRowValuestyleproperty(colproperty);
            if(oldvaluestyleproperty==null) oldvaluestyleproperty="";
            valuestyleproperty=oldvaluestyleproperty+" "+valuestyleproperty;
        }
        colproperty=ReportAssistant.getInstance().getHorizontalRowValueStylepropertyKey(this.wx_belongto_datasetid,colproperty,
                rbean.getSbean().isMultiDatasetRows());
        rrequest.getCdb(rbean.getId()).setRowValuestyleproperty(colproperty,valuestyleproperty);
    }
    
    public String getRowValuestyleproperty(String colproperty)
    {
        colproperty=ReportAssistant.getInstance().getHorizontalRowValueStylepropertyKey(this.wx_belongto_datasetid,colproperty,
                rbean.getSbean().isMultiDatasetRows());
        String rowvaluestyleproperty=rrequest.getCdb(rbean.getId()).getDynRowValuestyleproperty(colproperty);
        if(rowvaluestyleproperty==null) rowvaluestyleproperty=rbean.getDbean().getValuestyleproperty(rrequest,false);
        return rowvaluestyleproperty;
    }
    
    public void setColLabelstyleproperty(String colproperty,String labelstyleproperty,boolean isAppend)
    {
        if(isAppend)
        {
            if(labelstyleproperty==null||labelstyleproperty.equals("")) return;
            String oldlabelstyleproperty=getColLabelstyleproperty(colproperty);
            if(oldlabelstyleproperty==null) oldlabelstyleproperty="";
            labelstyleproperty=oldlabelstyleproperty+" "+labelstyleproperty;
        }
        if(rrequest.getDisplayReportTypeObj(rbean) instanceof AbsDetailReportType)
        {
            colproperty=this.wx_rowdata_key+"_col_"+colproperty;
        }
        rrequest.getCdb(rbean.getId()).setColLabelstyleproperty(colproperty,labelstyleproperty);
    }
    
    public String getColLabelstyleproperty(String colproperty)
    {
        String key=colproperty;
        if(rrequest.getDisplayReportTypeObj(rbean) instanceof AbsDetailReportType)
        {
            key=this.wx_rowdata_key+"_col_"+colproperty;
        }
        String labelstyleproperty=rrequest.getCdb(rbean.getId()).getDynColLabelstyleproperty(key);
        if(labelstyleproperty==null)
        {
            ColBean cbTmp=getColBeanByColProperty(colproperty);
            if(cbTmp==null) return "";
            labelstyleproperty=cbTmp.getLabelstyleproperty(rrequest,false);
        }
        return labelstyleproperty;
    }

    public void setColValuestyleproperty(String colproperty,String valuestyleproperty,boolean isAppend)
    {
        if(isAppend)
        {
            if(valuestyleproperty==null||valuestyleproperty.equals("")) return;
            String oldvaluestyleproperty=getColValuestyleproperty(colproperty);
            if(oldvaluestyleproperty==null) oldvaluestyleproperty="";
            valuestyleproperty=oldvaluestyleproperty+" "+valuestyleproperty;
        }
        rrequest.getCdb(rbean.getId()).setColValuestyleproperty(this.wx_rowdata_key+"_col_"+colproperty,valuestyleproperty);
    }
    
    public String getColValuestyleproperty(String colproperty)
    {
        String valuestyleproperty=rrequest.getCdb(rbean.getId()).getDynColValuestyleproperty(this.wx_rowdata_key+"_col_"+colproperty);
        if(valuestyleproperty==null)
        {
            ColBean cbTmp=getColBeanByColProperty(colproperty);
            if(cbTmp==null) return "";
            valuestyleproperty=cbTmp.getValuestyleproperty(rrequest,false);
        }
        return valuestyleproperty;
    }

    public boolean insertBefore(Map<String,Object> mColValues,boolean updateNavigateInfo)
    {
        AbsReportDataPojo dataObjNew=createNewPojoInstance(rrequest,rbean,mColValues);
        int myidx=getPositionIndex();
        if(myidx==-1) return false;
        this.lstAllDataObjs.add(myidx,dataObjNew);
        if(updateNavigateInfo) updateNavigateInfo(rrequest,rbean,1);
        return true;
    }

    public boolean append(Map<String,Object> mColValues,boolean updateNavigateInfo)
    {
        AbsReportDataPojo dataObjNew=createNewPojoInstance(rrequest,rbean,mColValues);
        int myidx=getPositionIndex();
        if(myidx==-1) return false;
        if(myidx>=this.lstAllDataObjs.size()-1)
        {
            this.lstAllDataObjs.add(dataObjNew);
        }else
        {
            this.lstAllDataObjs.add(myidx+1,dataObjNew);
        }
        if(updateNavigateInfo) updateNavigateInfo(rrequest,rbean,1);
        return true;
    }
    
    public boolean delete(boolean updateNavigateInfo)
    {
        int myidx=getPositionIndex();
        if(myidx==-1) return false;
        this.lstAllDataObjs.remove(myidx);
        if(updateNavigateInfo) updateNavigateInfo(rrequest,rbean,-1);
        return true;
    }
    
    public int getPositionIndex()
    {
        if(this.lstAllDataObjs==null||this.lstAllDataObjs.size()==0) return -1;
        AbsReportDataPojo dataObjTmp;
        for(int myidx=0;myidx<this.lstAllDataObjs.size();myidx++)
        {
            dataObjTmp=this.lstAllDataObjs.get(myidx);
            if(dataObjTmp.getWx_rowdata_key().equals(this.wx_rowdata_key)) return myidx;
        }
        return -1;
    }
    
    public boolean isFirst()
    {
        return getPositionIndex()==0;
    }
    
    public boolean isLast()
    {
        int myidx=getPositionIndex();
        return myidx>0&&myidx==this.lstAllDataObjs.size()-1;
    }
    
    public void clear(boolean updateNavigateInfo)
    {
        if(this.lstAllDataObjs!=null)
        {
            if(updateNavigateInfo) updateNavigateInfo(rrequest,rbean,0-this.lstAllDataObjs.size());
            this.lstAllDataObjs.clear();
        }
    }
    
    public static void addRowDataObj(ReportRequest rrequest,ReportBean rbean,Map<String,Object> mColValues,boolean updateNavigateInfo)
    {
        AbsReportDataPojo dataObjNew=createNewPojoInstance(rrequest,rbean,mColValues);
        List<AbsReportDataPojo> lstReportData=rrequest.getDisplayReportTypeObj(rbean).getLstReportData();
        if(lstReportData==null)
        {
            lstReportData=new ArrayList<AbsReportDataPojo>();
            rrequest.getDisplayReportTypeObj(rbean).setLstReportData(lstReportData);
        }
        lstReportData.add(dataObjNew);
        if(updateNavigateInfo) updateNavigateInfo(rrequest,rbean,1);
    }
    
    public static AbsReportDataPojo createNewPojoInstance(ReportRequest rrequest,ReportBean rbean,Map<String,Object> mColValues)
    {
        AbsReportDataPojo dataObjNew=ReportAssistant.getInstance().getPojoClassInstance(rrequest,rbean,rbean.getPojoClassObj());
        if(mColValues!=null&&mColValues.size()>0)
        {
            CacheDataBean cdb=rrequest.getCdb(rbean.getId());
            String propertyTmp;
            Object valueTmp;
            for(Entry<String,Object> entryTmp:mColValues.entrySet())
            {
                propertyTmp=entryTmp.getKey();
                valueTmp=entryTmp.getValue();
                if(valueTmp==null) continue;
                ColBean cbTmp=rbean.getDbean().getColBeanByColProperty(propertyTmp);
                if(cbTmp!=null)
                {
                    dataObjNew.setColValue(cbTmp,valueTmp);
                }else
                {
                    cbTmp=cdb.getDynamicColBeanByColumn(propertyTmp);
                    if(cbTmp!=null)
                    {
                        dataObjNew.setDynamicColData(propertyTmp,valueTmp);
                    }else
                    {
                        throw new WabacusRuntimeException("为报表"+rbean.getPath()+"新增记录行失败，没有找到property/column为"+propertyTmp+"的<col/>");
                    }
                }
            }
        }
        return dataObjNew;
    }
    
    public static List<AbsReportDataPojo> getLstReportData(ReportRequest rrequest,ReportBean rbean)
    {
        return rrequest.getDisplayReportTypeObj(rbean).getLstReportData();
    }
    
    private static void updateNavigateInfo(ReportRequest rrequest,ReportBean rbean,int deltaRecordcount)
    {
        CacheDataBean cdb=rrequest.getCdb(rbean.getId());
        cdb.setRecordcount(cdb.getRecordcount()+deltaRecordcount);
        if(!cdb.isLoadAllReportData()&&cdb.getRecordcount()<=0)
        {
            cdb.setPagecount(0);
        }
    }
    
    private ColBean getColBeanByColProperty(String colproperty)
    {
        ColBean cbTmp;
        if(this.mDynamicColData!=null&&this.mDynamicColData.containsKey(colproperty))
        {
            cbTmp=rrequest.getCdb(rbean.getId()).getDynamicColBeanByColumn(colproperty);
        }else
        {
            cbTmp=rbean.getDbean().getColBeanByColProperty(colproperty);
        }
        return cbTmp;
    }
    
    public String getLinkChartUrl(String linktype,String linkid,String linkreportid,String conditionValues)
    {
        linktype=linktype==null?"":linktype.trim().toLowerCase();
        if(linktype.equals("")) return "";
        AbsReportType reportTypeObj=rrequest.getDisplayReportTypeObj(rbean);
        if(!(reportTypeObj instanceof FusionChartsReportType))
        {
            throw new WabacusRuntimeException("报表"+rbean.getPath()+"不是fusioncharts报表类型，不能调用此方法得到图表跳转链接");
        }
        String resultStr=null;
        if("xmlurl".equals(linktype))
        {
            String token=Config.showreport_url.indexOf("?")>0?"&":"?";
            StringBuffer urlBuf=new StringBuffer();
            urlBuf.append(token+"ACTIONTYPE=getChartDataString");
            urlBuf.append("&PAGEID="+linkid);
            urlBuf.append("&REPORTID="+linkreportid);
            if(conditionValues!=null&&!conditionValues.trim().equals(""))
            {//指定了查询条件参数
                List<String> lstConVals=Tools.parseStringToList(conditionValues,"&",false);
                for(String convalTmp:lstConVals)
                {
                    int idx=convalTmp.indexOf("=");
                    if(idx<=0) continue;
                    urlBuf.append("&"+convalTmp.substring(0,idx).trim()).append("="+convalTmp.substring(idx+1).trim());
                }
            }
            resultStr="newchart-xmlurl-"+urlBuf.toString();
        }else if("xml".equals(linktype))
        {
            Object childComBean=rrequest.getPagebean().getChildComponentBean(linkreportid,true);
            if(childComBean==null||!(childComBean instanceof ReportBean))
            {
                throw new WabacusRuntimeException("报表"+rbean.getPath()+"链接的组件ID："+linkreportid+"不存在或者不是报表");
            }
            if(conditionValues!=null&&!conditionValues.trim().equals(""))
            {
                List<String> lstConVals=Tools.parseStringToList(conditionValues,"&",false);
                for(String convalTmp:lstConVals)
                {
                    int idx=convalTmp.indexOf("=");
                    if(idx<=0) continue;
                    rrequest.setAttribute(convalTmp.substring(0,idx).trim(),convalTmp.substring(idx+1).trim());
                }
            }
            AbsReportType linkedReportTypeObj=(AbsReportType)rrequest.getComponentTypeObj(linkreportid,reportTypeObj.getParentContainerType(),false);
            if(linkedReportTypeObj==null)
            {
                linkedReportTypeObj=(AbsReportType)rrequest.getComponentTypeObj(linkreportid,reportTypeObj.getParentContainerType(),true);
                linkedReportTypeObj.init();
            }
            if(!(linkedReportTypeObj instanceof FusionChartsReportType))
            {
                throw new WabacusRuntimeException("报表"+rbean.getPath()+"链接的报表"+linkreportid+"不是fusioncharts报表类型，不能调用此方法链接到它");
            }
            linkedReportTypeObj.setHasLoadedDataFlag(false);
            linkedReportTypeObj.loadReportData(true);
            ((FusionChartsReportType)reportTypeObj).setLinkedChartData(linkid,((FusionChartsReportType)linkedReportTypeObj).loadStringChartData(true));
            resultStr="newchart-xml-"+linkid;
        }else
        {
            throw new WabacusRuntimeException("获取图表报表"+rbean.getPath()+"的链接URL时，传入的linktype："+linktype+"无效");
        }
        return resultStr;
    }
    
    public void format()
    {}
}
