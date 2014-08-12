var sender_event1 = null;	//CPU图表的事件变量
var sender_event2 = null;	//内存使用率表的事件变量
var cpuCount = 0;		//CPU的总数
var discCount = 0;		//磁盘总数
//var dataString = "";		//图表的组成数据
var colors = null;			//保存多个颜色值
var str="f6a91b2c80d4e537";//生成颜色值字典
var processName = "";//进程名称
var processID = 0;//进程ID号
var softName = "";//软件标识
var chartcpu = null;//cpu图表ID
var chartmem = null;//内存图表ID

var sysInfoCnt = 0;//缓存服务器系统信息数据计数
var cpuInfoCnt = 0;//缓存服务器CPU数据计数
var memInfoCnt = 0;//缓存服务器内存数据计数
var diskInfoCnt = 0;//缓存服务器磁盘数据计数
var procInfoCnt = 0;//缓存服务器进程数据计数
var serverStateCnt = 0;//缓存服务器工作状态数据计数
var tableSpaceInfoCnt = 0;//缓存服务器数据库表数据计数
//更新系统信息数据
function updateSystemInfo(data){
	try{
		if(data.serverName != undefined){
			$("#name").attr("value",data.serverName);
		}
		if(data.cpuType != undefined){
			$("#cputype").attr("value",data.cpuType);
		}
		if(data.cpuCount != undefined){
			$("#cpucount").attr("value",data.cpuCount);
		}
		if(data.memSize != undefined){
			$("#memsize").attr("value",data.memSize);
		}
		if(data.diskCount != undefined){
			$("#diskcount").attr("value",data.diskCount);
		}
		if(data.serverIP != undefined){
			$("#ipaddr").attr("value",data.serverIP);
		}
		if(data.osInfo != undefined){
			$("#osversion").attr("value",data.osInfo);
		}
		if(data.totalCpuUsage != undefined){
			$("#totalCPUUsageValue").attr("value",(data.totalCpuUsage).toFixed(2) + "%");
		}
		if(data.memUsageInfo !=undefined && data.memUsageInfo.memUsage != undefined){
			$("#totalMemUsageValue").attr("value",(data.memUsageInfo.memUsage).toFixed(2) + "%");
		}
	}catch(error){
		
	}
}
//绘制CPU使用率图
function drawCPUChart(data){
	var cpuCount = data.cpuCount;
	if(cpuCount == undefined || cpuCount == 0){
		//alert("没有CPU数据，请检查服务器状态，然后刷新页面！");
		return false;
	}else{
		 var dataString='<chart legendPosition="RIGHT" manageResize="1" bgColor="B3E9F7" bgAlpha="100" canvasBorderThickness="1" canvasBorderColor="008040" canvasBgColor="000000" yAxisMaxValue="100"  decimals="0" numdivlines="9" numVDivLines="28" numDisplaySets="30" divLineColor="008040" vDivLineColor="008040" divLineAlpha="100" chartLeftMargin="10" baseFontColor="00dd00" showRealTimeValue="0" numberSuffix="%" labelDisplay="rotate" slantLabels="1" toolTipBgColor="000000" toolTipBorderColor="008040" baseFontSize="11" showAlternateHGridColor="0" legendBgColor="000000" legendBorderColor="008040" showLabels="0">';
		 colors=new Array();
		 randomColor(cpuCount,colors);
		 for(var i=0;i<cpuCount;i++){
			 dataString+='<dataset color="'+colors[i]+'" seriesName="'+data.cpuUsageInfos[i].cpuID+'" showValues="0" alpha="100" anchorAlpha="0" lineThickness="2"><set value="0" /></dataset>';
		 }
		 dataString+='</chart>';
		 //新建FusionChars图表对象
		 chartcpu = new FusionCharts("fusionCharts/RealTimeLine.swf", "cpuUsageID", "100%", "100%", "0", "1" );
		 chartcpu.setXMLData(dataString);
		 chartcpu.render("cpuUsage");
		//为FusionChars对象增减监听对象
		 FusionCharts("cpuUsageID").addEventListener("Rendered", function(e,a) {
			if(e.sender.id=="tmpChartId") 
			return;
			sender_event1=e.sender;
	 	 });
		 dataString = null;
	}
	cpuCount = null;
}
//绘制内存使用率图
function drawMemChart(data){
	if(data.memUsageInfo == undefined || data.memUsageInfo.memUsage == undefined){
		 return false;
	 }else{
		//初始化FusionChars图表的各个元素
		 var dataString = '<chart manageResize="1" bgColor="B3E9F7" bgAlpha="100" canvasBorderThickness="1" canvasBorderColor="008040" canvasBgColor="000000" yAxisMaxValue="100"  decimals="0" numdivlines="9" numVDivLines="28" numDisplaySets="30" divLineColor="008040" vDivLineColor="008040" divLineAlpha="100" chartLeftMargin="10" baseFontColor="00dd00" showRealTimeValue="0" numberSuffix="%" labelDisplay="rotate" slantLabels="1" toolTipBgColor="000000" toolTipBorderColor="008040" baseFontSize="11" showAlternateHGridColor="0" legendBgColor="000000" legendBorderColor="008040" legendPadding="35" showLabels="0">';
		 dataString += '<categories><category label="Start"/></categories>';
	
		
		 dataString+='<dataset color="26F314" seriesName="内存使用率" showValues="0" alpha="100" anchorAlpha="0" lineThickness="2"><set value="0" /></dataset>';
		 
		 dataString+='</chart>';
		 //新建FusionChars图表对象
		 chartmem = new FusionCharts("fusionCharts/RealTimeLine.swf", "memUsageID", "100%", "100%", "0", "1" );
		 chartmem.setXMLData(dataString);
		 chartmem.render("memUsage");
		//为FusionChars对象增加监听对象
		 FusionCharts("memUsageID").addEventListener("Rendered", function(e,a) {
			if(e.sender.id=="tmpChartId") 
			return;
			sender_event2=e.sender;
	 	 });
		 dataString = null;
	 }
}
//更新磁盘使用情况数据
function setDiskData(data){
	if(data.diskCount == undefined || data.diskCount == 0){
		return false;
	}
	var dataStr = "";
	for(var i=0; i<data.diskCount; i++){
		dataStr += '<tr><td>' + data.diskUsageInfos[i].diskName + '</td><td>' + data.diskUsageInfos[i].mountPoint + '</td><td>' + data.diskUsageInfos[i].diskSize + '</td><td>' + data.diskUsageInfos[i].diskUsedSize + '</td><td>' + (data.diskUsageInfos[i].diskUsage).toFixed(2) + '</td></tr>';
	}
	$("#diskUsage tbody").html(dataStr);
	dataStr = "";
	//若服务器返回的数据不够八条，则在原有数据后面追加空数据
	if(data.diskCount<8){
		for(var i=0; i<8-data.diskCount; i++){
			dataStr += '<tr><td></td><td></td><td></td><td></td><td></td></tr>';
		}
		$("#diskUsage tbody").append(dataStr);
	}
	dataStr = null;
	//为磁盘数据增加样式，奇偶交叉样式
	$("#diskUsage tbody tr td").addClass("cls-data-td-editlist");
	$("#diskUsage tbody tr:even").addClass("cls-data-tr-even").mouseover(
			function(){
				$(this).attr("style","background-color:#CCFAFF;");
			}).mouseout(function(){
				$(this).attr("style","background-color:white;");
			});
	$("#diskUsage tbody tr:odd").addClass("cls-data-tr-odd").mouseover(
			function(){
				$(this).attr("style","background-color:#CCFAFF;");
			}).mouseout(function(){
				$(this).attr("style","background-color:#FBFBFB;");
			});
}
//更新进程信息
function setProcessData(data){
	if(data.procCount == undefined || data.procCount == 0){
		return false;
	}
	//根据服务器的返回值构建图表的所有数据
	var dataStr = "";
	for(var i=0; i<data.procCount; i++){
		var status = "";
		if(data.procInfos[i].procStatus == "N"){
			status = "正常";
			dataStr += '<tr>';
		}else{
			status = "故障";
			dataStr += '<tr style="color:red;">';
		}
		dataStr += '<td class="cls-data-td-editlist">' + data.procInfos[i].softwareName + '</td><td class="cls-data-td-editlist">' + data.procInfos[i].imageName + '</td><td class="cls-data-td-editlist">' + data.procInfos[i].procID + '</td><td class="cls-data-td-editlist">' + data.procInfos[i].userName + '</td><td class="cls-data-td-editlist">' + status + '</td><td class="cls-data-td-editlist">';
		dataStr += (data.procInfos[i].cpuUsage).toFixed(3) + '</td><td class="cls-data-td-editlist">' + data.procInfos[i].totalCPUTime + '</td><td class="cls-data-td-editlist">' + data.procInfos[i].memUsed + '</td><td class="cls-data-td-editlist">' + data.procInfos[i].priority + '</td><td class="cls-data-td-editlist">' + data.procInfos[i].statusDesc + '</td></tr>';
	}
	$("#processData tbody").html(dataStr);
	
	//为表格数据增加样式，并且当数据不足八条时，用空数据补到八条
	dataStr = "";
	if(data.procCount<8){
		for(var i=0; i<8-data.procCount; i++){
			dataStr += '<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>';
		}
		$("#processData tbody").append(dataStr);
	}
	dataStr = null;
	$("#processData tbody tr td").addClass("cls-data-td-editlist");
	$("#processData tbody tr").attr("oncontextmenu","return false;");
	$("#processData tbody tr:even").addClass("cls-data-tr-even").mouseover(
			function(){
				$(this).removeClass("cls-data-tr-even").addClass("TRHover");
			}).mouseout(function(){
				$(this).removeClass("TRHover").addClass("cls-data-tr-even");
			});
	$("#processData tbody tr:odd").addClass("cls-data-tr-odd").mouseover(
			function(){
				$(this).removeClass("cls-data-tr-odd").addClass("TRHover");
			}).mouseout(function(){
				$(this).removeClass("TRHover").addClass("cls-data-tr-odd");
			});
	
	/*禁用右键菜单*/
	/*$(document).bind("contextmenu",function(e){
			return false;
	});*/
	var role = $("#role").val();
	//判断是否为高级用户
	if(role == 0){
		//为每条进程数据绑定右键处理函数
		var menuTarget = $("#processData tbody tr");
		var menu = $("#contextmenu");
		$("#processData tbody tr").bind("contextmenu",function(){
			$(this).addClass("TRHover");
			var target = $(this);
			var targetTD = target.context.childNodes;
			softName = targetTD[1].innerText;
			processName = targetTD[1].innerText;
			processID = targetTD[2].innerText;
			//根据进程ID禁止一些操作
			if(processID == -1){
				$("#stop").addClass("disableselect");
				$("#start").removeClass("disableselect");
				$("#restart").addClass("disableselect");
			}else{
				$("#start").addClass("disableselect");
				$("#stop").removeClass("disableselect");
				$("#restart").removeClass("disableselect");
			}
			//弹出右键菜单
			var event = window.event||event;
			var pagex,pagey;
			pagex = event.clientX + (document.body.scrollLeft || document.documentElement.scrollLeft);
			pagey = event.clientY + (document.body.scrollTop || document.documentElement.scrollTop);
			$("#contextmenu").css({"left":pagex +5,"top": pagey+5,"display":"inline"});
			return false;
		});
		
		//在页面其他位置点击是，右键菜单消失
		document.onclick = function(){
			if(document.activeElement != menu)
				contextmenu.style.display = "none";
		}
	}
}

