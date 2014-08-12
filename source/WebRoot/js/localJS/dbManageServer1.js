var xmlHttp;
var sender_event1;	//CPU图表的事件变量
var sender_event2;	//内存使用率表的事件变量
var cpuCount;		//CPU的总数
var discCount;		//磁盘总数
var dataString;		//图表的组成数据
var colors;			//保存多个颜色值
var str="f6a91b2c80d4e537";//生成颜色值字典
var oldTimeCPU;		//记录上一次CPU数据请求的时间
var oldTimeDisc;		//记录上一次磁盘数据请求的时间
var oldTimeMem;		//记录上一次内存数据请求的时间


function initCharts(url,chart){
	$.ajax({
		   type: "GET",
		   url: url+"?t="+Math.random(),
		   dataType: "json",
		   success: function(dataObjs,Status) {
			   
			   if(dataObjs!=undefined){
				   switch(chart){
				   		case 0:
				   		{
				   			
				   			 cpuCount=dataObjs.m_iCPUUseCnt;
							 dataObjs.m_arrCPUState;
							 if(cpuCount==undefined){
								 //setTimeout(function(){initCharts(url,chart);},5000);
								 alert("服务器请求CPU使用数据无返回值，请确认服务器开启后再刷新页面！");
								 return false;
							 }
							 else{
								
								 //bgImage="../images/chartsBgPic4.gif" bgImageDisplayMode="tile" toolTipBgColor="000000" toolTipBorderColor="008040" baseFontSize="11" showAlternateHGridColor="0" legendBgColor="000000" legendBorderColor="008040" bgAlpha="100" canvasBorderThickness="1" canvasBorderColor="008040" canvasBgColor="000000" divLineColor="008040" vDivLineColor="008040" divLineAlpha="100" chartLeftMargin="10" baseFontColor="F73322"
								 dataString='<chart paletteThemeColor="669933" manageResize="1" yAxisMaxValue="100" decimals="0" numdivlines="9" numVDivLines="28" numDisplaySets="30" showRealTimeValue="0" numberSuffix="%" labelDisplay="rotate" slantLabels="1" legendPadding="35" showLabels="1"><categories><category label="Start"/></categories>';
								 colors=new Array();
								 randomColor(cpuCount+1,colors);
								 for(var i=0;i<cpuCount;i++){
									 dataString+='<dataset color="'+colors[i]+'" seriesName="CPU ID:'+dataObjs.m_arrCPUState[i].m_iCpuID+'" showValues="0" alpha="100" anchorAlpha="0" lineThickness="2"><set value="0" /></dataset>';
								 }
								 dataString+='<dataset color="'+colors[cpuCount]+'" seriesName="Total CPU Usage" showValues="0" alpha="100" anchorAlpha="0" lineThickness="2"><set value="0" /></dataset>';
								 dataString+='</chart>';
								 //FusionCharts.setCurrentRenderer('javascript');
								 var chart1 = new FusionCharts("../charts/RealTimeLine.swf", "cpuUsageID", "100%", "100%", "0", "1" );
								 chart1.setXMLData(dataString);
								 chart1.render("cpuUsage");
								
								 FusionCharts("cpuUsageID").addEventListener("Rendered", function(e,a) {
									if(e.sender.id=="tmpChartId") 
									return;
									sender_event1=e.sender;
							 	 });
								 
								 
								 pollCPUServer();
							 }
				   			break;
				   		}
				   		case 1:
					   		{
								 dataString='<chart manageResize="1" bgImage="../images/chartsBgPic5.gif" bgImageDisplayMode="tile" bgAlpha="100" canvasBorderThickness="1" exportEnabled="1" exportAtClient="1" exportHandler="CPUChartsSaveId" exportDialogMessage="���ڵ���..." exportFileName="�ڴ�ʹ����ͼ" exportFormats="PNG=���ΪPNGͼƬ�ļ�|JPG=���ΪJPGͼƬ�ļ�|PDF=���ΪPDF��ʽ�ļ�" bgAlpha="100" canvasBorderThickness="1"  exportDialogColor="e1f5ff" exportDialogBorderColor="0372ab" exportDialogFontColor="0372ab" exportDialogPBColor="0372ab" canvasBorderColor="008040" canvasBgColor="000000" yAxisMaxValue="100"  decimals="0" numdivlines="9" numVDivLines="28" numDisplaySets="30" divLineColor="008040" vDivLineColor="008040" divLineAlpha="100" chartLeftMargin="10" baseFontColor="F73322" showRealTimeValue="0" numberSuffix="%" labelDisplay="rotate" slantLabels="1" toolTipBgColor="000000" toolTipBorderColor="008040" baseFontSize="11" showAlternateHGridColor="0" legendBgColor="000000" legendBorderColor="008040" legendPadding="35" showLabels="1"><categories><category label="Start"/></categories>';					 
								 for(var i=0;i<memCount;i++){
									 dataString+='<dataset color="'+colors[i]+'" seriesName="内存编号:'+dataObjs.m_arrCPUState[i].m_iCpuID+'" showValues="0" alpha="100" anchorAlpha="0" lineThickness="2"><set value="0" /></dataset>';
								 }
								
								 dataString+='</chart>';
								 //FusionCharts.setCurrentRenderer('javascript');
								 var chart2 = new FusionCharts("../charts/RealTimeLine.swf", "memUsageID", "100%", "100%", "0", "1" );
								 chart2.setXMLData(dataString);
								 chart2.render("memUsage");
							
								 FusionCharts("memUsageID").addEventListener("Rendered", function(e,a) {
									if(e.sender.id=="tmpChartId") 
									return;
									sender_event2=e.sender;
							 	 });
								 pollMemServer();
								 break;
					   		}
						default:
							break;
				   }
					 
				 }
		   			
			},
		   	error:function(xhr, textStatus, error)
		   	{
		   		alert("请求服务器数据失败 \n\nstatus="+xhr.status);	
		   	}
		});
}

