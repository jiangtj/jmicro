import com.vanniktech.maven.publish.MavenPublishBaseExtension

// Maven Central publishing (vanniktech/gradle-maven-publish-plugin).
// publishToMavenCentral()/signAllPublications() are enabled via gradle.properties
// (mavenCentralPublishing / signAllPublications) to avoid final-property conflicts.
plugins {
    id("com.vanniktech.maven.publish")
}

applyJMicroIdentity()

extensions.configure<MavenPublishBaseExtension> {
    pom {
        name.set(project.name)
        description.set("jmicro ${project.name} module")
        inceptionYear.set(JMicroMetadata.INCEPTION_YEAR)
        url.set(JMicroMetadata.PROJECT_URL)
        licenses {
            license {
                name.set(JMicroMetadata.LICENSE_NAME)
                url.set(JMicroMetadata.LICENSE_URL)
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("jiangtj")
                name.set("JiangTJ")
                url.set("https://github.com/jiangtj/")
            }
        }
        scm {
            url.set(JMicroMetadata.SCM_URL)
            connection.set("scm:git:git://github.com/jiangtj/jmicro.git")
            developerConnection.set("scm:git:ssh://github.com/jiangtj/jmicro.git")
        }
    }
}
