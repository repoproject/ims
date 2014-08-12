package com.wabacusdemo;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import com.wabacus.config.Config;
import com.wabacus.system.ReportRequest;
import com.wabacus.system.assistant.WabacusAssistant;
import com.wabacus.system.intercept.AbsFileUploadInterceptor;

/**
 * 文件上传拦截器
 * 
 * @author jyp
 * @修改人：zhouhl
 * @修改时间：2013-12-29
 * 
 */
public class FileUploadInterceptor extends AbsFileUploadInterceptor {

//	public boolean beforeDisplayFileUploadInterface(HttpServletRequest request,
//			Map<String, String> mFormAndConfigValues, PrintWriter out) {
//		return true;
//	}

	/**
	 * 文件上传之前执行的函数
	 * 
	 * @author jyp
	 * @修改人：zhouhl
	 * @修改时间：2013-12-30
	 * @返回值：查询语句字符串对象
	 */
	public boolean beforeFileUpload(HttpServletRequest request,
			FileItem fileitemObj, Map<String, String> mFormAndConfigValues,
			PrintWriter out) {
		
		// ReportRequest
		// rrequest=(ReportRequest)request.getAttribute("WX_REPORTREQUEST");
		System.out.println(mFormAndConfigValues.get(SAVEPATH_KEY));
		String strLJ = "";

		String fileName = mFormAndConfigValues.get(FILENAME_KEY);
		
		fileName = fileName.trim();
		int lengthF = fileName.length();
		if (fileName.length() > 80) {
			//out.println("<script language='javascript'>alert('文件名不能大于200个字符！');</script>");
			return false;
		}

		// 获取配置文件中数据库链接
		Connection conn = Config.getInstance().getDataSource("ds_oracle")
				.getConnection();
		try {
			strLJ = getPOJO(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			WabacusAssistant.getInstance().release(conn, null);
		}

		// 将链接地址保存到配置集合中
		mFormAndConfigValues.put(SAVEPATH_KEY, strLJ);
		// mFormAndConfigValues.put(FILENAME_KEY, fileName);
		return true;
	}

//	public boolean beforeDisplayFileUploadPrompt(HttpServletRequest request,
//			List lstFieldItems, Map<String, String> mFormAndConfigValues,
//			String failedMessage, PrintWriter out) {
//		String savePath = mFormAndConfigValues.get(SAVEPATH_KEY);
//		FileItem fItemTmp;
//		StringBuffer fileNamesBuf = new StringBuffer();
//		for (int i = 0; i < lstFieldItems.size(); i++) {
//			fItemTmp = (FileItem) lstFieldItems.get(i);
//			if (fItemTmp.isFormField())
//				continue;// 如果是普通表单域
//			fileNamesBuf.append(fItemTmp.getName()).append(";");
//		}
//		if (fileNamesBuf.charAt(fileNamesBuf.length() - 1) == ';')
//			fileNamesBuf.deleteCharAt(fileNamesBuf.length() - 1);
//
//		if (failedMessage != null && !failedMessage.trim().equals("")) {// 如果上传失败
//			out.println("<h4>温馨提示：上传文件失败，" + failedMessage + "</h4>");
//		} else {
//			if (savePath.trim().equals("###\\")) {
//				out.println("<h4>温馨提示：上传文件失败，文件名中间不能有空格！</h4>");
//
//			} else {
//				// 上传成功
//				out.println("<script language='javascript'>");
//				out.println("alert('上传文件成功');");
//
//				String inputboxid = mFormAndConfigValues.get("INPUTBOXID");
//				if (inputboxid != null && !inputboxid.trim().equals("")) {// 如果是文件上传输入框的上传，则自动关闭上传界面
//					String name = mFormAndConfigValues.get("param_name");
//					name=name+"["+fileNamesBuf.toString()+"]";
//					out.println("selectOK('" + name + "','name',null,true);");
//				}
//				out.println("</script>");
//			}
//		}
//		return false;
//		
//	}

	/**
	 * 获取公文连接字符串
	 * 
	 * @author jyp
	 * @修改者：zhouhl
	 * @修改时间：2013-12-31 返回值：公文连接字符串
	 */
	public String getPOJO(Connection conn) throws SQLException {
		// Connection conn = null;
		String pojo = "";
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;

		// 从数据库中获取数据
		try {
			pstmt1 = conn.prepareStatement("select t.* from yxglconfig t");
			rs1 = pstmt1.executeQuery();
			if (rs1.next()) {
				pojo = rs1.getString("gwlj");
			} else {
				pojo = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭结果集
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			// 关闭Statement
			if (pstmt1 != null) {
				try {
					pstmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		// 返回管理链接字符串
		return pojo;
	}
}
