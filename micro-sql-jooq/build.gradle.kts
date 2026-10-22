plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
}

dependencies {
    api(project(":micro-common"))
    api("org.springframework.boot:spring-boot-starter-jooq")
    api("org.springframework.boot:spring-boot-data-commons")

    compileOnly("org.jooq:jooq-meta")
    compileOnly("org.jooq:jooq-codegen")
    compileOnly("io.projectreactor:reactor-core")

    testImplementation("org.jooq:jooq-codegen")
    // 代码生成测试使用的内存数据库（建表脚本见 src/test/resources/test.sql）
    testImplementation("com.h2database:h2")
    testImplementation("jakarta.validation:jakarta.validation-api")
}
