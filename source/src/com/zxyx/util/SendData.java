package com.zxyx.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Calendar;

/**
 * 组装和发送组播数据
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */

public class SendData {
	// 发送数据包计数
	private static short count = 1;
	// 发送数据组播地址
	public static String sendIP;
	// 端口号
	public static int sendPort;
	
	public static int sendProcCommand(String sender, String senderLocation, int serverID, String softID, byte command) {		
		// 是否发送成功  1成功 0失败
		int sendState = 0;
		try {
//			out.println("创建MulticastSocket用于发送数据包......");
//			out.println("sender:" + sender + "    senderLocation:" + senderLocation + "    serverID:" + serverID 
//					+ "    softID:" + softID + "    command:" + command );
			
			// 组播地址		
			InetAddress address = InetAddress.getByName(sendIP);
//			InetAddress address = InetAddress.getByName("225.57.6.1");
			// 端口号
//			int port = 7000;
			// 组播socket
			MulticastSocket multicastSocket = null;
			try {
				multicastSocket = new MulticastSocket(sendPort);
				// 加入组播组
				multicastSocket.joinGroup(address);
				// 数据发送缓存，按规定的数据包格式组装数据
				byte[] buffer = new byte[8192];
				// 数据在buffer中存放的起始位置
				int index = 0;
				// 系统当前时间
				Calendar c = Calendar.getInstance();
/*				
				// 信源  
				byte source = (byte)0x1f;
				// 信宿  
				byte destination = (byte)0x1f;
				
				// 发送时刻    
				short year = (short)c.get(Calendar.YEAR);
				byte month = (byte)(c.get(Calendar.MONTH)+1);
				byte day = (byte)c.get(Calendar.DAY_OF_MONTH);
				byte hour = (byte)c.get(Calendar.HOUR_OF_DAY);
				byte minute = (byte)c.get(Calendar.MINUTE);
				byte second = (byte)c.get(Calendar.SECOND);
				short millisecond = (short)c.get(Calendar.MILLISECOND);				
				
				// 数据包编号（信源计算机+序号）
				byte computerNO = (byte)1;	// 信源计算机
				short packageNO = (count == 32767 ? 1 : count);	// 序号
				
				// 整包数据长度
				short length = (short)101;
				// 信息组数
				short group = (short)1;

				byte type = (byte)0x04;	// 信息类型
				short subType = (short)0x0001;	//软件编号+内部编号
				
				// 数据段长度
				short dataLen = (short)92;
				
				// 指令标识（监控服务填写）
				int commandID = -1;
*/
				
				// 信源  1B
				buffer[index] = (byte)0x1f;
				// 信宿  1B
				buffer[index + 1] = (byte)0x1f;
				
				// 发送时刻  9B
				ByteUtil.putShortM(buffer, (short)c.get(Calendar.YEAR), index + 2);
				buffer[index + 4] = (byte)(c.get(Calendar.MONTH)+1);
				buffer[index + 5] = (byte)c.get(Calendar.DAY_OF_MONTH);
				buffer[index + 6] = (byte)c.get(Calendar.HOUR_OF_DAY);
				buffer[index + 7] = (byte)c.get(Calendar.MINUTE);
				buffer[index + 8] = (byte)c.get(Calendar.SECOND);
				ByteUtil.putShortM(buffer, (short)c.get(Calendar.MILLISECOND), index + 9);
				
				// 数据包编号（信源计算机+序号）  3B
				buffer[index + 11] = (byte)1;	// 信源计算机
				short packageNO = (count == 32767 ? 1 : count);	
				ByteUtil.putShortM(buffer, packageNO, index + 12);	// 序号
				
				// 整包数据长度
				ByteUtil.putShortM(buffer, (short)101, index + 14);
				// 信息组数
				ByteUtil.putShortM(buffer, (short)1, index + 16);
				
				// 信息类别号  5B
				buffer[index + 18] = (byte)0x04;	// 信息类型
				buffer[index + 19] = (byte)0x1f;	// 信源  
				buffer[index + 20] = (byte)0x1f;	// 信宿  
				buffer[index + 21] = (byte)0x00;	//软件编号
				buffer[index + 22] = (byte)0x01;	//内部编号
				
				// 数据段长度  2B
				ByteUtil.putShortM(buffer, (short)92, index + 23);
				
				
				// 指令标识（监控服务填写）  4B
				ByteUtil.putIntL(buffer, -1, index + 25);
				// 用户名  32B
				copyBytes(sender.getBytes(), buffer, index + 29);
				// 客户端IP  32B
				copyBytes(senderLocation.getBytes(), buffer, index + 61);
				
				// 发送时间  9B
				ByteUtil.putShortL(buffer, (short)c.get(Calendar.YEAR), index + 93);
				buffer[index + 95] = (byte)(c.get(Calendar.MONTH)+1);;
				buffer[index + 96] = (byte)c.get(Calendar.DAY_OF_MONTH);
				buffer[index + 97] = (byte)c.get(Calendar.HOUR_OF_DAY);
				buffer[index + 98] = (byte)c.get(Calendar.MINUTE);
				buffer[index + 99] = (byte)c.get(Calendar.SECOND);
				ByteUtil.putShortL(buffer, (short)c.get(Calendar.MILLISECOND), index + 100);
				
				// 服务器ID  4B
				ByteUtil.putIntL(buffer, serverID, index + 102);
				// 软件标识  10B
				copyBytes(softID.getBytes(), buffer, index + 106);
				// 指令类别  1B
				buffer[index + 116] = command;
				
				// 将缓存中的数据打包(缓存中的数据共117B)
				DatagramPacket dp = new DatagramPacket(buffer, 117, address, sendPort);		
//				out.println("要发送的数据包，长度：" + dp.getLength() + "\n" + ByteUtil.byteToHexStr(dp.getData(), dp.getLength()));
				
				// 发送组播数据包
				multicastSocket.send(dp);	
				// 发送成功
				sendState = 1;
				// 数据包计数加1
				count++;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if (multicastSocket != null) {
					try {
						// 离开组播组
						multicastSocket.leaveGroup(address);
						// 关闭组播socket
						multicastSocket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		}
		return sendState;

	}
	
	/**
	 * 字节数组拷贝
	 */	
	private static void copyBytes(byte[] src, byte[] des ,int index) {
		for (int i = 0; i < src.length; i++) {
			des[index + i] = src[i];
		}
	}
		
}

	

