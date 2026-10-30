-- 权限 code 字典：仅用于给前端提供可用的权限 code（可重复执行）
-- 脚本内容即权威列表，内容变化后整体覆盖；角色与权限的绑定关系见 V1.1.0.sql
-- 新增业务权限点时改这里即可（Flyway 会按校验和变化重新执行 R__ 脚本）

DELETE FROM sys_permission;

INSERT INTO sys_permission (code, name)
VALUES ('book:read', '图书查看权限'),
       ('book:write', '图书新增/修改权限');
