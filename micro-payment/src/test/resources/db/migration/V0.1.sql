CREATE TABLE `payment_main` (
    `id`             INT            NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `create_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `order_no`       VARCHAR(50)    NOT NULL COMMENT '订单编号',
    `transaction_id` VARCHAR(50)    NULL COMMENT '第三方交易ID',
    `user_id`        INT            NOT NULL COMMENT '用户ID',
    `amount`         DECIMAL(12, 2) NOT NULL COMMENT '支付金额',
    `status`         TINYINT        NOT NULL COMMENT '支付状态 1 待支付 2 已支付 3 已取消 4 已退款 5 已失败',
    `pay_time`       DATETIME       NULL COMMENT '支付时间',
    `pay_method`     TINYINT        NOT NULL COMMENT '支付平台 1 余额 2 微信 3 支付宝',
    `pay_channel`    VARCHAR(50)    NOT NULL COMMENT '支付渠道 支付宝当面付 微信JSAPI等',
    PRIMARY KEY (`id`),
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='支付记录';

CREATE TABLE `payment_refund` (
    `id`                INT            NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `create_time`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `refund_no`         VARCHAR(50)    NOT NULL COMMENT '退款编号',
    `order_no`          VARCHAR(50)    NOT NULL COMMENT '退款的订单编号',
    `payment_id`        INT            NOT NULL COMMENT '退款的订单ID',
    `channel_refund_id` VARCHAR(50)    NULL COMMENT '第三方退款ID',
    `amount`            DECIMAL(12, 2) NOT NULL COMMENT '退款金额',
    `status`            TINYINT        NOT NULL COMMENT '退款状态 1 待退款 2 退款中 3 已退款 4 已取消 5 已失败',
    `refund_time`       DATETIME       NULL COMMENT '退款时间',
    PRIMARY KEY (`id`),
    INDEX `idx_refund_no` (`refund_no`),
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_payment_id` (`payment_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='支付退款记录';

CREATE TABLE `payment_callback` (
    `id`             INT          NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `notify_id`      VARCHAR(50)  NOT NULL COMMENT '通知ID',
    `type`           TINYINT      NOT NULL COMMENT '回调类型 1 支付回调 2 退款回调',
    `order_no`       VARCHAR(50) NOT NULL COMMENT '订单编号/退款编号',
    `transaction_id` VARCHAR(50) NOT NULL COMMENT '第三方交易/退款ID',
    `status`         TINYINT      NOT NULL COMMENT '处理状态 1 待处理 2 已处理',
    `callback_info`  JSON         NOT NULL COMMENT '回调内容',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='支付回调';
