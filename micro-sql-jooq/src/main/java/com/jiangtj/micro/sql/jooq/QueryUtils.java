package com.jiangtj.micro.sql.jooq;

import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.Record;

import java.util.Objects;

import static org.jooq.impl.DSL.condition;

@Slf4j
public abstract class QueryUtils {

    public static <R extends Record> PageUtils.FromStep<Record1<R>> page(DSLContext create, Table<R> table) {
        return PageUtils.selectFrom(create, table);
    }

    public static <R extends Record> Condition noEmptyCondition(DSLContext create, Table<R> table, Object example) {
        R record = create.newRecord(table, example);
        replaceEmptyWithNull(record);
        return condition(record);
    }

    public static void replaceEmptyWithNull(Record record) {
        for (int i = 0; i < record.size(); i++) {
            Field<?> field = Objects.requireNonNull(record.field(i));
            if (field.getDataType().isString()) {
                Object value = record.getValue(field);
                if ("".equals(value)) {
                    log.debug("{} is empty", field.getName());
                    record.set(field, null);
                }
            }
        }
    }
}

