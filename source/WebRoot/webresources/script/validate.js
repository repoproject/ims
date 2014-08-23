Array.prototype.max = function()
{
	var i, max = this[0];
	
	for( i = 1; i < this.length; i++ )
	{
		if( max < this[i] )
		{ 
			max = this[i];
		}
	}
	return max;
};

String.prototype.trim = function()
{
    return this.replace( /(^\s*)|(\s*$)/g, "" );
};

function isAlphaNumeric( strValue,boxObj,paramsObj)
{
	return checkExp( /^\w*$/gi, strValue );
}

function isDate( strValue,boxObj,paramsObj )
{
	if( isEmpty( strValue ) ) {return true;}

	if( !checkExp( /^\d{4}-[01]?\d-[0-3]?\d$/, strValue ) ) 
	{
		return false;
	}
	
	var arr = strValue.split( "-" );
	var year = arr[0];
	var month = arr[1];
	var day = arr[2];
	
	if(year<1900||year>2060)
	{
		return false;
	}

	if( !( ( 1<= month ) && ( 12 >= month ) && ( 31 >= day ) && ( 1 <= day ) ) )
	{
		return false;
	}
		
	if( !( ( year % 4 ) == 0 ) && ( month == 2) && ( day == 29 ) )
	{
		return false;
	}
	
	if( ( month <= 7 ) && ( ( month % 2 ) == 0 ) && ( day >= 31 ) )
	{
		return false;
	}
	
	if( ( month >= 8) && ( ( month % 2 ) == 1) && ( day >= 31 ) )
	{
		return false;
	}
	
	if( ( month == 2) && ( day >=30 ) )
	{
		return false;
	}
	
	return true;
}

function isShortDate( strValue,boxObj,paramsObj )
{
	var DATETIME = strValue;
	if( isEmpty( strValue ) ) return true;
	if( !checkExp(/^\d{4}-[01]?\d/g,DATETIME) )
	{
		return false;
	}

	var arr = DATETIME.split( "-" );
	var year = arr[0];
	var month = arr[1];
	if(year<1753)
	{
		return false;
	}

	if(arr.length==3)
	{
	   return false;
	}
	if( !((1<= month ) && ( 12 >= month )))
	{
		return false;				
	}
	
	return true;
}

function isEmail( strValue,boxObj ,paramsObj)
{
	if( isEmpty( strValue ) ) return true;
	
	var pattern = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/;
	return checkExp( pattern, strValue );
	
}

function isNumeric( strValue,boxObj,paramsObj )
{
	if( isEmpty( strValue ) ) return true;
	if( !checkExp( /^[+-]?\d+(\.\d+)?$/g, strValue ))
	{
		return false;
	}
	return true;
}

function isMoney( strValue,boxObj,paramsObj )
{
	if( isEmpty( strValue ) ) return true;
	
	return checkExp( /^[+-]?\d+(,\d{3})*(\.\d+)?$/g, strValue );
}

function isPhone( strValue,boxObj )
{
	if( isEmpty( strValue ) ) return true;
	
	return checkExp( /(^\(\d{3,5}\)\d{6,8}(-\d{2,8})?$)|(^\d+-\d+$)|(^(130|131|135|136|137|138|139)\d{8}$)/g, strValue );
}

function isPostalCode( strValue,boxObj,paramsObj )
{
	if( isEmpty( strValue ) ) return true;
	if(!checkExp( /(^$)|(^\d{6}$)/gi, strValue ))
	{
		return false;
	}
	return true;
}

function isURL( strValue,boxObj ,paramsObj)
{
	if( isEmpty( strValue ) ) return true;
	
	var pattern = /^(http|https|ftp):\/\/(\w+\.)+[a-z]{2,3}(\/\w+)*(\/\w+\.\w+)*(\?\w+=\w*(&\w+=\w*)*)*/gi;
	// var pattern = /^(http|https|ftp):(\/\/|\\\\)(\w+\.)+(net|com|cn|org|cc|tv|[0-9]{1,3})((\/|\\)[~]?(\w+(\.|\,)?\w\/)*([?]\w+[=])*\w+(\&\w+[=]\w+)*)*$/gi;
	// var pattern = ((http|https|ftp):(\/\/|\\\\)((\w)+[.]){1,}(net|com|cn|org|cc|tv|[0-9]{1,3})(((\/[\~]*|\\[\~]*)(\w)+)|[.](\w)+)*(((([?](\w)+){1}[=]*))*((\w)+){1}([\&](\w)+[\=](\w)+)*)*)/gi;

	return checkExp( pattern, strValue );
	
}
function trim(strValue)
{
	if(!strValue||strValue=='') return strValue;
	while(strValue.substring(0,1)==' ')
	{
		strValue=strValue.substring(1);
	}
	if(strValue=='') return strValue;
	while(strValue.substring(strValue.length-1,strValue.length)==' ')
	{
		strValue=strValue.substring(0,strValue.length-1);
	}
	return strValue;
}

