<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();//request.getParameter("username")
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>QUINTILES库存管理系统</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" type="text/css" href="css/login.css">
	<script type="text/javascript" src="<%=basePath%>js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/MD5.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/localJS/login.js"></script>
	
  </head>
  
  <body>
    <div class="outer">
    	<form action="servlet/Login" method="post" onsubmit="return loginSubmit(this,loginCallBack)">
    		<input type="text" name="username" size="20" class="inputField" id="username"/>
    		<input type="password" name="password" size="20" class="login_input" id="password"/>
    		<input class="submit" type="submit" value=" "  id="submit"/>
    		<!-- <input class="reset" type="reset" value=" " id="reset"/> -->
    	</form>
    	
    </div>
    
  </body>
  
</html>
