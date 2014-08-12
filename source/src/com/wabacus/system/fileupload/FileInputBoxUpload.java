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
package com.wabacus.system.fileupload;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import com.wabacus.config.Config;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.container.page.PageBean;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.inputbox.FileBox;
import com.wabacus.system.intercept.AbsFileUploadInterceptor;

public class FileInputBoxUpload extends AbsFileUpload
{
    public FileInputBoxUpload(HttpServletRequest request)
    {
        super(request);
    }

    public void showUploadForm(PrintWriter out)
    {
        String pageid=getRequestString("PAGEID","");
        String reportid=getRequestString("REPORTID","");
        String inputboxid=getRequestString("INPUTBOXID","");
        PageBean pbean=Config.getInstance().getPageBean(pageid);
        if(pbean==null)
        {
            throw new WabacusRuntimeException("页面ID："+pageid+"不存在");
        }
        ReportBean rbean=pbean.getReportChild(reportid,true);
        if(rbean==null)
        {
            throw new WabacusRuntimeException("ID为"+pageid+"的页面下不存在ID为"+reportid+"的报表");
        }
        String boxid=inputboxid;
        int idx=boxid.lastIndexOf("__");
        if(idx>0)
        {
            boxid=boxid.substring(0,idx);
        }
        FileBox fileboxObj=rbean.getUploadFileBox(boxid);
        if(fileboxObj==null)
        {
            throw new WabacusRuntimeException("报表"+rbean.getPath()+"下面不存在ID为"+boxid+"的文件上传输入框");
        }
        String parentWindowName;
        if(Config.getInstance().getSystemConfigValue("prompt-dialog-type","artdialog").equals("artdialog"))
        {
            out.print("<script type=\"text/javascript\"  src=\""+Config.webroot+"webresources/component/artDialog/artDialog.js\"></script>");
            out.print("<script type=\"text/javascript\"  src=\""+Config.webroot+"webresources/component/artDialog/plugins/iframeTools.js\"></script>");
            parentWindowName="artDialog.open.origin";
        }else
        {
            parentWindowName="parent";
        }
        out.print("<input type='hidden' name='INPUTBOXID' value='"+inputboxid+"'/>");
        out.print("<input type='hidden' name='PAGEID' value='"+pageid+"'/>");
        out.print("<input type='hidden' name='REPORTID' value='"+reportid+"'/>");
        Map<String,String> mFormFieldValues=(Map<String,String>)request.getAttribute("WX_FILE_UPLOAD_FIELDVALUES");
        showDynParamHiddenFields(mFormFieldValues,fileboxObj.getMDynParamColumns(),out);
        showDynParamHiddenFields(mFormFieldValues,fileboxObj.getMDynParamConditions(),out);
        boolean flag=true;
        if(fileboxObj.getInterceptor()!=null)
        {
            flag=fileboxObj.getInterceptor().beforeDisplayFileUploadInterface(request,mFormFieldValues,out);
        }
        if(flag)
        {
            out.print("<table border=0 cellspacing=1 cellpadding=2  style=\"margin:0px\" width=\"98%\" ID=\"Table1\" align=\"center\">");
            out.print("<tr class=filetitle><td style='font-size:13px;'>文件上传</td></tr>");
            out.print("<tr><td style='font-size:13px;'><input type=\"file\" contentEditable=\"false\" name=\"uploadfile\" id=\"file1\"></td></tr>");
            if(fileboxObj.getAllowTypes()!=null&&!fileboxObj.getAllowTypes().trim().equals(""))
            {
                out.print("<tr class=filetitle><td style='font-size:13px;'>["+stardardFileSuffixString(fileboxObj.getAllowTypes())
                        +"]</td></tr>");
            }
            out.print("<tr><td style='font-size:13px;'><input type=\"submit\" class=\"cls-button\" name=\"submit\" value=\"上传\">");
            if(fileboxObj.getDeletetype()==1)
            {
                out.print("&nbsp;&nbsp;<input type=\"button\" value=\"删除\"");
                out.print(" onclick=\""+parentWindowName+".setPopUpBoxValueToParent('','");
                out.print(inputboxid+"','");
                out.print(fileboxObj.getFillmode()+"','");
                out.print(rbean.getGuid()+"','");
                out.print(fileboxObj.getTypename());
                out.print("');\"/>");
            }
            out.print("</td></tr></table>");
        }
    }

    private void showDynParamHiddenFields(Map<String,String> mFormFieldValues,Map<String,String> mDynParams,PrintWriter out)
    {
        String oldvalue=null;
        if(mFormFieldValues!=null)
        {
            oldvalue=mFormFieldValues.get("OLDVALUE");
        }else
        {
            oldvalue=request.getParameter("OLDVALUE");
        }
        if(oldvalue!=null&&!oldvalue.trim().equals(""))
        {
            out.print("<input type='hidden' name='OLDVALUE' value='"+oldvalue.trim()+"'/>");
        }
        if(mDynParams==null||mDynParams.size()==0) return;
        String paramvalueTmp;
        for(String paramnameTmp:mDynParams.keySet())
        {
            if(mFormFieldValues!=null)
            {
                paramvalueTmp=mFormFieldValues.get(paramnameTmp);
            }else
            {
                paramvalueTmp=request.getParameter(paramnameTmp);
            }
            if(paramvalueTmp!=null&&!paramvalueTmp.trim().equals(""))
            {
                out.print("<input type='hidden' name='"+paramnameTmp+"' value='"+paramvalueTmp+"'/>");
            }
        }
    }

