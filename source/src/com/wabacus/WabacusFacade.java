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
package com.wabacus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.wabacus.config.Config;
import com.wabacus.config.ConfigLoadManager;
import com.wabacus.config.component.IComponentConfigBean;
import com.wabacus.config.component.application.report.ColBean;
import com.wabacus.config.component.application.report.ConditionBean;
import com.wabacus.config.component.application.report.ReportBean;
import com.wabacus.config.component.application.report.ReportDataSetValueBean;
import com.wabacus.config.component.application.report.SqlBean;
import com.wabacus.config.component.other.JavascriptFileBean;
import com.wabacus.config.database.type.AbsDatabaseType;
import com.wabacus.config.dataexport.PDFExportBean;
import com.wabacus.config.dataexport.WordRichExcelExportBean;
import com.wabacus.config.print.AbsPrintProviderConfigBean;
import com.wabacus.config.typeprompt.TypePromptBean;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.exception.WabacusRuntimeWarningException;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.WabacusResponse;
import com.wabacus.system.assistant.EditableReportAssistant;
import com.wabacus.system.assistant.PdfAssistant;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.system.component.IComponentType;
import com.wabacus.system.component.application.report.abstractreport.AbsListReportType;
import com.wabacus.system.component.application.report.abstractreport.AbsReportType;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportColBean;
import com.wabacus.system.component.application.report.abstractreport.configbean.AbsListReportFilterBean;
import com.wabacus.system.component.application.report.chart.FusionChartsReportType;
import com.wabacus.system.dataset.IReportDataSet;
import com.wabacus.system.dataset.sqldataset.GetAllDataSetByPreparedSQL;
import com.wabacus.system.dataset.sqldataset.GetAllDataSetBySQL;
import com.wabacus.system.dataset.sqldataset.GetDataSetByStoreProcedure;
import com.wabacus.system.fileupload.AbsFileUpload;
import com.wabacus.system.fileupload.DataImportReportUpload;
import com.wabacus.system.fileupload.DataImportTagUpload;
import com.wabacus.system.fileupload.FileInputBoxUpload;
import com.wabacus.system.fileupload.FileTagUpload;
import com.wabacus.system.inputbox.AbsSelectBox;
import com.wabacus.system.inputbox.ServerValidateBean;
import com.wabacus.system.inputbox.TextBox;
import com.wabacus.system.inputbox.autocomplete.AutoCompleteBean;
import com.wabacus.system.print.AbsPrintProvider;
import com.wabacus.system.serveraction.IServerAction;
import com.wabacus.util.Consts;
import com.wabacus.util.Consts_Private;
import com.wabacus.util.Tools;
import com.wabacus.util.UniqueArrayList;

public class WabacusFacade
{
    private static Log log=LogFactory.getLog(WabacusFacade.class);

    public static void displayReport(HttpServletRequest request,HttpServletResponse response)
    {
        ReportRequest rrequest=new ReportRequest(request,Consts.DISPLAY_ON_PAGE);
        WabacusResponse wresponse=new WabacusResponse(response);
        displayReport(rrequest,wresponse,rrequest.getStringAttribute("PAGEID",""));
    }

    public static String displayReport(String pageid,Map<String,String> mParams,Locale locale)
    {
        ReportRequest rrequest=new ReportRequest(pageid,Consts.DISPLAY_ON_PAGE,locale);
        if(mParams!=null)
        {
            for(Entry<String,String> entryTmp:mParams.entrySet())
            {
                rrequest.setAttribute(entryTmp.getKey(),entryTmp.getValue());
            }
        }
        WabacusResponse wresponse=new WabacusResponse(null);
        displayReport(rrequest,wresponse,pageid);
        StringBuffer resultBuf=wresponse.getOutBuf();
        if(resultBuf==null) return "";
        return resultBuf.toString();
    }

    private static void displayReport(ReportRequest rrequest,WabacusResponse wresponse,String pageid)
    {
        boolean success=true;
        String errorinfo=null;
        try
        {
            rrequest.setWResponse(wresponse);
            wresponse.setRRequest(rrequest);
            rrequest.init(pageid);
            if(rrequest.getSlaveReportTypeObj()!=null)
            {
                rrequest.getSlaveReportTypeObj().displayOnPage(null);
            }else
            {
                rrequest.getRefreshComponentTypeObj().displayOnPage(null);
            }
            log.debug(rrequest.getCurrentStatus());
        }catch(WabacusRuntimeWarningException wrwe)
        {
            String logwarnmess=wresponse.getMessageCollector().getLogWarnsMessages();
            if(logwarnmess!=null&&!logwarnmess.trim().equals(""))
            {
                log.warn("显示页面"+pageid+"时，"+logwarnmess);
            }
            if(wresponse.getStatecode()==Consts.STATECODE_FAILED)
            {
                success=false;
                errorinfo=wresponse.assembleResultsInfo(wrwe);
            }
        }catch(Exception wre)
        {
            wresponse.setStatecode(Consts.STATECODE_FAILED);
            String errormess=wresponse.getMessageCollector().getLogErrorsMessages();
            if(errormess!=null&&!errormess.trim().equals(""))
            {
                log.error("显示页面"+pageid+"失败，"+errormess,wre);
            }else
            {
                log.error("显示页面"+pageid+"失败",wre);
            }
            success=false;
            errorinfo=wresponse.assembleResultsInfo(wre);
        }finally
        {
            rrequest.destroy(success);
        }
        if(errorinfo!=null&&!errorinfo.trim().equals(""))
        {
            wresponse.println(errorinfo,true);
        }else
        {
            wresponse.println(rrequest.getWResponse().assembleResultsInfo(null));
        }
    }

    public static void exportReportDataOnWordRichexcel(HttpServletRequest request,HttpServletResponse response,int exporttype)
    {
        ReportRequest rrequest=new ReportRequest(request,exporttype);
        WabacusResponse wresponse=new WabacusResponse(response);
        exportReportDataOnWordRichexcel(rrequest.getStringAttribute("PAGEID",""),rrequest,wresponse,exporttype);
    }

    private static void exportReportDataOnWordRichexcel(String pageid,ReportRequest rrequest,WabacusResponse wresponse,int exporttype)
    {
        boolean success=true;
        String errorinfo=null;
        try
        {
            rrequest.setWResponse(wresponse);
            wresponse.setRRequest(rrequest);
            rrequest.init(pageid);
            wresponse.initOutput(getDataExportFilename(rrequest));
            IComponentType ccTypeObjTmp;
            Object dataExportTplObjTmp;
            WordRichExcelExportBean debeanTmp;
            for(IComponentConfigBean ccbeanTmp:rrequest.getLstComponentBeans())
            {
                ccTypeObjTmp=rrequest.getComponentTypeObj(ccbeanTmp,null,true);
                dataExportTplObjTmp=null;
                if(ccbeanTmp.getDataExportsBean()!=null)
                {//如果此组件配置了<dataexports/>
                    debeanTmp=(WordRichExcelExportBean)ccbeanTmp.getDataExportsBean().getDataExportBean(exporttype);//有在<dataexports/>中配置了type为此导出类型的<dataexport/>
                    if(debeanTmp!=null) dataExportTplObjTmp=debeanTmp.getDataExportTplObj();
                }
                ccTypeObjTmp.displayOnExportDataFile(dataExportTplObjTmp,true);
            }
        }catch(WabacusRuntimeWarningException wrwe)
        {
            String logwarnmess=rrequest.getWResponse().getMessageCollector().getLogWarnsMessages();
            if(logwarnmess!=null&&!logwarnmess.trim().equals(""))
            {
                log.warn("导出页面"+rrequest.getPagebean().getId()+"下的报表时，"+logwarnmess);
            }
            if(wresponse.getStatecode()==Consts.STATECODE_FAILED)
            {
                success=false;
                errorinfo=wresponse.assembleResultsInfo(wrwe);
            }
        }catch(Exception wre)
        {
            wresponse.setStatecode(Consts.STATECODE_FAILED);
            String errormess=rrequest.getWResponse().getMessageCollector().getLogErrorsMessages();
            if(errormess!=null&&!errormess.trim().equals(""))
            {
                log.error("导出页面"+rrequest.getPagebean().getId()+"下的报表失败，"+errormess,wre);
            }else
            {
                log.error("导出页面"+rrequest.getPagebean().getId()+"下的报表失败，",wre);
            }
            success=false;
            errorinfo=rrequest.getWResponse().assembleResultsInfo(wre);
        }finally
        {
            rrequest.destroy(success);
        }
        if(errorinfo!=null&&!errorinfo.trim().equals(""))
        {
            try
            {
                wresponse.println(errorinfo,true);
            }catch(Exception e)
            {
                log.error("导出页面"+pageid+"下的应用"+rrequest.getStringAttribute("INCLUDE_APPLICATIONIDS","")+"数据失败",e);
            }
        }
    }

