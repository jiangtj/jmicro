package com.jiangtj.micro.sql.jooq.dao;

import lombok.Getter;
import org.jooq.*;
import org.jooq.Record;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RNLinkDao<R1 extends TableRecord<R1>, T1, R2 extends TableRecord<R2>, T2, RT extends Record>
    extends AbstractRNDao<R1, T1, R2> {

    @Getter
    private final AbstractRNDao<R1, T1, RT> originalDao;

    @Getter
    private final TableField<R1, T2> tableField1;

    @Getter
    private final TableField<R2, T2> tableField2;

    public RNLinkDao(AbstractRNDao<R1, T1, RT> rnDao, TableField<R1, T2> tableField1, TableField<R2, T2> tableField2) {
        super(rnDao.queryField(), tableField2.getTable(), rnDao.isLogic());
        this.originalDao = rnDao;
        this.tableField1 = tableField1;
        this.tableField2 = tableField2;
    }

    @Override
    public Result<R2> fetch(DSLContext create, T1 value) {
        Result<RT> fetch = this.originalDao.fetch(create, value);
        List<T2> list = fetch.stream()
            .map(record -> record.get(tableField1))
            .toList();
        return create.selectFrom(tableField2.getTable())
            .where(tableField2.in(list))
            .fetch();
    }

    public Stream<R2> fetchOrdered(DSLContext create, T1 value) {
        Result<RT> fetch = this.originalDao.fetch(create, value);
        List<T2> list = fetch.stream()
            .map(record -> record.get(tableField1))
            .toList();
        Map<T2, R2> map = create.selectFrom(tableField2.getTable())
            .where(tableField2.in(list))
            .fetch()
            .collect(Collectors.toMap(tableField2::getValue, record -> record));
        return fetch.stream()
            .map(record -> map.get(record.get(tableField1)));
    }

    @Override
    int delete(DSLContext create, T1 value) {
        return this.originalDao.delete(create, value);
    }

    @Override
    int insert(DSLContext create, T1 value, R1 record) {
        return this.originalDao.insert(create, value, record);
    }

    @Override
    int[] insert(DSLContext create, T1 value, List<R1> list) {
        return this.originalDao.insert(create, value, list);
    }
}
