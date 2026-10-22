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
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    testImplementation(project(":micro-test"))
    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    runtimeOnly("com.h2database:h2")
}

// Demo apps should not be published.
tasks.named<Jar>("jar") {
    enabled = false
}
