<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=7" />
<title>运行中心管理系统-报警配置</title>

<link href="css/msgwarningconfig.css" rel="stylesheet" type="text/css" media="screen"/>
<script src="js/jquery-1.7.2.js" type="text/javascript"></script>
<script src="js/localJS/warningconfig.js" type="text/javascript"></script>
</head>
<body onload="setFormStyle()">
	<h1>声音告警配置</h1>
	<form method="post" action="servlet/ConfigVoiceWarning" class="pageForm required-validate" onsubmit="return customSubmit(this,callback)">
		
		<div>
			<label><input type="radio" id="voiceOpen" name="warnSwitch" checked class="isopen" value="1">打开声音告警</label><br/>
			<label><input type="radio" id="voiceClose" name="warnSwitch" class="isopen" value="0">关闭声音告警</label>
		</div>	
		<div id="outer">
			
				<fieldset class="level2">
					<legend>信息级别选择</legend>
					<label><input type="checkbox" checked id="serious" name="serious" value="1" />严重警告</label><br/>
					<label><input type="checkbox" checked id="important" name="important" value="1" />重要警告</label><br/>
					<label><input type="checkbox" checked id="general" name="general" value="1" />一般警告</label>
				</fieldset>
				
				<fieldset class="level3">
					<legend>自动停止时间配置</legend>
					<label><input type="radio" name="stopTimeSwitch" class="settimeopen" id="timerClose" value="0" />关闭</label><br/>
					<label><input type="radio" name="stopTimeSwitch" class="settimeopen" checked id="timerOpen" value="1" />开启</label><br/>
					<label id="seconds"><input type="text" name="time" value="1" id="times"/>秒</label>
				</fieldset>
		</div>
		<div id="footer">
			<input class="save_reset" type="submit" name="save" value="保存">
			<input class="save_reset" type="reset" name="reset" value="重置">
		</div>
	</form>
	
</body>
</html>