-- 权限 code 字典：仅用于给前端提供可用的权限 code（可重复执行）
-- 脚本内容即权威列表，内容变化后整体覆盖；角色与权限的绑定关系见 V1.1.0.sql
-- 新增业务权限点时改这里即可（Flyway 会按校验和变化重新执行 R__ 脚本）
-- 编码以 `:` 分层：不含 `:` 的是分组节点（如 book），仅供前端渲染权限树、整组勾选，
-- 后端鉴权仍使用具体权限点（如 book:read）

DELETE FROM sys_permission;

INSERT INTO sys_permission (code, name)
VALUES ('book', '图书模块'),
       ('book:read', '图书查看权限'),
       ('book:write', '图书新增/修改权限');
