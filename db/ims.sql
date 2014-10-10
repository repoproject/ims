/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50510
Source Host           : localhost:3306
Source Database       : ims

Target Server Type    : MYSQL
Target Server Version : 50510
File Encoding         : 65001

Date: 2014-10-10 23:04:13
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for b_cat
-- ----------------------------
DROP TABLE IF EXISTS `b_cat`;
CREATE TABLE `b_cat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `catno` varchar(50) NOT NULL COMMENT '货号',
  `catname` varchar(255) DEFAULT NULL COMMENT '物品名称',
  `seq` varchar(50) DEFAULT NULL COMMENT '编号，为全球统一编号预留',
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
  `catFrom` varchar(50) DEFAULT NULL COMMENT '来源，oversea，local',
  `dealer` varchar(255) DEFAULT NULL,
  `machineNo` varchar(50) DEFAULT NULL,
  `machineName` varchar(255) DEFAULT NULL,
  `operator` varchar(50) DEFAULT NULL,
  `makedate` datetime DEFAULT NULL,
  `modifydate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=139 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_cat
-- ----------------------------
INSERT INTO `b_cat` VALUES ('137', '11183974216', 'ISE Cal Low', '', '0', '11', '10', '10', '2014-10-01 00:00:00', '11', '2014-10-31 00:00:00', '14.0000', '0', null, '0', '11', null, '', null, '2014-10-09 23:20:11', '2014-10-09 23:20:11');
INSERT INTO `b_cat` VALUES ('138', '04880285190', 'Cell wash solution / NaOH', '111', '0', '22', '8', '1', '2014-10-01 00:00:00', '1', '2014-10-31 00:00:00', '12.0000', '0', null, '0', '1', '1', '1', null, '2014-10-09 23:20:58', '2014-10-09 23:21:30');

