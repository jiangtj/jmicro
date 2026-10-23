package com.jiangtj.micro.demobackend

import com.jiangtj.micro.sql.jooq.gen.GenerateHelper
import org.jooq.codegen.GenerationTool
import org.jooq.codegen.KotlinGenerator
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Target
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Tag("db-generate")
class DBGenerateTest(@Autowired private val properties: DataSourceProperties) {

    val packagePath: String = this::class.java.`package`.name
    val outputDirectory: String = "src/main/java"

    @Test
    fun generate() {
        GenerateHelper.init(properties)
        GenerationTool.generate(
            Configuration()
                .withJdbc(GenerateHelper.getJdbc())
                .withGenerator(
                    Generator()
                        .withName(KotlinGenerator::class.java.getName())
                        .withDatabase(
                            GenerateHelper.getDatabase(".*")
                                .withOutputSchemaToDefault(true)
                                .withOutputCatalogToDefault(true)
                        )
                        .withTarget(
                            Target()
                                .withPackageName("$packagePath.jooq")
                                .withDirectory(outputDirectory)
                        )
                        .withGenerate(
                            Generate()
                                .withSpatialTypes(false)
                                .withGeneratedSerialVersionUID(GeneratedSerialVersionUID.HASH)
                                .withValidationAnnotations(true)
                                .withFluentSetters(false)
                                .withPojos(true)
                                .withPojosEqualsAndHashCode(false)
                                .withPojosToString(false)
                                .withPojosAsKotlinDataClasses(true)
                                .withDaos(true)
                                .withSpringAnnotations(true)
                                .withDefaultSchema(false)
                                .withDefaultCatalog(true)
                        )
                )
        )
    }
}