    public static void exportReportDataOnPlainExcel(HttpServletRequest request,HttpServletResponse response)
    {
        ReportRequest rrequest=new ReportRequest(request,Consts.DISPLAY_ON_PLAINEXCEL);
        WabacusResponse wresponse=new WabacusResponse(response);
        exportReportDataOnPlainExcel(rrequest.getStringAttribute("PAGEID",""),rrequest,wresponse);
    }

    public static void exportReportDataOnPlainExcel(String pageid,Locale locale)
    {
        ReportRequest rrequest=new ReportRequest(pageid,Consts.DISPLAY_ON_PLAINEXCEL,locale);
        WabacusResponse wresponse=new WabacusResponse(null);
        exportReportDataOnPlainExcel(pageid,rrequest,wresponse);
    }

    private static void exportReportDataOnPlainExcel(String pageid,ReportRequest rrequest,WabacusResponse wresponse)
    {
        boolean success=true;
        try
        {
            rrequest.setWResponse(wresponse);
            wresponse.setRRequest(rrequest);
            rrequest.init(pageid);
            if(rrequest.getLstAllReportBeans()==null||rrequest.getLstAllReportBeans().size()==0)
            {
                throw new WabacusRuntimeException("导出页面"+pageid+"上的数据失败，plainexcel导出方式只能导出报表，不能导出其它应用");
            }
            Workbook workbook=new HSSFWorkbook();
            AbsReportType reportTypeObjTmp;
            for(ReportBean rbTmp:rrequest.getLstAllReportBeans())
            {
                reportTypeObjTmp=(AbsReportType)rrequest.getComponentTypeObj(rbTmp,null,false);
                reportTypeObjTmp.displayOnPlainExcel(workbook);
            }
            String title=WabacusAssistant.getInstance().encodeAttachFilename(rrequest.getRequest(),getDataExportFilename(rrequest));
            wresponse.getResponse().setHeader("Content-disposition","attachment;filename="+title+".xls");
            BufferedOutputStream bos=new BufferedOutputStream(wresponse.getResponse().getOutputStream());
            workbook.write(bos);
            bos.close();
        }catch(WabacusRuntimeWarningException wrwe)
        {
            String logwarnmess=rrequest.getWResponse().getMessageCollector().getLogWarnsMessages();
            if(logwarnmess!=null&&!logwarnmess.trim().equals(""))
            {
                log.warn("导出页面"+rrequest.getPagebean().getId()+"下的报表时，"+logwarnmess);
            }
            if(wresponse.getStatecode()==Consts.STATECODE_FAILED)
            {
                success=false;
            }
        }catch(Exception wre)
        {
            wresponse.setStatecode(Consts.STATECODE_FAILED);
            String errormess=rrequest.getWResponse().getMessageCollector().getLogErrorsMessages();
            if(errormess!=null&&!errormess.trim().equals(""))
            {
                log.error("导出页面"+rrequest.getPagebean().getId()+"下的报表失败，"+errormess,wre);
            }else
            {
                log.error("导出页面"+rrequest.getPagebean().getId()+"下的报表失败",wre);
            }
            success=false;
        }finally
        {
            rrequest.destroy(success);
        }
    }

    public static void exportReportDataOnPDF(HttpServletRequest request,HttpServletResponse response,int showtype)
    {
        ReportRequest rrequest=new ReportRequest(request,Consts.DISPLAY_ON_PDF);
        WabacusResponse wresponse=new WabacusResponse(response);
        exportReportDataOnPDF(rrequest.getStringAttribute("PAGEID",""),rrequest,wresponse);
    }

    private static void exportReportDataOnPDF(String pageid,ReportRequest rrequest,WabacusResponse wresponse)
    {
        boolean success=true;
        try
        {
            rrequest.setWResponse(wresponse);
            wresponse.setRRequest(rrequest);
            rrequest.init(pageid);
            if(rrequest.getLstAllReportBeans()==null||rrequest.getLstAllReportBeans().size()==0)
            {
                throw new WabacusRuntimeException("导出页面"+pageid+"上的数据失败，plainexcel导出方式只能导出报表，不能导出其它应用");
            }
            Document document=new Document();
            ByteArrayOutputStream baosResult=new ByteArrayOutputStream();
            PdfCopy pdfCopy=new PdfCopy(document,baosResult);
            document.open();
            boolean ispdfprint=rrequest.isPdfPrintAction();
            for(IComponentConfigBean ccbeanTmp:rrequest.getLstComponentBeans())
            {
                PDFExportBean pdfbeanTmp=null;
                if(ispdfprint)
                {
                    pdfbeanTmp=ccbeanTmp.getPdfPrintBean();
                }else if(ccbeanTmp.getDataExportsBean()!=null)
                {
                    pdfbeanTmp=(PDFExportBean)ccbeanTmp.getDataExportsBean().getDataExportBean(Consts.DATAEXPORT_PDF);
                }
                if(pdfbeanTmp!=null&&pdfbeanTmp.getPdftemplate()!=null&&!pdfbeanTmp.getPdftemplate().trim().equals(""))
                {//如果此组件配置了pdf模板
                    PdfAssistant.getInstance()
                            .addPdfPageToDocument(pdfCopy,PdfAssistant.getInstance().showReportDataOnPdfWithTpl(rrequest,ccbeanTmp));
                }
            }
            AbsReportType reportTypeObjTmp;
            for(ReportBean rbTmp:rrequest.getLstAllReportBeans())
            {
                reportTypeObjTmp=(AbsReportType)rrequest.getComponentTypeObj(rbTmp,null,false);
                if(rrequest.isReportInPdfTemplate(rbTmp.getId())) continue;
                PdfAssistant.getInstance().addPdfPageToDocument(pdfCopy,reportTypeObjTmp.displayOnPdf());
            }
            document.close();
            if(!ispdfprint)
            {
                String title=WabacusAssistant.getInstance().encodeAttachFilename(rrequest.getRequest(),getDataExportFilename(rrequest));
                wresponse.getResponse().setHeader("Content-disposition","attachment;filename="+title+".pdf");
            }
            wresponse.getResponse().setContentLength(baosResult.size());
            ServletOutputStream out=wresponse.getResponse().getOutputStream();
            baosResult.writeTo(out);
            out.flush();
            out.close();
            baosResult.close();
        }catch(WabacusRuntimeWarningException wrwe)
        {
            String logwarnmess=rrequest.getWResponse().getMessageCollector().getLogWarnsMessages();
            if(logwarnmess!=null&&!logwarnmess.trim().equals(""))
            {
                log.warn("导出页面"+rrequest.getPagebean().getId()+"下的报表时，"+logwarnmess);
            }
            if(wresponse.getStatecode()==Consts.STATECODE_FAILED)
            {
                success=false;
            }
        }catch(Exception wre)
        {
            wresponse.setStatecode(Consts.STATECODE_FAILED);
            String errormess=rrequest.getWResponse().getMessageCollector().getLogErrorsMessages();
            if(errormess!=null&&!errormess.trim().equals(""))
            {
                log.error("导出页面"+rrequest.getPagebean().getId()+"下的报表失败，"+errormess,wre);
            }else
            {
                log.error("导出页面"+rrequest.getPagebean().getId()+"下的报表失败",wre);
            }
            success=false;
        }finally
        {
            rrequest.destroy(success);
        }
    }

