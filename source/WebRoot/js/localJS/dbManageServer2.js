var sender_event1;	//CPU图表的事件变量
var sender_event2;	//内存使用率表的事件变量
var cpuCount;		//CPU的总数
var discCount;		//磁盘总数
var dataString;		//图表的组成数据
var colors;			//保存多个颜色值
var str="f6a91b2c80d4e537";//生成颜色值字典

//更新系统信息数据
function updateSystemInfo(data){
	$("#name").attr("value",data.serverName);
	$("#cputype").attr("value",data.cpuType);
	$("#cpucount").attr("value",data.cpuCount);
	$("#memsize").attr("value",data.memSize);
	$("#diskcount").attr("value",data.diskCount);
	$("#ipaddr").attr("value",data.serverIP);
	$("#osversion").attr("value",data.osInfo);
	$("#totalCPUUsageValue").attr("value",data.totalCpuUsage);
	$("#totalMemUsageValue").attr("value",data.memUsageInfo.memUsage);
}
//绘制CPU使用率图
function drawCPUChart(data){
	var cpuCount = data.cpuCount;
	if(cpucount == undefined){
		alert("没有CPU数据，请检查服务器状态，然后刷新页面！");
		return false;
	}else{
		 dataString='<chart manageResize="1" bgColor="B3E9F7" bgAlpha="100" canvasBorderThickness="1" canvasBorderColor="008040" canvasBgColor="000000" yAxisMaxValue="100"  decimals="0" numdivlines="9" numVDivLines="28" numDisplaySets="30" divLineColor="008040" vDivLineColor="008040" divLineAlpha="100" chartLeftMargin="10" baseFontColor="00dd00" showRealTimeValue="0" numberSuffix="%" labelDisplay="rotate" slantLabels="1" toolTipBgColor="000000" toolTipBorderColor="008040" baseFontSize="11" showAlternateHGridColor="0" legendBgColor="000000" legendBorderColor="008040" legendPadding="35" showLabels="0">';
		 colors=new Array();
		 randomColor(cpuCount,colors);
		 for(var i=0;i<cpuCount;i++){
			 dataString+='<dataset color="'+colors[i]+'" seriesName="CPU ID:'+data.cpuUsageInfos[i].cpuID+'" showValues="0" alpha="100" anchorAlpha="0" lineThickness="2"><set value="0" /></dataset>';
		 }
		 dataString+='</chart>';
		 //FusionCharts.setCurrentRenderer('javascript');
		 var chart1 = new FusionCharts("fusionCharts/RealTimeLine.swf", "cpuUsageID", "100%", "100%", "0", "1" );
		 chart1.setXMLData(dataString);
		 chart1.render("cpuUsage");
		
		 FusionCharts("cpuUsageID").addEventListener("Rendered", function(e,a) {
			if(e.sender.id=="tmpChartId") 
			return;
			sender_event1=e.sender;
	 	 });
	}
}
//绘制内存使用率图
function drawMemChart(data){
	var cpucount = data.cpuCount;
	
	 //dataString='<chart bgColor="000000" manageResize="1" yAxisMaxValue="100" decimals="0" numdivlines="9" numVDivLines="28" numDisplaySets="30" showRealTimeValue="0" numberSuffix="%" labelDisplay="rotate" slantLabels="1" legendPadding="35" showLabels="1">';
	 dataString = '<chart manageResize="1" bgColor="B3E9F7" bgAlpha="100" canvasBorderThickness="1" canvasBorderColor="008040" canvasBgColor="000000" yAxisMaxValue="100"  decimals="0" numdivlines="9" numVDivLines="28" numDisplaySets="30" divLineColor="008040" vDivLineColor="008040" divLineAlpha="100" chartLeftMargin="10" baseFontColor="00dd00" showRealTimeValue="0" numberSuffix="%" labelDisplay="rotate" slantLabels="1" toolTipBgColor="000000" toolTipBorderColor="008040" baseFontSize="11" showAlternateHGridColor="0" legendBgColor="000000" legendBorderColor="008040" legendPadding="35" showLabels="0">';
	 dataString += '<categories><category label="Start"/></categories>';

	
	 dataString+='<dataset color="26F314" seriesName="内存使用率" showValues="0" alpha="100" anchorAlpha="0" lineThickness="2"><set value="0" /></dataset>';
	 
	 dataString+='</chart>';
	 //FusionCharts.setCurrentRenderer('javascript');
	 var chart2 = new FusionCharts("fusionCharts/RealTimeLine.swf", "memUsageID", "100%", "100%", "0", "1" );
	 chart2.setXMLData(dataString);
	 chart2.render("memUsage");
	
	 FusionCharts("memUsageID").addEventListener("Rendered", function(e,a) {
		if(e.sender.id=="tmpChartId") 
		return;
		sender_event2=e.sender;
 	 });
}
//更新磁盘使用情况数据
function setDiskData(data){
	var dataStr = "";
	for(var i=0; i<data.diskCount; i++){
		dataStr += '<tr><td>' + data.diskUsageInfos[i].diskName + '</td><td>' + data.diskUsageInfos[i].mountPoint + '</td><td>' + data.diskUsageInfos[i].diskSize + '</td><td>' + data.diskUsageInfos[i].diskUsedSize + '</td><td>' + data.diskUsageInfos[i].diskUsage + '</td></tr>';
	}
	$("#diskUsage tbody").html(dataStr);
}
//更新进程信息
function setProcessData(data){
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
		dataStr += '<td>' + data.procInfos[i].softwareName + '</td><td>' + data.procInfos[i].imageName + '</td><td>' + data.procInfos[i].procID + '</td><td>' + data.procInfos[i].userName + '</td><td>' + status + '</td><td>';
		dataStr += data.procInfos[i].cPUUsage*100 + '</td><td>' + data.procInfos[i].totalCPUTime + '</td><td>' + data.procInfos[i].memUsed + '</td><td>' + data.procInfos[i].priority + '</td><td>' + data.procInfos[i].statusDesc + '</td></tr>';
	}
	$("#processData tbody").html(dataStr);
}
function initCharts(){
	$.ajax({
		   type: "GET",
		   url: "servlet/GetWebServer1Info?t="+Math.random(),
		   dataType: "json",
		   success: function(dataObjs,Status) {
			   
			   if(dataObjs!=undefined){
				   
				   
				   updateSystemInfo(dataObjs);
				   drawCPUChart(dataObjs);
				   drawMemChart(dataObjs);
				   setDiskData(dataObjs);
				   setProcessData(dataObjs);
			   }else{
				   alert("服务器返回数据为空！")
				   return false;
			   }
			   getServerData();	
			},
		   	error:function(xhr, textStatus, error)
		   	{
		   		alert("请求服务器数据失败 \n\nstatus="+xhr.status);	
		   	}
		});
}

