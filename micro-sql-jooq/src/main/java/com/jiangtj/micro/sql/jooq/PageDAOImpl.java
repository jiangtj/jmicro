package com.jiangtj.micro.sql.jooq;

import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DAOImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 支持分页查询的 DAO 实现基类。
 * <p>
 * 该类扩展了 JOOQ 的标准 DAO 实现，添加了分页查询功能。
 * </p>
 *
 * @param <R> 数据库记录类型
 * @param <P> POJO 类型
 * @param <T> 表主键类型
 */
public abstract class PageDAOImpl<R extends UpdatableRecord<R>, P, T> extends DAOImpl<R, P, T> {
    /**
     * 使用指定的表和 POJO 类型构造 DAO。
     *
     * @param table 数据库表
     * @param type  POJO 类型
     */
    protected PageDAOImpl(Table<R> table, Class<P> type) {
        super(table, type);
    }

    /**
     * 使用指定的表、POJO 类型和配置构造 DAO。
     *
     * @param table         数据库表
     * @param type          POJO 类型
     * @param configuration JOOQ 配置
     */
    protected PageDAOImpl(Table<R> table, Class<P> type, Configuration configuration) {
        super(table, type, configuration);
    }

    /**
     * 执行分页查询。
     *
     * @param pageable   分页参数
     * @param conditions 查询条件
     * @return 分页结果
     */
    public Page<P> fetchPage(Pageable pageable, Condition... conditions) {
        return PageUtils.selectFrom(ctx(), getTable()).conditions(conditions).pageable(pageable).fetchPage(getType());
    }
}
