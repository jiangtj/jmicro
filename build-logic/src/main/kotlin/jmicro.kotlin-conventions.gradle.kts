import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Kotlin/JVM support on top of the Java conventions.
plugins {
    id("jmicro.java-conventions")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.jetbrains.kotlin.plugin.lombok")
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(versionOf("java")))
        // Treat JSR-305 annotations (jsr305) strictly, like -Xjsr305=strict in Maven.
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

// Kotlin sources compile before Java so Java code can see Kotlin classes.
tasks.named<JavaCompile>("compileJava") {
    dependsOn("compileKotlin")
}
