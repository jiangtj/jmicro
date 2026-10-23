package com.jiangtj.micro.demobackend.book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 图书新增/修改的请求体
 */
public record BookForm(
    @NotBlank @Size(max = 128) String title,
    @NotBlank @Size(max = 64) String author,
    @Size(max = 32) String isbn,
    @NotNull @DecimalMin("0") BigDecimal price,
    @NotNull @Min(0) Integer stock
) {
}
