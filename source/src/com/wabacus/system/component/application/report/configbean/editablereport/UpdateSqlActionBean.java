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

public class UpdateSqlActionBean extends AbsEditSqlActionBean
{

    public UpdateSqlActionBean(EditActionGroupBean ownerGroupBean)
    {
        super(ownerGroupBean);
    }

    public UpdateSqlActionBean(String sql,List<EditableReportParamBean> lstParamsBean,EditActionGroupBean ownerGroupBean,String returnValueParamname)
    {
        super(ownerGroupBean);
        this.sql=sql;
        this.lstParamBeans=lstParamsBean;
        this.returnValueParamname=returnValueParamname;
    }
    
    public void parseActionscript(String reportTypeKey,String actionscript)
    {
        actionscript=this.parseAndRemoveReturnParamname(actionscript);
        if(this.isStandardUpdateSql(actionscript))
        {
            List<EditableReportParamBean> lstDynParamsTmp=new ArrayList<EditableReportParamBean>();
            actionscript=this.ownerGroupBean.getOwnerUpdateBean().parseStandardEditSql(actionscript,lstDynParamsTmp,
                    reportTypeKey);
            this.ownerGroupBean.addActionBean(new UpdateSqlActionBean(actionscript,lstDynParamsTmp,this.ownerGroupBean,this.returnValueParamname));
        }else
        {
            int idxwhere=actionscript.toLowerCase().indexOf(" where ");
            String whereclause=null;
            List<EditableReportParamBean> lstParamsBeanInWhereClause=null;
            if(idxwhere>0)
            {
                lstParamsBeanInWhereClause=new ArrayList<EditableReportParamBean>();
                whereclause=actionscript.substring(idxwhere).trim();
                whereclause=this.ownerGroupBean.getOwnerUpdateBean().parseStandardEditSql(whereclause,
                        lstParamsBeanInWhereClause,reportTypeKey);
                actionscript=actionscript.substring(0,idxwhere).trim();
            }
            AbsDatabaseType dbtype=Config.getInstance().getDataSource(this.ownerGroupBean.getDatasource()).getDbType();
            if(dbtype==null)
            {
                throw new WabacusConfigLoadingException("没有实现数据源"+this.ownerGroupBean.getDatasource()+"对应数据库类型的相应实现类");
            }
            List<UpdateSqlActionBean> lstRealUpdateSqls=dbtype.constructUpdateSql(actionscript.trim(),this.ownerGroupBean.getOwnerUpdateBean().getOwner().getReportBean(),reportTypeKey,this);
            List<EditableReportParamBean> lstParamsBean;
            String updatesql;
            for(UpdateSqlActionBean updateSqlBeanTmp:lstRealUpdateSqls)
            {
                updatesql=updateSqlBeanTmp.getSql();
                lstParamsBean=updateSqlBeanTmp.getLstParamBeans();
                if(whereclause!=null&&!whereclause.trim().equals(""))
                {
                    if(updatesql.indexOf("%where%")>0)
                    {
                        updatesql=Tools.replaceAll(updatesql,"%where%",whereclause);
                    }else
                    {
                        updatesql=updatesql+"  "+whereclause;
                    }
                    lstParamsBean.addAll(lstParamsBeanInWhereClause);
                }
                updateSqlBeanTmp.setSql(updatesql);
                updateSqlBeanTmp.setLstParamBeans(lstParamsBean);
                updateSqlBeanTmp.setReturnValueParamname(this.returnValueParamname);
                this.ownerGroupBean.addActionBean(updateSqlBeanTmp);
            }
        }
    }

