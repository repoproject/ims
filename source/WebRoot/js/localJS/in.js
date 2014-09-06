
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
	//alert(22);
	var d = new Date();
	//获取当前日期 YYYU-MM-DD
	var today=  getEditableReportColValues('in_edit','report1',{indate:true});
	if(today=null)
	{
		today=d.getFullYear()+'-'+(d.getMonth()+1)+'-'+d.getDate();
	}		
	
	setEditableReportColValue('in_edit','report1',{indate:today,num:"10"});
	//setReportInputBoxValue('in_edit','report1',false,{indate:'2013-01-01',num:'10'});
	//alert(11);
}

function test1()
{
	alert("1111");
	
	
	}


function testTypePromptCallBack1(textObj,colvaluesArr)
{
	 //alert("aaaa");
	 if(textObj==null) 
		 return; 
     var no = colvaluesArr[0].value; 
     var name = colvaluesArr[1].value; 
     setEditableReportColValue('in_edit','report1',{catno:no,catname:name});
}

