var seconds = 0;//保存在线时间秒数
var flag = 0;//保存报警图片状态
var warningID = null;

//获取北斗时 秒数
function getBeiDouSeconds(currentTime){

	var week = currentTime.getDay();
	var hours = currentTime.getHours();
	var minutes = currentTime.getMinutes();
	var seconds = currentTime.getSeconds();
	
	return (week*86400+hours*3600+minutes*60+seconds);
}

//获取北斗时 小时数
function getBeiDouHours(currentTime){
	var week = currentTime.getDay();
	var hours = currentTime.getHours();
	return ((week * 24) + hours);
}

//定义获取北斗时的函数
function getBeiDouTime(myDate){
	//获取北斗时 周数
	var year = myDate.getFullYear();
	var month = myDate.getMonth()+1;
	var day = myDate.getDate();
	
	day--;
	var yearInterval=year-2006;
	var sum = 0;
	//对月份的处理
	switch (month)           
	{   
	case 1:  
		sum=0; 
		break;  
	case 2:  
		sum=31; 
		break;  
	case 3:  
		sum=59;  
		break;  
	case 4:    
		sum=90;        
		break;    
	case 5:    
		sum=120;                                                                                                                                                  
		break;                                                                                       
	case 6:     
		sum=151;       
		break;    
	case 7:
		sum=181;     
		break;              
	case 8:            
		sum=212;          
		break;         
	case 9:           
		sum=243;                                                                                           
		break;    
	case 10:     
		sum=273;         
		break;  
	case 11:  
		sum=304;     
		break;   
	case 12:        
		sum=334;        
		break;     
	default:   
		break;    
	}  
	//对晕年2月份的处理
	sum+=day;
	if (((year%4==0)&&(year%100!=0))||(year%400==0))     
	{       
		if(month>2)                                                                                  
		{                                                                                            
			sum+=1;     
		}
	}   
	//对年份的处理
	for(var i=0;i<yearInterval;i++)
	{
		var tempCount=2006+i;
		if (((tempCount%4==0)&&(tempCount%100!=0))||(tempCount%400==0))     
		{
			sum+=366;
		}
		else
		{
			sum+=365;
		}
	}
	year = month = day = null;
	return parseInt(sum/7);
}

//根据服务器返回的时间解析时间字符串
function createServerTime(){
	var curTimeStr = $("#curTime").text();
	var tempArr = curTimeStr.split(" ");
	if((tempArr instanceof Array)&&(tempArr.length == 2)){
		var dateArr = tempArr[0].split("-");
		var timeArr = tempArr[1].split(":");
		if(!(dateArr instanceof Array) || !(timeArr instanceof Array) || (dateArr.length != 3) || (timeArr.length != 3)){
			return null;
		}else{
			var myDate = new Date(dateArr[0],dateArr[1]-1,dateArr[2],timeArr[0],timeArr[1],timeArr[2]);
			return myDate;
		}
	}else{
		return null;
	}
	
}

//对时，分，秒数据进行处理，若小于10在前面补0
function formatTime(data){
	var result = "";
	if(data<10){
		result = "0" + data;
	}else{
		result = data;
	}
	return result;
}

//在页面上显示北斗时，隔一秒钟更新一次
function displayBeiDou(){
	var myDate = createServerTime();
	myDate.setSeconds(myDate.getSeconds()+seconds);
	var str = myDate;
	str = myDate.getFullYear() + "-" + (myDate.getMonth()+1) + "-" + myDate.getDate() + "  " + formatTime(myDate.getHours()) + ":" + formatTime(myDate.getMinutes()) + ":" + formatTime(myDate.getSeconds());
	$("#beidouTime").html("<pre>当前时间   ：  " + str + "<pre>" );
	seconds++;
	//setTimeout("displayBeiDou()",1000);+ "      北斗时：    第 " +getBeiDouTime(myDate)+" 周       第" + getBeiDouHours(myDate) +"小时        第" + getBeiDouSeconds(myDate) + "秒</pre>"
}

//定义退出系统的操作
function logout(){
	$.ajax({
		   type: "POST",
		   url: "servlet/Logout?t="+Math.random(),
		   dataType: "text",
		   success: function(data,Status) {
		 			
			},
		   	error:function(xhr, textStatus, error)
		   	{
		   		alert("请求系统监视服务器数据出错，错误信息如下：\n\nstatus="+xhr.status);	
		   	}
		});
	return false;
}

//点击左侧导航栏中的系统监控的时候打开相应的标签
function indexPageSwitch(){
	$("#systemMonitorID").click(function(){
		$(".navTab-tab > li").removeClass("selected");
		$(".main").addClass("selected");
		$(".navTab-panel > div").css("display","none");
		$("#mainBox").css("display","block");
	});
}