function isNotEmpty( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( !strValue||strValue == '' )
		return false;
	else
		return true;
}

function isEmpty( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( strValue == "" )
		return true;
	else
		return false;
}

/**
 * 含空格后比较15个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan15( strValue,boxObj,paramsObj )
{
	//strValue=trim(strValue);
	if( strValue.length <= 15 )
		return true;
	else
		return false;
}

/**
 * 自动去掉空格后比较15个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan15Trim( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( strValue.length <= 15 )
		return true;
	else
		return false;
}
/**
 * 含空格后比较20个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan20( strValue,boxObj,paramsObj )
{
	//strValue=trim(strValue);
	if( strValue.length <= 20 )
		return true;
	else
		return false;
}

/**
 * 自动去掉空格后比较20个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan20Trim( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( strValue.length <= 20 )
		return true;
	else
		return false;
}

/**
 * 含空格后比较40个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan40( strValue,boxObj,paramsObj )
{
	//strValue=trim(strValue);
	if( strValue.length <= 40 )
		return true;
	else
		return false;
}

/**
 * 自动去掉空格后比较40个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan40Trim( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( strValue.length <= 40 )
		return true;
	else
		return false;
}


/**
 * 含空格后比较50个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan50( strValue,boxObj,paramsObj )
{
	//strValue=trim(strValue);
	if( strValue.length <= 50 )
		return true;
	else
		return false;
}

/**
 * 自动去掉空格后比较50个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan50Trim( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( strValue.length <= 50 )
		return true;
	else
		return false;
}

/**
 * 含空格后比较100个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan100( strValue,boxObj,paramsObj )
{
	//strValue=trim(strValue);
	if( strValue.length <= 100 )
		return true;
	else
		return false;
}
/**
 * 自动去掉空格后比较100个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan100Trim( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( strValue.length <= 100 )
		return true;
	else
		return false;
}

/**
 * 含空格后比较200个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan200( strValue,boxObj,paramsObj )
{
	//strValue=trim(strValue);
	if( strValue.length <= 200 )
		return true;
	else
		return false;
}

/**
 * 自动去掉空格后比较200个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan200Trim( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( strValue.length <= 200 )
		return true;
	else
		return false;
}


/**
 * 含空格后比较256个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan256( strValue,boxObj,paramsObj )
{
	//strValue=trim(strValue);
	if( strValue.length <= 256 )
		return true;
	else
		return false;
}

/**
 * 自动去掉空格后比较256个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan256Trim( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( strValue.length <= 256 )
		return true;
	else
		return false;
}

/**
 * 含空格后比较300个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan300( strValue,boxObj,paramsObj )
{
	//strValue=trim(strValue);
	if( strValue.length <= 300 )
		return true;
	else
		return false;
}

/**
 * 自动去掉空格后比较300个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan300Trim( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( strValue.length <= 300 )
		return true;
	else
		return false;
}

/**
 * 含空格后比较500个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan500( strValue,boxObj,paramsObj )
{
	//strValue=trim(strValue);
	if( strValue.length <= 500 )
		return true;
	else
		return false;
}

/**
 * 自动去掉空格后比较500个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan500Trim( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( strValue.length <= 500 )
		return true;
	else
		return false;
}

/**
 * 含空格后比较1024个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan1024( strValue,boxObj,paramsObj )
{
	//strValue=trim(strValue);
	if( strValue.length <= 1024 )
		return true;
	else
		return false;
}

/**
 * 自动去掉空格后比较1024个字符长度
 * @param strValue
 * @param boxObj
 * @param paramsObj
 * @return
 */
function isNotLongerThan1024Trim( strValue,boxObj,paramsObj )
{
	strValue=trim(strValue);
	if( strValue.length <= 1024 )
		return true;
	else
		return false;
}

/**
 * 针对获取的文件的时间查询条件的校验
 * @param strValue 开始时间
 * @param boxObj
 * @param paramsObj结束时间的输入框对象
 * @return 开始时间晚于结束时间则返回false，否则返回true
 */
