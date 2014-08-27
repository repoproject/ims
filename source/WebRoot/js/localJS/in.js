
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
	
	setEditableReportColValue('in_edit','report1',{indate:"2014-01-01",num:"10"});
	//setReportInputBoxValue('in_edit','report1',false,{indate:'2013-01-01',num:'10'});
	//alert(11);
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

