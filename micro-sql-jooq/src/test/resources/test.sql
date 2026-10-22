-- 测试库建表脚本
-- 由 DBGenerateTest 在 H2（MODE=MySQL）内存库中执行，用于生成 JOOQ 代码
-- 语句同样兼容 MySQL 8（引擎与字符集使用数据库默认值）

-- system_user
CREATE TABLE system_user (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    username   VARCHAR(32) NOT NULL,
    password   VARCHAR(32) NOT NULL,
    is_deleted TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- system_user_role（复合主键）
CREATE TABLE system_user_role (
    user_id BIGINT      NOT NULL,
    role    VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- system_operate_record（含 idx_operator 索引）
CREATE TABLE system_operate_record (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    create_time DATETIME     NOT NULL,
    operator    BIGINT       NOT NULL,
    content     VARCHAR(256) NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_operator ON system_operate_record (operator);
