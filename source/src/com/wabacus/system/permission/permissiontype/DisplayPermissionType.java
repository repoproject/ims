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
package com.wabacus.system.permission.permissiontype;

import com.wabacus.util.Consts;

public class DisplayPermissionType extends AbsPermissionType
{
    public DisplayPermissionType()
    {
        super();
        this.typeName=Consts.PERMISSION_TYPE_DISPLAY;
        this.defaultvalue="true";
        registerPermissionValue("true");
        registerPermissionValue("false");
    }
    
    public boolean isConsistentWithParentPermission(String permissionvalue,boolean parentPermission)
    {
        if(permissionvalue==null) return false;
        if(!this.getLstPermissionValues().contains(permissionvalue.toLowerCase().trim())) return false;
        if(permissionvalue.equals("true"))
        {
            if(parentPermission)
            {
                return false;
            }else
            {
                return true;
            }
        }else
        {
            if(parentPermission)
            {
                return true;
            }else
            {
                return false;
            }
        }
    }
}

