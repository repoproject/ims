SELECT a.id,a.catno,a.catname,a.batchno,a.machineNo,a.machineName,ifnull(a.total,0)+ifnull(o.outTotal,0)-ifnull(i.inTotal,0) opening,
ifnull(i.inVendor,0),ifnull(i.inCharges,0),ifnull(i.inInterlab,0),ifnull(i.inSponsor,0), ifnull(i.inTotal,0),
ifnull(o.outDiscard,0),ifnull(o.outIntelLab,0),ifnull(o.outOthre,0),ifnull(o.outSponsor,0),ifnull(o.outTrialTest,0),ifnull(o.outValidation,0),ifnull(o.outTotal,0),ifnull(a.total,0) closing,
p.CNY,p.EUR,p.GBP,p.SGD,p.USD,p.localprice,a.total*p.localprice,null as remark,
i.inTotal*p.localprice qtyIn,o.outTotal*p.localprice qtyOut,
0 checkQty,0 checkAmt,
0 pqtyIn,0 pqtyOut,
0 cqtyIn,0 cqtyOut 
from b_cat a LEFT JOIN
(
		SELECT
			ifnull(vin.catid, 0) catid,
			SUM(vin.inVendor) inVendor,
			SUM(vin.inCharges) inCharges,
			SUM(vin.inInterlab) inInterlab,
			SUM(vin.inSponsor) inSponsor,
      SUM(vin.inVendor) + SUM(vin.inCharges) + SUM(vin.inInterlab) + SUM(vin.inSponsor) inTotal
		FROM
			r_in_view vin
		WHERE
			vin.indate BETWEEN DATE('2014-01-01')
		AND DATE('2015-01-01')
		GROUP BY
			vin.catid WITH ROLLUP
)i
on a.id = i.catid
LEFT JOIN
(
		SELECT
			ifnull(o.catid, 0) catid,
			SUM(o.outDiscard) outDiscard,
			SUM(o.outIntelLab) outIntelLab,
			SUM(o.outOthre) outOthre,
			SUM(o.outSponsor) outSponsor,
			SUM(o.outTrialTest)outTrialTest ,
			SUM(o.outValidation) outValidation,
      SUM(o.outDiscard) + SUM(o.outIntelLab)+SUM(o.outOthre)+SUM(o.outSponsor)+SUM(o.outTrialTest)+SUM(o.outValidation) outTotal
		FROM
			r_out_view o
		WHERE
			o.outdate BETWEEN DATE('2014-01-01')
		AND DATE('2015-01-01')
		GROUP BY
			o.catid WITH ROLLUP
)o on a.id=o.catid 
LEFT JOIN r_price_view p ON a.id=p.catid
