import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

// Baseline for every JVM module: Java compile options, shared dependencies
// and the JUnit 5 test platform. Mirrors the Spring Boot parent POM defaults.
plugins {
    `java-library`
}

applyJMicroIdentity()

dependencies {
    add("compileOnly", libraryOf("jsr305"))
    add("implementation", libraryOf("kotlin-reflect"))
    add("implementation", libraryOf("kotlin-stdlib"))
    add("testImplementation", libraryOf("kotlin-test-junit5"))

    // Lombok
    add("compileOnly", libraryOf("lombok"))
    add("annotationProcessor", libraryOf("lombok"))
    add("testCompileOnly", libraryOf("lombok"))
    add("testAnnotationProcessor", libraryOf("lombok"))

    // Generates META-INF/spring-configuration-metadata.json for @ConfigurationProperties.
    add("annotationProcessor", "org.springframework.boot:spring-boot-configuration-processor")

    // JUnit 5 as default test platform (versions come from the Spring Boot BOM).
    add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    // Compile to Java 21 bytecode regardless of the JDK running Gradle
    // (jOOQ 3.21 requires Java 21, class file version 65).
    options.release.set(versionOf("java").toInt())
    // Keeps java.lang.reflect.Parameter#getName() working at runtime,
    // same as <parameters>true</parameters> in the Spring Boot parent POM.
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("FAILED", "SKIPPED")
    }
}
