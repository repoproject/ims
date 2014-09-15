
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
		
	var dataObj = getEditableReportColValues('in_add','report1',{indate:true,num:true});
	

	var myDate = dataObj.indate.value;
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
	setEditableReportColValue('in_add','report1',{indate:myDate,num:num1});
}

function testTypePromptCallBack1(textObj,colvaluesArr)
{
	 //alert("aaaa");
	 if(textObj==null) 
		 return; 
     var name = colvaluesArr[0].value; 
     var no = colvaluesArr[1].value; 
     var seq = colvaluesArr[2].value; 
     setEditableReportColValue('in_add','report1',{catno:no,catname:name,seq:seq});
}

