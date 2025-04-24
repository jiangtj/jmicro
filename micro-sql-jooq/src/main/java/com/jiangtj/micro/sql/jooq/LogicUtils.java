package com.jiangtj.micro.sql.jooq;

import org.jooq.*;
import org.jooq.Record;

import java.util.Objects;

import static org.jooq.impl.DSL.field;

public abstract class LogicUtils {
    public static Condition notDeleted() {
        return field("is_deleted").eq(0);
    }

    public static <R extends Record> Condition notDeleted(Table<R> table) {
        return Objects.requireNonNull(table.field("is_deleted", Number.class)).eq(0);
    }

    public static Condition notDeleted(Field<Number> field) {
        return field.eq(0);
    }

    public static <R extends Record> UpdateSetMoreStep<R> delete(DSLContext create, Table<R> table) {
        return create.update(table)
            .set(table.field("is_deleted", Number.class), 1);
    }

    public static <R extends Record> int deleteById(DSLContext create, Table<R> table, int id) {
        return delete(create, table)
            .where(Objects.requireNonNull(table.field("id", Integer.class)).eq(id))
            .execute();
    }
}
