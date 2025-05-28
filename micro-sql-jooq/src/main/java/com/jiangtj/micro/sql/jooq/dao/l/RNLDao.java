package com.jiangtj.micro.sql.jooq.dao.l;

import com.jiangtj.micro.sql.jooq.dao.RNDao;
import org.jooq.TableField;
import org.jooq.TableRecord;

public class RNLDao<R extends TableRecord<R>> extends RNDao<R, Long> {

    public RNLDao(TableField<R, Long> tableField, boolean isLogic) {
        super(tableField, isLogic);
    }
}
