package com.jiangtj.micro.sql.jooq.dao.i;

import com.jiangtj.micro.sql.jooq.dao.Dao;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class IDao<R extends UpdatableRecord<R>> extends Dao<R, Integer> {

    private static final Field<Integer> ID = DSL.field("id", SQLDataType.INTEGER);

    public IDao(Table<R> table, boolean isLogic) {
        super(table, ID, isLogic);
    }
}
