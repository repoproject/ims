package com.zxyx.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.zxyx.model.AppServer1Info;
import com.zxyx.model.AppServer2Info;
import com.zxyx.model.CMSServerInfo;
import com.zxyx.model.CPUUsageInfo;
import com.zxyx.model.ControlCommand;
import com.zxyx.model.ControlCommandInfo;
import com.zxyx.model.DBAServer1Info;
import com.zxyx.model.DBAServer2Info;
import com.zxyx.model.DiskUsageInfo;
import com.zxyx.model.FileServer1Info;
import com.zxyx.model.FileServer2Info;
import com.zxyx.model.MemUsageInfo;
import com.zxyx.model.ProcInfo;
import com.zxyx.model.ServerInfo;
import com.zxyx.model.ServerState;
import com.zxyx.model.TableSpaceInfo;
import com.zxyx.model.WarningInfo;
import com.zxyx.model.WarningInternal;
import com.zxyx.model.WebServer1Info;
import com.zxyx.model.WebServer2Info;

/**
 * 从二进制数据包中解析出各种类型的数据
 * @创建者 yyy
 * @创建时间 2013-11-18
 * @修改人 yyy
 * @修改时间 2014-01-16
 */
public class ParseData {

	/**
	 * 获取时间戳信息
	 * @param pkt 包含时间戳信息的二进制数据包
	 * @param index 时间戳信息在pkt中的起始位置
	 * @return String 
	*/
	public static String getDateTime(byte[] pkt, int index) {
				
		return ByteUtil.getShortL(pkt, index)
		+ "-"
		+ (pkt[index + 2]>9?pkt[index + 2]:("0" + pkt[index + 2]))
		+ "-"
		+ (pkt[index + 3]>9?pkt[index + 3]:("0" + pkt[index + 3]))
		+ " "
		+ (pkt[index + 4]>9?pkt[index + 4]:("0" + pkt[index + 4]))
		+ ":"
		+ (pkt[index + 5]>9?pkt[index + 5]:("0" + pkt[index + 5]))
		+ ":"
		+ (pkt[index + 6]>9?pkt[index + 6]:("0" + pkt[index + 6]))
		+ " "
		+ ByteUtil.getShortL(pkt, index + 7);
	}
	
	/**
	 * 获取服务器信息实例
	 * @param serverID 服务器标识
	 * @return ServerInfo 
	*/
	public static ServerInfo getServerInfo(int serverID) {
		
	    ServerInfo serverInfo = null;		

	    //  根据服务器标识获取服务器信息实例
	    switch (serverID) {	  
	    // 文件交换服务器1
		case 1:					
			serverInfo = FileServer1Info.getFileServer1Info();
			break;	
			
		// 文件交换服务器2
		case 2:			
			serverInfo = FileServer2Info.getFileServer2Info();						
			break;
	  
		// Web服务器1
		case 3:			
			serverInfo = WebServer1Info.getWebServer1Info();		
			break;
		
		// Web服务器2
		case 4:			
			serverInfo = WebServer2Info.getWebServer2Info();		
			break;
		
		// CMS服务器
		case 5:			
			serverInfo = CMSServerInfo.getCMSServerInfo();		
			break;
		
		// 数据库管理服务器1
		case 6:			
			serverInfo = DBAServer1Info.getDBAServer1Info();			
			break;
		
		// 数据库管理服务器2
		case 7:			
			serverInfo = DBAServer2Info.getDBAServer2Info();			
			break;
		
		// 应用服务器1
		case 8:			
			serverInfo = AppServer1Info.getAppServer1Info();
			break;
		
		// 应用服务器2
		case 9:			
			serverInfo = AppServer2Info.getAppServer2Info();		
			break;			
			
		default:
			break;
		}
	    
	    return serverInfo;
		
	}
	
