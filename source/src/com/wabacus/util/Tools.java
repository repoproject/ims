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
package com.wabacus.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.exception.WabacusConfigLoadingException;
import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.assistant.WabacusAssistant;

public class Tools
{
    private final static Log log=LogFactory.getLog(Tools.class);

    public static String removeHtmlTagForExcel(String srcString)
    {
        if(srcString==null||srcString.trim().equals("")) return srcString;
        srcString=RegexTools.replaceAll(srcString,"<table.*?\\>",false,"");
        srcString=RegexTools.replaceAll(srcString,"</table>",false,"");
        srcString=RegexTools.replaceAll(srcString,"<tr.*?\\>",false,"");
        srcString=RegexTools.replaceAll(srcString,"</tr>",false,"");
        srcString=RegexTools.replaceAll(srcString,"<td.*?\\>",false,"");
        srcString=RegexTools.replaceAll(srcString,"</td>",false,"");
        srcString=RegexTools.replaceAll(srcString,"<a.*?\\>",false,"");
        srcString=RegexTools.replaceAll(srcString,"</a>",false,"");
        return srcString;
    }

    public static String getRequestValue(HttpServletRequest req,String name,String defaultVal)
    {
        String value=req.getParameter(name);
        if(value==null||value.trim().equals(""))
        {
            return defaultVal;
        }
        return value.trim();
    }

    public static int getWidthHeightIntValue(String value)
    {
        if(value==null||value.trim().equals("")) return 0;
        value=value.toLowerCase().trim();
        String[] arr=WabacusAssistant.getInstance().parseHtmlElementSizeValueAndType(value);
        if(arr==null||arr.length==0) return 0;
        if(arr[0]==null||arr[0].trim().equals("")) return 0;
        return Integer.parseInt(arr[0]);
    }
    
    public static String getStrDatetime(String dateformat,Date date)
    {
        if(date==null) date=new java.util.Date();
        
        
        
        
        
        SimpleDateFormat format=new SimpleDateFormat(dateformat);
        String strDate=format.format(date);
        return strDate;
    }

    public static byte[] getBytesArrayFromInputStream(InputStream is)
    {
        try
        {
            if(is==null) return null;
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            byte[] buf=new byte[1024];
            int len;
            while((len=is.read(buf))!=-1)
            {
                baos.write(buf,0,len);
            }
            is.close();
            baos.flush();
            baos.close();
            return baos.toByteArray();
        }catch(IOException e)
        {
            log.error("从流中获取字节数组失败",e);
            return null;
        }
    }

    public static ByteArrayInputStream getInputStreamFromBytesArray(byte[] bytes)
    {
        if(bytes==null) return null;
        return new ByteArrayInputStream(bytes);
    }

    public static List<String> parseStringToList(String srcString,String token)
    {
        return parseStringToList(srcString,token,true);
    }

    public static List<String> parseStringToList(String srcString,String token,boolean includeBlank)
    {
        List<String> lstResults=new ArrayList<String>();
        if(srcString==null||srcString.trim().equals("")||token==null) return lstResults;
        srcString=srcString.trim();
        while(srcString.startsWith(token))
        {
            srcString=srcString.substring(token.length());
        }
        while(srcString.endsWith(token))
        {
            srcString=srcString.substring(0,srcString.length()-token.length());
        }
        int idx=srcString.indexOf(token);
        String tmp;
        while(idx>0)
        {
            tmp=srcString.substring(0,idx);
            if(!tmp.trim().equals("")||includeBlank) lstResults.add(tmp);
            srcString=srcString.substring(idx+token.length());
            while(srcString.startsWith(token))
            {
                srcString=srcString.substring(token.length());
            }
            idx=srcString.indexOf(token);
        }
        if(!srcString.equals("")&&(!srcString.trim().equals("")||includeBlank)) lstResults.add(srcString);
        return lstResults;
    }
    
    public static List<String> parseStringToList(String srcString,char token,char ignoretoken)
    {
        List<String> lstTemp=new ArrayList<String>();
        if(srcString!=null&&!srcString.trim().equals(""))
        {
            srcString=srcString.trim();
            while(srcString.charAt(0)==token)
            {
                srcString=srcString.substring(1).trim();
                if(srcString.equals("")) return lstTemp;
            }
            while(srcString.charAt(srcString.length()-1)==token)
            {
                srcString=srcString.substring(0,srcString.length()-1);
                if(srcString.equals("")) return lstTemp;
            }
            boolean isIn=false;
            StringBuffer sbuffer=new StringBuffer();
            for(int i=0;i<srcString.length();i++)
            {
                if(isIn)
                {
                    if(srcString.charAt(i)==ignoretoken)
                    {
                        isIn=false;
                    }
                    sbuffer.append(srcString.charAt(i));
                }else
                {
                    if(srcString.charAt(i)==ignoretoken)
                    {
                        isIn=true;
                        sbuffer.append(ignoretoken);
                    }else if(srcString.charAt(i)==token)
                    {
                        lstTemp.add(sbuffer.toString());
                        sbuffer=new StringBuffer();
                    }else
                    {
                        sbuffer.append(srcString.charAt(i));
                    }
                }
            }
            String temp=sbuffer.toString().trim();
            if(!temp.equals(""))
            {
                if(isIn)
                {
                    List<String> lstTemp2=parseStringToList(temp,String.valueOf(token));
                    for(int i=0;i<lstTemp2.size();i++)
                    {
                        lstTemp.add(lstTemp2.get(i));
                    }
                }else
                {
                    lstTemp.add(temp);
                }
            }
        }
        return lstTemp;
    }

