package com.jiangtj.micro.sql.jooq.dao;

import lombok.Getter;
import org.jooq.*;

import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractRNDao<R1 extends TableRecord<R1>, T1, R2 extends TableRecord<R2>> {

    private final Table<R2> table;

    private final TableField<R1, T1> queryField;
    @Getter
    private final boolean isLogic;

    public AbstractRNDao(TableField<R1, T1> queryField, Table<R2> table, boolean isLogic) {
        this.queryField = queryField;
        this.table = table;
        this.isLogic = isLogic;
    }

    public Table<R2> table() {
        return table;
    }

    public TableField<R1, T1> queryField() {
        return queryField;
    }

    abstract public Result<R2> fetch(DSLContext create, T1 value);

    public Stream<R2> fetchStream(DSLContext create, T1 value) {
        return fetch(create, value).stream();
    }

    public <V> List<V> fetch(DSLContext create, T1 value, Class<V> clz) {
        return fetch(create, value).into(clz);
    }

    abstract int delete(DSLContext create, T1 value);

    abstract int insert(DSLContext create, T1 value, R1 record);

    abstract int[] insert(DSLContext create, T1 value, List<R1> list);

    public <T2, R3 extends TableRecord<R3>> RNLinkDao<R1, T1, R2, T2, R3> createLink(TableField<R2, T2> tableField1, TableField<R3, T2> tableField2) {
        return new RNLinkDao<>(this, tableField1, tableField2);
    }
}
