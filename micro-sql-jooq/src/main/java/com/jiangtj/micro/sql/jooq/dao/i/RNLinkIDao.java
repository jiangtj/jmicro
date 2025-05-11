package com.jiangtj.micro.sql.jooq.dao.i;

import com.jiangtj.micro.sql.jooq.dao.AbstractRNDao;
import com.jiangtj.micro.sql.jooq.dao.RNLinkDao;
import org.jooq.TableField;
import org.jooq.TableRecord;

public class RNLinkIDao<R1 extends TableRecord<R1>, R2 extends TableRecord<R2>, R3 extends TableRecord<R3>>
    extends RNLinkDao<R1, Integer, R2, Integer, R3> {

    public RNLinkIDao(AbstractRNDao<R1, Integer, R2> rnDao, TableField<R2, Integer> tableField1, TableField<R3, Integer> tableField2) {
        super(rnDao, tableField1, tableField2);
    }
}