	/**
	 * 解析服务器系统信息
	 * @param pkt 包含服务器系统信息的二进制数据包
	 * @param index 服务器系统信息数据段在pkt中的起始位置
	 * @return void
	 * @throws UnsupportedEncodingException 
	*/	
	public static void parseSysInfo(byte[] pkt, int index) throws UnsupportedEncodingException {
		
		// 服务器标识  4B
		int serverID = ByteUtil.getIntL(pkt, index);
		// 获取服务器信息实例
		ServerInfo serverInfo = getServerInfo(serverID);	
		
	    // 解析服务器系统信息
		if (serverInfo != null) {
			// 服务器标识  4B
			serverInfo.setServerID(serverID);
			// 服务器名称  32B
			serverInfo.setServerName(new String(pkt, index + 4, 32, "gbk").trim());
			// 服务器IP地址  16B
			serverInfo.setServerIP(new String(pkt, index + 36, 16, "gbk").trim());
			// 操作系统版本信息  64B
			serverInfo.setOsInfo(new String(pkt, index + 52, 64, "gbk").trim());
			// CPU型号  64B
			serverInfo.setCpuType(new String(pkt, index + 116, 64, "gbk").trim());	
			// CPU数目  4B
			serverInfo.setCpuCount(ByteUtil.getIntL(pkt, index + 180));
			// 系统内存大小  8B
			serverInfo.setMemSize(ByteUtil.getLongL(pkt, index + 184));			
			// 服务器系统信息计数加1
			serverInfo.setSysInfoCnt(serverInfo.getSysInfoCnt() + 1);
		}	    
	}
	