function pollCPUServer(){
	$.ajax({
		   type: "GET",
		   url: "rhfwqxtxxCPU.action?t="+Math.random(),
		   dataType: "json",
		   success: function(dataObjs,Status) {
			   
			   if(dataObjs!=""||dataObjs!=null){
				     var dateTimeLabel;
				     if((dataObjs.datetime!=undefined)&&(dataObjs.datetime.indexOf(" ")!=-1)){
				   		 dateTimeLabel=dataObjs.datetime.split(" ");
						 if(dateTimeLabel[1]!=oldTimeCPU){
							 oldTimeCPU=dateTimeLabel[1];
							
							 feedData(dataObjs,1);
						 }
				   	 }
				
					 setTimeout("pollCPUServer()",5000);
//					 setTimeout(function(){pollCPUServer();},5000);
			 	}
	
		   },
		   error:function(xhr, textStatus, error)
		   	{
		   		alert("请求服务器CPU使用数据出错，错误原因如下： \n\nstatus="+xhr.status);	
		   	}
	});
}

function pollMemServer(){
	$.ajax({
		   type: "GET",
		   url: "rhfwqxtxxMEM.action?t="+Math.random(),
		   dataType: "json",
		   success: function(dataObjs,Status) {
			   if(dataObjs!=""||dataObjs!=null){
				     var dateTimeLabel;
				   	 if((dataObjs.datetime!=undefined)&&(dataObjs.datetime.indexOf(" ")!=-1)){
				   		 dateTimeLabel=dataObjs.datetime.split(" ");
						 if(dateTimeLabel[1]!=oldTimeMem){
							 oldTimeMem=dateTimeLabel[1];
							 feedData(dataObjs,2);
						 }
				   	 }
		
				
					 setTimeout("pollMemServer()",5000);
//					 setTimeout(function(){pollMemServer();},5000);
			 	}
	
		   },
		   error:function(xhr, textStatus, error)
		   	{
		   		alert("请求\n\nstatus="+xhr.status);	
		   	}
	});
}


function pollDiscServer(){

	$.ajax({
		   type: "GET",
		   cache: "false",
		  
		   url: "rhfwqxtxxDISC.action?t="+Math.random(),
		   dataType: "json",
		   success: function(dataObjs,Status) {
			   if(dataObjs!=""||dataObjs!=null){
				     var dateTimeLabel;
				     if((dataObjs.datetime!=undefined)&&(dataObjs.datetime.indexOf(" ")!=-1)){
				   		 dateTimeLabel=dataObjs.datetime.split(" ");
						 if(dateTimeLabel[1]!=oldTimeDisc){
							 oldTimeDisc=dateTimeLabel[1];
							 
						
							 for(var i=0; i<senderEventArrDisk.length; i++){
								 if(senderEventArrDisk[i]!=undefined ){
									 updater= senderEventArrDisk[i].feedData? senderEventArrDisk[i] : null;
									 if(updater){
										 updater.feedData("&value="+dataObjs.m_DiskState[i].m_dbDiskUsage);
	//									 updater.feedData("&value="+Math.random()*100);
									 }
								 } 
							 }
						 }
					 }
					 $("#discTimer").html(dataObjs.datetime);
					
					 setTimeout("pollDiscServer()",5000);
//					 setTimeout(function(){pollDiscServer();},5000);
			 	}
	
		   },
		   error:function(xhr, textStatus, error)
		   	{
		   		alert("\n\nstatus="+xhr.status);	
		   	}
	});
	
	
}

function feedData(dataObjs,index){
	//var dataObjs = eval("("+message+")");
			 var dateTimeLabel=dataObjs.datetime.split(" ");
			 var fd="&label=" + dateTimeLabel[1] + "&value=";
			 switch(index){
			 
				 case 1:
					 for(var i=0;i<cpuCount;i++){
						 fd+=dataObjs.m_arrCPUState[i].m_dbCpuUsage+"|";
					 }
					 fd += dataObjs.m_fTotalCpuUsage;
					 if(sender_event1!=undefined ){
						 updater= sender_event1.feedData? sender_event1 : null;
						 if(updater){
							 updater.feedData(fd);
						 }
					 } 
					 break;
				
				 case 2:
					 //alert(dataObjs.m_struMemoryState.m_dbMemUsage);
					 fd +=dataObjs.m_dbMemUsage;
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


function doStart(serverUrl,serverName){
	
	initCharts("rhfwqxtxxCPU.action",0);
	
	initCharts("rhfwqxtxxMEM.action",1);
	
	/*
	var chart1 = new FusionCharts("fusionCharts/RealTimeLine.swf", "cpuUsageChartID", "100%", "100%", "0", "1" );
	 chart1.setXMLUrl("xml/fileServer1/cpuCharts.xml");
	 chart1.render("cpuUsage");
	 
	 var chart2 = new FusionCharts("fusionCharts/RealTimeLine.swf", "memUsageChartID", "100%", "100%", "0", "1" );
	 chart2.setXMLUrl("xml/fileServer1/memCharts.xml");
	 chart2.render("memUsage");
	 */
}

