package com.jiangtj.micro.business.flyway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("jmicro.flyway")
public class MicroFlywayProperties {
    private boolean cleanOnValidationError = false;
}