	/**
	 * 解析服务器工况信息
	 * @param pkt 包含工况信息的二进制数据包
	 * @param index 工况信息数据段在pkt中的起始位置
	 * @return void
	 * @throws UnsupportedEncodingException 
	*/
	public static void parseGKInfo(byte[] pkt, int index) throws UnsupportedEncodingException {
		
		// 服务器标识  4B
		int serverID = ByteUtil.getIntM(pkt, index);
		// 数据段中包含的工况信息组数  4B
		int infoGroups = ByteUtil.getIntM(pkt, index + 4);	
		// 第一组工况信息在数据包中的起始位置
		int infoIndex = index + 8;
		// 每组工况信息的信息头部长度（由工况信息类型和信息长度组成） 8B
		int headLen = 8;
		// 获取服务器信息实例
		ServerInfo serverInfo = getServerInfo(serverID);		
	
		// 解析服务器工况信息
		if (serverInfo != null) {
			// 循环解析每一组工况信息
			for (int i = 0; i < infoGroups; i++) {
				// 信息类型  4B
				int infoType = ByteUtil.getIntM(pkt, infoIndex);
				// 信息长度  4B
				int infoLen = ByteUtil.getIntM(pkt, infoIndex + 4);
            
				// 根据工况信息类型解析每一组工况信息
				switch (infoType) {
				// CPU使用信息
				case 0:
					// 总CPU使用率  4B
					serverInfo.setTotalCpuUsage(ByteUtil.getFloatL(pkt, infoIndex + headLen));
					// CPU使用个数  4B
					int cpuUsedCount = ByteUtil.getIntL(pkt, infoIndex + headLen + 4);
					serverInfo.setCpuUsedCount(cpuUsedCount);

					ArrayList<CPUUsageInfo> cpuUsageInfos = new ArrayList<CPUUsageInfo>(cpuUsedCount);
					// 解析每一组CPU使用信息，每一组CPU使用信息长度为8B
					for (int j = 0; j < cpuUsedCount; j++) {
						CPUUsageInfo cpuUsageInfo = new CPUUsageInfo();
						// CPU标识  4B
						cpuUsageInfo.setCpuID(ByteUtil.getIntL(pkt, infoIndex + headLen + j * 8 + 8));
						// CPU使用率  4B
						cpuUsageInfo.setCpuUsage(ByteUtil.getFloatL(pkt, infoIndex + headLen + j * 8 + 12));
						
						cpuUsageInfos.add(j, cpuUsageInfo);
					}
					serverInfo.setCpuUsageInfos(cpuUsageInfos);
					
					// CPU使用信息计数加1
					serverInfo.setCpuInfoCnt(serverInfo.getCpuInfoCnt() + 1);
					
					break;

				// 内存使用信息
				case 1:
					MemUsageInfo memUsageInfo = new MemUsageInfo();
					// 内存使用大小  8B
					memUsageInfo.setMemUsedSize(ByteUtil.getLongL(pkt, infoIndex + headLen));
					// 内存使用率  4B
					memUsageInfo.setMemUsage(ByteUtil.getFloatL(pkt, infoIndex + headLen + 8));
					
					serverInfo.setMemUsageInfo(memUsageInfo);
					
					// 内存使用信息计数加1
					serverInfo.setMemInfoCnt(serverInfo.getMemInfoCnt() + 1);
					
					break;

				// 磁盘使用信息
				case 2:
					// 磁盘个数  4B
					int diskCount = ByteUtil.getIntL(pkt, infoIndex + headLen);
					serverInfo.setDiskCount(diskCount);
					
					ArrayList<DiskUsageInfo> diskUsageInfos = new ArrayList<DiskUsageInfo>(diskCount);
					// 解析每一组磁盘信息，每组磁盘信息长度为84B
					for (int j = 0; j < diskCount; j++) {
						DiskUsageInfo diskUsageInfo = new DiskUsageInfo();
						// 磁盘名称  32B
						diskUsageInfo.setDiskName(new String(pkt, infoIndex + headLen + j * 84 + 4, 32, "gbk").trim());
						// 安装点  32B
						diskUsageInfo.setMountPoint(new String(pkt, infoIndex + headLen + j * 84 + 36, 32, "gbk").trim());
						// 磁盘大小  8B
						diskUsageInfo.setDiskSize(ByteUtil.getLongL(pkt, infoIndex + headLen + j * 84 + 68));
						// 磁盘已使用空间  8B
						diskUsageInfo.setDiskUsedSize(ByteUtil.getLongL(pkt, infoIndex + headLen + j * 84 + 76));
						// 磁盘使用率  4B
						diskUsageInfo.setDiskUsage(ByteUtil.getFloatL(pkt, infoIndex + headLen + j * 84 + 84));
						
						diskUsageInfos.add(j, diskUsageInfo);
					}
					
					serverInfo.setDiskUsageInfos(diskUsageInfos);
					
					// 磁盘使用信息计数加1
					serverInfo.setDiskInfoCnt(serverInfo.getDiskInfoCnt() + 1);
					
					break;

				// 进程信息
				case 3:
					// 进程个数  4B
					int procCount = ByteUtil.getIntL(pkt, infoIndex + headLen);
					serverInfo.setProcCount(procCount);
					
					ArrayList<ProcInfo> procInfos = new ArrayList<ProcInfo>(procCount);
					// 解析每一个进程信息，每个进程信息长度为212B
					for (int j = 0; j < procCount; j++) {
						ProcInfo procInfo = new ProcInfo();
						// 软件名称  32B
						procInfo.setSoftwareName(new String(pkt, infoIndex + headLen + j * 212 + 4, 32, "gbk").trim());
						// 映像名称  32B
						procInfo.setImageName(new String(pkt, infoIndex + headLen + j * 212 + 36, 32, "gbk").trim());
						// 进程ID  8B
						procInfo.setProcID(ByteUtil.getLongL(pkt, infoIndex + headLen + j * 212 + 68));
						// 用户名  32B
						procInfo.setUserName(new String(pkt, infoIndex + headLen + j * 212 + 76, 32, "gbk").trim());
						// 进程状态  8B
						procInfo.setProcStatus(new String(pkt, infoIndex + headLen + j * 212 + 108, 8, "gbk").trim());
						// CPU使用率  4B
						procInfo.setCpuUsage(ByteUtil.getFloatL(pkt, infoIndex + headLen + j * 212 + 116));
						// 总CPU时间  4B
						procInfo.setTotalCPUTime(ByteUtil.getIntL(pkt, infoIndex + headLen + j * 212 + 120));
						// 内存使用  8B
						procInfo.setMemUsed(ByteUtil.getLongL(pkt, infoIndex + headLen + j * 212 + 124));
						// 进程优先级  4B
						procInfo.setPriority(ByteUtil.getIntL(pkt, infoIndex + headLen + j * 212 + 132));
						// 状态描述  80B
						procInfo.setStatusDesc(new String(pkt, infoIndex + headLen + j * 212 + 136, 80, "gbk").trim());
						
//						System.out.println(new String(pkt, infoIndex + headLen + j * 212 + 136, 80, "gbk").trim());
//						System.out.println(ByteUtil.byteToHexStr(pkt, infoIndex + headLen + j * 212 + 136, infoIndex + headLen + j * 212 + 136 + 80));
						
						procInfos.add(j, procInfo);
					}
					
					serverInfo.setProcInfos(procInfos);
					
					// 进程信息计数加1
					serverInfo.setProcInfoCnt(serverInfo.getProcInfoCnt() + 1);
					
					break;

				default:
					break;
				}
				// 下一组信息在数据包中的起始位置
				infoIndex = infoIndex + headLen + infoLen;
			}
		}
	}
	
