package com.jiangtj.micro.business.config

import jakarta.validation.constraints.Size

data class SystemConfigCUDto(
    @field:Size(max = 50)
    var key: String,
    var value: String,
)
