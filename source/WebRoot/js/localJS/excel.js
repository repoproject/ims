var contextPath;
$(function(){
	$('#report').click(function(){
		report();
	});
});

/**
 * 
 * @return
 */
function report(){
	var basepath= document.location.pathname.substring(document.location.pathname.indexOf('/')+1) ;

	basepath= basepath.substring(0,basepath.indexOf('/')) ;
	var url = "servlet/ExcelExport.htm";
	$.post(url,function(result){
		jsonObj = eval("(" + result + ")");
		if(jsonObj.result == "success"){
			alert("生成报表成功请去报表下载页面下载");
		}
	});
}