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
package com.wabacus.system.assistant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.system.CacheDataBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.commoninterface.IListReportRoworderPersistence;
import com.wabacus.system.component.application.report.CrossListReportType;
import com.wabacus.system.component.application.report.abstractreport.AbsListReportType;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportColBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportDisplayBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportFilterBean;
import com.wabacus.system.component.application.report.configbean.UltraListReportGroupBean;
import com.wabacus.system.component.application.report.configbean.crosslist.AbsCrossListReportColAndGroupBean;
import com.wabacus.system.component.application.report.configbean.crosslist.CrossListReportColBean;
import com.wabacus.system.component.application.report.configbean.crosslist.CrossListReportGroupBean;
import com.wabacus.system.datatype.AbsDateTimeType;
import com.wabacus.system.datatype.VarcharType;
import com.wabacus.util.Consts;
import com.wabacus.util.Consts_Private;
import com.wabacus.util.Tools;

public class ListReportAssistant
{
    private final static ListReportAssistant instance=new ListReportAssistant();

    private static Log log=LogFactory.getLog(ListReportAssistant.class);
    
    protected ListReportAssistant()
    {};

    public static ListReportAssistant getInstance()
    {
        return instance;
    }
    
    public  String[] calColPosition(ReportRequest rrequest,AbsListReportDisplayBean alrdbean,
            List<ColBean> lstColBeans,List<String> lstDisplayColids)
    {
        String firstDisplayColid=null;
        String lastDisplayColid=null;
        int totalDisplayColCount=0;
        for(ColBean cbTmp:lstColBeans)
        {
            if(cbTmp.getDisplaymode(rrequest,lstDisplayColids)<=0) continue;
            if(firstDisplayColid==null)
            {
                firstDisplayColid=cbTmp.getColid();
            }
            lastDisplayColid=cbTmp.getColid();
            totalDisplayColCount++;
        }
        if(alrdbean.getRowGroupColsNum()>0&&alrdbean.getRowgrouptype()==2)
        {
            totalDisplayColCount=totalDisplayColCount-alrdbean.getRowGroupColsNum()+1;
        }
        return new String[] { firstDisplayColid, lastDisplayColid,
                String.valueOf(totalDisplayColCount) };
    }
    
    public String getColLabelWithOrderBy(ColBean cbean,ReportRequest rrequest,String dynlabel)
    {
        String ordercolumn=cbean.getColumn();
        String label=rrequest.getI18NStringValue(dynlabel);
        label=label==null?"":label.trim();
        if(rrequest.getShowtype()!=Consts.DISPLAY_ON_PAGE) return label;
        if(ordercolumn==null||ordercolumn.trim().equals("")) return label;
        String[] orderbys=(String[])rrequest.getAttribute(cbean.getReportBean().getId(),
                "ORDERBYARRAY");
        String arrow="";
        String order="asc";
        if(orderbys!=null&&orderbys.length==2)
        {
            if(orderbys[0].equalsIgnoreCase(ordercolumn))
            {
                arrow=" <img src='"+Config.webroot+"/webresources/skin/"+rrequest.getPageskin()+"/images/";
                if(orderbys[1]==null||orderbys[1].equalsIgnoreCase("desc"))
                {
                    arrow=arrow+"desc.gif'>";
                    order="asc";
                }else
                {
                    arrow=arrow+"asc.gif'>";
                    order="desc";
                }
            }
        }
        arrow=Tools.replaceAll(arrow,"//","/");
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append("<span onmouseover=\"this.style.cursor='pointer';\" onclick=\"");
        resultBuf.append("try{clickorderby(this,'").append(ordercolumn).append("||").append(order).append("');}catch(e){logErrorsAsJsFileLoad(e);}\">");
        resultBuf.append(label).append(arrow);
        resultBuf.append("</span>");
        return resultBuf.toString();
    }
    
    public ColBean getClickOrderByCbean(ReportRequest rrequest,ReportBean rbean)
    {
        String[] orderbys=(String[])rrequest.getAttribute(rbean.getId(),"ORDERBYARRAY");
        if(orderbys==null||orderbys.length!=2) return null;
        return rbean.getDbean().getColBeanByColColumn(orderbys[0]);
    }
    