    public static List<String> parseAllStringToList(String srcString,String token)
    {
        if(srcString==null||srcString.equals("")||token==null||token.equals("")) return null;
        boolean isEndWithToken=srcString.endsWith(token);
        List<String> lstResults=new ArrayList<String>();
        int idx=srcString.indexOf(token);
        while(idx>=0)
        {
            lstResults.add(srcString.substring(0,idx));
            srcString=srcString.substring(idx+token.length());
            idx=srcString.indexOf(token);
        }
        if(!srcString.equals(""))
        {
            lstResults.add(srcString);
        }else if(isEndWithToken) lstResults.add("");
        return lstResults;
    }
    
    public static String getRealKeyByDefine(String keysymbol,String definekey)
    {
        if(definekey==null||definekey.trim().equals("")||definekey.trim().indexOf(keysymbol)!=0)
        {
            return definekey;
        }
        String realkey=definekey.trim().substring(keysymbol.length()).trim();
        if(realkey.indexOf("{")!=0||realkey.lastIndexOf("}")!=realkey.length()-1)
        {
            return definekey;
        }
        realkey=realkey.substring(1,realkey.length()-1).trim();
        
        
        
        
        return realkey;
    }

    public static boolean isDefineKey(String keysymbol,String value)
    {
        if(value==null||value.trim().equals("")||value.trim().indexOf(keysymbol)!=0)
        {
            return false;
        }
        value=value.trim();
        int len=value.length();
        value=getRealKeyByDefine(keysymbol,value);
        if(value.length()<len) return true;
        return false;
    }

    public static String htmlEncode(String src)
    {
        if(src==null||src.trim().equals(""))
        {
            return src;
        }
        StringBuffer result=new StringBuffer();
        char character;
        for(int i=0;i<src.length();i++)
        {
            character=src.charAt(i);
            if(character=='<')
            {
                result.append("&lt;");
            }else if(character=='>')
            {
                result.append("&gt;");
            }else if(character=='\"')
            {
                result.append("&quot;");
            }else if(character=='\'')
            {
                result.append("&#039;");
            }else if(character=='\\')
            {
                
                result.append(character);
            }else
            {
                result.append(character);
            }
        }
        return result.toString();
    }

    public static String onlyHtmlEncode(String src)
    {
        if(src==null||src.trim().equals(""))
        {
            return src;
        }
        StringBuffer result=new StringBuffer();
        char character;
        for(int i=0;i<src.length();i++)
        {
            character=src.charAt(i);
            if(character=='<')
            {
                result.append("&lt;");
            }else if(character=='>')
            {
                result.append("&gt;");
            }else if(character=='\"')
            {
                result.append("&quot;");
            }else if(character=='\'')
            {
                result.append("&#039;");
            }else if(character=='\\')
            {
                //result.append("&#092;");
                result.append(character);
            }else
            {
                result.append(character);
            }
        }
        return result.toString();
    }
    
    public static String jsParamEncode(String paramvalue)
    {
        if(paramvalue==null||paramvalue.trim().equals("")) return paramvalue;
        StringBuffer resultBuffer=new StringBuffer();
        for(int i=0;i<paramvalue.length();i++)
        {
            if(paramvalue.charAt(i)=='\'')
            {
                resultBuffer.append("wx_QUOTE_wx");
            }else if(paramvalue.charAt(i)=='"')
            {
                resultBuffer.append("wx_DBLQUOTE_wx");
            }else if(paramvalue.charAt(i)=='$')
            {
                resultBuffer.append("wx_DOLLAR_wx");
            }else
            {
                resultBuffer.append(paramvalue.charAt(i));
            }
        }
        return resultBuffer.toString();
    }

    public static String jsParamDecode(String paramvalue)
    {
        if(paramvalue==null||paramvalue.trim().equals("")) return paramvalue;
        paramvalue=Tools.replaceAll(paramvalue,"wx_QUOTE_wx","'");
        paramvalue=Tools.replaceAll(paramvalue,"wx_DBLQUOTE_wx","\"");
        paramvalue=Tools.replaceAll(paramvalue,"wx_DOLLAR_wx","$");
        return paramvalue;
    }

    public static String removeSQLKeyword(String src)
    {
        if(src==null||src.trim().equals(""))
        {
            return src;
        }
        StringBuffer result=new StringBuffer();
        char character;
        for(int i=0;i<src.length();i++)
        {
            character=src.charAt(i);
            if(character=='\'')
            {
                result.append("’");
            }else if(character=='=')
            {
                result.append("＝");
            }else if(character=='\"')
            {
                result.append("“");
            }else if(character=='\\')
            {
                result.append("/");
            }else if(character==';')
            {
                result.append("；");
            }else
            {
                result.append(character);
            }
        }
        return result.toString();
    }

    public static String getRequestNameAndValueAsString(HttpServletRequest request)
    {

        if(request==null)
        {
            return "";
        }
        Enumeration names=request.getParameterNames();
        if(names==null)
        {
            return "";
        }
        String name="";
        String value="";
        StringBuffer sbuffer=new StringBuffer();
        while(names.hasMoreElements())
        {
            name=(String)names.nextElement();
            value=getRequestValue(request,name,"");
            if(value.trim().equals(""))
            {
                continue;
            }
            sbuffer.append(name+"="+value+"&");
        }
        String temp=sbuffer.toString().trim();
        if(temp.endsWith("&"))
        {
            temp=temp.substring(0,temp.length()-1);
        }
        return temp;
    }