function getServerData(){
	$.ajax({
		   type: "GET",
		   url: "servlet/GetWebServer1Info?t="+Math.random(),
		   dataType: "json",
		   success: function(dataObjs,Status) {
			   
			   if(dataObjs!=""||dataObjs!=null){
				    updateSystemInfo(dataObjs);	
				    setDiskData(dataObjs);
				    setProcessData(dataObjs);
					feedData(dataObjs,1);
					feedData(dataObjs,2);
			   }
				
			setTimeout("getServerData()",5000);
//			setTimeout(function(){pollCPUServer();},5000);

	
		   },
		   error:function(xhr, textStatus, error)
		   	{
		   		alert("请求服务器数据出错，请重新刷新页面，错误原因如下： \n\nstatus="+xhr.status);	
		   	}
	});
}

function feedData(dataObjs,index){
			//var dataObjs = eval("("+message+")");
			 //var dateTimeLabel=dataObjs.datetime.split(" ");
			 var fd="&value=";
			 switch(index){
			 
				 case 1:
					 for(var i=0;i<dataObjs.cpuCount;i++){
						 fd+=dataObjs.cpuUsageInfos[i].cpuUsage+"|";
					 }
					fd = fd.substring(0, fd.length-2);
					 if(sender_event1!=undefined ){
						 updater= sender_event1.feedData? sender_event1 : null;
						 if(updater){
							 updater.feedData(fd);
						 }
					 } 
					 break;
				
				 case 2:
					 //alert(dataObjs.m_struMemoryState.m_dbMemUsage);
					 fd +=dataObjs.memUsageInfo.memUsage;
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
}


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


function doStart(){
	
	initCharts();
	
	//initCharts("rhfwqxtxxMEM.action",1);
	
	/*
	var chart1 = new FusionCharts("fusionCharts/RealTimeLine.swf", "cpuUsageChartID", "100%", "100%", "0", "1" );
	 chart1.setXMLUrl("xml/fileServer1/cpuCharts.xml");
	 chart1.render("cpuUsage");
	 
	 var chart2 = new FusionCharts("fusionCharts/RealTimeLine.swf", "memUsageChartID", "100%", "100%", "0", "1" );
	 chart2.setXMLUrl("xml/fileServer1/memCharts.xml");
	 chart2.render("memUsage");
	 */
}
$(document).ready(function(){
	//$("(tbody tr td):even").addClass("evenTr");
});
