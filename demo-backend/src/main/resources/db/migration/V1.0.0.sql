-- 图书管理 demo 建表脚本
-- 兼容 H2（MODE=MySQL）与 MySQL 8

-- book
CREATE TABLE book (
    id          BIGINT         NOT NULL AUTO_INCREMENT,
    title       VARCHAR(128)   NOT NULL,
    author      VARCHAR(64)    NOT NULL,
    isbn        VARCHAR(32),
    price       DECIMAL(10, 2) NOT NULL DEFAULT 0,
    stock       INT            NOT NULL DEFAULT 0,
    create_time DATETIME       NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO book (title, author, isbn, price, stock, create_time)
VALUES ('深入理解 Java 虚拟机', '周志明', '9787111641247', 129.00, 10, CURRENT_TIMESTAMP),
       ('Spring Boot 编程思想', '小马哥', '9787121359569', 99.00, 5, CURRENT_TIMESTAMP),
       ('代码整洁之道', 'Robert C. Martin', '9787115216878', 59.00, 8, CURRENT_TIMESTAMP);
