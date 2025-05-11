package com.jiangtj.micro.sql.jooq.dao;

import com.jiangtj.micro.sql.jooq.LogicUtils;
import com.jiangtj.micro.sql.jooq.PageUtils;
import lombok.Getter;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.jiangtj.micro.sql.jooq.LogicUtils.notDeleted;
import static com.jiangtj.micro.sql.jooq.QueryUtils.*;

public class Dao<R extends UpdatableRecord<R>> {

    private static final Field<Integer> ID = DSL.field("id", SQLDataType.INTEGER);

    private final Table<R> table;
    @Getter
    private final boolean isLogic;

    public Dao(Table<R> table, boolean isLogic) {
        this.table = table;
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

    public <T> List<T> fetchAll(DSLContext create, Class<T> clz) {
        return fetch(create).fetchInto(clz);
    }

    public R fetchById(DSLContext create, Integer id) {
        return create.selectFrom(table)
            .where(ID.eq(id))
            .fetchOne();
    }

    public PageUtils.FromStep<Record1<R>> fetchPage(DSLContext create) {
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
        Integer id = record.getValue(ID);
        Condition condition = ID.eq(id);
        if (isLogic) {
            condition = condition.and(notDeleted());
        }
        create.executeUpdate(record, condition);
        return record;
    }

    public int deleteById(DSLContext create, Integer id) {
        if (isLogic) {
            return LogicUtils.delete(create, table)
                .where(ID.eq(id))
                .execute();
        }
        return create.deleteFrom(table)
            .where(ID.eq(id))
            .execute();
    }

    public static <R extends UpdatableRecord<R>> Dao<R> createDao(Table<R> table) {
        return new Dao<>(table, false);
    }

    public static <R extends UpdatableRecord<R>> Dao<R> createDao(Table<R> table, boolean isLogic) {
        return new Dao<>(table, isLogic);
    }
}
