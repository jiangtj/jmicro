package com.jiangtj.micro.payment;

import com.jiangtj.micro.sql.jooq.gen.GenerateHelper;
import jakarta.annotation.Resource;
import org.jooq.codegen.GenerationTool;
import org.jooq.codegen.KotlinGenerator;
import org.jooq.meta.jaxb.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
public class DBGenerateTest {

    @Resource
    DataSourceProperties properties;

    @Test
    public void generate() throws Exception {
        GenerateHelper.init(properties);
        GenerationTool.generate(new Configuration()
            .withJdbc(GenerateHelper.getJdbc())
            .withGenerator(new Generator()
                .withName(KotlinGenerator.class.getName())
                .withDatabase(GenerateHelper.getDatabase("payment_.*")
                    .withOutputSchemaToDefault(true)
                    .withOutputCatalogToDefault(true)
                )
                .withTarget(new Target()
                    .withPackageName("com.jiangtj.micro.payment.jooq")
                    .withDirectory("src/main/java"))
                .withGenerate(new Generate()
                    .withSpatialTypes(false)
                    .withGeneratedSerialVersionUID(GeneratedSerialVersionUID.HASH)
                    .withValidationAnnotations(true)
                    .withPojos(true)
                    .withPojosEqualsAndHashCode(false)
                    .withPojosToString(false)
                    .withPojosAsKotlinDataClasses(true)
                    .withDaos(false)
                    .withSpringAnnotations(false)
                    .withDefaultSchema(false)
                    .withDefaultCatalog(true)
                )
            ));
    }
}