    private static String getDataExportFilename(ReportRequest rrequest)
    {
        StringBuffer filenameBuf=new StringBuffer();
        if(rrequest.getLstComponentBeans()==null||rrequest.getLstComponentBeans().size()==0) return "NoData";
        String filenameTmp;
        for(IComponentConfigBean ccbeanTmp:rrequest.getLstComponentBeans())
        {
            filenameTmp=null;
            if(ccbeanTmp.getDataExportsBean()!=null)
            {
                filenameTmp=ccbeanTmp.getDataExportsBean().getFilename(rrequest);
            }
            if(filenameTmp==null||filenameTmp.trim().equals(""))
            {//如果此组件没有在<dataexports/>中配置filename，则用标题做为文件名
                filenameTmp=ccbeanTmp.getTitle(rrequest);
            }
            if(filenameTmp==null||filenameTmp.trim().equals("")) continue;
            filenameBuf.append(filenameTmp).append(",");
        }
        if(filenameBuf.length()==0) return "DataExport";
        if(filenameBuf.charAt(filenameBuf.length()-1)==',') filenameBuf.deleteCharAt(filenameBuf.length()-1);
        return filenameBuf.toString();
    }

    public static void printComponents(HttpServletRequest request,HttpServletResponse response)
    {
        ReportRequest rrequest=new ReportRequest(request,Consts.DISPLAY_ON_PRINT);
        WabacusResponse wresponse=new WabacusResponse(response);
        rrequest.setWResponse(wresponse);
        wresponse.setRRequest(rrequest);
        String pageid=rrequest.getStringAttribute("PAGEID","");
        String printComid=rrequest.getStringAttribute("COMPONENTIDS","");
        boolean success=true;
        String errorinfo=null;
        try
        {
            rrequest.init(pageid);
            if(printComid.equals(""))
            {
                throw new WabacusRuntimeException("没有传入打印的组件ID");
            }
            if(rrequest.getLstComponentBeans()==null||rrequest.getLstComponentBeans().size()==0)
            {
                throw new WabacusRuntimeException("页面"+pageid+"不存在ID为"+printComid+"的组件");
            }
            if(rrequest.getLstComponentBeans().size()>1)
            {
                throw new WabacusRuntimeException("打印页面"+pageid+"上的组件"+printComid+"失败，一次只能打印一个组件");
            }
            AbsPrintProviderConfigBean printBean=rrequest.getLstComponentBeans().get(0).getPrintBean();
            if(printBean==null)
            {
                throw new WabacusRuntimeException("页面"+pageid+"ID为"+printComid+"的组件没有配置<print/>");
            }
            AbsPrintProvider printProvider=printBean.createPrintProvider(rrequest);
            printProvider.doPrint();
            wresponse.addOnloadMethod(rrequest.getLstComponentBeans().get(0).getPrintBean().getPrintJsMethodName(),"",true);
            wresponse.println(rrequest.getWResponse().assembleResultsInfo(null));
        }catch(WabacusRuntimeWarningException wrwe)
        {
            String logwarnmess=rrequest.getWResponse().getMessageCollector().getLogWarnsMessages();
            if(logwarnmess!=null&&!logwarnmess.trim().equals(""))
            {
                log.warn("打印页面"+pageid+"下的应用时，"+logwarnmess);
            }
            if(wresponse.getStatecode()==Consts.STATECODE_FAILED)
            {
                success=false;
                errorinfo=wresponse.assembleResultsInfo(wrwe);
            }
        }catch(Exception wre)
        {
            wresponse.setStatecode(Consts.STATECODE_FAILED);
            String errormess=rrequest.getWResponse().getMessageCollector().getLogErrorsMessages();
            if(errormess!=null&&!errormess.trim().equals(""))
            {
                log.error("打印页面"+pageid+"下的应用失败，"+errormess,wre);
            }else
            {
                log.error("打印页面"+pageid+"下的应用失败，",wre);
            }
            success=false;
            errorinfo=rrequest.getWResponse().assembleResultsInfo(wre);
        }finally
        {
            rrequest.destroy(success);
        }
        if(errorinfo!=null&&!errorinfo.trim().equals(""))
        {
            try
            {
                wresponse.println(errorinfo,true);
            }catch(Exception e)
            {
                log.error("打印页面"+pageid+"下的组件"+printComid+"数据失败",e);
            }
        }
    }

