import org.gradle.kotlin.dsl.execution.ProgramText.Companion.from

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("moengageInternal") {
            from("com.moengage:android-dependency-catalog-internal:1.2.0")
        }
        create("moengage") {
            from("com.moengage:android-dependency-catalog:2.8.0")
        }
    }
}
include(
    ":example",
    ":moengage-segment-integration",
    ":moengage-segment-kotlin-destination",
    ":kotlin-example"
)

rootProject.name = "moengage-segment"