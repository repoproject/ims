var seriousFlag = 1;//记录复选框-严重的选中状态，1为选中，初始为都选中
var importFlag = 1;//记录重要复选框的选中状态
var generalFlag = 1;//记录一般复选框的选中状态
var recoverFlag = 1;//记录恢复复选框的选中状态
var recoverableFlag = 1;//记录可选中报警的选中状态
var oneTimeFlag = 1;//记录一次性报警复选框的选中状态
var softName = "全部";//选择软件列表的名字
var softID = "all";//软件列表的ID
var softList = null;//软件列表
var timeoutID;//定时器ID
var absolutePath = "";//声音文件的绝对路径
var seriousCount = 0;//严重警告计数
var importCount = 0;//重要警告计数
var generalCount = 0;//一般警告计数
var voiceObj =null;//IE中声音对象
var warnData = new Array();//告警数据缓存数组
var lastWarningTime = "";//记忆上次产生声音告警的时间
var warnedTimeNumber = 0;
//根据服务器返回的报警类型编号获取报警类型字符串		
function getWarnType(data){
	var warnType = "";
	switch(data){
			case 1:
				warnType = "监控代理异常";
				break;
			case 2:
				warnType = "关键进程异常";
				break;
			case 3:
				warnType = "网络异常";
				break;
			case 4:
				warnType = "阈值告警";
				break;
			case 5:
				warnType = "监控服务启动退出";
				break;
			case 6:
				warnType = "业务运行异常（内部）";
				break;
			case 7:
				warnType = "业务运行异常（外部）";
				break;
			case 8:
				warnType = "数据库异常";
				break;
			default:
				warnType = "";
				break;
		}
	return warnType;
}
//根据服务器返回的报警状态编号获取报警状态
function getWarnStatus(data){
	var warnStatus = "";
	switch(data){
	case 0:
		warnStatus = "恢复";
		break;
	case 1:
		warnStatus = "告警";
		break;
	case 3:
		warnStatus = "一次性告警";
		break;
	default:
		warnStatus = "";
		break;
	}
	return warnStatus;
}
//根据软件ID获取软件名称
function setSoftName(softCode){
	//var result = "";
	if(softList == null){
		return false;
	}
	var count = softList.length;
	for(var i = 0; i< count; i++){
		if(softCode == softList[i].ID){
			return softList[i].NAME;
		}
	}
	return "";
}
//更新报警数据	
function updateData(){
	if(warnData == "" || warnData == null || warnData.length == 0)return false;
	var dataCount = warnData.length;
	var dataStr = "";
	for(var i = 0; i < dataCount; i++){
		if(seriousFlag == 1 && recoverFlag ==1){
			if(warnData[i].warnLevel == 10 && warnData[i].warnStatus == 0){
				if(softID == "") {
					return false;
				}else if(softID == "all"){
					dataStr += '<tr><td><img src="Images/serious.gif"/></td>';
					dataStr += '<td>' + setSoftName(warnData[i].softName) + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>严重</td><td>';
					dataStr += '恢复</td><td>' + warnData[i].warnInfo + '</td></tr>';
				}else{
					if(softID == warnData[i].softName){
						dataStr += '<tr><td><img src="Images/serious.gif"/></td>';
						dataStr += '<td>' + softName + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>严重</td><td>';
						dataStr += '恢复</td><td>' + warnData[i].warnInfo + '</td></tr>';
					}
				}
			}
		}
		if(seriousFlag == 1 && recoverableFlag ==1){
			if(warnData[i].warnLevel == 10 && warnData[i].warnStatus == 1){
				if(softID == "") {
					return false;
				}else if(softID == "all"){
					dataStr += '<tr><td><img src="Images/serious.gif"/></td>';
					dataStr += '<td>' + setSoftName(warnData[i].softName) + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>严重</td><td>';
					dataStr += '告警</td><td>' + warnData[i].warnInfo + '</td></tr>';
				}else{
					if(softID == warnData[i].softName){
						dataStr += '<tr><td><img src="Images/serious.gif"/></td>';
						dataStr += '<td>' + softName + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>严重</td><td>';
						dataStr += '告警</td><td>' + warnData[i].warnInfo + '</td></tr>';
					}
				}
			}
		}
		if(seriousFlag == 1 && oneTimeFlag ==1){
			if(warnData[i].warnLevel == 10 && warnData[i].warnStatus == 3){
				if(softID == "") {
					return false;
				}else if(softID == "all"){
					dataStr += '<tr><td><img src="Images/serious.gif"/></td>';
					dataStr += '<td>' + setSoftName(warnData[i].softName) + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>严重</td><td>';
					dataStr += '一次性告警</td><td>' + warnData[i].warnInfo + '</td></tr>';
				}else{
					if(softID == warnData[i].softName){
						dataStr += '<tr><td><img src="Images/serious.gif"/></td>';
						dataStr += '<td>' + softName + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>严重</td><td>';
						dataStr += '一次性告警</td><td>' + warnData[i].warnInfo + '</td></tr>';
					}
				}
			}
		}
		if(importFlag == 1 && recoverFlag ==1){
			if(warnData[i].warnLevel == 20 && warnData[i].warnStatus == 0){
				if(softID == "") {
					return false;
				}else if(softID == "all"){
					dataStr += '<tr><td><img src="Images/important.gif"/></td>';
					dataStr += '<td>' + setSoftName(warnData[i].softName) + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>重要</td><td>';
					dataStr += '恢复</td><td>' + warnData[i].warnInfo + '</td></tr>';
				}else{
					if(softID == warnData[i].softName){
						dataStr += '<tr><td><img src="Images/important.gif"/></td>';
						dataStr += '<td>' + softName + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>重要</td><td>';
						dataStr += '恢复</td><td>' + warnData[i].warnInfo + '</td></tr>';
					}
				}
			}
		}
		if(importFlag == 1 && recoverableFlag ==1){
			if(warnData[i].warnLevel == 20 && warnData[i].warnStatus == 1){
				if(softID == "") {
					return false;
				}else if(softID == "all"){
					dataStr += '<tr><td><img src="Images/important.gif"/></td>';
					dataStr += '<td>' + setSoftName(warnData[i].softName) + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>重要</td><td>';
					dataStr += '告警</td><td>' + warnData[i].warnInfo + '</td></tr>';
				}else{
					if(softID == warnData[i].softName){
						dataStr += '<tr><td><img src="Images/important.gif"/></td>';
						dataStr += '<td>' + softName + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>重要</td><td>';
						dataStr += '告警</td><td>' + warnData[i].warnInfo + '</td></tr>';
					}
				}
			}
		}
		if(importFlag == 1 && oneTimeFlag ==1){
			if(warnData[i].warnLevel == 20 && warnData[i].warnStatus == 3){
				if(softID == "") {
					return false;
				}else if(softID == "all"){
					dataStr += '<tr><td><img src="Images/important.gif"/></td>';
					dataStr += '<td>' + setSoftName(warnData[i].softName) + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>重要</td><td>';
					dataStr += '一次性告警</td><td>' + warnData[i].warnInfo + '</td></tr>';
				}else{
					if(softID == warnData[i].softName){
						dataStr += '<tr><td><img src="Images/important.gif"/></td>';
						dataStr += '<td>' + softName + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>重要</td><td>';
						dataStr += '一次性告警</td><td>' + warnData[i].warnInfo + '</td></tr>';
					}
				}
			}
		}if(generalFlag == 1 && recoverFlag ==1){
			if(warnData[i].warnLevel == 30 && warnData[i].warnStatus == 0){
				if(softID == "") {
					return false;
				}else if(softID == "all"){
					dataStr += '<tr><td><img src="Images/general.gif"/></td>';
					dataStr += '<td>' + setSoftName(warnData[i].softName) + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>一般</td><td>';
					dataStr += '恢复</td><td>' + warnData[i].warnInfo + '</td></tr>';
				}else{
					if(softID == warnData[i].softName){
						dataStr += '<tr><td><img src="Images/general.gif"/></td>';
						dataStr += '<td>' + softName + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>一般</td><td>';
						dataStr += '恢复</td><td>' + warnData[i].warnInfo + '</td></tr>';
					}
				}	
			}
		}
		//判断告警级别
		if(generalFlag == 1 && recoverableFlag ==1){
			if(warnData[i].warnLevel == 30 && warnData[i].warnStatus == 1){
				if(softID == "") {
					return false;
				}else if(softID == "all"){
					dataStr += '<tr><td><img src="Images/general.gif"/></td>';
					dataStr += '<td>' + setSoftName(warnData[i].softName) + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>一般</td><td>';
					dataStr += '告警</td><td>' + warnData[i].warnInfo + '</td></tr>';
				}else{
					if(softID == warnData[i].softName){
						dataStr += '<tr><td><img src="Images/general.gif"/></td>';
						dataStr += '<td>' + softName + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>一般</td><td>';
						dataStr += '告警</td><td>' + warnData[i].warnInfo + '</td></tr>';
					}
				}
				
			}
		}
		if(generalFlag == 1 && oneTimeFlag ==1){
			if(warnData[i].warnLevel == 30 && warnData[i].warnStatus == 3){
				if(softID == "") {
					return false;
				}else if(softID == "all"){
					dataStr += '<tr><td><img src="Images/general.gif"/></td>';
					dataStr += '<td>' + setSoftName(warnData[i].softName) + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>一般</td><td>';
					dataStr += '一次性告警</td><td>' + warnData[i].warnInfo + '</td></tr>';
				}else{
					if(softID == warnData[i].softName){
						dataStr += '<tr><td><img src="Images/general.gif"/></td>';
						dataStr += '<td>' + softName + '</td><td>' + warnData[i].warnTime + '</td><td>' + warnData[i].resumeTime + '</td><td>' + getWarnType(warnData[i].warnType) + '</td><td>一般</td><td>';
						dataStr += '一次性告警</td><td>' + warnData[i].warnInfo + '</td></tr>';
					}
				}
			}
		}
		
	}
	//将数据插入到页面相应的位置
	$("#warnDataID tbody").html(dataStr);
	
	//为表格中的数据增加样式，主要是奇，偶行区别样式和鼠标滑过效果
	$("#warnDataID tbody tr td").addClass("cls-data-td-editlist");
	$("#warnDataID tbody tr:even").addClass("cls-data-tr-even").mouseover(
			function(){
				$(this).removeClass("cls-data-tr-even").addClass("TRHover");
			}).mouseout(function(){
				$(this).removeClass("TRHover").addClass("cls-data-tr-even");
			});
	$("#warnDataID tbody tr:odd").addClass("cls-data-tr-odd").mouseover(
			function(){
				$(this).removeClass("cls-data-tr-odd").addClass("TRHover");
			}).mouseout(function(){
				$(this).removeClass("TRHover").addClass("cls-data-tr-odd");
			});
	dataCount = dataStr = null;
}

