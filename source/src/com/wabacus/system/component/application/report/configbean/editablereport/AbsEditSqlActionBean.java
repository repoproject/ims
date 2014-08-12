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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.database.type.AbsDatabaseType;
import com.wabacus.config.database.type.Oracle;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.EditableReportAssistant;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.system.buttons.EditableReportSQLButtonDataBean;
import com.wabacus.system.datatype.BlobType;
import com.wabacus.system.datatype.ClobType;
import com.wabacus.util.Tools;
import com.wabacus.util.UUIDGenerator;

public abstract class AbsEditSqlActionBean extends AbsEditActionBean
{
    private static Log log=LogFactory.getLog(AbsEditSqlActionBean.class);

    protected String sql;

    protected List<EditableReportParamBean> lstParamBeans;

    protected String returnValueParamname;//用于保存此SQL语句或存储过程返回值的变量名，可以是定义在<params/>的变量的name属性或rrequset的key
    
    public AbsEditSqlActionBean(EditActionGroupBean ownerGroupBean)
    {
        super(ownerGroupBean);
    }

    public String getSql()
    {
        return sql;
    }

    public void setSql(String sql)
    {
        this.sql=sql;
    }

    public List<EditableReportParamBean> getLstParamBeans()
    {
        return lstParamBeans;
    }

    public void setLstParamBeans(List<EditableReportParamBean> lstParamBeans)
    {
        this.lstParamBeans=lstParamBeans;
    }

    public String getReturnValueParamname()
    {
        return returnValueParamname;
    }

    public void setReturnValueParamname(String returnValueParamname)
    {
        this.returnValueParamname=returnValueParamname;
    }

    protected String parseAndRemoveReturnParamname(String configsql)
    {
        if(configsql==null||configsql.trim().equals("")) return configsql;
        int idx=configsql.indexOf("=");
        if(idx<0) return configsql;
        String returnValName=configsql.substring(0,idx).trim();
        if(Tools.isDefineKey("#",returnValName)||Tools.isDefineKey("rrequest",returnValName))
        {
            this.returnValueParamname=returnValName;
            configsql=configsql.substring(idx+1);
        }
        return configsql;
    }
    