    public String createColumnFilter(ReportRequest rrequest,AbsListReportColBean alrcbean)
    {
        ReportBean rbean=alrcbean.getOwner().getReportBean();
        StringBuffer resultBuf=new StringBuffer();
        AbsListReportFilterBean filterbean=alrcbean.getFilterBean();
        StringBuffer paramsBuf=new StringBuffer();
        paramsBuf.append("{reportguid:\"").append(rbean.getGuid()).append("\"");
        paramsBuf.append(",property:\"").append(((ColBean)alrcbean.getOwner()).getProperty()).append("\"");
        paramsBuf.append(",webroot:\"").append(Config.webroot).append("\"");
        paramsBuf.append(",skin:\"").append(rrequest.getPageskin()).append("\"");
        CacheDataBean cdb=rrequest.getCdb(rbean.getId());
        
        String imgurl="/webresources/skin/"+rrequest.getPageskin()+"/images/";
        paramsBuf.append(",urlParamName:\"");
        if(filterbean.isConditionRelate())
        {
            paramsBuf.append(filterbean.getConditionname()).append("\"");
            paramsBuf.append(",multiply:\"false\"");
            String filtervalue=rrequest.getStringAttribute(filterbean.getConditionname(),"");
            if(!filtervalue.equals(""))
            {
                imgurl=imgurl+"filter2.jpg";
            }else
            {
                imgurl=imgurl+"filter1.jpg";
            }
        }else
        {
            paramsBuf.append(filterbean.getId()).append("\"");
            paramsBuf.append(",multiply:\"true\"");
            String filtervalue=rrequest.getStringAttribute(filterbean.getId(),"");
            if(cdb.getFilteredBean()==null||filtervalue.trim().equals("")||!filterbean.equals(cdb.getFilteredBean()))
            {
                imgurl=imgurl+"filter1.jpg";
            }else
            {
                imgurl=imgurl+"filter2.jpg";
            }
        }
        imgurl=Tools.replaceAll(Config.webroot+imgurl,"//","/");//过滤图片路径
        
        paramsBuf.append(",filterwidth:").append(filterbean.getFilterwidth());
        paramsBuf.append(",filtermaxheight:").append(filterbean.getFiltermaxheight());
        paramsBuf.append("}");
        resultBuf.append("<SPAN class=\"filter_span\"><input type=\"button\" id=\"").append(rbean.getGuid()+alrcbean.hashCode()).append("\"");
        resultBuf.append(" onmouseout=\"try{drag_enabled=true;}catch(e){}\" onmouseover=\"try{drag_enabled=false;this.style.cursor='pointer';}catch(e){}\"");
        resultBuf.append(" style=\"width:16px;height:17px;background-color:transparent;border:0; background-image: url(").append(imgurl+");\"");
        resultBuf.append(" onclick=\"try{getFilterDataList(this,'").append(Tools.jsParamEncode(paramsBuf.toString())).append("');return false;}catch(e){logErrorsAsJsFileLoad(e);}\"");
        resultBuf.append("/></SPAN>");
        return resultBuf.toString();
    }
    
    public String appendCellResizeFunction(boolean type)
    {
        return "<span class=\"resize_span\"  onmouseover=\"try{drag_enabled=false;if(!this.isInit) initdrag(this,"
                +type
                +");}catch(e){logErrorsAsJsFileLoad(e);}\" onmouseout=\"try{drag_enabled=true;}catch(e){}\"><font width=\"3\">&nbsp;</font></span>";
    }
    
    public String addColFilterConditionToSql(ReportRequest rrequest,ReportBean rbean,ReportDataSetValueBean datasetbean,String sql)
    {
        String where=getFilterConditionExpression(rrequest,rbean,datasetbean);
        if(where==null) where="";
        sql=Tools.replaceAll(sql,Consts_Private.PLACEHODER_FILTERCONDITION,where);
        return sql;
    }

    public String getFilterConditionExpression(ReportRequest rrequest,ReportBean rbean,ReportDataSetValueBean datasetbean)
    {
        AbsListReportFilterBean filterBean=rrequest.getCdb(rbean.getId()).getFilteredBean();
        if(filterBean==null) return null;
        ColBean cbTmp=(ColBean)filterBean.getOwner();
        if(!cbTmp.isMatchDataSet(datasetbean)) return null;
        String filterval=rrequest.getStringAttribute(filterBean.getId(),"");
        if(filterval.equals("")) return null;
        if(cbTmp.getDatatypeObj()==null||cbTmp.getDatatypeObj() instanceof VarcharType||cbTmp.getDatatypeObj() instanceof AbsDateTimeType)
        {
            filterval=Tools.replaceAll(filterval,";;","','");
            if(!filterval.startsWith("'")) filterval="'"+filterval;
            if(filterval.endsWith("','")) filterval=filterval.substring(0,filterval.length()-3);
            if(!filterval.endsWith("'")) filterval=filterval+"'";
            if(filterval.equals("'")) filterval="";
        }else
        {
            filterval=Tools.replaceAll(filterval,";;",",");
            if(filterval.endsWith(",")) filterval=filterval.substring(0,filterval.length()-1);
        }
        String where=null;
        String column=null;
        if(filterBean.getFilterColumnExpression()!=null&&!filterBean.getFilterColumnExpression().trim().equals(""))
        {
            column=filterBean.getFilterColumnExpression();
        }else
        {
            column=cbTmp.getColumn();
        }
        if(!filterval.trim().equals(""))
        {
            where=" where "+column+" in ("+filterval+") ";
        }
        return where;
    }
    