//设置各个报警级别对应的报警数量
function setLevelCounts(data){
	var len = data.length;
	seriousCount = 0;
	importCount = 0;
	generalCount = 0;
	for(var i = 0; i < len; i++){
		if(data[i].warnLevel == 10){
			seriousCount++
		}
		if(data[i].warnLevel == 20){
			importCount++
		}
		if(data[i].warnLevel == 30){
			generalCount++
		}
	}
	$("#seriousValue").html(seriousCount);
	$("#importantValue").html(importCount);
	$("#generalValue").html(generalCount);
	len = null;
}

//通过AJAX隔一定的时间向服务器请求数据，并将数据更新到页面
function getServerWarningData(){
	$.ajax({
		   type: "GET",
		   url: "servlet/GetWarningInfo?t="+Math.random(),
		   //url:url+"?t=" +Math.random(),
		   dataType: "text",
		   success: function(dataObjs,Status) {
			   dataObjs = eval("(" + dataObjs+ ")");
			   if(dataObjs!=""&&dataObjs!=null){
				   warnData = dataObjs.warningInternals;
				   setLevelCounts(warnData)
				   updateData();
				   setVoiceWarn(dataObjs);
				   /*if(warnData.length > 0){
					   top.warningID = setInterval("top.setWarningPic()",1000);
				   }else{
					   if(top.warningID != null){
						   clearInterval(top.warningID);
					   }
				   }*/
						
				}
		   },
		   error:function(xhr, textStatus, error)
		   	{
		   		//alert("请求服务器数据出错，请重新刷新页面，错误原因如下： \n\nstatus="+xhr.status);	
		   	}
	});
}
//声音告警设定
function voiceWarning(type,time){
	
	voiceObj.settings.volume=50;
	switch(type){
	case 10:
		if(time == 0){
			voiceObj.settings.autoStart = true;
			voiceObj.settings.playCount = 999999;
			voiceObj.url = absolutePath + "serious.wav";
		}else{
			voiceObj.settings.autoStart = true;
			voiceObj.settings.playCount = time;
			voiceObj.url = absolutePath + "serious.wav";
		}
		break;
	case 20:
		if(time == 0){
			voiceObj.settings.autoStart = true;
			voiceObj.settings.playCount = 999999;
			voiceObj.url = absolutePath + "important.wav";
		}else{
			voiceObj.settings.autoStart = true;
			voiceObj.settings.playCount = time;
			voiceObj.url = absolutePath + "important.wav";
		}
		break;
	case 30:
		if(time == 0){
			voiceObj.settings.autoStart = true;
			voiceObj.settings.playCount = 999999;
			voiceObj.url = absolutePath + "general.wav";
		}else{
			voiceObj.settings.autoStart = true;
			voiceObj.settings.playCount = time;
			voiceObj.url = absolutePath + "general.wav";
		}
		break;
	}
}

