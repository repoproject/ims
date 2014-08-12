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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.database.type.AbsDatabaseType;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.system.datatype.IDataType;
import com.wabacus.system.datatype.VarcharType;
import com.wabacus.util.Tools;

public class StoreProcedureActionBean extends AbsEditSqlActionBean
{
    private static Log log=LogFactory.getLog(StoreProcedureActionBean.class);
    
    private List lstParams;
    
    public StoreProcedureActionBean(EditActionGroupBean ownerGroupBean)
    {
        super(ownerGroupBean);
    }

    public void parseActionscript(String reportTypeKey,String actionscript)
    {
        ReportBean rbean=this.ownerGroupBean.getOwnerUpdateBean().getOwner().getReportBean();
        actionscript=this.parseAndRemoveReturnParamname(actionscript);
        if(actionscript.startsWith("{")&&actionscript.endsWith("}"))
        {
            actionscript=actionscript.substring(1,actionscript.length()-1).trim();
        }
        String procedure=actionscript.substring("call ".length()).trim();
        if(procedure.equals(""))
        {
            throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"上的更新语句"+actionscript+"失败，没有指定要调用的存储过程名");
        }
        String procname=procedure;
        List lstProcedureParams=new ArrayList();
        int idxLeft=procedure.indexOf("(");
        if(idxLeft>0)
        {
            int idxRight=procedure.lastIndexOf(")");
            if(idxLeft==0||idxRight!=procedure.length()-1)
            {
                throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"上的更新语句"+actionscript+"失败，配置的要调用的存储过程格式不对");
            }
            procname=procedure.substring(0,idxLeft).trim();
            String params=procedure.substring(idxLeft+1,idxRight).trim();
            if(!params.equals(""))
            {
                List<String> lstParamsTmp=Tools.parseStringToList(params,',','\'');
                Object paramObjTmp;
                for(String paramTmp:lstParamsTmp)
                {
                    paramObjTmp=createEditParams(paramTmp,reportTypeKey);
                    if(paramObjTmp instanceof String)
                    {
                        String strParamTmp=((String)paramObjTmp);
                        if(strParamTmp.startsWith("'")&&strParamTmp.endsWith("'")) strParamTmp=strParamTmp.substring(1,strParamTmp.length()-1);
                        if(strParamTmp.startsWith("\"")&&strParamTmp.endsWith("\"")) strParamTmp=strParamTmp.substring(1,strParamTmp.length()-1);
                        paramObjTmp=strParamTmp;
                    }
                    lstProcedureParams.add(paramObjTmp);
                }
            }
        }
        StringBuffer tmpBuf=new StringBuffer("{call "+procname+"(");
        for(int i=0,len=lstProcedureParams.size();i<len;i++)
        {
            tmpBuf.append("?,");
        }
        if(this.returnValueParamname!=null&&!this.returnValueParamname.trim().equals("")) tmpBuf.append("?");
        if(tmpBuf.charAt(tmpBuf.length()-1)==',') tmpBuf.deleteCharAt(tmpBuf.length()-1);
        tmpBuf.append(")}");
        this.sql=tmpBuf.toString();
        this.lstParams=lstProcedureParams;
        this.ownerGroupBean.addActionBean(this);
    }

    public void updateData(ReportRequest rrequest,ReportBean rbean,Map<String,String> mRowData,
            Map<String,String> mParamValues) throws SQLException
    {
        AbsDatabaseType dbtype=rrequest.getDbType(this.ownerGroupBean.getDatasource());
        Connection conn=rrequest.getConnection(this.ownerGroupBean.getDatasource());
        CallableStatement cstmt=null;
        try
        {
            if(Config.show_sql) log.info("Execute sql:"+sql);
            cstmt=conn.prepareCall(sql);
            if(lstParams!=null&&lstParams.size()>0)
            {
                int idx=1;
                IDataType varcharTypeObj=Config.getInstance().getDataTypeByClass(VarcharType.class);
                EditableReportParamBean paramBeanTmp;
                for(Object paramObjTmp:this.lstParams)
                {
                    if(paramObjTmp instanceof EditableReportParamBean)
                    {
                        paramBeanTmp=(EditableReportParamBean)paramObjTmp;
                        paramBeanTmp.getDataTypeObj().setPreparedStatementValue(idx++,
                                getParamValue(mRowData,mParamValues,rbean,rrequest,paramBeanTmp),cstmt,dbtype);
                    }else
                    {
                        varcharTypeObj.setPreparedStatementValue(idx++,paramObjTmp==null?"":String.valueOf(paramObjTmp),cstmt,dbtype);
                    }
                }
            }
            int outputindex=-1;
            if(this.returnValueParamname!=null&&!this.returnValueParamname.trim().equals(""))
            {
                outputindex=this.lstParams==null?1:this.lstParams.size()+1;
                cstmt.registerOutParameter(outputindex,java.sql.Types.VARCHAR);
            }
            cstmt.execute();
            if(outputindex>0)
            {
                String rtnVal=cstmt.getString(outputindex);
                storeReturnValue(rrequest,mParamValues,rtnVal);
            }
        }finally
        {
            WabacusAssistant.getInstance().release(null,cstmt);
        }
    }

    public void doPostLoadFinally()
    {
        if(this.lstParams==null||this.lstParams.size()==0) return;
        for(Object paramObjTmp:this.lstParams)
        {
            if(paramObjTmp instanceof EditableReportParamBean)
            {
                this.ownerGroupBean.getOwnerUpdateBean().setRealParamnameInDoPostLoadFinally((EditableReportParamBean)paramObjTmp);
            }
        }
    }    
}
