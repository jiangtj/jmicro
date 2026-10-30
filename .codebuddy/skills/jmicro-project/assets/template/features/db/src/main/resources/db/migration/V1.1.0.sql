-- 用户 / 角色 / 权限表：账户从内存改为数据库存储（与 jmicro demo 一致）
-- 兼容 H2（MODE=MySQL）与 MySQL 8

-- 用户（password 保存 bcrypt 哈希）
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    username    VARCHAR(64)  NOT NULL,
    password    VARCHAR(100) NOT NULL,
    nickname    VARCHAR(64)  NOT NULL,
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    create_time DATETIME     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_sys_user_username UNIQUE (username)
);

-- 角色
CREATE TABLE sys_role (
    id   BIGINT      NOT NULL AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(64) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_sys_role_code UNIQUE (code)
);

-- 权限
CREATE TABLE sys_permission (
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    code VARCHAR(128) NOT NULL,
    name VARCHAR(64)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_sys_permission_code UNIQUE (code)
);

-- 用户 - 角色（不建外键，关系由应用层维护）
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

-- 角色 - 权限（直接保存权限编码，不建外键，关系由应用层维护）
CREATE TABLE sys_role_permission (
    role_id         BIGINT       NOT NULL,
    permission_code VARCHAR(128) NOT NULL,
    PRIMARY KEY (role_id, permission_code)
);

-- 初始化账号：admin/admin123、reader/reader123（密码为 bcrypt 哈希）
INSERT INTO sys_user (username, password, nickname, enabled, create_time)
VALUES ('admin', '$2a$10$KS1DPZK.Zdmap.X0foSXc.NPIBiZnMIowoWGog9jCvvYKlpWGMwCe', '管理员', TRUE, CURRENT_TIMESTAMP),
       ('reader', '$2a$10$hKHIpaOnacDvrJeAzjmV3.WtAARs.f9.j5KO1577R4ux7ZNo1mUhi', '读者', TRUE, CURRENT_TIMESTAMP);

INSERT INTO sys_role (code, name)
VALUES ('admin', '管理员'),
       ('reader', '读者');

-- 账号 - 角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username = 'admin' AND r.code = 'admin';
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username = 'reader' AND r.code = 'reader';

-- 角色 - 权限初始数据（admin 全部权限、reader 仅查看；权限 code 由 R__sys_permission.sql 维护）
INSERT INTO sys_role_permission (role_id, permission_code)
VALUES ((SELECT id FROM sys_role WHERE code = 'admin'), 'book:read'),
       ((SELECT id FROM sys_role WHERE code = 'admin'), 'book:write'),
       ((SELECT id FROM sys_role WHERE code = 'reader'), 'book:read');
