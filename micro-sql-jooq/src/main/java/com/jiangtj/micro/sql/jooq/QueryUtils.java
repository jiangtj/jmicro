package com.jiangtj.micro.sql.jooq;

import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.Record;

import java.util.Objects;

import static org.jooq.impl.DSL.condition;

/**
 * 查询工具类。
 */
@Slf4j
public abstract class QueryUtils {

    /**
     * 创建分页查询步骤。
     *
     * @param create JOOQ DSL上下文
     * @param table  查询的表
     * @param <R>    记录类型
     * @return 分页查询步骤对象
     */
    public static <R extends Record> PageUtils.FromStep<Record1<R>> page(DSLContext create, Table<R> table) {
        return PageUtils.selectFrom(create, table);
    }

    /**
     * 根据示例对象创建非空条件。
     *
     * @param create  JOOQ DSL上下文
     * @param table   查询的表
     * @param example 示例对象
     * @param <R>     记录类型
     * @return 条件表达式
     */
    public static <R extends Record> Condition noEmptyCondition(DSLContext create, Table<R> table, Object example) {
        R record = create.newRecord(table, example);
        replaceEmptyWithNull(record);
        return condition(record);
    }

    /**
     * 将记录中的空字符串替换为null。
     *
     * @param record 需要处理的记录
     */
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
