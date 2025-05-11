package com.jiangtj.micro.sql.jooq.dao.i;

import com.jiangtj.micro.sql.jooq.dao.RTableLinkDao;
import org.jooq.TableField;
import org.jooq.TableRecord;

public class RTableLinkIDao<R1 extends TableRecord<R1>, R2 extends TableRecord<R2>> extends RTableLinkDao<R1, Integer, Integer, R2> {

    public RTableLinkIDao(RTableIDao<R1> rTableDao, TableField<R2, Integer> linkField) {
        super(rTableDao, linkField);
    }

}