    public static String getFilterDataList(HttpServletRequest request,HttpServletResponse response)
    {
        ReportRequest rrequest=null;
        ReportBean rbean=null;
        StringBuffer resultBuf=new StringBuffer();
        try
        {
            rrequest=new ReportRequest(request,-1);
            WabacusResponse wresponse=new WabacusResponse(response);
            wresponse.setRRequest(rrequest);
            rrequest.setWResponse(wresponse);
            rrequest.initGetFilterDataList();
            rbean=rrequest.getLstAllReportBeans().get(0);
            
            
            String colproperty=rrequest.getStringAttribute("FILTER_COLPROP","");
            ColBean cbean=rbean.getDbean().getColBeanByColProperty(colproperty);
            if(cbean==null)
            {
                throw new WabacusRuntimeException("取过滤数据时，根据"+colproperty+"没有取到指定的<col/>配置信息");
            }
            List<ReportDataSetValueBean> lstDatasetBeans=rbean.getSbean().getLstDatasetValueBeansByValueid(cbean.getDatasetValueId());
            List<IReportDataSet> lstDatasetObjs=getLstDatasetObjs(rbean.getSbean(),lstDatasetBeans);
            AbsListReportColBean alrcbean=(AbsListReportColBean)cbean.getExtendConfigDataForReportType(AbsListReportType.KEY);
            AbsListReportFilterBean alfbean=alrcbean.getFilterBean();
            if(rbean.getInterceptor()!=null)
            {
                rrequest.setAttribute(rbean.getId()+"_WABACUS_FILTERBEAN",alfbean);
            }
            List<String> lstSelectedData=new ArrayList<String>();
            if(!alfbean.isConditionRelate())
            {
                String[][] selectedValsArr=getFilterDataArray(rrequest,lstDatasetBeans,lstDatasetObjs,cbean,true);


//                    return "<item><value>[error]</value><label><![CDATA[没有获取到列过滤选项数据]]></label></item>";

                if(selectedValsArr!=null&&selectedValsArr.length>0)
                {
                    for(int i=0;i<selectedValsArr[0].length;i++)
                    {
                        lstSelectedData.add(selectedValsArr[0][i]);
                    }
                }
            }else
            {//与查询条件相关联的列
                String filterval=rrequest.getStringAttribute(alfbean.getConditionname(),"");
                if(!filterval.equals(""))
                {
                    resultBuf.append("<item><value><![CDATA[(ALL_DATA)]]></value><label>(全部)</label></item>");
                }
            }
            log.debug(lstSelectedData);
            String[][] selectedValsArr=getFilterDataArray(rrequest,lstDatasetBeans,lstDatasetObjs,cbean,false);
//            if(selectedValsArr==null) return "<item><value>[error]</value><label><![CDATA[获取列过滤选项数据失败]]></label></item>";
            if(selectedValsArr==null||selectedValsArr.length==0)
            {
                resultBuf.append("<item><value>[nodata]</value><label>无选项数据</label></item>");
                return resultBuf.toString();
            }
            for(int i=0;i<selectedValsArr[0].length;i++)
            {
                resultBuf.append("<item>");
                resultBuf.append("<value");
                if(lstSelectedData.contains(selectedValsArr[0][i]))
                {
                    resultBuf.append(" isChecked=\"true\"");
                }
                resultBuf.append("><![CDATA["+selectedValsArr[0][i]+"]]></value>");
                if(selectedValsArr[1]!=null&&!"[BLANK]".equals(selectedValsArr[1][i]))
                {
                    resultBuf.append("<label><![CDATA["+selectedValsArr[1][i]+"]]></label>");
                }
                resultBuf.append("</item>");
            }
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("加载报表"+rbean!=null?rbean.getPath():""+"的列过滤数据失败",e);
        }finally
        {
            if(rrequest!=null) rrequest.destroy(false);
        }
        
        return resultBuf.toString();
    }

    private static List<IReportDataSet> getLstDatasetObjs(SqlBean sbean,List<ReportDataSetValueBean> lstDatasetBeans)
    {
        List<IReportDataSet> lstDatasetObjs=new ArrayList<IReportDataSet>();
        ReportDataSetValueBean datasetbean;
        IReportDataSet datasetObj=null;
        for(int i=0;i<lstDatasetBeans.size();i++)
        {
            datasetbean=lstDatasetBeans.get(i);
            if(datasetbean.getCustomizeDatasetObj()!=null)
            {
                datasetObj=datasetbean.getCustomizeDatasetObj();
            }else if(datasetbean.isStoreProcedure())
            {
                datasetObj=new GetDataSetByStoreProcedure();
            }else if(sbean.getStatementType()==SqlBean.STMTYPE_PREPAREDSTATEMENT)
            {
                datasetObj=new GetAllDataSetByPreparedSQL();
            }else
            {
                datasetObj=new GetAllDataSetBySQL();
            }
            lstDatasetObjs.add(datasetObj);
        }
        return lstDatasetObjs;
    }

    private static String[][] getFilterDataArray(ReportRequest rrequest,List<ReportDataSetValueBean> lstDatasetbeans,
            List<IReportDataSet> lstDatasetObjs,ColBean cbean,boolean isGetSelectedData) throws Exception
    {
        ReportBean rbean=cbean.getReportBean();
        Map<String,List<String>> mSelectedFilterValues=null;
        ColBean cbeanFiltered=null;
        if(isGetSelectedData)
        {
            mSelectedFilterValues=getMFilterColAndFilterValues(rrequest,rbean);
            AbsListReportFilterBean filterBean=rrequest.getCdb(rbean.getId()).getFilteredBean();
            if(filterBean!=null) cbeanFiltered=(ColBean)filterBean.getOwner();
        }
        int maxOptionsCount=Config.getInstance().getSystemConfigValue("colfilter-maxnum-options",-1);
        List<String[]> lstFilterOptionsData=new ArrayList<String[]>();
        int i=0;
        for(ReportDataSetValueBean dsvbeanTmp:lstDatasetbeans)
        {
            getOneDatasetFilterDataArray(rrequest,dsvbeanTmp,lstDatasetObjs.get(i),cbean,isGetSelectedData&&cbeanFiltered!=null
                    &&cbeanFiltered.isMatchDataSet(dsvbeanTmp)?mSelectedFilterValues:null,lstFilterOptionsData);
            if(maxOptionsCount>0&&maxOptionsCount<=lstFilterOptionsData.size()) break;
            i++;
        }
        String[][] strArrayResults=new String[2][lstFilterOptionsData.size()];
        i=0;
        for(String[] optionArrTmp:lstFilterOptionsData)
        {
            strArrayResults[0][i]=optionArrTmp[0];
            strArrayResults[1][i]=optionArrTmp[1];
            i++;
        }
        return strArrayResults;
    }
    
    private static Map<String,List<String>> getMFilterColAndFilterValues(ReportRequest rrequest,ReportBean rbean)
    {
        AbsListReportFilterBean filterBean=rrequest.getCdb(rbean.getId()).getFilteredBean();
        if(filterBean==null) return null;
        ColBean cbTmp=(ColBean)filterBean.getOwner();
        String filterval=rrequest.getStringAttribute(filterBean.getId(),"");
        if(filterval.equals("")) return null;
        Map<String,List<String>> mResults=new HashMap<String,List<String>>();
        mResults.put(cbTmp.getColumn(),Tools.parseStringToList(filterval,";;",false));
        return mResults;
    }
    
    private static void getOneDatasetFilterDataArray(ReportRequest rrequest,ReportDataSetValueBean datasetbean,IReportDataSet datasetObj,ColBean cbean,
            Map<String,List<String>> mSelectedFilterValues,List<String[]> lstFilterOptionsData) throws Exception
    {
        ReportBean rbean=cbean.getReportBean();
        int maxOptionsCount=Config.getInstance().getSystemConfigValue("colfilter-maxnum-options",-1);
        AbsDatabaseType dbtype=rrequest.getDbType(datasetbean.getDatasource());
        Object objTmp=datasetObj.getColFilterDataSet(rrequest,cbean,datasetbean,mSelectedFilterValues);
        if(objTmp==null||rrequest.getWResponse().getMessageCollector().hasErrors()||rrequest.getWResponse().getMessageCollector().hasWarnings())
        {
            return;
        }
        List<String> lstFilterDataLocal=null;
        if(objTmp instanceof ResultSet)
        {
            lstFilterDataLocal=new ArrayList<String>();
            ResultSet rs=(ResultSet)objTmp;
            Object valObj;
            String strvalue;
            int optioncnt=0;
            while(rs.next())
            {
                valObj=cbean.getDatatypeObj().getColumnValue(rs,cbean.getColumn(),dbtype);
                strvalue=cbean.getDatatypeObj().value2label(valObj);
                if(strvalue==null||strvalue.trim().equals("")) continue;
                if(!lstFilterDataLocal.contains(strvalue)) lstFilterDataLocal.add(strvalue);
                if(maxOptionsCount>0&&(++optioncnt+lstFilterOptionsData.size())==maxOptionsCount)
                {
                    break;
                }
            }
            rs.close();
        }else
        {
            if(!(objTmp instanceof List)) throw new WabacusRuntimeException("加载报表"+rbean.getPath()+"的列过滤数据失败，数据集返回的数据类型不合法，即不是ResultSet也不是List类型");
            lstFilterDataLocal=(List<String>)objTmp;
            if(maxOptionsCount>0)
            {
                while(lstFilterDataLocal.size()+lstFilterOptionsData.size()>maxOptionsCount)
                    lstFilterDataLocal.remove(lstFilterDataLocal.size()-1);
            }
        }
        if(lstFilterDataLocal==null||lstFilterDataLocal.size()==0) return;
        AbsListReportFilterBean alfbean=((AbsListReportColBean)cbean.getExtendConfigDataForReportType(AbsListReportType.KEY)).getFilterBean();
        if(rbean.getInterceptor()!=null)
        {
            lstFilterDataLocal=(List)rbean.getInterceptor().afterLoadData(rrequest,rbean,alfbean,lstFilterDataLocal);
        }
        String[] strValueArr=lstFilterDataLocal.toArray(new String[lstFilterDataLocal.size()]);
        String[] strLabelArr=null;
        if(alfbean.getFormatMethod()!=null&&alfbean.getFormatClass()!=null)
        {
            strLabelArr=(String[])alfbean.getFormatMethod().invoke(alfbean.getFormatClass(),new Object[] { rbean, strValueArr });
        }
        if(strLabelArr==null||strLabelArr.length!=strValueArr.length)
        {
            strLabelArr=null;
        }
        String[] strArrayTmp;
        for(int i=0;i<strValueArr.length;i++)
        {
            strArrayTmp=new String[2];
            strArrayTmp[0]=strValueArr[i];
            if(strLabelArr!=null)
            {
                strArrayTmp[1]=strLabelArr[i];
            }else
            {
                strArrayTmp[1]="[BLANK]";
            }
            lstFilterOptionsData.add(strArrayTmp);
        }
    }
    
