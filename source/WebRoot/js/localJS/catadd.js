
//alert(11);



function testTypePromptCallBack1(textObj,colvaluesArr)
{
	 //alert("aaaa");
	 if(textObj==null) 
		 return; 
     var name = colvaluesArr[0].value; 
     var no = colvaluesArr[1].value; 
     var seq = colvaluesArr[2].value; 
     setEditableReportColValue('cat_addedit','report1',{catno:no,catname:name,seq:seq});
}