function isStartTimeLargerEnd1(strValue,boxObj,paramsObj){
	var startTime = trim(strValue);
	var endTime = trim(paramsObj.datasObj.txtend1);
	//alert(startTime + "----" + endTime);
	var strd1 = startTime.replace(/-/g,"/");
	var strd2 = endTime.replace(/-/g,"/");
	//alert(strd1 + "----" + strd2);
	var d1 = new Date(Date.parse(strd1));
	var d2 = new Date(Date.parse(strd2));
	//alert(d1 + "-----" + d2);
	if(d1 > d2){
		//alert("false");
		startTime = endTime = strd1 = strd2 = d1 = d2 = null;
		return false;
	}else{
		//alert("true");
		startTime = endTime = strd1 = strd2 = d1 = d2 = null;
		return true;
	}
}


/**
 * 针对发送的文件列表的时间查询条件的校验
 * @param strValue 开始时间
 * @param boxObj
 * @param paramsObj结束时间的输入框对象
 * @return 开始时间晚于结束时间则返回false，否则返回true
 */
function isStartTimeLargerEnd2(strValue,boxObj,paramsObj){
	var startTime = trim(strValue);
	var endTime = trim(paramsObj.datasObj.txtend3);
	//alert(startTime + "----" + endTime);
	var strd1 = startTime.replace(/-/g,"/");
	var strd2 = endTime.replace(/-/g,"/");
	//alert(strd1 + "----" + strd2);
	var d1 = new Date(Date.parse(strd1));
	var d2 = new Date(Date.parse(strd2));
	//alert(d1 + "-----" + d2);
	if(d1 > d2){
		//alert("false");
		startTime = endTime = strd1 = strd2 = d1 = d2 = null;
		return false;
	}else{
		//alert("true");
		startTime = endTime = strd1 = strd2 = d1 = d2 = null;
		return true;
	}
}

/**
 * 针对发送的文件的图表时间查询条件的校验
 * @param strValue 开始时间
 * @param boxObj
 * @param paramsObj结束时间的输入框对象
 * @return 开始时间晚于结束时间则返回false，否则返回true
 */
function isStartTimeLargerEnd2TB(strValue,boxObj,paramsObj){
	var startTime = trim(strValue);
	var endTime = trim(paramsObj.datasObj.txtend2);
	//alert(startTime + "----" + endTime);
	var strd1 = startTime.replace(/-/g,"/");
	var strd2 = endTime.replace(/-/g,"/");
	//alert(strd1 + "----" + strd2);
	var d1 = new Date(Date.parse(strd1));
	var d2 = new Date(Date.parse(strd2));
	//alert(d1 + "-----" + d2);
	if(d1 > d2){
		//alert("false");
		return false;
	}else{
		//alert("true");
		return true;
	}
}


/**
 * 针对生成的文件列表的时间查询条件的校验
 * @param strValue 开始时间
 * @param boxObj
 * @param paramsObj结束时间的输入框对象
 * @return 开始时间晚于结束时间则返回false，否则返回true
 */
function isStartTimeLargerEnd3(strValue,boxObj,paramsObj){
	var startTime = trim(strValue);
	var endTime = trim(paramsObj.datasObj.txtend2);
	//alert(startTime + "----" + endTime);
	var strd1 = startTime.replace(/-/g,"/");
	var strd2 = endTime.replace(/-/g,"/");
	//alert(strd1 + "----" + strd2);
	var d1 = new Date(Date.parse(strd1));
	var d2 = new Date(Date.parse(strd2));
	//alert(d1 + "-----" + d2);
	if(d1 > d2){
		//alert("false");
		return false;
	}else{
		//alert("true");
		return true;
	}
}

/**
 * 针对生成的文件的图表时间查询条件的校验
 * @param strValue 开始时间
 * @param boxObj
 * @param paramsObj结束时间的输入框对象
 * @return 开始时间晚于结束时间则返回false，否则返回true
 */
function isStartTimeLargerEnd3TB(strValue,boxObj,paramsObj){
	var startTime = trim(strValue);
	var endTime = trim(paramsObj.datasObj.txtend3);
	//alert(startTime + "----" + endTime);
	var strd1 = startTime.replace(/-/g,"/");
	var strd2 = endTime.replace(/-/g,"/");
	//alert(strd1 + "----" + strd2);
	var d1 = new Date(Date.parse(strd1));
	var d2 = new Date(Date.parse(strd2));
	//alert(d1 + "-----" + d2);
	if(d1 > d2){
		//alert("false");
		return false;
	}else{
		//alert("true");
		return true;
	}
}

