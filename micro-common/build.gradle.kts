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
    api(libs.libphonenumber)
    api("tools.jackson.core:jackson-databind")
    api("tools.jackson.module:jackson-module-kotlin")
    api("ch.qos.logback:logback-classic")
    api("org.apache.logging.log4j:log4j-to-slf4j")
    api("org.slf4j:jul-to-slf4j")
    api(libs.kotlin.logging.jvm)
    api("jakarta.validation:jakarta.validation-api")
    api(libs.jspecify)

    // Optional: spring-core
    compileOnly("org.springframework:spring-core")

    testImplementation("org.hibernate.validator:hibernate-validator")
    testImplementation("org.junit.jupiter:junit-jupiter")
}
