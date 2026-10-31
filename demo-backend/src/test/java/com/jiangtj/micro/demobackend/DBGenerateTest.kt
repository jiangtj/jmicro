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
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@SpringBootTest
@Tag("db-generate")
class DBGenerateTest(@Autowired private val properties: DataSourceProperties) {

    val packagePath: String = this::class.java.`package`.name
    val outputDirectory: String = detectOutputDirectory()

    /**
     * 自动识别生成目录：优先选择已存在当前包路径的源码目录，其次选择存在的 src/main/java 或 src/main/kotlin
     */
    private fun detectOutputDirectory(): String {
        val projectDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath()
        val packageDir = packagePath.replace('.', File.separatorChar)
        val candidates = listOf("src/main/java", "src/main/kotlin")
        return candidates.firstOrNull { Files.isDirectory(projectDir.resolve(it).resolve(packageDir)) }
            ?: candidates.firstOrNull { Files.isDirectory(projectDir.resolve(it)) }
            ?: "src/main/java"
    }

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