    public static String getTypePromptDataList(HttpServletRequest request,HttpServletResponse response)
    {
        ReportRequest rrequest=null;
        StringBuffer resultBuf=new StringBuffer();
        try
        {
            rrequest=new ReportRequest(request,-1);
            WabacusResponse wresponse=new WabacusResponse(response);
            rrequest.setWResponse(wresponse);
            rrequest.initReportCommon();
            ReportBean rbean=rrequest.getLstAllReportBeans().get(0);
            String inputboxid=rrequest.getStringAttribute("INPUTBOXID","");
            if(inputboxid.equals(""))
            {
                throw new WabacusRuntimeException("没有取到输入框ID，无法获取输入提示数据");
            }
            int idx=inputboxid.lastIndexOf("__");
            if(idx>0)
            {//自动列表/列表表单的输入框
                inputboxid=inputboxid.substring(0,idx);
            }
            TextBox boxObj=rbean.getTextBoxWithingTypePrompt(inputboxid);
            if(boxObj==null)
            {
                throw new WabacusRuntimeException("没有取到相应输入框对象，无法获取提示数据");
            }
            TypePromptBean promptBean=boxObj.getTypePromptBean();
            if(promptBean==null)
            {
                throw new WabacusRuntimeException("输入框没有配置输入提示功能");
            }
            String inputvalue=rrequest.getStringAttribute("TYPE_PROMPT_TXTVALUE","");
            if(boxObj.getOwner() instanceof ConditionBean)
            {
                ConditionBean cbTmp=(ConditionBean)boxObj.getOwner();
                if(ConditionBean.LABELPOSITION_INNER.equals(cbTmp.getLabelposition())&&inputvalue.equals(cbTmp.getLabel(rrequest))) inputvalue="";
            }
            if(inputvalue.equals("")&&!promptBean.isSelectbox())
            {//如果没有取到用户输入的字符，则不给出提示信息，返回空
                return "";
            }else
            {
                List<Map<String,String>> lstResults=promptBean.getLstRuntimeOptionsData(rrequest,rbean,inputvalue);
                if(lstResults==null||lstResults.size()==0) return "";
                int cnt=promptBean.getResultcount();
                if(cnt>lstResults.size()) cnt=lstResults.size();
                for(int i=0;i<cnt;i++)
                {
                    resultBuf.append("<item ");
                    for(Entry<String,String> entryTmp:lstResults.get(i).entrySet())
                    {
                        resultBuf.append(entryTmp.getKey()).append("=\"").append(entryTmp.getValue()).append("\" ");
                    }
                    resultBuf.append("/>");
                }
            }
            
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("加载输入联想数据失败",e);
        }finally
        {
            if(rrequest!=null) rrequest.destroy(false);
        }
        return resultBuf.toString();
    }

    public static String getSelectBoxDataList(HttpServletRequest request,HttpServletResponse response)
    {
        ReportRequest rrequest=null;
        StringBuffer resultBuf=new StringBuffer();
        try
        {
            rrequest=new ReportRequest(request,-1);
            WabacusResponse wresponse=new WabacusResponse(response);
            rrequest.setWResponse(wresponse);
            rrequest.initReportCommon();
            ReportBean rbean=rrequest.getLstAllReportBeans().get(0);
            resultBuf.append("pageid:\"").append(rbean.getPageBean().getId()).append("\",");
            resultBuf.append("reportid:\"").append(rbean.getId()).append("\",");
            String selectboxParams=rrequest.getStringAttribute("SELECTBOXIDS_AND_PARENTVALUES","");
            if(selectboxParams.equals("")) return "";
            if(selectboxParams.startsWith("condition:"))
            {
                resultBuf.append("isConditionBox:\"true\",");
                selectboxParams=selectboxParams.substring("condition:".length());
                List<String> lstSelectboxIds=Tools.parseStringToList(selectboxParams,";",false);
                AbsSelectBox childSelectBoxTmp;
                for(String selectboxidTmp:lstSelectboxIds)
                {
                    childSelectBoxTmp=rbean.getChildSelectBoxInConditionById(selectboxidTmp);
                    if(childSelectBoxTmp==null)
                    {
                        throw new WabacusRuntimeException("报表"+rbean.getPath()+"不存在id为"+selectboxidTmp+"的子下拉框");
                    }
                    List<Map<String,String>> lstOptionsResult=childSelectBoxTmp.getOptionsList(rrequest,childSelectBoxTmp.getAllParentValues(rrequest,
                            selectboxidTmp));
                    if(lstOptionsResult==null||lstOptionsResult.size()==0) continue;
                    resultBuf.append(assembleOptionsResult(selectboxidTmp,childSelectBoxTmp.getInputboxInnerType(),lstOptionsResult));
                }
            }else
            {
                resultBuf.append("isConditionBox:\"false\",");
                List<Map<String,String>> lstParams=EditableReportAssistant.getInstance().parseSaveDataStringToList(selectboxParams);
                String realInputboxidTmp, inputboxidTmp;
                AbsSelectBox childSelectBoxTmp;
                for(Map<String,String> mSelectBoxParamsTmp:lstParams)
                {
                    realInputboxidTmp=mSelectBoxParamsTmp.get("wx_inputboxid");
                    if(realInputboxidTmp==null||realInputboxidTmp.trim().equals("")) continue;
                    inputboxidTmp=realInputboxidTmp;
                    int idx=inputboxidTmp.lastIndexOf("__");
                    if(idx>0) inputboxidTmp=inputboxidTmp.substring(0,idx);
                    childSelectBoxTmp=rbean.getChildSelectBoxInColById(inputboxidTmp);
                    if(childSelectBoxTmp==null)
                    {
                        throw new WabacusRuntimeException("报表"+rbean.getPath()+"不存在id为"+inputboxidTmp+"的子下拉框");
                    }
                    mSelectBoxParamsTmp.remove("wx_inputboxid");
                    List<Map<String,String>> lstOptionsResult=childSelectBoxTmp.getOptionsList(rrequest,mSelectBoxParamsTmp);
                    if(lstOptionsResult==null||lstOptionsResult.size()==0) continue;
                    resultBuf.append(assembleOptionsResult(realInputboxidTmp,childSelectBoxTmp.getInputboxInnerType(),lstOptionsResult));
                }
            }
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("加载下拉框数据失败",e);
        }finally
        {
            if(rrequest!=null) rrequest.destroy(false);
        }
        if(resultBuf.length()>0&&resultBuf.charAt(resultBuf.length()-1)==',') resultBuf.deleteCharAt(resultBuf.length()-1);
        if(resultBuf.length()>0) return "{"+resultBuf.toString()+"}";
        
        return resultBuf.toString();
    }

