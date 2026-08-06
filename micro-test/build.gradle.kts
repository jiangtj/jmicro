plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.maven.publish)
}

dependencies {
    api(project(":micro-common"))
    api(project(":micro-auth"))
    api("org.springframework.boot:spring-boot-starter-test")

    compileOnly("org.springframework.boot:spring-boot-webmvc-test")
    compileOnly("org.springframework.boot:spring-boot-starter-restclient-test")

    api("io.projectreactor:reactor-test")
}
