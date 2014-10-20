<%@ page language="java" import="java.util.*,java.text.*" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String username = "";
	username = (session.getAttribute("username")).toString();
	int role = Integer.parseInt((session.getAttribute("role").toString()));
	response.setHeader("Cache-Control","no-store");
	response.setDateHeader("Expires",0);
	response.setHeader("Pragma","no-cache");
	
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=7" />
<title>出入库管理系统（IMS）</title>

<link href="themes/default/style.css" rel="stylesheet" type="text/css" media="screen"/>
<link href="css/core.css" rel="stylesheet" type="text/css" media="screen"/>
<link href="themes/css/print.css" rel="stylesheet" type="text/css" media="print"/>

<link href="css/index.css" rel="stylesheet" type="text/css" media="screen"/>
<!-- link href="css/systemmonitor.css" rel="stylesheet" type="text/css" media="screen"/> -->
<!--[if IE]>
<link href="themes/css/ieHack.css" rel="stylesheet" type="text/css" media="screen"/>
<![endif]-->

<!--[if lte IE 9]>
<script src="js/speedup.js" type="text/javascript"></script>
<![endif]-->

<script src="js/jquery-1.7.2.js" type="text/javascript"></script>
<script type="text/javascript" src="js/MD5.js"></script>
<script src="js/jquery.cookie.js" type="text/javascript"></script>
<script src="js/jquery.validate.js" type="text/javascript"></script>
<script src="js/jquery.bgiframe.js" type="text/javascript"></script>

<!-- svg图表  supports Firefox 3.0+, Safari 3.0+, Chrome 5.0+, Opera 9.5+ and Internet Explorer 6.0+ -->
<!-- script type="text/javascript" src="chart/raphael.js"></script>
<script type="text/javascript" src="chart/g.raphael.js"></script>
<script type="text/javascript" src="chart/g.bar.js"></script>
<script type="text/javascript" src="chart/g.line.js"></script>
<script type="text/javascript" src="chart/g.pie.js"></script>
<script type="text/javascript" src="chart/g.dot.js"></script-->

<script src="js/dwz.core.js" type="text/javascript"></script>
<script src="js/dwz.util.date.js" type="text/javascript"></script>
<script src="js/dwz.validate.method.js" type="text/javascript"></script>
<script src="js/dwz.regional.zh.js" type="text/javascript"></script>
<script src="js/dwz.barDrag.js" type="text/javascript"></script>
<script src="js/dwz.drag.js" type="text/javascript"></script>
<script src="js/dwz.tree.js" type="text/javascript"></script>
<script src="js/dwz.accordion.js" type="text/javascript"></script>
<script src="js/dwz.ui.js" type="text/javascript"></script>
<script src="js/dwz.theme.js" type="text/javascript"></script>
<script src="js/dwz.switchEnv.js" type="text/javascript"></script>
<script src="js/dwz.alertMsg.js" type="text/javascript"></script>
<script src="js/dwz.contextmenu.js" type="text/javascript"></script>
<script src="js/dwz.navTab.js" type="text/javascript"></script>
<script src="js/dwz.tab.js" type="text/javascript"></script>
<script src="js/dwz.resize.js" type="text/javascript"></script>
<script src="js/dwz.dialog.js" type="text/javascript"></script>
<script src="js/dwz.dialogDrag.js" type="text/javascript"></script>
<script src="js/dwz.sortDrag.js" type="text/javascript"></script>
<script src="js/dwz.cssTable.js" type="text/javascript"></script>
<script src="js/dwz.stable.js" type="text/javascript"></script>
<script src="js/dwz.taskBar.js" type="text/javascript"></script>
<script src="js/dwz.ajax.js" type="text/javascript"></script>
<script src="js/dwz.pagination.js" type="text/javascript"></script>
<script src="js/dwz.database.js" type="text/javascript"></script>
<script src="js/dwz.datepicker.js" type="text/javascript"></script>
<script src="js/dwz.effects.js" type="text/javascript"></script>
<script src="js/dwz.panel.js" type="text/javascript"></script>
<script src="js/dwz.checkbox.js" type="text/javascript"></script>
<script src="js/dwz.history.js" type="text/javascript"></script>
<script src="js/dwz.combox.js" type="text/javascript"></script>
<script src="js/dwz.print.js" type="text/javascript"></script>
<script src="js/dwz.regional.zh.js" type="text/javascript"></script>
<script src="js/dwz.util.date.js" type="text/javascript"></script>
<script src="webresources/script/validate.js" type="text/javascript"></script>
<script src="webresources/script/wabacus_api.js" type="text/javascript"></script>
<script src="webresources/script/wabacus_editsystem.js" type="text/javascript"></script>
<script src="webresources/script/wabacus_system.js" type="text/javascript"></script>



<script type="text/javascript">

$(function(){
	DWZ.init("dwz.frag.xml", {
		loginUrl:"login.jsp", loginTitle:"登录",	// 弹出登录对话框
//		loginUrl:"login.html",	// 跳到登录页面
		statusCode:{ok:200, error:300, timeout:301}, //【可选】
		pageInfo:{pageNum:"pageNum", numPerPage:"numPerPage", orderField:"orderField", orderDirection:"orderDirection"}, //【可选】
		debug:false,	// 调试模式 【true|false】
		callback:function(){
			initEnv();
			$("#themeList").theme({themeBase:"themes"}); // themeBase 相对于index页面的主题base路径
		}
	});
});
</script>
</head>

