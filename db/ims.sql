/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50510
Source Host           : localhost:3306
Source Database       : ims

Target Server Type    : MYSQL
Target Server Version : 50510
File Encoding         : 65001

Date: 2014-08-14 09:10:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_code
-- ----------------------------
DROP TABLE IF EXISTS `t_code`;
CREATE TABLE `t_code` (
  `codetype` varchar(20) NOT NULL,
  `code` varchar(10) NOT NULL,
  `codename` varchar(50) DEFAULT NULL,
  `codealias` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`codetype`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` varchar(20) DEFAULT NULL,
  `name` varchar(100) NOT NULL DEFAULT '',
  `nickname` varchar(100) DEFAULT NULL,
  `password` varchar(100) NOT NULL,
  `role` int(2) DEFAULT NULL,
  `sex` int(2) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