//设置声音告警数据
function setVoiceWarn(data){
	if(data.voiceWarningConfig.warnSwitch == undefined || data.voiceWarningConfig == null){
		//alert("获取声音告警配置文件失败！");
		return false;
	}else if(data.voiceWarningConfig.warnSwitch == 0){
		//声音告警关闭
		voiceObj.settings.autoStart = false;
		return false;
	}else if(data.voiceWarningConfig.warnSwitch == 1){
		var lastPos = data.warningInternals.length-1;
		if(lastPos < 0){
			voiceObj.settings.autoStart = false;
			return false;
		}
		for(var i=lastPos; i>=0; i--){
			if(data.warningInternals[i].warnStatus != 0){
				if((data.voiceWarningConfig.serious ==1 && data.warningInternals[i].warnLevel == 10)||(data.voiceWarningConfig.important ==1 && data.warningInternals[i].warnLevel == 20)||(data.voiceWarningConfig.general ==1 && data.warningInternals[i].warnLevel == 30)){
					if(((data.warningInternals[i].warnTime).localeCompare(lastWarningTime)) > 0){
						lastWarningTime = data.warningInternals[i].warnTime;
						warnedTimeNumber = 0;
						if(data.voiceWarningConfig.stopTimeSwitch == 1){
							voiceWarning(data.warningInternals[i].warnLevel,data.voiceWarningConfig.time);
							break;
						}else{
							voiceWarning(data.warningInternals[i].warnLevel,0);
							break;
						}
					}else if((((data.warningInternals[i].warnTime).localeCompare(lastWarningTime)) == 0)){
						warnedTimeNumber+=5;
						if(warnedTimeNumber < data.voiceWarningConfig.time){
							voiceObj.settings.autoStart = true;
							voiceObj.settings.playCount = data.voiceWarningConfig.time - warnedTimeNumber;
						}else{
							voiceObj.settings.autoStart = false;
							warnedTimeNumber = 0;
						}
					}
				}
			}
		}
		/*
		if(data.warningInternals[lastPos].warnStatus == 0){
			voiceObj.settings.autoStart = false;
		}else if(data.warningInternals[lastPos].warnStatus == 1){
			if(data.voiceWarningConfig.serious ==1 && data.warningInternals[lastPos].warnLevel == 10){
				if(data.voiceWarningConfig.stopTimeSwitch == 1){
					voiceWarning(1,data.voiceWarningConfig.time);
					return false;
				}else{
					voiceWarning(1,0);
					return false;
				}
			}else if(data.voiceWarningConfig.important ==1 && data.warningInternals[lastPos].warnLevel == 20){
				if(data.voiceWarningConfig.stopTimeSwitch == 1){
					voiceWarning(2,data.voiceWarningConfig.time);
					return false;
				}else{
					voiceWarning(2,0);
					return false;
				}
			}else if(data.voiceWarningConfig.general ==1 && data.warningInternals[lastPos].warnLevel == 30){
				if(data.voiceWarningConfig.stopTimeSwitch == 1){
					voiceWarning(3,data.voiceWarningConfig.time);
					return false;
				}else{
					voiceWarning(3,0);
					return false;
				}
			}else{
				voiceObj.settings.autoStart = false;
				return false;
			}
			
		}*/
	}
}
//处理页面上下拉框选择的值，并刷新页面数据
function selectSoftType(index){
	//softType = index;
	softName = $("#softType option:selected").text();
	softID = $("#softType option:selected").val();
	updateData();
}
//停止刷新
function stopRefresh(){
	clearInterval(timeoutID);
	voiceObj.settings.autoStart = false;
}