    public String doFileUpload(List lstFieldItems,PrintWriter out)
    {
        String pageid=mFormFieldValues.get("PAGEID");
        String reportid=mFormFieldValues.get("REPORTID");
        String inputboxid=mFormFieldValues.get("INPUTBOXID");
        pageid=pageid==null?"":pageid.trim();
        inputboxid=inputboxid==null?"":inputboxid.trim();

        PageBean pbean=Config.getInstance().getPageBean(pageid);
        if(pbean==null)
        {
            throw new WabacusRuntimeException("页面ID："+pageid+"不存在");
        }
        ReportBean rbean=pbean.getReportChild(reportid,true);
        if(rbean==null)
        {
            throw new WabacusRuntimeException("ID为"+pageid+"的页面下不存在ID为"+reportid+"的报表");
        }
        mFormFieldValues.put(AbsFileUploadInterceptor.REPORTID_KEY,reportid);
        String boxid=inputboxid;
        int idx=boxid.lastIndexOf("__");
        if(idx>0)
        {
            boxid=boxid.substring(0,idx);
        }
        FileBox fileboxObj=rbean.getUploadFileBox(boxid);
        if(fileboxObj==null)
        {
            throw new WabacusRuntimeException("报表"+rbean.getPath()+"下面不存在ID为"+boxid+"的文件上传输入框");
        }
        this.interceptorObj=fileboxObj.getInterceptor();
        out.println(fileboxObj.createSelectOkFunction(inputboxid,false));
        String configAllowTypes=fileboxObj.getAllowTypes();
        if(configAllowTypes==null) configAllowTypes="";
        List<String> lstConfigAllowTypes=getFileSuffixList(configAllowTypes);
        boolean existUploadFile=false;
        FileItem item;
        for(Object itemObj:lstFieldItems)
        {
            item=(FileItem)itemObj;
            if(item.isFormField()) continue;
            String orginalFilename=item.getName();
            if((orginalFilename==null||orginalFilename.equals(""))) continue;
            orginalFilename=getFileNameFromAbsolutePath(orginalFilename);
            if(orginalFilename.equals("")) return "文件上传失败，文件路径不合法";
            String destfilename=getSaveFileName(orginalFilename,fileboxObj.getNewfilename());
            mFormFieldValues.put(AbsFileUploadInterceptor.ALLOWTYPES_KEY,configAllowTypes);
            mFormFieldValues.put(AbsFileUploadInterceptor.MAXSIZE_KEY,String.valueOf(fileboxObj.getMaxsize()));
            mFormFieldValues.put(AbsFileUploadInterceptor.FILENAME_KEY,destfilename);
            mFormFieldValues.put(AbsFileUploadInterceptor.SAVEPATH_KEY,fileboxObj.getSavePath());
            boolean shouldUpload=true;
            if(this.interceptorObj!=null)
            {
                shouldUpload=this.interceptorObj.beforeFileUpload(request,item,mFormFieldValues,out);
            }
            if(shouldUpload)
            {
                String errorMessage=doUploadFileAction(item,mFormFieldValues,orginalFilename,configAllowTypes,lstConfigAllowTypes);
                if(errorMessage!=null&&!errorMessage.trim().equals("")) return errorMessage;
            }
            else
            {
            	if (destfilename.length() > 80) {
        			return "文件名不能大于80个字符！";
        		}
            	else
            		return "错误！";
            }
            existUploadFile=true;
            String savevalue=mFormFieldValues.get(AbsFileUploadInterceptor.SAVEVALUE_KEY);
            if(savevalue==null)
            {
                String destfilenameTmp=mFormFieldValues.get(AbsFileUploadInterceptor.FILENAME_KEY);
                savevalue=fileboxObj.getFilePathOrUrl(destfilenameTmp);
            }
            if(savevalue==null) savevalue="";
            StringBuffer pathBuf=new StringBuffer();
            for(int i=0;i<savevalue.length();i++)
            {
                if(savevalue.charAt(i)=='\\')
                {
                    pathBuf.append("\\\\");
                }else
                {
                    pathBuf.append(savevalue.charAt(i));
                }
            }
            savevalue=pathBuf.toString();
            out.print("<script language='javascript'>");
            out.print("selectOK('"+savevalue+"',null,null,false);");
            out.print("</script>");
        }
        if(!existUploadFile)
        {
            return "请选择要上传的文件!";
        }
        return null;
    }
    
    public void promptSuccess(PrintWriter out,boolean isArtDialog)
    {
        if(isArtDialog)
        {
            out.println("artDialog.open.origin.wx_success('上传文件成功');");
            out.println("art.dialog.close();");
        }else
        {
            out.println("parent.wx_success('上传文件成功');");
            out.println("parent.closePopupWin();");
        }
    }
}