    public Object createEditParams(String paramname,String reportTypeKey)
    {
        if(paramname==null) return null;
        Object objResult=null;
        EditableReportParamBean paramBean=new EditableReportParamBean();
        if(Tools.isDefineKey("sequence",paramname))
        {
            objResult=Config.getInstance().getDataSource(this.ownerGroupBean.getDatasource()).getDbType().getSequenceValueByName(
                    Tools.getRealKeyByDefine("sequence",paramname));
        }else if(Tools.isDefineKey("#",paramname))
        {//是从<params/>中定义的变量中取值
            paramname=Tools.getRealKeyByDefine("#",paramname);
            EditableReportExternalValueBean editparamsbean=this.ownerGroupBean.getOwnerUpdateBean().getExternalValueBeanByName(paramname,true);
            paramBean.setParamname(paramname);
            paramBean.setOwner(editparamsbean);
            objResult=paramBean;
        }else if(Tools.isDefineKey("@",paramname))
        {
            if(this.ownerGroupBean.getOwnerUpdateBean().isAutoReportdata())
            {
                objResult=createParamBeanByColbean(Tools.getRealKeyByDefine("@",paramname),reportTypeKey,true,true);
            }else
            {
                ((EditableReportSQLButtonDataBean)this.getOwnerGroupBean().getOwnerUpdateBean()).setHasReportDataParams(true);
                paramBean.setParamname(paramname);
                objResult=paramBean;
            }
        }else if(WabacusAssistant.getInstance().isGetRequestContextValue(paramname)
                ||Tools.isDefineKey("!",paramname)||paramname.equals("uuid{}")||Tools.isDefineKey("increment",paramname))
        {
            paramBean.setParamname(paramname);
            objResult=paramBean;
            if(Tools.isDefineKey("url",paramname))
            {
                this.getOwnerGroupBean().getOwnerUpdateBean().getOwner().getReportBean().addParamNameFromURL(
                        Tools.getRealKeyByDefine("url",paramname));
            }
        }else
        {
            objResult=paramname;
        }
        return objResult;
    }
    
    
    public void updateData(ReportRequest rrequest,ReportBean rbean,Map<String,String> mRowData,
            Map<String,String> mParamValues) throws SQLException
    {
        AbsDatabaseType dbtype=rrequest.getDbType(this.ownerGroupBean.getDatasource());
        Connection conn=rrequest.getConnection(this.ownerGroupBean.getDatasource());
        Oracle oracleType=null;
        PreparedStatement pstmt=null;
        try
        {
            if(Config.show_sql) log.info("Execute sql:"+sql);
            pstmt=conn.prepareStatement(sql);
            if(sql.trim().toLowerCase().startsWith("select ")&&(dbtype instanceof Oracle))
            {
                oracleType=(Oracle)dbtype;
                if(lstParamBeans!=null&&lstParamBeans.size()>0)
                {
                    int colidx=1;
                    for(EditableReportParamBean paramBean:lstParamBeans)
                    {
                        if((paramBean.getDataTypeObj() instanceof ClobType)||(paramBean.getDataTypeObj() instanceof BlobType)) continue;
                        paramBean.getDataTypeObj().setPreparedStatementValue(colidx++,
                                getParamValue(mRowData,mParamValues,rbean,rrequest,paramBean),pstmt,dbtype);
                    }
                }
                ResultSet rs=pstmt.executeQuery();
                while(rs.next())
                {
                    if(lstParamBeans!=null&&lstParamBeans.size()>0)
                    {
                        int colidx=1;
                        for(EditableReportParamBean paramBean:lstParamBeans)
                        {
                            if(!(paramBean.getDataTypeObj() instanceof ClobType)&&!(paramBean.getDataTypeObj() instanceof BlobType)) continue;
                            String paramvalue=getParamValue(mRowData,mParamValues,rbean,rrequest,paramBean);
                            if(paramBean.getDataTypeObj() instanceof ClobType)
                            {
                                oracleType.setClobValueInSelectMode(paramvalue,(oracle.sql.CLOB)rs.getClob(colidx++));
                            }else
                            {
                                oracleType.setBlobValueInSelectMode(paramBean.getDataTypeObj().label2value(paramvalue),(oracle.sql.BLOB)rs
                                        .getBlob(colidx++));
                            }
                        }
                    }
                }
                rs.close();
            }else
            {
                if(lstParamBeans!=null&&lstParamBeans.size()>0)
                {
                    int idx=1;
                    for(EditableReportParamBean paramBean:lstParamBeans)
                    {
                        paramBean.getDataTypeObj().setPreparedStatementValue(idx++,
                                getParamValue(mRowData,mParamValues,rbean,rrequest,paramBean),pstmt,dbtype);
                    }
                }
                int rtnVal=pstmt.executeUpdate();
                storeReturnValue(rrequest,mParamValues,String.valueOf(rtnVal));
            }
        }finally
        {
            WabacusAssistant.getInstance().release(null,pstmt);
        }
    }