	/**
	 * 解析系统工作状态
	 * @param pkt 包含系统工作状态的二进制数据包
	 * @param index 系统工作状态数据段在pkt中的起始位置
	 * @return void
	*/
	public static void parseServerState(byte[] pkt, int index) {
		ServerState serverState = ServerState.getServerState();		
		
		// 文件交换服务器1状态信息的位置		
		int statusIndex = index + 4;
		// 解析文件交换服务器1的关键软件状态和本机状态
		serverState.setFileServer1SoftStatus(pkt[statusIndex]);
		serverState.setFileServer1NetStatus(pkt[statusIndex + 1]);
		
		// 文件交换服务器2状态信息的位置
        statusIndex += 6;
        // 解析文件交换服务器2的关键软件状态和本机状态
		serverState.setFileServer2SoftStatus(pkt[statusIndex]);
		serverState.setFileServer2NetStatus(pkt[statusIndex + 1]);
		
		// Web服务器1状态信息的位置
        statusIndex += 6;
        // 解析Web服务器1的关键软件状态和本机状态
		serverState.setWebServer1SoftStatus(pkt[statusIndex]);
		serverState.setWebServer1NetStatus(pkt[statusIndex + 1]);
		
		// Web服务器2状态信息的位置
        statusIndex += 6;
        // 解析Web服务器2的关键软件状态和本机状态
		serverState.setWebServer2SoftStatus(pkt[statusIndex]);
		serverState.setWebServer2NetStatus(pkt[statusIndex + 1]);
		
		// CMS服务器状态信息的位置
        statusIndex += 6;
        // 解析CMS服务器的关键软件状态和本机状态
		serverState.setCmsServer1SoftStatus(pkt[statusIndex]);
		serverState.setCmsServer1NetStatus(pkt[statusIndex + 1]);		

		// 数据库管理服务器1状态信息的位置
        statusIndex += 6;
        // 解析数据库管理服务器1的关键软件状态和本机状态
		serverState.setDbServer1SoftStatus(pkt[statusIndex]);
		serverState.setDbServer1NetStatus(pkt[statusIndex + 1]);
		
		// 数据库管理服务器2状态信息的位置
        statusIndex += 6;
        // 解析数据库管理服务器2的关键软件状态和本机状态
		serverState.setDbServer2SoftStatus(pkt[statusIndex]);
		serverState.setDbServer2NetStatus(pkt[statusIndex + 1]);
		
		// 应用服务器1状态信息的位置
        statusIndex += 6;
        // 解析应用服务器1的关键软件状态和本机状态
		serverState.setAppServer1SoftStatus(pkt[statusIndex]);
		serverState.setAppServer1NetStatus(pkt[statusIndex + 1]);
	
		// 应用服务器2状态信息的位置
        statusIndex += 6;
        // 解析应用服务器2的关键软件状态和本机状态
		serverState.setAppServer2SoftStatus(pkt[statusIndex]);
		serverState.setAppServer2NetStatus(pkt[statusIndex + 1]);
		
		// 服务器工作状态计数加1
		serverState.setServerStateCnt(serverState.getServerStateCnt() + 1);
		
		
	}
	
