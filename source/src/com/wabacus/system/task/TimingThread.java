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
package com.wabacus.system.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.config.Config;

public class TimingThread extends Thread
{
    private static Log log=LogFactory.getLog(TimingThread.class);

    private final static TimingThread instance=new TimingThread();

    private boolean RUNNING_FLAG=true;

    private List<ITask> lstTasks;

    private long intervalMilSeconds;
    
    private TimingThread()
    {
        intervalMilSeconds=Config.getInstance().getSystemConfigValue("timing-thread-interval",15)*1000L;
        if(intervalMilSeconds<=0) intervalMilSeconds=15*1000L;
    }

    public static TimingThread getInstance()
    {
        return instance;
    }

    public boolean addTask(ITask taskObj)
    {
        if(lstTasks==null)
        {
            lstTasks=new ArrayList<ITask>();
        }else
        {
            for(ITask taskTmp:lstTasks)
            {
                if(taskTmp.getTaskId().equals(taskObj.getTaskId())) return false;
            }
            lstTasks.add(taskObj);
        }
        return true;
    }

    public boolean removeTask(ITask taskObj)
    {
        if(lstTasks==null) return false;
        ITask taskTmp;
        for(int i=0;i<this.lstTasks.size();i++)
        {
            taskTmp=this.lstTasks.get(i);
            if(taskTmp.getTaskId().equals(taskObj.getTaskId()))
            {
                this.lstTasks.remove(i);
                return true;
            }
        }
        return false;
    }

    public void reset()
    {
        this.lstTasks=null;
        this.intervalMilSeconds=Long.MIN_VALUE;
    }

    public void start()
    {
        if(this.lstTasks==null||this.lstTasks.size()==0) return;
        super.start();
    }

    public void run()
    {
        while(RUNNING_FLAG)
        {
            if(this.lstTasks==null||this.lstTasks.size()==0) break;
            for(ITask taskObjTmp:lstTasks)
            {
                try
                {
                    if(taskObjTmp.shouldExecute()) taskObjTmp.execute();
                }catch(Exception e)
                {
                    log.error("执行任务："+taskObjTmp.getTaskId()+"时失败",e);
                }
            }
            if(this.intervalMilSeconds>0)
            {
                try
                {
                    Thread.sleep(this.intervalMilSeconds);
                }catch(InterruptedException e)
                {
                    log.warn("wabacus定时运行线程被中断睡眠状态",e);
                }
            }
        }
    }

    public void stopRunning()
    {
        RUNNING_FLAG=false;
        if(this.lstTasks!=null)
        {
            for(ITask taskObjTmp:lstTasks)
            {
                try
                {
                    taskObjTmp.destory();
                }catch(Exception e)
                {
                    log.error("停止任务："+taskObjTmp.getTaskId()+"时失败",e);
                }
            }
        }
    }
}
