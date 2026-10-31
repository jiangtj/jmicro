import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

// Imports the Spring Boot BOM so BOM-managed dependencies need no explicit version.
plugins {
    id("io.spring.dependency-management")
}

// Override the BOM's kotlin.version property so every BOM-managed Kotlin artifact
// (kotlin-stdlib, kotlin-compiler-embeddable, ...) stays in sync with the version
// catalog. A mismatch breaks the Kotlin Build Tools API used by the compiler.
extra["kotlin.version"] = versionOf("kotlin")

extensions.configure<DependencyManagementExtension> {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${versionOf("springBoot")}")
    }
}