    private static String assembleOptionsResult(String realSelectboxid,String selectboxtype,List<Map<String,String>> lstOptionsResult)
    {
        if(lstOptionsResult==null||lstOptionsResult.size()==0) return "";
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(realSelectboxid).append(":[");
        resultBuf.append("{selectboxtype:\"").append(selectboxtype).append("\"},");
        String labelTmp,valueTmp;
        for(Map<String,String> mItemsTmp:lstOptionsResult)
        {
            labelTmp=mItemsTmp.get("label");
            valueTmp=mItemsTmp.get("value");
            labelTmp=labelTmp==null?"":labelTmp.trim();
            valueTmp=valueTmp==null?"":valueTmp.trim();
            resultBuf.append("{name:\"").append(labelTmp).append("\",");
            resultBuf.append("value:\"").append(valueTmp).append("\"},");
        }
        if(resultBuf.charAt(resultBuf.length()-1)==',') resultBuf.deleteCharAt(resultBuf.length()-1);
        resultBuf.append("],");
        return resultBuf.toString();
    }

    public static String getAutoCompleteColValues(HttpServletRequest request,HttpServletResponse response)
    {
        StringBuffer resultBuf=new StringBuffer();
        ReportRequest rrequest=null;
        try
        {
            rrequest=new ReportRequest(request,-1);
            WabacusResponse wresponse=new WabacusResponse(response);
            rrequest.setWResponse(wresponse);
            rrequest.initGetAutoCompleteColValues();
            ReportBean rbean=rrequest.getLstAllReportBeans().get(0);
            String conditionparams=request.getParameter("AUTOCOMPLETE_COLCONDITION_VALUES");
            List<Map<String,String>> lstConditionParamsValue=EditableReportAssistant.getInstance().parseSaveDataStringToList(conditionparams);
            if(lstConditionParamsValue==null||lstConditionParamsValue.size()==0) return "";
            rrequest.setAttribute("COL_CONDITION_VALUES",lstConditionParamsValue.get(0));
            AutoCompleteBean accbean=rrequest.getAutoCompleteSourceInputBoxObj().getAutoCompleteBean();
            Map<String,String> mAutoCompleteData=accbean.getDatasetObj().getAutoCompleteColumnsData(rrequest,accbean,lstConditionParamsValue.get(0));
            if(rbean.getInterceptor()!=null)
            {
                mAutoCompleteData=(Map<String,String>)rbean.getInterceptor().afterLoadData(rrequest,rbean,accbean,mAutoCompleteData);
            }
            if(mAutoCompleteData==null||mAutoCompleteData.size()==0) return "";
            resultBuf.append("{");
            String propTmp, valueTmp;
            for(ColBean cbTmp:accbean.getLstAutoCompleteColBeans())
            {
                propTmp=cbTmp.getProperty();
                valueTmp=mAutoCompleteData.get(propTmp);
                if(valueTmp==null) valueTmp="";
                resultBuf.append(propTmp).append(":\"").append(valueTmp).append("\",");
                mAutoCompleteData.remove(propTmp);
            }
            for(Entry<String,String> entryTmp:mAutoCompleteData.entrySet())
            {
                resultBuf.append(entryTmp.getKey()).append(":\"").append(entryTmp.getValue()).append("\",");
            }
            if(resultBuf.charAt(resultBuf.length()-1)==',') resultBuf.deleteCharAt(resultBuf.length()-1);
            resultBuf.append("}");
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("加载自动填充的输入框数据失败",e);
        }finally
        {
            if(rrequest!=null) rrequest.destroy(false);
        }
        return resultBuf.toString();
    }

    public static void showUploadFilePage(HttpServletRequest request,PrintWriter out)
    {
        String contentType=request.getHeader("Content-type");
        String fileuploadtype=null;
        if(contentType!=null&&contentType.startsWith("multipart/"))
        {
            fileuploadtype=(String)request.getAttribute("FILEUPLOADTYPE");
            fileuploadtype=fileuploadtype==null?"":fileuploadtype.trim();
        }else
        {
            fileuploadtype=Tools.getRequestValue(request,"FILEUPLOADTYPE","");
        }
        AbsFileUpload fileUpload=getFileUploadObj(request,fileuploadtype);
        if(fileUpload==null)
        {
            out.println("显示文件上传界面失败，未知的文件上传类型");
            return;
        }
        importWebresources(out);
        out.println("<form  action=\""+Config.showreport_url
                +"\" style=\"margin:0px\" method=\"post\" onsubmit=\"return doFileUploadAction()\" enctype=\"multipart/form-data\" name=\"fileuploadform\">");
        out.println("<input type='hidden' name='FILEUPLOADTYPE' value='"+fileuploadtype+"'/>");
        fileUpload.showUploadForm(out);
        out.println("</form>");
        out.println("<div id=\"LOADING_IMG_ID\" class=\"cls-loading-img\"></div>");
    }

