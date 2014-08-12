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
package com.wabacus.system.component.application.report.chart.configbean;

import java.util.Map;

import com.wabacus.config.component.application.report.AbsConfigBean;
import com.wabacus.config.component.application.report.extendconfig.AbsExtendConfigBean;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.WabacusAssistant;

public class FusionChartsReportColBean  extends AbsExtendConfigBean
{
    private String nonfromdb_colvalue;//当<col/>的column配置为{non-fromdb}时，本<col/>配置的显示内容
    
    private Map<String,String> mDynNonfromdb_colvalueParts;
    
    public FusionChartsReportColBean(AbsConfigBean owner)
    {
        super(owner);
    }

    public String getNonfromdb_colvalue(ReportRequest rrequest)
    {
        return WabacusAssistant.getInstance().getStringValueWithDynPart(rrequest,this.nonfromdb_colvalue,this.mDynNonfromdb_colvalueParts,"");
    }

    public void setNonfromdb_colvalue(String nonfromdb_colvalue)
    {
        Object[] objArr=WabacusAssistant.getInstance().parseStringWithDynPart(nonfromdb_colvalue);
        this.nonfromdb_colvalue=(String)objArr[0];
        this.mDynNonfromdb_colvalueParts=(Map<String,String>)objArr[1];
    }

}

