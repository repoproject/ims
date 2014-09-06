-- ----------------------------
-- View structure for r_in_view
-- ----------------------------
DROP VIEW IF EXISTS `r_in_view`;
CREATE  VIEW `r_in_view` AS SELECT
  b.id,
	a.catno,
	a.catname,
  case when a.reason = '0' then a.num else 0 end inVendor,
  case when a.reason = '1' then a.num else 0 end inInterlab,
  case when a.reason = '2' then a.num else 0 end inSponsor,
  case when a.reason = '3' then a.num else 0 end inCharges,
	a.indate
FROM
	b_in a,b_cat b
WHERE a.catno = b.catno and a.batchno = b.batchno and a.price = b.price ; ;

-- ----------------------------
-- View structure for r_out_view
-- ----------------------------
DROP VIEW IF EXISTS `r_out_view`;
CREATE  VIEW `r_out_view` AS SELECT
  b.id,
	a.catno,
	a.catname,
  a.price,
  case when a.reason = '0' then a.num else 0 end outTrialTest,
  case when a.reason = '1' then a.num else 0 end outValidation,
  case when a.reason = '2' then a.num else 0 end outDiscard,
  case when a.reason = '3' then a.num else 0 end outIntelLab,
  case when a.reason = '4' then a.num else 0 end outSponsor,
  case when a.reason = '5' then a.num else 0 end outOthre,
	a.outdate
FROM
	b_out a,b_cat b
WHERE a.catno = b.catno and a.batchno = b.batchno and a.price = b.price ;

-- ----------------------------
-- View structure for r_view
-- ----------------------------
DROP VIEW IF EXISTS `r_view`;
CREATE  VIEW `r_view` AS SELECT
	a.id,
	a.catno,
	a.catname,
	a.batchno,
	a.price,
	a.priceUnit,
	a.cattype,
	a.machineno,
	a.machineName,
  i.inVendor,
  i.inInterlab,
  i.inSponsor,
  i.inCharges,
  i.inVendor + i.inInterlab + i.inSponsor +i.inCharges inTotal,
  o.outTrialTest,
  o.outValidation,
  o.outDiscard,
  o.outIntelLab,
  o.outSponsor,
  o.outOthre,
  o.outTrialTest+o.outValidation+o.outDiscard+o.outIntelLab+o.outSponsor+o.outOthre outTotal,
  a.total closing,
   case when a.priceUnit='0' then a.price end CNY, 
   case when a.priceUnit='1' then a.price end USD, 
   case when a.priceUnit='2' then a.price end SGD, 
   case when a.priceUnit='3' then a.price end EUR, 
   case when a.priceUnit='4' then a.price end GBP,
  a.localprice,
  a.total * a.localprice totalAmount,
  i.indate,
  o.outdate
FROM
	b_cat a LEFT JOIN r_in_view i ON a.id = i.id
LEFT JOIN r_out_view o ON a.id=o.id ;