/*
 * This file is generated by jOOQ.
 */
package com.jiangtj.micro.sql.jooq.jooq.tables.records;


import com.jiangtj.micro.sql.jooq.jooq.tables.pojos.Databasechangelog;
import org.jooq.Field;
import org.jooq.Record14;
import org.jooq.Row14;
import org.jooq.impl.TableRecordImpl;

import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DatabasechangelogRecord extends TableRecordImpl<DatabasechangelogRecord> implements Record14<String, String, String, LocalDateTime, Integer, String, String, String, String, String, String, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.ID</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.ID</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.AUTHOR</code>.
     */
    public void setAuthor(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.AUTHOR</code>.
     */
    public String getAuthor() {
        return (String) get(1);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.FILENAME</code>.
     */
    public void setFilename(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.FILENAME</code>.
     */
    public String getFilename() {
        return (String) get(2);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.DATEEXECUTED</code>.
     */
    public void setDateexecuted(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.DATEEXECUTED</code>.
     */
    public LocalDateTime getDateexecuted() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.ORDEREXECUTED</code>.
     */
    public void setOrderexecuted(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.ORDEREXECUTED</code>.
     */
    public Integer getOrderexecuted() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.EXECTYPE</code>.
     */
    public void setExectype(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.EXECTYPE</code>.
     */
    public String getExectype() {
        return (String) get(5);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.MD5SUM</code>.
     */
    public void setMd5sum(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.MD5SUM</code>.
     */
    public String getMd5sum() {
        return (String) get(6);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.DESCRIPTION</code>.
     */
    public void setDescription(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.DESCRIPTION</code>.
     */
    public String getDescription() {
        return (String) get(7);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.COMMENTS</code>.
     */
    public void setComments(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.COMMENTS</code>.
     */
    public String getComments() {
        return (String) get(8);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.TAG</code>.
     */
    public void setTag(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.TAG</code>.
     */
    public String getTag() {
        return (String) get(9);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.LIQUIBASE</code>.
     */
    public void setLiquibase(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.LIQUIBASE</code>.
     */
    public String getLiquibase() {
        return (String) get(10);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.CONTEXTS</code>.
     */
    public void setContexts(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.CONTEXTS</code>.
     */
    public String getContexts() {
        return (String) get(11);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.LABELS</code>.
     */
    public void setLabels(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.LABELS</code>.
     */
    public String getLabels() {
        return (String) get(12);
    }

    /**
     * Setter for <code>system-db.DATABASECHANGELOG.DEPLOYMENT_ID</code>.
     */
    public void setDeploymentId(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>system-db.DATABASECHANGELOG.DEPLOYMENT_ID</code>.
     */
    public String getDeploymentId() {
        return (String) get(13);
    }

    // -------------------------------------------------------------------------
    // Record14 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row14<String, String, String, LocalDateTime, Integer, String, String, String, String, String, String, String, String, String> fieldsRow() {
        return (Row14) super.fieldsRow();
    }

    @Override
    public Row14<String, String, String, LocalDateTime, Integer, String, String, String, String, String, String, String, String, String> valuesRow() {
        return (Row14) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.ID;
    }

    @Override
    public Field<String> field2() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.AUTHOR;
    }

    @Override
    public Field<String> field3() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.FILENAME;
    }

    @Override
    public Field<LocalDateTime> field4() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.DATEEXECUTED;
    }

    @Override
    public Field<Integer> field5() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.ORDEREXECUTED;
    }

    @Override
    public Field<String> field6() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.EXECTYPE;
    }

    @Override
    public Field<String> field7() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.MD5SUM;
    }

    @Override
    public Field<String> field8() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.DESCRIPTION;
    }

    @Override
    public Field<String> field9() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.COMMENTS;
    }

    @Override
    public Field<String> field10() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.TAG;
    }

    @Override
    public Field<String> field11() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.LIQUIBASE;
    }

    @Override
    public Field<String> field12() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.CONTEXTS;
    }

    @Override
    public Field<String> field13() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.LABELS;
    }

    @Override
    public Field<String> field14() {
        return com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG.DEPLOYMENT_ID;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getAuthor();
    }

    @Override
    public String component3() {
        return getFilename();
    }

    @Override
    public LocalDateTime component4() {
        return getDateexecuted();
    }

    @Override
    public Integer component5() {
        return getOrderexecuted();
    }

    @Override
    public String component6() {
        return getExectype();
    }

    @Override
    public String component7() {
        return getMd5sum();
    }

    @Override
    public String component8() {
        return getDescription();
    }

    @Override
    public String component9() {
        return getComments();
    }

    @Override
    public String component10() {
        return getTag();
    }

    @Override
    public String component11() {
        return getLiquibase();
    }

    @Override
    public String component12() {
        return getContexts();
    }

    @Override
    public String component13() {
        return getLabels();
    }

    @Override
    public String component14() {
        return getDeploymentId();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getAuthor();
    }

    @Override
    public String value3() {
        return getFilename();
    }

    @Override
    public LocalDateTime value4() {
        return getDateexecuted();
    }

    @Override
    public Integer value5() {
        return getOrderexecuted();
    }

    @Override
    public String value6() {
        return getExectype();
    }

    @Override
    public String value7() {
        return getMd5sum();
    }

    @Override
    public String value8() {
        return getDescription();
    }

    @Override
    public String value9() {
        return getComments();
    }

    @Override
    public String value10() {
        return getTag();
    }

    @Override
    public String value11() {
        return getLiquibase();
    }

    @Override
    public String value12() {
        return getContexts();
    }

    @Override
    public String value13() {
        return getLabels();
    }

    @Override
    public String value14() {
        return getDeploymentId();
    }

    @Override
    public DatabasechangelogRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value2(String value) {
        setAuthor(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value3(String value) {
        setFilename(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value4(LocalDateTime value) {
        setDateexecuted(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value5(Integer value) {
        setOrderexecuted(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value6(String value) {
        setExectype(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value7(String value) {
        setMd5sum(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value8(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value9(String value) {
        setComments(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value10(String value) {
        setTag(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value11(String value) {
        setLiquibase(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value12(String value) {
        setContexts(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value13(String value) {
        setLabels(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord value14(String value) {
        setDeploymentId(value);
        return this;
    }

    @Override
    public DatabasechangelogRecord values(String value1, String value2, String value3, LocalDateTime value4, Integer value5, String value6, String value7, String value8, String value9, String value10, String value11, String value12, String value13, String value14) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DatabasechangelogRecord
     */
    public DatabasechangelogRecord() {
        super(com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG);
    }

    /**
     * Create a detached, initialised DatabasechangelogRecord
     */
    public DatabasechangelogRecord(String id, String author, String filename, LocalDateTime dateexecuted, Integer orderexecuted, String exectype, String md5sum, String description, String comments, String tag, String liquibase, String contexts, String labels, String deploymentId) {
        super(com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG);

        setId(id);
        setAuthor(author);
        setFilename(filename);
        setDateexecuted(dateexecuted);
        setOrderexecuted(orderexecuted);
        setExectype(exectype);
        setMd5sum(md5sum);
        setDescription(description);
        setComments(comments);
        setTag(tag);
        setLiquibase(liquibase);
        setContexts(contexts);
        setLabels(labels);
        setDeploymentId(deploymentId);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised DatabasechangelogRecord
     */
    public DatabasechangelogRecord(Databasechangelog value) {
        super(com.jiangtj.micro.sql.jooq.jooq.tables.Databasechangelog.DATABASECHANGELOG);

        if (value != null) {
            setId(value.id());
            setAuthor(value.author());
            setFilename(value.filename());
            setDateexecuted(value.dateexecuted());
            setOrderexecuted(value.orderexecuted());
            setExectype(value.exectype());
            setMd5sum(value.md5sum());
            setDescription(value.description());
            setComments(value.comments());
            setTag(value.tag());
            setLiquibase(value.liquibase());
            setContexts(value.contexts());
            setLabels(value.labels());
            setDeploymentId(value.deploymentId());
            resetChangedOnNotNull();
        }
    }
}
