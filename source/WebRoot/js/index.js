//获取北斗时 秒数
function getBeiDouSeconds(currentTime){

	var week = currentTime.getDay();
	var hours = currentTime.getHours();
	var minutes = currentTime.getMinutes();
	var seconds = currentTime.getSeconds();
	
	return (week*86400+hours*3600+minutes*60+seconds);
	
	
}

//获取北斗时 小时数
function getBeiDouHour(currentTime){
	var week = currentTime.getDay();
	var hours = currentTime.getHours();
	return ((week * 24) + hours);
}

function getBeiDouTime(myDate){
	//获取北斗时 周数
	var year = myDate.getFullYear();
	var month = myDate.getMonth()+1;
	var day = myDate.getDate();
	
	day--;
	var yearInterval=year-2006;
	var sum = 0;
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
	
	sum+=day;
	if (((year%4==0)&&(year%100!=0))||(year%400==0))     
	{       
		if(month>2)                                                                                  
		{                                                                                            
			sum+=1;     
		}
	}   
	
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
	
	return parseInt(sum/7);

	//alert(year + "/" +month+"/"+day);
}

function displayBeiDou(){
	var timeTemp = new Date();
	
	//更新页面北斗时
	$("#beidouTime").html("当前时间：" + timeTemp + "北斗时，第" +getBeiDouTime(timeTemp)+" 周      第" + getBeiDouHours(timeTemp) +"时     第" + getBeiDouSeconds(timeTemp)) + "秒";
	setTimeout("displayBeiDou()",1000);
}

//设置报警灯的闪烁
function setWarningPic(){
	if(flag == 0){
		$("#warningPic").attr("src","Images/bitBrown.bmp");
		flag = 1;
	}else{
		$("#warningPic").attr("src","Images/bitRed.bmp");
		flag = 0;
	}
	setInterval("setWarningPic()",2000);
}
$(function(){
			
			//显示北斗时间
			displayBeiDou();
			//报警灯闪烁
			setWarningPic();
});