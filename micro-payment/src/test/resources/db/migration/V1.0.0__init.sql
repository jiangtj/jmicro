-- 创建 payment_record 表
CREATE TABLE `payment_record` (
    `id`             INT            NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `create_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `payment_id`     VARCHAR(255)   NOT NULL COMMENT '支付ID',
    `order_id`       VARCHAR(255)   NOT NULL COMMENT '订单ID',
    `user_id`        INT            NOT NULL COMMENT '用户ID',
    `amount`         DECIMAL(15, 2) NOT NULL COMMENT '支付金额',
    `status`         VARCHAR(50)    NOT NULL COMMENT '支付状态',
    `payment_time`   DATETIME       NULL COMMENT '支付时间',
    `transaction_id` VARCHAR(255)   NULL COMMENT '交易ID',
    `payment_method` VARCHAR(255)   NULL COMMENT '支付方式',
    PRIMARY KEY (`id`),
    INDEX `idx_payment_id` (`payment_id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='支付记录';

