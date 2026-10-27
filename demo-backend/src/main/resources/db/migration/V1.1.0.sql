-- 用户 / 角色 / 权限表：demo 登录账号由内存改为数据库存储
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

-- 用户 - 角色
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_sys_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user (id),
    CONSTRAINT fk_sys_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role (id)
);

-- 角色 - 权限
CREATE TABLE sys_role_permission (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_sys_role_permission_role FOREIGN KEY (role_id) REFERENCES sys_role (id),
    CONSTRAINT fk_sys_role_permission_permission FOREIGN KEY (permission_id) REFERENCES sys_permission (id)
);

-- 初始化账号：admin/admin123、reader/reader123（密码为 bcrypt 哈希）
INSERT INTO sys_user (username, password, nickname, enabled, create_time)
VALUES ('admin', '$2a$10$KS1DPZK.Zdmap.X0foSXc.NPIBiZnMIowoWGog9jCvvYKlpWGMwCe', '管理员', TRUE, CURRENT_TIMESTAMP),
       ('reader', '$2a$10$hKHIpaOnacDvrJeAzjmV3.WtAARs.f9.j5KO1577R4ux7ZNo1mUhi', '读者', TRUE, CURRENT_TIMESTAMP);

INSERT INTO sys_role (code, name)
VALUES ('admin', '管理员'),
       ('reader', '读者');

INSERT INTO sys_permission (code, name)
VALUES ('book:read', '图书查看权限'),
       ('book:write', '图书新增/修改权限');

-- 管理员：全部图书权限（simple 匹配，需逐条关联具体权限点）
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username = 'admin' AND r.code = 'admin';
-- 普通读者：只能查看图书
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username = 'reader' AND r.code = 'reader';

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p WHERE r.code = 'admin';
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p WHERE r.code = 'reader' AND p.code = 'book:read';
