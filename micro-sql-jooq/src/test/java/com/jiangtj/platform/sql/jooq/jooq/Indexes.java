/*
 * This file is generated by jOOQ.
 */
package com.jiangtj.platform.sql.jooq.jooq;


import com.jiangtj.platform.sql.jooq.jooq.tables.SystemOperateRecord;
import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables in system-db.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index SYSTEM_OPERATE_RECORD_IDX_OPERATOR = Internal.createIndex(DSL.name("idx_operator"), SystemOperateRecord.SYSTEM_OPERATE_RECORD, new OrderField[] { SystemOperateRecord.SYSTEM_OPERATE_RECORD.OPERATOR }, false);
}
