package com.jiangtj.micro.sql.jooq;

import org.jooq.*;
import org.jooq.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.function.Function;

import static com.jiangtj.micro.sql.jooq.QueryUtils.nec;
import static org.jooq.impl.DSL.field;

/**
 * 分页查询工具类。
 */
public interface PageUtils {

    /**
     * 执行分页查询并返回分页结果。
     *
     * @param create     JOOQ DSL上下文
     * @param table      查询的表
     * @param pojo       结果类型
     * @param pageable   分页参数
     * @param conditions 查询条件
     * @param <R>        记录类型
     * @param <T>        结果类型
     * @return 分页结果
     */
    static <R extends Record, T> Page<T> selectPage(DSLContext create, Table<R> table, Class<T> pojo, Pageable pageable,
                                                    Condition... conditions) {
        return selectFrom(create, table).conditions(conditions).pageable(pageable).fetchPage(pojo);
    }

    /**
     * 执行限制查询并返回结果列表。
     *
     * @param joinStep   JOIN查询步骤
     * @param pageable   分页参数
     * @param conditions 查询条件
     * @param <R>        记录类型
     * @return 查询结果
     */
    static <R extends Record> Result<R> selectLimitList(SelectJoinStep<R> joinStep, Pageable pageable,
                                                        Condition... conditions) {
        SelectConditionStep<R> where = joinStep.where(conditions);
        SelectLimitPercentAfterOffsetStep<R> limited = handlePageable(where, pageable);
        return limited.fetch();
    }

    /**
     * 执行计数查询，返回满足条件的记录数。
     *
     * @param joinStep   JOIN查询步骤
     * @param conditions 查询条件
     * @return 记录数
     */
    static Integer selectCount(SelectJoinStep<Record1<Integer>> joinStep, Condition... conditions) {
        return joinStep.where(conditions).fetchSingle().value1();
    }

    /**
     * 处理分页参数，将Spring Data的Pageable对象应用到JOOQ查询中。
     *
     * @param where       查询条件步骤
     * @param pageable    分页参数
     * @param <R>         记录类型
     * @param orderFields 排序字段
     * @return 应用了分页参数的查询步骤
     */
    static <R extends Record> SelectLimitPercentAfterOffsetStep<R> handlePageable(SelectOrderByStep<R> where,
                                                                                  Pageable pageable, OrderField<?>... orderFields) {
        Sort sort = pageable.getSort();
        SelectLimitStep<R> rs;
        if (orderFields.length > 0) {
            rs = where.orderBy(orderFields);
        } else if (!sort.isEmpty()) {
            List<SortField<Object>> list = sort.stream().map(order -> {
                String property = order.getProperty();
                if (order.isAscending()) {
                    return field(property).asc();
                }
                if (order.isDescending()) {
                    return field(property).desc();
                }
                return field(property).sortDefault();
            }).toList();
            rs = where.orderBy(list);
        } else {
            rs = where;
        }
        return rs.offset(pageable.getOffset()).limit(pageable.getPageSize());
    }

    /**
     * 创建一个选择步骤，用于构建查询。
     *
     * @param create DSL上下文
     * @param fields 要选择的字段
     * @return 选择步骤
     */
    static SelectStep<Record> select(DSLContext create, SelectFieldOrAsterisk... fields) {
        SelectSelectStep<Record> listStep = create.select(fields);
        SelectSelectStep<Record1<Integer>> countStep = create.selectCount();
        return new SelectStep<>(create, listStep, countStep);
    }

    /**
     * 创建一个针对特定表的选择步骤。
     *
     * @param create DSL上下文
     * @param table  要查询的表
     * @param <R>    记录类型
     * @return 选择步骤
     */
    static <R extends Record> SelectStep<Record1<R>> select(DSLContext create, Table<R> table) {
        SelectSelectStep<Record1<R>> listStep = create.select(table);
        SelectSelectStep<Record1<Integer>> countStep = create.selectCount();
        return new SelectStep<>(create, listStep, countStep);
    }

    /**
     * 创建一个针对特定表的FROM查询步骤。
     *
     * @param create DSL上下文
     * @param table  要查询的表
     * @param <R>    记录类型
     * @return FROM查询步骤
     */
    static <R extends Record> FromStep<Record1<R>, R> selectFrom(DSLContext create, Table<R> table) {
        return select(create, table).from(table);
    }

    /**
     * 将查询结果转换为分页对象。
     *
     * @param result   查询结果列表
     * @param count    总记录数
     * @param pageable 分页参数
     * @param <T>      结果类型
     * @return 分页对象
     */
    static <T> Page<T> toPage(List<T> result, Integer count, Pageable pageable) {
        return new PageImpl<>(result, pageable, count);
    }

    /**
     * 选择步骤记录，包含列表查询和计数查询。
     *
     */
    class SelectStep<R extends Record> {
        private final DSLContext ctx;
        private final SelectSelectStep<R> listStep;
        private final SelectSelectStep<Record1<Integer>> countStep;

        public SelectStep(DSLContext ctx, SelectSelectStep<R> listStep, SelectSelectStep<Record1<Integer>> countStep) {
            this.ctx = ctx;
            this.listStep = listStep;
            this.countStep = countStep;
        }

