/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50510
Source Host           : localhost:3306
Source Database       : ims

Target Server Type    : MYSQL
Target Server Version : 50510
File Encoding         : 65001

Date: 2014-08-27 22:00:33
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
  `cattype` varchar(50) DEFAULT NULL COMMENT '0-试剂,1-耗材',
  `batchno` varchar(50) NOT NULL COMMENT '批号',
  `total` int(11) unsigned zerofill DEFAULT NULL COMMENT '总数',
  `rType` varchar(50) DEFAULT NULL COMMENT '分组，按照R特性分组',
  `productDate` datetime DEFAULT NULL COMMENT '生产日期',
  `producer` varchar(255) DEFAULT NULL COMMENT '生产商',
  `expiredate` datetime DEFAULT NULL COMMENT '有效期，过期日',
  `price` double NOT NULL,
  `priceUnit` varchar(50) DEFAULT NULL,
  `dealer` varchar(255) DEFAULT NULL,
  `makedate` datetime DEFAULT NULL,
  `modifydate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_CAT_NO` (`catno`) USING BTREE COMMENT '不用作为主键，但是作为唯一索引标识不能重复'
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of b_cat
-- ----------------------------
INSERT INTO `b_cat` VALUES ('5', 'a', 'a', '0', 'a', '00000000004', '1', '2014-08-01 00:00:00', 'a', '2014-08-31 00:00:00', '10', '0', 'aa', '2014-08-25 21:29:00', '2014-08-25 21:29:00');
INSERT INTO `b_cat` VALUES ('6', 'bb', 'bb', '0', 'bb', '00000000000', '1', '2014-08-01 00:00:00', 'bbbb', '2014-08-31 00:00:00', '111', '0', 'bbb', '2014-08-25 21:29:30', '2014-08-25 21:29:30');

-- ----------------------------
-- Table structure for b_in
-- ----------------------------
DROP TABLE IF EXISTS `b_in`;
CREATE TABLE `b_in` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '货号，试剂号耗材号',
  `catno` varchar(50) NOT NULL,
  `catName` varchar(255) DEFAULT NULL COMMENT '试剂名称，冗余，b_cat.name',
  `batchNo` varchar(50) DEFAULT NULL COMMENT '批号',
  `cattype` varchar(50) DEFAULT NULL,
  `producer` varchar(255) DEFAULT NULL COMMENT '生产商',
  `dealer` varchar(255) DEFAULT NULL COMMENT '经销商',
  `productDate` datetime DEFAULT NULL COMMENT '生产日期',
  `reason` varchar(255) DEFAULT NULL COMMENT '入库原因 codetype=''orderreason''',
  `expiredate` datetime DEFAULT NULL COMMENT '失效日',
  `num` int(11) DEFAULT NULL COMMENT '数量',
  `numUnit` varchar(50) DEFAULT NULL COMMENT '数量单位',
  `price` decimal(10,2) DEFAULT NULL COMMENT '单价，原始单价',
  `priceUnit` varchar(50) DEFAULT NULL COMMENT '货币单位 codetype=''money'',存储codename',
  `localPrice` decimal(10,2) DEFAULT NULL COMMENT '本地货币单价，通过汇率转化',
  `taxRate` decimal(10,2) DEFAULT NULL COMMENT '税率',
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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='试剂表';

-- ----------------------------
-- Records of b_in
-- ----------------------------
INSERT INTO `b_in` VALUES ('15', 'a', 'a', 'a', '0', 'a', 'aa', '2014-08-01 00:00:00', '0', '2014-08-31 00:00:00', '10', null, '10.00', '0', null, '0.00', '2014-08-25 00:00:00', null, '1', '0', 'a', 'aaa', null, null, null);

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
  `machineNo` varchar(50) DEFAULT NULL COMMENT '设备编号',
  `catno` varchar(50) NOT NULL,
  `catname` varchar(255) DEFAULT NULL,
  `batchno` varchar(50) NOT NULL COMMENT '批号',
  `person` varchar(255) DEFAULT NULL COMMENT '出库人',
  `outDate` datetime DEFAULT NULL COMMENT '出库日期',
  `price` decimal(10,2) NOT NULL,
  `priceUnit` varchar(50) DEFAULT NULL,
  `num` int(11) DEFAULT NULL COMMENT '数量',
  `reason` varchar(255) DEFAULT NULL COMMENT '出库原因，codetype=''userreason''',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `operator` varchar(255) DEFAULT NULL COMMENT '操作者',
  `makedate` datetime DEFAULT NULL COMMENT '创建日期',
  `modifydate` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='出库记录';

