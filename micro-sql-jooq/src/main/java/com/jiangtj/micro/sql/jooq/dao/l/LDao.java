package com.jiangtj.micro.sql.jooq.dao.l;

import com.jiangtj.micro.sql.jooq.dao.Dao;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class LDao<R extends UpdatableRecord<R>> extends Dao<R, Long> {

    private static final Field<Long> ID = DSL.field("id", SQLDataType.BIGINT);

    public LDao(Table<R> table, boolean isLogic) {
        super(table, ID, isLogic);
    }
}
