package com.jiangtj.micro.sql.jooq.dao;

import com.jiangtj.micro.sql.jooq.dao.i.IDao;
import com.jiangtj.micro.sql.jooq.dao.i.RTableIDao;
import com.jiangtj.micro.sql.jooq.dao.l.LDao;
import com.jiangtj.micro.sql.jooq.dao.l.RTableLDao;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;

public interface DaoUtils {

    static <R extends UpdatableRecord<R>, T> Dao<R, T> createDao(TableField<R, T> idField) {
        return new Dao<>(idField, false);
    }

    static <R extends UpdatableRecord<R>, T> Dao<R, T> createDao(TableField<R, T> idField, boolean isLogic) {
        return new Dao<>(idField, isLogic);
    }

    static <R extends UpdatableRecord<R>> IDao<R> createIDao(Table<R> table) {
        return new IDao<>(table, false);
    }

    static <R extends UpdatableRecord<R>> IDao<R> createIDao(Table<R> table, boolean isLogic) {
        return new IDao<>(table, isLogic);
    }

    static <R extends TableRecord<R>, T> RNDao<R, T> createRNDao(TableField<R, T> tableField) {
        return new RNDao<>(tableField, false);
    }

    static <R extends TableRecord<R>, T1, T2> RTableDao<R, T1, T2> createRTableDao(TableField<R, T1> tableField1, TableField<R, T2> tableField2) {
        return new RTableDao<>(tableField1, tableField2, false);
    }

    static <R extends TableRecord<R>> RTableIDao<R> createRTableIDao(TableField<R, Integer> tableField1, TableField<R, Integer> tableField2) {
        return new RTableIDao<>(tableField1, tableField2, false);
    }

    static <R extends UpdatableRecord<R>> LDao<R> createLDao(Table<R> table) {
        return new LDao<>(table, false);
    }

    static <R extends UpdatableRecord<R>> LDao<R> createLDao(Table<R> table, boolean isLogic) {
        return new LDao<>(table, isLogic);
    }

    static <R extends TableRecord<R>> RTableLDao<R> createRTableLDao(TableField<R, Long> tableField1, TableField<R, Long> tableField2) {
        return new RTableLDao<>(tableField1, tableField2, false);
    }
}
