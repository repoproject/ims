package com.ims.rule;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ims.common.TaskData;
import com.ims.util.DBUtil;
import com.ims.util.DateTimeUtil;

/***
 * 用户信息校验类
 * @author guanqi
 *2014年10月20日
 */
public class AddEditUser {
	  private static Logger log = Logger.getLogger(AddEditUser.class);
	  
	  /***
	   *判断该昵称用户是否已经存在 
	   * @param strNikename 昵称
	   * @return true为已存在，false是不存在
	   */
	  public static boolean IsExitNickname(String strNikename)
	  {
	        // 判断昵称是否存在的sql
	        String sql = "select nickName from d_user where nickname=? ";

	        // 执行SQL
	        List<Object> list = DBUtil.query(sql, strNikename);
	        // 不存在
	        if (list.size() == 0) {

	            return false;
	        } else {
	            return true;
	        }
	  }
}
