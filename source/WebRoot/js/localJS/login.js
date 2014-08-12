function loginSubmit(form, callback){
		var $form = $(form);
		var pwd = form.elements[1].value;
		pwd = hex_md5(pwd);
		var formData = $form.serializeArray();
		formData[1].value = pwd;
		$.ajax({
			type: form.method || 'POST',
			url:$form.attr("action"),
			data:formData,
			dataType:"json",
			cache: false,
			success: callback ,
			error: function(){
				alert("服务器返回值出错！");
			}
		});
	return false;
}
function loginCallBack(json){
	if(json.result == 1){
		location.href = "index.jsp";
	}else if(json.result == 2){
		alert("输入的用户名不存在，请重新输入！");
		return false;
	}else if(json.result == 3){
		alert("输入的密码错误，请重新输入！");
		return false;
	}
}
/*$(document).ready(function(){
	$("#username").blur(function(){
		var username = $(this).val();
		username = hex_md5(username);
		$(this).val(username);
	});
});*/