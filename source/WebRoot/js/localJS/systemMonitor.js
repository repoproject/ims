var serverStateCnt = 0;//缓存服务器的数据计数

/*根据服务器返回的状态，获取系统监视页面中各种服务器和软件的显示样式
 * preNAme：服务器的名称
 * stat：服务器返回的数据状态，0：正常，1：故障，2：离线
 * */
function getPicUrl(preName,stat){
	var picUrl = "";
	switch(stat){
	case 0:
		picUrl = "Images/" + preName + ".W.gif";
		break;
	case 1:
		picUrl = "Images/" + preName + ".E.gif";
		break;
	case 2:
		picUrl = "Images/" + preName + ".O.gif";
		break;
	default:
		picUrl = "Images/" + preName + ".gif";
		break;
	}
	return picUrl;
}

function setServerPicUrl(message){
	if((typeof message) == "object" && message != null){
		 
		 $("#server0").attr("src",getPicUrl('Server',message.fileServer1NetStatus));
		 $("#server1").attr("src",getPicUrl('Server',message.fileServer2NetStatus));
		 $("#server2").attr("src",getPicUrl('Server',message.webServer1NetStatus));
		 $("#server3").attr("src",getPicUrl('Server',message.webServer2NetStatus));
		 $("#server4").attr("src",getPicUrl('Server',message.cmsServer1NetStatus));
		 
		 $("#dbserver0").attr("src",getPicUrl('DBServer',message.dbServer1NetStatus));
		 $("#dbserver1").attr("src",getPicUrl('DBServer',message.dbServer2NetStatus));
		 
		 $("#appserver0").attr("src",getPicUrl('Server',message.appServer1NetStatus));
		 $("#appserver1").attr("src",getPicUrl('Server',message.appServer2NetStatus));
		 
		 $("#soft0").attr("src",getPicUrl('Software',message.fileServer1SoftStatus));
		 $("#soft1").attr("src",getPicUrl('Software',message.fileServer2SoftStatus));
		 $("#soft2").attr("src",getPicUrl('Software',message.dbServer1SoftStatus));
		 $("#soft3").attr("src",getPicUrl('Software',message.dbServer2SoftStatus));
		 
		 $("#soft4").attr("src",getPicUrl('Software',message.webServer1SoftStatus));
		 $("#soft5").attr("src",getPicUrl('Software',message.webServer2SoftStatus));
		 $("#soft6").attr("src",getPicUrl('Software',message.cmsServer1SoftStatus));
		 $("#soft7").attr("src",getPicUrl('Software',message.appServer1SoftStatus));
		 $("#soft8").attr("src",getPicUrl('Software',message.appServer2SoftStatus));
		 
	}
}
function getStateData(){
	 $.ajax({
		   type: "GET",
		   url: "servlet/GetServerState?t="+Math.random(),
		   dataType: "json",
		   success: function(data,Status) {
		 	//若服务器返回的计数和页面缓存的数据不同，则说明是新接收到的数据，更新页面；否则就是服务器缓存的数据，不更新页面
		 		if(data.serverStateCnt != undefined && serverStateCnt != data.serverStateCnt){
		 			setServerPicUrl(data);
		 			serverStateCnt = data.serverStateCnt;
		 		}
			},
		   	error:function(xhr, textStatus, error)
		   	{
		   		//alert("请求系统监视服务器数据出错，错误信息如下：\n\nstatus="+xhr.status);	
		   	}
		});
	
	
}
$(document).ready(function(){
	getStateData();
	setInterval("getStateData();",1000);
});	