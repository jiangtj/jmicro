import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider

/**
 * Project-wide metadata shared by every published artifact.
 * Kept here (instead of root build script `extra` properties) so convention
 * plugins do not have to reach into another project's state.
 */
object JMicroMetadata {
    const val PROJECT_URL = "https://github.com/jiangtj/jmicro"
    const val SCM_URL = "https://github.com/jiangtj/jmicro/tree/master"
    const val LICENSE_NAME = "GNU Lesser General Public License v2.1"
    const val LICENSE_URL = "https://raw.githubusercontent.com/jiangtj/jmicro/master/LICENSE"
    const val INCEPTION_YEAR = "2024"
}

/**
 * Common Maven coordinates for every module of the build.
 * Previously applied through the root project's `subprojects {}` block.
 */
fun Project.applyJMicroIdentity() {
    group = rootProject.group
    version = rootProject.version
}

/**
 * The version catalog (`gradle/libs.versions.toml`) as seen by build-logic.
 * Type-safe `libs` accessors are not generated for precompiled script plugins,
 * so the catalog is looked up explicitly instead.
 */
val Project.versionCatalog: VersionCatalog
    get() = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

fun Project.versionOf(name: String): String =
    versionCatalog.findVersion(name).orElseThrow {
        IllegalStateException("Missing version '$name' in gradle/libs.versions.toml")
    }.requiredVersion

fun Project.libraryOf(alias: String): Provider<MinimalExternalModuleDependency> =
    versionCatalog.findLibrary(alias).orElseThrow {
        IllegalStateException("Missing library '$alias' in gradle/libs.versions.toml")
    }