//开始刷新
function startRefresh(){
	timeoutID = setInterval("getServerWarningData()",5000);
	voiceObj.settings.autoStart = true;
}
//初始化声音文件路径
function initVoicePath(url){
	absolutePath = url;
	voiceObj = new ActiveXObject("WMPlayer.OCX");
}
//获取指令状态
function getCommandState(code){
	var result = "";
	switch (code)
	{
	case 0:
		result = "已发出(无回执)";
		break;
	case 1:
		result = "已发出";
		break;
	case 2:
		result = "确认";
		break;
	case 3:
		result = "执行开始";
		break;
	case 4:
		result = "执行成功";
		break;
	case 5:
		result = "请求重发";
		break;		
	case 6:
		result = "执行失败";
		break;
	case 7:
		result = "执行超时";
		break;
	default :
		result = "--";
		break;
	}
	return result;
}
//根据服务器返回的数据向页面插入命令信息
function setCommandData(dataObjs){
	var count = dataObjs.controlCommands.length;
	var dataStr = "";
	var temp = null;
	if(count > 0){
		for(var i = 0; i < count; i++){
			temp = dataObjs.controlCommands[i];
			dataStr += '<tr><td>' + temp.sendTime + '</td><td>' + temp.sender + '</td><td>' + temp.senderLocation + '</td><td>' + temp.systemName + '</td><td>' + temp.commandName + '</td><td>' + temp.commandInfo + '</td><td>' + getCommandState(temp.commandState) + '</td><td>' + temp.stateTime +'</td></tr>';
		}
		//将数据插入到页面相应的位置
		$("#commandData tbody").html(dataStr);
		
		//为表格中的数据增加样式，主要是奇，偶行区别样式和鼠标滑过效果
		$("#commandData tbody tr td").addClass("cls-data-td-editlist");
		$("#commandData tbody tr:even").addClass("cls-data-tr-even").mouseover(
				function(){
					$(this).removeClass("cls-data-tr-even").addClass("TRHover");
				}).mouseout(function(){
					$(this).removeClass("TRHover").addClass("cls-data-tr-even");
		});
		$("#commandData tbody tr:odd").addClass("cls-data-tr-odd").mouseover(
				function(){
					$(this).removeClass("cls-data-tr-odd").addClass("TRHover");
				}).mouseout(function(){
					$(this).removeClass("TRHover").addClass("cls-data-tr-odd");
		});
	}
	count = dataStr = temp = null;
	
}