    public void storeRoworder(ReportRequest rrequest,ReportBean rbean)
    {
        String rowordertype=rrequest.getStringAttribute(rbean.getId()+"_ROWORDERTYPE","");
        if(rowordertype.equals("")||!Consts.lstAllRoworderTypes.contains(rowordertype)) return;
        
        String roworderparams=rrequest.getStringAttribute(rbean.getId()+"_ROWORDERPARAMS","");
        if(roworderparams.equals("")) return;
        AbsListReportBean alrbean=(AbsListReportBean)rbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
        IListReportRoworderPersistence lsObj=alrbean.getLoadStoreRoworderObject();//获取到读写行排序顺序值的类对象
        if(lsObj==null) lsObj=Config.default_roworder_object;
        List<Map<String,String>> lstColValusInAllRows=EditableReportAssistant.getInstance().parseSaveDataStringToList(roworderparams);
        if(lstColValusInAllRows.size()==0) return;
        Map<String,String> mColValuesInRow=lstColValusInAllRows.get(0);
        log.debug("被排序记录行参数："+mColValuesInRow);
        if(rowordertype.equals(Consts.ROWORDER_INPUTBOX))
        {
            String newrowordervalue=rrequest.getStringAttribute(rbean.getId()+"_ROWORDERVALUE","");
            lsObj.storeRoworderByInputbox(rrequest,rbean,mColValuesInRow,newrowordervalue);
        }else if(rowordertype.equals(Consts.ROWORDER_TOP))
        {
            lsObj.storeRoworderByTop(rrequest,rbean,mColValuesInRow);
        }else
        {
            String direct=rrequest.getStringAttribute(rbean.getId()+"_ROWORDERDIRECT","");
            String destrowParams=rrequest.getStringAttribute(rbean.getId()+"_DESTROWPARAMS","");
            Map<String,String> mColValuesInDestRow=null;
            if(!destrowParams.trim().equals(""))
            {
                mColValuesInDestRow=EditableReportAssistant.getInstance().parseSaveDataStringToList(destrowParams).get(0);
                log.debug("目标位置记录行参数："+mColValuesInDestRow);
            }
            if(rowordertype.equals(Consts.ROWORDER_DRAG))
            {
                lsObj.storeRoworderByDrag(rrequest,rbean,mColValuesInRow,mColValuesInDestRow,direct.toLowerCase().trim().equals("true"));
            }else
            {
                lsObj.storeRoworderByArrow(rrequest,rbean,mColValuesInRow,mColValuesInDestRow,direct.toLowerCase().trim().equals("true"));
            }
        }
    }
    
    public void addMDataFieldToClass(ClassPool pool,CtClass cclass) throws CannotCompileException,NotFoundException
    {
        CtField cfield=new CtField(pool.get("java.util.Map"),"mDynamicColData",cclass);
        cfield.setModifiers(Modifier.PRIVATE);
        cclass.addField(cfield);
        
        
        
        

        StringBuffer methodBuf=new StringBuffer();
        methodBuf.append("public Object getDynamicColData(String colname)");
        methodBuf.append("{if(mDynamicColData==null) return null;");
        methodBuf.append("return mDynamicColData.get(colname);}");
        CtMethod getMDataMethod=CtNewMethod.make(methodBuf.toString(),cclass);
        cclass.addMethod(getMDataMethod);

        methodBuf=new StringBuffer();
        methodBuf.append("public void setDynamicColData(String colname,Object value)");
        methodBuf.append("{if(mDynamicColData==null) mDynamicColData=new HashMap();");
        methodBuf.append("mDynamicColData.put(colname,value);}");
        CtMethod setMDataMethod=CtNewMethod.make(methodBuf.toString(),cclass);
        cclass.addMethod(setMDataMethod);
    }
    
    public AbsCrossListReportColAndGroupBean getCrossColAndGroupBean(Object colGroupBean)
    {
        if(colGroupBean instanceof ColBean)
        {
            return (CrossListReportColBean)((ColBean)colGroupBean).getExtendConfigDataForReportType(CrossListReportType.KEY);
        }else if(colGroupBean instanceof UltraListReportGroupBean)
        {
            return (CrossListReportGroupBean)((UltraListReportGroupBean)colGroupBean).getExtendConfigDataForReportType(CrossListReportType.KEY);
        }
        return null;
    }
}