-- ----------------------------
-- Records of b_out
-- ----------------------------
INSERT INTO `b_out` VALUES ('3', 'aa', 'aa', 'a', 'a', 'a', 'aa', '2014-08-25 00:00:00', '10.00', '0', '5', '0', 'aa', '001', '2014-08-25 22:20:54', '2014-08-25 22:20:54');
INSERT INTO `b_out` VALUES ('4', 'bbb', 'bbb', 'a', 'a', 'a', 'aaa', '2014-08-26 00:00:00', '10.00', '0', '1', '0', 'aaa', '001', '2014-08-26 16:21:28', '2014-08-26 16:21:28');

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
INSERT INTO `b_var` VALUES ('newinout', '3', '最近X天的出入库记录');
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
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of d_catcode
-- ----------------------------
INSERT INTO `d_catcode` VALUES ('1', '123', '1', '1', '1', '888', '1', null, '1');
INSERT INTO `d_catcode` VALUES ('3', '789', '33', '耗材1', '1', '33', '33', null, null);
INSERT INTO `d_catcode` VALUES ('4', '888', '88', '试剂3', '0', '44', '44', null, '二哥');
INSERT INTO `d_catcode` VALUES ('6', '飞', '飞', '飞 方法', '0', '444444', '', null, '123二恶');
INSERT INTO `d_catcode` VALUES ('7', '61', '651', '651', '0', '77777', '65161616', null, '问问');
INSERT INTO `d_catcode` VALUES ('8', '00', '00', '00', '0', '1000', '00', null, '123二恶');
INSERT INTO `d_catcode` VALUES ('9', '444', '666', '555', '1', '0', '777', null, '123二恶');
INSERT INTO `d_catcode` VALUES ('10', '999', '999', '999', '1', '0', '999', null, '123二恶');
INSERT INTO `d_catcode` VALUES ('11', '88', '88-', '88-', '0', '6', '88-', null, '深V深V');
INSERT INTO `d_catcode` VALUES ('12', '22', '222', '222', '0', '2222', '', null, '123二恶');
INSERT INTO `d_catcode` VALUES ('14', 'd', 'd', 'd', '0', '0', '', null, '123二恶');
INSERT INTO `d_catcode` VALUES ('15', '4', '4', '4', '0', '5', '', null, '123二恶');
INSERT INTO `d_catcode` VALUES ('16', '55', '55ff', '55', '0', '0', '55', null, 'wewev');
INSERT INTO `d_catcode` VALUES ('17', '3434', '', '343434', '0', '0', '', null, '123二恶');
INSERT INTO `d_catcode` VALUES ('18', '222', '', '222', '0', '-22', '', null, '123二恶');
INSERT INTO `d_catcode` VALUES ('19', '2323', '', '2323', '0', '11', '', null, '123二恶');
INSERT INTO `d_catcode` VALUES ('20', '3434', '', '3434', '0', '0', '', null, '123二恶');
INSERT INTO `d_catcode` VALUES ('21', '太挑剔', '他', '他', '0', '77', '他', null, '问问');
INSERT INTO `d_catcode` VALUES ('22', '222', '222', '222', '0', '222', '222', null, '123二恶');

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
INSERT INTO `d_user` VALUES ('1', 'a11111', 'name', '1', '1', '1', '1', '1', null, 'dddd', null);
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
DROP TRIGGER IF EXISTS `tg_In_Insert`;
DELIMITER ;;
CREATE TRIGGER `tg_In_Insert` AFTER INSERT ON `b_in` FOR EACH ROW begin
     declare cnt int;
     set cnt=(select count(id) from b_cat a where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price);
     if cnt > 0 then
         update b_cat set total = (total+new.num) where catno=catno and batchno=batchno and price=price;
     else
         insert into b_cat(catno,catname,cattype,batchno,total,rtype,productdate,producer,expiredate,price,priceunit,dealer,makedate,modifydate)
          values(new.catno,new.catname,new.cattype,new.batchno,new.num,new.rtype,new.productdate,new.producer,new.expiredate,new.price,new.priceunit,new.dealer,sysdate(),sysdate());
     end if;
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_In_Update`;
DELIMITER ;;
CREATE TRIGGER `tg_In_Update` AFTER UPDATE ON `b_in` FOR EACH ROW begin
   declare cnt int;
   set cnt=(select count(id) from b_cat a where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price);

   if old.catno = new.catno and old.batchno=new.batchno and old.price=new.price then
       update b_cat a set a.total = (a.total-old.num+new.num) where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price;
   else
       update b_cat a set a.total = a.total-old.num where a.catno=old.catno and a.batchno=old.batchno and a.price=old.price;
       if cnt > 0 then
           update b_cat a set a.total=a.total+new.num where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price;
       else
           insert into b_cat(catno,catname,cattype,batchno,total,rtype,prodectdate,producer,expiredate,price,priceunit,dealer,makedate,modifydate)
          values(new.catno,new.catname,new.cattype,new.batchno,new.num,new.rtype,new.productdate,new.producer,new.expiredate,new.price,new.priceunit,sysdate(),sysdate());
       end if;
   end if;
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_In_Delete`;
DELIMITER ;;
CREATE TRIGGER `tg_In_Delete` AFTER DELETE ON `b_in` FOR EACH ROW begin
     declare v_total int;
     set v_total=(select total-old.num from b_cat a where a.catno=old.catno and a.batchno=old.batchno and a.price=old.price);
     if v_total < 0 then
          set v_total = 0;
     end if;
     update b_cat set total = v_total where catno=old.catno and batchno=old.batchno and price=old.price;
     
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_Out_Insert`;
DELIMITER ;;
CREATE TRIGGER `tg_Out_Insert` AFTER INSERT ON `b_out` FOR EACH ROW begin
     declare v_total int;
     set v_total=(select total-new.num from b_cat a where a.catno=new.catno and a.batchno=new.batchno and a.price=new.price);
     if v_total < 0 then
          set v_total = 0;
     end if;
     update b_cat set total = v_total where catno=new.catno and batchno=new.batchno and price=new.price;
     
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_Out_Update`;
DELIMITER ;;
CREATE TRIGGER `tg_Out_Update` AFTER UPDATE ON `b_out` FOR EACH ROW begin
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_Out_Delete`;
DELIMITER ;;
CREATE TRIGGER `tg_Out_Delete` AFTER DELETE ON `b_out` FOR EACH ROW begin
     update b_cat set total = total+old.num where catno=old.catno and batchno=old.batchno and price=old.price;   
end
;;
DELIMITER ;
