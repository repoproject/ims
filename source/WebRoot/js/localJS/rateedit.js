
//alert(11);

var currentDate = new Date();
	//formatDate(new Date(),"yyyy-MM-dd");
init();

/**
 * 初始化界面
 */
function init()
{
	initInput();
}

/**
 * 初始化界面数据,设置默认值
 */
function initInput()
{
		
	var dataObj = getEditableReportColValues('rate_edit','report1',{startDateTime:true,rate:true});

	var myDate = dataObj.startDateTime.value;
	var myRate = dataObj.rate.value;

	
	//新增的页面，需要给开始时间默认值
	if(myDate=="")
	{
		var d=new Date();
		myDate= d.getFullYear()+'-'+(d.getMonth()+1)+'-'+d.getDate();
	}
	//新增的页面，需要给汇率默认值
	if(myRate=="")
	{
		myRate="1";
	}
	//给默认值
	setEditableReportColValue('rate_edit','report1',{startDateTime:myDate,rate:myRate});
}

function testTypePromptCallBack1(textObj,colvaluesArr)
{
	 alert("aaaa1");
	 if(textObj==null) 
		 return; 
     var name = colvaluesArr[0].value; 
     var no = colvaluesArr[1].value; 
     alert("aaa2a");
     setEditableReportColValue('rate_edit','report1',{foreignMoneyName:name});
     alert("aaaa3");
}