//设置数据库服务器表信息数据
function setTableData(data){
	try{
		if(data.tableSpaceInfo == undefined || data.tableSpaceInfo == null){
			return false;
		}else{
			$("#tableSizeValue").attr("value",data.tableSpaceInfo.totalSpace + "MB");
			$("#tableUsageValue").attr("value",((data.tableSpaceInfo.tableUsage)*100).toFixed(2) + "%");
			
		}
	}catch(ex){
		
	}
}

//初始化页面数据信息和图表
function initCharts(serverid){
	$.ajax({
		   type: "GET",
		   url:"servlet/GetServerInfo?serverID=" + serverid +"&t=" +Math.random(),
		   dataType: "text",
		   success: function(dataObjs,Status) {
			   dataObjs = eval("(" + dataObjs+ ")");
			   if(dataObjs.length == 0)return false;
			   if(dataObjs!=undefined){
				   updateSystemInfo(dataObjs);
				   //绘制内存使用率图表
				   drawMemChart(dataObjs);
				   drawCPUChart(dataObjs);
				   setDiskData(dataObjs);
				   setProcessData(dataObjs);
				   setTableData(dataObjs);
			   }else{
				   alert("服务器返回数据为空！")
				   return false;
			   }
			},
		   	error:function(xhr, textStatus, error)
		   	{
		   	}
		});
}
//相隔固定时间向服务器请求数据
function getServerData(serverid){
	$.ajax({
		   type: "GET",
		   url:"servlet/GetServerInfo?serverID=" + serverid +"&t=" +Math.random(),
		   dataType: "text",
		   success: function(dataObjs,Status) {
		   dataObjs = eval("(" + dataObjs+ ")");
		   if(dataObjs.length == 0)return false;
			   if(dataObjs!=""||dataObjs!=null){
				   
				   //若cpu图表对象为空，则重新绘制
				   if(chartcpu == null){
					   drawCPUChart(dataObjs);
				   }else{
					   //若本次服务器数据计数和上次相同，则说明发送的是服务器缓存的数据，不更新图表,若不相同则更新
					   if(dataObjs.cpuInfoCnt != undefined && cpuInfoCnt != dataObjs.cpuInfoCnt){
							//实时更新CPU数据
							feedData(dataObjs,1);
							//更新缓存计数
							cpuInfoCnt = dataObjs.cpuInfoCnt;
					   }
				   }
				   if(chartmem == null){
					   drawMemChart(dataObjs);
				   }else{
					 //若本次服务器数据计数和上次相同，则说明发送的是服务器缓存的数据，不更新图表,若不相同则更新
					   if(dataObjs.memInfoCnt != undefined && memInfoCnt != dataObjs.memInfoCnt){
						   //实时更新内存数据
							feedData(dataObjs,2);
							//更新缓存计数
							memInfoCnt = dataObjs.memInfoCnt;
					   }
				   }
				   //若本次服务器数据计数和上次相同，则说明发送的是服务器缓存的数据，不更新图表,若不相同则更新
				   if(dataObjs.sysInfoCnt != undefined && sysInfoCnt != dataObjs.sysInfoCnt){
					   	//更新系统信息数据
					    updateSystemInfo(dataObjs);
					    //更新缓存计数
						sysInfoCnt = dataObjs.sysInfoCnt;
				   }
				   //若本次服务器数据计数和上次相同，则说明发送的是服务器缓存的数据，不更新图表,若不相同则更新
				   if(dataObjs.diskInfoCnt != undefined && diskInfoCnt != dataObjs.diskInfoCnt){
					    //设置磁盘数据
					    setDiskData(dataObjs);
					    //更新缓存计数
						diskInfoCnt = dataObjs.diskInfoCnt;
				   }
				   //若本次服务器数据计数和上次相同，则说明发送的是服务器缓存的数据，不更新图表,若不相同则更新
				   if(dataObjs.procInfoCnt != undefined && procInfoCnt != dataObjs.procInfoCnt){
					    //设置进程数据
					    setProcessData(dataObjs);
					    //更新缓存计数
						procInfoCnt = dataObjs.procInfoCnt;
				   }
				   //若本次服务器数据计数和上次相同，则说明发送的是服务器缓存的数据，不更新图表,若不相同则更新
				   if(dataObjs.tableSpaceInfoCnt != undefined && tableSpaceInfoCnt != dataObjs.tableSpaceInfoCnt){	
					    //设置数据库表数据
					    setTableData(dataObjs);
					    //更新缓存计数
					    tableSpaceInfoCnt = dataObjs.tableSpaceInfoCnt;
				   }
				   
				   
				 /*//若本次服务器数据计数和上次相同，则说明发送的是服务器缓存的数据，不更新图表,若不相同则更新
				   if(dataObjs.count != undefined && dataCount != dataObjs.count){
					   if(chartcpu == null){
						   drawCPUChart(dataObjs);
					   }else{
						   //实时刷新CPU图表数据
							feedData(dataObjs,1);
					   }
					   if(chartmem == null){
						   drawMemChart(dataObjs);
					   }else{
						 //实时刷新内存数据
							feedData(dataObjs,2);
					   }
					   //更新系统信息数据
					    updateSystemInfo(dataObjs);
					    //设置磁盘数据
					    setDiskData(dataObjs);
					    //设置进程数据
					    setProcessData(dataObjs);
					    //设置数据库表数据
					    setTableData(dataObjs);
					    
					  //更新缓存计数
					    dataCount = dataObjs.count;
				   }*/
			   }
		   },
		   error:function(xhr, textStatus, error)
		   	{
		   		//alert("请求服务器数据出错，请重新刷新页面，错误原因如下： \n\nstatus="+xhr.status);	
		   	}
	});
}
//实时刷新图表数据，当index为1时刷新CPU图表数据，为2时刷新内存数据
function feedData(dataObjs,index){
	try{
			 var fd="&value=";
			 switch(index){
			 	//刷新CPU数据
				 case 1:
					 if(dataObjs.cpuCount == undefined || dataObjs.cpuCount == 0){
						 return false;
					 }
					 for(var i=0;i<dataObjs.cpuCount;i++){
						 fd+=(dataObjs.cpuUsageInfos[i].cpuUsage).toFixed(2)+"|";
					 }
					fd = fd.substring(0, fd.length-2);
					 if(sender_event1!=undefined ){
						 updater= sender_event1.feedData? sender_event1 : null;
						 if(updater){
							 updater.feedData(fd);
						 }
					 } 
					 break;
				//刷新内存数据
				 case 2:
					 if(dataObjs.memUsageInfo == undefined || dataObjs.memUsageInfo.memUsage == undefined){
						 return false;
					 }
					 //构造实时刷新数据
					 fd +=(dataObjs.memUsageInfo.memUsage).toFixed(2);
					 if(sender_event2!=undefined ){
						 updater= sender_event2.feedData? sender_event2 : null;
						 if(updater){
							 updater.feedData(fd);
						 }
					 }
					 break;
				default:
					break;
					 
				 
			}
	}catch(error){
		
	}
}

