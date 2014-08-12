<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户管理</title>
<script type="text/javascript">
function adjustheight()
{
	var ifrm = document.getElementById("frm");
	var subWeb = document.frames ? document.frames["frm"].document:ifrm.contentDocument;
	if(ifrm!=null && subWeb!=null)
	{
		ifrm.height = subWeb.body.scrollHeight;
	}
}
</script>
</head>
<body style="background-image:url(webresources/skin/vista/images/backgroud.bmp)">
<iframe width="100%" height="100%" frameborder="0" id="frm" name="frm" scrolling="no" onload="adjustheight()" src="/zxyx/ShowReport.wx?PAGEID=yhglpage"></iframe>
</body>
</html>