	/**
	 * 解析数据库表空间利用率信息
	 * @param pkt 包含数据库表空间利用率信息的二进制数据包
	 * @param index 数据库表空间利用率信息数据段在pkt中的起始位置
	 * @return void
	*/
	public static void parseTableSpaceInfo(byte[] pkt, int index) {
		
		// 服务器标识  4B
		int serverID = ByteUtil.getIntL(pkt, index);		
		
		//  根据服务器标识解析对应的数据库表空间使用信息
		switch (serverID) {
		case 6:
			
			DBAServer1Info dbaServer1Info = DBAServer1Info.getDBAServer1Info();
			TableSpaceInfo tableSpaceInfo1 = new TableSpaceInfo();
			
			// 服务器标识  4B
			tableSpaceInfo1.setServerID(serverID);
			// 表空间总大小  4B
			tableSpaceInfo1.setTotalSpace(ByteUtil.getIntL(pkt, index + 4));
			// 表空间空闲大小  4B
			tableSpaceInfo1.setFreeSpace(ByteUtil.getIntL(pkt, index + 8));
			// 表空间使用率  4B
			tableSpaceInfo1.setTableUsage(ByteUtil.getFloatL(pkt, index + 12));
			
			dbaServer1Info.setTableSpaceInfo(tableSpaceInfo1);
			
			// 数据表空间使用率信息计数
			dbaServer1Info.setTableSpaceInfoCnt(dbaServer1Info.getTableSpaceInfoCnt() + 1);	
			break;

		case 7:

			DBAServer2Info dbaServer2Info = DBAServer2Info.getDBAServer2Info();
			TableSpaceInfo tableSpaceInfo2 = new TableSpaceInfo();
			
			// 服务器标识  4B
			tableSpaceInfo2.setServerID(serverID);
			// 表空间总大小  4B
			tableSpaceInfo2.setTotalSpace(ByteUtil.getIntL(pkt, index + 4));
			// 表空间空闲大小  4B
			tableSpaceInfo2.setFreeSpace(ByteUtil.getIntL(pkt, index + 8));
			// 表空间使用率  4B
			tableSpaceInfo2.setTableUsage(ByteUtil.getFloatL(pkt, index + 12));
			
			dbaServer2Info.setTableSpaceInfo(tableSpaceInfo2);
			
			// 数据表空间使用率信息计数
			dbaServer2Info.setTableSpaceInfoCnt(dbaServer2Info.getTableSpaceInfoCnt() + 1);			
			break;

		default:
			break;
		}
	}	
	
	/**
	 * 解析告警信息
	 * @param pkt 包含告警信息的二进制数据包
	 * @param index 告警信息数据段在pkt中的起始位置
	 * @return void
	 * @throws UnsupportedEncodingException 
	*/
	public static void parseWarningInfo(byte[] pkt, int index) throws UnsupportedEncodingException {
	
		WarningInfo warningInfo = WarningInfo.getWarningInfo();
		ArrayList<WarningInternal> warningInternals = warningInfo.getWarningInternals();
		WarningInternal warningInternal = new WarningInternal();
		
		// 告警信息编号  4B
		warningInternal.setWarnID(ByteUtil.getIntL(pkt, index));
		// 软件名称  10B
		warningInternal.setSoftName(new String(pkt, index + 4, 10, "gbk").trim());
		// 进程名称  10B
		warningInternal.setProcName(new String(pkt, index + 14, 10, "gbk").trim());
		// 告警时间  9B
		warningInternal.setWarnTime(getDateTime(pkt, index + 24));	
		// 恢复时间   默认为空
		warningInternal.setResumeTime("");
		// 告警级别  1B
		warningInternal.setWarnLevel(pkt[index + 33]);
		// 告警类型  1B
		warningInternal.setWarnType(pkt[index + 34]);
		// 文件名  40B
		warningInternal.setFileName(new String(pkt, index + 35, 40, "gbk").trim());
		// 告警状态  1B
		warningInternal.setWarnStatus(pkt[index + 75]);
		// 保留1  1B
		warningInternal.setID1(pkt[index + 76]);
		// 保留2  1B
		warningInternal.setID2(pkt[index + 77]);
		// 告警信息长度  4B
		int warnInfoLen = ByteUtil.getIntL(pkt, index + 78);
		warningInternal.setWarnInfoLen(warnInfoLen);
		// 告警信息描述  warnInfoLen个字节
		warningInternal.setWarnInfo(new String(pkt, index + 82, warnInfoLen, "gbk").trim());
		
		// 若该告警信息的状态为恢复，则只更新列表中对应的状态为可恢复的告警信息
		if (warningInternal.getWarnStatus() == 0) {
			
			// 根据告警信息编号和服务器ID从列表中查找对应的状态为可恢复的告警信息并更新
			for (int i = warningInternals.size() - 1; i >= 0; i--) {
				WarningInternal tempWarningInternal = warningInternals.get(i);
				if (tempWarningInternal.getWarnStatus() == 1 
						&& tempWarningInternal.getWarnID() == warningInternal.getWarnID() 
						&& tempWarningInternal.getID1() == warningInternal.getID1()) {
					// 更新告警恢复时间
					tempWarningInternal.setResumeTime(warningInternal.getWarnTime());
					// 更新告警状态
					tempWarningInternal.setWarnStatus(warningInternal.getWarnStatus());				
				}
				
			}
		}	
		else {
//			System.out.println("告警信息列表长度：" + warningInternals.size());
			// 判断告警信息列表中的告警信息数量是否超过最大缓存数量，若超出，则删除列表中第一条告警信息
			if (warningInternals.size() >= warningInfo.MAX_COUNT) {	
				warningInternals.remove(0);
			}
			// 向列表中添加新的告警信息
			warningInternals.add(warningInternal);
		}	
	}
		
