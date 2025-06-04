package com.jiangtj.micro.demobackend.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NestDto {
    @NotNull
    private String name;

    @Valid
    private Example1 nest;

    @Data
    public static class Example1 {
        @NotNull
        private String name1;
    }
}
