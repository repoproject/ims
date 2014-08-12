<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>中心运行管理软件</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!-- link rel="stylesheet" type="text/css" href="<%=basePath%>css/style.css"> -->
	<link rel="stylesheet" type="text/css" href="<%=basePath%>css/wabacus_system.css">
	<!--[if IE]>
		<link href="<%=basePath%>css/iestyle.css" rel="stylesheet" type="text/css" media="screen"/>
	<![endif]-->
	<script type="text/javascript" src="<%=basePath%>js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/jquery.json-2.4.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/FusionCharts.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/FusionChartsExportComponent.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/localJS/getServerData.js"></script>
  </head>
  