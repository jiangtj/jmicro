package com.jiangtj.micro.sql.jooq.dao;

import lombok.Getter;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.TableField;
import org.jooq.TableRecord;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RNLinkDao<R1 extends TableRecord<R1>, T1, R2 extends TableRecord<R2>, T2, R3 extends TableRecord<R3>>
    extends AbstractRNDao<R1, T1, R3> {

    @Getter
    private final AbstractRNDao<R1, T1, R2> originalDao;

    @Getter
    private final TableField<R2, T2> tableField1;

    @Getter
    private final TableField<R3, T2> tableField2;

    public RNLinkDao(AbstractRNDao<R1, T1, R2> rnDao, TableField<R2, T2> tableField1, TableField<R3, T2> tableField2) {
        super(rnDao.queryField(), Objects.requireNonNull(tableField2.getTable()), rnDao.isLogic());
        this.originalDao = rnDao;
        this.tableField1 = tableField1;
        this.tableField2 = tableField2;
    }

    @Override
    public Result<R3> fetch(DSLContext create, T1 value) {
        Result<R2> fetch = this.originalDao.fetch(create, value);
        List<T2> list = fetch.stream()
            .map(record -> record.get(tableField1))
            .toList();
        return create.selectFrom(tableField2.getTable())
            .where(tableField2.in(list))
            .fetch();
    }

    public Stream<R3> fetchOrdered(DSLContext create, T1 value) {
        Result<R2> fetch = this.originalDao.fetch(create, value);
        List<T2> list = fetch.stream()
            .map(record -> record.get(tableField1))
            .toList();
        Map<T2, R3> map = create.selectFrom(tableField2.getTable())
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
