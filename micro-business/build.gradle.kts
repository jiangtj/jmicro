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
    api(project(":micro-auth"))
    api(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    api("com.github.ben-manes.caffeine:caffeine")

    // optional web stacks (OIDC server endpoints use servlet functional endpoints)
    compileOnly("org.springframework.boot:spring-boot-starter-web")

    // Flyway support is optional: consumers must opt-in by adding the Flyway
    // dependencies themselves. compileOnly + runtimeOnly keeps them visible for
    // compilation and runtime but non-transitive (not forced onto consumers).
    compileOnly("org.springframework.boot:spring-boot-starter-flyway")
    compileOnly("org.flywaydb:flyway-core")
}