//生成随机颜色值，生成颜色的种数由num参数决定，生成后的颜色值保存在参数arrayNum数组中
function randomColor(num,arrayNum)
{
	for(var i=0;i<num;i++)
	{
		 var color="";
		  arrayNum[i]=new Array();
		 for(var j=0;j<6;j++)
		 {
		   	color=color+str.charAt(Math.random()*str.length);
		 }
		 arrayNum[i]=color;
	}
	return arrayNum;
}

//进程指令发送函数，commandType为命令类型，softName为软件名称，user为当前用户名，ipAddr为当前登录用户的电脑IP地址，serverID为服务器的编号，从文件服务器1开始由0递增
function sendProcessCommand(commandType,softName,user,ipAddr,serverID){
	$.ajax({
		   type: "GET",
		   url:"servlet/SendProcCommand?commandType=" + commandType + "&softName=" + softName + "&user=" + user + "&ipAddr=" + ipAddr + "&serverID=" + serverID +"&t=" +Math.random(),
		   dataType: "text",
		   //ajax命令发送成功处理函数
		   success: function(dataObjs,Status) {
				dataObjs = eval("(" + dataObjs + ")");
				var commandStr = "";
				switch(commandType){
				case 0:
					commandStr = "启动";
					break;
				case 1:
					commandStr = "终止";
					break;
				case 2:
					commandStr = "重启";
					break;
				case 3:
					commandStr = "获取当前进程状态";
					break;
				default:
					commandStr = "";
					break;
				}
				if(dataObjs == null || dataObjs.result == undefined){
					alert(commandStr + "命令发送失败");
				}else if(dataObjs.result == 1){
					alert(commandStr + "命令发送成功");
				}
	
		   },
		   // //ajax命令发送失败处理函数
		   error:function(xhr, textStatus, error)
		   	{
			   alert(commandStr + "命令发送失败,未到服务器");
		   	}
	});
}