        public <T extends Record> FromStep<R, T> from(Table<T> table) {
            return new FromStep<>(ctx, table, listStep.from(table), countStep.from(table));
        }

        public SelectSelectStep<R> listStep() {
            return this.listStep;
        }

        public SelectSelectStep<Record1<Integer>> countStep() {
            return this.countStep;
        }
    }

    /**
     * FROM步骤记录，包含列表查询和计数查询的JOIN步骤。
     *
     */
    class FromStep<R extends Record, T extends Record> {
        private final DSLContext ctx;
        private final Table<T> table;
        private final SelectJoinStep<R> listStep;
        private final SelectJoinStep<Record1<Integer>> countStep;

        public FromStep(DSLContext ctx, Table<T> table, SelectJoinStep<R> listStep, SelectJoinStep<Record1<Integer>> countStep) {
            this.ctx = ctx;
            this.table = table;
            this.listStep = listStep;
            this.countStep = countStep;
        }

        public ConditionStep<R> where(Condition... conditions) {
            return new ConditionStep<>(listStep.where(conditions), countStep.where(conditions));
        }

        public ConditionStep<R> conditionByExample(Object example, Field<?>... ignoredFields) {
            Condition exampleConditions = nec(ctx, table, example, ignoredFields);
            return new ConditionStep<>(listStep.where(exampleConditions), countStep.where(exampleConditions));
        }

        public ConditionStep<R> conditions(Condition... conditions) {
            return new ConditionStep<>(listStep.where(conditions), countStep.where(conditions));
        }

        public LimitStep<R> pageable(Pageable pageable, OrderField<?>... orderFields) {
            return new LimitStep<>(handlePageable(listStep, pageable, orderFields), countStep, pageable);
        }

        public SelectJoinStep<R> listStep() {
            return this.listStep;
        }

        public SelectJoinStep<Record1<Integer>> countStep() {
            return this.countStep;
        }
    }

    /**
     * 条件步骤记录，包含应用了条件的列表查询和计数查询。
     *
     * @param <R> 记录类型
     */
    record ConditionStep<R extends Record>(SelectConditionStep<R> listStep,
                                           SelectConditionStep<Record1<Integer>> countStep) {

        public ConditionStep<R> and(@Nullable Condition conditions) {
            if (conditions == null) {
                return this;
            }
            return new ConditionStep<>(listStep.and(conditions), countStep.and(conditions));
        }

        public LimitStep<R> pageable(Pageable pageable, OrderField<?>... orderFields) {
            return new LimitStep<>(handlePageable(listStep, pageable, orderFields), countStep, pageable);
        }
    }

    /**
     * 限制步骤记录，包含应用了分页参数的列表查询和计数查询。
     *
     * @param <R> 记录类型
     */
    record LimitStep<R extends Record>(SelectLimitPercentAfterOffsetStep<R> listStep,
                                       SelectConnectByStep<Record1<Integer>> countStep, Pageable pageable) {
        /**
         * 执行查询并获取结果。
         *
         * @return 包含查询结果和计数的结果步骤
         */
        public ResultStep<Result<R>, Integer> fetch() {
            Result<R> result = listStep.fetch();
            Integer count = countStep.fetchSingle().value1();
            return new ResultStep<>(result, count, pageable);
        }

        /**
         * 执行查询并将结果转换为指定类型的列表。
         *
         * @param clz 目标类型
         * @param <T> 目标类型
         * @return 包含转换后的结果列表和计数的结果步骤
         */
        public <T> ResultStep<List<T>, Integer> fetchInto(Class<T> clz) {
            List<T> result = listStep.fetchInto(clz);
            Integer count = countStep.fetchSingle().value1();
            return new ResultStep<>(result, count, pageable);
        }

        /**
         * 执行查询并将结果转换为分页对象。
         *
         * @param clz 目标类型
         * @param <T> 目标类型
         * @return 分页结果对象
         */
        public <T> Page<T> fetchPage(Class<T> clz) {
            return fetchInto(clz).map(PageUtils::toPage);
        }

        /**
         * 使用自定义函数处理查询步骤。
         *
         * @param l   处理列表查询步骤的函数
         * @param c   处理计数查询步骤的函数
         * @param <L> 列表查询结果类型
         * @param <C> 计数查询结果类型
         * @return 包含处理后的结果的结果步骤
         */
        public <L, C> ResultStep<L, C> subscribe(Function<SelectLimitPercentAfterOffsetStep<R>, L> l,
                                                 Function<SelectConnectByStep<Record1<Integer>>, C> c) {
            return new ResultStep<>(l.apply(listStep), c.apply(countStep), pageable);
        }
    }

    /**
     * 结果步骤记录，包含查询结果、记录总数和分页参数。
     *
     * @param <R> 结果类型
     * @param <C> 计数类型
     */
    record ResultStep<R, C>(R result, C count, Pageable pageable) {
        /**
         * 使用处理器处理查询结果和计数结果。
         *
         * @param handler 结果处理器
         * @param <P>     处理结果类型
         * @return 处理后的结果
         */
        public <P> P map(ResultStepHandler<R, C, P> handler) {
            return handler.handle(result, count, pageable);
        }
    }

}
