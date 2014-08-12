<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();//request.getParameter("username")
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>中心运行管理系统</title>
    <script type="text/javascript">
    	function jump(){
    		if(window != top){
    			top.location.href = "login.jsp";
    		}else{
    			window.location.href = "login.jsp";
    		}
    	
    	}
    </script>
	
  </head>
  
  <body onload="jump()">
    
  </body>
  
</html>
