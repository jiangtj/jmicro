package com.jiangtj.micro.sql.jooq.dao;

import lombok.Getter;
import org.jooq.*;
import org.jooq.Record;

import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractRNDao<R1 extends TableRecord<R1>, T1, RT extends Record> {

    private final Table<RT> table;

    private final TableField<R1, T1> queryField;
    @Getter
    private final boolean isLogic;

    public AbstractRNDao(TableField<R1, T1> queryField, Table<RT> table, boolean isLogic) {
        this.queryField = queryField;
        this.table = table;
        this.isLogic = isLogic;
    }

    public Table<RT> table() {
        return table;
    }

    public TableField<R1, T1> queryField() {
        return queryField;
    }

    abstract public Result<RT> fetch(DSLContext create, T1 value);

    public Stream<RT> fetchStream(DSLContext create, T1 value) {
        return fetch(create, value).stream();
    }

    public <V> List<V> fetch(DSLContext create, T1 value, Class<V> clz) {
        return fetch(create, value).into(clz);
    }

    abstract int delete(DSLContext create, T1 value);

    abstract int insert(DSLContext create, T1 value, R1 record);

    abstract int[] insert(DSLContext create, T1 value, List<R1> list);

    public <T2, R2 extends TableRecord<R2>> RNLinkDao<R1, T1, R2, T2, RT> createLink(TableField<R1, T2> tableField1, TableField<R2, T2> tableField2) {
        return new RNLinkDao<>(this, tableField1, tableField2);
    }
}
