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

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.database.type.AbsDatabaseType;
import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.system.buttons.EditableReportSQLButtonDataBean;
import com.wabacus.util.Tools;

public class InsertSqlActionBean extends AbsEditSqlActionBean
{
    public InsertSqlActionBean(EditActionGroupBean ownerGroupBean)
    {
        super(ownerGroupBean);
    }
    
    public void parseActionscript(String reportTypeKey,String actionscript)
    {
        actionscript=this.parseAndRemoveReturnParamname(actionscript);
        if(this.isStandardInsertSql(actionscript))
        {
            List<EditableReportParamBean> lstDynParamsTmp=new ArrayList<EditableReportParamBean>();
            String realsql=this.ownerGroupBean.getOwnerUpdateBean().parseStandardEditSql(actionscript,lstDynParamsTmp,
                    reportTypeKey);
            addInsertSqlActionBean(realsql,lstDynParamsTmp,this.returnValueParamname);
        }else
        {
            AbsDatabaseType dbtype=Config.getInstance().getDataSource(this.ownerGroupBean.getDatasource()).getDbType();
            if(dbtype==null)
            {
                throw new WabacusConfigLoadingException("没有实现数据源"+this.ownerGroupBean.getDatasource()+"对应数据库类型的相应实现类");
            }
            dbtype.constructInsertSql(actionscript,this.ownerGroupBean.getOwnerUpdateBean().getOwner().getReportBean(),reportTypeKey,this);
        }
    }

    public void constructInsertSql(String configInsertSql,ReportBean rbean,String reportTypeKey)
    {
        List lstParams=new ArrayList();
        StringBuffer sqlBuffer=new StringBuffer();
        sqlBuffer.append("insert into ");
        configInsertSql=configInsertSql.substring("insert".length()).trim();
        if(configInsertSql.toLowerCase().indexOf("into ")==0)
        {
            configInsertSql=configInsertSql.substring("into".length()).trim();
        }
        int idxleft=configInsertSql.indexOf("(");
        if(idxleft<0||configInsertSql.endsWith("()"))
        {//没有指定要更新的字段，则将所有符合要求的从数据库取数据的<col/>全部更新到表中
            if(!this.ownerGroupBean.getOwnerUpdateBean().isAutoReportdata())
            {
                throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，在autoreportdata属性为false的<button/>中，不能配置insert into table这种格式的SQL语句");
            }
            if(configInsertSql.endsWith("()")) configInsertSql=configInsertSql.substring(0,configInsertSql.length()-2).trim();
            sqlBuffer.append(configInsertSql).append("(");
            for(ColBean cbean:rbean.getDbean().getLstCols())
            {
                EditableReportParamBean paramBean=createParamBeanByColbean(cbean.getProperty(),reportTypeKey,false,false);
                if(paramBean!=null)
                {
                    sqlBuffer.append(cbean.getColumn()+",");
                    lstParams.add(paramBean);
                }
            }
        }else
        {
            sqlBuffer.append(configInsertSql.substring(0,idxleft)).append("(");
            int idxright=configInsertSql.lastIndexOf(")");
            if(idxright!=configInsertSql.length()-1)
            {
                throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，配置的修改数据SQL语句"+configInsertSql+"不合法");
            }
            String cols=configInsertSql.substring(idxleft+1,idxright);
            List<String> lstInsertCols=Tools.parseStringToList(cols,',','\'');
            String columnname=null;
            String columnvalue=null;
            ColBean cb;
            for(String updatecol:lstInsertCols)
            {
                if(updatecol==null||updatecol.trim().equals("")) continue;
                int idxequals=updatecol.indexOf("=");
                if(idxequals>0)
                {
                    columnname=updatecol.substring(0,idxequals).trim();
                    columnvalue=updatecol.substring(idxequals+1).trim();
                    Object paramObjTmp=this.createEditParams(columnvalue,reportTypeKey);
                    if(paramObjTmp==null) continue;
                    lstParams.add(paramObjTmp);
                    sqlBuffer.append(columnname+",");
                }else
                {
                    if(!Tools.isDefineKey("@",updatecol))
                    {
                        throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，配置的添加数据SQL语句"+configInsertSql+"不合法");
                    }
                    if(this.getOwnerGroupBean().getOwnerUpdateBean().isAutoReportdata())
                    {
                        updatecol=Tools.getRealKeyByDefine("@",updatecol);
                        String realColProperty=updatecol.trim();
                        if(realColProperty.endsWith("__old")) realColProperty=realColProperty.substring(0,realColProperty.length()-"__old".length());
                        cb=rbean.getDbean().getColBeanByColProperty(realColProperty);
                        if(cb==null)
                        {
                            throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，配置的要更新字段"+updatecol+"不合法，没有取到其值对应的<col/>");
                        }
                        sqlBuffer.append(cb.getColumn()+",");
                        lstParams.add(createParamBeanByColbean(updatecol,reportTypeKey,true,true));
                    }else
                    {//对于直接配置更新脚本的<button/>，且@{}数据来自于客户端传入的
                        ((EditableReportSQLButtonDataBean)this.getOwnerGroupBean().getOwnerUpdateBean()).setHasReportDataParams(true);
                        EditableReportParamBean paramBean=new EditableReportParamBean();
                        paramBean.setParamname(updatecol);
                        lstParams.add(paramBean);
                        sqlBuffer.append(Tools.getRealKeyByDefine("@",updatecol)+",");
                    }
                }
            }
        }
        if(lstParams.size()==0)
        {
            throw new WabacusConfigLoadingException("解析报表"+rbean.getPath()+"的sql语句："+configInsertSql+"失败，SQL语句格式不对");
        }
        if(sqlBuffer.charAt(sqlBuffer.length()-1)==',') sqlBuffer.deleteCharAt(sqlBuffer.length()-1);
        sqlBuffer.append(") values(");
        List<EditableReportParamBean> lstParamsBeanTmp=new ArrayList<EditableReportParamBean>();
        for(int j=0;j<lstParams.size();j++)
        {
            if(lstParams.get(j) instanceof EditableReportParamBean)
            {
                sqlBuffer.append("?,");
                lstParamsBeanTmp.add((EditableReportParamBean)lstParams.get(j));
            }else
            {
                sqlBuffer.append(lstParams.get(j)).append(",");
            }
        }
        if(sqlBuffer.charAt(sqlBuffer.length()-1)==',') sqlBuffer.deleteCharAt(sqlBuffer.length()-1);
        sqlBuffer.append(")");
        addInsertSqlActionBean(sqlBuffer.toString(),lstParamsBeanTmp,this.returnValueParamname);
    }
    