    public static String replaceAll(String str,String src,String dest)
    {
        if(str==null||src==null||dest==null||str.equals("")||src.equals(""))
        {
            return str;
        }
        int lensrc=src.length();
        int idx=str.indexOf(src);
        while(idx>=0)
        {
            str=str.substring(0,idx)+dest+str.substring(idx+lensrc);
            idx=str.indexOf(src);
        }
        return str;
    }

    public static String replaceAllOnetime(String str,String src,String dest)
    {
        if(str==null||src==null||dest==null||str.equals("")||src.equals(""))
        {
            return str;
        }
        StringBuffer resultBuf=new StringBuffer();
        int lensrc=src.length();
        int idx=str.indexOf(src);
        while(idx>=0)
        {
            resultBuf.append(str.substring(0,idx)).append(dest);
            str=str.substring(idx+lensrc);
            idx=str.indexOf(src);
        }
        if(!str.equals("")) resultBuf.append(str);
        return resultBuf.toString();
    }
    
    public static String removeAll(String str,char c)
    {
        if(str==null)
        {
            return str;
        }
        StringBuffer sbuffer=new StringBuffer();
        for(int i=0;i<str.length();i++)
        {
            if(str.charAt(i)!=c)
            {
                sbuffer.append(str.charAt(i));
            }
        }
        return sbuffer.toString();
    }

    public static String getStandardFormatDate(String date) throws Exception
    {
        try
        {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd");
            String date2=sdf.format(sdf.parse(date));

            return date2;
        }catch(Exception e)
        {
            return null;
        }
    }

    public static String[] parseStringToArray(String str,char lefttoken,char righttoken)
    {
        if(str==null||str.trim().equals("")) return null;
        str=str.trim();
        List<String> lstString=new ArrayList<String>();
        StringBuffer buf=new StringBuffer();
        boolean isLeft=false;
        for(int k=0;k<str.length();k++)
        {
            if(str.charAt(k)==lefttoken)
            {
                if(!isLeft)
                {
                    isLeft=true;
                    buf=new StringBuffer();
                }else
                {
                    buf.append(str.charAt(k));
                }
            }else if(str.charAt(k)==righttoken)
            {
                if(isLeft)
                {
                    lstString.add(buf.toString().trim());
                }
                isLeft=false;
            }else
            {
                buf.append(str.charAt(k));
            }
        }
        if(lstString.size()>0)
        {
            return lstString.toArray(new String[lstString.size()]);
        }
        return null;
    }

    public static String[] parseStringToArray(String str,String token)
    {
        Object[] ostr=null;
        List lst_temp=parseStringToList(str,token);
        ostr=lst_temp.toArray();
        String[] strStr=new String[ostr.length];
        for(int j=0;j<ostr.length;j++)
        {
            strStr[j]=(String)ostr[j];
        }
        return strStr;
    }

    public static String removeAllSpace(String src)
    {
        if(src==null)
        {
            return src;
        }
        src=src.trim();
        StringBuffer sbuffer=new StringBuffer();
        for(int i=0;i<src.length();i++)
        {
            if(src.charAt(i)!=' ')
            {
                sbuffer.append(src.charAt(i));
            }
        }
        return sbuffer.toString();
    }

    public static String getXmlValueByTag(String xmlvalue,String startTag,String endTag)
    {
        if(xmlvalue==null)
        {
            return null;
        }
        if(startTag==null||endTag==null)
        {
            return "";
        }
        xmlvalue=xmlvalue.trim();
        startTag=startTag.trim();
        endTag=endTag.trim();
        
        
        
        
        int lenxml=xmlvalue.length();
        int lenstart=startTag.length();
        int lenend=endTag.length();
        if(lenxml<=lenstart+lenend)
        {
            return "";
        }
        int idxstart=xmlvalue.toLowerCase().indexOf(startTag.toLowerCase());
        int idxend=xmlvalue.toLowerCase().indexOf(endTag.toLowerCase());
        if(idxstart==-1||idxend==-1||idxstart>=idxend)
        {
            return "";
        }
        idxstart=idxstart+lenstart;
        String value=xmlvalue.substring(idxstart,idxend);
        return value.trim();
    }

    public static String removeBracketAndContentInside(String str,boolean throwExceptionIfNotMatch)
    {
        if(str==null||str.trim().equals("")) return str;
        String strOld=str;
        str=replaceCharacterInQuote(str,'(',"WX_QUOTE_LEFT",true);
        str=replaceCharacterInQuote(str,')',"WX_QUOTE_RIGHT",true);
        boolean flag=false;
        int countleft=0,i=0;
        StringBuffer resultBuf=new StringBuffer();
        for(int len=str.length();i<len;i++)
        {
            if(str.charAt(i)=='(')
            {
                if(!flag) flag=true;
                countleft++;
            }else if(str.charAt(i)==')')
            {
                if(!flag) break;
                countleft--;
                if(countleft==0) flag=false;
            }else if(!flag)
            {
                resultBuf.append(str.charAt(i));
            }
        }
        if(countleft==0&&i==str.length()) str=resultBuf.toString();
        if(throwExceptionIfNotMatch&&(str.indexOf("(")>=0||str.indexOf(")")>=0))
        {
            throw new WabacusConfigLoadingException("解析字符串"+strOld+"失败，左右括号不匹配");
        }
        str=Tools.replaceAll(str,"WX_QUOTE_LEFT","(");
        str=Tools.replaceAll(str,"WX_QUOTE_RIGHT",")");
        return str;
    }

