
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
		
	var dataObj = getEditableReportColValues('out_add','report1',{outdate:true,num:true});
	

	var myDate = dataObj.outdate.value; 
	var num1 = dataObj.num.value;
	
	//新增的页面，需要给入库时间默认值
	if(myDate=="")
	{
		var d=new Date();
		myDate= d.getFullYear()+'-'+(d.getMonth()+1)+'-'+d.getDate();
	}
	//新增的页面，需要给入库数量默认值
	if(num1=="")
	{
		num1="0";
	}
	//给入库时间和数量默认值
	setEditableReportColValue('out_add','report1',{outdate:myDate,num:num1});
}
/***
 * 回调函数，获取设备名称和序号
 * @param textObj
 * @param colvaluesArr
 * @return
 */
function testOutPromptCallBack1(textObj,colvaluesArr)
{
	 if(textObj==null) 
		 return; 
     var name = colvaluesArr[0].value; 
     var no = colvaluesArr[1].value; 
     setEditableReportColValue('out_add','report1',{machineno:no,machinename:name});
}

