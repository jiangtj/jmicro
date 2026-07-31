plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    api(project(":micro-sql-jooq"))
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-web")
    api(libs.wechatpay.java)

    testImplementation(project(":micro-flyway-starter"))
    testImplementation("org.flywaydb:flyway-core")
    testImplementation("org.flywaydb:flyway-mysql")
    testImplementation("com.mysql:mysql-connector-j")
    testImplementation("org.jooq:jooq-codegen")
}
