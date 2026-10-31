plugins {
    // Compiles src/main/kotlin/*.gradle.kts into convention plugins and
    // makes them discoverable as plugin ids by the main build.
    `kotlin-dsl`
}

// Third-party Gradle plugins that the convention plugins below apply.
// Declaring them here puts their implementation on the classpath of
// the precompiled script plugins, so `plugins { id("...") }` resolves.
dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:${libs.versions.springBoot.get()}")
    implementation("io.spring.gradle:dependency-management-plugin:${libs.versions.springDependencyManagement.get()}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    // Provides the "org.jetbrains.kotlin.plugin.spring" / "...plugin.lombok" plugin ids.
    implementation("org.jetbrains.kotlin:kotlin-allopen:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin:kotlin-lombok:${libs.versions.kotlin.get()}")
    implementation("com.vanniktech:gradle-maven-publish-plugin:${libs.versions.mavenPublish.get()}")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:${libs.versions.dokka.get()}")
}