    public static String removeSubStr(String src,String startstr,String endstr,String link)
    {
        if(src==null||src.trim().equals("")||startstr==null||startstr.equals("")||endstr==null||endstr.equals(""))
        {
            return src;
        }
        if(link==null) link="";
        int idx=src.indexOf(startstr);
        while(idx>=0)
        {
            String srcstart=src.substring(0,idx);
            src=src.substring(idx+startstr.length());
            idx=src.indexOf(endstr);
            if(idx<0)
            {
                src=srcstart;
                break;
            }
            String srcend=src.substring(idx+endstr.length());
            if(srcend.equals(""))
            {
                src=srcstart;
                break;
            }
            src=srcstart+link+srcend;
            idx=src.indexOf(startstr);
        }
        return src;
    }

    public static String replaceUrlParamValue(String url,String paramname,String newvalue)
    {
        if(url==null||url.trim().equals("")) return url;
        if(paramname==null||paramname.trim().equals("")) return url;
        url=removeSubStr(url,"?"+paramname+"=","&","?");
        url=removeSubStr(url,"&"+paramname+"=","&","&");
        if(newvalue!=null)
        {
            if(url.indexOf('?')>0)
            {
                url=url+'&';
            }else
            {
                url=url+'?';
            }
            url=url+paramname+'='+newvalue;
        }
        return url;
    }
    
    public static String getParamvalueFromUrl(String url,String paramname)
    {
        if(url==null||url.trim().equals("")) return "";
        if(paramname==null||paramname.trim().equals("")) return "";
        int idx=url.indexOf("?"+paramname+"=");
        if(idx<=0)
        {
            idx=url.indexOf("&"+paramname+"=");
        }
        if(idx<=0) return "";
        String paramvalue=url.substring(idx+("&"+paramname+"=").length());
        idx=paramvalue.indexOf("&");
        if(idx>=0) paramvalue=paramvalue.substring(0,idx);
        return paramvalue;
    }
    
    public static String convertBetweenStringAndAscii(String src,boolean flag)
    {
        if(src==null||src.equals("")) return src;
        StringBuffer sbuffer=new StringBuffer();
        if(flag)
        {
            char[] carrays=src.toCharArray();
            for(int i=0;i<carrays.length;i++)
            {
                sbuffer.append((int)carrays[i]);
                if(i!=carrays.length-1) sbuffer.append("_");
            }
        }else
        {
            String[] chars=src.split("_");
            for(int i=0;i<chars.length;i++)
            {
                sbuffer.append((char)Integer.parseInt(chars[i]));
            }
        }
        return sbuffer.toString();
    }

    public static String convertBetweenStringAndHex(String src,boolean flag)
    {
        if(src==null||src.equals("")) return src;
        if(flag)
        {
            byte[] bytes=src.getBytes();
            StringBuffer sbuffer=new StringBuffer();
            for(int i=0;i<bytes.length;i++)
            {
                sbuffer.append(Integer.toHexString(bytes[i]&0xFF));

            }
            return sbuffer.toString();
        }else
        {
            byte[] bytes=new byte[src.length()/2];
            try
            {
                for(int i=0;i<bytes.length;i++)
                {
                    bytes[i]=(byte)(0xff&Integer.parseInt(src.substring(i*2,i*2+2),16));

                }
            }catch(Exception e)
            {
                e.printStackTrace();
                return Tools.htmlEncode(src);
            }
            return Tools.htmlEncode(new String(bytes));
        }
    }

    public static void delete(File f,String suffix,boolean inherit) throws IOException
    {
        if(f.exists())
        {
            if(f.isFile())
            {
                if(suffix!=null&&!suffix.trim().equals(""))
                {
                    String temp=suffix.toLowerCase().trim();
                    String filename=f.getName().toLowerCase();
                    if(filename.endsWith(temp))
                    {
                        f.delete();
                    }
                }else
                {
                    f.delete();
                }
            }else if(f.isDirectory())
            {
                File[] files=f.listFiles();
                for(int i=0;i<files.length;i++)
                {
                    if(files[i].isFile()||inherit) delete(files[i],suffix,inherit);
                }
                if(f.listFiles().length==0)
                {
                    f.delete();
                }
            }
        }
    }

    public static String getPropertyValueByName(String propname,String src,
            boolean includeValueInStyle)
    {
        if(propname==null||propname.trim().equals("")) return null;
        if(src==null||src.trim().equals("")) return null;
        src=src.trim();
        propname=propname.toLowerCase().trim();
        Map<String,String> mAttributes=new HashMap<String,String>();
        RegexTools.parseHtmlTagAttribute(src,mAttributes);
        if(mAttributes.size()==0) return null;
        mAttributes=Tools.changeMapKeyToLowcase(mAttributes);//将键全部转换为小写形式
        if(mAttributes.containsKey(propname)) return mAttributes.get(propname).trim();
        if(!includeValueInStyle||!mAttributes.containsKey("style")) return null;
        return getPropertyValueFromStyle(propname,mAttributes.get("style"));
    }

    public static String getPropertyValueFromStyle(String propname,String style)
    {
        if(style==null||style.trim().equals("")) return null;
        if(propname==null||propname.trim().equals("")) return null;
        propname=propname.toLowerCase().trim();
        List<String> lstStyleProperties=Tools.parseStringToList(style,";");
        String propnameTmp;
        for(String propTmp:lstStyleProperties)
        {
            if(propTmp==null||propTmp.trim().indexOf(":")<=0) continue;
            propnameTmp=propTmp.substring(0,propTmp.indexOf(":"));
            if(propname.equals(propnameTmp.toLowerCase().trim()))
            {
                return propTmp.substring(propTmp.indexOf(":")+1).trim();
            }
        }
        return null;
    }
    
