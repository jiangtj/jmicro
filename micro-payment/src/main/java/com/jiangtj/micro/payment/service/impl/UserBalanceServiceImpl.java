package com.jiangtj.micro.payment.service.impl;

import com.jiangtj.micro.common.exceptions.MicroHttpException;
import com.jiangtj.micro.payment.entity.UserBalance;
import com.jiangtj.micro.payment.service.UserBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserBalanceServiceImpl implements UserBalanceService {

    @Override
    public BigDecimal getUserBalance(Long userId) {
        throw new MicroHttpException(500, "Not implemented");
        /*UserBalance balance = userBalanceRepository.findByUserId(userId)
                .orElseGet(() -> createUserBalance(userId));
        return balance.getBalance();*/
    }

    @Override
    @Transactional
    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    public BigDecimal deductBalance(Long userId, BigDecimal amount, String remark) {
        throw new MicroHttpException(500, "Not implemented");
        /*UserBalance balance = userBalanceRepository.findByUserIdWithLock(userId)
                .orElseGet(() -> createUserBalance(userId));

        if (balance.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("User balance is insufficient. Current balance: " + balance.getBalance() + ", Required amount: " + amount);
        }

        balance.setBalance(balance.getBalance().subtract(amount));
        userBalanceRepository.save(balance);

        // 记录余额变动日志（实际应用中应该实现）
        // balanceLogService.recordLog(userId, amount.negate(), remark);

        return balance.getBalance();*/
    }

    @Override
    @Transactional
    public BigDecimal refundBalance(Long userId, BigDecimal amount, String remark) {
        return addBalance(userId, amount, remark);
    }

    @Override
    @Transactional
    public BigDecimal addBalance(Long userId, BigDecimal amount, String remark) {
        throw new MicroHttpException(500, "Not implemented");
        /*UserBalance balance = userBalanceRepository.findByUserId(userId)
                .orElseGet(() -> createUserBalance(userId));

        balance.setBalance(balance.getBalance().add(amount));
        userBalanceRepository.save(balance);

        // 记录余额变动日志（实际应用中应该实现）
        // balanceLogService.recordLog(userId, amount, remark);

        return balance.getBalance();*/
    }

    private UserBalance createUserBalance(Long userId) {
        throw new MicroHttpException(500, "Not implemented");
        /*UserBalance balance = new UserBalance();
        balance.setUserId(userId);
        balance.setBalance(BigDecimal.ZERO);
        balance.setFrozenBalance(BigDecimal.ZERO);
        return userBalanceRepository.save(balance);*/
    }

    // 自定义异常类
    public static class InsufficientBalanceException extends RuntimeException {
        public InsufficientBalanceException(String message) {
            super(message);
        }
    }
}