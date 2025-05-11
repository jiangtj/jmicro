package com.jiangtj.micro.sql.jooq.dao;

import org.jooq.TableField;
import org.jooq.TableRecord;

public class RTableLinkDao<R1 extends TableRecord<R1>, T1, T2, R3 extends TableRecord<R3>> extends RNLinkDao<R1, T1, R1, T2, R3> {

    public RTableLinkDao(RTableDao<R1, T1, T2> rTableDao, TableField<R3, T2> linkField) {
        super(rTableDao, rTableDao.getTableField2(), linkField);
    }

}