    public static String removePropertyValueByName(String propname,String src)
    {
        if(propname==null||propname.trim().equals("")) return src;
        if(src==null||src.trim().equals("")) return src;
        src=src.trim();
        propname=propname.toLowerCase().trim();
        Map<String,String> mAttributes=new HashMap<String,String>();
        String restStyleProps=RegexTools.parseHtmlTagAttribute(src,mAttributes);
        StringBuffer resultBuf=new StringBuffer();
        resultBuf.append(restStyleProps).append(" ");
        for(Entry<String,String> entryTmp:mAttributes.entrySet())
        {
            if(propname.equals(entryTmp.getKey().toLowerCase())) continue;
            resultBuf.append(entryTmp.getKey()).append("=\"").append(entryTmp.getValue()).append("\" ");
        }
        return resultBuf.toString();
    }

    private static List<String> lstJsPropertyNames=new ArrayList<String>();
    static
    {
        lstJsPropertyNames.add("onblur");
        lstJsPropertyNames.add("onchange");
        lstJsPropertyNames.add("onclick");
        lstJsPropertyNames.add("ondblclick");
        lstJsPropertyNames.add("onfocus");
        lstJsPropertyNames.add("onkeydown");
        lstJsPropertyNames.add("onkeypress");
        lstJsPropertyNames.add("onkeyup");
        lstJsPropertyNames.add("onmousedown");
        lstJsPropertyNames.add("onmousemove");
        lstJsPropertyNames.add("onmouseout");
        lstJsPropertyNames.add("onmouseover");
        lstJsPropertyNames.add("onselect");
    }
    public static String mergeHtmlTagPropertyString(String styleproperty1,String styleproperty2,int overwritetype)
    {
        styleproperty1=styleproperty1==null?"":styleproperty1.trim();
        styleproperty2=styleproperty2==null?"":styleproperty2.trim();
        if(styleproperty1.equals("")) return styleproperty2;
        if(styleproperty2.equals("")) return styleproperty1;
        StringBuffer resultBuf=new StringBuffer();
        Map<String,String> mAttributes1=new HashMap<String,String>();
        String restStyleProps1=RegexTools.parseHtmlTagAttribute(styleproperty1,mAttributes1);
        Map<String,String> mAttributes2=new HashMap<String,String>();
        String restStyleProps2=RegexTools.parseHtmlTagAttribute(styleproperty2,mAttributes2);
        mAttributes1=changeMapKeyToLowcase(mAttributes1);
        mAttributes2=changeMapKeyToLowcase(mAttributes2);
        resultBuf.append(restStyleProps1.trim()).append(" ").append(restStyleProps2).append(" ");
        String propNameTmp;
        String propValTmp;
        for(Entry<String,String> attrEntryTmp:mAttributes1.entrySet())
        {
            propNameTmp=attrEntryTmp.getKey().trim();
            propValTmp=attrEntryTmp.getValue();
            if(mAttributes2.containsKey(propNameTmp))
            {
                String propVal2Tmp=mAttributes2.get(propNameTmp);
                propVal2Tmp=propVal2Tmp==null?"":propVal2Tmp.trim();
                if(overwritetype==0||(overwritetype==1&&!lstJsPropertyNames.contains(propNameTmp.toLowerCase())&&!propNameTmp.equalsIgnoreCase("style")))
                {
                    propValTmp=propVal2Tmp;
                }else
                {
                    if(propNameTmp.equalsIgnoreCase("style"))
                    {
                        propValTmp=mergeHtmlTagStyleValue(propValTmp,propVal2Tmp);
                    }else if(lstJsPropertyNames.contains(propNameTmp.toLowerCase())&&!propValTmp.trim().equals("")&&!propVal2Tmp.trim().equals("")
                            &&!propValTmp.endsWith(";"))
                    {
                        propValTmp=propValTmp+";"+propVal2Tmp;
                    }else
                    {
                        propValTmp=propValTmp+" "+propVal2Tmp;
                    }
                }
                mAttributes2.remove(propNameTmp);
            }
            resultBuf.append(propNameTmp).append("=\"").append(propValTmp).append("\" ");
        }
        for(Entry<String,String> attrEntryTmp:mAttributes2.entrySet())
        {
            resultBuf.append(attrEntryTmp.getKey()).append("=\"").append(attrEntryTmp.getValue()).append("\" ");
        }
        return resultBuf.toString().trim();
    }
    
    private static String mergeHtmlTagStyleValue(String style1,String style2)
    {
        style1=style1==null?"":style1.trim();
        style2=style2==null?"":style2.trim();
        if(style1.equals("")) return style2;
        if(style2.equals("")) return style1;
        Map<String,String> mAttributes1=new HashMap<String,String>();
        RegexTools.parseHtmlStyleValue(style1,mAttributes1);
        Map<String,String> mAttributes2=new HashMap<String,String>();
        RegexTools.parseHtmlStyleValue(style2,mAttributes2);
        mAttributes1=changeMapKeyToLowcase(mAttributes1);
        mAttributes2=changeMapKeyToLowcase(mAttributes2);
        StringBuffer resultBuf=new StringBuffer();
        String propNameTmp;
        String propValTmp;
        for(Entry<String,String> attrEntryTmp:mAttributes1.entrySet())
        {
            propNameTmp=attrEntryTmp.getKey().trim();
            propValTmp=attrEntryTmp.getValue();
            if(mAttributes2.containsKey(propNameTmp))
            {//style2中有此属性名和值，则覆盖掉
                String propVal2Tmp=mAttributes2.get(propNameTmp);
                propValTmp=propVal2Tmp==null?"":propVal2Tmp.trim();
                mAttributes2.remove(propNameTmp);
            }
            if(propValTmp==null||propValTmp.trim().equals("")) continue;
            resultBuf.append(propNameTmp).append(":").append(propValTmp.trim()).append(";");
        }
        for(Entry<String,String> attrEntryTmp:mAttributes2.entrySet())
        {
            propNameTmp=attrEntryTmp.getKey().trim();
            propValTmp=attrEntryTmp.getValue();
            if(propValTmp==null||propValTmp.trim().equals("")) continue;
            resultBuf.append(propNameTmp).append(":").append(propValTmp.trim()).append(";");
        }
        return resultBuf.toString();
    }