function getServerCommandData(){
	$.ajax({
		   type: "GET",
		   url: "servlet/GetControlCommandInfo?t="+Math.random(),
		   //url:url+"?t=" +Math.random(),
		   dataType: "text",
		   success: function(dataObjs,Status) {
			   dataObjs = eval("(" + dataObjs+ ")");
			   if(dataObjs!=""||dataObjs!=null){
				  setCommandData(dataObjs);
						
				}
		   },
		   error:function(xhr, textStatus, error)
		   	{
		   		//alert("请求服务器数据出错，请重新刷新页面，错误原因如下： \n\nstatus="+xhr.status);	
		   	}
	});
}
//获取软件列表
function getSoftwareList(){
		$.ajax({
			   type: "GET",
			   url: "servlet/GetSoftwareType?t="+Math.random(),
			   //url:url+"?t=" +Math.random(),
			   dataType: "text",
			   success: function(dataObjs,Status) {
				   dataObjs = eval("(" + dataObjs+ ")");
				   if(dataObjs!=""&&dataObjs!=null){
					   if(dataObjs.softwareList == undefined || dataObjs.softwareList == null)return false;
					   softList = dataObjs.softwareList;
					   var dataStr  = '<option value="all">全部</option>';
					   var count = softList.length;
					   
					   for(var i = 0; i < count; i++){
						   dataStr += '<option value="' + softList[i].ID + '">' + softList[i].NAME + '</option>';
					   }
					   $("#softType").html(dataStr);
					   /*setSoftSelect(softList)
					   updateData();
					   setVoiceWarn(dataObjs);
					   if(warnData.length > 0){
						   top.warningID = setInterval("top.setWarningPic()",1000);
					   }else{
						   if(top.warningID != null){
							   clearInterval(top.warningID);
						   }
					   }*/
							
					}
			   },
			   error:function(xhr, textStatus, error)
			   	{
			   		//alert("请求服务器数据出错，请重新刷新页面，错误原因如下： \n\nstatus="+xhr.status);	
			   	}
		});
	
}

