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
<title>管理软件</title>

<link href="themes/default/style.css" rel="stylesheet" type="text/css" media="screen"/>
<link href="css/core.css" rel="stylesheet" type="text/css" media="screen"/>
<link href="themes/css/print.css" rel="stylesheet" type="text/css" media="print"/>
<link href="uploadify/css/uploadify.css" rel="stylesheet" type="text/css" media="screen"/>
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

<script src="js/localJS/systemMonitor.js" type="text/javascript"></script>

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
	<div id="flashArea" style="display:none;">
		<!--div id="closeFlash"></div-->
		<h4 style="">您的系统没有安装flash播放软件，服务器下的图表数据需要flash才能显示，请先下载安装Adobe flash player</h4>
		<p><a href="download/flash/flashplayer12.0win_ax.exe">下载Flash_player12 flash播放(支持64位系统)</a></p>
	</div>
	<div id="layout">
		<div id="header">
			<div class="headerNav">
				<span id="logo">出入库管理</span>
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
				<div class="toggleCollapse"><h2>出入库管理</h2><div>收缩</div></div>

				<div class="accordion" fillSpace="sidebar">
					<!-- 中心运行管理模块 -->
					<div class="accordionHeader">
						<h2><span>Folder</span>出入库管理</h2>
					</div>
					<div class="accordionContent">
						<ul class="tree treeFolder">
							<li><a href="ShowReport.wx?PAGEID=yhglpage" target="navTab" rel="ftpserverstat" external="true" fresh="false">入库</a></li>
							<li><a href="ShowReport.wx?PAGEID=out" target="navTab" rel="ftpserverstat" external="true" fresh="false">出库</a></li>
							<li><a>数据维护</a>
							    <ul>
								    <li><a href="ShowReport.wx?PAGEID=ftppage" target="navTab" rel="ftpserverstat" external="true" fresh="false">试剂</a></li>
								    <li><a href="ShowReport.wx?PAGEID=ftppage" target="navTab" rel="ftpserverstat" external="true" fresh="false">原因</a></li>
								</ul>
							</li>
									
							<li><a>系统业务统计</a>
								<ul>
									<li><a href="excel.jsp" target="navTab" rel="ftpserverstat" external="true" fresh="false">R统计</a></li>
									<li><a href="ShowReport.wx?PAGEID=webmhpage" target="navTab" rel="webpagestat" external="true" fresh="false">invertory</a></li>
									<li><a href="ShowReport.wx?PAGEID=webjpage" target="navTab" rel="webserverstat" external="true" fresh="false">ttt</a></li>
								</ul>
							</li>
						</ul>
					</div>
					
					<!-- 用户管理模块 -->
					<div class="accordionHeader">
						<h2><span>Folder</span>用户管理</h2>
					</div>
					<div class="accordionContent">
						<ul class="tree treeFolder">
							<li>
                              <a href="ShowReport.wx?PAGEID=user" target="navTab" rel="userbaseinfo" external="true" fresh="false">用户基础信息维护</a>
							</li>
							<!--<li><a href="ShowReport.wx?PAGEID=listpage1" target="navTab" rel="pagination1" external="true" fresh="false">用户管理</a></li>-->
							<li id="msgParamNumberConfig">
							<!--<a href="ShowReport.wx?PAGEID=dxglpage" target="navTab" rel="textmsgparamsetting" external="true" fresh="false">短信用户号码参数配置</a>
								-->
							</li>
							<li>
							<a href="ChangePWD.jsp" target="dialog" rel="changepassword" width="600" mask="true">修改密码</a>
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
							<li tabid="main" class="main"><a href="javascript:;"><span><span class="home_icon">系统</span></span></a></li>
						</ul>
					</div>
					<div class="tabsLeft">left</div><!-- 禁用只需要添加一个样式 class="tabsLeft tabsLeftDisabled" -->
					<div class="tabsRight">right</div><!-- 禁用只需要添加一个样式 class="tabsRight tabsRightDisabled" -->
					<div class="tabsMore">more</div>
				</div>
				<ul class="tabsMoreList">
					<li><a href="javascript:;">系统监视</a></li>
				</ul>
				<div class="navTab-panel tabsPageContent layoutBox">
					<div class="page unitBox" id="mainBox" style="height:100%;">
						<!-- <iframe src="SystemMonitor.jsp" style="width:100%;height:616px;" frameborder="no" border="0" marginwidth="0" marginheight="0"></iframe> -->
						 
						 
					</div>
					
				</div>
			</div>
			
		</div>
		<div id="warning" width="80%" height="200px"></div>
	
	</div>
	
	
	<!-- div id="footer">
		 
		 
		 <a href="currentWarnInfo.jsp" target="dialog" external="true" mask="false" width="1000" height="300"><h2>实时信息显示</h2></a>
	 </div-->
	 <div style="width:50px;height:50px;background-color:#000000;position:absolute;left:10px;top:2px;z-index: 999;">
	 	<a href="CurrentWarnInfo.jsp" target="dialog" mask="false" height="260" title="实时信息显示" rel="warnInfoDialog" id="realTimeWarning"><img id="warningPic" src="Images/bitBrown.bmp" alt="" /></a>
	 </div>
	 
<script src="js/localJS/index.js" type="text/javascript"></script>

</body>
</html>