//初始化进程信息显示表格中的各种右键指令，以及这些指令的具体动作
function initContextMenu(serverID){
	var user = $("#user").val();
	var ipAddr = $("#ipAddr").val();
	
	//绑定启动命令处理函数
	$("#start").click(function(){
		if(processID == -1){
			sendProcessCommand(0,softName,user,ipAddr,serverID);
		}else{
			$("#contextmenu").css("display","inline");
			return false;
		}
	});
	//绑定终止命令处理函数
	$("#stop").click(function(){
		if(processID == -1){
			$("#contextmenu").css("display","inline");
			return false;
			
		}else{
			sendProcessCommand(1,softName,user,ipAddr,serverID);
		}
	});
	//绑定重启命令处理函数
	$("#restart").click(function(){
		if(processID == -1){
			$("#contextmenu").css("display","inline");
			return false;
		}else{
			sendProcessCommand(2,softName,user,ipAddr,serverID);
		}
	});
	//绑定获取状态命令处理函数
	$("#getStatus").click(function(){
		sendProcessCommand(3,softName,user,ipAddr,serverID);
	});

}

//获取各个服务器状态信息,并根据服务器的状态值选择性地更新图表数据
function getServerState(serverid){
	 $.ajax({
		   type: "GET",
		   url: "servlet/GetServerState?t="+Math.random(),
		   dataType: "json",
		   success: function(data,Status) {
		 			switch(serverid){
		 			case '1':
		 				if(data.fileServer1NetStatus == 0){
		 					getServerData(serverid);
		 				}
		 				break;
		 			case '2':
		 				if(data.fileServer2NetStatus == 0){
		 					getServerData(serverid);
		 				}
		 				break;
		 			case '3':
		 				if(data.webServer1NetStatus == 0){
		 					getServerData(serverid);
		 				}
		 				break;
		 			case '4':
		 				if(data.webServer2NetStatus == 0){
		 					getServerData(serverid);
		 				}
		 				break;
		 			case '5':
		 				if(data.cmsServer1NetStatus == 0){
		 					getServerData(serverid);
		 				}
		 				break;
		 			case '6':
		 				if(data.dbServer1NetStatus == 0){
		 					getServerData(serverid);
		 				}
		 				break;
		 			case '7':
		 				if(data.dbServer2NetStatus == 0){
		 					getServerData(serverid);
		 				}
		 				break;
		 			case '8':
		 				if(data.appServer1NetStatus == 0){
		 					getServerData(serverid);
		 				}
		 				break;
		 			case '9':
		 				if(data.appServer2NetStatus == 0){
		 					getServerData(serverid);
		 				}
		 				break;
		 			default :
		 				break;
		 			}
		 			
			},
		   	error:function(xhr, textStatus, error)
		   	{
		   		//alert("请求系统监视服务器数据出错，错误信息如下：\n\nstatus="+xhr.status);	
		   	}
		});
	
}
//页面加载时调用js代码
function doStart(serverid){
	chartcpu = null;
	//调用初始化图表函数
	initCharts(serverid);
	getServerState(serverid);
	var role = $("#role").val();
	//判断是否为高级用户
	if(role == 0){
		initContextMenu(serverid);
	}
	//隔5秒钟向服务器请求数据
	//setInterval("getServerState('" + serverid + "')",5000);
	setInterval("getServerData('" + serverid + "')",1000);
	
	/*禁用右键菜单*/
	$(document).bind("contextmenu",function(e){
			return false;
	});
	
	//动态加载页面显示样式文件
	var head = document.getElementsByTagName('HEAD').item(0);
	var styleS = document.createElement('link');
	var ht = location.href;
	var ind = ht.lastIndexOf("/");
	//获取服务器地址路径
	var server = ht.substring(0,ind);
	var pn = location.pathname;
	styleS.rel = 'stylesheet';
	styleS.type = 'text/css';
	//判断若为IE浏览器则加载iestyle样式文件，若为非IE浏览器加载style样式文件
	if(navigator.userAgent.indexOf("MSIE")>-1){
		styleS.href = server  + '/css/iestyle.css';
	}else{
		styleS.href = server  + '/css/style.css';
	}
	head.appendChild(styleS);
}