package com.jiangtj.micro.sql.jooq.dao.l;

import com.jiangtj.micro.sql.jooq.dao.RTableDao;
import org.jooq.TableField;
import org.jooq.TableRecord;

public class RTableLDao<R1 extends TableRecord<R1>> extends RTableDao<R1, Long, Long> {

    public RTableLDao(TableField<R1, Long> tableField1, TableField<R1, Long> tableField2, boolean isLogic) {
        super(tableField1, tableField2, isLogic);
    }

    @Override
    public <R2 extends TableRecord<R2>> RTableLinkLDao<R1, R2> createLink(TableField<R2, Long> linkField) {
        return new RTableLinkLDao<>(this, linkField);
    }
}
