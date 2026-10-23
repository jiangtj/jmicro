package com.jiangtj.micro.demobackend.controller

import com.jiangtj.micro.common.validation.MobilePhone
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("food")
class FoodController {

    @PostMapping("create")
    fun createFood(@Valid @RequestBody food: Food): Food = food

    @PostMapping("pay1")
    fun payFood1(@MobilePhone phone: String, @NotBlank name: String): String = "$phone paid $name"

    @PostMapping("pay2")
    fun payFood2(@MobilePhone phone: Long, @NotBlank name: String): String = "$phone paid $name"

    data class Food(
        @field:NotBlank val name: String,
        @field:Min(0) val price: Int,
    )
}
