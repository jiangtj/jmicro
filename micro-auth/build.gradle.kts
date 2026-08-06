plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.maven.publish)
}

dependencies {
    api(project(":micro-web"))
    api("org.springframework.boot:spring-boot-starter")

    // optional web stacks
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
}
