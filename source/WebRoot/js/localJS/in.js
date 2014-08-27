
alert(11);

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

function testTypePromptCallBack1()
{
	 if(textObj==null) return; 

     var aaa = textObj.typePromptObj.paramsObj; 
     //动态取出需要赋值的col中column对应的名称，即yxmc 
     var col = aaa.colsArray[0].collabel;       
     //动态取出选中下拉框中yxmc的值，即sql语句中的yxmc 
     var vlabel = colvaluesArr[0].label; 

     //动态取出需要赋值的col中column对应的名称，即department 
     var colhidden = aaa.colsArray[0].colvalue; 
     //动态取出选中下拉框中department的值，即sql语句中的department 
     var vvalue = colvaluesArr[0].value; 
     
     setEditableReportColValue(aaa.pageid,aaa.reportid,{colhidden:'"+vvalue+"',col:'"+vlabel+"'}); 
}