
function testTypePromptCallBack1(textObj,colvaluesArr)
{
	 //alert("aaaa");
	 if(textObj==null) 
		 return; 
     var name = colvaluesArr[0].value; 
     var no = colvaluesArr[1].value; 
     setEditableReportColValue('in_edit','report1',{catno:no,catname:name});
}