<body scroll="no">

	
	<div id="layout" >
		<div id="header">
			<div class="headerNav">
				<span id="logo">出入库管理系统（IMS）</span>
				<ul class="nav">
					  <%
					  		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					  		Date date = new Date();
					  		String str = formatter.format(date); 
					   %>
					   <input type="hidden" id="roleFlag" value="<%=role %>"/>
					  <span id="loginmsg"> 登录信息：<%=username %>/<span id="curTime"><%= str %></span></span><span id="beidouTime"></span>
					  <li><a href="servlet/Logout">退出</a></li>
				</ul>
				<ul class="themeList" id="themeList">
					<li theme="default"><div class="selected">蓝色</div></li>
					<li theme="green"><div>绿色</div></li>
					<!--li theme="red"><div>红色</div></li-->
					<li theme="purple"><div>紫色</div></li>
					<li theme="silver"><div>银色</div></li>
					<li theme="azure"><div>天蓝</div></li>
				</ul>
			</div>

			<!-- navMenu -->
			
		</div>

		<div id="leftside">
			<div id="sidebar_s">
				<div class="collapse">
					<div class="toggleCollapse"><div></div></div>
				</div>
			</div>
			<div id="sidebar">
				<div class="accordion" fillSpace="sidebar" >
					
					<div class="accordionHeader">
						<h2><span>Folder</span></h2>
					</div>
					<div class="accordionContent">
						<ul class=  "tree treeFolder expand">
							<li><a>出入库管理</a>
							    <ul>
								  <li><a href="ShowReport.wx?PAGEID=out" target="navTab" rel="userbaseinfo11" external="true" fresh="false">出库操作</a></li>
								  <li><a href="ShowReport.wx?PAGEID=in" target="navTab" rel="userbaseinfo12" external="true" fresh="false" >入库操作</a></li> 
								  <li><a href="ShowReport.wx?PAGEID=regent" target="navTab" rel="userbaseinfo14" external="true" fresh="false">试剂/耗材库</a></li> 
								</ul>
							</li>									
							<li><a>查询统计</a>
								<ul>
									<li><a href="ShowReport.wx?PAGEID=queryout" target="navTab" rel="userbaseinfo21" external="true" fresh="false" style="width:100%;height:100%;">出库查询</a></li>
									<li><a href="ShowReport.wx?PAGEID=queryin" target="navTab" rel="userbaseinfo22" external="true" fresh="false">入库查询</a></li>
									<li><a href="ShowReport.wx?PAGEID=queryexpired" target="navTab" rel="userbaseinfo23" external="true" fresh="false">查询过期</a></li>
									<li><a href="ShowReport.wx?PAGEID=querycat" target="navTab" rel="userbaseinfo24" external="true" fresh="false">库存查询</a></li>
									<li id="exportOut"></li>									 
									 <li id="inventoryStatics"></li>									 
									 <li><a href="ShowReport.wx?PAGEID=Rstatics" target="navTab" rel="userbaseinfo25" external="true" fresh="false">R统计</a></li>
								</ul>
							</li>
							<li><a>基础信息维护</a>
								<ul>
								    <li id="userManage"></li>
								    <li id="rateManage"></li>
								    <li id="importcat"></li>
								    <li id="price0"></li>
									<li><a href="ShowReport.wx?PAGEID=equip" target="navTab" rel="userbaseinfo35" external="true" fresh="false">设备信息管理</a></li>
									<li><a href="ChangePWD.jsp" target="dialog" rel="changepassword" width="600" mask="true">修改密码</a></li>
                                      
								</ul>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<div id="container">
			<div id="navTab" name="navTab" class="tabsPage">
				<div class="tabsPageHeader">
					<div class="tabsPageHeaderContent"><!-- 显示左右控制时添加 class="tabsPageHeaderMargin" -->
						<ul class="navTab-tab">
							<li tabid="main" class="main"><a href="javascript:;"><span><span class="home_icon">首页</span></span></a></li>
						</ul>
					</div>
					<div class="tabsLeft">left</div><!-- 禁用只需要添加一个样式 class="tabsLeft tabsLeftDisabled" -->
					<div class="tabsRight">right</div><!-- 禁用只需要添加一个样式 class="tabsRight tabsRightDisabled" -->
					<div class="tabsMore">more</div>
				</div>
				<ul class="tabsMoreList">
					<li><a href="javascript:;">首页</a></li>
				</ul>
				<div class="navTab-panel tabsPageContent layoutBox">
					<div class="page unitBox" id="mainBox" style="height:100%;">
					
						 <iframe src="ShowReport.wx?PAGEID=mainpage" style="width:100%;height:100%;" frameborder="no" border="0" marginwidth="0" marginheight="0"></iframe> 
						 
					</div>
					
				</div>
			</div>
			
		</div>
	
	</div>
	
	
	<!-- div id="footer">
		 
		 
		 <a href="currentWarnInfo.jsp" target="dialog" external="true" mask="false" width="1000" height="300"><h2>实时信息显示</h2></a>
	 </div-->
	 
<script src="js/localJS/index.js" type="text/javascript"></script>

</body>
</html>