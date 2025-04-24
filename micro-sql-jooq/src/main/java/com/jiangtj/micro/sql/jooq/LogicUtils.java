package com.jiangtj.micro.sql.jooq;

import org.jooq.*;
import org.jooq.Record;

import java.util.Objects;

import static org.jooq.impl.DSL.field;

/**
 * 逻辑删除工具类。
 */
public abstract class LogicUtils {
    /**
     * 获取未删除条件（使用默认字段名 'is_deleted'）。
     *
     * @return 未删除的条件表达式
     */
    public static Condition notDeleted() {
        return field("is_deleted").eq(0);
    }

    /**
     * 获取指定表的未删除条件。
     *
     * @param table 数据表对象
     * @param <R>   记录类型
     * @return 未删除的条件表达式
     */
    public static <R extends Record> Condition notDeleted(Table<R> table) {
        return Objects.requireNonNull(table.field("is_deleted", Number.class)).eq(0);
    }

    /**
     * 使用指定的字段获取未删除条件。
     *
     * @param field 逻辑删除标记字段
     * @return 未删除的条件表达式
     */
    public static Condition notDeleted(Field<Number> field) {
        return field.eq(0);
    }

    /**
     * 创建逻辑删除更新语句。
     *
     * @param create DSL 上下文
     * @param table  要删除的表
     * @param <R>    记录类型
     * @return 更新语句构建器
     */
    public static <R extends Record> UpdateSetMoreStep<R> delete(DSLContext create, Table<R> table) {
        return create.update(table).set(table.field("is_deleted", Number.class), 1);
    }

    /**
     * 根据 ID 执行逻辑删除。
     *
     * @param create DSL 上下文
     * @param table  要删除的表
     * @param id     记录 ID
     * @param <R>    记录类型
     * @return 受影响的记录数
     */
    public static <R extends Record> int deleteById(DSLContext create, Table<R> table, int id) {
        return delete(create, table).where(Objects.requireNonNull(table.field("id", Integer.class)).eq(id)).execute();
    }
}