	/**
	 * 解析控制指令执行状态信息
	 * @param pkt 包含控制指令执行状态信息的二进制数据包
	 * @param index 控制指令执行状态信息数据段在pkt中的起始位置
	 * @return void
	 * @throws UnsupportedEncodingException 
	*/
	public static void parseControlCommandInfo(byte[] pkt, int index) throws UnsupportedEncodingException {
	
		ControlCommandInfo controlCommandInfo = ControlCommandInfo.getControlCommandInfo();
		ArrayList<ControlCommand> controlCommands = controlCommandInfo.getControlCommands();
		ControlCommand controlCommand = new ControlCommand();
		
		// 控制指令标识  4B
		controlCommand.setCommandID(ByteUtil.getIntL(pkt, index));
		// 发送时间 9B
		controlCommand.setSendTime(getDateTime(pkt, index + 4));	
		// 发送者  32B
		controlCommand.setSender(new String(pkt, index + 13, 32, "gbk").trim());
		// 发送位置  32B
		controlCommand.setSenderLocation(new String(pkt, index + 45, 32, "gbk").trim());	
		// 系统名称  32B
		controlCommand.setSystemName(new String(pkt, index + 77, 32, "gbk").trim());
		// 设备名称  64B
		controlCommand.setEquipmentName(new String(pkt, index + 109, 64, "gbk").trim());
		// 指令名称  32B
		controlCommand.setCommandName(new String(pkt, index + 173, 32, "gbk").trim());
		// 指令状态  4B
		controlCommand.setCommandState(ByteUtil.getIntL(pkt, index + 205));
		// 状态时间  9B
		controlCommand.setStateTime(getDateTime(pkt, index + 209));	
		// 指令内容长度  4B
		int commandInfoLen = ByteUtil.getIntL(pkt, index + 218);
		controlCommand.setCommandInfoLen(commandInfoLen);
		// 指令内容  commandInfoLen个字节
		controlCommand.setCommandInfo(new String(pkt, index + 222, commandInfoLen, "gbk").trim());


		// 信息更新标识  false 未更新  true 更新 
		Boolean update = false;
		// 检查列表中信息的控制指令标识，若存在与新收到的信息的控制指令标识相同的信息，则对其进行更新
		for (int i = controlCommands.size() - 1; i >= 0; i--) {
			ControlCommand tempControlCommand = controlCommands.get(i);
			if (tempControlCommand.getCommandID() == controlCommand.getCommandID()) {				
				// 更新指令状态
				tempControlCommand.setCommandState(controlCommand.getCommandState());
				// 更新状态时间
				tempControlCommand.setStateTime(controlCommand.getStateTime());
				// 信息更新标识为true
				update = true;
			}			
		}
		
		// 若未进行行信息更新，则将新信息添加到列表中
		if (!update) {
			// 判断控制指令执行状态信息列表中的信息数量是否超过最大缓存数量，若超出，则删除列表中第一条控制指令执行状态信息		
			if (controlCommands.size() >= controlCommandInfo.MAX_COUNT) {
				controlCommands.remove(0);
			}			
			controlCommands.add(controlCommand);
		}
		
	}
}
