package com.jiangtj.micro.demobackend.validation

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class NestDto(
    @field:NotNull val name: String? = null,
    @field:Valid val nest: Example1? = null,
) {

    data class Example1(
        @field:NotNull val name1: String? = null,
    )
}
