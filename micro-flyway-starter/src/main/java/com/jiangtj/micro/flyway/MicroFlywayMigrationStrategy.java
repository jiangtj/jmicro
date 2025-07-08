package com.jiangtj.micro.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.exception.FlywayValidateException;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;

public class MicroFlywayMigrationStrategy implements FlywayMigrationStrategy {

    private final MicroFlywayProperties properties;

    public MicroFlywayMigrationStrategy(MicroFlywayProperties properties) {
        this.properties = properties;
    }

    @Override
    public void migrate(Flyway flyway) {
        try {
            flyway.migrate();
        } catch (FlywayValidateException ex) {
            if (!properties.isCleanOnValidationError()) {
                throw ex;
            }
            flyway.clean();
            flyway.migrate();
        }
    }
}