    public static void uploadFile(HttpServletRequest request,HttpServletResponse response)
    {
        PrintWriter out=null;
        try
        {
            out=response.getWriter();
        }catch(IOException e1)
        {
            throw new WabacusRuntimeException("从response中获取PrintWriter对象失败",e1);
        }
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset="+Config.encode+"\">");
        importWebresources(out);
        if(Config.getInstance().getSystemConfigValue("prompt-dialog-type","artdialog").equals("artdialog"))
        {
            out.print("<script type=\"text/javascript\"  src=\""+Config.webroot+"webresources/component/artDialog/artDialog.js\"></script>");
            out.print("<script type=\"text/javascript\"  src=\""+Config.webroot+"webresources/component/artDialog/plugins/iframeTools.js\"></script>");
        }
        /**if(true)
        {
            out.print("<table style=\"margin:0px;\"><tr><td style='font-size:13px;'><font color='#ff0000'>");
            out.print("这里是公共演示，不允许上传文件，您可以在本地部署WabacusDemo演示项目，进行完全体验，只需几步即可部署完成\n\rWabacusDemo.war位于下载包的samples/目录中");
            out.print("</font></td></tr></table>");
            return;
        }*/
        DiskFileUpload fileUploadObj=new DiskFileUpload();
        fileUploadObj.setHeaderEncoding(Config.encode);
        List lstFieldItems=null;
        String errorinfo=null;
        try
        {
            lstFieldItems=fileUploadObj.parseRequest(request);
            if(lstFieldItems==null||lstFieldItems.size()==0)
            {
                errorinfo="上传失败，没有取到要上传的文件";
            }
        }catch(FileUploadException e)
        {
            log.error("获取上传文件失败",e);
            errorinfo="获取上传文件失败";
        }
        Map<String,String> mFormFieldValues=new HashMap<String,String>();
        Iterator itFieldItems=lstFieldItems.iterator();
        FileItem item;
        while(itFieldItems.hasNext())
        {//将所有普通表单字段先取出放入mFormFieldValues中供后面传文件时使用
            item=(FileItem)itFieldItems.next();
            if(item.isFormField())
            {
                try
                {
                    mFormFieldValues.put(item.getFieldName(),item.getString(Config.encode));
                    request.setAttribute(item.getFieldName(),item.getString(Config.encode));
                }catch(UnsupportedEncodingException e)
                {
                    log.warn("进行文件上传时获取的表单数据不能转换成"+Config.encode+"编码类型",e);
                }
            }
        }
        String fileuploadtype=mFormFieldValues.get("FILEUPLOADTYPE");
        AbsFileUpload fileUpload=getFileUploadObj(request,fileuploadtype);
        boolean isPromtAuto=true;
        if(fileUpload==null)
        {
            errorinfo="上传文件失败，未知的文件上传类型";
        }else if(errorinfo==null||errorinfo.trim().equals(""))
        {
            fileUpload.setMFormFieldValues(mFormFieldValues);
            errorinfo=fileUpload.doFileUpload(lstFieldItems,out);
            if(fileUpload.getInterceptorObj()!=null)
            {
                isPromtAuto=fileUpload.getInterceptorObj().beforeDisplayFileUploadPrompt(request,lstFieldItems,fileUpload.getMFormFieldValues(),
                        errorinfo,out);
            }
        }
        out.println("<script language='javascript'>");
        out.println("  try{hideLoadingMessage();}catch(e){}");
        out.println("</script>");
        if(isPromtAuto)
        {
            if(errorinfo==null||errorinfo.trim().equals(""))
            {
                out.println("<script language='javascript'>");
                fileUpload.promptSuccess(out,Config.getInstance().getSystemConfigValue("prompt-dialog-type","artdialog").equals("artdialog"));
                out.println("</script>");
            }else
            {
                out.println("<table style=\"margin:0px;\"><tr><td style='font-size:13px;'><font color='#ff0000'>"+errorinfo
                        +"</font></td></tr></table>");
            }
        }
        if(errorinfo!=null&&!errorinfo.trim().equals(""))
        {
            if(fileUpload!=null)
            {
                request.setAttribute("WX_FILE_UPLOAD_FIELDVALUES",fileUpload.getMFormFieldValues());
            }
            showUploadFilePage(request,out);
        }
    }

    private static void importWebresources(PrintWriter out)
    {
        List<JavascriptFileBean> lstResult=new UniqueArrayList<JavascriptFileBean>();
        List<JavascriptFileBean> lstJsTmp=Config.getInstance().getLstDefaultGlobalJavascriptFiles();
        if(lstJsTmp!=null) lstResult.addAll(lstJsTmp);
        lstJsTmp=Config.getInstance().getLstGlobalJavascriptFiles();
        if(lstJsTmp!=null) lstResult.addAll(lstJsTmp);
        Collections.sort(lstResult);
        for(JavascriptFileBean jsBeanTmp:lstResult)
        {
            out.println("<script type=\"text/javascript\"  src=\""+jsBeanTmp.getJsfileurl()+"\"></script>");
        }
        List<String> lstCss=Config.getInstance().getUlstGlobalCss();
        if(lstCss!=null)
        {
            for(String cssTmp:lstCss)
            {
                out.println("<LINK rel=\"stylesheet\" type=\"text/css\" href=\""+Tools.replaceAll(cssTmp,Consts_Private.SKIN_PLACEHOLDER,Config.skin)+"\"/>");
            }
        }
    }
    
    private static AbsFileUpload getFileUploadObj(HttpServletRequest request,String fileuploadtype)
    {
        fileuploadtype=fileuploadtype==null?"":fileuploadtype.trim();
        AbsFileUpload fileUpload=null;
        if(fileuploadtype.equalsIgnoreCase(Consts_Private.FILEUPLOADTYPE_FILEINPUTBOX))
        {
            fileUpload=new FileInputBoxUpload(request);
        }else if(fileuploadtype.equalsIgnoreCase(Consts_Private.FILEUPLOADTYPE_FILETAG))
        {
            fileUpload=new FileTagUpload(request);
        }else if(fileuploadtype.equalsIgnoreCase(Consts_Private.FILEUPLOADTYPE_DATAIMPORTREPORT))
        {
            fileUpload=new DataImportReportUpload(request);
        }else if(fileuploadtype.equalsIgnoreCase(Consts_Private.FILEUPLOADTYPE_DATAIMPORTTAG))
        {
            fileUpload=new DataImportTagUpload(request);
        }
        return fileUpload;
    }
    
    public static void downloadFile(HttpServletRequest request,HttpServletResponse response)
    {
        response.setContentType("application/x-msdownload;");
        BufferedInputStream bis=null;
        BufferedOutputStream bos=null;
        String realfilepath=null;
        try
        {
            bos=new BufferedOutputStream(response.getOutputStream());
            String serverfilename=request.getParameter("serverfilename");
            String serverfilepath=request.getParameter("serverfilepath");
            String newfilename=request.getParameter("newfilename");
            if(serverfilename==null||serverfilename.trim().equals(""))
            {
                bos.write("没有取到要下载的文件名".getBytes());
                return;
            }
            if(serverfilename.indexOf("/")>=0||serverfilename.indexOf("\\")>=0)
            {
                bos.write("指定要下载的文件名包含非法字符".getBytes());
                return;
            }
            if(serverfilepath==null||serverfilepath.trim().equals(""))
            {
                bos.write("没有取到要下载的文件路径".getBytes());
                return;
            }
            if(newfilename==null||newfilename.trim().equals("")) newfilename=serverfilename;
            newfilename=WabacusAssistant.getInstance().encodeAttachFilename(request,newfilename);
            response.setHeader("Content-disposition","attachment;filename="+newfilename);
            String realserverfilepath=null;
            if(Tools.isDefineKey("$",serverfilepath))
            {
                realserverfilepath=Config.getInstance().getResourceString(null,null,serverfilepath,true);
            }else
            {
                realserverfilepath=Tools.decodeFilePath(serverfilepath);
            }
            if(realserverfilepath==null||realserverfilepath.trim().equals(""))
            {
                bos.write(("根据"+serverfilepath+"没有取到要下载的文件路径").getBytes());
            }
            realserverfilepath=WabacusAssistant.getInstance().parseConfigPathToRealPath(realserverfilepath,Config.webroot_abspath);
            if(Tools.isDefineKey("classpath",realserverfilepath))
            {
                realserverfilepath=Tools.getRealKeyByDefine("classpath",realserverfilepath);
                realserverfilepath=Tools.replaceAll(realserverfilepath+"/"+serverfilename,"//","/").trim();
                while(realserverfilepath.startsWith("/"))
                    realserverfilepath=realserverfilepath.substring(1);//因为这种配置方式是用ClassLoader进行加载，而不是Class，所以必须不能以/打头
                bis=new BufferedInputStream(ConfigLoadManager.currentDynClassLoader.getResourceAsStream(realserverfilepath));
                response.setContentLength(bis.available());
            }else
            {
                File downloadFileObj=new File(Tools.standardFilePath(realserverfilepath+File.separator+serverfilename));
                if(!downloadFileObj.exists()||downloadFileObj.isDirectory())
                {
                    bos.write(("没有找到要下载的文件"+serverfilename).getBytes());
                    return;
                }
                
                response.setContentLength((int)downloadFileObj.length());
                bis=new BufferedInputStream(new FileInputStream(downloadFileObj));
            }
            byte[] buff=new byte[1024];
            int bytesRead;
            while((bytesRead=bis.read(buff,0,buff.length))!=-1)
            {
                bos.write(buff,0,bytesRead);
            }
        }catch(IOException e)
        {
            throw new WabacusRuntimeException("下载文件"+realfilepath+"失败",e);
        }finally
        {
            try
            {
                if(bis!=null) bis.close();
            }catch(IOException e)
            {
                log.warn("下载文件"+realfilepath+"时，关闭输入流失败",e);
            }
            try
            {
                if(bos!=null) bos.close();
            }catch(IOException e)
            {
                log.warn("下载文件"+realfilepath+"时，关闭输出流失败",e);
            }
        }
    }

    public static String invokeServerAction(HttpServletRequest request,HttpServletResponse response)
    {
        String serverClassName=request.getParameter("WX_SERVERACTION_SERVERCLASS");
        if(serverClassName==null||serverClassName.trim().equals(""))
        {
            throw new WabacusRuntimeException("没有传入要调用的服务器端类");
        }
        String params=request.getParameter("WX_SERVERACTION_PARAMS");
        List<Map<String,String>> lstParamsValue=EditableReportAssistant.getInstance().parseSaveDataStringToList(params);
        try
        {
            Object obj=ConfigLoadManager.currentDynClassLoader.loadClassByCurrentLoader(serverClassName.trim()).newInstance();
            if(!(obj instanceof IServerAction))
            {
                throw new WabacusRuntimeException("调用的服务器端类"+serverClassName+"没有实现"+IServerAction.class.getName()+"接口");
            }
            return ((IServerAction)obj).executeServerAction(request,response,lstParamsValue);
        }catch(InstantiationException e)
        {
            throw new WabacusRuntimeException("调用的服务器端类"+serverClassName+"无法实例化",e);
        }catch(IllegalAccessException e)
        {
            throw new WabacusRuntimeException("调用的服务器端类"+serverClassName+"无法访问",e);
        }
    }
    
    public static String doServerValidateOnBlur(HttpServletRequest request,HttpServletResponse response)
    {
        String inputboxid=request.getParameter("INPUTBOXID");
        if(inputboxid==null||inputboxid.trim().equals(""))
        {
            throw new WabacusRuntimeException("没有传入要校验输入框的ID");
        }
        String boxvalue=request.getParameter("INPUTBOX_VALUE");
        String othervalues=request.getParameter("OTHER_VALUES");
        StringBuffer resultBuf=new StringBuffer();
        ReportRequest rrequest=null;
        try
        {
            rrequest=new ReportRequest(request,-1);
            WabacusResponse wresponse=new WabacusResponse(response);
            rrequest.setWResponse(wresponse);
            rrequest.initReportCommon();
            List<Map<String,String>> lstOthervalues=EditableReportAssistant.getInstance().parseSaveDataStringToList(othervalues);
            Map<String,String> mOtherValues=null;
            if(lstOthervalues!=null&&lstOthervalues.size()>0) mOtherValues=lstOthervalues.get(0);
            ReportBean rbean=rrequest.getLstAllReportBeans().get(0);
            ServerValidateBean  svb=rbean.getServerValidateBean(inputboxid);
            if(svb==null||svb.getLstParams()==null||svb.getLstParams().size()==0)
            {
                throw new WabacusRuntimeException("报表"+rbean.getPath()+"上的输入框"+inputboxid+"没有配置失去焦点时进行服务器端校验，无法完成校验操作");
            }
            List<String> lstErrorMessages=new ArrayList<String>();
            boolean isSuccess=svb.validate(rrequest,boxvalue,mOtherValues,lstErrorMessages,true);
            resultBuf.append("<WX-SUCCESS-FLAG>").append(isSuccess).append("</WX-SUCCESS-FLAG>");
            if(lstErrorMessages.size()>0)
            {
                resultBuf.append("<WX-ERROR-MESSAGE>");
                for(String errormsgTmp:lstErrorMessages)
                {
                    resultBuf.append(errormsgTmp).append(";");
                }
                if(resultBuf.charAt(resultBuf.length()-1)==';') resultBuf.deleteCharAt(resultBuf.length()-1);
                resultBuf.append("</WX-ERROR-MESSAGE>");
            }
            if(rrequest.getMServerValidateDatas()!=null&&rrequest.getMServerValidateDatas().size()>0)
            {
                resultBuf.append("<WX-SERVER-DATA>{");
                for(Entry<String,String> entryTmp:rrequest.getMServerValidateDatas().entrySet())
                {
                    resultBuf.append(entryTmp.getKey()+":\""+entryTmp.getValue()+"\",");
                }
                if(resultBuf.charAt(resultBuf.length()-1)==',') resultBuf.deleteCharAt(resultBuf.length()-1);
                resultBuf.append("}</WX-SERVER-DATA>");
            }
        }catch(Exception e)
        {
            log.error("对输入框"+inputboxid+"进行服务器端校验时失败",e);
            throw new WabacusRuntimeException("对输入框"+inputboxid+"进行服务器端校验时失败",e);
        }finally
        {
            if(rrequest!=null) rrequest.destroy(false);
        }
        return resultBuf.toString();        
    }

    public static String getChartDataString(HttpServletRequest request,HttpServletResponse response)
    {
        ReportRequest rrequest=null;
        try
        {
            rrequest=new ReportRequest(request,-1);
            WabacusResponse wresponse=new WabacusResponse(response);
            rrequest.setWResponse(wresponse);
            rrequest.initGetChartDataString();
            ReportBean rbean=rrequest.getLstAllReportBeans().get(0);
            AbsReportType reportTypeObj=(AbsReportType)rrequest.getComponentTypeObj(rbean,null,true);
            if(!(reportTypeObj instanceof FusionChartsReportType))
            {
                throw new WabacusRuntimeException("报表"+rbean.getPath()+"不是图表报表，不能加载其<chart/>数据");
            }
            ((FusionChartsReportType)reportTypeObj).init();
            ((FusionChartsReportType)reportTypeObj).loadReportData(true);
            return ((FusionChartsReportType)reportTypeObj).loadStringChartData(true);
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("加载图表报表数据失败",e);
        }finally
        {
            if(rrequest!=null) rrequest.destroy(false);
        }
    }

    public static String getChartDataStringFromLocalFile(HttpServletRequest request,HttpServletResponse response)
    {
        String xmlfile=request.getParameter("xmlfilename");
        if(xmlfile==null) return "";
        String filepath=FusionChartsReportType.chartXmlFileTempPath+File.separator+xmlfile;
        File f=new File(filepath);
        if(!f.exists()||!f.isFile()) return "";
        StringBuffer resultBuf=new StringBuffer();
        BufferedReader br=null;
        try
        {
            br=new BufferedReader(new InputStreamReader(new FileInputStream(f),Config.encode));
            String content=br.readLine();
            while(content!=null)
            {
                resultBuf.append(content);
                content=br.readLine();
            }
        }catch(Exception e)
        {
            throw new WabacusRuntimeException("读取图表数据文件"+xmlfile+"失败",e);
        }finally
        {
            try
            {
                br.close();
            }catch(IOException e)
            {
                log.error("读取图表数据文件"+xmlfile+"时关闭失败",e);
            }
            f.delete();
        }
        return resultBuf.toString();
    }
}
