<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=7" />
<title>运行中心管理系统-报警配置</title>

<link href="css/msgwarningconfig.css" rel="stylesheet" type="text/css" media="screen"/>
<script src="js/jquery-1.7.2.js" type="text/javascript"></script>
<script src="js/localJS/msgwarningconfig.js" type="text/javascript"></script>
</head>
<body onload="setFormStyle()">
	<h1>短信告警配置</h1>
	<form method="post" action="servlet/ConfigMsgWarning" class="pageForm required-validate" onsubmit="return customSubmit(this,callback)">
		
		<div>
			<label><input type="radio" id="voiceOpen" name="warnSwitch" class="isopen" value="1">打开短信告警</label><br/>
			<label><input type="radio" id="voiceClose" name="warnSwitch" class="isopen" checked value="0">关闭短信告警</label>
		</div>	
		<div id="outer">
			
				<fieldset class="level2">
					<legend>信息级别选择</legend>
					<label class="disableselect"><input type="checkbox" id="serious" name="serious" disabled value="1" />严重警告</label><br/>
					<label class="disableselect"><input type="checkbox" id="important" name="important" disabled value="1" />重要警告</label><br/>
					<label class="disableselect"><input type="checkbox" id="general" name="general" disabled value="1" />一般警告</label>
				</fieldset>
				
				<fieldset class="level3">
					<legend>相同告警发送间隔配置</legend>
					<label class="disableselect"><input type="radio" id="timerClose" name="intervalSwitch" disabled class="settimeopen" value="0" />关闭</label><br/>
					<label class="disableselect"><input type="radio" id="timerOpen" name="intervalSwitch" disabled class="settimeopen" checked value="1" />开启</label><br/>
					<label class="disableselect" id="seconds"><input type="text" name="time" value="1" id="times"/>秒</label>
				</fieldset>
		</div>
		<div id="footer">
			<input class="save_reset" type="submit" name="save" value="保存">
			<input class="save_reset" type="reset" name="reset" value="重置">
		</div>
	</form>
	
</body>
</html>