    public static String mergeJsonValue(String jsonvalue1,String jsonvalue2)
    {
        jsonvalue1=jsonvalue1==null?"":jsonvalue1.trim();
        jsonvalue2=jsonvalue2==null?"":jsonvalue2.trim();
        String resultStr=null;
        
        if(jsonvalue1.equals(""))
        {
            resultStr=jsonvalue2;
        }else if(jsonvalue2.equals(""))
        {
            resultStr=jsonvalue1;
        }else
        {
            Map<String,String> mAttributes1=new HashMap<String,String>();
            RegexTools.parseJsonValue(jsonvalue1,mAttributes1);
            Map<String,String> mAttributes2=new HashMap<String,String>();
            RegexTools.parseJsonValue(jsonvalue2,mAttributes2);
            StringBuffer resultBuf=new StringBuffer();
            String propNameTmp;
            String propValTmp;
            for(Entry<String,String> attrEntryTmp:mAttributes1.entrySet())
            {
                propNameTmp=attrEntryTmp.getKey().trim();
                propValTmp=attrEntryTmp.getValue();
                if(mAttributes2.containsKey(propNameTmp))
                {
                    String propVal2Tmp=mAttributes2.get(propNameTmp);
                    propValTmp=propVal2Tmp==null?"":propVal2Tmp.trim();
                    mAttributes2.remove(propNameTmp);
                }
                if(propValTmp==null||propValTmp.trim().equals("")) continue;
                resultBuf.append(propNameTmp).append(":").append(propValTmp.trim()).append(",");
            }
            for(Entry<String,String> attrEntryTmp:mAttributes2.entrySet())
            {
                propNameTmp=attrEntryTmp.getKey().trim();
                propValTmp=attrEntryTmp.getValue();
                if(propValTmp==null||propValTmp.trim().equals("")) continue;
                resultBuf.append(propNameTmp).append(":").append(propValTmp.trim()).append(",");
            }
            resultStr=resultBuf.toString();
        }
        if(!resultStr.equals("")&&resultStr.charAt(resultStr.length()-1)==',')
        {
            resultStr=resultStr.substring(0,resultStr.length()-1);
        }
        return resultStr;
    }
    
    public static Map<String,String> changeMapKeyToLowcase(Map<String,String> mChangedMapObj)
    {
        if(mChangedMapObj==null) return null;
        Map<String,String> mResultMap=new HashMap<String,String>();
        for(Entry<String,String> entryTmp:mChangedMapObj.entrySet())
        {
            mResultMap.put(entryTmp.getKey().toLowerCase(),entryTmp.getValue());
        }
        return mResultMap;
    }
    public static String standardHtmlTagProperties(String propString)
    {
        if(propString==null||propString.trim().equals("")) return "";
        propString=propString.trim();
        propString=Tools.replaceAll(propString,"&quot;","\"");
        Map<String,String> mTemp=new HashMap<String,String>();
        RegexTools.getTagAttributes(propString,"([A-Za-z0-9_]+=\\s*'[^']*')","([A-Za-z0-9_]+)=\\s*'(.*)'",mTemp);
        for(Entry<String,String> entryTmp:mTemp.entrySet())
        {
            if(entryTmp.getValue()==null||entryTmp.getValue().trim().equals("")) continue;
            if(entryTmp.getValue().indexOf("\"")>=0)
            {
                throw new WabacusRuntimeException("解析html标签属性字符串"+propString+"失败，属性名"+entryTmp.getKey()+"对应的属性值"+entryTmp.getValue()+"中出现了\"号");
            }
        }
        StringBuffer resultBuf=new StringBuffer();
        String quote=null;
        char c;
        for(int i=0,len=propString.length();i<len;i++)
        {
            c=propString.charAt(i);
            resultBuf.append(c);
            if(quote!=null)
            {
                if(quote.equals("\"")&&c=='\"'||quote.equals("'")&&c=='\'') quote=null;
            }else
            {
                if(c=='\"')
                {
                    quote="\"";
                }else if(c=='\'')
                {
                    quote="'";
                }else if(c=='=')
                {
                    int j=i+1;
                    char quotetype=' ';
                    for(;j<len;j++)
                    {
                        if(propString.charAt(j)==' ') continue;
                        if(propString.charAt(j)=='\"')
                        {
                            quotetype='\"';
                        }else if(propString.charAt(j)=='\'')
                        {
                            quotetype='\'';
                        }
                        
                        break;
                    }
                    if(j==len)
                    {
                        resultBuf.append("\"\"");
                        i=j;
                    }else if(quotetype==' ')
                    {
                        resultBuf.append("\"");
                        for(;j<len;j++)
                        {
                            if(propString.charAt(j)==' ') break;//找到了空格，则说明此属性值结束
                            resultBuf.append(propString.charAt(j));
                        }
                        resultBuf.append("\" ");
                        i=j;
                    }
                }
            }
        }
        if(quote!=null)
        {
            throw new WabacusRuntimeException("解析html标签属性字符串"+propString+"失败，引号"+quote+"没有成对");
        }
        return resultBuf.toString();
    }
    
