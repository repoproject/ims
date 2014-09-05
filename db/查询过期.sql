SELECT
	id,
	catno,
	catname,
	machinename,
	machineno,
	batchno,
	person,
	outdate,
	num,
	remark,
	localprice,
	(localprice * num) subtotal,
	(
		SELECT
			codename
		FROM
			d_code
		WHERE
			CODE = cattype
		AND codetype = 'cattype'
	) cattype
FROM
	b_out
WHERE
	b_out.reason = '2' /*DISCARDÊÇ2*/