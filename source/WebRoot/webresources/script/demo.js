/**
 * 测试行选中执行的回调函数
 */
function testcallbacufunc(pageid,reportid,selectedTrObjArr,deselectedTrObjArr)
{
	var str=getTrObjsValue(deselectedTrObjArr);
	if(str!=null&&str!='')
	{
		alert('本次被取消选中的行：'+str);
	}
	str=getTrObjsValue(selectedTrObjArr);
	if(str!=null&&str!='')
	{
		alert('本次选中的行：'+str);
	}
	var allSelectedTrObjsArr=getListReportSelectedTrObjs(pageid,reportid);//取到所有选中的行对象
	str=getTrObjsValue(allSelectedTrObjsArr);
	if(str!=null&&str!='')
	{
		alert('被选中的所有行：'+str);
	}
}

function getTrObjsValue(trObjsArr)
{
	if(trObjsArr==null||trObjsArr.length==0) return '';
	var str='';
	for(var i=0;i<trObjsArr.length;i++)
	{
		var tdChilds=trObjsArr[i].getElementsByTagName('TD');
		for(var j=0;j<tdChilds.length;j++)
		{
			var name=tdChilds[j].getAttribute('name');//获取当前列对应的<col/>的列名
			var value=tdChilds[j].getAttribute('value');//获取选中行的当前列的数据
			if(name&&name!='')
			{
				str=str+'[列名：'+name+'；列值：'+value+']';
			}
		}
	}
	return str;
}

function testTypePromptCallBack(textObj,colvaluesArr)
{
	if(textObj==null) return;
	var selectValus='';
	for(var i=0;i<colvaluesArr.length;i++)
	{
		selectValus+='[label:'+colvaluesArr[i].label+';value:'+colvaluesArr[i].value+']';
	}
	wx_alert('选中选项各列的值：'+selectValus);
	//执行此报表的搜索操作
	searchReportData('typepromptpage4','report1');
	/**
	 * 如果不想象上面一样写死要搜索的报表所在页面ID和报表本身ID（即这里的typepromptpage4和report1），可以从textObj.id中取到这两个值，因为框架为输入框分配的id中自动包括它所在的报表ID和报表所在的页面ID。
	 * 这对想写一个统一的功能非常有用，不需每个报表去写一个这种方法进行自动搜索。
	 */
}

function otherdata_clientpage1_beforesave(pageid,reportid,dataObjArr)
{
	if(dataObjArr!=null)
	{
		var mess='';
		var dataObjTmp;
		for(var i=0;i<dataObjArr.length;i++)
		{//遍历每条本次操作的记录
			dataObjTmp=dataObjArr[i];
			var myupdatetype=dataObjTmp['WX_TYPE'];//本条记录的更新类型，包括add、update、delete三种取值
			//alert(myupdatetype);
			if(myupdatetype=='delete')
			{//对本记录做删除操作
				mess=mess+'删除的部门编号：'+dataObjTmp['deptno__old'];
				mess=mess+';;;删除的部门名：'+dataObjTmp['deptname__old'];
			}else if(myupdatetype=='update')
			{//对本记录做修改操作
				mess=mess+'修改的旧部门编号：'+dataObjTmp['deptno__old']+';;;新部门编号：'+dataObjTmp['deptno'];
				mess=mess+';;;修改的旧部门名：'+dataObjTmp['deptname__old']+';;;新部门名：'+dataObjTmp['deptname'];
			}else if(myupdatetype=='add')
			{//对本记录做添加操作
				mess=mess+'添加的部门编号：'+dataObjTmp['deptno'];
				mess=mess+';;;添加的部门名：'+dataObjTmp['deptname'];
			}
		}
		//调用框架添加自定义数据的方法传入后台保存时使用
		var now=new Date();
		var yy=now.getYear();
		if(yy<1000) yy=yy+1900;
      var currentdate=yy+'-'+(now.getMonth()+1)+'-'+(now.getDay()+2)+' 00:00:00.0';
		setCustomizeParamValue(pageid,reportid,'testdate',currentdate);
		setCustomizeParamValue(pageid,reportid,'logcontent',mess);
	}	
}

function refreshreportapipage1Onload(pageid,componentid)
{
	if(pageid!='refreshreportapipage1') return;
	if(componentid==null||componentid=='refreshreportapipage1')
	{
		alert('加载完整个页面');
	}else if(componentid=='report1')
	{
		alert('加载完报表一');
	}else if(componentid=='report2')
	{
		alert('加载完报表二');
	}
}

function testGetReportColData(pageid,reportid,reporttype,columns,conditions)
{
	var datasObj=getEditableReportColValues(pageid,reportid,reporttype,columns,conditions);
	if(datasObj==null)
	{
		wx_alert('没有获取到数据');
	}else if(isArray(datasObj))
	{
		var dataObjTmp;
		for(var i=0;i<datasObj.length;i=i+1)
		{
			dataObjTmp=datasObj[i];
			var str='';
			for(var key in dataObjTmp)
			{
				str=str+' name:'+dataObjTmp[key].name+';value:'+dataObjTmp[key].value+';oldname:'+dataObjTmp[key].oldname+';oldvalue:'+dataObjTmp[key].oldvalue+'\n';
			}
			alert(str);
		}
	}else
	{
		var str='';
		for(var key in datasObj)
		{
			str=str+' name:'+datasObj[key].name+';value:'+datasObj[key].value+';oldname:'+datasObj[key].oldname+';oldvalue:'+datasObj[key].oldvalue+'\n';
		}
		alert(str);
	}
}

