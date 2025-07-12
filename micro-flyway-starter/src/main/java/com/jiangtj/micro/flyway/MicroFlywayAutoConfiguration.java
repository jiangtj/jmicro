package com.jiangtj.micro.flyway;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Flyway.class)
@ConditionalOnBooleanProperty(name = "spring.flyway.enabled", matchIfMissing = true)
@EnableConfigurationProperties({MicroFlywayProperties.class})
public class MicroFlywayAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MicroFlywayMigrationStrategy microFlywayMigrationStrategy(MicroFlywayProperties properties) {
        return new MicroFlywayMigrationStrategy(properties);
    }
}
