-- 系统配置项持久化表：保存 JMicroConfigService 写入的覆盖值（未配置时回退到默认值）
-- 只在同时启用 config 特性时才需要这张表；使用 JMicroConfigSaver 落库时创建
-- 兼容 H2（MODE=MySQL）与 MySQL 8

CREATE TABLE sys_config (
    config_key   VARCHAR(255) NOT NULL,
    config_value TEXT         NOT NULL,
    create_time  DATETIME     NOT NULL,
    update_time  DATETIME     NOT NULL,
    PRIMARY KEY (config_key)
);
