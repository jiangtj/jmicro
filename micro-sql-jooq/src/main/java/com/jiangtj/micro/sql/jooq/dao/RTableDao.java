package com.jiangtj.micro.sql.jooq.dao;

import lombok.Getter;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.TableField;
import org.jooq.TableRecord;

import java.util.List;

import static com.jiangtj.micro.sql.jooq.LogicUtils.notDeleted;
import static com.jiangtj.micro.sql.jooq.QueryUtils.predicate;

public class RTableDao<R extends TableRecord<R>, T1, T2> extends RNDao<R, T1> {

    @Getter
    private final TableField<R, T1> tableField1;

    @Getter
    private final TableField<R, T2> tableField2;

    public RTableDao(TableField<R, T1> tableField1, TableField<R, T2> tableField2, boolean isLogic) {
        super(tableField1, isLogic);
        this.tableField1 = tableField1;
        this.tableField2 = tableField2;
    }

    public List<T2> fetchField2(DSLContext create, T1 value) {
        return create
            .select(tableField2)
            .from(table())
            .where(tableField1.eq(value))
            .and(predicate(super.isLogic(), notDeleted()))
            .fetchStream()
            .map(Record1::value1)
            .toList();
    }

    public int insert(DSLContext create, T1 value1, T2 value2) {
        R record = create.newRecord(table());
        record.set(tableField1, value1);
        record.set(tableField2, value2);
        return insert(create, record);
    }

    public int insertField2(DSLContext create, T1 value1, T2 value2) {
        return insert(create, value1, value2);
    }

    public int[] insertField2(DSLContext create, T1 value1, List<T2> value2) {
        List<R> list = value2.stream()
            .map(v -> {
                R record = create.newRecord(table());
                record.set(tableField1, value1);
                record.set(tableField2, v);
                return record;
            })
            .toList();
        return insert(create, list);
    }

    public <R3 extends TableRecord<R3>> RTableLinkDao<R, T1, T2, R3> createLink(TableField<R3, T2> linkField) {
        return new RTableLinkDao<>(this, linkField);
    }
}
