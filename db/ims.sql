/*
Navicat MySQL Data Transfer

Source Server         : gq
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ims

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2014-10-13 23:18:13
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `b_cat`
-- ----------------------------
DROP TABLE IF EXISTS `b_cat`;
CREATE TABLE `b_cat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `catid` varchar(255) DEFAULT NULL COMMENT '物理编号，系统生成',
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
  `orde` int(11) DEFAULT '99999' COMMENT '排序号',
  `operator` varchar(50) DEFAULT NULL,
  `makedate` datetime DEFAULT NULL,
  `modifydate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=157 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_cat
-- ----------------------------

-- ----------------------------
-- Table structure for `b_file`
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_file
-- ----------------------------

-- ----------------------------
-- Table structure for `b_in`
-- ----------------------------
DROP TABLE IF EXISTS `b_in`;
CREATE TABLE `b_in` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '货号，试剂号耗材号',
  `catid` varchar(255) DEFAULT NULL COMMENT '物理编号，系统生成',
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
  `machineName` varchar(255) DEFAULT NULL COMMENT '设备编号',
  `rtype` varchar(50) DEFAULT NULL COMMENT 'R分类',
  `catFrom` varchar(50) DEFAULT NULL COMMENT '来源，oversea，local',
  `person` varchar(50) DEFAULT NULL COMMENT '入库人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `operator` varchar(255) DEFAULT NULL COMMENT '当前用户',
  `makedate` datetime DEFAULT NULL COMMENT '产生日期',
  `modifydate` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=147 DEFAULT CHARSET=utf8 COMMENT='试剂表';

-- ----------------------------
-- Records of b_in
-- ----------------------------

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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='设备机器表';

-- ----------------------------
-- Records of b_machine
-- ----------------------------

-- ----------------------------
-- Table structure for `b_out`
-- ----------------------------
DROP TABLE IF EXISTS `b_out`;
CREATE TABLE `b_out` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `catid` varchar(255) DEFAULT NULL COMMENT '物理编号，系统生成',
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
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='出库记录';

-- ----------------------------
-- Records of b_out
-- ----------------------------

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
-- Table structure for `b_report_discard`
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_report_discard
-- ----------------------------

-- ----------------------------
-- Table structure for `b_report_r`
-- ----------------------------
DROP TABLE IF EXISTS `b_report_r`;
CREATE TABLE `b_report_r` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `catid` int(11) DEFAULT NULL,
  `flag` varchar(10) DEFAULT NULL,
  `rType` varchar(10) DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=3109 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_report_r
-- ----------------------------

-- ----------------------------
-- Table structure for `b_report_validation`
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_report_validation
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
) ENGINE=InnoDB AUTO_INCREMENT=970 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of d_catcode
-- ----------------------------
INSERT INTO `d_catcode` VALUES ('715', '11183974216', null, 'ISE Cal Low', '0', '1', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('716', '04880455190', null, 'ISE Internal Standard', '0', '2', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('717', '04880285190', null, 'Cell wash solution / NaOH', '0', '3', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('718', '11970909216', null, 'ALB', '0', '4', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('719', '11876805', null, 'ALT', '0', '5', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('720', '11876848216', null, 'AST', '0', '6', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('721', '11875418216', null, 'CREA Jaff', '0', '7', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('722', '11775685216', null, 'CREA ', '0', '8', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('723', '11876899216', null, 'GLUCOSE HK', '0', '9', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('724', '11557335216', null, 'Saline', '0', '10', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('725', '11822713190', null, 'T-Bil', '0', '11', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('726', '11929917', null, 'TP', '0', '12', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('727', '11729691216', null, 'UREA/BUN', '0', '13', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('728', '11489828216', null, 'ISE Compensator', '0', '14', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('729', '11489828216', null, 'ISE Compensator', '0', '15', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('730', '11730347216', null, 'P', '0', '16', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('731', '03121305122', null, 'CFAS PUC', '0', '17', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('732', '03121305122', null, 'CFAS PUC', '0', '18', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('733', '03121305122', null, 'CFAS PUC', '0', '19', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('734', '04714423190', null, 'LDL', '0', '20', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('735', '11555448216', null, 'Hitergent', '0', '21', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('736', '03032639122', null, 'ApoB', '0', '22', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('737', '03002209122', null, 'LDH', '0', '23', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('738', '11555430216', null, 'NaOH-D / Basic wash', '0', '24', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('739', '11555430216', null, 'NaOH-D / Basic wash', '0', '25', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('740', '04880480190', null, 'ISE Diluent', '0', '26', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('741', '10820652216', null, 'ISE Ref.', '0', '27', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('742', '11183982216', null, 'ISE Cal High', '0', '28', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('743', '11730240216', null, 'Calcium', '0', '29', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('744', '04880307190', null, 'Acid Wash (2L)', '0', '30', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('745', '04880307190', null, 'Acid Wash (2L)', '0', '31', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('746', '11355279216', null, 'C.f.a.s. Proteins', '0', '32', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('747', '11355279216', null, 'C.f.a.s. Proteins', '0', '33', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('748', '12173107122', null, 'ALP', '0', '34', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('749', '11730711216', null, 'TG', '0', '35', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('750', '12132672216', null, 'CK', '0', '36', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('751', '12016958216', null, 'GGT', '0', '37', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('752', '11875426216', null, 'Uric Acid', '0', '38', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('753', '11877801190', null, 'U/CSF Protein', '0', '39', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('754', '11491458216', null, 'CHOL', '0', '40', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('755', '11491458216', null, 'CHOL', '0', '41', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('756', '11551353216', null, 'Mg', '0', '42', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('757', '20751995190', null, 'AMM', '0', '43', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('758', '11555421216', null, 'SMS/Acid Wash for MOD (12x66mL)', '0', '44', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('759', '11821792216', null, 'Lipase', '0', '45', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('760', '03289885190', null, 'CO2', '0', '46', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('761', '03289885190', null, 'CO2', '0', '47', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('762', '04713214190 ', null, 'HDL-C Plus (Gen. 3)   ', '0', '48', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('763', '11876473316', null, 'AMY', '0', '49', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('764', '11876562316', null, 'Amylase Pancreatic 917/P ', '0', '50', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('765', '03576116190', null, 'Tina-quant Alpha1-Microglobulin', '0', '51', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('766', '03121291122', null, 'Precipath PUC', '0', '52', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('767', '11876996216', null, 'Fe', '0', '53', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('768', '11557599316', null, 'Tina-quant Alpha-1-antitrypsin', '0', '54', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('769', '03555941190', null, 'CFAS PAC', '0', '55', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('770', '03555941190', null, 'CFAS PAC', '0', '56', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('771', '11660519316', null, 'Prealbumin', '0', '57', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('772', '11660390216', null, 'Lp (a)', '0', '58', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('773', '11660390216', null, 'Lp (a)', '0', '59', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('774', '03032612122', null, 'Apo A1', '0', '60', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('775', '03015084122', null, 'Transferrin', '0', '61', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('776', '10759350360', null, 'CFAS core, lot 16652301', '0', '62', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('777', '12172623160', null, 'CFAS Lipids, lot 16651901', '0', '63', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('778', '12172623160', null, 'CFAS Lipids, lot 16651901', '0', '64', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('779', '12172623160', null, 'CFAS Lipids', '0', '65', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('780', '04975774190', null, 'Cystatin C', '0', '66', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('781', '04975774190', null, 'Cystatin C', '0', '67', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('782', '04975901190', null, 'Cystatin C calibrator', '0', '68', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('783', '04975901190', null, 'Cystatin C calibrator', '0', '69', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('784', '04975901190', null, 'Cystatin C calibrator', '0', '70', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('785', '04975936190', null, 'Cystatin C control', '0', '71', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('786', '03121313122', null, 'Precinorm PUC', '0', '72', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('787', '11972855216', null, 'hs-CRP', '0', '73', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('788', '11098985122', null, 'Precinorm Fructosamine Control 3*1mL', '0', '74', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('789', '11174118122', null, 'Precipath Fructosamine Control 3*1mL', '0', '75', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('790', '11930010216', null, 'Fructosamine 408 tests', '0', '76', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('791', '11098993122', null, 'Precimat Fructosamine Calibrator 3*1mL', '0', '77', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('792', 'LK016.H', null, 'Roche Hitachi 911/912/917/P Modular Freelite Kappa Kit, 2*50 tests', '0', '78', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('793', 'LK018.H', null, 'Roche Hitachi 911/912/917/P Modular Freelite Lambda Kit, 2*50 tests', '0', '79', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('794', 'LK016.H', null, 'Roche Hitachi 911/912/917/P Modular Freelite Kappa Kit, 2*50 tests', '0', '80', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('795', 'LK018.H', null, 'Roche Hitachi 911/912/917/P Modular Freelite Lambda Kit, 2*50 tests', '0', '81', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('796', 'LK016.H', null, 'Roche Hitachi 911/912/917/P Modular Freelite Kappa Kit, 2*50 tests', '0', '82', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('797', 'LK018.H', null, 'Roche Hitachi 911/912/917/P Modular Freelite Lambda Kit, 2*50 tests', '0', '83', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('798', '279-75401', null, 'NEFA C reagent', '0', '84', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('799', '12148331122', null, 'Preciset sTFR ', '0', '85', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('800', '12148315122', null, 'sTFR', '0', '86', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('801', '12148340122', null, 'sTFR  control   set', '0', '87', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('802', '11776312190', null, 'Alcohol', '0', '88', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('803', '12172828322', null, 'Preciset RF', '0', '89', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('804', '12172828322', null, 'Preciset RF', '0', '90', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('805', ',03004953122', null, ' RF Ⅱ', '0', '91', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('806', '11557629316', null, 'Haptoglobin', '0', '92', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('807', '11557629316', null, 'Haptoglobin', '0', '93', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('808', '2075880912', null, 'Acetaminophen Calibrator', '0', '94', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('809', '03255379190', null, 'Acetaminophen UNIV', '0', '95', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('810', '03255379190', null, 'Acetaminophen UNIV', '0', '96', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('811', '04521536190', null, 'TDM Control Set', '0', '97', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('812', '04999622190', null, 'Tina-quant albumin', '0', '98', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('813', '04999622190', null, 'Tina-quant albumin', '0', '99', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('814', '11660497216', null, 'Cerulopasmin', '0', '100', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('815', '11298500316', null, 'ISE Cleaning Solution', '0', '101', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('816', '12146401216   ', null, 'Iron Standard (Fe Standard) ', '0', '102', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('817', '', null, 'Diluent TCH  level -1 1.3ml ', '0', '103', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('818', '2210-02', null, 'total Protein  Assay System', '0', '104', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('819', '10641600001', null, 'Halogen Lamp', '0', '105', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('820', '11660551216', null, 'β2-M icroglobulin', '0', '106', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('821', '04956885190', null, 'C-reactive   Protein', '0', '107', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('822', '04956885190', null, 'C-reactive   Protein', '0', '108', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('823', '04956885190', null, 'C-reactive   Protein', '0', '109', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('824', '04956885190', null, 'C-reactive   Protein', '0', '110', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('825', '03507246190', null, 'Ig A', '0', '111', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('826', '03507378190', null, 'Ig G', '0', '112', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('827', '03507041190', null, 'Ig M', '0', '113', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('828', '121349443122', null, 'precipath U  plus', '0', '114', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('829', '12149435122', null, 'Precinorm U Plus', '0', '115', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('830', '12146398216 ', null, 'UIBC 917/P  ', '0', '116', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('831', '12217716001', null, 'D-Bil', '0', '117', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('832', '05589037190', null, 'Bilirubin  Direct', '0', '118', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('833', '1290008', null, 'NAG/Creatinine High control', '0', '119', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('834', '1290009', null, 'NAG/Creatinine Medium control', '0', '120', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('835', '1290010', null, 'NAG/Creatinine Low control', '0', '121', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('836', '1290008', null, 'NAG/Creatinine High control', '0', '122', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('837', '1290009', null, 'NAG/Creatinine Medium control', '0', '123', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('838', '1290010', null, 'NAG/Creatinine Low control', '0', '124', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('839', 'QQ010205', null, 'Ra a  Hu Alpha-2 -Macrolobulin', '0', '125', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('840', 'S2005', null, 'Buffer 1', '0', '126', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('841', 'S200630', null, 'Buffer 3', '0', '127', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('842', 'x090801', null, 'human serum protein calibration ', '0', '128', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('843', 'x093901', null, 'human serum protein  low control ', '0', '129', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('844', 'x0940', null, 'human serum protein  high control ', '0', '130', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('845', '11875051216', null, 'Tina-quant C4c', '0', '131', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('846', '11875078216', null, 'Tina-quant C3c', '0', '132', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('847', '04498666190', null, 'Cholinesterase-1160 test', '0', '133', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('848', '04498666190', null, 'Cholinesterase-1160 test', '0', '134', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('849', '04498666190', null, 'CHE2', '0', '135', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('850', '05061458190', null, 'CA2   R2', '0', '136', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('851', '05061431190', null, 'CA2   R1', '0', '137', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('852', '05061458190', null, 'CA2   R2', '0', '138', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('853', '05061431190', null, 'CA2   R1', '0', '139', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('854', '05061458190', null, 'CA2   R2', '0', '140', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('855', '05061431190', null, 'CA2   R1', '0', '141', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('856', '05061458190', null, 'CA2   R2', '0', '142', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('857', '05061431190', null, 'CA2   R1', '0', '143', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('858', '', null, 'shipping cost for CA2', '0', '144', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('859', '', null, 'NGSP Certification Qc Material20ml', '0', '145', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('860', '', null, 'NGSP QuarterlyMonitoring Qc Material 40ml', '0', '146', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('861', '466-26201', null, 'NEFA C Control SERA I', '0', '147', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('862', '462-26301', null, 'NEFA C Control SERA II', '0', '148', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('863', '10825468001', null, 'Sodium Electrode', '0', '149', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('864', '10825441001', null, 'potassium Electrode', '0', '150', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('865', '03149501001', null, 'reference  Eliectrode', '0', '151', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('866', '03246353001', null, 'chloride Electrode', '0', '152', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('867', '', null, 'Solomon  Park  Calibrator', '0', '153', null, null, 'Modular Reagent');
INSERT INTO `d_catcode` VALUES ('868', '11800507001', null, 'Clean- liner  Elessys', '0', '154', null, null, 'Modular Reagent');

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
INSERT INTO `d_var` VALUES ('reportname', 'Inventory listing {yyyy-MM}.xls', '报表文件名称');
INSERT INTO `d_var` VALUES ('reportpath', '\\reports\\', '报表保存路径');
INSERT INTO `d_var` VALUES ('taskserverip', '127.0.0.1', '任务服务器，多台服务器集群使用');

-- ----------------------------
-- View structure for `r_in_view`
-- ----------------------------
DROP VIEW IF EXISTS `r_in_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `r_in_view` AS select `b`.`id` AS `catid`,(case when (`a`.`reason` = '0') then `a`.`num` else 0 end) AS `inVendor`,(case when (`a`.`reason` = '1') then `a`.`num` else 0 end) AS `inInterlab`,(case when (`a`.`reason` = '2') then `a`.`num` else 0 end) AS `inSponsor`,(case when (`a`.`reason` = '3') then `a`.`num` else 0 end) AS `inCharges`,`a`.`inDate` AS `indate` from (`b_in` `a` join `b_cat` `b`) where ((`a`.`catno` = `b`.`catno`) and (`a`.`batchNo` = `b`.`batchno`) and (`a`.`price` = `b`.`price`)) ;

-- ----------------------------
-- View structure for `r_out_view`
-- ----------------------------
DROP VIEW IF EXISTS `r_out_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `r_out_view` AS select `b`.`id` AS `catid`,(case when (`a`.`reason` = '0') then `a`.`num` else 0 end) AS `outTrialTest`,(case when (`a`.`reason` = '1') then `a`.`num` else 0 end) AS `outValidation`,(case when (`a`.`reason` = '2') then `a`.`num` else 0 end) AS `outDiscard`,(case when (`a`.`reason` = '3') then `a`.`num` else 0 end) AS `outIntelLab`,(case when (`a`.`reason` = '4') then `a`.`num` else 0 end) AS `outSponsor`,(case when (`a`.`reason` = '5') then `a`.`num` else 0 end) AS `outOther`,`a`.`outDate` AS `outdate` from (`b_out` `a` join `b_cat` `b`) where ((`a`.`catno` = `b`.`catno`) and (`a`.`batchno` = `b`.`batchno`) and (`a`.`price` = `b`.`price`)) ;

-- ----------------------------
-- View structure for `r_price_view`
-- ----------------------------
DROP VIEW IF EXISTS `r_price_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `r_price_view` AS select `a`.`id` AS `catid`,(case when (`a`.`priceUnit` = '0') then `a`.`price` end) AS `CNY`,(case when (`a`.`priceUnit` = '1') then `a`.`price` end) AS `USD`,(case when (`a`.`priceUnit` = '2') then `a`.`price` end) AS `SGD`,(case when (`a`.`priceUnit` = '3') then `a`.`price` end) AS `EUR`,(case when (`a`.`priceUnit` = '4') then `a`.`price` end) AS `GBP`,(select round((`a`.`price` / `b`.`rate`),2) from `d_rate` `b` where (`b`.`foreignMoney` = `a`.`priceUnit`) order by `b`.`startDateTime` desc limit 1) AS `localPrice` from `b_cat` `a` ;

-- ----------------------------
-- View structure for `r_qty_view`
-- ----------------------------
DROP VIEW IF EXISTS `r_qty_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `r_qty_view` AS select `r`.`id` AS `id`,`r`.`catid` AS `catid`,`r`.`flag` AS `flag`,(case when (month(now()) = 1) then 0 else round(ifnull(`r`.`cqtyIn`,0),2) end) AS `asQtyIn`,(case when (month(now()) = 1) then 0 else round(ifnull(`r`.`cqtyOut`,0),2) end) AS `asQtyOut` from `b_report_r` `r` where (`r`.`flag` = date_format((now() + interval -(1) month),'%Y%m')) ;

-- ----------------------------
-- Function structure for `getCatId`
-- ----------------------------
DROP FUNCTION IF EXISTS `getCatId`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `getCatId`(`p_catNo` varchar(255), p_batchNo varchar(255),p_price decimal,p_priceUnit varchar(255)) RETURNS varchar(255) CHARSET utf8
BEGIN
	#Routine body goes here...
    DECLARE v_catId,v_price,v_priceUnit VARCHAR(255);
 
    -- catno
    set p_catNo = REPLACE(IFNULL(p_catNo,'N/A'),' ','');
    IF p_catNo = '' or LOWER(p_catNo) = 'null' THEN
        set p_catNo = 'N/A';
    end IF;

    -- batchno
    set p_batchNo = REPLACE(IFNULL(p_batchNo,'N/A'),' ','');
    IF p_batchNo = '' or LOWER(p_batchNo) = 'null' THEN
        set p_batchNo = 'N/A';
    end IF;

    -- priceunit
    set v_priceUnit = getCodeName('money',p_priceUnit);
    IF v_priceUnit = '' or LOWER(v_priceUnit) = 'null' or ISNULL(v_priceUnit) THEN
        set v_priceUnit = 'N/A';
    end IF;

    -- price
    IF p_price = 0 or ISNULL(p_price) THEN
        set v_price = 'N/A';
    ELSE
        set v_price = CAST(ROUND(p_price*100) AS CHAR);
    end IF;
    
    set v_catId = CONCAT(p_catNo,'-',p_batchNo,'-',v_priceUnit,v_price);
	  RETURN v_catId;
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for `getCodeName`
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
-- Function structure for `getMoney`
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
CREATE TRIGGER `tg_In_Insert` BEFORE INSERT ON `b_in` FOR EACH ROW begin
     declare cnt int;
     set new.catid = getCatId(new.catno,new.batchno,new.price,new.priceunit);
     set cnt=(select count(id) from b_cat a where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price);
     if cnt > 0 then
         update b_cat set total = (total+new.num) where catno=catno and batchno=batchno and price=price;
     else
         insert into b_cat(catid,catno,      catname,      cattype,       batchno,      seq,       total      ,rtype      ,productdate,       producer,       catFrom  ,    expiredate      ,price      ,priceunit       ,localprice      ,dealer , makedate  ,modifydate       ,machinename)
                values(new.catid,new.catno,new.catname,new.cattype,new.batchno,new.seq,new.num,new.rtype,new.productdate,new.producer,new.catFrom,new.expiredate,new.price,new.priceunit,new.localprice,new.dealer, sysdate()   ,sysdate()    ,new.machinename);
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
     set new.catid = getCatId(new.catno,new.batchno,new.price,new.priceunit);
     set v_total=(select total from b_cat a where a.catid=new.catid);
     if v_total < new.num then
          set new.num = v_total;
     end if;
     update b_cat set total = (v_total-new.num),modifydate=sysdate() ,machineName =new.machineName,machineNo=new.machineNo where catid=new.catid;
     
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
