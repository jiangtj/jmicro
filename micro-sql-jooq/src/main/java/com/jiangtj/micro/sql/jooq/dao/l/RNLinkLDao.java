package com.jiangtj.micro.sql.jooq.dao.l;

import com.jiangtj.micro.sql.jooq.dao.AbstractRNDao;
import com.jiangtj.micro.sql.jooq.dao.RNLinkDao;
import org.jooq.TableField;
import org.jooq.TableRecord;

public class RNLinkLDao<R1 extends TableRecord<R1>, R2 extends TableRecord<R2>, R3 extends TableRecord<R3>>
    extends RNLinkDao<R1, Long, R2, Long, R3> {

    public RNLinkLDao(AbstractRNDao<R1, Long, R2> rnDao, TableField<R2, Long> tableField1, TableField<R3, Long> tableField2) {
        super(rnDao, tableField1, tableField2);
    }
}
