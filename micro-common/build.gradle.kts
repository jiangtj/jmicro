plugins {
    id("jmicro.library-conventions")
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
    // 覆盖 kotlin-stdlib 传递的 annotations:13.0，避免 IDE 无法解析
    // org.jetbrains.annotations.UnknownNullability 等类型注解
    api(libs.jetbrains.annotations)

    // Optional: spring-core
    compileOnly("org.springframework:spring-core")

    testImplementation("org.hibernate.validator:hibernate-validator")
    testImplementation("org.junit.jupiter:junit-jupiter")
}
