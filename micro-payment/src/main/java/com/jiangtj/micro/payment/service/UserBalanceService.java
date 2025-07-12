package com.jiangtj.micro.payment.service;

import java.math.BigDecimal;

public interface UserBalanceService {
    /**
     * 获取用户余额
     * @param userId 用户ID
     * @return 用户当前余额
     */
    BigDecimal getUserBalance(Long userId);

    /**
     * 扣减用户余额
     * @param userId 用户ID
     * @param amount 扣减金额
     * @param remark 备注
     * @return 扣减后的余额
     */
    BigDecimal deductBalance(Long userId, BigDecimal amount, String remark);

    /**
     * 退还用户余额
     * @param userId 用户ID
     * @param amount 退还金额
     * @param remark 备注
     * @return 退还后的余额
     */
    BigDecimal refundBalance(Long userId, BigDecimal amount, String remark);

    /**
     * 增加用户余额
     * @param userId 用户ID
     * @param amount 增加金额
     * @param remark 备注
     * @return 增加后的余额
     */
    BigDecimal addBalance(Long userId, BigDecimal amount, String remark);
}