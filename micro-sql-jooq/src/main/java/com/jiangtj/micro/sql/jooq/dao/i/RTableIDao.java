package com.jiangtj.micro.sql.jooq.dao.i;

import com.jiangtj.micro.sql.jooq.dao.RTableDao;
import org.jooq.TableField;
import org.jooq.TableRecord;

public class RTableIDao<R1 extends TableRecord<R1>> extends RTableDao<R1, Integer, Integer> {

    public RTableIDao(TableField<R1, Integer> tableField1, TableField<R1, Integer> tableField2, boolean isLogic) {
        super(tableField1, tableField2, isLogic);
    }

    @Override
    public <R2 extends TableRecord<R2>> RTableLinkIDao<R1, R2> createLink(TableField<R2, Integer> linkField) {
        return new RTableLinkIDao<>(this, linkField);
    }
}
