<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String username = "";
	username = (session.getAttribute("username")).toString();
 %>
<div class="pageContent">
	
	<form method="post" action="servlet/ModifyPassword" class="pageForm required-validate" onsubmit="return validateCallback(this, changePWDCallBack)">
		<div class="pageFormContent" layoutH="58">
		<input type="hidden" name="username" value="<%=username %>"/>
			<div class="unit">
				<label>旧密码：</label>
				<input type="password" name="oldPassword" size="30" minlength="4" maxlength="20" class="required" />
			</div>
			<div class="unit">
				<label>新密码：</label>
				<input type="password" id="cp_newPassword" name="newPassword" customvalid="customvalidPwd(element)" size="30" minlength="4" maxlength="20" class="required alphanumeric"/>
			</div>
			<div class="unit">
				<label>重复输入新密码：</label>
				<input type="password" name="rnewPassword" size="30"  minlength="4" maxlength="20"  equalTo="#cp_newPassword" class="required alphanumeric"/>
			</div>
			
		</div>
		<div class="formBar">
			<ul>
				<li><div class="buttonActive"><div class="buttonContent"><button type="submit">提交</button></div></div></li>
				<li><div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div></li>
			</ul>
		</div>
	</form>
	
</div>

<script type="text/javascript">
function customvalidPwd(element){
	if ($(element).val() == "111111") return false;
	return true;
}
</script>