    public static String replaceCharacterInQuote(String srcstring,char srcchar,String dest,boolean bothquot)
    {
        if(srcstring==null||srcstring.trim().equals("")) return srcstring;
        if(srcchar=='\"'||(bothquot&&srcchar=='\''))
        {
            throw new WabacusRuntimeException("不能将srcchar设置为引号字符进行替换");
        }
        StringBuffer resultBuf=new StringBuffer();
        char c;
        String quote=null;
        for(int i=0,len=srcstring.length();i<len;i++)
        {
            c=srcstring.charAt(i);
            if(quote!=null)
            {
                if(c==srcchar)
                {
                    resultBuf.append(dest);
                }else
                {
                    resultBuf.append(c);
                    if(quote.equals("\"")&&c=='\"'||quote.equals("'")&&c=='\'')
                    {
                        quote=null;
                    }
                }
            }else
            {
                resultBuf.append(c);
                if(c=='\"')
                {
                    quote="\"";
                }else if(c=='\''&&bothquot)
                {
                    quote="'";
                }
            }
        }
        if(quote!=null)
        {
            throw new WabacusRuntimeException("字符串"+srcstring+"的引号"+quote+"没有成对");
        }
        return resultBuf.toString();
    }
    
    public static String standardFilePath(String filePath)
    {
        if(filePath==null||filePath.trim().equals("")) return filePath;
        filePath=filePath.trim();
        if(File.separator.equals("\\"))
        {
            filePath=replaceAll(filePath,"/","\\");
            filePath=replaceAll(filePath,"\\\\","\\");
        }else if(File.separator.equals("/"))
        {
            filePath=replaceAll(filePath,"\\","/");
            filePath=replaceAll(filePath,"//","/");
        }
        return filePath;
    }

    public static String copyMapData(Map mSrc,Map mDest,boolean unique)
    {
        if(mSrc!=null&&mSrc.size()>0&&mDest!=null)
        {
            Iterator itKeys=mSrc.keySet().iterator();
            String key;
            if(unique)
            {
                while(itKeys.hasNext())
                {
                    key=(String)itKeys.next();
                    if(mDest.containsKey(key))
                    {
                        return key;
                    }
                }
            }
            itKeys=mSrc.keySet().iterator();
            while(itKeys.hasNext())
            {
                key=(String)itKeys.next();
                mDest.put(key,mSrc.get(key));
            }
        }
        return null;
    }

    public static String addPropertyValueToStylePropertyIfNotExist(String styleproperty,String propname,String propvalue)
    {
        String oldValue=getPropertyValueByName(propname,styleproperty,true);
        if(oldValue==null||oldValue.trim().equals(""))
        {
            if(styleproperty==null)
            {
                styleproperty=propname+"=\""+propvalue+"\"";
            }else
            {
                styleproperty+=(" "+propname+"=\""+propvalue+"\"");
            }
        }
        return styleproperty;
    }
    
    public static String urlDecode(String url)
    {
        if(url==null||url.trim().equals("")||url.indexOf("?")<=0) return url;
        int idx=url.indexOf("?");
        StringBuffer urlBuf=new StringBuffer();
        urlBuf.append(url.substring(0,idx)).append("?");
        url=url.substring(idx+1);
        String param;
        while(idx>0)
        {
            idx=url.indexOf("&");
            if(idx>0)
            {
                param=url.substring(0,idx);
                urlBuf.append(decodeUrlParam(param));
                urlBuf.append("&");
                url=url.substring(idx+1);
                idx=url.indexOf("&");
            }
        }
        if(!url.trim().equals(""))
        {
            urlBuf.append(decodeUrlParam(url));
        }
        return urlBuf.toString();
    }

    private static String decodeUrlParam(String param)
    {
        if(param==null||param.trim().equals("")||param.indexOf("=")<=0) return param;
        int idx=param.indexOf("=");
        String paramname=param.substring(0,idx);
        String paramvalue=param.substring(idx+1);
        if(paramname.trim().equals("")||paramvalue.trim().equals("")) return param;
        try
        {
            paramvalue=URLDecoder.decode(paramvalue,"UTF-8");
        }catch(UnsupportedEncodingException e)
        {
            log.warn("URL解码"+param+"失败",e);
        }
        return paramname+"="+paramvalue;
    }

    public static String formatStringBlank(String src)
    {
        src=src==null?"":src.trim();
        src=Tools.replaceAll(src,"\r"," ");
        src=Tools.replaceAll(src,"\n"," ");
        src=Tools.replaceAll(src,"\t"," ");
        src=Tools.replaceAll(src,"  "," ");
        return src;
    }

