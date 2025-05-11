package com.jiangtj.micro.sql.jooq;

import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.Record;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.jooq.impl.DSL.condition;
import static org.jooq.impl.DSL.noCondition;

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
    public static <R extends Record> Condition notEmptyCondition(DSLContext create, Table<R> table, Object example) {
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

    public static void replaceEmptyWithNullForUpdate(Record record) {
        for (int i = 0; i < record.size(); i++) {
            Field<?> field = Objects.requireNonNull(record.field(i));
            DataType<?> dataType = field.getDataType();
            boolean nullable = dataType.nullable();
            Object value = record.getValue(field);
            if (dataType.isString() && !nullable && value instanceof String str && str.isEmpty()) {
                log.debug("{} is empty, type is nonnull", field.getName());
                record.set(field, null);
                record.changed(field, false);
            }
        }
    }

    /**
     * 根据布尔条件返回对应的Condition对象
     *
     * @param predicate 条件判断标志
     * @param condition true 时候选条件对象
     * @return Condition 根据predicate值返回对应的条件对象
     */
    public static Condition predicate(boolean predicate, Condition condition) {
        return predicate ? condition : noCondition();
    }

    public static Condition predicate(boolean predicate, Supplier<Condition> condition) {
        return predicate ? condition.get() : noCondition();
    }

    public static Condition notEmpty(Function<String, Condition> condition, String fieldValue) {
        return predicate(StringUtils.hasLength(fieldValue), () -> condition.apply(fieldValue));
    }

    public static <T> Condition notNull(Function<T, Condition> condition, T fieldValue) {
        return predicate(fieldValue != null, () -> condition.apply(fieldValue));
    }

    public static Condition ne(Function<String, Condition> condition, String fieldValue) {
        return notEmpty(condition, fieldValue);
    }

    public static <R extends Record> Condition nec(DSLContext create, Table<R> table, Object example) {
        return notEmptyCondition(create, table, example);
    }

    public static <T> Condition nn(Function<T, Condition> condition, T fieldValue) {
        return notNull(condition, fieldValue);
    }

}
