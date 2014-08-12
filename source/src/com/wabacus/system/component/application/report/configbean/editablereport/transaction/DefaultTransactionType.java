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
package com.wabacus.system.component.application.report.configbean.editablereport.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wabacus.config.Config;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsEditActionBean;
import com.wabacus.system.component.application.report.configbean.editablereport.AbsJavaEditActionBean;
import com.wabacus.system.component.application.report.configbean.editablereport.EditActionGroupBean;
import com.wabacus.util.Consts;
import com.wabacus.util.Consts_Private;

public class DefaultTransactionType implements ITransactionType
{

    public void beginTransaction(ReportRequest rrequest,List<EditActionGroupBean> lstEditActionGroupBeans)
    {
        if(lstEditActionGroupBeans==null||lstEditActionGroupBeans.size()==0) return;
        Map<String,Connection> mConnections=new HashMap<String,Connection>();
        List<AbsJavaEditActionBean> lstJavaActionBeans=new ArrayList<AbsJavaEditActionBean>();
        String dsNameTmp, dsLevelTmp;
        Connection connTmp;
        try
        {
            for(EditActionGroupBean actionGroupBeanTmp:lstEditActionGroupBeans)
            {
                dsNameTmp=actionGroupBeanTmp.getDatasource();
                if(dsNameTmp==null||dsNameTmp.trim().equals("")) dsNameTmp=Config.getInstance().getDefault_datasourcename();
                if(mConnections.containsKey(dsNameTmp)) continue;
                connTmp=rrequest.getConnection(dsNameTmp);
                if(!connTmp.getAutoCommit()) continue;
                connTmp.setAutoCommit(false);
                dsLevelTmp=rrequest.getTransactionLevel(dsNameTmp);
                if(dsLevelTmp!=null&&!dsLevelTmp.trim().equals(""))
                {
                    if(!Consts_Private.M_ALL_TRANSACTION_LEVELS.containsKey(dsLevelTmp))
                    {
                        throw new WabacusRuntimeException("为页面"+rrequest.getPagebean().getId()+"的数据源"+dsNameTmp+"设置事务隔离级别："+dsLevelTmp
                                +"不合法，不支持这个事务隔离级别");
                    }
                    if(dsLevelTmp.equals(Consts.TRANS_NONE)) continue;
                    connTmp.setTransactionIsolation(Consts_Private.M_ALL_TRANSACTION_LEVELS.get(dsLevelTmp));
                }
                mConnections.put(dsNameTmp,connTmp);
                for(AbsEditActionBean actionBeanTmp:actionGroupBeanTmp.getLstEditActionBeans())
                {
                    if(actionBeanTmp instanceof AbsJavaEditActionBean)
                    {
                        ((AbsJavaEditActionBean)actionBeanTmp).beginTransaction();
                        lstJavaActionBeans.add((AbsJavaEditActionBean)actionBeanTmp);
                    }
                }
            }
        }catch(Exception e)
        {
            for(Entry<String,Connection> entryTmp:mConnections.entrySet())
            {
                try
                {
                    entryTmp.getValue().setAutoCommit(true);
                }catch(SQLException e1)
                {
                    e1.printStackTrace();
                }
            }
            for(AbsJavaEditActionBean javaActionTmp:lstJavaActionBeans)
            {
                javaActionTmp.rollbackTransaction();
            }
            throw new WabacusRuntimeException("启动页面"+rrequest.getPagebean().getId()+"的数据源事务失败",e);
        }
    }

    public void commitTransaction(ReportRequest rrequest,List<EditActionGroupBean> lstEditActionGroupBeans)
    {
        if(lstEditActionGroupBeans==null||lstEditActionGroupBeans.size()==0) return;
        Map<String,Connection> mConnections=new HashMap<String,Connection>();
        String dsNameTmp, dsLevelTmp;
        Connection connTmp;
        try
        {
            for(EditActionGroupBean actionGroupBeanTmp:lstEditActionGroupBeans)
            {
                dsNameTmp=actionGroupBeanTmp.getDatasource();
                if(dsNameTmp==null||dsNameTmp.trim().equals("")) dsNameTmp=Config.getInstance().getDefault_datasourcename();
                if(mConnections.containsKey(dsNameTmp)) continue;
                connTmp=rrequest.getConnection(dsNameTmp);
                dsLevelTmp=rrequest.getTransactionLevel(dsNameTmp);
                if(Consts.TRANS_NONE.equals(dsLevelTmp)) continue;
                connTmp.commit();
                connTmp.setAutoCommit(true);
                mConnections.put(dsNameTmp,connTmp);
                for(AbsEditActionBean actionBeanTmp:actionGroupBeanTmp.getLstEditActionBeans())
                {
                    if(actionBeanTmp instanceof AbsJavaEditActionBean) ((AbsJavaEditActionBean)actionBeanTmp).commitTransaction();
                }
            }
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("提交页面"+rrequest.getPagebean().getId()+"的数据源事务失败",e);
        }
    }

    public void rollbackTransaction(ReportRequest rrequest,List<EditActionGroupBean> lstEditActionGroupBeans)
    {
        if(lstEditActionGroupBeans==null||lstEditActionGroupBeans.size()==0) return;
        Map<String,Connection> mConnections=new HashMap<String,Connection>();
        String dsNameTmp, dsLevelTmp;
        Connection connTmp;
        try
        {
            for(EditActionGroupBean actionGroupBeanTmp:lstEditActionGroupBeans)
            {
                dsNameTmp=actionGroupBeanTmp.getDatasource();
                if(dsNameTmp==null||dsNameTmp.trim().equals("")) dsNameTmp=Config.getInstance().getDefault_datasourcename();
                if(mConnections.containsKey(dsNameTmp)) continue;
                connTmp=rrequest.getConnection(dsNameTmp);
                dsLevelTmp=rrequest.getTransactionLevel(dsNameTmp);
                if(Consts.TRANS_NONE.equals(dsLevelTmp)) continue;
                connTmp.rollback();
                connTmp.setAutoCommit(true);
                mConnections.put(dsNameTmp,connTmp);
                for(AbsEditActionBean actionBeanTmp:actionGroupBeanTmp.getLstEditActionBeans())
                {
                    if(actionBeanTmp instanceof AbsJavaEditActionBean) ((AbsJavaEditActionBean)actionBeanTmp).rollbackTransaction();
                }
            }
        }catch(SQLException e)
        {
            throw new WabacusRuntimeException("回滚页面"+rrequest.getPagebean().getId()+"的数据源事务失败",e);
        }
    }

}

