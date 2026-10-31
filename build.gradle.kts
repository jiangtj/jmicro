// Shared build logic lives in the `build-logic` included build
// (see build-logic/src/main/kotlin/jmicro.*-conventions.gradle.kts).
//
// Modules only apply a convention plugin id, for example:
//   plugins { id("jmicro.library-conventions") }      // publishable library
//   plugins { id("jmicro.application-conventions") }  // runnable app (demo-backend)
//
// Version catalog: gradle/libs.versions.toml
// Cross-project configuration used to live here in a `subprojects {}` block;
// it was moved to convention plugins to avoid root-script coupling and
// configuration-order issues.
