/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50510
Source Host           : localhost:3306
Source Database       : ims

Target Server Type    : MYSQL
Target Server Version : 50510
File Encoding         : 65001

Date: 2014-08-20 23:03:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for b_cat
-- ----------------------------
DROP TABLE IF EXISTS `b_cat`;
CREATE TABLE `b_cat` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `catno` varchar(20) NOT NULL COMMENT '货号',
  `name` varchar(100) NOT NULL COMMENT '物品名称',
  `type` varchar(255) DEFAULT NULL COMMENT '0-试剂,1-耗材',
  `batchid` int(11) NOT NULL COMMENT '批号',
  `batchname` varchar(255) DEFAULT NULL COMMENT '批次名称',
  `total` int(10) unsigned zerofill DEFAULT NULL COMMENT '总数',
  `group` varchar(10) DEFAULT NULL COMMENT '分组，按照R特性分组',
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_CAT_NO` (`catno`) USING BTREE COMMENT '不用作为主键，但是作为唯一索引标识不能重复'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_cat
-- ----------------------------

-- ----------------------------
-- Table structure for b_in
-- ----------------------------
DROP TABLE IF EXISTS `b_in`;
CREATE TABLE `b_in` (
  `id` int(11) NOT NULL COMMENT '货号，试剂号耗材号',
  `catId` varchar(50) NOT NULL,
  `batchNo` varchar(255) DEFAULT NULL COMMENT '批号',
  `producer` varchar(200) DEFAULT NULL COMMENT '生产商',
  `dealer` varchar(200) DEFAULT NULL COMMENT '经销商',
  `producerDate` datetime DEFAULT NULL COMMENT '生产日期',
  `reason` varchar(200) DEFAULT NULL COMMENT '入库原因 codetype=''orderreason''',
  `expire` datetime DEFAULT NULL COMMENT '失效日',
  `num` int(11) DEFAULT NULL COMMENT '数量',
  `numUnit` varchar(255) DEFAULT NULL COMMENT '数量单位',
  `price` decimal(10,2) DEFAULT NULL COMMENT '单价，原始单价',
  `priceUnit` varchar(255) DEFAULT NULL COMMENT '货币单位 codetype=''money'',存储codename',
  `localPrice` decimal(10,2) DEFAULT NULL COMMENT '本地货币单价，通过汇率转化',
  `taxRate` decimal(5,4) DEFAULT NULL COMMENT '税率',
  `orderDate` datetime DEFAULT NULL COMMENT '入库日期',
  `machineNo` varchar(50) DEFAULT NULL COMMENT '设备编号',
  `rType` varchar(50) DEFAULT NULL COMMENT 'R分类',
  `from` varchar(100) DEFAULT NULL COMMENT '来源，oversea，local',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='试剂表';

-- ----------------------------
-- Records of b_in
-- ----------------------------
INSERT INTO `b_in` VALUES ('1', '1', '1', 'www', 'dddd', '2014-08-20 22:56:45', 'fe', '2014-10-23 22:56:51', '10', '个', '10.00', 'USD', '60.00', '0.0000', '2014-08-20 22:57:41', '111', 'R1', 'vendor');

-- ----------------------------
-- Table structure for b_machine
-- ----------------------------
DROP TABLE IF EXISTS `b_machine`;
CREATE TABLE `b_machine` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '设备名称',
  `shortname` varchar(255) DEFAULT NULL COMMENT '设备简称',
  `seqno` int(11) DEFAULT NULL COMMENT '设备编号',
  `alias` varchar(255) DEFAULT NULL COMMENT '别名',
  `class` varchar(255) DEFAULT NULL COMMENT '分类',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备机器表';

-- ----------------------------
-- Records of b_machine
-- ----------------------------

-- ----------------------------
-- Table structure for b_out
-- ----------------------------
DROP TABLE IF EXISTS `b_out`;
CREATE TABLE `b_out` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `machineName` varchar(255) DEFAULT NULL COMMENT '设备名称',
  `machineNo` int(11) DEFAULT NULL COMMENT '设备编号',
  `catname` varchar(100) DEFAULT NULL,
  `batchno` int(11) NOT NULL COMMENT '批号',
  `person` varchar(255) NOT NULL COMMENT '出库人',
  `outDate` datetime DEFAULT NULL COMMENT '出库日期',
  `num` int(11) DEFAULT NULL COMMENT '数量',
  `reason` varchar(255) DEFAULT NULL COMMENT '出库原因，codetype=''userreason''',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `operator` varchar(255) DEFAULT NULL COMMENT '操作者',
  `makedate` datetime DEFAULT NULL COMMENT '创建日期',
  `modifydate` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='出库记录';

-- ----------------------------
-- Records of b_out
-- ----------------------------

-- ----------------------------
-- Table structure for b_person
-- ----------------------------
DROP TABLE IF EXISTS `b_person`;
CREATE TABLE `b_person` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `alias` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_person
-- ----------------------------

-- ----------------------------
-- Table structure for b_var
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

-- ----------------------------
-- Table structure for d_code
-- ----------------------------
DROP TABLE IF EXISTS `d_code`;
CREATE TABLE `d_code` (
  `codetype` varchar(50) NOT NULL COMMENT '码表类目',
  `code` varchar(10) NOT NULL COMMENT '码表编码',
  `codename` varchar(50) DEFAULT NULL COMMENT '名称',
  `codealias` varchar(50) DEFAULT NULL COMMENT '名称别名',
  PRIMARY KEY (`codetype`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of d_code
-- ----------------------------
INSERT INTO `d_code` VALUES ('money', '0', 'CNY', '人民币');
INSERT INTO `d_code` VALUES ('money', '1', 'USD', '美元');
INSERT INTO `d_code` VALUES ('money', '2', 'SGD', '新元');
INSERT INTO `d_code` VALUES ('money', '3', 'EUR', '欧元');
INSERT INTO `d_code` VALUES ('money', '4', 'GBP', '英镑');
INSERT INTO `d_code` VALUES ('orderreason', '0', 'From Vendor', null);
INSERT INTO `d_code` VALUES ('orderreason', '1', 'From InterLab', null);
INSERT INTO `d_code` VALUES ('orderreason', '2', 'From Sponsor', null);
INSERT INTO `d_code` VALUES ('orderreason', '3', 'Other Charges', null);
INSERT INTO `d_code` VALUES ('role', '0', '普通用户', null);
INSERT INTO `d_code` VALUES ('role', '1', '高级用户', null);
INSERT INTO `d_code` VALUES ('sex', '0', '女', null);
INSERT INTO `d_code` VALUES ('sex', '1', '男', null);
INSERT INTO `d_code` VALUES ('usereason', '0', 'Trial Test', null);
INSERT INTO `d_code` VALUES ('usereason', '1', 'Validation', null);
INSERT INTO `d_code` VALUES ('usereason', '2', 'Discard', null);
INSERT INTO `d_code` VALUES ('usereason', '3', 'To IntelLab', null);
INSERT INTO `d_code` VALUES ('usereason', '4', 'To Site/Sponsor', null);
INSERT INTO `d_code` VALUES ('usereason', '5', 'Othre Cost', null);

-- ----------------------------
-- Table structure for d_codetype
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
INSERT INTO `d_codetype` VALUES ('orderreason', '入库原因', null);
INSERT INTO `d_codetype` VALUES ('usereason', '出库原因', null);

-- ----------------------------
-- Table structure for d_dept
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
-- Table structure for d_user
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
INSERT INTO `d_user` VALUES ('1', 'a11111', 'name', null, 'nick', '111', '1', '1', null, 'dddd', null);
INSERT INTO `d_user` VALUES ('2', '11', '111', null, '1111', 'ecf6e9a713c8533bbad5393ec4a9dab4', '1', '1', null, '111111', null);
INSERT INTO `d_user` VALUES ('4', '2222', '张三', null, '三', 'ecf6e9a713c8533bbad5393ec4a9dab4', '0', '0', null, '发的', null);
INSERT INTO `d_user` VALUES ('5', '444', '李四', null, '四儿', 'ecf6e9a713c8533bbad5393ec4a9dab4', '0', '0', null, '所属省', null);
INSERT INTO `d_user` VALUES ('6', '444', '王五', null, '乌尔', 'ecf6e9a713c8533bbad5393ec4a9dab4', '1', '1', null, '辅导费', null);

-- ----------------------------
-- Table structure for d_var
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
