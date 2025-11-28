package com.jiangtj.micro.sql.jooq.dao;

import com.jiangtj.micro.sql.jooq.LogicUtils;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.TableField;
import org.jooq.TableRecord;

import java.util.List;
import java.util.Objects;

import static com.jiangtj.micro.sql.jooq.LogicUtils.notDeleted;
import static com.jiangtj.micro.sql.jooq.QueryUtils.predicate;

public class RNDao<R extends TableRecord<R>, T> extends AbstractRNDao<R, T, R>{

    public RNDao(TableField<R, T> tableField, boolean isLogic) {
        super(tableField, Objects.requireNonNull(tableField.getTable()), isLogic);
    }

    @Override
    public Result<R> fetch(DSLContext create, T value) {
        return create.selectFrom(table())
            .where(queryField().eq(value))
            .and(predicate(isLogic(), notDeleted()))
            .fetch();
    }

    @Override
    public int delete(DSLContext create, T value) {
        if (isLogic()) {
            return LogicUtils.delete(create, table())
                .where(queryField().eq(value))
                .execute();
        }
        return create.deleteFrom(table())
            .where(queryField().eq(value))
            .execute();
    }

    @Override
    public int insert(DSLContext create, T value, R record) {
        record.set(queryField(), value);
        return insert(create, record);
    }

    public int insert(DSLContext create, R record) {
        return create.executeInsert(record);
    }

    public int[] insert(DSLContext create, List<R> list) {
        return create.batchInsert(list).execute();
    }

    @Override
    public int[] insert(DSLContext create, T value, List<R> list) {
        list.forEach(record -> record.set(queryField(), value));
        return insert(create, list);
    }
}