    protected String getParamValue(Map<String,String> mRowData,Map<String,String> mParamValues,ReportBean rbean,ReportRequest rrequest,
            EditableReportParamBean paramBean)
    {
        String paramvalue=null;
        if(paramBean.getOwner() instanceof EditableReportExternalValueBean)
        {
            /**paramvalue=((EditableReportExternalValueBean)paramBean.getOwner()).getValue();
             if(Tools.isDefineKey("#",paramvalue))
            {//当前变量是引用绑定保存的其它报表的<params/>中定义的某个变量值
                paramvalue=getReferedOtherExternalValue(rbean,rrequest,paramBean,paramvalue);
            }else if(Tools.isDefineKey("@",paramvalue))
            {
                paramvalue=getExternalValueOfReferedCol(rbean,rrequest,paramBean,paramvalue);
            }else
            {*/
            paramvalue=paramBean.getParamValue(mParamValues.get(paramBean.getParamname()),rrequest,rbean);
            
        }else if(paramBean.getOwner() instanceof ColBean)
        {
            paramvalue=EditableReportAssistant.getInstance().getColParamValue(rrequest,rbean,mRowData,paramBean.getParamname());
            paramvalue=paramBean.getParamValue(paramvalue,rrequest,rbean);
        }else if(Tools.isDefineKey("@",paramBean.getParamname())&&!this.getOwnerGroupBean().getOwnerUpdateBean().isAutoReportdata())
        {//当前是配置在<button/>中的更新语句，且此<button/>不是自动从报表中获取保存数据，而是用户在客户端传入的数据
            paramvalue=mRowData.get(Tools.getRealKeyByDefine("@",paramBean.getParamname()));
        }else if("uuid{}".equals(paramBean.getParamname()))
        {
            paramvalue=UUIDGenerator.generateID();
        }else if(Tools.isDefineKey("increment",paramBean.getParamname()))
        {
            paramvalue=EditableReportAssistant.getInstance().getAutoIncrementIdValue(rrequest,rbean,this.ownerGroupBean.getDatasource(),paramBean.getParamname());
        }else if(Tools.isDefineKey("!",paramBean.getParamname()))
        {
            String customizeParamName=Tools.getRealKeyByDefine("!",paramBean.getParamname());
            Map<String,String> mCustomizedValues=rrequest.getMCustomizeEditData(rbean);
            if(mCustomizedValues==null||!mCustomizedValues.containsKey(customizeParamName))
            {
                paramvalue=null;
            }else
            {
                paramvalue=mCustomizedValues.get(customizeParamName);
            }
        }else if(WabacusAssistant.getInstance().isGetRequestContextValue(paramBean.getParamname()))
        {
            paramvalue=WabacusAssistant.getInstance().getRequestContextStringValue(rrequest,paramBean.getParamname(),null);  
        }
        return paramvalue;
    }

    protected void storeReturnValue(ReportRequest rrequest,Map<String,String> mExternalParamsValue,String rtnVal)
    {
        if(this.returnValueParamname==null||this.returnValueParamname.trim().equals("")) return;
        if(Tools.isDefineKey("#",this.returnValueParamname))
        {
            if(mExternalParamsValue!=null)
            {
                mExternalParamsValue.put(Tools.getRealKeyByDefine("#",this.returnValueParamname),rtnVal);
            }
        }else if(Tools.isDefineKey("rrequest",this.returnValueParamname))
        {
            rrequest.setAttribute(Tools.getRealKeyByDefine("rrequest",this.returnValueParamname),rtnVal);
        }
    }
    
    /*private String getExternalValueOfReferedCol(ReportBean rbean,ReportRequest rrequest,EditableReportParamBean paramBean,String paramvalue)
    {
        ColBean referredColBean=(ColBean)((EditableReportExternalValueBean)paramBean.getOwner()).getRefObj();
        String colParamname=referredColBean.getReportBean().getId()+referredColBean.getProperty();//被引用列对应的参数名
        if(paramvalue.indexOf(".insert.")>0)
        {
            List<Map<String,String>> lstInsertedCValues=rrequest.getLstInsertedData(referredColBean.getReportBean());
            if(lstInsertedCValues!=null&&lstInsertedCValues.size()>0)
            {
                paramvalue=paramBean.getParamValue(lstInsertedCValues.get(0).get(colParamname),rrequest,rbean);
            }else
            {
                paramvalue="";
            }
        }else if(paramvalue.indexOf(".update.")>0)
        {
            List<Map<String,String>> lstUpdatedCValues=rrequest.getLstUpdatedData(referredColBean.getReportBean());
            if(lstUpdatedCValues!=null&&lstUpdatedCValues.size()>0)
            {
                paramvalue=Tools.getRealKeyByDefine("@",paramvalue).trim();
                if(paramvalue.endsWith(".old"))
                {
                    paramvalue=lstUpdatedCValues.get(0).get(colParamname+"_old");
                    if(paramvalue==null)
                    {
                        paramvalue=lstUpdatedCValues.get(0).get(colParamname);
                    }
                    paramvalue=paramBean.getParamValue(paramvalue,rrequest,rbean);
                }else
                {
                    paramvalue=paramBean.getParamValue(lstUpdatedCValues.get(0).get(colParamname),rrequest,rbean);
                }
            }else
            {
                paramvalue="";
            }
        }else if(paramvalue.indexOf(".delete.")>0)
        {
            List<Map<String,String>> lstDeletedCValues=rrequest.getLstDeletedData(referredColBean.getReportBean());
            if(lstDeletedCValues!=null&&lstDeletedCValues.size()>0)
            {
                paramvalue=lstDeletedCValues.get(0).get(colParamname+"_old");
                if(paramvalue==null)
                {
                    paramvalue=lstDeletedCValues.get(0).get(colParamname);
                }
                paramvalue=paramBean.getParamValue(paramvalue,rrequest,rbean);
            }else
            {
                paramvalue="";
            }
        }else
        {
            List<Map<String,String>> lstInsertedCValues=rrequest.getLstInsertedData(referredColBean.getReportBean());
            if(lstInsertedCValues!=null&&lstInsertedCValues.size()>0)
            {
                paramvalue=paramBean.getParamValue(lstInsertedCValues.get(0).get(colParamname),rrequest,rbean);
            }else
            {
                List<Map<String,String>> lstUpdatedCValues=rrequest.getLstUpdatedData(referredColBean.getReportBean());
                if(lstUpdatedCValues!=null&&lstUpdatedCValues.size()>0)
                {
                    paramvalue=paramBean.getParamValue(lstUpdatedCValues.get(0).get(colParamname),rrequest,rbean);
                }else
                {
                    List<Map<String,String>> lstDeletedCValues=rrequest.getLstDeletedData(referredColBean.getReportBean());
                    if(lstDeletedCValues!=null&&lstDeletedCValues.size()>0)
                    {
                        paramvalue=lstDeletedCValues.get(0).get(colParamname+"_old");
                        if(paramvalue==null)
                        {
                            paramvalue=lstDeletedCValues.get(0).get(colParamname);
                        }
                        paramvalue=paramBean.getParamValue(paramvalue,rrequest,rbean);
                    }else
                    {
                        paramvalue="";
                    }
                }
            }
        }
        return paramvalue;
    }*/

