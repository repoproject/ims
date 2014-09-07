﻿SELECT DISTINCT b_cat.id, b_cat.catno, b_cat.catname, b_cat.batchno, b_cat.makedate, b_cat.makedate AS builtdate, CONCAT( date_format(b_cat.makedate, '%Y-%c-%d'), '新入库', b_in.num, '个', b_cat.batchno, '的', b_cat.catname ) description FROM b_cat, b_var, b_in WHERE DATEDIFF( CURRENT_DATE (), b_cat.makedate ) <= b_var.bizValue AND DATEDIFF( CURRENT_DATE (), b_cat.makedate ) >= 0 AND b_var.bizkey = 'banchTime' AND b_in.catno = b_cat.catno AND b_in.batchNo = b_cat.batchno ORDER BY b_cat.makedate DESC