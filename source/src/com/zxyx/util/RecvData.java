package com.zxyx.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接收和处理组播数据
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class RecvData extends HttpServlet {
	private static final long serialVersionUID = 1L;	
	private RecvDataThread recvDataThread;
	private String recvIP;
	private int recvPort;
	
	@Override
	public void init() throws ServletException {	
		super.init();
	
/*		// 初始化数据库连接参数		
		DBUtil.driver = getInitParameter("driver");  // 数据库驱动
        DBUtil.url = getInitParameter("url");  // 数据库url
		DBUtil.user = getInitParameter("user");  // 数据库用户名
		DBUtil.password = getInitParameter("password");  // 数据库密码
*/	
		// 获取上下文
		ServletContext servletContext = getServletConfig().getServletContext();
		
		// 初始化数据库连接参数		
		DBUtil.driver = servletContext.getInitParameter("driver");  // 数据库驱动
        DBUtil.url = servletContext.getInitParameter("url");  // 数据库url
		DBUtil.user = servletContext.getInitParameter("user");  // 数据库用户名
		DBUtil.password = servletContext.getInitParameter("password");  // 数据库密码
		// 接收数据组播地址
		recvIP = servletContext.getInitParameter("RecvIP");
		// 接收组播数据端口号
		recvPort = Integer.parseInt(servletContext.getInitParameter("RecvPort"));
		// 发送数据组播地址
		SendData.sendIP = servletContext.getInitParameter("SendIP");
		// 发送组播数据端口号
		SendData.sendPort = Integer.parseInt(servletContext.getInitParameter("SendPort"));
		

//		out.println("启动接收数据线程......");
    	recvDataThread = new RecvDataThread();
    	// 启动接收数据线程
    	recvDataThread.start();
    	
    	
    	
    	// 获取SJJS软件“计划”状态更新时间(分钟+秒)
    	int minute1 = Integer.parseInt(servletContext.getInitParameter("minute1"));
    	int second1 = Integer.parseInt(servletContext.getInitParameter("second1"));
    	// SJJS软件“计划”状态更新周期(单位:分钟)
    	int period1 = Integer.parseInt(servletContext.getInitParameter("period1")); 
    	
    	// 获取CPGL软件“计划”状态更新时间(分钟+秒)
    	int minute2 = Integer.parseInt(servletContext.getInitParameter("minute2"));
    	int second2 = Integer.parseInt(servletContext.getInitParameter("second2"));
    	// CPGL软件“计划”状态更新周期(单位:分钟)
    	int period2 = Integer.parseInt(servletContext.getInitParameter("period2")); 
    	
    	// 获取FXZZ软件“计划”状态更新时间(分钟+秒)
    	int minute3 = Integer.parseInt(servletContext.getInitParameter("minute3"));
    	int second3 = Integer.parseInt(servletContext.getInitParameter("second3"));
    	// FXZZ软件“计划”状态更新周期(单位:分钟)
    	int period3 = Integer.parseInt(servletContext.getInitParameter("period3")); 
    	
    	// 获取YXGL软件“计划”状态更新时间(分钟+秒)
    	int minute4 = Integer.parseInt(servletContext.getInitParameter("minute4"));
    	int second4 = Integer.parseInt(servletContext.getInitParameter("second4"));
    	// YXGL软件“计划”状态更新周期(单位:分钟)
    	int period4 = Integer.parseInt(servletContext.getInitParameter("period4")); 

    	// 获取系统当前的分钟数和秒数
    	Calendar cal = Calendar.getInstance();
    	int curSec = cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND);
    	
    	// 状态更新时间和当前时间的时间差（单位：秒）
    	int diff1 = (minute1 * 60 + second1) - curSec;
    	int diff2 = (minute2 * 60 + second2) - curSec;
    	int diff3 = (minute3 * 60 + second3) - curSec;
    	int diff4 = (minute4 * 60 + second4) - curSec;
    	
    	// 任务延迟运行时间
    	int delay1 = (diff1 >= 0 ? diff1 : (60 * 60 + diff1));
    	int delay2 = (diff2 >= 0 ? diff2 : (60 * 60 + diff2));
    	int delay3 = (diff3 >= 0 ? diff3 : (60 * 60 + diff3));
    	int delay4 = (diff4 >= 0 ? diff4 : (60 * 60 + diff4));
    	
    	// ”计划“状态更新定时器
    	Timer timer1 = new Timer();
    	Timer timer2 = new Timer();
    	Timer timer3 = new Timer();
    	Timer timer4 = new Timer();
    	
    	// 执行定时更新任务
    	timer1.schedule(new UpdateTimerTask("SJJS"), delay1 * 1000, period1 * 60 * 1000);
    	timer2.schedule(new UpdateTimerTask("CPGL"), delay2 * 1000, period2 * 60 * 1000);
    	timer3.schedule(new UpdateTimerTask("FXZZ"), delay3 * 1000, period3 * 60 * 1000);
    	timer4.schedule(new UpdateTimerTask("YXGL"), delay4 * 1000, period4 * 60 * 1000);
  
    	
    /*
    	
    	System.out.println("当前时间：" + cal.get(Calendar.YEAR) + "-" 
    			+ (cal.get(Calendar.MONTH)+1) + "-" 
    			+ cal.get(Calendar.DAY_OF_MONTH) + " "
    			+ cal.get(Calendar.HOUR_OF_DAY)+ ":"
    			+ cal.get(Calendar.MINUTE)+ ":"
    			+ cal.get(Calendar.SECOND)
    			);
    	System.out.println("更新时间：整点的" + minute1 + "分" + second1 + "秒");
    	System.out.println("更新时间和当前间差：" + diff1 + "秒");
    	System.out.println("延迟：" + delay1 + "秒执行");
    	System.out.println("周期：" + period1 + "分");
    	
    */

    	
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		if (recvDataThread != null) {
			recvDataThread.interrupt();
		}
	}
	
	private class RecvDataThread extends Thread {
		@Override
		public void run() {
			
			try {
//				out.println("创建MulticastSocket，准备接收组播数据包......");
				// 组播地址
//				InetAddress address = InetAddress.getByName("225.57.3.1");
				InetAddress address = InetAddress.getByName(recvIP);
				// 端口号
//				int port = 7000;
				// 组播socket
				MulticastSocket multicastSocket = null;		
				try {
//					multicastSocket = new MulticastSocket(port);
					multicastSocket = new MulticastSocket(recvPort);
					// 加入组播组
					multicastSocket.joinGroup(address);
					// 数据包接收缓存
					byte[] buffer = new byte[8192];
					
					while (true) {
						// UDP数据包
						DatagramPacket dp = new DatagramPacket(buffer,buffer.length);
						// 接收组播数据包
						multicastSocket.receive(dp);
			
						// 获取数据包中的信息组数
						short msgGroups = ByteUtil.getShortM(dp.getData(), 16);						
						// 第一组信息在数据包中的起始位置
						int msgIndex = 18; 
						// 每组信息的信息头部长度（由类别号和数据段长度组成），7字节
						int headLen = 7;
						
						// 根据每一组信息的信息类别将对应的数据交给相应的service进行解析
						for (int i = 0; i < msgGroups; i++) {
							// 信息类别，5字节
							String msgType = ByteUtil.byteToHexStr(dp.getData(), msgIndex, msgIndex + 5);
							// 数据段长度，2字节
							short dataLen = ByteUtil.getShortM(dp.getData(), msgIndex + 5);
							
							
//							System.out.println("发送时间：" + DateTimeUtil.getDateTime(dp.getData(), 2) + "\n" + "msgType:" + msgType + "\n" + "data:" + ByteUtil.byteToHexStr(dp.getData(),msgIndex + headLen,msgIndex + headLen + dataLen) );
							
							
							// 服务器系统信息
							if (msgType.equalsIgnoreCase("031F1F0001")) {
								/*out.println("发送时间：" + DateTimeUtil.getDateTime(dp.getData(), 2));
								out.println("数据包:" + "\n"+ByteUtil.byteToHexStr(dp.getData(),0,dp.getLength()));
								out.println("0x031F1F0001数据段:" + "\n"+ByteUtil.byteToHexStr(dp.getData(),msgIndex + headLen,msgIndex + headLen + dataLen));
								out.println();
								*/
								// 解析服务器系统信息
								ParseData.parseSysInfo(dp.getData(), msgIndex + headLen);
							}
							// 工况信息（CPU、内存、磁盘、进程）s
							if (msgType.equalsIgnoreCase("031F1F0002")) {
								/*out.println("发送时间：" + DateTimeUtil.getDateTime(dp.getData(), 2));
								out.println("数据包:" + "\n" + ByteUtil.byteToHexStr(dp.getData(),0,dp.getLength()));
								out.println("0x031F1F0002数据段:" + "\n" + ByteUtil.byteToHexStr(dp.getData(),msgIndex + headLen,msgIndex + headLen + dataLen));
								out.println();
								*/
								// 解析工况信息
								ParseData.parseGKInfo(dp.getData(), msgIndex + headLen);
							}
						    // 系统工作状态
							if (msgType.equalsIgnoreCase("031F1F0003")) {
								/*out.println("发送时间：" + DateTimeUtil.getDateTime(dp.getData(), 2));
								out.println("数据包:" + "\n" + ByteUtil.byteToHexStr(dp.getData(),0,dp.getLength()));
								out.println("0x031F1F0003数据段:" + "\n" + ByteUtil.byteToHexStr(dp.getData(),msgIndex + headLen,msgIndex + headLen + dataLen));
								out.println();	
								*/
								// 解析系统工作状态
								ParseData.parseServerState(dp.getData(), msgIndex + headLen);					
							}
							// 数据库表空间利用率
							if (msgType.equalsIgnoreCase("031F1F0004")) {
								/*out.println("发送时间：" + DateTimeUtil.getDateTime(dp.getData(), 2));
								out.println("数据包:" + "\n" + ByteUtil.byteToHexStr(dp.getData(),0,dp.getLength()));
								out.println("0x031F1F0004数据段:" + "\n" + ByteUtil.byteToHexStr(dp.getData(),msgIndex + headLen,msgIndex + headLen + dataLen));
								out.println();	
								*/
								// 解析数据库表空间利用率
								ParseData.parseTableSpaceInfo(dp.getData(), msgIndex + headLen);					
							}
							// 告警信息
							if (msgType.equalsIgnoreCase("051F1F0001")) {
								/*out.println("发送时间：" + DateTimeUtil.getDateTime(dp.getData(), 2));
								out.println("数据包:" + "\n" + ByteUtil.byteToHexStr(dp.getData(),0,dp.getLength()));
								out.println("0x051F1F0001数据段:" + "\n" + ByteUtil.byteToHexStr(dp.getData(),msgIndex + headLen,msgIndex + headLen + dataLen));
								out.println();	
								*/
								// 解析告警信息
								ParseData.parseWarningInfo(dp.getData(), msgIndex + headLen);	
								
							}
							// 控制指令执行状态
							if (msgType.equalsIgnoreCase("041F1F0002")) {
								/*out.println("发送时间：" + DateTimeUtil.getDateTime(dp.getData(), 2));
								out.println("数据包:" + "\n" + ByteUtil.byteToHexStr(dp.getData(),0,dp.getLength()));
								out.println("0x041F1F0002数据段:" + "\n" + ByteUtil.byteToHexStr(dp.getData(),msgIndex + headLen,msgIndex + headLen + dataLen));
								out.println();	
								*/
								// 解析控制指令执行状态
								ParseData.parseControlCommandInfo(dp.getData(), msgIndex + headLen);					
							}
							
					
							// 下一组信息在数据包中的起始位置
							msgIndex = msgIndex + headLen + dataLen;
						}

					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					if (multicastSocket != null) {
						try {
							// 离开组播组
							multicastSocket.leaveGroup(address);
							multicastSocket.close();
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}
			} catch (UnknownHostException e) {
				
				e.printStackTrace();
			}
			
		}
	}
	
	private class UpdateTimerTask extends java.util.TimerTask{
		String softwareID;
		public UpdateTimerTask(String softwareID) {
			this.softwareID = softwareID;
		}
		@Override
		public void run() {
			updateState(softwareID);
		}
			
	}
	
	private void updateState(String softwareID){
		Calendar cal = Calendar.getInstance();
		String curTime = cal.get(Calendar.YEAR) + "-" 
				+ (cal.get(Calendar.MONTH) + 1) + "-"
				+ cal.get(Calendar.DAY_OF_MONTH) + " "
				+ cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE) + ":" 
				+ cal.get(Calendar.SECOND);
	
//		System.out.println( "UpdateTimerTask开始执行    " + "    当前时间：" + curTime );
		// 查询计划数据是否存在
		String sql1 = "SELECT * FROM BUSINESSINFO WHERE SOFTWAREID = ? AND STATE = ? AND PLANENDTIME < to_date(?,'yyyy-MM-dd HH24:mi:ss')";
		List<Object> list = new ArrayList<Object>();
		try {
			list = DBUtil.executeQuery(sql1, softwareID, 40 , curTime);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 计划数据存在，更新计划数据状态
		if (list.size() != 0) {
			String sql2 = "UPDATE BUSINESSINFO SET STATE = ? WHERE SOFTWAREID = ? AND STATE = ? AND PLANENDTIME < to_date(?,'yyyy-MM-dd HH24:mi:ss')";
			DBUtil.executeUpdate(sql2, 50, softwareID, 40 , curTime);
		}
			
	}

	
}
					

