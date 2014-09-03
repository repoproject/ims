SELECT
	a.catno,
	a.catname,
	a.batchno,
	a.machineno,
	a.machineName,
	a.price,
  0 opening,/*º∆À„*/
  sum(a.inVendor),
  sum(a.inInterlab),
  sum(a.inSponsor),
  sum(a.inCharges),
  sum(a.intotal),
  sum(outTrialTest),
sum(outValidation),
sum(outDiscard),
sum(outIntelLab),
sum(outSponsor),
sum(outOthre),
sum(outTotal),
a.closing,
a.localprice,
a.totalAmount
FROM
	r_view a
WHERE
	(
		a.indate IS NULL
		OR a.indate BETWEEN DATE('2014-07-26')
		AND DATE('2014-08-25')
	)
AND (
	a.outdate IS NULL
	OR a.outdate BETWEEN DATE('2014-07-26')
	AND DATE('2014-08-25')
)
GROUP BY
	a.catno,
	a.catname,
	a.batchno,
	a.price,a.machineno,a.machineName;