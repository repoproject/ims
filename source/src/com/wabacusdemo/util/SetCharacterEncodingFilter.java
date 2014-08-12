package com.wabacusdemo.util;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.wabacus.config.Config;

/**
 * 
 * 设置所有WABACUS文件编码格式
 * @author jyp
 * @修改人：zhouhl
 * @修改时间：2013-12-30
 *
 */
public class SetCharacterEncodingFilter implements Filter
{
	
	//定义过滤器配置对象
    private FilterConfig filterConfig = null;
    
    /**
	 * 初始化过滤器配置对象
	 * @author jyp
	 * @修改人：zhouhl
	 * @修改时间：2013-12-30
	 * @返回值：无
	 */
    public void init(FilterConfig filterConfig) throws ServletException
    {

        this.filterConfig = filterConfig;
    }
    
    /**
	 * 完成对请求编码的拦截和设定
	 * @author jyp
	 * @修改人：zhouhl
	 * @修改时间：2013-12-30
	 * @返回值：无
	 */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
    {
        try
        {
            if(!Config.encode.equalsIgnoreCase("utf-8"))
            {
            	//如果当前项目采用的不是UTF-8编码
                HttpServletRequest httpRequest=(HttpServletRequest)request;
                String contentType=httpRequest.getContentType();
                if (contentType != null
                        && contentType.toLowerCase().startsWith(
                                "application/x-www-form-urlencoded; charset=utf-8"))
                {
                	//报表提交，编码设置为UTF-8
                    request.setCharacterEncoding("UTF-8");
                }else
                {
                	//编码设置为当前项目的编码
                    request.setCharacterEncoding(Config.encode);
                }
                response.setContentType("text/html; charset="+Config.encode);
            }else
            {
            	//项目采用的是UTF-8时，直接设置页面编码为UTF-8
                request.setCharacterEncoding("UTF-8");
                response.setContentType("text/html;charset=UTF-8");
            }
            chain.doFilter(request,response);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
	 * 销毁过滤器配置对象
	 * @author jyp
	 * @修改人：zhouhl
	 * @修改时间：2013-12-30
	 * @返回值：无
	 */
    public void destroy()
    {
        this.filterConfig = null;
    }
}