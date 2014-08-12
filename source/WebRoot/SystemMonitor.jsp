<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统监视</title>
<link href="css/systemmonitor.css" rel="stylesheet" type="text/css" media="screen"/>
<script src="js/jquery-1.7.2.js" type="text/javascript"></script>
<script src="js/localJS/systemMonitor.js" type="text/javascript"></script>
</head>
<body onload="ajax_Start()">
<img alt="" src="Images/background.bmp" width="100%" height="600"/>
<div style="float:left;">
	<div>
	<a href="FileExchangeServer1.jsp" rel="filechangeserver1"><img id="server0" title="文件交换服务器1" width="5%" height="80px" class="serverPic_top" src="Images/Server.bmp"/></a>
	<a href="FileEXchangeServer2.jsp"><img id="server1" title="文件交换服务器2" width="5%" height="80px" class="serverPic_top" src="Images/Server.bmp"/></a>
	<a href="WebServer1.jsp" rel="Webserver1"><img id="server2" title="WEB服务器1" width="5%" height="80px" class="serverPic_top" src="Images/Server.bmp"/></a>
	<a href="WebServer2.jsp"><img id="server3" title="WEB服务器2" width="5%" height="80px" class="serverPic_top" src="Images/Server.bmp"/></a>
	<a href="CMSServer.jsp"><img id="server4" title="CMS服务器" width="5%" height="80px" class="serverPic_top" src="Images/Server.bmp"/></a>
	</div>
	<div>
	<a href="FileExchangeServer1.jsp"><img id="soft0" title="文件交换服务器1软件" class="softPic_left" src="Images/Software.bmp" width="6%"/></a>
	<a href="FileExchangeServer2.jsp"><img id="soft1" title="文件交换服务器2软件" class="softPic_left" src="Images/Software.bmp" width="6%"/></a>
	<a href="DBManageServer1.jsp"><img id="soft2" title="数据库管理服务器1软件" class="softPic_left" src="Images/Software.bmp" width="6%"/></a>
	<a href="DBManageServer2.jsp"><img id="soft3" title="数据库管理服务器2软件" class="softPic_left" src="Images/Software.bmp" width="6%"/></a>
	</div>
	<div>
	<a href="DBManageServer1.jsp"><img id="dbserver0" title="数据库管理服务器1" src="Images/DBServer.gif" width="5%" height="80px"/></a>
	<a href="DBManageServer2.jsp"><img id="dbserver1" title="数据库管理服务器2" src="Images/DBServer.gif" width="5%" height="80px"/></a>
	
	<a href=""><img id="nasgate" src="Images/NAS.bmp"/></a>
	
	<a href="AppServer1.jsp"><img id="appserver0" src="Images/Server.bmp" title="应用服务器1" width="5%" height="80px"/></a>
	<a href="AppServer2.jsp"><img id="appserver1" src="Images/Server.bmp" title="应用服务器2" width="5%" height="80px"/></a>
	
	</div>
	<div>
	<a href="WebServer1.jsp"><img id="soft4" title="WEB服务器1软件" class="softPic_right" src="Images/Software.bmp" width="6%"/></a>
	<a href="WebServer2.jsp"><img id="soft5" title="WEB服务器2软件" class="softPic_right" src="Images/Software.bmp" width="6%"/></a>
	<a href="CMSServer.jsp"><img id="soft6" title="CMS服务器软件" class="softPic_right" src="Images/Software.bmp" width="6%"/></a>
	<a href="AppServer1.jsp"><img id="soft7" title="应用服务器1软件" class="softPic_right" src="Images/Software.bmp" width="6%"/></a>
	<a href="AppServer2.jsp"><img id="soft8" title="应用服务器2软件" class="softPic_right" src="Images/Software.bmp" width="6%"/></a>
	</div>
	
	<div>
		<a href="">	<img id="switch0" src="Images/Switch.bmp"/></a>
		<a href="">	<img id="switch1" src="Images/Switch.bmp"/></a>
	</div>
	
	<div>
		<a href="">	<img id="sanpic" src="Images/SAN.bmp"/></a>
	</div>	


</div>


</body>
</html>