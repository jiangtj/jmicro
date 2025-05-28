package com.jiangtj.micro.sql.jooq.dao.l;

import com.jiangtj.micro.sql.jooq.dao.RTableLinkDao;
import org.jooq.TableField;
import org.jooq.TableRecord;

public class RTableLinkLDao<R1 extends TableRecord<R1>, R2 extends TableRecord<R2>> extends RTableLinkDao<R1, Long, Long, R2> {

    public RTableLinkLDao(RTableLDao<R1> rTableDao, TableField<R2, Long> linkField) {
        super(rTableDao, linkField);
    }

}