-- ----------------------------
-- Table structure for b_file
-- ----------------------------
DROP TABLE IF EXISTS `b_file`;
CREATE TABLE `b_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `path` varchar(255) DEFAULT NULL COMMENT '路劲',
  `type` varchar(255) DEFAULT NULL COMMENT '文件类型,目前只有一种文件，即R统计文件',
  `makedate` datetime DEFAULT NULL COMMENT '文件生成日期',
  `modifydate` datetime DEFAULT NULL COMMENT '文件修改日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_file
-- ----------------------------
INSERT INTO `b_file` VALUES ('4', 'Inventory listing 2014-10.xls', '\\reports\\', null, '2014-10-06 13:31:59', '2014-10-09 23:52:20');

-- ----------------------------
-- Table structure for b_in
-- ----------------------------
DROP TABLE IF EXISTS `b_in`;
CREATE TABLE `b_in` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '货号，试剂号耗材号',
  `catno` varchar(50) NOT NULL,
  `catName` varchar(255) DEFAULT NULL COMMENT '试剂名称，冗余，b_cat.name',
  `seq` varchar(50) DEFAULT NULL COMMENT '编号，为全球统一编号预留',
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
  `catFrom` varchar(50) DEFAULT NULL COMMENT '来源，oversea，local',
  `person` varchar(50) DEFAULT NULL COMMENT '入库人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `operator` varchar(255) DEFAULT NULL COMMENT '当前用户',
  `makedate` datetime DEFAULT NULL COMMENT '产生日期',
  `modifydate` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=144 DEFAULT CHARSET=utf8 COMMENT='试剂表';

-- ----------------------------
-- Records of b_in
-- ----------------------------
INSERT INTO `b_in` VALUES ('142', '11183974216', 'ISE Cal Low', '', '11', '0', '11', '11', '2014-10-01 00:00:00', '0', '2014-10-31 00:00:00', '10', null, '14.0000', '0', null, null, '2014-10-09 00:00:00', '', '10', '0', '1', '', '1', '2014-10-09 23:20:11', '2014-10-09 23:20:11');
INSERT INTO `b_in` VALUES ('143', '04880285190', 'Cell wash solution / NaOH', '111', '22', '0', '1', '1', '2014-10-01 00:00:00', '0', '2014-10-31 00:00:00', '10', null, '12.0000', '0', null, null, '2014-10-09 00:00:00', '1', '1', '0', '1', '', '1', '2014-10-09 23:20:58', '2014-10-09 23:20:58');

-- ----------------------------
-- Table structure for b_machine
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='设备机器表';

-- ----------------------------
-- Records of b_machine
-- ----------------------------
INSERT INTO `b_machine` VALUES ('6', 'Modular Reagent', null, '1', null, '', '1');
INSERT INTO `b_machine` VALUES ('8', 'Bio-rad Controls for Modular', null, '12', null, '', '2');
INSERT INTO `b_machine` VALUES ('9', 'E411', null, '3', null, '', '3');

-- ----------------------------
-- Table structure for b_out
-- ----------------------------
DROP TABLE IF EXISTS `b_out`;
CREATE TABLE `b_out` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `machineName` varchar(255) DEFAULT NULL COMMENT '设备名称',
  `machineNo` varchar(50) DEFAULT NULL COMMENT '设备编号',
  `catno` varchar(50) NOT NULL,
  `catname` varchar(255) DEFAULT NULL,
  `rType` varchar(50) DEFAULT NULL COMMENT '分组，按照R特性分组',
  `seq` varchar(50) DEFAULT '' COMMENT '编号，为全球统一编号预留',
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='出库记录';

-- ----------------------------
-- Records of b_out
-- ----------------------------
INSERT INTO `b_out` VALUES ('11', '1', '1', '04880285190', 'Cell wash solution / NaOH', '1', '111', '22', '0', '1', '2014-10-09 00:00:00', '12.0000', '0', null, '2', '1', '', '', '', '', '', '1', '2014-10-09 23:21:30', '2014-10-09 23:21:30');

-- ----------------------------
-- Table structure for b_person
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
-- Table structure for b_report_discard
-- ----------------------------
DROP TABLE IF EXISTS `b_report_discard`;
CREATE TABLE `b_report_discard` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `catid` int(11) DEFAULT NULL,
  `flag` varchar(10) DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `rownum` int(11) DEFAULT NULL,
  `outDate` date DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `catno` varchar(100) DEFAULT NULL,
  `price` double(20,6) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `expiredate` date DEFAULT NULL,
  `qty` int(11) DEFAULT NULL,
  `totalAmnt` double(20,6) DEFAULT NULL,
  `rootCause` varchar(255) DEFAULT NULL,
  `section` varchar(255) DEFAULT NULL,
  `producer` varchar(255) DEFAULT NULL,
  `dealer` varchar(255) DEFAULT NULL,
  `catFrom` varchar(255) DEFAULT NULL,
  `makedate` datetime DEFAULT NULL,
  `operator` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_report_discard
-- ----------------------------

-- ----------------------------
-- Table structure for b_report_r
-- ----------------------------
DROP TABLE IF EXISTS `b_report_r`;
CREATE TABLE `b_report_r` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `catid` int(11) DEFAULT NULL,
  `flag` varchar(10) DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `catno` varchar(11) DEFAULT NULL,
  `machineNo` varchar(11) DEFAULT NULL,
  `catname` varchar(255) DEFAULT NULL,
  `opening` int(11) DEFAULT NULL,
  `inVendor` int(11) DEFAULT NULL,
  `inInterlab` int(11) DEFAULT NULL,
  `inSponsor` int(11) DEFAULT NULL,
  `inCharges` int(11) DEFAULT NULL,
  `inTotal` int(11) DEFAULT NULL,
  `outTrialTest` int(11) DEFAULT NULL,
  `outValidation` int(11) DEFAULT NULL,
  `outDiscard` int(11) DEFAULT NULL,
  `outIntelLab` int(11) DEFAULT NULL,
  `outSponsor` int(11) DEFAULT NULL,
  `outOther` int(11) DEFAULT NULL,
  `outTotal` int(11) DEFAULT NULL,
  `closing` int(11) DEFAULT NULL,
  `CNY` double(20,6) DEFAULT NULL,
  `USD` double(20,6) DEFAULT NULL,
  `SGD` double(20,6) DEFAULT NULL,
  `EUR` double(20,6) DEFAULT NULL,
  `GBP` double(20,6) DEFAULT NULL,
  `localprice` double(20,6) DEFAULT NULL,
  `totalAmount` double(20,6) DEFAULT NULL,
  `remark` varchar(1000) DEFAULT NULL,
  `qtyIn` double(20,6) DEFAULT NULL,
  `qtyOut` double(20,6) DEFAULT NULL,
  `checkQty` double(20,6) DEFAULT NULL,
  `checkAmt` double(20,6) DEFAULT NULL,
  `pqtyIn` double(20,6) DEFAULT NULL,
  `pqtyOut` double(20,6) DEFAULT NULL,
  `cqtyIn` double(20,6) DEFAULT NULL,
  `cqtyOut` double(20,6) DEFAULT NULL,
  `makedate` datetime DEFAULT NULL,
  `operator` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_report_r
-- ----------------------------
INSERT INTO `b_report_r` VALUES ('1', '201410', '201410', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `b_report_r` VALUES ('2', '11', '201409', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for b_report_validation
-- ----------------------------
DROP TABLE IF EXISTS `b_report_validation`;
CREATE TABLE `b_report_validation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `catid` int(11) DEFAULT NULL,
  `flag` varchar(10) DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `section` varchar(255) DEFAULT NULL,
  `outDate` date DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `rType` varchar(255) DEFAULT NULL,
  `catname` varchar(255) DEFAULT NULL,
  `qty` int(11) DEFAULT NULL,
  `priceCNY` double(20,6) DEFAULT NULL,
  `totalCostCNY` double(20,6) DEFAULT NULL,
  `totalCostUSD` double(20,6) DEFAULT NULL,
  `projectCode` varchar(255) DEFAULT NULL,
  `makeDate` datetime DEFAULT NULL,
  `operator` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_report_validation
