package com.jiangtj.micro.sql.jooq.dao.i;

import com.jiangtj.micro.sql.jooq.dao.RNDao;
import org.jooq.TableField;
import org.jooq.TableRecord;

public class RNIDao<R extends TableRecord<R>> extends RNDao<R, Integer> {

    public RNIDao(TableField<R, Integer> tableField, boolean isLogic) {
        super(tableField, isLogic);
    }
}