//隐藏或者关掉报警框后的系统监视页面样式变更
function maxSystemMonitorPic(){
	$(".serverPic_top").each(function(){
		$(this).css("top","145px");
	});
	$("#soft0").css("top","154px");
	$("#soft1").css("top","212px");
	$("#soft2").css("top","360px");
	$("#soft3").css("top","430px");
	/*$("#soft4").css("top","134px");
	$("#soft5").css("top","177px");
	$("#soft6").css("top","220px");*/
	$("#soft4").css("top","154px");
	$("#soft5").css("top","212px");
	$("#soft7").css("top","360px");
	$("#soft8").css("top","430px");
	
	$("#dbserver0").css("top","360px");
	$("#dbserver1").css("top","360px");
	$("#appserver0").css("top","360px");
	$("#appserver1").css("top","360px");
	
	$("#nasgate").css("top","380px");
	
	$("#switch0").css("top","490px");
	$("#switch1").css("top","490px");
	$("#sanpic").css("top","576px");
}

//弹出报警框后，系统监控页面向上收缩的样式变更
function minSystemMonitorPic(){
	$(".serverPic_top").each(function(){
		$(this).css("top","80px");
	});
	$("#soft0").css("top","84px");
	$("#soft1").css("top","142px");
	$("#soft2").css("top","240px");
	$("#soft3").css("top","300px");
	//$("#soft4").css("top","64px");
	//$("#soft5").css("top","107px");
	//$("#soft6").css("top","150px");
	$("#soft4").css("top","84px");
	$("#soft5").css("top","142px");
	$("#soft7").css("top","240px");
	$("#soft8").css("top","300px");
	
	$("#dbserver0").css("top","230px");
	$("#dbserver1").css("top","230px");
	$("#appserver0").css("top","230px");
	$("#appserver1").css("top","230px");
	
	$("#nasgate").css("top","246px");
	
	$("#switch0").css("top","330px");
	$("#switch1").css("top","330px");
	$("#sanpic").css("top","380px");
}
//检测是否有报警数据
/*function getWarningData(){
	$.ajax({
		   type: "GET",
		   url: "servlet/GetWarningInfo?t="+Math.random(),
		   //url:url+"?t=" +Math.random(),
		   dataType: "text",
		   cache: false,
		   success: function(dataObjs,Status) {
			   dataObjs = eval("(" + dataObjs+ ")");
			   if(dataObjs!=""||dataObjs!=null){
				   if(dataObjs.warningInternals.length > 0){
					   $("#warningPic").attr("src","Images/warnLight.gif");
				   }else{
					   $("#warningPic").attr("src","Images/bitBrown.bmp");
				   }
						
				}
			   return false;
		   },
		   error:function(xhr, textStatus, error)
		   	{
		   		//alert("请求告警数据出错，错误原因如下： \n\nstatus="+xhr.status);	
		   	}
	});
}*/
//检测当前浏览器是否安装了flash插件，若没有安装提示下载安装
function checkFlash(){
	var isIE = (navigator.appVersion.indexOf("MSIE", 0)>=0);
	//var hasFlash = true;
	var flashVer = 0;

	if(isIE){
		try{
			var objFlash = new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
			if(objFlash){
				flashVer = Number(objFlash.GetVariable('$version').split(' ')[1].replace(/\,/g,'.').replace(/^(\d+\.\d+).*$/,"$1"));
			}
		}catch(e){
			//hasFlash = false;
		}
	}else{
		var swf = navigator.plugins["Shockwave Flash"];
		if(swf){
			//hasFlash = false;
			var arr = swf.description.split(' ');
			var ver = null;
			for(var i = 0; i < arr.length; i++){
				ver = Number(arr[i]);
				if(!isNaN(ver)){
					flashVer = ver;
					break;
				}
			}
		}
	}
	return flashVer;
}

//页面加载的时候执行的脚本
(function($){
			
	//显示北斗时间
	seconds = 0;
	displayBeiDou();
	var temp0 = setInterval("displayBeiDou()",1000);
	
	/*//报警灯闪烁
	setWarningPic();
	warningID = setInterval("setWarningPic()",1000);
	clearInterval(warningID);*/	
	
	//首页样式调整
	indexPageSwitch();
	//maxSystemMonitorPic();
	
	//设置报警灯的显示状态
	//getWarningData();
	//var temp1 = setInterval("getWarningData()",5222);
	var aw = $(window).width();
	$("#realTimeWarning").attr("width",aw-8);
	//模块分权限显示
	var role = $("#roleFlag").val();
	if(role == 0){
		$("#userManage").html('<a href="ShowReport.wx?PAGEID=user" target="navTab" rel="userbaseinfo31" external="true" fresh="false" >用户信息管理</a> ');
		$("#inventoryStatics").html('<a href="excel.jsp" target="navTab" rel="ftpserverstat" external="true" fresh="false">库存统计</a>');
		}else{
		/*$("#userManage").html('<a href="javascript:void();" id="userInfo">用户基础信息维护</a>');
		$("#userInfo").click(function(){
			alert("当前用户不是高级用户，没有权限操作！");
			return false;
		});*/
		var temp0 = document.getElementById("userManage");
		if(temp0) temp0.parentNode.removeChild(temp0);
		//$("#userManage").parentNode.removeChild(this);
		var temp1 = document.getElementById("inventoryStatics");
		if(temp1) temp1.parentNode.removeChild(temp1);
		
		}
	
	//若浏览器端没装flash。提示安装
	if(checkFlash() == 0){
		$("#flashArea").css("display","block");
		$("#flashArea p a").click(function(){
			$("#flashArea").css("display","none");
		});
	}else{
		$("#flashArea").css("display","none");
	}
	
})(jQuery);