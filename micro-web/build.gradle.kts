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
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework:spring-web")
    api("org.springframework.boot:spring-boot-starter-json")
    api("org.springframework.boot:spring-boot-starter-kotlinx-serialization-json")
    api("org.springframework.boot:spring-boot-starter-aspectj")

    // servlet
    compileOnly("org.springframework:spring-webmvc")
    compileOnly("org.apache.tomcat.embed:tomcat-embed-core")

    // reactive
    compileOnly("io.projectreactor:reactor-core")
}
