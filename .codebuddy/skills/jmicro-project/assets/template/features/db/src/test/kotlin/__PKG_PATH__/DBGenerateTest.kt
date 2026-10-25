package __PKG__

import com.jiangtj.micro.sql.jooq.gen.GenerateHelper
import org.jooq.codegen.GenerationTool
import org.jooq.codegen.KotlinGenerator
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Generate
import org.jooq.meta.jaxb.GeneratedSerialVersionUID
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Target
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties
import org.springframework.boot.test.context.SpringBootTest

/**
 * 从数据库生成 jOOQ 代码到 `<base-package>.jooq`。
 *
 * 前置：数据库可连且迁移已执行（`./gradlew bootRun --args='--spring.profiles.active=dev'` 或先跑一次测试）。
 * 生成后 `*.jooq.tables.daos.*` 带 `@Repository`，可直接注入。
 */
@SpringBootTest
@Tag("db-generate")
class DBGenerateTest(@Autowired private val properties: DataSourceProperties) {

    private val packagePath: String = this::class.java.`package`.name
    private val outputDirectory: String = "src/main/java"

    @Test
    fun generate() {
        GenerateHelper.init(properties)
        GenerationTool.generate(
            Configuration()
                .withJdbc(GenerateHelper.getJdbc())
                .withGenerator(
                    Generator()
                        .withName(KotlinGenerator::class.java.name)
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
