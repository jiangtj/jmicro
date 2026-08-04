import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
    }
    dependencies {
        // Make the dependency-management plugin's types available to this build script
        // so we can type-safely configure the Spring Boot BOM import in the subprojects block.
        // Version catalog (libs) is not available in buildscript, so version is hardcoded here.
        classpath("io.spring.gradle:dependency-management-plugin:1.1.7")
        // Workaround: kotlin-build-statistics is needed by Kotlin Gradle plugin's
        // ClasspathEntrySnapshotTransform but isn't always resolved automatically.
        classpath("org.jetbrains.kotlin:kotlin-build-statistics:2.2.20")
    }
}

plugins {
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.lombok) apply false
    alias(libs.plugins.maven.publish) apply false
}

group = "com.jiangtj.micro"
version = "0.3.0"

// Project-wide metadata shared by all published artifacts
extra["projectUrl"] = "https://github.com/jiangtj/jmicro"
extra["scmUrl"] = "https://github.com/jiangtj/jmicro/tree/master"
extra["licenseName"] = "GNU Lesser General Public License v2.1"
extra["licenseUrl"] = "https://raw.githubusercontent.com/jiangtj/jmicro/master/LICENSE"

subprojects {
    // Common group/version
    group = rootProject.group
    version = rootProject.version

    // Apply JVM plugins uniformly so subprojects block can configure them.
    // micro-dependencies is a java-platform project (no java sources).
    plugins.withId("java") {
        // Common dependencies inherited from the parent POM.
        dependencies {
            "compileOnly"(rootProject.libs.jsr305)
            "implementation"(rootProject.libs.kotlin.reflect)
            "implementation"(rootProject.libs.kotlin.stdlib)
            "testImplementation"(rootProject.libs.kotlin.test.junit5)

            // Lombok (matches parent POM annotationProcessorPaths).
            "compileOnly"(rootProject.libs.lombok)
            "annotationProcessor"(rootProject.libs.lombok)
            "testCompileOnly"(rootProject.libs.lombok)
            "testAnnotationProcessor"(rootProject.libs.lombok)

            // spring-boot-configuration-processor (matches parent POM annotationProcessorPaths).
            "annotationProcessor"("org.springframework.boot:spring-boot-configuration-processor")

            // JUnit 5 as default test platform (mirrors spring-boot-starter-parent).
            "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        }

        // Compile to Java 17 bytecode regardless of the JDK running Gradle,
        // matching the Maven parent's <java.version>17</java.version>.
        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.release.set(libs.versions.java.get().toInt())
            // Spring Boot parent POM sets <parameters>true</parameters> so that
            // java.lang.reflect.Parameter#getName() returns real names at runtime.
            options.compilerArgs.add("-parameters")
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            testLogging {
                events("FAILED", "SKIPPED")
            }
        }
    }

    // Kotlin configuration shared by all modules that apply the Kotlin plugin.
    plugins.withId("org.jetbrains.kotlin.jvm") {
        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
                freeCompilerArgs.add("-Xjsr305=strict")
            }
        }

        // Kotlin sources should be compiled before Java so Java code can see Kotlin classes,
        // matching the kotlin-maven-plugin process-sources phase.
        tasks.named<JavaCompile>("compileJava") {
            dependsOn("compileKotlin")
        }
    }

    // Spring Dependency Management: import Spring Boot BOM so managed versions resolve.
    plugins.withId("io.spring.dependency-management") {
        extensions.configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
            imports {
                mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.springBoot.get()}")
            }
        }
    }
}
