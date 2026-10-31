import org.jetbrains.dokka.gradle.DokkaExtension

// Dokka API documentation generation (HTML).
plugins {
    id("org.jetbrains.dokka")
}

extensions.configure<DokkaExtension> {
    moduleName.set(project.name)
    moduleVersion.set(project.version.toString())
    dokkaSourceSets.configureEach {
        skipEmptyPackages.set(true)
        skipDeprecated.set(true)
        reportUndocumented.set(false)
        jdkVersion.set(versionOf("java").toInt())
        sourceLink {
            localDirectory.set(file("src/main"))
            remoteUrl.set(java.net.URI("${JMicroMetadata.SCM_URL}/src/main"))
            remoteLineSuffix.set("#L")
        }
    }
}