/**
 * 针对管理的文件的时间查询条件的校验
 * @param strValue 开始时间
 * @param boxObj
 * @param paramsObj结束时间的输入框对象
 * @return 开始时间晚于结束时间则返回false，否则返回true
 */
function isStartTimeLargerEnd4(strValue,boxObj,paramsObj){
	var startTime = trim(strValue);
	var endTime = trim(paramsObj.datasObj.txtend4);
	//alert(startTime + "----" + endTime);
	var strd1 = startTime.replace(/-/g,"/");
	var strd2 = endTime.replace(/-/g,"/");
	//alert(strd1 + "----" + strd2);
	var d1 = new Date(Date.parse(strd1));
	var d2 = new Date(Date.parse(strd2));
	//alert(d1 + "-----" + d2);
	if(d1 > d2){
		//alert("false");
		return false;
	}else{
		//alert("true");
		return true;
	}
}

/**
 * 针对内部业务流转的时间查询条件的校验
 * @param strValue 开始时间
 * @param boxObj
 * @param paramsObj结束时间的输入框对象
 * @return 开始时间晚于结束时间则返回false，否则返回true
 */
function isStartTimeLargerEnd5(strValue,boxObj,paramsObj){
	var startTime = trim(strValue);
	var endTime = trim(paramsObj.datasObj.txtend5);
	//alert(startTime + "----" + endTime);
	var strd1 = startTime.replace(/-/g,"/");
	var strd2 = endTime.replace(/-/g,"/");
	//alert(strd1 + "----" + strd2);
	var d1 = new Date(Date.parse(strd1));
	var d2 = new Date(Date.parse(strd2));
	//alert(d1 + "-----" + d2);
	if(d1 > d2){
		//alert("false");
		return false;
	}else{
		//alert("true");
		return true;
	}
}

/**
 * 针对查询条件中的日期和时间的校验函数，空可以通过，但是空格不行   by gq 2014-6-11 
 * @param strValue 被校验的时间字符串
 * @return 不合法的时间则返回失败，否则返回成功
 */
function isdatetime(strValue)
{
	//空字符串可以通过，但是空格不行
	if(strValue.length==0) return true;
	
    var result=strValue.match(/^(\d{4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/);
    
    if(result==null) return false;
    var d= new Date(result[1], result[3]-1, result[4], result[5], result[6], result[7]);
    //alert(d);
    return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate()==result[4]&&d.getHours()==result[5]&&d.getMinutes()==result[6]&&d.getSeconds()==result[7]);

}

/**
 * 针对输入框中的日期和时间的校验函数，空格和空都不通过   by gq 2014-6-11 
 * @param strValue 被校验的时间字符串
 * @return 不合法的时间则返回失败，否则返回成功
 */
function isdatetimeInput(strValue)
{
    var result=strValue.match(/^(\d{4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/);
    
    if(result==null)  return false;
    //result[2]实际是-)
    var d= new Date(result[1], result[3]-1, result[4], result[5], result[6], result[7]);
    
    return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate()==result[4]&&d.getHours()==result[5]&&d.getMinutes()==result[6]&&d.getSeconds()==result[7]);

}

/**
 * 校验用户名和密码中是否有(~!@#$%^&*()_+{}|:\"<>\/])的特殊字符，
 * 其中\是转义用，\"和\/实际是对后一个字符的校验 by gq 2014-06-11
 * @param strValue 被校验的字符串
 * @return 含有特殊字符返回失败，否则返回成功
 */
function stripscript(strValue) 
{ 
	//所检常用的测试
	var pattern = new RegExp("[`~!@#$%^&*()_+{}|:\"<>\/]");
	//更加严格的校验含全角半角
	//var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？\"]") 
		
	if(pattern.test(strValue))
	{
		return false;
	}

	return true;
	
} 

/**
 *校验输入的必须是数字 
 *@param strValue 被校验的字符串
 * @return 含有特殊字符返回失败，否则返回成功
 */
function isNumber(strValue) 
{ 
	//验证数字测试
	var pattern = new RegExp("^[0-9]*$");
		
	if(pattern.test(strValue))
	{
		return false;
	}

	return true;	
} 
