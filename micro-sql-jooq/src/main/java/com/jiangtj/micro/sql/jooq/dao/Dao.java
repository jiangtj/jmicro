package com.jiangtj.micro.sql.jooq.dao;

import com.jiangtj.micro.sql.jooq.LogicUtils;
import com.jiangtj.micro.sql.jooq.PageUtils;
import lombok.Getter;
import org.jooq.*;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.jiangtj.micro.sql.jooq.LogicUtils.notDeleted;
import static com.jiangtj.micro.sql.jooq.QueryUtils.*;

public class Dao<R extends UpdatableRecord<R>, T> {

    private final Field<T> ID;

    private final Table<R> table;
    @Getter
    private final boolean isLogic;

    public Dao(TableField<R, T> idField, boolean isLogic) {
        this.table = Objects.requireNonNull(idField.getTable());
        this.ID = idField;
        this.isLogic = isLogic;
    }

    public Dao(Table<R> table, Field<T> idField, boolean isLogic) {
        this.table = table;
        this.ID = idField;
        this.isLogic = isLogic;
    }

    public Table<R> table() {
        return table;
    }

    public SelectConditionStep<R> fetch(DSLContext create) {
        return create.selectFrom(table)
            .where(predicate(isLogic, notDeleted()));
    }

    public Stream<R> fetchAll(DSLContext create) {
        return fetch(create).fetchStream();
    }

    public <V> List<V> fetchAll(DSLContext create, Class<V> clz) {
        return fetch(create).fetchInto(clz);
    }

    @Nullable
    public R fetchById(DSLContext create, T id) {
        return create.selectFrom(table)
            .where(ID.eq(id))
            .fetchOne();
    }

    public Result<R> fetchByIds(DSLContext create, List<T> id) {
        return create.selectFrom(table)
            .where(ID.in(id))
            .fetch();
    }

    public PageUtils.FromStep<Record1<R>, R> fetchPage(DSLContext create) {
        return PageUtils.selectFrom(create, table);
    }

    public PageUtils.ConditionStep<Record1<R>> fetchPage(DSLContext create, Object example) {
        return fetchPage(create, example, Function.identity());
    }

    public PageUtils.ConditionStep<Record1<R>> fetchPage(DSLContext create, Object example, Function<Condition, Condition> conditionHandler) {
        return fetchPage(create)
            .conditions(conditionHandler.apply(nec(create, table, example)));
    }

    public R insert(DSLContext create, Object example) {
        R record = create.newRecord(table, example);
        record.insert();
        return record;
    }

    public R update(DSLContext create, Object example) {
        R record = create.newRecord(table, example);
        replaceEmptyWithNullForUpdate(record);
        T id = record.getValue(ID);
        Condition condition = ID.eq(id);
        if (isLogic) {
            condition = condition.and(notDeleted());
        }
        create.executeUpdate(record, condition);
        return record;
    }

    public int deleteById(DSLContext create, T id) {
        if (isLogic) {
            return LogicUtils.delete(create, table)
                .where(ID.eq(id))
                .execute();
        }
        return create.deleteFrom(table)
            .where(ID.eq(id))
            .execute();
    }
}
