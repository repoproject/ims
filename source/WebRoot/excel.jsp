<%@ page language="java" import="java.util.*,java.text.*" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title>Insert title here</title>
<link href="themes/default/style.css" rel="stylesheet" type="text/css" media="screen"/>
<link href="css/core.css" rel="stylesheet" type="text/css" media="screen"/>
<script src="js/jquery-1.7.2.js" type="text/javascript"></script>
<script src="js/localJS/excel.js" type="text/javascript"></script>
<script>
    contextPath = <%=request.getContextPath() %>;
</script>
</head>
<body>
    <form method="post">
    <div>
    <br>
    <br>
    <br>
    </div>
    <div class="panelBar">
            <div class="pages">
                   <input id="report" class="button" type="button" name="excel" value="生成报表" >
            </div>
        </div>
    <div class="pageContent">
	 <table class="list" width="100%" border="1">
		 <thead>
			 <tr>
				 <th width="100" align="center">日期</th>
				 <th width="400" align="left">文件名称</th>
			 </tr>
		 </thead>
	 	<tbody>
	            <tr>
	                <td>2014-10-26</td>
	                <td align="left"><a href="./reports/Inventory listing 2014-10.xls">Inventory listing 2014-10.xls</a></td>
	            </tr>
	            <tr>
	                <td>2014-09-26</td>
	                <td align="left"><a href="./reports/Inventory listing 2014-09.xls">Inventory listing 2014-09.xls</a></td>
	            </tr>
            </tbody>
        </table>
        </div>
      
    </form>
</body>
</html>