function resetPWDInvokeCallbackMethod(datasObj)
{
	if(datasObj.returnValue!=null&&datasObj.returnValue!='')
	{
		alert(datasObj.returnValue);
	}

	refreshComponentDisplay(datasObj.pageid,null,true);//刷新整个页面
}

function testInvokeCallbackMethod2(xmlHttpObj,datasObj)
{
	alert(xmlHttpObj.responseText);//打印服务器端方法返回的字符串
	if(datasObj==null) return;
	var datasObjArr=convertToArray(datasObj);
	printInvokeParamsData(datasObjArr);
	refreshComponentDisplay('invokeservermethod_alonepage1',null,true);
	//window.location.href='/WabacusDemo/ShowReport.wx?PAGEID=invokeservermethod_alonepage1';//刷新一下此页面，将新插入的记录显示出来
}

function printInvokeParamsData(datasObjArr)
{
	if(datasObjArr==null||datasObjArr.length==0) return;
	for(var i=0;i<datasObjArr.length;i++)
	{
		var str='';
		for(key in datasObjArr[i])
		{
			str=str+'参数名：'+key+'，参数值：'+datasObjArr[i][key]+'；';
		}
		if(str!='') alert(str);
	}
}

function testInvokeCallbackMethod3(datasObj)
{
	refreshComponentDisplay('invokeservermethod_sqlpage1',null,true);//刷新整个页面
}

function testInvokeCallbackMethod_autodbpage11(datasObj)
{
	alert('针对组件'+datasObj.componentid+'调用服务器端操作');
	if(datasObj.returnValue!=null&&datasObj.returnValue!='')
	{
		alert('服务器端返回值：'+datasObj.returnValue);
	}
	var componentguid=getComponentGuidById(datasObj.pageid, datasObj.componentid);//先取到组件GUID
	var datasArr=null;
	if(WX_ALL_SAVEING_DATA!=null) datasArr =WX_ALL_SAVEING_DATA[componentguid];
	if(datasArr!=null&&datasArr.length>0)
	{
		printInvokeParamsData(datasArr);
	}else
	{
		alert('无参数');
	}
	refreshComponentDisplay(datasObj.pageid,'vp1',true);//刷新vp1容器
}
function testInvokeCallbackMethod_autodbpage12(datasObj)
{
	alert('针对组件'+datasObj.componentid+'调用服务器端操作');
	if(datasObj.returnValue!=null&&datasObj.returnValue!='')
	{
		alert('服务器端返回值：'+datasObj.returnValue);
	}
	var componentguid=getComponentGuidById(datasObj.pageid, datasObj.componentid);//先取到组件GUID
	var datasArr=null;
	if(WX_ALL_SAVEING_DATA!=null) datasArr =WX_ALL_SAVEING_DATA[componentguid];
	if(datasArr!=null&&datasArr.length>0)
	{
		printInvokeParamsData(datasArr);
	}else
	{
		alert('无参数');
	}
	refreshComponentDisplay(datasObj.pageid,'vp2',true);//刷新vp2容器
}

function testInvokeCallbackMethod_autodbpage13(datasObj)
{
	alert('针对组件'+datasObj.componentid+'调用服务器端操作');
	if(datasObj.returnValue!=null&&datasObj.returnValue!='')
	{
		alert('服务器端返回值：'+datasObj.returnValue);
	}
	var componentguid=getComponentGuidById(datasObj.pageid, datasObj.componentid);//先取到组件GUID
	var datasArr=null;
	if(WX_ALL_SAVEING_DATA!=null) datasArr =WX_ALL_SAVEING_DATA[componentguid];
	if(datasArr!=null&&datasArr.length>0)
	{
		printInvokeParamsData(datasArr);
	}else
	{
		alert('无参数');
	}
	refreshComponentDisplay(datasObj.pageid,'vp3',true);//刷新vp3容器
}

function myCancelButtonEvent()
{
	alert('点击了\'取消\'按钮，执行取消事件');
}

function addCustomizeDataToButton(datasArr,paramsObj)
{
	if(datasArr==null||datasArr.length==0) return false;//没有传入报表数据，则中止调用
	var nameStrs='';
	for(var i=0;i<datasArr.length;i++)
	{
		var datasObj=datasArr[i];//取到存放当前记录数据的对象
		nameStrs+=datasObj['name'];//取到姓名列的值
	}
	paramsObj['logcontent']='更新姓名为'+nameStrs+'的记录的性别为女';
	paramsObj['updatetype']='设置性别';
	return true;
}

function setValidateErrorMessage(paramsObj)
{
	if(paramsObj.isSuccess===true)
	{
		alert('校验成功');
	}else
	{
		alert('校验失败');
	}
	if(paramsObj.serverDataObj==null||paramsObj.serverDataObj.errormessage==null||paramsObj.serverDataObj.errormessage=='')
	{//服务器端没有返回要显示的数据
		if(paramsObj.validatetype=='onblur')
		{
			document.getElementById('deptnomessid').innerHTML='';
		}
	}else
	{
		if(paramsObj.validatetype=='onblur')
		{
			document.getElementById('deptnomessid').innerHTML="<font color='red' size='2'>"+paramsObj.serverDataObj.errormessage+"</font>";
		}else
		{
			wx_warn(paramsObj.serverDataObj.errormessage);
		}
	}
}
function testConfigLinkedChart(paramsObj)
{
	FusionCharts(paramsObj.chartid).configureLink(
		{
			swfUrl:paramsObj.customizeData.swfUrl,    
			"renderAt":paramsObj.customizeData.targetId,    
			overlayButton: { show : false }
		}
	);
}