-- ----------------------------
INSERT INTO `b_report_validation` VALUES ('1', null, '201410', null, null, '', null, '', 'R1', 'Cell wash solution / NaOH', '2', '12.000000', '24.000000', '3.840000', '', '2014-10-10 23:01:13', '001');

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
INSERT INTO `b_var` VALUES ('banchTime', '3', '新批次提醒的阈值，距离当前时间X天之内的新批号');
INSERT INTO `b_var` VALUES ('expireTime', '7', '过期提醒的阈值，距离当前时间少于X天物品');
INSERT INTO `b_var` VALUES ('inTime', '3', '入库后悔天数');
INSERT INTO `b_var` VALUES ('newinout', '3', '最近X天的出入库记录');
INSERT INTO `b_var` VALUES ('outTime', '3', '出库后悔天数');
INSERT INTO `b_var` VALUES ('stockthreshold', '10', '库存少于X件进行提醒的阈值');

-- ----------------------------
-- Table structure for d_catcode
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
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of d_catcode
-- ----------------------------
INSERT INTO `d_catcode` VALUES ('44', '11183974216', '', 'ISE Cal Low', '0', '1', '', null, '');
INSERT INTO `d_catcode` VALUES ('45', '04880455190', '', 'ISE Internal Standard', '0', '2', '', null, '');
INSERT INTO `d_catcode` VALUES ('46', '04880285190', '111', 'Cell wash solution / NaOH', '0', '0', '', null, '1');
INSERT INTO `d_catcode` VALUES ('47', '11970909216', 'rte', 'ALB', '0', '3', '', null, 'E411');
INSERT INTO `d_catcode` VALUES ('48', '594', '', 'Liquichek Immunology Control Level 1(6x3ml)', '0', '4', '', null, '');
INSERT INTO `d_catcode` VALUES ('49', '595', '', 'Liquichek Immunology Control Level 2(6x3ml)', '0', '5', '', null, '');
INSERT INTO `d_catcode` VALUES ('50', '596', '', 'Liquichek Immunology Control Level 3(6x3ml)', '0', '6', '', null, 'E411');

