
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