    public void addInsertSqlActionBean(String sql,List<EditableReportParamBean> lstParamBeans,String returnValueParamName)
    {
        InsertSqlActionBean insertActionBean=new InsertSqlActionBean(this.ownerGroupBean);
        insertActionBean.setSql(sql);
        insertActionBean.setLstParamBeans(lstParamBeans);
        insertActionBean.setReturnValueParamname(returnValueParamName);
        this.ownerGroupBean.addActionBean(insertActionBean);
    }
    
    private boolean isStandardInsertSql(String insertsql)
    {
        insertsql=insertsql==null?"":insertsql.toLowerCase().trim();
        if(!insertsql.startsWith("insert")) return true;
        insertsql=insertsql.substring("insert".length()).trim();
        if(insertsql.startsWith("into ")) insertsql=insertsql.substring("into ".length()).trim();
        if(insertsql.equals("")) return true;
        insertsql=Tools.replaceCharacterInQuote(insertsql,'(',"$_LEFTBRACKET_$",true);
        insertsql=Tools.replaceCharacterInQuote(insertsql,')',"$_RIGHTBRACKET_$",true);
        
        int idx=insertsql.indexOf("(");
        if(idx<=0) return false;
        insertsql=insertsql.substring(idx+1).trim();
        int idxleft=1;
        int idxRightBacket=-1;
        for(int i=0;i<insertsql.length();i++)
        {
            if(insertsql.charAt(i)=='(')
            {
                idxleft++;
            }else if(insertsql.charAt(i)==')')
            {
                if(idxleft==1)
                {
                    idxRightBacket=i;
                    break;
                }else if(idxleft<=0)
                {
                    return true;
                }else
                {
                    idxleft--;
                }
            }
        }
        if(idxRightBacket==-1) return true;
        if(idxRightBacket==0&&(insertsql.equals(")")||insertsql.substring(1).trim().startsWith("where "))) return false;
        insertsql=insertsql.substring(idxRightBacket+1).trim();
        if(insertsql.equals("")||insertsql.startsWith("where ")) return false;
        return true;
    }
}