    public List<UpdateSqlActionBean> constructUpdateSql(String configUpdateSql,ReportBean rbean,String reportTypeKey)
    {
        StringBuffer sqlBuffer=new StringBuffer();
        List<EditableReportParamBean> lstParamsBean=new ArrayList<EditableReportParamBean>();
        int idxleft=configUpdateSql.indexOf("(");
        if(idxleft<0||configUpdateSql.endsWith("()"))
        {//没有指定要更新的字段，则将所有从数据库取数据的<col/>（不包括hidden="1"和="2"的<col/>）全部更新到表中
            if(!this.ownerGroupBean.getOwnerUpdateBean().isAutoReportdata())
            {
                throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，在autoreportdata属性为false的<button/>中，不能配置update table这种不带参数的SQL语句");
            }
            if(configUpdateSql.endsWith("()")) configUpdateSql=configUpdateSql.substring(0,configUpdateSql.length()-2);
            sqlBuffer.append(configUpdateSql).append(" set ");
            for(ColBean cbean:rbean.getDbean().getLstCols())
            {
                EditableReportParamBean paramBean=createParamBeanByColbean(cbean.getProperty(),reportTypeKey,false,false);
                if(paramBean!=null)
                {
                    sqlBuffer.append(cbean.getColumn()+"=?,");
                    lstParamsBean.add(paramBean);
                }
            }
        }else
        {
            sqlBuffer.append(configUpdateSql.substring(0,idxleft)).append(" set ");
            int idxright=configUpdateSql.lastIndexOf(")");
            if(idxright!=configUpdateSql.length()-1)
            {
                throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，配置的修改数据SQL语句"+configUpdateSql+"不合法");
            }
            String cols=configUpdateSql.substring(idxleft+1,idxright);
            List<String> lstUpdateCols=Tools.parseStringToList(cols,',','\'');
            String columnname=null;
            String columnvalue=null;
            ColBean cb;
            for(String updatecol:lstUpdateCols)
            {
                if(updatecol==null||updatecol.trim().equals("")) continue;
                int idxequals=updatecol.indexOf("=");
                if(idxequals>0)
                {
                    columnname=updatecol.substring(0,idxequals).trim();
                    columnvalue=updatecol.substring(idxequals+1).trim();
                    Object paramObjTmp=this.createEditParams(columnvalue,reportTypeKey);
                    if(paramObjTmp==null) continue;
                    sqlBuffer.append(columnname+"=");
                    if(paramObjTmp instanceof EditableReportParamBean)
                    {
                        sqlBuffer.append("?");
                        lstParamsBean.add((EditableReportParamBean)paramObjTmp);
                    }else
                    {
                        sqlBuffer.append(paramObjTmp);
                    }
                    sqlBuffer.append(",");
                }else
                {
                    if(!Tools.isDefineKey("@",updatecol))
                    {
                        throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，配置的修改数据SQL语句"+configUpdateSql+"不合法，更新的字段值必须采用@{}括住");
                    }
                    if(this.ownerGroupBean.getOwnerUpdateBean().isAutoReportdata())
                    {
                        updatecol=Tools.getRealKeyByDefine("@",updatecol);
                        String realColProperty=updatecol.trim();
                        if(realColProperty.endsWith("__old")) realColProperty=realColProperty.substring(0,realColProperty.length()-"__old".length());
                        cb=rbean.getDbean().getColBeanByColProperty(realColProperty);
                        if(cb==null)
                        {
                            throw new WabacusConfigLoadingException("加载报表"+rbean.getPath()+"失败，配置的要更新字段"+updatecol+"不合法，没有取到其值对应的<col/>");
                        }
                        lstParamsBean.add(createParamBeanByColbean(updatecol,reportTypeKey,true,true));
                        sqlBuffer.append(cb.getColumn()+"=?,");
                    }else
                    {
                        ((EditableReportSQLButtonDataBean)this.getOwnerGroupBean().getOwnerUpdateBean()).setHasReportDataParams(true);
                        EditableReportParamBean paramBean=new EditableReportParamBean();
                        paramBean.setParamname(updatecol);
                        lstParamsBean.add(paramBean);
                        sqlBuffer.append(Tools.getRealKeyByDefine("@",updatecol)+"=?,");
                    }
                }
            }
        }
        if(sqlBuffer.charAt(sqlBuffer.length()-1)==',') sqlBuffer.deleteCharAt(sqlBuffer.length()-1);
        List<UpdateSqlActionBean> lstUpdateSqls=new ArrayList<UpdateSqlActionBean>();
        lstUpdateSqls.add(new UpdateSqlActionBean(sqlBuffer.toString(),lstParamsBean,this.ownerGroupBean,this.returnValueParamname));
        return lstUpdateSqls;
    }
    
    private boolean isStandardUpdateSql(String updatesql)
    {
        updatesql=updatesql==null?"":updatesql.toLowerCase().trim();
        if(!updatesql.startsWith("update ")) return true;
        updatesql=updatesql.substring("update ".length()).trim();
        if(updatesql.equals("")) return true;
        updatesql=Tools.replaceCharacterInQuote(updatesql,'(',"$_LEFTBRACKET_$",true);
        updatesql=Tools.replaceCharacterInQuote(updatesql,')',"$_RIGHTBRACKET_$",true);
        int idxBracket1=updatesql.indexOf("(");
        if(idxBracket1==0) return true;
        if(idxBracket1<0)
        {
            if(updatesql.indexOf(" ")<0&&updatesql.indexOf(",")<0&&updatesql.indexOf("=")<0) return false;
        }else
        {
            String tablename=updatesql.substring(0,idxBracket1).trim();
            if(tablename.indexOf(" ")>=0||tablename.indexOf(",")>=0||tablename.indexOf("=")>=0) return true;
            updatesql=updatesql.substring(idxBracket1+1);
            int idxleft=1;
            int idxRightBacket=-1;
            for(int i=0;i<updatesql.length();i++)
            {
                if(updatesql.charAt(i)=='(')
                {
                    idxleft++;
                }else if(updatesql.charAt(i)==')')
                {
                    if(idxleft==1)
                    {
                        idxRightBacket=i;
                        break;
                    }else if(idxleft<=0)
                    {//左右括号没成对，多出了)
                        return true;
                    }else
                    {
                        idxleft--;
                    }
                }
            }
            if(idxRightBacket==-1) return true;
            if(idxRightBacket==0&&(updatesql.equals(")")||updatesql.substring(1).trim().startsWith("where "))) return false;
            updatesql=updatesql.substring(idxRightBacket+1).trim();
            if(updatesql.equals("")||updatesql.startsWith("where ")) return false;
        }
        return true;
    }
}
