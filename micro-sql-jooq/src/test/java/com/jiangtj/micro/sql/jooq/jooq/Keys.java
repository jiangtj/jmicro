/*
 * This file is generated by jOOQ.
 */
package com.jiangtj.micro.sql.jooq.jooq;


import com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangeloglock;
import com.jiangtj.micro.sql.jooq.jooq.tables.SystemOperateRecord;
import com.jiangtj.micro.sql.jooq.jooq.tables.SystemUser;
import com.jiangtj.micro.sql.jooq.jooq.tables.SystemUserRole;
import com.jiangtj.micro.sql.jooq.jooq.tables.records.DatabasechangeloglockRecord;
import com.jiangtj.micro.sql.jooq.jooq.tables.records.SystemOperateRecordRecord;
import com.jiangtj.micro.sql.jooq.jooq.tables.records.SystemUserRecord;
import com.jiangtj.micro.sql.jooq.jooq.tables.records.SystemUserRoleRecord;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * system-db.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<DatabasechangeloglockRecord> KEY_DATABASECHANGELOGLOCK_PRIMARY = Internal.createUniqueKey(Databasechangeloglock.DATABASECHANGELOGLOCK, DSL.name("KEY_DATABASECHANGELOGLOCK_PRIMARY"), new TableField[] { Databasechangeloglock.DATABASECHANGELOGLOCK.ID }, true);
    public static final UniqueKey<SystemOperateRecordRecord> KEY_SYSTEM_OPERATE_RECORD_PRIMARY = Internal.createUniqueKey(SystemOperateRecord.SYSTEM_OPERATE_RECORD, DSL.name("KEY_system_operate_record_PRIMARY"), new TableField[] { SystemOperateRecord.SYSTEM_OPERATE_RECORD.ID }, true);
    public static final UniqueKey<SystemUserRecord> KEY_SYSTEM_USER_PRIMARY = Internal.createUniqueKey(SystemUser.SYSTEM_USER, DSL.name("KEY_system_user_PRIMARY"), new TableField[] { SystemUser.SYSTEM_USER.ID }, true);
    public static final UniqueKey<SystemUserRoleRecord> KEY_SYSTEM_USER_ROLE_PRIMARY = Internal.createUniqueKey(SystemUserRole.SYSTEM_USER_ROLE, DSL.name("KEY_system_user_role_PRIMARY"), new TableField[] { SystemUserRole.SYSTEM_USER_ROLE.USER_ID, SystemUserRole.SYSTEM_USER_ROLE.ROLE }, true);
}
