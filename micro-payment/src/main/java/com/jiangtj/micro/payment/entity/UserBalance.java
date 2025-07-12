package com.jiangtj.micro.payment.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserBalance {
    private Long id;
    private Long userId;
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal frozenBalance = BigDecimal.ZERO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}