    public static String getDateFormatFromDateString(String datestr)
    {
        if(datestr==null||datestr.trim().equals("")) return null;
        List<String> lstDate=Tools.parseStringToList(datestr," ");
        String date=null;
        String time=null;
        if(lstDate.size()==2)
        {
            date=lstDate.get(0).trim();
            time=lstDate.get(1).trim();
        }else
        {
            String strTmp=lstDate.get(0);
            if(strTmp.indexOf(":")>0)
            {
                time=strTmp;
            }else
            {
                date=strTmp;
            }
        }
        StringBuffer resultBuf=new StringBuffer();
        if(date!=null&&!date.trim().equals(""))
        {
            List<String> lst=null;
            String seperate="";
            if(date.indexOf("-")>0)
            {
                lst=parseStringToList(date,"-");
                seperate="-";
            }else if(date.indexOf("/")>0)
            {//是MM/dd/yyyy格式的日期
                lst=parseStringToList(date,"/");
                seperate="/";
            }else
            {
                lst=new ArrayList<String>();
                lst.add(date);
            }
            String str=lst.get(0).trim();
            boolean hasYear=false;
            boolean hasMonth=false;
            boolean hasDay=false;
            if(str.length()==4)
            {
                resultBuf.append("yyyy");
                hasYear=true;
            }else if(str.length()==2||str.length()==1)
            {//可能是01或1
                resultBuf.append("MM");
                hasMonth=true;
            }else
            {
                return null;
            }
            if(lst.size()>1)
            {
                str=lst.get(1);
                if(str.length()==4)
                {
                    if(hasYear) return null;
                    resultBuf.append(seperate).append("yyyy");
                    hasYear=true;
                }else if(str.length()==2||str.length()==1)
                {
                    if(hasMonth)
                    {
                        resultBuf.append(seperate).append("dd");
                        hasDay=true;
                    }else
                    {
                        resultBuf.append(seperate).append("MM");
                        hasMonth=true;
                    }
                }else
                {
                    return null;
                }
            }
            if(lst.size()>2)
            {
                str=lst.get(2);
                if(str.length()==4)
                {
                    if(hasYear) return null;
                    resultBuf.append(seperate).append("yyyy");
                }else if(str.length()==2||str.length()==1)
                {
                    if(hasDay)
                    {
                        return null;
                    }
                    resultBuf.append(seperate).append("dd");
                }
            }
        }
        if(time!=null&&!time.trim().equals(""))
        {
            if(date!=null&&!date.trim().equals(""))
            {
                resultBuf.append(" ");
            }
            List<String> lstTime=Tools.parseStringToList(time,":");
            String str=lstTime.get(0);
            if(str.length()!=2&&str.length()!=1) return null;
            resultBuf.append("HH");
            if(lstTime.size()>1)
            {
                str=lstTime.get(1).trim();
                if(str.length()!=2&&str.length()!=1) return null;
                resultBuf.append(":mm");
            }
            if(lstTime.size()>2)
            {
                str=lstTime.get(2).trim();
                if(str.indexOf(".")>0)
                {
                    String sec=str.substring(0,str.indexOf("."));
                    if(sec.length()!=2&&sec.length()!=1) return null;
                    resultBuf.append(":ss");
                    String milsec=str.substring(str.indexOf(".")+1);
                    if(milsec.length()==0) return null;
                    resultBuf.append(".SSS");
                }else
                {
                    if(str.length()!=2&&str.length()!=1) return null;
                    resultBuf.append(":ss");
                }
            }
        }
        return resultBuf.toString();
    }
    
    
    private static Map<String,String> mEncodedFilePaths=new HashMap<String,String>();
    
    private static Map<String,String> mDecodedFilePaths=new HashMap<String,String>();
    
    public static String encodeFilePath(String filepath)
    {
        if(filepath==null||filepath.trim().equals("")) return "";
        if(mEncodedFilePaths.containsKey(filepath.toLowerCase().trim())) return mEncodedFilePaths.get(filepath.toLowerCase().trim());
        String filecode=(int)(Math.random()*10000)+"_"+(int)(Math.random()*10000);
        mEncodedFilePaths.put(filepath.toLowerCase().trim(),filecode);
        mDecodedFilePaths.put(filecode,filepath);
        return filecode;
    }
    
    public static String decodeFilePath(String filecode)
    {
        if(filecode==null||filecode.trim().equals("")) return "";
        return mDecodedFilePaths.get(filecode);
    }
    
    public static String getRandomString(int length)
    {
        if(length<=0) return "";
        StringBuffer resultBuf=new StringBuffer();
        for(int i=0;i<length;i++)
        {
            resultBuf.append((int)(Math.random()*10));
        }
        return resultBuf.toString();
    }
    
    public static boolean isEmpty(String string,boolean ignoreWhiteSpace)
    {
        if(string==null) return true;
        return ignoreWhiteSpace?string.trim().equals(""):string.equals("");
    }
    
    public static String substring(String str,Integer start,Integer length)
    {
        return substring(str,start.intValue(),length.intValue());
    }

    public static String substring(String str,int start,int length)
    {
        if(str!=null)
        {
            str=str.trim();
            if(length>0)
            {
                if(str.length()>start+length)
                {
                    str=str.substring(start,length);
                }
            }else
            {
                if(str.length()>start)
                {
                    str=str.substring(start);
                }
            }
        }
        return str;
    }

    public static String formatDouble(String srcString,String pattern)
    {
        try
        {
            if(srcString==null||srcString.trim().equals(""))
            {
                return "";
            }
            DecimalFormat df=new DecimalFormat(pattern);
            srcString=df.format(Double.parseDouble(srcString));
            return srcString;
        }catch(Exception e)
        {
            log.error("以"+pattern+"格式格式化"+srcString+"时，发生了异常：",e);
            return srcString;
        }
    }

    public static String formatLong(String srcString,String pattern)
    {
        try
        {
            if(srcString==null||srcString.trim().equals(""))
            {
                return "";
            }
            DecimalFormat df=new DecimalFormat(pattern);
            srcString=df.format(Long.parseLong(srcString));
            return srcString;
        }catch(Exception e)
        {
            log.error("以"+pattern+"格式格式化"+srcString+"时，发生了异常：",e);
            return srcString;
        }
    }
}