-- ----------------------------
-- Table structure for d_code
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
INSERT INTO `d_code` VALUES ('rtype', '11', 'CTM', 'CTM Suppliers_DTW');
INSERT INTO `d_code` VALUES ('rtype', '2', 'R2', null);
INSERT INTO `d_code` VALUES ('rtype', '20', 'Lab', 'Lab_Gen_Supplier');
INSERT INTO `d_code` VALUES ('rtype', '3', 'R3', null);
INSERT INTO `d_code` VALUES ('rtype', '4', 'R4', null);
INSERT INTO `d_code` VALUES ('rtype', '5', 'R5', null);
INSERT INTO `d_code` VALUES ('rtype', '6', 'R6', null);
INSERT INTO `d_code` VALUES ('rtype', '7', 'R7', null);
INSERT INTO `d_code` VALUES ('rtype', '8', 'R8', null);
INSERT INTO `d_code` VALUES ('rtype', '9', 'R9', null);
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
INSERT INTO `d_codetype` VALUES ('inreason', '入库原因', null);
INSERT INTO `d_codetype` VALUES ('outreason', '出库原因', null);
INSERT INTO `d_codetype` VALUES ('bool', '是否', null);
INSERT INTO `d_codetype` VALUES ('catfrom', '来源', '国内或者国外');
INSERT INTO `d_codetype` VALUES ('cattype', '物品类型', '试剂或耗材');
INSERT INTO `d_codetype` VALUES ('rtype', 'R分组', null);
INSERT INTO `d_codetype` VALUES ('rootcause', 'Discard的rootcause', '由于Discard原因出库时的进行root cause的枚举选择');
INSERT INTO `d_codetype` VALUES ('sections', 'Discard的Sections', '由于Discard原因出库时填写的sections / instruments');

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
-- Table structure for d_rate
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of d_rate
-- ----------------------------
INSERT INTO `d_rate` VALUES ('1', '0', 'CNY', '0', 'CNY', '1.000000', '2014-01-01 00:00:00', '001', '2014-09-03 13:33:04', '2014-09-03 13:33:07');
INSERT INTO `d_rate` VALUES ('2', '0', 'CNY', '1', 'USD', '0.159600', '2014-01-01 00:00:00', '001', '2014-09-03 13:33:04', '2014-09-03 13:33:04');
INSERT INTO `d_rate` VALUES ('3', '0', 'CNY', '2', 'SGD', '0.197917', '2014-01-01 00:00:00', '001', '2014-09-03 13:33:04', '2014-09-03 13:33:04');
INSERT INTO `d_rate` VALUES ('4', '0', 'CNY', '3', 'EUR', '0.124483', '2014-01-01 00:00:00', '001', '2014-09-03 13:33:04', '2014-09-03 13:33:04');
INSERT INTO `d_rate` VALUES ('5', '0', 'CNY', '4', 'GBP', '0.104938', '2014-01-01 00:00:00', '001', '2014-09-03 13:33:04', '2014-09-03 13:33:04');
INSERT INTO `d_rate` VALUES ('6', '0', 'CNY', '1', 'USD', '0.159631', '2014-08-01 00:00:00', '001', '2014-09-03 15:04:50', '2014-09-03 15:04:52');

-- ----------------------------
-- Table structure for d_task
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
INSERT INTO `d_task` VALUES ('1', 'daytask', '每日定时任务', '', null, '0', 'H', '20:25:00', '2014-09-14 00:00:00', '2099-09-03 13:20:58');
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
INSERT INTO `d_user` VALUES ('1', 'a11111', 'name', '1', '1', '1', '1', '1', null, 'dddd', null);
INSERT INTO `d_user` VALUES ('2', '11', '111', null, '1111', '1', '1', '1', null, '111111', null);
INSERT INTO `d_user` VALUES ('4', '2222', '张三', null, '三', '1', '0', '0', null, '发的', null);
INSERT INTO `d_user` VALUES ('5', '444', '李四', null, '四儿', '1', '0', '0', null, '所属省', null);
INSERT INTO `d_user` VALUES ('6', '444', '王五', null, '乌尔', '1', '1', '1', null, '辅导费', null);

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
INSERT INTO `d_var` VALUES ('localmoney', '0', '本币为CNY人民币');
INSERT INTO `d_var` VALUES ('reportname', 'Inventory listing {yyyy-MM}.xls', '报表文件名称');
INSERT INTO `d_var` VALUES ('reportpath', '\\reports\\', '报表保存路径');
INSERT INTO `d_var` VALUES ('taskserverip', '127.0.0.1', '任务服务器，多台服务器集群使用');

