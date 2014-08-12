//绑定短信告警配置开关
var timeopen = 0;
var openFlag = 0;
//根据用户的配置设定，实时地跟新页面元素的显示
function setFormStyle(){
	initVoiceWarning();
	
	//绑定相同告警发送间隔设置开关
	/*alert("open" + $(".settimeopen").value);
	if($(".settimeopen").value == 0){
		$("#seconds").addClass("disableselect");
		$("#times").attr("disabled","disabled");
		timeopen = 0;
	}else{
		$("#seconds").removeClass("disableselect");
		$("#times").removeAttr("disabled");
		timeopen = 1;
	}*/
	$(".isopen").click(function(){
		if(this.value == 0){
			$("#outer label").addClass("disableselect");
			$("#outer input").attr("disabled","disabled");
			openFlag = 0;
		}else{
			openFlag = 1;
			if(timeopen == 0){
				$("#outer .level2 label").removeClass("disableselect");
				$("#outer .level2 input").removeAttr("disabled");
				$("#outer .level3 label:eq(0)").removeClass("disableselect");
				$("#outer .level3 input:eq(0)").removeAttr("disabled");
				$("#outer .level3 label:eq(1)").removeClass("disableselect");
				$("#outer .level3 input:eq(1)").removeAttr("disabled");
			}else{
				$("#outer label").removeClass("disableselect");
				$("#outer input").removeAttr("disabled");
			}
		}
	});
	
	//绑定相同告警发送间隔设置开关
	$(".settimeopen").click(function(){
		if(this.value == 0){
			$("#seconds").addClass("disableselect");
			$("#times").attr("disabled","disabled");
			timeopen = 0;
		}else{
			$("#seconds").removeClass("disableselect");
			$("#times").removeAttr("disabled");
			timeopen = 1;
		}
	});
	
	//时间值测定，小于0时提示
	$("#times").blur(function(){
		var timeV = $(this).val();
		if(timeV <= 0){
			alert("输入的时间值应大于0，请重新输入！");
			$(this).val("");
		}else if(timeV > 3600){
			alert("输入的时间值应小于等于3600，请重新输入！");
			$(this).val("");
		}
	})
	
	//重置按钮处理函数
	$('input[name="reset"]').click(function(){
		initVoiceWarning();
	});
}
//点击保存后的响应函数
function customSubmit(form,callback){
	var $form = $(form);
	//var temp = form.elements[7];
	var value = $("#times").val();
	//alert(value + "---" + openFlag + "====" + timeopen);
	if(openFlag == 1 && timeopen == 1){
		if(value <=0){
			alert("时间数据不符合要求，应大于0，请重新输入！");
			return false;
		}else if(value > 3600){
			alert("输入的时间值应小于等于3600，请重新输入！");
			return false;
		}
	}
	$.ajax({
		type: form.method || 'POST',
		url:$form.attr("action"),
		data:$form.serializeArray(),
		dataType:"json",
		cache: false,
		success: callback || defaultCallBack,
		error: function(){
			alert("提交失败！");
			return false;
		}
	});
	$form = null;
	return false;
}

//请求服务器后的回调函数
function callback(json){
	if(json.result == 1){
		alert("数据设置成功！");
		return false;
	}else{
		alert("数据设置失败，数据不符要求!");
		return false;
	}
	
}
//根据从服务器返回的参数值跟新告警配置页面
function initVoiceWarning(){
	var url = "";
	url = "servlet/GetMsgWarningConfig";
	$.ajax({
		   type: "GET",
		   url: url + "?t="+Math.random(),
		   //url:url+"?t=" +Math.random(),
		   dataType: "text",
		   success: function(dataObjs,Status) {
			   dataObjs = eval("(" + dataObjs+ ")");
			   if(dataObjs!=""||dataObjs!=null){
				   /*warnData = dataObjs.warningInternals;*/
				   if(dataObjs.warnSwitch == 0){
						$("#outer label").addClass("disableselect");
						$("#outer input").attr("disabled","true");
						$("#voiceClose").attr("checked","checked");
						if(dataObjs.intervalSwitch == 0){
							timeopen = 0;
							$("#timerClose").attr("checked","checked");
						}else{
							timeopen = 1;
							$("#timerOpen").attr("checked","checked");
						}
						openFlag = 0;
					}else{
						openFlag = 1;
						$("#voiceOpen").attr("checked","checked");
						$("#outer label").removeClass("disableselect");
						$("#outer input").removeAttr("disabled","true");
						if(dataObjs.intervalSwitch == 0){
							$("#times").attr("disabled","disabled");
							$("#seconds").addClass("disableselect");
							$("#timerClose").attr("checked","checked");
							timeopen = 0;
						}else{
							$("#seconds").removeClass("disableselect");
							$("#times").removeAttr("disabled");
							$("#timerOpen").attr("checked","checked");
							timeopen = 1;
						}
					}
				   if(dataObjs.serious == 0){
						$("#serious").removeAttr("checked");
					}else{
						$("#serious").attr("checked","checked");
					}
					if(dataObjs.important == 0){
						$("#important").removeAttr("checked");
					}else{
						$("#important").attr("checked","checked");
					}
					if(dataObjs.general == 0){
						$("#general").removeAttr("checked");
					}else{
						$("#general").attr("checked","checked");
					}
					$("#times").val(dataObjs.time);
						
				}
		   },
		   error:function(xhr, textStatus, error)
		   	{
		   		alert("请求服务器数据出错，请重新刷新页面，错误原因如下： \n\nstatus="+xhr.status);	
		   	}
	});
	$("#times").focus();
}