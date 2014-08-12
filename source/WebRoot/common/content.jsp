<%@ page language="java" import="java.util.*,java.net.InetAddress" pageEncoding="UTF-8"%>
<%
	/*String username = "";
	Cookie[] cookies = request.getCookies();
	for(int i = 0; cookies != null && i<cookies.length; i++){
		Cookie cookie = cookies[i];
		if("username".equals(cookie.getName())){
			username = cookie.getValue();
		}
	}*/
	String username = "";
	username = (session.getAttribute("username")).toString();
	int role = Integer.parseInt((session.getAttribute("role").toString()));
	
	String ip = request.getRemoteAddr();
 %>
</div>
	<input type="hidden" id="user" value="<%=username %>"/>
	<input type="hidden" id="ipAddr" value="<%=ip %>"/>
	<input type="hidden" id="role" value="<%=role %>"/>
   <div id="content">
   	<fieldset>
   		<legend>服务器</legend>
   		
   		 <div class="top">
		   		<fieldset class="systemInfo">
		   			<legend>系统信息</legend>
		   			<dl >
		   				<dt>名称：</dt>
		   				<dd><input type="text" name="name" id="name" value="" readonly="readonly"/></dd>
		   			</dl>
		   			<dl>
		   				<dt>CPU型号：</dt>
		   				<dd><input type="text" name="cputype" id="cputype" value="" readonly="readonly"/></dd>
		   			</dl>
		   			<dl>
		   				<dt>CPU数目：</dt>
		   				<dd><input type="text" name="cpucount" id="cpucount" value="" readonly="readonly"/></dd>
		   			</dl>
		   			<dl>
		   				<dt>内存数（KB）：</dt>
		   				<dd><input type="text" name="memsize" id="memsize" value="" readonly="readonly"/></dd>
		   			</dl>
		   			<dl>
		   				<dt>硬盘数：</dt>
		   				<dd><input type="text" name="diskcount" id="diskcount" value="" readonly="readonly"/></dd>
		   			</dl>
		   			<dl>
		   				<dt>IP地址：</dt>
		   				<dd><input type="text" name="ipaddr" id="ipaddr" value="" readonly="readonly"/></dd>
		   			</dl>
		   			<dl>
		   				<dt>操作系统版本：</dt>
		   				<dd><input type="text" name="osversion" id="osversion" value="" readonly="readonly"/></dd>
		   			</dl>
		   		</fieldset>
		   		<fieldset class="usage">
		   			<legend>CPU使用信息</legend>
		   			<div class="usageTop">
		   				<input id="totalCPUUsageValue" type="text" readonly="readonly" value="0.00%"/><label id="totalUsageLabel">CPU总使用率</label>
		   			</div>
		   			<div id="cpuUsage">
		   			<img alt="" src="Images/cpuPic.png" width="100%" height="100%">
		   			</div>
		   		</fieldset>
		   		
		   		<fieldset class="usage">
			   		<legend>内存使用信息</legend>
			   		<div class="usageTop">
			   			<input id="totalMemUsageValue" type="text" readonly="readonly" value="0.00%"/><label id="totalUsageLabel">内存总使用率</label>
			   		</div>
		   			<div id="memUsage">
		   				<img alt="" src="Images/memPic.png" width="100%" height="100%">
		   			</div>
		   		</fieldset>
		 </div>
   		 <div class="middle">
	   		<fieldset class="displayInfo">
	   			<legend>磁盘使用情况</legend>
	   			<div id="diskUsage" class="displayData">
	   				<table class="cls-data-table" width="100%" style="table-layout:fixed;">
	   					<thead>
	   						<tr class="cls-data-tr-head-list">
	   							<th class="cls-data-th-list">磁盘名称</th>
	   							<th class="cls-data-th-list">安装点</th>
	   							<th class="cls-data-th-list">磁盘大小(KB)</th>
	   							<th class="cls-data-th-list">磁盘已使用空间(KB)</th>
	   							<th class="cls-data-th-list">磁盘使用率(%)</th>
	   						</tr>
	   					</thead>
	   					<tbody>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
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
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
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
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
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
	   						</tr>
	   						<tr class="cls-data-tr-even" onmouseover="this.style.backgroundColor='#CCFAFF';" onmouseout="this.style.backgroundColor='white';">
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
	   						</tr>
	   					</tbody>
	   					<tfoot></tfoot>
	   				</table>
	   			</div>
	   		</fieldset>
	   		
	   		<fieldset class="displayInfo">
	   			<legend>进程信息</legend>
	   			<div id="processData" class="displayData">
	   				<table class="cls-data-table" width="100%">
		   					<thead>
		   						<tr class="cls-data-tr-head-list">
		   							<th class="cls-data-th-list">软件名称</th>
		   							<th class="cls-data-th-list">进程名称</th>
		   							<th class="cls-data-th-list">进程ID</th>
		   							<th class="cls-data-th-list">用户名</th>
		   							<th class="cls-data-th-list">状态</th>
		   							<th class="cls-data-th-list">CPU使用率(%)</th>
		   							<th class="cls-data-th-list">总CPU时间(秒)</th>
		   							<th class="cls-data-th-list">内存(KB)</th>
		   							<th class="cls-data-th-list">优先级</th>
		   							<th class="cls-data-th-list">状态描述</th>
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
	   							<td class="cls-data-td-editlist"></td>
	   							<td class="cls-data-td-editlist"></td>
	   						</tr>
		   					</tbody>
		   					<tfoot></tfoot>
		   				</table>
	   			</div>
	   		</fieldset>
   		</div>
   	</fieldset>
</div>
<div id="contextmenu" style="display:none;border:2px solid #666666;background-color:#fefefe;width:140px;padding:2px;position:absolute;font-size:12px;">
	<div class="menu" id="start">启动进程</div>
	<div class="menu" id="stop">终止进程</div>
	<div class="menu" id="restart">重启进程</div>
	<div class="menu" id="getStatus">获取当前进程状态</div>
</div>
<script type="text/javascript">
	$(".menu").mouseover(function(){
		$(this).css("background-color","#2b5cc4");
	}).mouseout(function(){
		$(this).css("background-color","#fefefe");
	});
</script>
</body>
</html>