    /**private String getReferedOtherExternalValue(ReportBean rbean,ReportRequest rrequest,EditableReportParamBean paramBean,String paramvalue)
    {
        EditableReportExternalValueBean referredEValueBean=(EditableReportExternalValueBean)((EditableReportExternalValueBean)paramBean.getOwner())
                .getRefObj();
        ReportBean rbeanRefered=referredEValueBean.getOwner().getOwner().getOwner().getReportBean();
        if(paramvalue.indexOf(".insert.")>0)
        {//是引用其它报表在<insert/>中定义的变量的值
            List<Map<String,String>> lstInsertedEValues=rrequest.getLstInsertedExternalValues(rbeanRefered);
            if(lstInsertedEValues!=null&&lstInsertedEValues.size()>0)
            {
                paramvalue=paramBean.getParamValue(lstInsertedEValues.get(0).get(referredEValueBean.getName()),rrequest,rbean);
            }else
            {
                paramvalue="";
            }
        }else if(paramvalue.indexOf(".update.")>0)
        {//是引用其它报表在<update/>中定义的变量的值
            List<Map<String,String>> lstUpdatedEValues=rrequest.getLstUpdatedExternalValues(rbeanRefered);//取到被引用的报表本次保存时所有变量的数据
            if(lstUpdatedEValues!=null&&lstUpdatedEValues.size()>0)
            {
                paramvalue=paramBean.getParamValue(lstUpdatedEValues.get(0).get(referredEValueBean.getName()),rrequest,rbean);
            }else
            {
                paramvalue="";
            }
        }else if(paramvalue.indexOf(".delete.")>0)
        {//是引用其它报表在<delete/>中定义的变量的值
            List<Map<String,String>> lstDeletedEValues=rrequest.getLstDeletedExternalValues(rbeanRefered);
            if(lstDeletedEValues!=null&&lstDeletedEValues.size()>0)
            {
                paramvalue=paramBean.getParamValue(lstDeletedEValues.get(0).get(referredEValueBean.getName()),rrequest,rbean);
            }else
            {
                paramvalue="";
            }
        }
        return paramvalue;
    }*/
    
    public void doPostLoadFinally()
    {
        if(lstParamBeans==null||lstParamBeans.size()==0) return;
        for(EditableReportParamBean paramBeanTmp:this.lstParamBeans)
        {
            this.ownerGroupBean.getOwnerUpdateBean().setRealParamnameInDoPostLoadFinally(paramBeanTmp);
        }
    }
}
