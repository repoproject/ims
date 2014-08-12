<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'WarnInfoContent.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link href="css/easyui.css" rel="stylesheet" type="text/css" media="screen"/>
	<link href="css/wabacus_system.css" rel="stylesheet" type="text/css" media="screen"/>
	<link href="css/warnInfo.css" rel="stylesheet" type="text/css" media="screen"/>

	<script src="js/jquery-1.7.2.js" type="text/javascript"></script>
	
	<script src="js/jquery.easyui.min.js" type="text/javascript"></script>
	<script src="js/localJS/currentWarnInfo.js" type="text/javascript"></script>
	<script src="js/localJS/eventUtil.js" type="text/javascript"></script>
		
	</head>
  <!-- <iframe src="WarnInfoContent.jsp" width="100%" height="100%"></iframe> -->
  <body onload="initVoicePath('<%=basePath%>voice/')">
  <div class="easyui-tabs" >
  	
  	<div title="告警信息">
  		<div>
            	<div>
            		<label><input type="checkbox" name="level" value="1" id="serious" checked="checked"/><img src="Images/serious.gif" style="margin:0 4px;"/>严重：<span id="seriousValue"></span></label>
            		<label><input type="checkbox" name="level" value="2" id="import" checked="checked"/><img src="Images/important.gif" style="margin:0 4px;"/>重要：<span id="importantValue"></span></label>
            		<label><input type="checkbox" name="level" value="3" id="general" checked="checked"/><img src="Images/general.gif" style="margin:0 4px;"/>一般：<span id="generalValue"></span></label>
            		
            		<label><input type="checkbox" name="type" value="1" id="recover" checked="checked"/>恢复</label>
            		<label><input type="checkbox" name="type" value="2" id="recoverable" checked="checked"/>告警</label>
            		<label><input type="checkbox" name="type" value="3" id="oneTime" checked="checked"/>一次性告警</label>
            		软件类别：
            		<select id="softType" onChange="selectSoftType(this.options.selectedIndex)">
            			<!--<option value="all">全部</option>
            			<option>中心运行管理软件</option>
            			<option>产品管理和服务软件</option>
            			<option>综合产品分析和制作软件</option>
            			 
            			<option >产品综合和评估软件</option>
            			<option>中心运行管理软件</option>
            			<option>数据和产品接收</option>
            			<option>产品管理</option>
            			<option>Web门户</option>
            			<option>Web服务</option>-->
            		</select>
            		<label><button id="stopRefresh">停止刷新</button></label>
            	</div>
                <div style="height:141px;overflow-y:scroll;" class="warnTable">	  
				 <table class="cls-data-table" width="100%" id="warnDataID" ><!--  style="table-layout: fixed;"-->
	   					<thead>
	   						<!-- tr class="cls-data-tr-head-list">
	   							  <th class="cls-data-th-list" width="5%"></th>
				                  <th class="cls-data-th-list" width="10%">软件名称</th>
				                  <th class="cls-data-th-list" width="20%">告警时间</th>
				                  <th class="cls-data-th-list" width="20%">恢复时间</th>
				                  <th class="cls-data-th-list" width="10%">告警类型</th>
				                  <th class="cls-data-th-list" width="10%">告警级别</th>
				                  <th class="cls-data-th-list" width="10%">状态</th>
				                  <th class="cls-data-th-list" width="40%">信息描述</th>
	   							  
	   						</tr> -->
	   						<tr class="cls-data-tr-head-list">
	   							  <th class="cls-data-th-list" ></th>
				                  <th class="cls-data-th-list" >软件/模块名称</th>
				                  <th class="cls-data-th-list" >告警时间</th>
				                  <th class="cls-data-th-list" >恢复时间</th>
				                  <th class="cls-data-th-list" >告警类型</th>
				                  <th class="cls-data-th-list" >告警级别</th>
				                  <th class="cls-data-th-list" >状态</th>
				                  <th class="cls-data-th-list" >信息描述</th>
	   							  
	   						</tr>
	   					</thead>
	   					<tbody>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-odd" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='#FBFBFB';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-odd" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='#FBFBFB';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-odd" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='#FBFBFB';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-odd" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='#FBFBFB';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-odd" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='#FBFBFB';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-odd" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='#FBFBFB';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   					</tbody>
	   					<tfoot></tfoot>
	   				</table>
	   			</div>
            </div>
  	</div>
  	
  	<div title="控制命令状态">
  		<div style="height:141px;overflow-y:scroll;" class="warnTable">	
  		<table class="cls-data-table" width="100%" id="commandData">
	   					<thead>
	   						<tr class="cls-data-tr-head-list">
	   							  <th class="cls-data-th-list" >发送时间</th>
				                  <th class="cls-data-th-list" >发送者</th>
				                  <th class="cls-data-th-list" >发送位置</th>
				                  <th class="cls-data-th-list" >系统名称</th>
				                  <th class="cls-data-th-list" >指令名称</th>
				                  <th class="cls-data-th-list" >指令内容</th>
				                  <th class="cls-data-th-list" >指令状态</th>
				                  <th class="cls-data-th-list" >状态时间</th>
	   						</tr>
	   					</thead>
	   					<tbody>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-odd" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='#FBFBFB';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-odd" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='#FBFBFB';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-odd" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='#FBFBFB';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-odd" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='#FBFBFB';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-odd" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='#FBFBFB';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
	   					</tbody>
	   					<tfoot></tfoot>
	   				</table>
			</div>
  	</div>
  	
  </div>
  <!-- script type="text/javascript">
		var wmp = new ActiveXObject("WMPlayer.OCX");
	wmp.settings.volume=100;
	var div1 = document.getElementById("username");
	document.onclick = function(){
			wmp.settings.autoStart = true;
			wmp.url = "<%=basePath%>voice/test00.wma";
		};
	var div = document.getElementById("reset");
	div.onclick = function(){
		wmp.settings.autoStart = false;
		wmp.close();
	}
	</script> -->
    <!--div class="tabs">
      <div class="tabsHeader">
            <div class="tabsHeaderContent">
                  <ul>
                        <li class="selected"><a href="#"><span>控制命令状态</span></a></li>
                        <li><a href="#"><span>告警信息</span></a></li>
                  </ul>
            </div>
      </div>
      <div class="tabsContent" style="height:150px;">
            

            

      </div>
      <div class="tabsFooter">
            <div class="tabsFooterContent"></div>
      </div>
	</div-->
  </body>
</html>
