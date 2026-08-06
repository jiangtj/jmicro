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
    api("org.springframework.boot:spring-boot-starter")
    api(libs.jspecify)
    api(project(":micro-common"))

    // Flyway support is optional: consumers must opt-in by adding the Flyway
    // dependencies themselves. compileOnly + runtimeOnly keeps them visible for
    // compilation and runtime but non-transitive (not forced onto consumers).
    compileOnly("org.springframework.boot:spring-boot-starter-flyway")
    compileOnly("org.flywaydb:flyway-core")
}