-- ----------------------------
-- View structure for r_in_view
-- ----------------------------
DROP VIEW IF EXISTS `r_in_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `r_in_view` AS select `b`.`id` AS `catid`,(case when (`a`.`reason` = '0') then `a`.`num` else 0 end) AS `inVendor`,(case when (`a`.`reason` = '1') then `a`.`num` else 0 end) AS `inInterlab`,(case when (`a`.`reason` = '2') then `a`.`num` else 0 end) AS `inSponsor`,(case when (`a`.`reason` = '3') then `a`.`num` else 0 end) AS `inCharges`,`a`.`inDate` AS `indate` from (`b_in` `a` join `b_cat` `b`) where ((`a`.`catno` = `b`.`catno`) and (`a`.`batchNo` = `b`.`batchno`) and (`a`.`price` = `b`.`price`)) ;

-- ----------------------------
-- View structure for r_out_view
-- ----------------------------
DROP VIEW IF EXISTS `r_out_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `r_out_view` AS select `b`.`id` AS `catid`,(case when (`a`.`reason` = '0') then `a`.`num` else 0 end) AS `outTrialTest`,(case when (`a`.`reason` = '1') then `a`.`num` else 0 end) AS `outValidation`,(case when (`a`.`reason` = '2') then `a`.`num` else 0 end) AS `outDiscard`,(case when (`a`.`reason` = '3') then `a`.`num` else 0 end) AS `outIntelLab`,(case when (`a`.`reason` = '4') then `a`.`num` else 0 end) AS `outSponsor`,(case when (`a`.`reason` = '5') then `a`.`num` else 0 end) AS `outOther`,`a`.`outDate` AS `outdate` from (`b_out` `a` join `b_cat` `b`) where ((`a`.`catno` = `b`.`catno`) and (`a`.`batchno` = `b`.`batchno`) and (`a`.`price` = `b`.`price`)) ;

-- ----------------------------
-- View structure for r_price_view
-- ----------------------------
DROP VIEW IF EXISTS `r_price_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `r_price_view` AS select `a`.`id` AS `catid`,(case when (`a`.`priceUnit` = '0') then `a`.`price` end) AS `CNY`,(case when (`a`.`priceUnit` = '1') then `a`.`price` end) AS `USD`,(case when (`a`.`priceUnit` = '2') then `a`.`price` end) AS `SGD`,(case when (`a`.`priceUnit` = '3') then `a`.`price` end) AS `EUR`,(case when (`a`.`priceUnit` = '4') then `a`.`price` end) AS `GBP`,(select round((`a`.`price` / `b`.`rate`),2) from `d_rate` `b` where (`b`.`foreignMoney` = `a`.`priceUnit`) order by `b`.`startDateTime` desc limit 1) AS `localPrice` from `b_cat` `a` ;

-- ----------------------------
-- View structure for r_qty_view
-- ----------------------------
DROP VIEW IF EXISTS `r_qty_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER  VIEW `r_qty_view` AS SELECT
	r.id,
	r.catid,
  r.flag,
	CASE
WHEN MONTH (NOW()) = 1 THEN
	0
ELSE
	round(IFNULL(r.cqtyIn, 0),2)
END asQtyIn,
	CASE
WHEN MONTH (NOW()) = 1 THEN
	0
ELSE
	round(IFNULL(r.cqtyOut, 0),2)
END asQtyOut
FROM
	b_report_r r
WHERE
	r.flag = DATE_FORMAT(
		DATE_ADD(NOW(), INTERVAL - 1 MONTH),
		'%Y%m'
	) ;

-- ----------------------------
-- Function structure for getCodeName
-- ----------------------------
DROP FUNCTION IF EXISTS `getCodeName`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `getCodeName`(`p_codeType` varchar(255),`p_code` varchar(255)) RETURNS varchar(255) CHARSET utf8
BEGIN
	declare v_codeName VARCHAR(255);

  set v_codeName=(select a.codename from d_code a where a.codetype=p_codeType and a.code=p_code LIMIT 1);

	return (v_codeName);

