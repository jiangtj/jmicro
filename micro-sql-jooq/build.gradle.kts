plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    api(project(":micro-common"))
    api("org.springframework.boot:spring-boot-starter-jooq")
    api("org.springframework.boot:spring-boot-data-commons")

    compileOnly("org.jooq:jooq-meta")
    compileOnly("org.jooq:jooq-codegen")
    compileOnly("io.projectreactor:reactor-core")
}
