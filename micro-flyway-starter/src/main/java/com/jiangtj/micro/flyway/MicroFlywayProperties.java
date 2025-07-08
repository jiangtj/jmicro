package com.jiangtj.micro.flyway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("mirco.flyway")
public class MicroFlywayProperties {
    private boolean cleanOnValidationError = false;
}