END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for getMoney
-- ----------------------------
DROP FUNCTION IF EXISTS `getMoney`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `getMoney`(`p_inMoneyType` varchar(255),`p_inMoney` double,`p_outMoneyType` varchar(255)) RETURNS double
BEGIN
	-- 统一转换为本币，然后转换为目标币
  DECLARE v_outMoney,v_localMoney,v_rate DOUBLE;
  DECLARE v_localMoneyType VARCHAR(255);
  
  -- 系统中配置的本币
  set v_localMoneyType = (select sysvalue from d_var where syskey='localmoney' LIMIT 1);
  
  -- 输入币种与本币的最新汇率
  set v_rate = (select a.rate from d_rate a where a.localmoney = v_localMoneyType and a.foreignMoney = p_inMoneyType ORDER BY a.startdatetime desc limit 1);
  -- 输入币种换算成本币的价值
  set v_localMoney = p_inMoney / v_rate;

  -- 得到本币与输出币种之间的最新汇率
  set v_rate = (select a.rate from d_rate a where a.localmoney = v_localMoneyType and a.foreignMoney = p_outMoneyType ORDER BY a.startdatetime desc limit 1);
  -- 将本币换算成输出币种的价值
  set v_outMoney = v_localMoney * v_rate;

	RETURN (v_outMoney);
END
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
         insert into b_cat(catno,      catname,      cattype,       batchno,      seq,       total      ,rtype      ,productdate,       producer,       catFrom  ,    expiredate      ,price      ,priceunit       ,localprice      ,dealer , makedate  ,modifydate       ,machinename)
                values(new.catno,new.catname,new.cattype,new.batchno,new.seq,new.num,new.rtype,new.productdate,new.producer,new.catFrom,new.expiredate,new.price,new.priceunit,new.localprice,new.dealer, sysdate()   ,sysdate()    ,new.machinename);
     end if;
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_In_Update`;
DELIMITER ;;
CREATE TRIGGER `tg_In_Update` AFTER UPDATE ON `b_in` FOR EACH ROW begin
   declare cnt int;
   -- 判断库存表中原来是否有该试剂耗材
   set cnt=(select count(id) from b_cat a where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price);
   -- 如果修改的不是业务主键，目前只能走此分支，界面已经校验业务主键不能修改
   if old.catno = new.catno and old.batchno=new.batchno and old.price=new.price then
       update b_cat a set a.total = (a.total-old.num+new.num),a.modifydate=sysdate() where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price;
      UPDATE b_cat a
        SET 
              a.cattype = new.cattype,
              a.rtype = new.rtype,
              a.productdate = new.productdate,
              a.seq=new.seq,
              a.producer = new.producer,
              a.expiredate = new.expiredate,              
              a.priceunit = new.priceunit,              
              a.dealer = new.dealer
              WHERE
	a.catno = new.catno
             AND a.batchno = new.batchno
             AND a.price = new.price;
   else 
       -- 业务主键修改，先恢复修改前试剂的库存数量
       update b_cat a set a.total = a.total-old.num,a.modifydate=sysdate() where a.catno=old.catno and a.batchno=old.batchno and a.price=old.price;
       if cnt > 0 then
      -- 新试剂库存中存在，则更新新试剂的库存数量
           update b_cat a set a.total=a.total+new.num where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price;
     
       else
           insert into b_cat(catno,         catname,      cattype,        batchno,  total,    rtype,    productdate,    producer,    expiredate,    price,    priceunit,    localprice,        dealer,makedate,modifydate)
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
     update b_cat set total = (v_total-new.num),modifydate=sysdate() ,machineName =new.machineName,machineNo=new.machineNo where catno=new.catno and batchno=new.batchno and price=new.price;
     
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
      -- 同时修改所属设备和编号  
      update b_cat a set a.total = (a.total+old.num-new.num),a.modifydate=sysdate(),machineName =new.machineName,machineNo=new.machineNo where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price;
   else
       --  修改前的出库数据归库 同时修改所属设备和编号
       update b_cat a set a.total = a.total+old.num,a.modifydate=sysdate(),machineName =new.machineName,machineNo=new.machineNo where a.catno=old.catno and a.batchno=old.batchno and a.price=old.price;
      --  修改后的业务主键存在于库存中，则更新库存，不存在则出库数量为0
       if cnt > 0 then
           if v_total < new.num then
               set new.num = v_total; -- 超出库存，设定为库存
           end if;
          --  同时修改所属设备和编号
           update b_cat a set a.total=a.total-new.num,a.modifydate=sysdate(),machineName =new.machineName,machineNo=new.machineNo where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price;
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
