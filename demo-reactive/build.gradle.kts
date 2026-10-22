plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":micro-auth"))
    implementation(project(":micro-business"))
    implementation(project(":micro-spring-boot-starter"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation(project(":micro-test"))
    testImplementation("org.springframework.boot:spring-boot-webflux-test")
    testImplementation("io.projectreactor:reactor-test")
}

// Demo apps should not be published.
tasks.named<Jar>("jar") {
    enabled = false
}
