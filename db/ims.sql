/*
Navicat MySQL Data Transfer

Source Server         : gq
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ims

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2014-09-09 21:53:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `b_cat`
-- ----------------------------
DROP TABLE IF EXISTS `b_cat`;
CREATE TABLE `b_cat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `catno` varchar(50) NOT NULL COMMENT '货号',
  `catname` varchar(255) DEFAULT NULL COMMENT '物品名称',
  `cattype` varchar(50) DEFAULT NULL COMMENT '0-试剂,1-耗材',
  `batchno` varchar(50) NOT NULL COMMENT '批号',
  `total` int(11) DEFAULT NULL COMMENT '总数',
  `rType` varchar(50) DEFAULT NULL COMMENT '分组，按照R特性分组',
  `productDate` datetime DEFAULT NULL COMMENT '生产日期',
  `producer` varchar(255) DEFAULT NULL COMMENT '生产商',
  `expiredate` datetime DEFAULT NULL COMMENT '有效期，过期日',
  `price` decimal(10,4) NOT NULL,
  `priceUnit` varchar(50) DEFAULT NULL,
  `localPrice` decimal(10,4) DEFAULT NULL,
  `dealer` varchar(255) DEFAULT NULL,
  `machineNo` varchar(50) DEFAULT NULL,
  `machineName` varchar(255) DEFAULT NULL,
  `operator` varchar(50) DEFAULT NULL,
  `makedate` datetime DEFAULT NULL,
  `modifydate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_cat
-- ----------------------------
INSERT INTO `b_cat` VALUES ('15', 'a', 'a', '0', 'a', '0', '1', '2014-09-01 00:00:00', 'a', '2014-09-30 00:00:00', '21.0000', '0', '21.0000', 'a', null, null, null, '2014-09-06 22:00:13', '2014-09-08 10:42:48');
INSERT INTO `b_cat` VALUES ('16', 'b', 'b', '0', 'b', '-8', '1', '2014-09-01 00:00:00', 'b', '2014-09-30 00:00:00', '10.0000', '1', '62.6445', 'b', null, null, null, '2014-09-05 22:01:46', '2014-09-07 10:47:02');
INSERT INTO `b_cat` VALUES ('17', 'c', 'c', '0', 'c', '0', '2', '2014-09-03 00:00:00', 'c', '2014-09-30 00:00:00', '100.0000', '0', '100.0000', 'c', null, null, null, '2014-09-03 22:02:54', '2014-09-08 14:45:40');
INSERT INTO `b_cat` VALUES ('18', 'a', 'a', '0', 'a', '4', '1', '2014-09-01 00:00:00', 'a', '2014-09-01 00:00:00', '20.0000', '0', '20.0000', 'a', null, null, null, '2014-09-03 22:07:52', '2014-09-09 00:27:39');
INSERT INTO `b_cat` VALUES ('19', 'a', 'a', '0', 'b', '0', '1', '2014-08-01 00:00:00', 'a', '2014-11-30 00:00:00', '10.0000', '0', '10.0000', 'a', null, null, null, '2014-09-03 22:08:20', '2014-09-08 14:45:51');
INSERT INTO `b_cat` VALUES ('20', '888', '试剂3', '0', '688993', '0', '1', '2014-09-01 00:00:00', 'XX药厂', '2014-10-08 00:00:00', '21.2300', '0', '21.2300', 'XX供货', null, null, null, '2014-09-07 17:15:08', '2014-09-08 10:43:59');
INSERT INTO `b_cat` VALUES ('21', '77', '777', '0', '777', '0', '1', '2014-09-07 00:00:00', '777', '2014-09-07 00:00:00', '777.0000', '0', '777.0000', '777', null, null, null, '2014-09-07 20:35:39', '2014-09-08 14:45:58');
INSERT INTO `b_cat` VALUES ('22', '898988', '8989', '0', '888', '0', '1', '2014-09-07 00:00:00', '888', '2014-09-07 00:00:00', '8888.0000', '0', '8888.0000', '888', null, null, null, '2014-09-07 20:41:41', '2014-09-08 14:46:03');
INSERT INTO `b_cat` VALUES ('23', '56565', '56565', '0', '565656', '0', '1', '2014-09-07 00:00:00', '5665', '2014-09-07 00:00:00', '0.0000', '0', '0.0000', '5656', null, null, null, '2014-09-07 21:20:56', '2014-01-01 01:17:03');
INSERT INTO `b_cat` VALUES ('24', '666', '666', '0', '55', '0', '1', '2014-08-08 00:00:00', '8', '2014-09-09 00:00:00', '11.0000', '0', '11.0000', '8', null, null, null, '2014-09-08 00:23:52', '2014-09-08 22:19:49');
INSERT INTO `b_cat` VALUES ('25', '7878', '787878', '0', '888', '0', '1', '2014-09-02 00:00:00', '88', '2014-09-09 00:00:00', '8.0000', '0', '8.0000', '88', null, null, null, '2014-09-08 01:38:42', '2014-09-08 10:41:02');
INSERT INTO `b_cat` VALUES ('26', '123456', '测试试剂1', '0', '测试批号', '39', '1', '2014-09-01 00:00:00', '太极集团', '2014-09-20 00:00:00', '2.5000', '0', '2.5000', '万岚视图', null, null, null, '2014-09-08 10:47:52', '2014-09-08 22:21:33');
INSERT INTO `b_cat` VALUES ('27', '123456', '测试试剂1', '0', '156', '0', '1', '2014-09-10 00:00:00', '玩儿', '2014-09-09 00:00:00', '0.0000', '0', '0.0000', '123', null, null, null, '2014-09-08 14:56:55', '2014-09-08 15:36:28');
INSERT INTO `b_cat` VALUES ('28', '123456', '测试试剂1', '0', '156', '6', '1', '2014-09-10 00:00:00', '玩儿', '2014-09-09 00:00:00', '12.3000', '1', '12.3000', '123', null, null, null, '2014-09-08 15:36:28', '2014-09-08 22:16:17');
INSERT INTO `b_cat` VALUES ('29', '123456', '测试试剂1', '0', '批号1', '10', '1', '2014-09-02 00:00:00', '123', '2014-09-08 00:00:00', '1.2200', '0', '1.2200', '222', null, null, null, '2014-09-08 17:02:18', '2014-09-09 21:14:54');
INSERT INTO `b_cat` VALUES ('30', '123456', '测试试剂1', '0', '23', '1', '1', '2014-09-01 00:00:00', '1', '2014-09-27 00:00:00', '1.2000', '0', '1.2000', '1', null, null, null, '2014-09-08 22:25:54', '2014-09-08 22:25:54');
INSERT INTO `b_cat` VALUES ('31', '123456', '测试试剂1', '0', '2', '2', '1', '2014-09-08 00:00:00', '2', '2014-10-11 00:00:00', '2.0000', '0', '2.0000', '2', null, null, null, '2014-09-08 22:26:17', '2014-09-08 22:26:17');
INSERT INTO `b_cat` VALUES ('32', '123456', '测试试剂1', '0', '3', '5', '1', '2014-09-08 00:00:00', '3', '2014-10-10 00:00:00', '3.0000', '0', '3.0000', '3', null, null, null, '2014-09-08 22:26:42', '2014-09-08 22:26:42');
INSERT INTO `b_cat` VALUES ('33', '123456', '测试试剂1', '0', '4', '8', '1', '2014-09-02 00:00:00', '4', '2014-10-10 00:00:00', '4.0000', '0', '4.0000', '4', null, null, null, '2014-09-08 22:27:11', '2014-09-08 22:27:11');
INSERT INTO `b_cat` VALUES ('34', '123456', '测试试剂1', '0', '5', '5', '1', '2014-09-01 00:00:00', '5', '2014-10-11 00:00:00', '5.0000', '0', '5.0000', '5', null, null, null, '2014-09-08 22:27:32', '2014-09-08 22:50:33');
INSERT INTO `b_cat` VALUES ('35', '123456', '测试试剂1', '0', '6', '6', '1', '2014-09-08 00:00:00', '6', '2014-10-10 00:00:00', '6.0000', '0', '6.0000', '6', null, null, null, '2014-09-08 22:27:54', '2014-09-08 22:27:54');
INSERT INTO `b_cat` VALUES ('36', '123456', '测试试剂1', '0', '7', '7', '1', '2014-09-08 00:00:00', '7', '2014-10-03 00:00:00', '7.0000', '0', '7.0000', '7', null, null, null, '2014-09-08 22:28:12', '2014-09-08 22:28:12');
INSERT INTO `b_cat` VALUES ('37', '123456', '测试试剂1', '0', '8', '8', '1', '2014-09-08 00:00:00', '8', '2014-10-03 00:00:00', '8.0000', '0', '8.0000', '8', null, null, null, '2014-09-08 22:28:30', '2014-09-08 22:28:30');
INSERT INTO `b_cat` VALUES ('38', '123456', '测试试剂1', '0', '9', '9', '1', '2014-09-08 00:00:00', '9', '2014-10-03 00:00:00', '9.0000', '0', '9.0000', '9', null, null, null, '2014-09-08 22:28:54', '2014-09-08 22:28:54');

-- ----------------------------
-- Table structure for `b_in`
-- ----------------------------
DROP TABLE IF EXISTS `b_in`;
CREATE TABLE `b_in` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '货号，试剂号耗材号',
  `catno` varchar(50) NOT NULL,
  `catName` varchar(255) DEFAULT NULL COMMENT '试剂名称，冗余，b_cat.name',
  `seq` varchar(50) DEFAULT NULL,
  `batchNo` varchar(50) DEFAULT NULL COMMENT '批号',
  `cattype` varchar(50) DEFAULT NULL,
  `producer` varchar(255) DEFAULT NULL COMMENT '生产商',
  `dealer` varchar(255) DEFAULT NULL COMMENT '经销商',
  `productDate` datetime DEFAULT NULL COMMENT '生产日期',
  `reason` varchar(255) DEFAULT NULL COMMENT '入库原因 codetype=''inreason''',
  `expiredate` datetime DEFAULT NULL COMMENT '失效日',
  `num` int(11) DEFAULT NULL COMMENT '数量',
  `numUnit` varchar(50) DEFAULT NULL COMMENT '数量单位',
  `price` decimal(10,4) DEFAULT NULL COMMENT '单价，原始单价',
  `priceUnit` varchar(50) DEFAULT NULL COMMENT '货币单位 codetype=''money'',存储codename',
  `localPrice` decimal(10,4) DEFAULT NULL COMMENT '本地货币单价，通过汇率转化',
  `taxRate` decimal(10,4) DEFAULT NULL COMMENT '税率',
  `inDate` datetime DEFAULT NULL COMMENT '入库日期',
  `machineName` varchar(50) DEFAULT NULL COMMENT '设备编号',
  `rtype` varchar(50) DEFAULT NULL COMMENT 'R分类',
  `catFrom` varchar(100) DEFAULT NULL COMMENT '来源，oversea，local',
  `person` varchar(50) DEFAULT NULL COMMENT '入库人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `operator` varchar(255) DEFAULT NULL COMMENT '当前用户',
  `makedate` datetime DEFAULT NULL COMMENT '产生日期',
  `modifydate` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8 COMMENT='试剂表';

-- ----------------------------
-- Records of b_in
-- ----------------------------
INSERT INTO `b_in` VALUES ('23', 'b', 'b', null, 'b', '1', 'b', 'b', '2014-09-01 00:00:00', '1', '2014-09-30 00:00:00', '1', null, '10.0000', '1', '62.6445', '0.0000', '2014-09-03 00:00:00', null, '1', '0', '1', 'bb', null, null, null);
INSERT INTO `b_in` VALUES ('25', 'a', 'a', null, 'a', '0', 'a', 'a', '2014-09-01 00:00:00', '0', '2014-09-01 00:00:00', '10', null, '20.0000', '0', '20.0000', '0.0000', '2014-09-03 00:00:00', null, '1', '0', '2', 'a', null, null, null);
INSERT INTO `b_in` VALUES ('31', '666', '666', null, '55', '0', '8', '8', '2014-08-08 00:00:00', '0', '2014-09-09 00:00:00', '1', null, '11.0000', '0', '11.0000', '0.0000', '2014-08-26 00:00:00', null, '1', '0', '1', '', null, null, null);
INSERT INTO `b_in` VALUES ('33', '123456', '测试试剂1', null, '测试批号', '0', '太极集团', '万岚视图', '2014-09-01 00:00:00', '0', '2014-09-20 00:00:00', '51', null, '2.5000', '0', '2.5000', '0.0000', '2014-08-30 00:00:00', null, '1', '0', '1', '', null, null, null);
INSERT INTO `b_in` VALUES ('34', '123456', '测试试剂1', null, '156', '0', '玩儿', '123', '2014-09-10 00:00:00', '0', '2014-09-09 00:00:00', '12', null, '12.3000', '1', '77.0527', '0.0000', '2014-09-08 00:00:00', null, '1', '0', '1', '为', '1', '2014-09-08 14:56:55', '2014-09-08 19:24:13');
INSERT INTO `b_in` VALUES ('35', '123456', '测试试剂1', null, '批号1', '0', '123', '222', '2014-09-02 00:00:00', '0', '2014-09-08 00:00:00', '11', null, '1.2200', '1', '7.6426', '0.0000', '2014-09-08 00:00:00', null, '1', '0', '四儿', '', '四儿', '2014-09-08 17:02:18', '2014-09-08 17:02:45');
INSERT INTO `b_in` VALUES ('36', '123456', '测试试剂1', null, '23', '0', '1', '1', '2014-09-01 00:00:00', '0', '2014-09-27 00:00:00', '1', null, '1.2000', '0', '1.2000', '0.0000', '2014-09-08 00:00:00', null, '1', '0', '1', '1', '1', '2014-09-08 22:25:54', '2014-09-08 22:25:54');
INSERT INTO `b_in` VALUES ('37', '123456', '测试试剂1', null, '2', '0', '2', '2', '2014-09-08 00:00:00', '0', '2014-10-11 00:00:00', '2', null, '2.0000', '0', '2.0000', '0.0000', '2014-09-08 00:00:00', null, '1', '0', '1', '2', '1', '2014-09-08 22:26:17', '2014-09-08 22:26:17');
INSERT INTO `b_in` VALUES ('38', '123456', '测试试剂1', null, '3', '0', '3', '3', '2014-09-08 00:00:00', '0', '2014-10-10 00:00:00', '5', null, '3.0000', '0', '3.0000', '0.0000', '2014-09-08 00:00:00', null, '1', '0', '1', '3', '1', '2014-09-08 22:26:42', '2014-09-08 22:26:42');
INSERT INTO `b_in` VALUES ('39', '123456', '测试试剂1', null, '4', '0', '4', '4', '2014-09-02 00:00:00', '0', '2014-10-10 00:00:00', '8', null, '4.0000', '0', '4.0000', '0.0000', '2014-09-08 00:00:00', null, '1', '0', '1', '', '1', '2014-09-08 22:27:11', '2014-09-08 22:27:11');
INSERT INTO `b_in` VALUES ('40', '123456', '测试试剂1', null, '5', '0', '5', '5', '2014-09-01 00:00:00', '0', '2014-10-11 00:00:00', '5', null, '5.0000', '0', '5.0000', '0.0000', '2014-09-08 00:00:00', null, '1', '0', '1', '', '1', '2014-09-08 22:27:32', '2014-09-08 22:27:32');
INSERT INTO `b_in` VALUES ('41', '123456', '测试试剂1', null, '6', '0', '6', '6', '2014-09-08 00:00:00', '0', '2014-10-10 00:00:00', '6', null, '6.0000', '0', '6.0000', '0.0000', '2014-09-08 00:00:00', null, '1', '0', '1', '6', '1', '2014-09-08 22:27:54', '2014-09-08 22:27:54');
INSERT INTO `b_in` VALUES ('42', '123456', '测试试剂1', null, '7', '0', '7', '7', '2014-09-08 00:00:00', '0', '2014-10-03 00:00:00', '7', null, '7.0000', '0', '7.0000', '0.0000', '2014-09-08 00:00:00', null, '1', '0', '1', '', '1', '2014-09-08 22:28:12', '2014-09-08 22:28:12');
INSERT INTO `b_in` VALUES ('43', '123456', '测试试剂1', null, '8', '0', '8', '8', '2014-09-08 00:00:00', '0', '2014-10-03 00:00:00', '8', null, '8.0000', '0', '8.0000', '0.0000', '2014-09-08 00:00:00', null, '1', '0', '1', '', '1', '2014-09-08 22:28:30', '2014-09-08 22:28:30');
INSERT INTO `b_in` VALUES ('44', '123456', '测试试剂1', null, '9', '0', '9', '9', '2014-09-08 00:00:00', '0', '2014-10-03 00:00:00', '9', null, '9.0000', '0', '9.0000', '0.0000', '2014-09-08 00:00:00', null, '1', '0', '1', '', '1', '2014-09-08 22:28:54', '2014-09-08 22:28:54');

-- ----------------------------
-- Table structure for `b_machine`
-- ----------------------------
DROP TABLE IF EXISTS `b_machine`;
CREATE TABLE `b_machine` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `machineName` varchar(255) DEFAULT NULL COMMENT '设备名称',
  `shortname` varchar(255) DEFAULT NULL COMMENT '设备简称',
  `machineNo` varchar(50) DEFAULT NULL COMMENT '设备编号',
  `alias` varchar(255) DEFAULT NULL COMMENT '别名',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `orderNo` int(11) DEFAULT NULL COMMENT '排序号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='设备机器表';

-- ----------------------------
-- Records of b_machine
-- ----------------------------
INSERT INTO `b_machine` VALUES ('1', '设备1', null, 'sb1', null, '备注1', '1');
INSERT INTO `b_machine` VALUES ('2', '设备2', null, 'sb2', null, '备注2', '-10');
INSERT INTO `b_machine` VALUES ('3', '设备3', null, 'sb3', null, '备注3', '88');
INSERT INTO `b_machine` VALUES ('4', '设备1', null, 'sb11', null, '', '22');

-- ----------------------------
-- Table structure for `b_out`
-- ----------------------------
DROP TABLE IF EXISTS `b_out`;
CREATE TABLE `b_out` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `machineName` varchar(255) DEFAULT NULL COMMENT '设备名称',
  `machineNo` varchar(50) DEFAULT NULL COMMENT '设备编号',
  `catno` varchar(50) NOT NULL,
  `catname` varchar(255) DEFAULT NULL,
  `batchno` varchar(50) NOT NULL COMMENT '批号',
  `cattype` varchar(50) DEFAULT NULL COMMENT '0-试剂,1-耗材',
  `person` varchar(255) DEFAULT NULL COMMENT '出库人',
  `outDate` datetime DEFAULT NULL COMMENT '出库日期',
  `price` decimal(10,4) NOT NULL COMMENT '单价，原始单价',
  `priceUnit` varchar(50) DEFAULT NULL COMMENT '单价，原始单价',
  `localPrice` decimal(10,4) DEFAULT NULL COMMENT '本地货币单价，通过汇率转化',
  `num` int(11) DEFAULT NULL COMMENT '数量',
  `reason` varchar(255) DEFAULT NULL COMMENT '出库原因，codetype=''outreason''',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `projectCode` varchar(255) DEFAULT NULL COMMENT '项目编号',
  `description` varchar(255) DEFAULT NULL COMMENT 'validation description',
  `cause` varchar(255) DEFAULT NULL,
  `section` varchar(255) DEFAULT NULL COMMENT 'section/instruments',
  `operator` varchar(255) DEFAULT NULL COMMENT '操作者',
  `makedate` datetime DEFAULT NULL COMMENT '创建日期',
  `modifydate` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COMMENT='出库记录';

-- ----------------------------
-- Records of b_out
-- ----------------------------
INSERT INTO `b_out` VALUES ('7', 'aa', 'aa', 'a', 'a', 'a', null, '1', '2014-09-03 00:00:00', '20.0000', '0', '20.0000', '5', '0', 'a', null, null, null, null, '001', '2014-09-03 22:11:11', '2014-09-03 22:11:11');
INSERT INTO `b_out` VALUES ('8', '设备1', 'sb1', '123456', '测试试剂1', '测试批号', null, '1', '2014-09-08 00:00:00', '2.5000', '0', '2.5000', '5', '0', '测试1', null, null, null, null, '001', '2014-09-08 10:49:42', '2014-09-08 10:49:42');
INSERT INTO `b_out` VALUES ('9', '设备1', 'sb11', '123456', '测试试剂1', '156', null, '1', '2014-09-08 00:00:00', '12.3000', '1', '12.3000', '1', '0', '125', '2', '1', '3', '4', '1', '2014-09-08 21:01:35', '2014-09-08 21:01:35');
INSERT INTO `b_out` VALUES ('10', '设备2', 'sb2', '123456', '测试试剂1', '测试批号', null, '1', '2014-09-08 00:00:00', '2.5000', '0', '2.5000', '2', '0', '', '', '', '', '', '1', '2014-09-08 21:04:06', '2014-09-08 21:04:06');
INSERT INTO `b_out` VALUES ('11', '设备2', 'sb2', '123456', '测试试剂1', '测试批号', null, '1', '2014-09-08 00:00:00', '2.5000', '0', '2.5000', '1', '0', '2122', '1212', '2121', '1212', '', '1', '2014-09-08 22:15:29', '2014-09-08 22:15:29');
INSERT INTO `b_out` VALUES ('12', '设备3', 'sb3', '123456', '测试试剂1', '156', null, '1', '2014-09-08 00:00:00', '12.3000', '1', '12.3000', '5', '1', '', '22', '11', '33', '44', '1', '2014-09-08 22:16:17', '2014-09-08 22:16:17');
INSERT INTO `b_out` VALUES ('13', '设备3', 'sb3', '666', '666', '55', null, '1', '2014-09-08 00:00:00', '11.0000', '0', '11.0000', '1', '0', '', '33', '22', '44', '55', '1', '2014-09-08 22:19:49', '2014-09-08 22:19:49');
INSERT INTO `b_out` VALUES ('14', '设备2', 'sb2', '123456', '测试试剂1', '测试批号', null, '1', '2014-09-08 00:00:00', '2.5000', '0', '2.5000', '1', '0', '', '', '', '', '', '1', '2014-09-08 22:20:05', '2014-09-08 22:20:05');
INSERT INTO `b_out` VALUES ('15', '设备2', 'sb2', '123456', '测试试剂1', '测试批号', null, '1', '2014-09-08 00:00:00', '2.5000', '0', '2.5000', '1', '0', '', '4', '4', '4', '4', '1', '2014-09-08 22:20:23', '2014-09-08 22:20:23');
INSERT INTO `b_out` VALUES ('16', '设备2', 'sb2', '123456', '测试试剂1', '测试批号', null, '1', '2014-09-08 00:00:00', '2.5000', '0', '2.5000', '1', '0', '', '5', '5', '5', '5', '1', '2014-09-08 22:20:39', '2014-09-08 22:20:39');
INSERT INTO `b_out` VALUES ('17', '设备2', 'sb2', '123456', '测试试剂1', '测试批号', null, '1', '2014-09-08 00:00:00', '2.5000', '0', '2.5000', '1', '0', '6', '6', '6', '6', '6', '1', '2014-09-08 22:21:33', '2014-09-08 22:21:33');
INSERT INTO `b_out` VALUES ('19', '设备2', 'sb2', 'a', 'a', 'a', null, '1', '2014-09-09 00:00:00', '20.0000', '0', '20.0000', '1', '2', 'sss', '1', '1', '1.Global sequestered harmonized lots', 'Urinalysis', '1', '2014-09-09 00:27:39', '2014-09-09 00:27:39');
INSERT INTO `b_out` VALUES ('20', '设备1', 'sb11', '123456', '测试试剂1', '批号1', null, '1', '2014-09-09 00:00:00', '1.2200', '0', '1.2200', '1', '0', '11', '', '', '', '', '1', '2014-09-09 21:14:54', '2014-09-09 21:14:54');

-- ----------------------------
-- Table structure for `b_person`
-- ----------------------------
DROP TABLE IF EXISTS `b_person`;
CREATE TABLE `b_person` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `alias` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_person
-- ----------------------------

-- ----------------------------
-- Table structure for `b_var`
-- ----------------------------
DROP TABLE IF EXISTS `b_var`;
CREATE TABLE `b_var` (
  `bizkey` varchar(255) NOT NULL,
  `bizValue` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`bizkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务数据配置';

-- ----------------------------
-- Records of b_var
-- ----------------------------
INSERT INTO `b_var` VALUES ('banchTime', '3', '新批次提醒的阈值，距离当前时间X天之内的新批号');
INSERT INTO `b_var` VALUES ('expireTime', '7', '过期提醒的阈值，距离当前时间少于X天物品');
INSERT INTO `b_var` VALUES ('inTime', '3', '入库后悔天数');
INSERT INTO `b_var` VALUES ('newinout', '3', '最近X天的出入库记录');
INSERT INTO `b_var` VALUES ('outTime', '3', '出库后悔天数');
INSERT INTO `b_var` VALUES ('stockthreshold', '10', '库存少于X件进行提醒的阈值');

-- ----------------------------
-- Table structure for `d_catcode`
-- ----------------------------
DROP TABLE IF EXISTS `d_catcode`;
CREATE TABLE `d_catcode` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `catno` varchar(20) CHARACTER SET utf8 DEFAULT NULL COMMENT '货号',
  `seq` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '编号，未来全球统一编号的预留',
  `catname` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '试剂/耗材的名称',
  `cattype` int(11) DEFAULT NULL COMMENT '0-试剂,1-耗材',
  `orde` int(11) DEFAULT NULL COMMENT '排序号（耗材维约定成俗的，试剂为统计表排序预留）',
  `remark` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `machineNo` int(11) DEFAULT NULL COMMENT '所属设备ID',
  `machinename` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '所属设备名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of d_catcode
-- ----------------------------
INSERT INTO `d_catcode` VALUES ('27', '666', '666', '666', '0', '666', '6666', null, '666');
INSERT INTO `d_catcode` VALUES ('28', '777', '777', '777', '0', '777', '777', null, '777');
INSERT INTO `d_catcode` VALUES ('29', '888', '888', '888', '0', '888', '8888', null, '888');
INSERT INTO `d_catcode` VALUES ('30', '9999999', '999999', '9999999', '0', '999', '999999', null, '999999');
INSERT INTO `d_catcode` VALUES ('31', '000', '000', '0000', '0', '0', '0000', null, '0000');
INSERT INTO `d_catcode` VALUES ('32', '8989', '', '8989', '0', '0', '', null, '9889');
INSERT INTO `d_catcode` VALUES ('33', '77', '777', '777', '0', '0', '', null, '777');
INSERT INTO `d_catcode` VALUES ('34', '898988', '89898', '8989', '0', '0', '', null, '98989');
INSERT INTO `d_catcode` VALUES ('35', '7878', '7878787', '787878', '0', '0', '', null, '77888');
INSERT INTO `d_catcode` VALUES ('36', '55', '55', '55', '0', '55', '', null, '设备3');
INSERT INTO `d_catcode` VALUES ('37', '56565', '56565', '56565', '0', '0', '', null, '5565');
INSERT INTO `d_catcode` VALUES ('38', '123456', '123456', '测试试剂1', '0', '1', '', null, '设备2');

-- ----------------------------
-- Table structure for `d_code`
-- ----------------------------
DROP TABLE IF EXISTS `d_code`;
CREATE TABLE `d_code` (
  `codetype` varchar(50) NOT NULL COMMENT '码表类目',
  `code` varchar(10) NOT NULL COMMENT '码表编码',
  `codename` varchar(255) DEFAULT NULL COMMENT '名称',
  `codealias` varchar(50) DEFAULT NULL COMMENT '名称别名',
  PRIMARY KEY (`codetype`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of d_code
-- ----------------------------
INSERT INTO `d_code` VALUES ('bool', '0', '否', null);
INSERT INTO `d_code` VALUES ('bool', '1', '是', null);
INSERT INTO `d_code` VALUES ('catfrom', '0', 'local', null);
INSERT INTO `d_code` VALUES ('catfrom', '1', 'oversea', null);
INSERT INTO `d_code` VALUES ('cattype', '0', '试剂', null);
INSERT INTO `d_code` VALUES ('cattype', '1', '耗材', null);
INSERT INTO `d_code` VALUES ('inreason', '0', 'From Vendor', null);
INSERT INTO `d_code` VALUES ('inreason', '1', 'From InterLab', null);
INSERT INTO `d_code` VALUES ('inreason', '2', 'From Sponsor', null);
INSERT INTO `d_code` VALUES ('inreason', '3', 'Other Charges', null);
INSERT INTO `d_code` VALUES ('money', '0', 'CNY', '人民币');
INSERT INTO `d_code` VALUES ('money', '1', 'USD', '美元');
INSERT INTO `d_code` VALUES ('money', '2', 'SGD', '新元');
INSERT INTO `d_code` VALUES ('money', '3', 'EUR', '欧元');
INSERT INTO `d_code` VALUES ('money', '4', 'GBP', '英镑');
INSERT INTO `d_code` VALUES ('outreason', '0', 'Trial Test', null);
INSERT INTO `d_code` VALUES ('outreason', '1', 'Validation', null);
INSERT INTO `d_code` VALUES ('outreason', '2', 'Discard', null);
INSERT INTO `d_code` VALUES ('outreason', '3', 'To IntelLab', null);
INSERT INTO `d_code` VALUES ('outreason', '4', 'To Site/Sponsor', null);
INSERT INTO `d_code` VALUES ('outreason', '5', 'Othre Cost', null);
INSERT INTO `d_code` VALUES ('role', '0', '普通用户', null);
INSERT INTO `d_code` VALUES ('role', '1', '高级用户', null);
INSERT INTO `d_code` VALUES ('rootcause', '1', '1.Global sequestered harmonized lots', null);
INSERT INTO `d_code` VALUES ('rootcause', '2', '2.Reflex testing/ Low volume tests', null);
INSERT INTO `d_code` VALUES ('rootcause', '3', '3.Study cancellation', null);
INSERT INTO `d_code` VALUES ('rootcause', '4', '4.Change in study timeline (study starts late or end early) or sample volume', null);
INSERT INTO `d_code` VALUES ('rootcause', '5', '5.Validation (including change in testing requirements)', null);
INSERT INTO `d_code` VALUES ('rootcause', '6', '6.Incorrect reagent forecast (over-estimate) or order error', null);
INSERT INTO `d_code` VALUES ('rootcause', '7', '7.Lot-to-Lot verification or failure.', null);
INSERT INTO `d_code` VALUES ('rtype', '1', 'R1', null);
INSERT INTO `d_code` VALUES ('rtype', '10', 'R10', null);
INSERT INTO `d_code` VALUES ('rtype', '2', 'R2', null);
INSERT INTO `d_code` VALUES ('rtype', '3', 'R3', null);
INSERT INTO `d_code` VALUES ('rtype', '4', 'R4', null);
INSERT INTO `d_code` VALUES ('rtype', '5', 'R5', null);
INSERT INTO `d_code` VALUES ('rtype', '6', 'R6', null);
INSERT INTO `d_code` VALUES ('rtype', '7', 'R7', null);
INSERT INTO `d_code` VALUES ('rtype', '8', 'R8', null);
INSERT INTO `d_code` VALUES ('rtype', '9', 'R9', null);
INSERT INTO `d_code` VALUES ('rtype', '99', 'other', null);
INSERT INTO `d_code` VALUES ('sections', '1', 'Anatomic Pathology', null);
INSERT INTO `d_code` VALUES ('sections', '10', 'LCMSMS', null);
INSERT INTO `d_code` VALUES ('sections', '11', 'Liaison', null);
INSERT INTO `d_code` VALUES ('sections', '12', 'Microbiology', null);
INSERT INTO `d_code` VALUES ('sections', '13', 'Mod P', null);
INSERT INTO `d_code` VALUES ('sections', '14', 'Others', null);
INSERT INTO `d_code` VALUES ('sections', '15', 'PCR', null);
INSERT INTO `d_code` VALUES ('sections', '16', 'Phadia', null);
INSERT INTO `d_code` VALUES ('sections', '17', 'Urinalysis', null);
INSERT INTO `d_code` VALUES ('sections', '2', 'Centaur', null);
INSERT INTO `d_code` VALUES ('sections', '3', 'Coagulation', null);
INSERT INTO `d_code` VALUES ('sections', '4', 'DNA/ PBMC', null);
INSERT INTO `d_code` VALUES ('sections', '5', 'E411', null);
INSERT INTO `d_code` VALUES ('sections', '6', 'ELISA', null);
INSERT INTO `d_code` VALUES ('sections', '7', 'Flow CytometryFlow Cytometry', null);
INSERT INTO `d_code` VALUES ('sections', '8', 'Hematology', null);
INSERT INTO `d_code` VALUES ('sections', '9', 'Immulite', null);
INSERT INTO `d_code` VALUES ('sex', '0', '女', null);
INSERT INTO `d_code` VALUES ('sex', '1', '男', null);

-- ----------------------------
-- Table structure for `d_codetype`
-- ----------------------------
DROP TABLE IF EXISTS `d_codetype`;
CREATE TABLE `d_codetype` (
  `codetype` varchar(50) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `remark` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`codetype`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='码表汇总';

-- ----------------------------
-- Records of d_codetype
-- ----------------------------
INSERT INTO `d_codetype` VALUES ('money', '货币', null);
INSERT INTO `d_codetype` VALUES ('role', '角色', null);
INSERT INTO `d_codetype` VALUES ('sex', '性别', null);
INSERT INTO `d_codetype` VALUES ('inreason', '入库原因', null);
INSERT INTO `d_codetype` VALUES ('outreason', '出库原因', null);
INSERT INTO `d_codetype` VALUES ('bool', '是否', null);
INSERT INTO `d_codetype` VALUES ('catfrom', '来源', '国内或者国外');
INSERT INTO `d_codetype` VALUES ('cattype', '物品类型', '试剂或耗材');
INSERT INTO `d_codetype` VALUES ('rtype', 'R分组', null);
INSERT INTO `d_codetype` VALUES ('rootcause', 'Discard的rootcause', '由于Discard原因出库时的进行root cause的枚举选择');
INSERT INTO `d_codetype` VALUES ('sections', 'Discard的Sections', '由于Discard原因出库时填写的sections / instruments');

-- ----------------------------
-- Table structure for `d_dept`
-- ----------------------------
DROP TABLE IF EXISTS `d_dept`;
CREATE TABLE `d_dept` (
  `id` int(11) NOT NULL,
  `code` varchar(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `shortname` varchar(50) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `grade` varchar(5) DEFAULT NULL COMMENT '0-总部,1-区域公司,2-国家总公司,3-部门,4-小组,5-临时机构',
  `parent` int(11) DEFAULT NULL,
  `adress` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of d_dept
-- ----------------------------

-- ----------------------------
-- Table structure for `d_rate`
-- ----------------------------
DROP TABLE IF EXISTS `d_rate`;
CREATE TABLE `d_rate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `localMoney` varchar(255) NOT NULL COMMENT '本币,d_code中codetype=''mondy''的code',
  `localMoneyName` varchar(255) DEFAULT NULL COMMENT '本币名称，本币,d_code中codetype=''mondy''的codename',
  `foreignMoney` varchar(255) NOT NULL COMMENT '外币编号,d_code中codetype=''mondy''的code',
  `foreignMoneyName` varchar(255) DEFAULT NULL COMMENT '外币名称,d_code中codetype=''mondy''的codename',
  `rate` decimal(10,6) NOT NULL COMMENT '汇率',
  `startDateTime` datetime DEFAULT NULL COMMENT '汇率开始执行时间',
  `operator` varchar(255) DEFAULT NULL COMMENT '操作者',
  `maketime` datetime DEFAULT NULL COMMENT '数据创建时间',
  `modifytime` datetime DEFAULT NULL COMMENT '数据修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of d_rate
-- ----------------------------
INSERT INTO `d_rate` VALUES ('1', '0', 'CNY', '0', 'CNY', '1.000000', '2014-01-01 00:00:00', '001', '2014-09-03 13:33:04', '2014-09-03 13:33:07');
INSERT INTO `d_rate` VALUES ('2', '0', 'CNY', '1', 'USD', '0.159600', '2014-01-01 00:00:00', '001', '2014-09-03 13:33:04', '2014-09-03 13:33:04');
INSERT INTO `d_rate` VALUES ('3', '0', 'CNY', '2', 'SGD', '0.197917', '2014-01-01 00:00:00', '001', '2014-09-03 13:33:04', '2014-09-03 13:33:04');
INSERT INTO `d_rate` VALUES ('4', '0', 'CNY', '3', 'EUR', '0.124483', '2014-01-01 00:00:00', '001', '2014-09-03 13:33:04', '2014-09-03 13:33:04');
INSERT INTO `d_rate` VALUES ('5', '0', 'CNY', '4', 'GBP', '0.104938', '2014-01-01 00:00:00', '001', '2014-09-03 13:33:04', '2014-09-03 13:33:04');
INSERT INTO `d_rate` VALUES ('6', '0', 'CNY', '1', 'USD', '0.159631', '2014-08-01 00:00:00', '001', '2014-09-03 15:04:50', '2014-09-03 15:04:52');
INSERT INTO `d_rate` VALUES ('7', '0', 'CNY', '1', 'USD', '0.159632', '2015-01-01 00:00:00', '001', '2014-09-03 15:04:50', '2014-09-03 15:04:50');

-- ----------------------------
-- Table structure for `d_task`
-- ----------------------------
DROP TABLE IF EXISTS `d_task`;
CREATE TABLE `d_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Code` varchar(255) NOT NULL COMMENT '任务编号',
  `Name` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `Flag` varchar(255) DEFAULT NULL COMMENT '运行标识,代表月份',
  `TaskClass` varchar(255) DEFAULT NULL COMMENT '任务运行类',
  `runPoint` int(11) DEFAULT NULL COMMENT '运行时点，结合flag。eg flag=D,runPoint=26,即每月26日跑',
  `runType` varchar(50) DEFAULT NULL COMMENT '运行类型 Y-年,M-月,D-天,H-小时',
  `runTime` time DEFAULT NULL COMMENT '运行具体时点',
  `startDateTime` datetime DEFAULT NULL COMMENT '开始运行时间，如果需要在一个时间段运行，则此字段存在',
  `endDateTime` datetime DEFAULT NULL COMMENT '结束运行时间，如果一段时间之后停止执行，则需要配此字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of d_task
-- ----------------------------
INSERT INTO `d_task` VALUES ('1', 'daytask', '每日定时任务', '', null, '0', 'H', '00:00:00', '2014-09-03 13:19:33', '2099-09-03 13:20:58');
INSERT INTO `d_task` VALUES ('2', 'monthtask', '每月定时任务1月份', '1', null, '26', 'M', '00:00:00', null, null);
INSERT INTO `d_task` VALUES ('3', 'monthtask', '每月定时任务2月份', '2', null, '26', 'M', '00:00:00', null, null);
INSERT INTO `d_task` VALUES ('4', 'monthtask', '每月定时任务3月份', '3', null, '26', 'M', '00:00:00', null, null);
INSERT INTO `d_task` VALUES ('5', 'monthtask', '每月定时任务4月份', '4', null, '26', 'M', '00:00:00', null, null);
INSERT INTO `d_task` VALUES ('6', 'monthtask', '每月定时任务5月份', '5', null, '26', 'M', '00:00:00', null, null);
INSERT INTO `d_task` VALUES ('7', 'monthtask', '每月定时任务6月份', '6', null, '26', 'M', '00:00:00', null, null);
INSERT INTO `d_task` VALUES ('8', 'monthtask', '每月定时任务7月份', '7', null, '26', 'M', '00:00:00', null, null);
INSERT INTO `d_task` VALUES ('9', 'monthtask', '每月定时任务8月份', '8', null, '26', 'M', '00:00:00', null, null);
INSERT INTO `d_task` VALUES ('10', 'monthtask', '每月定时任务9月份', '9', null, '26', 'M', '00:00:00', null, null);
INSERT INTO `d_task` VALUES ('11', 'monthtask', '每月定时任务10月份', '10', null, '26', 'M', '00:00:00', null, null);
INSERT INTO `d_task` VALUES ('12', 'monthtask', '每月定时任务11月份', '11', null, '26', 'M', '00:00:00', null, null);
INSERT INTO `d_task` VALUES ('13', 'monthtask', '每月定时任务12月份', '12', null, '26', 'M', '00:00:00', null, null);

-- ----------------------------
-- Table structure for `d_user`
-- ----------------------------
DROP TABLE IF EXISTS `d_user`;
CREATE TABLE `d_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` varchar(20) DEFAULT NULL,
  `name` varchar(100) NOT NULL DEFAULT '',
  `ename` varchar(255) DEFAULT NULL,
  `nickname` varchar(100) DEFAULT NULL,
  `password` varchar(100) NOT NULL,
  `role` int(2) DEFAULT NULL,
  `sex` int(2) DEFAULT NULL,
  `dept` int(11) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `birthday` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of d_user
-- ----------------------------
INSERT INTO `d_user` VALUES ('1', 'a11111', 'name', '1', '1', '1', '1', '1', null, 'dddd', null);
INSERT INTO `d_user` VALUES ('2', '11', '111', null, '1111', '1', '1', '1', null, '111111', null);
INSERT INTO `d_user` VALUES ('4', '2222', '张三', null, '三', '1', '0', '0', null, '发的', null);
INSERT INTO `d_user` VALUES ('5', '444', '李四', null, '四儿', '1', '0', '0', null, '所属省', null);
INSERT INTO `d_user` VALUES ('6', '444', '王五', null, '乌尔', '1', '1', '1', null, '辅导费', null);

-- ----------------------------
-- Table structure for `d_var`
-- ----------------------------
DROP TABLE IF EXISTS `d_var`;
CREATE TABLE `d_var` (
  `syskey` varchar(20) NOT NULL,
  `sysvalue` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`syskey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统数据配置';

-- ----------------------------
-- Records of d_var
-- ----------------------------
INSERT INTO `d_var` VALUES ('localmoney', '0', '本币为CNY人民币');
INSERT INTO `d_var` VALUES ('reportpath', '/reports/', '报表保存路径');
INSERT INTO `d_var` VALUES ('taskserverip', '127.0.0.1', '任务服务器，多台服务器集群使用');

-- ----------------------------
-- View structure for `r_in_view`
-- ----------------------------
DROP VIEW IF EXISTS `r_in_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `r_in_view` AS select `b`.`id` AS `id`,`a`.`catno` AS `catno`,`a`.`catName` AS `catname`,(case when (`a`.`reason` = '0') then `a`.`num` else 0 end) AS `inVendor`,(case when (`a`.`reason` = '1') then `a`.`num` else 0 end) AS `inInterlab`,(case when (`a`.`reason` = '2') then `a`.`num` else 0 end) AS `inSponsor`,(case when (`a`.`reason` = '3') then `a`.`num` else 0 end) AS `inCharges`,`a`.`inDate` AS `indate` from (`b_in` `a` join `b_cat` `b`) where ((`a`.`catno` = `b`.`catno`) and (`a`.`batchNo` = `b`.`batchno`) and (`a`.`price` = `b`.`price`)) ;

-- ----------------------------
-- View structure for `r_out_view`
-- ----------------------------
DROP VIEW IF EXISTS `r_out_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `r_out_view` AS select `b`.`id` AS `id`,`a`.`catno` AS `catno`,`a`.`catname` AS `catname`,`a`.`price` AS `price`,(case when (`a`.`reason` = '0') then `a`.`num` else 0 end) AS `outTrialTest`,(case when (`a`.`reason` = '1') then `a`.`num` else 0 end) AS `outValidation`,(case when (`a`.`reason` = '2') then `a`.`num` else 0 end) AS `outDiscard`,(case when (`a`.`reason` = '3') then `a`.`num` else 0 end) AS `outIntelLab`,(case when (`a`.`reason` = '4') then `a`.`num` else 0 end) AS `outSponsor`,(case when (`a`.`reason` = '5') then `a`.`num` else 0 end) AS `outOthre`,`a`.`outDate` AS `outdate` from (`b_out` `a` join `b_cat` `b`) where ((`a`.`catno` = `b`.`catno`) and (`a`.`batchno` = `b`.`batchno`) and (`a`.`price` = `b`.`price`)) ;

-- ----------------------------
-- View structure for `r_view`
-- ----------------------------
DROP VIEW IF EXISTS `r_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `r_view` AS select `a`.`id` AS `id`,`a`.`catno` AS `catno`,`a`.`catname` AS `catname`,`a`.`batchno` AS `batchno`,`a`.`price` AS `price`,`a`.`priceUnit` AS `priceUnit`,`a`.`cattype` AS `cattype`,`a`.`machineNo` AS `machineno`,`a`.`machineName` AS `machineName`,`i`.`inVendor` AS `inVendor`,`i`.`inInterlab` AS `inInterlab`,`i`.`inSponsor` AS `inSponsor`,`i`.`inCharges` AS `inCharges`,(((`i`.`inVendor` + `i`.`inInterlab`) + `i`.`inSponsor`) + `i`.`inCharges`) AS `inTotal`,`o`.`outTrialTest` AS `outTrialTest`,`o`.`outValidation` AS `outValidation`,`o`.`outDiscard` AS `outDiscard`,`o`.`outIntelLab` AS `outIntelLab`,`o`.`outSponsor` AS `outSponsor`,`o`.`outOthre` AS `outOthre`,(((((`o`.`outTrialTest` + `o`.`outValidation`) + `o`.`outDiscard`) + `o`.`outIntelLab`) + `o`.`outSponsor`) + `o`.`outOthre`) AS `outTotal`,`a`.`total` AS `closing`,(case when (`a`.`priceUnit` = '0') then `a`.`price` end) AS `CNY`,(case when (`a`.`priceUnit` = '1') then `a`.`price` end) AS `USD`,(case when (`a`.`priceUnit` = '2') then `a`.`price` end) AS `SGD`,(case when (`a`.`priceUnit` = '3') then `a`.`price` end) AS `EUR`,(case when (`a`.`priceUnit` = '4') then `a`.`price` end) AS `GBP`,`a`.`localPrice` AS `localprice`,(`a`.`total` * `a`.`localPrice`) AS `totalAmount`,`i`.`indate` AS `indate`,`o`.`outdate` AS `outdate` from ((`b_cat` `a` left join `r_in_view` `i` on((`a`.`id` = `i`.`id`))) left join `r_out_view` `o` on((`a`.`id` = `o`.`id`))) ;
DROP TRIGGER IF EXISTS `tg_In_Insert_Before`;
DELIMITER ;;
CREATE TRIGGER `tg_In_Insert_Before` BEFORE INSERT ON `b_in` FOR EACH ROW begin
     -- 通过汇率计算本地单价
     declare v_localmoney int;
     declare v_rate double;
     set v_rate = 1;
     set v_localmoney = (select sysvalue from d_var where syskey='localmoney'); -- 得到本币
     if(new.priceunit <> v_localmoney) then
         set v_rate=(select rate from d_rate where localmoney = v_localmoney and foreignmoney=new.priceunit and sysdate() > startdatetime ORDER BY startDateTime desc limit 1); --  通过本币和单价换算汇率
     end if;
     set new.localprice = (new.price/v_rate);

end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_In_Insert`;
DELIMITER ;;
CREATE TRIGGER `tg_In_Insert` AFTER INSERT ON `b_in` FOR EACH ROW begin
     declare cnt int;
     set cnt=(select count(id) from b_cat a where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price);
     if cnt > 0 then
         update b_cat set total = (total+new.num) where catno=catno and batchno=batchno and price=price;
     else
         insert into b_cat(catno,catname,cattype,batchno,total,rtype,productdate,producer,expiredate,price,priceunit,localprice,dealer,makedate,modifydate)
          values(new.catno,new.catname,new.cattype,new.batchno,new.num,new.rtype,new.productdate,new.producer,new.expiredate,new.price,new.priceunit,new.localprice,new.dealer,sysdate(),sysdate());
     end if;
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_In_Update_Before`;
DELIMITER ;;
CREATE TRIGGER `tg_In_Update_Before` BEFORE UPDATE ON `b_in` FOR EACH ROW begin
     -- 通过汇率计算本地单价
     declare v_localmoney int;
     declare v_rate double;
     set v_rate = 1;
     set v_localmoney = (select sysvalue from d_var where syskey='localmoney'); -- 得到本币
     if(new.priceunit <> v_localmoney) then
         set v_rate=(select rate from d_rate where localmoney = v_localmoney and foreignmoney=new.priceunit and sysdate() > startdatetime ORDER BY startDateTime desc limit 1); --  通过本币和单价换算汇率
     end if;
     set new.localprice = (new.price/v_rate);

end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_In_Update`;
DELIMITER ;;
CREATE TRIGGER `tg_In_Update` AFTER UPDATE ON `b_in` FOR EACH ROW begin
   declare cnt int;
   set cnt=(select count(id) from b_cat a where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price);

   if old.catno = new.catno and old.batchno=new.batchno and old.price=new.price then
       update b_cat a set a.total = (a.total-old.num+new.num),a.modifydate=sysdate() where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price;
   else
       update b_cat a set a.total = a.total-old.num,a.modifydate=sysdate() where a.catno=old.catno and a.batchno=old.batchno and a.price=old.price;
       if cnt > 0 then
           update b_cat a set a.total=a.total+new.num where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price;
       else
           insert into b_cat(catno,catname,  cattype,        batchno,  total,    rtype,    productdate,    producer,    expiredate,    price,    priceunit,    localprice,        dealer,makedate,modifydate)
                values(new.catno,new.catname,new.cattype,new.batchno,new.num,new.rtype,new.productdate,new.producer,new.expiredate,new.price,new.priceunit,new.localprice,new.dealer,sysdate(),sysdate());
       end if;
   end if;
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_In_Delete`;
DELIMITER ;;
CREATE TRIGGER `tg_In_Delete` AFTER DELETE ON `b_in` FOR EACH ROW begin
     declare v_total,v_delnum int;
     set v_total=(select total from b_cat a where a.catno=old.catno and a.batchno=old.batchno and a.price=old.price);
     set v_delnum = old.num;
     if v_total < old.num  then
          set v_delnum = v_total;  --  只能删掉未出库的入库物品，已经出库的不能删除
          -- 已经出库的重新入库,这里无法实现，需要在程序中处理
          end if;
     update b_cat set total = v_total-v_delnum,modifydate=sysdate() where catno=old.catno and batchno=old.batchno and price=old.price;
     
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_Out_Insert`;
DELIMITER ;;
CREATE TRIGGER `tg_Out_Insert` BEFORE INSERT ON `b_out` FOR EACH ROW begin
     declare v_total,v_num int;
     set v_total=(select total from b_cat a where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price);
     if v_total < new.num then
          set new.num = v_total;
     end if;
     update b_cat set total = (v_total-new.num),modifydate=sysdate() where catno=new.catno and batchno=new.batchno and price=new.price;
     
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_Out_Update`;
DELIMITER ;;
CREATE TRIGGER `tg_Out_Update` BEFORE UPDATE ON `b_out` FOR EACH ROW begin
   declare cnt,v_total int;
   set cnt=(select count(id) from b_cat a where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price);
   set v_total=(select total from b_cat a where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price);
   --  修改的时候可能会修改业务主键，这里判断
   if old.catno = new.catno and old.batchno=new.batchno and old.price=new.price then
       if  v_total + old.num < new.num then 
           set new.num = v_total + old.num; -- 超额出库，设置最大出库数据为库存
       end if;
        update b_cat a set a.total = (a.total+old.num-new.num),a.modifydate=sysdate() where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price;
   else
       --  修改前的出库数据归库
       update b_cat a set a.total = a.total+old.num,a.modifydate=sysdate() where a.catno=old.catno and a.batchno=old.batchno and a.price=old.price;
      --  修改后的业务主键存在于库存中，则更新库存，不存在则出库数量为0
       if cnt > 0 then
           if v_total < new.num then
               set new.num = v_total; -- 超出库存，设定为库存
           end if;
           update b_cat a set a.total=a.total-new.num,a.modifydate=sysdate() where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price;
       else
           set new.num = 0; --  通过修改，出库了一种没有库存的物品，则出库记录修改为0
        end if;
   end if;
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_Out_Delete`;
DELIMITER ;;
CREATE TRIGGER `tg_Out_Delete` AFTER DELETE ON `b_out` FOR EACH ROW begin
     update b_cat set total = total+old.num,modifydate=sysdate() where catno=old.catno and batchno=old.batchno and price=old.price;   
end
;;
DELIMITER ;
