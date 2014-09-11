/***
 * 回调函数，获取设备名称和序号
 * @param textObj
 * @param colvaluesArr
 * @return
 */
function testOutMachineCallBack1(textObj,colvaluesArr)
{
	 if(textObj==null) 
		 return; 
     var name = colvaluesArr[0].value; 
     var no = colvaluesArr[1].value; 
     setEditableReportColValue('out_edit','report1',{machineno:no,machinename:name});
}