//加载页面时调用的代码，包括注册的各种事件响应函数
$(document).ready(function() {
		//向页面插入软件列表
		getSoftwareList();
			
		//绑定报警信息页面的各个复选框的点击事件
		$("#serious").click(function() {
					if ($(this).is(":checked")) {
						seriousFlag = 1;
					} else {
						seriousFlag = 0;
					}
					updateData()
		});
		$("#import").click(function() {
			if ($(this).is(":checked")) {
				importFlag = 1;
			} else {
				importFlag = 0;
			}
			updateData()
		});
		$("#general").click(function() {
			if ($(this).is(":checked")) {
				generalFlag = 1;
			} else {
				generalFlag = 0;
			}
			updateData()
		});
		$("#recover").click(function() {
			if ($(this).is(":checked")) {
				recoverFlag = 1;
			} else {
				recoverFlag = 0;
			}
			updateData()
		});
		$("#recoverable").click(function() {
			if ($(this).is(":checked")) {
				recoverableFlag = 1;
			} else {
				recoverableFlag = 0;
			}
			updateData()
		});
		$("#oneTime").click(function() {
			if ($(this).is(":checked")) {
				oneTimeFlag = 1;
			} else {
				oneTimeFlag = 0;
			}
			updateData()
			//alert(seriousFlag+"--"+importFlag+generalFlag+recoverFlag);
		});
		//刷新按钮的循环点击时间处理
		$("#stopRefresh").toggle(function(){
			stopRefresh();
			$(this).html("刷新");
		},function(){
			startRefresh();
			$(this).html("停止刷新");
		});
		
		//获取告警数据并更新页面
		getServerWarningData();
		timeoutID = setInterval("getServerWarningData()",5000);
		
		//获取命令状态并更新页面
		getServerCommandData();
		setInterval("getServerCommandData()", 1000);
	
});